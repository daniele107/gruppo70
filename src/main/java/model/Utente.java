package model;
/**
 * Rappresenta un utente del sistema Hackathon Manager.
 * Può essere organizzatore, giudice o partecipante.
 */
public class Utente {
    // Costanti per i ruoli
    public static final String RUOLO_ORGANIZZATORE = "ORGANIZZATORE";
    public static final String RUOLO_GIUDICE = "GIUDICE";
    public static final String RUOLO_PARTECIPANTE = "PARTECIPANTE";
    private int id;
    private String login;
    private String password;
    private String nome;
    private String cognome;
    private String email;
    private String ruolo; // ORGANIZZATORE, GIUDICE, PARTECIPANTE
    /**
     * Costruttore per creare un nuovo utente
     *
     * @param login    il login dell'utente
     * @param password la password dell'utente
     * @param nome     il nome dell'utente
     * @param cognome  il cognome dell'utente
     * @param email    l'email dell'utente
     * @param ruolo    il ruolo dell'utente
     */
    public Utente(String login, String password, String nome, String cognome, String email, String ruolo) {
        this.login = login;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.ruolo = ruolo;
    }
    /**
     * Costruttore di default per compatibilità
     *
     * @param login    the login
     * @param password the password
     */
    public Utente(String login, String password) {
        this(login, password, "", "", "", RUOLO_PARTECIPANTE);
    }
    /**
     * Verifica se l'utente è un organizzatore
     *
     * @return true se l'utente è un organizzatore
     */
    public boolean isOrganizzatore() {
        return RUOLO_ORGANIZZATORE.equals(ruolo);
    }
    /**
     * Verifica se l'utente è un giudice
     *
     * @return true se l'utente è un giudice
     */
    public boolean isGiudice() {
        return RUOLO_GIUDICE.equals(ruolo);
    }
    /**
     * Verifica se l'utente è un partecipante
     *
     * @return true se l'utente è un partecipante
     */
    public boolean isPartecipante() {
        return RUOLO_PARTECIPANTE.equals(ruolo);
    }
    // Getters e Setters
    /**
     * Ottiene l'ID univoco dell'utente
     * 
     * @return l'ID dell'utente
     */
    public int getId() { return id; }
    /**
     * Imposta l'ID dell'utente
     * 
     * @param id l'ID da impostare
     */
    public void setId(int id) { this.id = id; }
    /**
     * Ottiene il login dell'utente
     * 
     * @return il login dell'utente
     */
    public String getLogin() { return login; }
    /**
     * Imposta il login dell'utente
     * 
     * @param login il login da impostare (non può essere null o vuoto)
     * @throws IllegalArgumentException se il login è null o vuoto
     */
    public void setLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Il login non può essere null o vuoto");
        }
        this.login = login;
    }
    /**
     * Ottiene la password dell'utente
     * 
     * @return la password dell'utente
     */
    public String getPassword() { return password; }
    /**
     * Imposta la password dell'utente
     * 
     * @param password la password da impostare (non può essere null o vuota)
     * @throws IllegalArgumentException se la password è null o vuota
     */
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La password non può essere null o vuota");
        }
        this.password = password;
    }
    /**
     * Ottiene il nome dell'utente
     * 
     * @return il nome dell'utente
     */
    public String getNome() { return nome; }
    /**
     * Imposta il nome dell'utente
     * 
     * @param nome il nome da impostare
     */
    public void setNome(String nome) { this.nome = nome; }
    /**
     * Ottiene il cognome dell'utente
     * 
     * @return il cognome dell'utente
     */
    public String getCognome() { return cognome; }
    /**
     * Imposta il cognome dell'utente
     * 
     * @param cognome il cognome da impostare
     */
    public void setCognome(String cognome) { this.cognome = cognome; }
    /**
     * Ottiene l'email dell'utente
     * 
     * @return l'email dell'utente
     */
    public String getEmail() { return email; }
    /**
     * Imposta l'email dell'utente
     * 
     * @param email l'email da impostare (deve essere un formato email valido)
     * @throws IllegalArgumentException se l'email non è in formato valido
     */
    public void setEmail(String email) {
        if (email != null && !email.trim().isEmpty() && !isValidEmail(email)) {
            throw new IllegalArgumentException("Formato email non valido: " + email);
        }
        this.email = email;
    }
    /**
     * Ottiene il ruolo dell'utente
     * 
     * @return il ruolo dell'utente (ORGANIZZATORE, GIUDICE, PARTECIPANTE)
     */
    public String getRuolo() { return ruolo; }
    /**
     * Imposta il ruolo dell'utente
     * 
     * @param ruolo il ruolo da impostare (deve essere ORGANIZZATORE, GIUDICE o PARTECIPANTE)
     * @throws IllegalArgumentException se il ruolo non è valido
     */
    public void setRuolo(String ruolo) {
        if (!isValidRuolo(ruolo)) {
            throw new IllegalArgumentException("Ruolo non valido: " + ruolo + ". I ruoli validi sono: " + 
                RUOLO_ORGANIZZATORE + ", " + RUOLO_GIUDICE + ", " + RUOLO_PARTECIPANTE);
        }
        this.ruolo = ruolo;
    }
    /**
     * Valida il formato dell'email utilizzando una regex semplice
     * 
     * @param email l'email da validare
     * @return true se l'email è in formato valido
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    /**
     * Verifica se il ruolo è uno dei ruoli validi del sistema
     * 
     * @param ruolo il ruolo da verificare
     * @return true se il ruolo è valido
     */
    private boolean isValidRuolo(String ruolo) {
        return RUOLO_ORGANIZZATORE.equals(ruolo) || RUOLO_GIUDICE.equals(ruolo) || RUOLO_PARTECIPANTE.equals(ruolo);
    }
    /**
     * Restituisce una rappresentazione testuale dell'utente
     * 
     * @return stringa che rappresenta l'utente (senza password per sicurezza)
     */
    @Override
    public String toString() {
        return "Utente{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", ruolo='" + ruolo + '\'' +
                '}';
    }
}
