
        package gui;

import controller.Controller;
import model.Team;
import model.Voto;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.stream.Collectors;

/**
 * Finestra per la valutazione dei team: interfaccia più moderna ed elegante
 */
public class ValutaTeamGUI extends JFrame {
    private final Controller controller;
    private JTable teamTable;
    private DefaultTableModel tableModel;

    public ValutaTeamGUI(Controller controller) {
        super("Valuta Team - Hackathon Manager");
        this.controller = controller;
        initUI();
    }

    private void initUI() {
        // Look & Feel di sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(45, 62, 80));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titolo
        JLabel title = new JLabel("Valuta i Team Partecipanti", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        mainPanel.add(title, BorderLayout.NORTH);

        // Tabella dei team
        String[] columns = {"Nome Team", "Partecipanti"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        teamTable = new JTable(tableModel);
        teamTable.setRowHeight(30);
        teamTable.setFont(new Font("SansSerif", Font.PLAIN, 16));
        teamTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));
        teamTable.getTableHeader().setBackground(new Color(52, 152, 219));
        teamTable.getTableHeader().setForeground(Color.WHITE);
        teamTable.setSelectionBackground(new Color(52, 152, 219));
        JScrollPane scrollPane = new JScrollPane(teamTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Barra dei bottoni
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);

        JButton evaluateBtn = createStyledButton("Valuta");
        evaluateBtn.addActionListener(e -> evaluateSelectedTeam());
        buttonPanel.add(evaluateBtn);

        JButton closeBtn = createStyledButton("Chiudi");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        populateTable();
        setVisible(true);
    }

    private void populateTable() {
        tableModel.setRowCount(0);
        var teams = controller.getTeamsToEvaluate();
        if (teams.isEmpty()) {
            tableModel.addRow(new Object[]{"Nessun team disponibile", ""});
        } else {
            teams.forEach(team -> {
                String partecipanti = team.getPartecipanti().stream()
                        .map(p -> p.getNome() + " " + p.getCognome())
                        .collect(Collectors.joining(", "));
                tableModel.addRow(new Object[]{team.getNome(), partecipanti});
            });
        }
    }

    private void evaluateSelectedTeam() {
        int selectedRow = teamTable.getSelectedRow();
        if (selectedRow < 0 || teamTable.getValueAt(selectedRow, 0).equals("Nessun team disponibile")) {
            JOptionPane.showMessageDialog(this, "Seleziona un team da valutare.",
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String teamName = (String) tableModel.getValueAt(selectedRow, 0);
        Team team = controller.getTeamsToEvaluate().stream()
                .filter(t -> t.getNome().equals(teamName))
                .findFirst().orElse(null);
        if (team == null) return;

        String input = JOptionPane.showInputDialog(this,
                "Inserisci il voto per \"" + teamName + "\" (0-100):", "0");
        try {
            int score = Integer.parseInt(input);
            if (score < 0 || score > 100) throw new NumberFormatException();
            // Usa il costruttore corretto di Voto
            controller.valutaTeam(new Voto(team, score));
            JOptionPane.showMessageDialog(this, "Voto salvato con successo!",
                    "Fatto", JOptionPane.INFORMATION_MESSAGE);
            populateTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valore non valido. Inserisci un numero tra 0 e 100.",
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new RoundedBorder(10));
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(120, 40));
        return btn;
    }

    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        public RoundedBorder(int radius) { this.radius = radius; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}
