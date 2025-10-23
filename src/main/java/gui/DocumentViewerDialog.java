package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Dialog per la visualizzazione integrata dei documenti
 * Supporta visualizzazione di testo, immagini e download
 */
public class DocumentViewerDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    // Costanti per font
    private static final String FONT_FAMILY = "Segoe UI";
    
    private final String fileName;
    private final String mimeType;
    private final byte[] content;
    
    // Components
    private JLabel titleLabel;
    private JLabel infoLabel;
    private JScrollPane contentScrollPane;
    private JTextArea textContentArea;
    private JLabel imageLabel;
    private JButton downloadButton;
    private JButton closeButton;
    
    public DocumentViewerDialog(JDialog parent, String fileName, String mimeType, byte[] content) {
        super(parent, "Visualizzatore Documenti", true);
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.content = content;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadContent();
        
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents() {
        // Title
        titleLabel = new JLabel("📄 " + fileName);
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 16));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        // Info
        String sizeText = formatFileSize(content.length);
        String typeText = getFileTypeDescription(mimeType);
        infoLabel = new JLabel(String.format("Tipo: %s | Dimensione: %s", typeText, sizeText));
        infoLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        infoLabel.setForeground(Color.GRAY);
        
        // Content area
        textContentArea = new JTextArea();
        textContentArea.setEditable(false);
        textContentArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textContentArea.setBackground(new Color(248, 249, 250));
        textContentArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        contentScrollPane = new JScrollPane();
        contentScrollPane.setBorder(BorderFactory.createTitledBorder("Contenuto"));
        
        // Buttons
        downloadButton = new JButton("💾 Scarica File");
        downloadButton.setBackground(new Color(70, 130, 180)); // Blu scuro
        downloadButton.setForeground(Color.BLACK);
        downloadButton.setOpaque(true);
        downloadButton.setBorderPainted(true);
        downloadButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        downloadButton.setFocusPainted(false);
        downloadButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        closeButton = new JButton("❌ Chiudi");
        closeButton.setBackground(new Color(178, 34, 34)); // Rosso scuro
        closeButton.setForeground(Color.BLACK);
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(true);
        closeButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        closeButton.setFocusPainted(false);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(236, 240, 241));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(infoLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Content
        add(contentScrollPane, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(236, 240, 241));
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        footerPanel.add(downloadButton);
        footerPanel.add(Box.createHorizontalStrut(10));
        footerPanel.add(closeButton);
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        downloadButton.addActionListener(this::handleDownload);
        closeButton.addActionListener(e -> dispose());
    }
    
    private void loadContent() {
        if (isTextFile(mimeType)) {
            loadTextContent();
        } else if (isImageFile(mimeType)) {
            loadImageContent();
        } else {
            loadUnsupportedContent();
        }
    }
    
    private void loadTextContent() {
        try {
            String text = new String(content, StandardCharsets.UTF_8);
            textContentArea.setText(text);
            contentScrollPane.setViewportView(textContentArea);
        } catch (Exception e) {
            textContentArea.setText("Errore nella lettura del file di testo:\n" + e.getMessage());
            contentScrollPane.setViewportView(textContentArea);
        }
    }
    
    private void loadImageContent() {
        try {
            ImageIcon icon = new ImageIcon(content);
            Image image = icon.getImage();
            
            // Scale image if too large
            int maxWidth = 700;
            int maxHeight = 500;
            
            if (image.getWidth(null) > maxWidth || image.getHeight(null) > maxHeight) {
                double scaleX = (double) maxWidth / image.getWidth(null);
                double scaleY = (double) maxHeight / image.getHeight(null);
                double scale = Math.min(scaleX, scaleY);
                
                int newWidth = (int) (image.getWidth(null) * scale);
                int newHeight = (int) (image.getHeight(null) * scale);
                
                image = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            }
            
            imageLabel.setIcon(new ImageIcon(image));
            contentScrollPane.setViewportView(imageLabel);
        } catch (Exception e) {
            imageLabel.setText("Errore nel caricamento dell'immagine:\n" + e.getMessage());
            contentScrollPane.setViewportView(imageLabel);
        }
    }
    
    private void loadUnsupportedContent() {
        JPanel unsupportedPanel = new JPanel(new BorderLayout());
        unsupportedPanel.setBackground(new Color(248, 249, 250));
        
        JLabel messageLabel = new JLabel(
            "<html><div style='text-align: center; padding: 20px;'>" +
            "<h3>📄 File non visualizzabile</h3>" +
            "<p>Questo tipo di file non può essere visualizzato direttamente.</p>" +
            "<p>Usa il pulsante 'Scarica File' per aprirlo con l'applicazione predefinita.</p>" +
            "<br>" +
            "<p><b>Tipo:</b> " + getFileTypeDescription(mimeType) + "</p>" +
            "<p><b>Dimensione:</b> " + formatFileSize(content.length) + "</p>" +
            "</div></html>"
        );
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        
        unsupportedPanel.add(messageLabel, BorderLayout.CENTER);
        contentScrollPane.setViewportView(unsupportedPanel);
    }
    
    private void handleDownload(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salva documento");
        fileChooser.setSelectedFile(new File(fileName));
        
        // Set suggested directory to Downloads
        String userHome = System.getProperty("user.home");
        fileChooser.setCurrentDirectory(new File(userHome + "/Downloads"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            try (FileOutputStream fos = new FileOutputStream(selectedFile)) {
                fos.write(content);
                fos.flush();
                
                JOptionPane.showMessageDialog(this,
                    "File salvato con successo in:\n" + selectedFile.getAbsolutePath(),
                    "Download Completato",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Ask if user wants to open the file
                int openResult = JOptionPane.showConfirmDialog(this,
                    "Vuoi aprire il file con l'applicazione predefinita?",
                    "Apri File",
                    JOptionPane.YES_NO_OPTION);
                
                if (openResult == JOptionPane.YES_OPTION) {
                    openFileWithDefaultApplication(selectedFile);
                }
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Errore durante il salvataggio:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean isTextFile(String mimeType) {
        return mimeType != null && (
            mimeType.startsWith("text/") ||
            mimeType.equals("application/json") ||
            mimeType.equals("application/xml") ||
            mimeType.equals("text/xml")
        );
    }
    
    private boolean isImageFile(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }
    
    private String getFileTypeDescription(String mimeType) {
        if (mimeType == null) return "Sconosciuto";
        
        switch (mimeType) {
            case "application/pdf": return "PDF Document";
            case "application/msword": return "Word Document (.doc)";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document": return "Word Document (.docx)";
            case "text/plain": return "Text File";
            case "text/markdown": return "Markdown File";
            case "application/json": return "JSON File";
            case "application/xml": return "XML File";
            case "image/jpeg": return "JPEG Image";
            case "image/png": return "PNG Image";
            case "image/gif": return "GIF Image";
            case "application/zip": return "ZIP Archive";
            case "application/x-rar-compressed": return "RAR Archive";
            default: return mimeType;
        }
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
    
    /**
     * Apre il file con l'applicazione predefinita del sistema
     * @param file il file da aprire
     */
    private void openFileWithDefaultApplication(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Impossibile aprire il file:\n" + ex.getMessage(),
                "Errore",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
