
// File: Team.java
package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team {
    private String nome;
    private int voto = -1;

    public Team() {}
    public Team(String nome) { this.nome = nome; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }


    public int getVoto() { return voto; }
    public void setVoto(int voto) { this.voto = voto; }

    @Override
    public String toString() {
        return nome + " - Voto: " + voto;
    }
}
