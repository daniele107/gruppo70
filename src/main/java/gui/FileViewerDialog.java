package gui;

import controller.Controller;
import model.Documento;
import model.Utente;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialog avanzato per la visualizzazione e gestione dei documenti.
 * Permette anteprima, download, validazione e gestione completa dei file.
 */
public class FileViewerDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;
    
    // Costanti per stringhe duplicate
    private static final String VIDEO_FILTER = "Video";
    private static final String ERROR_TITLE = "Errore";
    private static final String NOME_COLUMN = "Nome";
    private static final String DIMENSIONE_COLUMN = "Dimensione";
    private static final String SEGOE_UI_FONT = "Segoe UI";
    private static final String IMAGE_PREFIX = "image/";
    
    // Componenti GUI
    private final transient Controller controller;
    private JTable documentsTable;
    private DefaultTableModel tableModel;
    private JButton downloadButton;
    private JButton previewButton;
    private JButton validateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JComboBox<String> filterComboBox;
    private JTextField searchField;
    private JLabel statusLabel;
    private JPanel previewPanel;
    private JTextArea documentInfoArea;
    
    // Dati
    private transient List<Documento> allDocuments;
    private transient Documento selectedDocument;
    private int teamId = -1; // -1 = tutti i documenti
    private int hackathonId = -1; // -1 = tutti gli hackathon
    
    /**
     * Costruttore per visualizzare tutti i documenti
     */
    public FileViewerDialog(JFrame parent, Controller controller) {
        this(parent, controller, -1, -1);
    }
    
    /**
     * Costruttore per visualizzare documenti di un team specifico
     */
    public FileViewerDialog(JFrame parent, Controller controller, int teamId) {
        this(parent, controller, teamId, -1);
    }
    
    /**
     * Costruttore completo
     */
    public FileViewerDialog(JFrame parent, Controller controller, int teamId, int hackathonId) {
        super(parent, "📁 Visualizzatore Documenti", true);
        this.controller = controller;
        this.teamId = teamId;
        this.hackathonId = hackathonId;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadDocuments();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(parent);
        setResizable(true);
    }
    
    /**
     * Inizializza i componenti della GUI
     */
    private void initializeComponents() {
        // Tabella documenti
        String[] columnNames = {NOME_COLUMN, "Tipo", DIMENSIONE_COLUMN, "Data", "Team", "Validato", "Caricato da"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return Boolean.class; // Validato column
                return String.class;
            }
        };
        
        documentsTable = new JTable(tableModel);
        documentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        documentsTable.setRowHeight(25);
        documentsTable.getTableHeader().setReorderingAllowed(false);
        
        // Configura larghezza colonne
        documentsTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Nome
        documentsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Tipo
        documentsTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Dimensione
        documentsTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Data
        documentsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Team
        documentsTable.getColumnModel().getColumn(5).setPreferredWidth(70);  // Validato
        documentsTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Caricato da
        
        // Filtri e ricerca
        filterComboBox = new JComboBox<>(new String[]{
            "Tutti i documenti", "Solo validati", "Non validati", 
            "PDF", "Immagini", VIDEO_FILTER, "Archivi", "Documenti Office"
        });
        
        searchField = new JTextField(20);
        searchField.setToolTipText("Cerca per nome documento o descrizione...");
        
        // Buttons
        downloadButton = createStyledButton("💾 Download", new Color(52, 152, 219));
        previewButton = createStyledButton("👁️ Anteprima", new Color(155, 89, 182));
        validateButton = createStyledButton("✅ Valida", new Color(46, 204, 113));
        deleteButton = createStyledButton("🗑️ Elimina", new Color(231, 76, 60));
        refreshButton = createStyledButton("🔄 Aggiorna", new Color(149, 165, 166));
        
        // Initially disable action buttons
        downloadButton.setEnabled(false);
        previewButton.setEnabled(false);
        validateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        // Status label
        statusLabel = new JLabel("Pronto");
        statusLabel.setForeground(new Color(39, 174, 96));
        
        // Preview panel
        previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(new TitledBorder("Dettagli Documento"));
        previewPanel.setPreferredSize(new Dimension(300, 0));
        
        documentInfoArea = new JTextArea();
        documentInfoArea.setEditable(false);
        documentInfoArea.setBackground(new Color(248, 249, 250));
        documentInfoArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        documentInfoArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        previewPanel.add(new JScrollPane(documentInfoArea), BorderLayout.CENTER);
    }
    
    /**
     * Configura il layout della finestra
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 10, 5, 10));
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("📁 Gestione Documenti");
        titleLabel.setFont(new Font(SEGOE_UI_FONT, Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        String subtitle = " - Tutti i documenti";
        if (teamId > 0) {
            subtitle = " - Team " + teamId;
        } else if (hackathonId > 0) {
            subtitle = " - Hackathon " + hackathonId;
        }
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font(SEGOE_UI_FONT, Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(new TitledBorder("Filtri e Ricerca"));
        filterPanel.setBackground(Color.WHITE);
        
        filterPanel.add(new JLabel("Filtro:"));
        filterPanel.add(filterComboBox);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Cerca:"));
        filterPanel.add(searchField);
        filterPanel.add(refreshButton);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Table panel
        JScrollPane tableScrollPane = new JScrollPane(documentsTable);
        tableScrollPane.setBorder(new TitledBorder("Documenti"));
        
        // Split pane per tabella e preview
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                                            tableScrollPane, previewPanel);
        splitPane.setDividerLocation(650);
        splitPane.setResizeWeight(0.7);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        bottomPanel.setBackground(Color.WHITE);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(previewButton);
        buttonPanel.add(downloadButton);
        
        // Solo organizzatori e giudici possono validare/eliminare
        Utente currentUser = controller.getCurrentUser();
        if (currentUser != null && (currentUser.isOrganizzatore() || currentUser.isGiudice())) {
            buttonPanel.add(validateButton);
            buttonPanel.add(deleteButton);
        }
        
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        // Table selection listener
        documentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedDocument();
            }
        });
        
        // Double click per anteprima
        documentsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && selectedDocument != null) {
                    handlePreview();
                }
            }
        });
        
        // Button listeners
        downloadButton.addActionListener(this::handleDownload);
        previewButton.addActionListener(e -> handlePreview());
        validateButton.addActionListener(this::handleValidate);
        deleteButton.addActionListener(this::handleDelete);
        refreshButton.addActionListener(e -> loadDocuments());
        
        // Filter listeners
        filterComboBox.addActionListener(e -> applyFilters());
        searchField.addActionListener(e -> applyFilters());
        
        // Real-time search
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                applyFilters();
            }
        });
    }
    
    /**
     * Carica i documenti dal database
     */
    private void loadDocuments() {
        SwingWorker<List<Documento>, Void> worker = new SwingWorker<List<Documento>, Void>() {
            @Override
            protected List<Documento> doInBackground() throws Exception {
                statusLabel.setText("🔄 Caricamento documenti...");
                statusLabel.setForeground(Color.BLUE);
                
                if (teamId > 0) {
                    return controller.getDocumentiTeam(teamId);
                } else if (hackathonId > 0) {
                    return controller.getDocumentiHackathon(hackathonId);
                } else {
                    return controller.getTuttiDocumenti();
                }
            }
            
            @Override
            protected void done() {
                try {
                    allDocuments = get();
                    applyFilters();
                    statusLabel.setText("✅ " + allDocuments.size() + " documenti caricati");
                    statusLabel.setForeground(new Color(39, 174, 96));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    statusLabel.setText("❌ Caricamento interrotto");
                    statusLabel.setForeground(Color.ORANGE);
                } catch (Exception e) {
                    statusLabel.setText("❌ Errore nel caricamento: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(FileViewerDialog.this,
                        "Errore nel caricamento dei documenti:\n" + e.getMessage(),
                        ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Applica i filtri alla lista documenti
     */
    private void applyFilters() {
        if (allDocuments == null) return;
        
        String filter = (String) filterComboBox.getSelectedItem();
        String searchText = searchField.getText().toLowerCase().trim();
        
        tableModel.setRowCount(0);
        
        for (Documento doc : allDocuments) {
            if (passesTypeFilter(doc, filter) && passesSearchFilter(doc, searchText)) {
                addDocumentToTable(doc);
            }
        }
        
        statusLabel.setText("📊 " + tableModel.getRowCount() + " documenti visualizzati");
    }
    
    /**
     * Verifica se il documento passa il filtro per tipo
     */
    private boolean passesTypeFilter(Documento doc, String filter) {
        switch (filter) {
            case "Solo validati":
                return doc.isValidato();
            case "Non validati":
                return !doc.isValidato();
            case "PDF":
                return doc.getTipo().contains("pdf");
            case "Immagini":
                return doc.getTipo().startsWith(IMAGE_PREFIX);
            case VIDEO_FILTER:
                return doc.getTipo().startsWith("video/");
            case "Archivi":
                return doc.getTipo().contains("zip") || doc.getTipo().contains("rar");
            case "Documenti Office":
                return doc.getTipo().contains("word") || doc.getTipo().contains("excel") || 
                       doc.getTipo().contains("powerpoint") || doc.getTipo().contains("officedocument");
            default:
                // "Tutti i documenti" or unknown filter
                return true;
        }
    }
    
    /**
     * Verifica se il documento passa il filtro di ricerca
     */
    private boolean passesSearchFilter(Documento doc, String searchText) {
        if (searchText.isEmpty()) {
            return true;
        }
        return doc.getNome().toLowerCase().contains(searchText) ||
               (doc.getDescrizione() != null && doc.getDescrizione().toLowerCase().contains(searchText));
    }
    
    /**
     * Aggiunge un documento alla tabella
     */
    private void addDocumentToTable(Documento doc) {
        try {
            String teamName = "Team " + doc.getTeamId();
            String userName = "Utente " + doc.getUtenteCaricamento();
            
            // Formatta la data
            String dataFormatted = "N/A";
            if (doc.getDataCaricamento() != null) {
                dataFormatted = doc.getDataCaricamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            }
            
            Object[] row = {
                doc.getNome(),
                getMimeTypeDescription(doc.getTipo()),
                formatFileSize(doc.getDimensione()),
                dataFormatted,
                teamName,
                doc.isValidato(),
                userName
            };
            
            tableModel.addRow(row);
        } catch (Exception e) {
            // Skip documenti con errori
        }
    }
    
    /**
     * Aggiorna il documento selezionato
     */
    private void updateSelectedDocument() {
        int selectedRow = documentsTable.getSelectedRow();
        if (selectedRow >= 0 && allDocuments != null) {
            String selectedName = (String) tableModel.getValueAt(selectedRow, 0);
            selectedDocument = allDocuments.stream()
                .filter(doc -> doc.getNome().equals(selectedName))
                .findFirst()
                .orElse(null);
        } else {
            selectedDocument = null;
        }
        
        updateButtonStates();
        updateDocumentInfo();
    }
    
    /**
     * Aggiorna lo stato dei pulsanti
     */
    private void updateButtonStates() {
        boolean hasSelection = selectedDocument != null;
        downloadButton.setEnabled(hasSelection);
        previewButton.setEnabled(hasSelection && isPreviewable(selectedDocument));
        
        Utente currentUser = controller.getCurrentUser();
        boolean canValidate = hasSelection && currentUser != null && 
                            (currentUser.isOrganizzatore() || currentUser.isGiudice());
        validateButton.setEnabled(canValidate && !selectedDocument.isValidato());
        
        boolean canDelete = hasSelection && currentUser != null && 
                          (currentUser.isOrganizzatore() || 
                           currentUser.getId() == selectedDocument.getUtenteCaricamento());
        deleteButton.setEnabled(canDelete);
    }
    
    /**
     * Aggiorna le informazioni del documento nel pannello
     */
    private void updateDocumentInfo() {
        if (selectedDocument == null) {
            documentInfoArea.setText("Nessun documento selezionato");
            return;
        }
        
        StringBuilder info = new StringBuilder();
        info.append("📄 DETTAGLI DOCUMENTO\n");
        info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        
        info.append(NOME_COLUMN).append(": ").append(selectedDocument.getNome()).append("\n");
        info.append("Tipo: ").append(getMimeTypeDescription(selectedDocument.getTipo())).append("\n");
        info.append(DIMENSIONE_COLUMN).append(": ").append(formatFileSize(selectedDocument.getDimensione())).append("\n");
        info.append("Team ID: ").append(selectedDocument.getTeamId()).append("\n");
        info.append("Hackathon ID: ").append(selectedDocument.getHackathonId()).append("\n");
        info.append("Caricato da: Utente ").append(selectedDocument.getUtenteCaricamento()).append("\n");
        
        if (selectedDocument.getDataCaricamento() != null) {
            info.append("Data caricamento: ").append(
                selectedDocument.getDataCaricamento().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
        }
        
        info.append("Validato: ").append(selectedDocument.isValidato() ? "✅ Sì" : "❌ No").append("\n");
        
        if (selectedDocument.isValidato() && selectedDocument.getDataValidazione() != null) {
            info.append("Data validazione: ").append(
                selectedDocument.getDataValidazione().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
            info.append("Validato da: Utente ").append(selectedDocument.getValidatoreId()).append("\n");
        }
        
        if (selectedDocument.getHash() != null) {
            info.append("Hash: ").append(selectedDocument.getHash().substring(0, 16)).append("...\n");
        }
        
        info.append("Percorso: ").append(selectedDocument.getPercorso()).append("\n");
        
        if (selectedDocument.getDescrizione() != null && !selectedDocument.getDescrizione().trim().isEmpty()) {
            info.append("\n📝 DESCRIZIONE:\n");
            info.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            info.append(selectedDocument.getDescrizione()).append("\n");
        }
        
        documentInfoArea.setText(info.toString());
        documentInfoArea.setCaretPosition(0);
    }
    
    /**
     * Gestisce il download del documento con storage fisico reale
     */
    private void handleDownload(ActionEvent e) {
        if (selectedDocument == null) return;
        
        File targetFile = selectDownloadFile();
        if (targetFile != null) {
            executeDownload(targetFile);
        }
    }
    
    /**
     * Apre il dialog per selezionare dove salvare il file
     */
    private File selectDownloadFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(selectedDocument.getNome()));
        fileChooser.setDialogTitle("Salva documento come...");
        
        int result = fileChooser.showSaveDialog(this);
        return result == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null;
    }
    
    /**
     * Esegue il download del documento nel file specificato
     */
    private void executeDownload(File targetFile) {
        SwingWorker<Boolean, Void> downloadWorker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return performDownload(targetFile);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    handleDownloadCompletion(success);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    handleDownloadInterruption();
                } catch (Exception ex) {
                    handleDownloadError(ex);
                }
            }
        };
        downloadWorker.execute();
    }
    
    /**
     * Esegue il download effettivo del documento
     */
    private Boolean performDownload(File targetFile) throws Exception {
        statusLabel.setText("💾 Download in corso...");
        statusLabel.setForeground(Color.BLUE);
        
        byte[] contenutoFile = controller.scaricaDocumento(selectedDocument.getId());
        
        if (contenutoFile != null) {
            Files.write(targetFile.toPath(), contenutoFile);
            return true;
        } else {
            createFallbackFile(targetFile);
            return false;
        }
    }
    
    /**
     * Crea un file di fallback con le informazioni del documento
     */
    private void createFallbackFile(File targetFile) throws Exception {
        String fallbackContent = "ERRORE: Impossibile scaricare il contenuto originale\n\n" +
            "Documento: " + selectedDocument.getNome() + "\n" +
            "Tipo: " + selectedDocument.getTipo() + "\n" +
            DIMENSIONE_COLUMN + ": " + selectedDocument.getDimensione() + " bytes\n" +
            "Data caricamento: " + selectedDocument.getDataCaricamento() + "\n" +
            "Descrizione: " + selectedDocument.getDescrizione() + "\n" +
            "Hash: " + (selectedDocument.getHash() != null ? selectedDocument.getHash() : "N/A") + "\n\n" +
            "Il file originale potrebbe non essere disponibile o corrotto.";
        
        Files.write(targetFile.toPath(), fallbackContent.getBytes());
    }
    
    /**
     * Gestisce il completamento del download
     */
    private void handleDownloadCompletion(boolean success) {
        if (success) {
            statusLabel.setText("✅ Download completato");
            statusLabel.setForeground(new Color(39, 174, 96));
            JOptionPane.showMessageDialog(FileViewerDialog.this,
                "Download completato con successo!",
                "Download", JOptionPane.INFORMATION_MESSAGE);
        }
        // Se non è riuscito, il file di fallback è stato comunque creato
    }
    
    /**
     * Gestisce l'interruzione del download
     */
    private void handleDownloadInterruption() {
        statusLabel.setText("❌ Download interrotto");
        statusLabel.setForeground(Color.ORANGE);
        JOptionPane.showMessageDialog(FileViewerDialog.this,
            "Download interrotto dall'utente",
            "Download Interrotto", JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Gestisce gli errori durante il download
     */
    private void handleDownloadError(Exception ex) {
        statusLabel.setText("❌ Errore download: " + ex.getMessage());
        statusLabel.setForeground(Color.RED);
        JOptionPane.showMessageDialog(FileViewerDialog.this,
            "Errore durante il download:\n" + ex.getMessage(),
            ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Gestisce l'anteprima del documento
     */
    private void handlePreview() {
        if (selectedDocument == null || !isPreviewable(selectedDocument)) return;
        
        try {
            // Per ora mostra informazioni dettagliate
            // In un'implementazione reale, aprirebbe il file con l'applicazione predefinita
            JDialog previewDialog = new JDialog(this, "👁️ Anteprima - " + selectedDocument.getNome(), true);
            previewDialog.setSize(600, 400);
            previewDialog.setLocationRelativeTo(this);
            
            JTextArea previewArea = new JTextArea();
            previewArea.setEditable(false);
            previewArea.setFont(new Font("Consolas", Font.PLAIN, 12));
            
            StringBuilder preview = new StringBuilder();
            preview.append("📄 ANTEPRIMA DOCUMENTO\n");
            preview.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            preview.append(NOME_COLUMN).append(" file: ").append(selectedDocument.getNome()).append("\n");
            preview.append("Tipo MIME: ").append(selectedDocument.getTipo()).append("\n");
            preview.append(DIMENSIONE_COLUMN).append(": ").append(formatFileSize(selectedDocument.getDimensione())).append("\n");
            preview.append("Percorso: ").append(selectedDocument.getPercorso()).append("\n\n");
            
            if (selectedDocument.getDescrizione() != null && !selectedDocument.getDescrizione().trim().isEmpty()) {
                preview.append("Descrizione:\n").append(selectedDocument.getDescrizione()).append("\n\n");
            }
            
            preview.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            preview.append("NOTA: In un'implementazione completa, qui verrebbe mostrata l'anteprima del contenuto\n");
            preview.append("del file (testo per i file di testo, miniatura per le immagini, etc.)");
            
            previewArea.setText(preview.toString());
            
            previewDialog.add(new JScrollPane(previewArea));
            previewDialog.setVisible(true);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Errore nell'apertura dell'anteprima:\n" + ex.getMessage(),
                ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Gestisce la validazione del documento
     */
    private void handleValidate(ActionEvent e) {
        if (selectedDocument == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Confermi la validazione del documento?\n\n" +
            NOME_COLUMN + ": " + selectedDocument.getNome() + "\n" +
            "Team: " + selectedDocument.getTeamId(),
            "Conferma Validazione", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> validateWorker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return controller.validaDocumento(selectedDocument.getId());
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            statusLabel.setText("✅ Documento validato");
                            statusLabel.setForeground(new Color(39, 174, 96));
                            loadDocuments(); // Ricarica per aggiornare lo stato
                            JOptionPane.showMessageDialog(FileViewerDialog.this,
                                "Documento validato con successo!",
                                "Validazione", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            statusLabel.setText("❌ Errore nella validazione");
                            statusLabel.setForeground(Color.RED);
                            JOptionPane.showMessageDialog(FileViewerDialog.this,
                                "Errore nella validazione del documento",
                                ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        statusLabel.setText("❌ Validazione interrotta");
                        statusLabel.setForeground(Color.ORANGE);
                    } catch (Exception ex) {
                        statusLabel.setText("❌ Errore: " + ex.getMessage());
                        statusLabel.setForeground(Color.RED);
                        JOptionPane.showMessageDialog(FileViewerDialog.this,
                            "Errore: " + ex.getMessage(),
                            ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            validateWorker.execute();
        }
    }
    
    /**
     * Gestisce l'eliminazione del documento
     */
    private void handleDelete(ActionEvent e) {
        if (selectedDocument == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "ATTENZIONE: Questa operazione eliminerà definitivamente il documento!\n\n" +
            NOME_COLUMN + ": " + selectedDocument.getNome() + "\n" +
            "Team: " + selectedDocument.getTeamId() + "\n\n" +
            "Sei sicuro di voler procedere?",
            "Conferma Eliminazione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> deleteWorker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return controller.eliminaDocumento(selectedDocument.getId());
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            statusLabel.setText("✅ Documento eliminato");
                            statusLabel.setForeground(new Color(39, 174, 96));
                            loadDocuments(); // Ricarica la lista
                            JOptionPane.showMessageDialog(FileViewerDialog.this,
                                "Documento eliminato con successo!",
                                "Eliminazione", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            statusLabel.setText("❌ Errore nell'eliminazione");
                            statusLabel.setForeground(Color.RED);
                            JOptionPane.showMessageDialog(FileViewerDialog.this,
                                "Errore nell'eliminazione del documento",
                                ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        statusLabel.setText("❌ Eliminazione interrotta");
                        statusLabel.setForeground(Color.ORANGE);
                    } catch (Exception ex) {
                        statusLabel.setText("❌ Errore: " + ex.getMessage());
                        statusLabel.setForeground(Color.RED);
                        JOptionPane.showMessageDialog(FileViewerDialog.this,
                            "Errore: " + ex.getMessage(),
                            ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            deleteWorker.execute();
        }
    }
    
    /**
     * Crea un pulsante con stile moderno
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font(SEGOE_UI_FONT, Font.BOLD, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    /**
     * Formatta la dimensione del file
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024L) return bytes + " B";
        if (bytes < 1048576L) return String.valueOf(Math.round(bytes / 102.4) / 10.0) + " KB";
        if (bytes < 1073741824L) return String.valueOf(Math.round(bytes / (102.4 * 1024.0)) / 10.0) + " MB";
        return String.valueOf(Math.round(bytes / (102.4 * 1024.0 * 1024.0)) / 10.0) + " GB";
    }
    
    /**
     * Ottiene una descrizione user-friendly del tipo MIME
     */
    private String getMimeTypeDescription(String mimeType) {
        if (mimeType == null) return "Sconosciuto";
        
        switch (mimeType.toLowerCase()) {
            case "application/pdf": return "PDF";
            case "application/msword": return "Word";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document": return "Word";
            case "text/plain": return "Testo";
            case "text/markdown": return "Markdown";
            case "application/zip": return "ZIP";
            case "application/x-rar-compressed": return "RAR";
            case "image/jpeg", "image/jpg": return "JPEG";
            case "image/png": return "PNG";
            case "image/gif": return "GIF";
            case "video/mp4": return "MP4";
            case "video/x-msvideo": return "AVI";
            case "video/quicktime": return "MOV";
            default: 
                if (mimeType.startsWith(IMAGE_PREFIX)) return "Immagine";
                if (mimeType.startsWith("video/")) return VIDEO_FILTER;
                if (mimeType.startsWith("text/")) return "Testo";
                return mimeType.substring(mimeType.lastIndexOf('/') + 1).toUpperCase();
        }
    }
    
    /**
     * Verifica se il documento può essere visualizzato in anteprima
     */
    private boolean isPreviewable(Documento documento) {
        if (documento == null) return false;
        
        String mimeType = documento.getTipo().toLowerCase();
        return mimeType.startsWith("text/") || 
               mimeType.equals("application/pdf") ||
               mimeType.startsWith(IMAGE_PREFIX);
    }
}
