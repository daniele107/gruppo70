package gui;

import controller.Controller;
import model.Team;
import model.Hackathon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialog per il caricamento periodico dei progressi durante l'hackathon
 * Permette ai team di caricare documenti con descrizione dettagliata
 */
public class ProgressUploadDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIRECTORY = "uploads/progress";
    
    private final Controller controller;
    private final MainFrame parentFrame;
    private Team selectedTeam;
    
    // Components
    private JComboBox<Team> teamComboBox;
    private JTextField titoloField;
    private JTextArea descrizioneArea;
    private JLabel fileLabel;
    private JButton selectFileButton;
    private JButton uploadButton;
    private JButton cancelButton;
    private JProgressBar uploadProgress;
    private JLabel statusLabel;
    
    private File selectedFile;
    
    public ProgressUploadDialog(MainFrame parent, Controller controller) {
        super(parent, "Carica Progresso Team", true);
        this.parentFrame = parent;
        this.controller = controller;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadUserTeams();
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents() {
        teamComboBox = new JComboBox<>();
        teamComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Team team) {
                    setText(team.getNome() + " (Hackathon: " + getHackathonName(team.getHackathonId()) + ")");
                }
                return this;
            }
        });
        
        titoloField = new JTextField();
        titoloField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        descrizioneArea = new JTextArea(8, 40);
        descrizioneArea.setLineWrap(true);
        descrizioneArea.setWrapStyleWord(true);
        descrizioneArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descrizioneArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        
        fileLabel = new JLabel("Nessun file selezionato");
        fileLabel.setForeground(Color.GRAY);
        
        selectFileButton = new JButton("📁 Seleziona Documento");
        selectFileButton.setBackground(new Color(52, 152, 219));
        selectFileButton.setForeground(Color.WHITE);
        selectFileButton.setFocusPainted(false);
        
        uploadButton = new JButton("📤 Carica Progresso");
        uploadButton.setBackground(new Color(46, 204, 113));
        uploadButton.setForeground(Color.WHITE);
        uploadButton.setFocusPainted(false);
        uploadButton.setEnabled(false);
        
        cancelButton = new JButton("❌ Annulla");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        
        uploadProgress = new JProgressBar();
        uploadProgress.setStringPainted(true);
        uploadProgress.setVisible(false);
        
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📈 Caricamento Progresso Team");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel timeLabel = new JLabel(LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(Color.LIGHT_GRAY);
        headerPanel.add(timeLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Team selection
        JPanel teamPanel = createFieldPanel("🏆 Team:", teamComboBox);
        mainPanel.add(teamPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Title
        JPanel titlePanel = createFieldPanel("📝 Titolo Progresso:", titoloField);
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Description
        JLabel descLabel = new JLabel("📄 Descrizione Dettagliata:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        descLabel.setForeground(new Color(52, 73, 94));
        mainPanel.add(descLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        
        JScrollPane descScrollPane = new JScrollPane(descrizioneArea);
        descScrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        mainPanel.add(descScrollPane);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // File selection
        JLabel fileHeaderLabel = new JLabel("📎 Documento Allegato:");
        fileHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fileHeaderLabel.setForeground(new Color(52, 73, 94));
        mainPanel.add(fileHeaderLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.setOpaque(false);
        filePanel.add(selectFileButton);
        filePanel.add(fileLabel);
        mainPanel.add(filePanel);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Progress bar
        uploadProgress.setPreferredSize(new Dimension(0, 25));
        mainPanel.add(uploadProgress);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Status
        mainPanel.add(statusLabel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(uploadButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(52, 73, 94));
        
        field.setPreferredSize(new Dimension(0, 35));
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        teamComboBox.addActionListener(e -> {
            selectedTeam = (Team) teamComboBox.getSelectedItem();
            updateUploadButtonState();
        });
        
        titoloField.addActionListener(e -> updateUploadButtonState());
        
        selectFileButton.addActionListener(this::handleFileSelection);
        uploadButton.addActionListener(this::handleUpload);
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void handleFileSelection(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleziona Documento Progresso");
        
        // Filtri per tipi di file comuni
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
            "Documenti (*.pdf, *.doc, *.docx, *.txt)", "pdf", "doc", "docx", "txt"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
            "Immagini (*.png, *.jpg, *.jpeg, *.gif)", "png", "jpg", "jpeg", "gif"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
            "Codice (*.java, *.py, *.js, *.html, *.css)", "java", "py", "js", "html", "css"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
            "Archivi (*.zip, *.rar, *.7z)", "zip", "rar", "7z"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            fileLabel.setText(selectedFile.getName() + " (" + formatFileSize(selectedFile.length()) + ")");
            fileLabel.setForeground(new Color(46, 204, 113));
            updateUploadButtonState();
        }
    }
    
    private void handleUpload(ActionEvent e) {
        if (!validateInput()) return;
        
        uploadProgress.setVisible(true);
        uploadProgress.setIndeterminate(true);
        uploadProgress.setString("Caricamento in corso...");
        statusLabel.setText("📤 Preparazione upload...");
        
        uploadButton.setEnabled(false);
        selectFileButton.setEnabled(false);
        
        SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return performUpload();
            }
            
            @Override
            protected void process(List<String> chunks) {
                if (!chunks.isEmpty()) {
                    statusLabel.setText(chunks.get(chunks.size() - 1));
                }
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    uploadProgress.setIndeterminate(false);
                    
                    if (success) {
                        uploadProgress.setValue(100);
                        uploadProgress.setString("✅ Upload completato!");
                        statusLabel.setText("✅ Progresso caricato con successo!");
                        statusLabel.setForeground(new Color(46, 204, 113));
                        
                        Timer timer = new Timer(2000, evt -> dispose());
                        timer.setRepeats(false);
                        timer.start();
                        
                        parentFrame.showToast("Progresso caricato con successo!", "success");
                    } else {
                        uploadProgress.setString("❌ Upload fallito");
                        statusLabel.setText("❌ Errore durante il caricamento");
                        statusLabel.setForeground(new Color(231, 76, 60));
                        uploadButton.setEnabled(true);
                        selectFileButton.setEnabled(true);
                    }
                } catch (Exception ex) {
                    uploadProgress.setString("❌ Errore");
                    statusLabel.setText("❌ Errore: " + ex.getMessage());
                    statusLabel.setForeground(new Color(231, 76, 60));
                    uploadButton.setEnabled(true);
                    selectFileButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private Boolean performUpload() throws Exception {
                // Creazione directory
        
        // Crea directory se non esiste
        Path uploadDir = Paths.get(UPLOAD_DIRECTORY, 
            String.valueOf(selectedTeam.getHackathonId()),
            String.valueOf(selectedTeam.getId()));
        Files.createDirectories(uploadDir);
        
        // Copia file
        
        // Genera nome file unico
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = timestamp + "_" + selectedFile.getName();
        Path targetPath = uploadDir.resolve(fileName);
        
        // Copia file
        Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        // Salvataggio nel database
        
        // Salva progresso nel database
        int progressId = controller.caricaProgresso(
            selectedTeam.getId(),
            titoloField.getText().trim(),
            descrizioneArea.getText().trim(),
            targetPath.toString()
        );
        
        return progressId > 0;
    }
    
    private boolean validateInput() {
        if (selectedTeam == null) {
            showError("Seleziona un team");
            return false;
        }
        
        if (titoloField.getText().trim().isEmpty()) {
            showError("Inserisci il titolo del progresso");
            titoloField.requestFocus();
            return false;
        }
        
        if (descrizioneArea.getText().trim().isEmpty()) {
            showError("Inserisci una descrizione del progresso");
            descrizioneArea.requestFocus();
            return false;
        }
        
        if (selectedFile == null) {
            showError("Seleziona un documento da allegare");
            return false;
        }
        
        // Verifica dimensione file (max 50MB)
        if (selectedFile.length() > 50 * 1024 * 1024) {
            showError("Il file è troppo grande (max 50MB)");
            return false;
        }
        
        return true;
    }
    
    private void updateUploadButtonState() {
        boolean canUpload = selectedTeam != null && 
                           !titoloField.getText().trim().isEmpty() &&
                           selectedFile != null;
        uploadButton.setEnabled(canUpload);
    }
    
    private void loadUserTeams() {
        try {
            List<Team> userTeams = controller.getTeamUtente();
            teamComboBox.removeAllItems();
            
            for (Team team : userTeams) {
                // Verifica che l'hackathon sia in corso
                Hackathon hackathon = controller.getHackathonById(team.getHackathonId());
                if (hackathon != null && hackathon.isEventoAvviato() && !hackathon.isEventoConcluso()) {
                    teamComboBox.addItem(team);
                }
            }
            
            if (teamComboBox.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this,
                    "Non hai team in hackathon attivi.\n" +
                    "Puoi caricare progressi solo durante hackathon in corso.",
                    "Nessun Team Disponibile",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (Exception e) {
            showError("Errore nel caricamento dei team: " + e.getMessage());
        }
    }
    
    private String getHackathonName(int hackathonId) {
        try {
            Hackathon hackathon = controller.getHackathonById(hackathonId);
            return hackathon != null ? hackathon.getNome() : "Sconosciuto";
        } catch (Exception e) {
            return "Sconosciuto";
        }
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Errore", JOptionPane.ERROR_MESSAGE);
    }
}
