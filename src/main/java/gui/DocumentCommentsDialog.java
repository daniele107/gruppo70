package gui;

import controller.Controller;
import model.Progress;
import model.ProgressComment;
import model.Team;
import model.Utente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialog per visualizzare e gestire i commenti su un documento di progresso
 */
public class DocumentCommentsDialog extends JDialog {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String DIALOG_TITLE = "Gestione Commenti Documento";
    private static final String ADD_COMMENT_LABEL = "Aggiungi Commento";
    private static final String UPDATE_COMMENT_LABEL = "Aggiorna Commento";
    private static final String REMOVE_COMMENT_LABEL = "Rimuovi Commento";
    private static final String REFRESH_LABEL = "🔄 Aggiorna";
    private static final String CLOSE_LABEL = "Chiudi";
    
    // Costanti per messaggi di stato
    private static final String SUCCESS_TITLE = "Successo";
    private static final String ERROR_TITLE = "Errore";
    private static final String SYSTEM_ERROR_TITLE = "Errore Sistema";
    private static final String ERROR_PREFIX = "Errore: ";
    private static final String SUCCESS_TOAST_TYPE = "success";
    
    private final transient Controller controller;
    private final transient Progress progress;
    private final transient Utente currentUser;
    private final MainFrame parentFrame;
    
    // GUI Components
    private JTable commentsTable;
    private DefaultTableModel tableModel;
    private JTextArea commentTextArea;
    private JButton addButton;
    private JButton updateButton;
    private JButton removeButton;
    private JLabel statusLabel;
    
    private transient List<ProgressComment> allComments;
    private transient ProgressComment selectedComment;
    
