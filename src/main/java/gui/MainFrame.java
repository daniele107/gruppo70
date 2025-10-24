package gui;
import controller.Controller;
import database.ConnectionManager;
import database.DataSourceFactory;
import model.Utente;
import model.Hackathon;
import java.util.List;
import javax.swing.JFrame;
import java.awt.Frame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Cursor;
import java.awt.Toolkit;
import javax.swing.SwingUtilities;
import java.util.logging.Logger;
import java.util.logging.Level;
/**
 * Frame principale dell'applicazione Hackathon Manager.
 * Contiene la barra dei menu e i pannelli per tutte le funzionalità.
 * Implementa un'interfaccia moderna seguendo gli standard Swing Hacks.
 */
public class MainFrame extends JFrame {
    private static final Logger LOG = Logger.getLogger(MainFrame.class.getName());
    private final transient Controller controller;
    // Menu components
    private JMenuBar mainMenuBar;
    private JMenu eventiMenu;
    private JMenu teamMenu;
    private JMenu registrazioniMenu;
    private JMenu valutazioniMenu;
    private JMenu utenteMenu;
    // Main panel with card layout
    private JPanel mainPanel;
    private CardLayout cardLayout;
    // Panels for different functionalities
    private EventiPanel eventiPanel;
    private TeamPanel teamPanel;
    private RegistrazioniPanel registrazioniPanel;
    private ValutazioniPanel valutazioniPanel;
    private UtentePanel utentePanel;
    // Constants for card names
    public static final String LOGIN_CARD = "LOGIN";
    public static final String EVENTI_CARD = "EVENTI";
    public static final String TEAM_CARD = "TEAM";
    public static final String REGISTRAZIONI_CARD = "REGISTRAZIONI";
    public static final String VALUTAZIONI_CARD = "VALUTAZIONI";
    public static final String UTENTE_CARD = "UTENTE";
    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219); // Blue
    private static final Color SECONDARY_COLOR = new Color(46, 204, 113); // Green
    private static final Color ACCENT_COLOR = new Color(155, 89, 182); // Purple
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250); // Light gray
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(52, 73, 94); // Dark gray
    private static final Color BORDER_COLOR = new Color(189, 195, 199); // Light gray
    // Font constants
    private static final String FONT_UI = "Segoe UI";
    // Toast type constants
    private static final String TOAST_TYPE_ERROR = "error";
    /**
     * Costruttore che inizializza la GUI principale
     */
    public MainFrame() {
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

        setupModernLookAndFeel();
        initializeFrame();
        initializeMenuBar();
        initializeMainPanel();
        setupEventHandlers();
        // Start with login panel
        showLoginPanel();
    }
    /**
     * Configura il look and feel moderno
     */
    private void setupModernLookAndFeel() {
        try {
            // Use system look and feel for modern appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Customize UI defaults for modern appearance
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            // Set modern colors
            UIManager.put("Button.background", PRIMARY_COLOR);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.focus", PRIMARY_COLOR);
            UIManager.put("Button.select", PRIMARY_COLOR.darker());
            UIManager.put("TextField.background", CARD_BACKGROUND);
            UIManager.put("TextField.foreground", TEXT_COLOR);
            UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            UIManager.put("PasswordField.background", CARD_BACKGROUND);
            UIManager.put("PasswordField.foreground", TEXT_COLOR);
            UIManager.put("PasswordField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            UIManager.put("ComboBox.background", CARD_BACKGROUND);
            UIManager.put("ComboBox.foreground", TEXT_COLOR);
            UIManager.put("ComboBox.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
            UIManager.put("MenuBar.background", PRIMARY_COLOR);
            UIManager.put("MenuBar.foreground", Color.WHITE);
            UIManager.put("Menu.background", PRIMARY_COLOR);
            UIManager.put("Menu.foreground", Color.WHITE);
            UIManager.put("MenuItem.background", PRIMARY_COLOR);
            UIManager.put("MenuItem.foreground", Color.WHITE);
            UIManager.put("MenuItem.selectionBackground", PRIMARY_COLOR.darker());
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("Label.foreground", TEXT_COLOR);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, () -> "Errore nel setup del look and feel: " + e.getMessage());
        }
    }
    /**
     * Inizializza il frame principale con supporto responsive
     */
    private void initializeFrame() {
        setTitle("Hackathon Manager - Sistema di Gestione Eventi");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Responsive sizing - adatta alla risoluzione dello schermo
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(1400, (int)(screenSize.width * 0.9));
        int height = Math.min(900, (int)(screenSize.height * 0.9));
        setSize(width, height);
        setLocationRelativeTo(null);
        setResizable(true);
        // Minimum size per evitare problemi su schermi piccoli
        setMinimumSize(new Dimension(800, 600));
        // Set modern ADS icon - SOLUZIONE ROBUSTA PER WINDOWS TASKBAR
        setADSIconForWindows();
        // Set background color
        getContentPane().setBackground(BACKGROUND_COLOR);
        // Listener per ridimensionamento responsive
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateResponsiveLayout();
            }
        });
    }

    /**
     * Imposta l'icona ADS per Windows con tecniche avanzate
     */
    private void setADSIconForWindows() {
        LOG.log(Level.INFO, "=== INIZIO SETUP ICONA ADS WINDOWS ===");
        
        try {
            // Metodo 1: Carica icone PNG multiple
            java.util.List<java.awt.Image> icons = loadPNGIcons();
            
            if (!icons.isEmpty()) {
                // Tecnica Windows: setIconImages + setIconImage
                setIconImages(icons);
                setIconImage(icons.get(0));
                
                LOG.log(Level.INFO, "✓ Metodo 1: Caricate " + icons.size() + " icone PNG");
                
                // Tecnica Windows: Forza aggiornamento taskbar
                forceWindowsTaskbarUpdate(icons.get(0));
                
            } else {
                // Metodo 2: Crea icona SVG dinamica
                LOG.log(Level.WARNING, "Fallback: Creazione icona SVG dinamica");
                createDynamicSVGIcon();
            }
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "ERRORE: Impossibile impostare icona ADS", e);
            // Metodo 3: Icona di emergenza
            createEmergencyIcon();
        }
        
        LOG.log(Level.INFO, "=== FINE SETUP ICONA ADS WINDOWS ===");
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
                    LOG.log(Level.FINE, "✓ Caricata icona " + size + "x" + size);
                }
            }
        } catch (Exception e) {
            LOG.log(Level.FINE, "Icona " + size + "x" + size + " non disponibile");
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
                    LOG.log(Level.INFO, "✓ Caricata icona principale");
                }
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Icona principale non disponibile");
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
                    LOG.log(Level.INFO, "✓ Taskbar Windows aggiornata con successo");
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Errore aggiornamento taskbar", e);
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
            LOG.log(Level.INFO, "✓ Icona SVG dinamica creata");
            
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Errore creazione icona SVG", e);
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
            LOG.log(Level.INFO, "✓ Icona di emergenza creata");
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Impossibile creare nessuna icona", e);
        }
    }
    /**
     * Aggiorna il layout in base alle dimensioni della finestra
     */
    private void updateResponsiveLayout() {
        int width = getWidth();
        // Adatta la barra dei menu
        if (width < 1000) {
            // Layout compatto per schermi piccoli
            mainMenuBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            updateMenuFonts(new Font(FONT_UI, Font.PLAIN, 12));
        } else {
            // Layout normale per schermi grandi
            mainMenuBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            updateMenuFonts(new Font(FONT_UI, Font.PLAIN, 14));
        }
        // Notifica i pannelli del ridimensionamento
        if (eventiPanel != null) eventiPanel.onResize(width);
    }
    /**
     * Aggiorna i font dei menu in base alle dimensioni
     */
    private void updateMenuFonts(Font font) {
        for (int i = 0; i < mainMenuBar.getMenuCount(); i++) {
            JMenu menu = mainMenuBar.getMenu(i);
            menu.setFont(font);
            for (int j = 0; j < menu.getItemCount(); j++) {
                JMenuItem item = menu.getItem(j);
                if (item != null) {
                    item.setFont(font);
                }
            }
        }
    }
    /**
     * Inizializza la barra dei menu con stile moderno
     */
    private void initializeMenuBar() {
        mainMenuBar = new JMenuBar();
        mainMenuBar.setBackground(PRIMARY_COLOR);
        mainMenuBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        // Menu Eventi
        eventiMenu = createStyledMenu("📅 Eventi");
        JMenuItem creaEventoItem = createStyledMenuItem("➕ Crea Evento");
        JMenuItem visualizzaEventiItem = createStyledMenuItem("Visualizza Eventi");
        JMenuItem gestisciEventiItem = createStyledMenuItem("Gestisci Eventi");
        eventiMenu.add(creaEventoItem);
        eventiMenu.add(visualizzaEventiItem);
        eventiMenu.add(gestisciEventiItem);
        // Menu Team
        teamMenu = createStyledMenu("👥 Team");
        JMenuItem creaTeamItem = createStyledMenuItem("➕ Crea Team");
        JMenuItem gestisciTeamItem = createStyledMenuItem("Gestisci Team");
        JMenuItem richiesteJoinItem = createStyledMenuItem("📨 Richieste di Join");
        teamMenu.add(creaTeamItem);
        teamMenu.add(gestisciTeamItem);
        teamMenu.add(richiesteJoinItem);
        
        teamMenu.addSeparator();
        JMenuItem caricaProgressoItem = createStyledMenuItem("📈 Carica Progresso");
        caricaProgressoItem.addActionListener(e -> showProgressUploadDialog());
        teamMenu.add(caricaProgressoItem);
        
        JMenuItem statoTeamItem = createStyledMenuItem("📊 Stato Team");
        statoTeamItem.addActionListener(e -> showTeamStatusDialog());
        teamMenu.add(statoTeamItem);
        // Menu Registrazioni
        registrazioniMenu = createStyledMenu("Registrazioni");
        JMenuItem registraUtenteItem = createStyledMenuItem("➕ Registra Utente");
        JMenuItem gestisciRegistrazioniItem = createStyledMenuItem("Gestisci Registrazioni");
        registrazioniMenu.add(registraUtenteItem);
        registrazioniMenu.add(gestisciRegistrazioniItem);
        // Menu Valutazioni
        valutazioniMenu = createStyledMenu("Valutazioni");
        JMenuItem assegnaVotiItem = createStyledMenuItem("📊 Assegna Voti");
        JMenuItem visualizzaClassificaItem = createStyledMenuItem("Visualizza Classifica");
        visualizzaClassificaItem.addActionListener(e -> showHackathonRankingDialog());
        JMenuItem revisioneProgressiItem = createStyledMenuItem("Revisione Progressi");
        revisioneProgressiItem.addActionListener(e -> showJudgeProgressReviewDialog());
        valutazioniMenu.add(assegnaVotiItem);
        valutazioniMenu.add(visualizzaClassificaItem);
        valutazioniMenu.addSeparator();
        valutazioniMenu.add(revisioneProgressiItem);
        // Menu Utente
        utenteMenu = createStyledMenu("👤 Utente");
        JMenuItem profiloItem = createStyledMenuItem("👤 Profilo");
        JMenuItem logoutItem = createStyledMenuItem("🚪 Logout");
        utenteMenu.add(profiloItem);
        utenteMenu.add(logoutItem);
        // Add menus to menu bar
        mainMenuBar.add(eventiMenu);
        mainMenuBar.add(teamMenu);
        mainMenuBar.add(registrazioniMenu);
        mainMenuBar.add(valutazioniMenu);
        mainMenuBar.add(utenteMenu);
        setJMenuBar(mainMenuBar);
        // Initially disable all menus except login
        setMenuEnabled(false);
    }
    /**
     * Crea un menu con stile moderno
     */
    private JMenu createStyledMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setFont(new Font(FONT_UI, Font.PLAIN, 14));
        menu.setForeground(Color.WHITE);
        menu.setBackground(PRIMARY_COLOR);
        return menu;
    }
    /**
     * Crea un menu item con stile moderno
     */
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font(FONT_UI, Font.PLAIN, 13));
        item.setForeground(Color.WHITE);
        item.setBackground(PRIMARY_COLOR);
        return item;
    }
    /**
     * Inizializza il pannello principale con CardLayout
     */
    private void initializeMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setBackground(BACKGROUND_COLOR);
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        // Create panels with modern styling
        LoginPanel loginPanel = new LoginPanel(controller, this);
        eventiPanel = new EventiPanel(controller, this);
        teamPanel = new TeamPanel(controller, this);
        registrazioniPanel = new RegistrazioniPanel(controller, this);
        valutazioniPanel = new ValutazioniPanel(controller, this);
        utentePanel = new UtentePanel(controller, this);
        // Add panels to card layout
        mainPanel.add(loginPanel, LOGIN_CARD);
        mainPanel.add(eventiPanel, EVENTI_CARD);
        mainPanel.add(teamPanel, TEAM_CARD);
        mainPanel.add(registrazioniPanel, REGISTRAZIONI_CARD);
        mainPanel.add(valutazioniPanel, VALUTAZIONI_CARD);
        mainPanel.add(utentePanel, UTENTE_CARD);
        add(mainPanel);
    }
    /**
     * Configura gli event handler per i menu
     */
    private void setupEventHandlers() {
        // Eventi menu handlers
        eventiMenu.getItem(0).addActionListener(e -> showEventiPanel()); // Crea Evento
        eventiMenu.getItem(1).addActionListener(e -> showEventiPanel()); // Visualizza Eventi
        eventiMenu.getItem(2).addActionListener(e -> showEventiPanel()); // Gestisci Eventi
        // Team menu handlers
        teamMenu.getItem(0).addActionListener(e -> showTeamPanel()); // Crea Team
        teamMenu.getItem(1).addActionListener(e -> showTeamPanel()); // Gestisci Team
        teamMenu.getItem(2).addActionListener(e -> showTeamPanel()); // Richieste di Join
        // Registrazioni menu handlers
        registrazioniMenu.getItem(0).addActionListener(e -> showRegistrazioniPanel()); // Registra Utente
        registrazioniMenu.getItem(1).addActionListener(e -> showRegistrazioniPanel()); // Gestisci Registrazioni
        // Valutazioni menu handlers
        valutazioniMenu.getItem(0).addActionListener(e -> showValutazioniPanel()); // Assegna Voti
        valutazioniMenu.getItem(1).addActionListener(e -> showValutazioniPanel()); // Visualizza Classifica
        // Utente menu handlers
        utenteMenu.getItem(0).addActionListener(e -> showUtentePanel()); // Profilo
        utenteMenu.getItem(1).addActionListener(e -> logout()); // Logout
    }
    /**
     * Mostra il pannello di login
     */
    public void showLoginPanel() {
        cardLayout.show(mainPanel, LOGIN_CARD);
        setMenuEnabled(false);
        setTitle("Hackathon Manager - Login");
    }
    /**
     * Torna al pannello di login dal pannello di registrazione
     */
    public void backToLogin() {
        showLoginPanel();
    }
    /**
     * Mostra il pannello eventi
     */
    public void showEventiPanel() {
        cardLayout.show(mainPanel, EVENTI_CARD);
        eventiPanel.refreshData();
        setTitle("Hackathon Manager - Eventi");
    }
    /**
     * Mostra il pannello team
     */
    public void showTeamPanel() {
        cardLayout.show(mainPanel, TEAM_CARD);
        teamPanel.refreshData();
        setTitle("Hackathon Manager - Team");
    }
    /**
     * Mostra il pannello registrazioni
     */
    public void showRegistrazioniPanel() {
        cardLayout.show(mainPanel, REGISTRAZIONI_CARD);
        registrazioniPanel.refreshData();
        setTitle("Hackathon Manager - Registrazioni");
    }
    /**
     * Mostra il pannello valutazioni
     */
    public void showValutazioniPanel() {
        cardLayout.show(mainPanel, VALUTAZIONI_CARD);
        valutazioniPanel.refreshData();
        setTitle("Hackathon Manager - Valutazioni");
    }
    /**
     * Mostra il pannello utente
     */
    public void showUtentePanel() {
        cardLayout.show(mainPanel, UTENTE_CARD);
        utentePanel.refreshData();
        setTitle("Hackathon Manager - Profilo Utente");
    }
    /**
     * Abilita/disabilita i menu in base all'autenticazione
     *
     * @param enabled true per abilitare i menu, false per disabilitarli
     */
    private void setMenuEnabled(boolean enabled) {
        eventiMenu.setEnabled(enabled);
        teamMenu.setEnabled(enabled);
        registrazioniMenu.setEnabled(enabled);
        valutazioniMenu.setEnabled(enabled);
        utenteMenu.setEnabled(enabled);
    }
    /**
     * Gestisce il login dell'utente
     */
    public void handleLogin() {
        Utente currentUser = controller.getCurrentUser();
        if (currentUser != null) {
            setMenuEnabled(true);
            updateMenuVisibility(currentUser);
            showEventiPanel(); // Default panel after login
            showToast("Benvenuto " + currentUser.getNome() + " " + currentUser.getCognome() + "! 👋", "success");
        }
    }
    /**
     * Aggiorna la visibilità dei menu in base al ruolo dell'utente
     *
     * @param user l'utente corrente
     */
    private void updateMenuVisibility(Utente user) {
        // Eventi menu - tutti possono vedere
        eventiMenu.setVisible(true);
        // Team menu - solo per partecipanti
        teamMenu.setVisible(user.isPartecipante());
        // Registrazioni menu - tutti possono vedere
        registrazioniMenu.setVisible(true);
        // Valutazioni menu - solo per giudici
        valutazioniMenu.setVisible(user.isGiudice());
        // Utente menu - tutti possono vedere
        utenteMenu.setVisible(true);
    }
    /**
     * Gestisce il logout dell'utente
     */
    private void logout() {
        int choice = showModernConfirmDialog("Sei sicuro di voler effettuare il logout?", 
                                           "Conferma Logout");
        if (choice == JOptionPane.YES_OPTION) {
            controller.logout();
            setMenuEnabled(false);
            showLoginPanel();
            showModernMessage("Logout effettuato con successo!", "Logout", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    /**
     * Mostra un toast notification moderno
     */
    public void showToast(String message, String type) {
        JPanel toastPanel = new JPanel(new BorderLayout());
        toastPanel.setBackground(getToastColor(type));
        toastPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getToastColor(type).darker(), 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font(FONT_UI, Font.PLAIN, 14));
        messageLabel.setForeground(Color.WHITE);
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font(FONT_UI, Font.BOLD, 16));
        closeButton.setForeground(Color.BLACK); // Testo nero per visibilità
        closeButton.setBackground(new Color(0, 0, 0, 0));
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toastPanel.add(messageLabel, BorderLayout.CENTER);
        toastPanel.add(closeButton, BorderLayout.EAST);
        // Posiziona il toast in modo responsive
        int toastWidth = Math.min(330, getWidth() - 40);
        int toastX = getWidth() - toastWidth - 20;
        toastPanel.setBounds(toastX, 20, toastWidth, 50);
        add(toastPanel);
        // Auto-hide dopo 4 secondi
        Timer timer = new Timer(4000, e -> {
            remove(toastPanel);
            revalidate();
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
        // Close button handler
        closeButton.addActionListener(e -> {
            timer.stop();
            remove(toastPanel);
            revalidate();
            repaint();
        });
        revalidate();
        repaint();
    }
    /**
     * Ottiene il colore del toast in base al tipo
     */
    private Color getToastColor(String type) {
        switch (type.toLowerCase()) {
            case "success": return SECONDARY_COLOR;
            case TOAST_TYPE_ERROR: return new Color(231, 76, 60);
            case "warning": return new Color(241, 196, 15);
            case "info": return PRIMARY_COLOR;
            default: return PRIMARY_COLOR;
        }
    }
    /**
     * Mostra un messaggio moderno (fallback)
     */
    public void showModernMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    /**
     * Mostra un messaggio di errore
     *
     * @param message il messaggio di errore
     */
    public void showError(String message) {
        showModernMessage(message, "Errore", JOptionPane.ERROR_MESSAGE);
    }
    /**
     * Mostra un messaggio di informazione
     *
     * @param message il messaggio di informazione
     */
    public void showInfo(String message) {
        showModernMessage(message, "Informazione", JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * Mostra un dialog di conferma moderno
     */
    public int showModernConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
    }
    /**
     * Ottiene il controller
     *
     * @return il controller
     */
    public Controller getController() {
        return controller;
    }
    /**
     * Ottiene i colori del tema
     */
    public static Color getPrimaryColor() { return PRIMARY_COLOR; }
    public static Color getSecondaryColor() { return SECONDARY_COLOR; }
    public static Color getAccentColor() { return ACCENT_COLOR; }
    public static Color getBackgroundColor() { return BACKGROUND_COLOR; }
    public static Color getCardBackground() { return CARD_BACKGROUND; }
    public static Color getTextColor() { return TEXT_COLOR; }
    public static Color getBorderColor() { return BORDER_COLOR; }
    
    /**
     * Mostra il dialog per il caricamento progressi
     */
    private void showProgressUploadDialog() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Usa FileUploadDialog che ora salva sia in documents che in progress
                FileUploadDialog dialog = new FileUploadDialog(this, controller);
                dialog.setVisible(true);
            } catch (Exception e) {
                System.err.println("ERRORE nell'apertura del dialog Carica Progresso:");
                e.printStackTrace();
                
                showToast("Errore nell'apertura del dialog: " + e.getMessage(), TOAST_TYPE_ERROR);
            }
        });
    }
    
    /**
     * Mostra il dialog per la revisione progressi (giudici)
     */
    private void showJudgeProgressReviewDialog() {
        if (controller.getCurrentUser() == null || !controller.getCurrentUser().isGiudice()) {
            showToast("Accesso negato: solo i giudici possono rivedere i progressi", TOAST_TYPE_ERROR);
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            JudgeProgressReviewDialog dialog = new JudgeProgressReviewDialog(this, controller);
            dialog.setVisible(true);
        });
    }
    
    /**
     * Mostra il dialog per le classifiche hackathon
     */
    private void showHackathonRankingDialog() {
        // Seleziona hackathon da visualizzare
        List<Hackathon> hackathons = controller.getTuttiHackathon();
        if (hackathons.isEmpty()) {
            showToast("Nessun hackathon trovato", TOAST_TYPE_ERROR);
            return;
        }
        
        // Se c'è solo un hackathon, mostralo direttamente
        if (hackathons.size() == 1) {
            SwingUtilities.invokeLater(() -> {
                HackathonRankingDialog dialog = new HackathonRankingDialog(this, controller, hackathons.get(0));
                dialog.setVisible(true);
            });
            return;
        }
        
        // Altrimenti permetti di scegliere
        Hackathon[] hackathonArray = hackathons.toArray(new Hackathon[0]);
        Hackathon selected = (Hackathon) JOptionPane.showInputDialog(
            this,
            "Seleziona l'hackathon per visualizzare la classifica:",
            "Seleziona Hackathon",
            JOptionPane.QUESTION_MESSAGE,
            null,
            hackathonArray,
            hackathonArray[0]
        );
        
        if (selected != null) {
            SwingUtilities.invokeLater(() -> {
                HackathonRankingDialog dialog = new HackathonRankingDialog(this, controller, selected);
                dialog.setVisible(true);
            });
        }
    }
    
    /**
     * Mostra il dialog per lo stato dei team
     */
    private void showTeamStatusDialog() {
        SwingUtilities.invokeLater(() -> {
            TeamStatusDialog dialog = new TeamStatusDialog(this, controller);
            dialog.setVisible(true);
        });
    }
} 
