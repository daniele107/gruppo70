package dao.postgres;
import dao.ValutazioneDAO;
import model.Valutazione;
import model.TeamRankingResult;
import database.ConnectionManager;
import database.DataAccessException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Implementazione PostgreSQL per ValutazioneDAO
 * Gestisce le operazioni di accesso ai dati per l'entità Valutazione
 */
// Package naming convention now follows standard Java conventions
public class ValutazionePostgresDAO implements ValutazioneDAO {
    // Costanti per i nomi delle colonne del database
    private static final String COLUMN_TEAM_ID = "team_id";
    private static final String COLUMN_ID = "id";
    // Costanti per i messaggi di errore
    private static final String ERROR_INSERT_FAILED = "Creating valutazione failed, no rows affected.";
    private static final String ERROR_INSERT_NO_ID = "Creating valutazione failed, no ID obtained.";
    private static final String ERROR_UPDATE_FAILED = "Updating valutazione failed, no rows affected.";
    private static final String ERROR_OPERATION = "Operazione database fallita";
    // Costanti per SQL fragments
    private static final String SQL_SELECT_ALL_FROM_VALUTAZIONE_WHERE = "SELECT * FROM valutazione WHERE ";
    private static final String ERROR_HACKATHON_ID = " hackathonId=";
    private final ConnectionManager cm;
    public ValutazionePostgresDAO(ConnectionManager connectionManager) {
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
            throw new DataAccessException(ERROR_OPERATION, e);
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
     * Esegue una query che restituisce una singola Valutazione
     */
    private Valutazione executeSingleResultQuery(PreparedStatement pstmt) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToValutazione(rs);
            }
        }
        return null;
    }
    /**
     * Esegue una query che restituisce una lista di Valutazione
     */
    private List<Valutazione> executeListResultQuery(PreparedStatement pstmt) throws SQLException {
        List<Valutazione> valutazioni = new ArrayList<>();
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                valutazioni.add(mapResultSetToValutazione(rs));
            }
        }
        return valutazioni;
    }
    /**
     * Esegue una query che restituisce un valore double (es. media)
     */
    private double executeSingleDoubleQuery(PreparedStatement pstmt) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                double value = rs.getDouble(1);
                return rs.wasNull() ? 0.0 : value;
            }
        }
        return 0.0;
    }
    /**
     * Esegue una query che restituisce un valore int (es. count)
     */
    private int executeSingleIntQuery(PreparedStatement pstmt) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    /**
     * Esegue una query che restituisce un boolean basato su count > 0
     */
    private boolean executeSingleBooleanQuery(PreparedStatement pstmt) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    @Override
    public Valutazione insert(Valutazione valutazione) {
        String sql = "INSERT INTO valutazione (giudice_id, " + COLUMN_TEAM_ID + ", hackathon_id, voto, commento, data_valutazione) VALUES (?, ?, ?, ?, ?, ?)";
        return executeWithTransaction(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, valutazione.getGiudiceId());
                stmt.setInt(2, valutazione.getTeamId());
                stmt.setInt(3, valutazione.getHackathonId());
                stmt.setInt(4, valutazione.getVoto());
                stmt.setString(5, valutazione.getCommento());
                stmt.setTimestamp(6, Timestamp.valueOf(valutazione.getDataValutazione()));
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException(ERROR_INSERT_FAILED);
                }
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        valutazione.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException(ERROR_INSERT_NO_ID);
                    }
                }
                return valutazione;
            }
        });
    }
    @Override
    public Valutazione update(Valutazione valutazione) {
        String sql = "UPDATE valutazione SET giudice_id = ?, " + COLUMN_TEAM_ID + " = ?, hackathon_id = ?, voto = ?, commento = ?, data_valutazione = ? WHERE " + COLUMN_ID + " = ?";
        return executeWithTransaction(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, valutazione.getGiudiceId());
                stmt.setInt(2, valutazione.getTeamId());
                stmt.setInt(3, valutazione.getHackathonId());
                stmt.setInt(4, valutazione.getVoto());
                stmt.setString(5, valutazione.getCommento());
                stmt.setTimestamp(6, Timestamp.valueOf(valutazione.getDataValutazione()));
                stmt.setInt(7, valutazione.getId());
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException(ERROR_UPDATE_FAILED);
                }
                return valutazione;
            }
        });
    }
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM valutazione WHERE " + COLUMN_ID + " = ?";
        return executeWithTransaction(conn -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
            }
        });
    }
    @Override
    public Valutazione findById(int id) {
        String sql = SQL_SELECT_ALL_FROM_VALUTAZIONE_WHERE + COLUMN_ID + " = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return executeSingleResultQuery(stmt);
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.findById " + COLUMN_ID + "=" + id, e);
        }
    }
    @Override
    public List<Valutazione> findAll() {
        String sql = "SELECT * FROM valutazione ORDER BY data_valutazione DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            return executeListResultQuery(stmt);
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.findAll", e);
        }
    }
    @Override
    public List<Valutazione> findByGiudice(int giudiceId) {
        String sql = SQL_SELECT_ALL_FROM_VALUTAZIONE_WHERE + "giudice_id = ? ORDER BY data_valutazione DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, giudiceId);
            return executeListResultQuery(stmt);
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.findByGiudice giudiceId=" + giudiceId, e);
        }
    }
    @Override
    public List<Valutazione> findByTeam(int teamId) {
        String sql = SQL_SELECT_ALL_FROM_VALUTAZIONE_WHERE + COLUMN_TEAM_ID + " = ? ORDER BY data_valutazione DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            return executeListResultQuery(stmt);
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.findByTeam " + COLUMN_TEAM_ID + "=" + teamId, e);
        }
    }
    @Override
    public List<Valutazione> findByHackathon(int hackathonId) {
        String sql = SQL_SELECT_ALL_FROM_VALUTAZIONE_WHERE + "hackathon_id = ? ORDER BY data_valutazione DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hackathonId);
            return executeListResultQuery(stmt);
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.findByHackathon" + ERROR_HACKATHON_ID + hackathonId, e);
        }
    }
    @Override
    public Valutazione findByGiudiceAndTeam(int giudiceId, int teamId) {
        String sql = SQL_SELECT_ALL_FROM_VALUTAZIONE_WHERE + "giudice_id = ? AND " + COLUMN_TEAM_ID + " = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, giudiceId);
            stmt.setInt(2, teamId);
            return executeSingleResultQuery(stmt);
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.findByGiudiceAndTeam giudiceId=" + giudiceId + " " + COLUMN_TEAM_ID + "=" + teamId, e);
        }
    }
    @Override
    public List<Valutazione> findByTeamAndHackathon(int teamId, int hackathonId) {
        String sql = SQL_SELECT_ALL_FROM_VALUTAZIONE_WHERE + COLUMN_TEAM_ID + " = ? AND hackathon_id = ? ORDER BY data_valutazione DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            stmt.setInt(2, hackathonId);
            return executeListResultQuery(stmt);
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.findByTeamAndHackathon " + COLUMN_TEAM_ID + "=" + teamId + ERROR_HACKATHON_ID + hackathonId, e);
        }
    }
    @Override
    public double findValutazioneMediaTeam(int teamId) {
        String sql = "SELECT AVG(voto) FROM valutazione WHERE " + COLUMN_TEAM_ID + " = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            return executeSingleDoubleQuery(stmt);
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.findValutazioneMediaTeam " + COLUMN_TEAM_ID + "=" + teamId, e);
        }
    }
    @Override
    public double findValutazioneMediaTeamInHackathon(int teamId, int hackathonId) {
        String sql = "SELECT AVG(voto) FROM valutazione WHERE " + COLUMN_TEAM_ID + " = ? AND hackathon_id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            stmt.setInt(2, hackathonId);
            return executeSingleDoubleQuery(stmt);
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.findValutazioneMediaTeamInHackathon " + COLUMN_TEAM_ID + "=" + teamId + ERROR_HACKATHON_ID + hackathonId, e);
        }
    }
    @Override
    public List<Valutazione> findAllOrderByVoto() {
        String sql = "SELECT * FROM valutazione ORDER BY voto DESC, data_valutazione DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            return executeListResultQuery(stmt);
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.findAllOrderByVoto", e);
        }
    }
    @Override
    public List<Valutazione> findByHackathonOrderByVoto(int hackathonId) {
        String sql = SQL_SELECT_ALL_FROM_VALUTAZIONE_WHERE + "hackathon_id = ? ORDER BY voto DESC, data_valutazione DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hackathonId);
            return executeListResultQuery(stmt);
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.findByHackathonOrderByVoto" + ERROR_HACKATHON_ID + hackathonId, e);
        }
    }
    @Override
    public int contaValutazioniTeam(int teamId) {
        String sql = "SELECT COUNT(*) FROM valutazione WHERE " + COLUMN_TEAM_ID + " = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            return executeSingleIntQuery(stmt);
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.contaValutazioniTeam " + COLUMN_TEAM_ID + "=" + teamId, e);
        }
    }
    @Override
    public int contaValutazioniHackathon(int hackathonId) {
        String sql = "SELECT COUNT(*) FROM valutazione WHERE hackathon_id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hackathonId);
            return executeSingleIntQuery(stmt);
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.contaValutazioniHackathon" + ERROR_HACKATHON_ID + hackathonId, e);
        }
    }
    @Override
    public int contaValutazioniGiudice(int giudiceId) {
        String sql = "SELECT COUNT(*) FROM valutazione WHERE giudice_id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, giudiceId);
            return executeSingleIntQuery(stmt);
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.contaValutazioniGiudice giudiceId=" + giudiceId, e);
        }
    }
    @Override
    public boolean haGiudiceValutatoTeam(int giudiceId, int teamId) {
        String sql = "SELECT COUNT(*) FROM valutazione WHERE giudice_id = ? AND " + COLUMN_TEAM_ID + " = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, giudiceId);
            stmt.setInt(2, teamId);
            return executeSingleBooleanQuery(stmt);
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.haGiudiceValutatoTeam giudiceId=" + giudiceId + " " + COLUMN_TEAM_ID + "=" + teamId, e);
        }
    }
    @Override
    public List<Integer> findClassificaTeam(int hackathonId) {
        String sql = "SELECT " + COLUMN_TEAM_ID + ", AVG(voto) as media_voto FROM valutazione " +
                    "WHERE hackathon_id = ? GROUP BY " + COLUMN_TEAM_ID + " ORDER BY media_voto DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hackathonId);
            List<Integer> classifica = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    classifica.add(rs.getInt(COLUMN_TEAM_ID));
                }
            }
            return classifica;
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.findClassificaTeam" + ERROR_HACKATHON_ID + hackathonId, e);
        }
    }

    @Override
    public List<TeamRankingResult> findTeamRankingByHackathon(int hackathonId) {
        String sql = "SELECT " + COLUMN_TEAM_ID + ", AVG(voto) AS media_voto, COUNT(*) AS num_voti " +
                     "FROM valutazione WHERE hackathon_id = ? " +
                     "GROUP BY " + COLUMN_TEAM_ID + " " +
                     "ORDER BY media_voto DESC, num_voti DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hackathonId);
            List<TeamRankingResult> result = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int teamId = rs.getInt(COLUMN_TEAM_ID);
                    double average = rs.getDouble("media_voto");
                    int count = rs.getInt("num_voti");
                    result.add(new TeamRankingResult(teamId, average, count));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.findTeamRankingByHackathon" + ERROR_HACKATHON_ID + hackathonId, e);
        }
    }
    @Override
    public Integer findTeamVincitore(int hackathonId) {
        String sql = "SELECT " + COLUMN_TEAM_ID + " FROM valutazione WHERE hackathon_id = ? " +
                    "GROUP BY " + COLUMN_TEAM_ID + " ORDER BY AVG(voto) DESC LIMIT 1";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hackathonId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(COLUMN_TEAM_ID);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.findTeamVincitore" + ERROR_HACKATHON_ID + hackathonId, e);
        }
    }
    @Override
    public List<Integer> findTeamNonValutati(int giudiceId, int hackathonId) {
        String sql = "SELECT DISTINCT t.id FROM team t " +
                    "LEFT JOIN valutazione v ON t.id = v." + COLUMN_TEAM_ID + " AND v.giudice_id = ? " +
                    "WHERE t.hackathon_id = ? AND v.id IS NULL";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, giudiceId);
            stmt.setInt(2, hackathonId);
            List<Integer> teamNonValutati = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    teamNonValutati.add(rs.getInt("id"));
                }
            }
            return teamNonValutati;
        } catch (SQLException e) {
            throw new DataAccessException("ValutazionePostgresDAO.findTeamNonValutati giudiceId=" + giudiceId + ERROR_HACKATHON_ID + hackathonId, e);
        }
    }
    /**
     * Mappa un ResultSet in un oggetto Valutazione
     */
    private Valutazione mapResultSetToValutazione(ResultSet rs) throws SQLException {
        Valutazione valutazione = new Valutazione();
        valutazione.setId(rs.getInt(COLUMN_ID));
        valutazione.setGiudiceId(rs.getInt("giudice_id"));
        valutazione.setTeamId(rs.getInt(COLUMN_TEAM_ID));
        valutazione.setHackathonId(rs.getInt("hackathon_id"));
        valutazione.setVoto(rs.getInt("voto"));
        valutazione.setCommento(rs.getString("commento"));
        valutazione.setDataValutazione(rs.getTimestamp("data_valutazione").toLocalDateTime());
        return valutazione;
    }
} 
