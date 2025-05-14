
// File: Partecipante.java
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Partecipante extends Utente {
    private final List<Invito> inviti = new ArrayList<>();

    public Partecipante() { super(); }

    public Partecipante(String nome, String cognome, LocalDate data_nascita, String email, String password) {
        super(nome, cognome, data_nascita, email, password);
    }

    /**
     * Restituisce gli inviti in modo non modificabile.
     */
    public List<Invito> getInviti() { return Collections.unmodifiableList(inviti); }

    /**
     * Aggiunge un invito alla lista.
     */
    public void addInvito(Invito inv) { inviti.add(inv); }
}