package model;
import java.time.LocalDateTime;
/**
 * Rappresenta la registrazione di un utente ad un hackathon.
 */
public class Registrazione {
    private int id;
    private int utenteId;
    private int hackathonId;
    private LocalDateTime dataRegistrazione;
    private Ruolo ruolo;
    private boolean confermata;
    /**
     * Enum per i ruoli possibili
     */
    public enum Ruolo {
        ORGANIZZATORE,
        GIUDICE,
        PARTECIPANTE
    }
    /**
     * Costruttore per creare una nuova registrazione
     *
     * @param utenteId   l'ID dell'utente
     * @param hackathonId l'ID dell'hackathon
     * @param ruolo      il ruolo dell'utente nell'hackathon
     */
    public Registrazione() {
        this.dataRegistrazione = LocalDateTime.now();
        this.confermata = false;
    }

    public Registrazione(int utenteId, int hackathonId, Ruolo ruolo) {
        this.utenteId = utenteId;
        this.hackathonId = hackathonId;
        this.ruolo = ruolo;
        this.dataRegistrazione = LocalDateTime.now();
        this.confermata = false;
    }
    /**
     * Conferma la registrazione
     */
    public void conferma() {
        this.confermata = true;
    }
    /**
     * Verifica se la registrazione è confermata
     *
     * @return true se la registrazione è confermata
     */
    public boolean isConfermata() {
        return confermata;
    }
    /**
     * Verifica se l'utente è un organizzatore
     *
     * @return true se l'utente è un organizzatore
     */
    public boolean isOrganizzatore() {
        return ruolo == Ruolo.ORGANIZZATORE;
    }
    /**
     * Verifica se l'utente è un giudice
     *
     * @return true se l'utente è un giudice
     */
    public boolean isGiudice() {
        return ruolo == Ruolo.GIUDICE;
    }
    /**
     * Verifica se l'utente è un partecipante
     *
     * @return true se l'utente è un partecipante
     */
    public boolean isPartecipante() {
        return ruolo == Ruolo.PARTECIPANTE;
    }
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUtenteId() { return utenteId; }
    public void setUtenteId(int utenteId) { this.utenteId = utenteId; }
    public int getHackathonId() { return hackathonId; }
    public void setHackathonId(int hackathonId) { this.hackathonId = hackathonId; }
    public LocalDateTime getDataRegistrazione() { return dataRegistrazione; }
    public void setDataRegistrazione(LocalDateTime dataRegistrazione) { this.dataRegistrazione = dataRegistrazione; }
    public Ruolo getRuolo() { return ruolo; }
    public void setRuolo(Ruolo ruolo) { this.ruolo = ruolo; }
    public void setConfermata(boolean confermata) { this.confermata = confermata; }
    @Override
    public String toString() {
        return "ID: " + id + 
               " | Utente: " + utenteId + 
               " | Hackathon: " + hackathonId + 
               " | Ruolo: " + ruolo + 
               " | Confermata: " + (confermata ? "SI" : "NO") +
               " | Data: " + (dataRegistrazione != null ? dataRegistrazione.toLocalDate() : "N/A");
    }
} 
