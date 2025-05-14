package gui;

import controller.Controller;
import model.*;
import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {
    private final Utente utente;
    private final Controller controller;

    public Dashboard(Utente u, Controller controller) {
        super("Dashboard - Benvenuto");
        this.utente = u;
        this.controller = controller;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel lbl = new JLabel("Ciao, " + utente.getNome() + " " + utente.getCognome() + "!");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 20f));
        panel.add(lbl);

        JButton prof = new JButton("Profilo");
        prof.addActionListener(e -> new ProfiloUtenteGUI(utente, controller));
        panel.add(prof);

        if (utente instanceof Partecipante) {
            JButton inviti = new JButton("Inviti");
            inviti.addActionListener(e -> new InvitiPartecipanteGUI((Partecipante)utente, controller));
            panel.add(inviti);
        } else if (utente instanceof Organizzatore) {
            JButton creaHack = new JButton("Crea Hackathon");
            creaHack.addActionListener(e -> new CreaHackathonGUI(controller, (Organizzatore)utente));
            panel.add(creaHack);
        } else if (utente instanceof Giudice) {
            JButton valuta = new JButton("Valuta Team");
            valuta.addActionListener(e -> new ValutaTeamGUI(controller));
            panel.add(valuta);
        }

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> { new SignIn(controller); dispose(); });
        panel.add(logout);

        add(panel);
        pack();
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
}
