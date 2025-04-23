package model;

import java.time.LocalDate;

public class Utente {
  protected String nome;
  protected String cognome;
  protected LocalDate data_nascita;
  protected String email;
  protected String password;
  public Utente(String nome, String cognome, LocalDate data_nascita, String email, String password) {
    this.nome = nome;
    this.cognome = cognome;
    this.data_nascita = data_nascita;
    this.email = email;
    this.password = password;
  }

  public Utente() {

  }

  public Utente Registrazione(String nome, String cognome, LocalDate data_nascita, String email, String password){
    return new Utente(nome, cognome, data_nascita, email, password);
  }
  public Utente SignIn(String email, String password){
    if (this.email.equals(email) && this.password.equals(password)) {
      return this;
    } else {
      return null;
    }
  }
  }
