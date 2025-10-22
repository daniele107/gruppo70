package dao.postgres;
import dao.UtenteDAO;
import database.ConnectionManager;
import database.DataAccessException;
import model.Utente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Implementazione PostgreSQL dell'interfaccia UtenteDAO.
 * Gestisce tutte le operazioni CRUD e specifiche per gli utenti.
 * 
 * Refactored per dependency injection e transazioni per-connection.
 */
// Package naming convention now follows standard Java conventions
public class UtentePostgresDAO implements UtenteDAO {
    private final ConnectionManager cm;
    /**
     * Costruttore per dependency injection
     * 
     * @param connectionManager il ConnectionManager da utilizzare
     */
    public UtentePostgresDAO(ConnectionManager connectionManager) {
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
     * {@inheritDoc}
     * 
     * Implementazione PostgreSQL che utilizza una query INSERT con RETURNING
     * per ottenere l'ID generato automaticamente.
     */
    @Override
    public int insert(Utente utente) {
        String sql = "INSERT INTO utente (login, password, nome, cognome, email, ruolo) " +
                    "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, utente.getLogin());
                pstmt.setString(2, utente.getPassword());
                pstmt.setString(3, utente.getNome());
                pstmt.setString(4, utente.getCognome());
                pstmt.setString(5, utente.getEmail());
                pstmt.setString(6, utente.getRuolo());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        utente.setId(id);
                        return id;
                    }
                }
            }
            return -1;
        });
    }
    /**
     * {@inheritDoc}
     * 
     * Aggiorna tutti i campi dell'utente nel database utilizzando l'ID come chiave.
     */
    @Override
    public boolean update(Utente utente) {
        String sql = "UPDATE utente SET login = ?, password = ?, nome = ?, cognome = ?, " +
                    "email = ?, ruolo = ? WHERE id = ?";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, utente.getLogin());
                pstmt.setString(2, utente.getPassword());
                pstmt.setString(3, utente.getNome());
                pstmt.setString(4, utente.getCognome());
                pstmt.setString(5, utente.getEmail());
                pstmt.setString(6, utente.getRuolo());
                pstmt.setInt(7, utente.getId());
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }
    /**
     * {@inheritDoc}
     * 
     * Elimina l'utente dal database. L'operazione è irreversibile.
     */
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM utente WHERE id = ?";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }
    /**
     * {@inheritDoc}
     * 
     * Esegue una query SELECT per cercare l'utente con l'ID specificato.
     */
    @Override
    public Utente findById(int id) {
        String sql = "SELECT * FROM utente WHERE id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtente(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("UtentePostgresDAO.findById id=" + id, e);
        }
        return null;
    }
    /**
     * {@inheritDoc}
     * 
     * Recupera tutti gli utenti dal database ordinati per nome e cognome.
     */
    @Override
    public List<Utente> findAll() {
        String sql = "SELECT * FROM utente ORDER BY nome, cognome";
        List<Utente> utenti = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                utenti.add(mapResultSetToUtente(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("UtentePostgresDAO.findAll", e);
        }
        return utenti;
    }
    /**
     * {@inheritDoc}
     * 
     * Cerca un utente utilizzando il login come criterio di ricerca.
     */
    @Override
    public Utente findByLogin(String login) {
        String sql = "SELECT * FROM utente WHERE login = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtente(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("UtentePostgresDAO.findByLogin login", e);
        }
        return null;
    }
    /**
     * {@inheritDoc}
     * 
     * Cerca un utente utilizzando l'email come criterio di ricerca.
     */
    @Override
    public Utente findByEmail(String email) {
        String sql = "SELECT * FROM utente WHERE email = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtente(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("UtentePostgresDAO.findByEmail email", e);
        }
        return null;
    }
    /**
     * {@inheritDoc}
     * 
     * Verifica le credenziali dell'utente confrontando login e password.
     * NOTA: In un ambiente di produzione, le password dovrebbero essere hashate.
     */
    @Override
    public Utente autentica(String login, String password) {
        String sql = "SELECT * FROM utente WHERE login = ? AND password = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtente(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("UtentePostgresDAO.autentica login", e);
        }
        return null;
    }
    /**
     * {@inheritDoc}
     * 
     * Utilizza il metodo findByRuolo per trovare tutti gli organizzatori.
     */
    @Override
    public List<Utente> findOrganizzatori() {
        return findByRuolo("ORGANIZZATORE");
    }
    /**
     * {@inheritDoc}
     * 
     * Utilizza il metodo findByRuolo per trovare tutti i giudici.
     */
    @Override
    public List<Utente> findGiudici() {
        return findByRuolo("GIUDICE");
    }
    /**
     * {@inheritDoc}
     * 
     * Utilizza il metodo findByRuolo per trovare tutti i partecipanti.
     */
    @Override
    public List<Utente> findPartecipanti() {
        return findByRuolo("PARTECIPANTE");
    }
    /**
     * {@inheritDoc}
     * 
     * Cerca tutti gli utenti con il ruolo specificato, ordinati per nome e cognome.
     */
    @Override
    public List<Utente> findByRuolo(String ruolo) {
        String sql = "SELECT * FROM utente WHERE ruolo = ? ORDER BY nome, cognome";
        List<Utente> utenti = new ArrayList<>();
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ruolo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    utenti.add(mapResultSetToUtente(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("UtentePostgresDAO.findByRuolo ruolo=" + ruolo, e);
        }
        return utenti;
    }
    /**
     * {@inheritDoc}
     * 
     * Verifica l'esistenza del login utilizzando una query COUNT.
     */
    @Override
    public boolean isLoginUtilizzato(String login) {
        String sql = "SELECT COUNT(*) as count FROM utente WHERE login = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("UtentePostgresDAO.isLoginUtilizzato login", e);
        }
        return false;
    }
    /**
     * {@inheritDoc}
     * 
     * Verifica l'esistenza dell'email utilizzando una query COUNT.
     */
    @Override
    public boolean isEmailUtilizzata(String email) {
        String sql = "SELECT COUNT(*) as count FROM utente WHERE email = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("UtentePostgresDAO.isEmailUtilizzata email", e);
        }
        return false;
    }
    /**
     * {@inheritDoc}
     * 
     * Aggiorna la password dell'utente. In un ambiente di produzione
     * la password dovrebbe essere hashata prima del salvataggio.
     */
    @Override
    public boolean cambiaPassword(int utenteId, String nuovaPassword) {
        String sql = "UPDATE utente SET password = ? WHERE id = ?";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nuovaPassword);
                pstmt.setInt(2, utenteId);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }
    /**
     * {@inheritDoc}
     * 
     * Aggiorna il ruolo dell'utente nel database.
     */
    @Override
    public boolean aggiornaRuolo(int utenteId, String nuovoRuolo) {
        String sql = "UPDATE utente SET ruolo = ? WHERE id = ?";
        return executeWithTransaction(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nuovoRuolo);
                pstmt.setInt(2, utenteId);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        });
    }
    /**
     * Mappa un ResultSet in un oggetto Utente
     *
     * @param rs il ResultSet da mappare
     * @return l'oggetto Utente mappato
     * @throws SQLException se si verifica un errore durante la lettura
     */
    private Utente mapResultSetToUtente(ResultSet rs) throws SQLException {
        Utente utente = new Utente(
            rs.getString("login"),
            rs.getString("password"),
            rs.getString("nome"),
            rs.getString("cognome"),
            rs.getString("email"),
            rs.getString("ruolo")
        );
        utente.setId(rs.getInt("id"));
        return utente;
    }
    
    @Override
    public int contaGiudiciAttivi() {
        String sql = "SELECT COUNT(*) FROM utente WHERE ruolo = 'GIUDICE'";
        try (Connection conn = cm.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("UtentePostgresDAO.contaGiudiciAttivi", e);
        }
        return 0;
    }
}
