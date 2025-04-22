package com.hackathon.app;

import java.time.LocalDate;

public class Hackathon {
    private String titolo;
    private String sede;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private int massimoPartecipanti;
    private int dimensioneTeam;

    public Hackathon(String titolo, String sede, LocalDate dataInizio, LocalDate dataFine, int massimoPartecipanti, int dimensioneTeam);
    public String getTitolo();
    public void setTitolo(String titolo);
    public String getSede();
    public void setSede(String sede);
    public LocalDate getDataInizio();
    public void setDataInizio(LocalDate dataInizio);
    public LocalDate getDataFine();
    public void setDataFine(LocalDate dataFine);
    public int getMassimoPartecipanti();
    public void setMassimoPartecipanti(int massimoPartecipanti);
    public int getDimensioneTeam();
    public void setDimensioneTeam(int dimensioneTeam);
    
    public static void main(String[] args);
}
