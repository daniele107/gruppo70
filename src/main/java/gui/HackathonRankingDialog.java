package gui;

import controller.Controller;
import model.Team;
import model.Hackathon;
import model.Valutazione;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Dialog per visualizzare le classifiche finali dell'hackathon
 * Si aggiorna automaticamente quando tutti i giudici hanno votato
 */
public class HackathonRankingDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String FONT_FAMILY = "Segoe UI";
    
    private final transient Controller controller;
    private final transient Hackathon hackathon;
    
    // Components
    private transient JTable rankingTable;
    private transient DefaultTableModel tableModel;
    private transient JLabel statusLabel;
    private transient JLabel hackathonInfoLabel;
    private transient JButton refreshButton;
    private transient JButton exportButton;
    private transient JButton closeButton;
    private transient JProgressBar votingProgressBar;
    private transient JLabel votingStatusLabel;
    
    private transient List<TeamRanking> currentRankings;
    
    public HackathonRankingDialog(MainFrame parent, Controller controller, Hackathon hackathon) {
        super(parent, "Classifica Hackathon: " + hackathon.getNome(), true);
        this.controller = controller;
        this.hackathon = hackathon;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadRankingData();
        
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents() {
        // Table per classifica
        String[] columns = {"Posizione", "Team", "Punteggio Medio", "Voti Ricevuti", "Dettaglio Voti"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // Posizione
                if (columnIndex == 2) return Double.class;  // Punteggio
                if (columnIndex == 3) return Integer.class; // Voti ricevuti
                return String.class;
            }
        };
        
        rankingTable = new JTable(tableModel);
        rankingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rankingTable.setRowHeight(30);
        rankingTable.getTableHeader().setReorderingAllowed(false);
        
        // Custom renderer per evidenziare i primi 3 posti
        rankingTable.setDefaultRenderer(Object.class, new RankingTableCellRenderer());
        
        // Info hackathon
        hackathonInfoLabel = new JLabel();
        hackathonInfoLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        hackathonInfoLabel.setForeground(new Color(52, 73, 94));
        
        // Progress bar per stato votazioni
        votingProgressBar = new JProgressBar();
        votingProgressBar.setStringPainted(true);
        votingProgressBar.setPreferredSize(new Dimension(0, 25));
        
        votingStatusLabel = new JLabel();
        votingStatusLabel.setFont(new Font(FONT_FAMILY, Font.ITALIC, 12));
        
        // Buttons
        refreshButton = createButton("🔄 Aggiorna Classifica", new Color(52, 152, 219));
        exportButton = createButton("📊 Esporta Risultati", new Color(155, 89, 182));
        closeButton = createButton("❌ Chiudi", new Color(149, 165, 166));
        
        statusLabel = new JLabel("Caricamento classifica...");
        statusLabel.setFont(new Font(FONT_FAMILY, Font.ITALIC, 12));
        statusLabel.setForeground(Color.GRAY);
    }
    
    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        button.setFocusPainted(false);
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        return button;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JLabel titleLabel = new JLabel("🏆 Classifica Finale Hackathon");
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 22));
        titleLabel.setForeground(Color.BLACK);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(hackathonInfoLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        // Voting status panel
        JPanel votingPanel = new JPanel(new BorderLayout(10, 10));
        votingPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "📊 Stato Valutazioni",
            0, 0,
            new Font(FONT_FAMILY, Font.BOLD, 14),
            new Color(52, 73, 94)
        ));
        
        votingPanel.add(votingProgressBar, BorderLayout.CENTER);
        votingPanel.add(votingStatusLabel, BorderLayout.SOUTH);
        
        // Ranking table
        JPanel rankingPanel = new JPanel(new BorderLayout());
        rankingPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "🥇 Classifica Team",
            0, 0,
            new Font(FONT_FAMILY, Font.BOLD, 14),
            new Color(52, 73, 94)
        ));
        
        JScrollPane tableScrollPane = new JScrollPane(rankingTable);
        rankingPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Combine panels
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.add(votingPanel, BorderLayout.NORTH);
        contentPanel.add(rankingPanel, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(236, 240, 241));
        bottomPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(exportButton);
        buttonsPanel.add(Box.createHorizontalStrut(10));
        buttonsPanel.add(closeButton);
        
        bottomPanel.add(buttonsPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        updateHackathonInfo();
    }
    
    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> loadRankingData());
        exportButton.addActionListener(this::handleExportResults);
        closeButton.addActionListener(e -> dispose());
    }
    
    private void updateHackathonInfo() {
        String info = String.format(
            "<html><font color='lightgray'>%s • %s • %s</font></html>",
            hackathon.getSede(),
            hackathon.getDataInizio().format(DATE_FORMAT),
            hackathon.isEventoConcluso() ? "CONCLUSO" : "IN CORSO"
        );
        hackathonInfoLabel.setText(info);
    }
    
    private void loadRankingData() {
        statusLabel.setText("🔄 Caricamento dati classifica...");
        refreshButton.setEnabled(false);
        
        SwingWorker<RankingData, Void> worker = new SwingWorker<RankingData, Void>() {
            @Override
            protected RankingData doInBackground() throws Exception {
                return calculateRankings();
            }
            
            @Override
            protected void done() {
                try {
                    RankingData data = get();
                    updateRankingDisplay(data);
                    updateVotingProgress(data);
                    
                    if (data.isVotingComplete) {
                        statusLabel.setText("✅ Votazioni completate - Classifica finale disponibile");
                        statusLabel.setForeground(new Color(46, 204, 113));
                    } else {
                        statusLabel.setText("⏳ Votazioni in corso - Classifica parziale");
                        statusLabel.setForeground(new Color(241, 196, 15));
                    }
                } catch (Exception e) {
                    statusLabel.setText("❌ Errore nel caricamento: " + e.getMessage());
                    statusLabel.setForeground(new Color(231, 76, 60));
                    JOptionPane.showMessageDialog(HackathonRankingDialog.this,
                        "Errore nel caricamento della classifica:\n" + e.getMessage(),
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    refreshButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private RankingData calculateRankings() throws Exception {
        RankingData data = new RankingData();
        
        // Ottieni tutti i team dell'hackathon
        List<Team> teams = controller.getTeamsByHackathon(hackathon.getId());
        
        // Ottieni tutte le valutazioni dell'hackathon (serve per i dettagli dei voti e il conteggio totale)
        List<Valutazione> valutazioni = controller.getValutazioniHackathon(hackathon.getId());
        
        // Dati aggregati dal DAO: media e numero voti per team
        java.util.List<model.TeamRankingResult> daoRanking = controller.getTeamRanking(hackathon.getId());
        java.util.Map<Integer, model.TeamRankingResult> teamIdToAgg = new java.util.HashMap<>();
        for (model.TeamRankingResult r : daoRanking) {
            teamIdToAgg.put(r.getTeamId(), r);
        }
        
        // Calcola statistiche votazioni
        int totalJudges = controller.contaGiudiciAttivi();
        int expectedVotes = teams.size() * totalJudges;
        int actualVotes = valutazioni.size();
        
        data.totalTeams = teams.size();
        data.totalJudges = totalJudges;
        data.expectedVotes = expectedVotes;
        data.actualVotes = actualVotes;
        data.isVotingComplete = actualVotes >= expectedVotes;
        
        // Calcola ranking per ogni team usando i dati aggregati del DAO e costruendo i dettagli
        data.rankings = new ArrayList<>();
        for (Team team : teams) {
            model.TeamRankingResult agg = teamIdToAgg.get(team.getId());
            List<Valutazione> teamVotes = valutazioni.stream()
                .filter(v -> v.getTeamId() == team.getId())
                .collect(Collectors.toList());
            String voteDetails = teamVotes.isEmpty() ? "Nessun voto" : teamVotes.stream()
                .map(v -> String.valueOf(v.getVoto()))
                .collect(Collectors.joining(", "));
            double average = (agg != null) ? agg.getAverageScore() : 0.0;
            int count = (agg != null) ? agg.getVotesCount() : 0;
            TeamRanking ranking = new TeamRanking(
                team.getNome(),
                average,
                count,
                voteDetails,
                team.getId()
            );
            data.rankings.add(ranking);
        }
        
        // Ordina per punteggio decrescente
        data.rankings.sort((a, b) -> Double.compare(b.averageScore, a.averageScore));
        
        // Assegna posizioni
        for (int i = 0; i < data.rankings.size(); i++) {
            data.rankings.get(i).position = i + 1;
        }
        
        return data;
    }
    
    private void updateRankingDisplay(RankingData data) {
        tableModel.setRowCount(0);
        currentRankings = data.rankings;
        
        for (TeamRanking ranking : data.rankings) {
            Object[] row = {
                ranking.position,
                ranking.teamName,
                Math.round(ranking.averageScore * 100.0) / 100.0,
                ranking.votesReceived,
                ranking.voteDetails
            };
            tableModel.addRow(row);
        }
        
        if (data.rankings.isEmpty()) {
            Object[] emptyRow = {"-", "Nessun team trovato", "-", "-", "-"};
            tableModel.addRow(emptyRow);
        }
    }
    
    private void updateVotingProgress(RankingData data) {
        if (data.expectedVotes > 0) {
            int percentage = Math.min(100, (data.actualVotes * 100) / data.expectedVotes);
            votingProgressBar.setValue(percentage);
            votingProgressBar.setString(percentage + "% completato");
            
            if (percentage == 100) {
                votingProgressBar.setForeground(new Color(46, 204, 113));
                votingStatusLabel.setText("✅ Tutte le valutazioni sono state raccolte!");
                votingStatusLabel.setForeground(new Color(46, 204, 113));
            } else {
                votingProgressBar.setForeground(new Color(52, 152, 219));
                votingStatusLabel.setText(String.format(
                    "⏳ Raccolti %d/%d voti da %d giudici per %d team",
                    data.actualVotes, data.expectedVotes, data.totalJudges, data.totalTeams
                ));
                votingStatusLabel.setForeground(new Color(241, 196, 15));
            }
        } else {
            votingProgressBar.setValue(0);
            votingProgressBar.setString("Nessuna valutazione attesa");
            votingStatusLabel.setText("⚠️ Nessun team o giudice trovato");
            votingStatusLabel.setForeground(Color.GRAY);
        }
    }
    
    private void handleExportResults(ActionEvent e) {
        if (currentRankings == null || currentRankings.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Nessun dato da esportare.",
                "Esportazione",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Esporta Classifica");
        fileChooser.setSelectedFile(new java.io.File("classifica_" + 
            hackathon.getNome().replaceAll("[^a-zA-Z0-9]", "_") + ".txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                exportRankingToFile(fileChooser.getSelectedFile());
                JOptionPane.showMessageDialog(this,
                    "Classifica esportata con successo!",
                    "Esportazione Completata",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Errore durante l'esportazione:\n" + ex.getMessage(),
                    "Errore Esportazione",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportRankingToFile(java.io.File file) throws Exception {
        StringBuilder content = new StringBuilder();
        content.append("=".repeat(60)).append("\n");
        content.append("CLASSIFICA FINALE HACKATHON\n");
        content.append("=".repeat(60)).append("\n");
        content.append("Hackathon: ").append(hackathon.getNome()).append("\n");
        content.append("Sede: ").append(hackathon.getSede()).append("\n");
        content.append("Data: ").append(hackathon.getDataInizio().format(DATE_FORMAT)).append("\n");
        content.append("Stato: ").append(hackathon.isEventoConcluso() ? "CONCLUSO" : "IN CORSO").append("\n");
        content.append("\n");
        
        content.append("CLASSIFICA:\n");
        content.append("-".repeat(60)).append("\n");
        
        for (TeamRanking ranking : currentRankings) {
            content.append(String.format("%2d. %-20s | Punteggio: %5.2f | Voti: %2d | Dettaglio: %s\n",
                ranking.position,
                ranking.teamName,
                ranking.averageScore,
                ranking.votesReceived,
                ranking.voteDetails
            ));
        }
        
        content.append("\n");
        content.append("Generato il: ").append(java.time.LocalDateTime.now().format(DATE_FORMAT)).append("\n");
        
        java.nio.file.Files.write(file.toPath(), content.toString().getBytes());
    }
    
    // Classe per i dati del ranking
    private static class RankingData {
        List<TeamRanking> rankings;
        int totalTeams;
        int totalJudges;
        int expectedVotes;
        int actualVotes;
        boolean isVotingComplete;
    }
    
    // Classe per rappresentare il ranking di un team
    private static class TeamRanking {
        String teamName;
        double averageScore;
        int votesReceived;
        String voteDetails;
        @SuppressWarnings("unused")
        int teamId;
        int position;
        
        TeamRanking(String teamName, double averageScore, int votesReceived, 
                   String voteDetails, int teamId) {
            this.teamName = teamName;
            this.averageScore = averageScore;
            this.votesReceived = votesReceived;
            this.voteDetails = voteDetails;
            this.teamId = teamId;
        }
    }
    
    // Renderer personalizzato per evidenziare i primi 3 posti
    private class RankingTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected && row < currentRankings.size()) {
                int position = currentRankings.get(row).position;
                
                switch (position) {
                    case 1: // Primo posto - Oro
                        c.setBackground(new Color(255, 215, 0, 50));
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    case 2: // Secondo posto - Argento
                        c.setBackground(new Color(192, 192, 192, 50));
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    case 3: // Terzo posto - Bronzo
                        c.setBackground(new Color(205, 127, 50, 50));
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    default:
                        c.setBackground(Color.WHITE);
                        setFont(getFont().deriveFont(Font.PLAIN));
                        break;
                }
            }
            
            return c;
        }
    }
}
