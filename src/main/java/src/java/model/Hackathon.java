
// File: Hackathon.java
package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Hackathon {
    private String titolo;
    private String sede;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private int massimoPartecipanti;
    private int dimensioneTeam;
    private Organizzatore organizzatore;

    public Hackathon() {}

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getSede() { return sede; }
    public void setSede(String sede) { this.sede = sede; }

    public LocalDateTime getDataInizio() { return dataInizio; }
    public void setDataInizio(LocalDateTime dataInizio) { this.dataInizio = dataInizio; }

    public LocalDateTime getDataFine() { return dataFine; }
    public void setDataFine(LocalDateTime dataFine) { this.dataFine = dataFine; }

    public int getMassimoPartecipanti() { return massimoPartecipanti; }
    public void setMassimoPartecipanti(int massimoPartecipanti) { this.massimoPartecipanti = massimoPartecipanti; }

    public int getDimensioneTeam() { return dimensioneTeam; }
    public void setDimensioneTeam(int dimensioneTeam) { this.dimensioneTeam = dimensioneTeam; }

    public Organizzatore getOrganizzatore() { return organizzatore; }
    public void setOrganizzatore(Organizzatore organizzatore) { this.organizzatore = organizzatore; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hackathon)) return false;
        Hackathon h = (Hackathon) o;
        return Objects.equals(titolo, h.titolo) && Objects.equals(sede, h.sede)
                && Objects.equals(dataInizio, h.dataInizio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titolo, sede, dataInizio);
    }
}
