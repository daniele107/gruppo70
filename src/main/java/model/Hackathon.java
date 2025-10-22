package model;
import java.time.LocalDateTime;
/**
 * Rappresenta un evento Hackathon con tutte le sue caratteristiche
 * e informazioni di gestione.
 */
public class Hackathon {
    private int id;
    private String nome;
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private String sede;
    private boolean isVirtuale;
    private int organizzatoreId;
    private int maxPartecipanti;
    private int maxTeam;
    private boolean registrazioniAperte;
    private String descrizioneProblema;
    private boolean eventoAvviato;
    private boolean eventoConcluso;
    /**
     * Costruttore per creare un nuovo Hackathon
     *
     * @param nome              il nome dell'hackathon
     * @param dataInizio        la data di inizio
     * @param sede              la sede dell'evento
     * @param isVirtuale        se l'evento è virtuale
     * @param organizzatoreId   l'ID dell'organizzatore
     * @param maxPartecipanti   il numero massimo di partecipanti
     * @param maxTeam           il numero massimo di team
     */
    public Hackathon(String nome, LocalDateTime dataInizio, String sede, 
                    boolean isVirtuale, int organizzatoreId, int maxPartecipanti, int maxTeam) {
        this.nome = nome;
        this.dataInizio = dataInizio;
        this.dataFine = dataInizio.plusDays(2); // Durata fissa di 2 giorni
        this.sede = sede;
        this.isVirtuale = isVirtuale;
        this.organizzatoreId = organizzatoreId;
        this.maxPartecipanti = maxPartecipanti;
        this.maxTeam = maxTeam;
        this.registrazioniAperte = false;
        this.eventoAvviato = false;
        this.eventoConcluso = false;
    }
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDateTime getDataInizio() { return dataInizio; }
    public void setDataInizio(LocalDateTime dataInizio) { this.dataInizio = dataInizio; }
    public LocalDateTime getDataFine() { return dataFine; }
    public void setDataFine(LocalDateTime dataFine) { this.dataFine = dataFine; }
    public String getSede() { return sede; }
    public void setSede(String sede) { this.sede = sede; }
    public boolean isVirtuale() { return isVirtuale; }
    public void setVirtuale(boolean virtuale) { isVirtuale = virtuale; }
    public int getOrganizzatoreId() { return organizzatoreId; }
    public void setOrganizzatoreId(int organizzatoreId) { this.organizzatoreId = organizzatoreId; }
    public int getMaxPartecipanti() { return maxPartecipanti; }
    public void setMaxPartecipanti(int maxPartecipanti) { this.maxPartecipanti = maxPartecipanti; }
    public int getMaxTeam() { return maxTeam; }
    public void setMaxTeam(int maxTeam) { this.maxTeam = maxTeam; }
    public boolean isRegistrazioniAperte() { return registrazioniAperte; }
    public void setRegistrazioniAperte(boolean registrazioniAperte) { this.registrazioniAperte = registrazioniAperte; }
    public String getDescrizioneProblema() { return descrizioneProblema; }
    public void setDescrizioneProblema(String descrizioneProblema) { this.descrizioneProblema = descrizioneProblema; }
    public boolean isEventoAvviato() { return eventoAvviato; }
    public void setEventoAvviato(boolean eventoAvviato) { this.eventoAvviato = eventoAvviato; }
    public boolean isEventoConcluso() { return eventoConcluso; }
    public void setEventoConcluso(boolean eventoConcluso) { this.eventoConcluso = eventoConcluso; }
    @Override
    public String toString() {
        return "Hackathon{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", dataInizio=" + dataInizio +
                ", sede='" + sede + '\'' +
                ", isVirtuale=" + isVirtuale +
                ", registrazioniAperte=" + registrazioniAperte +
                '}';
    }
} 
