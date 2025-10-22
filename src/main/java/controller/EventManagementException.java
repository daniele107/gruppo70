package controller;

/**
 * Eccezione dedicata per errori nella gestione degli eventi/hackathon.
 * Estende RuntimeException per evitare throws ridondanti nel controller.
 */
public class EventManagementException extends RuntimeException {
    
    /**
     * Costruttore con messaggio di errore
     * 
     * @param message Messaggio di errore
     */
    public EventManagementException(String message) {
        super(message);
    }
    
    /**
     * Costruttore con messaggio di errore e causa
     * 
     * @param message Messaggio di errore
     * @param cause Causa dell'errore
     */
    public EventManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
