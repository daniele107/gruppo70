package dao;

import model.Documento;
import java.util.List;

/**
 * Data Access Object per la gestione dei documenti.
 * Definisce le operazioni CRUD per i documenti caricati dai team.
 */
public interface DocumentoDAO {
    
    /**
     * Inserisce un nuovo documento nel database
     * 
     * @param documento il documento da inserire
     * @return l'ID del documento inserito, -1 se l'inserimento fallisce
     */
    int insert(Documento documento);
    
    /**
     * Trova un documento per ID
     * 
     * @param id l'ID del documento
     * @return il documento trovato o null se non esiste
     */
    Documento findById(int id);
    
    /**
     * Trova tutti i documenti di un team
     * 
     * @param teamId l'ID del team
     * @return lista dei documenti del team
     */
    List<Documento> findByTeam(int teamId);
    
    /**
     * Trova tutti i documenti di un hackathon
     * 
     * @param hackathonId l'ID dell'hackathon
     * @return lista dei documenti dell'hackathon
     */
    List<Documento> findByHackathon(int hackathonId);
    
    /**
     * Trova tutti i documenti caricati da un utente
     * 
     * @param utenteId l'ID dell'utente
     * @return lista dei documenti caricati dall'utente
     */
    List<Documento> findByUtente(int utenteId);
    
    /**
     * Trova documenti per tipo MIME
     * 
     * @param tipo il tipo MIME
     * @return lista dei documenti del tipo specificato
     */
    List<Documento> findByTipo(String tipo);
    
    /**
     * Trova documenti validati o non validati
     * 
     * @param validato true per documenti validati, false per non validati
     * @return lista dei documenti filtrati per stato di validazione
     */
    List<Documento> findByValidato(boolean validato);
    
    /**
     * Aggiorna un documento esistente
     * 
     * @param documento il documento da aggiornare
     * @return true se l'aggiornamento è riuscito
     */
    boolean update(Documento documento);
    
    /**
     * Valida un documento
     * 
     * @param documentoId l'ID del documento
     * @param validatoreId l'ID dell'utente che valida
     * @return true se la validazione è riuscita
     */
    boolean valida(int documentoId, int validatoreId);
    
    /**
     * Rimuove la validazione di un documento
     * 
     * @param documentoId l'ID del documento
     * @return true se l'operazione è riuscita
     */
    boolean rimuoviValidazione(int documentoId);
    
    /**
     * Elimina un documento
     * 
     * @param id l'ID del documento da eliminare
     * @return true se l'eliminazione è riuscita
     */
    boolean delete(int id);
    
    /**
     * Elimina tutti i documenti di un team
     * 
     * @param teamId l'ID del team
     * @return numero di documenti eliminati
     */
    int deleteByTeam(int teamId);
    
    /**
     * Elimina tutti i documenti di un hackathon
     * 
     * @param hackathonId l'ID dell'hackathon
     * @return numero di documenti eliminati
     */
    int deleteByHackathon(int hackathonId);
    
    /**
     * Conta i documenti di un team
     * 
     * @param teamId l'ID del team
     * @return numero di documenti del team
     */
    int countByTeam(int teamId);
    
    /**
     * Conta i documenti di un hackathon
     * 
     * @param hackathonId l'ID dell'hackathon
     * @return numero di documenti dell'hackathon
     */
    int countByHackathon(int hackathonId);
    
    /**
     * Calcola la dimensione totale dei documenti di un team
     * 
     * @param teamId l'ID del team
     * @return dimensione totale in bytes
     */
    long getTotalSizeByTeam(int teamId);
    
    /**
     * Calcola la dimensione totale dei documenti di un hackathon
     * 
     * @param hackathonId l'ID dell'hackathon
     * @return dimensione totale in bytes
     */
    long getTotalSizeByHackathon(int hackathonId);
    
    /**
     * Trova tutti i documenti del sistema (per amministratori)
     * 
     * @return lista di tutti i documenti
     */
    List<Documento> findAll();
    
    /**
     * Verifica se un file con lo stesso hash esiste già
     * 
     * @param hash l'hash del file
     * @return true se esiste un file con lo stesso hash
     */
    boolean existsByHash(String hash);
    
    /**
     * Trova documenti per hash
     * 
     * @param hash l'hash del file
     * @return lista dei documenti con lo stesso hash
     */
    List<Documento> findByHash(String hash);
}
