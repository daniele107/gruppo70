package service;

import dao.AuditLogDAO;
import dao.DocumentoDAO;
import dao.ProgressCommentDAO;
import dao.RegistrazioneDAO;
import database.ConnectionManager;
import database.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test per DocumentReviewService con Testcontainers PostgreSQL
 */
@Testcontainers
class DocumentReviewServiceTest {

    @Container
    @SuppressWarnings("resource") // Gestito automaticamente da Testcontainers
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("hackathon_test")
            .withUsername("test")
            .withPassword("test");

    private DocumentReviewService service;
    private ProgressCommentDAO mockCommentDAO;
    private DocumentoDAO mockDocumentDAO;
    private RegistrazioneDAO mockRegistrationDAO;
    private AuditLogDAO mockAuditDAO;
    private ConnectionManager mockConnectionManager;

    private static final int JUDGE_ID = 1;
    private static final int DOCUMENT_ID = 100;
    private static final int HACKATHON_ID = 10;
    private static final int TEAM_ID = 5;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @BeforeEach
    void setUp() throws Exception {
        // Setup mock DAOs
        mockCommentDAO = mock(ProgressCommentDAO.class);
        mockDocumentDAO = mock(DocumentoDAO.class);
        mockRegistrationDAO = mock(RegistrazioneDAO.class);
        mockAuditDAO = mock(AuditLogDAO.class);
        mockConnectionManager = mock(ConnectionManager.class);

        // Setup real database connection for integration tests
        Connection realConnection = postgres.createConnection("");
        when(mockConnectionManager.getConnection()).thenReturn(realConnection);

        // Initialize test database schema
        initializeTestSchema(realConnection);

        // Create service instance
        service = new DocumentReviewService(
                mockCommentDAO,
                mockDocumentDAO,
                mockRegistrationDAO,
                mockAuditDAO
        );
    }

