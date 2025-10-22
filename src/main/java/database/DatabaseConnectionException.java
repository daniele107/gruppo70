package database;
/**
 * Eccezione custom per problemi di connessione al database.
 * Fornisce contesto specifico per gli errori di connessione.
 */
public class DatabaseConnectionException extends Exception {
    /**
     * Costruttore con messaggio
     * 
     * @param message Messaggio di errore
     */
    public DatabaseConnectionException(String message) {
        super(message);
    }
    /**
     * Costruttore con messaggio e causa
     * 
     * @param message Messaggio di errore
     * @param cause Causa dell'errore
     */
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
