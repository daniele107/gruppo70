package com.hackathon.app

import java.time.LocalDate;
import java.util.List;

public class Hackathon {
    protected String titolo;
    protected String sede;
    protected LocalDate dataInizio;
    protected LocalDate dataFine;
    protected int massimoPartecipanti;
    protected int dimensioneTeam;
}

public class Utente {
    protected String nome;
    protected String cognome;
    protected LocalDate dataNascita;
    protected String email;
    protected String password;
    public Utente Registrazione(nome String, cognome String, dataNascita LocalDate, email String, password String);
    public Utente SignIn(email String, password String);
}

public class Organizzatore extends Utente {
    public LocalDate AperturaIscrizioni (data LocalDate);
    public LocalDate ChiusuraIscrizioni (data LocalDate);
    public String Invito(email String);
}

public class Giudice extends Utente {
    public String DescrizioneProblema();
}

public class Partecipante extends Utente {
    public String Invita(email String);
    public String AccettaInvito();
    public String RifiutaInvito();
}

public class Documento {
    protected LocalDate data;
    protected String documento;
    protected String modificaDocumento(nuovoDocumento String);
}

public class Voto {
    protected int voto;
    private int assegnazioneVoto();
    public String PubblicazioneClassifica();
}

public class Team {
    protected String nome;
    protected Voto voto;
    protected List<Partecipante> partecipanti;
}
