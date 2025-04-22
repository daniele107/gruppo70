package com.hackathon.app;


import java.time.LocalDate;

public class Organizzatore extends Utente {
  public LocalDate AperturaIscrizioni(LocalDate data);
  public LocalDate ChiusuraIscrizioni(LocalDate data);
  public String Invito(String email);
}
