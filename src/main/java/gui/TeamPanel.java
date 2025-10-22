package gui;
import controller.Controller;
import model.Team;
import model.Utente;
import model.RichiestaJoin;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.ListSelectionModel;
import static javax.swing.SwingConstants.*;
import java.util.logging.Logger;
import java.util.logging.Level;
/**
 * Pannello per la gestione dei team.
 */
public class TeamPanel extends JPanel {
    // Logger per il debug
    private static final Logger logger = Logger.getLogger(TeamPanel.class.getName());
    
    // Costanti per messaggi
    private static final String ERRORE_TITLE = "Errore";
    
    // Costanti per testi pulsanti
    private static final String AGGIORNA_BTN = "Aggiorna";
    private static final String CREA_BTN = "Crea Team";
    private static final String CREA_TEAM_TEXT = "✅ Crea Team";
    private static final String CREA_NUOVO_TEAM = "Crea Nuovo Team";
    private static final String ANNULLA_BTN = "Annulla";
    private static final String ERRORE_GENERICO = "Errore imprevisto: ";
    private static final String ERRORE_DATABASE = "Errore di database: ";
    private static final String ERRORE_SISTEMA = "Errore Sistema";
    private static final String LISTA_AGGIORNATA = "Lista Aggiornata";
    private static final String CREAZIONE_IN_CORSO = "\u23F3 Creazione...";
    private static final String FONT_SEGOE_UI = "Segoe UI";
    
    // Messaggi statici
    private static final String DIAGNOSTICA_MESSAGE = """
        Diagnostica completata.
        
        Se il pulsante \'Gestisci Team\' non funziona:
        1. Seleziona un team dalla lista
        2. Ricarica il pannello (premi F5 o torna al pannello)
        3. Controlla la console per messaggi di debug
        
        Vuoi ricaricare il pannello ora?""";
    
    private static final String NO_PARTECIPANTI_DISPONIBILI = """
        Nessun partecipante disponibile per l'invito
        
        Possibili motivi:
        • Il nuovo partecipante non è registrato all'hackathon
        • La registrazione non è confermata
        • È già membro del team
        • È il capo team (non può invitare se stesso)
        
        Controlla la console per il debug dettagliato.""";
    
    private static final String NO_PARTECIPANTI_DISPONIBILI_SIMPLE = """
        Nessun partecipante disponibile per l'invito
        
        Controlla la console per debug dettagliato.""";
    
    private static final String NO_PARTECIPANTI_DISPONIBILI_DIALOG = """
        Nessun partecipante disponibile per l'invito.
        
        Possibili motivi:
        • Il nuovo partecipante non è registrato all'hackathon
        • La registrazione non è confermata
        • È già membro del team
        • È il capo team (non può invitare se stesso)
        
        Controlla la console per il debug dettagliato.""";
    
    private static final String SELEZIONA_TEAM_MESSAGE = """
        Seleziona un team dalla lista prima di cliccare \'Gestisci Team\'.
        Se non vedi team nella lista, prova a:
        1. Registrarti a un hackathon come partecipante
        2. Creare un nuovo team
        3. Cliccare il pulsante \'\uD83D\uDD0D Debug\' per diagnosticare""";
    
