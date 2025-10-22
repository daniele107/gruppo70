package dao.postgres;

import dao.AuditLogDAO;
import database.ConnectionManager;
import database.DataAccessException;
import model.AuditLog;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Implementazione PostgreSQL del DAO per l'audit log.
 */
public class AuditLogPostgresDAO implements AuditLogDAO {
    
    private static final Logger LOGGER = Logger.getLogger(AuditLogPostgresDAO.class.getName());
    private final ConnectionManager connectionManager;
    private final ObjectMapper objectMapper;
    
    // SQL Queries - Base constants
    private static final String TABLE = "audit_log";
    private static final String SELECT_COLUMNS = 
        "SELECT id, utente_id, azione, risorsa, risorsa_id, dettagli, ip_address, user_agent, ";
    private static final String SELECT_COLUMNS_END = 
        "timestamp, session_id, risultato, durata_ms, metadata ";
    private static final String COUNT_ALIAS = "count";
    private static final String FROM_TABLE = " FROM " + TABLE;
    private static final String BASE_SELECT = SELECT_COLUMNS + SELECT_COLUMNS_END + FROM_TABLE;
    private static final String SQL_COUNT_ALL = "SELECT COUNT(*) FROM " + TABLE;
    
    private static final String INSERT_AUDIT_LOG = 
        "INSERT INTO " + TABLE + " (utente_id, azione, risorsa, risorsa_id, dettagli, ip_address, " +
        "user_agent, session_id, risultato, durata_ms, metadata) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb) RETURNING id";
    
    private static final String FIND_BY_ID = 
        BASE_SELECT + " WHERE id = ?";
    
    private static final String FIND_BY_UTENTE = 
        BASE_SELECT + " WHERE utente_id = ? ORDER BY timestamp DESC";
    
    private static final String FIND_BY_AZIONE = 
        BASE_SELECT + " WHERE azione = ? ORDER BY timestamp DESC";
    
    private static final String FIND_BY_RISORSA = 
        BASE_SELECT + " WHERE risorsa = ? %s ORDER BY timestamp DESC";
    
    private static final String FIND_BY_DATE_RANGE = 
        BASE_SELECT + " WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";
    
    private static final String FIND_BY_RISULTATO = 
        BASE_SELECT + " WHERE risultato = ? ORDER BY timestamp DESC";
    
    private static final String FIND_CRITICAL = 
        BASE_SELECT + " WHERE risultato = 'FAILURE' OR azione IN ('LOGIN_FAILED', 'ACCESS_DENIED', " +
        "'PERMISSION_ERROR', 'SECURITY_VIOLATION') ORDER BY timestamp DESC";
    
    private static final String COUNT_BY_UTENTE = 
        SQL_COUNT_ALL + " WHERE utente_id = ?";
    
    private static final String COUNT_BY_AZIONE = 
        SQL_COUNT_ALL + " WHERE azione = ?";
    
    private static final String GET_STATS_BY_PERIOD = 
        "SELECT risultato, COUNT(*) as " + COUNT_ALIAS + FROM_TABLE + " " +
        "WHERE timestamp BETWEEN ? AND ? GROUP BY risultato";
    
    private static final String GET_TOP_ACTIONS = 
        "SELECT azione, COUNT(*) as " + COUNT_ALIAS + FROM_TABLE + " " +
        "GROUP BY azione ORDER BY " + COUNT_ALIAS + " DESC LIMIT ?";
    
    private static final String GET_TOP_USERS = 
        "SELECT utente_id, COUNT(*) as " + COUNT_ALIAS + FROM_TABLE + " " +
        "WHERE utente_id IS NOT NULL GROUP BY utente_id ORDER BY " + COUNT_ALIAS + " DESC LIMIT ?";
    
    private static final String DELETE_OLDER_THAN = 
        "DELETE FROM " + TABLE + " WHERE timestamp < ?";
    
    private static final String FIND_ALL_PAGINATED = 
        BASE_SELECT + " ORDER BY timestamp DESC LIMIT ? OFFSET ?";
    
    private static final String COUNT_ALL = SQL_COUNT_ALL;
    
