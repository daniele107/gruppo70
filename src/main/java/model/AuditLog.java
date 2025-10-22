package model;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Rappresenta un'entry nel log di audit del sistema.
 * Traccia tutte le operazioni significative eseguite dagli utenti.
 */
public class AuditLog {
    
    public enum AuditAction {
        // Autenticazione
        LOGIN("LOGIN", "Accesso utente"),
        LOGOUT("LOGOUT", "Uscita utente"),
        LOGIN_FAILED("LOGIN_FAILED", "Tentativo accesso fallito"),
        
        // Gestione utenti
        CREATE_USER("CREATE_USER", "Creazione utente"),
        UPDATE_USER("UPDATE_USER", "Modifica utente"),
        DELETE_USER("DELETE_USER", "Eliminazione utente"),
        
        // Gestione hackathon
        CREATE_HACKATHON("CREATE_HACKATHON", "Creazione hackathon"),
        UPDATE_HACKATHON("UPDATE_HACKATHON", "Modifica hackathon"),
        DELETE_HACKATHON("DELETE_HACKATHON", "Eliminazione hackathon"),
        
        // Gestione team
        CREATE_TEAM("CREATE_TEAM", "Creazione team"),
        UPDATE_TEAM("UPDATE_TEAM", "Modifica team"),
        DELETE_TEAM("DELETE_TEAM", "Eliminazione team"),
        JOIN_TEAM("JOIN_TEAM", "Adesione a team"),
        LEAVE_TEAM("LEAVE_TEAM", "Uscita da team"),
        TEAM_FINALIZATION("TEAM_FINALIZATION", "Finalizzazione team"),
        
        // Gestione registrazioni
        REGISTER_HACKATHON("REGISTER_HACKATHON", "Registrazione hackathon"),
        APPROVE_REGISTRATION("APPROVE_REGISTRATION", "Approvazione registrazione"),
        REJECT_REGISTRATION("REJECT_REGISTRATION", "Rifiuto registrazione"),
        
        // Gestione documenti
        UPLOAD_DOCUMENT("UPLOAD_DOCUMENT", "Caricamento documento"),
        DOWNLOAD_DOCUMENT("DOWNLOAD_DOCUMENT", "Download documento"),
        DELETE_DOCUMENT("DELETE_DOCUMENT", "Eliminazione documento"),
        VALIDATE_DOCUMENT("VALIDATE_DOCUMENT", "Validazione documento"),
        
        // Gestione valutazioni
        CREATE_EVALUATION("CREATE_EVALUATION", "Creazione valutazione"),
        UPDATE_EVALUATION("UPDATE_EVALUATION", "Modifica valutazione"),
        DELETE_EVALUATION("DELETE_EVALUATION", "Eliminazione valutazione"),
        
        // Operazioni sistema
        BACKUP_DATABASE("BACKUP_DATABASE", "Backup database"),
        BACKUP_FILES("BACKUP_FILES", "Backup file"),
        SEND_EMAIL("SEND_EMAIL", "Invio email"),
        SEND_NOTIFICATION("SEND_NOTIFICATION", "Invio notifica"),
        
        // Sicurezza
        ACCESS_DENIED("ACCESS_DENIED", "Accesso negato"),
        PERMISSION_ERROR("PERMISSION_ERROR", "Errore permessi"),
        SECURITY_VIOLATION("SECURITY_VIOLATION", "Violazione sicurezza");
        
        private final String code;
        private final String description;
        
        AuditAction(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
    }
    
    public enum AuditResult {
        SUCCESS("SUCCESS"),
        FAILURE("FAILURE"),
        WARNING("WARNING");
        
        private final String value;
        
        AuditResult(String value) {
            this.value = value;
        }
        
        public String getValue() { return value; }
    }
    
    private int id;
    private Integer utenteId; // Nullable per operazioni di sistema
    private AuditAction azione;
    private String risorsa;
    private Integer risorsaId; // Nullable
    private String dettagli;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;
    private String sessionId;
    private AuditResult risultato;
    private Integer durataMsec; // Nullable
    private Map<String, Object> metadata; // JSONB
    
    /**
     * Costruttore vuoto per la deserializzazione
     */
    public AuditLog() {
        this.timestamp = LocalDateTime.now();
        this.risultato = AuditResult.SUCCESS;
    }
    
    /**
     * Costruttore principale per operazioni utente
     */
    public AuditLog(Integer utenteId, AuditAction azione, String risorsa, Integer risorsaId, String dettagli) {
        this();
        this.utenteId = utenteId;
        this.azione = azione;
        this.risorsa = risorsa;
        this.risorsaId = risorsaId;
        this.dettagli = dettagli;
    }
    
