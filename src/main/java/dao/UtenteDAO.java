package dao;
import model.Utente;
import java.util.List;
/**
 * Interfaccia per l'accesso ai dati degli Utenti.
 * Definisce tutti i metodi CRUD e le operazioni specifiche per la gestione degli utenti.
 */
public interface UtenteDAO {
    /**
     * Inserisce un nuovo utente nel database
     *
     * @param utente l'utente da inserire
     * @return l'ID dell'utente inserito
     */
    int insert(Utente utente);
    /**
     * Aggiorna un utente esistente nel database
     *
     * @param utente l'utente da aggiornare
     * @return true se l'aggiornamento è riuscito
     */
    boolean update(Utente utente);
    /**
     * Elimina un utente dal database
     *
     * @param id l'ID dell'utente da eliminare
     * @return true se l'eliminazione è riuscita
     */
    boolean delete(int id);
    /**
     * Trova un utente per ID
     *
     * @param id l'ID dell'utente
     * @return l'utente trovato o null se non esiste
     */
    Utente findById(int id);
    /**
     * Trova tutti gli utenti
     *
     * @return lista di tutti gli utenti
     */
    List<Utente> findAll();
    /**
     * Trova un utente per login
     *
     * @param login il login dell'utente
     * @return l'utente trovato o null se non esiste
     */
    Utente findByLogin(String login);
    /**
     * Trova un utente per email
     *
     * @param email l'email dell'utente
     * @return l'utente trovato o null se non esiste
     */
    Utente findByEmail(String email);
    /**
     * Verifica le credenziali di un utente
     *
     * @param login    il login dell'utente
     * @param password la password dell'utente
     * @return l'utente se le credenziali sono corrette, null altrimenti
     */
    Utente autentica(String login, String password);
    /**
     * Trova tutti gli organizzatori
     *
     * @return lista di tutti gli organizzatori
     */
    List<Utente> findOrganizzatori();
    /**
     * Trova tutti i giudici
     *
     * @return lista di tutti i giudici
     */
    List<Utente> findGiudici();
    /**
     * Trova tutti i partecipanti
     *
     * @return lista di tutti i partecipanti
     */
    List<Utente> findPartecipanti();
    /**
     * Trova utenti per ruolo
     *
     * @param ruolo il ruolo da cercare
     * @return lista di utenti con il ruolo specificato
     */
    List<Utente> findByRuolo(String ruolo);
    /**
     * Verifica se un login è già utilizzato
     *
     * @param login il login da verificare
     * @return true se il login è già utilizzato
     */
    boolean isLoginUtilizzato(String login);
    /**
     * Verifica se un'email è già utilizzata
     *
     * @param email l'email da verificare
     * @return true se l'email è già utilizzata
     */
    boolean isEmailUtilizzata(String email);
    /**
     * Cambia la password di un utente
     *
     * @param utenteId      l'ID dell'utente
     * @param nuovaPassword la nuova password
     * @return true se il cambio password è riuscito
     */
    boolean cambiaPassword(int utenteId, String nuovaPassword);
    /**
     * Aggiorna il ruolo di un utente
     *
     * @param utenteId l'ID dell'utente
     * @param nuovoRuolo il nuovo ruolo
     * @return true se l'aggiornamento è riuscito
     */
    boolean aggiornaRuolo(int utenteId, String nuovoRuolo);
    
    /**
     * Conta il numero di giudici attivi
     *
     * @return il numero di giudici attivi
     */
    int contaGiudiciAttivi();
} 
