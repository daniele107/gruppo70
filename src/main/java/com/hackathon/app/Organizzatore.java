package com.hackathon.app;
import java.time.LocalDate;

public class Organizzatore extends Utente {
  public Organizzatore(String nome, String cognome, LocalDate dataNascita, String email, String passowrd) {
    super(nome, cognome, dataNascita, email, passowrd);
  }

  public LocalDate AperturaIscrizioni(LocalDate data);
  public LocalDate ChiusuraIscrizioni(LocalDate data);
}
