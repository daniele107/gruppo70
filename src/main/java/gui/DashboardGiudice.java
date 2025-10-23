package gui;

import controller.Controller;
import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dashboard specifica per i giudici degli hackathon.
 * Fornisce strumenti per valutazione, revisione progressi e gestione hackathon.
 */
public class DashboardGiudice extends JPanel {
    
    private static final long serialVersionUID = 1L;
    private static final String FONT_FAMILY = "Segoe UI";
    private static final String ERROR_LOADING_MSG = "Errore nel caricamento";
    
    private final transient Controller controller;
    private final transient JFrame mainFrame;
    
    
    // Statistiche giudice
    private JLabel totalHackathonLabel;
    private JLabel totalEvaluationsLabel;
    private JLabel pendingEvaluationsLabel;
    private JLabel averageScoreLabel;
    
    // Valutazioni in sospeso
    private JTable pendingTable;
    private DefaultTableModel pendingTableModel;
    private JButton evaluateButton;
    private JButton viewTeamButton;
    
    // Hackathon assegnati
    private DefaultListModel<String> hackathonListModel;
    private JList<String> hackathonList;
    private JButton viewHackathonButton;
    
    // Progressi da rivedere
    private JTable progressTable;
    private DefaultTableModel progressTableModel;
    private JButton commentButton;
    private JButton viewDocumentsButton;
    
    // Notifiche
    private DefaultListModel<String> notificationListModel;
    private JList<String> notificationList;
    private JButton viewAllNotificationsButton;
    
    // Azioni rapide
    private JButton generateReportButton;
    private JButton viewRankingsButton;
    private JButton manageHackathonButton;
    
    // Dati
    private transient List<Hackathon> assignedHackathons;
    private transient List<Team> teamsToEvaluate;
    private transient List<Progress> progressToReview;
    
    /**
     * Costruttore della dashboard giudice
     */
    public DashboardGiudice(Controller controller, JFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }
    
    /**
     * Inizializza i componenti della dashboard
     */
    private void initializeComponents() {
        // Statistiche
        totalHackathonLabel = createStatLabel("0");
        totalEvaluationsLabel = createStatLabel("0");
        pendingEvaluationsLabel = createStatLabel("0");
        averageScoreLabel = createStatLabel("0.0");
        
        // Tabella valutazioni in sospeso
        String[] pendingColumns = {"Team", "Hackathon", "Membri", "Progressi", "Scadenza"};
        pendingTableModel = new DefaultTableModel(pendingColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        pendingTable = new JTable(pendingTableModel);
        pendingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pendingTable.setRowHeight(25);
        
        evaluateButton = createActionButton("Valuta Team", new Color(241, 196, 15));
        viewTeamButton = createActionButton("👥 Dettagli Team", new Color(52, 152, 219));
        
        // Hackathon assegnati
        hackathonListModel = new DefaultListModel<>();
        hackathonList = new JList<>(hackathonListModel);
        hackathonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hackathonList.setVisibleRowCount(4);
        viewHackathonButton = createActionButton("Gestisci Hackathon", new Color(155, 89, 182));
        
        // Tabella progressi da rivedere
        String[] progressColumns = {"Team", "Titolo", "Data", "Commentato"};
        progressTableModel = new DefaultTableModel(progressColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Boolean.class;
                return String.class;
            }
        };
        progressTable = new JTable(progressTableModel);
        progressTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        progressTable.setRowHeight(25);
        
        commentButton = createActionButton("💬 Aggiungi Commento", new Color(46, 204, 113));
        viewDocumentsButton = createActionButton("📄 Visualizza Documenti", new Color(52, 152, 219));
        
        // Notifiche
        notificationListModel = new DefaultListModel<>();
        notificationList = new JList<>(notificationListModel);
        notificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notificationList.setVisibleRowCount(4);
        viewAllNotificationsButton = createActionButton("🔔 Tutte le Notifiche", new Color(241, 196, 15));
        
        // Azioni rapide
        generateReportButton = createActionButton("📊 Genera Report", new Color(155, 89, 182));
        viewRankingsButton = createActionButton("Visualizza Classifiche", new Color(241, 196, 15));
        manageHackathonButton = createActionButton("Gestisci Hackathon", new Color(149, 165, 166));
        
        // Initially disable buttons that require selection
        evaluateButton.setEnabled(false);
        viewTeamButton.setEnabled(false);
        commentButton.setEnabled(false);
        viewDocumentsButton.setEnabled(false);
        viewHackathonButton.setEnabled(false);
    }
    
