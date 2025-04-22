package com.hackathon.app;

public class Invito {
  protected String messaggio;
  protected LocalDate dataInvio;
  
  public Invito(String messaggio, LocalDate dataInvio);

  public void accetta_invito(String messaggio);
  public void rifiuta_invito(String messaggio);
}
