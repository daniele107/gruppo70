// File: Registrazione.java
package gui;

import controller.Controller;
import model.Utente;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Registrazione extends JFrame {
    private final Controller controller;
    private final JTextField nomeField = new JTextField(15);
    private final JTextField cognomeField = new JTextField(15);
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField pwdField = new JPasswordField(20);
    private final JComboBox<String> roleBox = new JComboBox<>(
            new String[]{"Partecipante","Organizzatore","Giudice"});

    public Registrazione(Controller controller) {
        super("Registrazione - Hackathon Manager");
        this.controller = controller;
        initUI();
    }

    private void initUI() {
        // Look & Feel di sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);

        // ESC torna alla home
        getRootPane().registerKeyboardAction(e -> {
            dispose();
            new MainMenuGUI(controller);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(45, 62, 80));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Registrazione", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nomeLbl = new JLabel("Nome:"); nomeLbl.setForeground(Color.WHITE);
        formPanel.add(nomeLbl, gbc);
        gbc.gridx = 1;
        formPanel.add(nomeField, gbc);

        gbc.gridy = 1; gbc.gridx = 0;
        JLabel cognomeLbl = new JLabel("Cognome:"); cognomeLbl.setForeground(Color.WHITE);
        formPanel.add(cognomeLbl, gbc);
        gbc.gridx = 1;
        formPanel.add(cognomeField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        JLabel emailLbl = new JLabel("Email:"); emailLbl.setForeground(Color.WHITE);
        formPanel.add(emailLbl, gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        JLabel pwdLbl = new JLabel("Password:"); pwdLbl.setForeground(Color.WHITE);
        formPanel.add(pwdLbl, gbc);
        gbc.gridx = 1;
        formPanel.add(pwdField, gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        JLabel roleLbl = new JLabel("Ruolo:"); roleLbl.setForeground(Color.WHITE);
        formPanel.add(roleLbl, gbc);
        gbc.gridx = 1;
        formPanel.add(roleBox, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setOpaque(false);

        JButton regBtn = createStyledButton("Registrati");
        regBtn.addActionListener(e -> onRegister());
        btnPanel.add(regBtn);

        JButton cancelBtn = createStyledButton("Annulla");
        // Rendi il background colorabile
        cancelBtn.setOpaque(true);
        cancelBtn.setContentAreaFilled(true);
        // Non disegnare il bordo per mostrare il rosso pieno
        cancelBtn.setBorderPainted(false);
        Color defaultColor = cancelBtn.getBackground();
        cancelBtn.addActionListener(e -> {
            dispose();
            new SignIn(controller);
        });
        cancelBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancelBtn.setBackground(new Color(231, 76, 60));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                cancelBtn.setBackground(defaultColor);
            }
        });
        btnPanel.add(cancelBtn);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(new RoundedBorder(10));
        return btn;
    }

    private void onRegister() {
        String nome = nomeField.getText().trim();
        String cognome = cognomeField.getText().trim();
        String email = emailField.getText().trim();
        String pwd = new String(pwdField.getPassword());
        String ruolo = (String) roleBox.getSelectedItem();

        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tutti i campi sono obbligatori.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Utente u = controller.registraUtente(nome, cognome, email, pwd, ruolo);
        if (u == null) {
            JOptionPane.showMessageDialog(this,
                    "Email già utilizzata.", "Errore", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Registrazione avvenuta.", "Successo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new SignIn(controller);
        }
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
