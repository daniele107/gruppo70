// File: src/main/java/gui/MainMenuGUI.java
package gui;

import controller.Controller;
import model.Utente;
import model.Organizzatore;
import model.Giudice;
import model.Partecipante;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.Box;

/**
 * Finestra principale dell'applicazione,
 * stile simile alla schermata di login con sfondo gradiente,
 * e slider marquee più fluido.
 */
public class MainMenuGUI extends JFrame {
    private final Controller controller;
    private Utente currentUser;

    public MainMenuGUI(Controller controller) {
        // Look and Feel Nimbus
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        this.controller = controller;
        this.currentUser = controller.getCurrentUser();

        setTitle("Hackathon Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);

        buildUI();
    }

    private void buildUI() {
        GradientPanel container = new GradientPanel();
        container.setLayout(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titolo
        JLabel titleLabel = new JLabel("Hackathon Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        container.add(titleLabel, BorderLayout.NORTH);

        // Pulsanti
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(Box.createVerticalGlue());

        // Login e Registrazione sempre visibili
        addButton(buttonPanel, "Login", e -> swapToLogin());
        addButton(buttonPanel, "Registrazione", e -> swapToRegister());

        if (currentUser != null) {
            // Solo dopo login: Dashboard e altre funzioni
            addSeparator(buttonPanel);
            addButton(buttonPanel, "Dashboard", e -> new Dashboard(currentUser, controller).setVisible(true));
            addButton(buttonPanel, "Profilo", e -> new ProfiloUtenteGUI(currentUser, controller).setVisible(true));
            if (currentUser instanceof Organizzatore) {
                addButton(buttonPanel, "Crea Hackathon", e -> new CreaHackathonGUI(controller, (Organizzatore) currentUser).setVisible(true));
            }
            if (currentUser instanceof Partecipante) {
                addButton(buttonPanel, "Crea Team", e -> new CreaTeamGUI(controller, (Partecipante) currentUser).setVisible(true));
                addButton(buttonPanel, "I miei Inviti", e -> new InvitiPartecipanteGUI((Partecipante) currentUser, controller).setVisible(true));
            }
            if (currentUser instanceof Giudice) {
                addButton(buttonPanel, "Valuta Team", e -> new ValutaTeamGUI(controller).setVisible(true));
            }
            addSeparator(buttonPanel);
            addButton(buttonPanel, "Logout", e -> {
                controller.setCurrentUser(null);
                currentUser = null;
                buildUI();
            });
        }

        buttonPanel.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(buttonPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        container.add(scroll, BorderLayout.CENTER);

        // Marquee in basso
        MarqueePanel marqueePanel = new MarqueePanel(
                "Progetto Realizzato da Alessandro Minopoli, Daniele Megna e Simone Iodice - Federico II, prof. PORFIRIO TRAMONTANA"
        );
        container.add(marqueePanel, BorderLayout.SOUTH);

        setContentPane(container);
        revalidate(); repaint();
    }

    private void swapToLogin() {
        dispose();
        new SignIn(controller).setVisible(true);
    }

    private void swapToRegister() {
        dispose();
        new Registrazione(controller).setVisible(true);
    }

    private void addButton(JPanel panel, String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 60));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        button.setBackground(new Color(255, 255, 255, 220));
        button.setFocusPainted(false);
        button.addActionListener(action);
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    private void addSeparator(JPanel panel) {
        panel.add(Box.createVerticalStrut(15));
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(Color.WHITE);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(15));
    }

    // Panel con sfondo gradiente
    private static class GradientPanel extends JPanel {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth(), h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, new Color(10, 30, 90), 0, h, new Color(30, 144, 255));
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);
        }
    }

    // Panel per marquee fluido
    private static class MarqueePanel extends JPanel {
        private final String text;
        private int x;
        private final int speed = 2;
        public MarqueePanel(String text) {
            this.text = "   " + text + "   ";
            setPreferredSize(new Dimension(0, 30));
            setBackground(new Color(20, 20, 20));
            Timer timer = new Timer(25, e -> {
                x -= speed;
                if (x + getFontMetrics(getFont()).stringWidth(this.text) < 0) x = getWidth();
                repaint();
            });
            timer.start();
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.drawString(this.text, x, getHeight() - 8);
        }
    }
}
