package dao.postgres;

import dao.NotificationDAO;
import database.ConnectionManager;
import database.DataAccessException;
import model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Implementazione PostgreSQL del DAO per le notifiche.
 * Gestisce tutte le operazioni di accesso ai dati per le notifiche.
 */
public class NotificationPostgresDAO implements NotificationDAO {
    
    private static final Logger LOGGER = Logger.getLogger(NotificationPostgresDAO.class.getName());
    private static final String MSG_STACK_TRACE_COMPLETO = "Stack trace completo:";
    private static final String SQL_ERROR_PREFIX = "SQL Error: ";
    private final ConnectionManager connectionManager;
    
    // SQL Queries
    private static final String INSERT_NOTIFICATION = 
        "INSERT INTO notifications (utente_id, titolo, messaggio, tipo, data_creazione, letta) " +
        "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
    
    private static final String SELECT_FIELDS = 
        "SELECT id, utente_id, titolo, messaggio, tipo, data_creazione, letta, data_lettura ";
    
    private static final String FIND_BY_ID = 
        SELECT_FIELDS + "FROM notifications WHERE id = ?";
    
    private static final String FIND_BY_UTENTE = 
        SELECT_FIELDS + "FROM notifications WHERE utente_id = ? ORDER BY data_creazione DESC";
    
    private static final String FIND_UNREAD_BY_UTENTE = 
        SELECT_FIELDS + "FROM notifications WHERE utente_id = ? AND letta = FALSE ORDER BY data_creazione DESC";
    
    private static final String FIND_BY_UTENTE_AND_TYPE = 
        SELECT_FIELDS + "FROM notifications WHERE utente_id = ? AND tipo = ? ORDER BY data_creazione DESC";
    
    private static final String MARK_AS_READ = 
        "UPDATE notifications SET letta = TRUE, data_lettura = CURRENT_TIMESTAMP WHERE id = ? AND utente_id = ?";
    
    private static final String MARK_ALL_AS_READ = 
        "UPDATE notifications SET letta = TRUE, data_lettura = CURRENT_TIMESTAMP WHERE utente_id = ? AND letta = FALSE";
    
    private static final String DELETE_NOTIFICATION = 
        "DELETE FROM notifications WHERE id = ?";
    
    private static final String DELETE_BY_UTENTE = 
        "DELETE FROM notifications WHERE utente_id = ?";
    
    private static final String COUNT_UNREAD_BY_UTENTE = 
        "SELECT COUNT(*) FROM notifications WHERE utente_id = ? AND letta = FALSE";
    
    private static final String FIND_ALL = 
        SELECT_FIELDS + "FROM notifications ORDER BY data_creazione DESC";
    
    private static final String DELETE_OLDER_THAN = 
        "DELETE FROM notifications WHERE data_creazione < CURRENT_TIMESTAMP - INTERVAL '%d days'";
    