    @SuppressWarnings({"unused", "squid:S1068"}) // Controller will be used for future functionality
    private final transient Controller controller;
    private final MainFrame mainFrame;
    private javax.swing.Timer autoRefreshTimer;
    // Components
    private DefaultListModel<Team> teamListModel;
    private JList<Team> teamList;
    private JButton creaTeamButton;
    private JButton gestisciTeamButton;
    private JButton richiesteJoinButton;
    private JButton debugButton;
    private JButton refreshButton;
    /**
     * Costruttore che inizializza il pannello team
     *
     * @param controller il controller dell'applicazione
     * @param mainFrame  il frame principale
     */
    public TeamPanel(Controller controller, MainFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        // Auto refresh periodico per mantenere aggiornata la lista team/partecipanti
        autoRefreshTimer = new javax.swing.Timer(10_000, e -> safeAutoRefresh());
        autoRefreshTimer.setRepeats(true);
        autoRefreshTimer.start();
    }
    /**
     * Inizializza i componenti del pannello
     */
    private void initializeComponents() {
        // List model for teams
        teamListModel = new DefaultListModel<>();
        teamList = new JList<>(teamListModel);
        teamList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Buttons
        creaTeamButton = new JButton(CREA_NUOVO_TEAM);
        gestisciTeamButton = new JButton("Gestisci Team");
        richiesteJoinButton = new JButton("Richieste di Join");
        // Debug button (temporaneo)
        debugButton = new JButton("🔍 Debug");
        debugButton.addActionListener(e -> {
            controller.debugHackathonDisponibili();
            JOptionPane.showMessageDialog(this, "Controlla la console per i dettagli di debug", "Debug Info", JOptionPane.INFORMATION_MESSAGE);
        });
        // Refresh button
        refreshButton = new JButton(AGGIORNA_BTN);
        refreshButton.addActionListener(e -> {
            refreshData();
            JOptionPane.showMessageDialog(this, "Dati aggiornati con successo!", "Aggiornamento Completato", JOptionPane.INFORMATION_MESSAGE);
        });
        // Initially disable buttons that require selection
        gestisciTeamButton.setEnabled(false);
        richiesteJoinButton.setEnabled(false);
    }
    /**
     * Configura il layout del pannello
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        // Title
        JLabel titleLabel = new JLabel("Gestione Team", CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        // List panel
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Team"));
        JScrollPane scrollPane = new JScrollPane(teamList);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        listPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(listPanel, BorderLayout.CENTER);
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Azioni"));
        buttonsPanel.add(creaTeamButton);
        buttonsPanel.add(gestisciTeamButton);
        buttonsPanel.add(richiesteJoinButton);
        buttonsPanel.add(debugButton);
        buttonsPanel.add(refreshButton);
        contentPanel.add(buttonsPanel, BorderLayout.EAST);
        add(contentPanel, BorderLayout.CENTER);
    }
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        // Create team button
        creaTeamButton.addActionListener(e -> showCreaTeamDialog());
        // List selection listener
        teamList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Evita eventi multipli durante la selezione
                Team selectedTeam = teamList.getSelectedValue();
                boolean hasValidSelection = selectedTeam != null && selectedTeam.getId() != -1;
                gestisciTeamButton.setEnabled(hasValidSelection);
                richiesteJoinButton.setEnabled(hasValidSelection);
            }
        });
        // Action buttons
        gestisciTeamButton.addActionListener(e -> handleGestisciTeam());
        richiesteJoinButton.addActionListener(e -> handleRichiesteJoin());
        // Debug button per diagnosticare problemi
        debugButton.addActionListener(e -> {
            // Debug utente corrente
            // Forza abilitazione pulsante se ci sono team
            if (teamListModel.size() > 0) {
                Team firstTeam = teamListModel.getElementAt(0);
                if (firstTeam != null && firstTeam.getId() != -1) {
                    gestisciTeamButton.setEnabled(true);
                    teamList.setSelectedIndex(0);
                }
            }
            // Esegui debug del controller
            controller.debugHackathonDisponibili();
            // Mostra messaggio con opzioni
            int option = JOptionPane.showConfirmDialog(this, DIAGNOSTICA_MESSAGE,
                "Diagnostica Completata", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                refreshData();
            }
        });
    }
    /**
     * Aggiorna i dati del pannello
     */
    public void refreshData() {
        // Debug refresh data
        teamListModel.clear();
        try {
            // Carica i team dell'utente corrente
            java.util.List<Team> teams = controller.getTeamUtente();
            if (teams.isEmpty()) {
                Team placeholderTeam = new Team("Nessun team trovato - Crea un nuovo team", -1, -1, 0);
                teamListModel.addElement(placeholderTeam);
            } else {
                for (Team team : teams) {
                    teamListModel.addElement(team);
                }
            }
            // Forza il refresh della lista
            teamList.revalidate();
            teamList.repaint();
            // Reset selezione e stato pulsanti
            teamList.clearSelection();
            gestisciTeamButton.setEnabled(false);
            richiesteJoinButton.setEnabled(false);
            // Verifica se ci sono elementi selezionabili
            if (teamListModel.size() > 0) {
                Team firstTeam = teamListModel.getElementAt(0);
                // Se c'è almeno un team valido, selezionalo automaticamente per facilitare il testing
                if (firstTeam.getId() != -1) {
                    teamList.setSelectedIndex(0);
                }
            } else {
                // No action needed when no valid teams are available
            }
        } catch (Exception e) {
            Team errorTeam = new Team("Errore: " + e.getMessage(), -1, -1, 0);
            teamListModel.addElement(errorTeam);
        }
    }

