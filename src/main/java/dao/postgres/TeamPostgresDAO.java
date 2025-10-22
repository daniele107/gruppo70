package dao.postgres;
import dao.TeamDAO;
import database.ConnectionManager;
import database.DataAccessException;
import model.Team;
import model.RichiestaJoin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
/**
 * Implementazione PostgreSQL dell'interfaccia TeamDAO.
 * Gestisce tutte le operazioni CRUD e specifiche per i team.
 * 
 * Refactored per dependency injection e transazioni per-connection.
 */
// Package naming convention now follows standard Java conventions
public class TeamPostgresDAO implements TeamDAO {
    // Costanti per i nomi delle colonne del database
    private static final String COLUMN_COUNT = "count";
    private static final String COLUMN_MEMBRI = "membri";
    private static final String COLUMN_UTENTE_ID = "utente_id";
    private static final String COLUMN_TEAM_ID = "team_id";
    // Costanti per i frammenti SQL
    private static final String SQL_SELECT_COUNT_AS = "SELECT COUNT(*) as ";
    // Costanti per i messaggi di errore
    private static final String ERROR_UTENTE_ID = "utenteId=";
    private static final String ERROR_TEAM_ID = "teamId=";
    private static final Logger LOGGER = Logger.getLogger(TeamPostgresDAO.class.getName());
    private final ConnectionManager cm;
    /**
     * Costruttore per dependency injection
     * 
     * @param connectionManager il ConnectionManager da utilizzare
     */
    public TeamPostgresDAO(ConnectionManager connectionManager) {
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
     * Esegue una query che restituisce un singolo valore intero
     */
    private int executeSingleIntQuery(PreparedStatement pstmt, String columnName) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(columnName);
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
     * Esegue una query che restituisce una lista di RichiestaJoin
     */
    private List<RichiestaJoin> executeRichiestaJoinListQuery(PreparedStatement pstmt) throws SQLException {
        List<RichiestaJoin> richieste = new ArrayList<>();
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                richieste.add(mapResultSetToRichiestaJoin(rs));
            }
        }
        return richieste;
    }
    /**
     * Esegue una query che restituisce una lista di Integer
     */
    private List<Integer> executeIntegerListQuery(PreparedStatement pstmt) throws SQLException {
        List<Integer> results = new ArrayList<>();
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                results.add(rs.getInt(1));
            }
        }
        return results;
    }
    /**
     * Esegue una query che restituisce un boolean basato su membri e dimensione massima
     */
    private boolean executeSingleBooleanQueryWithDimensione(PreparedStatement pstmt) throws SQLException {
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                int membri = rs.getInt(COLUMN_MEMBRI);
                int dimensioneMassima = rs.getInt("dimensione_massima");
                return membri >= dimensioneMassima;
            }
        }
        return false;
    }
    @Override
    public int insert(Team team) {
        String sql = "INSERT INTO team (nome, hackathon_id, capo_team_id, dimensione_massima) " +
                    "VALUES (?, ?, ?, ?) RETURNING id";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, team.getNome());
                pstmt.setInt(2, team.getHackathonId());
                pstmt.setInt(3, team.getCapoTeamId());
                pstmt.setInt(4, team.getDimensioneMassima());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        team.setId(id);
                        // Aggiungi automaticamente il capo team come membro
                        String insertMemberSql = "INSERT INTO team_members (team_id, utente_id) VALUES (?, ?)";
                        try (PreparedStatement memberStmt = conn.prepareStatement(insertMemberSql)) {
                            memberStmt.setInt(1, id);
                            memberStmt.setInt(2, team.getCapoTeamId());
                            memberStmt.executeUpdate();
                        }
                        return id;
                    }
                }
            }
            return -1;
        });
    }
    @Override
    public boolean update(Team team) {
        String sql = "UPDATE team SET nome = ?, hackathon_id = ?, capo_team_id = ?, " +
                    "dimensione_massima = ? WHERE id = ?";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, team.getNome());
                pstmt.setInt(2, team.getHackathonId());
                pstmt.setInt(3, team.getCapoTeamId());
                pstmt.setInt(4, team.getDimensioneMassima());
                pstmt.setInt(5, team.getId());
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM team WHERE id = ?";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }
    @Override
    public Team findById(int id) {
        String sql = "SELECT * FROM team WHERE id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTeam(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.findById id=" + id, e);
        }
        return null;
    }
    @Override
    public List<Team> findAll() {
        String sql = "SELECT * FROM team ORDER BY nome";
        List<Team> teams = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                teams.add(mapResultSetToTeam(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.findAll", e);
        }
        return teams;
    }
    @Override
    public List<Team> findByHackathon(int hackathonId) {
        String sql = "SELECT * FROM team WHERE hackathon_id = ? ORDER BY nome";
        List<Team> teams = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    teams.add(mapResultSetToTeam(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.findByHackathon id=" + hackathonId, e);
        }
        return teams;
    }
    @Override
    public List<Team> findByMembro(int utenteId) {
        String sql = "SELECT t.* FROM team t " +
                    "JOIN team_members tm ON t.id = tm.team_id " +
                    "WHERE tm.utente_id = ? ORDER BY t.nome";
        List<Team> teams = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, utenteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    teams.add(mapResultSetToTeam(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.findByMembro id=" + utenteId, e);
        }
        return teams;
    }
    @Override
    public List<Team> findByCapoTeam(int capoTeamId) {
        String sql = "SELECT * FROM team WHERE capo_team_id = ? ORDER BY nome";
        List<Team> teams = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, capoTeamId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    teams.add(mapResultSetToTeam(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.findByCapoTeam id=" + capoTeamId, e);
        }
        return teams;
    }
    @Override
    public boolean rimuoviMembro(int teamId, int utenteId) {
        String sql = "DELETE FROM team_members WHERE team_id = ? AND utente_id = ?";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, teamId);
                pstmt.setInt(2, utenteId);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }
    @Override
    public boolean aggiungiMembro(int teamId, int utenteId) {
        String sql = "INSERT INTO team_members (team_id, utente_id) VALUES (?, ?) " +
                    "ON CONFLICT (team_id, utente_id) DO NOTHING";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, teamId);
                pstmt.setInt(2, utenteId);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }
    @Override
    public int contaMembri(int teamId) {
        String sql = SQL_SELECT_COUNT_AS + COLUMN_MEMBRI + " FROM team_members WHERE team_id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            return executeSingleIntQuery(pstmt, COLUMN_MEMBRI);
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.contaMembri " + ERROR_TEAM_ID + teamId, e);
        }
    }
    public boolean haRaggiuntoLimiteMembri(int teamId) {
        String sql = SQL_SELECT_COUNT_AS + COLUMN_MEMBRI + ", dimensione_massima FROM team_members tm " +
                    "JOIN team t ON tm.team_id = t.id " +
                    "WHERE t.id = ? GROUP BY t.dimensione_massima";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            return executeSingleBooleanQueryWithDimensione(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.haRaggiuntoLimiteMembri " + ERROR_TEAM_ID + teamId, e);
        }
    }
    @Override
    public boolean isMembro(int teamId, int utenteId) {
        String sql = SQL_SELECT_COUNT_AS + COLUMN_COUNT + " FROM team_members WHERE team_id = ? AND utente_id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            pstmt.setInt(2, utenteId);
            return executeSingleBooleanQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.isMembro " + ERROR_TEAM_ID + teamId + " " + ERROR_UTENTE_ID + utenteId, e);
        }
    }
    @Override
    public boolean isCapoTeam(int teamId, int utenteId) {
        String sql = SQL_SELECT_COUNT_AS + COLUMN_COUNT + " FROM team WHERE id = ? AND capo_team_id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            pstmt.setInt(2, utenteId);
            return executeSingleBooleanQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.isCapoTeam " + ERROR_TEAM_ID + teamId + " " + ERROR_UTENTE_ID + utenteId, e);
        }
    }
    public boolean cambiaCapoTeam(int teamId, int nuovoCapoTeamId) {
        String sql = "UPDATE team SET capo_team_id = ? WHERE id = ?";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, nuovoCapoTeamId);
                pstmt.setInt(2, teamId);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }
    @Override
    public boolean rifiutaRichiestaJoin(int richiestaId) {
        // Implementazione semplificata: rimuove la richiesta di join
        // In un'implementazione completa, questo dovrebbe aggiornare lo stato della richiesta
        String sql = "DELETE FROM richiesta_join WHERE id = ?";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, richiestaId);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }
    @Override
    public boolean accettaRichiestaJoin(int richiestaId) {
        // Aggiorna lo stato della richiesta e aggiunge il membro al team in transazione
        return executeWithTransaction(conn -> {
            // Recupera la richiesta
            String selectSql = "SELECT utente_id, team_id FROM richiesta_join WHERE id = ?";
            try (PreparedStatement sel = conn.prepareStatement(selectSql)) {
                sel.setInt(1, richiestaId);
                try (ResultSet rs = sel.executeQuery()) {
                    if (!rs.next()) {
                        return false;
                    }
                    int utenteId = rs.getInt(COLUMN_UTENTE_ID);
                    int teamId = rs.getInt(COLUMN_TEAM_ID);
                    // Aggiorna lo stato a ACCETTATA
                    String updateSql = "UPDATE richiesta_join SET stato = 'ACCETTATA' WHERE id = ?";
                    try (PreparedStatement up = conn.prepareStatement(updateSql)) {
                        up.setInt(1, richiestaId);
                        up.executeUpdate();
                    }
                    // Aggiunge il membro al team (idempotente)
                    String addSql = "INSERT INTO team_members (team_id, utente_id) VALUES (?, ?) ON CONFLICT (team_id, utente_id) DO NOTHING";
                    try (PreparedStatement add = conn.prepareStatement(addSql)) {
                        add.setInt(1, teamId);
                        add.setInt(2, utenteId);
                        add.executeUpdate();
                    }
                    return true;
                }
            }
        });
    }
    @Override
    public int insertRichiestaJoin(RichiestaJoin richiesta) {
        String sql = "INSERT INTO richiesta_join (utente_id, team_id, messaggio_motivazionale, stato) VALUES (?, ?, ?, ?) RETURNING id";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, richiesta.getUtenteId());
                pstmt.setInt(2, richiesta.getTeamId());
                pstmt.setString(3, richiesta.getMessaggioMotivazionale());
                pstmt.setString(4, richiesta.getStato().toString());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        richiesta.setId(id);
                        return id;
                    }
                }
            }
            return -1;
        });
    }
    @Override
    public boolean updateRichiestaJoin(RichiestaJoin richiesta) {
        String sql = "UPDATE richiesta_join SET messaggio_motivazionale = ?, stato = ? WHERE id = ?";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, richiesta.getMessaggioMotivazionale());
                pstmt.setString(2, richiesta.getStato().toString());
                pstmt.setInt(3, richiesta.getId());
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }
    @Override
    public List<RichiestaJoin> findRichiesteJoin(int teamId) {
        String sql = "SELECT * FROM richiesta_join WHERE team_id = ? ORDER BY data_richiesta DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            return executeRichiestaJoinListQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.findRichiesteJoin fallita", e);
        }
    }
    @Override
    public List<RichiestaJoin> findRichiesteJoinInAttesa(int teamId) {
        String sql = "SELECT * FROM richiesta_join WHERE team_id = ? AND stato = 'IN_ATTESA' ORDER BY data_richiesta DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            return executeRichiestaJoinListQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.findRichiesteJoinInAttesa fallita", e);
        }
    }
    @Override
    public List<RichiestaJoin> findRichiesteJoinByUtente(int utenteId) {
        String sql = "SELECT * FROM richiesta_join WHERE utente_id = ? ORDER BY data_richiesta DESC";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, utenteId);
            return executeRichiestaJoinListQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.findRichiesteJoinByUtente fallita", e);
        }
    }
    @Override
    public List<Integer> findMembri(int teamId) {
        String sql = "SELECT utente_id FROM team_members WHERE team_id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            return executeIntegerListQuery(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.findMembri fallita", e);
        }
    }
    @Override
    public boolean haSpazioDisponibile(int teamId) {
        return !haRaggiuntoLimiteMembri(teamId);
    }
    /**
     * Mappa un ResultSet in un oggetto RichiestaJoin
     */
    private RichiestaJoin mapResultSetToRichiestaJoin(ResultSet rs) throws SQLException {
        RichiestaJoin richiesta = new RichiestaJoin(
            rs.getInt(COLUMN_UTENTE_ID),
            rs.getInt(COLUMN_TEAM_ID),
            rs.getString("messaggio_motivazionale")
        );
        richiesta.setId(rs.getInt("id"));
        richiesta.setStato(RichiestaJoin.StatoRichiesta.valueOf(rs.getString("stato")));
        richiesta.setDataRichiesta(rs.getTimestamp("data_richiesta").toLocalDateTime());
        return richiesta;
    }
    /**
     * Mappa un ResultSet in un oggetto Team
     *
     * @param rs il ResultSet da mappare
     * @return l'oggetto Team mappato
     * @throws SQLException se si verifica un errore durante la lettura
     */
    private Team mapResultSetToTeam(ResultSet rs) throws SQLException {
        Team team = new Team(
            rs.getString("nome"),
            rs.getInt("hackathon_id"),
            rs.getInt("capo_team_id"),
            rs.getInt("dimensione_massima")
        );
        team.setId(rs.getInt("id"));
        
        // Imposta i campi di definitivit√† se presenti
        try {
            team.setDefinitivo(rs.getBoolean("definitivo"));
            java.sql.Timestamp dataDefinizioneTs = rs.getTimestamp("data_definitivo");
            if (dataDefinizioneTs != null) {
                team.setDataDefinitivo(dataDefinizioneTs.toLocalDateTime());
            }
        } catch (SQLException e) {
            // Campi non presenti, ignora (per compatibilit√† con vecchie versioni)
        }
        
        return team;
    }
    
    @Override
    public int rendiDefinitiviTeamHackathon(int hackathonId) {
        String sql = "UPDATE team SET definitivo = TRUE, data_definitivo = CURRENT_TIMESTAMP WHERE hackathon_id = ? AND definitivo = FALSE";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            int teamsUpdated = pstmt.executeUpdate();
            LOGGER.log(Level.INFO, "Resi definitivi {0} team per hackathon ID: {1}", new Object[]{teamsUpdated, hackathonId});
            return teamsUpdated;
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.rendiDefinitiviTeamHackathon hackathonId=" + hackathonId, e);
        }
    }
    
    @Override
    public boolean isTeamDefinitivo(int teamId) {
        String sql = "SELECT definitivo FROM team WHERE id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("definitivo");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.isTeamDefinitivo teamId=" + teamId, e);
        }
        return false;
    }
    
    @Override
    public RichiestaJoin findRichiestaJoinById(int richiestaId) {
        String sql = "SELECT * FROM richiesta_join WHERE id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, richiestaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    RichiestaJoin richiesta = new RichiestaJoin(
                        rs.getInt(COLUMN_UTENTE_ID),
                        rs.getInt(COLUMN_TEAM_ID),
                        rs.getString("messaggio_motivazionale")
                    );
                    richiesta.setId(rs.getInt("id"));
                    richiesta.setDataRichiesta(rs.getTimestamp("data_richiesta").toLocalDateTime());

                    String statoStr = rs.getString("stato");
                    if (statoStr != null) {
                        richiesta.setStato(RichiestaJoin.StatoRichiesta.valueOf(statoStr));
                    }

                    return richiesta;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("TeamPostgresDAO.findRichiestaJoinById richiestaId=" + richiestaId, e);
        }
        return null;
    }
}
