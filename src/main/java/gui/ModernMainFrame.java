package gui;
import controller.Controller;
import database.ConnectionManager;
import database.DataSourceFactory;
import model.Utente;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.Timer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Frame principale modernizzato dell'applicazione Hackathon Manager.
 * Implementa un design system PhD-level con Material Design 3 e Fluent UI principles.
 * Features: Modern navigation, responsive layout, dark/light theme, micro-animations
 */
public class ModernMainFrame extends JFrame {
    // Serialization
    private static final long serialVersionUID = 1L;
    private final transient Controller controller;
    // Componenti layout
    private JPanel rootPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JPanel headerPanel;
    private JPanel footerPanel;
    // Componenti navigazione
    private JLabel appTitleLabel;
    private JLabel userInfoLabel;
    private ModernComponents.ModernButton themeToggleButton;
    // Area contenuto principale
    private CardLayout cardLayout;
    // Navigation buttons
    private ModernComponents.ModernButton eventiNavButton;
    private ModernComponents.ModernButton teamNavButton;
    private ModernComponents.ModernButton registrazioniNavButton;
    private ModernComponents.ModernButton gestioneRegistrazioniNavButton;
    private ModernComponents.ModernButton valutazioniNavButton;
    private ModernComponents.ModernButton progressiNavButton;
    private ModernComponents.ModernButton reportNavButton;
    private ModernComponents.ModernButton notificheNavButton;
    private ModernComponents.ModernButton utenteNavButton;
    // Panel instances
    private TeamPanel teamPanel;
    private EventiPanel eventiPanel;
    private RegistrazioniPanel registrazioniPanel;
    private GestioneRegistrazioniPanel gestioneRegistrazioniPanel;
    private ValutazioniPanel valutazioniPanel;
    private ProgressPanel progressPanel;
    private ReportPanel reportPanel;
    private NotificationPanel notificationPanel;
    private ModernUtentePanel utentePanel;
    
    // Dashboard panels role-based (removed unused fields)
    // Costanti per i nomi delle card
    public static final String LOGIN_CARD = "LOGIN";
    public static final String EVENTI_CARD = "EVENTI";
    public static final String TEAM_CARD = "TEAM";
    public static final String REGISTRAZIONI_CARD = "REGISTRAZIONI";
    public static final String GESTIONE_REGISTRAZIONI_CARD = "GESTIONE_REGISTRAZIONI";
    public static final String VALUTAZIONI_CARD = "VALUTAZIONI";
    public static final String PROGRESSI_CARD = "PROGRESSI";
    public static final String REPORT_CARD = "REPORT";
    public static final String NOTIFICHE_CARD = "NOTIFICHE";
    public static final String UTENTE_CARD = "UTENTE";
    // Constants
    private static final String APP_TITLE = "Hackathon Manager";
    private static final String ERROR_SYSTEM_TITLE = "Errore Sistema";
    // Utente corrente e stato
    private transient Utente currentUser;
    private boolean isLoggedIn = false;
    /**
     * Costruttore che inizializza la GUI principale moderna.
     * 
     * Configura il frame principale con:
     * - Layout moderno responsive
     * - Sistema di navigazione a sidebar
     * - Gestione degli stati utente
     * - Theme switching capabilities
     * 
     * @see #initializeFrame()
     * @see #createModernLayout()
     * @see #initializePanels()
     */
    public ModernMainFrame() {
        ConnectionManager connectionManager = new ConnectionManager(DataSourceFactory.createDataSource());
        this.controller = new Controller(connectionManager);

        // Verifica configurazione database all'avvio
        try {
            System.out.println("🔍 Verifica configurazione database...");
            connectionManager.testTables();
            System.out.println("✅ Database configurato correttamente");
        } catch (Exception e) {
            System.err.println("❌ Errore configurazione database: " + e.getMessage());
            System.err.println("⚠️ Verificare che il database sia configurato correttamente");
            System.err.println("📖 Consultare README_DATABASE.md per le istruzioni");
        }

        initializeFrame();
        createModernLayout();
        initializePanels();
        setupEventHandlers();
        // Inizia con il pannello di login
        showLoginPanel();
    }
    /**
     * Inizializza le proprietà base del frame.
     * 
     * Configura:
     * - Dimensioni e posizionamento del frame
     * - Comportamento di chiusura
     * - Proprietà specifiche per macOS se disponibili
     * - Colori di background del design system
     */
    private void initializeFrame() {
        setTitle(APP_TITLE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 800));
        setPreferredSize(new Dimension(1400, 900));
        // Centra sullo schermo
        setLocationRelativeTo(null);
        
        // Set modern ADS icon - SOLUZIONE ROBUSTA PER WINDOWS TASKBAR
        setADSIconForWindows();
        
