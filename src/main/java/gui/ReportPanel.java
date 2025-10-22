package gui;
import controller.Controller;
import model.Hackathon;
import model.Team;
import model.Valutazione;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import static javax.swing.SwingConstants.*;
import java.util.logging.Logger;
import java.util.logging.Level;
/**
 * Pannello per report e statistiche avanzate per organizzatori.
 * Fornisce visualizzazioni dettagliate sui dati degli hackathon.
 */
public class ReportPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(ReportPanel.class.getName());
    // Constants for repeated string literals
    private static final String SELEZIONA_HACKATHON = "Seleziona Hackathon";
    private static final String MEDIA_VOTI = "Media Voti";
    private static final String SEGOE_UI = "Segoe UI";
    private static final String TOTALE_TEAM = "Totale Team";
    private static final String TOTALE_GIUDICI = "Totale Giudici";
    private static final String TOTALE_PARTECIPANTI = "Totale Partecipanti";
    private static final String ERRORE = "Errore";
    // Separator constants
    private static final String SEPARATOR_LONG = "================================================================================\n";
    private static final String SEPARATOR_SHORT = "--------------------------------------------------------------------------------\n";
    // private static final String PERCENTAGE_FORMAT = "%.1f%%"; // Non utilizzato al momento
    
    private final transient Controller controller;
    private final JFrame mainFrame;
    // Components
    private JComboBox<String> hackathonComboBox;
    private JButton generaReportButton;
    private JButton esportaPDFButton;
    private JButton refreshButton;
    private JButton debugButton;
    // Report panels
    @SuppressWarnings("java:S1104") // Fields are used across multiple methods
    private JPanel statsOverviewPanel;
    @SuppressWarnings("java:S1104") // Fields are used across multiple methods
    private JPanel teamStatsPanel;
    @SuppressWarnings("java:S1104") // Fields are used across multiple methods
    private JPanel judgeStatsPanel;
    @SuppressWarnings("java:S1104") // Fields are used across multiple methods
    private JPanel registrationStatsPanel;
    // Chart panels
    @SuppressWarnings("java:S1104") // Fields are used across multiple methods
    private ChartPanel teamChartPanel;
    @SuppressWarnings("java:S1104") // Fields are used across multiple methods
    private ChartPanel judgeChartPanel;
    @SuppressWarnings("java:S1104") // Fields are used across multiple methods
    private ChartPanel registrationChartPanel;
    // Data models
    private DefaultTableModel teamTableModel;
    private DefaultTableModel judgeTableModel;
    private DefaultTableModel registrationTableModel;
    /**
     * Costruttore che inizializza il pannello report
     *
     * @param controller il controller dell'applicazione
     * @param mainFrame  il frame principale
     */
    public ReportPanel(Controller controller, JFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        // Carica dati iniziali
        refreshData();
    }
    /**
     * Inizializza i componenti del pannello
     */
    private void initializeComponents() {
        // Combo box per selezione hackathon
        hackathonComboBox = new JComboBox<>();
        hackathonComboBox.addItem(SELEZIONA_HACKATHON);
        // Buttons
        generaReportButton = createStyledButton("\uD83D\uDCCA Genera Report", new Color(52, 152, 219));
        esportaPDFButton = createStyledButton("📄 Esporta Report", new Color(155, 89, 182));
        refreshButton = createStyledButtonWithTextColor("\uD83D\uDD04 Aggiorna", new Color(46, 204, 113), Color.BLACK);
        debugButton = createStyledButtonWithTextColor("\uD83D\uDD0D Debug", new Color(149, 165, 166), Color.BLACK);
        // Initially disable buttons
        generaReportButton.setEnabled(false);
        esportaPDFButton.setEnabled(false);
        // Table models
        teamTableModel = new DefaultTableModel(
            new String[]{"Team", "Membri", "Progressi", "Voto Medio", "Status"}, 0
        );
        judgeTableModel = new DefaultTableModel(
            new String[]{"Giudice", "Voti Assegnati", MEDIA_VOTI, "Commenti"}, 0
        );
        registrationTableModel = new DefaultTableModel(
            new String[]{"Tipo Utente", "Totale", "Registrati", "Percentuale"}, 0
        );
        // Add hackathon selection listener
        hackathonComboBox.addActionListener(e -> {
            String selected = (String) hackathonComboBox.getSelectedItem();
            boolean hasSelection = selected != null && !SELEZIONA_HACKATHON.equals(selected);
            generaReportButton.setEnabled(hasSelection);
            esportaPDFButton.setEnabled(hasSelection && teamTableModel.getRowCount() != 0);
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
        JLabel titleLabel = new JLabel("📈 Report e Statistiche Avanzate", LEFT);
        titleLabel.setFont(new Font(SEGOE_UI, Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        JLabel subtitleLabel = new JLabel("Analisi dettagliata dei dati degli hackathon", LEFT);
        subtitleLabel.setFont(new Font(SEGOE_UI, Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(149, 165, 166));
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlsPanel.setOpaque(false);
        JPanel hackathonPanel = new JPanel(new BorderLayout());
        hackathonPanel.setOpaque(false);
        hackathonPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            SELEZIONA_HACKATHON
        ));
        hackathonPanel.add(hackathonComboBox, BorderLayout.CENTER);
        controlsPanel.add(hackathonPanel);
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(debugButton);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "Azioni Report"
        ));
        actionPanel.add(generaReportButton);
        actionPanel.add(esportaPDFButton);
        // Stats panels
        JPanel statsContainer = new JPanel(new BorderLayout());
        statsContainer.setOpaque(false);
        // Create tabbed pane for tables and charts
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(SEGOE_UI, Font.PLAIN, 12));
        // Tab 1: Tables
        JPanel tablesPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        tablesPanel.setOpaque(false);
        // Overview stats
        statsOverviewPanel = createStatsPanel("\uD83D\uDCCA Panoramica Generale",
            new String[]{TOTALE_TEAM, TOTALE_GIUDICI, TOTALE_PARTECIPANTI, MEDIA_VOTI});
        // Team stats
        teamStatsPanel = createTablePanel("👥 Statistiche Team", teamTableModel);
        // Judge stats
        judgeStatsPanel = createTablePanel("🧑\u200D⚖️ Statistiche Giudici", judgeTableModel);
        // Registration stats
        registrationStatsPanel = createTablePanel("\uD83D\uDCDD Statistiche Registrazioni", registrationTableModel);
        tablesPanel.add(statsOverviewPanel);
        tablesPanel.add(teamStatsPanel);
        tablesPanel.add(judgeStatsPanel);
        tablesPanel.add(registrationStatsPanel);
        // Tab 2: Charts
        JPanel chartsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        chartsPanel.setOpaque(false);
        // Initialize chart panels with sample data
        teamChartPanel = ChartPanel.createBarChart("\uD83D\uDCCA Voti per Team", ChartPanel.createSampleData());
        judgeChartPanel = ChartPanel.createBarChart("🧑\u200D⚖️ Voti per Giudice", ChartPanel.createSampleData());
        registrationChartPanel = ChartPanel.createPieChart("\uD83D\uDCDD Distribuzione Ruoli", ChartPanel.createPieSampleData());
        chartsPanel.add(teamChartPanel);
        chartsPanel.add(judgeChartPanel);
        chartsPanel.add(registrationChartPanel);
        // Add empty panel for layout
        JPanel emptyPanel = new JPanel();
        emptyPanel.setOpaque(false);
        chartsPanel.add(emptyPanel);
        // Add tabs
        tabbedPane.addTab("📋 Tabelle", tablesPanel);
        tabbedPane.addTab("📈 Grafici", chartsPanel);
        statsContainer.add(tabbedPane, BorderLayout.CENTER);
        contentPanel.add(actionPanel, BorderLayout.NORTH);
        contentPanel.add(statsContainer, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        generaReportButton.addActionListener(e -> generaReport());
        esportaPDFButton.addActionListener(e -> esportaPDF());
        refreshButton.addActionListener(e -> {
            refreshData();
            JOptionPane.showMessageDialog(mainFrame, "\u2705 Report aggiornati!", "Aggiornamento", JOptionPane.INFORMATION_MESSAGE);
        });
        debugButton.addActionListener(e -> showDebugInfo());
    }
    /**
     * Aggiorna i dati del pannello
     */
    public void refreshData() {
        try {
            // Carica hackathon disponibili
            loadAvailableHackathons();
            // Reset delle tabelle
            teamTableModel.setRowCount(0);
            judgeTableModel.setRowCount(0);
            registrationTableModel.setRowCount(0);
            // Reset overview stats
            updateOverviewStats(null);
        } catch (Exception e) {
            // Log error but continue execution
            logger.log(Level.WARNING, "Errore durante l'aggiornamento del report: " + e.getMessage(), e);
        }
    }
    /**
     * Carica gli hackathon disponibili per il report
     */
    private void loadAvailableHackathons() {
        try {
            hackathonComboBox.removeAllItems();
            hackathonComboBox.addItem(SELEZIONA_HACKATHON);
            List<Hackathon> hackathons = controller.getTuttiHackathon();
            for (Hackathon h : hackathons) {
                hackathonComboBox.addItem(h.getId() + ": " + h.getNome());
            }
            if (hackathons.isEmpty()) {
                hackathonComboBox.addItem("Nessun hackathon disponibile");
            }
        } catch (Exception e) {
            hackathonComboBox.addItem("Errore caricamento hackathon: " + e.getMessage());
        }
    }
    /**
     * Genera il report per l'hackathon selezionato
     */
    private void generaReport() {
        String selectedHackathon = (String) hackathonComboBox.getSelectedItem();
        if (selectedHackathon == null || SELEZIONA_HACKATHON.equals(selectedHackathon)) {
            JOptionPane.showMessageDialog(this, "Seleziona un hackathon valido", ERRORE, JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int hackathonId = extractHackathonId(selectedHackathon);
            generaReportButton.setText("\u23F3 Generazione...");
            generaReportButton.setEnabled(false);
            // Genera report in background
            SwingUtilities.invokeLater(() -> {
                try {
                    generateReportData(hackathonId);
                    esportaPDFButton.setEnabled(true);
                    JOptionPane.showMessageDialog(mainFrame, "\u2705 Report generato con successo!", "Report", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Errore durante la generazione del report:%n" + e.getMessage(),
                        ERRORE,
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    generaReportButton.setText("\uD83D\uDCCA Genera Report");
                    generaReportButton.setEnabled(true);
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Errore nell'identificazione dell'hackathon:%n" + e.getMessage(),
                ERRORE,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Genera i dati del report
     */
    private void generateReportData(int hackathonId) {
        try {
            // Ottieni dati dell'hackathon
            Hackathon hackathon = controller.getHackathonById(hackathonId);
            if (hackathon == null) {
                throw new IllegalArgumentException("Hackathon non trovato");
            }
            // Statistiche team
            List<Team> teams = controller.getTeamsByHackathon(hackathonId);
            generateTeamStats(teams);
            // Statistiche giudici
            List<Valutazione> valutazioni = controller.getValutazioniHackathon(hackathonId);
            generateJudgeStats(valutazioni);
            // Statistiche registrazioni
            generateRegistrationStats(hackathon);
            // Aggiorna overview stats
            updateOverviewStats(hackathon);
            // Aggiorna grafici con dati reali
            updateChartsWithRealData(teams, valutazioni);
        } catch (Exception e) {
            throw new IllegalStateException("Errore nella generazione dei dati: " + e.getMessage());
        }
    }
    /**
     * Aggiorna i grafici con dati reali
     */
    private void updateChartsWithRealData(List<Team> teams, List<Valutazione> valutazioni) {
        try {
            // Grafico team - voti per team
            Map<String, Number> teamData = new HashMap<>();
            for (Team team : teams) {
                double mediaVoto = controller.getValutazioneMediaTeam(team.getId());
                teamData.put(team.getNome(), mediaVoto);
            }
            // Aggiorna grafico team
            if (!teamData.isEmpty()) {
                ChartPanel newTeamChart = ChartPanel.createBarChart("\uD83D\uDCCA Voti Medi per Team", teamData);
                replaceChartPanel(teamChartPanel, newTeamChart);
                teamChartPanel = newTeamChart;
            }
            // Grafico giudici - voti assegnati per giudice
            Map<String, Number> judgeData = new HashMap<>();
            for (Valutazione v : valutazioni) {
                String judgeName = "Giudice " + v.getGiudiceId();
                judgeData.put(judgeName, judgeData.getOrDefault(judgeName, 0).intValue() + 1);
            }
            // Aggiorna grafico giudici
            if (!judgeData.isEmpty()) {
                ChartPanel newJudgeChart = ChartPanel.createBarChart("🧑\u200D⚖️ Voti Assegnati per Giudice", judgeData);
                replaceChartPanel(judgeChartPanel, newJudgeChart);
                judgeChartPanel = newJudgeChart;
            }
        } catch (Exception e) {
            // Log error but continue execution
            logger.log(Level.WARNING, "Errore durante l'aggiornamento del report: " + e.getMessage(), e);
        }
    }
    /**
     * Sostituisce un pannello grafico con uno nuovo
     */
    private void replaceChartPanel(ChartPanel oldPanel, ChartPanel newPanel) {
        if (oldPanel != null && oldPanel.getParent() != null) {
            oldPanel.getParent().remove(oldPanel);
            oldPanel.getParent().add(newPanel);
            oldPanel.getParent().revalidate();
            oldPanel.getParent().repaint();
        }
    }
    /**
     * Genera statistiche dei team
     */
    private void generateTeamStats(List<Team> teams) {
        for (Team team : teams) {
            try {
                int membriCount = controller.contaMembriTeam(team.getId());
                int progressiCount = controller.getProgressiTeam(team.getId()).size();
                double mediaVoto = controller.getValutazioneMediaTeam(team.getId());
                String status = team.getNome(); // Placeholder
                teamTableModel.addRow(new Object[]{
                    team.getNome(),
                    membriCount,
                    progressiCount,
                    String.valueOf(Math.round(mediaVoto * 10.0) / 10.0),
                    status
                });
            } catch (Exception e) {
                teamTableModel.addRow(new Object[]{
                    team.getNome(),
                    ERRORE,
                    ERRORE,
                    ERRORE,
                    ERRORE
                });
            }
        }
    }
    /**
     * Genera statistiche dei giudici
     */
    private void generateJudgeStats(List<Valutazione> valutazioni) {
        Map<String, JudgeStats> judgeStatsMap = new HashMap<>();
        for (Valutazione v : valutazioni) {
            try {
                String judgeName = "Giudice " + v.getGiudiceId(); // Placeholder
                JudgeStats stats = judgeStatsMap.computeIfAbsent(judgeName, k -> new JudgeStats());
                stats.votiCount++;
                stats.mediaVoti += v.getVoto();
                if (v.getCommento() != null && !v.getCommento().isEmpty()) {
                    stats.commentiCount++;
                }
            } catch (Exception e) {
                // Ignora errori per giudici singoli
            }
        }
        // Aggiungi righe alla tabella
        for (Map.Entry<String, JudgeStats> entry : judgeStatsMap.entrySet()) {
            JudgeStats stats = entry.getValue();
            double media = stats.votiCount > 0 ? stats.mediaVoti / stats.votiCount : 0;
            judgeTableModel.addRow(new Object[]{
                entry.getKey(),
                stats.votiCount,
                String.valueOf(Math.round(media * 10.0) / 10.0),
                stats.commentiCount
            });
        }
    }
    /**
     * Genera statistiche delle registrazioni
     */
    private void generateRegistrationStats(Hackathon hackathon) {
        try {
            // Partecipanti
            int partecipantiRegistrati = controller.contaPartecipanti(hackathon.getId());
            int partecipantiTotali = 100; // Placeholder
            double percPartecipanti = partecipantiTotali > 0 ? (double) partecipantiRegistrati / partecipantiTotali * 100 : 0;
            // Giudici
            int giudiciRegistrati = 20; // Placeholder
            int giudiciTotali = 20; // Placeholder
            double percGiudici = giudiciTotali > 0 ? (double) giudiciRegistrati / giudiciTotali * 100 : 0;
            // Organizzatori
            int organizzatoriRegistrati = 1; // Placeholder
            int organizzatoriTotali = 1; // Placeholder
            double percOrganizzatori = organizzatoriTotali > 0 ? (double) organizzatoriRegistrati / organizzatoriTotali * 100 : 0;
            registrationTableModel.addRow(new Object[]{"Partecipanti", partecipantiTotali, partecipantiRegistrati, Math.round(percPartecipanti * 100.0) / 100.0 + "%"});
            registrationTableModel.addRow(new Object[]{"Giudici", giudiciTotali, giudiciRegistrati, Math.round(percGiudici * 100.0) / 100.0 + "%"});
            registrationTableModel.addRow(new Object[]{"Organizzatori", organizzatoriTotali, organizzatoriRegistrati, Math.round(percOrganizzatori * 100.0) / 100.0 + "%"});
        } catch (Exception e) {
            registrationTableModel.addRow(new Object[]{ERRORE, "N/A", "N/A", "N/A"});
        }
    }
    /**
     * Aggiorna le statistiche generali
     */
    private void updateOverviewStats(Hackathon hackathon) {
        if (hackathon == null) {
            // Reset stats
            updateStatsPanel(statsOverviewPanel,
                new String[]{TOTALE_TEAM, TOTALE_GIUDICI, TOTALE_PARTECIPANTI, MEDIA_VOTI},
                new String[]{"-", "-", "-", "-"});
            return;
        }
        try {
            int totalTeams = controller.getTeamsByHackathon(hackathon.getId()).size();
            int totalPartecipanti = controller.contaPartecipanti(hackathon.getId());
            int totalGiudici = 5; // Placeholder
            double mediaGenerale = 7.5; // Placeholder
            updateStatsPanel(statsOverviewPanel,
                new String[]{TOTALE_TEAM, TOTALE_GIUDICI, TOTALE_PARTECIPANTI, MEDIA_VOTI},
                new String[]{
                    String.valueOf(totalTeams),
                    String.valueOf(totalGiudici),
                    String.valueOf(totalPartecipanti),
                    String.valueOf(Math.round(mediaGenerale * 10.0) / 10.0)
                });
        } catch (Exception e) {
            updateStatsPanel(statsOverviewPanel,
                new String[]{TOTALE_TEAM, TOTALE_GIUDICI, TOTALE_PARTECIPANTI, MEDIA_VOTI},
                new String[]{ERRORE, ERRORE, ERRORE, ERRORE});
        }
    }
    /**
     * Esporta il report in formato testuale strutturato
     */
    private void esportaPDF() {
        String selectedHackathon = (String) hackathonComboBox.getSelectedItem();
        if (selectedHackathon == null || SELEZIONA_HACKATHON.equals(selectedHackathon)) {
            JOptionPane.showMessageDialog(this, "Seleziona un hackathon valido", ERRORE, JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int hackathonId = extractHackathonId(selectedHackathon);
            Hackathon hackathon = controller.getHackathonById(hackathonId);
            if (hackathon == null) {
                JOptionPane.showMessageDialog(this, "Hackathon non trovato", ERRORE, JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Genera contenuto del report
            String reportContent = generateReportContent(hackathon);
            // Salva il file
            saveReportToFile(reportContent, hackathon);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Errore nell'esportazione del report:%n" + e.getMessage(),
                ERRORE,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Genera il contenuto completo del report
     */
    private String generateReportContent(Hackathon hackathon) {
        StringBuilder report = new StringBuilder();
        // Header del report
        report.append(SEPARATOR_LONG);
        report.append("                           REPORT HACKATHON MANAGER\n");
        report.append(SEPARATOR_LONG + "\n");
        report.append("EVENTO: ").append(hackathon.getNome()).append("\n");
        report.append("SEDE: ").append(hackathon.getSede()).append("\n");
        report.append("DATA INIZIO: ").append(hackathon.getDataInizio()).append("\n");
        report.append("DATA FINE: ").append(hackathon.getDataFine()).append("\n");
        report.append("DESCRIZIONE: ").append(hackathon.getDescrizioneProblema()).append("\n");
        report.append("STATO REGISTRAZIONI: ").append(hackathon.isRegistrazioniAperte() ? "APERTE" : "CHIUSE").append("\n");
        report.append("EVENTO AVVIATO: ").append(hackathon.isEventoAvviato() ? "SÌ" : "NO").append("\n");
        report.append("EVENTO CONCLUSO: ").append(hackathon.isEventoConcluso() ? "SÌ" : "NO").append("\n\n");
        // Statistiche generali
        report.append(SEPARATOR_LONG);
        report.append("                           STATISTICHE GENERALI\n");
        report.append(SEPARATOR_LONG + "\n");
        try {
            int totalTeams = teamTableModel.getRowCount();
            int totalPartecipanti = controller.contaPartecipanti(hackathon.getId());
            int totalGiudici = 5; // Placeholder
            report.append("• Totale Team: ").append(totalTeams).append("\n");
            report.append("• Totale Partecipanti: ").append(totalPartecipanti).append("\n");
            report.append("• Totale Giudici: ").append(totalGiudici).append("\n\n");
        } catch (Exception e) {
            report.append("Errore nel recupero delle statistiche generali\n\n");
        }
        // Statistiche team
        report.append(SEPARATOR_LONG);
        report.append("                           STATISTICHE TEAM\n");
        report.append(SEPARATOR_LONG + "\n");
        appendTableToReport(report, teamTableModel, "Team", "Membri", "Progressi", "Voto Medio", "Status");
        // Statistiche giudici
        report.append("\n" + SEPARATOR_LONG);
        report.append("                           STATISTICHE GIUDICI\n");
        report.append(SEPARATOR_LONG + "\n");
        appendTableToReport(report, judgeTableModel, "Giudice", "Voti Assegnati", MEDIA_VOTI, "Commenti");
        // Statistiche registrazioni
        report.append("\n" + SEPARATOR_LONG);
        report.append("                           STATISTICHE REGISTRAZIONI\n");
        report.append(SEPARATOR_LONG + "\n");
        appendTableToReport(report, registrationTableModel, "Tipo Utente", "Totale", "Registrati", "Percentuale");
        // Footer
        report.append("\n" + SEPARATOR_LONG);
        report.append("Report generato il: ").append(java.time.LocalDateTime.now()).append("\n");
        report.append("Sistema: Hackathon Manager v1.0\n");
        report.append(SEPARATOR_LONG);
        return report.toString();
    }
    /**
     * Aggiunge una tabella al report
     */
    private void appendTableToReport(StringBuilder report, DefaultTableModel tableModel, 
                                   String col1, String col2, String col3, String col4) {
        // Intestazioni delle colonne
        report.append(String.format("%-25s %-15s %-15s %-15s%n", col1, col2, col3, col4));
        report.append(SEPARATOR_SHORT);
        // Dati della tabella
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            report.append(String.format("%-25s %-15s %-15s %-15s%n",
                tableModel.getValueAt(i, 0),
                tableModel.getValueAt(i, 1),
                tableModel.getValueAt(i, 2),
                tableModel.getValueAt(i, 3)));
        }
        report.append("\n");
    }
    /**
     * Aggiunge una tabella al report
     */
    private void appendTableToReport(StringBuilder report, DefaultTableModel model,
                                   String col1, String col2, String col3, String col4, String col5) {
        if (model.getRowCount() == 0) {
            report.append("Nessun dato disponibile\n\n");
            return;
        }
        // Header tabella
        report.append(String.format("%-20s %-15s %-15s %-15s %-15s%n", col1, col2, col3, col4, col5));
        report.append(SEPARATOR_SHORT);
        // Righe dati
        for (int i = 0; i < model.getRowCount(); i++) {
            String val1 = model.getValueAt(i, 0) != null ? model.getValueAt(i, 0).toString() : "";
            String val2 = model.getValueAt(i, 1) != null ? model.getValueAt(i, 1).toString() : "";
            String val3 = model.getValueAt(i, 2) != null ? model.getValueAt(i, 2).toString() : "";
            String val4 = model.getValueAt(i, 3) != null ? model.getValueAt(i, 3).toString() : "";
            String val5 = model.getValueAt(i, 4) != null ? model.getValueAt(i, 4).toString() : "";
            // Tronca valori lunghi
            val1 = val1.length() > 18 ? val1.substring(0, 15) + "..." : val1;
            report.append(String.format("%-20s %-15s %-15s %-15s %-15s%n",
                                      val1, val2, val3, val4, val5));
        }
        report.append("\n");
    }
    /**
     * Salva il report su file
     */
    private void saveReportToFile(String content, Hackathon hackathon) {
        try {
            // Crea nome file basato sull'hackathon
            String fileName = "Report_" + hackathon.getNome().replaceAll("[^a-zA-Z0-9]", "_") + ".txt";
            // Mostra dialog per scegliere dove salvare
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File(fileName));
            fileChooser.setDialogTitle("Salva Report");
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                // Aggiungi estensione .txt se non presente
                if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                    selectedFile = new java.io.File(selectedFile.getAbsolutePath() + ".txt");
                }
                // Scrivi il file
                try (java.io.FileWriter writer = new java.io.FileWriter(selectedFile)) {
                    writer.write(content);
                }
                JOptionPane.showMessageDialog(this,
                    "Report esportato con successo!%n%nFile salvato: " + selectedFile.getAbsolutePath(),
                    "Esportazione Completata",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Errore durante il salvataggio del file:%n" + e.getMessage(),
                "Errore Salvataggio",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Mostra informazioni di debug
     */
    private void showDebugInfo() {
        StringBuilder debugInfo = new StringBuilder();
        debugInfo.append("\uD83D\uDD0D DEBUG REPORT PANEL\n\n");
        try {
            debugInfo.append("🎯 HACKATHON SELEZIONATO: ").append(hackathonComboBox.getSelectedItem()).append("\n");
            debugInfo.append("\uD83D\uDCCA TEAM NEL REPORT: ").append(teamTableModel.getRowCount()).append("\n");
            debugInfo.append("🧑\u200D⚖️ GIUDICI NEL REPORT: ").append(judgeTableModel.getRowCount()).append("\n");
            debugInfo.append("\uD83D\uDCDD REGISTRAZIONI NEL REPORT: ").append(registrationTableModel.getRowCount()).append("\n");
        } catch (Exception e) {
            debugInfo.append("\u274C ERRORE: ").append(e.getMessage()).append("\n");
        }
        JTextArea textArea = new JTextArea(debugInfo.toString());
        textArea.setRows(15);
        textArea.setColumns(50);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "\uD83D\uDD0D Debug Report Panel", JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * Crea un pannello per le statistiche con valori
     */
    private JPanel createStatsPanel(String title, String[] labels) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(SEGOE_UI, Font.BOLD, 14),
            new Color(52, 73, 94)
        ));
        panel.setBackground(Color.WHITE);
        JPanel contentPanel = new JPanel(new GridLayout(labels.length, 2, 10, 5));
        contentPanel.setOpaque(false);
        for (String label : labels) {
            contentPanel.add(new JLabel(label + ":"));
            contentPanel.add(new JLabel("-", SwingConstants.RIGHT));
        }
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    /**
     * Aggiorna un pannello di statistiche
     */
    private void updateStatsPanel(JPanel panel, String[] labels, String[] values) {
        BorderLayout layout = (BorderLayout) panel.getLayout();
        JPanel contentPanel = (JPanel) layout.getLayoutComponent(BorderLayout.CENTER);
        contentPanel.removeAll();
        for (int i = 0; i < labels.length; i++) {
            contentPanel.add(new JLabel(labels[i] + ":"));
            contentPanel.add(new JLabel(values[i], SwingConstants.RIGHT));
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    /**
     * Crea un pannello con tabella
     */
    private JPanel createTablePanel(String title, DefaultTableModel model) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(SEGOE_UI, Font.BOLD, 14),
            new Color(52, 73, 94)
        ));
        panel.setBackground(Color.WHITE);
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    /**
     * Estrae l'ID dell'hackathon dalla stringa selezionata
     */
    private int extractHackathonId(String hackathonString) {
        try {
            return Integer.parseInt(hackathonString.split(":")[0]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato hackathon non valido: " + hackathonString);
        }
    }
    /**
     * Crea un pulsante stilizzato
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font(SEGOE_UI, Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
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
     * Crea un pulsante stilizzato con colore testo personalizzato
     */
    private JButton createStyledButtonWithTextColor(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font(SEGOE_UI, Font.BOLD, 14)); // Font più grande
        button.setForeground(textColor); // Testo personalizzato
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
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
     * Classe helper per statistiche giudici
     */
    private static class JudgeStats {
        int votiCount = 0;
        double mediaVoti = 0.0;
        int commentiCount = 0;
    }
}