    /**
     * Costruttore che inizializza il DAO con il ConnectionManager
     * 
     * @param connectionManager il manager delle connessioni al database
     */
    public NotificationPostgresDAO(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    @Override
    public int insert(Notification notification) {
        if (notification == null) {
            throw new IllegalArgumentException("La notifica non può essere null");
        }
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_NOTIFICATION)) {
            
            statement.setInt(1, notification.getUserId());
            statement.setString(2, notification.getTitle());
            statement.setString(3, notification.getMessage());
            statement.setString(4, notification.getType().name());
            statement.setTimestamp(5, Timestamp.valueOf(notification.getCreatedAt()));
            statement.setBoolean(6, notification.isRead());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    notification.setId(id);
                    LOGGER.log(Level.INFO, "Notifica inserita con successo. ID: {0}", id);
                    return id;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nell''inserimento della notifica per utente ID: {0}", notification.getUserId());
            LOGGER.log(Level.SEVERE, MSG_STACK_TRACE_COMPLETO, e);
            throw new DataAccessException("Errore nell''inserimento della notifica per utente ID: " + notification.getUserId() + ". " + SQL_ERROR_PREFIX + e.getMessage(), e);
        }
        return -1;
    }
    
    @Override
    public Notification findById(int id) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToNotification(resultSet);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, () -> String.format("Errore nella ricerca della notifica per ID: %d", id));
            LOGGER.log(Level.SEVERE, MSG_STACK_TRACE_COMPLETO, e);
            throw new DataAccessException("Errore nella ricerca della notifica per ID: " + id + ". " + SQL_ERROR_PREFIX + e.getMessage(), e);
        }
        return null;
    }
    
    @Override
    public List<Notification> findByUtente(int utenteId) {
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_UTENTE)) {
            
            statement.setInt(1, utenteId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    notifications.add(mapResultSetToNotification(resultSet));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, () -> String.format("Errore nella ricerca delle notifiche per utente: %d", utenteId));
            LOGGER.log(Level.SEVERE, MSG_STACK_TRACE_COMPLETO, e);
            throw new DataAccessException("Errore nella ricerca delle notifiche per utente: " + utenteId + ". " + SQL_ERROR_PREFIX + e.getMessage(), e);
        }
        
        return notifications;
    }
    
    @Override
    public List<Notification> findUnreadByUtente(int utenteId) {
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_UNREAD_BY_UTENTE)) {
            
            statement.setInt(1, utenteId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    notifications.add(mapResultSetToNotification(resultSet));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, () -> String.format("Errore nella ricerca delle notifiche non lette per utente: %d", utenteId));
            LOGGER.log(Level.SEVERE, MSG_STACK_TRACE_COMPLETO, e);
            throw new DataAccessException("Errore nella ricerca delle notifiche non lette per utente: " + utenteId + ". " + SQL_ERROR_PREFIX + e.getMessage(), e);
        }
        
        return notifications;
    }
    
    @Override
    public List<Notification> findByUtenteAndType(int utenteId, Notification.NotificationType tipo) {
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_UTENTE_AND_TYPE)) {
            
            statement.setInt(1, utenteId);
            statement.setString(2, tipo.name());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    notifications.add(mapResultSetToNotification(resultSet));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nella ricerca delle notifiche per utente ID: {0} e tipo: {1}", new Object[]{utenteId, tipo});
            LOGGER.log(Level.SEVERE, MSG_STACK_TRACE_COMPLETO, e);
            throw new DataAccessException("Errore nella ricerca delle notifiche per utente: " + utenteId + " e tipo: " + tipo + ". " + SQL_ERROR_PREFIX + e.getMessage(), e);
        }
        
        return notifications;
    }
    
    @Override
    public boolean markAsRead(int notificationId, int utenteId) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(MARK_AS_READ)) {
            
            statement.setInt(1, notificationId);
            statement.setInt(2, utenteId);
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                LOGGER.log(Level.INFO, "Notifica {0} segnata come letta per utente {1}", new Object[]{notificationId, utenteId});
            }
            
            return success;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel segnare la notifica ID: {0} come letta per utente ID: {1}", new Object[]{notificationId, utenteId});
            LOGGER.log(Level.SEVERE, MSG_STACK_TRACE_COMPLETO, e);
            throw new DataAccessException("Errore nell''aggiornamento della notifica ID: " + notificationId + " per utente: " + utenteId + ". " + SQL_ERROR_PREFIX + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean markAllAsRead(int utenteId) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(MARK_ALL_AS_READ)) {
            
            statement.setInt(1, utenteId);
            
            int rowsAffected = statement.executeUpdate();
            LOGGER.log(Level.INFO, "Segnate {0} notifiche come lette per utente {1}", new Object[]{rowsAffected, utenteId});
            
            return true; // Ritorna sempre true anche se non ci sono notifiche da aggiornare
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel segnare tutte le notifiche come lette per utente ID: {0}", utenteId);
            LOGGER.log(Level.SEVERE, MSG_STACK_TRACE_COMPLETO, e);
            throw new DataAccessException("Errore nell''aggiornamento delle notifiche per utente: " + utenteId + ". " + SQL_ERROR_PREFIX + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean delete(int id) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_NOTIFICATION)) {
            
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                LOGGER.log(Level.INFO, "Notifica {0} eliminata con successo", id);
            }
            
            return success;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, () -> "Errore nell''eliminazione della notifica ID: " + id);
            LOGGER.log(Level.SEVERE, MSG_STACK_TRACE_COMPLETO, e);
            throw new DataAccessException("Errore nell''eliminazione della notifica ID: " + id + ". " + SQL_ERROR_PREFIX + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteByUtente(int utenteId) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_UTENTE)) {
            
            statement.setInt(1, utenteId);
            
            int rowsAffected = statement.executeUpdate();
            LOGGER.log(Level.INFO, "Eliminate {0} notifiche per utente {1}", new Object[]{rowsAffected, utenteId});
            
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, () -> "Errore nell''eliminazione delle notifiche per utente ID: " + utenteId);
            LOGGER.log(Level.SEVERE, MSG_STACK_TRACE_COMPLETO, e);
            throw new DataAccessException("Errore nell''eliminazione delle notifiche per utente: " + utenteId + ". " + SQL_ERROR_PREFIX + e.getMessage(), e);
        }
    }
    
    @Override
    public int countUnreadByUtente(int utenteId) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_UNREAD_BY_UTENTE)) {
            
            statement.setInt(1, utenteId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, () -> "Errore nel conteggio delle notifiche non lette per utente ID: " + utenteId);
            LOGGER.log(Level.SEVERE, MSG_STACK_TRACE_COMPLETO, e);
            throw new DataAccessException("Errore nel conteggio delle notifiche non lette per utente: " + utenteId + ". " + SQL_ERROR_PREFIX + e.getMessage(), e);
        }
        return 0;
    }
    
    @Override
    public List<Notification> findAll() {
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                notifications.add(mapResultSetToNotification(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nella ricerca di tutte le notifiche dal database", e);
            LOGGER.log(Level.SEVERE, MSG_STACK_TRACE_COMPLETO, e);
            throw new DataAccessException("Errore nella ricerca di tutte le notifiche. " + SQL_ERROR_PREFIX + e.getMessage(), e);
        }
        
        return notifications;
    }
    
    @Override
    public int deleteOlderThan(int days) {
        String query = String.format(DELETE_OLDER_THAN, days);
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            int rowsAffected = statement.executeUpdate();
            LOGGER.log(Level.INFO, "Eliminate {0} notifiche più vecchie di {1} giorni", new Object[]{rowsAffected, days});
            
            return rowsAffected;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nell''eliminazione delle notifiche più vecchie di {0} giorni", days);
            LOGGER.log(Level.SEVERE, MSG_STACK_TRACE_COMPLETO, e);
            throw new DataAccessException("Errore nell''eliminazione delle notifiche più vecchie di " + days + " giorni. " + SQL_ERROR_PREFIX + e.getMessage(), e);
        }
    }
    
    /**
     * Mappa un ResultSet a un oggetto Notification
     * 
     * @param resultSet il ResultSet da mappare
     * @return l'oggetto Notification
     * @throws SQLException se si verifica un errore SQL
     */
    private Notification mapResultSetToNotification(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int utenteId = resultSet.getInt("utente_id");
        String titolo = resultSet.getString("titolo");
        String messaggio = resultSet.getString("messaggio");
        String tipoString = resultSet.getString("tipo");
        Timestamp dataCreazione = resultSet.getTimestamp("data_creazione");
        boolean letta = resultSet.getBoolean("letta");
        Timestamp dataLettura = resultSet.getTimestamp("data_lettura");
        
        // Converti il tipo string in enum
        Notification.NotificationType tipo;
        try {
            tipo = Notification.NotificationType.valueOf(tipoString);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Tipo notifica non riconosciuto: {0}. Uso INFO come default.", tipoString);
            tipo = Notification.NotificationType.INFO;
        }
        
        // Crea la notifica
        Notification notification = new Notification(utenteId, titolo, messaggio, tipo);
        notification.setId(id);
        notification.setCreatedAt(dataCreazione.toLocalDateTime());
        notification.setRead(letta);
        
        if (dataLettura != null) {
            notification.setReadAt(dataLettura.toLocalDateTime());
        }
        
        return notification;
    }
}
