package dao;

import model.AuditLog;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface per l'accesso ai dati del log di audit.
 */
public interface AuditLogDAO {
    
    /**
     * Inserisce una nuova entry di audit
     *
     * @param auditLog l'entry da inserire
     * @return l'ID dell'entry inserita o -1 se fallito
     */
    int insert(AuditLog auditLog);
    
    /**
     * Trova un'entry di audit per ID
     *
     * @param id l'ID dell'entry
     * @return l'entry o null se non trovata
     */
    AuditLog findById(int id);
    
    /**
     * Trova tutte le entry di audit per un utente
     *
     * @param utenteId l'ID dell'utente
     * @return lista delle entry
     */
    List<AuditLog> findByUtente(int utenteId);
    
    /**
     * Trova entry di audit per azione
     *
     * @param azione l'azione da cercare
     * @return lista delle entry
     */
    List<AuditLog> findByAzione(AuditLog.AuditAction azione);
    
    /**
     * Trova entry di audit per risorsa
     *
     * @param risorsa il tipo di risorsa
     * @param risorsaId l'ID della risorsa (opzionale)
     * @return lista delle entry
     */
    List<AuditLog> findByRisorsa(String risorsa, Integer risorsaId);
    
    /**
     * Trova entry di audit in un intervallo di tempo
     *
     * @param dataInizio data di inizio
     * @param dataFine data di fine
     * @return lista delle entry
     */
    List<AuditLog> findByDateRange(LocalDateTime dataInizio, LocalDateTime dataFine);
    
    /**
     * Trova entry di audit per risultato
     *
     * @param risultato il risultato dell'operazione
     * @return lista delle entry
     */
    List<AuditLog> findByRisultato(AuditLog.AuditResult risultato);
    
    /**
     * Trova entry di audit critiche (errori di sicurezza, fallimenti)
     *
     * @return lista delle entry critiche
     */
    List<AuditLog> findCritical();
    
    /**
     * Trova entry di audit con filtri combinati
     *
     * @param utenteId ID utente (opzionale)
     * @param azione azione (opzionale)
     * @param risorsa risorsa (opzionale)
     * @param risultato risultato (opzionale)
     * @param dataInizio data inizio (opzionale)
     * @param dataFine data fine (opzionale)
     * @param limit numero massimo risultati
     * @return lista delle entry
     */
    List<AuditLog> findWithFilters(Integer utenteId, AuditLog.AuditAction azione, 
                                  String risorsa, AuditLog.AuditResult risultato,
                                  LocalDateTime dataInizio, LocalDateTime dataFine, int limit);
    
    /**
     * Conta le entry di audit per un utente
     *
     * @param utenteId l'ID dell'utente
     * @return numero di entry
     */
    int countByUtente(int utenteId);
    
    /**
     * Conta le entry di audit per azione
     *
     * @param azione l'azione
     * @return numero di entry
     */
    int countByAzione(AuditLog.AuditAction azione);
    
    /**
     * Ottiene statistiche di audit per periodo
     *
     * @param dataInizio data di inizio
     * @param dataFine data di fine
     * @return mappa con statistiche
     */
    Map<String, Integer> getStatisticsByPeriod(LocalDateTime dataInizio, LocalDateTime dataFine);
    
    /**
     * Ottiene le azioni più frequenti
     *
     * @param limit numero massimo risultati
     * @return mappa azione -> conteggio
     */
    Map<AuditLog.AuditAction, Integer> getTopActions(int limit);
    
    /**
     * Ottiene gli utenti più attivi
     *
     * @param limit numero massimo risultati
     * @return mappa utenteId -> conteggio
     */
    Map<Integer, Integer> getTopUsers(int limit);
    
    /**
     * Elimina entry di audit più vecchie di un certo numero di giorni
     *
     * @param giorni numero di giorni di retention
     * @return numero di entry eliminate
     */
    int deleteOlderThan(int giorni);
    
    /**
     * Trova tutte le entry di audit (con paginazione)
     *
     * @param offset offset per la paginazione
     * @param limit numero massimo di risultati
     * @return lista delle entry
     */
    List<AuditLog> findAll(int offset, int limit);
    
    /**
     * Conta il numero totale di entry di audit
     *
     * @return numero totale di entry
     */
    int count();
}
