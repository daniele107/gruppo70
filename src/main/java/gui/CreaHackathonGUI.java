// File: CreaHackathonGUI.java
package gui;

import controller.Controller;
import model.Hackathon;
import model.Organizzatore;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CreaHackathonGUI extends JFrame {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final Controller controller;
    private final Organizzatore organizzatore;
    private final JTextField titleField = new JTextField(20);
    private final JTextField locationField = new JTextField(20);
    private final JTextField startField = new JTextField(20);
    private final JTextField endField = new JTextField(20);
    private final JSpinner maxParticipantsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
    private final JSpinner teamSizeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    public CreaHackathonGUI(Controller controller, Organizzatore org) {
        super("Crea Hackathon");
        this.controller = controller;
        this.organizzatore = org;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        addField(panel, gbc, 0, "Titolo:", titleField);
        addField(panel, gbc, 1, "Sede:", locationField);
        addField(panel, gbc, 2, "Inizio (yyyy-MM-dd'T'HH:mm):", startField);
        addField(panel, gbc, 3, "Fine (yyyy-MM-dd'T'HH:mm):", endField);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Max partecipanti:"), gbc);
        gbc.gridx = 1;
        panel.add(maxParticipantsSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Dimensione team:"), gbc);
        gbc.gridx = 1;
        panel.add(teamSizeSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JButton createButton = new JButton("Crea Hackathon");
        createButton.addActionListener(e -> onCreate());
        panel.add(createButton, gbc);

        add(panel);
        pack();
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void onCreate() {
        try {
            Hackathon hackathon = new Hackathon();
            hackathon.setTitolo(titleField.getText().trim());
            hackathon.setSede(locationField.getText().trim());
            hackathon.setDataInizio(LocalDateTime.parse(startField.getText().trim(), DATE_FORMAT));
            hackathon.setDataFine(LocalDateTime.parse(endField.getText().trim(), DATE_FORMAT));
            hackathon.setMassimoPartecipanti((Integer) maxParticipantsSpinner.getValue());
            hackathon.setDimensioneTeam((Integer) teamSizeSpinner.getValue());
            hackathon.setOrganizzatore(organizzatore);

            controller.creaHackathon(hackathon);
            JOptionPane.showMessageDialog(this, "Hackathon creato con successo.", "Successo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato data non valido. Usa yyyy-MM-dd'T'HH:mm.", "Errore di data", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
}