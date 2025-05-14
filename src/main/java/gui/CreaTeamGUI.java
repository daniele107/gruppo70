// File: CreaTeamGUI.java
package gui;

import controller.Controller;
import model.Partecipante;
import model.Team;

import javax.swing.*;
import java.awt.*;

public class CreaTeamGUI extends JFrame {
    private final Controller controller;
    private final Partecipante partecipante;
    private final JTextField nomeField = new JTextField(20);

    public CreaTeamGUI(Controller controller, Partecipante p) {
        super("Crea Team");
        this.controller = controller;
        this.partecipante = p;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        panel.add(new JLabel("Nome Team:"), gbc);
        gbc.gridx = 1;
        panel.add(nomeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JButton createButton = new JButton("Crea Team");
        createButton.addActionListener(e -> onCreate());
        panel.add(createButton, gbc);

        add(panel);
        pack();
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    private void onCreate() {
        String nome = nomeField.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Inserisci un nome per il team.", "Errore di input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Team t = new Team(nome);
        t.addPartecipante(partecipante);
        controller.creaTeam(t);
        JOptionPane.showMessageDialog(this, "Team creato con successo.", "Successo", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
