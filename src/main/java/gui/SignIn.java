// File: SignIn.java
package gui;

import controller.Controller;
import model.Utente;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.KeyEvent;

public class SignIn extends JFrame {
    private final Controller controller;
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField pwdField = new JPasswordField(20);

    public SignIn(Controller controller) {
        super("Login - Hackathon Manager");
        this.controller = controller;
        initUI();
    }

    private void initUI() {
        // Look & Feel di sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Pannello principale con sfondo scuro
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(45, 62, 80));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titolo
        JLabel title = new JLabel("Benvenuto", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        mainPanel.add(title, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel emailLbl = new JLabel("Email:");
        emailLbl.setForeground(Color.WHITE);
        formPanel.add(emailLbl, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(emailField, gbc);

        gbc.gridy = 1; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel pwdLbl = new JLabel("Password:");
        pwdLbl.setForeground(Color.WHITE);
        formPanel.add(pwdLbl, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(pwdField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setOpaque(false);

        JButton loginBtn = createStyledButton("Accedi");
        // Imposta colore nero
        loginBtn.setBackground(Color.BLACK);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.addActionListener(e -> onLogin());
        btnPanel.add(loginBtn);

        JButton regBtn = createStyledButton("Registrati");
        // Imposta colore nero
        regBtn.setBackground(Color.BLACK);
        regBtn.setForeground(Color.WHITE);
        regBtn.addActionListener(e -> {
            dispose();
            new Registrazione(controller);
        });
        btnPanel.add(regBtn);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        // ESC key to return home
        getRootPane().registerKeyboardAction(e -> {
            dispose();
            new MainMenuGUI(controller);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private void onLogin() {
        String email = emailField.getText().trim();
        String pwd = new String(pwdField.getPassword());
        Utente u = controller.login(email, pwd);
        if (u == null) {
            JOptionPane.showMessageDialog(this,
                    "Credenziali non valide.", "Errore", JOptionPane.ERROR_MESSAGE);
        } else {
            dispose();
            new Dashboard(u, controller);
        }
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(120, 40));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new RoundedBorder(10));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        return btn;
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
