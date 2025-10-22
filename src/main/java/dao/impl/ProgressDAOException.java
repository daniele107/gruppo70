package dao.impl;
/**
 * Eccezione personalizzata per le operazioni del ProgressDAO
 * Sostituisce l'uso di RuntimeException generico
 */
public class ProgressDAOException extends RuntimeException {
    /**
     * Costruttore con messaggio
     * 
     * @param message il messaggio di errore
     */
    public ProgressDAOException(String message) {
        super(message);
    }
    /**
     * Costruttore con messaggio e causa
     * 
     * @param message il messaggio di errore
     * @param cause la causa dell'errore
     */
    public ProgressDAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
