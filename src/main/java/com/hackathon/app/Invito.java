package com.hackathon.app;

public class Invito {
  protected String messaggio;
  protected LocalDate dataInvio;
  
  public Invito(String messaggio, LocalDate dataInvio);

  public void AccettaInvito();
  public void RifiutaInvito();
}
