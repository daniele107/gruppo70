package database;
/**
 * Eccezione custom per problemi di configurazione del database.
 * Utilizzata quando la configurazione è mancante o non valida.
 */
public class DatabaseConfigurationException extends RuntimeException {
    /**
     * Costruttore con messaggio
     * 
     * @param message Messaggio di errore
     */
    public DatabaseConfigurationException(String message) {
        super(message);
    }
    /**
     * Costruttore con messaggio e causa
     * 
     * @param message Messaggio di errore
     * @param cause Causa dell'errore
     */
    public DatabaseConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