    private void safeAutoRefresh() {
        try {
            if (isShowing()) {
                refreshData();
            }
        } catch (Exception ignored) { }
    }
    /**
     * Mostra il dialog per creare un nuovo team
     */
    @SuppressWarnings("java:S3776") // Cognitive Complexity acknowledged due to UI workflow
    private void showCreaTeamDialog() {
        JDialog dialog = new JDialog(mainFrame, CREA_NUOVO_TEAM, true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setResizable(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Header
        JLabel headerLabel = new JLabel(CREA_NUOVO_TEAM, CENTER);
        headerLabel.setFont(new Font(FONT_SEGOE_UI, Font.BOLD, 16));
        headerLabel.setForeground(new Color(52, 152, 219));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        dialog.add(headerLabel, gbc);
        // Form fields
        JTextField nomeTeamField = new JTextField(20);
        JSpinner dimensioneSpinner = new JSpinner(new SpinnerNumberModel(3, 2, 10, 1));
        JComboBox<String> hackathonComboBox = new JComboBox<>();
        // Carica gli hackathon disponibili
        loadHackathons(hackathonComboBox);
        // Add components
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Evento:"), gbc);
        gbc.gridx = 1;
        dialog.add(hackathonComboBox, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Nome Team:"), gbc);
        gbc.gridx = 1;
        dialog.add(nomeTeamField, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Dimensione Massima:"), gbc);
        gbc.gridx = 1;
        dialog.add(dimensioneSpinner, gbc);
        // Status label
        JLabel statusLabel = new JLabel("", CENTER);
        statusLabel.setForeground(new Color(231, 76, 60));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        dialog.add(statusLabel, gbc);
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton confirmButton = new JButton(CREA_BTN);
        JButton cancelButton = new JButton(ANNULLA_BTN);
        // Styling buttons
        confirmButton.setBackground(new Color(46, 204, 113));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        confirmButton.addActionListener(e -> {
            String selectedHackathon = (String) hackathonComboBox.getSelectedItem();
            String nomeTeam = nomeTeamField.getText().trim();
            int dimensioneMassima = (Integer) dimensioneSpinner.getValue();
            if (selectedHackathon == null || selectedHackathon.equals("Nessun evento disponibile") || 
                selectedHackathon.equals("Nessun evento disponibile - Registrati come partecipante")) {
                statusLabel.setText("❌ Seleziona un evento disponibile");
                statusLabel.setForeground(new Color(231, 76, 60));
                return;
            }
            if (nomeTeam.isEmpty()) {
                statusLabel.setText("❌ Il nome del team è obbligatorio");
                statusLabel.setForeground(new Color(231, 76, 60));
                return;
            }
            // Estrai l'ID dell'hackathon dal testo (formato: "ID: Nome Evento")
            int hackathonId = extractHackathonId(selectedHackathon);
            if (hackathonId == -1) {
                statusLabel.setText("\u274C Errore nell'identificazione dell'evento");
                statusLabel.setForeground(new Color(231, 76, 60));
                return;
            }
            // Verifica che l'utente non sia già in un team per questo hackathon
            try {
                var teamUtente = controller.getTeamUtente();
                for (var team : teamUtente) {
                    if (team.getHackathonId() == hackathonId) {
                        statusLabel.setText("❌ Sei già membro di un team per questo hackathon");
                        statusLabel.setForeground(new Color(231, 76, 60));
                        return;
                    }
                }
            } catch (Exception ex) {
                // Se c'è un errore nel controllo, continua comunque
                // Log dell'errore per debug
                logger.log(Level.WARNING, "Errore durante il controllo team esistenti", ex);
            }
            // Disabilita il pulsante durante l'operazione
            confirmButton.setEnabled(false);
            confirmButton.setText(CREAZIONE_IN_CORSO);
            // Esegui la creazione del team
            try {
                int teamId = controller.creaTeam(hackathonId, nomeTeam, dimensioneMassima);
                if (teamId > 0) {
                    statusLabel.setText("✅ Team creato con successo! ID: " + teamId);
                    statusLabel.setForeground(new Color(46, 204, 113));
                    // Chiudi il dialog dopo un breve delay
                    Timer timer = new Timer(1500, evt -> {
                        dialog.dispose();
                        refreshData();
                        mainFrame.showToast("Team '" + nomeTeam + "' creato con successo! \uD83C\uDF89", "success");
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    statusLabel.setText("\u274C Errore durante la creazione del team");
                    statusLabel.setForeground(new Color(231, 76, 60));
                    confirmButton.setEnabled(true);
                    confirmButton.setText(CREA_TEAM_TEXT);
                }
            } catch (IllegalArgumentException ex) {
                statusLabel.setText("❌ " + ex.getMessage());
                statusLabel.setForeground(new Color(231, 76, 60));
                confirmButton.setEnabled(true);
                confirmButton.setText(CREA_TEAM_TEXT);
            } catch (database.DataAccessException ex) {
                statusLabel.setText("❌ " + ERRORE_DATABASE + ex.getMessage());
                statusLabel.setForeground(new Color(231, 76, 60));
                confirmButton.setEnabled(true);
                confirmButton.setText(CREA_TEAM_TEXT);
            } catch (Exception ex) {
                statusLabel.setText("❌ " + ERRORE_GENERICO + ex.getMessage());
                statusLabel.setForeground(new Color(231, 76, 60));
                confirmButton.setEnabled(true);
                confirmButton.setText(CREA_TEAM_TEXT);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        dialog.setVisible(true);
    }
    /**
     * Carica gli hackathon disponibili nel combo box
     * Mostra solo gli eventi per cui l'utente è registrato come partecipante e la registrazione è confermata
     */
    private void loadHackathons(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        try {
            // Ottieni solo gli hackathon disponibili per la creazione del team
            var hackathons = controller.getHackathonDisponibiliPerTeam();
            if (hackathons.isEmpty()) {
                comboBox.addItem("Nessun evento disponibile - Registrati come partecipante");
            } else {
                for (var hackathon : hackathons) {
                    String item = hackathon.getId() + ": " + hackathon.getNome();
                    comboBox.addItem(item);
                }
            }
        } catch (Exception e) {
            comboBox.addItem("Errore nel caricamento eventi");
        }
    }
    /**
     * Estrae l'ID dell'hackathon dal testo del combo box
     */
    private int extractHackathonId(String text) {
        try {
            if (text.contains(":")) {
                String idPart = text.substring(0, text.indexOf(":"));
                return Integer.parseInt(idPart.trim());
            }
        } catch (NumberFormatException e) {
            // Ignora errori di parsing
        }
        return -1;
    }
    /**
     * Gestisce la gestione del team
     */
    private void handleGestisciTeam() {
        // Verifica stato generale
        Team selectedTeam = teamList.getSelectedValue();
        // Debug team selezionato
        if (selectedTeam == null) {
            // Se ci sono team nella lista ma nessuno è selezionato, seleziona il primo
            if (teamListModel.size() > 0) {
                Team firstTeam = teamListModel.getElementAt(0);
                if (firstTeam != null && firstTeam.getId() != -1) {
                    teamList.setSelectedIndex(0);
                    selectedTeam = firstTeam;
                }
            }
            if (selectedTeam == null) {
                JOptionPane.showMessageDialog(this,
                    SELEZIONA_TEAM_MESSAGE,
                    "Nessun Team Selezionato",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        // Verifica che il team non sia un placeholder
        if (selectedTeam.getId() == -1) {
            JOptionPane.showMessageDialog(this,
                "Questo non è un team reale. Crea un nuovo team o seleziona un team esistente.",
                "Team Non Valido",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Verifica che l'utente sia membro del team
        try {
            java.util.List<Integer> membri = controller.getTeamMembers(selectedTeam.getId());
            boolean isMember = membri.contains(controller.getCurrentUser().getId());
            if (!isMember) {
                JOptionPane.showMessageDialog(this,
                    "Non sei autorizzato a gestire questo team.",
                    "Accesso Negato",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Errore durante la verifica delle autorizzazioni: " + e.getMessage(),
                ERRORE_TITLE,
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            showGestisciTeamDialog(selectedTeam);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Errore durante l'apertura del dialog: " + e.getMessage(),
                ERRORE_TITLE,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Mostra il dialog per gestire un team
     */
    @SuppressWarnings("java:S3776") // Cognitive Complexity acknowledged due to rich UI flow
    private void showGestisciTeamDialog(Team team) {
        JDialog dialog = new JDialog(mainFrame, "Gestione Team: " + team.getNome(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setResizable(false);
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(52, 152, 219));
        JLabel headerLabel = new JLabel("👥 Gestione Team: " + team.getNome());
        headerLabel.setFont(new Font(FONT_SEGOE_UI, Font.BOLD, 16));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // Team info panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informazioni Team"));
        infoPanel.add(new JLabel("Nome:"));
        infoPanel.add(new JLabel(team.getNome()));
        infoPanel.add(new JLabel("ID:"));
        infoPanel.add(new JLabel(String.valueOf(team.getId())));
        infoPanel.add(new JLabel("Dimensione Massima:"));
        infoPanel.add(new JLabel(team.getDimensioneMassima() + " membri"));
        infoPanel.add(new JLabel("Capo Team:"));
        infoPanel.add(new JLabel("ID: " + team.getCapoTeamId()));
        // Members panel
        JPanel membersPanel = new JPanel(new BorderLayout());
        membersPanel.setBorder(BorderFactory.createTitledBorder("Membri del Team"));
        DefaultListModel<String> membersListModel = new DefaultListModel<>();
        JList<String> membersList = new JList<>(membersListModel);
        JScrollPane membersScrollPane = new JScrollPane(membersList);
        membersScrollPane.setPreferredSize(new Dimension(300, 200));
        // Load team members
        try {
            List<Integer> memberIds = controller.getTeamMembers(team.getId());
            for (Integer memberId : memberIds) {
                Utente member = controller.getUtenteById(memberId);
                if (member != null) {
                    String memberInfo = member.getNome() + " " + member.getCognome() + 
                                      (memberId == team.getCapoTeamId() ? " (Capo Team)" : "");
                    membersListModel.addElement(memberInfo);
                }
            }
        } catch (Exception e) {
            membersListModel.addElement("Errore nel caricamento membri: " + e.getMessage());
        }
        membersPanel.add(membersScrollPane, BorderLayout.CENTER);
        // Actions panel with enhanced layout
        JPanel actionsPanel = new JPanel(new GridLayout(3, 1, 8, 8));
        actionsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "🔧 Azioni Team",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font(FONT_SEGOE_UI, Font.BOLD, 12),
            new Color(52, 152, 219)
        ));
        actionsPanel.setBackground(new Color(248, 249, 250)); // Light gray background
        ((javax.swing.border.TitledBorder) actionsPanel.getBorder()).setTitleColor(new Color(52, 152, 219));
        JButton inviteButton = new JButton("📨 Invita Membro");
        JButton removeButton = new JButton("❌ Rimuovi Membro");
        JButton leaveButton = new JButton("🚪 Lascia Team");
        // Style buttons with enhanced visibility
        Font buttonFont = new Font(FONT_SEGOE_UI, Font.BOLD, 14);
        Dimension buttonSize = new Dimension(180, 45);
        // Invite button - Green theme
        inviteButton.setFont(buttonFont);
        inviteButton.setBackground(new Color(46, 204, 113));
        inviteButton.setForeground(Color.WHITE);
        inviteButton.setFocusPainted(false);
        inviteButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(34, 153, 84), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        inviteButton.setMinimumSize(buttonSize);
        inviteButton.setPreferredSize(buttonSize);
        inviteButton.setMaximumSize(buttonSize);
        // Remove button - Red theme
        removeButton.setFont(buttonFont);
        removeButton.setBackground(new Color(231, 76, 60));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);
        removeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(192, 57, 43), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        removeButton.setMinimumSize(buttonSize);
        removeButton.setPreferredSize(buttonSize);
        removeButton.setMaximumSize(buttonSize);
        // Leave button - Purple theme
        leaveButton.setFont(buttonFont);
        leaveButton.setBackground(new Color(155, 89, 182));
        leaveButton.setForeground(Color.WHITE);
        leaveButton.setFocusPainted(false);
        leaveButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(142, 68, 173), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        leaveButton.setMinimumSize(buttonSize);
        leaveButton.setPreferredSize(buttonSize);
        leaveButton.setMaximumSize(buttonSize);
        // Add hover effects
        addHoverEffect(inviteButton, new Color(34, 153, 84), new Color(46, 204, 113));
        addHoverEffect(removeButton, new Color(192, 57, 43), new Color(231, 76, 60));
        addHoverEffect(leaveButton, new Color(142, 68, 173), new Color(155, 89, 182));
        // Add action listeners
        inviteButton.addActionListener(e -> showInviteMemberDialog(dialog, team));
        removeButton.addActionListener(e -> showRemoveMemberDialog(dialog, team));
        leaveButton.addActionListener(e -> {
            // Ottieni informazioni sui membri per un messaggio più informativo
            try {
                List<Integer> membri = controller.getTeamMembers(team.getId());
                boolean isCapoTeam = team.getCapoTeamId() == controller.getCurrentUser().getId();
                int numMembri = membri.size();
                String message;
                String title;
                if (isCapoTeam) {
                    if (numMembri == 1) {
                        message = "Sei l'ultimo membro del team. Lasciando il team, questo verrà ELIMINATO permanentemente.\n\nSei sicuro di voler procedere?";
                        title = "⚠️ Eliminazione Team";
                    } else {
                        message = "Sei il capo del team con " + (numMembri - 1) + " altri membri.\n" +
                                 "Lasciando il team, un altro membro verrà promosso a capo team.\n\nSei sicuro di voler procedere?";
                        title = "🔄 Promozione Nuovo Capo";
                    }
                } else {
                    message = "Sei sicuro di voler lasciare il team '" + team.getNome() + "'?\n" +
                             "Questa azione non può essere annullata.";
                    title = "🚪 Lascia Team";
                }
                int result = JOptionPane.showConfirmDialog(dialog, message, title, JOptionPane.YES_NO_OPTION,
                    isCapoTeam ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                handleLeaveTeam(team, dialog, isCapoTeam, numMembri);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Errore nel recupero informazioni team: " + ex.getMessage(), ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        });
        actionsPanel.add(inviteButton);
        actionsPanel.add(removeButton);
        actionsPanel.add(leaveButton);
        // Add panels to content
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(infoPanel, BorderLayout.NORTH);
        topPanel.add(membersPanel, BorderLayout.CENTER);
        contentPanel.add(topPanel, BorderLayout.CENTER);
        contentPanel.add(actionsPanel, BorderLayout.EAST);
        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Chiudi");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        // Add all panels to dialog
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    /**
     * Gestisce l'uscita dal team
     */
    private void handleLeaveTeam(Team team, JDialog dialog, boolean isCapoTeam, int numMembri) {
        try {
            boolean success = controller.leaveTeam(team.getId());
            if (success) {
                String successMessage;
                if (isCapoTeam && numMembri == 1) {
                    successMessage = "Hai lasciato il team e questo è stato eliminato.";
                } else if (isCapoTeam) {
                    successMessage = "Hai lasciato il team. Un nuovo capo è stato promosso.";
                } else {
                    successMessage = "Hai lasciato il team con successo.";
                }
                JOptionPane.showMessageDialog(dialog, successMessage, "Operazione Completata", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
                mainFrame.showToast("Hai lasciato il team '" + team.getNome() + "'", "success");
            } else {
                JOptionPane.showMessageDialog(dialog, "Errore durante l'uscita dal team", ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Errore: " + ex.getMessage(), ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Mostra il dialog per invitare un nuovo membro al team
     */
    @SuppressWarnings("java:S3776") // Cognitive Complexity acknowledged due to rich UI flow
    private void showInviteMemberDialog(JDialog parentDialog, Team team) {
        JDialog inviteDialog = new JDialog(parentDialog, "Invita Nuovo Membro", true);
        inviteDialog.setLayout(new BorderLayout());
        inviteDialog.setSize(500, 400);
        inviteDialog.setLocationRelativeTo(parentDialog);
        inviteDialog.setResizable(false);
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(46, 204, 113));
        JLabel headerLabel = new JLabel("📨 Invita Nuovo Membro al Team: " + team.getNome());
        headerLabel.setFont(new Font(FONT_SEGOE_UI, Font.BOLD, 14));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // Lista utenti disponibili
        DefaultListModel<Utente> availableUsersModel = new DefaultListModel<>();
        JList<Utente> availableUsersList = new JList<>(availableUsersModel);
        availableUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableUsersList.setCellRenderer(new UserListCellRenderer());
        JScrollPane usersScrollPane = new JScrollPane(availableUsersList);
        usersScrollPane.setPreferredSize(new Dimension(400, 200));
        usersScrollPane.setBorder(BorderFactory.createTitledBorder("Partecipanti Disponibili"));
        // Carica utenti disponibili
        try {
            List<Utente> availableUsers = controller.getUtentiDisponibiliPerInvito(team.getHackathonId(), team.getId());
            if (availableUsers.isEmpty()) {
                availableUsersModel.addElement(new Utente("Nessun partecipante disponibile", "") {
                    @Override
                    public String toString() {
                        return NO_PARTECIPANTI_DISPONIBILI;
                    }
                });
                // Mostra messaggio informativo all'utente
                JOptionPane.showMessageDialog(inviteDialog,
                    NO_PARTECIPANTI_DISPONIBILI_DIALOG,
                    "Nessun Partecipante Disponibile",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Utente user : availableUsers) {
                    availableUsersModel.addElement(user);
                }
            }
        } catch (Exception e) {
            availableUsersModel.addElement(new Utente("Errore nel caricamento", "") {
                @Override
                public String toString() {
                    return "Errore nel caricamento utenti disponibili";
                }
            });
        }
        // Buttons with enhanced styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton refreshUsersButton = new JButton("🔄 Aggiorna");
        JButton inviteButton = new JButton("📨 Invia Invito");
        JButton cancelButton = new JButton("❌ Annulla");
        // Styling buttons with better visibility
        Font dialogButtonFont = new Font(FONT_SEGOE_UI, Font.BOLD, 12);
        Dimension dialogButtonSize = new Dimension(120, 35);
        refreshUsersButton.setFont(dialogButtonFont);
        refreshUsersButton.setBackground(new Color(52, 152, 219));
        refreshUsersButton.setForeground(Color.WHITE);
        refreshUsersButton.setFocusPainted(false);
        refreshUsersButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        refreshUsersButton.setPreferredSize(dialogButtonSize);
        refreshUsersButton.setMinimumSize(dialogButtonSize);
        inviteButton.setFont(dialogButtonFont);
        inviteButton.setBackground(new Color(46, 204, 113));
        inviteButton.setForeground(Color.WHITE);
        inviteButton.setFocusPainted(false);
        inviteButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(34, 153, 84), 2),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        inviteButton.setPreferredSize(dialogButtonSize);
        inviteButton.setMinimumSize(dialogButtonSize);
        cancelButton.setFont(dialogButtonFont);
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(192, 57, 43), 2),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        cancelButton.setPreferredSize(dialogButtonSize);
        cancelButton.setMinimumSize(dialogButtonSize);
        // Add hover effects to dialog buttons
        addHoverEffect(refreshUsersButton, new Color(41, 128, 185), new Color(52, 152, 219));
        addHoverEffect(inviteButton, new Color(34, 153, 84), new Color(46, 204, 113));
        addHoverEffect(cancelButton, new Color(192, 57, 43), new Color(231, 76, 60));
        // Action listeners
        refreshUsersButton.addActionListener(e -> {
            // Ricarica la lista degli utenti disponibili
            availableUsersModel.clear();
            try {
                List<Utente> availableUsers = controller.getUtentiDisponibiliPerInvito(team.getHackathonId(), team.getId());
                if (availableUsers.isEmpty()) {
                    availableUsersModel.addElement(new Utente("Nessun partecipante disponibile", "") {
                        @Override
                        public String toString() {
                            return NO_PARTECIPANTI_DISPONIBILI_SIMPLE;
                        }
                    });
                    JOptionPane.showMessageDialog(inviteDialog,
                        "Ancora nessun partecipante disponibile.\nControlla la console per debug dettagliato.",
                        LISTA_AGGIORNATA,
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    for (Utente user : availableUsers) {
                        availableUsersModel.addElement(user);
                    }
                    JOptionPane.showMessageDialog(inviteDialog,
                        "Lista aggiornata! Trovati " + availableUsers.size() + " partecipanti disponibili.",
                        LISTA_AGGIORNATA,
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(inviteDialog,
                    "Errore durante l'aggiornamento: " + ex.getMessage(),
                    "Errore Refresh",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        // Message area for motivational text
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createTitledBorder("Messaggio motivazionale (facoltativo)"));
        JTextArea messageArea = new JTextArea(4, 40);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScroll = new JScrollPane(messageArea);
        messagePanel.add(messageScroll, BorderLayout.CENTER);

        inviteButton.addActionListener(e -> {
            Utente selectedUser = availableUsersList.getSelectedValue();
            if (selectedUser == null || selectedUser.getId() <= 0) {
                JOptionPane.showMessageDialog(inviteDialog,
                    "Seleziona un partecipante da invitare",
                    "Nessun Utente Selezionato",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            String msg = messageArea.getText();
            if (msg != null && msg.length() > 500) {
                JOptionPane.showMessageDialog(inviteDialog,
                    "Il messaggio motivazionale è troppo lungo (max 500 caratteri)",
                    "Messaggio troppo lungo",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Invia come richiesta di join con messaggio motivazionale
            boolean success = controller.inviaRichiestaJoin(team.getId(), msg == null ? "" : msg.trim());
            if (success) {
                JOptionPane.showMessageDialog(inviteDialog,
                    "✅ Richiesta inviata con successo. Il capo team potrà accettarla o rifiutarla.",
                    "Richiesta Inviata",
                    JOptionPane.INFORMATION_MESSAGE);
                inviteDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(inviteDialog,
                    "\u274C Errore durante l'invio della richiesta",
                    "Errore Richiesta",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> inviteDialog.dispose());
        buttonPanel.add(refreshUsersButton);
        buttonPanel.add(inviteButton);
        buttonPanel.add(cancelButton);
        // Add components
        JPanel centerStack = new JPanel(new BorderLayout());
        centerStack.add(usersScrollPane, BorderLayout.CENTER);
        centerStack.add(messagePanel, BorderLayout.SOUTH);
        contentPanel.add(centerStack, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        inviteDialog.add(headerPanel, BorderLayout.NORTH);
        inviteDialog.add(contentPanel, BorderLayout.CENTER);
        inviteDialog.setVisible(true);
    }
    /**
     * Mostra il dialog per rimuovere un membro dal team
     */
    @SuppressWarnings("java:S3776") // Cognitive Complexity acknowledged due to rich UI flow
    private void showRemoveMemberDialog(JDialog parentDialog, Team team) {
        JDialog removeDialog = new JDialog(parentDialog, "Rimuovi Membro dal Team", true);
        removeDialog.setLayout(new BorderLayout());
        removeDialog.setSize(500, 400);
        removeDialog.setLocationRelativeTo(parentDialog);
        removeDialog.setResizable(false);
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(231, 76, 60));
        JLabel headerLabel = new JLabel("❌ Rimuovi Membro dal Team: " + team.getNome());
        headerLabel.setFont(new Font(FONT_SEGOE_UI, Font.BOLD, 14));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // Lista membri del team (escluso il capo team)
        DefaultListModel<Utente> teamMembersModel = new DefaultListModel<>();
        JList<Utente> teamMembersList = new JList<>(teamMembersModel);
        teamMembersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamMembersList.setCellRenderer(new UserListCellRenderer());
        JScrollPane membersScrollPane = new JScrollPane(teamMembersList);
        membersScrollPane.setPreferredSize(new Dimension(400, 200));
        membersScrollPane.setBorder(BorderFactory.createTitledBorder("Membri del Team (escluso capo team)"));
        // Carica membri del team
        try {
            List<Integer> memberIds = controller.getTeamMembers(team.getId());
            if (memberIds.isEmpty()) {
                teamMembersModel.addElement(new Utente("Nessun membro nel team", "") {
                    @Override
                    public String toString() {
                        return "Nessun membro nel team";
                    }
                });
            } else {
                for (Integer memberId : memberIds) {
                    Utente member = controller.getUtenteById(memberId);
                    if (member != null && member.getId() != team.getCapoTeamId()) {
                        // Escludi il capo team dalla lista di rimozione
                        teamMembersModel.addElement(member);
                    }
                }
                if (teamMembersModel.isEmpty()) {
                    teamMembersModel.addElement(new Utente("Nessun membro rimovibile", "") {
                        @Override
                        public String toString() {
                            return "Solo il capo team è membro del team";
                        }
                    });
                }
            }
        } catch (Exception e) {
            teamMembersModel.addElement(new Utente("Errore nel caricamento", "") {
                @Override
                public String toString() {
                    return "Errore nel caricamento membri del team";
                }
            });
        }
        // Buttons with enhanced styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        JButton removeButton = new JButton("❌ Rimuovi Membro");
        JButton cancelButton = new JButton("❌ Annulla");
        // Styling buttons with better visibility
        Font dialogButtonFont = new Font(FONT_SEGOE_UI, Font.BOLD, 13);
        Dimension dialogButtonSize = new Dimension(140, 40);
        removeButton.setFont(dialogButtonFont);
        removeButton.setBackground(new Color(231, 76, 60));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);
        removeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(192, 57, 43), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        removeButton.setPreferredSize(dialogButtonSize);
        removeButton.setMinimumSize(dialogButtonSize);
        cancelButton.setFont(dialogButtonFont);
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(127, 140, 141), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        cancelButton.setPreferredSize(dialogButtonSize);
        cancelButton.setMinimumSize(dialogButtonSize);
        // Add hover effects to dialog buttons
        addHoverEffect(removeButton, new Color(192, 57, 43), new Color(231, 76, 60));
        addHoverEffect(cancelButton, new Color(127, 140, 141), new Color(149, 165, 166));
        // Action listeners
        removeButton.addActionListener(e -> {
            Utente selectedMember = teamMembersList.getSelectedValue();
            if (selectedMember != null && selectedMember.getId() > 0) {
                int confirm = JOptionPane.showConfirmDialog(removeDialog,
                    "Sei sicuro di voler rimuovere " + selectedMember.getNome() + " " + selectedMember.getCognome() + " dal team?",
                    "Conferma Rimozione",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = controller.rimuoviMembroDalTeam(team.getId(), selectedMember.getId());
                    if (success) {
                        JOptionPane.showMessageDialog(removeDialog,
                            "✅ Membro rimosso con successo dal team",
                            "Membro Rimosso",
                            JOptionPane.INFORMATION_MESSAGE);
                        // Aggiorna la lista membri nel dialog principale
                        refreshMembersList(parentDialog);
                        removeDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(removeDialog,
                            "❌ Errore durante la rimozione del membro",
                            "Errore Rimozione",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(removeDialog,
                    "Seleziona un membro da rimuovere",
                    "Nessun Membro Selezionato",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> removeDialog.dispose());
        buttonPanel.add(removeButton);
        buttonPanel.add(cancelButton);
        // Add components
        contentPanel.add(membersScrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        removeDialog.add(headerPanel, BorderLayout.NORTH);
        removeDialog.add(contentPanel, BorderLayout.CENTER);
        removeDialog.setVisible(true);
    }
    /**
     * Aggiorna la lista membri nel dialog principale
     */
    private void refreshMembersList(JDialog parentDialog) {
        try {
            // Trova il pannello membri nel dialog principale e aggiornalo
            // Questa è una implementazione semplificata - in una versione più completa
            // si potrebbe passare un riferimento al pannello membri
            // Per ora, ricarichiamo semplicemente i dati del pannello team
            refreshData();
            JOptionPane.showMessageDialog(parentDialog,
                "Lista membri aggiornata. Ricarica il pannello per vedere i cambiamenti.",
                LISTA_AGGIORNATA,
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            // Log dell'errore per debug
            logger.log(Level.WARNING, "Errore durante l'aggiornamento lista membri", e);
        }
    }
    /**
     * Renderer personalizzato per la lista utenti
     */
    private static class UserListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Utente user && user.getId() > 0) {
                setText(user.getNome() + " " + user.getCognome() + " (" + user.getLogin() + ")");
            }
            return this;
        }
    }
    /**
     * Aggiunge effetto hover a un pulsante
     *
     * @param button il pulsante a cui aggiungere l'effetto
     * @param hoverColor il colore durante l'hover
     * @param defaultColor il colore di default
     */
    private void addHoverEffect(JButton button, Color hoverColor, Color defaultColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(hoverColor.darker(), 3),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(defaultColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(defaultColor.darker(), 2),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });
    }
    /**
     * Gestisce le richieste di join
     */
    private void handleRichiesteJoin() {
        Team selectedTeam = teamList.getSelectedValue();
        if (selectedTeam != null) {
            // Verifica che l'utente corrente sia il capo team
            if (!controller.isCapoTeam(selectedTeam.getId(), controller.getCurrentUser().getId())) {
                JOptionPane.showMessageDialog(this,
                    "Solo il capo team può gestire le richieste di join.",
                    "Accesso Negato",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Recupera le richieste di join in attesa
            try {
                List<RichiestaJoin> richieste = controller.getRichiesteJoin(selectedTeam.getId());
                if (richieste.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Non ci sono richieste di join in attesa per questo team.",
                        "Nessuna Richiesta",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                // Mostra il dialog per gestire le richieste
                showRichiesteJoinDialog(selectedTeam, richieste);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Errore durante il recupero delle richieste: " + e.getMessage(),
                    ERRORE_SISTEMA,
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Seleziona un team per vedere le richieste di join.",
                "Nessun Team Selezionato",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    /**
     * Mostra il dialog per gestire le richieste di join
     */
    private void showRichiesteJoinDialog(Team team, List<RichiestaJoin> richieste) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                                   "Richieste di Join - " + team.getNome(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        // Panel principale
        JPanel mainPanel = new JPanel(new BorderLayout());
        // Titolo
        JLabel titleLabel = new JLabel("Richieste di partecipazione al team", SwingConstants.CENTER);
        titleLabel.setFont(new Font(FONT_SEGOE_UI, Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        // Lista delle richieste
        DefaultListModel<RichiestaJoin> richiesteModel = new DefaultListModel<>();
        for (RichiestaJoin richiesta : richieste) {
            richiesteModel.addElement(richiesta);
        }
        JList<RichiestaJoin> richiesteList = new JList<>(richiesteModel);
        richiesteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        richiesteList.setCellRenderer(new RichiestaJoinListCellRenderer());
        JScrollPane scrollPane = new JScrollPane(richiesteList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Richieste in Attesa"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        // Panel dei pulsanti
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton accettaButton = new JButton("✅ Accetta");
        JButton rifiutaButton = new JButton("❌ Rifiuta");
        JButton aggiornaButton = new JButton("🔄 Aggiorna");
        JButton chiudiButton = new JButton("❌ Chiudi");
        // Styling dei pulsanti
        Font buttonFont = new Font(FONT_SEGOE_UI, Font.BOLD, 12);
        Dimension buttonSize = new Dimension(120, 35);
        accettaButton.setFont(buttonFont);
        accettaButton.setBackground(new Color(46, 204, 113));
        accettaButton.setForeground(Color.WHITE);
        accettaButton.setFocusPainted(false);
        accettaButton.setPreferredSize(buttonSize);
        rifiutaButton.setFont(buttonFont);
        rifiutaButton.setBackground(new Color(231, 76, 60));
        rifiutaButton.setForeground(Color.WHITE);
        rifiutaButton.setFocusPainted(false);
        rifiutaButton.setPreferredSize(buttonSize);
        aggiornaButton.setFont(buttonFont);
        aggiornaButton.setBackground(new Color(52, 152, 219));
        aggiornaButton.setForeground(Color.WHITE);
        aggiornaButton.setFocusPainted(false);
        aggiornaButton.setPreferredSize(buttonSize);
        chiudiButton.setFont(buttonFont);
        chiudiButton.setBackground(new Color(149, 165, 166));
        chiudiButton.setForeground(Color.WHITE);
        chiudiButton.setFocusPainted(false);
        chiudiButton.setPreferredSize(buttonSize);
        // Hover effects
        addHoverEffect(accettaButton, new Color(34, 153, 84), new Color(46, 204, 113));
        addHoverEffect(rifiutaButton, new Color(192, 57, 43), new Color(231, 76, 60));
        addHoverEffect(aggiornaButton, new Color(41, 128, 185), new Color(52, 152, 219));
        addHoverEffect(chiudiButton, new Color(127, 140, 141), new Color(149, 165, 166));
        // Inizialmente disabilita i pulsanti accetta/rifiuta
        accettaButton.setEnabled(false);
        rifiutaButton.setEnabled(false);
        // Listener per la selezione della lista
        richiesteList.addListSelectionListener(e -> {
            boolean hasSelection = !richiesteList.isSelectionEmpty();
            accettaButton.setEnabled(hasSelection);
            rifiutaButton.setEnabled(hasSelection);
        });
        // Action listeners
        accettaButton.addActionListener(e -> {
            RichiestaJoin richiestaSelezionata = richiesteList.getSelectedValue();
            if (richiestaSelezionata != null) {
                handleAccettaRichiesta(richiestaSelezionata, richiesteModel);
            }
        });
        rifiutaButton.addActionListener(e -> {
            RichiestaJoin richiestaSelezionata = richiesteList.getSelectedValue();
            if (richiestaSelezionata != null) {
                handleRifiutaRichiesta(richiestaSelezionata, richiesteModel);
            }
        });
        aggiornaButton.addActionListener(e -> {
            // Ricarica le richieste
            List<RichiestaJoin> nuoveRichieste = controller.getRichiesteJoin(team.getId());
            richiesteModel.clear();
            for (RichiestaJoin richiesta : nuoveRichieste) {
                richiesteModel.addElement(richiesta);
            }
            JOptionPane.showMessageDialog(dialog,
                "Lista aggiornata! Trovate " + nuoveRichieste.size() + " richieste.",
                LISTA_AGGIORNATA,
                JOptionPane.INFORMATION_MESSAGE);
        });
        chiudiButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(accettaButton);
        buttonPanel.add(rifiutaButton);
        buttonPanel.add(aggiornaButton);
        buttonPanel.add(chiudiButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    /**
     * Gestisce l'accettazione di una richiesta
     */
    private void handleAccettaRichiesta(RichiestaJoin richiesta, DefaultListModel<RichiestaJoin> model) {
        try {
            boolean success = controller.accettaRichiestaJoin(richiesta.getId());
            if (success) {
                // Rimuovi dalla lista
                model.removeElement(richiesta);
                JOptionPane.showMessageDialog(this,
                    "\u2705 Richiesta accettata con successo!\nL'utente è stato aggiunto al team.",
                    "Richiesta Accettata",
                    JOptionPane.INFORMATION_MESSAGE);
                // Aggiorna la vista del team panel
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "\u274C Errore durante l'accettazione della richiesta.",
                    "Errore Accettazione",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Errore durante l'accettazione: " + e.getMessage(),
                ERRORE_SISTEMA,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Gestisce il rifiuto di una richiesta
     */
    private void handleRifiutaRichiesta(RichiestaJoin richiesta, DefaultListModel<RichiestaJoin> model) {
        try {
            boolean success = controller.rifiutaRichiestaJoin(richiesta.getId());
            if (success) {
                // Rimuovi dalla lista
                model.removeElement(richiesta);
                JOptionPane.showMessageDialog(this,
                    "Richiesta rifiutata con successo!",
                    "Richiesta Rifiutata",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Errore durante il rifiuto della richiesta.",
                    "Errore Rifiuto",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Errore durante il rifiuto: " + e.getMessage(),
                ERRORE_SISTEMA,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Renderer personalizzato per la lista delle richieste di join
     */
    private class RichiestaJoinListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof RichiestaJoin richiesta) {
                try {
                    // Recupera le informazioni dell'utente
                    Utente utente = controller.getUtenteById(richiesta.getUtenteId());
                    String nomeUtente = utente != null ? utente.getNome() + " " + utente.getCognome() : "Utente #" + richiesta.getUtenteId();
                    String text = nomeUtente + " (ID: " + richiesta.getUtenteId() + ") - Richiesta del " +
                                              richiesta.getDataRichiesta().toLocalDate().toString();
                    if (richiesta.getMessaggioMotivazionale() != null &&
                        !richiesta.getMessaggioMotivazionale().trim().isEmpty()) {
                        // Tronca il messaggio se troppo lungo
                        String messaggio = richiesta.getMessaggioMotivazionale();
                        if (messaggio.length() > 50) {
                            messaggio = messaggio.substring(0, 47) + "...";
                        }
                        text += "\n💬 \"" + messaggio + "\"";
                    }
                    setText(text);
                    setFont(new Font(FONT_SEGOE_UI, Font.PLAIN, 11));
                    // Imposta l'altezza per supportare testo su più righe
                    setPreferredSize(new Dimension(list.getWidth(), 40));
                } catch (Exception e) {
                    setText("Errore nel caricamento della richiesta #" + richiesta.getId());
                    setForeground(Color.RED);
                    setFont(new Font(FONT_SEGOE_UI, Font.PLAIN, 11));
                }
            }
            return this;
        }
    }
} 