    /**
     * Costruttore per operazioni di sistema
     */
    public AuditLog(AuditAction azione, String risorsa, String dettagli) {
        this();
        this.utenteId = null; // Operazione di sistema
        this.azione = azione;
        this.risorsa = risorsa;
        this.dettagli = dettagli;
    }
    
    
    /**
     * Builder pattern per AuditLog
     */
    public static class Builder {
        private Integer utenteId;
        private AuditAction azione;
        private String risorsa;
        private Integer risorsaId;
        private String dettagli;
        private String ipAddress;
        private String userAgent;
        private String sessionId;
        
        public Builder(Integer utenteId, AuditAction azione, String risorsa) {
            this.utenteId = utenteId;
            this.azione = azione;
            this.risorsa = risorsa;
        }
        
        public Builder risorsaId(Integer risorsaId) {
            this.risorsaId = risorsaId;
            return this;
        }
        
        public Builder dettagli(String dettagli) {
            this.dettagli = dettagli;
            return this;
        }
        
        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }
        
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }
        
        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }
        
        public AuditLog build() {
            AuditLog auditLog = new AuditLog(utenteId, azione, risorsa, risorsaId, dettagli);
            auditLog.setIpAddress(ipAddress);
            auditLog.setUserAgent(userAgent);
            auditLog.setSessionId(sessionId);
            return auditLog;
        }
    }
    
    /**
     * Imposta il risultato dell'operazione
     */
    public AuditLog withResult(AuditResult risultato) {
        this.risultato = risultato;
        return this;
    }
    
    /**
     * Imposta la durata dell'operazione
     */
    public AuditLog withDuration(long startTime) {
        this.durataMsec = (int)(System.currentTimeMillis() - startTime);
        return this;
    }
    
    /**
     * Imposta i metadati aggiuntivi
     */
    public AuditLog withMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }
    
    /**
     * Verifica se l'entry rappresenta un'operazione critica per la sicurezza
     */
    public boolean isCritical() {
        return azione == AuditAction.LOGIN_FAILED ||
               azione == AuditAction.ACCESS_DENIED ||
               azione == AuditAction.PERMISSION_ERROR ||
               azione == AuditAction.SECURITY_VIOLATION ||
               risultato == AuditResult.FAILURE;
    }
    
    /**
     * Verifica se l'entry rappresenta un'operazione di sistema
     */
    public boolean isSystemOperation() {
        return utenteId == null;
    }
    
    /**
     * Genera una descrizione leggibile dell'operazione
     */
    public String getReadableDescription() {
        StringBuilder desc = new StringBuilder();
        
        if (utenteId != null) {
            desc.append("Utente ").append(utenteId).append(" ");
        } else {
            desc.append("Sistema ");
        }
        
        desc.append(azione.getDescription().toLowerCase());
        
        if (risorsa != null) {
            desc.append(" su ").append(risorsa);
            if (risorsaId != null) {
                desc.append(" (ID: ").append(risorsaId).append(")");
            }
        }
        
        if (risultato != AuditResult.SUCCESS) {
            desc.append(" - ").append(risultato.getValue());
        }
        
        return desc.toString();
    }
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Integer getUtenteId() { return utenteId; }
    public void setUtenteId(Integer utenteId) { this.utenteId = utenteId; }
    
    public AuditAction getAzione() { return azione; }
    public void setAzione(AuditAction azione) { this.azione = azione; }
    
    public String getRisorsa() { return risorsa; }
    public void setRisorsa(String risorsa) { this.risorsa = risorsa; }
    
    public Integer getRisorsaId() { return risorsaId; }
    public void setRisorsaId(Integer risorsaId) { this.risorsaId = risorsaId; }
    
    public String getDettagli() { return dettagli; }
    public void setDettagli(String dettagli) { this.dettagli = dettagli; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public AuditResult getRisultato() { return risultato; }
    public void setRisultato(AuditResult risultato) { this.risultato = risultato; }
    
    public Integer getDurataMsec() { return durataMsec; }
    public void setDurataMsec(Integer durataMsec) { this.durataMsec = durataMsec; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    @Override
    public String toString() {
        return String.format("AuditLog{id=%d, utente=%s, azione=%s, risorsa=%s, risultato=%s, timestamp=%s}",
                           id, utenteId, azione, risorsa, risultato, timestamp);
    }
}
