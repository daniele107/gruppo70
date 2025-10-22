package dao;
import model.Progress;
import java.util.List;
/**
 * Interfaccia per l'accesso ai dati dei Progress.
 * Definisce tutti i metodi CRUD e le operazioni specifiche per la gestione dei progressi.
 */
public interface ProgressDAO {
    /**
     * Inserisce un nuovo progresso nel database
     *
     * @param progress il progresso da inserire
     * @return l'ID del progresso inserito
     */
    int insert(Progress progress);
    /**
     * Aggiorna un progresso esistente nel database
     *
     * @param progress il progresso da aggiornare
     * @return true se l'aggiornamento è riuscito
     */
    boolean update(Progress progress);
    /**
     * Elimina un progresso dal database
     *
     * @param id l'ID del progresso da eliminare
     * @return true se l'eliminazione è riuscita
     */
    boolean delete(int id);
    /**
     * Trova un progresso per ID
     *
     * @param id l'ID del progresso
     * @return il progresso trovato o null se non esiste
     */
    Progress findById(int id);
    /**
     * Trova tutti i progressi
     *
     * @return lista di tutti i progressi
     */
    List<Progress> findAll();
    /**
     * Trova i progressi di un team specifico
     *
     * @param teamId l'ID del team
     * @return lista dei progressi del team
     */
    List<Progress> findByTeam(int teamId);
    /**
     * Trova i progressi di un hackathon specifico
     *
     * @param hackathonId l'ID dell'hackathon
     * @return lista dei progressi dell'hackathon
     */
    List<Progress> findByHackathon(int hackathonId);
    /**
     * Trova i progressi di un team in un hackathon specifico
     *
     * @param teamId      l'ID del team
     * @param hackathonId l'ID dell'hackathon
     * @return lista dei progressi del team nell'hackathon
     */
    List<Progress> findByTeamAndHackathon(int teamId, int hackathonId);
    /**
     * Trova i progressi commentati da un giudice specifico
     *
     * @param giudiceId l'ID del giudice
     * @return lista dei progressi commentati dal giudice
     */
    List<Progress> findByGiudice(int giudiceId);
    /**
     * Trova i progressi senza commenti di giudice
     *
     * @return lista dei progressi senza commenti
     */
    List<Progress> findSenzaCommenti();
    /**
     * Trova i progressi con commenti di giudice
     *
     * @return lista dei progressi con commenti
     */
    List<Progress> findConCommenti();
    /**
     * Aggiunge un commento di giudice ad un progresso
     *
     * @param progressId   l'ID del progresso
     * @param giudiceId    l'ID del giudice
     * @param commento     il commento del giudice
     * @return true se l'aggiunta del commento è riuscita
     */
    boolean aggiungiCommentoGiudice(int progressId, int giudiceId, String commento);
    /**
     * Aggiorna il commento di un giudice ad un progresso
     *
     * @param progressId   l'ID del progresso
     * @param giudiceId    l'ID del giudice
     * @param nuovoCommento il nuovo commento
     * @return true se l'aggiornamento è riuscito
     */
    boolean aggiornaCommentoGiudice(int progressId, int giudiceId, String nuovoCommento);
    /**
     * Rimuove il commento di un giudice da un progresso
     *
     * @param progressId l'ID del progresso
     * @param giudiceId  l'ID del giudice
     * @return true se la rimozione è riuscita
     */
    boolean rimuoviCommentoGiudice(int progressId, int giudiceId);
    /**
     * Verifica se un progresso ha un commento di giudice
     *
     * @param progressId l'ID del progresso
     * @return true se il progresso ha un commento
     */
    boolean haCommentoGiudice(int progressId);
    /**
     * Trova il commento di un giudice specifico per un progresso
     *
     * @param progressId l'ID del progresso
     * @param giudiceId  l'ID del giudice
     * @return il commento del giudice o null se non esiste
     */
    String findCommentoGiudice(int progressId, int giudiceId);
    /**
     * Conta il numero di progressi di un team
     *
     * @param teamId l'ID del team
     * @return il numero di progressi
     */
    int contaProgressiTeam(int teamId);
    /**
     * Conta il numero di progressi di un hackathon
     *
     * @param hackathonId l'ID dell'hackathon
     * @return il numero di progressi
     */
    int contaProgressiHackathon(int hackathonId);
    /**
     * Conta il numero di progressi commentati da un giudice
     *
     * @param giudiceId l'ID del giudice
     * @return il numero di progressi commentati
     */
    int contaProgressiCommentati(int giudiceId);
    /**
     * Trova l'ultimo progresso di un team
     *
     * @param teamId l'ID del team
     * @return l'ultimo progresso del team o null se non esiste
     */
    Progress findUltimoProgressoTeam(int teamId);
    /**
     * Trova tutti i progressi ordinati per data di caricamento (più recenti prima)
     *
     * @return lista dei progressi ordinati per data
     */
    List<Progress> findAllOrderByDataCaricamento();
    /**
     * Trova i progressi di un team ordinati per data di caricamento
     *
     * @param teamId l'ID del team
     * @return lista dei progressi del team ordinati per data
     */
    List<Progress> findByTeamOrderByDataCaricamento(int teamId);
} 
