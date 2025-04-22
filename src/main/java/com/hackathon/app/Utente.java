package com.hackathon.app;

import java.time.LocalDate;

public class Utente {
  protected String nome;
  protected String cognome;
  protected LocalDate dataNascita;
  protected String email;
  protected String password;

  public Utente(String nome, String cognome, LocalDate dataNascita, String email, String password );

  public Utente Registrazione(String nome, String cognome, LocalDate dataNascita, String email, String password);
  public Utente SignIn(String email, String password);


}
