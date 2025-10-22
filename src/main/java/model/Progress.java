package model;
import java.time.LocalDateTime;
/**
 * Rappresenta i progressi di un team durante un hackathon.
 * Include documenti caricati e commenti dei giudici.
 */
public class Progress {
    private int id;
    private int teamId;
    private int hackathonId;
    private String titolo;
    private String descrizione;
    private String documentoPath;
    private LocalDateTime dataCaricamento;
    private String commentoGiudice;
    private int giudiceId;
    private LocalDateTime dataCommento;
    /**
     * Costruttore vuoto per la deserializzazione
     */
    public Progress() {
        this.dataCaricamento = LocalDateTime.now();
    }
    /**
     * Costruttore per creare un nuovo progresso
     *
     * @param teamId        l'ID del team
     * @param hackathonId   l'ID dell'hackathon
     * @param titolo        il titolo del progresso
     * @param descrizione   la descrizione del progresso
     * @param documentoPath il percorso del documento caricato
     */
    public Progress(int teamId, int hackathonId, String titolo, String descrizione, String documentoPath) {
        this.teamId = teamId;
        this.hackathonId = hackathonId;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.documentoPath = documentoPath;
        this.dataCaricamento = LocalDateTime.now();
    }
    /**
     * Aggiunge un commento da parte di un giudice
     *
     * @param giudiceId     l'ID del giudice
     * @param commento      il commento del giudice
     */
    public void aggiungiCommentoGiudice(int giudiceId, String commento) {
        this.giudiceId = giudiceId;
        this.commentoGiudice = commento;
        this.dataCommento = LocalDateTime.now();
    }
    /**
     * Verifica se il progresso ha un commento da un giudice
     *
     * @return true se c'è un commento
     */
    public boolean haCommentoGiudice() {
        return commentoGiudice != null && !commentoGiudice.isEmpty();
    }
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }
    public int getHackathonId() { return hackathonId; }
    public void setHackathonId(int hackathonId) { this.hackathonId = hackathonId; }
    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public String getDocumentoPath() { return documentoPath; }
    public void setDocumentoPath(String documentoPath) { this.documentoPath = documentoPath; }
    public LocalDateTime getDataCaricamento() { return dataCaricamento; }
    public void setDataCaricamento(LocalDateTime dataCaricamento) { this.dataCaricamento = dataCaricamento; }
    public String getCommentoGiudice() { return commentoGiudice; }
    public void setCommentoGiudice(String commentoGiudice) { this.commentoGiudice = commentoGiudice; }
    public int getGiudiceId() { return giudiceId; }
    public void setGiudiceId(int giudiceId) { this.giudiceId = giudiceId; }
    public LocalDateTime getDataCommento() { return dataCommento; }
    public void setDataCommento(LocalDateTime dataCommento) { this.dataCommento = dataCommento; }
    @Override
    public String toString() {
        return "Progress{" +
                "id=" + id +
                ", teamId=" + teamId +
                ", hackathonId=" + hackathonId +
                ", titolo='" + titolo + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", documentoPath='" + documentoPath + '\'' +
                ", dataCaricamento=" + dataCaricamento +
                ", commentoGiudice='" + commentoGiudice + '\'' +
                ", giudiceId=" + giudiceId +
                ", dataCommento=" + dataCommento +
                '}';
    }
} 
