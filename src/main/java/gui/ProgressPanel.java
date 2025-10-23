package gui;
import controller.Controller;
import model.Progress;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.time.format.DateTimeFormatter;
import static javax.swing.SwingConstants.*;
/**
 * Pannello per la gestione dei progressi dei team.
 * Permette ai team di caricare progressi e ai giudici di commentarli.
 */
public class ProgressPanel extends JPanel {
    // Constants
    private static final String FONT_NAME = "Segoe UI";
    private static final String ERROR_TITLE = "Errore";
    
    private final transient Controller controller;
    private final transient JFrame mainFrame;
    // Components
    private DefaultListModel<Progress> progressListModel;
    private JList<Progress> progressList;
    private JButton caricaProgressoButton;
    private JButton aggiungiCommentoButton;
    private JButton refreshButton;
    private JButton debugButton;
    // Selected items
    private transient Progress selectedProgress;
    /**
     * Costruttore che inizializza il pannello progressi
     *
     * @param controller il controller dell'applicazione
     * @param mainFrame  il frame principale
     */
    public ProgressPanel(Controller controller, JFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        // Carica i dati iniziali
        refreshData();
    }
    /**
     * Inizializza i componenti del pannello
     */
    private void initializeComponents() {
        // List model for progress
        progressListModel = new DefaultListModel<>();
        progressList = new JList<>(progressListModel);
        progressList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        progressList.setCellRenderer(new ProgressListCellRenderer());
        // Buttons
        caricaProgressoButton = createStyledButton("📤 Carica Progresso", new Color(52, 152, 219));
        aggiungiCommentoButton = createStyledButton("💬 Aggiungi Commento", new Color(155, 89, 182));
        refreshButton = createStyledButton("\uD83D\uDD04 Aggiorna", new Color(46, 204, 113));
        debugButton = createStyledButton("\uD83D\uDD0D Debug", new Color(149, 165, 166));
        // Initially disable buttons that require selection
        aggiungiCommentoButton.setEnabled(false);
        // Add selection listener
        progressList.addListSelectionListener(e -> {
            selectedProgress = progressList.getSelectedValue();
            aggiungiCommentoButton.setEnabled(selectedProgress != null);
        });
    }
    /**
     * Configura il layout del pannello
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(MainFrame.getBackgroundColor());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("🚀 Gestione Progressi", LEFT);
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        JLabel subtitleLabel = new JLabel("Carica e commenta i progressi dei team", LEFT);
        subtitleLabel.setFont(new Font(FONT_NAME, Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(149, 165, 166));
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        // Buttons panel in header
        JPanel headerButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerButtonsPanel.setOpaque(false);
        headerButtonsPanel.add(refreshButton);
        headerButtonsPanel.add(debugButton);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(headerButtonsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        // Progress list
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setOpaque(false);
        listPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "Progressi Team",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(FONT_NAME, Font.BOLD, 14),
            new Color(52, 73, 94)
        ));
        JScrollPane scrollPane = new JScrollPane(progressList);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        listPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(listPanel, BorderLayout.CENTER);
        // Action buttons
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "Azioni",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(FONT_NAME, Font.BOLD, 14),
            new Color(52, 73, 94)
        ));
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(caricaProgressoButton);
        buttonsPanel.add(aggiungiCommentoButton);
        actionPanel.add(buttonsPanel);
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        JLabel infoLabel = new JLabel("💡 Seleziona un progresso per aggiungere commenti (solo giudici)");
        infoLabel.setFont(new Font(FONT_NAME, Font.ITALIC, 12));
        infoLabel.setForeground(new Color(149, 165, 166));
        infoPanel.add(infoLabel);
        actionPanel.add(infoPanel);
        contentPanel.add(actionPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        caricaProgressoButton.addActionListener(e -> showCaricaProgressoDialog());
        aggiungiCommentoButton.addActionListener(e -> showAggiungiCommentoDialog());
        refreshButton.addActionListener(e -> {
            refreshData();
            JOptionPane.showMessageDialog(mainFrame, "\u2705 Progressi aggiornati!", "Aggiornamento", JOptionPane.INFORMATION_MESSAGE);
        });
        debugButton.addActionListener(e -> showDebugInfo());
    }
    /**
     * Aggiorna i dati del pannello
     */
    public void refreshData() {
        progressListModel.clear();
        selectedProgress = null;
        aggiungiCommentoButton.setEnabled(false);
        try {
            // Per ora carichiamo tutti i progressi disponibili
            // In futuro potremmo filtrare per team specifici
            List<Progress> progressi = controller.getTuttiProgressi();
            if (progressi.isEmpty()) {
                progressListModel.addElement(new Progress(-1, -1, "Nessun progresso trovato", "Carica il primo progresso del tuo team", ""));
            } else {
                for (Progress progress : progressi) {
                    progressListModel.addElement(progress);
                }
            }
        } catch (Exception e) {
            progressListModel.addElement(new Progress(-1, -1, "Errore caricamento", "Impossibile caricare i progressi: " + e.getMessage(), ""));
        }
    }
    /**
     * Mostra il dialog per caricare un nuovo progresso
     */
    private void showCaricaProgressoDialog() {
        // Usa il dialog moderno e unificato
        FileUploadDialog uploadDialog = new FileUploadDialog((JFrame) SwingUtilities.getWindowAncestor(this), controller);
        uploadDialog.setVisible(true);
        
        // Refresh data after upload
        refreshData();
    }
    /**
     * Mostra il dialog per aggiungere un commento giudice
     */
    private void showAggiungiCommentoDialog() {
        if (selectedProgress == null) {
            JOptionPane.showMessageDialog(this, "Seleziona un progresso", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return;
        }
        JDialog dialog = new JDialog(mainFrame, "Aggiungi Commento Giudice", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(mainFrame);
        // Titolo
        JLabel titleLabel = new JLabel("💬 Commento Giudice", CENTER);
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        dialog.add(titleLabel, BorderLayout.NORTH);
        // Info progresso
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Progresso:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(selectedProgress.getTitolo()), gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Team ID:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(String.valueOf(selectedProgress.getTeamId())), gbc);
        dialog.add(infoPanel, BorderLayout.CENTER);
        // Campo commento
        JPanel commentPanel = new JPanel(new BorderLayout());
        commentPanel.setBorder(BorderFactory.createTitledBorder("Commento"));
        JTextArea commentoArea = new JTextArea(4, 30);
        commentoArea.setLineWrap(true);
        commentoArea.setWrapStyleWord(true);
        commentPanel.add(new JScrollPane(commentoArea), BorderLayout.CENTER);
        dialog.add(commentPanel, BorderLayout.SOUTH);
        // Se esiste già un commento, mostralo
        if (selectedProgress.getCommentoGiudice() != null && !selectedProgress.getCommentoGiudice().isEmpty()) {
            commentoArea.setText(selectedProgress.getCommentoGiudice());
        }
        // Pannello pulsanti
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton salvaButton = new JButton("\uD83D\uDCBE Salva Commento");
        JButton annullaButton = new JButton("\u274C Annulla");
        salvaButton.setBackground(new Color(155, 89, 182));
        salvaButton.setForeground(Color.BLACK);
        salvaButton.setOpaque(true);
        salvaButton.setBorderPainted(true);
        salvaButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        salvaButton.setFocusPainted(false);
        salvaButton.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        annullaButton.setBackground(new Color(231, 76, 60));
        annullaButton.setForeground(Color.BLACK);
        annullaButton.setOpaque(true);
        annullaButton.setBorderPainted(true);
        annullaButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        annullaButton.setFocusPainted(false);
        annullaButton.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        salvaButton.addActionListener(e -> {
            String commento = commentoArea.getText().trim();
            if (commento.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Inserisci un commento", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                boolean success = controller.aggiungiCommentoGiudice(selectedProgress.getId(), commento);
                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                        "\u2705 Commento aggiunto con successo!",
                        "Successo",
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "\u274C Errore durante il salvataggio del commento",
                        ERROR_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "\u274C Errore: " + ex.getMessage(),
                    "Errore Sistema",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        annullaButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(salvaButton);
        buttonPanel.add(annullaButton);
        // Aggiungi i pulsanti alla fine del commentPanel
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(commentPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(southPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    /**
     * Mostra informazioni di debug
     */
    private void showDebugInfo() {
        StringBuilder debugInfo = new StringBuilder();
        debugInfo.append("\uD83D\uDD0D DEBUG PROGRESS PANEL\n\n");
        try {
            debugInfo.append("👤 UTENTE CORRENTE: ").append(controller.getCurrentUser().getLogin()).append("\n");
            debugInfo.append("🎯 PROGRESSI TOTALI: ").append(progressListModel.size()).append("\n");
            if (selectedProgress != null) {
                debugInfo.append("📋 PROGRESSO SELEZIONATO:\n");
                debugInfo.append("  ID: ").append(selectedProgress.getId()).append("\n");
                debugInfo.append("  Team ID: ").append(selectedProgress.getTeamId()).append("\n");
                debugInfo.append("  Titolo: ").append(selectedProgress.getTitolo()).append("\n");
                debugInfo.append("  Descrizione: ").append(selectedProgress.getDescrizione()).append("\n");
                debugInfo.append("  Documento: ").append(selectedProgress.getDocumentoPath()).append("\n");
                debugInfo.append("  Commento Giudice: ").append(selectedProgress.getCommentoGiudice() != null ? selectedProgress.getCommentoGiudice() : "Nessuno").append("\n");
            }
        } catch (Exception e) {
            debugInfo.append("\u274C ERRORE: ").append(e.getMessage()).append("\n");
        }
        JTextArea textArea = new JTextArea(debugInfo.toString());
        textArea.setRows(15);
        textArea.setColumns(50);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "\uD83D\uDD0D Debug Progress Panel", JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * Crea un pulsante stilizzato
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        button.setForeground(Color.BLACK);
        button.setBackground(backgroundColor);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        return button;
    }
    /**
     * Renderer personalizzato per la lista dei progressi
     */
    private class ProgressListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Progress progress) {
                StringBuilder text = new StringBuilder();
                text.append("📋 ").append(progress.getTitolo()).append("\n");
                text.append("👥 Team ID: ").append(progress.getTeamId()).append(" | ");
                text.append("📅 ").append(progress.getDataCaricamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                if (progress.getCommentoGiudice() != null && !progress.getCommentoGiudice().isEmpty()) {
                    text.append("\n💬 Giudice: ").append(progress.getCommentoGiudice());
                } else {
                    text.append("\n💬 Nessun commento giudice");
                }
                setText(text.toString());
                setFont(new Font(FONT_NAME, Font.PLAIN, 11));
                setPreferredSize(new Dimension(list.getWidth(), 60));
            } else {
                setText("Progresso non valido");
                setForeground(Color.RED);
            }
            return this;
        }
    }
}
