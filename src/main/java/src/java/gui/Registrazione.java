
// File: src/main/java/gui/Registrazione.java
package gui;

import controller.Controller;
import model.Utente;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Registrazione extends JFrame {
    private final Controller controller;
    private final JTextField nomeField = new JTextField();
    private final JTextField cognomeField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JPasswordField pwdField = new JPasswordField();
    private final JComboBox<String> roleBox = new JComboBox<>(new String[]{"Partecipante","Organizzatore","Giudice"});

    public Registrazione(Controller controller) {
        super("Registrazione - Hackathon Manager");
        this.controller = controller;
        initUI();
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initUI() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        GradientPanel mainPanel = new GradientPanel(30, 45, 60);
        mainPanel.setLayout(new BorderLayout(20,20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));

        JLabel title = new JLabel("Registrazione", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0;
        JLabel nomeLbl = new JLabel("Nome:"); nomeLbl.setFont(new Font("Segoe UI", Font.PLAIN,16)); nomeLbl.setForeground(Color.WHITE);
        formPanel.add(nomeLbl,gbc);
        gbc.gridx=1;
        nomeField.setPreferredSize(new Dimension(240,36)); nomeField.setFont(new Font("Segoe UI", Font.PLAIN,16));
        formPanel.add(nomeField,gbc);

        gbc.gridx=0; gbc.gridy=1;
        JLabel cognomeLbl = new JLabel("Cognome:"); cognomeLbl.setFont(new Font("Segoe UI", Font.PLAIN,16)); cognomeLbl.setForeground(Color.WHITE);
        formPanel.add(cognomeLbl,gbc);
        gbc.gridx=1;
        cognomeField.setPreferredSize(new Dimension(240,36)); cognomeField.setFont(new Font("Segoe UI", Font.PLAIN,16));
        formPanel.add(cognomeField,gbc);

        gbc.gridx=0; gbc.gridy=2;
        JLabel emailLbl = new JLabel("Email:"); emailLbl.setFont(new Font("Segoe UI", Font.PLAIN,16)); emailLbl.setForeground(Color.WHITE);
        formPanel.add(emailLbl,gbc);
        gbc.gridx=1;
        emailField.setPreferredSize(new Dimension(240,36)); emailField.setFont(new Font("Segoe UI", Font.PLAIN,16));
        formPanel.add(emailField,gbc);

        gbc.gridx=0; gbc.gridy=3;
        JLabel pwdLbl = new JLabel("Password:"); pwdLbl.setFont(new Font("Segoe UI", Font.PLAIN,16)); pwdLbl.setForeground(Color.WHITE);
        formPanel.add(pwdLbl,gbc);
        gbc.gridx=1;
        pwdField.setPreferredSize(new Dimension(240,36)); pwdField.setFont(new Font("Segoe UI", Font.PLAIN,16));
        formPanel.add(pwdField,gbc);

        gbc.gridx=0; gbc.gridy=4;
        JLabel roleLbl = new JLabel("Ruolo:"); roleLbl.setFont(new Font("Segoe UI", Font.PLAIN,16)); roleLbl.setForeground(Color.WHITE);
        formPanel.add(roleLbl,gbc);
        gbc.gridx=1;
        roleBox.setPreferredSize(new Dimension(240,36)); roleBox.setFont(new Font("Segoe UI", Font.PLAIN,16));
        formPanel.add(roleBox,gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,10)); btnPanel.setOpaque(false);
        JButton regBtn = createStyledButton("Registrati"); regBtn.addActionListener(e -> onRegister());
        // Hover: green for reg
        Color regOrig = regBtn.getBackground();
        regBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { regBtn.setBackground(Color.GREEN); }
            @Override public void mouseExited(MouseEvent e) { regBtn.setBackground(regOrig); }
        });
        btnPanel.add(regBtn);

        JButton cancelBtn = createStyledButton("Annulla"); cancelBtn.addActionListener(e -> { dispose(); new SignIn(controller); });
        // Hover: red for cancel
        Color cancelOrig = cancelBtn.getBackground();
        cancelBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { cancelBtn.setBackground(Color.RED); }
            @Override public void mouseExited(MouseEvent e) { cancelBtn.setBackground(cancelOrig); }
        });
        btnPanel.add(cancelBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        getRootPane().registerKeyboardAction(e -> { dispose(); new SignIn(controller); },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        setContentPane(mainPanel);
    }

    private void onRegister() {
        String nome = nomeField.getText().trim(), cognome = cognomeField.getText().trim();
        String email = emailField.getText().trim(), pwd = new String(pwdField.getPassword());
        String ruolo = (String) roleBox.getSelectedItem();
        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tutti i campi sono obbligatori.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Utente u = controller.registraUtente(nome, cognome, email, pwd, ruolo);
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Email già utilizzata.", "Errore", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Registrazione avvenuta.", "Successo", JOptionPane.INFORMATION_MESSAGE);
            dispose(); new SignIn(controller);
        }
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(140, 44)); btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(new Color(52, 152, 219)); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setOpaque(true); btn.setContentAreaFilled(true);
        return btn;
    }

    private static class GradientPanel extends JPanel {
        private final Color start, end;
        public GradientPanel(int r,int g,int b) { start = new Color(r,g,b); end = new Color(20,30,50);}
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; int w = getWidth(), h = getHeight();
            g2.setPaint(new GradientPaint(0,0,start,0,h,end)); g2.fillRect(0,0,w,h);
        }
    }
}
