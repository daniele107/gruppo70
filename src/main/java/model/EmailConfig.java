package model;

import java.util.Properties;

/**
 * Configurazione per il sistema email SMTP.
 * Gestisce le impostazioni del server email e le credenziali di autenticazione.
 */
public class EmailConfig {
    // Costanti per evitare duplicazione di stringhe
    private static final String DEFAULT_FROM_NAME = "Hackathon Manager";
    private static final String TIMEOUT_VALUE = "10000";
    private String smtpHost;
    private int smtpPort;
    private String username;
    private String password;
    private boolean tlsEnabled;
    private boolean authRequired;
    private String fromEmail;
    private String fromName;
    
    // Configurazioni predefinite per provider comuni
    public static final EmailConfig GMAIL_CONFIG = new EmailConfig(
        "smtp.gmail.com", 587, true, true, DEFAULT_FROM_NAME
    );
    
    public static final EmailConfig OUTLOOK_CONFIG = new EmailConfig(
        "smtp-mail.outlook.com", 587, true, true, DEFAULT_FROM_NAME
    );
    
    public static final EmailConfig YAHOO_CONFIG = new EmailConfig(
        "smtp.mail.yahoo.com", 587, true, true, DEFAULT_FROM_NAME
    );
    
    /**
     * Costruttore vuoto per configurazione manuale
     */
    public EmailConfig() {
        this.tlsEnabled = true;
        this.authRequired = true;
        this.fromName = DEFAULT_FROM_NAME;
    }
    
    /**
     * Costruttore per configurazioni predefinite
     */
    private EmailConfig(String smtpHost, int smtpPort, boolean tlsEnabled, boolean authRequired, String fromName) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.tlsEnabled = tlsEnabled;
        this.authRequired = authRequired;
        this.fromName = fromName;
    }
    
    /**
     * Configura le credenziali email
     */
    public void setCredentials(String username, String password, String fromEmail) {
        this.username = username;
        this.password = password;
        this.fromEmail = fromEmail;
    }
    
    /**
     * Genera le proprietà JavaMail per la connessione SMTP
     */
    public Properties getMailProperties() {
        Properties props = new Properties();
        
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort));
        props.put("mail.smtp.auth", String.valueOf(authRequired));
        
        if (tlsEnabled) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }
        
        // Configurazioni di sicurezza
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", smtpHost);
        
        // Timeout configurations
        props.put("mail.smtp.connectiontimeout", TIMEOUT_VALUE); // 10 seconds
        props.put("mail.smtp.timeout", TIMEOUT_VALUE); // 10 seconds
        props.put("mail.smtp.writetimeout", TIMEOUT_VALUE); // 10 seconds
        
        return props;
    }
    
    /**
     * Verifica se la configurazione è valida
     */
    public boolean isValid() {
        return smtpHost != null && !smtpHost.trim().isEmpty() &&
               smtpPort > 0 && smtpPort <= 65535 &&
               fromEmail != null && !fromEmail.trim().isEmpty() &&
               username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty();
    }
    
    // Getters e Setters
    public String getSmtpHost() { return smtpHost; }
    public void setSmtpHost(String smtpHost) { this.smtpHost = smtpHost; }
    
    public int getSmtpPort() { return smtpPort; }
    public void setSmtpPort(int smtpPort) { this.smtpPort = smtpPort; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public boolean isTlsEnabled() { return tlsEnabled; }
    public void setTlsEnabled(boolean tlsEnabled) { this.tlsEnabled = tlsEnabled; }
    
    public boolean isAuthRequired() { return authRequired; }
    public void setAuthRequired(boolean authRequired) { this.authRequired = authRequired; }
    
    public String getFromEmail() { return fromEmail; }
    public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }
    
    public String getFromName() { return fromName; }
    public void setFromName(String fromName) { this.fromName = fromName; }
    
    @Override
    public String toString() {
        return "EmailConfig{host='" + smtpHost + "', port=" + smtpPort + ", from='" + fromEmail + "', tls=" + tlsEnabled + "}";
    }
}
