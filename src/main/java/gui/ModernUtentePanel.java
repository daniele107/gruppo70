package gui;
import controller.Controller;
import model.Utente;
import javax.swing.*;
import java.awt.*;
/**
 * Pannello moderno per la gestione del profilo utente con design specifico per ruoli.
 * Implementa un design system professionale con statistiche avanzate per giudici.
 */
public class ModernUtentePanel extends JPanel {
    // Serialization
    private static final long serialVersionUID = 1L;
    
    // Costanti per stringhe duplicate
    private static final String GIUDICE_ROLE = "GIUDICE";
    private static final String ORGANIZZATORE_ROLE = "ORGANIZZATORE";
    private static final String ERRORE_SISTEMA = "Errore Sistema";
    private static final String SEGOE_UI = "Segoe UI";
    private final transient Controller controller;
    // Componenti moderni
    private ModernComponents.ModernCard profileCard;
    private ModernComponents.ModernCard statsCard;
    private ModernComponents.ModernCard actionsCard;
    // Componenti informazioni profilo
    private JLabel nomeCompletoLabel;
    private JLabel emailLabel;
    private JLabel ruoloLabel;
    private JLabel badgeLabel;
    // Componenti statistiche (per giudici)
    private JPanel valutazioniFatteLabel;
    private JPanel eventiValutatiLabel;
    private JPanel mediaVotiLabel;
    private JProgressBar experienceBar;
    // Pulsanti azioni
    private ModernComponents.ModernButton modificaProfiloButton;
    private ModernComponents.ModernButton cambiaPasswordButton;
    private ModernComponents.ModernButton viewStatisticsButton;
    // Colori
    private static final Color PROFILE_ACCENT = new Color(52, 152, 219);
    private static final Color STATS_ACCENT = new Color(46, 204, 113);
    private static final Color ACTIONS_ACCENT = new Color(155, 89, 182);
    public ModernUtentePanel(Controller controller) {
        this.controller = controller;
        setupLayout();
        initializeComponents();
        setupEventHandlers();
        // Aggiorna i dati iniziali
        refreshData();
    }
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(DesignSystem.getBackgroundPrimary());
        setBorder(BorderFactory.createEmptyBorder(
            DesignSystem.SPACE_2XL, DesignSystem.SPACE_2XL, 
            DesignSystem.SPACE_2XL, DesignSystem.SPACE_2XL));
    }
    private void initializeComponents() {
        // Contenuto principale con layout griglia
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(DesignSystem.SPACE_LG, DesignSystem.SPACE_LG, 
                               DesignSystem.SPACE_LG, DesignSystem.SPACE_LG);
        gbc.fill = GridBagConstraints.BOTH;
        // Card profilo (in alto, larghezza completa)
        createProfileCard();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0; gbc.weighty = 0.4;
        mainContent.add(profileCard, gbc);
        // Card statistiche (in basso a sinistra)
        createStatsCard();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.6; gbc.weighty = 0.6;
        mainContent.add(statsCard, gbc);
        // Card azioni (in basso a destra)
        createActionsCard();
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 0.4; gbc.weighty = 0.6;
        mainContent.add(actionsCard, gbc);
        add(mainContent, BorderLayout.CENTER);
    }
    private void createProfileCard() {
        profileCard = new ModernComponents.ModernCard();
        profileCard.setLayout(new BorderLayout());
        profileCard.setBackground(Color.WHITE);
        // Intestazione con icona
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setOpaque(false);
        JLabel headerLabel = new JLabel("Profilo Utente", SVGIcon.createUserIcon(24), SwingConstants.LEFT);
        headerLabel.setFont(DesignSystem.TITLE_LARGE);
        headerLabel.setForeground(PROFILE_ACCENT);
        headerLabel.setIconTextGap(12);
        header.add(headerLabel);
        profileCard.add(header, BorderLayout.NORTH);
        // Contenuto profilo
        JPanel profileContent = new JPanel(new BorderLayout());
        profileContent.setOpaque(false);
        profileContent.setBorder(BorderFactory.createEmptyBorder(
            DesignSystem.SPACE_LG, DesignSystem.SPACE_2XL, 
            DesignSystem.SPACE_LG, DesignSystem.SPACE_2XL));
        // Avatar e informazioni base
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        avatarPanel.setOpaque(false);
        // Avatar (icona utente grande)
        JLabel avatarLabel = new JLabel(SVGIcon.createUserIcon(64));
        avatarLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PROFILE_ACCENT, 3),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        avatarPanel.add(avatarLabel);
        // Pannello informazioni
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        nomeCompletoLabel = new JLabel();
        nomeCompletoLabel.setFont(DesignSystem.TITLE_MEDIUM);
        nomeCompletoLabel.setForeground(DesignSystem.getTextPrimary());
        emailLabel = new JLabel();
        emailLabel.setFont(DesignSystem.BODY_LARGE);
        emailLabel.setForeground(DesignSystem.getTextSecondary());
        ruoloLabel = new JLabel();
        ruoloLabel.setFont(DesignSystem.LABEL_LARGE);
        badgeLabel = new JLabel();
        badgeLabel.setFont(DesignSystem.LABEL_SMALL);
        badgeLabel.setOpaque(true);
        badgeLabel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        infoPanel.add(nomeCompletoLabel);
        infoPanel.add(Box.createVerticalStrut(DesignSystem.SPACE_SM));
        infoPanel.add(emailLabel);
        infoPanel.add(Box.createVerticalStrut(DesignSystem.SPACE_SM));
        infoPanel.add(ruoloLabel);
        infoPanel.add(Box.createVerticalStrut(DesignSystem.SPACE_XS));
        infoPanel.add(badgeLabel);
        avatarPanel.add(infoPanel);
        profileContent.add(avatarPanel, BorderLayout.CENTER);
        profileCard.add(profileContent, BorderLayout.CENTER);
    }
    private void createStatsCard() {
        statsCard = new ModernComponents.ModernCard();
        statsCard.setLayout(new BorderLayout());
        statsCard.setBackground(Color.WHITE);
        // Intestazione
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setOpaque(false);
        JLabel headerLabel = new JLabel("Statistiche", SVGIcon.createStatsIcon(20), SwingConstants.LEFT);
        headerLabel.setFont(DesignSystem.TITLE_MEDIUM);
        headerLabel.setForeground(STATS_ACCENT);
        headerLabel.setIconTextGap(8);
        header.add(headerLabel);
        statsCard.add(header, BorderLayout.NORTH);
        // Contenuto statistiche
        JPanel statsContent = new JPanel();
        statsContent.setLayout(new BoxLayout(statsContent, BoxLayout.Y_AXIS));
        statsContent.setOpaque(false);
        statsContent.setBorder(BorderFactory.createEmptyBorder(
            DesignSystem.SPACE_MD, DesignSystem.SPACE_LG, 
            DesignSystem.SPACE_LG, DesignSystem.SPACE_LG));
        // Statistiche per giudici
        valutazioniFatteLabel = createStatLabel("Valutazioni Fatte", "0");
        eventiValutatiLabel = createStatLabel("Eventi Valutati", "0");
        mediaVotiLabel = createStatLabel("Media Voti Assegnati", "0.0");
        statsContent.add(valutazioniFatteLabel);
        statsContent.add(Box.createVerticalStrut(DesignSystem.SPACE_MD));
        statsContent.add(eventiValutatiLabel);
        statsContent.add(Box.createVerticalStrut(DesignSystem.SPACE_MD));
        statsContent.add(mediaVotiLabel);
        statsContent.add(Box.createVerticalStrut(DesignSystem.SPACE_LG));
        // Barra esperienza
        JLabel experienceLabel = new JLabel("Livello Esperienza");
        experienceLabel.setFont(DesignSystem.LABEL_MEDIUM);
        experienceLabel.setForeground(DesignSystem.getTextSecondary());
        experienceBar = new ModernComponents.ModernProgressBar(0, 100);
        experienceBar.setValue(0);
        experienceBar.setStringPainted(true);
        experienceBar.setString("Novizio");
        statsContent.add(experienceLabel);
        statsContent.add(Box.createVerticalStrut(DesignSystem.SPACE_XS));
        statsContent.add(experienceBar);
        statsCard.add(statsContent, BorderLayout.CENTER);
    }
    private void createActionsCard() {
        actionsCard = new ModernComponents.ModernCard();
        actionsCard.setLayout(new BorderLayout());
        actionsCard.setBackground(Color.WHITE);
        // Intestazione
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setOpaque(false);
        JLabel headerLabel = new JLabel("Azioni", SVGIcon.createSettingsIcon(20), SwingConstants.LEFT);
        headerLabel.setFont(DesignSystem.TITLE_MEDIUM);
        headerLabel.setForeground(ACTIONS_ACCENT);
        headerLabel.setIconTextGap(8);
        header.add(headerLabel);
        actionsCard.add(header, BorderLayout.NORTH);
        // Contenuto azioni
        JPanel actionsContent = new JPanel();
        actionsContent.setLayout(new BoxLayout(actionsContent, BoxLayout.Y_AXIS));
        actionsContent.setOpaque(false);
        actionsContent.setBorder(BorderFactory.createEmptyBorder(
            DesignSystem.SPACE_MD, DesignSystem.SPACE_LG, 
            DesignSystem.SPACE_LG, DesignSystem.SPACE_LG));
        // Pulsanti azioni
        modificaProfiloButton = new ModernComponents.ModernButton("Modifica Profilo");
        modificaProfiloButton.setBackground(DesignSystem.PRIMARY_500);
        modificaProfiloButton.setFont(DesignSystem.LABEL_LARGE);
        modificaProfiloButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        cambiaPasswordButton = new ModernComponents.ModernButton("Cambia Password");
        cambiaPasswordButton.setBackground(DesignSystem.WARNING_500);
        cambiaPasswordButton.setFont(DesignSystem.LABEL_LARGE);
        cambiaPasswordButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        viewStatisticsButton = new ModernComponents.ModernButton("Visualizza Statistiche");
        viewStatisticsButton.setBackground(STATS_ACCENT);
        viewStatisticsButton.setFont(DesignSystem.LABEL_LARGE);
        viewStatisticsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        // Pulsante refresh statistiche
        ModernComponents.ModernButton refreshStatsButton = new ModernComponents.ModernButton("🔄 Aggiorna Statistiche");
        refreshStatsButton.setBackground(PROFILE_ACCENT);
        refreshStatsButton.setFont(DesignSystem.LABEL_MEDIUM);
        refreshStatsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        refreshStatsButton.addActionListener(e -> {
            refreshData();
            JOptionPane.showMessageDialog(this,
                "✅ Statistiche aggiornate con i dati più recenti!",
                "Aggiornamento Completato",
                JOptionPane.INFORMATION_MESSAGE);
        });
        actionsContent.add(modificaProfiloButton);
        actionsContent.add(Box.createVerticalStrut(DesignSystem.SPACE_MD));
        actionsContent.add(cambiaPasswordButton);
        actionsContent.add(Box.createVerticalStrut(DesignSystem.SPACE_MD));
        actionsContent.add(viewStatisticsButton);
        actionsContent.add(Box.createVerticalStrut(DesignSystem.SPACE_SM));
        actionsContent.add(refreshStatsButton);
        actionsContent.add(Box.createVerticalGlue());
        actionsCard.add(actionsContent, BorderLayout.CENTER);
    }
    private JPanel createStatLabel(String title, String value) {
        JPanel statPanel = new JPanel(new BorderLayout());
        statPanel.setOpaque(false);
        statPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(DesignSystem.LABEL_MEDIUM);
        titleLabel.setForeground(DesignSystem.getTextSecondary());
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(DesignSystem.TITLE_SMALL);
        valueLabel.setForeground(STATS_ACCENT);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        statPanel.add(titleLabel, BorderLayout.WEST);
        statPanel.add(valueLabel, BorderLayout.EAST);
        return statPanel;
    }
    private void setupEventHandlers() {
        modificaProfiloButton.addActionListener(e -> showModernModificaProfiloDialog());
        cambiaPasswordButton.addActionListener(e -> showModernCambiaPasswordDialog());
        viewStatisticsButton.addActionListener(e -> showDetailedStatistics());
    }
    public void refreshData() {
        Utente currentUser = controller.getCurrentUser();
        if (currentUser != null) {
            // Aggiorna informazioni profilo
            nomeCompletoLabel.setText(currentUser.getNome() + " " + currentUser.getCognome());
            emailLabel.setText(currentUser.getEmail());
            ruoloLabel.setText("Ruolo: " + currentUser.getRuolo());
            // Aggiorna badge ruolo
            updateRoleBadge(currentUser.getRuolo());
            // Aggiorna statistiche (reali per giudici, dati esempio per altri ruoli)
            updateStatistics(currentUser.getRuolo());
        }
    }
    private void updateRoleBadge(String ruolo) {
        switch (ruolo) {
            case GIUDICE_ROLE:
                badgeLabel.setText("⚖️ GIUDICE ESPERTO");
                badgeLabel.setBackground(new Color(231, 76, 60));
                badgeLabel.setForeground(Color.WHITE);
                break;
            case ORGANIZZATORE_ROLE:
                badgeLabel.setText("👑 ORGANIZZATORE");
                badgeLabel.setBackground(new Color(155, 89, 182));
                badgeLabel.setForeground(Color.WHITE);
                break;
            case "PARTECIPANTE":
                badgeLabel.setText("🚀 PARTECIPANTE");
                badgeLabel.setBackground(new Color(52, 152, 219));
                badgeLabel.setForeground(Color.WHITE);
                break;
            default:
                badgeLabel.setText("👤 UTENTE");
                badgeLabel.setBackground(DesignSystem.getTextSecondary());
                badgeLabel.setForeground(Color.WHITE);
        }
    }
    private void updateStatistics(String ruolo) {
        try {
        if (GIUDICE_ROLE.equals(ruolo)) {
                // Recupera statistiche reali per il giudice
                Controller.GiudiceStats stats = controller.getStatisticheGiudice(controller.getCurrentUser().getId());
                ((JLabel)valutazioniFatteLabel.getComponent(1)).setText(String.valueOf(stats.valutazioniFatte));
                ((JLabel)eventiValutatiLabel.getComponent(1)).setText(String.valueOf(stats.eventiValutati));
                ((JLabel)mediaVotiLabel.getComponent(1)).setText(String.valueOf(Math.round(stats.mediaVoti * 10.0) / 10.0));
            // Aggiorna esperienza basata sulle valutazioni
                int esperienza = Math.min(100, stats.valutazioniFatte * 3);
                experienceBar.setValue(esperienza);
                experienceBar.setString("Esperto (" + esperienza + "%)");
            viewStatisticsButton.setVisible(true);
        } else if (ORGANIZZATORE_ROLE.equals(ruolo)) {
                // Per ora manteniamo dati di esempio per organizzatore
            ((JLabel)valutazioniFatteLabel.getComponent(0)).setText("Eventi Organizzati");
            ((JLabel)valutazioniFatteLabel.getComponent(1)).setText("3");
            ((JLabel)eventiValutatiLabel.getComponent(0)).setText("Partecipanti Totali");
            ((JLabel)eventiValutatiLabel.getComponent(1)).setText("127");
            ((JLabel)mediaVotiLabel.getComponent(0)).setText("Rating Medio");
            ((JLabel)mediaVotiLabel.getComponent(1)).setText("4.6");
            experienceBar.setValue(80);
            experienceBar.setString("Organizzatore Esperto (80%)");
            viewStatisticsButton.setVisible(true);
        } else {
                // Statistiche partecipante - dati di esempio per ora
            ((JLabel)valutazioniFatteLabel.getComponent(0)).setText("Hackathon Partecipati");
            ((JLabel)valutazioniFatteLabel.getComponent(1)).setText("2");
            ((JLabel)eventiValutatiLabel.getComponent(0)).setText("Team Formati");
            ((JLabel)eventiValutatiLabel.getComponent(1)).setText("1");
            ((JLabel)mediaVotiLabel.getComponent(0)).setText("Miglior Posizione");
            ((JLabel)mediaVotiLabel.getComponent(1)).setText("3°");
            experienceBar.setValue(30);
            experienceBar.setString("In Crescita (30%)");
            viewStatisticsButton.setVisible(false);
            }
        } catch (Exception e) {
            // Fallback ai valori di default
            if (GIUDICE_ROLE.equals(ruolo)) {
                ((JLabel)valutazioniFatteLabel.getComponent(1)).setText("0");
                ((JLabel)eventiValutatiLabel.getComponent(1)).setText("0");
                ((JLabel)mediaVotiLabel.getComponent(1)).setText("0.0");
                experienceBar.setValue(0);
                experienceBar.setString("Nessuna esperienza");
                viewStatisticsButton.setVisible(true);
            }
        }
    }
    private void showModernModificaProfiloDialog() {
        try {
            Utente currentUser = controller.getCurrentUser();
            if (currentUser == null) {
        JOptionPane.showMessageDialog(this, 
                    "Errore: Nessun utente autenticato.",
                    ERRORE_SISTEMA,
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Crea dialog per modifica profilo
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                                       "Modifica Profilo", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(450, 300);
            dialog.setLocationRelativeTo(this);
            // Titolo
            JLabel titleLabel = new JLabel("✏️ Modifica Profilo", SwingConstants.CENTER);
            titleLabel.setFont(new Font(SEGOE_UI, Font.BOLD, 18));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
            dialog.add(titleLabel, BorderLayout.NORTH);
            // Pannello form
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 15, 10, 15);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            // Campi input con valori attuali
            JTextField nomeField = new JTextField(currentUser.getNome(), 20);
            JTextField cognomeField = new JTextField(currentUser.getCognome(), 20);
            JTextField emailField = new JTextField(currentUser.getEmail(), 20);
            // Labels
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Nome:"), gbc);
            gbc.gridx = 1;
            formPanel.add(nomeField, gbc);
            gbc.gridx = 0; gbc.gridy = 1;
            formPanel.add(new JLabel("Cognome:"), gbc);
            gbc.gridx = 1;
            formPanel.add(cognomeField, gbc);
            gbc.gridx = 0; gbc.gridy = 2;
            formPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1;
            formPanel.add(emailField, gbc);
            // Info login (non modificabile)
            gbc.gridx = 0; gbc.gridy = 3;
            formPanel.add(new JLabel("Login:"), gbc);
            gbc.gridx = 1;
            JTextField loginField = new JTextField(currentUser.getLogin(), 20);
            loginField.setEditable(false);
            loginField.setBackground(Color.LIGHT_GRAY);
            formPanel.add(loginField, gbc);
            dialog.add(formPanel, BorderLayout.CENTER);
            // Pannello pulsanti
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton salvaButton = new JButton("💾 Salva Modifiche");
            JButton annullaButton = new JButton("❌ Annulla");
            // Styling pulsanti
            salvaButton.setBackground(new Color(46, 204, 113));
            salvaButton.setForeground(Color.WHITE);
            salvaButton.setFocusPainted(false);
            salvaButton.setFont(new Font(SEGOE_UI, Font.BOLD, 12));
            annullaButton.setBackground(new Color(231, 76, 60));
            annullaButton.setForeground(Color.WHITE);
            annullaButton.setFocusPainted(false);
            annullaButton.setFont(new Font(SEGOE_UI, Font.BOLD, 12));
            salvaButton.addActionListener(e -> {
                String nuovoNome = nomeField.getText().trim();
                String nuovoCognome = cognomeField.getText().trim();
                String nuovaEmail = emailField.getText().trim();
                try {
                    boolean success = controller.aggiornaProfilo(nuovoNome, nuovoCognome, nuovaEmail);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                            "✅ Profilo aggiornato con successo!\n\n" +
                            "Nuovi dati:\n" +
                            "• Nome: " + nuovoNome + "\n" +
                            "• Cognome: " + nuovoCognome + "\n" +
                            "• Email: " + nuovaEmail,
                            "Profilo Aggiornato",
            JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        // Aggiorna il pannello con i nuovi dati
                        refreshData();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "❌ Errore durante l'aggiornamento del profilo.",
                            "Errore Aggiornamento",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "❌ " + ex.getMessage(),
                        "Errore Validazione",
                        JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "❌ Errore durante l'aggiornamento: " + ex.getMessage(),
                        ERRORE_SISTEMA,
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            annullaButton.addActionListener(e -> dialog.dispose());
            buttonPanel.add(salvaButton);
            buttonPanel.add(annullaButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Errore durante l'apertura del dialog modifica profilo: " + e.getMessage(),
                ERRORE_SISTEMA,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showModernCambiaPasswordDialog() {
        try {
            Utente currentUser = controller.getCurrentUser();
            if (currentUser == null) {
        JOptionPane.showMessageDialog(this, 
                    "Errore: Nessun utente autenticato.",
                    ERRORE_SISTEMA,
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Crea dialog per cambio password
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                                       "Cambia Password", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(450, 280);
            dialog.setLocationRelativeTo(this);
            // Titolo
            JLabel titleLabel = new JLabel("🔒 Cambia Password", SwingConstants.CENTER);
            titleLabel.setFont(new Font(SEGOE_UI, Font.BOLD, 18));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
            dialog.add(titleLabel, BorderLayout.NORTH);
            // Pannello form
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 15, 10, 15);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            // Campi password
            JPasswordField passwordAttualeField = new JPasswordField(20);
            JPasswordField nuovaPasswordField = new JPasswordField(20);
            JPasswordField confermaPasswordField = new JPasswordField(20);
            // Labels
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Password Attuale:"), gbc);
            gbc.gridx = 1;
            formPanel.add(passwordAttualeField, gbc);
            gbc.gridx = 0; gbc.gridy = 1;
            formPanel.add(new JLabel("Nuova Password:"), gbc);
            gbc.gridx = 1;
            formPanel.add(nuovaPasswordField, gbc);
            gbc.gridx = 0; gbc.gridy = 2;
            formPanel.add(new JLabel("Conferma Password:"), gbc);
            gbc.gridx = 1;
            formPanel.add(confermaPasswordField, gbc);
            // Info sicurezza
            gbc.gridx = 0; gbc.gridy = 3;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(5, 15, 5, 15);
            JLabel infoLabel = new JLabel("💡 La nuova password deve avere almeno 8 caratteri");
            infoLabel.setFont(new Font(SEGOE_UI, Font.ITALIC, 11));
            infoLabel.setForeground(Color.GRAY);
            formPanel.add(infoLabel, gbc);
            dialog.add(formPanel, BorderLayout.CENTER);
            // Pannello pulsanti
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton cambiaButton = new JButton("🔒 Cambia Password");
            JButton annullaButton = new JButton("❌ Annulla");
            // Styling pulsanti - testo NERO per visibilità
            cambiaButton.setBackground(new Color(70, 130, 180)); // Blu scuro
            cambiaButton.setForeground(Color.BLACK); // Testo NERO
            cambiaButton.setFocusPainted(false);
            cambiaButton.setFont(new Font(SEGOE_UI, Font.BOLD, 14));
            cambiaButton.setOpaque(true);
            cambiaButton.setBorderPainted(true);
            cambiaButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

            annullaButton.setBackground(new Color(178, 34, 34)); // Rosso scuro
            annullaButton.setForeground(Color.BLACK); // Testo NERO
            annullaButton.setFocusPainted(false);
            annullaButton.setFont(new Font(SEGOE_UI, Font.BOLD, 14));
            annullaButton.setOpaque(true);
            annullaButton.setBorderPainted(true);
            annullaButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            cambiaButton.addActionListener(e -> {
                String passwordAttuale = new String(passwordAttualeField.getPassword());
                String nuovaPassword = new String(nuovaPasswordField.getPassword());
                String confermaPassword = new String(confermaPasswordField.getPassword());
                try {
                    boolean success = controller.cambiaPassword(passwordAttuale, nuovaPassword, confermaPassword);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                            "✅ Password cambiata con successo!\n\n" +
                            "La prossima volta che effettuerai l'accesso,\n" +
                            "utilizza la nuova password.",
                            "Password Aggiornata",
            JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        // Non è necessario aggiornare il pannello per il cambio password
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "❌ Errore durante il cambio della password.",
                            "Errore Cambio Password",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "❌ " + ex.getMessage(),
                        "Errore Validazione",
                        JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                        "❌ Errore durante il cambio password: " + ex.getMessage(),
                        ERRORE_SISTEMA,
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            annullaButton.addActionListener(e -> dialog.dispose());
            buttonPanel.add(cambiaButton);
            buttonPanel.add(annullaButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            // Gestione eventi speciali
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowOpened(java.awt.event.WindowEvent windowEvent) {
                    passwordAttualeField.requestFocus();
                }
            });
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Errore durante l'apertura del dialog cambio password: " + e.getMessage(),
                ERRORE_SISTEMA,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showDetailedStatistics() {
        try {
            String ruolo = controller.getCurrentUser().getRuolo();
            if (GIUDICE_ROLE.equals(ruolo)) {
                showGiudiceDetailedStatistics();
            } else if (ORGANIZZATORE_ROLE.equals(ruolo)) {
                showOrganizzatoreDetailedStatistics();
            } else {
                showPartecipanteDetailedStatistics();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Errore durante la visualizzazione delle statistiche dettagliate: " + e.getMessage(),
                "Errore Statistiche",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showGiudiceDetailedStatistics() {
        Controller.GiudiceStats stats = controller.getStatisticheGiudice(controller.getCurrentUser().getId());
        // Crea dialog dettagliato
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                                   "Statistiche Dettagliate - Giudice", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        // Titolo
        JLabel titleLabel = new JLabel("📊 Statistiche Giudice: " + controller.getCurrentUser().getNome(),
                                     SwingConstants.CENTER);
        titleLabel.setFont(new Font(SEGOE_UI, Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        dialog.add(titleLabel, BorderLayout.NORTH);
        // Pannello statistiche
        JPanel statsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        // Statistiche principali
        gbc.gridx = 0; gbc.gridy = 0;
        statsPanel.add(new JLabel("🏆 Valutazioni Totali:"), gbc);
        gbc.gridx = 1;
        statsPanel.add(new JLabel(String.valueOf(stats.valutazioniFatte)), gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        statsPanel.add(new JLabel("🎯 Eventi Valutati:"), gbc);
        gbc.gridx = 1;
        statsPanel.add(new JLabel(String.valueOf(stats.eventiValutati)), gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        statsPanel.add(new JLabel("📈 Media Voti Assegnati:"), gbc);
        gbc.gridx = 1;
        statsPanel.add(new JLabel(String.valueOf(Math.round(stats.mediaVoti * 100.0) / 100.0) + "/10"), gbc);
        // Statistiche aggiuntive
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 5, 15);
        if (stats.valutazioniFatte > 0) {
            String livello;
            if (stats.valutazioniFatte >= 50) {
                livello = "🎖️ Giudice Maestro";
            } else if (stats.valutazioniFatte >= 25) {
                livello = "🏅 Giudice Esperto";
            } else if (stats.valutazioniFatte >= 10) {
                livello = "🥈 Giudice Qualificato";
            } else {
                livello = "🥉 Giudice Principiante";
            }
            statsPanel.add(new JLabel("🏆 Livello: " + livello), gbc);
            gbc.gridy = 4;
            double accuratezza;
            if (stats.mediaVoti > 7) {
                accuratezza = 90;
            } else if (stats.mediaVoti > 5) {
                accuratezza = 75;
            } else {
                accuratezza = 60;
            }
            statsPanel.add(new JLabel("🎯 Accuratezza Valutativa: " + Math.round(accuratezza) + "%"), gbc);
        } else {
            statsPanel.add(new JLabel("ℹ️ Nessuna valutazione effettuata ancora"), gbc);
        }
        dialog.add(statsPanel, BorderLayout.CENTER);
        // Pulsante chiusura
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("Chiudi");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    private void showOrganizzatoreDetailedStatistics() {
        // Per ora implementazione semplice
        String msgOrg = """
            📊 Statistiche Organizzatore

            🏆 Eventi Organizzati: 3
            👥 Partecipanti Totali: 127
            ⭐ Rating Medio: 4.6/5

            🚀 Statistiche dettagliate in sviluppo...
            """;
        JOptionPane.showMessageDialog(this,
            msgOrg,
            "Statistiche Organizzatore",
            JOptionPane.INFORMATION_MESSAGE);
    }
    private void showPartecipanteDetailedStatistics() {
        // Per ora implementazione semplice
        String msgPar = """
            📊 Statistiche Partecipante

            🏆 Hackathon Partecipati: 2
            👥 Team Formati: 1
            🥇 Miglior Posizione: 3°

            🚀 Statistiche dettagliate in sviluppo...
            """;
        JOptionPane.showMessageDialog(this, 
            msgPar,
            "Statistiche Partecipante",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
