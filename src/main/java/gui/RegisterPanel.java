package gui;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
/**
 * Pannello di registrazione con design moderno ispirato a Canva
 * Implementa sfondo con gradiente montagne, form semi-trasparente e design minimalista
 */
public class RegisterPanel extends JPanel {
    // Colori moderni ispirati al design Canva
    private static final Color PRIMARY_COLOR = new Color(34, 34, 34);      // Nero elegante
    private static final Color TIFFANY_COLOR = new Color(72, 201, 176);     // Verde Tiffany
    private static final Color BACKGROUND_START = new Color(34, 139, 34);   // Verde scuro (montagne)
    private static final Color BACKGROUND_MIDDLE = new Color(144, 238, 144); // Verde chiaro (prati)
    private static final Color BACKGROUND_END = new Color(135, 206, 235);   // Azzurro (cielo)
    private static final Color FORM_BACKGROUND = new Color(255, 255, 255, 230); // Bianco semi-trasparente
    private static final Color TEXT_COLOR = new Color(34, 34, 34);          // Testo scuro
    private static final Color BORDER_COLOR = new Color(200, 200, 200);     // Bordo grigio chiaro
    private static final Color PLACEHOLDER_COLOR = new Color(150, 150, 150); // Placeholder
    private static final Color ERROR_COLOR = new Color(220, 53, 69);        // Rosso errore
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);      // Verde successo
    private static final Color WEAK_COLOR = new Color(220, 53, 69);         // Rosso debole
    private static final Color MEDIUM_COLOR = new Color(241, 196, 15);      // Giallo medio
    private static final Color STRONG_COLOR = new Color(40, 167, 69);       // Verde forte
    // Font moderni
    private static final String MODERN_FONT = "Segoe UI";
    // Costanti per stringhe duplicate
    private static final String COMPLETE_REGISTRATION_TEXT = "✅ Completa Registrazione";
    private static final String WARNING_TYPE = "warning";
    // Componenti GUI
    private JTextField nameField;
    private JTextField surnameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox termsCheckBox;
    private JButton registerButton;
    private JPanel formPanel;
    private JPanel loadingSpinner;
    private JProgressBar passwordStrengthBar;
    private JLabel strengthLabel;
    // MainFrame reference
    private MainFrame mainFrame;
    // Stato validazione
    private boolean isNameValid = false;
    private boolean isSurnameValid = false;
    private boolean isEmailValid = false;
    private boolean isPasswordValid = false;
    private boolean isConfirmPasswordValid = false;
    private boolean isTermsAccepted = false;
    // Step del form
    private int currentStep = 1;
    private static final int TOTAL_STEPS = 2;
    /**
     * Costruttore
     */
    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setupLayout();
        createRegisterForm();
    }
    /**
     * Configura il layout del pannello
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        // Listener per ridimensionamento responsive
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateResponsiveLayout();
            }
        });
    }
    /**
     * Disegna lo sfondo con gradiente montagne
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        // Abilita antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        // Crea gradiente a tre colori (cielo -> prati -> montagne)
        int height = getHeight();
        int width = getWidth();
        // Gradiente cielo (parte superiore)
        GradientPaint skyGradient = new GradientPaint(
            0, 0, BACKGROUND_END,
            0, height * 0.4f, BACKGROUND_MIDDLE
        );
        g2d.setPaint(skyGradient);
        g2d.fillRect(0, 0, width, (int)(height * 0.4f));
        // Gradiente prati (parte centrale)
        GradientPaint grassGradient = new GradientPaint(
            0, height * 0.4f, BACKGROUND_MIDDLE,
            0, height * 0.7f, BACKGROUND_START
        );
        g2d.setPaint(grassGradient);
        g2d.fillRect(0, (int)(height * 0.4f), width, (int)(height * 0.3f));
        // Gradiente montagne (parte inferiore)
        GradientPaint mountainGradient = new GradientPaint(
            0, height * 0.7f, BACKGROUND_START,
            0, height, new Color(25, 100, 25)
        );
        g2d.setPaint(mountainGradient);
        g2d.fillRect(0, (int)(height * 0.7f), width, (int)(height * 0.3f));
        // Aggiungi dettagli delle montagne
        drawMountains(g2d, width, height);
        g2d.dispose();
    }
    /**
     * Disegna le montagne sullo sfondo
     */
    private void drawMountains(Graphics2D g2d, int width, int height) {
        g2d.setColor(new Color(25, 100, 25, 180));
        // Montagna 1 (sinistra)
        int[] x1 = {0, width * 3 / 8, width / 2};
        int[] y1 = {height, (int)(height * 0.6f), (int)(height * 0.7f)};
        g2d.fillPolygon(x1, y1, 3);
        // Montagna 2 (centro)
        int[] x2 = {width / 4, width / 2, width * 3 / 4};
        int[] y2 = {(int)(height * 0.7f), (int)(height * 0.5f), (int)(height * 0.7f)};
        g2d.fillPolygon(x2, y2, 3);
        // Montagna 3 (destra)
        int[] x3 = {width / 2, width * 5 / 8, width};
        int[] y3 = {(int)(height * 0.7f), (int)(height * 0.6f), height};
        g2d.fillPolygon(x3, y3, 3);
    }
    /**
     * Crea il form di registrazione
     */
    private void createRegisterForm() {
        // Rimuovi componenti esistenti
        removeAll();
        // Pannello principale centrato
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        // Form panel semi-trasparente con bordi arrotondati
        formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                // Abilita antialiasing
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Disegna sfondo semi-trasparente con bordi arrotondati
                int arc = 20;
                int shadowOffset = 6;
                // Ombra soft
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fill(new RoundRectangle2D.Float(shadowOffset, shadowOffset, 
                    (float)getWidth() - shadowOffset, (float)getHeight() - shadowOffset, arc, arc));
                // Sfondo principale semi-trasparente
                g2d.setColor(FORM_BACKGROUND);
                g2d.fill(new RoundRectangle2D.Float(0, 0, 
                    (float)getWidth() - shadowOffset, (float)getHeight() - shadowOffset, arc, arc));
                // Bordo sottile
                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1));
                g2d.draw(new RoundRectangle2D.Float(0, 0, 
                    (float)getWidth() - shadowOffset, (float)getHeight() - shadowOffset, arc, arc));
                g2d.dispose();
            }
        };
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        formPanel.setPreferredSize(new Dimension(450, 600));
        formPanel.setMaximumSize(new Dimension(450, 600));
        // Header con progress bar
        JPanel headerPanel = createHeaderPanel();
        formPanel.add(headerPanel);
        formPanel.add(Box.createVerticalStrut(30));
        // Step 1: Dati base
        if (currentStep == 1) {
            createStep1Form();
        } else {
            createStep2Form();
        }
        // Aggiungi il form al pannello centrato
        centerPanel.add(formPanel);
        // Aggiungi al pannello principale
        add(centerPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    /**
     * Crea l'header con progress bar
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        // Titolo
        JLabel titleLabel = new JLabel("Crea il tuo account", SwingConstants.CENTER);
        titleLabel.setFont(new Font(MODERN_FONT, Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Progress bar
        JProgressBar progressBar = new JProgressBar(0, TOTAL_STEPS);
        progressBar.setValue(currentStep);
        progressBar.setStringPainted(true);
        progressBar.setString("Step " + currentStep + " di " + TOTAL_STEPS);
        progressBar.setFont(new Font(MODERN_FONT, Font.PLAIN, 12));
        progressBar.setForeground(TIFFANY_COLOR);
        progressBar.setBackground(new Color(240, 240, 240));
        progressBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        progressBar.setPreferredSize(new Dimension(300, 25));
        progressBar.setMaximumSize(new Dimension(300, 25));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(progressBar, BorderLayout.SOUTH);
        return headerPanel;
    }
    /**
     * Crea il form del primo step
     */
    private void createStep1Form() {
        // Nome
        nameField = createStyledTextField("Nome", "Inserisci il tuo nome");
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setMaximumSize(new Dimension(350, 45));
        nameField.setPreferredSize(new Dimension(350, 45));
        // Cognome
        surnameField = createStyledTextField("Cognome", "Inserisci il tuo cognome");
        surnameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        surnameField.setMaximumSize(new Dimension(350, 45));
        surnameField.setPreferredSize(new Dimension(350, 45));
        // Email
        emailField = createStyledTextField("Email", "Inserisci la tua email");
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setMaximumSize(new Dimension(350, 45));
        emailField.setPreferredSize(new Dimension(350, 45));
        // Password
        passwordField = createStyledPasswordField("Password", "Scegli una password");
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(350, 45));
        passwordField.setPreferredSize(new Dimension(350, 45));
        // Password strength bar
        JPanel strengthPanel = new JPanel(new BorderLayout());
        strengthPanel.setOpaque(false);
        strengthPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        strengthPanel.setMaximumSize(new Dimension(350, 60));
        passwordStrengthBar = new JProgressBar(0, 100);
        passwordStrengthBar.setStringPainted(false);
        passwordStrengthBar.setPreferredSize(new Dimension(350, 8));
        passwordStrengthBar.setMaximumSize(new Dimension(350, 8));
        strengthLabel = new JLabel("Inserisci una password");
        strengthLabel.setFont(new Font(MODERN_FONT, Font.PLAIN, 12));
        strengthLabel.setForeground(PLACEHOLDER_COLOR);
        strengthLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        strengthPanel.add(passwordStrengthBar, BorderLayout.NORTH);
        strengthPanel.add(strengthLabel, BorderLayout.SOUTH);
        // Conferma Password
        confirmPasswordField = createStyledPasswordField("Conferma Password", "Ripeti la password");
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmPasswordField.setMaximumSize(new Dimension(350, 45));
        confirmPasswordField.setPreferredSize(new Dimension(350, 45));
        // Checkbox termini
        termsCheckBox = new JCheckBox("Accetto i Termini e Condizioni");
        termsCheckBox.setFont(new Font(MODERN_FONT, Font.PLAIN, 12));
        termsCheckBox.setForeground(TEXT_COLOR);
        termsCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        termsCheckBox.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        // Link termini
        JLabel termsLink = new JLabel("Leggi i termini");
        termsLink.setFont(new Font(MODERN_FONT, Font.PLAIN, 10));
        termsLink.setForeground(PRIMARY_COLOR);
        termsLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        termsLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        termsLink.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        termsLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                termsLink.setText("<html><u>Leggi i termini</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                termsLink.setText("Leggi i termini");
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.showToast("Apertura termini e condizioni...", "info");
            }
        });
        // Bottone continua
        JButton continueButton = createStyledButton("Continua", TIFFANY_COLOR, e -> nextStep());
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.setMaximumSize(new Dimension(350, 40));
        continueButton.setPreferredSize(new Dimension(350, 40));
        // Aggiungi componenti
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(surnameField);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(strengthPanel);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(confirmPasswordField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(termsCheckBox);
        formPanel.add(termsLink);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(continueButton);
        // Validazione real-time
        setupRealTimeValidation();
    }
    /**
     * Crea il form del secondo step
     */
    private void createStep2Form() {
        // Social login options
        JLabel socialLabel = new JLabel("Oppure registrati con:", SwingConstants.CENTER);
        socialLabel.setFont(new Font(MODERN_FONT, Font.PLAIN, 14));
        socialLabel.setForeground(TEXT_COLOR.brighter());
        socialLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        socialLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        // Google button
        JButton googleButton = createSocialButton("🔍 Accedi con Google", new Color(234, 67, 53));
        googleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        googleButton.setMaximumSize(new Dimension(350, 45));
        googleButton.setPreferredSize(new Dimension(350, 45));
        // GitHub button
        JButton githubButton = createSocialButton("🐙 Accedi con GitHub", new Color(36, 41, 46));
        githubButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        githubButton.setMaximumSize(new Dimension(350, 45));
        githubButton.setPreferredSize(new Dimension(350, 45));
        // Separator
        JPanel separatorPanel = new JPanel(new BorderLayout());
        separatorPanel.setOpaque(false);
        separatorPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        separatorPanel.setMaximumSize(new Dimension(350, 20));
        JLabel separatorLabel = new JLabel("oppure", SwingConstants.CENTER);
        separatorLabel.setFont(new Font(MODERN_FONT, Font.PLAIN, 12));
        separatorLabel.setForeground(TEXT_COLOR.brighter());
        separatorPanel.add(separatorLabel, BorderLayout.CENTER);
        // Bottone registrazione finale
        registerButton = createStyledButton(COMPLETE_REGISTRATION_TEXT, TIFFANY_COLOR, e -> handleRegister());
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(350, 40));
        registerButton.setPreferredSize(new Dimension(350, 40));
        // Spinner di caricamento
        loadingSpinner = createLoadingSpinner();
        loadingSpinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadingSpinner.setVisible(false);
        // Bottone torna al login
        JButton backToLoginButton = createStyledButton("← Torna al Login", PRIMARY_COLOR, e -> backToLogin());
        backToLoginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backToLoginButton.setMaximumSize(new Dimension(350, 40));
        backToLoginButton.setPreferredSize(new Dimension(350, 40));
        // Link per tornare al login
        JLabel backToLoginLink = new JLabel("Hai già un account? Accedi", SwingConstants.CENTER);
        backToLoginLink.setFont(new Font(MODERN_FONT, Font.PLAIN, 12));
        backToLoginLink.setForeground(PRIMARY_COLOR);
        backToLoginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        backToLoginLink.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        backToLoginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backToLoginLink.setText("<html><u>Hai già un account? Accedi</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                backToLoginLink.setText("Hai già un account? Accedi");
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                backToLogin();
            }
        });
        // Aggiungi componenti
        formPanel.add(socialLabel);
        formPanel.add(googleButton);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(githubButton);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(separatorPanel);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(registerButton);
        formPanel.add(loadingSpinner);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(backToLoginButton);
        formPanel.add(backToLoginLink);
    }
    /**
     * Crea un campo di testo stilizzato in stile Canva
     */
    private JTextField createStyledTextField(String placeholder, String tooltip) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setColor(PLACEHOLDER_COLOR);
                    g2d.setFont(getFont().deriveFont(Font.PLAIN, 14));
                    g2d.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
                    g2d.dispose();
                }
            }
        };
        field.setFont(new Font(MODERN_FONT, Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        field.setToolTipText(tooltip);
        // Focus effects
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(TEXT_COLOR);
                }
                animateBorder(field, BORDER_COLOR, PRIMARY_COLOR, 200);
                field.setBackground(new Color(248, 250, 252));
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(PLACEHOLDER_COLOR);
                }
                animateBorder(field, PRIMARY_COLOR, BORDER_COLOR, 200);
                field.setBackground(Color.WHITE);
            }
        });
        return field;
    }
    /**
     * Crea un campo password stilizzato in stile Canva
     */
    private JPasswordField createStyledPasswordField(String placeholder, String tooltip) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setColor(PLACEHOLDER_COLOR);
                    g2d.setFont(getFont().deriveFont(Font.PLAIN, 14));
                    g2d.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
                    g2d.dispose();
                }
            }
        };
        field.setFont(new Font(MODERN_FONT, Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        field.setToolTipText(tooltip);
        // Focus effects
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setForeground(TEXT_COLOR);
                }
                animateBorder(field, BORDER_COLOR, PRIMARY_COLOR, 200);
                field.setBackground(new Color(248, 250, 252));
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setForeground(PLACEHOLDER_COLOR);
                }
                animateBorder(field, PRIMARY_COLOR, BORDER_COLOR, 200);
                field.setBackground(Color.WHITE);
            }
        });
        return field;
    }
    /**
     * Crea un bottone social in stile Canva
     */
    private JButton createSocialButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                // Abilita antialiasing
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Disegna sfondo con bordi arrotondati
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                // Disegna testo
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        button.setFont(new Font(MODERN_FONT, Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                animateButtonHover(button, backgroundColor, backgroundColor.darker(), 150);
                button.setSize((int)(button.getPreferredSize().width * 1.02), 
                             (int)(button.getPreferredSize().height * 1.02));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                animateButtonHover(button, backgroundColor.darker(), backgroundColor, 150);
                button.setSize(button.getPreferredSize());
            }
        });
        return button;
    }
    /**
     * Crea un bottone stilizzato in stile Canva
     */
    private JButton createStyledButton(String text, Color backgroundColor, ActionListener action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                // Abilita antialiasing
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Disegna sfondo con bordi arrotondati
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                // Disegna testo
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        button.setFont(new Font(MODERN_FONT, Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        // Hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                animateButtonHover(button, backgroundColor, backgroundColor.darker(), 150);
                button.setSize((int)(button.getPreferredSize().width * 1.02), 
                             (int)(button.getPreferredSize().height * 1.02));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                animateButtonHover(button, backgroundColor.darker(), backgroundColor, 150);
                button.setSize(button.getPreferredSize());
            }
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(backgroundColor.darker().darker());
                button.setSize((int)(button.getPreferredSize().width * 0.98), 
                             (int)(button.getPreferredSize().height * 0.98));
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
                button.setSize(button.getPreferredSize());
            }
        });
        return button;
    }
    /**
     * Crea uno spinner di caricamento
     */
    private JPanel createLoadingSpinner() {
        final float[] rotationAngle = {0.0f};
        JPanel spinner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                // Abilita antialiasing
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = 12;
                // Disegna il cerchio di caricamento
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                int startAngle = (int)(rotationAngle[0] * 180 / Math.PI);
                g2d.drawArc(centerX - radius, centerY - radius, 
                           radius * 2, radius * 2, startAngle, 270);
                g2d.dispose();
            }
        };
        // Timer per l'animazione di rotazione
        Timer rotationTimer = new Timer(16, e -> {
            rotationAngle[0] += 0.2f;
            spinner.repaint();
        });
        rotationTimer.start();
        spinner.setPreferredSize(new Dimension(24, 24));
        spinner.setOpaque(false);
        return spinner;
    }
    /**
     * Configura la validazione real-time
     */
    private void setupRealTimeValidation() {
        // Validazione nome
        nameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateName(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateName(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateName(); }
        });
        // Validazione cognome
        surnameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateSurname(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateSurname(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateSurname(); }
        });
        // Validazione email
        emailField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
        });
        // Validazione password
        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validatePassword(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validatePassword(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validatePassword(); }
        });
        // Validazione conferma password
        confirmPasswordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateConfirmPassword(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateConfirmPassword(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateConfirmPassword(); }
        });
        // Validazione checkbox
        termsCheckBox.addActionListener(e -> isTermsAccepted = termsCheckBox.isSelected());
    }
    /**
     * Validazione nome
     */
    private void validateName() {
        String name = nameField.getText();
        isNameValid = name.length() >= 2;
        if (!name.isEmpty()) {
            if (isNameValid) {
                nameField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(SUCCESS_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            } else {
                nameField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ERROR_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }
        }
    }
    /**
     * Validazione cognome
     */
    private void validateSurname() {
        String surname = surnameField.getText();
        isSurnameValid = surname.length() >= 2;
        if (!surname.isEmpty()) {
            if (isSurnameValid) {
                surnameField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(SUCCESS_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            } else {
                surnameField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ERROR_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }
        }
    }
    /**
     * Validazione email
     */
    private void validateEmail() {
        String email = emailField.getText();
        isEmailValid = email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        if (!email.isEmpty()) {
            if (isEmailValid) {
                emailField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(SUCCESS_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            } else {
                emailField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ERROR_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }
        }
    }
    /**
     * Validazione password con strength indicator
     */
    private void validatePassword() {
        String password = new String(passwordField.getPassword());
        isPasswordValid = password.length() >= 6;
        // Calcola strength
        int strength = calculatePasswordStrength(password);
        passwordStrengthBar.setValue(strength);
        if (!password.isEmpty()) {
            if (strength < 30) {
                passwordStrengthBar.setForeground(WEAK_COLOR);
                strengthLabel.setText("Password debole");
                strengthLabel.setForeground(WEAK_COLOR);
            } else if (strength < 70) {
                passwordStrengthBar.setForeground(MEDIUM_COLOR);
                strengthLabel.setText("Password media");
                strengthLabel.setForeground(MEDIUM_COLOR);
            } else {
                passwordStrengthBar.setForeground(STRONG_COLOR);
                strengthLabel.setText("Password forte");
                strengthLabel.setForeground(STRONG_COLOR);
            }
            if (isPasswordValid) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(SUCCESS_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            } else {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ERROR_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }
        } else {
            passwordStrengthBar.setValue(0);
            strengthLabel.setText("Inserisci una password");
            strengthLabel.setForeground(PLACEHOLDER_COLOR);
        }
        // Valida anche conferma password
        validateConfirmPassword();
    }
    /**
     * Calcola la strength della password
     */
    private int calculatePasswordStrength(String password) {
        int strength = 0;
        if (password.length() >= 8) strength += 20;
        if (password.matches(".*[a-z].*")) strength += 20;
        if (password.matches(".*[A-Z].*")) strength += 20;
        if (password.matches(".*\\d.*")) strength += 20;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) strength += 20;
        return Math.min(strength, 100);
    }
    /**
     * Validazione conferma password
     */
    private void validateConfirmPassword() {
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        isConfirmPasswordValid = password.equals(confirmPassword) && !password.isEmpty();
        if (!confirmPassword.isEmpty()) {
            if (isConfirmPasswordValid) {
                confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(SUCCESS_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            } else {
                confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(ERROR_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }
        }
    }
    /**
     * Passa al prossimo step
     */
    private void nextStep() {
        // Validazione
        if (!isNameValid || !isSurnameValid || !isEmailValid || !isPasswordValid || !isConfirmPasswordValid || !isTermsAccepted) {
            mainFrame.showToast("Completa tutti i campi obbligatori", WARNING_TYPE);
            return;
        }
        currentStep = 2;
        createRegisterForm();
    }
    /**
     * Gestisce la registrazione
     */
    private void handleRegister() {
        // Ottieni i dati dal form
        String nome = nameField.getText().trim();
        String cognome = surnameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        // Validazione finale
        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty()) {
            mainFrame.showToast("Completa tutti i campi obbligatori", WARNING_TYPE);
            return;
        }
        if (!isNameValid || !isSurnameValid || !isEmailValid || !isPasswordValid || !isConfirmPasswordValid) {
            mainFrame.showToast("Correggi gli errori nel form", WARNING_TYPE);
            return;
        }
        // Disabilita bottone e mostra spinner
        registerButton.setEnabled(false);
        registerButton.setText("");
        loadingSpinner.setVisible(true);
        // Genera un login basato su nome e cognome
        String login = (nome + "." + cognome).toLowerCase().replaceAll("\\s+", "");
        // Registrazione asincrona
        Timer registerTimer = new Timer(1000, e -> {
            try {
                // Usa il controller per la registrazione
                boolean success = mainFrame.getController().registraUtente(
                    login, password, nome, cognome, email, "PARTECIPANTE"
                );
                if (success) {
                    mainFrame.showToast("Registrazione completata con successo! 🎉", "success");
                    backToLogin();
                } else {
                    mainFrame.showToast("Errore: login o email già utilizzati", "error");
                    registerButton.setEnabled(true);
                    registerButton.setText(COMPLETE_REGISTRATION_TEXT);
                    loadingSpinner.setVisible(false);
                }
            } catch (Exception ex) {
                mainFrame.showToast("Errore durante la registrazione: " + ex.getMessage(), "error");
                registerButton.setEnabled(true);
                registerButton.setText(COMPLETE_REGISTRATION_TEXT);
                loadingSpinner.setVisible(false);
            }
        });
        registerTimer.setRepeats(false);
        registerTimer.start();
    }
    /**
     * Torna al login
     */
    private void backToLogin() {
        mainFrame.showToast("Tornando al login...", "info");
        mainFrame.backToLogin();
    }
    /**
     * Animazione del bordo
     */
    private void animateBorder(JComponent component, Color fromColor, Color toColor, int duration) {
        Timer borderTimer = new Timer(16, new ActionListener() {
            private float progress = 0.0f;
            private float step = 1.0f / (duration / 16.0f);
            @Override
            public void actionPerformed(ActionEvent e) {
                progress += step;
                if (progress >= 1.0f) {
                    progress = 1.0f;
                    ((Timer)e.getSource()).stop();
                }
                // Interpola il colore
                int r = (int)(fromColor.getRed() + (toColor.getRed() - fromColor.getRed()) * progress);
                int g = (int)(fromColor.getGreen() + (toColor.getGreen() - fromColor.getGreen()) * progress);
                int b = (int)(fromColor.getBlue() + (toColor.getBlue() - fromColor.getBlue()) * progress);
                Color interpolatedColor = new Color(r, g, b);
                component.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(interpolatedColor, 1, true),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }
        });
        borderTimer.start();
    }
    /**
     * Animazione hover del bottone
     */
    private void animateButtonHover(JButton button, Color fromColor, Color toColor, int duration) {
        Timer hoverTimer = new Timer(16, new ActionListener() {
            private float progress = 0.0f;
            private float step = 1.0f / (duration / 16.0f);
            @Override
            public void actionPerformed(ActionEvent e) {
                progress += step;
                if (progress >= 1.0f) {
                    progress = 1.0f;
                    ((Timer)e.getSource()).stop();
                }
                // Interpola il colore
                int r = (int)(fromColor.getRed() + (toColor.getRed() - fromColor.getRed()) * progress);
                int g = (int)(fromColor.getGreen() + (toColor.getGreen() - fromColor.getGreen()) * progress);
                int b = (int)(fromColor.getBlue() + (toColor.getBlue() - fromColor.getBlue()) * progress);
                button.setBackground(new Color(r, g, b));
            }
        });
        hoverTimer.start();
    }
    /**
     * Aggiorna il layout responsive
     */
    private void updateResponsiveLayout() {
        int width = getWidth();
        if (width < 600) {
            // Layout compatto per schermi piccoli
            if (formPanel != null) {
                formPanel.setPreferredSize(new Dimension(width - 40, 550));
                formPanel.setMaximumSize(new Dimension(width - 40, 550));
            }
        } else {
            // Layout normale per schermi grandi
            if (formPanel != null) {
                formPanel.setPreferredSize(new Dimension(450, 600));
                formPanel.setMaximumSize(new Dimension(450, 600));
            }
        }
        revalidate();
        repaint();
    }
} 
