package gui;

import controller.Controller;
import model.Team;
import model.Hackathon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialog per visualizzare lo stato dei team e la loro definitività
 */
public class TeamStatusDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String SEGOE_UI = "Segoe UI";
    
    private final transient Controller controller;
    
    // Components
    private JTable teamsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton closeButton;
    private JLabel statusLabel;
    
    public TeamStatusDialog(MainFrame parent, Controller controller) {
        super(parent, "Stato Team - Formazione e Definitività", true);
        this.controller = controller;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadTeamData();
        
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents() {
        // Table per team
        String[] columns = {"Team", "Hackathon", "Membri", "Stato", "Data Definitività", "Può essere modificato"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return Boolean.class; // Può essere modificato
                return String.class;
            }
        };
        
        teamsTable = new JTable(tableModel);
        teamsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamsTable.setRowHeight(30);
        teamsTable.getTableHeader().setReorderingAllowed(false);
        
        // Custom renderer per evidenziare i team definitivi
        teamsTable.setDefaultRenderer(Object.class, new TeamStatusCellRenderer());
        
        // Buttons
        refreshButton = createButton("🔄 Aggiorna", new Color(52, 152, 219));
        closeButton = createButton("❌ Chiudi", new Color(149, 165, 166));
        
        statusLabel = new JLabel("Caricamento stato team...");
        statusLabel.setFont(new Font(SEGOE_UI, Font.ITALIC, 12));
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
        button.setFont(new Font(SEGOE_UI, Font.BOLD, 14));
        return button;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JLabel titleLabel = new JLabel("👥 Stato Team e Definitività");
        titleLabel.setFont(new Font(SEGOE_UI, Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Visualizza lo stato di formazione dei team e la loro definitività");
        subtitleLabel.setFont(new Font(SEGOE_UI, Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(new Color(241, 243, 244));
        infoPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel infoLabel = new JLabel("<html><b>ℹ️ Informazioni:</b> I team diventano definitivi quando si chiudono le registrazioni dell'hackathon. " +
            "Dopo questo momento non possono più essere modificati.</html>");
        infoLabel.setFont(new Font(SEGOE_UI, Font.PLAIN, 12));
        infoLabel.setForeground(new Color(52, 73, 94));
        infoPanel.add(infoLabel);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "📋 Lista Team",
            0, 0,
            new Font(SEGOE_UI, Font.BOLD, 14),
            new Color(52, 73, 94)
        ));
        
        JScrollPane tableScrollPane = new JScrollPane(teamsTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(236, 240, 241));
        bottomPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(closeButton, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> loadTeamData());
        closeButton.addActionListener(e -> dispose());
    }
    
    private void loadTeamData() {
        statusLabel.setText("🔄 Caricamento dati team...");
        refreshButton.setEnabled(false);
        
        SwingWorker<List<TeamInfo>, Void> worker = new SwingWorker<List<TeamInfo>, Void>() {
            @Override
            protected List<TeamInfo> doInBackground() throws Exception {
                List<TeamInfo> teamInfos = new java.util.ArrayList<>();
                List<Team> allTeams = controller.getTuttiTeam();
                
                for (Team team : allTeams) {
                    try {
                        Hackathon hackathon = controller.getHackathonById(team.getHackathonId());
                        int memberCount = controller.contaMembriTeam(team.getId());
                        
                        TeamInfo info = new TeamInfo(
                            team.getNome(),
                            hackathon != null ? hackathon.getNome() : "Sconosciuto",
                            memberCount,
                            team.isDefinitivo() ? "🔒 Definitivo" : "🔓 In formazione",
                            team.getDataDefinitivo(),
                            team.pueEssereModificato()
                        );
                        
                        teamInfos.add(info);
                    } catch (Exception e) {
                        // Skip teams with errors
                    }
                }
                
                return teamInfos;
            }
            
            @Override
            protected void done() {
                try {
                    List<TeamInfo> teamInfos = get();
                    updateTeamTable(teamInfos);
                    statusLabel.setText("✅ Caricati " + teamInfos.size() + " team");
                    statusLabel.setForeground(new Color(46, 204, 113));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    statusLabel.setText("❌ Operazione interrotta");
                    statusLabel.setForeground(new Color(231, 76, 60));
                } catch (Exception e) {
                    statusLabel.setText("❌ Errore nel caricamento: " + e.getMessage());
                    statusLabel.setForeground(new Color(231, 76, 60));
                } finally {
                    refreshButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private void updateTeamTable(List<TeamInfo> teamInfos) {
        tableModel.setRowCount(0);
        
        for (TeamInfo info : teamInfos) {
            String dataDefinitivo = info.dataDefinitivo != null ? 
                info.dataDefinitivo.format(DATE_FORMAT) : "-";
            
            Object[] row = {
                info.nomeTeam,
                info.nomeHackathon,
                info.numeroMembri + " membri",
                info.stato,
                dataDefinitivo,
                info.pueEssereModificato
            };
            
            tableModel.addRow(row);
        }
        
        if (teamInfos.isEmpty()) {
            Object[] emptyRow = {"Nessun team trovato", "", "", "", "", false};
            tableModel.addRow(emptyRow);
        }
    }
    
    // Classe per le informazioni del team
    private static class TeamInfo {
        String nomeTeam;
        String nomeHackathon;
        int numeroMembri;
        String stato;
        java.time.LocalDateTime dataDefinitivo;
        boolean pueEssereModificato;
        
        TeamInfo(String nomeTeam, String nomeHackathon, int numeroMembri, 
                String stato, java.time.LocalDateTime dataDefinitivo, boolean pueEssereModificato) {
            this.nomeTeam = nomeTeam;
            this.nomeHackathon = nomeHackathon;
            this.numeroMembri = numeroMembri;
            this.stato = stato;
            this.dataDefinitivo = dataDefinitivo;
            this.pueEssereModificato = pueEssereModificato;
        }
    }
    
    // Renderer personalizzato per evidenziare i team definitivi
    private class TeamStatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected && row < tableModel.getRowCount()) {
                String stato = (String) tableModel.getValueAt(row, 3); // Colonna stato
                
                if (stato != null && stato.contains("Definitivo")) {
                    // Team definitivo - sfondo rosso chiaro
                    c.setBackground(new Color(255, 235, 235));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (stato != null && stato.contains("In formazione")) {
                    // Team in formazione - sfondo verde chiaro
                    c.setBackground(new Color(235, 255, 235));
                    setFont(getFont().deriveFont(Font.PLAIN));
                } else {
                    c.setBackground(Color.WHITE);
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
            }
            
            return c;
        }
    }
}