    /**
     * Configura il layout della dashboard
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 249, 250));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content - Grid layout 2x3
        JPanel mainPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        mainPanel.setBackground(new Color(248, 249, 250));
        
        // Pannelli dashboard
        JPanel statsPanel = createStatsPanel();
        JPanel evaluationPanel = createEvaluationPanel();
        JPanel hackathonPanel = createHackathonPanel();
        JPanel progressPanel = createProgressPanel();
        JPanel notificationsPanel = createNotificationsPanel();
        JPanel actionsPanel = createActionsPanel();
        
        mainPanel.add(statsPanel);
        mainPanel.add(evaluationPanel);
        mainPanel.add(hackathonPanel);
        mainPanel.add(progressPanel);
        mainPanel.add(notificationsPanel);
        mainPanel.add(actionsPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Crea il pannello header
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("Dashboard Giudice");
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        Utente currentUser = controller.getCurrentUser();
        String welcomeText = currentUser != null ? 
            "Benvenuto, Giudice " + currentUser.getNome() + " " + currentUser.getCognome() + "!" :
            "Benvenuto, Giudice!";
        JLabel welcomeLabel = new JLabel(welcomeText);
        welcomeLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.GRAY);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(welcomeLabel, BorderLayout.SOUTH);
        
        JLabel dateTimeLabel = new JLabel(LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        dateTimeLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        dateTimeLabel.setForeground(Color.GRAY);
        
        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(dateTimeLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Crea il pannello statistiche
     */
    private JPanel createStatsPanel() {
        JPanel panel = createDashboardPanel("📊 Le Tue Statistiche");
        panel.setLayout(new GridLayout(2, 2, 10, 10));
        
        panel.add(createStatCard("Hackathon Giudicati", totalHackathonLabel, "H"));
        panel.add(createStatCard("Valutazioni Totali", totalEvaluationsLabel, "V"));
        panel.add(createStatCard("Valutazioni in Sospeso", pendingEvaluationsLabel, "P"));
        panel.add(createStatCard("Voto Medio Assegnato", averageScoreLabel, "📊"));
        
        return panel;
    }
    
