package gui;

import controller.Controller;
import model.Team;
import model.Utente;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Dialog avanzato per l'upload di file e documenti.
 * Fornisce validazioni complete, anteprima e gestione errori.
 */
public class FileUploadDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(FileUploadDialog.class.getName());
    
    // Costanti per validazione
    private static final long MAX_FILE_SIZE = 52428800L; // 50MB
    private static final String ERROR_TITLE = "Errore";
    private static final String[] ALLOWED_EXTENSIONS = {
        "pdf", "doc", "docx", "txt", "md", "zip", "rar", 
        "jpg", "jpeg", "png", "gif", "mp4", "avi", "mov"
    };
    private static final String[] ALLOWED_MIME_TYPES = {
        "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "text/plain", "text/markdown", "application/zip", "application/x-rar-compressed",
        "image/jpeg", "image/png", "image/gif", "video/mp4", "video/x-msvideo", "video/quicktime"
    };
    
    // Componenti GUI
    private final transient Controller controller;
    private final JFrame ownerFrame;
    private JComboBox<TeamItem> teamComboBox;
    private JTextField nomeFileField;
    private JTextArea descrizioneArea;
    private JLabel filePathLabel;
    private JLabel fileSizeLabel;
    private JLabel fileTypeLabel;
    private JProgressBar uploadProgressBar;
    private JButton scegliFileButton;
    private JButton uploadButton;
    private JButton annullaButton;
    private JButton anteprimaButton;
    
    // Dati del file selezionato
    private File selectedFile;
    // Hash calcolato lato controller; non richiesto qui
    private boolean uploadInProgress = false;
    
    /**
     * Costruttore del dialog di upload
     *
     * @param parent il frame genitore
     * @param controller il controller dell'applicazione
     */
    public FileUploadDialog(JFrame parent, Controller controller) {
        super(parent, "📤 Carica Documento", true);
        this.ownerFrame = parent;
        this.controller = controller;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadTeams();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(ownerFrame);
        setResizable(false);
    }
    
    /**
     * Inizializza i componenti della GUI
     */
    private void initializeComponents() {
        // Team selection
        teamComboBox = new JComboBox<>();
        teamComboBox.setRenderer(new TeamComboBoxRenderer());
        
        // File info fields
        nomeFileField = new JTextField(30);
        descrizioneArea = new JTextArea(4, 30);
        descrizioneArea.setLineWrap(true);
        descrizioneArea.setWrapStyleWord(true);
        descrizioneArea.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // File details labels
        filePathLabel = new JLabel("Nessun file selezionato");
        fileSizeLabel = new JLabel("Dimensione: -");
        fileTypeLabel = new JLabel("Tipo: -");
        
        // Progress bar
        uploadProgressBar = new JProgressBar(0, 100);
        uploadProgressBar.setStringPainted(true);
        uploadProgressBar.setString("Pronto per l'upload");
        uploadProgressBar.setVisible(false);
        
        // Buttons
        scegliFileButton = createStyledButton("📁 Scegli File", new Color(52, 152, 219));
        uploadButton = createStyledButton("⬆️ Carica", new Color(46, 204, 113));
        annullaButton = createStyledButton("❌ Annulla", new Color(231, 76, 60));
        anteprimaButton = createStyledButton("👁️ Anteprima", new Color(155, 89, 182));
        
        uploadButton.setEnabled(false);
        anteprimaButton.setEnabled(false);
    }
    
    /**
     * Configura il layout della finestra
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("📤 Carica Nuovo Documento");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 73, 94));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Team selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Team:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(teamComboBox, gbc);
        
        // Nome file
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Nome documento:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(nomeFileField, gbc);
        
        // Descrizione
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Descrizione:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        formPanel.add(new JScrollPane(descrizioneArea), gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // File selection panel
        JPanel filePanel = new JPanel(new BorderLayout(10, 10));
        filePanel.setBorder(new TitledBorder("Selezione File"));
        filePanel.setBackground(Color.WHITE);
        
        JPanel fileButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fileButtonPanel.setBackground(Color.WHITE);
        fileButtonPanel.add(scegliFileButton);
        fileButtonPanel.add(anteprimaButton);
        
        JPanel fileInfoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        fileInfoPanel.setBackground(Color.WHITE);
        fileInfoPanel.add(filePathLabel);
        fileInfoPanel.add(fileSizeLabel);
        fileInfoPanel.add(fileTypeLabel);
        
        filePanel.add(fileButtonPanel, BorderLayout.NORTH);
        filePanel.add(fileInfoPanel, BorderLayout.CENTER);
        
        mainPanel.add(filePanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
        
        // Bottom panel with progress and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        bottomPanel.add(uploadProgressBar, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(uploadButton);
        buttonPanel.add(annullaButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        scegliFileButton.addActionListener(this::handleFileSelection);
        uploadButton.addActionListener(this::handleUpload);
        annullaButton.addActionListener(e -> dispose());
        anteprimaButton.addActionListener(this::handlePreview);
        
        // Auto-fill nome file quando viene selezionato
        nomeFileField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateUploadButtonState();
            }
        });

        // Drag & Drop area: abilita drop del file sull'intero dialog
        new java.awt.dnd.DropTarget(this, new java.awt.dnd.DropTargetListener() {
            @Override public void dragEnter(java.awt.dnd.DropTargetDragEvent dtde) { 
                // Metodo vuoto intenzionalmente - gestisce l'ingresso del drag
            }
            @Override public void dragOver(java.awt.dnd.DropTargetDragEvent dtde) { 
                // Metodo vuoto intenzionalmente - gestisce il movimento durante il drag
            }
            @Override public void dropActionChanged(java.awt.dnd.DropTargetDragEvent dtde) { 
                // Metodo vuoto intenzionalmente - gestisce il cambio di azione del drop
            }
            @Override public void dragExit(java.awt.dnd.DropTargetEvent dte) { 
                // Metodo vuoto intenzionalmente - gestisce l'uscita dal drag
            }
            @Override
            public void drop(java.awt.dnd.DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
                    java.util.List<?> dropped = (java.util.List<?>) dtde.getTransferable()
                        .getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                    if (dropped != null && !dropped.isEmpty() && dropped.get(0) instanceof java.io.File) {
                        selectedFile = (java.io.File) dropped.get(0);
                        updateFileInfo();
                        if (nomeFileField.getText().trim().isEmpty()) {
                            nomeFileField.setText(selectedFile.getName());
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FileUploadDialog.this,
                        "Errore nel drop del file: " + ex.getMessage(), ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    
    /**
     * Carica i team disponibili per l'utente corrente
     */
    private void loadTeams() {
        teamComboBox.removeAllItems();
        teamComboBox.addItem(new TeamItem(null, "Seleziona un team..."));
        
        try {
            Utente currentUser = controller.getCurrentUser();
            logger.log(Level.FINE, "Current user = {0}", (currentUser != null ? currentUser.getLogin() + " (ID: " + currentUser.getId() + ")" : "null"));
            
            if (currentUser != null) {
                List<Team> userTeams = controller.getTeamUtente();
                logger.log(Level.FINE, "Found {0} teams for user", userTeams.size());
                
                for (Team team : userTeams) {
                    logger.log(Level.FINE, "Adding team: {0} (ID: {1})", new Object[]{team.getNome(), team.getId()});
                    teamComboBox.addItem(new TeamItem(team, team.getNome()));
                }
                
                if (userTeams.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Non fai parte di nessun team.\nContatta l'organizzatore per essere aggiunto a un team.",
                        "Nessun Team Disponibile", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Errore: utente non autenticato.\nEffettua il login prima di caricare documenti.",
                    ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception in loadTeams: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this,
                "Errore nel caricamento dei team: " + e.getMessage(),
                ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Gestisce la selezione del file
     */
    private void handleFileSelection(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleziona documento da caricare");
        
        // Filtri per tipi di file
        FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf");
        FileNameExtensionFilter docFilter = new FileNameExtensionFilter("Word Documents (*.doc, *.docx)", "doc", "docx");
        FileNameExtensionFilter textFilter = new FileNameExtensionFilter("Text Files (*.txt, *.md)", "txt", "md");
        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("Images (*.jpg, *.png, *.gif)", "jpg", "jpeg", "png", "gif");
        FileNameExtensionFilter videoFilter = new FileNameExtensionFilter("Videos (*.mp4, *.avi, *.mov)", "mp4", "avi", "mov");
        FileNameExtensionFilter archiveFilter = new FileNameExtensionFilter("Archives (*.zip, *.rar)", "zip", "rar");
        
        fileChooser.addChoosableFileFilter(pdfFilter);
        fileChooser.addChoosableFileFilter(docFilter);
        fileChooser.addChoosableFileFilter(textFilter);
        fileChooser.addChoosableFileFilter(imageFilter);
        fileChooser.addChoosableFileFilter(videoFilter);
        fileChooser.addChoosableFileFilter(archiveFilter);
        fileChooser.setAcceptAllFileFilterUsed(true);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            updateFileInfo();
            
            // Auto-fill nome se vuoto
            if (nomeFileField.getText().trim().isEmpty()) {
                nomeFileField.setText(selectedFile.getName());
            }
        }
    }
    
    /**
     * Aggiorna le informazioni del file selezionato
     */
    private void updateFileInfo() {
        if (selectedFile == null) {
            filePathLabel.setText("Nessun file selezionato");
            fileSizeLabel.setText("Dimensione: -");
            fileTypeLabel.setText("Tipo: -");
            anteprimaButton.setEnabled(false);
            updateUploadButtonState();
            return;
        }
        
        try {
            // Path
            filePathLabel.setText("File: " + selectedFile.getName());
            
            // Size
            long sizeBytes = selectedFile.length();
            String sizeText = formatFileSize(sizeBytes);
            fileSizeLabel.setText("Dimensione: " + sizeText);
            
            // Type
            String mimeType = Files.probeContentType(selectedFile.toPath());
            fileTypeLabel.setText("Tipo: " + (mimeType != null ? mimeType : "Sconosciuto"));
            
            // Validazione
            String validationError = validateFile(selectedFile);
            if (validationError != null) {
                fileSizeLabel.setText(fileSizeLabel.getText() + " ⚠️");
                fileTypeLabel.setText(fileTypeLabel.getText() + " - " + validationError);
                fileTypeLabel.setForeground(Color.RED);
            } else {
                fileTypeLabel.setForeground(Color.BLACK);
            }
            
            anteprimaButton.setEnabled(isPreviewable(selectedFile));
            updateUploadButtonState();
            
        } catch (Exception ex) {
            fileTypeLabel.setText("Errore nel leggere il file: " + ex.getMessage());
            fileTypeLabel.setForeground(Color.RED);
        }
    }
    
    /**
     * Valida il file selezionato
     */
    private String validateFile(File file) {
        // Controllo dimensione
        if (file.length() > MAX_FILE_SIZE) {
            return "File troppo grande (max " + formatFileSize(MAX_FILE_SIZE) + ")";
        }
        
        // Controllo estensione
        String fileName = file.getName().toLowerCase();
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        if (!Arrays.asList(ALLOWED_EXTENSIONS).contains(extension)) {
            return "Tipo file non supportato";
        }
        
        // Controllo MIME type
        try {
            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType != null && !Arrays.asList(ALLOWED_MIME_TYPES).contains(mimeType)) {
                return "Formato file non valido";
            }
        } catch (IOException e) {
            return "Impossibile verificare il tipo file";
        }
        
        return null; // File valido
    }
    
    // Calcolo hash spostato nel controller durante il caricamento effettivo
    
    /**
     * Conferma l'upload con l'utente
     */
    private boolean confirmUpload(TeamItem selectedTeam, String nomeFile) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Confermi il caricamento del documento?\n\n" +
            "Team: " + selectedTeam.displayName + "\n" +
            "Nome: " + nomeFile + "\n" +
            "File: " + selectedFile.getName() + "\n" +
            "Dimensione: " + formatFileSize(selectedFile.length()),
            "Conferma Upload", JOptionPane.YES_NO_OPTION);
        
        return confirm == JOptionPane.YES_OPTION;
    }
    
    /**
     * Crea il worker per l'upload in background
     */
    private SwingWorker<Boolean, Integer> createUploadWorker(TeamItem selectedTeam, String nomeFile) {
        return new SwingWorker<Boolean, Integer>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return performFileUpload(selectedTeam, nomeFile, progress -> publish(progress));
            }
            
            @Override
            protected void process(List<Integer> chunks) {
                if (!chunks.isEmpty()) {
                    int progress = chunks.get(chunks.size() - 1);
                    uploadProgressBar.setIndeterminate(false);
                    uploadProgressBar.setValue(progress);
                    uploadProgressBar.setString("Caricamento... " + progress + "%");
                }
            }
            
            @Override
            protected void done() {
                uploadInProgress = false;
                uploadButton.setEnabled(true);
                
                try {
                    boolean success = get();
                    if (success) {
                        uploadProgressBar.setString("✅ Upload completato!");
                        uploadProgressBar.setForeground(new Color(46, 204, 113));
                        
                        JOptionPane.showMessageDialog(FileUploadDialog.this,
                            "Documento caricato con successo!",
                            "Upload Completato", JOptionPane.INFORMATION_MESSAGE);
                        
                        dispose();
                    } else {
                        uploadProgressBar.setString("❌ Upload fallito");
                        uploadProgressBar.setForeground(Color.RED);
                        JOptionPane.showMessageDialog(FileUploadDialog.this,
                            "Errore durante il caricamento del documento",
                            ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    uploadProgressBar.setString("❌ Upload interrotto");
                    uploadProgressBar.setForeground(Color.ORANGE);
                    JOptionPane.showMessageDialog(FileUploadDialog.this,
                        "Upload interrotto dall'utente",
                        "Upload Interrotto", JOptionPane.WARNING_MESSAGE);
                } catch (Exception e) {
                    uploadProgressBar.setString("❌ Errore: " + e.getMessage());
                    uploadProgressBar.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(FileUploadDialog.this,
                        "Errore: " + e.getMessage(),
                        "Errore Upload", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
    }
    
    /**
     * Esegue l'upload del file con reporting del progresso
     */
    private Boolean performFileUpload(TeamItem selectedTeam, String nomeFile, 
                                     java.util.function.Consumer<Integer> progressCallback) throws UploadException {
        try {
            // Simula copia file (in un sistema reale, qui ci sarebbe l'upload al server)
            // Percorso generato (non utilizzato direttamente nel caricamento completo)
            createUploadPath(selectedTeam.team.getId(), selectedFile.getName());
            
            progressCallback.accept(25);
            Thread.sleep(500); // Simula tempo di upload
            
            progressCallback.accept(50);
            
            // Determina tipo MIME
            String mimeType = determineMimeType();
            
            progressCallback.accept(75);
            
            // Salva fisicamente e nel database tramite controller
            boolean success = uploadToController(selectedTeam, nomeFile, mimeType);
            
            progressCallback.accept(100);
            
            return success;
            
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new UploadException("Upload interrotto dall'utente", ie);
        } catch (UploadException e) {
            throw e; // Re-throw UploadException as-is
        } catch (Exception e) {
            throw new UploadException("Errore durante l'upload: " + e.getMessage(), e);
        }
    }
    
    /**
     * Determina il tipo MIME del file selezionato
     */
    private String determineMimeType() throws UploadException {
        try {
            String mimeType = Files.probeContentType(selectedFile.toPath());
            return mimeType != null ? mimeType : "application/octet-stream";
        } catch (Exception e) {
            throw new UploadException("Errore nel determinare il tipo MIME del file", e);
        }
    }
    
    /**
     * Carica il file tramite il controller
     */
    private boolean uploadToController(TeamItem selectedTeam, String nomeFile, String mimeType) throws UploadException {
        try {
            byte[] contenuto = Files.readAllBytes(selectedFile.toPath());
            return controller.caricaDocumentoConContenuto(
                selectedTeam.team.getId(),
                nomeFile,
                descrizioneArea.getText().trim(),
                mimeType,
                contenuto
            );
        } catch (Exception e) {
            throw new UploadException("Errore nel caricamento del file tramite controller", e);
        }
    }
    
    /**
     * Gestisce l'upload del file
     */
    private void handleUpload(ActionEvent e) {
        logger.log(Level.FINE, "handleUpload called, uploadInProgress = {0}", uploadInProgress);
        if (uploadInProgress) return;
        
        // Validazioni
        TeamItem selectedTeam = (TeamItem) teamComboBox.getSelectedItem();
        logger.log(Level.FINE, "selectedTeam = {0}", (selectedTeam != null ? selectedTeam.toString() : "null"));
        if (selectedTeam == null || selectedTeam.team == null) {
            logger.log(Level.FINE, "Team validation failed");
            JOptionPane.showMessageDialog(this, "Seleziona un team", ERROR_TITLE, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nomeFile = nomeFileField.getText().trim();
        logger.log(Level.FINE, "nomeFile = '{0}'", nomeFile);
        if (nomeFile.isEmpty()) {
            logger.log(Level.FINE, "Nome file validation failed");
            JOptionPane.showMessageDialog(this, "Inserisci un nome per il documento", ERROR_TITLE, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        logger.log(Level.FINE, "selectedFile = {0}", (selectedFile != null ? selectedFile.getName() : "null"));
        if (selectedFile == null) {
            logger.log(Level.FINE, "File selection validation failed");
            JOptionPane.showMessageDialog(this, "Seleziona un file", ERROR_TITLE, JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String validationError = validateFile(selectedFile);
        logger.log(Level.FINE, "File validation result = {0}", (validationError != null ? "FAILED: " + validationError : "PASSED"));
        if (validationError != null) {
            JOptionPane.showMessageDialog(this, "File non valido: " + validationError, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Conferma upload
        logger.log(Level.FINE, "Showing confirmation dialog");
        if (!confirmUpload(selectedTeam, nomeFile)) {
            logger.log(Level.FINE, "Upload cancelled by user");
            return;
        }
        
        logger.log(Level.FINE, "Starting upload worker");
        // Avvia upload in background
        uploadInProgress = true;
        uploadButton.setEnabled(false);
        uploadProgressBar.setVisible(true);
        uploadProgressBar.setIndeterminate(true);
        uploadProgressBar.setString("Caricamento in corso...");
        
        SwingWorker<Boolean, Integer> uploadWorker = createUploadWorker(selectedTeam, nomeFile);
        uploadWorker.execute();
    }
    
    /**
     * Gestisce l'anteprima del file
     */
    private void handlePreview(ActionEvent e) {
        if (selectedFile == null) return;
        
        try {
            // Apri con applicazione predefinita del sistema
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(selectedFile);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Anteprima non supportata su questo sistema",
                    "Anteprima", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Impossibile aprire il file per l'anteprima: " + ex.getMessage(),
                "Errore Anteprima", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Aggiorna lo stato del pulsante upload
     */
    private void updateUploadButtonState() {
        boolean canUpload = selectedFile != null && 
                          !nomeFileField.getText().trim().isEmpty() && 
                          teamComboBox.getSelectedItem() != null &&
                          ((TeamItem) teamComboBox.getSelectedItem()).team != null &&
                          validateFile(selectedFile) == null &&
                          !uploadInProgress;
        uploadButton.setEnabled(canUpload);
    }
    
    /**
     * Crea un pulsante con stile moderno
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    /**
     * Formatta la dimensione del file in formato leggibile
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024L) return bytes + " B";
        if (bytes < 1048576L) return String.valueOf(Math.round(bytes / 102.4) / 10.0) + " KB";
        if (bytes < 1073741824L) return String.valueOf(Math.round(bytes / (102.4 * 1024.0)) / 10.0) + " MB";
        return String.valueOf(Math.round(bytes / (102.4 * 1024.0 * 1024.0)) / 10.0) + " GB";
    }
    
    /**
     * Verifica se il file può essere visualizzato in anteprima
     */
    private boolean isPreviewable(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".txt") || fileName.endsWith(".pdf") || 
               fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
               fileName.endsWith(".png") || fileName.endsWith(".gif");
    }
    
    /**
     * Crea il percorso di upload per il file
     */
    private String createUploadPath(int teamId, String fileName) {
        return "/uploads/team" + teamId + "/" + System.currentTimeMillis() + "_" + fileName;
    }
    
    /**
     * Eccezione personalizzata per errori di upload
     */
    private static class UploadException extends Exception {
        private static final long serialVersionUID = 1L;
        
        public UploadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Classe per rappresentare un item del team nella combobox
     */
    private static class TeamItem {
        final Team team;
        final String displayName;
        
        TeamItem(Team team, String displayName) {
            this.team = team;
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    /**
     * Renderer personalizzato per la combobox dei team
     */
    private static class TeamComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof TeamItem item) {
                setText(item.displayName);
                if (item.team == null) {
                    setForeground(Color.GRAY);
                    setFont(getFont().deriveFont(Font.ITALIC));
                }
            }
            
            return this;
        }
    }
}