    // Error message constants
    private static final String UTENTE_ID_PARAM = "utenteId=";
    private static final String COUNT_FAILED_SQL = "count failed. sql=";
    private static final String LIMIT_PARAM = "limit=";
    
    public AuditLogPostgresDAO(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Serializza i metadata in formato JSON
     */
    private String serializeMetadata(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }
        
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Errore serializzazione metadata", e);
            return "{}";
        }
    }
    
    /**
     * Processa il risultato di una top action
     */
    private void processTopActionResult(String azioneCode, int count, Map<AuditLog.AuditAction, Integer> topActions) {
        try {
            AuditLog.AuditAction azione = AuditLog.AuditAction.valueOf(azioneCode);
            topActions.put(azione, count);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Azione sconosciuta nel database: {0}", azioneCode);
        }
    }
    
    @Override
    public int insert(AuditLog auditLog) {
        if (auditLog == null) {
            throw new IllegalArgumentException("AuditLog non può essere null");
        }
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_AUDIT_LOG)) {
            
            statement.setObject(1, auditLog.getUtenteId());
            statement.setString(2, auditLog.getAzione().getCode());
            statement.setString(3, auditLog.getRisorsa());
            statement.setObject(4, auditLog.getRisorsaId());
            statement.setString(5, auditLog.getDettagli());
            statement.setString(6, auditLog.getIpAddress());
            statement.setString(7, auditLog.getUserAgent());
            statement.setString(8, auditLog.getSessionId());
            statement.setString(9, auditLog.getRisultato().getValue());
            statement.setObject(10, auditLog.getDurataMsec());
            
            // Serializza metadata come JSON
            String metadataJson = serializeMetadata(auditLog.getMetadata());
            statement.setString(11, metadataJson);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    auditLog.setId(id);
                    LOGGER.log(Level.FINE, () -> "AuditLog inserito con ID: " + id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                "AuditLogPostgresDAO.insert failed. sql='" + INSERT_AUDIT_LOG + "', " + UTENTE_ID_PARAM + 
                auditLog.getUtenteId() + ", azione=" + auditLog.getAzione(), e);
        }
        return -1;
    }
    
    @Override
    public AuditLog findById(int id) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToAuditLog(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                "AuditLogPostgresDAO.findById failed. sql='" + FIND_BY_ID + "', id=" + id, e);
        }
        return null;
    }
    
    @Override
    public List<AuditLog> findByUtente(int utenteId) {
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_UTENTE)) {
            
            statement.setInt(1, utenteId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapResultSetToAuditLog(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                "AuditLogPostgresDAO.findByUtente failed. sql='" + FIND_BY_UTENTE + "', " + UTENTE_ID_PARAM + utenteId, e);
        }
        
        return logs;
    }
    
    @Override
    public List<AuditLog> findByAzione(AuditLog.AuditAction azione) {
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_AZIONE)) {
            
            statement.setString(1, azione.getCode());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapResultSetToAuditLog(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                "AuditLogPostgresDAO.findByAzione failed. sql='" + FIND_BY_AZIONE + "', azione=" + azione, e);
        }
        
        return logs;
    }
    
    @Override
    public List<AuditLog> findByRisorsa(String risorsa, Integer risorsaId) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = String.format(FIND_BY_RISORSA, risorsaId != null ? " AND risorsa_id = ?" : "");
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, risorsa);
            if (risorsaId != null) {
                statement.setInt(2, risorsaId);
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapResultSetToAuditLog(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                "AuditLogPostgresDAO.findByRisorsa failed. sql='" + FIND_BY_RISORSA + "', risorsa=" + risorsa, e);
        }
        
        return logs;
    }
    
    @Override
    public List<AuditLog> findByDateRange(LocalDateTime dataInizio, LocalDateTime dataFine) {
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_DATE_RANGE)) {
            
            statement.setTimestamp(1, Timestamp.valueOf(dataInizio));
            statement.setTimestamp(2, Timestamp.valueOf(dataFine));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapResultSetToAuditLog(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                "AuditLogPostgresDAO.findByDateRange failed. sql='" + FIND_BY_DATE_RANGE + "', dataInizio=" + dataInizio + ", dataFine=" + dataFine, e);
        }
        
        return logs;
    }
    
    @Override
    public List<AuditLog> findByRisultato(AuditLog.AuditResult risultato) {
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_RISULTATO)) {
            
            statement.setString(1, risultato.getValue());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapResultSetToAuditLog(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                "AuditLogPostgresDAO.findByRisultato failed. sql='" + FIND_BY_RISULTATO + "', risultato=" + risultato, e);
        }
        
        return logs;
    }
    
    @Override
    public List<AuditLog> findCritical() {
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_CRITICAL)) {
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapResultSetToAuditLog(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                "AuditLogPostgresDAO.findCritical failed. sql='" + FIND_CRITICAL + "'", e);
        }
        
        return logs;
    }
    
    @Override
    public List<AuditLog> findWithFilters(Integer utenteId, AuditLog.AuditAction azione, 
                                         String risorsa, AuditLog.AuditResult risultato,
                                         LocalDateTime dataInizio, LocalDateTime dataFine, int limit) {
        List<AuditLog> logs = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            SELECT_COLUMNS + SELECT_COLUMNS_END + FROM_TABLE + " WHERE 1=1");
        
        List<Object> parameters = new ArrayList<>();
        
        if (utenteId != null) {
            sql.append(" AND utente_id = ?");
            parameters.add(utenteId);
        }
        
        if (azione != null) {
            sql.append(" AND azione = ?");
            parameters.add(azione.getCode());
        }
        
        if (risorsa != null) {
            sql.append(" AND risorsa = ?");
            parameters.add(risorsa);
        }
        
        if (risultato != null) {
            sql.append(" AND risultato = ?");
            parameters.add(risultato.getValue());
        }
        
        if (dataInizio != null && dataFine != null) {
            sql.append(" AND timestamp BETWEEN ? AND ?");
            parameters.add(Timestamp.valueOf(dataInizio));
            parameters.add(Timestamp.valueOf(dataFine));
        }
        
        sql.append(" ORDER BY timestamp DESC LIMIT ?");
        parameters.add(limit);
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapResultSetToAuditLog(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                "AuditLogPostgresDAO.findByFiltri failed. sql='" + FIND_BY_RISORSA + "'", e);
        }
        
        return logs;
    }
    
    @Override
    public int countByUtente(int utenteId) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_BY_UTENTE)) {
            
            statement.setInt(1, utenteId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e, () -> COUNT_FAILED_SQL + "'" + COUNT_BY_UTENTE + "', " + UTENTE_ID_PARAM + utenteId);
            return 0;
        }
        return 0;
    }
    
    @Override
    public int countByAzione(AuditLog.AuditAction azione) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_BY_AZIONE)) {
            
            statement.setString(1, azione.getCode());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e, () -> COUNT_FAILED_SQL + "'" + COUNT_BY_AZIONE + "', azione=" + azione);
            return 0;
        }
        return 0;
    }
    
    @Override
    public Map<String, Integer> getStatisticsByPeriod(LocalDateTime dataInizio, LocalDateTime dataFine) {
        Map<String, Integer> stats = new HashMap<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_STATS_BY_PERIOD)) {
            
            statement.setTimestamp(1, Timestamp.valueOf(dataInizio));
            statement.setTimestamp(2, Timestamp.valueOf(dataFine));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    stats.put(resultSet.getString("risultato"), resultSet.getInt(COUNT_ALIAS));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                "AuditLogPostgresDAO.getStatsByPeriod failed. sql='" + GET_STATS_BY_PERIOD + "', dataInizio=" + dataInizio + ", dataFine=" + dataFine, e);
        }
        
        return stats;
    }
    
    @Override
    public Map<AuditLog.AuditAction, Integer> getTopActions(int limit) {
        Map<AuditLog.AuditAction, Integer> topActions = new LinkedHashMap<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_TOP_ACTIONS)) {
            
            statement.setInt(1, limit);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String azioneCode = resultSet.getString("azione");
                    int count = resultSet.getInt(COUNT_ALIAS);
                    processTopActionResult(azioneCode, count, topActions);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                "AuditLogPostgresDAO.getTopActions failed. sql='" + GET_TOP_ACTIONS + "', " + LIMIT_PARAM + limit, e);
        }
        
        return topActions;
    }
    
    @Override
    public Map<Integer, Integer> getTopUsers(int limit) {
        Map<Integer, Integer> topUsers = new LinkedHashMap<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_TOP_USERS)) {
            
            statement.setInt(1, limit);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    topUsers.put(resultSet.getInt("utente_id"), resultSet.getInt(COUNT_ALIAS));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                "AuditLogPostgresDAO.getTopUsers failed. sql='" + GET_TOP_USERS + "', " + LIMIT_PARAM + limit, e);
        }
        
        return topUsers;
    }
    
    @Override
    public int deleteOlderThan(int giorni) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(giorni);
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_OLDER_THAN)) {
            
            statement.setTimestamp(1, Timestamp.valueOf(cutoffDate));
            
            int deleted = statement.executeUpdate();
            LOGGER.log(Level.INFO, "Eliminate {0} entry di audit più vecchie di {1} giorni", new Object[]{deleted, giorni});
            return deleted;
            
        } catch (SQLException e) {
            throw new DataAccessException(
                "AuditLogPostgresDAO.deleteOlderThan failed. sql='" + DELETE_OLDER_THAN + "', cutoffDate=" + cutoffDate, e);
        }
    }
    
    @Override
    public List<AuditLog> findAll(int offset, int limit) {
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_PAGINATED)) {
            
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapResultSetToAuditLog(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                "AuditLogPostgresDAO.findAll failed. sql='" + FIND_ALL_PAGINATED + "', " + LIMIT_PARAM + limit + ", offset=" + offset, e);
        }
        
        return logs;
    }
    
    @Override
    public int count() {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_ALL)) {
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e, () -> COUNT_FAILED_SQL + "'" + COUNT_ALL + "'");
            return 0;
        }
        return 0;
    }
    
    /**
     * Mappa un ResultSet a un oggetto AuditLog
     */
    private AuditLog mapResultSetToAuditLog(ResultSet resultSet) throws SQLException {
        AuditLog auditLog = new AuditLog();
        
        auditLog.setId(resultSet.getInt("id"));
        auditLog.setUtenteId((Integer) resultSet.getObject("utente_id"));
        
        // Mappa azione
        String azioneCode = resultSet.getString("azione");
        try {
            auditLog.setAzione(AuditLog.AuditAction.valueOf(azioneCode));
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Azione sconosciuta: {0}", azioneCode);
            auditLog.setAzione(AuditLog.AuditAction.LOGIN); // Default fallback
        }
        
        auditLog.setRisorsa(resultSet.getString("risorsa"));
        auditLog.setRisorsaId((Integer) resultSet.getObject("risorsa_id"));
        auditLog.setDettagli(resultSet.getString("dettagli"));
        auditLog.setIpAddress(resultSet.getString("ip_address"));
        auditLog.setUserAgent(resultSet.getString("user_agent"));
        auditLog.setTimestamp(resultSet.getTimestamp("timestamp").toLocalDateTime());
        auditLog.setSessionId(resultSet.getString("session_id"));
        
        // Mappa risultato
        String risultatoValue = resultSet.getString("risultato");
        try {
            auditLog.setRisultato(AuditLog.AuditResult.valueOf(risultatoValue));
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Risultato sconosciuto: {0}", risultatoValue);
            auditLog.setRisultato(AuditLog.AuditResult.SUCCESS); // Default fallback
        }
        
        auditLog.setDurataMsec((Integer) resultSet.getObject("durata_ms"));
        
        // Deserializza metadata
        String metadataJson = resultSet.getString("metadata");
        if (metadataJson != null && !metadataJson.trim().isEmpty()) {
            try {
                Map<String, Object> metadata = objectMapper.readValue(metadataJson, 
                    new TypeReference<Map<String, Object>>() {});
                auditLog.setMetadata(metadata);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Errore deserializzazione metadata", e);
            }
        }
        
        return auditLog;
    }
}