    /**
     * Crea il pannello valutazioni
     */
    private JPanel createEvaluationPanel() {
        JPanel panel = createDashboardPanel("Team da Valutare");
        panel.setLayout(new BorderLayout(5, 5));
        
        JScrollPane scrollPane = new JScrollPane(pendingTable);
        scrollPane.setPreferredSize(new Dimension(0, 120));
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(evaluateButton);
        buttonPanel.add(viewTeamButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crea il pannello hackathon
     */
    private JPanel createHackathonPanel() {
        JPanel panel = createDashboardPanel("I Tuoi Hackathon");
        panel.setLayout(new BorderLayout(5, 5));
        
        JScrollPane scrollPane = new JScrollPane(hackathonList);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(viewHackathonButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crea il pannello progressi
     */
    private JPanel createProgressPanel() {
        JPanel panel = createDashboardPanel("📈 Progressi da Rivedere");
        panel.setLayout(new BorderLayout(5, 5));
        
        JScrollPane scrollPane = new JScrollPane(progressTable);
        scrollPane.setPreferredSize(new Dimension(0, 120));
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(commentButton);
        buttonPanel.add(viewDocumentsButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crea il pannello notifiche
     */
    private JPanel createNotificationsPanel() {
        JPanel panel = createDashboardPanel("🔔 Notifiche Recenti");
        panel.setLayout(new BorderLayout(5, 5));
        
        JScrollPane scrollPane = new JScrollPane(notificationList);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(viewAllNotificationsButton, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crea il pannello azioni
     */
    private JPanel createActionsPanel() {
        JPanel panel = createDashboardPanel("⚡ Azioni Rapide");
        panel.setLayout(new GridLayout(3, 1, 5, 5));
        
        panel.add(generateReportButton);
        panel.add(viewRankingsButton);
        panel.add(manageHackathonButton);
        
        return panel;
    }
    
    /**
     * Crea il pannello footer
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(new Color(248, 249, 250));
        
        JButton refreshButton = createActionButton("🔄 Aggiorna Dashboard", new Color(149, 165, 166));
        refreshButton.addActionListener(e -> refreshData());
        
        JLabel statusLabel = new JLabel("Pronto per valutazioni");
        statusLabel.setForeground(new Color(39, 174, 96));
        statusLabel.setFont(new Font(FONT_FAMILY, Font.ITALIC, 12));
        
        panel.add(statusLabel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(refreshButton);
        
        return panel;
    }
    
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        // Evaluation table selection
        pendingTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = pendingTable.getSelectedRow() != -1;
            evaluateButton.setEnabled(hasSelection);
            viewTeamButton.setEnabled(hasSelection);
        });
        
        // Progress table selection
        progressTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = progressTable.getSelectedRow() != -1;
            commentButton.setEnabled(hasSelection);
            viewDocumentsButton.setEnabled(hasSelection);
        });
        
        // Hackathon list selection
        hackathonList.addListSelectionListener(e -> 
            viewHackathonButton.setEnabled(hackathonList.getSelectedIndex() != -1)
        );
        
        // Button actions
        evaluateButton.addActionListener(this::handleEvaluateTeam);
        viewTeamButton.addActionListener(this::handleViewTeam);
        commentButton.addActionListener(this::handleAddComment);
        viewDocumentsButton.addActionListener(this::handleViewDocuments);
        viewHackathonButton.addActionListener(this::handleViewHackathon);
        viewAllNotificationsButton.addActionListener(this::handleViewAllNotifications);
        generateReportButton.addActionListener(this::handleGenerateReport);
        viewRankingsButton.addActionListener(this::handleViewRankings);
        manageHackathonButton.addActionListener(this::handleManageHackathon);
    }
    
    /**
     * Aggiorna tutti i dati della dashboard
     */
    public void refreshData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadStatistics();
                loadTeamsToEvaluate();
                loadAssignedHackathons();
                loadProgressToReview();
                loadRecentNotifications();
                return null;
            }
            
            @Override
            protected void done() {
                updateUIComponents();
            }
        };
        worker.execute();
    }
    
    /**
     * Carica le statistiche del giudice
     */
    @SuppressWarnings("java:S3776") // Cognitive complexity acceptable for statistics loading
    private void loadStatistics() {
        try {
            Utente currentUser = controller.getCurrentUser();
            if (currentUser == null) return;
            
            // Per ora simuliamo le valutazioni del giudice usando le statistiche
            Controller.GiudiceStats stats = controller.getStatisticheGiudice(currentUser.getId());
            
            // Valutazioni in sospeso (team senza valutazione) - simulato
            final int pendingCount = teamsToEvaluate.size(); // Semplificato per ora
            
            // Voto medio dalle statistiche
            double averageScore = stats.mediaVoti;
            
            SwingUtilities.invokeLater(() -> {
                totalHackathonLabel.setText(String.valueOf(stats.eventiValutati));
                totalEvaluationsLabel.setText(String.valueOf(stats.valutazioniFatte));
                pendingEvaluationsLabel.setText(String.valueOf(pendingCount));
                averageScoreLabel.setText(String.valueOf(Math.round(averageScore * 10.0) / 10.0));
                
                // Update colors based on pending evaluations
                if (pendingCount > 0) {
                    pendingEvaluationsLabel.setForeground(new Color(231, 76, 60));
                } else {
                    pendingEvaluationsLabel.setForeground(new Color(39, 174, 96));
                }
            });
            
        } catch (Exception e) {
            // Handle errors silently
        }
    }
    
    /**
     * Carica i team da valutare
     */
    @SuppressWarnings("java:S3776") // Cognitive complexity acceptable for team loading
    private void loadTeamsToEvaluate() {
        try {
            Utente currentUser = controller.getCurrentUser();
            if (currentUser == null) return;
            
            // Simula team da valutare - otteniamo tutti i team per ora
            teamsToEvaluate = controller.getTuttiTeam();
            
            SwingUtilities.invokeLater(() -> {
                pendingTableModel.setRowCount(0);
                
                for (Team team : teamsToEvaluate) {
                    try {
                        Hackathon hackathon = controller.getHackathonById(team.getHackathonId());
                        // Simula conteggio membri
                        int memberCount = 3; // Default member count
                        List<Progress> teamProgress = controller.getProgressiTeam(team.getId());
                        
                        String deadline = hackathon.getDataFine().format(
                            DateTimeFormatter.ofPattern("dd/MM HH:mm"));
                        
                        Object[] row = {
                            team.getNome(),
                            hackathon.getNome(),
                            memberCount + " membri",
                            teamProgress.size() + " progressi",
                            deadline
                        };
                        
                        pendingTableModel.addRow(row);
                    } catch (Exception e) {
                        // Skip teams with errors
                    }
                }
                
                if (pendingTableModel.getRowCount() == 0) {
                    pendingTableModel.addRow(new Object[]{
                        "Nessun team da valutare", "", "", "", ""
                    });
                }
            });
            
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                pendingTableModel.setRowCount(0);
                String errorMsg = ERROR_LOADING_MSG;
                pendingTableModel.addRow(new Object[]{
                    errorMsg, "", "", "", ""
                });
            });
        }
    }
    
    /**
     * Carica gli hackathon assegnati
     */
    private void loadAssignedHackathons() {
        try {
            Utente currentUser = controller.getCurrentUser();
            if (currentUser == null) return;
            
            List<Registrazione> registrazioni = controller.getRegistrazioniUtente();
            assignedHackathons = registrazioni.stream()
                .filter(r -> r.isGiudice() && r.isConfermata())
                .map(r -> controller.getHackathonById(r.getHackathonId()))
                .toList();
            
            SwingUtilities.invokeLater(() -> {
                hackathonListModel.clear();
                
                for (Hackathon hackathon : assignedHackathons) {
                    String status = getHackathonStatus(hackathon);
                    String formattedDate = hackathon.getDataInizio().format(DateTimeFormatter.ofPattern("dd/MM"));
                    String listItem = status + " " + hackathon.getNome() + " (" + formattedDate + ")";
                    hackathonListModel.addElement(listItem);
                }
                
                if (hackathonListModel.isEmpty()) {
                    hackathonListModel.addElement("Nessun hackathon assegnato");
                }
            });
            
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                hackathonListModel.clear();
                String errorMsg = ERROR_LOADING_MSG;
                hackathonListModel.addElement(errorMsg);
            });
        }
    }
    
    /**
     * Carica i progressi da rivedere
     */
    @SuppressWarnings({"java:S3776", "java:S1192"}) // Cognitive complexity and string literals acceptable
    private void loadProgressToReview() {
        try {
            Utente currentUser = controller.getCurrentUser();
            if (currentUser == null) return;
            
            // Ottieni tutti i progressi dei team che il giudice deve valutare
            // Simula progressi da rivedere
            progressToReview = controller.getTuttiProgressi().stream()
                .filter(p -> p.getCommentoGiudice() == null || p.getCommentoGiudice().trim().isEmpty())
                .toList();
            
            SwingUtilities.invokeLater(() -> {
                progressTableModel.setRowCount(0);
                
                for (Progress progress : progressToReview) {
                    try {
                        Team team = controller.getTuttiTeam().stream()
                            .filter(t -> t.getId() == progress.getTeamId())
                            .findFirst().orElse(null);
                        if (team == null) continue;
                        boolean hasComment = progress.getCommentoGiudice() != null && 
                                           !progress.getCommentoGiudice().trim().isEmpty();
                        
                        String date = progress.getDataCaricamento().format(
                            DateTimeFormatter.ofPattern("dd/MM HH:mm"));
                        
                        Object[] row = {
                            team.getNome(),
                            progress.getTitolo(),
                            date,
                            hasComment
                        };
                        
                        progressTableModel.addRow(row);
                    } catch (Exception e) {
                        // Skip progress with errors
                    }
                }
                
                if (progressTableModel.getRowCount() == 0) {
                    progressTableModel.addRow(new Object[]{
                        "Nessun progresso da rivedere", "", "", false
                    });
                }
            });
            
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                progressTableModel.setRowCount(0);
                String errorMsg = ERROR_LOADING_MSG;
                progressTableModel.addRow(new Object[]{
                    errorMsg, "", "", false
                });
            });
        }
    }
    
    /**
     * Carica le notifiche recenti
     */
    private void loadRecentNotifications() {
        try {
            List<Notification> recentNotifications = controller.getNotificheUtente();
            
            SwingUtilities.invokeLater(() -> {
                notificationListModel.clear();
                
                int count = 0;
                for (Notification notification : recentNotifications) {
                    if (count >= 5) break;
                    
                    String status = notification.isRead() ? "📖" : "🔔";
                    String listItem = status + " " + notification.getTitle();
                    notificationListModel.addElement(listItem);
                    count++;
                }
                
                if (notificationListModel.isEmpty()) {
                    notificationListModel.addElement("Nessuna notifica");
                }
            });
            
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                notificationListModel.clear();
                String errorMsg = ERROR_LOADING_MSG;
                notificationListModel.addElement(errorMsg);
            });
        }
    }
    
    /**
     * Aggiorna l'interfaccia utente
     */
    private void updateUIComponents() {
        revalidate();
        repaint();
    }
    
    // Event Handlers
    
    private void handleEvaluateTeam(ActionEvent e) {
        int selectedRow = pendingTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < teamsToEvaluate.size()) {
            Team selectedTeam = teamsToEvaluate.get(selectedRow);
            showEvaluationDialog(selectedTeam);
        }
    }
    
    private void handleViewTeam(ActionEvent e) {
        int selectedRow = pendingTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < teamsToEvaluate.size()) {
            Team selectedTeam = teamsToEvaluate.get(selectedRow);
            showTeamDetailsDialog(selectedTeam);
        }
    }
    
    private void handleAddComment(ActionEvent e) {
        int selectedRow = progressTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < progressToReview.size()) {
            Progress selectedProgress = progressToReview.get(selectedRow);
            showCommentDialog(selectedProgress);
        }
    }
    
    private void handleViewDocuments(ActionEvent e) {
        int selectedRow = progressTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < progressToReview.size()) {
            Progress selectedProgress = progressToReview.get(selectedRow);
            FileViewerDialog dialog = new FileViewerDialog(mainFrame, controller, selectedProgress.getTeamId());
            dialog.setVisible(true);
        }
    }
    
    private void handleViewHackathon(ActionEvent e) {
        int selectedIndex = hackathonList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < assignedHackathons.size()) {
            Hackathon selectedHackathon = assignedHackathons.get(selectedIndex);
            // Navigate to hackathon management or show details
            JOptionPane.showMessageDialog(this,
                "Gestione hackathon: " + selectedHackathon.getNome(),
                "Hackathon", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void handleViewAllNotifications(ActionEvent e) {
        // Navigate to notifications panel
        JOptionPane.showMessageDialog(this,
            "Visualizzazione di tutte le notifiche",
            "Notifiche", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void handleGenerateReport(ActionEvent e) {
        // Show report generation options
        String[] options = {"Report Hackathon", "Report Team", "Report Personale"};
        String choice = (String) JOptionPane.showInputDialog(this,
            "Seleziona il tipo di report da generare:",
            "Genera Report",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice != null) {
            JOptionPane.showMessageDialog(this,
                "Generazione " + choice + " in corso...",
                "Report", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void handleViewRankings(ActionEvent e) {
        if (mainFrame instanceof MainFrame mainFrameInstance) {
            mainFrameInstance.showValutazioniPanel();
        }
    }
    
    private void handleManageHackathon(ActionEvent e) {
        if (mainFrame instanceof MainFrame mainFrameInstance) {
            mainFrameInstance.showEventiPanel();
        }
    }
    
    /**
     * Mostra il dialog di valutazione per un team
     */
    private void showEvaluationDialog(Team team) {
        JDialog dialog = new JDialog(mainFrame, "Valuta Team: " + team.getNome(), true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Team info
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(248, 249, 250));
        infoArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        infoArea.setText("Team: " + team.getNome() + "\n" +
                        "Hackathon ID: " + team.getHackathonId() + "\n" +
                        "Dimensione massima: " + team.getDimensioneMassima() + "\n\n" +
                        "Inserisci la tua valutazione per questo team:");
        
        // Score input
        JPanel scorePanel = new JPanel(new FlowLayout());
        scorePanel.add(new JLabel("Voto (0-10):"));
        JSpinner scoreSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        scorePanel.add(scoreSpinner);
        
        // Comment input
        JTextArea commentArea = new JTextArea(5, 30);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setBorder(BorderFactory.createTitledBorder("Commento (opzionale)"));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton submitButton = createActionButton("✅ Conferma Valutazione", new Color(46, 204, 113));
        JButton cancelButton = createActionButton("Annulla", new Color(231, 76, 60));
        
        submitButton.addActionListener(ev -> {
            int score = (Integer) scoreSpinner.getValue();
            String comment = commentArea.getText().trim();
            
            boolean success = controller.assegnaVoto(team.getId(), score, comment);
            if (success) {
                JOptionPane.showMessageDialog(dialog,
                    "Valutazione assegnata con successo!",
                    "Successo", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Errore nell'assegnazione della valutazione",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(ev -> dialog.dispose());
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        
        panel.add(infoArea, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scorePanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(commentArea), BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Mostra i dettagli di un team
     */
    private void showTeamDetailsDialog(Team team) {
        JOptionPane.showMessageDialog(this,
            "Dettagli team: " + team.getNome() + "\n" +
            "ID: " + team.getId() + "\n" +
            "Hackathon: " + team.getHackathonId() + "\n" +
            "Capo team: " + team.getCapoTeamId(),
            "Dettagli Team", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Mostra il dialog per aggiungere un commento
     */
    private void showCommentDialog(Progress progress) {
        String comment = JOptionPane.showInputDialog(this,
            "Aggiungi un commento per il progresso:\n\"" + progress.getTitolo() + "\"",
            "Aggiungi Commento",
            JOptionPane.QUESTION_MESSAGE);
        
        if (comment != null && !comment.trim().isEmpty()) {
            boolean success = controller.aggiungiCommentoGiudice(progress.getId(), comment.trim());
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Commento aggiunto con successo!",
                    "Successo", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Errore nell'aggiunta del commento",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Ottiene lo status di un hackathon
     */
    private String getHackathonStatus(Hackathon hackathon) {
        if (hackathon.isEventoAvviato()) {
            return hackathon.isEventoConcluso() ? "Concluso" : "In corso";
        }
        return "In attesa";
    }
    
    // Utility methods (same as DashboardPartecipante)
    
    private JPanel createDashboardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder(BorderFactory.createRaisedBevelBorder(), title,
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font(FONT_FAMILY, Font.BOLD, 14), new Color(52, 73, 94)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel, String icon) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLoweredBevelBorder(),
            new EmptyBorder(8, 8, 8, 8)
        ));
        
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 16));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 10));
        titleLabel.setForeground(Color.GRAY);
        
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(valueLabel, BorderLayout.CENTER);
        
        card.add(iconLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(FONT_FAMILY, Font.BOLD, 16));
        label.setForeground(new Color(52, 152, 219));
        return label;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        button.setFocusPainted(false);
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
}
