package gui;

import controller.Controller;
import model.Utente;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ProfiloUtenteGUI extends JFrame {
    private final Utente utente;
    private final Controller controller;
    private final JTextField nomeField;
    private final JTextField cognomeField;
    private final JTextField dataField;
    private final JTextField emailField;
    private final JPasswordField pwdField;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    public ProfiloUtenteGUI(Utente u, Controller controller) {
        super("Profilo Utente");
        this.utente = u;
        this.controller = controller;

        nomeField = new JTextField(u.getNome(), 15);
        cognomeField = new JTextField(u.getCognome(), 15);
        dataField = new JTextField(u.getDataNascita()!=null ? u.getDataNascita().format(DATE_FORMAT) : "", 10);
        emailField = new JTextField(u.getEmail(), 20);
        pwdField = new JPasswordField(u.getPassword(), 20);

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        int row = 0;

        gbc.gridx=0; gbc.gridy=row; panel.add(new JLabel("Nome:"), gbc);
        gbc.gridx=1; panel.add(nomeField, gbc);

        gbc.gridy=++row; gbc.gridx=0; panel.add(new JLabel("Cognome:"), gbc);
        gbc.gridx=1; panel.add(cognomeField, gbc);

        gbc.gridy=++row; gbc.gridx=0; panel.add(new JLabel("Data di nascita (YYYY-MM-DD):"), gbc);
        gbc.gridx=1; panel.add(dataField, gbc);

        gbc.gridy=++row; gbc.gridx=0; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx=1; panel.add(emailField, gbc);

        gbc.gridy=++row; gbc.gridx=0; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx=1; panel.add(pwdField, gbc);

        gbc.gridy=++row; gbc.gridx=0; gbc.gridwidth=2;
        JButton save = new JButton("Salva modifiche");
        save.addActionListener(e -> onSave());
        panel.add(save, gbc);

        add(panel);
        pack();
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    private void onSave() {
        try {
            LocalDate data = LocalDate.parse(dataField.getText().trim(), DATE_FORMAT);
            controller.aggiornaUtente(utente,
                nomeField.getText().trim(),
                cognomeField.getText().trim(),
                data,
                emailField.getText().trim(),
                new String(pwdField.getPassword())
            );
            JOptionPane.showMessageDialog(this,
                "Profilo aggiornato.", "Successo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                "Formato data non valido. Usa YYYY-MM-DD.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}
