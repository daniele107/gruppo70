package model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Classe che rappresenta le statistiche del sistema.
 * Contiene metriche e indicatori di performance per dashboard e report.
 */
public class Statistics {
    
    // Costanti per evitare duplicazione di stringhe
    private static final String KPI_TASSO_PARTECIPAZIONE = "tasso_partecipazione";
    private static final String KPI_TASSO_COMPLETAMENTO_TEAM = "tasso_completamento_team";
    
    private int id;
    private String tipo;
    private String periodo;
    private LocalDateTime dataCalcolo;
    private Map<String, Object> metriche;
    private Map<String, Double> kpi;
    private boolean aggiornate;
    
    // Statistiche generali sistema
    private int totaleUtenti;
    private int totaleHackathon;
    private int totaleTeam;
    private int totaleValutazioni;
    private int totaleDocumenti;
    private long dimensioneTotaleStorage;
    
    // Statistiche per periodo
    private int nuoviUtentiPeriodo;
    private int nuoviHackathonPeriodo;
    private int nuoviTeamPeriodo;
    private int nuoveValutazioniPeriodo;
    
    // Statistiche per ruolo
    private int numeroOrganizzatori;
    private int numeroGiudici;
    private int numeroPartecipanti;
    
    // Statistiche hackathon
    private int hackathonAttivi;
    private int hackathonConclusi;
    private int hackathonInProgrammazione;
    private double mediaPartecipantiPerHackathon;
    private double mediaTeamPerHackathon;
    
    // Statistiche team
    private double mediaMembriPerTeam;
    private int teamCompleti;
    private int teamInCompleti;
    private double mediaDocumentiPerTeam;
    
    // Statistiche valutazioni
    private double mediaVotiGenerale;
    private double mediaVotiPerGiudice;
    private int valutazioniPendenti;
    private int valutazioniCompletate;
    
    // Statistiche documenti
    private double mediaDimensioneDocumento;
    private Map<String, Integer> distribuzioneFormatiFile;
    private int documentiValidati;
    private int documentiNonValidati;
    
    /**
     * Costruttore vuoto
     */
    public Statistics() {
        this.dataCalcolo = LocalDateTime.now();
        this.metriche = new HashMap<>();
        this.kpi = new HashMap<>();
        this.distribuzioneFormatiFile = new HashMap<>();
        this.aggiornate = false;
    }
    
    /**
     * Costruttore con tipo e periodo
     *
     * @param tipo il tipo di statistiche
     * @param periodo il periodo di riferimento
     */
    public Statistics(String tipo, String periodo) {
        this();
        this.tipo = tipo;
        this.periodo = periodo;
    }
    
