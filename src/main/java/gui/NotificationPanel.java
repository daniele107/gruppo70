package gui;
import controller.Controller;
import model.Notification;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import static javax.swing.SwingConstants.*;
/**
 * Pannello per la gestione delle notifiche avanzate.
 * Permette di visualizzare, gestire e filtrare le notifiche dell'utente.
 */
public class NotificationPanel extends JPanel {
    private final transient Controller controller;
    private final transient JFrame mainFrame;
    // Components
    private DefaultListModel<Notification> notificationListModel;
    private JList<Notification> notificationList;
    private JButton refreshButton;
    private JButton markAsReadButton;
    private JButton markAllAsReadButton;
    private JButton deleteButton;
    private JButton filterButton;
    private JButton debugButton;
    // Filter components
    private JComboBox<String> typeFilterComboBox;
    private JComboBox<String> statusFilterComboBox;
    // Selected items
    private transient Notification selectedNotification;
    // Notification badge (for unread count)
    private int unreadCount = 0;
    // Constants for messages
    private static final String MSG_SUCCESSO = "Successo";
    private static final String MSG_ERRORE = "Errore";
    private static final String MSG_ERRORE_PREFIX = "Errore: ";
    private static final String MSG_ERRORE_SISTEMA = "Errore Sistema";
    // Font constants
    private static final String FONT_SEGOE_UI = "Segoe UI";
    /**
     * Costruttore che inizializza il pannello notifiche
     *
     * @param controller il controller dell'applicazione
     * @param mainFrame  il frame principale
     */
    public NotificationPanel(Controller controller, JFrame mainFrame) {
        this.controller = controller;
        this.mainFrame = mainFrame;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        // Carica dati iniziali
        refreshData();
    }
    /**
     * Inizializza i componenti del pannello
     */
    private void initializeComponents() {
        // List model for notifications
        notificationListModel = new DefaultListModel<>();
        notificationList = new JList<>(notificationListModel);
        notificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notificationList.setCellRenderer(new NotificationListCellRenderer());
        // Buttons
        refreshButton = createStyledButtonWithTextColor("\uD83D\uDD04 Aggiorna", new Color(46, 204, 113), Color.BLACK);
        markAsReadButton = createStyledButtonWithTextColor("📖 Segna come letta", new Color(52, 152, 219), Color.BLACK);
        markAllAsReadButton = createStyledButtonWithTextColor("📚 Segna tutte come lette", new Color(155, 89, 182), Color.BLACK);
        deleteButton = createStyledButtonWithTextColor("\uD83D\uDDD1\uFE0F Elimina", new Color(231, 76, 60), Color.BLACK);
        filterButton = createStyledButtonWithTextColor("\uD83D\uDD0D Filtra", new Color(241, 196, 15), Color.BLACK);
        debugButton = createStyledButtonWithTextColor("\uD83D\uDD0D Debug", new Color(149, 165, 166), Color.BLACK);
        // Initially disable buttons that require selection
        markAsReadButton.setEnabled(false);
        deleteButton.setEnabled(false);
        // Filter components
        typeFilterComboBox = new JComboBox<>(new String[]{
            "Tutti i tipi", "Informazione", MSG_SUCCESSO, "Avviso", MSG_ERRORE,
            "Richiesta Team", "Nuovo Commento", "Aggiornamento Evento", "Sistema"
        });
        statusFilterComboBox = new JComboBox<>(new String[]{
            "Tutti gli stati", "Non lette", "Lette"
        });
        // Add notification selection listener
        notificationList.addListSelectionListener(e -> {
            selectedNotification = notificationList.getSelectedValue();
            markAsReadButton.setEnabled(selectedNotification != null && !selectedNotification.isRead());
            deleteButton.setEnabled(selectedNotification != null);
        });
    }
    /**
     * Configura il layout del pannello
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(MainFrame.getBackgroundColor());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("🔔 Centro Notifiche", LEFT);
        titleLabel.setFont(new Font(FONT_SEGOE_UI, Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 73, 94));
        JLabel subtitleLabel = new JLabel("Gestisci tutte le tue notifiche", LEFT);
        subtitleLabel.setFont(new Font(FONT_SEGOE_UI, Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(149, 165, 166));
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        // Buttons panel in header
        JPanel headerButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerButtonsPanel.setOpaque(false);
        headerButtonsPanel.add(refreshButton);
        headerButtonsPanel.add(debugButton);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(headerButtonsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "Filtri"
        ));
        JPanel filterControls = new JPanel(new GridLayout(1, 4, 10, 0));
        filterControls.setOpaque(false);
        filterControls.add(new JLabel("Tipo:"));
        filterControls.add(typeFilterComboBox);
        filterControls.add(new JLabel("Stato:"));
        filterControls.add(statusFilterComboBox);
        filterPanel.add(filterControls);
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "Azioni"
        ));
        actionPanel.add(markAsReadButton);
        actionPanel.add(markAllAsReadButton);
        actionPanel.add(deleteButton);
        actionPanel.add(filterButton);
        // Notifications list
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setOpaque(false);
        listPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "Notifiche",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font(FONT_SEGOE_UI, Font.BOLD, 14),
            new Color(52, 73, 94)
        ));
        JScrollPane scrollPane = new JScrollPane(notificationList);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        listPanel.add(scrollPane, BorderLayout.CENTER);
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            "Informazioni"
        ));
        JLabel infoLabel = new JLabel("💡 Seleziona una notifica per visualizzare i dettagli e le azioni disponibili");
        infoLabel.setFont(new Font(FONT_SEGOE_UI, Font.ITALIC, 12));
        infoLabel.setForeground(new Color(149, 165, 166));
        infoPanel.add(infoLabel);
        contentPanel.add(filterPanel, BorderLayout.NORTH);
        contentPanel.add(listPanel, BorderLayout.CENTER);
        contentPanel.add(actionPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }
    /**
     * Configura gli event handler
     */
    private void setupEventHandlers() {
        refreshButton.addActionListener(e -> {
            refreshData();
            JOptionPane.showMessageDialog(mainFrame, "\u2705 Notifiche aggiornate!", "Aggiornamento", JOptionPane.INFORMATION_MESSAGE);
        });
        markAsReadButton.addActionListener(e -> markAsRead());
        markAllAsReadButton.addActionListener(e -> markAllAsRead());
        deleteButton.addActionListener(e -> deleteNotification());
        filterButton.addActionListener(e -> applyFilters());
        debugButton.addActionListener(e -> showDebugInfo());
        // Add filter listeners
        typeFilterComboBox.addActionListener(e -> applyFilters());
        statusFilterComboBox.addActionListener(e -> applyFilters());
    }
    /**
     * Aggiorna i dati del pannello
     */
    public void refreshData() {
        try {
            // Carica tutte le notifiche dell'utente corrente
            List<Notification> notifications = controller.getNotificheUtente();
            notificationListModel.clear();
            if (notifications.isEmpty()) {
                notificationListModel.addElement(createEmptyNotification());
            } else {
                for (Notification notification : notifications) {
                    notificationListModel.addElement(notification);
                }
            }
            // Calcola numero notifiche non lette
            unreadCount = (int) notifications.stream()
                .filter(n -> !n.isRead())
                .count();
        } catch (Exception e) {
            notificationListModel.clear();
            notificationListModel.addElement(createErrorNotification(e.getMessage()));
        }
    }
    /**
     * Applica i filtri alle notifiche
     */
    private void applyFilters() {
        String typeFilter = (String) typeFilterComboBox.getSelectedItem();
        String statusFilter = (String) statusFilterComboBox.getSelectedItem();
        try {
            List<Notification> allNotifications = controller.getNotificheUtente();
            List<Notification> filteredNotifications = allNotifications.stream()
                .filter(notification -> {
                    // Filter by type
                    if (!"Tutti i tipi".equals(typeFilter)) {
                        if (!notification.getType().getDescription().equals(typeFilter)) {
                            return false;
                        }
                    }
                    // Filter by status
                    if ("Non lette".equals(statusFilter)) {
                        return !notification.isRead();
                    } else if ("Lette".equals(statusFilter)) {
                        return notification.isRead();
                    }
                    return true;
                })
                .toList();
            // Update list
            notificationListModel.clear();
            if (filteredNotifications.isEmpty()) {
                notificationListModel.addElement(createEmptyNotification());
            } else {
                for (Notification notification : filteredNotifications) {
                    notificationListModel.addElement(notification);
                }
            }
            JOptionPane.showMessageDialog(mainFrame, "\uD83D\uDD0D Filtri applicati: " + filteredNotifications.size() + " notifiche", "Filtri", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Errore nell'applicazione dei filtri:\n" + e.getMessage(),
                MSG_ERRORE,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Segna la notifica selezionata come letta
     */
    private void markAsRead() {
        if (selectedNotification == null) {
            JOptionPane.showMessageDialog(this, "Seleziona una notifica", MSG_ERRORE, JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            boolean success = controller.segnaNotificaComeLetta(selectedNotification.getId());
            if (success) {
                selectedNotification.markAsRead();
                notificationList.repaint();
                markAsReadButton.setEnabled(false);
                unreadCount = Math.max(0, unreadCount - 1);
                JOptionPane.showMessageDialog(mainFrame, "\u2705 Notifica segnata come letta!", MSG_SUCCESSO, JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Errore nel segnare la notifica come letta",
                    MSG_ERRORE,
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                MSG_ERRORE_PREFIX + e.getMessage(),
                MSG_ERRORE_SISTEMA,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Segna tutte le notifiche come lette
     */
    private void markAllAsRead() {
        try {
            boolean success = controller.segnaTutteNotificheComeLette();
            if (success) {
                // Aggiorna tutte le notifiche nella lista
                for (int i = 0; i < notificationListModel.size(); i++) {
                    Notification notification = notificationListModel.get(i);
                    if (notification != null && !notification.isRead()) {
                        notification.markAsRead();
                    }
                }
                notificationList.repaint();
                markAsReadButton.setEnabled(false);
                unreadCount = 0;
                JOptionPane.showMessageDialog(mainFrame, "\u2705 Tutte le notifiche segnate come lette!", MSG_SUCCESSO, JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Errore nel segnare tutte le notifiche come lette",
                    MSG_ERRORE,
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                MSG_ERRORE_PREFIX + e.getMessage(),
                MSG_ERRORE_SISTEMA,
                JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Elimina la notifica selezionata
     */
    private void deleteNotification() {
        if (selectedNotification == null) {
            JOptionPane.showMessageDialog(this, "Seleziona una notifica", MSG_ERRORE, JOptionPane.ERROR_MESSAGE);
            return;
        }
        int result = JOptionPane.showConfirmDialog(this,
            "Sei sicuro di voler eliminare questa notifica?\n\n" + selectedNotification.getTitle(),
            "Conferma Eliminazione",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            try {
                boolean success = controller.eliminaNotifica(selectedNotification.getId());
                if (success) {
                    if (!selectedNotification.isRead()) {
                        unreadCount = Math.max(0, unreadCount - 1);
                    }
                    notificationListModel.removeElement(selectedNotification);
                    selectedNotification = null;
                    markAsReadButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    if (notificationListModel.isEmpty()) {
                        notificationListModel.addElement(createEmptyNotification());
                    }
                    JOptionPane.showMessageDialog(mainFrame, "\uD83D\uDDD1\uFE0F Notifica eliminata!", MSG_SUCCESSO, JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Errore nell'eliminazione della notifica",
                        MSG_ERRORE,
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    MSG_ERRORE_PREFIX + e.getMessage(),
                    MSG_ERRORE_SISTEMA,
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    /**
     * Mostra informazioni di debug
     */
    private void showDebugInfo() {
        StringBuilder debugInfo = new StringBuilder();
        debugInfo.append("\uD83D\uDD0D DEBUG NOTIFICATION PANEL\n\n");
        try {
            debugInfo.append("👤 UTENTE CORRENTE: ").append(controller.getCurrentUser().getLogin()).append("\n");
            debugInfo.append("🔔 NOTIFICHE TOTALI: ").append(notificationListModel.size()).append("\n");
            debugInfo.append("📧 NON LETTE: ").append(unreadCount).append("\n");
            debugInfo.append("🎯 SELEZIONATA: ").append(selectedNotification != null ? selectedNotification.getTitle() : "Nessuna").append("\n");
            debugInfo.append("🔽 FILTRO TIPO: ").append(typeFilterComboBox.getSelectedItem()).append("\n");
            debugInfo.append("🔽 FILTRO STATO: ").append(statusFilterComboBox.getSelectedItem()).append("\n");
        } catch (Exception e) {
            debugInfo.append("\u274C ERRORE: ").append(e.getMessage()).append("\n");
        }
        JTextArea textArea = new JTextArea(debugInfo.toString());
        textArea.setRows(15);
        textArea.setColumns(50);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "\uD83D\uDD0D Debug Notification Panel", JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * Restituisce il numero di notifiche non lette
     */
    public int getUnreadCount() {
        return unreadCount;
    }
    /**
     * Crea una notifica placeholder per lista vuota
     */
    private Notification createEmptyNotification() {
        Notification empty = new Notification(-1, "Nessuna notifica", "Non ci sono notifiche da visualizzare", Notification.NotificationType.INFO);
        empty.setRead(true);
        return empty;
    }
    /**
     * Crea una notifica di errore
     */
    private Notification createErrorNotification(String errorMessage) {
        Notification error = new Notification(-1, "Errore caricamento", MSG_ERRORE_PREFIX + errorMessage, Notification.NotificationType.ERROR);
        error.setRead(true);
        return error;
    }
    /**
     * Crea un pulsante stilizzato
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font(FONT_SEGOE_UI, Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
     * Crea un pulsante stilizzato con colore testo personalizzato
     */
    private JButton createStyledButtonWithTextColor(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font(FONT_SEGOE_UI, Font.BOLD, 14)); // Font più grande
        button.setForeground(textColor); // Testo personalizzato
        button.setBackground(backgroundColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
     * Renderer personalizzato per la lista delle notifiche
     */
    private class NotificationListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Notification) {
                Notification notification = (Notification) value;
                StringBuilder text = new StringBuilder();
                // Icona e stato
                text.append(notification.getType().getIcon());
                if (!notification.isRead()) {
                    text.append(" 🔵"); // Punto blu per notifiche non lette
                } else {
                    text.append(" ⚪"); // Punto bianco per notifiche lette
                }
                text.append(" ");
                // Titolo
                text.append(notification.getTitle());
                // Timestamp relativo
                if (notification.getCreatedAt() != null) {
                    long hoursAgo = ChronoUnit.HOURS.between(notification.getCreatedAt(), LocalDateTime.now());
                    if (hoursAgo < 1) {
                        text.append(" (ora)");
                    } else if (hoursAgo < 24) {
                        text.append(" (").append(hoursAgo).append("h fa)");
                    } else {
                        long daysAgo = ChronoUnit.DAYS.between(notification.getCreatedAt(), LocalDateTime.now());
                        text.append(" (").append(daysAgo).append("g fa)");
                    }
                }
                setText(text.toString());
                setFont(new Font(FONT_SEGOE_UI, Font.PLAIN, 12));
                // Colori diversi per notifiche lette/non lette
                if (!notification.isRead()) {
                    setForeground(new Color(52, 73, 94)); // Colore più scuro per non lette
                    setFont(new Font(FONT_SEGOE_UI, Font.BOLD, 12)); // Grassetto per non lette
                } else {
                    setForeground(new Color(149, 165, 166)); // Colore più chiaro per lette
                }
                setPreferredSize(new Dimension(list.getWidth(), 40));
            } else {
                setText("Notifica non valida");
                setForeground(Color.RED);
            }
            return this;
        }
    }
}
