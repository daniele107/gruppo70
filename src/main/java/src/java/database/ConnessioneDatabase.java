package src.java.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton per la connessione al database PostgreSQL "hackathon".
 */
public class ConnessioneDatabase {

    private static ConnessioneDatabase instance;
    private Connection connection;

    private final String url = "jdbc:postgresql://localhost:5432/hackathon";
    private final String user = "postgres";      // modifica se necessario
    private final String password = "postgres";  // modifica se necessario
    private final String driver = "org.postgresql.Driver";

    // Costruttore privato per il singleton
    private ConnessioneDatabase() throws SQLException {
        try {
            Class.forName(driver);  // Caricamento driver (utile se JDBC < 4)
            this.connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL non trovato", e);
        }
    }

    /**
     * Restituisce l'istanza singleton della classe.
     * Riapre la connessione se è chiusa.
     */
    public static ConnessioneDatabase getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new ConnessioneDatabase();
        }
        return instance;
    }

    /**
     * Restituisce la connessione attiva al database.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Chiude la connessione, se aperta.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Errore chiusura connessione: " + e.getMessage());
            }
        }
    }
}
