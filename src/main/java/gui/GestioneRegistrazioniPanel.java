package gui;
import controller.Controller;
import database.DataAccessException;
import model.Registrazione;
import model.Hackathon;
import model.Utente;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import static javax.swing.SwingConstants.*;
/**
 * Pannello per la gestione delle registrazioni da parte degli organizzatori.
 * Permette di visualizzare e confermare le registrazioni degli utenti agli hackathon.
 */
public class GestioneRegistrazioniPanel extends JPanel {
    // Costanti per stringhe duplicate
    private static final String TUTTI_GLI_EVENTI = "Tutti gli eventi";
    private static final String ERROR_TOAST = "error";
    
    private final transient Controller controller;
    private final MainFrame mainFrame;
    // Components
    private DefaultListModel<Registrazione> registrazioniListModel;
    private JList<Registrazione> registrazioniList;
    private JComboBox<String> hackathonFilterComboBox;
    private JButton confermaButton;
    private JButton rifiutaButton;
    private JButton refreshButton;
    private JLabel statusLabel;
    /**
     * Costruttore che inizializza il pannello gestione registrazioni
     *
     * @param controller il controller dell'applicazione
     * @param mainFrame  il frame principale
     */
    public GestioneRegistrazioniPanel(Controller controller, MainFrame mainFrame) {
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
        // Filter combo box
        hackathonFilterComboBox = new JComboBox<>();
        hackathonFilterComboBox.addItem(TUTTI_GLI_EVENTI);
        // Buttons
        confermaButton = new JButton("\u2705 Conferma Registrazione");
        rifiutaButton = new JButton("\u274C Rifiuta Registrazione");
        refreshButton = new JButton("\uD83D\uDD04 Refresh");
        
        // Style buttons
        confermaButton.setBackground(new Color(46, 204, 113)); // Verde
        confermaButton.setForeground(Color.BLACK);
        confermaButton.setFocusPainted(false);
        confermaButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        confermaButton.setOpaque(true);
        confermaButton.setBorderPainted(false);
        
        rifiutaButton.setBackground(new Color(231, 76, 60)); // Rosso
        rifiutaButton.setForeground(Color.BLACK);
        rifiutaButton.setFocusPainted(false);
        rifiutaButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rifiutaButton.setOpaque(true);
        rifiutaButton.setBorderPainted(false);
        
        refreshButton.setBackground(new Color(52, 152, 219)); // Blu
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(false);
        
        // Status label
        statusLabel = new JLabel("Seleziona un evento per visualizzare le registrazioni", CENTER);
        statusLabel.setForeground(new Color(52, 152, 219));
        // Initially disable buttons that require selection
        confermaButton.setEnabled(false);
        rifiutaButton.setEnabled(false);
    }
    /**
     * Configura il layout del pannello
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        // Title
        JLabel titleLabel = new JLabel("Gestione Registrazioni - Organizzatore", CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 152, 219));
        add(titleLabel, BorderLayout.NORTH);
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtro Eventi"));
        filterPanel.add(new JLabel("Evento:"));
        filterPanel.add(hackathonFilterComboBox);
        filterPanel.add(refreshButton);
        contentPanel.add(filterPanel, BorderLayout.NORTH);
        // List panel
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Registrazioni in Attesa"));
        JScrollPane scrollPane = new JScrollPane(registrazioniList);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        listPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(listPanel, BorderLayout.CENTER);
        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        contentPanel.add(statusPanel, BorderLayout.SOUTH);
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Azioni"));
        buttonsPanel.add(confermaButton);
        buttonsPanel.add(rifiutaButton);
        contentPanel.add(buttonsPanel, BorderLayout.EAST);
        add(contentPanel, BorderLayout.CENTER);
    }
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        // Filter combo box listener
        hackathonFilterComboBox.addActionListener(e -> loadRegistrazioni());
        // List selection listener
        registrazioniList.addListSelectionListener(e -> {
            boolean hasSelection = !registrazioniList.isSelectionEmpty();
            Registrazione selected = registrazioniList.getSelectedValue();
            confermaButton.setEnabled(hasSelection && selected != null && !selected.isConfermata());
            rifiutaButton.setEnabled(hasSelection && selected != null && !selected.isConfermata());
        });
        // Action buttons
        confermaButton.addActionListener(e -> confermaRegistrazione());
        rifiutaButton.addActionListener(e -> rifiutaRegistrazione());
        refreshButton.addActionListener(e -> refreshData());
    }
    /**
     * Aggiorna i dati del pannello
     */
    public void refreshData() {
        loadHackathons();
        loadRegistrazioni();
    }
    /**
     * Carica gli hackathon nel filtro
     */
    private void loadHackathons() {
        hackathonFilterComboBox.removeAllItems();
        hackathonFilterComboBox.addItem(TUTTI_GLI_EVENTI);
        try {
            List<Hackathon> hackathons = controller.getTuttiHackathon();
            for (Hackathon hackathon : hackathons) {
                hackathonFilterComboBox.addItem(hackathon.getId() + ": " + hackathon.getNome());
            }
        } catch (Exception e) {
            statusLabel.setText("\u274C Errore nel caricamento eventi: " + e.getMessage());
            statusLabel.setForeground(new Color(231, 76, 60));
        }
    }
    /**
     * Carica le registrazioni in base al filtro selezionato
     */
    private void loadRegistrazioni() {
        registrazioniListModel.clear();
        try {
            String selectedHackathon = (String) hackathonFilterComboBox.getSelectedItem();
            if (selectedHackathon == null || selectedHackathon.equals(TUTTI_GLI_EVENTI)) {
                loadAllRegistrations();
            } else {
                loadRegistrationsForSpecificHackathon(selectedHackathon);
            }
        } catch (Exception e) {
            handleLoadRegistrationsError(e);
        }
    }
    
