package model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Classe che rappresenta i dati per la generazione di report.
 * Contiene tutte le informazioni necessarie per creare report dettagliati.
 */
public class ReportData {
    
    private int id;
    private String tipoReport;
    private String titolo;
    private String descrizione;
    private LocalDateTime dataGenerazione;
    private int utenteGeneratore;
    private Map<String, Object> dati;
    private String formatoOutput;
    private String percorsoFile;
    private boolean completato;
    private String errore;
    
    // Dati specifici per report hackathon
    private Hackathon hackathon;
    private List<Hackathon> hackathons;
    private List<Team> teams;
    private List<Registrazione> registrazioni;
    private List<Valutazione> valutazioni;
    private List<Progress> progressi;
    private List<Documento> documenti;
    private List<Utente> utenti;
    
    // Statistiche del sistema
    private Statistics statistiche;
    
    // Statistiche aggregate
    private int numeroPartecipanti;
    private int numeroGiudici;
    private int numeroTeam;
    private int numeroValutazioni;
    private double mediaVoti;
    private int numeroDocumenti;
    private long dimensioneTotaleDocumenti;
    
    /**
     * Costruttore vuoto
     */
    public ReportData() {
        this.dataGenerazione = LocalDateTime.now();
        this.completato = false;
    }
    
    /**
     * Costruttore per report hackathon
     *
     * @param tipoReport il tipo di report
     * @param titolo il titolo del report
     * @param hackathon l'hackathon di riferimento
     * @param utenteGeneratore l'ID dell'utente che genera il report
     */
    public ReportData(String tipoReport, String titolo, Hackathon hackathon, int utenteGeneratore) {
        this();
        this.tipoReport = tipoReport;
        this.titolo = titolo;
        this.hackathon = hackathon;
        this.utenteGeneratore = utenteGeneratore;
    }
    
