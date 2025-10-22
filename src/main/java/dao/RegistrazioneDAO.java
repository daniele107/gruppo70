package dao;
import model.Registrazione;
import java.util.List;
/**
 * Interfaccia per l'accesso ai dati delle Registrazioni.
 * Definisce tutti i metodi CRUD e le operazioni specifiche per la gestione delle registrazioni.
 */
public interface RegistrazioneDAO {
    /**
     * Inserisce una nuova registrazione nel database
     *
     * @param registrazione la registrazione da inserire
     * @return l'ID della registrazione inserita
     */
    int insert(Registrazione registrazione);
    /**
     * Aggiorna una registrazione esistente nel database
     *
     * @param registrazione la registrazione da aggiornare
     * @return true se l'aggiornamento è riuscito
     */
    boolean update(Registrazione registrazione);
    /**
     * Elimina una registrazione dal database
     *
     * @param id l'ID della registrazione da eliminare
     * @return true se l'eliminazione è riuscita
     */
    boolean delete(int id);
    /**
     * Trova una registrazione per ID
     *
     * @param id l'ID della registrazione
     * @return la registrazione trovata o null se non esiste
     */
    Registrazione findById(int id);
    /**
     * Trova tutte le registrazioni
     *
     * @return lista di tutte le registrazioni
     */
    List<Registrazione> findAll();
    /**
     * Trova le registrazioni di un utente specifico
     *
     * @param utenteId l'ID dell'utente
     * @return lista delle registrazioni dell'utente
     */
    List<Registrazione> findByUtente(int utenteId);
    /**
     * Trova le registrazioni per un hackathon specifico
     *
     * @param hackathonId l'ID dell'hackathon
     * @return lista delle registrazioni dell'hackathon
     */
    List<Registrazione> findByHackathon(int hackathonId);
    /**
     * Trova una registrazione specifica di un utente ad un hackathon
     *
     * @param utenteId   l'ID dell'utente
     * @param hackathonId l'ID dell'hackathon
     * @return la registrazione trovata o null se non esiste
     */
    Registrazione findByUtenteAndHackathon(int utenteId, int hackathonId);
    /**
     * Trova le registrazioni confermate per un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @return lista delle registrazioni confermate
     */
    List<Registrazione> findConfermateByHackathon(int hackathonId);
    /**
     * Trova le registrazioni non confermate per un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @return lista delle registrazioni non confermate
     */
    List<Registrazione> findNonConfermateByHackathon(int hackathonId);
    /**
     * Trova le registrazioni per ruolo in un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @param ruolo       il ruolo da cercare
     * @return lista delle registrazioni con il ruolo specificato
     */
    List<Registrazione> findByHackathonAndRuolo(int hackathonId, Registrazione.Ruolo ruolo);
    /**
     * Trova tutti gli organizzatori di un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @return lista delle registrazioni degli organizzatori
     */
    List<Registrazione> findOrganizzatori(int hackathonId);
    /**
     * Trova tutti i giudici di un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @return lista delle registrazioni dei giudici
     */
    List<Registrazione> findGiudici(int hackathonId);
    /**
     * Trova tutti i partecipanti di un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @return lista delle registrazioni dei partecipanti
     */
    List<Registrazione> findPartecipanti(int hackathonId);
    /**
     * Conferma una registrazione
     *
     * @param registrazioneId l'ID della registrazione
     * @return true se la conferma è riuscita
     */
    boolean confermaRegistrazione(int registrazioneId);
    /**
     * Verifica se un utente è registrato ad un hackathon
     *
     * @param utenteId   l'ID dell'utente
     * @param hackathonId l'ID dell'hackathon
     * @return true se l'utente è registrato
     */
    boolean isRegistrato(int utenteId, int hackathonId);
    /**
     * Verifica se un utente è confermato per un hackathon
     *
     * @param utenteId   l'ID dell'utente
     * @param hackathonId l'ID dell'hackathon
     * @return true se l'utente è confermato
     */
    boolean isConfermato(int utenteId, int hackathonId);
    /**
     * Conta il numero di registrazioni per un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @return il numero di registrazioni
     */
    int contaRegistrazioni(int hackathonId);
    /**
     * Conta il numero di registrazioni confermate per un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @return il numero di registrazioni confermate
     */
    int contaRegistrazioniConfermate(int hackathonId);
    /**
     * Conta il numero di registrazioni per ruolo in un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @param ruolo       il ruolo da contare
     * @return il numero di registrazioni per quel ruolo
     */
    int contaRegistrazioniPerRuolo(int hackathonId, Registrazione.Ruolo ruolo);
    /**
     * Trova tutte le registrazioni non confermate
     *
     * @return lista delle registrazioni non confermate
     */
    List<Registrazione> findNonConfermate();
} 
