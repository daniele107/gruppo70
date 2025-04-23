package model;

import java.time.LocalDate;

public class Invito {
  protected String messaggio;
  protected LocalDate data_invito;

  public Invito() {}

  public void accetta_invito(String messaggio) {}
  public void rifiuta_invito(String messaggio) {}
}
