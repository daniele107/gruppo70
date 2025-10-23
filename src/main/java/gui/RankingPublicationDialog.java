package gui;

import controller.Controller;
import model.Hackathon;
import model.RankingSnapshot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;

/**
 * Dialog per la pubblicazione della classifica finale di un hackathon
 * Solo gli organizzatori possono pubblicare le classifiche
 */
public class RankingPublicationDialog extends JDialog {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String DIALOG_TITLE = "Pubblicazione Classifica";
    private static final String PREVIEW_LABEL = "🔍 Anteprima";
    private static final String PUBLISH_LABEL = "📢 Pubblica";
    private static final String VIEW_PUBLISHED_LABEL = "📊 Visualizza Pubblicata";
    private static final String CLOSE_LABEL = "Chiudi";
    
    private final transient Controller controller;
    private final transient Hackathon hackathon;
    private final MainFrame parentFrame;
    
    // GUI Components
    private JTextArea previewArea;
    private JTextArea overrideReasonArea;
    private JButton previewButton;
    private JButton publishButton;
    private JButton viewPublishedButton;
    private JLabel statusLabel;
    private JLabel hackathonInfoLabel;
    private JCheckBox forcePublishCheckBox;
    
    public RankingPublicationDialog(MainFrame parent, Controller controller, Hackathon hackathon) {
        super(parent, DIALOG_TITLE, true);
        this.parentFrame = parent;
        this.controller = controller;
        this.hackathon = hackathon;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        updateStatus();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        // Info hackathon
        hackathonInfoLabel = new JLabel("Hackathon: " + hackathon.getNome());
        hackathonInfoLabel.setFont(hackathonInfoLabel.getFont().deriveFont(Font.BOLD, 16f));
        
        // Area anteprima
        previewArea = new JTextArea(20, 60);
        previewArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        previewArea.setEditable(false);
        previewArea.setBorder(BorderFactory.createTitledBorder("Anteprima Classifica"));
        
        // Area motivo override
        overrideReasonArea = new JTextArea(3, 60);
        overrideReasonArea.setLineWrap(true);
        overrideReasonArea.setWrapStyleWord(true);
        overrideReasonArea.setBorder(BorderFactory.createTitledBorder("Motivo Override (opzionale)"));
        overrideReasonArea.setEnabled(false);
        
        // Checkbox per forzare pubblicazione
        forcePublishCheckBox = new JCheckBox("Forza pubblicazione (ignora voti mancanti)");
        forcePublishCheckBox.addActionListener(e -> {
            boolean enabled = forcePublishCheckBox.isSelected();
            overrideReasonArea.setEnabled(enabled);
            if (!enabled) {
                overrideReasonArea.setText("");
            }
        });
        
        // Pulsanti
        previewButton = new JButton(PREVIEW_LABEL);
        publishButton = new JButton(PUBLISH_LABEL);
        viewPublishedButton = new JButton(VIEW_PUBLISHED_LABEL);
        
        // Style buttons - colori scuri con testo bianco
        previewButton.setBackground(new Color(70, 130, 180)); // Blu scuro
        previewButton.setForeground(Color.BLACK); // Bianco
        previewButton.setFocusPainted(false);
        previewButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        previewButton.setOpaque(true);
        previewButton.setBorderPainted(true);
        previewButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        
        publishButton.setBackground(new Color(34, 139, 34)); // Verde scuro
        publishButton.setForeground(Color.BLACK); // Bianco
        publishButton.setFocusPainted(false);
        publishButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        publishButton.setOpaque(true);
        publishButton.setBorderPainted(true);
        publishButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        
        viewPublishedButton.setBackground(new Color(75, 0, 130)); // Viola scuro
        viewPublishedButton.setForeground(Color.BLACK); // Bianco
        viewPublishedButton.setFocusPainted(false);
        viewPublishedButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewPublishedButton.setOpaque(true);
        viewPublishedButton.setBorderPainted(true);
        viewPublishedButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        
        // Status label
        statusLabel = new JLabel("Pronto per generare anteprima");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel superiore con info hackathon
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(hackathonInfoLabel);
        add(topPanel, BorderLayout.NORTH);
        
        // Panel centrale con anteprima e override
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        JScrollPane previewScrollPane = new JScrollPane(previewArea);
        previewScrollPane.setPreferredSize(new Dimension(850, 400));
        centerPanel.add(previewScrollPane, BorderLayout.CENTER);
        
        // Panel per override
        JPanel overridePanel = new JPanel(new BorderLayout());
        overridePanel.add(forcePublishCheckBox, BorderLayout.NORTH);
        JScrollPane overrideScrollPane = new JScrollPane(overrideReasonArea);
        overrideScrollPane.setPreferredSize(new Dimension(850, 80));
        overridePanel.add(overrideScrollPane, BorderLayout.CENTER);
        centerPanel.add(overridePanel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Panel pulsanti
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(previewButton);
        buttonPanel.add(publishButton);
        buttonPanel.add(viewPublishedButton);
        buttonPanel.add(new JSeparator(SwingConstants.VERTICAL));
        
        JButton closeButton = new JButton(CLOSE_LABEL);
        closeButton.setBackground(new Color(105, 105, 105)); // Grigio scuro
        closeButton.setForeground(Color.BLACK); // Bianco
        closeButton.setFocusPainted(false);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(true);
        closeButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        // Panel inferiore con status e pulsanti
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        previewButton.addActionListener(this::handlePreview);
        publishButton.addActionListener(this::handlePublish);
        viewPublishedButton.addActionListener(this::handleViewPublished);
        // Close button is already set up in setupLayout()
    }
    
    private void handlePreview(ActionEvent e) {
        statusLabel.setText("🔄 Generazione anteprima...");
        previewButton.setEnabled(false);
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return controller.anteprimaClassificaNuova(hackathon.getId());
            }
            
            @Override
            protected void done() {
                try {
                    String preview = get();
                    previewArea.setText(preview);
                    
                    // Verifica se tutti i voti sono stati acquisiti
                    boolean allVotesAcquired = controller.tuttiVotiAcquisitiNuovo(hackathon.getId());
                    if (allVotesAcquired) {
                        statusLabel.setText("✅ Anteprima generata - Tutti i voti acquisiti");
                        publishButton.setEnabled(true);
                    } else {
                        statusLabel.setText("⚠️ Anteprima generata - Voti mancanti (usa override per pubblicare)");
                        publishButton.setEnabled(false);
                    }
                    
                    previewButton.setEnabled(true);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt(); // Re-interrupt the thread
                    statusLabel.setText("❌ Operazione interrotta");
                    previewButton.setEnabled(true);
                    JOptionPane.showMessageDialog(RankingPublicationDialog.this,
                        "Operazione interrotta dall'utente.",
                        "Operazione Interrotta",
                        JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    statusLabel.setText("❌ Errore nella generazione: " + ex.getMessage());
                    previewButton.setEnabled(true);
                    JOptionPane.showMessageDialog(RankingPublicationDialog.this,
                        "Errore nella generazione dell'anteprima:\n" + ex.getMessage(),
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void handlePublish(ActionEvent e) {
        // Verifica permessi organizzatore
        if (!isCurrentUserOrganizer()) {
            JOptionPane.showMessageDialog(this,
                "Solo gli organizzatori possono pubblicare le classifiche.",
                "Permessi Insufficienti",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Verifica se anteprima è stata generata
        if (previewArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Genera prima un'anteprima della classifica.",
                "Anteprima Richiesta",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Se forza pubblicazione è selezionato, verifica il motivo
        String overrideReason = null;
        if (forcePublishCheckBox.isSelected()) {
            overrideReason = overrideReasonArea.getText().trim();
            if (overrideReason.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Inserisci un motivo per l'override quando forzi la pubblicazione.",
                    "Motivo Override Richiesto",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        // Conferma pubblicazione
        String message = forcePublishCheckBox.isSelected() ?
            "Sei sicuro di voler pubblicare la classifica con override?\n" +
            "Motivo: " + overrideReason :
            "Sei sicuro di voler pubblicare la classifica finale?\n" +
            "Questa azione è irreversibile.";
            
        int confirm = JOptionPane.showConfirmDialog(this,
            message,
            "Conferma Pubblicazione",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            performPublish(overrideReason);
        }
    }
    
    private void performPublish(String overrideReason) {
        statusLabel.setText("🔄 Pubblicazione in corso...");
        publishButton.setEnabled(false);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return controller.pubblicaClassificaNuova(
                    hackathon.getId(),
                    controller.getCurrentUser().getId(),
                    overrideReason
                );
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        statusLabel.setText("✅ Classifica pubblicata con successo!");
                        JOptionPane.showMessageDialog(RankingPublicationDialog.this,
                            "La classifica è stata pubblicata con successo!",
                            "Pubblicazione Completata",
                            JOptionPane.INFORMATION_MESSAGE);
                        parentFrame.showToast("Classifica pubblicata con successo!", "success");
                        
                        // Aggiorna stato pulsanti
                        publishButton.setEnabled(false);
                        viewPublishedButton.setEnabled(true);
                        updateStatus();
                    } else {
                        statusLabel.setText("❌ Errore nella pubblicazione");
                        JOptionPane.showMessageDialog(RankingPublicationDialog.this,
                            "Errore nella pubblicazione della classifica.\n" +
                            "Verifica i permessi e che non sia già stata pubblicata.",
                            "Errore Pubblicazione",
                            JOptionPane.ERROR_MESSAGE);
                        publishButton.setEnabled(true);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt(); // Re-interrupt the thread
                    statusLabel.setText("❌ Operazione interrotta");
                    publishButton.setEnabled(true);
                    JOptionPane.showMessageDialog(RankingPublicationDialog.this,
                        "Operazione interrotta dall'utente.",
                        "Operazione Interrotta",
                        JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    statusLabel.setText("❌ Errore nella pubblicazione: " + ex.getMessage());
                    publishButton.setEnabled(true);
                    JOptionPane.showMessageDialog(RankingPublicationDialog.this,
                        "Errore nella pubblicazione:\n" + ex.getMessage(),
                        "Errore Sistema",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void handleViewPublished(ActionEvent e) {
        RankingSnapshot published = controller.getClassificaPubblicataNuova(hackathon.getId());
        if (published == null) {
            JOptionPane.showMessageDialog(this,
                "Nessuna classifica pubblicata per questo hackathon.",
                "Classifica Non Trovata",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Mostra dialog con classifica pubblicata
        PublishedRankingViewerDialog viewer = new PublishedRankingViewerDialog(
            this, published, hackathon);
        viewer.setVisible(true);
    }
    
    private void updateStatus() {
        // Verifica se esiste già una classifica pubblicata
        RankingSnapshot existing = controller.getClassificaPubblicataNuova(hackathon.getId());
        if (existing != null) {
            statusLabel.setText("📊 Classifica già pubblicata il " + 
                existing.getCreatedAt().format(DATE_FORMAT));
            publishButton.setEnabled(false);
            viewPublishedButton.setEnabled(true);
        } else {
            statusLabel.setText("Nessuna classifica pubblicata");
            viewPublishedButton.setEnabled(false);
        }
    }
    
    private boolean isCurrentUserOrganizer() {
        try {
            return controller.getCurrentUser().getRuolo().equals("ORGANIZZATORE");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Dialog interno per visualizzare una classifica pubblicata
     */
    private static class PublishedRankingViewerDialog extends JDialog {
        
        public PublishedRankingViewerDialog(Dialog parent, RankingSnapshot snapshot, Hackathon hackathon) {
            super(parent, "Classifica Pubblicata", true);
            
            setLayout(new BorderLayout());
            
            // Info header
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            headerPanel.add(new JLabel("Hackathon: " + hackathon.getNome()));
            headerPanel.add(new JLabel(" | Versione: " + snapshot.getVersion()));
            headerPanel.add(new JLabel(" | Pubblicata: " + snapshot.getCreatedAt().format(DATE_FORMAT)));
            add(headerPanel, BorderLayout.NORTH);
            
            // Area testo con JSON formattato
            JTextArea contentArea = new JTextArea(25, 70);
            contentArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            contentArea.setEditable(false);
            contentArea.setText(formatJson(snapshot.getJsonPayload()));
            
            JScrollPane scrollPane = new JScrollPane(contentArea);
            add(scrollPane, BorderLayout.CENTER);
            
            // Pulsante chiudi
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton closeButton = new JButton(CLOSE_LABEL);
            closeButton.setBackground(new Color(105, 105, 105)); // Grigio scuro
            closeButton.setForeground(Color.BLACK); // Bianco
            closeButton.setFocusPainted(false);
            closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            closeButton.setOpaque(true);
            closeButton.setBorderPainted(true);
            closeButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            closeButton.addActionListener(e -> dispose());
            buttonPanel.add(closeButton);
            add(buttonPanel, BorderLayout.SOUTH);
            
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setSize(800, 600);
            setLocationRelativeTo(parent);
        }
        
        private String formatJson(String json) {
            // Formattazione semplice del JSON per migliore leggibilità
            try {
                return json.replace(",", ",\n")
                          .replace("{", "{\n  ")
                          .replace("}", "\n}")
                          .replace("[", "[\n  ")
                          .replace("]", "\n]");
            } catch (Exception e) {
                return json;
            }
        }
    }
}
