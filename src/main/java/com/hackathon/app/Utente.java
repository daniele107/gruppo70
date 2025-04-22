package com.hackathon.app;

import java.time.LocalDate;

public class Utente {
    private String nome;
    private String cognome;
    private LocalDate dataNascita;
    private String email;
    private String password;

    public Utente Utente(String nome, String cognome, LocalDate dataNascita, String email, String passowrd);
    
    public Utente Registrazione(String nome, String cognome, LocalDate dataNascita, String email, String password);
    public Utente SignIn(String email, String password);

    public String getNome();
    public void setNome(String nome);
    public String getCognome();
    public void setCognome(String cognome);
    public LocalDate getDataNascita();
    public void setDataNascita(LocalDate dataNascita);
    public String getEmail();
    public void setEmail(String email);
    public String getPassword();
    public void setPassword(String password);
}
