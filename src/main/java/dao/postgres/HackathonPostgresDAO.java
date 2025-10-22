package dao.postgres;
import dao.HackathonDAO;
import database.ConnectionManager;
import database.DataAccessException;
import model.Hackathon;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.function.Supplier;
/**
 * Implementazione PostgreSQL dell'interfaccia HackathonDAO.
 * Gestisce tutte le operazioni CRUD e specifiche per gli hackathon.
 * 
 * Refactored per dependency injection e transazioni per-connection.
 */
// Package naming convention now follows standard Java conventions
public class HackathonPostgresDAO implements HackathonDAO {
    private static final Logger LOGGER = Logger.getLogger(HackathonPostgresDAO.class.getName());
    private static final String MSG_NO_HACKATHON_FOUND = "Nessun hackathon trovato con ID: {0}";
    // Costanti riutilizzate per evitare duplicazione di stringhe
    private static final String SQL_ERROR_SUFFIX = ". SQL Error: ";
    private static final String LOG_PREFIX_ERROR_DURANTE = "Errore durante ";
    private final ConnectionManager cm;
    
    /**
     * Costruttore per dependency injection
     * 
     * @param connectionManager il ConnectionManager da utilizzare
     */
    public HackathonPostgresDAO(ConnectionManager connectionManager) {
        this.cm = connectionManager;
    }
    
    /**
     * Helper method for lazy logging with lambda expressions
     */
    private void logLazy(Level level, Supplier<String> messageSupplier) {
        if (LOGGER.isLoggable(level)) {
            LOGGER.log(level, messageSupplier.get());
        }
    }
    
