package gui;
import controller.Controller;
import model.EventRequest;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;
/**
 * Dialog per la creazione di un nuovo evento seguendo il pattern MVC.
 * Implementa un'interfaccia pulita senza rettangoli superflui
 * e con font/contrasti ottimizzati per la leggibilità.
 */
public final class CreateEventDialog extends JDialog {
    private static final Logger LOGGER = Logger.getLogger(CreateEventDialog.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String FONT_SEGOE_UI = "Segoe UI";
    private final transient Controller controller;
    // Componenti del form
    private JTextField nomeField;
    private JTextField sedeField;
    private JTextField dataInizioField;
    private JTextField dataFineField;
    private JTextField descrizioneField;
    private JCheckBox virtualeCheckBox;
    private JSpinner maxPartecipantiSpinner;
    private JSpinner maxTeamSpinner;
    private JButton creaButton;
    private JButton annullaButton;
    /**
     * Costruttore del dialog
     * 
     * @param parent il parent window
     * @param controller il controller MVC per la gestione eventi
     */
    public CreateEventDialog(Window parent, Controller controller) {
        super(parent, "Crea Nuovo Evento", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        initializeDialog();
        createComponents();
        layoutComponents();
        setupEventHandlers();
        setupValidation();
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    /**
     * Inizializza le impostazioni base del dialog
     */
    private void initializeDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBackground(new Color(245, 245, 245));
        getContentPane().setBackground(new Color(245, 245, 245));
    }
    /**
     * Crea tutti i componenti del form
     */
    private void createComponents() {
        // Campi di testo
        nomeField = createStyledTextField();
        sedeField = createStyledTextField();
        dataInizioField = createStyledTextField();
        dataFineField = createStyledTextField();
        descrizioneField = createStyledTextField();
        // Imposta placeholder text per le date
        dataInizioField.setToolTipText("Formato: YYYY-MM-DD HH:MM (es. 2024-12-15 09:00)");
        dataFineField.setToolTipText("Formato: YYYY-MM-DD HH:MM (es. 2024-12-17 18:00)");
        // Aggiungi placeholder text visibile
        dataInizioField.setText("2024-12-15 09:00");
        dataFineField.setText("2024-12-17 18:00");
        // Checkbox per evento virtuale
        virtualeCheckBox = createStyledCheckBox("Evento Virtuale");
        // Spinner per numeri
        maxPartecipantiSpinner = createStyledSpinner(new SpinnerNumberModel(50, 1, 10000, 1));
        maxTeamSpinner = createStyledSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        // Pulsanti - RIMUOVE COMPLETAMENTE I RETTANGOLI SUPERFLUI
        creaButton = createStyledButton("Crea Evento", new Color(46, 204, 113));
        annullaButton = createStyledButton("Annulla", Color.GRAY);
        // Assicuriamoci che i pulsanti non abbiano bordi extra
        creaButton.setFocusPainted(false);
        creaButton.setBorderPainted(false);
        annullaButton.setFocusPainted(false);
        annullaButton.setBorderPainted(false);
    }
    /**
     * Organizza i componenti nel layout
     */
    private void layoutComponents() {
        setLayout(new BorderLayout());
        // Header con titolo
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        // Form centrale
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        // Pulsanti in basso
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    /**
     * Crea il pannello header con il titolo
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 152, 219));
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));
        // Titolo principale - NESSUN RETTANGOLO EXTRA
        JLabel titleLabel = new JLabel("➕ Crea Nuovo Evento");
        titleLabel.setFont(new Font(FONT_SEGOE_UI, Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(titleLabel, BorderLayout.CENTER);
        return panel;
    }
    /**
     * Crea il pannello del form
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;
        // Nome evento
        addFormRow(panel, gbc, row++, "Nome Evento:", nomeField);
        // Sede
        addFormRow(panel, gbc, row++, "Sede:", sedeField);
        // Data inizio
        addFormRow(panel, gbc, row++, "Data Inizio:", dataInizioField);
        // Data fine
        addFormRow(panel, gbc, row++, "Data Fine:", dataFineField);
        // Evento virtuale
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(virtualeCheckBox, gbc);
        gbc.gridwidth = 1;
        // Max partecipanti
        addFormRow(panel, gbc, row++, "Max Partecipanti:", maxPartecipantiSpinner);
        // Max team
        addFormRow(panel, gbc, row++, "Max Team:", maxTeamSpinner);
        // Descrizione problema (opzionale)
        addFormRow(panel, gbc, row, "Descrizione (opzionale):", descrizioneField);
        return panel;
    }
    /**
     * Aggiunge una riga al form con label e componente
     */
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent component) {
        // Label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font(FONT_SEGOE_UI, Font.PLAIN, 14));
        label.setForeground(new Color(44, 62, 80));
        panel.add(label, gbc);
        // Componente
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(component, gbc);
    }
    /**
     * Crea il pannello dei pulsanti
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 25, 20, 25));
        // Aggiungi pulsanti - NESSUN COMPONENTE EXTRA
        panel.add(annullaButton);
        panel.add(creaButton);
        return panel;
    }
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        // Pulsante Crea
        creaButton.addActionListener(e -> handleCreaEvento());
        // Pulsante Annulla
        annullaButton.addActionListener(e -> handleAnnulla());
        // Chiusura dialog con ESC
        getRootPane().registerKeyboardAction(
            e -> handleAnnulla(),
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        // Enter sul pulsante crea
        getRootPane().setDefaultButton(creaButton);
    }
    /**
     * Configura la validazione real-time
     */
    private void setupValidation() {
        // Validation listeners su TUTTI i campi obbligatori
        nomeField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateForm();
            }
        });
        sedeField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateForm();
            }
        });
        // AGGIUNGIAMO I LISTENER MANCANTI per le date
        dataInizioField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateForm();
            }
        });
        dataFineField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateForm();
            }
        });
        // Validazione iniziale
        validateForm();
    }
    /**
     * Valida il form e abilita/disabilita il pulsante Crea
     */
    private void validateForm() {
        String nome = nomeField.getText().trim();
        String sede = sedeField.getText().trim();
        String dataInizio = dataInizioField.getText().trim();
        String dataFine = dataFineField.getText().trim();
        boolean isValid = !nome.isEmpty() && !sede.isEmpty() && !dataInizio.isEmpty() && !dataFine.isEmpty();
        // Debug della validazione
        creaButton.setEnabled(isValid);
    }
    /**
     * Gestisce la creazione dell'evento tramite Controller MVC
     */
    private void handleCreaEvento() {
        LOGGER.info("=== PULSANTE CREA EVENTO PREMUTO ===");
        try {
            // Disabilita il pulsante per evitare doppi click
            creaButton.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // Costruisce la richiesta dall'input dell'utente
            EventRequest request = buildEventRequestFromForm();
            LOGGER.log(java.util.logging.Level.INFO, "EventRequest creato: {0}", request);
            // Chiama il controller MVC per creare l'evento
            long eventId = controller.creaEventoDaRequest(request);
            LOGGER.log(java.util.logging.Level.INFO, "Risultato creazione evento - ID: {0}", eventId);
            if (eventId > 0) {
                LOGGER.log(java.util.logging.Level.INFO, "Evento creato con successo dalla UI - ID: {0}", eventId);
                JOptionPane.showMessageDialog(this,
                    "Evento '" + request.nome() + "' creato con successo!\nID: " + eventId,
                    "Successo",
                    JOptionPane.INFORMATION_MESSAGE);
                // Aggiorna la lista eventi nel pannello principale
                if (getOwner() instanceof MainFrame) {
                    // Il refresh automatico dovrebbe gestire questo ora
                }
                dispose(); // Chiude il dialog solo se la creazione è riuscita
            } else {
                JOptionPane.showMessageDialog(this,
                    "Errore durante la creazione dell'evento.\nRiprova o contatta il supporto.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (database.DataAccessException e) {
            LOGGER.severe("Errore di accesso ai dati: " + e.getMessage());
             // Mostra lo stack trace completo nel log
            String errorMsg = "Errore di database durante la creazione:\n" + e.getMessage();
            if (e.getCause() != null) {
                errorMsg += "\n\nDettagli tecnici:\n" + e.getCause().getMessage();
            }
            JOptionPane.showMessageDialog(
                this,
                errorMsg,
                "Errore Database",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Errore di validazione: " + e.getMessage());
            JOptionPane.showMessageDialog(
                this,
                "Errore di validazione:\n" + e.getMessage(),
                "Dati Non Validi",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (Exception e) {
            LOGGER.severe("Errore generico durante la creazione evento dalla UI: " + e.getMessage());
             // Mostra lo stack trace completo nel log
            String errorMsg = "Errore imprevisto durante la creazione:\n" + e.getMessage();
            if (e.getCause() != null) {
                errorMsg += "\nCausa: " + e.getCause().getMessage();
            }
            JOptionPane.showMessageDialog(
                this,
                errorMsg,
                "Errore Creazione Evento",
                JOptionPane.ERROR_MESSAGE
            );
        } finally {
            // Ripristina il cursore e riabilita il pulsante
            setCursor(Cursor.getDefaultCursor());
            creaButton.setEnabled(true);
        }
    }
    /**
     * Costruisce EventRequest dai dati del form
     */
    private EventRequest buildEventRequestFromForm() throws IllegalArgumentException {
        String nome = nomeField.getText().trim();
        String sede = sedeField.getText().trim();
        String dataInizioStr = dataInizioField.getText().trim();
        String dataFineStr = dataFineField.getText().trim();
        String descrizione = descrizioneField.getText().trim();
        boolean virtuale = virtualeCheckBox.isSelected();
        int maxPartecipanti = (Integer) maxPartecipantiSpinner.getValue();
        int maxTeam = (Integer) maxTeamSpinner.getValue();
        // Debug dei dati inseriti
        // Parsing delle date
        LocalDateTime dataInizio;
        LocalDateTime dataFine;
        try {
            dataInizio = LocalDateTime.parse(dataInizioStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato data inizio non valido. Usa: YYYY-MM-DD HH:MM (es. 2024-12-15 09:00)");
        }
        try {
            dataFine = LocalDateTime.parse(dataFineStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato data fine non valido. Usa: YYYY-MM-DD HH:MM (es. 2024-12-15 09:00)");
        }
        // Descrizione opzionale
        String descrizioneFinale = descrizione.isEmpty() ? null : descrizione;
        return new EventRequest.Builder(nome, dataInizio, dataFine)
                .sede(sede)
                .virtuale(virtuale)
                .maxPartecipanti(maxPartecipanti)
                .maxTeam(maxTeam)
                .descrizioneProblema(descrizioneFinale)
                .build();
    }
    /**
     * Gestisce l'annullamento
     */
    private void handleAnnulla() {
        LOGGER.info("Creazione evento annullata dall'utente");
        dispose();
    }
    /**
     * Mostra il dialog e restituisce se un evento è stato creato
     * 
     * @return true se un evento è stato creato
     */
    public boolean showDialog() {
        setVisible(true);
        return false; // Il successo viene gestito tramite callback
    }
    /**
     * Crea un JTextField con stile standardizzato
     */
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font(FONT_SEGOE_UI, Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            new EmptyBorder(8, 8, 8, 8)
        ));
        return field;
    }
    /**
     * Crea un JButton con stile standardizzato
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font(FONT_SEGOE_UI, Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setPreferredSize(new Dimension(150, 40));
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        // Rimuove elementi visivi superflui
        button.setFocusPainted(false);
        return button;
    }
    /**
     * Crea un JCheckBox con stile standardizzato
     */
    private JCheckBox createStyledCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(new Font(FONT_SEGOE_UI, Font.PLAIN, 14));
        checkBox.setForeground(new Color(44, 62, 80));
        checkBox.setOpaque(false);
        checkBox.setFocusPainted(false);
        return checkBox;
    }
    /**
     * Crea un JSpinner con stile standardizzato
     */
    private JSpinner createStyledSpinner(SpinnerModel model) {
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(new Font(FONT_SEGOE_UI, Font.PLAIN, 14));
        spinner.setPreferredSize(new Dimension(100, 35));
        // Migliora l'aspetto dell'editor
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
            JTextField textField = defaultEditor.getTextField();
            textField.setFont(new Font(FONT_SEGOE_UI, Font.PLAIN, 14));
            textField.setBorder(new EmptyBorder(8, 8, 8, 8));
        }
        return spinner;
    }
}
