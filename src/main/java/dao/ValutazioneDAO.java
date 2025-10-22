package dao;
import model.Valutazione;
import java.util.List;
import model.TeamRankingResult;
/**
 * Interfaccia DAO per l'entità Valutazione
 * Definisce le operazioni di accesso ai dati per le valutazioni dei giudici
 */
public interface ValutazioneDAO {
    /**
     * Inserisce una nuova valutazione
     * @param valutazione la valutazione da inserire
     * @return la valutazione inserita con ID generato
     */
    Valutazione insert(Valutazione valutazione);
    /**
     * Aggiorna una valutazione esistente
     * @param valutazione la valutazione da aggiornare
     * @return la valutazione aggiornata
     */
    Valutazione update(Valutazione valutazione);
    /**
     * Elimina una valutazione per ID
     * @param id l'ID della valutazione da eliminare
     * @return true se eliminata con successo, false altrimenti
     */
    boolean delete(int id);
    /**
     * Trova una valutazione per ID
     * @param id l'ID della valutazione
     * @return la valutazione trovata o null se non esiste
     */
    Valutazione findById(int id);
    /**
     * Trova tutte le valutazioni
     * @return lista di tutte le valutazioni
     */
    List<Valutazione> findAll();
    /**
     * Trova tutte le valutazioni di un giudice
     * @param giudiceId l'ID del giudice
     * @return lista delle valutazioni del giudice
     */
    List<Valutazione> findByGiudice(int giudiceId);
    /**
     * Trova tutte le valutazioni di un team
     * @param teamId l'ID del team
     * @return lista delle valutazioni del team
     */
    List<Valutazione> findByTeam(int teamId);
    /**
     * Trova tutte le valutazioni di un hackathon
     * @param hackathonId l'ID dell'hackathon
     * @return lista delle valutazioni dell'hackathon
     */
    List<Valutazione> findByHackathon(int hackathonId);
    /**
     * Trova la valutazione di un giudice per un team specifico
     * @param giudiceId l'ID del giudice
     * @param teamId l'ID del team
     * @return la valutazione trovata o null se non esiste
     */
    Valutazione findByGiudiceAndTeam(int giudiceId, int teamId);
    /**
     * Trova tutte le valutazioni di un team in un hackathon
     * @param teamId l'ID del team
     * @param hackathonId l'ID dell'hackathon
     * @return lista delle valutazioni del team nell'hackathon
     */
    List<Valutazione> findByTeamAndHackathon(int teamId, int hackathonId);
    /**
     * Trova la valutazione media di un team
     * @param teamId l'ID del team
     * @return la valutazione media o 0 se non ci sono valutazioni
     */
    double findValutazioneMediaTeam(int teamId);
    /**
     * Trova la valutazione media di un team in un hackathon
     * @param teamId l'ID del team
     * @param hackathonId l'ID dell'hackathon
     * @return la valutazione media o 0 se non ci sono valutazioni
     */
    double findValutazioneMediaTeamInHackathon(int teamId, int hackathonId);
    /**
     * Trova tutte le valutazioni ordinate per voto decrescente
     * @return lista delle valutazioni ordinate per voto
     */
    List<Valutazione> findAllOrderByVoto();
    /**
     * Trova tutte le valutazioni di un hackathon ordinate per voto decrescente
     * @param hackathonId l'ID dell'hackathon
     * @return lista delle valutazioni ordinate per voto
     */
    List<Valutazione> findByHackathonOrderByVoto(int hackathonId);
    /**
     * Conta il numero di valutazioni di un team
     * @param teamId l'ID del team
     * @return il numero di valutazioni
     */
    int contaValutazioniTeam(int teamId);
    /**
     * Conta il numero di valutazioni di un hackathon
     * @param hackathonId l'ID dell'hackathon
     * @return il numero di valutazioni
     */
    int contaValutazioniHackathon(int hackathonId);
    /**
     * Conta il numero di valutazioni di un giudice
     * @param giudiceId l'ID del giudice
     * @return il numero di valutazioni
     */
    int contaValutazioniGiudice(int giudiceId);
    /**
     * Verifica se un giudice ha già valutato un team
     * @param giudiceId l'ID del giudice
     * @param teamId l'ID del team
     * @return true se ha già valutato, false altrimenti
     */
    boolean haGiudiceValutatoTeam(int giudiceId, int teamId);
    /**
     * Trova la classifica dei team in un hackathon
     * @param hackathonId l'ID dell'hackathon
     * @return lista dei team ordinati per valutazione media decrescente
     */
    List<Integer> findClassificaTeam(int hackathonId);
    /**
     * Trova il team vincitore di un hackathon
     * @param hackathonId l'ID dell'hackathon
     * @return l'ID del team vincitore o null se non ci sono valutazioni
     */
    Integer findTeamVincitore(int hackathonId);

    /**
     * Returns ranking entries with average score and votes count for a given hackathon.
     * Sorted by average score desc, then votes count desc.
     * @param hackathonId the hackathon identifier
     * @return list of TeamRankingResult
     */
    List<TeamRankingResult> findTeamRankingByHackathon(int hackathonId);
    /**
     * Trova tutti i team che non sono stati valutati da un giudice
     * @param giudiceId l'ID del giudice
     * @param hackathonId l'ID dell'hackathon
     * @return lista degli ID dei team non valutati
     */
    List<Integer> findTeamNonValutati(int giudiceId, int hackathonId);
}
