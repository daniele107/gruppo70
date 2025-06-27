
// File: Partecipante.java
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Partecipante extends Utente {
    private String nomeTeam;

    public Partecipante() { super(); }

    public Partecipante(String nome, String cognome, LocalDate dataNascita, String email, String password, String nomeTeam) {
        super(nome, cognome, dataNascita, email, password);
        this.nomeTeam = nomeTeam;
    }
    public String getNome() {
        return nomeTeam;
    }
    public void setNome(String nomeTeam) {
        this.nomeTeam = nomeTeam;
    }

}