    // Getters e Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getPeriodo() {
        return periodo;
    }
    
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }
    
    public LocalDateTime getDataCalcolo() {
        return dataCalcolo;
    }
    
    public void setDataCalcolo(LocalDateTime dataCalcolo) {
        this.dataCalcolo = dataCalcolo;
    }
    
    public Map<String, Object> getMetriche() {
        return metriche;
    }
    
    public void setMetriche(Map<String, Object> metriche) {
        this.metriche = metriche;
    }
    
    public Map<String, Double> getKpi() {
        return kpi;
    }
    
    public void setKpi(Map<String, Double> kpi) {
        this.kpi = kpi;
    }
    
    public boolean isAggiornate() {
        return aggiornate;
    }
    
    public void setAggiornate(boolean aggiornate) {
        this.aggiornate = aggiornate;
    }
    
    public int getTotaleUtenti() {
        return totaleUtenti;
    }
    
    public void setTotaleUtenti(int totaleUtenti) {
        this.totaleUtenti = totaleUtenti;
    }
    
    public int getTotaleHackathon() {
        return totaleHackathon;
    }
    
    public void setTotaleHackathon(int totaleHackathon) {
        this.totaleHackathon = totaleHackathon;
    }
    
    public int getTotaleTeam() {
        return totaleTeam;
    }
    
    public void setTotaleTeam(int totaleTeam) {
        this.totaleTeam = totaleTeam;
    }
    
    public int getTotaleValutazioni() {
        return totaleValutazioni;
    }
    
    public void setTotaleValutazioni(int totaleValutazioni) {
        this.totaleValutazioni = totaleValutazioni;
    }
    
    public int getTotaleDocumenti() {
        return totaleDocumenti;
    }
    
    public void setTotaleDocumenti(int totaleDocumenti) {
        this.totaleDocumenti = totaleDocumenti;
    }
    
    public long getDimensioneTotaleStorage() {
        return dimensioneTotaleStorage;
    }
    
    public void setDimensioneTotaleStorage(long dimensioneTotaleStorage) {
        this.dimensioneTotaleStorage = dimensioneTotaleStorage;
    }
    
    public int getNuoviUtentiPeriodo() {
        return nuoviUtentiPeriodo;
    }
    
    public void setNuoviUtentiPeriodo(int nuoviUtentiPeriodo) {
        this.nuoviUtentiPeriodo = nuoviUtentiPeriodo;
    }
    
    public int getNuoviHackathonPeriodo() {
        return nuoviHackathonPeriodo;
    }
    
    public void setNuoviHackathonPeriodo(int nuoviHackathonPeriodo) {
        this.nuoviHackathonPeriodo = nuoviHackathonPeriodo;
    }
    
    public int getNuoviTeamPeriodo() {
        return nuoviTeamPeriodo;
    }
    
    public void setNuoviTeamPeriodo(int nuoviTeamPeriodo) {
        this.nuoviTeamPeriodo = nuoviTeamPeriodo;
    }
    
    public int getNuoveValutazioniPeriodo() {
        return nuoveValutazioniPeriodo;
    }
    
    public void setNuoveValutazioniPeriodo(int nuoveValutazioniPeriodo) {
        this.nuoveValutazioniPeriodo = nuoveValutazioniPeriodo;
    }
    
    public int getNumeroOrganizzatori() {
        return numeroOrganizzatori;
    }
    
    public void setNumeroOrganizzatori(int numeroOrganizzatori) {
        this.numeroOrganizzatori = numeroOrganizzatori;
    }
    
    public int getNumeroGiudici() {
        return numeroGiudici;
    }
    
    public void setNumeroGiudici(int numeroGiudici) {
        this.numeroGiudici = numeroGiudici;
    }
    
    public int getNumeroPartecipanti() {
        return numeroPartecipanti;
    }
    
    public void setNumeroPartecipanti(int numeroPartecipanti) {
        this.numeroPartecipanti = numeroPartecipanti;
    }
    
    public int getHackathonAttivi() {
        return hackathonAttivi;
    }
    
    public void setHackathonAttivi(int hackathonAttivi) {
        this.hackathonAttivi = hackathonAttivi;
    }
    
    public int getHackathonConclusi() {
        return hackathonConclusi;
    }
    
    public void setHackathonConclusi(int hackathonConclusi) {
        this.hackathonConclusi = hackathonConclusi;
    }
    
    public int getHackathonInProgrammazione() {
        return hackathonInProgrammazione;
    }
    
    public void setHackathonInProgrammazione(int hackathonInProgrammazione) {
        this.hackathonInProgrammazione = hackathonInProgrammazione;
    }
    
    public double getMediaPartecipantiPerHackathon() {
        return mediaPartecipantiPerHackathon;
    }
    
    public void setMediaPartecipantiPerHackathon(double mediaPartecipantiPerHackathon) {
        this.mediaPartecipantiPerHackathon = mediaPartecipantiPerHackathon;
    }
    
    public double getMediaTeamPerHackathon() {
        return mediaTeamPerHackathon;
    }
    
    public void setMediaTeamPerHackathon(double mediaTeamPerHackathon) {
        this.mediaTeamPerHackathon = mediaTeamPerHackathon;
    }
    
    public double getMediaMembriPerTeam() {
        return mediaMembriPerTeam;
    }
    
    public void setMediaMembriPerTeam(double mediaMembriPerTeam) {
        this.mediaMembriPerTeam = mediaMembriPerTeam;
    }
    
    public int getTeamCompleti() {
        return teamCompleti;
    }
    
    public void setTeamCompleti(int teamCompleti) {
        this.teamCompleti = teamCompleti;
    }
    
    public int getTeamInCompleti() {
        return teamInCompleti;
    }
    
    public void setTeamInCompleti(int teamInCompleti) {
        this.teamInCompleti = teamInCompleti;
    }
    
    public double getMediaDocumentiPerTeam() {
        return mediaDocumentiPerTeam;
    }
    
    public void setMediaDocumentiPerTeam(double mediaDocumentiPerTeam) {
        this.mediaDocumentiPerTeam = mediaDocumentiPerTeam;
    }
    
    public double getMediaVotiGenerale() {
        return mediaVotiGenerale;
    }
    
    public void setMediaVotiGenerale(double mediaVotiGenerale) {
        this.mediaVotiGenerale = mediaVotiGenerale;
    }
    
    public double getMediaVotiPerGiudice() {
        return mediaVotiPerGiudice;
    }

    public void setMediaVotiPerGiudice(double mediaVotiPerGiudice) {
        this.mediaVotiPerGiudice = mediaVotiPerGiudice;
    }

    /**
     * Restituisce la media generale dei voti (alias per getMediaVotiGenerale)
     *
     * @return media generale dei voti
     */
    public double getMediaVoti() {
        return mediaVotiGenerale;
    }

    /**
     * Imposta la media generale dei voti (alias per setMediaVotiGenerale)
     *
     * @param mediaVoti la media generale dei voti
     */
    public void setMediaVoti(double mediaVoti) {
        this.mediaVotiGenerale = mediaVoti;
    }

    public int getValutazioniPendenti() {
        return valutazioniPendenti;
    }
    
    public void setValutazioniPendenti(int valutazioniPendenti) {
        this.valutazioniPendenti = valutazioniPendenti;
    }
    
    public int getValutazioniCompletate() {
        return valutazioniCompletate;
    }
    
    public void setValutazioniCompletate(int valutazioniCompletate) {
        this.valutazioniCompletate = valutazioniCompletate;
    }
    
    public double getMediaDimensioneDocumento() {
        return mediaDimensioneDocumento;
    }
    
    public void setMediaDimensioneDocumento(double mediaDimensioneDocumento) {
        this.mediaDimensioneDocumento = mediaDimensioneDocumento;
    }
    
    public Map<String, Integer> getDistribuzioneFormatiFile() {
        return distribuzioneFormatiFile;
    }
    
    public void setDistribuzioneFormatiFile(Map<String, Integer> distribuzioneFormatiFile) {
        this.distribuzioneFormatiFile = distribuzioneFormatiFile;
    }
    
    public int getDocumentiValidati() {
        return documentiValidati;
    }
    
    public void setDocumentiValidati(int documentiValidati) {
        this.documentiValidati = documentiValidati;
    }
    
    public int getDocumentiNonValidati() {
        return documentiNonValidati;
    }
    
    public void setDocumentiNonValidati(int documentiNonValidati) {
        this.documentiNonValidati = documentiNonValidati;
    }
    
    // ===== METODI MANCANTI PER COMPATIBILITÀ GUI =====
    
    /**
     * Ottiene il voto medio (alias per compatibilità GUI)
     * @return il voto medio generale
     */
    public double getVotoMedio() {
        return getMediaVotiGenerale();
    }
    
    /**
     * Imposta il voto medio (alias per compatibilità GUI)
     * @param votoMedio il voto medio
     */
    public void setVotoMedio(double votoMedio) {
        setMediaVotiGenerale(votoMedio);
    }
    
    /**
     * Ottiene il tasso di partecipazione
     * @return il tasso di partecipazione
     */
    public double getParticipationRate() {
        return getKPIValue(KPI_TASSO_PARTECIPAZIONE);
    }
    
    /**
     * Imposta il tasso di partecipazione
     * @param participationRate il tasso di partecipazione
     */
    public void setParticipationRate(double participationRate) {
        aggiungiKPI(KPI_TASSO_PARTECIPAZIONE, participationRate);
    }
    
    /**
     * Ottiene il tasso di completamento team
     * @return il tasso di completamento team
     */
    public double getTeamCompletionRate() {
        return getKPIValue(KPI_TASSO_COMPLETAMENTO_TEAM);
    }
    
    /**
     * Imposta il tasso di completamento team
     * @param teamCompletionRate il tasso di completamento team
     */
    public void setTeamCompletionRate(double teamCompletionRate) {
        aggiungiKPI(KPI_TASSO_COMPLETAMENTO_TEAM, teamCompletionRate);
    }
    
    /**
     * Imposta il totale delle registrazioni
     * @param totaleRegistrazioni il totale delle registrazioni
     */
    public void setTotaleRegistrazioni(int totaleRegistrazioni) {
        // Aggiungiamo come metrica personalizzata
        aggiungiMetrica("totale_registrazioni", totaleRegistrazioni);
    }
    
    /**
     * Ottiene il totale delle registrazioni
     * @return il totale delle registrazioni
     */
    public int getTotaleRegistrazioni() {
        Object value = getMetrica("totale_registrazioni");
        if (value instanceof Integer integerValue) {
            return integerValue;
        }
        return 0;
    }
    
    // Metodi di utilità
    
    /**
     * Calcola i KPI principali
     */
    public void calcolaKPI() {
        // Tasso di completamento team
        if (totaleTeam > 0) {
            kpi.put(KPI_TASSO_COMPLETAMENTO_TEAM, (double) teamCompleti / totaleTeam * 100);
        }
        
        // Tasso di partecipazione
        if (totaleUtenti > 0) {
            kpi.put(KPI_TASSO_PARTECIPAZIONE, (double) numeroPartecipanti / totaleUtenti * 100);
        }
        
        // Efficienza valutazioni
        if (valutazioniPendenti + valutazioniCompletate > 0) {
            kpi.put("efficienza_valutazioni", 
                (double) valutazioniCompletate / (valutazioniPendenti + valutazioniCompletate) * 100);
        }
        
        // Tasso di validazione documenti
        if (totaleDocumenti > 0) {
            kpi.put("tasso_validazione_documenti", (double) documentiValidati / totaleDocumenti * 100);
        }
        
        // Utilizzo storage
        long maxStorage = 10L * 1024 * 1024 * 1024; // 10GB esempio
        kpi.put("utilizzo_storage", (double) dimensioneTotaleStorage / maxStorage * 100);
        
        // Qualità media hackathon (basata su media voti)
        kpi.put("qualita_media_hackathon", mediaVotiGenerale);
    }
    
    /**
     * Aggiunge una metrica personalizzata
     *
     * @param nome il nome della metrica
     * @param valore il valore della metrica
     */
    public void aggiungiMetrica(String nome, Object valore) {
        metriche.put(nome, valore);
    }
    
    /**
     * Ottiene una metrica per nome
     *
     * @param nome il nome della metrica
     * @return il valore della metrica o null se non esiste
     */
    public Object getMetrica(String nome) {
        return metriche.get(nome);
    }
    
    /**
     * Aggiunge un KPI
     *
     * @param nome il nome del KPI
     * @param valore il valore del KPI
     */
    public void aggiungiKPI(String nome, double valore) {
        kpi.put(nome, valore);
    }
    
    /**
     * Ottiene un KPI per nome
     *
     * @param nome il nome del KPI
     * @return il valore del KPI o 0.0 se non esiste
     */
    public double getKPIValue(String nome) {
        return kpi.getOrDefault(nome, 0.0);
    }
    
    /**
     * Ottiene la dimensione totale dello storage in formato leggibile
     *
     * @return stringa con la dimensione formattata
     */
    public String getDimensioneTotaleStorageFormattata() {
        if (dimensioneTotaleStorage < 1024) {
            return dimensioneTotaleStorage + " B";
        } else if (dimensioneTotaleStorage < 1024 * 1024) {
            return String.valueOf(Math.round(dimensioneTotaleStorage / 102.4) / 10.0) + " KB";
        } else if (dimensioneTotaleStorage < 1024 * 1024 * 1024) {
            return String.valueOf(Math.round(dimensioneTotaleStorage / (102.4 * 1024.0)) / 10.0) + " MB";
        } else {
            return String.valueOf(Math.round(dimensioneTotaleStorage / (102.4 * 1024.0 * 1024.0)) / 10.0) + " GB";
        }
    }
    
    /**
     * Verifica se le statistiche sono recenti (meno di 1 ora)
     *
     * @return true se le statistiche sono recenti
     */
    public boolean sonoRecenti() {
        return dataCalcolo.isAfter(LocalDateTime.now().minusHours(1));
    }
    
    /**
     * Segna le statistiche come aggiornate
     */
    public void aggiornaTimestamp() {
        this.dataCalcolo = LocalDateTime.now();
        this.aggiornate = true;
    }
    
    /**
     * Ottiene un riassunto delle statistiche principali
     *
     * @return riassunto delle statistiche
     */
    public String getRiassunto() {
        StringBuilder riassunto = new StringBuilder();
        
        riassunto.append("=== STATISTICHE SISTEMA ===\n");
        riassunto.append("Tipo: ").append(tipo).append("\n");
        riassunto.append("Periodo: ").append(periodo).append("\n");
        riassunto.append("Ultimo aggiornamento: ").append(dataCalcolo).append("\n\n");
        
        riassunto.append("UTENTI:\n");
        riassunto.append("- Totale: ").append(totaleUtenti).append("\n");
        riassunto.append("- Organizzatori: ").append(numeroOrganizzatori).append("\n");
        riassunto.append("- Giudici: ").append(numeroGiudici).append("\n");
        riassunto.append("- Partecipanti: ").append(numeroPartecipanti).append("\n\n");
        
        riassunto.append("HACKATHON:\n");
        riassunto.append("- Totale: ").append(totaleHackathon).append("\n");
        riassunto.append("- Attivi: ").append(hackathonAttivi).append("\n");
        riassunto.append("- Conclusi: ").append(hackathonConclusi).append("\n\n");
        
        riassunto.append("TEAM E VALUTAZIONI:\n");
        riassunto.append("- Team totali: ").append(totaleTeam).append("\n");
        riassunto.append("- Valutazioni: ").append(totaleValutazioni).append("\n");
        riassunto.append("- Media voti: ").append(String.valueOf(Math.round(mediaVotiGenerale * 100.0) / 100.0)).append("/10\n\n");
        
        riassunto.append("DOCUMENTI:\n");
        riassunto.append("- Totali: ").append(totaleDocumenti).append("\n");
        riassunto.append("- Validati: ").append(documentiValidati).append("\n");
        riassunto.append("- Storage utilizzato: ").append(getDimensioneTotaleStorageFormattata()).append("\n\n");
        
        riassunto.append("KPI PRINCIPALI:\n");
        for (Map.Entry<String, Double> entry : kpi.entrySet()) {
            riassunto.append("- ").append(entry.getKey()).append(": ")
                     .append(String.valueOf(Math.round(entry.getValue() * 10.0) / 10.0));
            if (entry.getKey().contains("tasso") || entry.getKey().contains("utilizzo") || entry.getKey().contains("efficienza")) {
                riassunto.append("%");
            }
            riassunto.append("\n");
        }
        
        return riassunto.toString();
    }
    
    @Override
    public String toString() {
        return "Statistics{tipo='" + tipo + "', periodo='" + periodo + "', aggiornate=" + aggiornate + ", dataCalcolo=" + dataCalcolo + "}";
    }
}