    // Getters e Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTipoReport() {
        return tipoReport;
    }
    
    public void setTipoReport(String tipoReport) {
        this.tipoReport = tipoReport;
    }
    
    public String getTitolo() {
        return titolo;
    }
    
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }
    
    public String getDescrizione() {
        return descrizione;
    }
    
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    
    public LocalDateTime getDataGenerazione() {
        return dataGenerazione;
    }
    
    public void setDataGenerazione(LocalDateTime dataGenerazione) {
        this.dataGenerazione = dataGenerazione;
    }
    
    public int getUtenteGeneratore() {
        return utenteGeneratore;
    }
    
    public void setUtenteGeneratore(int utenteGeneratore) {
        this.utenteGeneratore = utenteGeneratore;
    }
    
    public Map<String, Object> getDati() {
        return dati;
    }
    
    public void setDati(Map<String, Object> dati) {
        this.dati = dati;
    }
    
    public String getFormatoOutput() {
        return formatoOutput;
    }
    
    public void setFormatoOutput(String formatoOutput) {
        this.formatoOutput = formatoOutput;
    }
    
    public String getPercorsoFile() {
        return percorsoFile;
    }
    
    public void setPercorsoFile(String percorsoFile) {
        this.percorsoFile = percorsoFile;
    }
    
    public boolean isCompletato() {
        return completato;
    }
    
    public void setCompletato(boolean completato) {
        this.completato = completato;
    }
    
    public String getErrore() {
        return errore;
    }
    
    public void setErrore(String errore) {
        this.errore = errore;
    }
    
    public Hackathon getHackathon() {
        return hackathon;
    }
    
    public void setHackathon(Hackathon hackathon) {
        this.hackathon = hackathon;
    }
    
    public List<Team> getTeams() {
        return teams;
    }
    
    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
    
    public List<Registrazione> getRegistrazioni() {
        return registrazioni;
    }
    
    public void setRegistrazioni(List<Registrazione> registrazioni) {
        this.registrazioni = registrazioni;
    }
    
    public List<Valutazione> getValutazioni() {
        return valutazioni;
    }
    
    public void setValutazioni(List<Valutazione> valutazioni) {
        this.valutazioni = valutazioni;
    }
    
    public List<Progress> getProgressi() {
        return progressi;
    }
    
    public void setProgressi(List<Progress> progressi) {
        this.progressi = progressi;
    }
    
    public List<Documento> getDocumenti() {
        return documenti;
    }
    
    public void setDocumenti(List<Documento> documenti) {
        this.documenti = documenti;
    }
    
    public List<Hackathon> getHackathons() {
        return hackathons;
    }
    
    public void setHackathons(List<Hackathon> hackathons) {
        this.hackathons = hackathons;
    }
    
    public List<Utente> getUtenti() {
        return utenti;
    }
    
    public void setUtenti(List<Utente> utenti) {
        this.utenti = utenti;
    }
    
    public Statistics getStatistiche() {
        return statistiche;
    }
    
    public void setStatistiche(Statistics statistiche) {
        this.statistiche = statistiche;
    }
    
    public int getNumeroPartecipanti() {
        return numeroPartecipanti;
    }
    
    public void setNumeroPartecipanti(int numeroPartecipanti) {
        this.numeroPartecipanti = numeroPartecipanti;
    }
    
    public int getNumeroGiudici() {
        return numeroGiudici;
    }
    
    public void setNumeroGiudici(int numeroGiudici) {
        this.numeroGiudici = numeroGiudici;
    }
    
    public int getNumeroTeam() {
        return numeroTeam;
    }
    
    public void setNumeroTeam(int numeroTeam) {
        this.numeroTeam = numeroTeam;
    }
    
    public int getNumeroValutazioni() {
        return numeroValutazioni;
    }
    
    public void setNumeroValutazioni(int numeroValutazioni) {
        this.numeroValutazioni = numeroValutazioni;
    }
    
    public double getMediaVoti() {
        return mediaVoti;
    }
    
    public void setMediaVoti(double mediaVoti) {
        this.mediaVoti = mediaVoti;
    }
    
    public int getNumeroDocumenti() {
        return numeroDocumenti;
    }
    
    public void setNumeroDocumenti(int numeroDocumenti) {
        this.numeroDocumenti = numeroDocumenti;
    }
    
    public long getDimensioneTotaleDocumenti() {
        return dimensioneTotaleDocumenti;
    }
    
    public void setDimensioneTotaleDocumenti(long dimensioneTotaleDocumenti) {
        this.dimensioneTotaleDocumenti = dimensioneTotaleDocumenti;
    }
    
    // Metodi di utilità
    
    /**
     * Calcola le statistiche aggregate dai dati presenti
     */
    public void calcolaStatistiche() {
        if (registrazioni != null) {
            numeroPartecipanti = (int) registrazioni.stream()
                .filter(r -> r.isPartecipante() && r.isConfermata())
                .count();
            numeroGiudici = (int) registrazioni.stream()
                .filter(r -> r.isGiudice() && r.isConfermata())
                .count();
        }
        
        if (teams != null) {
            numeroTeam = teams.size();
        }
        
        if (valutazioni != null) {
            numeroValutazioni = valutazioni.size();
            if (!valutazioni.isEmpty()) {
                mediaVoti = valutazioni.stream()
                    .mapToInt(Valutazione::getVoto)
                    .average()
                    .orElse(0.0);
            }
        }
        
        if (documenti != null) {
            numeroDocumenti = documenti.size();
            dimensioneTotaleDocumenti = documenti.stream()
                .mapToLong(Documento::getDimensione)
                .sum();
        }
    }
    
    /**
     * Ottiene la dimensione totale dei documenti in formato leggibile
     *
     * @return stringa con la dimensione formattata
     */
    public String getDimensioneTotaleFormattata() {
        if (dimensioneTotaleDocumenti < 1024) {
            return dimensioneTotaleDocumenti + " B";
        } else if (dimensioneTotaleDocumenti < 1024 * 1024) {
            return String.valueOf(Math.round(dimensioneTotaleDocumenti / 102.4) / 10.0) + " KB";
        } else if (dimensioneTotaleDocumenti < 1024 * 1024 * 1024) {
            return String.valueOf(Math.round(dimensioneTotaleDocumenti / (102.4 * 1024.0)) / 10.0) + " MB";
        } else {
            return String.valueOf(Math.round(dimensioneTotaleDocumenti / (102.4 * 1024.0 * 1024.0)) / 10.0) + " GB";
        }
    }
    
    /**
     * Verifica se il report contiene dati sufficienti
     *
     * @return true se il report ha dati sufficienti
     */
    public boolean hasDatiSufficienti() {
        return hackathon != null && 
               (teams != null && !teams.isEmpty()) && 
               (registrazioni != null && !registrazioni.isEmpty());
    }
    
    /**
     * Ottiene un riassunto testuale del report
     *
     * @return riassunto del report
     */
    public String getRiassunto() {
        StringBuilder riassunto = new StringBuilder();
        
        riassunto.append("Report: ").append(titolo).append("\n");
        riassunto.append("Tipo: ").append(tipoReport).append("\n");
        riassunto.append("Data generazione: ").append(dataGenerazione).append("\n\n");
        
        if (hackathon != null) {
            riassunto.append("Hackathon: ").append(hackathon.getNome()).append("\n");
            riassunto.append("Periodo: ").append(hackathon.getDataInizio())
                     .append(" - ").append(hackathon.getDataFine()).append("\n\n");
        }
        
        riassunto.append("STATISTICHE:\n");
        riassunto.append("- Partecipanti: ").append(numeroPartecipanti).append("\n");
        riassunto.append("- Giudici: ").append(numeroGiudici).append("\n");
        riassunto.append("- Team: ").append(numeroTeam).append("\n");
        riassunto.append("- Valutazioni: ").append(numeroValutazioni).append("\n");
        riassunto.append("- Media voti: ").append(String.valueOf(Math.round(mediaVoti * 100.0) / 100.0)).append("/10\n");
        riassunto.append("- Documenti: ").append(numeroDocumenti)
                 .append(" (").append(getDimensioneTotaleFormattata()).append(")\n");
        
        return riassunto.toString();
    }
    
    /**
     * Segna il report come completato
     */
    public void completaReport() {
        this.completato = true;
        this.errore = null;
    }
    
    /**
     * Segna il report come fallito con un errore
     *
     * @param errore il messaggio di errore
     */
    public void falliReport(String errore) {
        this.completato = false;
        this.errore = errore;
    }
    
    @Override
    public String toString() {
        return "ReportData{id=" + id + ", tipo='" + tipoReport + "', titolo='" + titolo + "', completato=" + completato + "}";
    }
}
