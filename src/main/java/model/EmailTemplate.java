package model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Classe che rappresenta un template email per il sistema.
 * Gestisce i template per diversi tipi di comunicazioni automatiche.
 */
public class EmailTemplate {
    
    private int id;
    private String nome;
    private String tipo;
    private String oggetto;
    private String corpo;
    private String corpoHtml;
    private Map<String, String> variabili;
    private boolean attivo;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataModifica;
    private int utenteCreatore;
    private String lingua;
    private int priorita;
    
    // Tipi di template predefiniti
    public enum TipoTemplate {
        BENVENUTO("Benvenuto nel sistema"),
        REGISTRAZIONE_HACKATHON("Registrazione hackathon"),
        CONFERMA_REGISTRAZIONE("Conferma registrazione"),
        RIFIUTO_REGISTRAZIONE("Rifiuto registrazione"),
        INVITO_TEAM("Invito team"),
        RICHIESTA_JOIN("Richiesta join team"),
        ACCETTAZIONE_TEAM("Accettazione nel team"),
        RIFIUTO_TEAM("Rifiuto team"),
        AVVIO_HACKATHON("Avvio hackathon"),
        CONCLUSIONE_HACKATHON("Conclusione hackathon"),
        NUOVO_COMMENTO("Nuovo commento"),
        NUOVA_VALUTAZIONE("Nuova valutazione"),
        RISULTATI_FINALI("Risultati finali"),
        PROMEMORIA("Promemoria"),
        SISTEMA("Comunicazione sistema");
        
        private final String descrizione;
        
        TipoTemplate(String descrizione) {
            this.descrizione = descrizione;
        }
        
        public String getDescrizione() {
            return descrizione;
        }
    }
    
    /**
     * Costruttore vuoto
     */
    public EmailTemplate() {
        this.dataCreazione = LocalDateTime.now();
        this.dataModifica = LocalDateTime.now();
        this.variabili = new HashMap<>();
        this.attivo = true;
        this.lingua = "it";
        this.priorita = 5;
    }
    
    /**
     * Costruttore con parametri principali
     *
     * @param nome il nome del template
     * @param tipo il tipo di template
     * @param oggetto l'oggetto dell'email
     * @param corpo il corpo dell'email
     */
    public EmailTemplate(String nome, String tipo, String oggetto, String corpo) {
        this();
        this.nome = nome;
        this.tipo = tipo;
        this.oggetto = oggetto;
        this.corpo = corpo;
    }
    
    /**
     * Costruttore completo
     *
     * @param nome il nome del template
     * @param tipo il tipo di template
     * @param oggetto l'oggetto dell'email
     * @param corpo il corpo dell'email
     * @param corpoHtml il corpo HTML dell'email
     * @param utenteCreatore l'ID dell'utente creatore
     */
    public EmailTemplate(String nome, String tipo, String oggetto, String corpo, 
                        String corpoHtml, int utenteCreatore) {
        this(nome, tipo, oggetto, corpo);
        this.corpoHtml = corpoHtml;
        this.utenteCreatore = utenteCreatore;
    }
    
