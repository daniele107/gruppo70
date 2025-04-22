package com.hackathon.app;

import java.time.LocalDate;

public class Partecipante extends Utente {

  public Partecipante(String nome, String cognome, LocalDate dataNascita, String email, String password) {
    super(nome, cognome, dataNascita, email, password);
  }
}