package dao;
import model.Hackathon;
import java.util.List;
/**
 * Interfaccia per l'accesso ai dati degli Hackathon.
 * Definisce tutti i metodi CRUD e le operazioni specifiche per la gestione degli hackathon.
 */
public interface HackathonDAO {
    /**
     * Inserisce un nuovo hackathon nel database
     *
     * @param hackathon l'hackathon da inserire
     * @return l'ID dell'hackathon inserito
     */
    int insert(Hackathon hackathon);
    /**
     * Aggiorna un hackathon esistente nel database
     *
     * @param hackathon l'hackathon da aggiornare
     * @return true se l'aggiornamento è riuscito
     */
    boolean update(Hackathon hackathon);
    /**
     * Elimina un hackathon dal database
     *
     * @param id l'ID dell'hackathon da eliminare
     * @return true se l'eliminazione è riuscita
     */
    boolean delete(int id);
    /**
     * Trova un hackathon per ID
     *
     * @param id l'ID dell'hackathon
     * @return l'hackathon trovato o null se non esiste
     */
    Hackathon findById(int id);
    /**
     * Trova tutti gli hackathon
     *
     * @return lista di tutti gli hackathon
     */
    List<Hackathon> findAll();
    /**
     * Trova gli hackathon organizzati da un utente specifico
     *
     * @param organizzatoreId l'ID dell'organizzatore
     * @return lista degli hackathon organizzati
     */
    List<Hackathon> findByOrganizzatore(int organizzatoreId);
    /**
     * Trova gli hackathon con registrazioni aperte
     *
     * @return lista degli hackathon con registrazioni aperte
     */
    List<Hackathon> findConRegistrazioniAperte();
    /**
     * Trova gli hackathon in corso
     *
     * @return lista degli hackathon attualmente in corso
     */
    List<Hackathon> findInCorso();
    /**
     * Trova gli hackathon conclusi
     *
     * @return lista degli hackathon conclusi
     */
    List<Hackathon> findConclusi();
    /**
     * Apre le registrazioni per un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @return true se l'operazione è riuscita
     */
    boolean apriRegistrazioni(int hackathonId);
    /**
     * Chiude le registrazioni per un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @return true se l'operazione è riuscita
     */
    boolean chiudiRegistrazioni(int hackathonId);
    /**
     * Avvia un hackathon (pubblica il problema)
     *
     * @param hackathonId l'ID dell'hackathon
     * @param descrizioneProblema la descrizione del problema
     * @return true se l'operazione è riuscita
     */
    boolean avviaHackathon(int hackathonId, String descrizioneProblema);
    /**
     * Conclude un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @return true se l'operazione è riuscita
     */
    boolean concludeHackathon(int hackathonId);
    /**
     * Verifica se un hackathon ha raggiunto il numero massimo di partecipanti
     *
     * @param hackathonId l'ID dell'hackathon
     * @return true se ha raggiunto il limite
     */
    boolean haRaggiuntoLimitePartecipanti(int hackathonId);
    /**
     * Verifica se un hackathon ha raggiunto il numero massimo di team
     *
     * @param hackathonId l'ID dell'hackathon
     * @return true se ha raggiunto il limite
     */
    boolean haRaggiuntoLimiteTeam(int hackathonId);
    /**
     * Conta il numero di partecipanti registrati ad un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @return il numero di partecipanti
     */
    int contaPartecipanti(int hackathonId);
    /**
     * Conta il numero di team creati per un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @return il numero di team
     */
    int contaTeam(int hackathonId);
    /**
     * Avvia un hackathon (imposta evento_avviato = true)
     *
     * @param hackathonId l'ID dell'hackathon da avviare
     * @return true se l'evento è stato avviato
     */
    boolean avviaEvento(int hackathonId);
    /**
     * Conclude un hackathon (imposta evento_concluso = true)
     *
     * @param hackathonId l'ID dell'hackathon da concludere
     * @return true se l'evento è stato concluso
     */
    boolean concludeEvento(int hackathonId);
    
    /**
     * Pubblica le classifiche di un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @return true se le classifiche sono state pubblicate
     */
    boolean pubblicaClassifiche(int hackathonId);

    /**
     * Elimina tutti gli hackathon conclusi (evento_concluso = TRUE).
     *
     * @return numero di hackathon eliminati
     */
    int deleteConclusi();

    /**
     * Pulisce lo stato del database e resetta eventuali transazioni interrotte
     */
    void cleanupDatabaseState();
} 
