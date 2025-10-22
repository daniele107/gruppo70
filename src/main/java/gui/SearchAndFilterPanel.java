package gui;

import controller.Controller;
import model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Pannello avanzato per ricerca globale e filtri multipli.
 * Permette di cercare attraverso hackathon, team, utenti, documenti e progressi.
 */
public class SearchAndFilterPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    // Constants for repeated string literals
    private static final String SEGOE_UI = "Segoe UI";
    private static final String ERRORE = "Errore";
    private static final String TUTTE_LE_CATEGORIE = "Tutte le categorie";
    private static final String TUTTI_I_TIPI = "Tutti i tipi";
    private static final String TUTTI_GLI_STATI = "Tutti gli stati";
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String IN_ATTESA = "In attesa";
    
    private final transient Controller controller;
    private final JFrame mainFrame;
    
    // Componenti ricerca
    private JTextField globalSearchField;
    private JButton searchButton;
    private JButton clearButton;
    private JLabel resultCountLabel;
    
    // Filtri
    private JComboBox<String> categoryFilterCombo;
    private JComboBox<String> typeFilterCombo;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> dateRangeCombo;
    private JCheckBox advancedFiltersCheck;
    
    // Filtri avanzati (nascosti inizialmente)
    private JPanel advancedFiltersPanel;
    private JSpinner minScoreSpinner;
    private JSpinner maxScoreSpinner;
    private JTextField locationField;
    private JCheckBox includeClosedCheck;
    
    // Risultati
    private JTable resultsTable;
    private DefaultTableModel resultsTableModel;
    private JButton viewDetailsButton;
    private JButton exportResultsButton;
    
    // Suggerimenti ricerca
    private JLabel suggestionsLabel;
    
    // Dati
    private transient List<SearchResult> allResults;
    private transient List<SearchResult> filteredResults;
    
    /**
     * Costruttore del pannello ricerca
     */
    public SearchAndFilterPanel(Controller controller, JFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadSuggestions();
    }
    
    /**
     * Inizializza i componenti
     */
    private void initializeComponents() {
        // Campo ricerca globale
        globalSearchField = new JTextField(30);
        globalSearchField.setFont(new Font(SEGOE_UI, Font.PLAIN, 14));
        globalSearchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        globalSearchField.setToolTipText("Cerca in hackathon, team, utenti, documenti...");
        
        searchButton = createStyledButton("🔍 Cerca", new Color(52, 152, 219));
        clearButton = createStyledButton("🗑️ Pulisci", new Color(149, 165, 166));
        resultCountLabel = new JLabel("0 risultati trovati");
        resultCountLabel.setFont(new Font(SEGOE_UI, Font.ITALIC, 12));
        resultCountLabel.setForeground(Color.GRAY);
        
        // Filtri principali
        categoryFilterCombo = new JComboBox<>(new String[]{
            TUTTE_LE_CATEGORIE, "Hackathon", "Team", "Utenti", "Documenti", "Progressi", "Valutazioni"
        });
        
        typeFilterCombo = new JComboBox<>(new String[]{
            TUTTI_I_TIPI, "Attivi", "Conclusi", "In corso", IN_ATTESA, "Validati", "Non validati"
        });
        
        statusFilterCombo = new JComboBox<>(new String[]{
            TUTTI_GLI_STATI, "Pubblico", "Privato", "Confermato", "In sospeso", "Rifiutato"
        });
        
        dateRangeCombo = new JComboBox<>(new String[]{
            "Qualsiasi periodo", "Ultima settimana", "Ultimo mese", "Ultimi 3 mesi", "Ultimo anno"
        });
        
        // Checkbox filtri avanzati
        advancedFiltersCheck = new JCheckBox("Filtri avanzati");
        advancedFiltersCheck.setFont(new Font(SEGOE_UI, Font.BOLD, 12));
        
        // Filtri avanzati
        minScoreSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        maxScoreSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 10, 1));
        locationField = new JTextField(15);
        includeClosedCheck = new JCheckBox("Includi eventi chiusi");
        
        // Tabella risultati
        String[] columnNames = {"Tipo", "Nome/Titolo", "Descrizione", "Stato", "Data", "Punteggio", "Azioni"};
        resultsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Solo colonna azioni
            }
        };
        
        resultsTable = new JTable(resultsTableModel);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setRowHeight(30);
        resultsTable.setFont(new Font(SEGOE_UI, Font.PLAIN, 12));
        
        // Configura larghezza colonne
        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Tipo
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Nome
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(250); // Descrizione
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Stato
        resultsTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Data
        resultsTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Punteggio
        resultsTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Azioni
        
        // Sorter per tabella
        TableRowSorter<DefaultTableModel> tableSorter = new TableRowSorter<>(resultsTableModel);
        resultsTable.setRowSorter(tableSorter);
        
        viewDetailsButton = createStyledButton("👁️ Visualizza", new Color(52, 152, 219));
        exportResultsButton = createStyledButton("📤 Esporta", new Color(155, 89, 182));
        
        // Suggerimenti
        suggestionsLabel = new JLabel("💡 Prova a cercare: \"hackathon 2024\", \"team alpha\", \"documenti pdf\"");
        suggestionsLabel.setFont(new Font(SEGOE_UI, Font.ITALIC, 11));
        suggestionsLabel.setForeground(new Color(52, 152, 219));
        
        // Initially disable buttons
        viewDetailsButton.setEnabled(false);
        exportResultsButton.setEnabled(false);
        
        // Initialize data
        allResults = new ArrayList<>();
        filteredResults = new ArrayList<>();
    }
    
    /**
     * Configura il layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(248, 249, 250));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // Filters panel
        JPanel filtersPanel = createFiltersPanel();
        mainPanel.add(filtersPanel, BorderLayout.NORTH);
        
        // Results panel
        JPanel resultsPanel = createResultsPanel();
        mainPanel.add(resultsPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Footer with suggestions
        JPanel suggestionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        suggestionsPanel.setBackground(new Color(248, 249, 250));
        suggestionsPanel.add(suggestionsLabel);
        add(suggestionsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Crea il pannello header
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            new EmptyBorder(20, 20, 15, 20)
        ));
        
        // Titolo
        JLabel titleLabel = new JLabel("🔍 Ricerca Globale e Filtri Avanzati");
        titleLabel.setFont(new Font(SEGOE_UI, Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        JLabel subtitleLabel = new JLabel("Cerca attraverso tutti i dati del sistema con filtri intelligenti");
        subtitleLabel.setFont(new Font(SEGOE_UI, Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);
        
        // Campo ricerca principale
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("🔍"));
        searchPanel.add(globalSearchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        
        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Crea il pannello filtri
     */
    private JPanel createFiltersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("🎛️ Filtri di Ricerca"));
        panel.setBackground(Color.WHITE);
        
        // Filtri principali
        JPanel mainFiltersPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        mainFiltersPanel.setBackground(Color.WHITE);
        mainFiltersPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        mainFiltersPanel.add(new JLabel("Categoria:"));
        mainFiltersPanel.add(categoryFilterCombo);
        mainFiltersPanel.add(new JLabel("Tipo:"));
        mainFiltersPanel.add(typeFilterCombo);
        
        mainFiltersPanel.add(new JLabel("Stato:"));
        mainFiltersPanel.add(statusFilterCombo);
        mainFiltersPanel.add(new JLabel("Periodo:"));
        mainFiltersPanel.add(dateRangeCombo);
        
        panel.add(mainFiltersPanel, BorderLayout.NORTH);
        
        // Checkbox filtri avanzati
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkboxPanel.setBackground(Color.WHITE);
        checkboxPanel.add(advancedFiltersCheck);
        checkboxPanel.add(resultCountLabel);
        panel.add(checkboxPanel, BorderLayout.CENTER);
        
        // Filtri avanzati (inizialmente nascosti)
        advancedFiltersPanel = createAdvancedFiltersPanel();
        advancedFiltersPanel.setVisible(false);
        panel.add(advancedFiltersPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crea il pannello filtri avanzati
     */
    private JPanel createAdvancedFiltersPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new TitledBorder("🔧 Filtri Avanzati"),
            new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(new Color(248, 249, 250));
        
        panel.add(new JLabel("Punteggio min:"));
        panel.add(minScoreSpinner);
        panel.add(new JLabel("Punteggio max:"));
        panel.add(maxScoreSpinner);
        
        panel.add(new JLabel("Località:"));
        panel.add(locationField);
        panel.add(includeClosedCheck);
        panel.add(new JLabel()); // Empty cell
        
        return panel;
    }
    
    /**
     * Crea il pannello risultati
     */
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("📊 Risultati della Ricerca"));
        panel.setBackground(Color.WHITE);
        
        // Tabella risultati
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        // Pannello azioni
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.add(viewDetailsButton);
        actionsPanel.add(exportResultsButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        // Ricerca in tempo reale
        globalSearchField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Non necessario per la ricerca in tempo reale
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                // Ricerca automatica dopo 500ms di inattività
                Timer timer = new Timer(500, ev -> performSearch());
                timer.setRepeats(false);
                timer.start();
            }
        });
        
        // Pulsanti ricerca
        searchButton.addActionListener(e -> performSearch());
        clearButton.addActionListener(this::clearSearch);
        
        // Filtri
        categoryFilterCombo.addActionListener(e -> applyFilters());
        typeFilterCombo.addActionListener(e -> applyFilters());
        statusFilterCombo.addActionListener(e -> applyFilters());
        dateRangeCombo.addActionListener(e -> applyFilters());
        
        // Filtri avanzati toggle
        advancedFiltersCheck.addActionListener(e -> {
            boolean visible = advancedFiltersCheck.isSelected();
            advancedFiltersPanel.setVisible(visible);
            revalidate();
            repaint();
        });
        
        // Filtri avanzati
        minScoreSpinner.addChangeListener(e -> applyFilters());
        maxScoreSpinner.addChangeListener(e -> applyFilters());
        locationField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Non necessario per il filtro in tempo reale
            }
            @Override
            public void keyPressed(KeyEvent e) {
                // Non necessario per il filtro in tempo reale
            }
            @Override
            public void keyReleased(KeyEvent e) {
                Timer timer = new Timer(300, ev -> applyFilters());
                timer.setRepeats(false);
                timer.start();
            }
        });
        includeClosedCheck.addActionListener(e -> applyFilters());
        
        // Selezione tabella
        resultsTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = !resultsTable.getSelectionModel().isSelectionEmpty();
            viewDetailsButton.setEnabled(hasSelection);
            exportResultsButton.setEnabled(!filteredResults.isEmpty());
        });
        
        // Azioni risultati
        viewDetailsButton.addActionListener(this::handleViewDetails);
        exportResultsButton.addActionListener(this::handleExportResults);
        
        // Double click per visualizzare dettagli
        resultsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    handleViewDetails(null);
                }
            }
        });
    }
    
    /**
     * Esegue la ricerca globale
     */
    private void performSearch() {
        String searchTerm = globalSearchField.getText().trim();
        
        SwingWorker<List<SearchResult>, Void> searchWorker = new SwingWorker<List<SearchResult>, Void>() {
            @Override
            protected List<SearchResult> doInBackground() throws Exception {
                List<SearchResult> results = new ArrayList<>();
                
                if (searchTerm.isEmpty()) {
                    // Se nessun termine, carica tutto
                    results.addAll(searchHackathons(""));
                    results.addAll(searchTeams(""));
                    results.addAll(searchUsers(""));
                    results.addAll(searchDocuments(""));
                    results.addAll(searchProgress(""));
                } else {
                    // Ricerca per termine
                    results.addAll(searchHackathons(searchTerm));
                    results.addAll(searchTeams(searchTerm));
                    results.addAll(searchUsers(searchTerm));
                    results.addAll(searchDocuments(searchTerm));
                    results.addAll(searchProgress(searchTerm));
                }
                
                return results;
            }
            
            @Override
            protected void done() {
                try {
                    allResults = get();
                    applyFilters();
                    updateSuggestions();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    JOptionPane.showMessageDialog(SearchAndFilterPanel.this,
                        "Ricerca interrotta dall'utente",
                        ERRORE, JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(SearchAndFilterPanel.this,
                        "Errore durante la ricerca: " + e.getMessage(),
                        ERRORE, JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        searchWorker.execute();
    }
    
    /**
     * Cerca negli hackathon
     */
    private List<SearchResult> searchHackathons(String term) {
        List<SearchResult> results = new ArrayList<>();
        
        try {
            List<Hackathon> hackathons = controller.getTuttiHackathon();
            
            for (Hackathon h : hackathons) {
                if (term.isEmpty() || 
                    h.getNome().toLowerCase().contains(term.toLowerCase()) ||
                    h.getSede().toLowerCase().contains(term.toLowerCase()) ||
                    (h.getDescrizioneProblema() != null && h.getDescrizioneProblema().toLowerCase().contains(term.toLowerCase()))) {
                    
                    String status = getHackathonStatus(h);
                    String description = h.getSede() + " • " + (h.getDescrizioneProblema() != null ? 
                        h.getDescrizioneProblema().substring(0, Math.min(100, h.getDescrizioneProblema().length())) + "..." : 
                        "Nessuna descrizione");
                    
                    results.add(new SearchResult.Builder()
                        .type("🏆 Hackathon")
                        .name(h.getNome())
                        .description(description)
                        .status(status)
                        .date(h.getDataInizio().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
                        .score(0) // Hackathons don't have scores
                        .id(h.getId())
                        .entityType("HACKATHON")
                        .build());
                }
            }
        } catch (Exception e) {
            // Skip on error
        }
        
        return results;
    }
    
    /**
     * Cerca nei team
     */
    private List<SearchResult> searchTeams(String term) {
        List<SearchResult> results = new ArrayList<>();
        
        try {
            List<Team> teams = controller.getTuttiTeam();
            
            for (Team t : teams) {
                if (term.isEmpty() || t.getNome().toLowerCase().contains(term.toLowerCase())) {
                    
                    int memberCount = controller.getTeamDAO().contaMembri(t.getId());
                    String description = memberCount + "/" + t.getDimensioneMassima() + " membri • Hackathon " + t.getHackathonId();
                    
                    // Calculate average score if available
                    double avgScore = calculateTeamAverageScore(t.getId());
                    
                    results.add(new SearchResult.Builder()
                        .type("👥 Team")
                        .name(t.getNome())
                        .description(description)
                        .status(memberCount >= 2 ? "Attivo" : "Incompleto")
                        .date("") // Teams don't have specific dates
                        .score(avgScore)
                        .id(t.getId())
                        .entityType("TEAM")
                        .build());
                }
            }
        } catch (Exception e) {
            // Skip on error
        }
        
        return results;
    }
    
    /**
     * Cerca negli utenti
     */
    private List<SearchResult> searchUsers(String term) {
        List<SearchResult> results = new ArrayList<>();
        
        try {
            List<Utente> users = controller.getTuttiUtenti();
            
            for (Utente u : users) {
                if (term.isEmpty() || 
                    u.getNome().toLowerCase().contains(term.toLowerCase()) ||
                    u.getCognome().toLowerCase().contains(term.toLowerCase()) ||
                    u.getEmail().toLowerCase().contains(term.toLowerCase()) ||
                    u.getLogin().toLowerCase().contains(term.toLowerCase())) {
                    
                    String description = u.getEmail() + " • " + u.getRuolo();
                    
                    results.add(new SearchResult.Builder()
                        .type("👤 Utente")
                        .name(u.getNome() + " " + u.getCognome())
                        .description(description)
                        .status(u.getRuolo())
                        .date("") // Users don't have specific dates in this context
                        .score(0) // Users don't have scores
                        .id(u.getId())
                        .entityType("USER")
                        .build());
                }
            }
        } catch (Exception e) {
            // Skip on error
        }
        
        return results;
    }
    
    /**
     * Cerca nei documenti
     */
    private List<SearchResult> searchDocuments(String term) {
        List<SearchResult> results = new ArrayList<>();
        
        try {
            List<Documento> documents = controller.getTuttiDocumenti();
            
            for (Documento d : documents) {
                if (term.isEmpty() || 
                    d.getNome().toLowerCase().contains(term.toLowerCase()) ||
                    (d.getDescrizione() != null && d.getDescrizione().toLowerCase().contains(term.toLowerCase())) ||
                    d.getTipo().toLowerCase().contains(term.toLowerCase())) {
                    
                    String description = formatFileSize(d.getDimensione()) + " • " + getMimeTypeDescription(d.getTipo()) + 
                        " • Team " + d.getTeamId();
                    
                    results.add(new SearchResult.Builder()
                        .type("📄 Documento")
                        .name(d.getNome())
                        .description(description)
                        .status(d.isValidato() ? "Validato" : IN_ATTESA)
                        .date(d.getDataCaricamento().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
                        .score(0) // Documents don't have scores
                        .id(d.getId())
                        .entityType("DOCUMENT")
                        .build());
                }
            }
        } catch (Exception e) {
            // Skip on error
        }
        
        return results;
    }
    
    /**
     * Cerca nei progressi
     */
    private List<SearchResult> searchProgress(String term) {
        List<SearchResult> results = new ArrayList<>();
        
        try {
            List<Progress> allProgress = controller.getTuttiProgressi();
            
            for (Progress progress : allProgress) {
                if (term.isEmpty() || 
                    (progress.getTitolo() != null && 
                     progress.getTitolo().toLowerCase().contains(term.toLowerCase()))) {
                    
                    String description = "Team: " + progress.getTeamId() + 
                                       " • Hackathon: " + progress.getHackathonId();
                    
                    results.add(new SearchResult.Builder()
                        .type("📈 Progresso")
                        .name(progress.getTitolo())
                        .description(description)
                        .status(progress.haCommentoGiudice() ? "Commentato" : IN_ATTESA)
                        .date(progress.getDataCaricamento() != null ? 
                              progress.getDataCaricamento().format(DateTimeFormatter.ofPattern(DATE_FORMAT)) : "")
                        .score(0) // Progressi non hanno punteggi
                        .id(progress.getId())
                        .entityType("PROGRESS")
                        .build());
                }
            }
        } catch (Exception e) {
            // Skip on error
        }
        
        return results;
    }
    
    /**
     * Applica i filtri ai risultati
     */
    private void applyFilters() {
        if (allResults == null) return;
        
        filteredResults = allResults.stream()
            .filter(this::passesFilters)
            .collect(Collectors.toList());
        
        updateResultsTable();
        updateResultCount();
    }
    
    /**
     * Verifica se un risultato passa i filtri
     */
    private boolean passesFilters(SearchResult result) {
        return passesCategoryFilter(result) && 
               passesTypeFilter(result) && 
               passesStatusFilter(result) && 
               passesAdvancedFilters(result);
    }
    
    private boolean passesCategoryFilter(SearchResult result) {
        String categoryFilter = (String) categoryFilterCombo.getSelectedItem();
        return TUTTE_LE_CATEGORIE.equals(categoryFilter) || 
               result.getType().contains(categoryFilter);
    }
    
    private boolean passesTypeFilter(SearchResult result) {
        String typeFilter = (String) typeFilterCombo.getSelectedItem();
        return TUTTI_I_TIPI.equals(typeFilter) || 
               result.getStatus().toLowerCase().contains(typeFilter.toLowerCase());
    }
    
    private boolean passesStatusFilter(SearchResult result) {
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        return TUTTI_GLI_STATI.equals(statusFilter) || 
               result.getStatus().toLowerCase().contains(statusFilter.toLowerCase());
    }
    
    private boolean passesAdvancedFilters(SearchResult result) {
        if (!advancedFiltersCheck.isSelected()) {
            return true;
        }
        
        return passesScoreFilter(result) && passesLocationFilter(result);
    }
    
    private boolean passesScoreFilter(SearchResult result) {
        int minScore = (Integer) minScoreSpinner.getValue();
        int maxScore = (Integer) maxScoreSpinner.getValue();
        return result.getScore() >= minScore && result.getScore() <= maxScore;
    }
    
    private boolean passesLocationFilter(SearchResult result) {
        String locationFilter = locationField.getText().trim();
        return locationFilter.isEmpty() || 
               result.getDescription().toLowerCase().contains(locationFilter.toLowerCase());
    }
    
    /**
     * Aggiorna la tabella dei risultati
     */
    private void updateResultsTable() {
        resultsTableModel.setRowCount(0);
        
        for (SearchResult result : filteredResults) {
            Object[] row = {
                result.getType(),
                result.getName(),
                result.getDescription(),
                result.getStatus(),
                result.getDate(),
                result.getScore() > 0 ? String.valueOf(Math.round(result.getScore() * 10.0) / 10.0) + "/10" : "N/A",
                "Visualizza"
            };
            
            resultsTableModel.addRow(row);
        }
        
        exportResultsButton.setEnabled(!filteredResults.isEmpty());
    }
    
    /**
     * Aggiorna il contatore risultati
     */
    private void updateResultCount() {
        int total = allResults != null ? allResults.size() : 0;
        int filtered = filteredResults.size();
        
        if (total == filtered) {
            resultCountLabel.setText(total + " risultati trovati");
        } else {
            resultCountLabel.setText(filtered + " di " + total + " risultati");
        }
        
        resultCountLabel.setForeground(filtered > 0 ? new Color(39, 174, 96) : Color.GRAY);
    }
    
    /**
     * Pulisce la ricerca
     */
    private void clearSearch(ActionEvent e) {
        globalSearchField.setText("");
        categoryFilterCombo.setSelectedIndex(0);
        typeFilterCombo.setSelectedIndex(0);
        statusFilterCombo.setSelectedIndex(0);
        dateRangeCombo.setSelectedIndex(0);
        
        if (advancedFiltersCheck.isSelected()) {
            minScoreSpinner.setValue(0);
            maxScoreSpinner.setValue(10);
            locationField.setText("");
            includeClosedCheck.setSelected(false);
        }
        
        allResults.clear();
        filteredResults.clear();
        updateResultsTable();
        updateResultCount();
        loadSuggestions();
    }
    
    /**
     * Carica suggerimenti di ricerca
     */
    private void loadSuggestions() {
        // Update suggestions based on available data
        SwingWorker<String, Void> suggestionsWorker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                try {
                    int hackathonCount = controller.getTuttiHackathon().size();
                    int teamCount = controller.getTuttiTeam().size();
                    int userCount = controller.getTuttiUtenti().size();
                    
                    return "💡 Database: " + hackathonCount + " hackathon, " + teamCount + " team, " + userCount + " utenti • Prova: \"hackathon 2024\", \"team alpha\", \"documenti pdf\"";
                } catch (Exception e) {
                    return "💡 Prova a cercare: \"hackathon 2024\", \"team alpha\", \"documenti pdf\"";
                }
            }
            
            @Override
            protected void done() {
                try {
                    suggestionsLabel.setText(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    // Keep default suggestions
                } catch (Exception e) {
                    // Keep default suggestions
                }
            }
        };
        
        suggestionsWorker.execute();
    }
    
    /**
     * Aggiorna i suggerimenti in base ai risultati
     */
    private void updateSuggestions() {
        if (filteredResults.isEmpty()) {
            suggestionsLabel.setText("🔍 Nessun risultato trovato. Prova termini diversi o rimuovi alcuni filtri.");
            suggestionsLabel.setForeground(new Color(231, 76, 60));
        } else {
            suggestionsLabel.setText("✅ " + filteredResults.size() + " risultati trovati. Usa i filtri per affinare la ricerca.");
            suggestionsLabel.setForeground(new Color(39, 174, 96));
        }
    }
    
    // Event Handlers
    
    private void handleViewDetails(ActionEvent e) {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < filteredResults.size()) {
            SearchResult result = filteredResults.get(selectedRow);
            showResultDetails(result);
        }
    }
    
    private void handleExportResults(ActionEvent e) {
        String[] formats = {"CSV", "Excel", "PDF"};
        String format = (String) JOptionPane.showInputDialog(this,
            "Seleziona formato di esportazione:",
            "Esporta Risultati", JOptionPane.QUESTION_MESSAGE,
            null, formats, formats[0]);
        
        if (format != null) {
            JOptionPane.showMessageDialog(this,
                "Esportazione di " + filteredResults.size() + " risultati in formato " + format + " completata!\n" +
                "File: search_results_" + System.currentTimeMillis() + "." + format.toLowerCase(),
                "Esportazione", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Mostra i dettagli di un risultato
     */
    private void showResultDetails(SearchResult result) {
        switch (result.getEntityType()) {
            case "HACKATHON":
                try {
                    Hackathon hackathon = controller.getHackathonById(result.getId());
                    HackathonDetailsDialog dialog = new HackathonDetailsDialog(mainFrame, controller, hackathon);
                    dialog.setVisible(true);
                } catch (Exception e) {
                    showErrorDialog("Errore nel caricamento dettagli hackathon");
                }
                break;
                
            case "TEAM":
                showTeamDetails(result.getId());
                break;
                
            case "USER":
                showUserDetails(result.getId());
                break;
                
            case "DOCUMENT":
                try {
                    @SuppressWarnings("unused") // Il valore del documento non viene utilizzato ma serve per validazione
                    Documento documento = controller.getDocumentoById(result.getId());
                    FileViewerDialog dialog = new FileViewerDialog(mainFrame, controller);
                    dialog.setVisible(true);
                } catch (Exception e) {
                    showErrorDialog("Errore nel caricamento dettagli documento");
                }
                break;
                
            case "PROGRESS":
                showProgressDetails(result);
                break;
                
            default:
                JOptionPane.showMessageDialog(this,
                    "Dettagli per " + result.getName() + "\n" +
                    "Tipo: " + result.getType() + "\n" +
                    "Stato: " + result.getStatus() + "\n" +
                    "Descrizione: " + result.getDescription(),
                    "Dettagli", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Mostra dettagli team
     */
    private void showTeamDetails(int teamId) {
        try {
            Team team = controller.getTeamById(teamId);
            int memberCount = controller.getTeamDAO().contaMembri(teamId);
            List<Progress> teamProgress = controller.getProgressiTeam(teamId);
            
            StringBuilder details = new StringBuilder();
            details.append("👥 DETTAGLI TEAM\n");
            details.append("═══════════════════════════════════════\n\n");
            details.append("Nome: ").append(team.getNome()).append("\n");
            details.append("Membri: ").append(memberCount).append("/").append(team.getDimensioneMassima()).append("\n");
            details.append("Hackathon: ").append(team.getHackathonId()).append("\n");
            details.append("Capo Team: Utente ").append(team.getCapoTeamId()).append("\n");
            details.append("Progressi: ").append(teamProgress.size()).append("\n");
            
            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 200));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Dettagli Team", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showErrorDialog("Errore nel caricamento dettagli team");
        }
    }
    
    /**
     * Mostra dettagli utente
     */
    private void showUserDetails(int userId) {
        try {
            Utente user = controller.getUtenteById(userId);
            
            JOptionPane.showMessageDialog(this,
                "👤 PROFILO UTENTE\n\n" +
                "Nome: " + user.getNome() + " " + user.getCognome() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Login: " + user.getLogin() + "\n" +
                "Ruolo: " + user.getRuolo(),
                "Dettagli Utente", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showErrorDialog("Errore nel caricamento dettagli utente");
        }
    }
    
    /**
     * Mostra dialog di errore
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, ERRORE, JOptionPane.ERROR_MESSAGE);
    }
    
    // Utility methods
    
    /**
     * Calcola il punteggio medio di un team
     */
    private double calculateTeamAverageScore(int teamId) {
        try {
            List<Valutazione> evaluations = controller.getValutazioniTeam(teamId);
            if (!evaluations.isEmpty()) {
                return evaluations.stream().mapToInt(Valutazione::getVoto).average().orElse(0);
            }
        } catch (Exception e) {
            // Skip score calculation on error
        }
        return 0;
    }
    
    private String getHackathonStatus(Hackathon hackathon) {
        if (hackathon.isEventoConcluso()) {
            return "Concluso";
        } else if (hackathon.isEventoAvviato()) {
            return "In corso";
        } else if (hackathon.isRegistrazioniAperte()) {
            return "Registrazioni aperte";
        } else {
            return "In preparazione";
        }
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.valueOf(Math.round(bytes / 102.4) / 10.0) + " KB";
        if (bytes < 1024 * 1024 * 1024) return String.valueOf(Math.round(bytes / (102.4 * 1024.0)) / 10.0) + " MB";
        return String.valueOf(Math.round(bytes / (102.4 * 1024.0 * 1024.0)) / 10.0) + " GB";
    }
    
    private String getMimeTypeDescription(String mimeType) {
        if (mimeType == null) return "Sconosciuto";
        
        switch (mimeType.toLowerCase()) {
            case "application/pdf": return "PDF";
            case "application/msword": return "Word";
            case "text/plain": return "Testo";
            case "image/jpeg", "image/jpg": return "JPEG";
            case "image/png": return "PNG";
            default: return mimeType.substring(mimeType.lastIndexOf('/') + 1).toUpperCase();
        }
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font(SEGOE_UI, Font.BOLD, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    /**
     * Classe per rappresentare un risultato di ricerca
     */
    private static class SearchResult {
        private final String type;
        private final String name;
        private final String description;
        private final String status;
        private final String date;
        private final double score;
        private final int id;
        private final String entityType;
        
        public SearchResult(Builder builder) {
            this.type = builder.type;
            this.name = builder.name;
            this.description = builder.description;
            this.status = builder.status;
            this.date = builder.date;
            this.score = builder.score;
            this.id = builder.id;
            this.entityType = builder.entityType;
        }
        
        public static class Builder {
            private String type;
            private String name;
            private String description;
            private String status;
            private String date;
            private double score;
            private int id;
            private String entityType;
            
            public Builder type(String type) { this.type = type; return this; }
            public Builder name(String name) { this.name = name; return this; }
            public Builder description(String description) { this.description = description; return this; }
            public Builder status(String status) { this.status = status; return this; }
            public Builder date(String date) { this.date = date; return this; }
            public Builder score(double score) { this.score = score; return this; }
            public Builder id(int id) { this.id = id; return this; }
            public Builder entityType(String entityType) { this.entityType = entityType; return this; }
            
            public SearchResult build() {
                return new SearchResult(this);
            }
        }
        
        // Getters
        public String getType() { return type; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getStatus() { return status; }
        public String getDate() { return date; }
        public double getScore() { return score; }
        public int getId() { return id; }
        public String getEntityType() { return entityType; }
    }
    
    /**
     * Mostra i dettagli di un progresso
     */
    private void showProgressDetails(SearchResult result) {
        JDialog dialog = new JDialog(mainFrame, "Dettagli Progresso", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel titleLabel = new JLabel("📈 " + result.getName());
        titleLabel.setFont(new Font(SEGOE_UI, Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        // Dettagli
        JTextArea detailsArea = new JTextArea(10, 40);
        detailsArea.setEditable(false);
        detailsArea.setBackground(new Color(248, 249, 250));
        detailsArea.setFont(new Font(SEGOE_UI, Font.PLAIN, 12));
        detailsArea.setText(
            "Titolo: " + result.getName() + "\n" +
            "Tipo: " + result.getType() + "\n" +
            "Stato: " + result.getStatus() + "\n" +
            "Descrizione: " + result.getDescription() + "\n" +
            "Data: " + result.getDate() + "\n" +
            "ID: " + result.getId() + "\n\n" +
            "Questo progresso è stato caricato dal team e " +
            (result.getStatus().contains("Commentato") ? "ha ricevuto un commento dal giudice" : "è in attesa di commenti") + "."
        );
        
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        
        // Pulsante chiudi
        JButton closeButton = createStyledButton("❌ Chiudi", new Color(149, 165, 166));
        closeButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Aggiorna i dati del pannello
     */
    public void refreshData() {
        performSearch();
    }
}
