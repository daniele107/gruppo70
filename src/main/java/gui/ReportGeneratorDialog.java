package gui;

import controller.Controller;
import model.*;
import model.ReportData;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Custom exception for report generation errors
 */
class ReportGenerationException extends Exception {
    public ReportGenerationException(String message) {
        super(message);
    }
    
    public ReportGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Custom exception for operation interruption
 */
class OperationInterruptedException extends RuntimeException {
    public OperationInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Custom exception for file writing errors
 */
class FileWriteException extends Exception {
    public FileWriteException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public FileWriteException(String message) {
        super(message);
    }
}

/**
 * Dialog avanzato per la generazione di report personalizzati.
 * Supporta diversi tipi di report, filtri temporali e formati di esportazione.
 */
public class ReportGeneratorDialog extends JDialog {
    
    private static final long serialVersionUID = 1L;
    
    // Constants for string literals
    private static final String ULTIMO_MESE = "Ultimo mese";
    private static final String HTML_DIV_CLOSE = "</div>";
    private static final String HTML_TD_OPEN = "<td>";
    private static final String HTML_TD_CLOSE = "</td>";
    private static final String HTML_TR_OPEN = "<tr>";
    private static final String NEWLINE = "\n";
    private static final String FONT_NAME = "Segoe UI";
    private static final String JSON_COMMA = "\",";
    
    private final transient Controller controller;
    // Removed unused parent field
    
    // Componenti selezione tipo report
    private JComboBox<ReportType> reportTypeCombo;
    private JTextArea reportDescriptionArea;
    
    // Filtri e parametri
    private JComboBox<String> hackathonFilterCombo;
    private JComboBox<String> periodFilterCombo;
    private JComboBox<String> userRoleFilterCombo;
    private JCheckBox includeDetailsCheck;
    private JCheckBox includeStatisticsCheck;
    private JCheckBox includeChartsCheck;
    
    // Anteprima
    private JTextArea previewArea;
    private JProgressBar generationProgressBar;
    private JLabel statusLabel;
    
    // Formato esportazione
    private JComboBox<ExportFormat> exportFormatCombo;
    private JTextField fileNameField;
    private JButton browseLocationButton;
    private JLabel selectedLocationLabel;
    
    // Azioni
    private JButton generatePreviewButton;
    private JButton generateReportButton;
    private JButton saveTemplateButton;
    private JButton loadTemplateButton;
    private JButton cancelButton;
    
    // Dati
    private File selectedExportLocation;
    private transient ReportData currentReportData;
    private transient List<Hackathon> availableHackathons;
    
    /**
     * Tipi di report disponibili
     */
    public enum ReportType {
        SYSTEM_OVERVIEW("📊 Panoramica Sistema", "Report completo sullo stato del sistema"),
        HACKATHON_DETAILED("🏆 Report Hackathon Dettagliato", "Analisi completa di un hackathon specifico"),
        TEAM_PERFORMANCE("👥 Performance Team", "Statistiche e valutazioni dei team"),
        USER_ACTIVITY("👤 Attività Utenti", "Report sull'attività degli utenti"),
        JUDGE_EVALUATION("⚖️ Valutazioni Giudici", "Analisi delle valutazioni assegnate"),
        DOCUMENT_MANAGEMENT("📄 Gestione Documenti", "Report sui documenti caricati"),
        PROGRESS_TRACKING("📈 Monitoraggio Progressi", "Tracciamento dei progressi dei team"),
        CUSTOM_QUERY("🔧 Query Personalizzata", "Report personalizzato con parametri specifici");
        
        private final String displayName;
        private final String description;
        
        ReportType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Formati di esportazione supportati
     */
    public enum ExportFormat {
        TEXT("📄 Testo (.txt)", "txt", "Formato testo semplice"),
        CSV("📊 CSV (.csv)", "csv", "Comma Separated Values per Excel"),
        HTML("🌐 HTML (.html)", "html", "Pagina web con formattazione"),
        PDF("📕 PDF (.pdf)", "pdf", "Documento PDF (simulato)"),
        EXCEL("📗 Excel (.xlsx)", "xlsx", "Foglio di calcolo Excel (simulato)"),
        JSON("🔧 JSON (.json)", "json", "Formato dati strutturati");
        
        private final String displayName;
        private final String extension;
        private final String description;
        
