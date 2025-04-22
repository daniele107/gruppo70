package com.hackathon.app;

public class Invito {
  private String messaggio;
  private LocalDate dataInvio;
  
  public Invito(String messaggio, LocalDate dataInvio);
  public String getMessaggio();
  public void setMessaggio(String messaggio);
  public LocalDate getDataInvio();
  public void setDataInvio(LocalDate dataInvio);
  
  public Partecipante AccettaInvito();
  public void RifiutaInvito();
}
