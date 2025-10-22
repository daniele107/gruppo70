package gui;
import controller.Controller;
import controller.EventManagementException;
import model.Hackathon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.SwingConstants.*;
/**
 * Pannello per la gestione degli eventi/hackathon.
 * Implementa un design moderno e responsive.
 */
public class EventiPanel extends JPanel {
    private final transient Controller controller;
    private final transient MainFrame mainFrame;
    // Components
    private transient JPanel eventiContainer;
    private transient JButton creaEventoButton;
    private transient JButton apriRegistrazioniButton;
    private transient JButton chiudiRegistrazioniButton;
    private transient JButton avviaEventoButton;
    private transient JButton concludeEventoButton;
    private transient JButton eliminaConclusiButton;
    private transient JButton refreshButton;
    // Selected event
    private transient Hackathon selectedEvento;
    // Modern styling
    private static final String FONT_FAMILY = "Segoe UI";
    private static final String TITLE_GESTIONE_EVENTI = "Gestione Eventi";
    private static final String SUBTITLE_HACKATHON = "Crea e gestisci i tuoi hackathon";
    private static final String TITLE_EVENTI_DISPONIBILI = "Eventi Disponibili";
    private static final String TITLE_AZIONI = "Azioni";
    private static final String ERROR_TEXT = "error";
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(52, 73, 94);
    private static final Color BORDER_COLOR = new Color(189, 195, 199);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    private static final Color INFO_COLOR = new Color(52, 152, 219);
    private static final Logger LOGGER = Logger.getLogger(EventiPanel.class.getName());
    private static final String TOAST_SUCCESS = "success";
    private static final String AUTH_ERROR_TITLE = "Errore di Autenticazione";
    private static final String ERRORE_TITLE = "Errore";
    /**
     * Costruttore che inizializza il pannello eventi
     */
    public EventiPanel(Controller controller, MainFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        // Carica automaticamente gli eventi all'inizializzazione
        SwingUtilities.invokeLater(this::refreshData);
        setLayout(new BorderLayout());
        setBackground(MainFrame.getBackgroundColor());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    /**
     * Inizializza i componenti del pannello
     */
    private void initializeComponents() {
        // Eventi container
        eventiContainer = new JPanel();
        eventiContainer.setLayout(new BoxLayout(eventiContainer, BoxLayout.Y_AXIS));
        eventiContainer.setBackground(MainFrame.getBackgroundColor());
        // Buttons
        creaEventoButton = createStyledButtonWithTextColor("Crea Nuovo Evento", Color.WHITE, TEXT_COLOR);
        apriRegistrazioniButton = createStyledButton("Apri Registrazioni", INFO_COLOR);
        chiudiRegistrazioniButton = createStyledButton("Chiudi Registrazioni", WARNING_COLOR);
        avviaEventoButton = createStyledButton("Avvia Evento", SUCCESS_COLOR);
        concludeEventoButton = createStyledButton("Conclude Evento", ERROR_COLOR);
        refreshButton = createStyledButtonWithTextColor("\uD83D\uDD04 Aggiorna", new Color(52, 152, 219), Color.BLACK);
        eliminaConclusiButton = createStyledButtonWithTextColor("\uD83D\uDDD1\uFE0F Elimina Conclusi", new Color(192, 57, 43), new Color(64, 64, 64));
        // Initially disable buttons that require selection
        apriRegistrazioniButton.setEnabled(false);
        chiudiRegistrazioniButton.setEnabled(false);
        avviaEventoButton.setEnabled(false);
        concludeEventoButton.setEnabled(false);
        refreshButton.setEnabled(true);
        eliminaConclusiButton.setEnabled(true);
    }
    /**
     * Crea un bottone stilizzato con colore testo personalizzato
     */
    private JButton createStyledButtonWithTextColor(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        button.setForeground(textColor);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        return button;
    }
    /**
     * Crea un bottone stilizzato
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        return button;
    }
    /**
     * Configura il layout del pannello con supporto responsive
     */
    private void setupLayout() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel(TITLE_GESTIONE_EVENTI, SVGIcon.createEventiIcon(24), LEFT);
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        JLabel subtitleLabel = new JLabel(SUBTITLE_HACKATHON, LEFT);
        subtitleLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_COLOR.brighter());
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        // Pannello pulsanti a destra
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(creaEventoButton);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        // Main content con layout responsive
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        // Events list
        JPanel eventsPanel = new JPanel(new BorderLayout());
        eventsPanel.setOpaque(false);
        eventsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            TITLE_EVENTI_DISPONIBILI,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(FONT_FAMILY, Font.BOLD, 14),
            TEXT_COLOR
        ));
        JScrollPane scrollPane = new JScrollPane(eventiContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        eventsPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(eventsPanel, BorderLayout.CENTER);
                            contentPanel.add(eventsPanel, BorderLayout.CENTER);
                    // Actions panel - responsive
                    JPanel actionsPanel = new JPanel();
                    actionsPanel.setLayout(new BorderLayout());
                    actionsPanel.setOpaque(false);
                    actionsPanel.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR),
                        TITLE_AZIONI,
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font(FONT_FAMILY, Font.BOLD, 14),
                        TEXT_COLOR
                    ));
                    // Create a grid panel for buttons (3 columns, 2 rows)
                    JPanel buttonsGridPanel = new JPanel(new GridLayout(2, 3, 15, 10));
                    buttonsGridPanel.setOpaque(false);
                    buttonsGridPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
                    // Add buttons to grid
                    buttonsGridPanel.add(apriRegistrazioniButton);
                    buttonsGridPanel.add(chiudiRegistrazioniButton);
                    buttonsGridPanel.add(avviaEventoButton);
                    buttonsGridPanel.add(concludeEventoButton);
                    buttonsGridPanel.add(eliminaConclusiButton);

                    // PULSANTE PER ESEGUIRE L'AGGIORNAMENTO DATE
                    JButton updateDatesButton = new JButton("🔄 Aggiorna Date Test");
                    updateDatesButton.setBackground(new Color(255, 140, 0)); // Arancione scuro
                    updateDatesButton.setForeground(Color.BLACK); // Testo nero per contrasto
                    updateDatesButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 14)); // Font più grande
                    updateDatesButton.setBorder(BorderFactory.createRaisedBevelBorder());
                    updateDatesButton.setToolTipText("Esegue l'aggiornamento automatico delle date nel database per testare l'eliminazione");
                    updateDatesButton.addActionListener(e -> {
                        try {
                            boolean success = controller.eseguiAggiornamentoDateTest();
                            if (success) {
                                JOptionPane.showMessageDialog(this, "Date aggiornate con successo!\nOra puoi testare 'Elimina Conclusi'", "Aggiornamento Completato", JOptionPane.INFORMATION_MESSAGE);
                                // La lista si aggiorna automaticamente quando il pannello viene ridisegnato
                            } else {
                                JOptionPane.showMessageDialog(this, "Nessun evento da aggiornare o errore durante l'operazione", "Avviso", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), ERRORE_TITLE, JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    buttonsGridPanel.add(updateDatesButton);

                    // Center the buttons grid
                    JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    centerPanel.setOpaque(false);
                    centerPanel.add(buttonsGridPanel);
                    actionsPanel.add(centerPanel, BorderLayout.CENTER);
                    add(actionsPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);
        // Listener per ridimensionamento
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateResponsiveLayout();
            }
        });
    }
    /**
     * Aggiorna il layout responsive
     */
    private void updateResponsiveLayout() {
        int width = getWidth();
        // Se la larghezza è 0 (inizializzazione), usa un valore di default
        if (width == 0) {
            width = 1200; // Assume schermo grande per default
        }
        // Layout responsive basato sulla larghezza
        if (width < 1000) {
            // Layout compatto per schermi piccoli
            setBorder(new EmptyBorder(10, 10, 10, 10));
        } else {
            // Layout normale per schermi grandi
            setBorder(new EmptyBorder(20, 20, 20, 20));
        }
        revalidate();
        repaint();
    }
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        // Create event button
        creaEventoButton.addActionListener(e -> showCreaEventoDialog());
        // Action buttons
        apriRegistrazioniButton.addActionListener(e -> handleApriRegistrazioni());
        chiudiRegistrazioniButton.addActionListener(e -> handleChiudiRegistrazioni());
        avviaEventoButton.addActionListener(e -> handleAvviaEvento());
        concludeEventoButton.addActionListener(e -> handleConcludeEvento());
        eliminaConclusiButton.addActionListener(e -> handleEliminaConclusi());
        refreshButton.addActionListener(e -> {
            refreshData();
            mainFrame.showToast("✅ Eventi aggiornati con successo!", TOAST_SUCCESS);
        });
    }
    /**
     * Crea una card per un evento
     */
    private JPanel createEventCard(Hackathon evento) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(800, 120));
        card.setPreferredSize(new Dimension(800, 120));
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("🎯 " + evento.getNome());
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        // Status indicator
        String status = getEventStatus(evento);
        Color statusColor = getStatusColor(evento);
        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        statusLabel.setForeground(statusColor);
        statusLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        // Details
        JPanel detailsPanel = new JPanel(new GridLayout(2, 3, 20, 5));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        detailsPanel.add(createDetailLabel("Sede", evento.getSede()));
        detailsPanel.add(createDetailLabel("Max Partecipanti", String.valueOf(evento.getMaxPartecipanti())));
        detailsPanel.add(createDetailLabel("Max Team", String.valueOf(evento.getMaxTeam())));
        String dataInizio = evento.getDataInizio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String dataFine = evento.getDataFine().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        detailsPanel.add(createDetailLabel("Inizio", dataInizio));
        detailsPanel.add(createDetailLabel("Fine", dataFine));
        detailsPanel.add(createDetailLabel("Tipo", evento.isVirtuale() ? "Virtuale" : "Presenziale"));
        // Selection button
        JButton selectButton = new JButton("Seleziona");
        selectButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        selectButton.setBackground(INFO_COLOR);
        selectButton.setForeground(Color.BLACK); // Testo nero per contrasto
        selectButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        selectButton.setFocusPainted(false);
        selectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectButton.addActionListener(e -> {
            selectedEvento = evento;
            updateButtonStates();
            // Effetto di feedback visivo
            selectButton.setText("✓ Selezionato");
            selectButton.setBackground(SUCCESS_COLOR);
            selectButton.setForeground(Color.BLACK); // Mantieni testo nero
            selectButton.setEnabled(false);
        });
        // Layout
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(headerPanel, BorderLayout.NORTH);
        leftPanel.add(detailsPanel, BorderLayout.CENTER);
        card.add(leftPanel, BorderLayout.CENTER);
        card.add(selectButton, BorderLayout.EAST);
        return card;
    }
    /**
     * Crea una label per i dettagli
     */
    private JLabel createDetailLabel(String label, String value) {
        JLabel detailLabel = new JLabel(label + ": " + value);
        detailLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        detailLabel.setForeground(TEXT_COLOR.brighter());
        return detailLabel;
    }
    /**
     * Ottiene lo status dell'evento
     */
    private String getEventStatus(Hackathon evento) {
        if (evento.isEventoConcluso()) {
            return "Concluso";
        } else if (evento.isEventoAvviato()) {
            return "In Corso";
        } else if (evento.isRegistrazioniAperte()) {
            return "Registrazioni Aperte";
        } else {
            return "In Preparazione";
        }
    }
    /**
     * Ottiene il colore dello status
     */
    private Color getStatusColor(Hackathon evento) {
        if (evento.isEventoConcluso()) {
            return ERROR_COLOR;
        } else if (evento.isEventoAvviato()) {
            return SUCCESS_COLOR;
        } else if (evento.isRegistrazioniAperte()) {
            return INFO_COLOR;
        } else {
            return WARNING_COLOR;
        }
    }
    /**
     * Aggiorna lo stato dei bottoni e dell'interfaccia
     */
    private void updateButtonStates() {
        boolean hasSelection = selectedEvento != null;
        // Logica per determinare quando i bottoni sono abilitati
        boolean canOpenRegistrations = hasSelection && !selectedEvento.isRegistrazioniAperte();
        boolean canCloseRegistrations = hasSelection && selectedEvento.isRegistrazioniAperte();
        boolean canStartEvent = hasSelection && !selectedEvento.isEventoAvviato() && !selectedEvento.isEventoConcluso();
        boolean canCompleteEvent = hasSelection && selectedEvento.isEventoAvviato() && !selectedEvento.isEventoConcluso();
        // Aggiorna stato con tooltip
        updateButtonStateWithTooltip(apriRegistrazioniButton, canOpenRegistrations);
        updateButtonStateWithTooltip(chiudiRegistrazioniButton, canCloseRegistrations);
        updateButtonStateWithTooltip(avviaEventoButton, canStartEvent);
        updateButtonStateWithTooltip(concludeEventoButton, canCompleteEvent);
        // Forza aggiornamento del layout
        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
        });
    }
    /**
     * Aggiorna lo stato di un singolo bottone con tooltip
     */
    private void updateButtonStateWithTooltip(JButton button, boolean enabled) {
        button.setEnabled(enabled);
        if (!enabled) {
            String tooltipText = getDisabledTooltip(button);
            button.setToolTipText(tooltipText);
            button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            // Effetto visivo per bottoni disabilitati
            button.setBackground(new Color(200, 200, 200));
            button.setForeground(new Color(100, 100, 100));
        } else {
            // Bottone attivo - tooltip con azione
            String activeTooltip = getActiveTooltip(button);
            button.setToolTipText(activeTooltip);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            // Ripristina colori originali
            if (button == apriRegistrazioniButton) {
                button.setBackground(INFO_COLOR);
                button.setForeground(Color.WHITE);
            } else if (button == chiudiRegistrazioniButton) {
                button.setBackground(WARNING_COLOR);
                button.setForeground(Color.WHITE);
            } else if (button == avviaEventoButton) {
                button.setBackground(SUCCESS_COLOR);
                button.setForeground(Color.WHITE);
            } else if (button == concludeEventoButton) {
                button.setBackground(ERROR_COLOR);
                button.setForeground(Color.WHITE);
            }
        }
        button.repaint();
    }
    /**
     * Restituisce il tooltip per un bottone disabilitato
     */
    private String getDisabledTooltip(JButton button) {
        if (selectedEvento == null) {
            return "Seleziona un evento per abilitare questa azione";
        }
        if (button == chiudiRegistrazioniButton) {
            return "Le registrazioni sono già chiuse o l\'evento non è disponibile";
        } else if (button == apriRegistrazioniButton) {
            return "Le registrazioni sono già aperte o l\'evento non è disponibile";
        } else if (button == avviaEventoButton) {
            return "L\'evento è già stato avviato o concluso";
        } else if (button == concludeEventoButton) {
            return "L'evento deve essere prima avviato";
        }
        return "Azione non disponibile per questo evento";
    }
    /**
     * Restituisce il tooltip per un bottone attivo
     */
    private String getActiveTooltip(JButton button) {
        if (button == chiudiRegistrazioniButton) {
            return "Chiudi le registrazioni per questo evento";
        } else if (button == apriRegistrazioniButton) {
            return "Apri le registrazioni per questo evento";
        } else if (button == avviaEventoButton) {
            return "Avvia questo evento";
        } else if (button == concludeEventoButton) {
            return "Conclude questo evento";
        }
        return "Clicca per eseguire l'azione";
    }
    /**
     * Aggiorna i dati del pannello
     */
    public void refreshData() {
        eventiContainer.removeAll();
        List<Hackathon> eventi = controller.getTuttiHackathon();
        if (eventi.isEmpty()) {
            showEmptyState();
        } else {
            showEventiList(eventi);
        }
        eventiContainer.revalidate();
        eventiContainer.repaint();
        // Reset selection
        selectedEvento = null;
        updateButtonStates();
    }
    /**
     * Mostra lo stato vuoto
     */
    private void showEmptyState() {
        JPanel emptyPanel = new JPanel(new BorderLayout());
        emptyPanel.setOpaque(false);
        emptyPanel.setBorder(new EmptyBorder(50, 0, 50, 0));
        JLabel emptyIcon = new JLabel("📅", CENTER);
        emptyIcon.setFont(new Font(FONT_FAMILY, Font.PLAIN, 48));
        emptyIcon.setForeground(TEXT_COLOR.brighter());
        JLabel emptyText = new JLabel("Nessun evento disponibile", CENTER);
        emptyText.setFont(new Font(FONT_FAMILY, Font.PLAIN, 16));
        emptyText.setForeground(TEXT_COLOR.brighter());
        emptyText.setBorder(new EmptyBorder(10, 0, 0, 0));
        JLabel emptySubtext = new JLabel("Crea il tuo primo evento per iniziare", CENTER);
        emptySubtext.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        emptySubtext.setForeground(TEXT_COLOR.brighter().brighter());
        emptySubtext.setBorder(new EmptyBorder(5, 0, 0, 0));
        emptyPanel.add(emptyIcon, BorderLayout.NORTH);
        emptyPanel.add(emptyText, BorderLayout.CENTER);
        emptyPanel.add(emptySubtext, BorderLayout.SOUTH);
        eventiContainer.add(emptyPanel);
    }
    /**
     * Mostra la lista degli eventi
     */
    private void showEventiList(List<Hackathon> eventi) {
        for (Hackathon evento : eventi) {
            JPanel card = createEventCard(evento);
            eventiContainer.add(card);
            eventiContainer.add(Box.createVerticalStrut(10));
        }
    }
    /**
     * Mostra il dialog per creare un nuovo evento
     */
    private void showCreaEventoDialog() {
        LOGGER.info("=== PULSANTE CREA EVENTO CLICCATO ===");
        try {
            // Debug utente corrente
            model.Utente currentUser = controller.getCurrentUser();
            if (currentUser == null) {
                LOGGER.warning("Utente corrente è NULL");
                JOptionPane.showMessageDialog(this, 
                    "Errore: utente non autenticato",
                    AUTH_ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Verifica che l'utente sia un organizzatore
            if (!"ORGANIZZATORE".equals(currentUser.getRuolo())) {
                LOGGER.warning("Accesso negato - utente non organizzatore");
                JOptionPane.showMessageDialog(this, 
                    "Solo gli organizzatori possono creare eventi.\nEffettua il login come organizzatore.",
                    AUTH_ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            LOGGER.info("Utente validato, apertura dialog...");
            // Crea e mostra il dialog
            CreateEventDialog dialog = new CreateEventDialog(mainFrame, controller);
            dialog.showDialog();
            LOGGER.info("Dialog mostrato");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l\'apertura del dialog di creazione evento", e);
            JOptionPane.showMessageDialog(this,
                "Errore durante l\'apertura del form di creazione.",
                ERRORE_TITLE,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Gestisce l'apertura delle registrazioni
     */
    private void handleApriRegistrazioni() {
        if (selectedEvento != null) {
            if (controller.apriRegistrazioni(selectedEvento.getId())) {
                mainFrame.showToast("Registrazioni aperte con successo! 🔓", TOAST_SUCCESS);
                refreshData();
            } else {
                mainFrame.showToast("Errore durante l\'apertura delle registrazioni", ERROR_TEXT);
            }
        }
    }
    /**
     * Gestisce la chiusura delle registrazioni
     */
    private void handleChiudiRegistrazioni() {
        if (selectedEvento != null) {
            if (controller.chiudiRegistrazioni(selectedEvento.getId())) {
                mainFrame.showToast("Registrazioni chiuse con successo! 🔒", TOAST_SUCCESS);
                refreshData();
            } else {
                mainFrame.showToast("Errore durante la chiusura delle registrazioni", ERROR_TEXT);
            }
        }
    }
    /**
     * Gestisce l'avvio dell'evento
     */
    private void handleAvviaEvento() {
        if (selectedEvento != null) {
            // Mostra dialog per inserire la descrizione del problema
            String descrizioneProblema = showAvviaEventoDialog();
            if (descrizioneProblema != null && !descrizioneProblema.trim().isEmpty()) {
                if (controller.avviaHackathon(selectedEvento.getId(), descrizioneProblema)) {
                    mainFrame.showToast("Hackathon avviato con successo! 🚀", TOAST_SUCCESS);
                    refreshData();
                } else {
                    mainFrame.showToast("Errore durante l\'avvio dell\'hackathon", ERROR_TEXT);
                }
            }
        }
    }
    /**
     * Gestisce la conclusione dell'evento
     */
    private void handleConcludeEvento() {
        if (selectedEvento != null) {
            int choice = JOptionPane.showConfirmDialog(mainFrame,
                "Sei sicuro di voler concludere l\'evento?",
                "Conclude Evento",
                JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                boolean success = controller.concludeEvento(selectedEvento.getId());
                if (success) {
                    mainFrame.showToast("Evento concluso con successo!", TOAST_SUCCESS);
                    refreshData();
                } else {
                    mainFrame.showToast("Errore durante la conclusione dell\'hackathon", ERROR_TEXT);
                }
            }
        }
    }

    /**
     * Elimina tutti gli eventi conclusi con conferma.
     */
    private void handleEliminaConclusi() {
        LOGGER.info("=== PULSANTE ELIMINA CONCLUSI CLICCATO ===");
        
        if (!verificaAutorizzazioneOrganizzatore()) {
            return;
        }
        
        if (mostraConfermaEliminazione()) {
            eseguiEliminazioneEventiConclusi();
        } else {
            LOGGER.info("Operazione annullata dall'utente");
        }
    }
    
    /**
     * Verifica che l'utente corrente sia un organizzatore autorizzato
     * 
     * @return true se l'utente è autorizzato, false altrimenti
     */
    private boolean verificaAutorizzazioneOrganizzatore() {
        model.Utente currentUser = controller.getCurrentUser();
        if (currentUser == null) {
            LOGGER.warning("Utente corrente è NULL");
            JOptionPane.showMessageDialog(mainFrame,
                    "Errore: utente non autenticato",
                    AUTH_ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!"ORGANIZZATORE".equals(currentUser.getRuolo())) {
            LOGGER.warning("Accesso negato - utente non organizzatore");
            JOptionPane.showMessageDialog(mainFrame,
                    "Solo gli organizzatori possono eliminare eventi conclusi.",
                    "Permessi Insufficienti",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Mostra il dialog di conferma per l'eliminazione eventi
     * 
     * @return true se l'utente conferma, false altrimenti
     */
    private boolean mostraConfermaEliminazione() {
        LOGGER.info("Utente validato, mostrando conferma...");
        int confirmChoice = JOptionPane.showConfirmDialog(mainFrame,
                "Questa operazione eliminerà TUTTI gli eventi conclusi.\n" +
                "L'operazione non può essere annullata. Procedere?",
                "Elimina Eventi Conclusi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        return confirmChoice == JOptionPane.YES_OPTION;
    }
    
    /**
     * Esegue l'eliminazione degli eventi conclusi e gestisce i risultati
     */
    private void eseguiEliminazioneEventiConclusi() {
        LOGGER.info("Conferma ricevuta, chiamando controller...");
        try {
            boolean ok = controller.eliminaEventiConclusi();
            LOGGER.info(String.format("Risultato controller: %s", ok));
            if (ok) {
                mainFrame.showToast("\uD83D\uDDD1\uFE0F Eventi conclusi eliminati con successo!", TOAST_SUCCESS);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                        "Nessun evento concluso trovato da eliminare.",
                        "Nessuna Operazione",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (EventManagementException e) {
            gestisciErroreEliminazione(e);
        } catch (Exception e) {
            gestisciErroreGenerico(e);
        }
    }
    
    /**
     * Gestisce gli errori di tipo EventManagementException
     * 
     * @param e l'eccezione catturata
     */
    private void gestisciErroreEliminazione(EventManagementException e) {
        LOGGER.log(Level.SEVERE, "Errore durante eliminazione eventi conclusi", e);
        String errorMessage = e.getMessage();
        
        if (isErroreTransazione(errorMessage)) {
            gestisciErroreTransazione();
        } else if (isErroreDatabase(errorMessage)) {
            mostraErroreDatabase(errorMessage);
        } else {
            mostraErroreGenericoEliminazione(errorMessage);
        }
    }
    
    /**
     * Verifica se l'errore è di tipo transazione
     * 
     * @param errorMessage il messaggio di errore
     * @return true se è un errore di transazione
     */
    private boolean isErroreTransazione(String errorMessage) {
        return errorMessage.contains("transazione") && errorMessage.contains("interrotta");
    }
    
    /**
     * Verifica se l'errore è di tipo database
     * 
     * @param errorMessage il messaggio di errore
     * @return true se è un errore di database
     */
    private boolean isErroreDatabase(String errorMessage) {
        return errorMessage.contains("database") || 
               errorMessage.contains("constraint") || 
               errorMessage.contains("violates");
    }
    
    /**
     * Gestisce gli errori di transazione con opzioni di recovery
     */
    private void gestisciErroreTransazione() {
        String errorMessage = "Errore di transazione del database rilevato.\n\n" +
                             "Questo problema può essere causato da:\n" +
                             "• Un'operazione precedente fallita che ha corrotto la transazione\n" +
                             "• Problemi di connessione al database\n" +
                             "• Vincoli di integrità non rispettati\n\n" +
                             "Soluzioni:\n" +
                             "1. Riavviare l'applicazione\n" +
                             "2. Verificare la connessione al database\n" +
                             "3. Contattare l'amministratore di sistema";

        Object[] options = {"Riprova", "Pulisci Database", "Annulla"};
        int transactionChoice = JOptionPane.showOptionDialog(mainFrame,
            errorMessage,
            "Errore Transazione",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            options,
            options[0]);

        if (transactionChoice == JOptionPane.YES_OPTION) {
            handleEliminaConclusi();
        } else if (transactionChoice == JOptionPane.NO_OPTION) {
            eseguiCleanupDatabase();
        }
    }
    
    /**
     * Esegue il cleanup del database e riprova l'operazione
     */
    private void eseguiCleanupDatabase() {
        try {
            controller.cleanupDatabaseState();
            JOptionPane.showMessageDialog(mainFrame,
                "Stato del database pulito con successo.\nOra puoi riprovare l'operazione.",
                "Cleanup Riuscito",
                JOptionPane.INFORMATION_MESSAGE);
            handleEliminaConclusi();
        } catch (Exception cleanupEx) {
            JOptionPane.showMessageDialog(mainFrame,
                "Errore durante la pulizia del database: " + cleanupEx.getMessage(),
                "Errore Cleanup",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Mostra un messaggio di errore per problemi del database
     * 
     * @param originalMessage il messaggio di errore originale
     */
    private void mostraErroreDatabase(String originalMessage) {
        String errorMessage = "Errore del database: " + originalMessage +
                             "\n\nVerificare che il database sia configurato correttamente e che tutte le tabelle esistano.";
        JOptionPane.showMessageDialog(mainFrame,
                errorMessage,
                "Errore Database",
                JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Mostra un messaggio di errore generico per l'eliminazione
     * 
     * @param errorMessage il messaggio di errore
     */
    private void mostraErroreGenericoEliminazione(String errorMessage) {
        JOptionPane.showMessageDialog(mainFrame,
                "Errore durante l\'eliminazione: " + errorMessage,
                ERRORE_TITLE,
                JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Gestisce gli errori generici
     * 
     * @param e l'eccezione catturata
     */
    private void gestisciErroreGenerico(Exception e) {
        LOGGER.log(Level.SEVERE, "Errore generico durante eliminazione eventi conclusi", e);
        JOptionPane.showMessageDialog(mainFrame,
                "Errore durante l\'eliminazione: " + e.getMessage(),
                ERRORE_TITLE,
                JOptionPane.ERROR_MESSAGE);
    }
    /**
     * Mostra il dialog per avviare l'evento
     */
    private String showAvviaEventoDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Avvia Hackathon", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(INFO_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel titleLabel = new JLabel("🚀 Avvia Hackathon: " + selectedEvento.getNome());
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        // Content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel instructionLabel = new JLabel("Inserisci la descrizione del problema che verrà pubblicata ai partecipanti:");
        instructionLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        instructionLabel.setForeground(TEXT_COLOR);
        JTextArea descrizioneArea = new JTextArea();
        descrizioneArea.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        descrizioneArea.setLineWrap(true);
        descrizioneArea.setWrapStyleWord(true);
        descrizioneArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane scrollPane = new JScrollPane(descrizioneArea);
        scrollPane.setPreferredSize(new Dimension(450, 200));
        // Placeholder text
        descrizioneArea.setText("Esempio:\n\nSviluppa un'applicazione mobile per la gestione intelligente del traffico urbano.\n\nRequisiti:\n- Interfaccia intuitiva per gli utenti\n- Integrazione con sensori IoT\n- Algoritmi di ottimizzazione del percorso\n- Dashboard per le autorità cittadine\n\nTecnologie consigliate: React Native, Node.js, MongoDB");
        contentPanel.add(instructionLabel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        JButton avviaButton = new JButton("🚀 Avvia Hackathon");
        avviaButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        avviaButton.setBackground(SUCCESS_COLOR);
        avviaButton.setForeground(Color.WHITE);
        avviaButton.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        avviaButton.setFocusPainted(false);
        avviaButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JButton cancelButton = new JButton("Annulla");
        cancelButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        cancelButton.setBackground(ERROR_COLOR);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        avviaButton.addActionListener(e -> {
            String descrizione = descrizioneArea.getText().trim();
            if (descrizione.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Inserisci una descrizione del problema", 
                    "Descrizione Richiesta", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            dialog.setVisible(false);
        });
        cancelButton.addActionListener(e -> dialog.setVisible(false));
        buttonPanel.add(avviaButton);
        buttonPanel.add(cancelButton);
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        // Focus on text area
        SwingUtilities.invokeLater(() -> {
            descrizioneArea.requestFocusInWindow();
            descrizioneArea.selectAll();
        });
        dialog.setVisible(true);
        // Return the description if dialog was confirmed
        if (dialog.isVisible()) {
            return null; // Dialog was cancelled
        } else {
            return descrizioneArea.getText().trim();
        }
    }
    /**
     * Gestisce il ridimensionamento responsive del pannello
     */
    public void onResize(int width) {
        // Adatta il layout in base alle dimensioni
        if (width < 1000) {
            // Layout compatto per schermi piccoli
            setBorder(new EmptyBorder(10, 10, 10, 10));
        } else {
            // Layout normale per schermi grandi
            setBorder(new EmptyBorder(20, 20, 20, 20));
        }
        // Aggiorna il layout
        revalidate();
        repaint();
    }
}