        // Proprietà frame moderno
        setBackground(DesignSystem.getBackgroundPrimary());
        // Decorazioni finestra stile Mac se supportate
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("apple.awt.application.name", APP_TITLE);
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
    }
    /**
     * Crea il layout moderno dell'applicazione
     */
    private void createModernLayout() {
        rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(DesignSystem.getBackgroundPrimary());
        createHeader();
        createSidebar();
        createMainContent();
        createFooter();
        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(sidebarPanel, BorderLayout.WEST);
        rootPanel.add(contentPanel, BorderLayout.CENTER);
        rootPanel.add(footerPanel, BorderLayout.SOUTH);
        setContentPane(rootPanel);
    }
    /**
     * Crea l'header moderno
     */
    private void createHeader() {
        headerPanel = new ModernComponents.ModernCard(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBackground(DesignSystem.getSurfaceElevated());
        // Lato sinistro - Titolo app con icona coding e tipografia moderna
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftPanel.setOpaque(false);
        // Aggiungi icona SVG coding
        JLabel codingIcon = SVGIcon.createAdvancedCodingIcon(32);
        leftPanel.add(codingIcon);
        // Aggiungi un po' di spaziatura
        leftPanel.add(Box.createHorizontalStrut(8));
        appTitleLabel = new JLabel(APP_TITLE);
        appTitleLabel.setFont(DesignSystem.HEADLINE_MEDIUM);
        appTitleLabel.setForeground(DesignSystem.getTextPrimary());
        leftPanel.add(appTitleLabel);
        // Lato destro - Info utente e toggle tema
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        userInfoLabel = new JLabel("Ospite", SVGIcon.createUserIcon(20), SwingConstants.LEFT);
        userInfoLabel.setFont(DesignSystem.BODY_MEDIUM);
        userInfoLabel.setForeground(DesignSystem.getTextSecondary());
        userInfoLabel.setIconTextGap(8);
        rightPanel.add(userInfoLabel);
        rightPanel.add(ModernLayout.createHorizontalSpacer(DesignSystem.SPACE_LG));
        themeToggleButton = new ModernComponents.ModernButton("");
        themeToggleButton.setIcon(getCurrentThemeIconSVG());
        themeToggleButton.setPreferredSize(new Dimension(45, 35));
        themeToggleButton.addActionListener(e -> toggleTheme());
        rightPanel.add(themeToggleButton);
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
    }
    /**
     * Crea la sidebar di navigazione moderna
     */
    private void createSidebar() {
        sidebarPanel = new ModernComponents.ModernCard();
        sidebarPanel.setPreferredSize(new Dimension(280, 0));
        sidebarPanel.setBackground(DesignSystem.getSurfaceElevated());
        sidebarPanel.setLayout(new BorderLayout());
        // Header navigazione - CENTRATO con icona SVG
        JPanel navHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        navHeader.setOpaque(false);
        navHeader.setBorder(BorderFactory.createEmptyBorder(
            DesignSystem.SPACE_2XL, DesignSystem.SPACE_LG, 
            DesignSystem.SPACE_XL, DesignSystem.SPACE_LG));
        JLabel navTitle = new JLabel("Navigazione", SVGIcon.createNavigationIcon(20), SwingConstants.LEFT);
        navTitle.setFont(DesignSystem.TITLE_MEDIUM);
        navTitle.setForeground(DesignSystem.getTextPrimary());
        navTitle.setIconTextGap(8);
        navHeader.add(navTitle);
        // Navigation buttons - PERFETTAMENTE CENTRATI
        JPanel navigationContainer = new JPanel();
        navigationContainer.setLayout(new BoxLayout(navigationContainer, BoxLayout.Y_AXIS));
        navigationContainer.setOpaque(false);
        navigationContainer.setBorder(BorderFactory.createEmptyBorder(
            DesignSystem.SPACE_XL, DesignSystem.SPACE_MD, DesignSystem.SPACE_LG, DesignSystem.SPACE_MD));
        createNavigationButtons();
        // Aggiungi spazio sopra per centrare meglio
        navigationContainer.add(Box.createVerticalStrut(DesignSystem.SPACE_LG));
        // Aggiungi ogni pulsante con centratura perfetta
        navigationContainer.add(eventiNavButton);
        navigationContainer.add(Box.createVerticalStrut(DesignSystem.SPACE_SM));
        navigationContainer.add(teamNavButton);
        navigationContainer.add(Box.createVerticalStrut(DesignSystem.SPACE_SM));
        navigationContainer.add(registrazioniNavButton);
        navigationContainer.add(Box.createVerticalStrut(DesignSystem.SPACE_SM));
        navigationContainer.add(gestioneRegistrazioniNavButton);
        navigationContainer.add(Box.createVerticalStrut(DesignSystem.SPACE_SM));
        navigationContainer.add(valutazioniNavButton);
        navigationContainer.add(Box.createVerticalStrut(DesignSystem.SPACE_SM));
        navigationContainer.add(progressiNavButton);
        navigationContainer.add(Box.createVerticalStrut(DesignSystem.SPACE_SM));
        navigationContainer.add(reportNavButton);
        navigationContainer.add(Box.createVerticalStrut(DesignSystem.SPACE_SM));
        navigationContainer.add(notificheNavButton);
        navigationContainer.add(Box.createVerticalStrut(DesignSystem.SPACE_SM));
        navigationContainer.add(utenteNavButton);
        // Aggiungi spazio sotto per spingere verso il centro
        navigationContainer.add(Box.createVerticalGlue());
        // Logout button at bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(
            DesignSystem.SPACE_LG, DesignSystem.SPACE_LG, 
            DesignSystem.SPACE_2XL, DesignSystem.SPACE_LG));
        ModernComponents.ModernButton logoutButton = createNavButton("Esci", DesignSystem.ERROR_500);
        logoutButton.addActionListener(e -> handleLogout());
        bottomPanel.add(logoutButton);
        sidebarPanel.add(navHeader, BorderLayout.NORTH);
        sidebarPanel.add(new ModernComponents.ModernScrollPane(navigationContainer), BorderLayout.CENTER);
        sidebarPanel.add(bottomPanel, BorderLayout.SOUTH);
        // Initially hide sidebar (show only when logged in)
        sidebarPanel.setVisible(false);
    }
    /**
     * Crea i pulsanti di navigazione moderni
     */
    private void createNavigationButtons() {
        eventiNavButton = createNavButton("Eventi", DesignSystem.PRIMARY_500);
        eventiNavButton.addActionListener(e -> showEventiPanel());
        teamNavButton = createNavButton("Team", DesignSystem.SECONDARY_500);
        teamNavButton.addActionListener(e -> showTeamPanel());
        registrazioniNavButton = createNavButton("Registrazioni", DesignSystem.INFO_500);
        registrazioniNavButton.addActionListener(e -> showRegistrazioniPanel());
        gestioneRegistrazioniNavButton = createNavButton("Gestione Registrazioni", DesignSystem.ERROR_500);
        gestioneRegistrazioniNavButton.addActionListener(e -> showGestioneRegistrazioniPanel());
        valutazioniNavButton = createNavButton("Valutazioni", DesignSystem.WARNING_500);
        valutazioniNavButton.addActionListener(e -> showValutazioniPanel());
        progressiNavButton = createNavButton("Progressi", new Color(138, 43, 226)); // Blue Violet
        progressiNavButton.addActionListener(e -> showProgressPanel());
        reportNavButton = createNavButton("Report", new Color(52, 73, 94)); // Dark Blue Gray
        reportNavButton.addActionListener(e -> showReportPanel());
        notificheNavButton = createNavButton("Notifiche", new Color(142, 68, 173)); // Purple
        notificheNavButton.addActionListener(e -> showNotificationPanel());
        utenteNavButton = createNavButton("Profilo", DesignSystem.ACCENT_500);
        utenteNavButton.addActionListener(e -> showUtentePanel());
    }
    /**
     * Crea un pulsante di navigazione moderno
     */
    private ModernComponents.ModernButton createNavButton(String text, Color backgroundColor) {
        ModernComponents.ModernButton button = new ModernComponents.ModernButton(text);
        button.setFont(DesignSystem.LABEL_LARGE);
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setPreferredSize(new Dimension(220, 45)); // Più stretti per centrarli
        button.setMaximumSize(new Dimension(220, 45)); // Dimensione fissa
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // CENTRALI
        // Add hover animation
        AnimationUtils.addHoverShadowEffect(button);
        // Special hover for logout button
        if (text.equals("Esci")) {
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                Color originalColor = backgroundColor;
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    button.setBackground(DesignSystem.ERROR_600); // ROSSO più scuro al hover
                    button.repaint();
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    button.setBackground(originalColor);
                    button.repaint();
                }
            });
        }
        return button;
    }
    /**
     * Crea l'area di contenuto principale
     */
    private void createMainContent() {
        contentPanel = new JPanel();
        contentPanel.setBackground(DesignSystem.getBackgroundPrimary());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
            DesignSystem.SPACE_LG, DesignSystem.SPACE_LG, 
            DesignSystem.SPACE_LG, DesignSystem.SPACE_LG));
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
    }
    /**
     * Crea il footer moderno con scritta scorrevole
     */
    private void createFooter() {
        footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(DesignSystem.getSurfaceElevated());
        footerPanel.setPreferredSize(new Dimension(0, 50));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(
            DesignSystem.SPACE_SM, DesignSystem.SPACE_LG, 
            DesignSystem.SPACE_SM, DesignSystem.SPACE_LG));
        JLabel statusLabel = new JLabel(APP_TITLE + " • " + getCurrentTimeString(), SVGIcon.createStatusIcon(16), SwingConstants.LEFT);
        statusLabel.setFont(DesignSystem.BODY_SMALL);
        statusLabel.setForeground(DesignSystem.getTextSecondary());
        statusLabel.setIconTextGap(8);
        // Create scrolling text panel
        ScrollingTextPanel scrollingPanel = new ScrollingTextPanel();
        JLabel versionLabel = new JLabel("v2.0.0", SVGIcon.createInfoIcon(16), SwingConstants.LEFT);
        versionLabel.setFont(DesignSystem.BODY_SMALL);
        versionLabel.setForeground(DesignSystem.getTextSecondary());
        versionLabel.setIconTextGap(6);
        footerPanel.add(statusLabel, BorderLayout.WEST);
        footerPanel.add(scrollingPanel, BorderLayout.CENTER);
        footerPanel.add(versionLabel, BorderLayout.EAST);
        // Update time every second
        Timer timeTimer = new Timer(1000, e -> statusLabel.setText(APP_TITLE + " • " + getCurrentTimeString()));
        timeTimer.start();
    }
    /**
     * Pannello con testo scorrevole per le informazioni del progetto
     */
    private class ScrollingTextPanel extends JPanel {
        private String text = "Università di Napoli Federico II • Progetto di OOP e Database • " +
                             "Prof. Porfirio Tramontana, Prof. Bernardo Breve e Prof. Silvio Barra • " +
                             "Realizzato da: Minopoli Alessandro, Megna Daniele, Simone Iodice • " +
                             APP_TITLE + " ";
        private int xPosition = 0;
        private Timer scrollTimer;
        private FontMetrics fontMetrics;
        public ScrollingTextPanel() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.ITALIC, 14));
            fontMetrics = getFontMetrics(getFont());
            // Start smooth scrolling animation with slower, more fluid movement
            scrollTimer = new Timer(30, e -> {
                xPosition -= 1;
                int textWidth = fontMetrics.stringWidth(text);
                if (xPosition < -textWidth) {
                    xPosition = getWidth();
                }
                repaint();
            });
            scrollTimer.start();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(getFont());
            g2.setColor(DesignSystem.getTextSecondary());
            int y = (getHeight() + fontMetrics.getAscent() - fontMetrics.getDescent()) / 2;
            g2.drawString(text, xPosition, y);
            // Draw again for seamless loop
            int textWidth = fontMetrics.stringWidth(text);
            g2.drawString(text, xPosition + textWidth + 50, y);
            g2.dispose();
        }
        @Override
        public void addNotify() {
            super.addNotify();
            if (scrollTimer != null && !scrollTimer.isRunning()) {
                scrollTimer.start();
            }
        }
        @Override
        public void removeNotify() {
            super.removeNotify();
            if (scrollTimer != null && scrollTimer.isRunning()) {
                scrollTimer.stop();
            }
        }
    }
    /**
     * Inizializza i pannelli dell'applicazione
     */
    private void initializePanels() {
        // Create panels with modern design
        ModernLoginPanel loginPanel = new ModernLoginPanel(controller, this);
        // Cast to MainFrame for compatibility with existing panels
        // Nota: alcuni pannelli legacy richiedono un MainFrame, ma possiamo passare null per ora
        MainFrame legacyFrame = null;
        try {
            this.eventiPanel = new EventiPanel(controller, legacyFrame);
            this.teamPanel = new TeamPanel(controller, legacyFrame);
            this.registrazioniPanel = new RegistrazioniPanel(controller, legacyFrame);
            this.gestioneRegistrazioniPanel = new GestioneRegistrazioniPanel(controller, legacyFrame);
            this.valutazioniPanel = new ValutazioniPanel(controller, legacyFrame);
            this.utentePanel = new ModernUtentePanel(controller);
        } catch (Exception e) {
            // Log the exception with context
            System.err.println("Errore durante l'inizializzazione dei pannelli: " + e.getMessage());
            e.printStackTrace();
            // Crea pannelli di fallback
            this.teamPanel = null; // Sarà gestito dall'inizializzazione lazy
            this.utentePanel = new ModernUtentePanel(controller);
        }
        // Add panels to card layout
        contentPanel.add(loginPanel, LOGIN_CARD);
        contentPanel.add(eventiPanel, EVENTI_CARD);
        if (teamPanel != null) {
        contentPanel.add(teamPanel, TEAM_CARD);
        } else {
            // Crea un pannello di errore come fallback
            JPanel errorPanel = new JPanel(new BorderLayout());
            errorPanel.add(new JLabel("Errore: Pannello Team non inizializzato", SwingConstants.CENTER), BorderLayout.CENTER);
            contentPanel.add(errorPanel, TEAM_CARD);
        }
        contentPanel.add(registrazioniPanel, REGISTRAZIONI_CARD);
        contentPanel.add(gestioneRegistrazioniPanel, GESTIONE_REGISTRAZIONI_CARD);
        contentPanel.add(valutazioniPanel, VALUTAZIONI_CARD);
        contentPanel.add(utentePanel, UTENTE_CARD);
    }
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        // Window closing event
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                handleApplicationExit();
            }
        });
    }
            // ===================== METODI DI NAVIGAZIONE =====================
    public void showLoginPanel() {
        cardLayout.show(contentPanel, LOGIN_CARD);
        sidebarPanel.setVisible(false);
        isLoggedIn = false;
        updateUserInfo(null);
        revalidate();
        repaint();
    }
    public void showEventiPanel() {
        if (isLoggedIn) {
            cardLayout.show(contentPanel, EVENTI_CARD);
            updateActiveNavButton(eventiNavButton);
            // Aggiorna i dati del pannello eventi
            if (eventiPanel != null) {
                eventiPanel.refreshData();
            }
        }
    }
    public void showTeamPanel() {
        if (!isLoggedIn) {
            return;
        }
        try {
            // Inizializzazione lazy del pannello se necessario
            if (teamPanel == null) {
                try {
                    MainFrame legacyFrame = null; // Per compatibilità
                    this.teamPanel = new TeamPanel(controller, legacyFrame);
                    // Aggiungi al card layout se non già presente
                    if (contentPanel != null) {
                        try {
                            contentPanel.add(teamPanel, TEAM_CARD);
                        } catch (IllegalArgumentException e) {
                            // Log the exception with context
                            System.err.println("Errore durante l'aggiunta del pannello team al layout: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    // Log the exception with context
                    System.err.println("Errore durante l'inizializzazione del pannello team: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Errore durante l'inizializzazione del pannello team",
                        ERROR_SYSTEM_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            cardLayout.show(contentPanel, TEAM_CARD);
            updateActiveNavButton(teamNavButton);
            if (teamPanel != null) {
                teamPanel.refreshData();
                // Forza un refresh della UI dopo il caricamento
                SwingUtilities.invokeLater(() -> {
                    teamPanel.revalidate();
                    teamPanel.repaint();
                });
            } else {
                JOptionPane.showMessageDialog(this,
                    "Errore interno: impossibile inizializzare il pannello team",
                    ERROR_SYSTEM_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            // Log the exception with context
            System.err.println("Errore durante l'apertura del pannello team: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Errore durante l'apertura del pannello team: " + e.getMessage(),
                "Errore",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    public void showRegistrazioniPanel() {
        if (isLoggedIn) {
            cardLayout.show(contentPanel, REGISTRAZIONI_CARD);
            updateActiveNavButton(registrazioniNavButton);
            // Aggiorna i dati del pannello registrazioni
            if (registrazioniPanel != null) {
                registrazioniPanel.refreshData();
            }
        }
    }
    public void showGestioneRegistrazioniPanel() {
        if (isLoggedIn && currentUser != null && currentUser.isOrganizzatore()) {
            cardLayout.show(contentPanel, GESTIONE_REGISTRAZIONI_CARD);
            updateActiveNavButton(gestioneRegistrazioniNavButton);
            // Aggiorna i dati del pannello gestione registrazioni
            if (gestioneRegistrazioniPanel != null) {
                gestioneRegistrazioniPanel.refreshData();
            }
        }
    }
    public void showValutazioniPanel() {
        if (isLoggedIn) {
            cardLayout.show(contentPanel, VALUTAZIONI_CARD);
            updateActiveNavButton(valutazioniNavButton);
            // Aggiorna i dati del pannello valutazioni
            if (valutazioniPanel != null) {
                valutazioniPanel.refreshData();
            }
        }
    }
    public void showProgressPanel() {
        if (!isLoggedIn) {
            return;
        }
        try {
            // Inizializzazione lazy del pannello se necessario
            if (progressPanel == null) {
                try {
                    this.progressPanel = new ProgressPanel(controller, this);
                    // Aggiungi al card layout se non già presente
                    if (contentPanel != null) {
                        try {
                            contentPanel.add(progressPanel, PROGRESSI_CARD);
                        } catch (IllegalArgumentException e) {
                            // Log the exception with context
                            System.err.println("Errore durante l'aggiunta del pannello progressi al layout: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    // Log the exception with context
                    System.err.println("Errore durante l'inizializzazione del pannello progressi: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Errore durante l'inizializzazione del pannello progressi:\n" + e.getMessage(),
                        ERROR_SYSTEM_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            // Mostra il pannello
            cardLayout.show(contentPanel, PROGRESSI_CARD);
            updateActiveNavButton(progressiNavButton);
            // Aggiorna i dati del pannello progressi
            if (progressPanel != null) {
                progressPanel.refreshData();
            }
        } catch (Exception e) {
            // Log the exception with context
            System.err.println("Errore durante la visualizzazione del pannello progressi: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Errore durante la visualizzazione del pannello progressi:\n" + e.getMessage(),
                ERROR_SYSTEM_TITLE,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    public void showReportPanel() {
        if (!isLoggedIn) {
            return;
        }
        try {
            // Inizializzazione lazy del pannello se necessario
            if (reportPanel == null) {
                try {
                    this.reportPanel = new ReportPanel(controller, this);
                    // Aggiungi al card layout se non già presente
                    if (contentPanel != null) {
                        try {
                            contentPanel.add(reportPanel, REPORT_CARD);
                        } catch (IllegalArgumentException e) {
                            // Log the exception with context
                            System.err.println("Errore durante l'aggiunta del pannello report al layout: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    // Log the exception with context
                    System.err.println("Errore durante l'inizializzazione del pannello report: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Errore durante l'inizializzazione del pannello report:\n" + e.getMessage(),
                        ERROR_SYSTEM_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            // Mostra il pannello
            cardLayout.show(contentPanel, REPORT_CARD);
            updateActiveNavButton(reportNavButton);
            // Aggiorna i dati del pannello report
            if (reportPanel != null) {
                reportPanel.refreshData();
            }
        } catch (Exception e) {
            // Log the exception with context
            System.err.println("Errore durante la visualizzazione del pannello report: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Errore durante la visualizzazione del pannello report:\n" + e.getMessage(),
                ERROR_SYSTEM_TITLE,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    public void showNotificationPanel() {
        if (!isLoggedIn) {
            return;
        }
        try {
            // Inizializzazione lazy del pannello se necessario
            if (notificationPanel == null) {
                try {
                    this.notificationPanel = new NotificationPanel(controller, this);
                    // Aggiungi al card layout se non già presente
                    if (contentPanel != null) {
                        try {
                            contentPanel.add(notificationPanel, NOTIFICHE_CARD);
                        } catch (IllegalArgumentException e) {
                            // Log the exception with context
                            System.err.println("Errore durante l'aggiunta del pannello notifiche al layout: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    // Log the exception with context
                    System.err.println("Errore durante l'inizializzazione del pannello notifiche: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Errore durante l'inizializzazione del pannello notifiche:\n" + e.getMessage(),
                        ERROR_SYSTEM_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            // Mostra il pannello
            cardLayout.show(contentPanel, NOTIFICHE_CARD);
            updateActiveNavButton(notificheNavButton);
            // Aggiorna i dati del pannello notifiche
            if (notificationPanel != null) {
                notificationPanel.refreshData();
            }
        } catch (Exception e) {
            // Log the exception with context
            System.err.println("Errore durante la visualizzazione del pannello notifiche: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Errore durante la visualizzazione del pannello notifiche:\n" + e.getMessage(),
                ERROR_SYSTEM_TITLE,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    public void showUtentePanel() {
        if (isLoggedIn) {
            cardLayout.show(contentPanel, UTENTE_CARD);
            updateActiveNavButton(utenteNavButton);
            // Aggiorna i dati del pannello utente
            if (utentePanel != null) {
                utentePanel.refreshData();
            }
        }
    }
    /**
     * Called when user successfully logs in
     */
    public void onLoginSuccess(Utente user) {
        this.currentUser = user;
        this.isLoggedIn = true;
        updateUserInfo(user);
        sidebarPanel.setVisible(true);
        showEventiPanel(); // Default panel after login
                // Show success toast
        ModernComponents.showToast(this, "Accesso riuscito! Benvenuto " + user.getNome(),
                                 ModernComponents.ToastNotification.ToastType.SUCCESS);
        revalidate();
        repaint();
    }
    /**
     * Updates the active navigation button appearance
     */
    private void updateActiveNavButton(ModernComponents.ModernButton activeButton) {
        // Reset all buttons to default appearance
        ModernComponents.ModernButton[] navButtons = {
            eventiNavButton, teamNavButton, registrazioniNavButton, 
            gestioneRegistrazioniNavButton, valutazioniNavButton, utenteNavButton
        };
        for (ModernComponents.ModernButton button : navButtons) {
            // Reset to default background (you might want to implement this in ModernButton)
            button.repaint();
        }
        // Highlight active button (you might want to implement this in ModernButton)
        if (activeButton != null) {
            activeButton.repaint();
        }
    }
    /**
     * Updates user information in the header
     */
    private void updateUserInfo(Utente user) {
        if (user != null) {
            userInfoLabel.setText(user.getNome() + " " + user.getCognome() +
                       " (" + user.getRuolo() + ")");
            // Update navigation button visibility based on user role
            updateNavigationVisibility(user);
        } else {
            userInfoLabel.setText("Ospite");
        }
    }
    /**
     * Updates navigation button visibility based on user role
     */
    private void updateNavigationVisibility(Utente user) {
        // Eventi - visible to all
        eventiNavButton.setVisible(true);
        // Team - visible to participants
        teamNavButton.setVisible(user.isPartecipante());
        // Registrazioni - visible to all
        registrazioniNavButton.setVisible(true);
        // Gestione Registrazioni - visible only to organizers
        gestioneRegistrazioniNavButton.setVisible(user.isOrganizzatore());
        // Valutazioni - visible to judges
        valutazioniNavButton.setVisible(user.isGiudice());
        // Progressi - visible to participants and judges
        progressiNavButton.setVisible(user.isPartecipante() || user.isGiudice());
        // Report - visible only to organizers
        reportNavButton.setVisible(user.isOrganizzatore());
        // Notifiche - visible to all authenticated users
        notificheNavButton.setVisible(true);
        // Profilo - visible to all
        utenteNavButton.setVisible(true);
    }
    /**
     * Handles logout
     */
    private void handleLogout() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Sei sicuro di voler uscire?",
            "Conferma Uscita",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (result == JOptionPane.YES_OPTION) {
            showLoginPanel();
            ModernComponents.showToast(this, "Disconnessione effettuata con successo", 
                                     ModernComponents.ToastNotification.ToastType.INFO);
        }
    }
    /**
     * Toggles between light and dark theme
     */
    private void toggleTheme() {
        DesignSystem.Theme currentTheme = DesignSystem.getCurrentTheme();
        DesignSystem.Theme newTheme = (currentTheme == DesignSystem.Theme.LIGHT) ? 
                                     DesignSystem.Theme.DARK : DesignSystem.Theme.LIGHT;
        DesignSystem.setTheme(newTheme);
                    themeToggleButton.setIcon(getCurrentThemeIconSVG());
        // Update all components colors
        updateThemeColors();
        String themeText = newTheme == DesignSystem.Theme.LIGHT ? "chiaro" : "scuro";
        ModernComponents.showToast(this, "Tema cambiato in modalità " + themeText, 
                                 ModernComponents.ToastNotification.ToastType.INFO);
    }
    /**
     * Updates colors when theme changes
     */
    private void updateThemeColors() {
        // Update root panel
        rootPanel.setBackground(DesignSystem.getBackgroundPrimary());
        contentPanel.setBackground(DesignSystem.getBackgroundPrimary());
        // Update header
        headerPanel.setBackground(DesignSystem.getSurfaceElevated());
        appTitleLabel.setForeground(DesignSystem.getTextPrimary());
        userInfoLabel.setForeground(DesignSystem.getTextSecondary());
        // Update sidebar
        sidebarPanel.setBackground(DesignSystem.getSurfaceElevated());
        // Update footer
        footerPanel.setBackground(DesignSystem.getSurfaceElevated());
        // Repaint everything
        SwingUtilities.updateComponentTreeUI(this);
        repaint();
    }
    /**
     * Gets current theme icon SVG based on current theme
     */
    private Icon getCurrentThemeIconSVG() {
        return DesignSystem.getCurrentTheme() == DesignSystem.Theme.LIGHT ? 
               SVGIcon.createMoonIcon(20) : SVGIcon.createSunIcon(20);
    }
    /**
     * Gets current time as formatted string
     */
    private String getCurrentTimeString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    /**
     * Gestisce l'uscita dall'applicazione
     */
    private void handleApplicationExit() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Sei sicuro di voler uscire dall'applicazione?",
            "Esci da " + APP_TITLE,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
            // ===================== METODI UTILITY =====================
    public void showToast(String message, ModernComponents.ToastNotification.ToastType type) {
        ModernComponents.showToast(this, message, type);
    }
    public Controller getController() {
        return controller;
    }
    public Utente getCurrentUser() {
        return currentUser;
    }
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    // Compatibility methods for existing code
    public void backToLogin() {
        showLoginPanel();
    }
    // Static color getters for compatibility
    public static Color getPrimaryColor() {
        return DesignSystem.PRIMARY_500;
    }
    public static Color getSecondaryColor() {
        return DesignSystem.SECONDARY_500;
    }
    public static Color getBackgroundColor() {
        return DesignSystem.getBackgroundPrimary();
    }
    public static Color getTextColor() {
        return DesignSystem.getTextPrimary();
    }
    public static Color getBorderColor() {
        return DesignSystem.getBorderLight();
    }

    /**
     * Imposta l'icona ADS per Windows con tecniche avanzate
     */
    private void setADSIconForWindows() {
        try {
            // Metodo 1: Carica icone PNG multiple
            java.util.List<java.awt.Image> icons = loadPNGIcons();
            
            if (!icons.isEmpty()) {
                // Tecnica Windows: setIconImages + setIconImage
                setIconImages(icons);
                setIconImage(icons.get(0));
                
                // Tecnica Windows: Forza aggiornamento taskbar
                forceWindowsTaskbarUpdate(icons.get(0));
                
            } else {
                // Metodo 2: Crea icona SVG dinamica
                createDynamicSVGIcon();
            }
            
        } catch (Exception e) {
            // Metodo 3: Icona di emergenza
            createEmergencyIcon();
        }
    }
    
    /**
     * Carica tutte le icone PNG disponibili
     */
    private java.util.List<java.awt.Image> loadPNGIcons() {
        java.util.List<java.awt.Image> icons = new java.util.ArrayList<>();
        
        // Dimensioni Windows in ordine di priorità
        int[] sizes = {256, 128, 64, 48, 32, 16};
        
        for (int size : sizes) {
            loadSingleIcon(icons, "/icons/app_icon_" + size + ".png", size);
        }
        
        // Fallback: icona principale
        if (icons.isEmpty()) {
            loadFallbackIcon(icons);
        }
        
        return icons;
    }
    
    /**
     * Carica una singola icona della dimensione specificata
     */
    private void loadSingleIcon(java.util.List<java.awt.Image> icons, String iconPath, int size) {
        try {
            java.net.URL iconURL = getClass().getResource(iconPath);
            
            if (iconURL != null) {
                java.awt.Image iconImage = new ImageIcon(iconURL).getImage();
                if (iconImage != null) {
                    icons.add(iconImage);
                }
            }
        } catch (Exception e) {
            // Continua con la prossima dimensione
        }
    }
    
    /**
     * Carica l'icona principale come fallback
     */
    private void loadFallbackIcon(java.util.List<java.awt.Image> icons) {
        try {
            java.net.URL iconURL = getClass().getResource("/icons/app_icon.png");
            if (iconURL != null) {
                java.awt.Image iconImage = new ImageIcon(iconURL).getImage();
                if (iconImage != null) {
                    icons.add(iconImage);
                }
            }
        } catch (Exception e) {
            // Icona principale non disponibile
        }
    }
    
    /**
     * Forza l'aggiornamento della taskbar di Windows
     */
    private void forceWindowsTaskbarUpdate(java.awt.Image icon) {
        try {
            // Tecnica 1: Delay + re-impostazione
            Thread.sleep(50);
            setIconImage(icon);
            
            // Tecnica 2: Minimizza/massimizza per forzare refresh
            SwingUtilities.invokeLater(() -> {
                try {
                    setState(Frame.ICONIFIED);
                    Thread.sleep(100);
                    setState(Frame.NORMAL);
                    setIconImage(icon);
                } catch (Exception e) {
                    // Log the exception with context
                    System.err.println("Errore durante l'aggiornamento della taskbar di Windows: " + e.getMessage());
                }
            });
            
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Crea icona SVG dinamica
     */
    private void createDynamicSVGIcon() {
        try {
            Icon adsIcon = SVGIcon.createADSAppIcon(32);
            java.awt.image.BufferedImage iconImage = new java.awt.image.BufferedImage(
                32, 32, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g2d = iconImage.createGraphics();
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            adsIcon.paintIcon(null, g2d, 0, 0);
            g2d.dispose();
            
            setIconImage(iconImage);
            
        } catch (Exception e) {
            // Log the exception with context
            System.err.println("Errore durante la creazione dell'icona SVG dinamica: " + e.getMessage());
            e.printStackTrace();
            createEmergencyIcon();
        }
    }
    
    /**
     * Crea icona di emergenza semplice
     */
    private void createEmergencyIcon() {
        try {
            java.awt.image.BufferedImage iconImage = new java.awt.image.BufferedImage(
                32, 32, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g2d = iconImage.createGraphics();
            
            // Disegna un'icona semplice "ADS"
            g2d.setColor(new Color(52, 152, 219));
            g2d.fillRect(0, 0, 32, 32);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("ADS", 6, 20);
            g2d.dispose();
            
            setIconImage(iconImage);
            
        } catch (Exception e) {
            // Log the exception with context
            System.err.println("Errore durante la creazione dell'icona di emergenza: " + e.getMessage());
        }
    }
}
