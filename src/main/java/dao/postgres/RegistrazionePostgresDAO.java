package dao.postgres;
import dao.RegistrazioneDAO;
import database.ConnectionManager;
import database.DataAccessException;
import model.Registrazione;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Implementazione PostgreSQL dell'interfaccia RegistrazioneDAO.
 * Gestisce tutte le operazioni CRUD e specifiche per le registrazioni.
 * 
 * Refactored per dependency injection e transazioni per-connection.
 */
// Package naming convention now follows standard Java conventions
public class RegistrazionePostgresDAO implements RegistrazioneDAO {
    // Costanti per i nomi delle colonne del database
    private static final String COLUMN_COUNT = "count";
    private static final String SELECT_COUNT = "SELECT COUNT(*)";
    private final ConnectionManager cm;
    /**
     * Costruttore per dependency injection
     * 
     * @param connectionManager il ConnectionManager da utilizzare
     */
    public RegistrazionePostgresDAO(ConnectionManager connectionManager) {
        this.cm = connectionManager;
    }
    /**
     * Esegue un'operazione di database con gestione delle transazioni
     * 
     * @param operation l'operazione da eseguire
     * @param <T> il tipo di ritorno
     * @return il risultato dell'operazione
     * @throws DataAccessException se si verifica un errore
     */
    private <T> T executeWithTransaction(DatabaseOperation<T> operation) {
        try (Connection conn = cm.getConnection()) {
            conn.setAutoCommit(false);
            return executeOperationWithRollback(conn, operation);
        } catch (SQLException e) {
            throw new DataAccessException("Operazione database fallita", e);
        }
    }
    /**
     * Esegue l'operazione con gestione del rollback
     */
    private <T> T executeOperationWithRollback(Connection conn, DatabaseOperation<T> operation) throws SQLException {
        try {
            T result = operation.execute(conn);
            conn.commit();
            return result;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }
    /**
     * Interfaccia funzionale per operazioni di database
     */
    @FunctionalInterface
    private interface DatabaseOperation<T> {
        T execute(Connection conn) throws SQLException;
    }
    /**
     * Esegue una query che restituisce un singolo risultato
     */
    private Registrazione executeSingleResultQuery(PreparedStatement pstmt) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToRegistrazione(rs);
            }
        }
        return null;
    }
    /**
     * Esegue una query che restituisce una lista di risultati
     */
    private List<Registrazione> executeListResultQuery(PreparedStatement pstmt) throws SQLException {
        List<Registrazione> registrazioni = new ArrayList<>();
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                registrazioni.add(mapResultSetToRegistrazione(rs));
            }
        }
        return registrazioni;
    }
    /**
     * Esegue una query che restituisce un singolo valore intero
     */
    private int executeSingleIntQuery(PreparedStatement pstmt) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(COLUMN_COUNT);
            }
        }
        return 0;
    }
    /**
     * Esegue una query che restituisce un singolo valore booleano
     */
    private boolean executeSingleBooleanQuery(PreparedStatement pstmt) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(COLUMN_COUNT) > 0;
            }
        }
        return false;
    }
    /**
     * Esegue una query che restituisce un singolo valore booleano da una colonna specifica
     */
    private boolean executeSingleBooleanQuery(PreparedStatement pstmt, String columnName) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getBoolean(columnName);
            }
        }
        return false;
    }
    @Override
    public int insert(Registrazione registrazione) {
        String sql = "INSERT INTO registrazione (utente_id, hackathon_id, data_registrazione, " +
                    "ruolo, confermata) VALUES (?, ?, ?, ?, ?) RETURNING id";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, registrazione.getUtenteId());
                pstmt.setInt(2, registrazione.getHackathonId());
                pstmt.setTimestamp(3, Timestamp.valueOf(registrazione.getDataRegistrazione()));
                pstmt.setString(4, registrazione.getRuolo().toString());
                pstmt.setBoolean(5, registrazione.isConfermata());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        registrazione.setId(id);
                        return id;
                    }
                }
            }
            return -1;
        });
    }
    @Override
    public boolean update(Registrazione registrazione) {
        String sql = "UPDATE registrazione SET utente_id = ?, hackathon_id = ?, " +
                    "data_registrazione = ?, ruolo = ?, confermata = ? WHERE id = ?";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, registrazione.getUtenteId());
                pstmt.setInt(2, registrazione.getHackathonId());
                pstmt.setTimestamp(3, Timestamp.valueOf(registrazione.getDataRegistrazione()));
                pstmt.setString(4, registrazione.getRuolo().toString());
                pstmt.setBoolean(5, registrazione.isConfermata());
                pstmt.setInt(6, registrazione.getId());
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM registrazione WHERE id = ?";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }
    @Override
    public Registrazione findById(int id) {
        String sql = "SELECT * FROM registrazione WHERE id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return executeSingleResultQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("RegistrazionePostgresDAO.findById fallita", e);
        }
    }
    @Override
    public List<Registrazione> findAll() {
        String sql = "SELECT * FROM registrazione ORDER BY data_registrazione DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            return executeListResultQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("RegistrazionePostgresDAO.findAll fallita", e);
        }
    }
    @Override
    public List<Registrazione> findByUtente(int utenteId) {
        String sql = "SELECT * FROM registrazione WHERE utente_id = ? ORDER BY data_registrazione DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, utenteId);
            return executeListResultQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("RegistrazionePostgresDAO.findByUtente fallita", e);
        }
    }
    @Override
    public List<Registrazione> findByHackathon(int hackathonId) {
        String sql = "SELECT * FROM registrazione WHERE hackathon_id = ? ORDER BY data_registrazione DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            return executeListResultQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("RegistrazionePostgresDAO.findByHackathon fallita", e);
        }
    }
    @Override
    public List<Registrazione> findConfermateByHackathon(int hackathonId) {
        String sql = "SELECT * FROM registrazione WHERE hackathon_id = ? AND confermata = true ORDER BY data_registrazione DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            return executeListResultQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("RegistrazionePostgresDAO.findConfermateByHackathon fallita", e);
        }
    }
    @Override
    public boolean confermaRegistrazione(int registrazioneId) {
        String sql = "UPDATE registrazione SET confermata = true WHERE id = ?";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, registrazioneId);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }
    @Override
    public boolean isRegistrato(int utenteId, int hackathonId) {
        String sql = SELECT_COUNT + " as " + COLUMN_COUNT + " FROM registrazione WHERE utente_id = ? AND hackathon_id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, utenteId);
            pstmt.setInt(2, hackathonId);
            return executeSingleBooleanQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("RegistrazionePostgresDAO.isRegistrato fallita", e);
        }
    }
    @Override
    public int contaRegistrazioniPerRuolo(int hackathonId, Registrazione.Ruolo ruolo) {
        String sql = SELECT_COUNT + " as " + COLUMN_COUNT + " FROM registrazione WHERE hackathon_id = ? AND ruolo = ? AND confermata = true";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            pstmt.setString(2, ruolo.toString());
            return executeSingleIntQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("RegistrazionePostgresDAO.contaRegistrazioniPerRuolo fallita", e);
        }
    }
    @Override
    public int contaRegistrazioniConfermate(int hackathonId) {
        String sql = SELECT_COUNT + " as " + COLUMN_COUNT + " FROM registrazione WHERE hackathon_id = ? AND confermata = true";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            return executeSingleIntQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("RegistrazionePostgresDAO.contaRegistrazioniConfermate fallita", e);
        }
    }
    @Override
    public List<Registrazione> findOrganizzatori(int hackathonId) {
        return findByHackathonAndRuolo(hackathonId, Registrazione.Ruolo.ORGANIZZATORE);
    }
    @Override
    public List<Registrazione> findGiudici(int hackathonId) {
        return findByHackathonAndRuolo(hackathonId, Registrazione.Ruolo.GIUDICE);
    }
    @Override
    public List<Registrazione> findPartecipanti(int hackathonId) {
        return findByHackathonAndRuolo(hackathonId, Registrazione.Ruolo.PARTECIPANTE);
    }
    @Override
    public List<Registrazione> findByHackathonAndRuolo(int hackathonId, Registrazione.Ruolo ruolo) {
        String sql = "SELECT * FROM registrazione WHERE hackathon_id = ? AND ruolo = ? ORDER BY data_registrazione DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            pstmt.setString(2, ruolo.toString());
            return executeListResultQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("RegistrazionePostgresDAO.findByHackathonAndRuolo fallita", e);
        }
    }
    @Override
    public List<Registrazione> findNonConfermateByHackathon(int hackathonId) {
        String sql = "SELECT * FROM registrazione WHERE hackathon_id = ? AND confermata = false ORDER BY data_registrazione DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            return executeListResultQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("RegistrazionePostgresDAO.findNonConfermateByHackathon fallita", e);
        }
    }
    @Override
    public List<Registrazione> findNonConfermate() {
        String sql = "SELECT * FROM registrazione WHERE confermata = false ORDER BY data_registrazione DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            return executeListResultQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("RegistrazionePostgresDAO.findNonConfermate fallita", e);
        }
    }
    @Override
    public Registrazione findByUtenteAndHackathon(int utenteId, int hackathonId) {
        String sql = "SELECT * FROM registrazione WHERE utente_id = ? AND hackathon_id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, utenteId);
            pstmt.setInt(2, hackathonId);
            return executeSingleResultQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("RegistrazionePostgresDAO.findByUtenteAndHackathon fallita", e);
        }
    }
    @Override
    public boolean isConfermato(int utenteId, int hackathonId) {
        String sql = "SELECT confermata FROM registrazione WHERE utente_id = ? AND hackathon_id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, utenteId);
            pstmt.setInt(2, hackathonId);
            return executeSingleBooleanQuery(pstmt, "confermata");
        } catch (SQLException e) {
            throw new DataAccessException("RegistrazionePostgresDAO.isConfermato fallita", e);
        }
    }
    @Override
    public int contaRegistrazioni(int hackathonId) {
        String sql = SELECT_COUNT + " as " + COLUMN_COUNT + " FROM registrazione WHERE hackathon_id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            return executeSingleIntQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("RegistrazionePostgresDAO.contaRegistrazioni fallita", e);
        }
    }
    /**
     * Mappa un ResultSet in un oggetto Registrazione
     *
     * @param rs il ResultSet da mappare
     * @return l'oggetto Registrazione mappato
     * @throws SQLException se si verifica un errore durante la lettura
     */
    private Registrazione mapResultSetToRegistrazione(ResultSet rs) throws SQLException {
        Registrazione registrazione = new Registrazione(
            rs.getInt("utente_id"),
            rs.getInt("hackathon_id"),
            Registrazione.Ruolo.valueOf(rs.getString("ruolo"))
        );
        registrazione.setId(rs.getInt("id"));
        registrazione.setDataRegistrazione(rs.getTimestamp("data_registrazione").toLocalDateTime());
        registrazione.setConfermata(rs.getBoolean("confermata"));
        return registrazione;
    }
}
