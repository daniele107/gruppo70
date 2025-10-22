package gui;
import controller.Controller;
import model.Utente;
import javax.swing.*;
import javax.swing.ScrollPaneConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
/**
 * Pannello di login moderno e responsive con design ottimizzato
 */
public class ModernLoginPanel extends JPanel {
    // Serialization
    private static final long serialVersionUID = 1L;
    // Constants
    private static final String FONT_UI = "Segoe UI";
    private static final String CARD_LOGIN = "login";
    private final transient Controller controller;
    private final ModernMainFrame mainFrame;
    // UI Components - WORKING MODERN FIELDS
    private FixedTextField usernameField;
    private FixedPasswordField passwordField;
    private ModernComponents.ModernButton loginButton;
    private ModernComponents.ModernButton registerButton;
    public ModernLoginPanel(Controller controller, ModernMainFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        initializeComponents();
        createResponsiveLayout();
        setupEventHandlers();
    }
    /**
     * Inizializza i componenti UI
     */
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setOpaque(false);
        // Modern fields that WORK
        usernameField = new FixedTextField("Nome utente");
        passwordField = new FixedPasswordField("Password");
        // Login button
        loginButton = new ModernComponents.ModernButton("Accedi");
        loginButton.setBackground(DesignSystem.PRIMARY_500);
        loginButton.setFont(new Font(FONT_UI, Font.BOLD, 16));
        // Register button
        registerButton = new ModernComponents.ModernButton("Crea Account");
        registerButton.setBackground(DesignSystem.SECONDARY_500);
        registerButton.setFont(new Font(FONT_UI, Font.BOLD, 14));
        // Add hover effects
        AnimationUtils.addHoverShadowEffect(loginButton);
        AnimationUtils.addHoverShadowEffect(registerButton);
    }
    /**
     * Crea il layout responsive
     */
    private void createResponsiveLayout() {
        // Main container with flexible sizing
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setOpaque(false);
        // Login card with flexible dimensions
        ModernComponents.ModernCard loginCard = new ModernComponents.ModernCard(new BorderLayout());
        loginCard.setElevation(3);
        // Header
        JPanel headerPanel = createHeaderPanel();
        // Content area with form fields
        JPanel contentPanel = createContentPanel();
        // Footer with register button
        JPanel footerPanel = createFooterPanel();
        // Assemble card
        loginCard.add(headerPanel, BorderLayout.NORTH);
        loginCard.add(contentPanel, BorderLayout.CENTER);
        loginCard.add(footerPanel, BorderLayout.SOUTH);
        // Set responsive preferred size ottimizzata per i campi centrati
        loginCard.setPreferredSize(new Dimension(450, 600));
        loginCard.setMinimumSize(new Dimension(400, 550));
        loginCard.setMaximumSize(new Dimension(500, 650));
        // Add card to main container
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        mainContainer.add(loginCard, gbc);
        add(mainContainer, BorderLayout.CENTER);
    }
    /**
     * Crea l'header del pannello
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));
        // Welcome title
        JLabel titleLabel = new JLabel("Bentornato!", SwingConstants.CENTER);
        titleLabel.setFont(new Font(FONT_UI, Font.BOLD, 28));
        titleLabel.setForeground(DesignSystem.getTextPrimary());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Subtitle
        JLabel subtitleLabel = new JLabel("Accedi al tuo account", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font(FONT_UI, Font.PLAIN, 14));
        subtitleLabel.setForeground(DesignSystem.getTextSecondary());
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(10));
        header.add(subtitleLabel);
        return header;
    }
    /**
     * Crea il contenuto principale con campi perfettamente centrati
     */
    private JPanel createContentPanel() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 40, 25, 40));
        // Container principale centrato per tutti gli elementi
        JPanel fieldsContainer = new JPanel();
        fieldsContainer.setLayout(new BoxLayout(fieldsContainer, BoxLayout.Y_AXIS));
        fieldsContainer.setOpaque(false);
        fieldsContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Username field perfettamente centrato
        JPanel usernamePanel = createCenteredFieldPanel("NOME UTENTE", usernameField);
        // Password field perfettamente centrato
        JPanel passwordPanel = createCenteredFieldPanel("PASSWORD", passwordField);
        // Login button perfettamente centrato
        JPanel loginButtonPanel = new JPanel();
        loginButtonPanel.setLayout(new BoxLayout(loginButtonPanel, BoxLayout.X_AXIS));
        loginButtonPanel.setOpaque(false);
        loginButtonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Set dimensioni ottimali per tutti i componenti
        usernameField.setPreferredSize(new Dimension(350, 50));
        usernameField.setMaximumSize(new Dimension(350, 50));
        passwordField.setPreferredSize(new Dimension(350, 50));
        passwordField.setMaximumSize(new Dimension(350, 50));
        loginButton.setPreferredSize(new Dimension(350, 50));
        loginButton.setMaximumSize(new Dimension(350, 50));
        loginButtonPanel.add(Box.createHorizontalGlue());
        loginButtonPanel.add(loginButton);
        loginButtonPanel.add(Box.createHorizontalGlue());
        // Assembla i componenti con spaziature ottimali
        fieldsContainer.add(usernamePanel);
        fieldsContainer.add(Box.createVerticalStrut(20));
        fieldsContainer.add(passwordPanel);
        fieldsContainer.add(Box.createVerticalStrut(30));
        fieldsContainer.add(loginButtonPanel);
        content.add(fieldsContainer);
        return content;
    }
    /**
     * Crea il footer con il bottone di registrazione
     */
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));
        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(DesignSystem.getBorderLight());
        // OR label
        JLabel orLabel = new JLabel("OPPURE", SwingConstants.CENTER);
        orLabel.setFont(new Font(FONT_UI, Font.BOLD, 12));
        orLabel.setForeground(DesignSystem.getTextSecondary());
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Register button panel
        JPanel registerPanel = new JPanel(new BorderLayout());
        registerPanel.setOpaque(false);
        registerPanel.add(registerButton, BorderLayout.CENTER);
        // Set register button size
        registerButton.setPreferredSize(new Dimension(320, 40));
        footer.add(separator);
        footer.add(Box.createVerticalStrut(15));
        footer.add(orLabel);
        footer.add(Box.createVerticalStrut(15));
        footer.add(registerPanel);
        return footer;
    }
    /**
     * Crea un pannello per un campo input perfettamente centrato
     */
    private JPanel createCenteredFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Label centrata
        JLabel label = new JLabel(labelText);
        label.setFont(new Font(FONT_UI, Font.BOLD, 14));
        label.setForeground(DesignSystem.getTextPrimary());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Campo centrato
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Container per centrare il campo orizzontalmente
        JPanel fieldContainer = new JPanel();
        fieldContainer.setLayout(new BoxLayout(fieldContainer, BoxLayout.X_AXIS));
        fieldContainer.setOpaque(false);
        fieldContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        fieldContainer.add(Box.createHorizontalGlue());
        fieldContainer.add(field);
        fieldContainer.add(Box.createHorizontalGlue());
        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
        panel.add(fieldContainer);
        return panel;
    }
    /**
     * Configura gli event handlers
     */
    private void setupEventHandlers() {
        // Login button action
        loginButton.addActionListener(e -> handleLogin());
        // Register button action
        registerButton.addActionListener(e -> handleRegister());
        // Enter key support
        Action loginAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        };
        usernameField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), CARD_LOGIN);
        usernameField.getActionMap().put(CARD_LOGIN, loginAction);
        passwordField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), CARD_LOGIN);
        passwordField.getActionMap().put(CARD_LOGIN, loginAction);
    }
    /**
     * Gestisce il login
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getPasswordText().trim();
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please fill in all fields", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Disable button during processing
        loginButton.setEnabled(false);
        loginButton.setText("🔄 Signing In...");
        // Simulate async processing
        SwingUtilities.invokeLater(() -> {
            try {
                boolean success = controller.login(username, password);
                if (success) {
                    Utente user = controller.getCurrentUser();
                    if (user != null) {
                        mainFrame.onLoginSuccess(user);
                        showMessage("Bentornato, " + user.getNome() + "!", "Accesso Riuscito", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    showMessage("Nome utente o password non validi", "Accesso Fallito", JOptionPane.ERROR_MESSAGE);
                    passwordField.setText("");
                }
            } catch (Exception ex) {
                showMessage("Accesso fallito: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                // Re-enable button
                loginButton.setEnabled(true);
                                    loginButton.setText("Accedi");
            }
        });
    }
    /**
     * Gestisce la registrazione
     */
    private void handleRegister() {
        RegisterDialog dialog = new RegisterDialog(mainFrame, controller);
        dialog.setVisible(true);
    }
    /**
     * Mostra un messaggio
     */
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    /**
     * Dialog per la registrazione
     */
    private static class RegisterDialog extends JDialog {
        private final transient Controller controller;
        private FixedTextField nameField;
        private FixedTextField emailField;
        private FixedTextField usernameField;
        private FixedPasswordField passwordField;
        private FixedPasswordField confirmPasswordField;
        private FixedComboBox<String> roleComboBox;
        private ModernComponents.ModernButton registerButton;
        private ModernComponents.ModernButton cancelButton;
        public RegisterDialog(Frame parent, Controller controller) {
            super(parent, "Crea Nuovo Account", true);
            this.controller = controller;
            initializeDialog();
            createDialogLayout();
            setupDialogHandlers();
        }
        private void initializeDialog() {
            // Dimensioni più ragionevoli per la finestra di registrazione
            setSize(450, 650);
            
            // Assicurati che la finestra non sia più grande dello schermo
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int maxWidth = (int) (screenSize.width * 0.9); // Massimo 90% della larghezza schermo
            int maxHeight = (int) (screenSize.height * 0.9); // Massimo 90% dell'altezza schermo
            
            if (getWidth() > maxWidth) setSize(maxWidth, getHeight());
            if (getHeight() > maxHeight) setSize(getWidth(), maxHeight);
            
            setLocationRelativeTo(getParent());
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(true); // Permetti il ridimensionamento se necessario
            // Initialize modern working fields
            nameField = new FixedTextField("Inserisci il tuo nome completo");
            emailField = new FixedTextField("Inserisci la tua email");
            usernameField = new FixedTextField("Scegli un nome utente");
            passwordField = new FixedPasswordField("Crea una password");
            confirmPasswordField = new FixedPasswordField("Conferma la tua password");
            // Role selection combo box with descriptions
            String[] roles = {
                "PARTECIPANTE - Partecipa agli hackathon",
                "ORGANIZZATORE - Organizza e gestisce hackathon", 
                "GIUDICE - Valuta i progetti degli hackathon"
            };
            roleComboBox = new FixedComboBox<>(roles);
            roleComboBox.setSelectedIndex(0); // Default to PARTECIPANTE
            registerButton = new ModernComponents.ModernButton("Crea Account");
            registerButton.setBackground(DesignSystem.PRIMARY_500);
            registerButton.setFont(new Font(FONT_UI, Font.BOLD, 14));
            cancelButton = new ModernComponents.ModernButton("Annulla");
            cancelButton.setBackground(DesignSystem.NEUTRAL_400);
            cancelButton.setFont(new Font(FONT_UI, Font.PLAIN, 14));
        }
        private void createDialogLayout() {
            setLayout(new BorderLayout());
            setBackground(DesignSystem.getBackgroundPrimary());
            
            // Create main panel
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBackground(DesignSystem.getBackgroundPrimary());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
            // Title section
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
            titlePanel.setOpaque(false);
            JLabel titleLabel = new JLabel("Crea Nuovo Account", SwingConstants.CENTER);
            titleLabel.setFont(new Font(FONT_UI, Font.BOLD, 26));
            titleLabel.setForeground(DesignSystem.getTextPrimary());
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel subtitleLabel = new JLabel("Unisciti ad Hackathon Manager oggi", SwingConstants.CENTER);
            subtitleLabel.setFont(new Font(FONT_UI, Font.PLAIN, 14));
            subtitleLabel.setForeground(DesignSystem.getTextSecondary());
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titlePanel.add(titleLabel);
            titlePanel.add(Box.createVerticalStrut(8));
            titlePanel.add(subtitleLabel);
            // Form section centrata
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setOpaque(false);
            formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            // Add fields with proper spacing e centraggio perfetto
            addFieldToFormPanel(formPanel, "NOME COMPLETO", nameField);
            addFieldToFormPanel(formPanel, "INDIRIZZO EMAIL", emailField);
            addFieldToFormPanel(formPanel, "NOME UTENTE", usernameField);
            addFieldToFormPanel(formPanel, "PASSWORD", passwordField);
            addFieldToFormPanel(formPanel, "CONFERMA PASSWORD", confirmPasswordField);
            addFieldToFormPanel(formPanel, "RUOLO", roleComboBox);
            // Button section centrata
            JPanel buttonPanel = createButtonPanel();
            // Assemble main panel con centraggio ottimale
            mainPanel.add(titlePanel);
            mainPanel.add(Box.createVerticalStrut(30));
            mainPanel.add(formPanel);
            mainPanel.add(Box.createVerticalStrut(25));
            mainPanel.add(buttonPanel);
            // Add scroll capability for small screens
            JScrollPane scrollPane = new JScrollPane(mainPanel);
            scrollPane.setBorder(null);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            add(scrollPane, BorderLayout.CENTER);
        }
        private void addFieldToFormPanel(JPanel parent, String label, JComponent field) {
            JPanel fieldPanel = new JPanel();
            fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
            fieldPanel.setOpaque(false);
            fieldPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            // Label centrata
            JLabel fieldLabel = new JLabel(label);
            fieldLabel.setFont(new Font(FONT_UI, Font.BOLD, 13));
            fieldLabel.setForeground(DesignSystem.getTextPrimary());
            fieldLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            // Container per centrare il campo orizzontalmente
            JPanel fieldContainer = new JPanel();
            fieldContainer.setLayout(new BoxLayout(fieldContainer, BoxLayout.X_AXIS));
            fieldContainer.setOpaque(false);
            fieldContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
            // Set dimensioni consistenti e centrate
            field.setPreferredSize(new Dimension(380, 45));
            field.setMaximumSize(new Dimension(380, 45));
            field.setAlignmentX(Component.CENTER_ALIGNMENT);
            fieldContainer.add(Box.createHorizontalGlue());
            fieldContainer.add(field);
            fieldContainer.add(Box.createHorizontalGlue());
            fieldPanel.add(fieldLabel);
            fieldPanel.add(Box.createVerticalStrut(8));
            fieldPanel.add(fieldContainer);
            parent.add(fieldPanel);
            parent.add(Box.createVerticalStrut(20));
        }
        private JPanel createButtonPanel() {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.setOpaque(false);
            buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            // Set button sizes più consistenti
            cancelButton.setPreferredSize(new Dimension(160, 45));
            cancelButton.setMaximumSize(new Dimension(160, 45));
            registerButton.setPreferredSize(new Dimension(180, 45));
            registerButton.setMaximumSize(new Dimension(180, 45));
            // Centra i bottoni perfettamente
            buttonPanel.add(Box.createHorizontalGlue());
            buttonPanel.add(cancelButton);
            buttonPanel.add(Box.createHorizontalStrut(20));
            buttonPanel.add(registerButton);
            buttonPanel.add(Box.createHorizontalGlue());
            return buttonPanel;
        }
        private void setupDialogHandlers() {
            cancelButton.addActionListener(e -> dispose());
            registerButton.addActionListener(e -> {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String username = usernameField.getText().trim();
                String password = passwordField.getPasswordText().trim();
                String confirmPassword = confirmPasswordField.getPasswordText().trim();
                if (name.isEmpty() || email.isEmpty() || username.isEmpty() || 
                    password.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Compila tutti i campi", 
                        "Campi Richiesti", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(this, "Le password non coincidono", 
                        "Password Non Corrispondenti", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    // Split name into first and last name for the controller
                    String[] nameParts = name.split(" ", 2);
                    String firstName = nameParts[0];
                    String lastName = nameParts.length > 1 ? nameParts[1] : "";
                    // Get selected role and extract the role name (before the " - ")
                    String selectedRoleText = (String) roleComboBox.getSelectedItem();
                    String selectedRole = selectedRoleText.split(" - ")[0];
                    boolean success = controller.registraUtente(username, password, firstName, lastName, email, selectedRole);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Account creato con successo!\nOra puoi accedere.", 
                            "Registrazione Riuscita", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Registrazione fallita. Il nome utente potrebbe già esistere.", 
                            "Registrazione Fallita", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Errore di registrazione: " + ex.getMessage(), 
                        "Errore", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
        }
    }
}
