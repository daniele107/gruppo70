package gui;

import controller.Controller;
import model.Progress;
import model.Team;
import model.Hackathon;
import model.Documento;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

/**
 * Dialog per i giudici per rivedere e commentare i progressi dei team
 */
public class JudgeProgressReviewDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String SUCCESSO_TITLE = "Successo";
    private static final String ERRORE_TITLE = "Errore";
    private static final String ERRORE_SISTEMA_TITLE = "Errore Sistema";
    private static final String ERRORE_PREFIX = "Errore: ";
    private static final String SUCCESS_TOAST = "success";
    private static final String INSERISCI_COMMENTO_MSG = "Inserisci un commento prima di salvare.";
    private static final String COMMENTO_RICHIESTO_TITLE = "Commento Richiesto";
    private static final String COMMENTO_AGGIUNTO_MSG = "Commento aggiunto con successo!";
    private static final String COMMENTO_MODIFICATO_MSG = "Commento modificato con successo!";
    private static final String COMMENTO_RIMOSSO_MSG = "Commento rimosso con successo!";
    private static final String ERRORE_DOWNLOAD_PREFIX = "Errore durante il download: ";
    private static final String IMPOSSIBILE_APRIRE_CARTELLA_PREFIX = "Impossibile aprire la cartella: ";
    private static final String DOCUMENTO_NON_TROVATO_DB = "Documento non trovato nel database";
    private static final String IMPOSSIBILE_SCARICARE_DOCUMENTO = "Impossibile scaricare il documento";
    private static final String ERRORE_CARICAMENTO_DOCUMENTO_PREFIX = "Errore durante il caricamento del documento:\n";
    private static final String ERRORE_RICOSTRUZIONE_FILE_PREFIX = "Errore durante la ricostruzione del file: ";
    private static final String IMPOSSIBILE_APRIRE_DOCUMENTO_PREFIX = "Impossibile aprire il documento:\n";
    private static final String ERRORE_NEL_CARICAMENTO_PREFIX = "❌ Errore nel caricamento: ";
    private static final String ERRORE_CARICAMENTO_PROGRESSI_PREFIX = "Errore nel caricamento dei progressi:\n";
    private static final String BTN_APRI_FILE = "🔍 APRI FILE";
    private static final String ERRORE_APERTURA_TITLE = "Errore Apertura";
    private static final String FONT_SEGOE_UI = "Segoe UI";
    
    private final transient Controller controller;
    private final transient MainFrame parentFrame;
    
    // Components
    private transient JTable progressTable;
    private transient DefaultTableModel tableModel;
    private transient JTextArea progressDetailsArea;
    private transient JTextArea commentArea;
    private transient JButton addCommentButton;
    private transient JButton updateCommentButton;
    private transient JButton removeCommentButton;
    private transient JButton viewDocumentButton;
    private transient JButton refreshButton;
    private transient JButton downloadButton;
    private transient JButton openFolderButton;
    private transient JTextField filterField;
    private transient JCheckBox onlyWithFileCheckbox;
    private transient JCheckBox onlyWithCommentsCheckbox;
    private transient TableRowSorter<DefaultTableModel> sorter;
    private transient JLabel statusLabel;
    
    private transient List<Progress> allProgress;
    private transient Progress selectedProgress;
    
    public JudgeProgressReviewDialog(MainFrame parent, Controller controller) {
        super(parent, "Revisione Progressi Team - Dashboard Giudice", true);
        this.parentFrame = parent;
        this.controller = controller;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadProgressData();
        
        setSize(1000, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents() {
        // Table per progressi
        String[] columns = {"Team", "Hackathon", "Titolo", "Data", "Ha Commento", "Documento"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class; // Renderizziamo tutto come testo per massima compatibilità visiva
            }
        };
        
        progressTable = new JTable(tableModel);
        progressTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        progressTable.setRowHeight(28);
        progressTable.getTableHeader().setReorderingAllowed(false);
        progressTable.setFillsViewportHeight(true);
        progressTable.setAutoCreateRowSorter(true);
        sorter = new TableRowSorter<>(tableModel);
        progressTable.setRowSorter(sorter);
        progressTable.setShowHorizontalLines(true);
        progressTable.setShowVerticalLines(false);
        progressTable.setGridColor(new Color(230,230,230));

        // Renderer zebra + tooltip
        progressTable.setDefaultRenderer(Object.class, new ZebraCellRenderer());

        // Larghezze colonne per leggibilità
        javax.swing.table.TableColumnModel cols = progressTable.getColumnModel();
        cols.getColumn(0).setPreferredWidth(160); // Team
        cols.getColumn(1).setPreferredWidth(160); // Hackathon
        cols.getColumn(2).setPreferredWidth(320); // Titolo
        cols.getColumn(3).setPreferredWidth(140); // Data
        cols.getColumn(4).setPreferredWidth(110); // Ha Commento
        cols.getColumn(5).setPreferredWidth(120); // Documento
        
        // Filtri rapidi
        filterField = new JTextField();
        filterField.setToolTipText("Filtra per Team, Hackathon o Titolo");
        onlyWithFileCheckbox = new JCheckBox("Solo con file 📎");
        onlyWithCommentsCheckbox = new JCheckBox("Solo con commenti 💬");

        // Dettagli progresso
        progressDetailsArea = new JTextArea(8, 50);
        progressDetailsArea.setEditable(false);
        progressDetailsArea.setFont(new Font(FONT_SEGOE_UI, Font.PLAIN, 12));
        progressDetailsArea.setBackground(new Color(248, 249, 250));
        progressDetailsArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Area commento
        commentArea = new JTextArea(6, 50);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setFont(new Font(FONT_SEGOE_UI, Font.PLAIN, 12));
        commentArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        
        // Buttons
        addCommentButton = createButton("💬 Aggiungi Commento", new Color(52, 152, 219));
        updateCommentButton = createButton("✏️ Modifica Commento", new Color(241, 196, 15));
        removeCommentButton = createButton("🗑️ Rimuovi Commento", new Color(231, 76, 60));
        viewDocumentButton = createButton(BTN_APRI_FILE, new Color(46, 125, 50));
        downloadButton = createButton("💾 Scarica", new Color(52, 152, 219));
        openFolderButton = createButton("📂 Apri Cartella", new Color(127, 140, 141));
        refreshButton = createButton("🔄 Aggiorna", new Color(46, 204, 113));
        
        // Inizialmente disabilitati
        addCommentButton.setEnabled(false);
        updateCommentButton.setEnabled(false);
        removeCommentButton.setEnabled(false);
        viewDocumentButton.setEnabled(false);
        downloadButton.setEnabled(false);
        openFolderButton.setEnabled(false);
        
        statusLabel = new JLabel("Seleziona un progresso per iniziare la revisione");
        statusLabel.setFont(new Font(FONT_SEGOE_UI, Font.ITALIC, 12));
        statusLabel.setForeground(Color.GRAY);
    }
    
    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        button.setFocusPainted(false);
        button.setFont(new Font(FONT_SEGOE_UI, Font.BOLD, 14));
        return button;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("⚖️ Revisione Progressi Team");
        titleLabel.setFont(new Font(FONT_SEGOE_UI, Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        
        JLabel subtitleLabel = new JLabel("Esamina e commenta i progressi dei team durante l'hackathon");
        subtitleLabel.setFont(new Font(FONT_SEGOE_UI, Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content - Split pane
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(400);
        mainSplitPane.setResizeWeight(0.4);
        
        // Left panel - Progress list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "📋 Progressi da Rivedere",
            0, 0,
            new Font(FONT_SEGOE_UI, Font.BOLD, 14),
            new Color(52, 73, 94)
        ));
        // Filter bar
        JPanel filterBar = new JPanel(new BorderLayout(8, 0));
        JPanel filterChecks = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterChecks.add(onlyWithFileCheckbox);
        filterChecks.add(onlyWithCommentsCheckbox);
        filterBar.add(filterField, BorderLayout.CENTER);
        filterBar.add(filterChecks, BorderLayout.SOUTH);
        filterBar.setBorder(new EmptyBorder(6, 6, 6, 6));
        leftPanel.add(filterBar, BorderLayout.NORTH);
        
        JScrollPane tableScrollPane = new JScrollPane(progressTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        progressTable.getTableHeader().setFont(new Font(FONT_SEGOE_UI, Font.BOLD, 12));
        progressTable.setFont(new Font(FONT_SEGOE_UI, Font.PLAIN, 12));
        tableScrollPane.setPreferredSize(new Dimension(380, 0));
        leftPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Right panel - Details and comments
        JPanel rightPanel = new JPanel(new BorderLayout());
        
        // Details section
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "📄 Dettagli Progresso",
            0, 0,
            new Font(FONT_SEGOE_UI, Font.BOLD, 14),
            new Color(52, 73, 94)
        ));
        
        JScrollPane detailsScrollPane = new JScrollPane(progressDetailsArea);
        detailsScrollPane.setPreferredSize(new Dimension(0, 200));
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Comment section
        JPanel commentPanel = new JPanel(new BorderLayout());
        commentPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "💭 Commento Giudice",
            0, 0,
            new Font(FONT_SEGOE_UI, Font.BOLD, 14),
            new Color(52, 73, 94)
        ));
        
        JScrollPane commentScrollPane = new JScrollPane(commentArea);
        commentPanel.add(commentScrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setBackground(new Color(236, 240, 241));
        
        // Prominente bottone per aprire file
        buttonsPanel.add(viewDocumentButton);
        buttonsPanel.add(Box.createHorizontalStrut(15));
        
        // Altri bottoni commenti
        buttonsPanel.add(addCommentButton);
        buttonsPanel.add(updateCommentButton);
        buttonsPanel.add(removeCommentButton);
        buttonsPanel.add(Box.createHorizontalStrut(10));
        buttonsPanel.add(downloadButton);
        buttonsPanel.add(openFolderButton);
        
        commentPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Combine right panels
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setTopComponent(detailsPanel);
        rightSplitPane.setBottomComponent(commentPanel);
        rightSplitPane.setDividerLocation(250);
        rightSplitPane.setResizeWeight(0.4);
        
        rightPanel.add(rightSplitPane, BorderLayout.CENTER);
        
        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightPanel);
        
        add(mainSplitPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(236, 240, 241));
        statusPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        statusPanel.add(statusLabel);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        progressTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleProgressSelection();
            }
        });
        
        addCommentButton.addActionListener(this::handleAddComment);
        updateCommentButton.addActionListener(this::handleUpdateComment);
        removeCommentButton.addActionListener(this::handleRemoveComment);
        viewDocumentButton.addActionListener(this::handleViewDocument);
        refreshButton.addActionListener(e -> loadProgressData());
        downloadButton.addActionListener(this::handleDownloadSelected);
        openFolderButton.addActionListener(this::handleOpenFolder);

        // Filtri
        filterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateFilter(); }
        });
        onlyWithFileCheckbox.addActionListener(e -> updateFilter());
        onlyWithCommentsCheckbox.addActionListener(e -> updateFilter());
        
        // Aggiungi listener per doppio click sulla riga per aprire il file
        // Popup menu
        JPopupMenu popup = new JPopupMenu();
        JMenuItem openItem = new JMenuItem("🔍 Apri File");
        JMenuItem downloadItem = new JMenuItem("💾 Scarica");
        JMenuItem openFolderItem = new JMenuItem("📂 Apri Cartella");
        JMenuItem manageCommentsItem = new JMenuItem("💬 Gestisci Commenti");
        popup.add(openItem);
        popup.add(downloadItem);
        popup.add(openFolderItem);
        popup.addSeparator();
        popup.add(manageCommentsItem);

        openItem.addActionListener(this::handleViewDocument);
        downloadItem.addActionListener(this::handleDownloadSelected);
        openFolderItem.addActionListener(this::handleOpenFolder);
        manageCommentsItem.addActionListener(e -> handleManageComments());

        progressTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && selectedProgress != null) {
                    // Doppio click = apri file se disponibile, altrimenti gestione commenti
                    if (selectedProgress.getDocumentoPath() != null && !selectedProgress.getDocumentoPath().trim().isEmpty()) {
                        handleViewDocument(null);
                    } else {
                        handleManageComments();
                    }
                }
                if (SwingUtilities.isRightMouseButton(evt)) {
                    int row = progressTable.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        progressTable.setRowSelectionInterval(row, row);
                        handleProgressSelection();
                        popup.show(progressTable, evt.getX(), evt.getY());
                    }
                }
            }
        });
    }

    private void updateFilter() {
        List<RowFilter<DefaultTableModel, Integer>> filters = new ArrayList<>();
        String text = filterField.getText();
        if (text != null && !text.trim().isEmpty()) {
            String regex = ".*" + java.util.regex.Pattern.quote(text.trim()) + ".*";
            filters.add(RowFilter.regexFilter("(?i)" + regex, 0)); // Team
            filters.add(RowFilter.regexFilter("(?i)" + regex, 1)); // Hackathon
            filters.add(RowFilter.regexFilter("(?i)" + regex, 2)); // Titolo
        }
        RowFilter<DefaultTableModel, Integer> combinedText = null;
        if (!filters.isEmpty()) {
            combinedText = RowFilter.orFilter(filters);
        }

        List<RowFilter<DefaultTableModel, Integer>> hardFilters = new ArrayList<>();
        if (onlyWithFileCheckbox.isSelected()) {
            hardFilters.add(RowFilter.regexFilter("📎", 5));
        }
        if (onlyWithCommentsCheckbox.isSelected()) {
            hardFilters.add(RowFilter.regexFilter("💬", 4));
        }

        RowFilter<DefaultTableModel, Integer> finalFilter = null;
        if (combinedText != null && !hardFilters.isEmpty()) {
            hardFilters.add(0, combinedText);
            finalFilter = RowFilter.andFilter(hardFilters);
        } else if (combinedText != null) {
            finalFilter = combinedText;
        } else if (!hardFilters.isEmpty()) {
            finalFilter = RowFilter.andFilter(hardFilters);
        }
        sorter.setRowFilter(finalFilter);
    }

    private void handleDownloadSelected(ActionEvent e) {
        if (!isValidProgressForDownload()) {
            return;
        }
        
        try {
            if (isDocumentoFromDatabase()) {
                downloadDocumentoFromDatabase();
            } else {
                downloadLocalFile();
            }
        } catch (Exception ex) {
            mostraErroreDownload(ex);
        }
    }
    
    /**
     * Verifica se il progresso selezionato è valido per il download
     */
    private boolean isValidProgressForDownload() {
        return selectedProgress != null && 
               selectedProgress.getDocumentoPath() != null && 
               !selectedProgress.getDocumentoPath().trim().isEmpty();
    }
    
    /**
     * Verifica se il documento proviene dal database
     */
    private boolean isDocumentoFromDatabase() {
        return selectedProgress.getTitolo().startsWith("📄");
    }
    
    /**
     * Scarica un documento dal database
     */
    private void downloadDocumentoFromDatabase() throws Exception {
        Documento target = trovaDocumentoTarget();
        if (target == null) {
            return;
        }
        
        byte[] bytes = controller.scaricaDocumento(target.getId());
        if (bytes == null || bytes.length == 0) {
            return;
        }
        
        salvaFileConChooser(target.getNome(), bytes);
    }
    
    /**
     * Trova il documento target nel database
     */
    private Documento trovaDocumentoTarget() throws Exception {
        List<Documento> docs = controller.getDocumentiHackathon(selectedProgress.getHackathonId());
        for (Documento d : docs) {
            if (selectedProgress.getDocumentoPath().equals(d.getPercorso())) {
                return d;
            }
        }
        return null;
    }
    
    /**
     * Salva un file usando JFileChooser
     */
    private void salvaFileConChooser(String nomeDefault, byte[] bytes) throws Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(nomeDefault));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(chooser.getSelectedFile())) {
                fos.write(bytes);
                fos.flush();
            }
        }
    }
    
    /**
     * Scarica un file locale copiandolo
     */
    private void downloadLocalFile() throws Exception {
        File src = ottieniFileOrigine();
        if (src == null) {
            return;
        }
        
        copiaFileConChooser(src);
    }
    
    /**
     * Ottiene il file di origine, tentando di ricrearlo se non esiste
     */
    private File ottieniFileOrigine() {
        File src = new File(selectedProgress.getDocumentoPath());
        if (!src.exists()) {
            handleProgressFileView();
            src = new File(selectedProgress.getDocumentoPath());
            if (!src.exists()) {
                return null;
            }
        }
        return src;
    }
    
    /**
     * Copia un file usando JFileChooser
     */
    private void copiaFileConChooser(File src) throws Exception {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(src.getName()));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            copiaFile(src, chooser.getSelectedFile());
        }
    }
    
    /**
     * Copia un file da src a dest
     */
    private void copiaFile(File src, File dest) throws Exception {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(src);
             java.io.FileOutputStream fos = new java.io.FileOutputStream(dest)) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = fis.read(buf)) != -1) {
                fos.write(buf, 0, r);
            }
            fos.flush();
        }
    }
    
    /**
     * Mostra un messaggio di errore per il download
     */
    private void mostraErroreDownload(Exception ex) {
        JOptionPane.showMessageDialog(this, 
            ERRORE_DOWNLOAD_PREFIX + ex.getMessage(), 
            ERRORE_TITLE, 
            JOptionPane.ERROR_MESSAGE);
    }

    private void handleOpenFolder(ActionEvent e) {
        if (selectedProgress == null || selectedProgress.getDocumentoPath() == null) return;
        try {
            File file = new File(selectedProgress.getDocumentoPath());
            File folder = file.getParentFile();
            if (folder != null && folder.exists()) {
                Desktop.getDesktop().open(folder);
            } else {
                JOptionPane.showMessageDialog(this, "Cartella non trovata", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, IMPOSSIBILE_APRIRE_CARTELLA_PREFIX + ex.getMessage(), ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Renderer con righe zebra e tooltip automatico per migliorare leggibilità
     */
    private static class ZebraCellRenderer extends DefaultTableCellRenderer {
        private static final Color ODD = new Color(252, 252, 252);
        private static final Color EVEN = new Color(245, 247, 250);
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground((row % 2 == 0) ? EVEN : ODD);
            }
            if (value != null) {
                setToolTipText(value.toString());
            } else {
                setToolTipText(null);
            }
            return c;
        }
    }
    
    private void handleProgressSelection() {
        int selectedRow = progressTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < allProgress.size()) {
            selectedProgress = allProgress.get(selectedRow);
            displayProgressDetails();
            updateButtonStates();
            String hasFileText = (selectedProgress.getDocumentoPath() != null && !selectedProgress.getDocumentoPath().trim().isEmpty()) 
                                ? " 📎 (con file allegato - doppio-click per aprire)" 
                                : " (solo testo)";
            statusLabel.setText("Progresso selezionato: " + selectedProgress.getTitolo() + hasFileText);
        } else {
            selectedProgress = null;
            clearProgressDetails();
            updateButtonStates();
            statusLabel.setText("Seleziona un progresso per iniziare la revisione");
        }
    }
    
    private void displayProgressDetails() {
        if (selectedProgress == null) return;
        
        StringBuilder details = new StringBuilder();
        details.append("🏆 Team: ").append(getTeamName(selectedProgress.getTeamId())).append("\n");
        details.append("🎯 Hackathon: ").append(getHackathonName(selectedProgress.getHackathonId())).append("\n");
        if (selectedProgress.getDataCaricamento() != null) {
            details.append("📅 Caricato: ").append(selectedProgress.getDataCaricamento().format(DATE_FORMAT)).append("\n");
        }
        details.append("📝 Titolo: ").append(selectedProgress.getTitolo()).append("\n\n");
        
        if (selectedProgress.getDescrizione() != null && !selectedProgress.getDescrizione().trim().isEmpty()) {
            details.append("📄 Descrizione:\n");
            details.append(selectedProgress.getDescrizione()).append("\n\n");
        }
        
        if (selectedProgress.getDocumentoPath() != null) {
            details.append("📎 Documento allegato:\n");
            details.append(selectedProgress.getDocumentoPath()).append("\n");
        }
        
        progressDetailsArea.setText(details.toString());
        progressDetailsArea.setCaretPosition(0);
        
        // Carica commento esistente se presente
        if (selectedProgress.getCommentoGiudice() != null) {
            commentArea.setText(selectedProgress.getCommentoGiudice());
        } else {
            commentArea.setText("");
        }
    }
    
    private void clearProgressDetails() {
        progressDetailsArea.setText("");
        commentArea.setText("");
    }
    
    private void updateButtonStates() {
        boolean hasSelection = selectedProgress != null;
        boolean hasComment = hasSelection && selectedProgress.getCommentoGiudice() != null 
                           && !selectedProgress.getCommentoGiudice().trim().isEmpty();
        boolean hasDocument = hasSelection && selectedProgress.getDocumentoPath() != null &&
                             !selectedProgress.getDocumentoPath().trim().isEmpty();
        
        addCommentButton.setEnabled(hasSelection && !hasComment);
        updateCommentButton.setEnabled(hasSelection && hasComment);
        removeCommentButton.setEnabled(hasSelection && hasComment);
        viewDocumentButton.setEnabled(hasDocument);
        
        // Aggiorna il testo del bottone in base alla disponibilità del file
        if (hasDocument) {
            viewDocumentButton.setText(BTN_APRI_FILE);
            viewDocumentButton.setToolTipText("Clicca per aprire il file (download/ricrea se mancante). Doppio-click apre direttamente.");
        } else if (hasSelection) {
            viewDocumentButton.setText("📄 Nessun File");
            viewDocumentButton.setToolTipText("Questo progresso non ha file allegati");
        } else {
            viewDocumentButton.setText(BTN_APRI_FILE);
            viewDocumentButton.setToolTipText("Seleziona un progresso per aprire il file");
        }
    }
    
    private void handleAddComment(ActionEvent e) {
        if (selectedProgress == null || commentArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                INSERISCI_COMMENTO_MSG,
                COMMENTO_RICHIESTO_TITLE,
                JOptionPane.WARNING_MESSAGE);
            commentArea.requestFocus();
            return;
        }
        
        try {
            // Trova documento associato per consentire commenti per documento
            model.Documento doc = controller.getDocumentoByPercorso(selectedProgress.getTeamId(), selectedProgress.getDocumentoPath());
            if (doc == null) {
                JOptionPane.showMessageDialog(this,
                    "Nessun documento associato a questo progresso.",
                    "Documento mancante",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            var res = controller.aggiungiCommentoDocumento(doc.getId(), commentArea.getText().trim());
            boolean success = res.success;
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    COMMENTO_AGGIUNTO_MSG,
                    SUCCESSO_TITLE,
                    JOptionPane.INFORMATION_MESSAGE);
                loadProgressData(); // Ricarica per aggiornare la tabella
                parentFrame.showToast(COMMENTO_AGGIUNTO_MSG, SUCCESS_TOAST);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Errore nell'aggiunta del commento: " + res.error + (res.message != null ? (" - " + res.message) : ""),
                    ERRORE_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                ERRORE_PREFIX + ex.getMessage(),
                ERRORE_SISTEMA_TITLE,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleUpdateComment(ActionEvent e) {
        if (selectedProgress == null || commentArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                INSERISCI_COMMENTO_MSG,
                COMMENTO_RICHIESTO_TITLE,
                JOptionPane.WARNING_MESSAGE);
            commentArea.requestFocus();
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Sei sicuro di voler modificare il commento esistente?",
            "Conferma Modifica",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = controller.aggiornaCommentoGiudiceNuovo(
                    selectedProgress.getId(),
                    controller.getCurrentUser().getId(),
                    commentArea.getText().trim()
                );
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        COMMENTO_MODIFICATO_MSG,
                        SUCCESSO_TITLE,
                        JOptionPane.INFORMATION_MESSAGE);
                    loadProgressData();
                    parentFrame.showToast(COMMENTO_MODIFICATO_MSG, SUCCESS_TOAST);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Errore nella modifica del commento.",
                        ERRORE_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    ERRORE_PREFIX + ex.getMessage(),
                    ERRORE_SISTEMA_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleRemoveComment(ActionEvent e) {
        if (selectedProgress == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Sei sicuro di voler rimuovere il commento?\n" +
            "Questa azione non può essere annullata.",
            "Conferma Rimozione",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = controller.rimuoviCommentoGiudiceNuovo(
                    selectedProgress.getId(),
                    controller.getCurrentUser().getId()
                );
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        COMMENTO_RIMOSSO_MSG,
                        SUCCESSO_TITLE,
                        JOptionPane.INFORMATION_MESSAGE);
                    commentArea.setText("");
                    loadProgressData();
                    parentFrame.showToast(COMMENTO_RIMOSSO_MSG, SUCCESS_TOAST);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Errore nella rimozione del commento.",
                        ERRORE_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    ERRORE_PREFIX + ex.getMessage(),
                    ERRORE_SISTEMA_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleViewDocument(ActionEvent e) {
        if (selectedProgress == null || selectedProgress.getDocumentoPath() == null) return;
        
        // Determina se è un documento dalla tabella documents o un progresso con file
        if (selectedProgress.getTitolo().startsWith("📄")) {
            // È un documento dalla tabella documents - usa scaricaDocumento
            handleDocumentView();
        } else {
            // È un progresso con file - prova il percorso diretto
            handleProgressFileView();
        }
    }
    
    private void handleDocumentView() {
        try {
            // Trova il documento corrispondente
            List<Documento> documents = controller.getDocumentiHackathon(selectedProgress.getHackathonId());
            Documento targetDoc = null;
            
            for (Documento doc : documents) {
                if (doc.getPercorso().equals(selectedProgress.getDocumentoPath())) {
                    targetDoc = doc;
                    break;
                }
            }
            
            if (targetDoc == null) {
                JOptionPane.showMessageDialog(this,
                    DOCUMENTO_NON_TROVATO_DB,
                    ERRORE_TITLE,
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Scarica il contenuto del documento
            byte[] fileContent = controller.scaricaDocumento(targetDoc.getId());
            if (fileContent.length == 0) {
                JOptionPane.showMessageDialog(this,
                    IMPOSSIBILE_SCARICARE_DOCUMENTO,
                    "Errore Download",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ricrea il file nel percorso atteso se mancante e apri
            File expected = new File(targetDoc.getPercorso());
            try {
                if (!expected.exists()) {
                    File parent = expected.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }
                    try (java.io.FileOutputStream fos = new java.io.FileOutputStream(expected)) {
                        fos.write(fileContent);
                        fos.flush();
                    }
                }
                Desktop.getDesktop().open(expected);
                return;
            } catch (Exception openEx) {
                // Fallback: visualizzatore integrato
                showDocumentViewerDialog(targetDoc.getNome(), targetDoc.getTipo(), fileContent);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                ERRORE_CARICAMENTO_DOCUMENTO_PREFIX + ex.getMessage(),
                ERRORE_TITLE,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleProgressFileView() {
        File document = ottieniDocumentoPerVisualizzazione();
        if (document != null && document.exists()) {
            apriDocumento(document);
        }
    }
    
    /**
     * Ottiene il documento per la visualizzazione, ricreandolo se necessario
     */
    private File ottieniDocumentoPerVisualizzazione() {
        File document = new File(selectedProgress.getDocumentoPath());
        
        if (document.exists()) {
            return document;
        }
        
        // Prova a ricostruire il file
        File ricostruito = tentaRicostruzioneAutomatica();
        if (ricostruito != null && ricostruito.exists()) {
            return ricostruito;
        }
        
        // Chiedi all'utente di mappare manualmente
        return tentaRicostruzioneManuale();
    }
    
    /**
     * Tenta di ricostruire automaticamente il file dal database
     */
    private File tentaRicostruzioneAutomatica() {
        try {
            String expectedPath = selectedProgress.getDocumentoPath();
            String fileName = new File(expectedPath).getName();
            
            Documento candidate = cercaDocumentoPerNome(fileName);
            if (candidate == null) {
                return null;
            }
            
            byte[] bytes = controller.scaricaDocumento(candidate.getId());
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            
            return salvaFileRicostruito(expectedPath, bytes);
        } catch (Exception ignored) {
            return null;
        }
    }
    
    /**
     * Cerca un documento per nome nell'hackathon corrente
     */
    private Documento cercaDocumentoPerNome(String fileName) throws Exception {
        List<Documento> docs = controller.getDocumentiHackathon(selectedProgress.getHackathonId());
        for (Documento d : docs) {
            if (fileName.equalsIgnoreCase(d.getNome())) {
                return d;
            }
        }
        return null;
    }
    
    /**
     * Salva il file ricostruito nel percorso atteso
     */
    private File salvaFileRicostruito(String expectedPath, byte[] bytes) throws Exception {
        File expectedFile = new File(expectedPath);
        creaDirectorySeNecessario(expectedFile.getParentFile());
        
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(expectedFile)) {
            fos.write(bytes);
            fos.flush();
        }
        
        return expectedFile;
    }
    
    /**
     * Crea la directory se non esiste
     */
    private void creaDirectorySeNecessario(File parent) {
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }
    
    /**
     * Tenta la ricostruzione manuale chiedendo all'utente
     */
    private File tentaRicostruzioneManuale() {
        if (!chiediConfermaRicostruzioneManuale()) {
            return null;
        }
        
        File source = selezionaFileSorgente();
        if (source == null) {
            return null;
        }
        
        return copiaFileNelPercorsoAtteso(source);
    }
    
    /**
     * Chiede conferma all'utente per la ricostruzione manuale
     */
    private boolean chiediConfermaRicostruzioneManuale() {
        int choice = JOptionPane.showConfirmDialog(this,
            "File non trovato. Vuoi selezionare manualmente il file per ricrearlo nel percorso atteso?\n" +
            selectedProgress.getDocumentoPath(),
            "Ricrea File Mancante",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        return choice == JOptionPane.YES_OPTION;
    }
    
    /**
     * Apre il dialog per selezionare il file sorgente
     */
    private File selezionaFileSorgente() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleziona il file originale da copiare");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }
    
    /**
     * Copia il file sorgente nel percorso atteso
     */
    private File copiaFileNelPercorsoAtteso(File source) {
        try {
            File expectedFile = new File(selectedProgress.getDocumentoPath());
            creaDirectorySeNecessario(expectedFile.getParentFile());
            
            copiaContenutoFile(source, expectedFile);
            return expectedFile;
        } catch (Exception copyEx) {
            mostraErroreRicostruzioneFile(copyEx);
            return null;
        }
    }
    
    /**
     * Copia il contenuto di un file in un altro
     */
    private void copiaContenutoFile(File source, File dest) throws Exception {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(source);
             java.io.FileOutputStream fos = new java.io.FileOutputStream(dest)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
            fos.flush();
        }
    }
    
    /**
     * Mostra errore durante la ricostruzione del file
     */
    private void mostraErroreRicostruzioneFile(Exception copyEx) {
        JOptionPane.showMessageDialog(this,
            ERRORE_RICOSTRUZIONE_FILE_PREFIX + copyEx.getMessage(),
            ERRORE_TITLE,
            JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Apre il documento con l'applicazione predefinita
     */
    private void apriDocumento(File document) {
        try {
            Desktop.getDesktop().open(document);
        } catch (Exception ex) {
            mostraErroreAperturaDocumento(ex);
        }
    }
    
    /**
     * Mostra errore durante l'apertura del documento
     */
    private void mostraErroreAperturaDocumento(Exception ex) {
        JOptionPane.showMessageDialog(this,
            IMPOSSIBILE_APRIRE_DOCUMENTO_PREFIX + ex.getMessage(),
            ERRORE_APERTURA_TITLE,
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void showDocumentViewerDialog(String fileName, String mimeType, byte[] content) {
        SwingUtilities.invokeLater(() -> {
            DocumentViewerDialog viewer = new DocumentViewerDialog(
                this, fileName, mimeType, content);
            viewer.setVisible(true);
        });
    }
    
    private void handleManageComments() {
        if (selectedProgress == null) {
            JOptionPane.showMessageDialog(this,
                "Seleziona un progresso per gestire i commenti.",
                "Nessun Progresso Selezionato",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        DocumentCommentsDialog commentsDialog = new DocumentCommentsDialog(
            parentFrame, controller, selectedProgress);
        commentsDialog.setVisible(true);
        
        // Aggiorna la tabella dopo la chiusura del dialog
        loadProgressData();
    }
    
    private void loadProgressData() {
        statusLabel.setText("🔄 Caricamento progressi e documenti...");
        
        SwingWorker<List<Progress>, Void> worker = new SwingWorker<List<Progress>, Void>() {
            @Override
            protected List<Progress> doInBackground() throws Exception {
                // Carica tutti i progressi degli hackathon in corso
                List<Progress> allProgressList = new ArrayList<>();
                List<Hackathon> activeHackathons = controller.getHackathonInCorso();
                
                if (activeHackathons.isEmpty()) {
                    // Fallback: carica progressi di tutti gli hackathon
                    allProgressList = controller.getTuttiProgressi();
                    
                    // Aggiungi documenti di tutti gli hackathon disponibili
                    List<Hackathon> allHackathons = controller.getTuttiHackathon();
                    for (Hackathon h : allHackathons) {
                        List<Documento> hackathonDocs = controller.getDocumentiHackathon(h.getId());
                        addDocumentsAsProgress(allProgressList, hackathonDocs);
                    }
                } else {
                    for (Hackathon hackathon : activeHackathons) {
                        List<Progress> hackathonProgress = controller.getTuttiProgressi().stream()
                            .filter(p -> p.getHackathonId() == hackathon.getId())
                            .collect(java.util.stream.Collectors.toList());
                        allProgressList.addAll(hackathonProgress);
                        
                        // Aggiungi documenti per questo hackathon
                        List<Documento> hackathonDocs = controller.getDocumentiHackathon(hackathon.getId());
                        addDocumentsAsProgress(allProgressList, hackathonDocs);
                    }
                }
                
                return allProgressList;
            }
            
            @Override
            protected void done() {
                try {
                    allProgress = get();
                    updateProgressTable();
                    statusLabel.setText("✅ Caricati " + allProgress.size() + " elementi (progressi + documenti)");
                } catch (Exception e) {
                    statusLabel.setText(ERRORE_NEL_CARICAMENTO_PREFIX + e.getMessage());
                    JOptionPane.showMessageDialog(JudgeProgressReviewDialog.this,
                        ERRORE_CARICAMENTO_PROGRESSI_PREFIX + e.getMessage(),
                        ERRORE_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Converte i documenti in "progressi virtuali" per la visualizzazione unificata
     */
    private void addDocumentsAsProgress(List<Progress> progressList, List<Documento> documents) {
        for (Documento doc : documents) {
            // Crea un Progress "virtuale" dal Documento
            Progress virtualProgress = new Progress(
                doc.getTeamId(),
                doc.getHackathonId(),
                "📄 " + doc.getNome(), // Prefisso per distinguere i documenti
                "Documento caricato: " + doc.getTipo() + " (" + doc.getDimensione() + " bytes)",
                doc.getPercorso() // Usa il percorso del documento
            );
            // Imposta la data di caricamento
            virtualProgress.setDataCaricamento(doc.getDataCaricamento());
            progressList.add(virtualProgress);
        }
    }
    
    private void updateProgressTable() {
        tableModel.setRowCount(0);
        
        for (Progress progress : allProgress) {
            String teamName = getTeamName(progress.getTeamId());
            String hackathonName = getHackathonName(progress.getHackathonId());
            String date = progress.getDataCaricamento() != null ? progress.getDataCaricamento().format(DATE_FORMAT) : "";

            // Badge commenti
            int commentCount = 0;
            try { commentCount = controller.contaCommentiDocumentoNuovo(progress.getId()); } catch (Exception ignored) {}
            String commentsInfo = commentCount > 0 ? ("💬 " + commentCount) : "—";

            // Badge documento
            String hasDocument = (progress.getDocumentoPath() != null && !progress.getDocumentoPath().trim().isEmpty()) ? "📎 Sì" : "—";

            Object[] row = {teamName, hackathonName, progress.getTitolo(), date, commentsInfo, hasDocument};
            tableModel.addRow(row);
        }
        
        if (allProgress.isEmpty()) {
            Object[] emptyRow = {"Nessun progresso trovato", "", "", "", "No", ""};
            tableModel.addRow(emptyRow);
        }
    }
    
    private String getTeamName(int teamId) {
        try {
            Team team = controller.getTuttiTeam().stream()
                .filter(t -> t.getId() == teamId)
                .findFirst().orElse(null);
            return team != null ? team.getNome() : "Team " + teamId;
        } catch (Exception e) {
            return "Team " + teamId;
        }
    }
    
    private String getHackathonName(int hackathonId) {
        try {
            Hackathon hackathon = controller.getHackathonById(hackathonId);
            return hackathon != null ? hackathon.getNome() : "Hackathon " + hackathonId;
        } catch (Exception e) {
            return "Hackathon " + hackathonId;
        }
    }
}
