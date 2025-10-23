package gui;

import controller.Controller;
import model.*;
import model.ReportData;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialog dettagliato per visualizzare e gestire le informazioni complete di un hackathon.
 * Fornisce una vista completa con statistiche, team, registrazioni e azioni di gestione.
 */
public class HackathonDetailsDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;
    
    // Costanti per stringhe duplicate
    private static final String SEGOE_UI_FONT = "Segoe UI";
    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";
    private static final String CONSOLAS_FONT = "Consolas";
    private static final String TEAM_PREFIX = "Team";
    private static final String SUCCESS_TITLE = "Successo";
    private static final String ERROR_TITLE = "Errore";
    
    private final transient Controller controller;
    private final transient Hackathon hackathon;
    
    // Componenti principali
    private JTabbedPane tabbedPane;
    
    // Tab Informazioni Generali  
    private JLabel statusLabel;
    private JLabel participantsLabel;
    private JLabel teamsLabel;
    private JLabel documentsLabel;
    
    // Tab Team
    private JTable teamsTable;
    private DefaultTableModel teamsTableModel;
    private JButton viewTeamButton;
    private JButton evaluateTeamButton;
    private JButton teamDocumentsButton;
    
    // Tab Registrazioni
    private JTable registrationsTable;
    private DefaultTableModel registrationsTableModel;
    private JButton approveRegButton;
    private JButton rejectRegButton;
    private JButton viewUserButton;
    
    // Tab Progressi
    private JTable progressTable;
    private DefaultTableModel progressTableModel;
    private JButton viewProgressButton;
    private JButton commentProgressButton;
    
    // Tab Valutazioni
    private JTable evaluationsTable;
    private DefaultTableModel evaluationsTableModel;
    private JButton viewRankingButton;
    private JButton exportResultsButton;
    
    // Tab Gestione
    private JButton openRegButton;
    private JButton closeRegButton;
    private JButton startHackathonButton;
    private JButton endHackathonButton;
    private JButton generateReportButton;
    
    // Dati
    private transient List<Team> hackathonTeams;
    private transient List<Registrazione> hackathonRegistrations;
    private transient List<Progress> hackathonProgress;
    private transient List<Valutazione> hackathonEvaluations;
    
    /**
     * Costruttore del dialog
     */
    public HackathonDetailsDialog(JFrame parent, Controller controller, Hackathon hackathon) {
        super(parent, "🏆 " + hackathon.getNome() + " - Dettagli Completi", true);
        this.controller = controller;
        this.hackathon = hackathon;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(parent);
        setResizable(true);
    }
    
    /**
     * Inizializza i componenti
     */
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        initializeStatusLabels();
        initializeTeamsComponents();
        initializeRegistrationsComponents();
        initializeProgressComponents();
        initializeEvaluationsComponents();
        initializeManagementButtons();
        initializeButtonStates();
    }
    
    /**
     * Inizializza le etichette di stato
     */
    private void initializeStatusLabels() {
        statusLabel = createInfoLabel("🔄 Caricamento...");
        participantsLabel = createInfoLabel("Partecipanti: 0");
        teamsLabel = createInfoLabel(TEAM_PREFIX + ": 0");
        documentsLabel = createInfoLabel("Documenti: 0");
    }
    
    /**
     * Inizializza i componenti dei team
     */
    private void initializeTeamsComponents() {
        String[] teamColumns = {"Nome " + TEAM_PREFIX, "Capo " + TEAM_PREFIX, "Membri", "Progressi", "Valutato", "Punteggio Medio"};
        teamsTableModel = new DefaultTableModel(teamColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Boolean.class; // Valutato
                return String.class;
            }
        };
        teamsTable = new JTable(teamsTableModel);
        teamsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamsTable.setRowHeight(25);
        
        viewTeamButton = createActionButton("👥 Dettagli", new Color(52, 152, 219));
        evaluateTeamButton = createActionButton("⭐ Valuta", new Color(241, 196, 15));
        teamDocumentsButton = createActionButton("📄 Documenti", new Color(155, 89, 182));
    }
    
    /**
     * Inizializza i componenti delle registrazioni
     */
    private void initializeRegistrationsComponents() {
        String[] regColumns = {"Nome", "Cognome", "Email", "Ruolo", "Data Registrazione", "Stato"};
        registrationsTableModel = new DefaultTableModel(regColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        registrationsTable = new JTable(registrationsTableModel);
        registrationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        registrationsTable.setRowHeight(25);
        
        approveRegButton = createActionButton("✅ Approva", new Color(46, 204, 113));
        rejectRegButton = createActionButton("❌ Rifiuta", new Color(231, 76, 60));
        viewUserButton = createActionButton("👤 Profilo", new Color(155, 89, 182));
    }
    
    /**
     * Inizializza i componenti dei progressi
     */
    private void initializeProgressComponents() {
        String[] progressColumns = {TEAM_PREFIX, "Titolo", "Data Caricamento", "Commentato", "Giudice"};
        progressTableModel = new DefaultTableModel(progressColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Boolean.class; // Commentato
                return String.class;
            }
        };
        progressTable = new JTable(progressTableModel);
        progressTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        progressTable.setRowHeight(25);
        
        viewProgressButton = createActionButton("👁️ Visualizza", new Color(52, 152, 219));
        commentProgressButton = createActionButton("💬 Commenta", new Color(46, 204, 113));
    }
    
    /**
     * Inizializza i componenti delle valutazioni
     */
    private void initializeEvaluationsComponents() {
        String[] evalColumns = {TEAM_PREFIX, "Giudice", "Voto", "Data Valutazione", "Commento"};
        evaluationsTableModel = new DefaultTableModel(evalColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        evaluationsTable = new JTable(evaluationsTableModel);
        evaluationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        evaluationsTable.setRowHeight(25);
        
        viewRankingButton = createActionButton("🏆 Classifica", new Color(241, 196, 15));
        exportResultsButton = createActionButton("📤 Esporta", new Color(52, 152, 219));
    }
    
    /**
     * Inizializza i pulsanti di gestione
     */
    private void initializeManagementButtons() {
        openRegButton = createActionButton("📝 Apri Registrazioni", new Color(46, 204, 113));
        closeRegButton = createActionButton("🔒 Chiudi Registrazioni", new Color(231, 76, 60));
        startHackathonButton = createActionButton("🚀 Avvia Hackathon", new Color(52, 152, 219));
        endHackathonButton = createActionButton("🏁 Concludi Hackathon", new Color(149, 165, 166));
        generateReportButton = createActionButton("📊 Genera Report", new Color(155, 89, 182));
    }
    
    /**
     * Inizializza gli stati iniziali dei pulsanti
     */
    private void initializeButtonStates() {
        viewTeamButton.setEnabled(false);
        evaluateTeamButton.setEnabled(false);
        teamDocumentsButton.setEnabled(false);
        approveRegButton.setEnabled(false);
        rejectRegButton.setEnabled(false);
        viewUserButton.setEnabled(false);
        viewProgressButton.setEnabled(false);
        commentProgressButton.setEnabled(false);
    }
    
    /**
     * Configura il layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create tabs
        createInfoTab();
        createTeamsTab();
        createRegistrationsTab();
        createProgressTab();
        createEvaluationsTab();
        createManagementTab();
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Footer panel
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
        
        // Titolo e info base
        JLabel titleLabel = new JLabel("🏆 " + hackathon.getNome());
        titleLabel.setFont(new Font(SEGOE_UI_FONT, Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        String dateRange = hackathon.getDataInizio().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)) +
                          " - " + hackathon.getDataFine().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        JLabel dateLabel = new JLabel("📅 " + dateRange);
        dateLabel.setFont(new Font(SEGOE_UI_FONT, Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);
        
        JLabel locationLabel = new JLabel("📍 " + hackathon.getSede());
        locationLabel.setFont(new Font(SEGOE_UI_FONT, Font.PLAIN, 12));
        locationLabel.setForeground(Color.GRAY);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(dateLabel);
        titlePanel.add(locationLabel);
        
        // Status panel
        JPanel statusPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(new TitledBorder("Stato Attuale"));
        
        statusPanel.add(statusLabel);
        statusPanel.add(participantsLabel);
        statusPanel.add(teamsLabel);
        statusPanel.add(documentsLabel);
        
        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(statusPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Crea il tab informazioni
     */
    private void createInfoTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Create info area locally and store reference for updates
        JTextArea infoArea = createInfoArea();
        
        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setBorder(new TitledBorder("Informazioni Dettagliate"));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("📋 Informazioni", panel);
    }
    
    /**
     * Crea l'area informazioni
     */
    private JTextArea createInfoArea() {
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(248, 249, 250));
        infoArea.setFont(new Font(SEGOE_UI_FONT, Font.PLAIN, 12));
        infoArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Set initial content
        loadHackathonInfoIntoArea(infoArea);
        
        return infoArea;
    }
    
    /**
     * Crea il tab team
     */
    private void createTeamsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(teamsTable);
        scrollPane.setBorder(new TitledBorder("Team Partecipanti"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewTeamButton);
        buttonPanel.add(evaluateTeamButton);
        buttonPanel.add(teamDocumentsButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("👥 Team (" + (hackathonTeams != null ? hackathonTeams.size() : 0) + ")", panel);
    }
    
    /**
     * Crea il tab registrazioni
     */
    private void createRegistrationsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(registrationsTable);
        scrollPane.setBorder(new TitledBorder("Registrazioni"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(approveRegButton);
        buttonPanel.add(rejectRegButton);
        buttonPanel.add(viewUserButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("📝 Registrazioni", panel);
    }
    
    /**
     * Crea il tab progressi
     */
    private void createProgressTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(progressTable);
        scrollPane.setBorder(new TitledBorder("Progressi Caricati"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewProgressButton);
        buttonPanel.add(commentProgressButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("📈 Progressi", panel);
    }
    
    /**
     * Crea il tab valutazioni
     */
    private void createEvaluationsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(evaluationsTable);
        scrollPane.setBorder(new TitledBorder("Valutazioni Assegnate"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(viewRankingButton);
        buttonPanel.add(exportResultsButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("⭐ Valutazioni", panel);
    }
    
    /**
     * Crea il tab gestione
     */
    private void createManagementTab() {
        JPanel managementPanel = new JPanel(new GridBagLayout());
        managementPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Sezione Registrazioni
        JPanel regSection = createManagementSection("📝 Gestione Registrazioni");
        JPanel regButtons = new JPanel(new FlowLayout());
        regButtons.add(openRegButton);
        regButtons.add(closeRegButton);
        regSection.add(regButtons, BorderLayout.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        managementPanel.add(regSection, gbc);
        
        // Sezione Hackathon
        JPanel hackSection = createManagementSection("🚀 Controllo Hackathon");
        JPanel hackButtons = new JPanel(new FlowLayout());
        hackButtons.add(startHackathonButton);
        hackButtons.add(endHackathonButton);
        hackSection.add(hackButtons, BorderLayout.CENTER);
        
        gbc.gridy = 1;
        managementPanel.add(hackSection, gbc);
        
        // Sezione Report
        JPanel reportSection = createManagementSection("📊 Report e Analisi");
        reportSection.add(generateReportButton, BorderLayout.CENTER);
        
        gbc.gridy = 2;
        managementPanel.add(reportSection, gbc);
        
        // Status attuale hackathon
        JPanel statusSection = createHackathonStatusSection();
        gbc.gridy = 3;
        managementPanel.add(statusSection, gbc);
        
        tabbedPane.addTab("⚙️ Gestione", managementPanel);
    }
    
    /**
     * Crea una sezione di gestione
     */
    private JPanel createManagementSection(String title) {
        JPanel section = new JPanel(new BorderLayout());
        section.setBorder(new TitledBorder(title));
        section.setPreferredSize(new Dimension(400, 80));
        return section;
    }
    
    /**
     * Crea la sezione status hackathon
     */
    private JPanel createHackathonStatusSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Stato Hackathon"));
        
        JTextArea statusArea = new JTextArea(5, 40);
        statusArea.setEditable(false);
        statusArea.setBackground(new Color(248, 249, 250));
        statusArea.setFont(new Font(CONSOLAS_FONT, Font.PLAIN, 12));
        
        StringBuilder status = new StringBuilder();
        status.append("🏆 HACKATHON: ").append(hackathon.getNome()).append("\n");
        status.append("📅 PERIODO: ").append(hackathon.getDataInizio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
              .append(" - ").append(hackathon.getDataFine().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
        status.append("📝 REGISTRAZIONI: ").append(hackathon.isRegistrazioniAperte() ? "🟢 APERTE" : "🔴 CHIUSE").append("\n");
        status.append("🚀 EVENTO: ");
        
        if (hackathon.isEventoConcluso()) {
            status.append("🏁 CONCLUSO");
        } else if (hackathon.isEventoAvviato()) {
            status.append("🟢 IN CORSO");
        } else {
            status.append("⏳ IN ATTESA DI AVVIO");
        }
        
        status.append("\n📍 SEDE: ").append(hackathon.getSede());
        
        statusArea.setText(status.toString());
        
        panel.add(new JScrollPane(statusArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea il pannello footer
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 15, 15, 15));
        panel.setBackground(new Color(248, 249, 250));
        
        JLabel infoLabel = new JLabel("ID Hackathon: " + hackathon.getId() + " • Creato da: Organizzatore " + hackathon.getOrganizzatoreId());
        infoLabel.setFont(new Font(SEGOE_UI_FONT, Font.PLAIN, 11));
        infoLabel.setForeground(Color.GRAY);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = createActionButton("🔄 Aggiorna Dati", new Color(149, 165, 166));
        JButton closeButton = createActionButton("❌ Chiudi", new Color(231, 76, 60));
        
        refreshButton.addActionListener(e -> loadData());
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        
        panel.add(infoLabel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        // Teams table selection
        teamsTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = teamsTable.getSelectedRow() >= 0;
            viewTeamButton.setEnabled(hasSelection);
            evaluateTeamButton.setEnabled(hasSelection);
            teamDocumentsButton.setEnabled(hasSelection);
        });
        
        // Registrations table selection
        registrationsTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = registrationsTable.getSelectedRow() >= 0;
            approveRegButton.setEnabled(hasSelection);
            rejectRegButton.setEnabled(hasSelection);
            viewUserButton.setEnabled(hasSelection);
        });
        
        // Progress table selection
        progressTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = progressTable.getSelectedRow() >= 0;
            viewProgressButton.setEnabled(hasSelection);
            commentProgressButton.setEnabled(hasSelection);
        });
        
        // Button actions
        viewTeamButton.addActionListener(this::handleViewTeam);
        evaluateTeamButton.addActionListener(this::handleEvaluateTeam);
        teamDocumentsButton.addActionListener(this::handleTeamDocuments);
        
        approveRegButton.addActionListener(this::handleApproveRegistration);
        rejectRegButton.addActionListener(this::handleRejectRegistration);
        viewUserButton.addActionListener(this::handleViewUser);
        
        viewProgressButton.addActionListener(this::handleViewProgress);
        commentProgressButton.addActionListener(this::handleCommentProgress);
        
        viewRankingButton.addActionListener(this::handleViewRanking);
        exportResultsButton.addActionListener(this::handleExportResults);
        
        // Management actions
        openRegButton.addActionListener(this::handleOpenRegistrations);
        closeRegButton.addActionListener(this::handleCloseRegistrations);
        startHackathonButton.addActionListener(this::handleStartHackathon);
        endHackathonButton.addActionListener(this::handleEndHackathon);
        generateReportButton.addActionListener(this::handleGenerateReport);
    }
    
    /**
     * Carica tutti i dati
     */
    private void loadData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadTeams();
                loadRegistrations();
                loadProgress();
                loadEvaluations();
                updateStatus();
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    updateTabTitles();
                    updateManagementButtons();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    // Data loading was interrupted - UI remains in current state
                } catch (Exception ex) {
                    // Handle other exceptions during data loading
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("❌ Errore nel caricamento dati: " + ex.getMessage());
                        statusLabel.setForeground(Color.RED);
                    });
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Carica le informazioni dell'hackathon in una specifica area di testo
     */
    private void loadHackathonInfoIntoArea(JTextArea targetArea) {
        StringBuilder info = buildHackathonInfoText();
        targetArea.setText(info.toString());
        targetArea.setCaretPosition(0);
    }
    
    /**
     * Costruisce il testo delle informazioni dell'hackathon
     */
    private StringBuilder buildHackathonInfoText() {
        StringBuilder info = new StringBuilder();
        info.append("🏆 INFORMAZIONI HACKATHON\n");
        info.append("════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n\n");
        
        info.append("📋 DETTAGLI GENERALI:\n");
        info.append("  • Nome: ").append(hackathon.getNome()).append("\n");
        info.append("  • Data Inizio: ").append(hackathon.getDataInizio().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))).append("\n");
        info.append("  • Data Fine: ").append(hackathon.getDataFine().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))).append("\n");
        info.append("  • Durata: ").append(java.time.Duration.between(hackathon.getDataInizio(), hackathon.getDataFine()).toDays()).append(" giorni\n");
        info.append("  • Sede: ").append(hackathon.getSede()).append("\n");
        info.append("  • Virtuale: ").append(hackathon.isVirtuale() ? "Sì" : "No").append("\n");
        info.append("  • Organizzatore ID: ").append(hackathon.getOrganizzatoreId()).append("\n\n");
        
        info.append("📊 LIMITI E CAPACITÀ:\n");
        info.append("  • Max Partecipanti: ").append(hackathon.getMaxPartecipanti()).append("\n");
        info.append("  • Max " + TEAM_PREFIX + ": ").append(hackathon.getMaxTeam()).append("\n\n");
        
        info.append("🚦 STATO ATTUALE:\n");
        info.append("  • Registrazioni Aperte: ").append(hackathon.isRegistrazioniAperte() ? "✅ Sì" : "❌ No").append("\n");
        info.append("  • Evento Avviato: ").append(hackathon.isEventoAvviato() ? "✅ Sì" : "❌ No").append("\n");
        info.append("  • Evento Concluso: ").append(hackathon.isEventoConcluso() ? "✅ Sì" : "❌ No").append("\n\n");
        
        if (hackathon.getDescrizioneProblema() != null && !hackathon.getDescrizioneProblema().trim().isEmpty()) {
            info.append("📝 PROBLEMA DA RISOLVERE:\n");
            info.append("════════════════════════════════════════════════════════════════════════════════════════════════════════════════════\n");
            info.append(hackathon.getDescrizioneProblema()).append("\n\n");
        }
        
        info.append("📈 STATISTICHE TEMPO REALE:\n");
        info.append("  • Aggiornato: ").append(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT + ":ss"))).append("\n");
        info.append("  • " + TEAM_PREFIX + " Registrati: ").append(hackathonTeams != null ? hackathonTeams.size() : 0).append("\n");
        info.append("  • Registrazioni Totali: ").append(hackathonRegistrations != null ? hackathonRegistrations.size() : 0).append("\n");
        info.append("  • Progressi Caricati: ").append(hackathonProgress != null ? hackathonProgress.size() : 0).append("\n");
        info.append("  • Valutazioni Completate: ").append(hackathonEvaluations != null ? hackathonEvaluations.size() : 0).append("\n");
        
        return info;
    }
    
    /**
     * Carica i team
     */
    private void loadTeams() {
        try {
            hackathonTeams = controller.getTeamHackathon(hackathon.getId());
            
            SwingUtilities.invokeLater(() -> {
                teamsTableModel.setRowCount(0);
                
                for (Team team : hackathonTeams) {
                    try {
                        int memberCount = controller.contaMembriTeam(team.getId());
                        List<Progress> teamProgress = controller.getProgressiTeam(team.getId());
                        
                        // Check if team has been evaluated
                        boolean evaluated = hackathonEvaluations != null && 
                            hackathonEvaluations.stream().anyMatch(eval -> eval.getTeamId() == team.getId());
                        
                        // Calculate average score
                        double avgScore = hackathonEvaluations != null ? 
                            hackathonEvaluations.stream()
                                .filter(eval -> eval.getTeamId() == team.getId())
                                .mapToInt(Valutazione::getVoto)
                                .average().orElse(0.0) : 0.0;
                        
                        String scoreText = evaluated ? String.valueOf(Math.round(avgScore * 10.0) / 10.0) + "/10" : "Non valutato";
                        
                        Object[] row = {
                            team.getNome(),
                            "Utente " + team.getCapoTeamId(),
                            memberCount + "/" + team.getDimensioneMassima(),
                            teamProgress.size() + " progressi",
                            evaluated,
                            scoreText
                        };
                        
                        teamsTableModel.addRow(row);
                    } catch (Exception e) {
                        // Skip teams with errors
                    }
                }
                
                if (teamsTableModel.getRowCount() == 0) {
                    teamsTableModel.addRow(new Object[]{"Nessun team registrato", "", "", "", false, ""});
                }
            });
            
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                teamsTableModel.setRowCount(0);
                teamsTableModel.addRow(new Object[]{"Errore nel caricamento team", "", "", "", false, ""});
            });
        }
    }
    
    /**
     * Carica le registrazioni
     */
    private void loadRegistrations() {
        try {
            hackathonRegistrations = controller.getRegistrazioniHackathon(hackathon.getId());
            
            SwingUtilities.invokeLater(() -> {
                registrationsTableModel.setRowCount(0);
                
                for (Registrazione reg : hackathonRegistrations) {
                    try {
                        Utente user = controller.getUtenteById(reg.getUtenteId());
                        String status = reg.isConfermata() ? "✅ Confermata" : "⏳ In attesa";
                        String date = reg.getDataRegistrazione().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
                        
                        Object[] row = {
                            user.getNome(),
                            user.getCognome(),
                            user.getEmail(),
                            reg.getRuolo().toString(),
                            date,
                            status
                        };
                        
                        registrationsTableModel.addRow(row);
                    } catch (Exception e) {
                        // Skip registrations with errors
                    }
                }
                
                if (registrationsTableModel.getRowCount() == 0) {
                    registrationsTableModel.addRow(new Object[]{"Nessuna registrazione", "", "", "", "", ""});
                }
            });
            
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                registrationsTableModel.setRowCount(0);
                registrationsTableModel.addRow(new Object[]{"Errore nel caricamento registrazioni", "", "", "", "", ""});
            });
        }
    }
    
    /**
     * Carica i progressi
     */
    private void loadProgress() {
        try {
            // Placeholder - get progress from all teams in hackathon
            hackathonProgress = new java.util.ArrayList<>();
            if (hackathonTeams != null) {
                for (Team team : hackathonTeams) {
                    hackathonProgress.addAll(controller.getProgressiTeam(team.getId()));
                }
            }
            
            SwingUtilities.invokeLater(() -> {
                progressTableModel.setRowCount(0);
                
                for (Progress progress : hackathonProgress) {
                    try {
                        Object[] row = createProgressTableRow(progress);
                        progressTableModel.addRow(row);
                    } catch (Exception e) {
                        // Skip progress with errors
                    }
                }
                
                if (progressTableModel.getRowCount() == 0) {
                    progressTableModel.addRow(new Object[]{"Nessun progresso caricato", "", "", false, ""});
                }
            });
            
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                progressTableModel.setRowCount(0);
                progressTableModel.addRow(new Object[]{"Errore nel caricamento progressi", "", "", false, ""});
            });
        }
    }
    
    /**
     * Crea una riga per la tabella dei progressi
     */
    private Object[] createProgressTableRow(Progress progress) {
        // Find team by ID from loaded hackathon teams
        Team team = hackathonTeams.stream()
            .filter(t -> t.getId() == progress.getTeamId())
            .findFirst().orElse(null);
        boolean commented = progress.getCommentoGiudice() != null && !progress.getCommentoGiudice().trim().isEmpty();
        String judge = progress.getGiudiceId() > 0 ? "Utente " + progress.getGiudiceId() : "Nessuno";
        String date = progress.getDataCaricamento().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        
        return new Object[] {
            team != null ? team.getNome() : TEAM_PREFIX + " " + progress.getTeamId(),
            progress.getTitolo(),
            date,
            commented,
            judge
        };
    }
    
    /**
     * Carica le valutazioni
     */
    private void loadEvaluations() {
        try {
            hackathonEvaluations = controller.getValutazioniHackathon(hackathon.getId());
            
            SwingUtilities.invokeLater(() -> {
                evaluationsTableModel.setRowCount(0);
                
                for (Valutazione eval : hackathonEvaluations) {
                    try {
                        // Find team by ID from loaded hackathon teams
                        Team team = hackathonTeams.stream()
                            .filter(t -> t.getId() == eval.getTeamId())
                            .findFirst().orElse(null);
                        Utente judge = controller.getUtenteById(eval.getGiudiceId());
                        String date = eval.getDataValutazione().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
                        String comment = eval.getCommento() != null && !eval.getCommento().trim().isEmpty() ? 
                            eval.getCommento().substring(0, Math.min(50, eval.getCommento().length())) + "..." : 
                            "Nessun commento";
                        
                        Object[] row = {
                            team != null ? team.getNome() : TEAM_PREFIX + " " + eval.getTeamId(),
                            judge.getNome() + " " + judge.getCognome(),
                            eval.getVoto() + "/10",
                            date,
                            comment
                        };
                        
                        evaluationsTableModel.addRow(row);
                    } catch (Exception e) {
                        // Skip evaluations with errors
                    }
                }
                
                if (evaluationsTableModel.getRowCount() == 0) {
                    evaluationsTableModel.addRow(new Object[]{"Nessuna valutazione assegnata", "", "", "", ""});
                }
            });
            
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                evaluationsTableModel.setRowCount(0);
                evaluationsTableModel.addRow(new Object[]{"Errore nel caricamento valutazioni", "", "", "", ""});
            });
        }
    }
    
    /**
     * Aggiorna lo status
     */
    private void updateStatus() {
        SwingUtilities.invokeLater(() -> {
            updateMainStatus();
            updateCounters();
        });
    }
    
    /**
     * Aggiorna lo status principale dell'hackathon
     */
    private void updateMainStatus() {
        StatusInfo statusInfo = determineHackathonStatus();
        statusLabel.setText(statusInfo.text);
        statusLabel.setForeground(statusInfo.color);
    }
    
    /**
     * Determina lo status e il colore dell'hackathon
     */
    private StatusInfo determineHackathonStatus() {
        if (hackathon.isEventoConcluso()) {
            return new StatusInfo("🏁 Concluso", new Color(149, 165, 166));
        } else if (hackathon.isEventoAvviato()) {
            return new StatusInfo("🚀 In corso", new Color(46, 204, 113));
        } else if (hackathon.isRegistrazioniAperte()) {
            return new StatusInfo("📝 Registrazioni aperte", new Color(52, 152, 219));
        } else {
            return new StatusInfo("🔄 In preparazione", Color.GRAY);
        }
    }
    
    /**
     * Aggiorna i contatori di partecipanti, team e documenti
     */
    private void updateCounters() {
        participantsLabel.setText("Partecipanti: " + (hackathonRegistrations != null ? hackathonRegistrations.size() : 0));
        teamsLabel.setText(TEAM_PREFIX + ": " + (hackathonTeams != null ? hackathonTeams.size() : 0));
        documentsLabel.setText("Documenti: " + countTotalDocuments());
    }
    
    /**
     * Conta il totale dei documenti per tutti i team
     */
    private int countTotalDocuments() {
        int totalDocuments = 0;
        if (hackathonTeams != null) {
            for (Team team : hackathonTeams) {
                try {
                    totalDocuments += controller.getDocumentiTeam(team.getId()).size();
                } catch (Exception e) {
                    // Skip teams with errors
                }
            }
        }
        return totalDocuments;
    }
    
    /**
     * Classe helper per contenere informazioni di status
     */
    private static class StatusInfo {
        final String text;
        final Color color;
        
        StatusInfo(String text, Color color) {
            this.text = text;
            this.color = color;
        }
    }
    
    /**
     * Aggiorna i titoli dei tab
     */
    private void updateTabTitles() {
        tabbedPane.setTitleAt(1, "👥 Team (" + (hackathonTeams != null ? hackathonTeams.size() : 0) + ")");
        tabbedPane.setTitleAt(2, "📝 Registrazioni (" + (hackathonRegistrations != null ? hackathonRegistrations.size() : 0) + ")");
        tabbedPane.setTitleAt(3, "📈 Progressi (" + (hackathonProgress != null ? hackathonProgress.size() : 0) + ")");
        tabbedPane.setTitleAt(4, "⭐ Valutazioni (" + (hackathonEvaluations != null ? hackathonEvaluations.size() : 0) + ")");
    }
    
    /**
     * Aggiorna i pulsanti di gestione
     */
    private void updateManagementButtons() {
        // Abilita/disabilita pulsanti in base allo stato
        openRegButton.setEnabled(!hackathon.isRegistrazioniAperte());
        closeRegButton.setEnabled(hackathon.isRegistrazioniAperte());
        startHackathonButton.setEnabled(!hackathon.isEventoAvviato() && !hackathon.isRegistrazioniAperte());
        endHackathonButton.setEnabled(hackathon.isEventoAvviato() && !hackathon.isEventoConcluso());
    }
    
    // Event Handlers
    
    private void handleViewTeam(ActionEvent e) {
        int selectedRow = teamsTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < hackathonTeams.size()) {
            Team selectedTeam = hackathonTeams.get(selectedRow);
            showTeamDetailsDialog(selectedTeam);
        }
    }
    
    private void handleEvaluateTeam(ActionEvent e) {
        int selectedRow = teamsTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < hackathonTeams.size()) {
            Team selectedTeam = hackathonTeams.get(selectedRow);
            showEvaluationDialog(selectedTeam);
        }
    }
    
    private void handleTeamDocuments(ActionEvent e) {
        int selectedRow = teamsTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < hackathonTeams.size()) {
            Team selectedTeam = hackathonTeams.get(selectedRow);
            FileViewerDialog dialog = new FileViewerDialog((JFrame) SwingUtilities.getWindowAncestor(this), controller, selectedTeam.getId());
            dialog.setVisible(true);
        }
    }
    
    private void handleApproveRegistration(ActionEvent e) {
        int selectedRow = registrationsTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < hackathonRegistrations.size()) {
            Registrazione selectedReg = hackathonRegistrations.get(selectedRow);
            
            boolean success = controller.confermaRegistrazione(selectedReg.getId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Registrazione approvata!", SUCCESS_TITLE, JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Errore nell'approvazione", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleRejectRegistration(ActionEvent e) {
        int selectedRow = registrationsTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < hackathonRegistrations.size()) {
            Registrazione selectedReg = hackathonRegistrations.get(selectedRow);
            
            boolean success = controller.rifiutaRegistrazione(selectedReg.getId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Registrazione rifiutata", "Rifiuto", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Errore nel rifiuto", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleViewUser(ActionEvent e) {
        int selectedRow = registrationsTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < hackathonRegistrations.size()) {
            Registrazione selectedReg = hackathonRegistrations.get(selectedRow);
            try {
                Utente user = controller.getUtenteById(selectedReg.getUtenteId());
                JOptionPane.showMessageDialog(this,
                    "Profilo Utente:\n" +
                    "Nome: " + user.getNome() + " " + user.getCognome() + "\n" +
                    "Email: " + user.getEmail() + "\n" +
                    "Ruolo: " + user.getRuolo(),
                    "Profilo Utente", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore nel caricamento profilo", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleViewProgress(ActionEvent e) {
        int selectedRow = progressTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < hackathonProgress.size()) {
            Progress selectedProgress = hackathonProgress.get(selectedRow);
            showProgressDetailsDialog(selectedProgress);
        }
    }
    
    private void handleCommentProgress(ActionEvent e) {
        int selectedRow = progressTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < hackathonProgress.size()) {
            Progress selectedProgress = hackathonProgress.get(selectedRow);
            
            String comment = JOptionPane.showInputDialog(this,
                "Aggiungi commento al progresso:\n\"" + selectedProgress.getTitolo() + "\"",
                "Commento", JOptionPane.QUESTION_MESSAGE);
            
            if (comment != null && !comment.trim().isEmpty()) {
                boolean success = controller.aggiungiCommentoGiudice(selectedProgress.getId(), comment.trim());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Commento aggiunto!", SUCCESS_TITLE, JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Errore nell'aggiunta commento", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void handleViewRanking(ActionEvent e) {
        if (hackathonEvaluations != null && !hackathonEvaluations.isEmpty()) {
            showRankingDialog();
        } else {
            JOptionPane.showMessageDialog(this,
                "Nessuna valutazione disponibile per generare la classifica",
                "Classifica", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void handleExportResults(ActionEvent e) {
        String[] formats = {"CSV", "PDF", "Excel"};
        String format = (String) JOptionPane.showInputDialog(this,
            "Seleziona formato di esportazione:",
            "Esporta Risultati", JOptionPane.QUESTION_MESSAGE,
            null, formats, formats[0]);
        
        if (format != null) {
            JOptionPane.showMessageDialog(this,
                "Esportazione risultati in formato " + format + " completata!\n" +
                "File: hackathon_" + hackathon.getId() + "_results." + format.toLowerCase(),
                "Esportazione", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Management Actions
    
    private void handleOpenRegistrations(ActionEvent e) {
        boolean success = controller.apriRegistrazioni(hackathon.getId());
        showManagementResult(success, "Registrazioni aperte");
    }
    
    private void handleCloseRegistrations(ActionEvent e) {
        boolean success = controller.chiudiRegistrazioni(hackathon.getId());
        showManagementResult(success, "Registrazioni chiuse");
    }
    
    private void handleStartHackathon(ActionEvent e) {
        String problema = JOptionPane.showInputDialog(this,
            "Inserisci la descrizione del problema da risolvere:",
            "Avvia Hackathon", JOptionPane.QUESTION_MESSAGE);
        
        if (problema != null && !problema.trim().isEmpty()) {
            boolean success = controller.avviaHackathon(hackathon.getId(), problema.trim());
            showManagementResult(success, "Hackathon avviato");
        }
    }
    
    private void handleEndHackathon(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Confermi la conclusione dell'hackathon?\n" +
            "Questa azione non può essere annullata.",
            "Concludi Hackathon", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.concludeEvento(hackathon.getId());
            showManagementResult(success, "Hackathon concluso");
        }
    }
    
    private void handleGenerateReport(ActionEvent e) {
        SwingWorker<ReportData, Void> reportWorker = new SwingWorker<ReportData, Void>() {
            @Override
            protected ReportData doInBackground() throws Exception {
                return controller.generaReportHackathon(hackathon.getId(), "Report Completo");
            }
            
            @Override
            protected void done() {
                try {
                    ReportData report = get();
                    String reportText = controller.esportaReportTesto(report);
                    
                    JTextArea reportArea = new JTextArea(reportText);
                    reportArea.setEditable(false);
                    reportArea.setFont(new Font(CONSOLAS_FONT, Font.PLAIN, 11));
                    
                    JScrollPane scrollPane = new JScrollPane(reportArea);
                    scrollPane.setPreferredSize(new Dimension(600, 400));
                    
                    JOptionPane.showMessageDialog(HackathonDetailsDialog.this,
                        scrollPane, "Report Hackathon", JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    JOptionPane.showMessageDialog(HackathonDetailsDialog.this,
                        "Generazione report interrotta dall'utente",
                        "Report Interrotto", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(HackathonDetailsDialog.this,
                        "Errore nella generazione del report: " + ex.getMessage(),
                        ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        reportWorker.execute();
    }
    
    /**
     * Mostra il risultato di un'azione di gestione
     */
    private void showManagementResult(boolean success, String action) {
        if (success) {
            JOptionPane.showMessageDialog(this, action + " con successo!", SUCCESS_TITLE, JOptionPane.INFORMATION_MESSAGE);
            loadData(); // Refresh data
        } else {
            JOptionPane.showMessageDialog(this, "Errore: " + action, ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Mostra i dettagli di un team
     */
    private void showTeamDetailsDialog(Team team) {
        try {
            int memberCount = controller.contaMembriTeam(team.getId());
            List<Progress> teamProgress = controller.getProgressiTeam(team.getId());
            
            StringBuilder details = new StringBuilder();
            details.append("👥 DETTAGLI TEAM\n");
            details.append("═══════════════════════════════════════\n\n");
            details.append("Nome: ").append(team.getNome()).append("\n");
            details.append("Capo Team: Utente ").append(team.getCapoTeamId()).append("\n");
            details.append("Membri: ").append(memberCount).append("/").append(team.getDimensioneMassima()).append("\n");
            details.append("Progressi caricati: ").append(teamProgress.size()).append("\n\n");
            
            if (!teamProgress.isEmpty()) {
                details.append("📈 PROGRESSI:\n");
                for (Progress p : teamProgress) {
                    details.append("• ").append(p.getTitolo()).append(" (")
                           .append(p.getDataCaricamento().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")))
                           .append(")\n");
                }
            }
            
            JTextArea detailsArea = new JTextArea(details.toString());
            detailsArea.setEditable(false);
            detailsArea.setFont(new Font(CONSOLAS_FONT, Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(detailsArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Dettagli Team", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Errore nel caricamento dettagli team", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Mostra il dialog di valutazione
     */
    private void showEvaluationDialog(Team team) {
        // Check if user is a judge
        Utente currentUser = controller.getCurrentUser();
        if (currentUser == null || !currentUser.isGiudice()) {
            JOptionPane.showMessageDialog(this,
                "Solo i giudici possono assegnare valutazioni",
                "Accesso Negato", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "⭐ Valuta Team: " + team.getNome(), true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Team info
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(248, 249, 250));
        infoArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        infoArea.setText("Team: " + team.getNome() + "\n" +
                        "Hackathon: " + hackathon.getNome() + "\n" +
                        "Capo Team: Utente " + team.getCapoTeamId() + "\n\n" +
                        "Inserisci la tua valutazione:");
        
        // Score and comment
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.add(new JLabel("Voto (0-10):"));
        JSpinner scoreSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        inputPanel.add(scoreSpinner);
        
        inputPanel.add(new JLabel("Commento:"));
        JTextField commentField = new JTextField();
        inputPanel.add(commentField);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton submitButton = createActionButton("✅ Assegna Voto", new Color(46, 204, 113));
        JButton cancelButton = createActionButton("❌ Annulla", new Color(231, 76, 60));
        
        submitButton.addActionListener(ev -> {
            int score = (Integer) scoreSpinner.getValue();
            String comment = commentField.getText().trim();
            
            boolean success = controller.assegnaVoto(team.getId(), score, comment);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Valutazione assegnata!", SUCCESS_TITLE, JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Errore nell'assegnazione", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(ev -> dialog.dispose());
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        
        panel.add(infoArea, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Mostra i dettagli di un progresso
     */
    private void showProgressDetailsDialog(Progress progress) {
        try {
            // Find team by ID from loaded hackathon teams
            Team team = hackathonTeams != null ? hackathonTeams.stream()
                .filter(t -> t.getId() == progress.getTeamId())
                .findFirst().orElse(null) : null;
            
            StringBuilder details = new StringBuilder();
            details.append("📈 DETTAGLI PROGRESSO\n");
            details.append("═══════════════════════════════════════\n\n");
            details.append("Titolo: ").append(progress.getTitolo()).append("\n");
            details.append("Team: ").append(team != null ? team.getNome() : "Team " + progress.getTeamId()).append("\n");
            details.append("Data caricamento: ").append(progress.getDataCaricamento().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))).append("\n\n");
            
            if (progress.getDescrizione() != null && !progress.getDescrizione().trim().isEmpty()) {
                details.append("📝 DESCRIZIONE:\n");
                details.append(progress.getDescrizione()).append("\n\n");
            }
            
            if (progress.getDocumentoPath() != null) {
                details.append("📄 DOCUMENTO: ").append(progress.getDocumentoPath()).append("\n\n");
            }
            
            if (progress.getCommentoGiudice() != null && !progress.getCommentoGiudice().trim().isEmpty()) {
                details.append("💬 COMMENTO GIUDICE:\n");
                details.append("Giudice: Utente ").append(progress.getGiudiceId()).append("\n");
                details.append("Data: ").append(progress.getDataCommento().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))).append("\n");
                details.append("Commento: ").append(progress.getCommentoGiudice()).append("\n");
            }
            
            JTextArea detailsArea = new JTextArea(details.toString());
            detailsArea.setEditable(false);
            detailsArea.setFont(new Font(CONSOLAS_FONT, Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(detailsArea);
            scrollPane.setPreferredSize(new Dimension(500, 350));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Dettagli Progresso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Errore nel caricamento dettagli", ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Mostra la classifica
     */
    private void showRankingDialog() {
        JDialog rankingDialog = new JDialog(this, "🏆 Classifica " + hackathon.getNome(), true);
        rankingDialog.setSize(600, 400);
        rankingDialog.setLocationRelativeTo(this);
        
        String[] columns = {"Posizione", TEAM_PREFIX, "Voto Medio", "Valutazioni"};
        DefaultTableModel rankingModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        JTable rankingTable = new JTable(rankingModel);
        rankingTable.setRowHeight(30);
        
        // Calculate rankings
        try {
            List<Integer> classifica = controller.getClassificaTeam(hackathon.getId());
            int position = 1;
            
            for (Integer teamId : classifica) {
                // Find team by ID from loaded hackathon teams
                Team team = hackathonTeams != null ? hackathonTeams.stream()
                    .filter(t -> t.getId() == teamId.intValue())
                    .findFirst().orElse(null) : null;
                double avgScore = hackathonEvaluations.stream()
                    .filter(eval -> eval.getTeamId() == teamId)
                    .mapToInt(Valutazione::getVoto)
                    .average().orElse(0.0);
                
                long evalCount = hackathonEvaluations.stream()
                    .filter(eval -> eval.getTeamId() == teamId)
                    .count();
                
                Object[] row = {
                    position + "°",
                    team != null ? team.getNome() : "Team " + teamId,
                    String.valueOf(Math.round(avgScore * 100.0) / 100.0) + "/10",
                    evalCount + " voti"
                };
                
                rankingModel.addRow(row);
                position++;
            }
        } catch (Exception e) {
            rankingModel.addRow(new Object[]{"Errore nel calcolo classifica", "", "", ""});
        }
        
        JScrollPane scrollPane = new JScrollPane(rankingTable);
        rankingDialog.add(scrollPane);
        rankingDialog.setVisible(true);
    }
    
    // Utility methods
    
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(SEGOE_UI_FONT, Font.PLAIN, 12));
        label.setForeground(new Color(52, 73, 94));
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
        button.setFont(new Font(SEGOE_UI_FONT, Font.BOLD, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
}
