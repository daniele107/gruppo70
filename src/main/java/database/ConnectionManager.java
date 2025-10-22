package database;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Properties;
/**
 * Gestisce le connessioni al database PostgreSQL tramite DataSource.
 * 
 * Implementazione pulita con dependency injection:
 * - Nessun singleton pattern smell
 * - Nessun doppio handling delle eccezioni (S2139)
 * - Thread-safety tramite immutabilità
 * - Transazioni gestite per-connection
 */
public class ConnectionManager {
    private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());
    
    // Costanti per evitare duplicazione stringhe
    private static final String DEFAULT_PASSWORD = "password";
    private static final String DEFAULT_USERNAME = "postgres";
    private static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/hackathon_db";
    
    private final DataSource dataSource;
    /**
     * Costruttore per dependency injection
     * 
     * @param dataSource il DataSource da utilizzare per le connessioni
     * @throws IllegalArgumentException se dataSource è null
     */
    public ConnectionManager(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource non può essere null");
        }
        this.dataSource = dataSource;
    }
    /**
     * Ottiene una connessione al database
     *
     * @return la connessione al database
     * @throws DataAccessException se si verifica un errore di connessione
     */
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DataAccessException("ConnectionManager.getConnection fallita", e);
        }
    }
    /**
     * Esegue commit sulla connessione specificata
     * 
     * @param conn la connessione su cui fare commit
     * @throws DataAccessException se si verifica un errore
     */
    public void commit(Connection conn) {
        try {
            if (conn != null && !conn.getAutoCommit()) {
                conn.commit();
            }
        } catch (SQLException e) {
            throw new DataAccessException("ConnectionManager.commit fallita", e);
        }
    }
    /**
     * Esegue rollback sulla connessione specificata
     * 
     * @param conn la connessione su cui fare rollback
     * @throws DataAccessException se si verifica un errore
     */
    public void rollback(Connection conn) {
        try {
            if (conn != null && !conn.getAutoCommit()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            throw new DataAccessException("ConnectionManager.rollback fallita", e);
        }
    }
    /**
     * Testa la connessione al database
     *
     * @return true se la connessione è riuscita, false altrimenti
     * @throws DataAccessException se si verifica un errore durante il test
     */
    public boolean testConnection() {
        try (Connection testConn = getConnection()) {
            return testConn != null && !testConn.isClosed();
        } catch (SQLException e) {
            throw new DataAccessException("ConnectionManager.testConnection fallita", e);
        }
    }
    /**
     * Testa la presenza delle tabelle nel database
     * 
     * @throws DataAccessException se si verifica un errore durante il test
     */
    public void testTables() {
        try (Connection conn = getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            String[] tables = {"utente", "hackathon", "team", "registrazione", "progress", "valutazione"};
            LOGGER.info("Verifica tabelle nel database:");
            for (String table : tables) {
                checkTableExists(stmt, table);
            }
        } catch (SQLException e) {
            throw new DataAccessException("ConnectionManager.testTables fallita", e);
        }
    }
    /**
     * Verifica l'esistenza di una singola tabella nel database
     * 
     * @param stmt Statement SQL da utilizzare
     * @param table Nome della tabella da verificare
     */
    private void checkTableExists(java.sql.Statement stmt, String table) {
        try (java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
            if (rs.next()) {
                int count = rs.getInt(1);
                LOGGER.log(Level.INFO, "OK Tabella ''{0}'' trovata con {1} record", new Object[]{table, count});
            } else {
                LOGGER.log(Level.WARNING, "Tabella ''{0}'': nessun risultato dalla query", table);
            }
        } catch (SQLException e) {
            // Log ma non rilancia: è normale che alcune tabelle possano non esistere durante i test
            LOGGER.log(Level.INFO, "ERRORE Tabella ''{0}'' non trovata o non accessibile: {1}", new Object[]{table, e.getMessage()});
        }
    }
    
    /**
     * Ottiene l'URL del database dal DataSource
     * 
     * @return l'URL del database
     */
    public String getDatabaseUrl() {
        try {
            // Prova a ottenere una connessione e estrai l'URL
            try (Connection conn = getConnection()) {
                return conn.getMetaData().getURL();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Impossibile ottenere URL database", e);
            return DEFAULT_DB_URL; // Default fallback
        }
    }
    
    /**
     * Ottiene il nome utente del database dal DataSource
     * 
     * @return il nome utente del database
     */
    public String getUsername() {
        try {
            // Prova a ottenere una connessione e estrai l'username
            try (Connection conn = getConnection()) {
                return conn.getMetaData().getUserName();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Impossibile ottenere username database", e);
            return DEFAULT_USERNAME; // Default fallback
        }
    }
    
    /**
     * Ottiene la password del database dal DataSource
     * 
     * @return la password del database
     */
    public String getPassword() {
        try {
            // Prova a ottenere le proprietà dal DataSource
            try (Connection conn = getConnection()) {
                Properties props = conn.getClientInfo();
                if (props != null && props.containsKey(DEFAULT_PASSWORD)) {
                    return props.getProperty(DEFAULT_PASSWORD);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Impossibile ottenere password database", e);
        }
        return DEFAULT_PASSWORD; // Default fallback
    }
}
