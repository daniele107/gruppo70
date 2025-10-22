package model;
import java.util.ArrayList;
import java.util.List;
/**
 * Rappresenta un team di partecipanti ad un hackathon.
 * Gestisce la composizione del team e le richieste di join.
 */
public class Team {
    private int id;
    private String nome;
    private int hackathonId;
    private int capoTeamId;
    private int dimensioneMassima;
    private boolean definitivo;
    private java.time.LocalDateTime dataDefinitivo;
    private List<Integer> membriId;
    private List<RichiestaJoin> richiesteJoin;
    /**
     * Costruttore per creare un nuovo team
     *
     * @param nome              il nome del team
     * @param hackathonId       l'ID dell'hackathon
     * @param capoTeamId        l'ID del capo team
     * @param dimensioneMassima la dimensione massima del team
     */
    public Team(String nome, int hackathonId, int capoTeamId, int dimensioneMassima) {
        this.nome = nome;
        this.hackathonId = hackathonId;
        this.capoTeamId = capoTeamId;
        this.dimensioneMassima = dimensioneMassima;
        this.definitivo = false; // I team iniziano come non definitivi
        this.dataDefinitivo = null;
        this.membriId = new ArrayList<>();
        this.membriId.add(capoTeamId); // Il capo team è automaticamente membro
        this.richiesteJoin = new ArrayList<>();
    }
    /**
     * Aggiunge un membro al team se c'è spazio disponibile e il team non è definitivo
     *
     * @param utenteId l'ID dell'utente da aggiungere
     * @return true se l'aggiunta è riuscita, false altrimenti
     */
    public boolean aggiungiMembro(int utenteId) {
        if (definitivo) {
            return false; // Non si possono aggiungere membri a team definitivi
        }
        if (membriId.size() < dimensioneMassima && !membriId.contains(utenteId)) {
            membriId.add(utenteId);
            return true;
        }
        return false;
    }
    /**
     * Rimuove un membro dal team
     *
     * @param utenteId l'ID dell'utente da rimuovere
     * @return true se la rimozione è riuscita, false altrimenti
     */
    public boolean rimuoviMembro(int utenteId) {
        if (definitivo) {
            return false; // Non si possono rimuovere membri da team definitivi
        }
        if (utenteId != capoTeamId) { // Il capo team non può essere rimosso
            return membriId.remove(Integer.valueOf(utenteId));
        }
        return false;
    }
    /**
     * Verifica se il team ha spazio per nuovi membri
     *
     * @return true se c'è spazio disponibile
     */
    public boolean haSpazioDisponibile() {
        return membriId.size() < dimensioneMassima;
    }
    /**
     * Aggiunge una richiesta di join al team
     *
     * @param richiesta la richiesta di join
     */
    public void aggiungiRichiestaJoin(RichiestaJoin richiesta) {
        richiesteJoin.add(richiesta);
    }
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getHackathonId() { return hackathonId; }
    public void setHackathonId(int hackathonId) { this.hackathonId = hackathonId; }
    public int getCapoTeamId() { return capoTeamId; }
    public void setCapoTeamId(int capoTeamId) { this.capoTeamId = capoTeamId; }
    public int getDimensioneMassima() { return dimensioneMassima; }
    public void setDimensioneMassima(int dimensioneMassima) { this.dimensioneMassima = dimensioneMassima; }
    public List<Integer> getMembriId() { return new ArrayList<>(membriId); }
    public void setMembriId(List<Integer> membriId) { this.membriId = new ArrayList<>(membriId); }
    public List<RichiestaJoin> getRichiesteJoin() { return new ArrayList<>(richiesteJoin); }
    public void setRichiesteJoin(List<RichiestaJoin> richiesteJoin) { this.richiesteJoin = new ArrayList<>(richiesteJoin); }
    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", hackathonId=" + hackathonId +
                ", capoTeamId=" + capoTeamId +
                ", dimensioneMassima=" + dimensioneMassima +
                ", membriId=" + membriId +
                '}';
    }
    
    /**
     * Verifica se il team è definitivo
     *
     * @return true se il team è definitivo
     */
    public boolean isDefinitivo() {
        return definitivo;
    }
    
    /**
     * Imposta il team come definitivo
     *
     * @param definitivo true se il team deve essere definitivo
     */
    public void setDefinitivo(boolean definitivo) {
        this.definitivo = definitivo;
        if (definitivo && this.dataDefinitivo == null) {
            this.dataDefinitivo = java.time.LocalDateTime.now();
        }
    }
    
    /**
     * Ottiene la data in cui il team è diventato definitivo
     *
     * @return la data di definitivit√† o null se non è definitivo
     */
    public java.time.LocalDateTime getDataDefinitivo() {
        return dataDefinitivo;
    }
    
    /**
     * Imposta la data di definitivit√† del team
     *
     * @param dataDefinitivo la data di definitivit√†
     */
    public void setDataDefinitivo(java.time.LocalDateTime dataDefinitivo) {
        this.dataDefinitivo = dataDefinitivo;
    }
    
    /**
     * Verifica se il team può essere modificato
     *
     * @return true se il team può essere modificato (non è definitivo)
     */
    public boolean pueEssereModificato() {
        return !definitivo;
    }
    
    /**
     * Rende il team definitivo con timestamp corrente
     */
    public void rendiDefinitivo() {
        this.definitivo = true;
        this.dataDefinitivo = java.time.LocalDateTime.now();
    }
} 
