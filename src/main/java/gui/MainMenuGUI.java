// File: MainMenuGUI.java
package gui;

import controller.Controller;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

public class MainMenuGUI extends JFrame {
    public MainMenuGUI(Controller controller) {
        super("Hackathon Manager");
        initUI(controller);
    }

    private void initUI(Controller controller) {
        // Usa look-and-feel di sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Pannello principale
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(45, 62, 80));

        // Titolo
        JLabel title = new JLabel("Hackathon Manager", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // Pannello bottoni
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 100));

        // Etichette e azioni
        String[][] actions = {
                {"Login", "LOGIN"},
                {"Registrati", "REGISTER"},
                {"Esci", "EXIT"}
        };

        for (String[] act : actions) {
            String text = act[0];
            String cmd = act[1];
            JButton btn = createStyledButton(text);
            // Colora di nero i pulsanti Login e Registrati
            if ("Login".equals(text) || "Registrati".equals(text)) {
                btn.setBackground(Color.BLACK);
                btn.setForeground(Color.WHITE);
            }
            btn.setActionCommand(cmd);
            btn.addActionListener(e -> handleAction(e.getActionCommand(), controller));
            buttonPanel.add(btn);
            buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2025 Hackathon Manager", SwingConstants.CENTER);
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(footer, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 18));
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new RoundedBorder(10));
        btn.setOpaque(true);
        return btn;
    }

    private void handleAction(String cmd, Controller controller) {
        switch (cmd) {
            case "LOGIN":
                new SignIn(controller);
                break;
            case "REGISTER":
                new Registrazione(controller);
                break;
            case "EXIT":
                System.exit(0);
                break;
        }
        dispose();
    }

    // Bordo arrotondato per i bottoni
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        public RoundedBorder(int radius) {
            this.radius = radius;
        }
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
