
// File: Team.java
package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team {
    private String nome;
    private final List<Partecipante> partecipanti = new ArrayList<>();
    private int voto = -1;

    public Team() {}
    public Team(String nome) { this.nome = nome; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    /**
     * Restituisce i partecipanti in modo non modificabile.
     */
    public List<Partecipante> getPartecipanti() { return Collections.unmodifiableList(partecipanti); }

    /**
     * Aggiunge un partecipante al team.
     */
    public void addPartecipante(Partecipante p) { partecipanti.add(p); }

    public int getVoto() { return voto; }
    public void setVoto(int voto) { this.voto = voto; }

    @Override
    public String toString() {
        return nome + " (" + partecipanti.size() + " membri) - Voto: " + voto;
    }
}
