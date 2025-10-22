package dao;
import model.EventRequest;
/**
 * Interfaccia per l'accesso ai dati degli Eventi/Hackathon.
 * Estende le operazioni di HackathonDAO con funzionalità specifiche per EventRequest.
 */
public interface EventDAO {
    /**
     * Inserisce un nuovo evento dal una EventRequest
     *
     * @param request la richiesta di creazione evento
     * @param organizzatoreId l'ID dell'organizzatore
     * @return l'ID dell'evento inserito
     */
    long insertFromRequest(EventRequest request, int organizzatoreId);
    /**
     * Aggiorna un evento esistente da una EventRequest
     *
     * @param eventId l'ID dell'evento da aggiornare
     * @param request la richiesta con i nuovi dati
     * @param organizzatoreId l'ID dell'organizzatore (per validazione permessi)
     * @return true se l'aggiornamento è riuscito
     */
    boolean updateFromRequest(long eventId, EventRequest request, int organizzatoreId);
}
