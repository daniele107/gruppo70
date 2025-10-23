package gui;
import controller.Controller;
import model.Utente;
import javax.swing.*;
import java.awt.*;
import static javax.swing.SwingConstants.*;
/**
 * Pannello per la gestione del profilo utente.
 */
public class UtentePanel extends JPanel {
    private static final String CAMBIA_PASSWORD_TEXT = "Cambia Password";
    private final transient Controller controller;
    private final MainFrame mainFrame;
    // Components
    private JLabel nomeLabel;
    private JLabel cognomeLabel;
    private JLabel emailLabel;
    private JLabel ruoloLabel;
    private JButton modificaProfiloButton;
    private JButton cambiaPasswordButton;
    /**
     * Costruttore che inizializza il pannello utente
     *
     * @param controller il controller dell'applicazione
     * @param mainFrame  il frame principale
     */
    public UtentePanel(Controller controller, MainFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    /**
     * Inizializza i componenti del pannello
     */
    private void initializeComponents() {
        // Labels for user info
        nomeLabel = new JLabel("Nome: ");
        cognomeLabel = new JLabel("Cognome: ");
        emailLabel = new JLabel("Email: ");
        ruoloLabel = new JLabel("Ruolo: ");
        // Buttons
        modificaProfiloButton = new JButton("Modifica Profilo");
        cambiaPasswordButton = new JButton(CAMBIA_PASSWORD_TEXT);
        
        // Style buttons
        modificaProfiloButton.setBackground(new Color(52, 152, 219)); // Blu
        modificaProfiloButton.setForeground(Color.WHITE);
        modificaProfiloButton.setFocusPainted(false);
        modificaProfiloButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        cambiaPasswordButton.setBackground(new Color(46, 204, 113)); // Verde
        cambiaPasswordButton.setForeground(Color.WHITE);
        cambiaPasswordButton.setFocusPainted(false);
        cambiaPasswordButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }
    /**
     * Configura il layout del pannello
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        // Title
        JLabel titleLabel = new JLabel("Profilo Utente", CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // User info panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informazioni Utente"));
        infoPanel.add(new JLabel("Nome:"));
        infoPanel.add(nomeLabel);
        infoPanel.add(new JLabel("Cognome:"));
        infoPanel.add(cognomeLabel);
        infoPanel.add(new JLabel("Email:"));
        infoPanel.add(emailLabel);
        infoPanel.add(new JLabel("Ruolo:"));
        infoPanel.add(ruoloLabel);
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(infoPanel, gbc);
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Azioni"));
        buttonsPanel.add(modificaProfiloButton);
        buttonsPanel.add(cambiaPasswordButton);
        gbc.gridy = 1;
        contentPanel.add(buttonsPanel, gbc);
        add(contentPanel, BorderLayout.CENTER);
    }
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        // Modify profile button
        modificaProfiloButton.addActionListener(e -> showModificaProfiloDialog());
        // Change password button
        cambiaPasswordButton.addActionListener(e -> showCambiaPasswordDialog());
    }
    /**
     * Aggiorna i dati del pannello
     */
    public void refreshData() {
        Utente currentUser = controller.getCurrentUser();
        if (currentUser != null) {
            nomeLabel.setText(currentUser.getNome());
            cognomeLabel.setText(currentUser.getCognome());
            emailLabel.setText(currentUser.getEmail());
            ruoloLabel.setText(currentUser.getRuolo());
        }
    }
    /**
     * Mostra il dialog per modificare il profilo
     */
    private void showModificaProfiloDialog() {
        Utente currentUser = controller.getCurrentUser();
        if (currentUser == null) {
            mainFrame.showError("Utente non autenticato");
            return;
        }
        JDialog dialog = new JDialog(mainFrame, "Modifica Profilo", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Form fields
        JTextField nomeField = new JTextField(currentUser.getNome(), 20);
        JTextField cognomeField = new JTextField(currentUser.getCognome(), 20);
        JTextField emailField = new JTextField(currentUser.getEmail(), 20);
        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        dialog.add(nomeField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Cognome:"), gbc);
        gbc.gridx = 1;
        dialog.add(cognomeField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton confirmButton = new JButton("Salva");
        JButton cancelButton = new JButton("Annulla");
        
        // Style buttons
        confirmButton.setBackground(new Color(52, 152, 219)); // Blu
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        cancelButton.setBackground(new Color(231, 76, 60)); // Rosso
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        confirmButton.addActionListener(e -> {
            String nome = nomeField.getText().trim();
            String cognome = cognomeField.getText().trim();
            String email = emailField.getText().trim();
            if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty()) {
                mainFrame.showError("Tutti i campi sono obbligatori");
                return;
            }
            if (!email.contains("@")) {
                mainFrame.showError("Inserisci un'email valida");
                return;
            }
            // Implementazione placeholder per aggiornamento utente
            mainFrame.showInfo("Profilo aggiornato con successo!");
            dialog.dispose();
            refreshData();
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }
    /**
     * Mostra il dialog per cambiare password
     */
    private void showCambiaPasswordDialog() {
        JDialog dialog = new JDialog(mainFrame, CAMBIA_PASSWORD_TEXT, true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Form fields
        JPasswordField vecchiaPasswordField = new JPasswordField(20);
        JPasswordField nuovaPasswordField = new JPasswordField(20);
        JPasswordField confermaPasswordField = new JPasswordField(20);
        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Password Attuale:"), gbc);
        gbc.gridx = 1;
        dialog.add(vecchiaPasswordField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Nuova Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(nuovaPasswordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Conferma Password:"), gbc);
        gbc.gridx = 1;
        dialog.add(confermaPasswordField, gbc);
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton confirmButton = new JButton(CAMBIA_PASSWORD_TEXT);
        JButton cancelButton = new JButton("Annulla");
        
        // Style buttons
        confirmButton.setBackground(new Color(52, 152, 219)); // Blu
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        cancelButton.setBackground(new Color(231, 76, 60)); // Rosso
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        confirmButton.addActionListener(e -> {
            String vecchiaPassword = new String(vecchiaPasswordField.getPassword());
            String nuovaPassword = new String(nuovaPasswordField.getPassword());
            String confermaPassword = new String(confermaPasswordField.getPassword());
            if (vecchiaPassword.isEmpty() || nuovaPassword.isEmpty() || confermaPassword.isEmpty()) {
                mainFrame.showError("Tutti i campi sono obbligatori");
                return;
            }
            if (!nuovaPassword.equals(confermaPassword)) {
                mainFrame.showError("Le password non coincidono");
                return;
            }
            if (nuovaPassword.length() < 6) {
                mainFrame.showError("La password deve essere di almeno 6 caratteri");
                return;
            }
            // Implementazione placeholder per cambio password
            mainFrame.showInfo("Password cambiata con successo!");
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }
} 
