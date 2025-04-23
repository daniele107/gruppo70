package model;

import java.time.LocalDateTime;

public class Hackathon {
    private String titolo;
    private String sede;
    private LocalDateTime data_inizio;
    private LocalDateTime data_fine;
    private int massimo_partecipanti;
    private int dimensione_team;
    Organizzatore organizzatore;

    public Hackathon() {}

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public LocalDateTime getData_inizio() {
        return data_inizio;
    }

    public void setData_inizio(LocalDateTime data_inizio) {
        this.data_inizio = data_inizio;
    }

    public LocalDateTime getData_fine() {
        return data_fine;
    }

    public void setData_fine(LocalDateTime data_fine) {
        this.data_fine = data_fine;
    }

    public int getMassimo_partecipanti() {
        return massimo_partecipanti;
    }

    public void setMassimo_partecipanti(int massimo_partecipanti) {
        this.massimo_partecipanti = massimo_partecipanti;
    }

    public int getDimensione_team() {
        return dimensione_team;
    }

    public void setDimensione_team(int dimensione_team) {
        this.dimensione_team = dimensione_team;
    }
    }
