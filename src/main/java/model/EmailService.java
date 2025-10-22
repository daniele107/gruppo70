package model;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Servizio per l'invio di email tramite SMTP.
 * Supporta invio sincrono e asincrono con gestione degli errori.
 */
public class EmailService {
    
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    private EmailConfig config;
    private Session mailSession;
    private ExecutorService executorService;
    private boolean isConfigured = false;
    
    public EmailService() {
        this.executorService = Executors.newFixedThreadPool(3); // Pool per invii asincroni
    }
    
    /**
     * Configura il servizio email
     */
    public boolean configure(EmailConfig config) {
        if (config == null || !config.isValid()) {
            LOGGER.severe("Configurazione email non valida");
            return false;
        }
        
        try {
            this.config = config;
            
            // Crea l'autenticatore
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getUsername(), config.getPassword());
                }
            };
            
            // Crea la sessione mail
            this.mailSession = Session.getInstance(config.getMailProperties(), authenticator);
            this.mailSession.setDebug(false); // Imposta a true per debug dettagliato
            
            this.isConfigured = true;
            LOGGER.info("Servizio email configurato correttamente: " + config.toString());
            
            return true;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nella configurazione del servizio email", e);
            this.isConfigured = false;
            return false;
        }
    }
    
    /**
     * Invia un'email in modo sincrono
     */
    public boolean sendEmail(String to, String subject, String body) {
        return sendEmail(to, subject, body, false);
    }
    
    /**
     * Invia un'email con opzione HTML
     */
    public boolean sendEmail(String to, String subject, String body, boolean isHtml) {
        if (!isConfigured) {
            LOGGER.severe("Servizio email non configurato");
            return false;
        }
        
        try {
            // Crea il messaggio
            MimeMessage message = new MimeMessage(mailSession);
            
            // Imposta mittente
            message.setFrom(new InternetAddress(config.getFromEmail(), config.getFromName()));
            
            // Imposta destinatario
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            
            // Imposta oggetto
            message.setSubject(subject, "UTF-8");
            
            // Imposta corpo del messaggio
            if (isHtml) {
                message.setContent(body, "text/html; charset=utf-8");
            } else {
                message.setText(body, "UTF-8");
            }
            
            // Imposta data
            message.setSentDate(new Date());
            
            // Invia il messaggio
            Transport.send(message);
            
            LOGGER.info("Email inviata con successo a " + to + " - Oggetto: " + subject);
            return true;
            
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Errore nell\'invio email a " + to, e);
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore generico nell\'invio email a " + to, e);
            return false;
        }
    }
    
    /**
     * Invia un'email in modo asincrono
     */
    public CompletableFuture<Boolean> sendEmailAsync(String to, String subject, String body) {
        return sendEmailAsync(to, subject, body, false);
    }
    
    /**
     * Invia un'email in modo asincrono con opzione HTML
     */
    public CompletableFuture<Boolean> sendEmailAsync(String to, String subject, String body, boolean isHtml) {
        return CompletableFuture.supplyAsync(() -> {
            return sendEmail(to, subject, body, isHtml);
        }, executorService);
    }
    
    /**
     * Invia email multiple in modo asincrono
     */
    public CompletableFuture<Integer> sendBulkEmailAsync(String[] recipients, String subject, String body, boolean isHtml) {
        return CompletableFuture.supplyAsync(() -> {
            int successCount = 0;
            
            for (String recipient : recipients) {
                if (sendEmail(recipient, subject, body, isHtml)) {
                    successCount++;
                }
                
                // Pausa breve tra invii per evitare rate limiting
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            LOGGER.info("Invio bulk completato: " + successCount + "/" + recipients.length + " email inviate");
            return successCount;
            
        }, executorService);
    }
    
    /**
     * Testa la configurazione inviando un'email di test
     */
    public boolean testConfiguration(String testRecipient) {
        if (!isConfigured) {
            return false;
        }
        
        String subject = "Test Configurazione - Hackathon Manager";
        String body = "Questa è un'email di test per verificare la configurazione SMTP.\n\n" +
                     "Se ricevi questo messaggio, la configurazione è corretta.\n\n" +
                     "Hackathon Manager System\n" +
                     "Data: " + new Date();
        
        return sendEmail(testRecipient, subject, body);
    }
    
    /**
     * Ottiene statistiche del servizio email
     */
    public String getServiceStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== STATISTICHE SERVIZIO EMAIL ===\n");
        stats.append("Configurato: ").append(isConfigured ? "✅ Sì" : "❌ No").append("\n");
        
        if (isConfigured) {
            stats.append("Server SMTP: ").append(config.getSmtpHost()).append(":").append(config.getSmtpPort()).append("\n");
            stats.append("TLS Abilitato: ").append(config.isTlsEnabled() ? "✅ Sì" : "❌ No").append("\n");
            stats.append("Autenticazione: ").append(config.isAuthRequired() ? "✅ Richiesta" : "❌ Non richiesta").append("\n");
            stats.append("Email mittente: ").append(config.getFromEmail()).append("\n");
            stats.append("Nome mittente: ").append(config.getFromName()).append("\n");
        }
        
        return stats.toString();
    }
    
    /**
     * Verifica la connessione al server SMTP
     */
    public boolean testConnection() {
        if (!isConfigured) {
            return false;
        }
        
        try {
            // Testa la connessione senza inviare email
            Transport transport = mailSession.getTransport("smtp");
            transport.connect(config.getSmtpHost(), config.getSmtpPort(), 
                            config.getUsername(), config.getPassword());
            transport.close();
            
            LOGGER.info("Connessione SMTP testata con successo");
            return true;
            
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Errore nel test connessione SMTP", e);
            return false;
        }
    }
    
    /**
     * Chiude il servizio e libera le risorse
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            LOGGER.info("Servizio email terminato");
        }
    }
    
    // Getters
    public boolean isConfigured() { return isConfigured; }
    public EmailConfig getConfig() { return config; }
}
