package gui;
import controller.Controller;
import model.Valutazione;
import model.Team;
import model.Hackathon;
import javax.swing.*;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import static javax.swing.SwingConstants.*;
/**
 * Pannello per la gestione delle valutazioni.
 */
public class ValutazioniPanel extends JPanel {
    private static final String ERRORE_SISTEMA = "Errore Sistema";
    // sonar:ignore=S1068 // Controller will be used for future functionality
    @SuppressWarnings("unused") // Controller will be used for future functionality
    private final transient Controller controller;
    private final MainFrame mainFrame;
    // Components
    private DefaultListModel<Valutazione> valutazioniListModel;
    private JList<Valutazione> valutazioniList;
    private JButton assegnaVotiButton;
    private JButton visualizzaClassificaButton;
    private JButton revisioneProgressiButton;
    private JButton debugButton;
    private JButton refreshButton;
    /**
     * Costruttore che inizializza il pannello valutazioni
     *
     * @param controller il controller dell'applicazione
     * @param mainFrame  il frame principale
     */
    public ValutazioniPanel(Controller controller, MainFrame mainFrame) {
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
        // List model for evaluations
        valutazioniListModel = new DefaultListModel<>();
        valutazioniList = new JList<>(valutazioniListModel);
        valutazioniList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Buttons
        assegnaVotiButton = new JButton("Assegna Voti");
        visualizzaClassificaButton = new JButton("Visualizza Classifica");
        revisioneProgressiButton = new JButton("📋 Revisione Progressi");
        debugButton = new JButton("\uD83D\uDD0D Debug");
        refreshButton = new JButton("\uD83D\uDD04 Aggiorna");
        // Assegna Voti should always be enabled (it's for creating new evaluations)
        assegnaVotiButton.setEnabled(true);
        revisioneProgressiButton.setEnabled(true);
        debugButton.setEnabled(true);
        refreshButton.setEnabled(true);
    }
    /**
     * Configura il layout del pannello
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        // Title
        JLabel titleLabel = new JLabel("Gestione Valutazioni", CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        // List panel
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Valutazioni"));
        JScrollPane scrollPane = new JScrollPane(valutazioniList);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        listPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(listPanel, BorderLayout.CENTER);
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Azioni"));
        buttonsPanel.add(assegnaVotiButton);
        buttonsPanel.add(visualizzaClassificaButton);
        buttonsPanel.add(revisioneProgressiButton);
        buttonsPanel.add(debugButton);
        buttonsPanel.add(refreshButton);
        contentPanel.add(buttonsPanel, BorderLayout.EAST);
        add(contentPanel, BorderLayout.CENTER);
    }
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        // Assign votes button
        assegnaVotiButton.addActionListener(e -> showAssegnaVotiDialog());
        // List selection listener (for future functionality like editing existing evaluations)
        valutazioniList.addListSelectionListener(e -> {
            // Assegna Voti remains always enabled for creating new evaluations
            // Future: could enable/disable based on selection for editing existing evaluations
        });
        // Action buttons
        visualizzaClassificaButton.addActionListener(e -> handleVisualizzaClassifica());
        revisioneProgressiButton.addActionListener(e -> showJudgeProgressReviewDialog());
        debugButton.addActionListener(e -> handleDebug());
        refreshButton.addActionListener(e -> {
            refreshData();
            JOptionPane.showMessageDialog(this, "\u2705 Dati aggiornati con successo!", "Aggiornamento Completato", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    /**
     * Aggiorna i dati del pannello
     */
    public void refreshData() {
        valutazioniListModel.clear();
        try {
            // Carica valutazioni esistenti per questo giudice
            List<Valutazione> valutazioni = controller.getValutazioniHackathon(1); 
            for (Valutazione valutazione : valutazioni) {
                valutazioniListModel.addElement(valutazione);
            }
            if (valutazioni.isEmpty()) {
                // Aggiungi un elemento informativo
                valutazioniListModel.addElement(new Valutazione(-1, -1, -1, 0, "Nessuna valutazione presente - Usa 'Assegna Voti' per iniziare"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                """
                Errore durante il caricamento delle valutazioni: %s

                Controlla la console per i dettagli tecnici.
                """.formatted(e.getMessage()),
                ERRORE_SISTEMA,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Mostra il dialog per assegnare voti
     */
    @SuppressWarnings("java:S3776") // Cognitive complexity acceptable for UI dialog method
    private void showAssegnaVotiDialog() {
        JDialog dialog = new JDialog(mainFrame, "Assegna Voti", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Carica team disponibili per la valutazione
        JComboBox<Team> teamComboBox = new JComboBox<>();
        teamComboBox.addItem(null); // Placeholder
        try {
            // Ottieni tutti i team degli hackathon in corso
            List<Team> teams = new ArrayList<>();
            List<Hackathon> hackathonInCorso = controller.getHackathonInCorso();
            
            if (hackathonInCorso.isEmpty()) {
                // Fallback: usa tutti gli hackathon disponibili
                List<Hackathon> tuttiHackathon = controller.getTuttiHackathon();
                for (Hackathon h : tuttiHackathon) {
                    teams.addAll(controller.getTeamsByHackathon(h.getId()));
                }
            } else {
                // Usa solo gli hackathon in corso
                for (Hackathon h : hackathonInCorso) {
                    teams.addAll(controller.getTeamsByHackathon(h.getId()));
                }
            }
            if (teams.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    """
                    Nessun team disponibile per la valutazione.

                    Possibili motivi:
                    • Nessun team iscritto all'hackathon
                    • Hackathon non avviato
                    • Problema con i dati del database

                    Controlla la console per il debug dettagliato.
                    """,
                    "Nessun Team Disponibile",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            for (Team team : teams) {
                teamComboBox.addItem(team);
            }
            // Imposta renderer personalizzato per mostrare nome team
            teamComboBox.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Team team) {
                        setText(team.getNome());
                    } else if (value == null) {
                        setText("Seleziona Team");
                    }
                    return this;
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog,
                "Errore durante il caricamento dei team: " + e.getMessage(),
                ERRORE_SISTEMA,
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        JSpinner votoSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 10, 1));
        JTextArea commentoArea = new JTextArea(4, 20);
        commentoArea.setLineWrap(true);
        commentoArea.setWrapStyleWord(true);
        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Team:"), gbc);
        gbc.gridx = 1;
        dialog.add(teamComboBox, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Voto (0-10):"), gbc);
        gbc.gridx = 1;
        dialog.add(votoSpinner, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Commento:"), gbc);
        gbc.gridx = 1;
        dialog.add(new JScrollPane(commentoArea), gbc);
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton confirmButton = new JButton("Assegna Voto");
        JButton cancelButton = new JButton("Annulla");
        // Styling buttons
        confirmButton.setBackground(new Color(46, 204, 113));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        confirmButton.addActionListener(e -> {
            Team selectedTeam = (Team) teamComboBox.getSelectedItem();
            Integer voto = (Integer) votoSpinner.getValue();
            String commento = commentoArea.getText().trim();
            if (selectedTeam == null) {
                JOptionPane.showMessageDialog(dialog,
                    "Seleziona un team per assegnare il voto.",
                    "Team Non Selezionato",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
                // Valutazione assegnata
            try {
                boolean success = controller.assegnaVoto(selectedTeam.getId(), voto, commento);
                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                        """
                        \u2705 Voto assegnato con successo!

                        Team: %s
                        Voto: %d
                        Commento: %s
                        """.formatted(
                            selectedTeam.getNome(),
                            voto,
                            commento.isEmpty() ? "(nessuno)" : commento.substring(0, Math.min(50, commento.length())) + "..."
                        ),
                        "Voto Assegnato",
                        JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
                    refreshData(); // Ricarica la lista valutazioni
                    JOptionPane.showMessageDialog(dialog,
                        "💡 Le statistiche nel pannello utente verranno aggiornate al prossimo accesso.",
                        "Nota",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "\u274C Errore durante l'assegnazione del voto.\nPotresti aver già valutato questo team.",
                        "Errore Assegnazione",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Errore durante l'assegnazione del voto: " + ex.getMessage(),
                    ERRORE_SISTEMA,
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }
    /**
     * Gestisce la visualizzazione della classifica
     */
    private void handleVisualizzaClassifica() {
        try {
            // Crea dialog per la classifica
            JDialog dialog = new JDialog(mainFrame, "Classifica Team", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(mainFrame);
            // Titolo
            JLabel titleLabel = new JLabel("Classifica Finale Hackathon", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
            dialog.add(titleLabel, BorderLayout.NORTH);
            // Crea tabella per la classifica
            String[] columnNames = {"Posizione", "Team", "Voto Medio", "Valutazioni"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Non modificabile
                }
            };
            JTable table = new JTable(tableModel);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.getTableHeader().setReorderingAllowed(false);
            // Ottieni tutti i team degli hackathon in corso
            List<Team> teams = new ArrayList<>();
            List<Hackathon> hackathonInCorso = controller.getHackathonInCorso();
            
            if (hackathonInCorso.isEmpty()) {
                // Fallback: usa tutti gli hackathon disponibili
                List<Hackathon> tuttiHackathon = controller.getTuttiHackathon();
                for (Hackathon h : tuttiHackathon) {
                    teams.addAll(controller.getTeamsByHackathon(h.getId()));
                }
            } else {
                // Usa solo gli hackathon in corso
                for (Hackathon h : hackathonInCorso) {
                    teams.addAll(controller.getTeamsByHackathon(h.getId()));
                }
            } 
            // Calcola classifica
            List<TeamScore> classifica = new ArrayList<>();
            for (Team team : teams) {
                try {
                    double mediaVoto = controller.getValutazioneMediaTeam(team.getId());
                    // Conta quante valutazioni ha ricevuto questo team
                    long numValutazioni = controller.getValutazioniHackathon(1).stream()
                        .filter(v -> v.getTeamId() == team.getId())
                        .count();
                    if (numValutazioni > 0) { // Solo team con almeno una valutazione
                        classifica.add(new TeamScore(team, mediaVoto, (int) numValutazioni));
                    }
                } catch (Exception e) {
                    // Log error but continue processing other teams
                    // Log error but continue processing other teams
                    // Using System.err for non-critical errors that shouldn't stop processing
                }
            }
            // Ordina per voto medio decrescente
            classifica.sort((a, b) -> Double.compare(b.mediaVoto, a.mediaVoto));
            // Popola tabella
            int posizione = 1;
            for (TeamScore score : classifica) {
                Object[] row = {
                    posizione++,
                    score.team.getNome(),
                    String.format("%.2f", score.mediaVoto),
                    score.numValutazioni
                };
                tableModel.addRow(row);
            }
            if (classifica.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Nessuna valutazione presente.\nI giudici devono prima assegnare dei voti ai team.",
                    "Classifica Non Disponibile",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Risultati Finali"));
            dialog.add(scrollPane, BorderLayout.CENTER);
            // Pannello pulsanti
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton closeButton = new JButton("Chiudi");
            JButton refreshButton = new JButton("Aggiorna");
            closeButton.setBackground(new Color(149, 165, 166));
            closeButton.setForeground(Color.WHITE);
            closeButton.setFocusPainted(false);
            refreshButton.setBackground(new Color(52, 152, 219));
            refreshButton.setForeground(Color.WHITE);
            refreshButton.setFocusPainted(false);
            closeButton.addActionListener(e -> dialog.dispose());
            refreshButton.addActionListener(e -> {
                dialog.dispose();
                handleVisualizzaClassifica(); // Ricarica classifica
            });
            buttonPanel.add(refreshButton);
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Errore durante la visualizzazione della classifica: " + e.getMessage(),
                ERRORE_SISTEMA,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Gestisce le informazioni di debug
     */
    @SuppressWarnings("java:S3776") // Cognitive complexity acceptable for debug method
    private void handleDebug() {
        StringBuilder debugInfo = new StringBuilder();
        debugInfo.append("\uD83D\uDD0D INFORMAZIONI DIAGNOSTICHE VALUTAZIONI\n\n");
        try {
            // Info sull'utente corrente
            debugInfo.append("👤 UTENTE CORRENTE:\n");
            debugInfo.append("Login: ").append(controller.getCurrentUser().getLogin()).append("\n");
            debugInfo.append("Nome: ").append(controller.getCurrentUser().getNome()).append(" ").append(controller.getCurrentUser().getCognome()).append("\n");
            debugInfo.append("Ruolo: ").append(controller.getCurrentUser().getRuolo()).append("\n\n");
            // Info sull'hackathon
            debugInfo.append("🏆 HACKATHON CORRENTI:\n");
            List<Hackathon> hackathonInCorso = controller.getHackathonInCorso();
            if (hackathonInCorso.isEmpty()) {
                debugInfo.append("\u274C NESSUN HACKATHON IN CORSO!\n");
                debugInfo.append("Utilizzando fallback con tutti gli hackathon disponibili\n");
            } else {
                debugInfo.append("\u2705 Hackathon in corso: ").append(hackathonInCorso.size()).append("\n");
                for (Hackathon h : hackathonInCorso) {
                    debugInfo.append("- ID: ").append(h.getId()).append(", Nome: ").append(h.getNome()).append("\n");
                }
            }
            debugInfo.append("\n");
            
            // Info sui team disponibili
            debugInfo.append("👥 TEAM DISPONIBILI:\n");
            List<Team> teams = new ArrayList<>();
            
            if (hackathonInCorso.isEmpty()) {
                // Fallback: usa tutti gli hackathon disponibili
                List<Hackathon> tuttiHackathon = controller.getTuttiHackathon();
                for (Hackathon h : tuttiHackathon) {
                    teams.addAll(controller.getTeamsByHackathon(h.getId()));
                }
            } else {
                // Usa solo gli hackathon in corso
                for (Hackathon h : hackathonInCorso) {
                    teams.addAll(controller.getTeamsByHackathon(h.getId()));
                }
            }
            debugInfo.append("Numero team: ").append(teams.size()).append("\n");
            if (teams.isEmpty()) {
                debugInfo.append("\u274C NESSUN TEAM TROVATO!\n");
                debugInfo.append("Possibili soluzioni:\n");
                debugInfo.append("- Eseguire setup dati: documentazione/debug_team_setup.sql\n");
                debugInfo.append("- Verificare che l'hackathon abbia team iscritti\n");
                debugInfo.append("- Controllare che l'hackathon sia avviato\n");
            } else {
                debugInfo.append("\u2705 Team trovati:\n");
                for (Team team : teams) {
                    debugInfo.append("  - ").append(team.getNome()).append(" (ID: ").append(team.getId()).append(")\n");
                }
            }
            debugInfo.append("\n");
            // Info sulle valutazioni esistenti
            debugInfo.append("\uD83D\uDCCA VALUTAZIONI ESISTENTI:\n");
            List<Valutazione> valutazioni = controller.getValutazioniHackathon(1);
            debugInfo.append("Numero valutazioni: ").append(valutazioni.size()).append("\n");
            if (valutazioni.isEmpty()) {
                debugInfo.append("ℹ️ Nessuna valutazione presente - normale se è la prima volta\n");
            } else {
                debugInfo.append("\u2705 Valutazioni esistenti:\n");
                for (Valutazione val : valutazioni) {
                    debugInfo.append("  - ID: ").append(val.getId())
                             .append(", Team ID: ").append(val.getTeamId())
                             .append(", Giudice ID: ").append(val.getGiudiceId())
                             .append(", Voto: ").append(val.getVoto())
                             .append(", Commento: ").append(val.getCommento() != null ? val.getCommento() : "nessuno")
                             .append("\n");
                }
            }
            debugInfo.append("\n");
            // Verifica stato pannello
            debugInfo.append("🎛️ STATO PANNELLO:\n");
            debugInfo.append("Pulsante 'Assegna Voti' abilitato: ").append(assegnaVotiButton.isEnabled()).append("\n");
            debugInfo.append("Pulsante 'Visualizza Classifica' abilitato: ").append(visualizzaClassificaButton.isEnabled()).append("\n");
            debugInfo.append("Elementi nella lista: ").append(valutazioniListModel.size()).append("\n");
            debugInfo.append("\n");
            // Suggerimenti per risoluzione problemi
            debugInfo.append("💡 SUGGERIMENTI PER RISOLUZIONE:\n");
            if (teams.isEmpty()) {
                debugInfo.append("1. \uD83D\uDD27 Eseguire: documentazione/debug_team_setup.sql\n");
                debugInfo.append("2. \uD83D\uDD0D Verificare che l'hackathon sia avviato\n");
                debugInfo.append("3. \uD83D\uDCCA Controllare che ci siano team iscritti\n");
            } else if (!assegnaVotiButton.isEnabled()) {
                debugInfo.append("1. \u2705 Il pulsante dovrebbe essere sempre abilitato\n");
                debugInfo.append("2. \uD83D\uDD04 Provare a ricaricare il pannello\n");
            } else {
                debugInfo.append("1. \u2705 Tutto sembra configurato correttamente\n");
                debugInfo.append("2. 🎯 Provare ad assegnare un voto\n");
            }
        } catch (Exception e) {
            debugInfo.append("\u274C ERRORE durante raccolta informazioni: ").append(e.getMessage()).append("\n");
        }
        debugInfo.append("\n=== FINE DEBUG ===");
        // Mostra il dialog con le informazioni di debug
        JTextArea textArea = new JTextArea(debugInfo.toString());
        textArea.setRows(20);
        textArea.setColumns(60);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this,
            scrollPane,
            "\uD83D\uDD0D Debug Valutazioni Panel",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Mostra il dialog per la revisione progressi (giudici)
     */
    private void showJudgeProgressReviewDialog() {
        if (controller.getCurrentUser() == null || !controller.getCurrentUser().isGiudice()) {
            JOptionPane.showMessageDialog(this,
                "Accesso negato: solo i giudici possono rivedere i progressi",
                "Accesso Negato",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            JudgeProgressReviewDialog dialog = new JudgeProgressReviewDialog(mainFrame, controller);
            dialog.setVisible(true);
        });
    }
    
    /**
     * Classe helper per gestire i punteggi dei team
     */
    private static class TeamScore {
        final Team team;
        final double mediaVoto;
        final int numValutazioni;
        TeamScore(Team team, double mediaVoto, int numValutazioni) {
            this.team = team;
            this.mediaVoto = mediaVoto;
            this.numValutazioni = numValutazioni;
        }
    }
} 
