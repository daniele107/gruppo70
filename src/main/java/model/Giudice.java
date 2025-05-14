
// File: Giudice.java
package model;

import java.time.LocalDate;

public class Giudice extends Utente {
    public Giudice() { super(); }
    public Giudice(String nome, String cognome, LocalDate data_nascita, String email, String password) {
        super(nome, cognome, data_nascita, email, password);
    }
}
