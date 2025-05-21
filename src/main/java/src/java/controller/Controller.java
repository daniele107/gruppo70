// File: src/main/java/controller/Controller.java
package controller;

import model.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller centralizza la logica applicativa e persistenza in file.
 */
public class Controller implements Serializable {
    private static final long serialVersionUID = 1L;

    // Collezioni di dominio
    private List<Utente> utenti;
    private List<Documento> docs;
    private List<Voto> voti;
    private List<Invito> inviti;
    private List<Team> teams;
    private List<Hackathon> hacks;

    private transient Utente currentUser;

    public Controller() {
        this.utenti = new ArrayList<>();
        this.docs    = new ArrayList<>();
        this.voti    = new ArrayList<>();
        this.inviti  = new ArrayList<>();
        this.teams   = new ArrayList<>();
        this.hacks   = new ArrayList<>();
    }

    /**
     * Carica stato da file, o nuovo controller se non esiste.
     */
    public static Controller loadState() {
        File file = new File("data/state.dat");
        if (!file.exists()) return new Controller();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (Controller) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new Controller();
        }
    }

    /**
     * Salva stato su file.
     */
    public void saveState() {
        new File("data").mkdirs();
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("data/state.dat"))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registra un nuovo utente con ruolo: Partecipante, Organizzatore o Giudice.
     */
    public Utente registraUtente(String nome,
                                 String cognome,
                                 String email,
                                 String password,
                                 String ruolo) {
        boolean exists = utenti.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
        if (exists) return null;

        Utente u;
        switch (ruolo) {
            case "Organizzatore":
                u = new Organizzatore(nome, cognome, null, email, password);
                break;
            case "Giudice":
                u = new Giudice(nome, cognome, null, email, password);
                break;
            default:
                u = new Partecipante(nome, cognome, null, email, password);
                break;
        }
        utenti.add(u);
        return u;
    }

    /**
     * Esegue login con email e password.
     */
    public Utente login(String email, String pwd) {
        Optional<Utente> opt = utenti.stream()
                .filter(u -> u.checkCredentials(email, pwd))
                .findFirst();
        if (opt.isPresent()) {
            currentUser = opt.get();
            return currentUser;
        }
        return null;
    }

    /**
     * Restituisce l'utente attualmente autenticato.
     */
    public Utente getCurrentUser() {
        return currentUser;
    }

    /**
     * Restituisce la lista di tutti gli Utenti registrati.
     *
     * @return lista immutabile di Utente
     */
    public List<Utente> getUtenti() {
        return Collections.unmodifiableList(utenti);
    }

    /**
     * Imposta l'utente corrente (bypass login).
     *
     * @param user Utente da impostare come currentUser
     */
    public void setCurrentUser(Utente user) {
        this.currentUser = user;
    }

    /**
     * Aggiorna dati profilo utente.
     */
    public void aggiornaUtente(Utente u,
                               String nome,
                               String cognome,
                               LocalDate dataNascita,
                               String email,
                               String password) {
        u.setNome(nome);
        u.setCognome(cognome);
        u.setDataNascita(dataNascita);
        u.setEmail(email);
        u.setPassword(password);
    }

    // DOCUMENTI
    public void caricaDocumento(Documento d) { docs.add(d); }
    public List<Documento> getDocumenti() { return Collections.unmodifiableList(docs); }
    public void modificaDocumento(Documento d, String contenuto) { d.modificaDocumento(contenuto); }
    public void cancellaDocumento(Documento d) { docs.remove(d); }

    // VOTI
    public void valutaTeam(Voto voto) {
        if (currentUser instanceof Giudice) voti.add(voto);
    }
    public List<Voto> getVoti() {
        if (currentUser instanceof Giudice) return Collections.unmodifiableList(voti);
        return Collections.emptyList();
    }

    // INVITI
    public void creaInvito(Invito i) {
        if (currentUser instanceof Organizzatore) inviti.add(i);
        else if (currentUser instanceof Partecipante) {
            ((Partecipante) currentUser).addInvito(i);
            inviti.add(i);
        }
    }
    public List<Invito> getInviti(Partecipante p) {
        if (currentUser instanceof Partecipante && currentUser.equals(p))
            return Collections.unmodifiableList(p.getInviti());
        return Collections.emptyList();
    }
    public void rispondiInvito(Invito invito, boolean accept) {
        if (currentUser instanceof Partecipante) {
            if (accept) invito.accetta(); else invito.rifiuta();
        }
    }

    // TEAM
    public void creaTeam(Team t) {
        if (currentUser instanceof Partecipante) {
            t.addPartecipante((Partecipante) currentUser);
            teams.add(t);
        }
    }
    public List<Team> getTeams(Partecipante p) {
        if (currentUser instanceof Partecipante && currentUser.equals(p)) {
            return teams.stream()
                    .filter(t -> t.getPartecipanti().contains(p))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
    public List<Team> getTeamsToEvaluate() {
        if (currentUser instanceof Giudice) return Collections.unmodifiableList(teams);
        return Collections.emptyList();
    }

    // HACKATHON
    public void creaHackathon(Hackathon h) {
        if (currentUser instanceof Organizzatore) {
            h.setOrganizzatore((Organizzatore) currentUser);
            hacks.add(h);
        }
    }
    public List<Hackathon> getHackathons(Organizzatore o) {
        if (currentUser instanceof Organizzatore && currentUser.equals(o)) {
            return hacks.stream()
                    .filter(h -> h.getOrganizzatore().equals(o))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
