package model;
import java.time.LocalDateTime;
/**
 * Rappresenta una notifica nel sistema.
 * Le notifiche possono essere di diversi tipi e possono essere lette o non lette.
 */
public class Notification {
    public enum NotificationType {
        INFO("INFO", "Informazione"),
        SUCCESS("OK", "Successo"),
        WARNING("WARN", "Avviso"),
        ERROR("ERR", "Errore"),
        TEAM_JOIN_REQUEST("TEAM", "Richiesta Team"),
        NEW_COMMENT("MSG", "Nuovo Commento"),
        EVENT_UPDATE("EVENT", "Aggiornamento Evento"),
        SYSTEM("SYS", "Sistema");
        private final String icon;
        private final String description;
        NotificationType(String icon, String description) {
            this.icon = icon;
            this.description = description;
        }
        public String getIcon() {
            return icon;
        }
        public String getDescription() {
            return description;
        }
    }
    private int id;
    private int userId;
    private String title;
    private String message;
    private NotificationType type;
    private LocalDateTime createdAt;
    private boolean isRead;
    private LocalDateTime readAt;
    private String actionUrl; // Per azioni future (es. link a una pagina specifica)
    /**
     * Costruttore vuoto per la deserializzazione
     */
    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }
    /**
     * Costruttore principale per creare una nuova notifica
     *
     * @param userId    l'ID dell'utente destinatario
     * @param title     il titolo della notifica
     * @param message   il messaggio della notifica
     * @param type      il tipo di notifica
     */
    public Notification(int userId, String title, String message, NotificationType type) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }
    /**
     * Costruttore completo
     *
     * @param userId     l'ID dell'utente destinatario
     * @param title      il titolo della notifica
     * @param message    il messaggio della notifica
     * @param type       il tipo di notifica
     * @param actionUrl  URL per azioni aggiuntive
     */
    public Notification(int userId, String title, String message, NotificationType type, String actionUrl) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
        this.actionUrl = actionUrl;
    }
    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public NotificationType getType() {
        return type;
    }
    public void setType(NotificationType type) {
        this.type = type;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public boolean isRead() {
        return isRead;
    }
    public void setRead(boolean read) {
        isRead = read;
    }
    public LocalDateTime getReadAt() {
        return readAt;
    }
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
    public String getActionUrl() {
        return actionUrl;
    }
    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
    /**
     * Segna la notifica come letta
     */
    public void markAsRead() {
        this.isRead = true;
    }
    /**
     * Restituisce una rappresentazione testuale della notifica
     */
    @Override
    public String toString() {
        return "[" + type.getIcon() + "] " + title + ": " +
            (message.length() > 50 ? message.substring(0, 50) + "..." : message);
    }
    /**
     * Restituisce una rappresentazione dettagliata della notifica
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.getIcon()).append(" ").append(type.getDescription()).append("\n");
        sb.append("Titolo: ").append(title).append("\n");
        sb.append("Messaggio: ").append(message).append("\n");
        sb.append("Data: ").append(createdAt != null ? createdAt.toString() : "N/A").append("\n");
        sb.append("Stato: ").append(isRead ? "Letta" : "Non letta");
        if (actionUrl != null && !actionUrl.isEmpty()) {
            sb.append("\nAzione: ").append(actionUrl);
        }
        return sb.toString();
    }
    /**
     * Crea una notifica di benvenuto per nuovi utenti
     */
    public static Notification createWelcomeNotification(int userId, String username) {
        return new Notification(
            userId,
            "Benvenuto in Hackathon Manager!",
            "Ciao " + username + "! Benvenuto nella piattaforma di gestione hackathon. " +
            "Inizia esplorando gli eventi disponibili o creando il tuo primo team.",
            NotificationType.SUCCESS
        );
    }
    /**
     * Crea una notifica per una nuova richiesta di partecipazione al team
     */
    public static Notification createTeamJoinRequestNotification(int userId, String applicantName, String teamName) {
        return new Notification(
            userId,
            "Nuova richiesta di partecipazione",
            applicantName + " ha richiesto di unirsi al team '" + teamName + "'. " +
            "Puoi accettare o rifiutare la richiesta dalla sezione Team.",
            NotificationType.TEAM_JOIN_REQUEST,
            "#/team/join-requests"
        );
    }
    /**
     * Crea una notifica per un nuovo commento sui progressi
     */
    public static Notification createNewCommentNotification(int userId, String judgeName, String teamName) {
        return new Notification(
            userId,
            "Nuovo commento ricevuto",
            "Il giudice " + judgeName + " ha lasciato un commento sui progressi del team '" + teamName + "'.",
            NotificationType.NEW_COMMENT,
            "#/progress/comments"
        );
    }
    /**
     * Crea una notifica per aggiornamenti dell'evento
     */
    public static Notification createEventUpdateNotification(int userId, String eventName, String updateMessage) {
        return new Notification(
            userId,
            "Aggiornamento evento: " + eventName,
            updateMessage,
            NotificationType.EVENT_UPDATE,
            "#/events/" + eventName.toLowerCase().replace(" ", "-")
        );
    }
    /**
     * Crea una notifica di sistema
     */
    public static Notification createSystemNotification(int userId, String title, String message) {
        return new Notification(
            userId,
            title,
            message,
            NotificationType.SYSTEM
        );
    }
}