    private void initializeTestSchema(Connection connection) throws Exception {
        try (Statement stmt = connection.createStatement()) {
            // Create test tables (simplified)
            stmt.execute("CREATE TABLE IF NOT EXISTS utente (" +
                    "id SERIAL PRIMARY KEY, " +
                    "nome VARCHAR(100), " +
                    "cognome VARCHAR(100), " +
                    "tipo VARCHAR(50)" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS hackathon (" +
                    "id SERIAL PRIMARY KEY, " +
                    "nome VARCHAR(200)" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS team (" +
                    "id SERIAL PRIMARY KEY, " +
                    "nome VARCHAR(200)" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS documents (" +
                    "id SERIAL PRIMARY KEY, " +
                    "title VARCHAR(200), " +
                    "team_id INTEGER" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS judge_comment (" +
                    "id SERIAL PRIMARY KEY, " +
                    "document_id INTEGER, " +
                    "judge_id INTEGER, " +
                    "text TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updated_at TIMESTAMP" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS registrazione (" +
                    "id SERIAL PRIMARY KEY, " +
                    "utente_id INTEGER, " +
                    "hackathon_id INTEGER, " +
                    "ruolo VARCHAR(50)" +
                    ")");

            // Insert test data
            stmt.execute("INSERT INTO utente (id, nome, cognome, tipo) VALUES " +
                    "(1, 'Mario', 'Rossi', 'GIUDICE'), " +
                    "(2, 'Luigi', 'Verdi', 'GIUDICE'), " +
                    "(3, 'Anna', 'Bianchi', 'PARTECIPANTE')");

            stmt.execute("INSERT INTO hackathon (id, nome) VALUES (10, 'Test Hackathon')");
            stmt.execute("INSERT INTO team (id, nome) VALUES (5, 'Test Team')");
            stmt.execute("INSERT INTO documents (id, title, team_id) VALUES (100, 'Test Document', 5)");

            stmt.execute("INSERT INTO registrazione (utente_id, hackathon_id, ruolo) VALUES " +
                    "(1, 10, 'GIUDICE'), " +
                    "(2, 10, 'GIUDICE')");
        }
    }

    @Test
    @DisplayName("Aggiunta commento con successo")
    void testAddComment_Success() throws Exception {
        // Arrange
        String commentText = "Ottimo lavoro sul progetto!";
        
        // Mock document exists and belongs to team
        Documento mockDocument = new Documento();
        mockDocument.setId(DOCUMENT_ID);
        mockDocument.setTeamId(TEAM_ID);
        when(mockDocumentDAO.findById(DOCUMENT_ID)).thenReturn(mockDocument);

        // Mock judge is registered for hackathon
        Registrazione mockRegistration = new Registrazione();
        mockRegistration.setUtenteId(JUDGE_ID);
        mockRegistration.setHackathonId(HACKATHON_ID);
        mockRegistration.setRuolo(Registrazione.Ruolo.GIUDICE);
        when(mockRegistrationDAO.findByUtenteAndHackathon(JUDGE_ID, HACKATHON_ID))
                .thenReturn(mockRegistration);

        // Mock rate limit check (not exceeded)
        when(mockCommentDAO.countByJudgeAndTeamInLastHour(JUDGE_ID, TEAM_ID)).thenReturn(5);

        // Mock successful insert
        when(mockCommentDAO.insert(any(ProgressComment.class))).thenReturn(true);

        // Act
        boolean result = service.addComment(DOCUMENT_ID, JUDGE_ID, commentText);

        // Assert
        assertTrue(result);
        verify(mockCommentDAO).insert(any(ProgressComment.class));
        verify(mockAuditDAO).insert(any(AuditLog.class));
    }

    @Test
    @DisplayName("Aggiunta commento fallisce per rate limit")
    void testAddComment_RateLimitExceeded() throws Exception {
        // Arrange
        String commentText = "Commento che supera il rate limit";
        
        Documento mockDocument = new Documento();
        mockDocument.setId(DOCUMENT_ID);
        mockDocument.setTeamId(TEAM_ID);
        when(mockDocumentDAO.findById(DOCUMENT_ID)).thenReturn(mockDocument);

        Registrazione mockRegistration = new Registrazione();
        mockRegistration.setUtenteId(JUDGE_ID);
        mockRegistration.setHackathonId(HACKATHON_ID);
        mockRegistration.setRuolo(Registrazione.Ruolo.GIUDICE);
        when(mockRegistrationDAO.findByUtenteAndHackathon(JUDGE_ID, HACKATHON_ID))
                .thenReturn(mockRegistration);

        // Mock rate limit exceeded (30+ comments in last hour)
        when(mockCommentDAO.countByJudgeAndTeamInLastHour(JUDGE_ID, TEAM_ID)).thenReturn(30);

        // Act & Assert
        assertThrows(DataAccessException.class, () -> 
                service.addComment(DOCUMENT_ID, JUDGE_ID, commentText));
        
        verify(mockCommentDAO, never()).insert(any(ProgressComment.class));
    }

    @Test
    @DisplayName("Aggiunta commento fallisce per ACL - giudice non registrato")
    void testAddComment_ACL_JudgeNotRegistered() throws Exception {
        // Arrange
        String commentText = "Commento da giudice non autorizzato";
        
        Documento mockDocument = new Documento();
        mockDocument.setId(DOCUMENT_ID);
        mockDocument.setTeamId(TEAM_ID);
        when(mockDocumentDAO.findById(DOCUMENT_ID)).thenReturn(mockDocument);

        // Mock judge not registered for hackathon
        when(mockRegistrationDAO.findByUtenteAndHackathon(JUDGE_ID, HACKATHON_ID))
                .thenReturn(null);

        // Act & Assert
        assertThrows(DataAccessException.class, () -> 
                service.addComment(DOCUMENT_ID, JUDGE_ID, commentText));
        
        verify(mockCommentDAO, never()).insert(any(ProgressComment.class));
    }

    @Test
    @DisplayName("Validazione testo commento - troppo corto")
    void testAddComment_ValidationFail_TooShort() throws Exception {
        // Arrange
        String commentText = "Ok"; // Troppo corto (< 5 caratteri)

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
                service.addComment(DOCUMENT_ID, JUDGE_ID, commentText));
    }

    @Test
    @DisplayName("Validazione testo commento - troppo lungo")
    void testAddComment_ValidationFail_TooLong() throws Exception {
        // Arrange
        String commentText = "x".repeat(2001); // Troppo lungo (> 2000 caratteri)

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
                service.addComment(DOCUMENT_ID, JUDGE_ID, commentText));
    }

    @Test
    @DisplayName("Sanitizzazione HTML nel commento")
    void testAddComment_HTMLSanitization() throws Exception {
        // Arrange
        String commentText = "Buon lavoro! <script>alert('xss')</script> <b>Ottimo</b>";
        String expectedSanitized = "Buon lavoro!  Ottimo"; // Script rimosso, tag HTML rimossi
        
        Documento mockDocument = new Documento();
        mockDocument.setId(DOCUMENT_ID);
        mockDocument.setTeamId(TEAM_ID);
        when(mockDocumentDAO.findById(DOCUMENT_ID)).thenReturn(mockDocument);

        Registrazione mockRegistration = new Registrazione();
        mockRegistration.setUtenteId(JUDGE_ID);
        mockRegistration.setHackathonId(HACKATHON_ID);
        mockRegistration.setRuolo(Registrazione.Ruolo.GIUDICE);
        when(mockRegistrationDAO.findByUtenteAndHackathon(JUDGE_ID, HACKATHON_ID))
                .thenReturn(mockRegistration);

        when(mockCommentDAO.countByJudgeAndTeamInLastHour(JUDGE_ID, TEAM_ID)).thenReturn(5);
        when(mockCommentDAO.insert(any(ProgressComment.class))).thenReturn(true);

        // Act
        boolean result = service.addComment(DOCUMENT_ID, JUDGE_ID, commentText);

        // Assert
        assertTrue(result);
        verify(mockCommentDAO).insert(argThat(comment -> 
                comment.getText().equals(expectedSanitized)));
    }

    @Test
    @DisplayName("Aggiornamento commento con successo")
    void testUpdateComment_Success() throws Exception {
        // Arrange
        String newCommentText = "Commento aggiornato";
        
        ProgressComment existingComment = new ProgressComment();
        existingComment.setId(1);
        existingComment.setDocumentId(DOCUMENT_ID);
        existingComment.setJudgeId(JUDGE_ID);
        existingComment.setText("Commento originale");
        existingComment.setCreatedAt(LocalDateTime.now().minusHours(1));
        
        when(mockCommentDAO.findByDocumentAndJudge(DOCUMENT_ID, JUDGE_ID))
                .thenReturn(existingComment);
        when(mockCommentDAO.update(any(ProgressComment.class))).thenReturn(true);

        // Act
        boolean result = service.updateComment(DOCUMENT_ID, JUDGE_ID, newCommentText);

        // Assert
        assertTrue(result);
        verify(mockCommentDAO).update(argThat(comment -> 
                comment.getText().equals("Commento aggiornato") &&
                comment.getUpdatedAt() != null));
        verify(mockAuditDAO).insert(any(AuditLog.class));
    }

    @Test
    @DisplayName("Rimozione commento con successo")
    void testRemoveComment_Success() throws Exception {
        // Arrange
        ProgressComment existingComment = new ProgressComment();
        existingComment.setId(1);
        existingComment.setDocumentId(DOCUMENT_ID);
        existingComment.setJudgeId(JUDGE_ID);
        
        when(mockCommentDAO.findByDocumentAndJudge(DOCUMENT_ID, JUDGE_ID))
                .thenReturn(existingComment);
        when(mockCommentDAO.delete(1)).thenReturn(true);

        // Act
        boolean result = service.removeComment(DOCUMENT_ID, JUDGE_ID);

        // Assert
        assertTrue(result);
        verify(mockCommentDAO).delete(1);
        verify(mockAuditDAO).insert(any(AuditLog.class));
    }

    @Test
    @DisplayName("Rimozione commento fallisce - commento non trovato")
    void testRemoveComment_NotFound() throws Exception {
        // Arrange
        when(mockCommentDAO.findByDocumentAndJudge(DOCUMENT_ID, JUDGE_ID))
                .thenReturn(null);

        // Act
        boolean result = service.removeComment(DOCUMENT_ID, JUDGE_ID);

        // Assert
        assertFalse(result);
        verify(mockCommentDAO, never()).delete(anyInt());
    }

    @Test
    @DisplayName("Cascading delete - rimozione documento rimuove commenti")
    void testCascadingDelete_DocumentRemoval() throws Exception {
        // Arrange
        List<ProgressComment> comments = List.of(
                createComment(1, DOCUMENT_ID, JUDGE_ID),
                createComment(2, DOCUMENT_ID, 2)
        );
        
        when(mockCommentDAO.findByDocument(DOCUMENT_ID)).thenReturn(comments);
        when(mockCommentDAO.delete(anyInt())).thenReturn(true);

        // Act
        service.handleDocumentDeletion(DOCUMENT_ID);

        // Assert
        verify(mockCommentDAO).delete(1);
        verify(mockCommentDAO).delete(2);
        verify(mockAuditDAO, times(2)).insert(any(AuditLog.class));
    }

    private ProgressComment createComment(int id, int documentId, int judgeId) {
        ProgressComment comment = new ProgressComment();
        comment.setId(id);
        comment.setDocumentId(documentId);
        comment.setJudgeId(judgeId);
        comment.setText("Test comment");
        comment.setCreatedAt(LocalDateTime.now());
        return comment;
    }

    @AfterEach
    void tearDown() throws Exception {
        // Cleanup test data
        if (mockConnectionManager.getConnection() != null) {
            try (Connection conn = mockConnectionManager.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute("TRUNCATE TABLE judge_comment CASCADE");
                stmt.execute("TRUNCATE TABLE registrazione CASCADE");
                stmt.execute("TRUNCATE TABLE documents CASCADE");
                stmt.execute("TRUNCATE TABLE team CASCADE");
                stmt.execute("TRUNCATE TABLE hackathon CASCADE");
                stmt.execute("TRUNCATE TABLE utente CASCADE");
            }
        }
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }
}
