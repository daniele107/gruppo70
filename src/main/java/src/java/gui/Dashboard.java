// File: src/main/java/gui/Dashboard.java
package gui;

import controller.Controller;
import model.Giudice;
import model.Organizzatore;
import model.Partecipante;
import model.Utente;
import model.Hackathon;
import model.Team;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dashboard principale: mostra riepilogo e statistiche.
 */
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
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Overview", createOverviewPanel());
        tabs.addTab("Statistiche", createStatsPanel());
        tabs.addTab("Link", createLinksPanel());

        getContentPane().add(tabs);
        setVisible(true);
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        if (utente instanceof Partecipante) {
            List<Team> teams = controller.getTeams((Partecipante) utente);
            for (Team t : teams) {
                listModel.addElement("Team: " + t.toString());
            }
        } else if (utente instanceof Organizzatore) {
            List<Hackathon> hacks = controller.getHackathons((Organizzatore) utente);
            for (Hackathon h : hacks) {
                listModel.addElement("Hackathon: " + h.toString());
            }
        } else if (utente instanceof Giudice) {
            listModel.addElement("Nessun elemento disponibile.");
        }

        JList<String> list = new JList<>(listModel);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (utente instanceof Partecipante) {
            int cntTeams = controller.getTeams((Partecipante) utente).size();
            int cntInv = controller.getInviti((Partecipante) utente).size();
            panel.add(new JLabel("Numero team: " + cntTeams));
            panel.add(new JLabel("Inviti pendenti: " + cntInv));
            panel.add(new JLabel("Partecipazioni totali: " + cntTeams));
        } else if (utente instanceof Organizzatore) {
            int cntHacks = controller.getHackathons((Organizzatore) utente).size();
            int cntTeamsEval = controller.getTeamsToEvaluate().size();
            panel.add(new JLabel("Hackathon creati: " + cntHacks));
            panel.add(new JLabel("Team da valutare: " + cntTeamsEval));
            panel.add(new JLabel("Organizzatore attivo"));
        } else if (utente instanceof Giudice) {
            int cntVotes = controller.getVoti().size();
            int cntTeamsEval = controller.getTeamsToEvaluate().size();
            panel.add(new JLabel("Valutazioni effettuate: " + cntVotes));
            panel.add(new JLabel("Team da valutare: " + cntTeamsEval));
            panel.add(new JLabel("Giudice attivo"));
        }
        return panel;
    }

    private JPanel createLinksPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton profBtn = new JButton("Profilo");
        profBtn.addActionListener(e -> new ProfiloUtenteGUI(utente, controller).setVisible(true));
        panel.add(profBtn);

        JButton logoutB = new JButton("Logout");
        logoutB.addActionListener(e -> {
            dispose();
            new SignIn(controller).setVisible(true);
        });
        panel.add(logoutB);

        return panel;
    }
}
