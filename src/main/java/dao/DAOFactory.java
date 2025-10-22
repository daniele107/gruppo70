package dao;

import dao.postgres.*;
import database.ConnectionManager;

/**
 * Factory per la creazione delle istanze dei DAO.
 * Centralizza la creazione delle implementazioni concrete e permette
 * di cambiare facilmente l'implementazione senza modificare il codice client.
 */
public final class DAOFactory {
    
    private final ConnectionManager connectionManager;
    
    /**
     * Costruttore privato per evitare istanziazione diretta
     */
    private DAOFactory(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    /**
     * Crea una nuova istanza della factory con il ConnectionManager specificato
     * 
     * @param connectionManager il ConnectionManager da utilizzare
     * @return una nuova istanza della DAOFactory
     */
    public static DAOFactory create(ConnectionManager connectionManager) {
        return new DAOFactory(connectionManager);
    }
    
    /**
     * Crea un'istanza di EventDAO
     * 
     * @return un'istanza di EventDAO
     */
    public EventDAO eventDAO() {
        return new EventPostgresDAO(connectionManager);
    }
    
    /**
     * Crea un'istanza di HackathonDAO
     * 
     * @return un'istanza di HackathonDAO
     */
    public HackathonDAO hackathonDAO() {
        return new HackathonPostgresDAO(connectionManager);
    }
    
    /**
     * Crea un'istanza di UtenteDAO
     * 
     * @return un'istanza di UtenteDAO
     */
    public UtenteDAO utenteDAO() {
        return new UtentePostgresDAO(connectionManager);
    }
    
    /**
     * Crea un'istanza di TeamDAO
     * 
     * @return un'istanza di TeamDAO
     */
    public TeamDAO teamDAO() {
        return new TeamPostgresDAO(connectionManager);
    }
    
    /**
     * Crea un'istanza di RegistrazioneDAO
     * 
     * @return un'istanza di RegistrazioneDAO
     */
    public RegistrazioneDAO registrazioneDAO() {
        return new RegistrazionePostgresDAO(connectionManager);
    }
    
    /**
     * Crea un'istanza di ProgressDAO
     * 
     * @return un'istanza di ProgressDAO
     */
    public ProgressDAO progressDAO() {
        return new ProgressPostgresDAO(connectionManager);
    }
    
    /**
     * Crea un'istanza di ValutazioneDAO
     * 
     * @return un'istanza di ValutazioneDAO
     */
    public ValutazioneDAO valutazioneDAO() {
        return new ValutazionePostgresDAO(connectionManager);
    }
    
    /**
     * Crea un'istanza di NotificationDAO
     * 
     * @return un'istanza di NotificationDAO
     */
    public NotificationDAO notificationDAO() {
        return new NotificationPostgresDAO(connectionManager);
    }
    
    /**
     * Crea un'istanza di DocumentoDAO
     * 
     * @return un'istanza di DocumentoDAO
     */
    public DocumentoDAO documentoDAO() {
        return new DocumentoPostgresDAO(connectionManager);
    }
    
    /**
     * Crea un'istanza di AuditLogDAO
     * 
     * @return un'istanza di AuditLogDAO
     */
    public AuditLogDAO auditLogDAO() {
        return new AuditLogPostgresDAO(connectionManager);
    }

    /**
     * Crea un'istanza di ProgressCommentDAO
     *
     * @return un'istanza di ProgressCommentDAO
     */
    public dao.ProgressCommentDAO progressCommentDAO() {
        return new dao.postgres.ProgressCommentPostgresDAO(connectionManager);
    }

    /**
     * Crea un'istanza di RankingSnapshotDAO
     *
     * @return un'istanza di RankingSnapshotDAO
     */
    public dao.RankingSnapshotDAO rankingSnapshotDAO() {
        return new dao.postgres.RankingSnapshotPostgresDAO(connectionManager);
    }
}
