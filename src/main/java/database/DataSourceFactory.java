package database;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;
/**
 * Factory per la creazione di DataSource per diversi database.
 * 
 * Classe di utilità che fornisce metodi statici per creare DataSource
 * configurati per PostgreSQL e altri database supportati.
 */
public final class DataSourceFactory {
    private static final Logger LOGGER = Logger.getLogger(DataSourceFactory.class.getName());
    // Costanti per le chiavi delle proprietà
    private static final String DB_URL_KEY = "db.url";
    private static final String DB_USERNAME_KEY = "db.username";
    private static final String DB_PASSWORD_KEY = "db.password";
    /**
     * Costruttore privato per impedire l'istanziazione della classe di utilità
     */
    private DataSourceFactory() {
        throw new UnsupportedOperationException("Classe di utilità");
    }
    /**
     * Crea un DataSource PostgreSQL con parametri specificati
     *
     * @param url URL di connessione PostgreSQL
     * @param username Nome utente per l'autenticazione
     * @param password Password per l'autenticazione
     * @return DataSource PostgreSQL configurato
     */
    public static DataSource postgres(String url, String username, String password) {
        // Implementazione temporanea senza dipendenze esterne
        // In produzione utilizzare PGSimpleDataSource
        return new SimpleDataSource(url, username, password);
    }
    /**
     * Crea un DataSource per il vendor specificato
     * 
     * @param vendor Tipo di database ("postgres", etc.)
     * @param url URL di connessione
     * @param username Nome utente per l'autenticazione
     * @param password Password per l'autenticazione
     * @return DataSource configurato per il vendor specificato
     * @throws UnsupportedOperationException se il vendor non è supportato
     */
    public static DataSource of(String vendor, String url, String username, String password) {
        if ("postgres".equalsIgnoreCase(vendor)) {
            return postgres(url, username, password);
        }
        throw new UnsupportedOperationException("Vendor non supportato: " + vendor);
    }
    /**
     * Crea un DataSource PostgreSQL configurato tramite properties
     * 
     * @return DataSource configurato
     * @throws DataAccessException se la configurazione fallisce
     */
    public static DataSource createDataSource() {
        Properties props = loadDatabaseProperties();
        String url = props.getProperty(DB_URL_KEY, "jdbc:postgresql://localhost:5432/hackathon_manager");
        String username = props.getProperty(DB_USERNAME_KEY, "postgres");
        String password = props.getProperty(DB_PASSWORD_KEY);
        if (password == null || password.trim().isEmpty()) {
            throw new DataAccessException("Password del database non configurata. Verificare db.properties o variabile d'ambiente DB_PASSWORD");
        }
        DataSource dataSource = postgres(url, username, password);
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "DataSource PostgreSQL configurato per: {0}", url);
        }
        return dataSource;
    }
    /**
     * Carica le proprietà del database dal file db.properties
     * 
     * @return Properties con la configurazione del database
     */
    private static Properties loadDatabaseProperties() {
        Properties props = new Properties();
        // Prova prima a caricare da file esterno
        try (InputStream input = DataSourceFactory.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                props.load(input);
                LOGGER.info("Configurazione database caricata da db.properties");
            } else {
                LOGGER.warning("File db.properties non trovato, uso configurazione di default");
            }
        } catch (IOException e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "Errore nel caricamento di db.properties: {0}. Utilizzando configurazione di default", e.getMessage());
            }
        }
        // Fallback su variabili d'ambiente se le proprietà non sono nel file
        loadPasswordFromEnvironment(props);
        return props;
    }
    /**
     * Carica la password da variabile d'ambiente se non presente nel file properties
     * 
     * @param props Properties da aggiornare
     */
    private static void loadPasswordFromEnvironment(Properties props) {
        if (props.getProperty(DB_PASSWORD_KEY) == null) {
            String envPassword = System.getenv("DB_PASSWORD");
            if (envPassword != null) {
                props.setProperty(DB_PASSWORD_KEY, envPassword);
                LOGGER.info("Password caricata da variabile d'ambiente DB_PASSWORD");
            }
        }
    }
}

/**
 * Implementazione semplice di DataSource per testing senza dipendenze esterne
 */
class SimpleDataSource implements DataSource {
    private final String url;
    private final String username;
    private final String password;

    public SimpleDataSource(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        // Not implemented
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        // Not implemented
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("Unwrap not supported");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
