package com.hackathon.app;

import java.time.LocalDate;

public class Giudice extends Utente {

  public Giudice(String nome, String cognome, LocalDate dataNascita, String email, String password) {
    super(nome, cognome, dataNascita, email, password);
  }

  public String DescrizioneProblema();
}
