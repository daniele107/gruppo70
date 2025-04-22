package com.hackathon.app;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Hackathon {
  private String titolo;
  private String sede;
  private LocalDateTime dataInizio;
  private LocalDateTime dataFine;
  private int massimoPartecipanti;
  private int dimensioneTeam;
  Organizzatore organizzatore;

  public Hackathon(String titolo, String sede, LocalDate dataInizio, LocalDate dataFine, int massimoPartecipanti, int dimensioneTeam);

  public String getTitolo();
  public void setTitolo(String titolo);
  public String getSede();
  public void setSede(String sede);
  public LocalDateTime getDataInizio();
  public void setDataInizio(LocalDateTime dataInizio);
  public LocalDateTime getDataFine();
  public void setDataFine(LocalDateTime dataFine);
  public int getMassimoPartecipanti();
  public void setMassimoPartecipanti(int massimoPartecipanti);
  public int getDimensioneTeam();
  public void setDimensioneTeam(int dimensioneTeam);

}
