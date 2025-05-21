
// File: Invito.java
package model;

import java.time.LocalDate;

/**
 * Invito ad un hackathon per un partecipante.
 */
public class Invito {
    public enum Stato { INVIATO, ACCETTATO, RIFIUTATO }

    private final Hackathon hackathon;
    private final String messaggio;
    private final LocalDate data;
    private Stato stato;

    /**
     * Crea un invito specificando hackathon e messaggio.
     */
    public Invito(Hackathon hackathon, String messaggio) {
        this.hackathon = hackathon;
        this.messaggio = messaggio;
        this.data = LocalDate.now();
        this.stato = Stato.INVIATO;
    }

    public String getMessaggio() { return messaggio; }
    public LocalDate getData() { return data; }
    public Stato getStato() { return stato; }
    public Hackathon getHackathon() { return hackathon; }

    public void accetta() { this.stato = Stato.ACCETTATO; }
    public void rifiuta()  { this.stato = Stato.RIFIUTATO; }

    @Override
    public String toString() {
        return "[" + stato + "] " + messaggio + " (" + data + ")";
    }
}
