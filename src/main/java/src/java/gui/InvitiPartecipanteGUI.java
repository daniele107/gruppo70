// File: InvitiPartecipanteGUI.java
package gui;

import controller.Controller;
import model.Invito;
import model.Partecipante;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class InvitiPartecipanteGUI extends JFrame {
    private final Partecipante partecipante;
    private final Controller controller;

    public InvitiPartecipanteGUI(Partecipante p, Controller controller) {
        super("I miei Inviti");
        this.partecipante = p;
        this.controller = controller;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(400, 300);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("I miei inviti", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        mainPanel.add(title, BorderLayout.NORTH);

        List<Invito> inviti = controller.getInviti(partecipante);
        if (inviti.isEmpty()) {
            JLabel empty = new JLabel("Non ci sono inviti al momento.", SwingConstants.CENTER);
            mainPanel.add(empty, BorderLayout.CENTER);
        } else {
            JPanel listPanel = new JPanel();
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            for (Invito invito : inviti) {
                JPanel row = new JPanel(new BorderLayout(5, 5));
                row.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                JLabel lbl = new JLabel(invito.getHackathon().getTitolo());
                row.add(lbl, BorderLayout.CENTER);

                JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                JButton accetta = new JButton("Accetta");
                accetta.addActionListener((ActionEvent e) -> handle(invito, true));
                JButton rifiuta = new JButton("Rifiuta");
                rifiuta.addActionListener((ActionEvent e) -> handle(invito, false));
                btns.add(accetta);
                btns.add(rifiuta);
                row.add(btns, BorderLayout.EAST);

                listPanel.add(row);
                listPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
            JScrollPane scroll = new JScrollPane(listPanel);
            mainPanel.add(scroll, BorderLayout.CENTER);
        }

        add(mainPanel);
        setVisible(true);
    }

    private void handle(Invito invito, boolean accept) {
        controller.rispondiInvito(invito, accept);
        JOptionPane.showMessageDialog(this,
                accept ? "Invito accettato." : "Invito rifiutato.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new InvitiPartecipanteGUI(partecipante, controller);
    }
}