package model;
import java.time.LocalDateTime;
/**
 * Rappresenta una valutazione assegnata da un giudice ad un team.
 */
public class Valutazione {
    private int id;
    private int giudiceId;
    private int teamId;
    private int hackathonId;
    private int voto; // 0-10
    private String commento;
    private LocalDateTime dataValutazione;
    /**
     * Costruttore vuoto per la deserializzazione
     */
    public Valutazione() {
        this.dataValutazione = LocalDateTime.now();
    }
    /**
     * Costruttore per creare una nuova valutazione
     *
     * @param giudiceId   l'ID del giudice
     * @param teamId      l'ID del team
     * @param hackathonId l'ID dell'hackathon
     * @param voto        il voto assegnato (0-10)
     * @param commento    il commento del giudice
     */
    public Valutazione(int giudiceId, int teamId, int hackathonId, int voto, String commento) {
        this.giudiceId = giudiceId;
        this.teamId = teamId;
        this.hackathonId = hackathonId;
        this.voto = Math.max(0, Math.min(10, voto)); // Assicura che il voto sia tra 0 e 10
        this.commento = commento;
        this.dataValutazione = LocalDateTime.now();
    }
    /**
     * Verifica se il voto è valido (tra 0 e 10)
     *
     * @return true se il voto è valido
     */
    public boolean isVotoValido() {
        return voto >= 0 && voto <= 10;
    }
    /**
     * Ottiene la valutazione come stringa descrittiva
     *
     * @return la valutazione descrittiva
     */
    public String getValutazioneDescrittiva() {
        if (voto >= 9) return "Eccellente";
        if (voto >= 7) return "Molto Buono";
        if (voto >= 6) return "Buono";
        if (voto >= 5) return "Sufficiente";
        if (voto >= 3) return "Insufficiente";
        return "Molto Scarso";
    }
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getGiudiceId() { return giudiceId; }
    public void setGiudiceId(int giudiceId) { this.giudiceId = giudiceId; }
    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }
    public int getHackathonId() { return hackathonId; }
    public void setHackathonId(int hackathonId) { this.hackathonId = hackathonId; }
    public int getVoto() { return voto; }
    public void setVoto(int voto) { 
        this.voto = Math.max(0, Math.min(10, voto)); // Assicura che il voto sia tra 0 e 10
    }
    public String getCommento() { return commento; }
    public void setCommento(String commento) { this.commento = commento; }
    public LocalDateTime getDataValutazione() { return dataValutazione; }
    public void setDataValutazione(LocalDateTime dataValutazione) { this.dataValutazione = dataValutazione; }
    @Override
    public String toString() {
        return "Valutazione{" +
                "id=" + id +
                ", giudiceId=" + giudiceId +
                ", teamId=" + teamId +
                ", hackathonId=" + hackathonId +
                ", voto=" + voto +
                ", commento='" + commento + '\'' +
                ", dataValutazione=" + dataValutazione +
                '}';
    }
} 
