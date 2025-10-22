package model;
import java.time.LocalDateTime;
/**
 * Rappresenta una richiesta di adesione ad un team da parte di un partecipante.
 */
public class RichiestaJoin {
    private int id;
    private int utenteId;
    private int teamId;
    private String messaggioMotivazionale;
    private LocalDateTime dataRichiesta;
    private StatoRichiesta stato;
    /**
     * Enum per lo stato della richiesta
     */
    public enum StatoRichiesta {
        IN_ATTESA,
        ACCETTATA,
        RIFIUTATA
    }
    /**
     * Costruttore per creare una nuova richiesta di join
     *
     * @param utenteId               l'ID dell'utente che fa la richiesta
     * @param teamId                 l'ID del team
     * @param messaggioMotivazionale il messaggio motivazionale
     */
    public RichiestaJoin(int utenteId, int teamId, String messaggioMotivazionale) {
        this.utenteId = utenteId;
        this.teamId = teamId;
        this.messaggioMotivazionale = messaggioMotivazionale;
        this.dataRichiesta = LocalDateTime.now();
        this.stato = StatoRichiesta.IN_ATTESA;
    }
    /**
     * Accetta la richiesta di join
     */
    public void accetta() {
        this.stato = StatoRichiesta.ACCETTATA;
    }
    /**
     * Rifiuta la richiesta di join
     */
    public void rifiuta() {
        this.stato = StatoRichiesta.RIFIUTATA;
    }
    /**
     * Verifica se la richiesta è in attesa
     *
     * @return true se la richiesta è in attesa
     */
    public boolean isInAttesa() {
        return stato == StatoRichiesta.IN_ATTESA;
    }
    /**
     * Verifica se la richiesta è stata accettata
     *
     * @return true se la richiesta è stata accettata
     */
    public boolean isAccettata() {
        return stato == StatoRichiesta.ACCETTATA;
    }
    /**
     * Verifica se la richiesta è stata rifiutata
     *
     * @return true se la richiesta è stata rifiutata
     */
    public boolean isRifiutata() {
        return stato == StatoRichiesta.RIFIUTATA;
    }
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUtenteId() { return utenteId; }
    public void setUtenteId(int utenteId) { this.utenteId = utenteId; }
    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }
    public String getMessaggioMotivazionale() { return messaggioMotivazionale; }
    public void setMessaggioMotivazionale(String messaggioMotivazionale) { this.messaggioMotivazionale = messaggioMotivazionale; }
    public LocalDateTime getDataRichiesta() { return dataRichiesta; }
    public void setDataRichiesta(LocalDateTime dataRichiesta) { this.dataRichiesta = dataRichiesta; }
    public StatoRichiesta getStato() { return stato; }
    public void setStato(StatoRichiesta stato) { this.stato = stato; }
    @Override
    public String toString() {
        return "RichiestaJoin{" +
                "id=" + id +
                ", utenteId=" + utenteId +
                ", teamId=" + teamId +
                ", messaggioMotivazionale='" + messaggioMotivazionale + '\'' +
                ", dataRichiesta=" + dataRichiesta +
                ", stato=" + stato +
                '}';
    }
} 
