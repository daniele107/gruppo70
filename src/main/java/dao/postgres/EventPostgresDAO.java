package dao.postgres;
import dao.EventDAO;
import database.ConnectionManager;
import database.DataAccessException;
import model.EventRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Implementazione PostgreSQL per EventDAO.
 * Gestisce la conversione tra EventRequest e Hackathon entity.
 * Segue le best practices SonarLint già consolidate nel progetto.
 */
public final class EventPostgresDAO implements EventDAO {
    private static final Logger LOGGER = Logger.getLogger(EventPostgresDAO.class.getName());
    // Costanti SQL per evitare duplicazione
    private static final String TABLE_HACKATHON = "hackathon";
    private static final String COL_ID = "id";
    private static final String COL_NOME = "nome";
    private static final String COL_DATA_INIZIO = "data_inizio";
    private static final String COL_DATA_FINE = "data_fine";
    private static final String COL_SEDE = "sede";
    private static final String COL_IS_VIRTUALE = "is_virtuale";
    private static final String COL_ORGANIZZATORE_ID = "organizzatore_id";
    private static final String COL_MAX_PARTECIPANTI = "max_partecipanti";
    private static final String COL_MAX_TEAM = "max_team";
    private static final String COL_DESCRIZIONE_PROBLEMA = "descrizione_problema";
    private static final String INSERT_SQL = 
        "INSERT INTO " + TABLE_HACKATHON + " (" +
        COL_NOME + ", " + COL_DATA_INIZIO + ", " + COL_DATA_FINE + ", " +
        COL_SEDE + ", " + COL_IS_VIRTUALE + ", " + COL_ORGANIZZATORE_ID + ", " +
        COL_MAX_PARTECIPANTI + ", " + COL_MAX_TEAM + ", " + COL_DESCRIZIONE_PROBLEMA +
        ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String EQUALS_PARAM = " = ?";
    private static final String UPDATE_SQL = 
        "UPDATE " + TABLE_HACKATHON + " SET " +
        COL_NOME + EQUALS_PARAM + ", " + COL_DATA_INIZIO + EQUALS_PARAM + ", " + COL_DATA_FINE + EQUALS_PARAM + ", " +
        COL_SEDE + EQUALS_PARAM + ", " + COL_IS_VIRTUALE + EQUALS_PARAM + ", " +
        COL_MAX_PARTECIPANTI + EQUALS_PARAM + ", " + COL_MAX_TEAM + EQUALS_PARAM + ", " +
        COL_DESCRIZIONE_PROBLEMA + EQUALS_PARAM + " " +
        "WHERE " + COL_ID + EQUALS_PARAM + " AND " + COL_ORGANIZZATORE_ID + EQUALS_PARAM;
    private final ConnectionManager connectionManager;
    /**
     * Costruttore che inizializza il DAO
     */
    public EventPostgresDAO(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    @Override
    public long insertFromRequest(EventRequest request, int organizzatoreId) {
        try {
            return executeWithTransaction(connection -> {
                long generatedId = executeInsertReturningKey(connection, request, organizzatoreId);
                LOGGER.log(Level.INFO, "Evento inserito con successo - ID: {0}, Nome: {1}", 
                    new Object[]{generatedId, request.nome()});
                return generatedId;
            });
        } catch (Exception e) {
            throw new DataAccessException("Failed to insert event '" + request.nome() + "': " + e.getMessage(), e);
        }
    }
    @Override
    public boolean updateFromRequest(long eventId, EventRequest request, int organizzatoreId) {
        try {
            return executeWithTransaction(connection -> {
                int rowsAffected = executeUpdate(connection, eventId, request, organizzatoreId);
                boolean success = rowsAffected > 0;
                if (success) {
                    LOGGER.log(Level.INFO, "Evento aggiornato con successo - ID: {0}", eventId);
                } else {
                    LOGGER.log(Level.WARNING, "Nessun evento aggiornato - ID: {0}, Organizzatore: {1}", 
                        new Object[]{eventId, organizzatoreId});
                }
                return success;
            });
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update event with ID " + eventId + ": " + e.getMessage(), e);
        }
    }
    /**
     * Esegue un'operazione in transazione gestita
     */
    private <T> T executeWithTransaction(DatabaseOperation<T> operation) throws SQLException {
        try (Connection connection = connectionManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                T result = operation.execute(connection);
                connection.commit();
                return result;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }
    /**
     * Esegue l'inserimento con chiave generata
     */
    private long executeInsertReturningKey(Connection connection, EventRequest request, int organizzatoreId) 
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setInsertParameters(statement, request, organizzatoreId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserimento fallito, nessuna riga modificata");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Inserimento fallito, nessuna chiave generata");
                }
            }
        }
    }
    /**
     * Esegue l'aggiornamento
     */
    private int executeUpdate(Connection connection, long eventId, EventRequest request, int organizzatoreId) 
            throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            setUpdateParameters(statement, eventId, request, organizzatoreId);
            return statement.executeUpdate();
        }
    }
    /**
     * Imposta i parametri per l'inserimento
     */
    private void setInsertParameters(PreparedStatement statement, EventRequest request, int organizzatoreId) 
            throws SQLException {
        statement.setString(1, request.nome());
        statement.setTimestamp(2, Timestamp.valueOf(request.dataInizio()));
        statement.setTimestamp(3, Timestamp.valueOf(request.dataFine()));
        statement.setString(4, request.sede());
        statement.setBoolean(5, request.virtuale());
        statement.setInt(6, organizzatoreId);
        statement.setInt(7, request.maxPartecipanti());
        statement.setInt(8, request.maxTeam());
        statement.setString(9, request.descrizioneProblema());
    }
    /**
     * Imposta i parametri per l'aggiornamento
     */
    private void setUpdateParameters(PreparedStatement statement, long eventId, EventRequest request, int organizzatoreId) 
            throws SQLException {
        statement.setString(1, request.nome());
        statement.setTimestamp(2, Timestamp.valueOf(request.dataInizio()));
        statement.setTimestamp(3, Timestamp.valueOf(request.dataFine()));
        statement.setString(4, request.sede());
        statement.setBoolean(5, request.virtuale());
        statement.setInt(6, request.maxPartecipanti());
        statement.setInt(7, request.maxTeam());
        statement.setString(8, request.descrizioneProblema());
        statement.setLong(9, eventId);
        statement.setInt(10, organizzatoreId);
    }
    /**
     * Interfaccia funzionale per operazioni database
     */
    @FunctionalInterface
    private interface DatabaseOperation<T> {
        T execute(Connection connection) throws SQLException;
    }
}
