package dao;
import model.Team;
import model.RichiestaJoin;
import java.util.List;
/**
 * Interfaccia per l'accesso ai dati dei Team.
 * Definisce tutti i metodi CRUD e le operazioni specifiche per la gestione dei team.
 */
public interface TeamDAO {
    /**
     * Inserisce un nuovo team nel database
     *
     * @param team il team da inserire
     * @return l'ID del team inserito
     */
    int insert(Team team);
    /**
     * Aggiorna un team esistente nel database
     *
     * @param team il team da aggiornare
     * @return true se l'aggiornamento è riuscito
     */
    boolean update(Team team);
    /**
     * Elimina un team dal database
     *
     * @param id l'ID del team da eliminare
     * @return true se l'eliminazione è riuscita
     */
    boolean delete(int id);
    /**
     * Trova un team per ID
     *
     * @param id l'ID del team
     * @return il team trovato o null se non esiste
     */
    Team findById(int id);
    /**
     * Trova tutti i team
     *
     * @return lista di tutti i team
     */
    List<Team> findAll();
    /**
     * Trova i team di un hackathon specifico
     *
     * @param hackathonId l'ID dell'hackathon
     * @return lista dei team dell'hackathon
     */
    List<Team> findByHackathon(int hackathonId);
    /**
     * Trova i team di cui un utente è membro
     *
     * @param utenteId l'ID dell'utente
     * @return lista dei team dell'utente
     */
    List<Team> findByMembro(int utenteId);
    /**
     * Trova i team di cui un utente è capo
     *
     * @param capoTeamId l'ID del capo team
     * @return lista dei team di cui l'utente è capo
     */
    List<Team> findByCapoTeam(int capoTeamId);
    /**
     * Aggiunge un membro ad un team
     *
     * @param teamId   l'ID del team
     * @param utenteId l'ID dell'utente da aggiungere
     * @return true se l'aggiunta è riuscita
     */
    boolean aggiungiMembro(int teamId, int utenteId);
    /**
     * Rimuove un membro da un team
     *
     * @param teamId   l'ID del team
     * @param utenteId l'ID dell'utente da rimuovere
     * @return true se la rimozione è riuscita
     */
    boolean rimuoviMembro(int teamId, int utenteId);
    /**
     * Verifica se un utente è membro di un team
     *
     * @param teamId   l'ID del team
     * @param utenteId l'ID dell'utente
     * @return true se l'utente è membro del team
     */
    boolean isMembro(int teamId, int utenteId);
    /**
     * Verifica se un utente è capo di un team
     *
     * @param teamId   l'ID del team
     * @param utenteId l'ID dell'utente
     * @return true se l'utente è capo del team
     */
    boolean isCapoTeam(int teamId, int utenteId);
    /**
     * Verifica se un team ha spazio disponibile
     *
     * @param teamId l'ID del team
     * @return true se il team ha spazio disponibile
     */
    boolean haSpazioDisponibile(int teamId);
    /**
     * Conta il numero di membri di un team
     *
     * @param teamId l'ID del team
     * @return il numero di membri
     */
    int contaMembri(int teamId);
    /**
     * Trova tutti i membri di un team
     *
     * @param teamId l'ID del team
     * @return lista degli ID dei membri
     */
    List<Integer> findMembri(int teamId);
    /**
     * Inserisce una richiesta di join
     *
     * @param richiesta la richiesta di join
     * @return l'ID della richiesta inserita
     */
    int insertRichiestaJoin(RichiestaJoin richiesta);
    /**
     * Aggiorna una richiesta di join
     *
     * @param richiesta la richiesta di join da aggiornare
     * @return true se l'aggiornamento è riuscito
     */
    boolean updateRichiestaJoin(RichiestaJoin richiesta);
    /**
     * Trova le richieste di join per un team
     *
     * @param teamId l'ID del team
     * @return lista delle richieste di join
     */
    List<RichiestaJoin> findRichiesteJoin(int teamId);
    /**
     * Trova le richieste di join in attesa per un team
     *
     * @param teamId l'ID del team
     * @return lista delle richieste di join in attesa
     */
    List<RichiestaJoin> findRichiesteJoinInAttesa(int teamId);
    /**
     * Trova le richieste di join di un utente
     *
     * @param utenteId l'ID dell'utente
     * @return lista delle richieste di join dell'utente
     */
    List<RichiestaJoin> findRichiesteJoinByUtente(int utenteId);
    /**
     * Accetta una richiesta di join
     *
     * @param richiestaId l'ID della richiesta
     * @return true se l'accettazione è riuscita
     */
    boolean accettaRichiestaJoin(int richiestaId);
    /**
     * Rifiuta una richiesta di join
     *
     * @param richiestaId l'ID della richiesta
     * @return true se il rifiuto è riuscito
     */
    boolean rifiutaRichiestaJoin(int richiestaId);
    
    /**
     * Rende definitivi tutti i team di un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @return il numero di team resi definitivi
     */
    int rendiDefinitiviTeamHackathon(int hackathonId);
    
    /**
     * Verifica se un team è definitivo
     *
     * @param teamId l'ID del team
     * @return true se il team è definitivo
     */
    boolean isTeamDefinitivo(int teamId);
    
    /**
     * Trova una richiesta di join per ID
     *
     * @param richiestaId l'ID della richiesta
     * @return la richiesta di join o null se non trovata
     */
    RichiestaJoin findRichiestaJoinById(int richiestaId);
} 