    public DocumentCommentsDialog(MainFrame parent, Controller controller, Progress progress) {
        super(parent, DIALOG_TITLE, true);
        this.parentFrame = parent;
        this.controller = controller;
        this.progress = progress;
        this.currentUser = controller.getCurrentUser();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadComments();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        // Tabella commenti
        String[] columnNames = {"Giudice", "Commento", "Creato", "Aggiornato"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        commentsTable = new JTable(tableModel);
        commentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        commentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleCommentSelection();
            }
        });
        
        // Area testo commento
        commentTextArea = new JTextArea(5, 40);
        commentTextArea.setLineWrap(true);
        commentTextArea.setWrapStyleWord(true);
        commentTextArea.setBorder(BorderFactory.createTitledBorder("Nuovo Commento"));
        
        // Pulsanti
        addButton = new JButton(ADD_COMMENT_LABEL);
        updateButton = new JButton(UPDATE_COMMENT_LABEL);
        removeButton = new JButton(REMOVE_COMMENT_LABEL);
        // I pulsanti Refresh/Close sono creati in setupLayout e cablati in setupEventHandlers
        
        // Inizialmente disabilita i pulsanti di modifica
        updateButton.setEnabled(false);
        removeButton.setEnabled(false);
        
        // Status label
        statusLabel = new JLabel("Caricamento commenti...");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel superiore con info documento
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Documento: " + progress.getTitolo()));
        infoPanel.add(new JLabel(" | Team: " + getTeamName(progress.getTeamId())));
        add(infoPanel, BorderLayout.NORTH);
        
        // Panel centrale con tabella
        JScrollPane tableScrollPane = new JScrollPane(commentsTable);
        tableScrollPane.setPreferredSize(new Dimension(750, 300));
        
        // Panel per area testo
        JScrollPane textScrollPane = new JScrollPane(commentTextArea);
        textScrollPane.setPreferredSize(new Dimension(750, 120));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        centerPanel.add(textScrollPane, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        
        // Panel pulsanti
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPanel.add(new JButton(REFRESH_LABEL));
        buttonPanel.add(new JButton(CLOSE_LABEL));
        
        // Panel inferiore con status e pulsanti
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        addButton.addActionListener(this::handleAddComment);
        updateButton.addActionListener(this::handleUpdateComment);
        removeButton.addActionListener(this::handleRemoveComment);
        
        // Refresh button
        getRootPane().getComponent(2); // Get bottom panel
        JPanel bottomPanel = (JPanel) getRootPane().getComponent(2);
        JPanel buttonPanel = (JPanel) bottomPanel.getComponent(1);
        JButton refreshButton = (JButton) buttonPanel.getComponent(5);
        JButton closeButton = (JButton) buttonPanel.getComponent(6);
        
        refreshButton.addActionListener(e -> loadComments());
        closeButton.addActionListener(e -> dispose());
    }
    
    private void handleCommentSelection() {
        int selectedRow = commentsTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < allComments.size()) {
            selectedComment = allComments.get(selectedRow);
            
            // Mostra il commento nell'area testo
            commentTextArea.setText(selectedComment.getText());
            
            // Abilita i pulsanti solo se è il commento dell'utente corrente
            boolean isOwnComment = selectedComment.getJudgeId() == currentUser.getId();
            updateButton.setEnabled(isOwnComment);
            removeButton.setEnabled(isOwnComment);
            
            commentTextArea.setBorder(BorderFactory.createTitledBorder(
                isOwnComment ? "Modifica Commento" : "Commento Selezionato (Solo Lettura)"));
            commentTextArea.setEditable(isOwnComment);
        } else {
            selectedComment = null;
            commentTextArea.setText("");
            commentTextArea.setBorder(BorderFactory.createTitledBorder("Nuovo Commento"));
            commentTextArea.setEditable(true);
            updateButton.setEnabled(false);
            removeButton.setEnabled(false);
        }
    }
    
    private void handleAddComment(ActionEvent e) {
        String commentText = commentTextArea.getText().trim();
        if (commentText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Inserisci un commento prima di salvare.",
                "Commento Richiesto",
                JOptionPane.WARNING_MESSAGE);
            commentTextArea.requestFocus();
            return;
        }
        
        if (commentText.length() < 5 || commentText.length() > 2000) {
            JOptionPane.showMessageDialog(this,
                "Il commento deve essere tra 5 e 2000 caratteri.",
                "Lunghezza Non Valida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            boolean success = controller.aggiungiCommentoGiudice(
                progress.getId(),
                commentText
            );
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Commento aggiunto con successo!",
                    SUCCESS_TITLE,
                    JOptionPane.INFORMATION_MESSAGE);
                commentTextArea.setText("");
                loadComments();
                parentFrame.showToast("Commento aggiunto con successo!", SUCCESS_TOAST_TYPE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Errore nell'aggiunta del commento. Verifica i permessi o il rate limit.",
                    ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                ERROR_PREFIX + ex.getMessage(),
                SYSTEM_ERROR_TITLE,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleUpdateComment(ActionEvent e) {
        if (selectedComment == null) return;
        
        String commentText = commentTextArea.getText().trim();
        if (commentText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Inserisci un commento prima di salvare.",
                "Commento Richiesto",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (commentText.length() < 5 || commentText.length() > 2000) {
            JOptionPane.showMessageDialog(this,
                "Il commento deve essere tra 5 e 2000 caratteri.",
                "Lunghezza Non Valida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Sei sicuro di voler modificare il commento?",
            "Conferma Modifica",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = controller.aggiornaCommentoGiudice(
                    progress.getId(),
                    currentUser.getId(),
                    commentText
                );
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Commento modificato con successo!",
                        SUCCESS_TITLE,
                        JOptionPane.INFORMATION_MESSAGE);
                    loadComments();
                    parentFrame.showToast("Commento modificato con successo!", SUCCESS_TOAST_TYPE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Errore nella modifica del commento.",
                        ERROR_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    ERROR_PREFIX + ex.getMessage(),
                    SYSTEM_ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleRemoveComment(ActionEvent e) {
        if (selectedComment == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Sei sicuro di voler rimuovere il commento?\n" +
            "Questa azione non può essere annullata.",
            "Conferma Rimozione",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = controller.rimuoviCommentoGiudice(
                    progress.getId(),
                    currentUser.getId()
                );
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Commento rimosso con successo!",
                        SUCCESS_TITLE,
                        JOptionPane.INFORMATION_MESSAGE);
                    commentTextArea.setText("");
                    selectedComment = null;
                    loadComments();
                    parentFrame.showToast("Commento rimosso con successo!", SUCCESS_TOAST_TYPE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Errore nella rimozione del commento.",
                        ERROR_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    ERROR_PREFIX + ex.getMessage(),
                    SYSTEM_ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadComments() {
        statusLabel.setText("🔄 Caricamento commenti...");
        
        SwingWorker<List<ProgressComment>, Void> worker = new SwingWorker<List<ProgressComment>, Void>() {
            @Override
            protected List<ProgressComment> doInBackground() throws Exception {
                return controller.getCommentiDocumento(progress.getId());
            }
            
            @Override
            protected void done() {
                try {
                    allComments = get();
                    updateCommentsTable();
                    statusLabel.setText("✅ Caricati " + allComments.size() + " commenti");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Re-interrupt the thread
                    statusLabel.setText("❌ Operazione interrotta");
                    JOptionPane.showMessageDialog(DocumentCommentsDialog.this,
                        "Operazione interrotta: " + e.getMessage(),
                        ERROR_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    statusLabel.setText("❌ Errore nel caricamento: " + e.getMessage());
                    JOptionPane.showMessageDialog(DocumentCommentsDialog.this,
                        "Errore nel caricamento dei commenti:\n" + e.getMessage(),
                        ERROR_TITLE,
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void updateCommentsTable() {
        tableModel.setRowCount(0);
        
        for (ProgressComment comment : allComments) {
            String judgeName = getJudgeName(comment.getJudgeId());
            String createdDate = comment.getCreatedAt().format(DATE_FORMAT);
            String updatedDate = comment.getUpdatedAt() != null ? 
                comment.getUpdatedAt().format(DATE_FORMAT) : "-";
            
            Object[] row = {judgeName, comment.getText(), createdDate, updatedDate};
            tableModel.addRow(row);
        }
        
        if (allComments.isEmpty()) {
            Object[] emptyRow = {"Nessun commento trovato", "", "", ""};
            tableModel.addRow(emptyRow);
        }
    }
    
    private String getTeamName(int teamId) {
        try {
            return controller.getTuttiTeam().stream()
                .filter(t -> t.getId() == teamId)
                .findFirst()
                .map(Team::getNome)
                .orElse("Team " + teamId);
        } catch (Exception e) {
            return "Team " + teamId;
        }
    }
    
    private String getJudgeName(int judgeId) {
        try {
            Utente u = controller.getUtenteById(judgeId);
            if (u != null) {
                return u.getNome() + " " + u.getCognome();
            }
            return "Giudice " + judgeId;
        } catch (Exception e) {
            return "Giudice " + judgeId;
        }
    }
}
