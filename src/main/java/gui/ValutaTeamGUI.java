
// File: ValutaTeamGUI.java
package gui;

import controller.Controller;
import model.Team;
import model.Voto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ValutaTeamGUI extends JFrame {
    private final Controller controller;

    public ValutaTeamGUI(Controller controller) {
        super("Valuta Team");
        this.controller = controller;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(400, 300);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Valuta i Team", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        mainPanel.add(title, BorderLayout.NORTH);

        List<Team> teams = controller.getTeamsToEvaluate();
        if (teams.isEmpty()) {
            JLabel empty = new JLabel("Nessun team da valutare.", SwingConstants.CENTER);
            mainPanel.add(empty, BorderLayout.CENTER);
        } else {
            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            for (Team t : teams) {
                JPanel row = new JPanel(new BorderLayout(5, 5));
                row.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                JLabel lbl = new JLabel(t.getNome());
                row.add(lbl, BorderLayout.CENTER);

                JButton vote = new JButton("Vota");
                vote.addActionListener((ActionEvent e) -> new VotoDialog(this, controller, t));
                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                btnPanel.add(vote);
                row.add(btnPanel, BorderLayout.EAST);

                listPanel.add(row);
                listPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
            JScrollPane scroll = new JScrollPane(listPanel);
            mainPanel.add(scroll, BorderLayout.CENTER);
        }

        add(mainPanel);
        setVisible(true);
    }

    private static class VotoDialog extends JDialog {
        public VotoDialog(JFrame parent, Controller controller, Team team) {
            super(parent, "Vota " + team.getNome(), true);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5);

            gbc.gridx=0; gbc.gridy=0;
            add(new JLabel("Punteggio (1-10):"), gbc);
            SpinnerNumberModel model = new SpinnerNumberModel(5, 1, 10, 1);
            JSpinner spinner = new JSpinner(model);
            gbc.gridx=1;
            add(spinner, gbc);

            JButton submit = new JButton("Invia");
            submit.addActionListener(e -> {
                int score = (Integer) spinner.getValue();
                controller.valutaTeam(new Voto(team, score));
                JOptionPane.showMessageDialog(this, "Voto registrato.", "Successo", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            });
            gbc.gridx=0; gbc.gridy=1; gbc.gridwidth=2;
            add(submit, gbc);

            pack();
            setLocationRelativeTo(parent);
            setVisible(true);
        }
    }
}