    /**
     * Carica tutte le registrazioni non confermate
     */
    private void loadAllRegistrations() throws DataAccessException {
        List<Registrazione> registrazioni = controller.getRegistrazioniNonConfermate();
        if (registrazioni.isEmpty()) {
            addNoRegistrationsMessage("Nessuna registrazione in attesa di conferma");
            updateStatusLabel("\u2705 Tutte le registrazioni sono già confermate", new Color(46, 204, 113));
        } else {
            addRegistrationsToModel(registrazioni);
            updateStatusLabel("\uD83D\uDCDD Trovate " + registrazioni.size() + " registrazioni in attesa di conferma", new Color(52, 152, 219));
        }
    }
    
    /**
     * Carica le registrazioni per un hackathon specifico
     */
    private void loadRegistrationsForSpecificHackathon(String selectedHackathon) throws DataAccessException {
        int hackathonId = extractHackathonId(selectedHackathon);
        if (hackathonId != -1) {
            List<Registrazione> registrazioni = controller.getRegistrazioniNonConfermatePerHackathon(hackathonId);
            if (registrazioni.isEmpty()) {
                addNoRegistrationsMessage("Nessuna registrazione in attesa per questo evento");
                updateStatusLabel("\u2705 Tutte le registrazioni per questo evento sono confermate", new Color(46, 204, 113));
            } else {
                addRegistrationsToModel(registrazioni);
                updateStatusLabel("\uD83D\uDCDD Trovate " + registrazioni.size() + " registrazioni in attesa per questo evento", new Color(52, 152, 219));
            }
        }
    }
    
    /**
     * Aggiunge un messaggio quando non ci sono registrazioni
     */
    private void addNoRegistrationsMessage(String message) {
        registrazioniListModel.addElement(new Registrazione(-1, -1, Registrazione.Ruolo.PARTECIPANTE) {
            @Override
            public String toString() {
                return message;
            }
        });
    }
    
    /**
     * Aggiunge le registrazioni al model
     */
    private void addRegistrationsToModel(List<Registrazione> registrazioni) {
        for (Registrazione registrazione : registrazioni) {
            registrazioniListModel.addElement(registrazione);
        }
    }
    
    /**
     * Aggiorna lo status label con testo e colore
     */
    private void updateStatusLabel(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(color);
    }
    
    /**
     * Gestisce gli errori nel caricamento delle registrazioni
     */
    private void handleLoadRegistrationsError(Exception e) {
        addNoRegistrationsMessage("Errore nel caricamento registrazioni: " + e.getMessage());
        updateStatusLabel("\u274C Errore nel caricamento registrazioni: " + e.getMessage(), new Color(231, 76, 60));
    }
    /**
     * Conferma la registrazione selezionata
     */
    private void confermaRegistrazione() {
        Registrazione selectedRegistrazione = registrazioniList.getSelectedValue();
        if (selectedRegistrazione == null) {
            return;
        }
        int result = JOptionPane.showConfirmDialog(
            this,
            "Confermare la registrazione di " + getUtenteName(selectedRegistrazione.getUtenteId()) + 
            " come " + selectedRegistrazione.getRuolo() + "?",
            "Conferma Registrazione",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (result == JOptionPane.YES_OPTION) {
            try {
                boolean success = controller.confermaRegistrazione(selectedRegistrazione.getId());
                if (success) {
                    mainFrame.showToast("\u2705 Registrazione confermata con successo!", "success");
                    loadRegistrazioni(); // Ricarica la lista
                } else {
                    mainFrame.showToast("\u274C Errore durante la conferma", ERROR_TOAST);
                }
            } catch (Exception e) {
                mainFrame.showToast("\u274C Errore: " + e.getMessage(), ERROR_TOAST);
            }
        }
    }
    /**
     * Rifiuta la registrazione selezionata
     */
    private void rifiutaRegistrazione() {
        Registrazione selectedRegistrazione = registrazioniList.getSelectedValue();
        if (selectedRegistrazione == null) {
            return;
        }
        int result = JOptionPane.showConfirmDialog(
            this,
            "Rifiutare la registrazione di " + getUtenteName(selectedRegistrazione.getUtenteId()) + 
            " come " + selectedRegistrazione.getRuolo() + "?\n\nQuesta azione eliminerà la registrazione.",
            "Rifiuta Registrazione",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (result == JOptionPane.YES_OPTION) {
            try {
                boolean success = controller.rifiutaRegistrazione(selectedRegistrazione.getId());
                if (success) {
                    mainFrame.showToast("\u2705 Registrazione rifiutata", "success");
                    loadRegistrazioni(); // Ricarica la lista
                } else {
                    mainFrame.showToast("❌ Errore durante il rifiuto", ERROR_TOAST);
                }
            } catch (Exception e) {
                mainFrame.showToast("\u274C Errore: " + e.getMessage(), ERROR_TOAST);
            }
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
     * Ottiene il nome dell'utente dato l'ID
     */
    private String getUtenteName(int utenteId) {
        try {
            Utente utente = controller.getUtenteById(utenteId);
            if (utente != null) {
                return utente.getNome() + " " + utente.getCognome();
            }
        } catch (Exception e) {
            // Ignora errori
        }
        return "Utente ID: " + utenteId;
    }
}
