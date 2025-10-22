package model;

import java.time.LocalDateTime;

/**
 * Classe che rappresenta un documento caricato nel sistema.
 * Gestisce metadati e informazioni sui file caricati dai team.
 */
public class Documento {
    
    private int id;
    private int teamId;
    private int hackathonId;
    private String nome;
    private String percorso;
    private String tipo;
    private long dimensione;
    private String hash;
    private LocalDateTime dataCaricamento;
    private int utenteCaricamento;
    private String descrizione;
    private boolean validato;
    private int validatoreId;
    private LocalDateTime dataValidazione;
    
    /**
     * Costruttore vuoto per la creazione di istanze
     */
    public Documento() {
        this.dataCaricamento = LocalDateTime.now();
        this.validato = false;
    }
    
    /**
     * Costruttore con parametri principali
     *
     * @param teamId l'ID del team che carica il documento
     * @param hackathonId l'ID dell'hackathon
     * @param nome il nome del file
     * @param percorso il percorso del file
     * @param tipo il tipo MIME del file
     * @param dimensione la dimensione in bytes
     * @param utenteCaricamento l'ID dell'utente che carica il file
     */
    public Documento(int teamId, int hackathonId, String nome, String percorso, 
                    String tipo, long dimensione, int utenteCaricamento) {
        this();
        this.teamId = teamId;
        this.hackathonId = hackathonId;
        this.nome = nome;
        this.percorso = percorso;
        this.tipo = tipo;
        this.dimensione = dimensione;
        this.utenteCaricamento = utenteCaricamento;
    }
    
    /**
     * Costruttore completo
     *
     * @param teamId l'ID del team
     * @param hackathonId l'ID dell'hackathon
     * @param nome il nome del file
     * @param percorso il percorso del file
     * @param tipo il tipo MIME del file
     * @param dimensione la dimensione in bytes
     * @param hash l'hash del file per verifica integrità
     * @param utenteCaricamento l'ID dell'utente che carica il file
     * @param descrizione descrizione opzionale del documento
     */
    public Documento(int teamId, int hackathonId, String nome, String percorso, 
                    String tipo, long dimensione, String hash, int utenteCaricamento, String descrizione) {
        this(teamId, hackathonId, nome, percorso, tipo, dimensione, utenteCaricamento);
        this.hash = hash;
        this.descrizione = descrizione;
    }
    
    // Getters e Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getTeamId() {
        return teamId;
    }
    
    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
    
    public int getHackathonId() {
        return hackathonId;
    }
    
    public void setHackathonId(int hackathonId) {
        this.hackathonId = hackathonId;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getPercorso() {
        return percorso;
    }
    
    public void setPercorso(String percorso) {
        this.percorso = percorso;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public long getDimensione() {
        return dimensione;
    }
    
    public void setDimensione(long dimensione) {
        this.dimensione = dimensione;
    }
    
    public String getHash() {
        return hash;
    }
    
    public void setHash(String hash) {
        this.hash = hash;
    }
    
    public LocalDateTime getDataCaricamento() {
        return dataCaricamento;
    }
    
    public void setDataCaricamento(LocalDateTime dataCaricamento) {
        this.dataCaricamento = dataCaricamento;
    }
    
    public int getUtenteCaricamento() {
        return utenteCaricamento;
    }
    
    public void setUtenteCaricamento(int utenteCaricamento) {
        this.utenteCaricamento = utenteCaricamento;
    }
    
    public String getDescrizione() {
        return descrizione;
    }
    
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    
    public boolean isValidato() {
        return validato;
    }
    
    public void setValidato(boolean validato) {
        this.validato = validato;
    }
    
    public int getValidatoreId() {
        return validatoreId;
    }
    
    public void setValidatoreId(int validatoreId) {
        this.validatoreId = validatoreId;
    }
    
    public LocalDateTime getDataValidazione() {
        return dataValidazione;
    }
    
    public void setDataValidazione(LocalDateTime dataValidazione) {
        this.dataValidazione = dataValidazione;
    }
    
    // Metodi di utilità
    
    /**
     * Restituisce la dimensione del file in formato leggibile
     *
     * @return stringa con la dimensione formattata (es: "1.5 MB")
     */
    public String getDimensioneFormattata() {
        if (dimensione < 1024) {
            return dimensione + " B";
        } else if (dimensione < 1024 * 1024) {
            return String.valueOf(Math.round(dimensione / 102.4) / 10.0) + " KB";
        } else if (dimensione < 1024 * 1024 * 1024) {
            return String.valueOf(Math.round(dimensione / (102.4 * 1024.0)) / 10.0) + " MB";
        } else {
            return String.valueOf(Math.round(dimensione / (102.4 * 1024.0 * 1024.0)) / 10.0) + " GB";
        }
    }
    
    /**
     * Verifica se il documento è un'immagine
     *
     * @return true se il documento è un'immagine
     */
    public boolean isImmagine() {
        if (tipo == null) return false;
        return tipo.startsWith("image/");
    }
    
    /**
     * Verifica se il documento è un PDF
     *
     * @return true se il documento è un PDF
     */
    public boolean isPdf() {
        return "application/pdf".equals(tipo);
    }
    
    /**
     * Verifica se il documento è un documento di testo
     *
     * @return true se il documento è di testo
     */
    public boolean isDocumentoTesto() {
        if (tipo == null) return false;
        return tipo.startsWith("text/") || 
               tipo.equals("application/msword") ||
               tipo.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }
    
    /**
     * Verifica se il documento è un archivio
     *
     * @return true se il documento è un archivio
     */
    public boolean isArchivio() {
        if (tipo == null) return false;
        return tipo.equals("application/zip") ||
               tipo.equals("application/x-rar-compressed") ||
               tipo.equals("application/x-tar") ||
               tipo.equals("application/gzip");
    }
    
    /**
     * Ottiene l'estensione del file dal nome
     *
     * @return l'estensione del file o stringa vuota se non presente
     */
    public String getEstensione() {
        if (nome == null || !nome.contains(".")) {
            return "";
        }
        return nome.substring(nome.lastIndexOf(".") + 1).toLowerCase();
    }
    
    /**
     * Valida il documento impostando il validatore e la data
     *
     * @param validatoreId l'ID dell'utente che valida il documento
     */
    public void valida(int validatoreId) {
        this.validato = true;
        this.validatoreId = validatoreId;
        this.dataValidazione = LocalDateTime.now();
    }
    
    /**
     * Rimuove la validazione del documento
     */
    public void rimuoviValidazione() {
        this.validato = false;
        this.validatoreId = 0;
        this.dataValidazione = null;
    }
    
    @Override
    public String toString() {
        return "Documento{id=" + id + ", nome='" + nome + "', tipo='" + tipo + "', dimensione=" + getDimensioneFormattata() + ", validato=" + validato + "}";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Documento documento = (Documento) obj;
        return id == documento.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
