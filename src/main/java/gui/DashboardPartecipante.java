package gui;

import controller.Controller;
import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dashboard specifica per i partecipanti agli hackathon.
 * Fornisce una vista ottimizzata per le attività dei partecipanti.
 */
public class DashboardPartecipante extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    // Costanti per evitare duplicazione di stringhe
    @SuppressWarnings("SpellCheckingInspection")
    private static final String FONT_FAMILY = "Segoe UI";
    
    private final transient Controller controller;
    private final transient JFrame mainFrame;
    
    // Componenti dashboard
    
    // Statistiche
    private JLabel totalHackathonLabel;
    private JLabel activeTeamLabel;
    private JLabel documentsUploadedLabel;
    private JLabel notificationsCountLabel;
    
    // Team corrente
    private JLabel currentTeamLabel;
    private JLabel teamMembersLabel;
    private JButton viewTeamButton;
    private JButton uploadDocumentButton;
    
    // Hackathon attivi
    private DefaultListModel<String> hackathonListModel;
    private JList<String> hackathonList;
    private JButton joinHackathonButton;
    
    // Notifiche recenti
    private DefaultListModel<String> notificationListModel;
    private JList<String> notificationList;
    private JButton viewAllNotificationsButton;
    
    // Quick Actions
    private JButton createTeamButton;
    private JButton findTeamButton;
    private JButton viewProgressButton;
    
    // Dati
    private transient List<Hackathon> availableHackathons;
    private transient Team currentTeam;
    
    /**
     * Costruttore della dashboard partecipante
     */
    public DashboardPartecipante(Controller controller, JFrame mainFrame) {
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
        activeTeamLabel = createStatLabel("Nessuno");
        documentsUploadedLabel = createStatLabel("0");
        notificationsCountLabel = createStatLabel("0");
        
        // Team corrente
        currentTeamLabel = createInfoLabel("Nessun team attivo");
        teamMembersLabel = createInfoLabel("0 membri");
        viewTeamButton = createActionButton("👥 Visualizza Team", new Color(52, 152, 219));
        uploadDocumentButton = createActionButton("📤 Carica Documento", new Color(46, 204, 113));
        
        // Hackathon disponibili
        hackathonListModel = new DefaultListModel<>();
        hackathonList = new JList<>(hackathonListModel);
        hackathonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hackathonList.setVisibleRowCount(4);
        joinHackathonButton = createActionButton("Iscriviti", new Color(155, 89, 182));
        
        // Notifiche recenti
        notificationListModel = new DefaultListModel<>();
        notificationList = new JList<>(notificationListModel);
        notificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notificationList.setVisibleRowCount(4);
        viewAllNotificationsButton = createActionButton("Tutte le Notifiche", new Color(241, 196, 15));
        
        // Quick Actions
        createTeamButton = createActionButton("Crea Team", new Color(46, 204, 113));
        findTeamButton = createActionButton("Trova Team", new Color(52, 152, 219));
        viewProgressButton = createActionButton("I Miei Progressi", new Color(155, 89, 182));
        
        // Initially disable buttons that require team
        viewTeamButton.setEnabled(false);
        uploadDocumentButton.setEnabled(false);
        viewProgressButton.setEnabled(false);
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
        JPanel teamPanel = createTeamPanel();
        JPanel hackathonPanel = createHackathonPanel();
        JPanel notificationsPanel = createNotificationsPanel();
        JPanel quickActionsPanel = createQuickActionsPanel();
        
        // Aggiungi pannelli al layout
        mainPanel.add(statsPanel);
        mainPanel.add(teamPanel);
        mainPanel.add(hackathonPanel);
        mainPanel.add(notificationsPanel);
        mainPanel.add(quickActionsPanel);
        
        // Pannello vuoto per bilanciare il layout
        JPanel emptyPanel = createEmptyPanel();
        mainPanel.add(emptyPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Footer con refresh
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
        
        // Titolo e benvenuto
        JLabel titleLabel = new JLabel("Dashboard Partecipante");
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        Utente currentUser = controller.getCurrentUser();
        String welcomeText = currentUser != null ? 
            "Benvenuto, " + currentUser.getNome() + " " + currentUser.getCognome() + "!" :
            "Benvenuto!";
        JLabel welcomeLabel = new JLabel(welcomeText);
        welcomeLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.GRAY);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(welcomeLabel, BorderLayout.SOUTH);
        
        // Data e ora corrente
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
        JPanel panel = createDashboardPanel("Le Tue Statistiche");
        panel.setLayout(new GridLayout(2, 2, 10, 10));
        
        panel.add(createStatCard("Hackathon Partecipati", totalHackathonLabel, ""));
        panel.add(createStatCard("Team Attivo", activeTeamLabel, ""));
        panel.add(createStatCard("Documenti Caricati", documentsUploadedLabel, ""));
        panel.add(createStatCard("Notifiche Non Lette", notificationsCountLabel, ""));
        
        return panel;
    }
    
    /**
     * Crea il pannello team corrente
     */
    private JPanel createTeamPanel() {
        JPanel panel = createDashboardPanel("👥 Il Mio Team");
        panel.setLayout(new BorderLayout(5, 5));
        
        // Info team
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(currentTeamLabel);
        infoPanel.add(teamMembersLabel);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(viewTeamButton);
        buttonPanel.add(uploadDocumentButton);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crea il pannello hackathon disponibili
     */
    private JPanel createHackathonPanel() {
        JPanel panel = createDashboardPanel("🚀 Hackathon Disponibili");
        panel.setLayout(new BorderLayout(5, 5));
        
        JScrollPane scrollPane = new JScrollPane(hackathonList);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(joinHackathonButton, BorderLayout.SOUTH);
        
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
     * Crea il pannello azioni rapide
     */
    private JPanel createQuickActionsPanel() {
        JPanel panel = createDashboardPanel("⚡ Azioni Rapide");
        panel.setLayout(new GridLayout(3, 1, 5, 5));
        
        panel.add(createTeamButton);
        panel.add(findTeamButton);
        panel.add(viewProgressButton);
        
        return panel;
    }
    
    /**
     * Crea un pannello vuoto per bilanciare il layout
     */
    private JPanel createEmptyPanel() {
        JPanel panel = createDashboardPanel("💡 Suggerimenti");
        panel.setLayout(new BorderLayout());
        
        JTextArea tipsArea = new JTextArea();
        tipsArea.setEditable(false);
        tipsArea.setBackground(Color.WHITE);
        tipsArea.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        tipsArea.setLineWrap(true);
        tipsArea.setWrapStyleWord(true);
        tipsArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        @SuppressWarnings("SpellCheckingInspection")
        String tips = """
            SUGGERIMENTI PER PARTECIPANTI:

            - Unisciti a un team prima dell'inizio dell'hackathon
            - Carica regolarmente i progressi del tuo progetto
            - Collabora attivamente con i membri del team
            - Rispetta le scadenze per la consegna
            - Controlla spesso le notifiche per aggiornamenti importanti
            """;
        tipsArea.setText(tips);
        
        panel.add(tipsArea, BorderLayout.CENTER);
        
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
        
        panel.add(refreshButton);
        
        return panel;
    }
    
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        // Team actions
        viewTeamButton.addActionListener(this::handleViewTeam);
        uploadDocumentButton.addActionListener(this::handleUploadDocument);
        
        // Hackathon actions
        joinHackathonButton.addActionListener(this::handleJoinHackathon);
        hackathonList.addListSelectionListener(e -> 
            joinHackathonButton.setEnabled(!hackathonList.isSelectionEmpty())
        );
        
        // Notification actions
        viewAllNotificationsButton.addActionListener(this::handleViewAllNotifications);
        
        // Quick actions
        createTeamButton.addActionListener(this::handleCreateTeam);
        findTeamButton.addActionListener(this::handleFindTeam);
        viewProgressButton.addActionListener(this::handleViewProgress);
    }
    
    /**
     * Aggiorna tutti i dati della dashboard
     */
    public void refreshData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                loadStatistics();
                loadCurrentTeam();
                loadAvailableHackathons();
                loadRecentNotifications();
                return null;
            }
            
            @Override
            protected void done() {
                refreshUI();
            }
        };
        worker.execute();
    }
    
    /**
     * Carica le statistiche del partecipante
     */
    private void loadStatistics() {
        try {
            Utente currentUser = controller.getCurrentUser();
            if (currentUser == null) return;
            
            // Conta hackathon partecipati
            List<Registrazione> registrazioni = controller.getRegistrazioniUtente();
            long hackathonCount = registrazioni.stream()
                .filter(r -> r.isPartecipante() && r.isConfermata())
                .count();
            
            // Team attivo
            List<Team> userTeams = controller.getTeamUtente();
            currentTeam = userTeams.isEmpty() ? null : userTeams.get(0);
            
            // Documenti caricati
            List<Documento> userDocuments = controller.getDocumentiUtente();
            
            // Notifiche non lette
            int unreadCount = controller.contaNotificheNonLette();
            
            // Aggiorna labels
            SwingUtilities.invokeLater(() -> {
                totalHackathonLabel.setText(String.valueOf(hackathonCount));
                activeTeamLabel.setText(currentTeam != null ? currentTeam.getNome() : "Nessuno");
                documentsUploadedLabel.setText(String.valueOf(userDocuments.size()));
                notificationsCountLabel.setText(String.valueOf(unreadCount));
            });
            
        } catch (Exception e) {
            // Handle errors silently in dashboard
        }
    }
    
    /**
     * Carica informazioni del team corrente
     */
    private void loadCurrentTeam() {
        SwingUtilities.invokeLater(() -> {
            if (currentTeam != null) {
                currentTeamLabel.setText("Team: " + currentTeam.getNome());
                
                try {
                    int memberCount = controller.contaMembriTeam(currentTeam.getId());
                    teamMembersLabel.setText(memberCount + " membri");
                } catch (Exception e) {
                    teamMembersLabel.setText("N/A membri");
                }
                
                viewTeamButton.setEnabled(true);
                uploadDocumentButton.setEnabled(true);
                viewProgressButton.setEnabled(true);
                createTeamButton.setEnabled(false);
            } else {
                currentTeamLabel.setText("Nessun team attivo");
                teamMembersLabel.setText("Unisciti a un team!");
                
                viewTeamButton.setEnabled(false);
                uploadDocumentButton.setEnabled(false);
                viewProgressButton.setEnabled(false);
                createTeamButton.setEnabled(true);
            }
        });
    }
    
    /**
     * Carica hackathon disponibili
     */
    private void loadAvailableHackathons() {
        try {
            // Usa i metodi disponibili nel Controller per hackathon disponibili all'utente
            availableHackathons = controller.getHackathonDisponibiliPerUtente();
            
            SwingUtilities.invokeLater(() -> {
                hackathonListModel.clear();
                for (Hackathon hackathon : availableHackathons) {
                    String listItem = hackathon.getNome() + " - " + 
                        hackathon.getDataInizio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    hackathonListModel.addElement(listItem);
                }
                
                if (hackathonListModel.isEmpty()) {
                    hackathonListModel.addElement("Nessun hackathon disponibile");
                    joinHackathonButton.setEnabled(false);
                } else {
                    joinHackathonButton.setEnabled(false); // Enabled on selection
                }
            });
            
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                hackathonListModel.clear();
                hackathonListModel.addElement("Errore nel caricamento");
                joinHackathonButton.setEnabled(false);
            });
        }
    }
    
    /**
     * Carica notifiche recenti
     */
    private void loadRecentNotifications() {
        try {
            List<Notification> recentNotifications = controller.getNotificheUtente();
            
            SwingUtilities.invokeLater(() -> {
                notificationListModel.clear();
                int count = 0;
                for (Notification notification : recentNotifications) {
                    if (count >= 5) break; // Mostra solo le prime 5
                    
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
                notificationListModel.addElement("Errore nel caricamento");
            });
        }
    }
    
    /**
     * Aggiorna l'interfaccia utente
     */
    private void refreshUI() {
        // Update colors based on data
        updateStatCardColors();
        revalidate();
        repaint();
    }
    
    /**
     * Aggiorna i colori delle statistiche
     */
    private void updateStatCardColors() {
        // Cambia colori in base ai dati
        if (Integer.parseInt(notificationsCountLabel.getText()) > 0) {
            notificationsCountLabel.setForeground(new Color(231, 76, 60));
        } else {
            notificationsCountLabel.setForeground(new Color(39, 174, 96));
        }
    }
    
    // Event Handlers
    
    private void handleViewTeam(ActionEvent e) {
        if (mainFrame instanceof MainFrame mainframe) {
            mainframe.showTeamPanel();
        } else if (mainFrame instanceof ModernMainFrame) {
            // ModernMainFrame navigation - functionality to be implemented in future versions
            JOptionPane.showMessageDialog(this, "Navigazione non ancora implementata per ModernMainFrame");
        }
    }
    
    private void handleUploadDocument(ActionEvent e) {
        if (currentTeam != null) {
            FileUploadDialog dialog = new FileUploadDialog(mainFrame, controller);
            dialog.setVisible(true);
            refreshData(); // Refresh after upload
        }
    }
    
    private void handleJoinHackathon(ActionEvent e) {
        int selectedIndex = hackathonList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < availableHackathons.size()) {
            Hackathon selectedHackathon = availableHackathons.get(selectedIndex);
            
            // Show registration dialog or navigate to registration panel
            @SuppressWarnings("SpellCheckingInspection")
            int confirm = JOptionPane.showConfirmDialog(this,
                "Vuoi iscriverti all'hackathon:\n" + selectedHackathon.getNome() + "?",
                "Conferma Iscrizione", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = controller.registraUtenteAdHackathon(
                    selectedHackathon.getId(), Registrazione.Ruolo.PARTECIPANTE);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Iscrizione completata con successo!",
                        "Iscrizione", JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Errore durante l'iscrizione. Verifica i requisiti.",
                        "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void handleViewAllNotifications(ActionEvent e) {
        // Navigate to notifications panel
        if (mainFrame instanceof ModernMainFrame) {
            // ModernMainFrame notifications navigation - functionality to be implemented in future versions
            JOptionPane.showMessageDialog(this, "Navigazione notifiche non ancora implementata per ModernMainFrame");
        }
    }
    
    private void handleCreateTeam(ActionEvent e) {
        if (mainFrame instanceof MainFrame mainframe) {
            mainframe.showTeamPanel();
        }
    }
    
    private void handleFindTeam(ActionEvent e) {
        if (mainFrame instanceof MainFrame mainframe) {
            mainframe.showTeamPanel();
        }
    }
    
    private void handleViewProgress(ActionEvent e) {
        if (currentTeam != null) {
            // Navigate to progress panel or show progress dialog
            JOptionPane.showMessageDialog(this,
                "Visualizzazione progressi per team: " + currentTeam.getNome(),
                "Progressi", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Utility methods
    
    /**
     * Crea un pannello base per la dashboard
     */
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
    
    /**
     * Crea una card per le statistiche
     */
    @SuppressWarnings("SameParameterValue")
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
    
    /**
     * Crea una label per le statistiche
     */
    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(FONT_FAMILY, Font.BOLD, 16));
        label.setForeground(new Color(52, 152, 219));
        return label;
    }
    
    /**
     * Crea una label informativa
     */
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        label.setForeground(new Color(52, 73, 94));
        return label;
    }
    
    /**
     * Crea un pulsante per le azioni
     */
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 11));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
}
