
// File: Voto.java
package model;

import java.util.Objects;

/**
 * Rappresenta un voto assegnato a un team.
 */
public class Voto {
    private final Team team;
    private final int punteggio;

    /**
     * Costruttore che associa il voto al team.
     */
    public Voto(Team team, int punteggio) {
        this.team = Objects.requireNonNull(team, "Team non può essere null");
        if (punteggio < 0 || punteggio > 10) {
            throw new IllegalArgumentException("Il punteggio deve essere compreso tra 0 e 10.");
        }
        this.punteggio = punteggio;
        this.team.setVoto(punteggio);
    }

    public Team getTeam() { return team; }
    public int getPunteggio() { return punteggio; }

    /**
     * Restituisce la classificazione testuale del voto.
     */
    public String getClassificazione() {
        if (punteggio >= 9) return "Ottimo";
        if (punteggio >= 6) return "Sufficiente";
        return "Insufficiente";
    }

    @Override
    public String toString() {
        return team.getNome() + ": " + punteggio + " (" + getClassificazione() + ")";
    }
}