    /**
     * Helper method for lazy logging with lambda expressions and exception
     */
    private void logLazy(Level level, Supplier<String> messageSupplier, Throwable throwable) {
        if (LOGGER.isLoggable(level)) {
            LOGGER.log(level, messageSupplier.get(), throwable);
        }
    }
    @Override
    public int insert(Hackathon hackathon) {
        String sql = "INSERT INTO hackathon (nome, data_inizio, data_fine, sede, is_virtuale, " +
                    "organizzatore_id, max_partecipanti, max_team, registrazioni_aperte, " +
                    "descrizione_problema, evento_avviato, evento_concluso) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try {
            return executeInsert(sql, hackathon);
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "inserimento hackathon '" + hackathon.getNome() + "': " + e.getMessage(), e);
            throw new DataAccessException("HackathonPostgresDAO.insert nome=" + hackathon.getNome() + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
    }
    private int executeInsert(String sql, Hackathon hackathon) throws SQLException {
        try (Connection conn = cm.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                setHackathonParameters(pstmt, hackathon);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        hackathon.setId(id);
                        conn.commit();
                        return id;
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "rollback insert hackathon: " + hackathon.getNome(), e);
                conn.rollback();
                throw new DataAccessException("Failed to execute insert operation for hackathon: " + hackathon.getNome(), e);
            }
        }
        return -1;
    }
    @Override
    public boolean update(Hackathon hackathon) {
        String sql = "UPDATE hackathon SET nome = ?, data_inizio = ?, data_fine = ?, sede = ?, " +
                    "is_virtuale = ?, organizzatore_id = ?, max_partecipanti = ?, max_team = ?, " +
                    "registrazioni_aperte = ?, descrizione_problema = ?, evento_avviato = ?, " +
                    "evento_concluso = ? WHERE id = ?";
        try {
            return executeUpdate(sql, hackathon);
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "aggiornamento hackathon ID " + hackathon.getId() + ": " + e.getMessage(), e);
            throw new DataAccessException("HackathonPostgresDAO.update id=" + hackathon.getId() + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
    }
    private boolean executeUpdate(String sql, Hackathon hackathon) throws SQLException {
        try (Connection conn = cm.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                setHackathonParameters(pstmt, hackathon);
                pstmt.setInt(13, hackathon.getId()); // Additional parameter for WHERE clause
                int rowsAffected = pstmt.executeUpdate();
                conn.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "rollback update hackathon ID: " + hackathon.getId(), e);
                conn.rollback();
                throw new DataAccessException("Failed to execute update operation for hackathon ID: " + hackathon.getId(), e);
            }
        }
    }
    private void setHackathonParameters(PreparedStatement pstmt, Hackathon hackathon) throws SQLException {
        pstmt.setString(1, hackathon.getNome());
        pstmt.setTimestamp(2, Timestamp.valueOf(hackathon.getDataInizio()));
        pstmt.setTimestamp(3, Timestamp.valueOf(hackathon.getDataFine()));
        pstmt.setString(4, hackathon.getSede());
        pstmt.setBoolean(5, hackathon.isVirtuale());
        pstmt.setInt(6, hackathon.getOrganizzatoreId());
        pstmt.setInt(7, hackathon.getMaxPartecipanti());
        pstmt.setInt(8, hackathon.getMaxTeam());
        pstmt.setBoolean(9, hackathon.isRegistrazioniAperte());
        pstmt.setString(10, hackathon.getDescrizioneProblema());
        pstmt.setBoolean(11, hackathon.isEventoAvviato());
        pstmt.setBoolean(12, hackathon.isEventoConcluso());
    }
    private boolean executeSimpleUpdate(String sql, int id) throws SQLException {
        try (Connection conn = cm.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                conn.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "rollback simple update ID: " + id, e);
                conn.rollback();
                throw new DataAccessException("Failed to execute simple update operation for ID: " + id, e);
            }
        }
    }
    private boolean executeAvviaHackathon(String sql, String descrizioneProblema, int hackathonId) throws SQLException {
        try (Connection conn = cm.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, descrizioneProblema);
                pstmt.setInt(2, hackathonId);
                int rowsAffected = pstmt.executeUpdate();
                conn.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "rollback avvia hackathon ID: " + hackathonId, e);
                conn.rollback();
                throw new DataAccessException("Failed to execute avvia hackathon operation for ID: " + hackathonId, e);
            }
        }
    }
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM hackathon WHERE id = ?";
        try {
            return executeDelete(sql, id);
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "eliminazione hackathon ID " + id + ": " + e.getMessage(), e);
            throw new DataAccessException("HackathonPostgresDAO.delete id=" + id + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
    }
    private boolean executeDelete(String sql, int id) throws SQLException {
        try (Connection conn = cm.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                conn.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "rollback delete ID: " + id, e);
                conn.rollback();
                throw new DataAccessException("Failed to execute delete operation for ID: " + id, e);
            }
        }
    }
    @Override
    public Hackathon findById(int id) {
        String sql = "SELECT * FROM hackathon WHERE id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToHackathon(rs);
                }
            }
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "ricerca hackathon ID " + id + ": " + e.getMessage(), e);
            throw new DataAccessException("HackathonPostgresDAO.findById id=" + id + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
        return null;
    }
    @Override
    public List<Hackathon> findAll() {
        String sql = "SELECT * FROM hackathon ORDER BY data_inizio DESC";
        List<Hackathon> hackathons = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Hackathon h = mapResultSetToHackathon(rs);
                hackathons.add(h);
            }
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "retrieve all hackathons", e);
            throw new DataAccessException("Failed to retrieve all hackathons from database" + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
        return hackathons;
    }
    @Override
    public List<Hackathon> findByOrganizzatore(int organizzatoreId) {
        String sql = "SELECT * FROM hackathon WHERE organizzatore_id = ? ORDER BY data_inizio DESC";
        List<Hackathon> hackathons = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, organizzatoreId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    hackathons.add(mapResultSetToHackathon(rs));
                }
            }
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "retrieve hackathons for organizer ID: " + organizzatoreId, e);
            throw new DataAccessException("Failed to retrieve hackathons for organizer ID: " + organizzatoreId + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
        return hackathons;
    }
    @Override
    public List<Hackathon> findConRegistrazioniAperte() {
        String sql = "SELECT * FROM hackathon WHERE registrazioni_aperte = true ORDER BY data_inizio";
        List<Hackathon> hackathons = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                hackathons.add(mapResultSetToHackathon(rs));
            }
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "retrieve hackathons with open registrations", e);
            throw new DataAccessException("Failed to retrieve hackathons with open registrations from database" + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
        return hackathons;
    }
    @Override
    public List<Hackathon> findInCorso() {
        String sql = "SELECT * FROM hackathon WHERE evento_avviato = true AND evento_concluso = false " +
                    "AND data_inizio <= NOW() AND data_fine >= NOW() ORDER BY data_inizio";
        List<Hackathon> hackathons = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                hackathons.add(mapResultSetToHackathon(rs));
            }
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "retrieve hackathons in progress", e);
            throw new DataAccessException("Failed to retrieve hackathons currently in progress from database" + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
        return hackathons;
    }
    @Override
    public List<Hackathon> findConclusi() {
        String sql = "SELECT * FROM hackathon WHERE evento_concluso = true ORDER BY data_fine DESC";
        List<Hackathon> hackathons = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                hackathons.add(mapResultSetToHackathon(rs));
            }
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "retrieve concluded hackathons", e);
            throw new DataAccessException("Failed to retrieve concluded hackathons from database" + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
        return hackathons;
    }
    @Override
    public boolean apriRegistrazioni(int hackathonId) {
        String sql = "UPDATE hackathon SET registrazioni_aperte = true WHERE id = ?";
        try {
            return executeSimpleUpdate(sql, hackathonId);
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "apertura registrazioni hackathon ID " + hackathonId + ": " + e.getMessage(), e);
            throw new DataAccessException("HackathonPostgresDAO.apriRegistrazioni id=" + hackathonId + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
    }
    @Override
    public boolean chiudiRegistrazioni(int hackathonId) {
        String sql = "UPDATE hackathon SET registrazioni_aperte = false WHERE id = ?";
        try {
            return executeSimpleUpdate(sql, hackathonId);
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "chiusura registrazioni hackathon ID " + hackathonId + ": " + e.getMessage(), e);
            throw new DataAccessException("HackathonPostgresDAO.chiudiRegistrazioni id=" + hackathonId + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
    }
    @Override
    public boolean avviaHackathon(int hackathonId, String descrizioneProblema) {
        String sql = "UPDATE hackathon SET evento_avviato = true, descrizione_problema = ? WHERE id = ?";
        try {
            return executeAvviaHackathon(sql, descrizioneProblema, hackathonId);
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "avvio hackathon ID " + hackathonId + ": " + e.getMessage(), e);
            throw new DataAccessException("HackathonPostgresDAO.avviaHackathon id=" + hackathonId + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
    }
    @Override
    public boolean concludeHackathon(int hackathonId) {
        String sql = "UPDATE hackathon SET evento_concluso = true WHERE id = ?";
        try {
            return executeSimpleUpdate(sql, hackathonId);
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "conclusione hackathon ID " + hackathonId + ": " + e.getMessage(), e);
            throw new DataAccessException("HackathonPostgresDAO.concludeHackathon id=" + hackathonId + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
    }
    @Override
    public boolean haRaggiuntoLimitePartecipanti(int hackathonId) {
        String sql = "SELECT COUNT(*) as partecipanti, max_partecipanti FROM registrazione r " +
                    "JOIN hackathon h ON r.hackathon_id = h.id " +
                    "WHERE h.id = ? AND r.confermata = true GROUP BY h.max_partecipanti";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int partecipanti = rs.getInt("partecipanti");
                    int maxPartecipanti = rs.getInt("max_partecipanti");
                    return partecipanti >= maxPartecipanti;
                }
            }
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "verifica limite partecipanti hackathon ID " + hackathonId + ": " + e.getMessage(), e);
            throw new DataAccessException("HackathonPostgresDAO.haRaggiuntoLimitePartecipanti id=" + hackathonId + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
        return false;
    }
    @Override
    public boolean haRaggiuntoLimiteTeam(int hackathonId) {
        String sql = "SELECT COUNT(*) as team, max_team FROM team t " +
                    "JOIN hackathon h ON t.hackathon_id = h.id " +
                    "WHERE h.id = ? GROUP BY h.max_team";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int team = rs.getInt("team");
                    int maxTeam = rs.getInt("max_team");
                    return team >= maxTeam;
                }
            }
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "verifica limite team hackathon ID " + hackathonId + ": " + e.getMessage(), e);
            throw new DataAccessException("HackathonPostgresDAO.haRaggiuntoLimiteTeam id=" + hackathonId + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
        return false;
    }
    @Override
    public int contaPartecipanti(int hackathonId) {
        String sql = "SELECT COUNT(*) as partecipanti FROM registrazione " +
                    "WHERE hackathon_id = ? AND confermata = true";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("partecipanti");
                }
            }
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "conteggio partecipanti hackathon ID " + hackathonId + ": " + e.getMessage(), e);
            throw new DataAccessException("HackathonPostgresDAO.contaPartecipanti id=" + hackathonId + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
        return 0;
    }
    @Override
    public int contaTeam(int hackathonId) {
        String sql = "SELECT COUNT(*) as team FROM team WHERE hackathon_id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("team");
                }
            }
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "conteggio team hackathon ID " + hackathonId + ": " + e.getMessage(), e);
            throw new DataAccessException("HackathonPostgresDAO.contaTeam id=" + hackathonId + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
        return 0;
    }
    /**
     * Mappa un ResultSet in un oggetto Hackathon
     *
     * @param rs il ResultSet da mappare
     * @return l'oggetto Hackathon mappato
     * @throws SQLException se si verifica un errore durante la lettura
     */
    private Hackathon mapResultSetToHackathon(ResultSet rs) throws SQLException {
        Hackathon hackathon = new Hackathon(
            rs.getString("nome"),
            rs.getTimestamp("data_inizio").toLocalDateTime(),
            rs.getString("sede"),
            rs.getBoolean("is_virtuale"),
            rs.getInt("organizzatore_id"),
            rs.getInt("max_partecipanti"),
            rs.getInt("max_team")
        );
        hackathon.setId(rs.getInt("id"));
        hackathon.setDataFine(rs.getTimestamp("data_fine").toLocalDateTime());
        hackathon.setRegistrazioniAperte(rs.getBoolean("registrazioni_aperte"));
        hackathon.setDescrizioneProblema(rs.getString("descrizione_problema"));
        hackathon.setEventoAvviato(rs.getBoolean("evento_avviato"));
        hackathon.setEventoConcluso(rs.getBoolean("evento_concluso"));
        return hackathon;
    }
    @Override
    public boolean avviaEvento(int hackathonId) {
        String sql = "UPDATE hackathon SET evento_avviato = TRUE WHERE id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logLazy(Level.INFO, () -> "Hackathon avviato con successo - ID: " + hackathonId);
                return true;
            } else {
                LOGGER.log(Level.WARNING, MSG_NO_HACKATHON_FOUND, hackathonId);
                return false;
            }
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "avvio evento hackathon ID " + hackathonId + ": " + e.getMessage(), e);
            throw new DataAccessException("HackathonPostgresDAO.avviaEvento id=" + hackathonId + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
    }
    @Override
    public boolean concludeEvento(int hackathonId) {
        String sql = "UPDATE hackathon SET evento_concluso = TRUE WHERE id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logLazy(Level.INFO, () -> "Hackathon concluso con successo - ID: " + hackathonId);
                return true;
            } else {
                LOGGER.log(Level.WARNING, MSG_NO_HACKATHON_FOUND, hackathonId);
                return false;
            }
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "conclusione evento hackathon ID " + hackathonId + ": " + e.getMessage(), e);
            throw new DataAccessException("HackathonPostgresDAO.concludeEvento id=" + hackathonId + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean pubblicaClassifiche(int hackathonId) {
        String sql = "UPDATE hackathon SET classifiche_pubblicate = TRUE WHERE id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hackathonId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logLazy(Level.INFO, () -> "Classifiche pubblicate per hackathon ID: " + hackathonId);
                return true;
            } else {
                LOGGER.log(Level.WARNING, MSG_NO_HACKATHON_FOUND, hackathonId);
                return false;
            }
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "pubblicazione classifiche hackathon ID " + hackathonId + ": " + e.getMessage(), e);
            throw new DataAccessException("HackathonPostgresDAO.pubblicaClassifiche id=" + hackathonId + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
    }

    /**
     * Elimina tutti gli hackathon conclusi. Grazie a ON DELETE CASCADE su FK delle tabelle
     * figlie, verranno rimossi anche team/registrazioni/progress/valutazioni collegati.
     *
     * @return numero di hackathon eliminati
     */
    @Override
    public int deleteConclusi() {
        String countSql = "SELECT COUNT(*) FROM hackathon WHERE evento_concluso = TRUE";
        String deleteSql = "DELETE FROM hackathon WHERE evento_concluso = TRUE";

        try (Connection conn = cm.getConnection()) {
            conn.setAutoCommit(false); // Inizia transazione

            try {
                // Prima conta quanti sono
                int count;
                try (PreparedStatement pstmt = conn.prepareStatement(countSql);
                     ResultSet rs = pstmt.executeQuery()) {
                    count = rs.next() ? rs.getInt(1) : 0;
                }

                logLazy(Level.INFO, () -> "Trovati " + count + " hackathon conclusi da eliminare");

                if (count == 0) {
                    conn.rollback();
                    LOGGER.log(Level.INFO, "Nessun hackathon concluso trovato");
                    return 0;
                }

                // Elimina in ordine per evitare vincoli di foreign key
                int totalDeleted = 0;

                // 1. Elimina record correlati dalle tabelle figlie
                String deleteValutazioni = "DELETE FROM valutazione WHERE hackathon_id IN (SELECT id FROM hackathon WHERE evento_concluso = TRUE)";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteValutazioni)) {
                    int deleted = pstmt.executeUpdate();
                    logLazy(Level.INFO, () -> "Eliminati " + deleted + " record dalla tabella valutazione");
                }

                    // 2. Elimina progress (deve essere fatto PRIMA dei team!)
                    // Elimina progress che appartengono a team di hackathon conclusi
                    String deleteProgress = "DELETE FROM progress WHERE team_id IN (SELECT id FROM team WHERE hackathon_id IN (SELECT id FROM hackathon WHERE evento_concluso = TRUE))";
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteProgress)) {
                        int deleted = pstmt.executeUpdate();
                        logLazy(Level.INFO, () -> "Eliminati " + deleted + " record dalla tabella progress");
                    }

                    // 3. Elimina registrazioni
                    String deleteRegistrazioni = "DELETE FROM registrazione WHERE hackathon_id IN (SELECT id FROM hackathon WHERE evento_concluso = TRUE)";
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteRegistrazioni)) {
                        int deleted = pstmt.executeUpdate();
                        logLazy(Level.INFO, () -> "Eliminati " + deleted + " record dalla tabella registrazione");
                    }

                    // 4. Elimina richieste join prima dei team
                    String deleteRichiesteJoin = "DELETE FROM richiesta_join WHERE team_id IN (SELECT id FROM team WHERE hackathon_id IN (SELECT id FROM hackathon WHERE evento_concluso = TRUE))";
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteRichiesteJoin)) {
                        int deleted = pstmt.executeUpdate();
                        logLazy(Level.INFO, () -> "Eliminati " + deleted + " record dalla tabella richiesta_join");
                    }

                    // 5. Elimina documents PRIMA dei team (vincolo documents_team_id_fkey)
                    String deleteDocuments = "DELETE FROM documents WHERE team_id IN (SELECT id FROM team WHERE hackathon_id IN (SELECT id FROM hackathon WHERE evento_concluso = TRUE))";
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteDocuments)) {
                        int deleted = pstmt.executeUpdate();
                        logLazy(Level.INFO, () -> "Eliminati " + deleted + " record dalla tabella documents");
                    }

                    // 6. Elimina team
                    String deleteTeam = "DELETE FROM team WHERE hackathon_id IN (SELECT id FROM hackathon WHERE evento_concluso = TRUE)";
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteTeam)) {
                        int deleted = pstmt.executeUpdate();
                        logLazy(Level.INFO, () -> "Eliminati " + deleted + " record dalla tabella team");
                    }

                // 7. Elimina audit log correlati
                String deleteAuditLog = "DELETE FROM audit_log WHERE risorsa = 'HACKATHON' AND risorsa_id IN (SELECT id FROM hackathon WHERE evento_concluso = TRUE)";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteAuditLog)) {
                    int deleted = pstmt.executeUpdate();
                    logLazy(Level.INFO, () -> "Eliminati " + deleted + " record dalla tabella audit_log");
                }

                // 8. Finalmente elimina gli hackathon
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                    totalDeleted = pstmt.executeUpdate();
                    final int finalTotalDeleted = totalDeleted;
                    logLazy(Level.INFO, () -> "Eliminati " + finalTotalDeleted + " hackathon conclusi");
                }

                conn.commit(); // Commit della transazione
                return totalDeleted;

            } catch (SQLException e) {
                conn.rollback(); // Rollback in caso di errore
                logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "deleteConclusi operation", e);
                throw new DataAccessException("HackathonPostgresDAO.deleteConclusi failed" + SQL_ERROR_SUFFIX + e.getMessage(), e);
            }
        } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "deleteConclusi operation", e);
            throw new DataAccessException("HackathonPostgresDAO.deleteConclusi failed" + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
    }
    /**
     * Pulisce lo stato del database e resetta eventuali transazioni interrotte
     * Questo metodo può essere utile per ripristinare lo stato del database
     * dopo un errore di transazione
     */
    public void cleanupDatabaseState() {
        LOGGER.info("Iniziando cleanup dello stato del database");

        try (Connection conn = cm.getConnection()) {
            cleanupConnectionState(conn);
            LOGGER.info("Database state cleaned up successfully");
            } catch (SQLException e) {
            logLazy(Level.SEVERE, () -> LOG_PREFIX_ERROR_DURANTE + "cleanup database state", e);
            throw new DataAccessException(LOG_PREFIX_ERROR_DURANTE + "cleanup database state" + SQL_ERROR_SUFFIX + e.getMessage(), e);
        }
    }

    /**
     * Pulisce lo stato della connessione
     */
    private void cleanupConnectionState(Connection conn) throws SQLException {
        // Controlla se c'è una transazione aperta
        if (!conn.getAutoCommit()) {
            try {
                // Prova a fare rollback
                conn.rollback();
                LOGGER.info("Rollback eseguito durante cleanup");
            } catch (SQLException rollbackEx) {
                throw new DataAccessException(LOG_PREFIX_ERROR_DURANTE + "rollback in cleanup" + SQL_ERROR_SUFFIX + rollbackEx.getMessage(), rollbackEx);
            }
        }

        // Ripristina sempre l'auto-commit
        conn.setAutoCommit(true);
    }
}
