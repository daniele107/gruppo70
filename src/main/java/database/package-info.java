/**
 * Package per la gestione delle connessioni al database.
 * 
 * <h2>Architettura</h2>
 * <p>Questo package implementa una gestione sicura e thread-safe delle connessioni 
 * al database PostgreSQL utilizzando le seguenti best practices:</p>
 * 
 * <h3>Sicurezza</h3>
 * <ul>
 *   <li><strong>Configurazione esterna</strong>: Credenziali caricate da file properties o variabili d'ambiente</li>
 *   <li><strong>Gestione errori robusta</strong>: Eccezioni custom con contesto specifico</li>
 *   <li><strong>Logging strutturato</strong>: Utilizzo di java.util.logging per tracciabilità</li>
 * </ul>
 * 
 * <h3>Performance</h3>
 * <ul>
 *   <li><strong>Singleton thread-safe</strong>: Initialization-on-demand holder pattern</li>
 *   <li><strong>Gestione transazioni</strong>: Commit/rollback espliciti</li>
 *   <li><strong>Resource management</strong>: Try-with-resources per cleanup automatico</li>
 * </ul>
 * 
 * <h3>Classi principali</h3>
 * <ul>
 *   <li>{@link database.ConnectionManager} - Gestione principale delle connessioni</li>
 *   <li>{@link database.DatabaseConnectionException} - Eccezioni di connessione</li>
 *   <li>{@link database.DatabaseConfigurationException} - Eccezioni di configurazione</li>
 * </ul>
 * 
 * <h3>Configurazione</h3>
 * <p>La configurazione può essere fornita tramite:</p>
 * <ol>
 *   <li>File <code>db.properties</code> nel classpath</li>
 *   <li>Variabile d'ambiente <code>DB_PASSWORD</code></li>
 *   <li>Configurazione di default per ambiente di sviluppo</li>
 * </ol>
 * 
 * @author Hackathon Manager Team
 * @version 1.0
 * @since 1.0
 */
package database;