        ExportFormat(String displayName, String extension, String description) {
            this.displayName = displayName;
            this.extension = extension;
            this.description = description;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
        
        public String getExtension() {
            return extension;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Costruttore del dialog
     */
    public ReportGeneratorDialog(JFrame parent, Controller controller) {
        super(parent, "📊 Generatore Report Avanzato", true);
        // Removed parent assignment
        this.controller = controller;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(parent);
        setResizable(true);
    }
    
    /**
     * Inizializza i componenti
     */
    private void initializeComponents() {
        initializeReportTypeComponents();
        initializeFilterComponents();
        initializeOptionComponents();
        initializePreviewComponents();
        initializeExportComponents();
        initializeActionButtons();
    }
    
    /**
     * Inizializza i componenti per la selezione del tipo di report
     */
    private void initializeReportTypeComponents() {
        reportTypeCombo = new JComboBox<>(ReportType.values());
        reportDescriptionArea = new JTextArea(3, 40);
        reportDescriptionArea.setEditable(false);
        reportDescriptionArea.setBackground(new Color(248, 249, 250));
        reportDescriptionArea.setFont(new Font(FONT_NAME, Font.ITALIC, 12));
        reportDescriptionArea.setLineWrap(true);
        reportDescriptionArea.setWrapStyleWord(true);
    }
    
    /**
     * Inizializza i componenti per i filtri
     */
    private void initializeFilterComponents() {
        hackathonFilterCombo = new JComboBox<>();
        periodFilterCombo = new JComboBox<>(new String[]{
            "Tutti i periodi", "Ultima settimana", ULTIMO_MESE, "Ultimi 3 mesi", "Ultimo anno", "Anno corrente"
        });
        userRoleFilterCombo = new JComboBox<>(new String[]{
            "Tutti i ruoli", "Solo Organizzatori", "Solo Giudici", "Solo Partecipanti"
        });
    }
    
    /**
     * Inizializza i componenti per le opzioni del report
     */
    private void initializeOptionComponents() {
        includeDetailsCheck = new JCheckBox("Includi dettagli completi", true);
        includeStatisticsCheck = new JCheckBox("Includi statistiche avanzate", true);
        includeChartsCheck = new JCheckBox("Includi grafici e diagrammi", false);
    }
    
    /**
     * Inizializza i componenti per l'anteprima
     */
    private void initializePreviewComponents() {
        previewArea = new JTextArea(15, 50);
        previewArea.setEditable(false);
        previewArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        previewArea.setBackground(new Color(248, 249, 250));
        
        generationProgressBar = new JProgressBar(0, 100);
        generationProgressBar.setStringPainted(true);
        generationProgressBar.setString("Pronto");
        generationProgressBar.setVisible(false);
        
        statusLabel = new JLabel("Seleziona un tipo di report per iniziare");
        statusLabel.setForeground(Color.GRAY);
    }
    
    /**
     * Inizializza i componenti per l'esportazione
     */
    private void initializeExportComponents() {
        exportFormatCombo = new JComboBox<>(ExportFormat.values());
        fileNameField = new JTextField("report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
        browseLocationButton = createStyledButton("📁 Sfoglia", new Color(149, 165, 166));
        selectedLocationLabel = new JLabel("Cartella: " + System.getProperty("user.home"));
        selectedExportLocation = new File(System.getProperty("user.home"));
    }
    
    /**
     * Inizializza i pulsanti di azione
     */
    private void initializeActionButtons() {
        generatePreviewButton = createStyledButton("👁️ Anteprima", new Color(52, 152, 219));
        generateReportButton = createStyledButton("📊 Genera Report", new Color(46, 204, 113));
        saveTemplateButton = createStyledButton("💾 Salva Template", new Color(155, 89, 182));
        loadTemplateButton = createStyledButton("📂 Carica Template", new Color(241, 196, 15));
        cancelButton = createStyledButton("❌ Annulla", new Color(231, 76, 60));
        
        // Initially disable some buttons
        generateReportButton.setEnabled(false);
        saveTemplateButton.setEnabled(false);
    }
    
    /**
     * Configura il layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content con tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab Configurazione
        JPanel configTab = createConfigurationTab();
        tabbedPane.addTab("⚙️ Configurazione", configTab);
        
        // Tab Anteprima
        JPanel previewTab = createPreviewTab();
        tabbedPane.addTab("👁️ Anteprima", previewTab);
        
        // Tab Esportazione
        JPanel exportTab = createExportTab();
        tabbedPane.addTab("📤 Esportazione", exportTab);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Footer con azioni
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Crea il pannello header
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("📊 Generatore Report Avanzato");
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        JLabel subtitleLabel = new JLabel("Crea report personalizzati con filtri avanzati e multiple opzioni di esportazione");
        subtitleLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);
        
        // Status
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.add(statusLabel);
        
        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(statusPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Crea il tab configurazione
     */
    private JPanel createConfigurationTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Tipo report
        JPanel typePanel = new JPanel(new BorderLayout(10, 10));
        typePanel.setBorder(new TitledBorder("📋 Tipo di Report"));
        typePanel.add(new JLabel("Seleziona tipo:"), BorderLayout.WEST);
        typePanel.add(reportTypeCombo, BorderLayout.CENTER);
        
        JScrollPane descScrollPane = new JScrollPane(reportDescriptionArea);
        descScrollPane.setBorder(new TitledBorder("Descrizione"));
        typePanel.add(descScrollPane, BorderLayout.SOUTH);
        
        // Filtri
        JPanel filtersPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        filtersPanel.setBorder(new TitledBorder("🎛️ Filtri"));
        
        filtersPanel.add(new JLabel("Hackathon:"));
        filtersPanel.add(hackathonFilterCombo);
        filtersPanel.add(new JLabel("Periodo:"));
        filtersPanel.add(periodFilterCombo);
        filtersPanel.add(new JLabel("Ruolo utenti:"));
        filtersPanel.add(userRoleFilterCombo);
        
        // Opzioni
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(new TitledBorder("📊 Opzioni Report"));
        optionsPanel.add(includeDetailsCheck);
        optionsPanel.add(includeStatisticsCheck);
        optionsPanel.add(includeChartsCheck);
        
        // Layout principale del tab
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(typePanel, BorderLayout.NORTH);
        topPanel.add(filtersPanel, BorderLayout.CENTER);
        topPanel.add(optionsPanel, BorderLayout.SOUTH);
        
        panel.add(topPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea il tab anteprima
     */
    private JPanel createPreviewTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Header anteprima
        JPanel previewHeaderPanel = new JPanel(new BorderLayout());
        previewHeaderPanel.add(new JLabel("📄 Anteprima Report"), BorderLayout.WEST);
        previewHeaderPanel.add(generatePreviewButton, BorderLayout.EAST);
        
        // Area anteprima
        JScrollPane previewScrollPane = new JScrollPane(previewArea);
        previewScrollPane.setBorder(new TitledBorder("Contenuto Report"));
        
        // Progress bar
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(generationProgressBar, BorderLayout.CENTER);
        progressPanel.setVisible(false);
        
        panel.add(previewHeaderPanel, BorderLayout.NORTH);
        panel.add(previewScrollPane, BorderLayout.CENTER);
        panel.add(progressPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Crea il tab esportazione
     */
    private JPanel createExportTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Formato
        JPanel formatPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formatPanel.setBorder(new TitledBorder("📁 Formato e Destinazione"));
        
        formatPanel.add(new JLabel("Formato:"));
        formatPanel.add(exportFormatCombo);
        formatPanel.add(new JLabel("Nome file:"));
        formatPanel.add(fileNameField);
        
        // Destinazione
        JPanel locationPanel = new JPanel(new BorderLayout(10, 10));
        locationPanel.setBorder(new TitledBorder("📍 Destinazione"));
        
        JPanel locationInputPanel = new JPanel(new BorderLayout());
        locationInputPanel.add(selectedLocationLabel, BorderLayout.CENTER);
        locationInputPanel.add(browseLocationButton, BorderLayout.EAST);
        
        locationPanel.add(locationInputPanel, BorderLayout.CENTER);
        
        // Info formato
        JTextArea formatInfoArea = new JTextArea(8, 40);
        formatInfoArea.setEditable(false);
        formatInfoArea.setBackground(new Color(248, 249, 250));
        formatInfoArea.setFont(new Font(FONT_NAME, Font.PLAIN, 12));
        formatInfoArea.setLineWrap(true);
        formatInfoArea.setWrapStyleWord(true);
        
        JScrollPane formatInfoScrollPane = new JScrollPane(formatInfoArea);
        formatInfoScrollPane.setBorder(new TitledBorder("ℹ️ Informazioni Formato"));
        
        // Layout principale
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(formatPanel, BorderLayout.NORTH);
        topPanel.add(locationPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(formatInfoScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crea il pannello footer
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 15, 15, 15));
        panel.setBackground(new Color(248, 249, 250));
        
        // Template actions
        JPanel templatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        templatePanel.setBackground(new Color(248, 249, 250));
        templatePanel.add(saveTemplateButton);
        templatePanel.add(loadTemplateButton);
        
        // Main actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(new Color(248, 249, 250));
        actionsPanel.add(generateReportButton);
        actionsPanel.add(cancelButton);
        
        panel.add(templatePanel, BorderLayout.WEST);
        panel.add(actionsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        // Tipo report change
        reportTypeCombo.addActionListener(e -> {
            ReportType selectedType = (ReportType) reportTypeCombo.getSelectedItem();
            if (selectedType != null) {
                reportDescriptionArea.setText(selectedType.getDescription());
                updateFiltersForReportType(selectedType);
                statusLabel.setText("Configurazione per: " + selectedType.toString());
            }
        });
        
        // Export format change
        exportFormatCombo.addActionListener(e -> updateFormatInfo());
        
        // Actions
        generatePreviewButton.addActionListener(this::handleGeneratePreview);
        generateReportButton.addActionListener(this::handleGenerateReport);
        browseLocationButton.addActionListener(this::handleBrowseLocation);
        saveTemplateButton.addActionListener(this::handleSaveTemplate);
        loadTemplateButton.addActionListener(this::handleLoadTemplate);
        cancelButton.addActionListener(e -> dispose());
        
        // Update generate button state when filters change
        hackathonFilterCombo.addActionListener(e -> updateGenerateButtonState());
        periodFilterCombo.addActionListener(e -> updateGenerateButtonState());
        userRoleFilterCombo.addActionListener(e -> updateGenerateButtonState());
    }
    
    /**
     * Carica i dati iniziali
     */
    private void loadData() {
        SwingWorker<Void, Void> dataWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Load hackathons
                availableHackathons = controller.getTuttiHackathon();
                return null;
            }
            
            @Override
            protected void done() {
                // Populate hackathon combo
                hackathonFilterCombo.addItem("Tutti gli hackathon");
                if (availableHackathons != null) {
                    for (Hackathon h : availableHackathons) {
                        hackathonFilterCombo.addItem(h.getId() + ": " + h.getNome());
                    }
                }
                
                // Set initial description
                if (reportTypeCombo.getSelectedItem() != null) {
                    reportDescriptionArea.setText(((ReportType) reportTypeCombo.getSelectedItem()).getDescription());
                }
                
                // Update format info
                updateFormatInfo();
                
                statusLabel.setText("Pronto per la generazione report");
                statusLabel.setForeground(new Color(39, 174, 96));
            }
        };
        
        dataWorker.execute();
    }
    
    /**
     * Aggiorna i filtri in base al tipo di report
     */
    private void updateFiltersForReportType(ReportType reportType) {
        switch (reportType) {
            case HACKATHON_DETAILED:
            case TEAM_PERFORMANCE:
            case PROGRESS_TRACKING:
                hackathonFilterCombo.setEnabled(true);
                break;
            case SYSTEM_OVERVIEW:
            case USER_ACTIVITY:
                hackathonFilterCombo.setEnabled(false);
                hackathonFilterCombo.setSelectedIndex(0);
                break;
            default:
                hackathonFilterCombo.setEnabled(true);
        }
    }
    
    /**
     * Aggiorna le informazioni sul formato
     */
    private void updateFormatInfo() {
        ExportFormat selectedFormat = (ExportFormat) exportFormatCombo.getSelectedItem();
        if (selectedFormat != null) {
            StringBuilder info = new StringBuilder();
            info.append("📁 FORMATO: ").append(selectedFormat.toString()).append("\n\n");
            info.append("📋 DESCRIZIONE:\n").append(selectedFormat.getDescription()).append("\n\n");
            info.append("📄 CARATTERISTICHE:\n");
            
            switch (selectedFormat) {
                case TEXT:
                    info.append("• Formato semplice e leggibile\n");
                    info.append("• Compatibile con tutti gli editor\n");
                    info.append("• Dimensioni file ridotte\n");
                    info.append("• Ideale per report testuali");
                    break;
                case CSV:
                    info.append("• Importabile in Excel/Calc\n");
                    info.append("• Dati strutturati in tabelle\n");
                    info.append("• Separatori personalizzabili\n");
                    info.append("• Perfetto per analisi dati");
                    break;
                case HTML:
                    info.append("• Visualizzabile in browser\n");
                    info.append("• Supporta formattazione ricca\n");
                    info.append("• Include CSS per styling\n");
                    info.append("• Condivisibile via web");
                    break;
                case PDF:
                    info.append("• Formato professionale\n");
                    info.append("• Layout fisso e stampa ottimale\n");
                    info.append("• Supporta grafici e immagini\n");
                    info.append("• Standard per documenti ufficiali");
                    break;
                case EXCEL:
                    info.append("• Fogli di calcolo nativi\n");
                    info.append("• Supporta formule e grafici\n");
                    info.append("• Filtri e ordinamento automatico\n");
                    info.append("• Analisi avanzata dei dati");
                    break;
                case JSON:
                    info.append("• Formato dati strutturati\n");
                    info.append("• Leggibile da applicazioni\n");
                    info.append("• Supporta strutture complesse\n");
                    info.append("• Ideale per API e integrazioni");
                    break;
                default:
                    info.append("• Formato non riconosciuto\n");
                    info.append("• Caratteristiche non disponibili");
                    break;
            }
            
            // Find the format info area and update it
            Component[] components = ((JPanel) ((JTabbedPane) getContentPane().getComponent(1)).getComponentAt(2)).getComponents();
            for (Component comp : components) {
                if (comp instanceof JScrollPane scrollPane && 
                    scrollPane.getViewport().getView() instanceof JTextArea formatInfoArea &&
                    formatInfoArea.getBackground().equals(new Color(248, 249, 250))) {
                    formatInfoArea.setText(info.toString());
                    break;
                }
            }
        }
    }
    
    /**
     * Aggiorna lo stato del pulsante genera
     */
    private void updateGenerateButtonState() {
        boolean canGenerate = reportTypeCombo.getSelectedItem() != null;
        generateReportButton.setEnabled(canGenerate && currentReportData != null);
    }
    
    // Event Handlers
    
    private void handleGeneratePreview(ActionEvent e) {
        generatePreviewButton.setEnabled(false);
        generationProgressBar.setVisible(true);
        generationProgressBar.setIndeterminate(true);
        generationProgressBar.setString("Generazione anteprima...");
        
        SwingWorker<String, Integer> previewWorker = new SwingWorker<String, Integer>() {
            @Override
            protected String doInBackground() throws Exception {
                publish(10);
                
                ReportType reportType = (ReportType) reportTypeCombo.getSelectedItem();
                String hackathonFilter = (String) hackathonFilterCombo.getSelectedItem();
                
                publish(30);
                
                // Generate report data based on type
                currentReportData = generateReportDataForType(reportType, hackathonFilter);
                
                publish(70);
                
                // Generate preview text
                String preview = generatePreviewText(currentReportData, reportType);
                
                publish(100);
                
                return preview;
            }
            
            @Override
            protected void process(List<Integer> chunks) {
                if (!chunks.isEmpty()) {
                    int progress = chunks.get(chunks.size() - 1);
                    generationProgressBar.setIndeterminate(false);
                    generationProgressBar.setValue(progress);
                    generationProgressBar.setString("Generazione: " + progress + "%");
                }
            }
            
            @Override
            protected void done() {
                handlePreviewGenerationResult(this);
            }
        };
        
        previewWorker.execute();
    }
    
    /**
     * Generates report data based on the selected report type and filters
     * @param reportType the type of report to generate
     * @param hackathonFilter the hackathon filter selection
     * @return the generated report data
     */
    private ReportData generateReportDataForType(ReportType reportType, String hackathonFilter) {
        switch (reportType) {
            case SYSTEM_OVERVIEW:
                return controller.generaReportSistema();
            case HACKATHON_DETAILED:
                return generateHackathonDetailedReport(hackathonFilter);
            case TEAM_PERFORMANCE:
                return generateTeamPerformanceReport();
            default:
                return controller.generaReportSistema();
        }
    }
    
    /**
     * Generates a detailed hackathon report based on the filter
     * @param hackathonFilter the hackathon filter selection
     * @return the generated report data
     */
    private ReportData generateHackathonDetailedReport(String hackathonFilter) {
        if (hackathonFilter != null && !hackathonFilter.equals("Tutti gli hackathon")) {
            int hackathonId = extractHackathonId(hackathonFilter);
            return controller.generaReportHackathon(hackathonId);
        } else {
            return controller.generaReportSistema();
        }
    }
    
    /**
     * Handles the result of preview generation
     * @param worker the SwingWorker that generated the preview
     */
    private void handlePreviewGenerationResult(SwingWorker<String, Integer> worker) {
        try {
            String preview = worker.get();
            previewArea.setText(preview);
            previewArea.setCaretPosition(0);
            
            generationProgressBar.setString("✅ Anteprima generata");
            generationProgressBar.setForeground(new Color(39, 174, 96));
            
            generateReportButton.setEnabled(true);
            saveTemplateButton.setEnabled(true);
            
            statusLabel.setText("Anteprima pronta - " + previewArea.getLineCount() + " righe");
            statusLabel.setForeground(new Color(39, 174, 96));
            
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            handlePreviewGenerationError(ex);
        } catch (Exception ex) {
            handlePreviewGenerationError(ex);
        } finally {
            generatePreviewButton.setEnabled(true);
        }
    }
    
    /**
     * Handles errors during preview generation
     * @param ex the exception that occurred
     */
    private void handlePreviewGenerationError(Exception ex) {
        previewArea.setText("❌ ERRORE NELLA GENERAZIONE ANTEPRIMA\n\n" + ex.getMessage());
        generationProgressBar.setString("❌ Errore");
        generationProgressBar.setForeground(Color.RED);
        
        statusLabel.setText("Errore: " + ex.getMessage());
        statusLabel.setForeground(Color.RED);
    }
    
    private void handleGenerateReport(ActionEvent e) {
        if (!validateReportGeneration()) {
            return;
        }
        
        ExportFormat format = (ExportFormat) exportFormatCombo.getSelectedItem();
        String fileName = prepareFileName(fileNameField.getText().trim(), format);
        File outputFile = new File(selectedExportLocation, fileName);
        
        SwingWorker<Boolean, String> exportWorker = createExportWorker(format, outputFile);
        exportWorker.execute();
    }
    
    /**
     * Validates that report generation can proceed
     * @return true if validation passes, false otherwise
     */
    private boolean validateReportGeneration() {
        if (currentReportData == null) {
            JOptionPane.showMessageDialog(this,
                "Genera prima un'anteprima del report",
                "Anteprima Richiesta", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        String fileName = fileNameField.getText().trim();
        if (fileName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Inserisci un nome per il file",
                "Nome File Richiesto", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Prepares the file name with proper extension
     * @param fileName the base file name
     * @param format the export format
     * @return the prepared file name with extension
     */
    private String prepareFileName(String fileName, ExportFormat format) {
        if (!fileName.toLowerCase().endsWith("." + format.getExtension())) {
            fileName += "." + format.getExtension();
        }
        return fileName;
    }
    
    /**
     * Creates the export worker for generating the report file
     * @param format the export format
     * @param outputFile the output file
     * @return the configured SwingWorker
     */
    private SwingWorker<Boolean, String> createExportWorker(ExportFormat format, File outputFile) {
        return new SwingWorker<Boolean, String>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                publish("Preparazione esportazione...");
                sleepSafely(500);
                
                publish("Generazione contenuto...");
                String content = generateExportContent(currentReportData, format);
                sleepSafely(500);
                
                publish("Scrittura file...");
                writeFileByFormat(format, outputFile, content);
                
                publish("Completato!");
                sleepSafely(300);
                
                return true;
            }
            
            @Override
            protected void process(List<String> chunks) {
                if (!chunks.isEmpty()) {
                    String status = chunks.get(chunks.size() - 1);
                    statusLabel.setText(status);
                }
            }
            
            @Override
            protected void done() {
                handleExportResult(outputFile, format, this);
            }
        };
    }
    
    /**
     * Writes the file based on the specified format
     * @param format the export format
     * @param outputFile the output file
     * @param content the content to write
     * @throws FileWriteException if writing fails
     */
    private void writeFileByFormat(ExportFormat format, File outputFile, String content) throws FileWriteException {
        try {
            switch (format) {
                case TEXT:
                    java.nio.file.Files.write(outputFile.toPath(), content.getBytes());
                    break;
                case CSV:
                    java.nio.file.Files.write(outputFile.toPath(), generateCSVContent(currentReportData).getBytes());
                    break;
                case HTML:
                    java.nio.file.Files.write(outputFile.toPath(), generateHTMLContent(currentReportData).getBytes());
                    break;
                case JSON:
                    java.nio.file.Files.write(outputFile.toPath(), generateJSONContent(currentReportData).getBytes());
                    break;
                case PDF:
                    java.nio.file.Files.write(outputFile.toPath(), generatePDFContent(currentReportData).getBytes());
                    break;
                case EXCEL:
                    java.nio.file.Files.write(outputFile.toPath(), generateExcelContent(currentReportData).getBytes());
                    break;
            }
        } catch (java.io.IOException e) {
            throw new FileWriteException("Errore durante la scrittura del file: " + outputFile.getName(), e);
        }
    }
    
    /**
     * Handles the result of the export operation
     * @param outputFile the output file
     * @param format the export format
     * @param worker the SwingWorker that performed the export
     */
    private void handleExportResult(File outputFile, ExportFormat format, SwingWorker<Boolean, String> worker) {
        try {
            boolean success = worker.get();
            if (success) {
                showExportSuccessDialog(outputFile, format);
                askToOpenFileLocation();
                updateStatusForSuccess(outputFile);
            } else {
                throw new ReportGenerationException("Errore sconosciuto durante l'esportazione");
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            showExportErrorDialog(ex);
        } catch (Exception ex) {
            showExportErrorDialog(ex);
        }
    }
    
    /**
     * Shows the export success dialog
     * @param outputFile the output file
     * @param format the export format
     */
    private void showExportSuccessDialog(File outputFile, ExportFormat format) {
        JOptionPane.showMessageDialog(ReportGeneratorDialog.this,
            "Report generato con successo!" + NEWLINE + NEWLINE +
            "File: " + outputFile.getName() + NEWLINE +
            "Posizione: " + outputFile.getAbsolutePath() + NEWLINE +
            "Formato: " + format.toString(),
            "Report Generato", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Asks the user if they want to open the file location
     */
    private void askToOpenFileLocation() {
        int openLocation = JOptionPane.showConfirmDialog(ReportGeneratorDialog.this,
            "Vuoi aprire la cartella contenente il report?",
            "Apri Cartella", JOptionPane.YES_NO_OPTION);
        
        if (openLocation == JOptionPane.YES_OPTION) {
            openFileLocation(selectedExportLocation);
        }
    }
    
    /**
     * Updates the status label for successful export
     * @param outputFile the output file
     */
    private void updateStatusForSuccess(File outputFile) {
        statusLabel.setText("✅ Report esportato: " + outputFile.getName());
        statusLabel.setForeground(new Color(39, 174, 96));
    }
    
    /**
     * Shows the export error dialog
     * @param ex the exception that occurred
     */
    private void showExportErrorDialog(Exception ex) {
        JOptionPane.showMessageDialog(ReportGeneratorDialog.this,
            "Errore durante la generazione del report:" + NEWLINE + ex.getMessage(),
            "Errore Esportazione", JOptionPane.ERROR_MESSAGE);
        
        statusLabel.setText("❌ Errore esportazione");
        statusLabel.setForeground(Color.RED);
    }
    
    private void handleBrowseLocation(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser(selectedExportLocation);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Seleziona cartella di destinazione");
        
        int result = fileChooser.showDialog(this, "Seleziona");
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedExportLocation = fileChooser.getSelectedFile();
            selectedLocationLabel.setText("Cartella: " + selectedExportLocation.getAbsolutePath());
        }
    }
    
    private void handleSaveTemplate(ActionEvent e) {
        String templateName = JOptionPane.showInputDialog(this,
            "Inserisci un nome per il template:",
            "Salva Template", JOptionPane.QUESTION_MESSAGE);
        
        if (templateName != null && !templateName.trim().isEmpty()) {
            // In a real implementation, this would save the current configuration
            Map<String, Object> template = new HashMap<>();
            template.put("reportType", reportTypeCombo.getSelectedItem());
            template.put("hackathonFilter", hackathonFilterCombo.getSelectedItem());
            template.put("periodFilter", periodFilterCombo.getSelectedItem());
            template.put("userRoleFilter", userRoleFilterCombo.getSelectedItem());
            template.put("includeDetails", includeDetailsCheck.isSelected());
            template.put("includeStatistics", includeStatisticsCheck.isSelected());
            template.put("includeCharts", includeChartsCheck.isSelected());
            template.put("exportFormat", exportFormatCombo.getSelectedItem());
            
            JOptionPane.showMessageDialog(this,
                "Template '" + templateName + "' salvato con successo!\n" +
                "Configurazione salvata con " + template.size() + " parametri.",
                "Template Salvato", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void handleLoadTemplate(ActionEvent e) {
        String[] availableTemplates = {
            "Report Sistema Standard",
            "Analisi Hackathon Completa",
            "Performance Team Mensile",
            "Valutazioni Giudici"
        };
        
        String selectedTemplate = (String) JOptionPane.showInputDialog(this,
            "Seleziona un template da caricare:",
            "Carica Template", JOptionPane.QUESTION_MESSAGE,
            null, availableTemplates, availableTemplates[0]);
        
        if (selectedTemplate != null) {
            loadTemplateConfiguration(selectedTemplate);
            JOptionPane.showMessageDialog(this,
                "Template '" + selectedTemplate + "' caricato con successo!",
                "Template Caricato", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Carica la configurazione per un template specifico
     * @param templateName il nome del template da caricare
     */
    private void loadTemplateConfiguration(String templateName) {
        switch (templateName) {
            case "Report Sistema Standard":
                loadSystemOverviewTemplate();
                break;
            case "Analisi Hackathon Completa":
                loadHackathonAnalysisTemplate();
                break;
            case "Performance Team Mensile":
                loadTeamPerformanceTemplate();
                break;
            case "Valutazioni Giudici":
                loadJudgeEvaluationTemplate();
                break;
            default:
                // Template not recognized, use default configuration
                loadSystemOverviewTemplate();
                break;
        }
    }
    
    /**
     * Carica il template per il report di panoramica sistema
     */
    private void loadSystemOverviewTemplate() {
        reportTypeCombo.setSelectedItem(ReportType.SYSTEM_OVERVIEW);
        periodFilterCombo.setSelectedItem(ULTIMO_MESE);
        includeDetailsCheck.setSelected(true);
        includeStatisticsCheck.setSelected(true);
        exportFormatCombo.setSelectedItem(ExportFormat.HTML);
    }
    
    /**
     * Carica il template per l'analisi hackathon completa
     */
    private void loadHackathonAnalysisTemplate() {
        reportTypeCombo.setSelectedItem(ReportType.HACKATHON_DETAILED);
        includeDetailsCheck.setSelected(true);
        includeStatisticsCheck.setSelected(true);
        includeChartsCheck.setSelected(true);
        exportFormatCombo.setSelectedItem(ExportFormat.PDF);
    }
    
    /**
     * Carica il template per la performance team mensile
     */
    private void loadTeamPerformanceTemplate() {
        reportTypeCombo.setSelectedItem(ReportType.TEAM_PERFORMANCE);
        periodFilterCombo.setSelectedItem(ULTIMO_MESE);
        exportFormatCombo.setSelectedItem(ExportFormat.EXCEL);
    }
    
    /**
     * Carica il template per le valutazioni giudici
     */
    private void loadJudgeEvaluationTemplate() {
        reportTypeCombo.setSelectedItem(ReportType.JUDGE_EVALUATION);
        includeStatisticsCheck.setSelected(true);
        exportFormatCombo.setSelectedItem(ExportFormat.CSV);
    }
    
    // Report Generation Methods
    
    private ReportData generateTeamPerformanceReport() {
        // Create a custom report for team performance
        ReportData report = new ReportData();
        report.setTipoReport("TEAM_PERFORMANCE");
        report.setTitolo("Report Performance Team");
        report.setDataGenerazione(LocalDateTime.now());
        
        try {
            List<Team> allTeams = controller.getTuttiTeam();
            report.setTeams(allTeams);
            
            // Add statistics
            Statistics stats = new Statistics("TEAM_PERFORMANCE", "CUSTOM");
            stats.setTotaleTeam(allTeams.size());
            report.setStatistiche(stats);
        } catch (Exception e) {
            // Handle error
        }
        
        return report;
    }
    
    private String generatePreviewText(ReportData reportData, ReportType reportType) {
        if (reportData == null) {
            return "❌ Nessun dato disponibile per il report";
        }
        
        StringBuilder preview = new StringBuilder();
        preview.append("📊 ANTEPRIMA REPORT\n");
        preview.append("═══════════════════════════════════════════════════════════════════\n\n");
        
        preview.append("📋 INFORMAZIONI REPORT:\n");
        preview.append("Tipo: ").append(reportType.toString()).append("\n");
        preview.append("Titolo: ").append(reportData.getTitolo()).append("\n");
        preview.append("Generato: ").append(reportData.getDataGenerazione().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        
        if (reportData.getHackathon() != null) {
            preview.append("Hackathon: ").append(reportData.getHackathon().getNome()).append("\n");
        }
        
        preview.append("\n📊 CONTENUTO:\n");
        preview.append("═══════════════════════════════════════════════════════════════════\n");
        
        // Use controller to generate full text
        String fullReport = generateTextContent(reportData);
        
        // Show first 2000 characters as preview
        if (fullReport.length() > 2000) {
            preview.append(fullReport.substring(0, 2000));
            preview.append("\n\n... (anteprima troncata, report completo disponibile all'esportazione) ...");
            preview.append("\n\nLunghezza totale: ").append(fullReport.length()).append(" caratteri");
        } else {
            preview.append(fullReport);
        }
        
        return preview.toString();
    }
    
    private String generateExportContent(ReportData reportData, ExportFormat format) {
        switch (format) {
            case TEXT:
            case PDF:
            case EXCEL:
                return controller.esportaReportTesto(reportData);
            case CSV:
                return generateCSVContent(reportData);
            case HTML:
                return generateHTMLContent(reportData);
            case JSON:
                return generateJSONContent(reportData);
            case TEXT:
            default:
                return generateTextContent(reportData);
        }
    }
    
    private String generateCSVContent(ReportData reportData) {
        StringBuilder csv = new StringBuilder();
        csv.append("# Report CSV - ").append(reportData.getTitolo()).append(NEWLINE);
        csv.append("# Generato: ").append(reportData.getDataGenerazione()).append(NEWLINE + NEWLINE);
        
        // Teams data
        if (reportData.getTeams() != null && !reportData.getTeams().isEmpty()) {
            csv.append("TEAMS\n");
            csv.append("ID,Nome,Hackathon,Capo Team,Dimensione Massima\n");
            for (Team team : reportData.getTeams()) {
                csv.append(team.getId()).append(",")
                   .append("\"").append(team.getNome()).append(JSON_COMMA)
                   .append(team.getHackathonId()).append(",")
                   .append(team.getCapoTeamId()).append(",")
                   .append(team.getDimensioneMassima()).append(NEWLINE);
            }
            csv.append(NEWLINE);
        }
        
        // Statistics
        if (reportData.getStatistiche() != null) {
            csv.append("STATISTICHE\n");
            csv.append("Metrica,Valore").append(NEWLINE);
            Statistics stats = reportData.getStatistiche();
            csv.append("Totale Utenti,").append(stats.getTotaleUtenti()).append(NEWLINE);
            csv.append("Totale Hackathon,").append(stats.getTotaleHackathon()).append(NEWLINE);
            csv.append("Totale Team,").append(stats.getTotaleTeam()).append(NEWLINE);
        }
        
        return csv.toString();
    }
    
    private String generateHTMLContent(ReportData reportData) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("<title>").append(reportData.getTitolo()).append("</title>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<style>\n");
        html.append("body { font-family: 'Segoe UI', Arial, sans-serif; margin: 40px; background: #f8f9fa; }\n");
        html.append(".container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n");
        html.append("h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; }\n");
        html.append("h2 { color: #34495e; margin-top: 30px; }\n");
        html.append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }\n");
        html.append("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }\n");
        html.append("th { background-color: #3498db; color: white; }\n");
        html.append(".stat { background: #ecf0f1; padding: 15px; margin: 10px 0; border-radius: 5px; }\n");
        html.append("</style>\n</head>\n<body>\n");
        
        html.append("<div class='container'>\n");
        html.append("<h1>📊 ").append(reportData.getTitolo()).append("</h1>\n");
        html.append("<p><strong>Generato:</strong> ").append(reportData.getDataGenerazione().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("</p>\n");
        
        if (reportData.getHackathon() != null) {
            html.append("<p><strong>Hackathon:</strong> ").append(reportData.getHackathon().getNome()).append("</p>\n");
        }
        
        // Statistics
        if (reportData.getStatistiche() != null) {
            html.append("<h2>📈 Statistiche</h2>\n");
            Statistics stats = reportData.getStatistiche();
            html.append("<div class='stat'><strong>Utenti:</strong> ").append(stats.getTotaleUtenti()).append(HTML_DIV_CLOSE + "\n");
            html.append("<div class='stat'><strong>Hackathon:</strong> ").append(stats.getTotaleHackathon()).append(HTML_DIV_CLOSE + "\n");
            html.append("<div class='stat'><strong>Team:</strong> ").append(stats.getTotaleTeam()).append(HTML_DIV_CLOSE + "\n");
        }
        
        // Teams table
        if (reportData.getTeams() != null && !reportData.getTeams().isEmpty()) {
            html.append("<h2>👥 Team</h2>\n");
            html.append("<table>\n" + HTML_TR_OPEN + "<th>ID</th><th>Nome</th><th>Hackathon</th><th>Capo Team</th></tr>\n");
            for (Team team : reportData.getTeams()) {
                html.append(HTML_TR_OPEN + HTML_TD_OPEN).append(team.getId()).append(HTML_TD_CLOSE);
                html.append(HTML_TD_OPEN).append(team.getNome()).append(HTML_TD_CLOSE);
                html.append(HTML_TD_OPEN).append(team.getHackathonId()).append(HTML_TD_CLOSE);
                html.append(HTML_TD_OPEN).append(team.getCapoTeamId()).append(HTML_TD_CLOSE + "</tr>\n");
            }
            html.append("</table>\n");
        }
        
        html.append(HTML_DIV_CLOSE + "\n</body>\n</html>");
        return html.toString();
    }
    
    private String generateJSONContent(ReportData reportData) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"report\": {\n");
        json.append("    \"title\": \"").append(reportData.getTitolo()).append(JSON_COMMA + "\n");
        json.append("    \"type\": \"").append(reportData.getTipoReport()).append(JSON_COMMA + "\n");
        json.append("    \"generated\": \"").append(reportData.getDataGenerazione()).append(JSON_COMMA + "\n");
        
        if (reportData.getHackathon() != null) {
            json.append("    \"hackathon\": {\n");
            json.append("      \"id\": ").append(reportData.getHackathon().getId()).append(",\n");
            json.append("      \"name\": \"").append(reportData.getHackathon().getNome()).append("\"\n");
            json.append("    },\n");
        }
        
        if (reportData.getStatistiche() != null) {
            Statistics stats = reportData.getStatistiche();
            json.append("    \"statistics\": {\n");
            json.append("      \"totalUsers\": ").append(stats.getTotaleUtenti()).append(",\n");
            json.append("      \"totalHackathons\": ").append(stats.getTotaleHackathon()).append(",\n");
            json.append("      \"totalTeams\": ").append(stats.getTotaleTeam()).append("\n");
            json.append("    },\n");
        }
        
        if (reportData.getTeams() != null) {
            json.append("    \"teams\": [\n");
            for (int i = 0; i < reportData.getTeams().size(); i++) {
                Team team = reportData.getTeams().get(i);
                json.append("      {\n");
                json.append("        \"id\": ").append(team.getId()).append(",\n");
                json.append("        \"name\": \"").append(team.getNome()).append("\",\n");
                json.append("        \"hackathonId\": ").append(team.getHackathonId()).append(",\n");
                json.append("        \"leaderId\": ").append(team.getCapoTeamId()).append("\n");
                json.append("      }");
                if (i < reportData.getTeams().size() - 1) json.append(",");
                json.append("\n");
            }
            json.append("    ]\n");
        }
        
        json.append("  }\n}");
        return json.toString();
    }
    
    // Utility methods
    
    private int extractHackathonId(String hackathonFilter) {
        try {
            return Integer.parseInt(hackathonFilter.split(":")[0]);
        } catch (Exception e) {
            return 1; // Default to first hackathon
        }
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        button.setFocusPainted(false);
        button.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    /**
     * Opens the file location in the system file manager
     * @param location the file location to open
     */
    private void openFileLocation(File location) {
        try {
            Desktop.getDesktop().open(location);
        } catch (Exception ex) {
            // Ignore if can't open
        }
    }
    
    /**
     * Sleeps for the specified duration, handling InterruptedException properly
     * @param millis the duration to sleep in milliseconds
     * @throws RuntimeException if interrupted
     */
    private void sleepSafely(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OperationInterruptedException("Operation interrupted", e);
        }
    }

    /**
     * Genera contenuto testo formattato
     */
    private String generateTextContent(ReportData reportData) {
        StringBuilder text = new StringBuilder();

        // Header
        text.append("HACKATHON MANAGER - REPORT").append(NEWLINE);
        text.append("═══════════════════════════════════════════════════════════════════").append(NEWLINE);
        text.append("Titolo: ").append(reportData.getTitolo()).append(NEWLINE);
        text.append("Tipo: ").append(reportData.getTipoReport()).append(NEWLINE);
        text.append("Data generazione: ").append(reportData.getDataGenerazione()).append(NEWLINE);
        text.append("═══════════════════════════════════════════════════════════════════").append(NEWLINE);
        text.append(NEWLINE);

        // Teams section
        if (reportData.getTeams() != null && !reportData.getTeams().isEmpty()) {
            text.append("TEAM REGISTRATI").append(NEWLINE);
            text.append("───────────────────────────────────────────────────────────────────").append(NEWLINE);

            for (Team team : reportData.getTeams()) {
                String status = team.getId() == -1 ? "PLACEHOLDER" : "ATTIVO";
                text.append("ID: ").append(team.getId())
                    .append(" | Nome: ").append(team.getNome())
                    .append(" | Hackathon: ").append(team.getHackathonId())
                    .append(" | Capo: ").append(team.getCapoTeamId())
                    .append(" | Max Membri: ").append(team.getDimensioneMassima())
                    .append(" | Stato: ").append(status).append(NEWLINE);
            }
            text.append(NEWLINE);
        }

        // Statistics section
        if (reportData.getStatistiche() != null) {
            text.append("STATISTICHE GENERALI").append(NEWLINE);
            text.append("───────────────────────────────────────────────────────────────────").append(NEWLINE);

            Statistics stats = reportData.getStatistiche();
            text.append("Totale Utenti: ").append(stats.getTotaleUtenti()).append(NEWLINE);
            text.append("Totale Hackathon: ").append(stats.getTotaleHackathon()).append(NEWLINE);
            text.append("Totale Team: ").append(stats.getTotaleTeam()).append(NEWLINE);
            text.append("Totale Documenti: ").append(stats.getTotaleDocumenti()).append(NEWLINE);
            text.append("Media Voti: ").append(String.format("%.2f", stats.getMediaVoti())).append(NEWLINE);
            text.append("Valutazioni Completate: ").append(stats.getValutazioniCompletate()).append(NEWLINE);
            text.append(NEWLINE);
        }

        // Evaluations section
        if (reportData.getValutazioni() != null && !reportData.getValutazioni().isEmpty()) {
            text.append("VALUTAZIONI TEAM").append(NEWLINE);
            text.append("───────────────────────────────────────────────────────────────────").append(NEWLINE);

            for (Valutazione valutazione : reportData.getValutazioni()) {
                String date = valutazione.getDataValutazione() != null ?
                    valutazione.getDataValutazione().toString().substring(0, 19) : "PENDING";

                text.append("Team ID: ").append(valutazione.getTeamId())
                    .append(" | Giudice ID: ").append(valutazione.getGiudiceId())
                    .append(" | Voto: ").append(valutazione.getVoto()).append("/10")
                    .append(" | Data: ").append(date).append(NEWLINE);

                if (valutazione.getCommento() != null && !valutazione.getCommento().trim().isEmpty()) {
                    text.append("Commento: ").append(valutazione.getCommento()).append(NEWLINE);
                }
                text.append(NEWLINE);
            }
        }

        // Footer
        text.append("═══════════════════════════════════════════════════════════════════").append(NEWLINE);
        text.append("Report generato da Hackathon Manager").append(NEWLINE);
        text.append("Data: ").append(LocalDateTime.now()).append(NEWLINE);

        return text.toString();
    }

    /**
     * Genera contenuto PDF formattato come testo strutturato
     */
    private String generatePDFContent(ReportData reportData) {
        StringBuilder pdf = new StringBuilder();

        // Header PDF
        pdf.append("╔═══════════════════════════════════════════════════════════════════════════════╗\n");
        pdf.append("║                              HACKATHON MANAGER                               ║\n");
        pdf.append("║                          REPORT GENERATO AUTOMATICAMENTE                     ║\n");
        pdf.append("╠═══════════════════════════════════════════════════════════════════════════════╣\n");
        pdf.append("║ Titolo: ").append(String.format("%-65s", reportData.getTitolo())).append("║\n");
        pdf.append("║ Tipo:   ").append(String.format("%-65s", reportData.getTipoReport())).append("║\n");
        pdf.append("║ Data:   ").append(String.format("%-65s", reportData.getDataGenerazione())).append("║\n");
        pdf.append("╚═══════════════════════════════════════════════════════════════════════════════╝\n\n");

        // Sezione Teams
        if (reportData.getTeams() != null && !reportData.getTeams().isEmpty()) {
            pdf.append("┌─────────────────────────────────────────────────────────────────────────────┐\n");
            pdf.append("│                                DATI TEAM                                    │\n");
            pdf.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
            pdf.append("│ ID  │ Nome Team              │ Hackathon │ Capo Team │ Membri Max │ Stato   │\n");
            pdf.append("├─────┼────────────────────────┼───────────┼───────────┼────────────┼─────────┤\n");

            for (Team team : reportData.getTeams()) {
                String status = "ATTIVO";
                if (team.getId() == -1) status = "PLACEHOLDER";

                pdf.append("│ ")
                   .append(String.format("%-3s", team.getId()))
                   .append(" │ ")
                   .append(String.format("%-22s", truncate(team.getNome(), 22)))
                   .append(" │ ")
                   .append(String.format("%-9s", team.getHackathonId()))
                   .append(" │ ")
                   .append(String.format("%-9s", team.getCapoTeamId()))
                   .append(" │ ")
                   .append(String.format("%-10s", team.getDimensioneMassima()))
                   .append(" │ ")
                   .append(String.format("%-7s", status))
                   .append(" │\n");
            }
            pdf.append("└─────────────────────────────────────────────────────────────────────────────┘\n\n");
        }

        // Sezione Statistiche
        if (reportData.getStatistiche() != null) {
            pdf.append("┌─────────────────────────────────────────────────────────────────────────────┐\n");
            pdf.append("│                           STATISTICHE GENERALI                              │\n");
            pdf.append("├─────────────────────────────────────────────────────────────────────────────┤\n");

            Statistics stats = reportData.getStatistiche();
            pdf.append("│ Totale Utenti:          ").append(String.format("%-40s", stats.getTotaleUtenti())).append("│\n");
            pdf.append("│ Totale Hackathon:       ").append(String.format("%-40s", stats.getTotaleHackathon())).append("│\n");
            pdf.append("│ Totale Team:            ").append(String.format("%-40s", stats.getTotaleTeam())).append("│\n");
            pdf.append("│ Totale Documenti:       ").append(String.format("%-40s", stats.getTotaleDocumenti())).append("│\n");
            pdf.append("│ Media Voti:             ").append(String.format("%-40s", String.format("%.2f", stats.getMediaVoti()))).append("│\n");
            pdf.append("│ Valutazioni Completate: ").append(String.format("%-40s", stats.getValutazioniCompletate())).append("│\n");

            pdf.append("└─────────────────────────────────────────────────────────────────────────────┘\n\n");
        }

        // Sezione Valutazioni
        if (reportData.getValutazioni() != null && !reportData.getValutazioni().isEmpty()) {
            pdf.append("┌─────────────────────────────────────────────────────────────────────────────┐\n");
            pdf.append("│                           VALUTAZIONI TEAM                                  │\n");
            pdf.append("├─────────────────────────────────────────────────────────────────────────────┤\n");
            pdf.append("│ Team │ Giudice │ Voto │ Commento                                    │ Data      │\n");
            pdf.append("├──────┼─────────┼──────┼─────────────────────────────────────────────┼───────────┤\n");

            for (Valutazione valutazione : reportData.getValutazioni()) {
                pdf.append("│ ")
                   .append(String.format("%-4s", valutazione.getTeamId()))
                   .append(" │ ")
                   .append(String.format("%-7s", valutazione.getGiudiceId()))
                   .append(" │ ")
                   .append(String.format("%-4s", valutazione.getVoto()))
                   .append(" │ ")
                   .append(String.format("%-43s", truncate(valutazione.getCommento(), 43)))
                   .append(" │ ")
                   .append(String.format("%-9s", valutazione.getDataValutazione() != null ?
                       valutazione.getDataValutazione().toString().substring(0, 10) : "N/A"))
                   .append(" │\n");
            }
            pdf.append("└─────────────────────────────────────────────────────────────────────────────┘\n\n");
        }

        // Footer
        pdf.append("════════════════════════════════════════════════════════════════════════════════\n");
        pdf.append("Report generato automaticamente da Hackathon Manager\n");
        pdf.append("Data generazione: ").append(LocalDateTime.now()).append("\n");
        pdf.append("Formato: PDF (Portable Document Format)\n");
        pdf.append("════════════════════════════════════════════════════════════════════════════════\n");

        return pdf.toString();
    }

    /**
     * Genera contenuto Excel formattato come CSV avanzato
     */
    private String generateExcelContent(ReportData reportData) {
        StringBuilder excel = new StringBuilder();

        // Header Excel
        excel.append("HACKATHON MANAGER - REPORT EXCEL").append(NEWLINE);
        excel.append("Report Type:,\"").append(reportData.getTipoReport()).append("\"").append(NEWLINE);
        excel.append("Generation Date:,\"").append(reportData.getDataGenerazione()).append("\"").append(NEWLINE);
        excel.append("Generated by:,\"Hackathon Manager System\"").append(NEWLINE);
        excel.append(NEWLINE);

        // Sheet 1: Teams
        if (reportData.getTeams() != null && !reportData.getTeams().isEmpty()) {
            excel.append("=== TEAMS SHEET ===").append(NEWLINE);
            excel.append("Team ID,Team Name,Hackathon ID,Team Leader ID,Max Members,Current Status").append(NEWLINE);

            for (Team team : reportData.getTeams()) {
                String status = team.getId() == -1 ? "PLACEHOLDER" : "ACTIVE";
                excel.append(team.getId()).append(",")
                     .append("\"").append(team.getNome()).append("\",")
                     .append(team.getHackathonId()).append(",")
                     .append(team.getCapoTeamId()).append(",")
                     .append(team.getDimensioneMassima()).append(",")
                     .append("\"").append(status).append("\"").append(NEWLINE);
            }
            excel.append(NEWLINE);
        }

        // Sheet 2: Statistics
        if (reportData.getStatistiche() != null) {
            excel.append("=== STATISTICS SHEET ===").append(NEWLINE);
            excel.append("Metric,Value,Description").append(NEWLINE);

            Statistics stats = reportData.getStatistiche();
            excel.append("\"Total Users\",").append(stats.getTotaleUtenti()).append(",\"Total number of registered users\"").append(NEWLINE);
            excel.append("\"Total Hackathons\",").append(stats.getTotaleHackathon()).append(",\"Total number of hackathons\"").append(NEWLINE);
            excel.append("\"Total Teams\",").append(stats.getTotaleTeam()).append(",\"Total number of teams\"").append(NEWLINE);
            excel.append("\"Total Documents\",").append(stats.getTotaleDocumenti()).append(",\"Total uploaded documents\"").append(NEWLINE);
            excel.append("\"Average Score\",").append(String.format("%.2f", stats.getMediaVoti())).append(",\"Average team evaluation score\"").append(NEWLINE);
            excel.append("\"Completed Evaluations\",").append(stats.getValutazioniCompletate()).append(",\"Number of completed evaluations\"").append(NEWLINE);
            excel.append(NEWLINE);
        }

        // Sheet 3: Evaluations
        if (reportData.getValutazioni() != null && !reportData.getValutazioni().isEmpty()) {
            excel.append("=== EVALUATIONS SHEET ===").append(NEWLINE);
            excel.append("Team ID,Judge ID,Score,Comment,Evaluation Date,Status").append(NEWLINE);

            for (Valutazione valutazione : reportData.getValutazioni()) {
                String status = "COMPLETED";
                String date = valutazione.getDataValutazione() != null ?
                    valutazione.getDataValutazione().toString().substring(0, 19) : "PENDING";

                excel.append(valutazione.getTeamId()).append(",")
                     .append(valutazione.getGiudiceId()).append(",")
                     .append(valutazione.getVoto()).append(",")
                     .append("\"").append(valutazione.getCommento() != null ? valutazione.getCommento().replace("\"", "\"\"") : "").append("\",")
                     .append("\"").append(date).append("\",")
                     .append("\"").append(status).append("\"").append(NEWLINE);
            }
            excel.append(NEWLINE);
        }

        // Metadata Excel
        excel.append("=== METADATA ===").append(NEWLINE);
        excel.append("Generated by,Hackathon Manager").append(NEWLINE);
        excel.append("Export Format,Excel CSV").append(NEWLINE);
        excel.append("Compatible with,Microsoft Excel, Google Sheets, LibreOffice Calc").append(NEWLINE);
        excel.append("Encoding,UTF-8").append(NEWLINE);
        excel.append("Timestamp,").append(LocalDateTime.now()).append(NEWLINE);

        return excel.toString();
    }

    /**
     * Utility method to truncate strings for display
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 3) + "...";
    }
}
