
// File: Invito.java
package model;

import java.time.LocalDate;

/**
 * Invito ad un hackathon per un partecipante.
 */
public class Invito {
    public enum Stato { INVIATO, ACCETTATO, RIFIUTATO }
    private String mittente;
    private String destinatario;
    private final Hackathon hackathon;
    private final String messaggio;
    private final LocalDate data;
    private Stato stato;

    /**
     * Crea un invito specificando hackathon e messaggio.
     */
    public Invito(Hackathon hackathon, String messaggio, String mittente, String destinatario) {
        this.hackathon = hackathon;
        this.messaggio = messaggio;
        this.data = LocalDate.now();
        this.stato = Stato.INVIATO;
        this.mittente = mittente;
        this.destinatario = destinatario;
    }

    public String getMessaggio() { return messaggio; }
    public LocalDate getData() { return data; }
    public Stato getStato() { return stato; }
    public Hackathon getHackathon() { return hackathon; }
    public String getMittente() { return mittente; }
    public String getDestinatario() { return destinatario; }

    public void accetta() { this.stato = Stato.ACCETTATO; }
    public void rifiuta()  { this.stato = Stato.RIFIUTATO; }

    public void setMittente(String mittente) { this.mittente = mittente; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    @Override
    public String toString() {
        return "[" + stato + "] " + messaggio + " (" + data + ")";
    }
}