    // Getters e Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getOggetto() {
        return oggetto;
    }
    
    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }
    
    public String getCorpo() {
        return corpo;
    }
    
    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }
    
    public String getCorpoHtml() {
        return corpoHtml;
    }
    
    public void setCorpoHtml(String corpoHtml) {
        this.corpoHtml = corpoHtml;
    }
    
    public Map<String, String> getVariabili() {
        return variabili;
    }
    
    public void setVariabili(Map<String, String> variabili) {
        this.variabili = variabili;
    }
    
    public boolean isAttivo() {
        return attivo;
    }
    
    public void setAttivo(boolean attivo) {
        this.attivo = attivo;
    }
    
    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }
    
    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }
    
    public LocalDateTime getDataModifica() {
        return dataModifica;
    }
    
    public void setDataModifica(LocalDateTime dataModifica) {
        this.dataModifica = dataModifica;
    }
    
    public int getUtenteCreatore() {
        return utenteCreatore;
    }
    
    public void setUtenteCreatore(int utenteCreatore) {
        this.utenteCreatore = utenteCreatore;
    }
    
    public String getLingua() {
        return lingua;
    }
    
    public void setLingua(String lingua) {
        this.lingua = lingua;
    }
    
    public int getPriorita() {
        return priorita;
    }
    
    public void setPriorita(int priorita) {
        this.priorita = priorita;
    }
    
    // Metodi di utilità
    
    /**
     * Aggiunge una variabile al template
     *
     * @param nome il nome della variabile
     * @param descrizione la descrizione della variabile
     */
    public void aggiungiVariabile(String nome, String descrizione) {
        variabili.put(nome, descrizione);
    }
    
    /**
     * Rimuove una variabile dal template
     *
     * @param nome il nome della variabile da rimuovere
     */
    public void rimuoviVariabile(String nome) {
        variabili.remove(nome);
    }
    
    /**
     * Verifica se il template ha una variabile specifica
     *
     * @param nome il nome della variabile
     * @return true se la variabile esiste
     */
    public boolean hasVariabile(String nome) {
        return variabili.containsKey(nome);
    }
    
    /**
     * Sostituisce le variabili nel testo con i valori forniti
     *
     * @param testo il testo contenente le variabili
     * @param valori i valori delle variabili
     * @return il testo con le variabili sostituite
     */
    public String sostituisciVariabili(String testo, Map<String, String> valori) {
        if (testo == null || valori == null) {
            return testo;
        }
        
        String risultato = testo;
        for (Map.Entry<String, String> entry : valori.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            String valore = entry.getValue() != null ? entry.getValue() : "";
            risultato = risultato.replace(placeholder, valore);
        }
        
        return risultato;
    }
    
    /**
     * Genera l'oggetto dell'email con le variabili sostituite
     *
     * @param valori i valori delle variabili
     * @return l'oggetto dell'email processato
     */
    public String generaOggetto(Map<String, String> valori) {
        return sostituisciVariabili(oggetto, valori);
    }
    
    /**
     * Genera il corpo dell'email con le variabili sostituite
     *
     * @param valori i valori delle variabili
     * @return il corpo dell'email processato
     */
    public String generaCorpo(Map<String, String> valori) {
        return sostituisciVariabili(corpo, valori);
    }
    
    /**
     * Genera il corpo HTML dell'email con le variabili sostituite
     *
     * @param valori i valori delle variabili
     * @return il corpo HTML dell'email processato
     */
    public String generaCorpoHtml(Map<String, String> valori) {
        if (corpoHtml == null) {
            return null;
        }
        return sostituisciVariabili(corpoHtml, valori);
    }
    
    /**
     * Verifica se il template è valido per l'invio
     *
     * @return true se il template è valido
     */
    public boolean isValido() {
        return attivo && 
               nome != null && !nome.trim().isEmpty() &&
               tipo != null && !tipo.trim().isEmpty() &&
               oggetto != null && !oggetto.trim().isEmpty() &&
               corpo != null && !corpo.trim().isEmpty();
    }
    
    /**
     * Aggiorna il timestamp di modifica
     */
    public void aggiornaModifica() {
        this.dataModifica = LocalDateTime.now();
    }
    
    /**
     * Crea una copia del template
     *
     * @return una copia del template
     */
    public EmailTemplate copia() {
        EmailTemplate copia = new EmailTemplate();
        copia.nome = this.nome + " (Copia)";
        copia.tipo = this.tipo;
        copia.oggetto = this.oggetto;
        copia.corpo = this.corpo;
        copia.corpoHtml = this.corpoHtml;
        copia.variabili = new HashMap<>(this.variabili);
        copia.attivo = false; // Le copie sono disattivate per default
        copia.lingua = this.lingua;
        copia.priorita = this.priorita;
        
        return copia;
    }
    
    /**
     * Ottiene le variabili presenti nel testo
     *
     * @param testo il testo da analizzare
     * @return lista delle variabili trovate
     */
    public static java.util.List<String> estraiVariabili(String testo) {
        java.util.List<String> variabiliTrovate = new java.util.ArrayList<>();
        if (testo == null) {
            return variabiliTrovate;
        }
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\$\\{([^}]+)\\}");
        java.util.regex.Matcher matcher = pattern.matcher(testo);
        
        while (matcher.find()) {
            String variabile = matcher.group(1);
            if (!variabiliTrovate.contains(variabile)) {
                variabiliTrovate.add(variabile);
            }
        }
        
        return variabiliTrovate;
    }
    
    // Template predefiniti
    
    /**
     * Crea un template di benvenuto
     *
     * @return template di benvenuto
     */
    public static EmailTemplate creaBenvenuto() {
        EmailTemplate template = new EmailTemplate(
            "Benvenuto",
            TipoTemplate.BENVENUTO.name(),
            "Benvenuto in Hackathon Manager, ${nome}!",
            "Caro ${nome} ${cognome},\n\n" +
            "Benvenuto in Hackathon Manager! Siamo felici di averti nella nostra community.\n\n" +
            "Il tuo account è stato creato con successo:\n" +
            "- Email: ${email}\n" +
            "- Ruolo: ${ruolo}\n\n" +
            "Ora puoi:\n" +
            "- Esplorare gli hackathon disponibili\n" +
            "- Registrarti agli eventi\n" +
            "- Creare o unirti a un team\n\n" +
            "Buona fortuna!\n\n" +
            "Il team di Hackathon Manager"
        );
        
        template.aggiungiVariabile("nome", "Nome dell'utente");
        template.aggiungiVariabile("cognome", "Cognome dell'utente");
        template.aggiungiVariabile("email", "Email dell'utente");
        template.aggiungiVariabile("ruolo", "Ruolo dell'utente");
        
        return template;
    }
    
    /**
     * Crea un template per conferma registrazione hackathon
     *
     * @return template conferma registrazione
     */
    public static EmailTemplate creaConfermaRegistrazione() {
        EmailTemplate template = new EmailTemplate(
            "Conferma Registrazione",
            TipoTemplate.CONFERMA_REGISTRAZIONE.name(),
            "Registrazione confermata per ${nomeHackathon}",
            "Caro ${nome} ${cognome},\n\n" +
            "La tua registrazione per l'hackathon \"${nomeHackathon}\" è stata confermata!\n\n" +
            "Dettagli dell'evento:\n" +
            "- Nome: ${nomeHackathon}\n" +
            "- Data: ${dataInizio} - ${dataFine}\n" +
            "- Sede: ${sede}\n" +
            "- Ruolo: ${ruolo}\n\n" +
            "Prossimi passi:\n" +
            "- Crea o unisciti a un team\n" +
            "- Preparati per l'evento\n" +
            "- Controlla gli aggiornamenti\n\n" +
            "Ci vediamo all'hackathon!\n\n" +
            "Il team organizzatore"
        );
        
        template.aggiungiVariabile("nome", "Nome dell'utente");
        template.aggiungiVariabile("cognome", "Cognome dell'utente");
        template.aggiungiVariabile("nomeHackathon", "Nome dell'hackathon");
        template.aggiungiVariabile("dataInizio", "Data di inizio");
        template.aggiungiVariabile("dataFine", "Data di fine");
        template.aggiungiVariabile("sede", "Sede dell'evento");
        template.aggiungiVariabile("ruolo", "Ruolo nell'hackathon");
        
        return template;
    }
    
    /**
     * Crea un template per avvio hackathon
     *
     * @return template avvio hackathon
     */
    public static EmailTemplate creaAvvioHackathon() {
        EmailTemplate template = new EmailTemplate(
            "Avvio Hackathon",
            TipoTemplate.AVVIO_HACKATHON.name(),
            "🚀 ${nomeHackathon} è iniziato!",
            "Cari partecipanti,\n\n" +
            "L'hackathon \"${nomeHackathon}\" è ufficialmente iniziato! 🎉\n\n" +
            "PROBLEMA DA RISOLVERE:\n" +
            "${problemaDescrizione}\n\n" +
            "INFORMAZIONI IMPORTANTI:\n" +
            "- Durata: fino al ${dataFine}\n" +
            "- Team registrati: ${numeroTeam}\n" +
            "- Partecipanti totali: ${numeroPartecipanti}\n\n" +
            "COSA FARE ORA:\n" +
            "- Coordinarsi con il proprio team\n" +
            "- Iniziare a lavorare sulla soluzione\n" +
            "- Caricare i progressi regolarmente\n" +
            "- Rispettare le scadenze\n\n" +
            "Buona fortuna a tutti!\n\n" +
            "Gli organizzatori"
        );
        
        template.aggiungiVariabile("nomeHackathon", "Nome dell'hackathon");
        template.aggiungiVariabile("problemaDescrizione", "Descrizione del problema");
        template.aggiungiVariabile("dataFine", "Data di fine");
        template.aggiungiVariabile("numeroTeam", "Numero di team");
        template.aggiungiVariabile("numeroPartecipanti", "Numero di partecipanti");
        
        return template;
    }
    
    @Override
    public String toString() {
        return "EmailTemplate{id=" + id + ", nome='" + nome + "', tipo='" + tipo + "', attivo=" + attivo + "}";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        EmailTemplate template = (EmailTemplate) obj;
        return id == template.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
