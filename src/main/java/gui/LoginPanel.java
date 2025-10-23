package gui;
import controller.Controller;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import static javax.swing.SwingConstants.*;
/**
 * Pannello per l'autenticazione e registrazione degli utenti.
 * Implementa un design moderno seguendo gli standard Swing Hacks.
 */
public class LoginPanel extends JPanel {
    private final transient Controller controller;
    private final transient MainFrame mainFrame;
    // Login components
    private transient JTextField loginField;
    private transient JPasswordField passwordField;
    private transient JButton loginButton;
    private transient JButton registerButton;
    // Register components
    private transient JTextField registerLoginField;
    private transient JPasswordField registerPasswordField;
    private transient JTextField nomeField;
    private transient JTextField cognomeField;
    private transient JTextField emailField;
    private transient JComboBox<String> ruoloComboBox;
    private transient JButton confirmRegisterButton;
    private transient JButton backToLoginButton;
    // Panel switching
    private transient CardLayout cardLayout;
    // Register panel reference
    private transient RegisterPanel registerPanelModern;
    // Modern styling
    private static final String FONT_FAMILY = "Segoe UI";
    private static final String USERNAME_TEXT = "Username";
    private static final String PASSWORD_TEXT = "Password";
    private static final String COGNOME_TEXT = "Cognome";
    private static final String EMAIL_TEXT = "Email";
    // Card layout constants
    private static final String CARD_LOGIN = "LOGIN";
    private static final String CARD_REGISTER = "REGISTER";
    private static final Color GRADIENT_START = new Color(52, 152, 219);
    private static final Color GRADIENT_END = new Color(155, 89, 182);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(52, 73, 94);
    private static final Color PLACEHOLDER_COLOR = new Color(149, 165, 166);
    private static final Color BORDER_COLOR = new Color(189, 195, 199);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    /**
     * Costruttore che inizializza il pannello di login
     *
     * @param controller il controller dell'applicazione
     * @param mainFrame  il frame principale
     */
    public LoginPanel(Controller controller, MainFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(MainFrame.getBackgroundColor());
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    /**
     * Inizializza i componenti del pannello
     */
    private void initializeComponents() {
        // Login components
        loginField = createStyledTextField(USERNAME_TEXT);
        passwordField = createStyledPasswordField(PASSWORD_TEXT);
        loginButton = createStyledButton("🔐 Accedi", SUCCESS_COLOR);
        registerButton = createStyledButton("📝 Registrati", MainFrame.getPrimaryColor());
        // Register components
        registerLoginField = createStyledTextField(USERNAME_TEXT);
        registerPasswordField = createStyledPasswordField(PASSWORD_TEXT);
        nomeField = createStyledTextField("Nome");
        cognomeField = createStyledTextField(COGNOME_TEXT);
        emailField = createStyledTextField(EMAIL_TEXT);
        ruoloComboBox = createStyledComboBox(new String[]{"PARTECIPANTE", "GIUDICE", "ORGANIZZATORE"});
        confirmRegisterButton = createStyledButton("✅ Conferma Registrazione", SUCCESS_COLOR);
        backToLoginButton = createStyledButton("⬅️ Torna al Login", MainFrame.getPrimaryColor());
        // Card layout for switching between login and register
        cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        JPanel loginPanel = createModernLoginPanel();
        JPanel registerPanel = createModernRegisterPanel();
        // Create modern register panel
        registerPanelModern = new RegisterPanel(mainFrame);
        cardPanel.add(loginPanel, CARD_LOGIN);
        cardPanel.add(registerPanel, CARD_REGISTER);
        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, CARD_LOGIN);
    }
    /**
     * Crea un campo di testo stilizzato
     */
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
        field.setBackground(CARD_BACKGROUND);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        field.setPreferredSize(new Dimension(300, 45));
        // Placeholder effect
        field.setText(placeholder);
        field.setForeground(PLACEHOLDER_COLOR);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                }
                // Focus state con animazione
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(MainFrame.getPrimaryColor(), 2, true),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)
                ));
                // Effetto glow
                field.setBackground(new Color(248, 250, 252));
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(PLACEHOLDER_COLOR);
                    field.setText(placeholder);
                }
                // Ripristina stato normale con transizione
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
                field.setBackground(CARD_BACKGROUND);
            }
        });
        return field;
    }
    /**
     * Crea un campo password stilizzato
     */
    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
        field.setBackground(CARD_BACKGROUND);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        field.setPreferredSize(new Dimension(300, 45));
        // Placeholder effect
        field.setEchoChar((char) 0);
        field.setText(placeholder);
        field.setForeground(PLACEHOLDER_COLOR);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('•');
                    field.setForeground(TEXT_COLOR);
                }
                // Effetto bordo colorato
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(MainFrame.getPrimaryColor(), 2, true),
                    BorderFactory.createEmptyBorder(10, 14, 10, 14)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0);
                    field.setForeground(PLACEHOLDER_COLOR);
                    field.setText(placeholder);
                }
                // Ripristina bordo normale
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }
        });
        return field;
    }
    /**
     * Crea un bottone stilizzato
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        button.setForeground(Color.BLACK);
        button.setBackground(backgroundColor);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 45));
        // Micro-interazioni moderne con feedback tattile
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Effetto hover con scala
                button.setBackground(backgroundColor.darker());
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(backgroundColor.darker().darker(), 2),
                    BorderFactory.createEmptyBorder(10, 22, 10, 22)
                ));
                // Feedback visivo immediato
                button.setFont(new Font(FONT_FAMILY, Font.BOLD, 15));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
                button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
                button.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                // Feedback tattile - effetto "pressed"
                button.setBackground(backgroundColor.darker().darker());
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(backgroundColor.darker().darker().darker(), 3),
                    BorderFactory.createEmptyBorder(11, 23, 11, 23)
                ));
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(backgroundColor.darker().darker(), 2),
                    BorderFactory.createEmptyBorder(10, 22, 10, 22)
                ));
            }
        });
        return button;
    }
    /**
     * Crea un combo box stilizzato
     */
    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBackground(CARD_BACKGROUND);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        comboBox.setPreferredSize(new Dimension(300, 45));
        return comboBox;
    }
    /**
     * Crea il pannello di login moderno
     */
    private JPanel createModernLoginPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        // Header con gradiente e ombra avanzata
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Ombra multipla per effetto 3D
                for (int i = 0; i < 3; i++) {
                    g2d.setColor(new Color(0, 0, 0, 20 - i * 5));
                    g2d.fillRoundRect(3 + i, 3 + i, getWidth()-6-i*2, getHeight()-6-i*2, 15, 15);
                }
                // Gradiente con più punti di controllo
                GradientPaint gradient = new GradientPaint(0, 0, GRADIENT_START, getWidth(), getHeight(), GRADIENT_END);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth()-3, getHeight()-3, 15, 15);
                // Highlight per effetto glass
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRoundRect(0, 0, getWidth()-3, (getHeight()-3)/2, 15, 15);
                g2d.dispose();
            }
        };
        headerPanel.setPreferredSize(new Dimension(400, 120));
        headerPanel.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("🚀 Hackathon Manager", CENTER);
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
        JLabel subtitleLabel = new JLabel("Sistema di Gestione Eventi", CENTER);
        subtitleLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        // Card centrale
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_BACKGROUND);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        cardPanel.setMaximumSize(new Dimension(400, 500));
        cardPanel.setPreferredSize(new Dimension(400, 500));
        // Contenuto della card
        JLabel welcomeLabel = new JLabel("👋 Benvenuto!", CENTER);
        welcomeLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 20));
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        JLabel loginLabel = new JLabel("Accedi al tuo account", CENTER);
        loginLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        loginLabel.setForeground(PLACEHOLDER_COLOR);
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        // Form fields
        loginField.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginField.setBorder(new EmptyBorder(0, 0, 15, 0));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(new EmptyBorder(0, 0, 25, 0));
        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBorder(new EmptyBorder(0, 0, 15, 0));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        // Link alla registrazione moderna
        JLabel registerLink = new JLabel("Non hai un account? Registrati", CENTER);
        registerLink.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        registerLink.setForeground(MainFrame.getPrimaryColor());
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLink.setBorder(new EmptyBorder(20, 0, 0, 0));
        // Hover effect per il link
        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                registerLink.setText("<html><u>Non hai un account? Registrati</u></html>");
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                registerLink.setText("Non hai un account? Registrati");
            }
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                LoginPanel.this.showModernRegisterPanel();
            }
        });
        buttonPanel.add(registerLink);
        // Assembla la card
        cardPanel.add(welcomeLabel);
        cardPanel.add(loginLabel);
        cardPanel.add(loginField);
        cardPanel.add(passwordField);
        cardPanel.add(buttonPanel);
        // Centra la card
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        centerPanel.add(cardPanel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        return mainPanel;
    }
    /**
     * Crea il pannello di registrazione moderno
     */
    private JPanel createModernRegisterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        // Header con gradiente
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, GRADIENT_START, getWidth(), getHeight(), GRADIENT_END);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        headerPanel.setPreferredSize(new Dimension(400, 100));
        headerPanel.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("📝 Registrazione", CENTER);
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        // Scrollable card
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_BACKGROUND);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(25, 35, 25, 35)
        ));
        cardPanel.setMaximumSize(new Dimension(450, 600));
        cardPanel.setPreferredSize(new Dimension(450, 600));
        // Contenuto della card
        JLabel registerLabel = new JLabel("Crea il tuo account", CENTER);
        registerLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 18));
        registerLabel.setForeground(TEXT_COLOR);
        registerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLabel.setBorder(new EmptyBorder(0, 0, 25, 0));
        // Form fields
        registerLoginField.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLoginField.setBorder(new EmptyBorder(0, 0, 15, 0));
        registerPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerPasswordField.setBorder(new EmptyBorder(0, 0, 15, 0));
        nomeField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nomeField.setBorder(new EmptyBorder(0, 0, 15, 0));
        cognomeField.setAlignmentX(Component.CENTER_ALIGNMENT);
        cognomeField.setBorder(new EmptyBorder(0, 0, 15, 0));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setBorder(new EmptyBorder(0, 0, 15, 0));
        ruoloComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        ruoloComboBox.setBorder(new EmptyBorder(0, 0, 25, 0));
        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        confirmRegisterButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmRegisterButton.setBorder(new EmptyBorder(0, 0, 15, 0));
        backToLoginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(confirmRegisterButton);
        buttonPanel.add(backToLoginButton);
        // Assembla la card
        cardPanel.add(registerLabel);
        cardPanel.add(registerLoginField);
        cardPanel.add(registerPasswordField);
        cardPanel.add(nomeField);
        cardPanel.add(cognomeField);
        cardPanel.add(emailField);
        cardPanel.add(ruoloComboBox);
        cardPanel.add(buttonPanel);
        // Scroll pane per gestire il contenuto
        JScrollPane scrollPane = new JScrollPane(cardPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        // Centra il contenuto
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        centerPanel.add(scrollPane);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        return mainPanel;
    }
    /**
     * Configura il layout del pannello
     */
    private void setupLayout() {
        setPreferredSize(new Dimension(800, 600));
        // Listener per ridimensionamento responsive
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateResponsiveLayout();
            }
        });
    }
    /**
     * Aggiorna il layout responsive del login
     */
    private void updateResponsiveLayout() {
        int width = getWidth();
        if (width < 600) {
            // Layout compatto per schermi piccoli
            loginField.setPreferredSize(new Dimension(width - 100, 40));
            passwordField.setPreferredSize(new Dimension(width - 100, 40));
            loginButton.setPreferredSize(new Dimension(width - 150, 40));
            registerButton.setPreferredSize(new Dimension(width - 150, 40));
        } else {
            // Layout normale per schermi grandi
            loginField.setPreferredSize(new Dimension(300, 45));
            passwordField.setPreferredSize(new Dimension(300, 45));
            loginButton.setPreferredSize(new Dimension(200, 45));
            registerButton.setPreferredSize(new Dimension(200, 45));
        }
        revalidate();
        repaint();
    }
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        // Login button
        loginButton.addActionListener(e -> handleLogin());
        // Register button
        registerButton.addActionListener(e -> 
            cardLayout.show((JPanel) getComponent(0), CARD_REGISTER)
        );
        // Confirm register button
        confirmRegisterButton.addActionListener(e -> handleRegister());
        // Back to login button
        backToLoginButton.addActionListener(e -> {
            cardLayout.show((JPanel) getComponent(0), CARD_LOGIN);
            clearRegisterFields();
        });
        // Enter key on password field for login
        passwordField.addActionListener(e -> handleLogin());
    }
    /**
     * Gestisce il tentativo di login
     */
    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = new String(passwordField.getPassword());
        // Controlla se sono placeholder
        if (login.equals(USERNAME_TEXT) || password.equals(PASSWORD_TEXT)) {
            mainFrame.showError("Inserisci login e password");
            return;
        }
        if (login.isEmpty() || password.isEmpty()) {
            mainFrame.showError("Inserisci login e password");
            return;
        }
        if (controller.login(login, password)) {
            mainFrame.handleLogin();
            clearLoginFields();
        } else {
            mainFrame.showError("Login o password non corretti");
            passwordField.setText("");
            passwordField.setEchoChar('•');
        }
    }
    /**
     * Gestisce il tentativo di registrazione
     */
    private void handleRegister() {
        String login = registerLoginField.getText().trim();
        String password = new String(registerPasswordField.getPassword());
        String nome = nomeField.getText().trim();
        String cognome = cognomeField.getText().trim();
        String email = emailField.getText().trim();
        String ruolo = (String) ruoloComboBox.getSelectedItem();
        // Controlla se sono placeholder
        if (login.equals(USERNAME_TEXT) || password.equals(PASSWORD_TEXT) || 
            nome.equals("Nome") || cognome.equals(COGNOME_TEXT) || email.equals(EMAIL_TEXT)) {
            mainFrame.showError("Tutti i campi sono obbligatori");
            return;
        }
        // Validazione
        if (login.isEmpty() || password.isEmpty() || nome.isEmpty() || cognome.isEmpty() || email.isEmpty()) {
            mainFrame.showError("Tutti i campi sono obbligatori");
            return;
        }
        if (password.length() < 6) {
            mainFrame.showError("La password deve essere di almeno 6 caratteri");
            return;
        }
        if (!email.contains("@")) {
            mainFrame.showError("Inserisci un'email valida");
            return;
        }
        if (controller.registraUtente(login, password, nome, cognome, email, ruolo)) {
            mainFrame.showInfo("Registrazione completata con successo! Ora puoi effettuare il login.");
            cardLayout.show((JPanel) getComponent(0), CARD_LOGIN);
            clearRegisterFields();
        } else {
            mainFrame.showError("Errore durante la registrazione. Login o email già utilizzati.");
        }
    }
    /**
     * Pulisce i campi del form di login
     */
    private void clearLoginFields() {
        loginField.setText(USERNAME_TEXT);
        loginField.setForeground(PLACEHOLDER_COLOR);
        passwordField.setText(PASSWORD_TEXT);
        passwordField.setEchoChar((char) 0);
        passwordField.setForeground(PLACEHOLDER_COLOR);
    }
    /**
     * Pulisce i campi del form di registrazione
     */
    private void clearRegisterFields() {
        registerLoginField.setText(USERNAME_TEXT);
        registerLoginField.setForeground(PLACEHOLDER_COLOR);
        registerPasswordField.setText(PASSWORD_TEXT);
        registerPasswordField.setEchoChar((char) 0);
        registerPasswordField.setForeground(PLACEHOLDER_COLOR);
        nomeField.setText("Nome");
        nomeField.setForeground(PLACEHOLDER_COLOR);
        cognomeField.setText(COGNOME_TEXT);
        cognomeField.setForeground(PLACEHOLDER_COLOR);
        emailField.setText(EMAIL_TEXT);
        emailField.setForeground(PLACEHOLDER_COLOR);
        ruoloComboBox.setSelectedIndex(0);
    }
    /**
     * Mostra il pannello di registrazione moderno
     */
    private void showModernRegisterPanel() {
        // Sostituisci il contenuto del pannello principale con il pannello di registrazione moderno
        Container parent = getParent();
        if (parent != null) {
            parent.removeAll();
            parent.add(registerPanelModern);
            parent.revalidate();
            parent.repaint();
        }
    }
} 
