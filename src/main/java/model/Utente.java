// File: Utente.java
package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Rappresenta un utente generico del sistema.
 */
public class Utente implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String nome;
    protected String cognome;
    protected LocalDate dataNascita;
    protected String email;
    protected String password;

    public Utente(String nome, String cognome, LocalDate dataNascita, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.dataNascita = dataNascita;
        this.email = email;
        this.password = password;
    }

    public Utente() {}

    public Utente(String nome, String cognome, String email, String password) {
        this(nome, cognome, null, email, password);
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public LocalDate getDataNascita() { return dataNascita; }
    public void setDataNascita(LocalDate dataNascita) { this.dataNascita = dataNascita; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean checkCredentials(String email, String password) {
        return this.email.equalsIgnoreCase(email) && this.password.equals(password);
    }

    @Override
    public String toString() {
        return nome + " " + cognome + " <" + email + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utente)) return false;
        Utente u = (Utente) o;
        return email != null && email.equalsIgnoreCase(u.email);
    }

    @Override
    public int hashCode() {
        return email != null ? email.toLowerCase().hashCode() : 0;
    }
}