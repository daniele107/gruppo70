package dao;

import model.Notification;
import java.util.List;

/**
 * Data Access Object per la gestione delle notifiche.
 * Definisce le operazioni CRUD per le notifiche degli utenti.
 */
public interface NotificationDAO {
    
    /**
     * Inserisce una nuova notifica nel database
     * 
     * @param notification la notifica da inserire
     * @return l'ID della notifica inserita, -1 se l'inserimento fallisce
     */
    int insert(Notification notification);
    
    /**
     * Trova una notifica per ID
     * 
     * @param id l'ID della notifica
     * @return la notifica trovata o null se non esiste
     */
    Notification findById(int id);
    
    /**
     * Trova tutte le notifiche di un utente
     * 
     * @param utenteId l'ID dell'utente
     * @return lista delle notifiche dell'utente
     */
    List<Notification> findByUtente(int utenteId);
    
    /**
     * Trova tutte le notifiche non lette di un utente
     * 
     * @param utenteId l'ID dell'utente
     * @return lista delle notifiche non lette dell'utente
     */
    List<Notification> findUnreadByUtente(int utenteId);
    
    /**
     * Trova tutte le notifiche di un determinato tipo per un utente
     * 
     * @param utenteId l'ID dell'utente
     * @param tipo il tipo di notifica
     * @return lista delle notifiche del tipo specificato
     */
    List<Notification> findByUtenteAndType(int utenteId, Notification.NotificationType tipo);
    
    /**
     * Segna una notifica come letta
     * 
     * @param notificationId l'ID della notifica
     * @param utenteId l'ID dell'utente che legge la notifica
     * @return true se l'operazione è riuscita
     */
    boolean markAsRead(int notificationId, int utenteId);
    
    /**
     * Segna tutte le notifiche di un utente come lette
     * 
     * @param utenteId l'ID dell'utente
     * @return true se l'operazione è riuscita
     */
    boolean markAllAsRead(int utenteId);
    
    /**
     * Elimina una notifica
     * 
     * @param id l'ID della notifica da eliminare
     * @return true se l'eliminazione è riuscita
     */
    boolean delete(int id);
    
    /**
     * Elimina tutte le notifiche di un utente
     * 
     * @param utenteId l'ID dell'utente
     * @return true se l'eliminazione è riuscita
     */
    boolean deleteByUtente(int utenteId);
    
    /**
     * Conta le notifiche non lette di un utente
     * 
     * @param utenteId l'ID dell'utente
     * @return il numero di notifiche non lette
     */
    int countUnreadByUtente(int utenteId);
    
    /**
     * Trova tutte le notifiche del sistema (per amministratori)
     * 
     * @return lista di tutte le notifiche
     */
    List<Notification> findAll();
    
    /**
     * Elimina le notifiche più vecchie di un certo numero di giorni
     * 
     * @param days numero di giorni
     * @return numero di notifiche eliminate
     */
    int deleteOlderThan(int days);
}
