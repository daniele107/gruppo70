
// File: Organizzatore.java
package model;

import java.time.LocalDate;

public class Organizzatore extends Utente {
    public Organizzatore() { super(); }
    public Organizzatore(String nome, String cognome, LocalDate dataNascita, String email, String password) {
        super(nome, cognome, dataNascita, email, password);
    }

    /**
     * Crea un invito per il partecipante.
     */
    public Invito creaInvito(Hackathon h, String messaggio) {
        return new Invito(h, messaggio);
    }
}