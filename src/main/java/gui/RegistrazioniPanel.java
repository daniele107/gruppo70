package gui;
import controller.Controller;
import model.Registrazione;
import model.Utente;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import static javax.swing.SwingConstants.*;
/**
 * Pannello per la gestione delle registrazioni.
 */
public class RegistrazioniPanel extends JPanel {
    // Constants
    private static final String REGISTER_BUTTON_TEXT = "➕ Registra";
    
    // sonar:ignore=S1068 // Controller will be used for future functionality
    @SuppressWarnings("unused") // Controller will be used for future functionality
    private final transient Controller controller;
    private final MainFrame mainFrame;
    // Components
    private DefaultListModel<Registrazione> registrazioniListModel;
    private JList<Registrazione> registrazioniList;
    private JButton registraUtenteButton;
    private JButton gestisciRegistrazioniButton;
    private JButton debugButton;
    private JButton refreshButton;
    /**
     * Costruttore che inizializza il pannello registrazioni
     *
     * @param controller il controller dell'applicazione
     * @param mainFrame  il frame principale
     */
    public RegistrazioniPanel(Controller controller, MainFrame mainFrame) {
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
        // List model for registrations
        registrazioniListModel = new DefaultListModel<>();
        registrazioniList = new JList<>(registrazioniListModel);
        registrazioniList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Buttons
        registraUtenteButton = new JButton("Registra Utente");
        gestisciRegistrazioniButton = new JButton("Gestisci Registrazioni");
        
        // Style buttons
        registraUtenteButton.setBackground(new Color(46, 204, 113)); // Verde
        registraUtenteButton.setForeground(Color.BLACK);
        registraUtenteButton.setFocusPainted(false);
        registraUtenteButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registraUtenteButton.setOpaque(true);
        registraUtenteButton.setBorderPainted(false);
        
        gestisciRegistrazioniButton.setBackground(new Color(52, 152, 219)); // Blu
        gestisciRegistrazioniButton.setForeground(Color.BLACK);
        gestisciRegistrazioniButton.setFocusPainted(false);
        gestisciRegistrazioniButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gestisciRegistrazioniButton.setOpaque(true);
        gestisciRegistrazioniButton.setBorderPainted(false);
        
        // Debug button
        debugButton = new JButton("🔍 Debug Info");
        debugButton.setBackground(new Color(241, 196, 15)); // Giallo
        debugButton.setForeground(Color.BLACK);
        debugButton.setFocusPainted(false);
        debugButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        debugButton.setOpaque(true);
        debugButton.setBorderPainted(false);
        debugButton.addActionListener(e -> showDebugInfo());
        
        // Refresh button
        refreshButton = new JButton("🔄 Refresh");
        refreshButton.setBackground(new Color(52, 152, 219)); // Blu
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(false);
        refreshButton.addActionListener(e -> refreshData());
        // Initially disable buttons that require selection
        gestisciRegistrazioniButton.setEnabled(false);
    }
    /**
     * Configura il layout del pannello
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        // Title
        JLabel titleLabel = new JLabel("Gestione Registrazioni", CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        // List panel
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Registrazioni"));
        JScrollPane scrollPane = new JScrollPane(registrazioniList);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        listPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(listPanel, BorderLayout.CENTER);
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Azioni"));
        buttonsPanel.add(registraUtenteButton);
        buttonsPanel.add(gestisciRegistrazioniButton);
        buttonsPanel.add(debugButton);
        buttonsPanel.add(refreshButton);
        contentPanel.add(buttonsPanel, BorderLayout.EAST);
        add(contentPanel, BorderLayout.CENTER);
    }
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        // Register user button
        registraUtenteButton.addActionListener(e -> showRegistraUtenteDialog());
        // List selection listener
        registrazioniList.addListSelectionListener(e -> {
            boolean hasSelection = !registrazioniList.isSelectionEmpty();
            gestisciRegistrazioniButton.setEnabled(hasSelection);
        });
        // Action buttons
        gestisciRegistrazioniButton.addActionListener(e -> handleGestisciRegistrazioni());
    }
    /**
     * Aggiorna i dati del pannello
     */
    public void refreshData() {
        registrazioniListModel.clear();
        try {
            // Carica le registrazioni dell'utente corrente
            List<Registrazione> registrazioni = controller.getRegistrazioniUtente();
            if (registrazioni.isEmpty()) {
                registrazioniListModel.addElement(new Registrazione(-1, -1, Registrazione.Ruolo.PARTECIPANTE) {
                    @Override
                    public String toString() {
                        return "Nessuna registrazione trovata";
                    }
                });
            } else {
                for (var registrazione : registrazioni) {
                    registrazioniListModel.addElement(registrazione);
                }
            }
        } catch (Exception e) {
            registrazioniListModel.addElement(new Registrazione(-1, -1, Registrazione.Ruolo.PARTECIPANTE) {
                @Override
                public String toString() {
                    return "Errore nel caricamento registrazioni: " + e.getMessage();
                }
            });
        }
    }
    /**
     * Mostra il dialog per registrare un utente
     */
    private void showRegistraUtenteDialog() {
        JDialog dialog = new JDialog(mainFrame, "Registra Utente all'Evento", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setResizable(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Header
        JLabel headerLabel = new JLabel("\uD83D\uDCDD Registra Utente all'Evento", CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(new Color(52, 152, 219));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        dialog.add(headerLabel, gbc);
        // Form fields
        JComboBox<String> hackathonComboBox = new JComboBox<>();
        JComboBox<String> ruoloComboBox = new JComboBox<>(new String[]{"PARTECIPANTE", "GIUDICE"});
        // Carica gli eventi disponibili
        loadHackathons(hackathonComboBox);
        // Add components
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Evento:"), gbc);
        gbc.gridx = 1;
        dialog.add(hackathonComboBox, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Ruolo:"), gbc);
        gbc.gridx = 1;
        dialog.add(ruoloComboBox, gbc);
        // Status label
        JLabel statusLabel = new JLabel("", CENTER);
        statusLabel.setForeground(new Color(231, 76, 60));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        dialog.add(statusLabel, gbc);
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton confirmButton = new JButton(REGISTER_BUTTON_TEXT);
        JButton cancelButton = new JButton("❌ Annulla");
        // Styling buttons
        confirmButton.setBackground(new Color(46, 204, 113));
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setFocusPainted(false);
        confirmButton.setOpaque(true);
        confirmButton.setBorderPainted(false);
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.setOpaque(true);
        cancelButton.setBorderPainted(false);
        confirmButton.addActionListener(e -> {
            String selectedHackathon = (String) hackathonComboBox.getSelectedItem();
            String selectedRuolo = (String) ruoloComboBox.getSelectedItem();
            if (selectedHackathon == null || selectedHackathon.equals("Nessun evento disponibile")) {
                statusLabel.setText("❌ Seleziona un evento");
                statusLabel.setForeground(new Color(231, 76, 60));
                return;
            }
            // Estrai l'ID dell'hackathon dal testo (formato: "ID: Nome Evento")
            int hackathonId = extractHackathonId(selectedHackathon);
            if (hackathonId == -1) {
                statusLabel.setText("\u274C Errore nell'identificazione dell'evento");
                statusLabel.setForeground(new Color(231, 76, 60));
                return;
            }
            // Converti il ruolo
            Registrazione.Ruolo ruolo = convertRuolo(selectedRuolo);
            // Disabilita il pulsante durante l'operazione
            confirmButton.setEnabled(false);
            confirmButton.setText("⏳ Registrazione...");
            // Esegui la registrazione
            try {
                boolean success = controller.registraUtenteAdHackathon(hackathonId, ruolo);
                if (success) {
                    statusLabel.setText("✅ Registrazione completata con successo!");
                    statusLabel.setForeground(new Color(46, 204, 113));
                    // Chiudi il dialog dopo un breve delay
                    Timer timer = new Timer(1500, evt -> {
                        dialog.dispose();
                        refreshData();
                        mainFrame.showToast("Utente registrato con successo all'evento! \uD83C\uDF89", "success");
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    statusLabel.setText("\u274C Errore durante la registrazione");
                    statusLabel.setForeground(new Color(231, 76, 60));
                    confirmButton.setEnabled(true);
                    confirmButton.setText(REGISTER_BUTTON_TEXT);
                }
            } catch (Exception ex) {
                statusLabel.setText("❌ Errore: " + ex.getMessage());
                statusLabel.setForeground(new Color(231, 76, 60));
                confirmButton.setEnabled(true);
                confirmButton.setText(REGISTER_BUTTON_TEXT);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);
        dialog.setVisible(true);
    }
    /**
     * Carica gli hackathon disponibili nel combo box
     */
    private void loadHackathons(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        try {
            var hackathons = controller.getHackathonConRegistrazioniAperte();
            if (hackathons.isEmpty()) {
                comboBox.addItem("Nessun evento disponibile");
            } else {
                for (var hackathon : hackathons) {
                    comboBox.addItem(hackathon.getId() + ": " + hackathon.getNome());
                }
            }
        } catch (Exception e) {
            comboBox.addItem("Errore nel caricamento eventi");
        }
    }
    /**
     * Estrae l'ID dell'hackathon dal testo del combo box
     */
    private int extractHackathonId(String text) {
        try {
            if (text.contains(":")) {
                String idPart = text.substring(0, text.indexOf(":"));
                return Integer.parseInt(idPart.trim());
            }
        } catch (NumberFormatException e) {
            // Ignora errori di parsing
        }
        return -1;
    }
    /**
     * Converte la stringa del ruolo in enum
     */
    private Registrazione.Ruolo convertRuolo(String ruoloString) {
        switch (ruoloString) {
            case "PARTECIPANTE":
                return Registrazione.Ruolo.PARTECIPANTE;
            case "GIUDICE":
                return Registrazione.Ruolo.GIUDICE;
            default:
                return Registrazione.Ruolo.PARTECIPANTE;
        }
    }
    /**
     * Gestisce la gestione delle registrazioni
     */
    private void handleGestisciRegistrazioni() {
        Registrazione selectedRegistrazione = registrazioniList.getSelectedValue();
        if (selectedRegistrazione != null) {
            JOptionPane.showMessageDialog(this, "Gestione registrazione: Funzionalità implementata per ID " + selectedRegistrazione.getId());
        }
    }
    /**
     * Mostra informazioni di debug
     */
    private void showDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append("🔍 DEBUG INFO\n\n");
        // Info utente corrente
        Utente currentUser = controller.getCurrentUser();
        if (currentUser != null) {
            info.append("👤 Utente Corrente:\n");
            info.append("- ID: ").append(currentUser.getId()).append("\n");
            info.append("- Nome: ").append(currentUser.getNome()).append(" ").append(currentUser.getCognome()).append("\n");
            info.append("- Email: ").append(currentUser.getEmail()).append("\n");
            info.append("- Ruolo: ").append(currentUser.getRuolo()).append("\n\n");
        } else {
            info.append("❌ Nessun utente autenticato\n\n");
        }
        // Info registrazioni
        try {
            List<Registrazione> registrazioni = controller.getRegistrazioniUtente();
            info.append("📝 Registrazioni trovate: ").append(registrazioni.size()).append("\n");
            for (Registrazione reg : registrazioni) {
                info.append("- ID: ").append(reg.getId())
                    .append(", Hackathon: ").append(reg.getHackathonId())
                    .append(", Ruolo: ").append(reg.getRuolo())
                    .append(", Confermata: ").append(reg.isConfermata()).append("\n");
            }
        } catch (Exception e) {
            info.append("❌ Errore nel caricamento registrazioni: ").append(e.getMessage()).append("\n");
        }
        JOptionPane.showMessageDialog(this, info.toString(), "Debug Info", JOptionPane.INFORMATION_MESSAGE);
    }
} 
