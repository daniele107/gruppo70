package service;

import dao.RankingSnapshotDAO;
import dao.RegistrazioneDAO;
import dao.TeamDAO;
import dao.ValutazioneDAO;
import dao.DocumentoDAO;
import database.ConnectionManager;
import model.*;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test per RankingService con Testcontainers PostgreSQL
 */
@Testcontainers
class RankingServiceTest {

    @Container
    @SuppressWarnings("resource") // Gestito automaticamente da Testcontainers
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("hackathon_test")
            .withUsername("test")
            .withPassword("test");

    private RankingService service;
    private ValutazioneDAO mockValutazioneDAO;
    private TeamDAO mockTeamDAO;
    private RegistrazioneDAO mockRegistrationDAO;
    private RankingSnapshotDAO mockSnapshotDAO;
    private DocumentoDAO mockDocumentDAO;
    private ConnectionManager mockConnectionManager;

    private static final int HACKATHON_ID = 10;
    private static final int ORGANIZER_ID = 1;
    private static final int TEAM1_ID = 101;
    private static final int TEAM2_ID = 102;
    private static final int TEAM3_ID = 103;
    private static final int JUDGE1_ID = 201;
    private static final int JUDGE2_ID = 202;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @BeforeEach
    void setUp() throws Exception {
        // Setup mock DAOs
        mockValutazioneDAO = mock(ValutazioneDAO.class);
        mockTeamDAO = mock(TeamDAO.class);
        mockRegistrationDAO = mock(RegistrazioneDAO.class);
        mockSnapshotDAO = mock(RankingSnapshotDAO.class);
        mockDocumentDAO = mock(DocumentoDAO.class);
        mockConnectionManager = mock(ConnectionManager.class);

        // Setup real database connection for integration tests
        Connection realConnection = postgres.createConnection("");
        when(mockConnectionManager.getConnection()).thenReturn(realConnection);

        // Initialize test database schema
        initializeTestSchema(realConnection);

        // Create service instance
        service = new RankingService(
                mockTeamDAO,
                mockValutazioneDAO,
                mockRegistrationDAO,
                mockDocumentDAO,
                mockSnapshotDAO
        );
    }

    private void initializeTestSchema(Connection connection) throws Exception {
        try (Statement stmt = connection.createStatement()) {
            // Create test tables
            stmt.execute("CREATE TABLE IF NOT EXISTS hackathon (" +
                    "id SERIAL PRIMARY KEY, " +
                    "nome VARCHAR(200), " +
                    "data_inizio TIMESTAMP, " +
                    "data_fine TIMESTAMP" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS team (" +
                    "id SERIAL PRIMARY KEY, " +
                    "nome VARCHAR(200), " +
                    "data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS utente (" +
                    "id SERIAL PRIMARY KEY, " +
                    "nome VARCHAR(100), " +
                    "cognome VARCHAR(100), " +
                    "tipo VARCHAR(50)" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS valutazione (" +
                    "id SERIAL PRIMARY KEY, " +
                    "hackathon_id INTEGER, " +
                    "team_id INTEGER, " +
                    "giudice_id INTEGER, " +
                    "voto DECIMAL(3,1), " +
                    "data_valutazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS ranking_snapshot (" +
                    "id SERIAL PRIMARY KEY, " +
                    "hackathon_id INTEGER, " +
                    "version INTEGER, " +
                    "json_payload JSONB, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "UNIQUE(hackathon_id, version)" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS registrazione (" +
                    "id SERIAL PRIMARY KEY, " +
                    "utente_id INTEGER, " +
                    "hackathon_id INTEGER, " +
                    "ruolo VARCHAR(50)" +
                    ")");

            // Insert test data
            stmt.execute("INSERT INTO hackathon (id, nome, data_inizio, data_fine) VALUES " +
                    "(10, 'Test Hackathon', '2024-01-01 09:00:00', '2024-01-03 18:00:00')");

            stmt.execute("INSERT INTO team (id, nome, data_creazione) VALUES " +
                    "(101, 'Team Alpha', '2024-01-01 10:00:00'), " +
                    "(102, 'Team Beta', '2024-01-01 11:00:00'), " +
                    "(103, 'Team Gamma', '2024-01-01 09:30:00')");

            stmt.execute("INSERT INTO utente (id, nome, cognome, tipo) VALUES " +
                    "(1, 'Mario', 'Organizzatore', 'ORGANIZZATORE'), " +
                    "(201, 'Giudice', 'Uno', 'GIUDICE'), " +
                    "(202, 'Giudice', 'Due', 'GIUDICE')");

            stmt.execute("INSERT INTO registrazione (utente_id, hackathon_id, ruolo) VALUES " +
                    "(201, 10, 'GIUDICE'), " +
                    "(202, 10, 'GIUDICE')");
        }
    }

    @Test
    @DisplayName("Preview ranking con tie-breaker per deviazione standard")
    void testPreviewRanking_TieBreakerStandardDeviation() throws Exception {
        // Arrange - Team con stessa media ma deviazione standard diversa
        List<TeamRankingResult> mockResults = Arrays.asList(
                new TeamRankingResult(TEAM1_ID, 8.0, 2), // Media 8.0, voti: 7.0, 9.0 (std dev = 1.0)
                new TeamRankingResult(TEAM2_ID, 8.0, 2)  // Media 8.0, voti: 8.0, 8.0 (std dev = 0.0)
        );
        
        when(mockValutazioneDAO.findTeamRankingByHackathon(HACKATHON_ID)).thenReturn(mockResults);
        
        // Mock valutazioni individuali per calcolo deviazione standard
        when(mockValutazioneDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createValutazione(TEAM1_ID, JUDGE1_ID, 7),
                createValutazione(TEAM1_ID, JUDGE2_ID, 9),
                createValutazione(TEAM2_ID, JUDGE1_ID, 8),
                createValutazione(TEAM2_ID, JUDGE2_ID, 8)
        ));
        
        when(mockTeamDAO.findById(TEAM1_ID)).thenReturn(createTeam(TEAM1_ID, "Team Alpha"));
        when(mockTeamDAO.findById(TEAM2_ID)).thenReturn(createTeam(TEAM2_ID, "Team Beta"));
        when(mockTeamDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createTeam(TEAM1_ID, "Team Alpha"),
                createTeam(TEAM2_ID, "Team Beta")
        ));
        when(mockRegistrationDAO.findGiudici(HACKATHON_ID)).thenReturn(Arrays.asList(
                createRegistration(JUDGE1_ID, HACKATHON_ID, "GIUDICE"),
                createRegistration(JUDGE2_ID, HACKATHON_ID, "GIUDICE")
        ));

        // Act
        RankingService.Preview preview = service.computePreview(HACKATHON_ID, true);

        // Assert
        assertNotNull(preview);
        assertFalse(preview.entries.isEmpty());
        assertTrue(preview.entries.size() >= 2);
        
        // Verifica che Team Beta sia prima di Team Alpha (tie-breaker per std dev)
        RankingService.Entry firstEntry = preview.entries.get(0);
        assertEquals("Team Beta", firstEntry.teamName());
    }

    @Test
    @DisplayName("Preview ranking con tie-breaker per timestamp submission")
    void testPreviewRanking_TieBreakerEarliestSubmission() throws Exception {
        // Arrange - Team con stessa media e stessa deviazione standard
        List<TeamRankingResult> mockResults = Arrays.asList(
                new TeamRankingResult(TEAM1_ID, 8.0, 2),
                new TeamRankingResult(TEAM3_ID, 8.0, 2)
        );
        
        when(mockValutazioneDAO.findTeamRankingByHackathon(HACKATHON_ID)).thenReturn(mockResults);
        
        // Mock valutazioni con stessa deviazione standard
        when(mockValutazioneDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createValutazione(TEAM1_ID, JUDGE1_ID, 8),
                createValutazione(TEAM1_ID, JUDGE2_ID, 8),
                createValutazione(TEAM3_ID, JUDGE1_ID, 8),
                createValutazione(TEAM3_ID, JUDGE2_ID, 8)
        ));
        
        // Team Gamma creato prima (09:30) vs Team Alpha (10:00)
        when(mockTeamDAO.findById(TEAM1_ID)).thenReturn(createTeam(TEAM1_ID, "Team Alpha"));
        when(mockTeamDAO.findById(TEAM3_ID)).thenReturn(createTeam(TEAM3_ID, "Team Gamma"));
        when(mockTeamDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createTeam(TEAM1_ID, "Team Alpha"),
                createTeam(TEAM3_ID, "Team Gamma")
        ));
        when(mockRegistrationDAO.findGiudici(HACKATHON_ID)).thenReturn(Arrays.asList(
                createRegistration(JUDGE1_ID, HACKATHON_ID, "GIUDICE"),
                createRegistration(JUDGE2_ID, HACKATHON_ID, "GIUDICE")
        ));

        // Act
        RankingService.Preview preview = service.computePreview(HACKATHON_ID, true);

        // Assert
        assertNotNull(preview);
        assertFalse(preview.entries.isEmpty());
        assertTrue(preview.entries.size() >= 2);
        
        // Verifica che Team Gamma sia prima di Team Alpha per earliest submission
        RankingService.Entry firstEntry = preview.entries.get(0);
        assertEquals("Team Gamma", firstEntry.teamName());
    }

    @Test
    @DisplayName("Preview ranking con tie-breaker alfabetico per nome team")
    void testPreviewRanking_TieBreakerTeamNameAlphabetical() throws Exception {
        // Arrange - Team identici tranne per il nome
        List<TeamRankingResult> mockResults = Arrays.asList(
                new TeamRankingResult(TEAM1_ID, 8.0, 2), // Team Alpha
                new TeamRankingResult(TEAM2_ID, 8.0, 2)  // Team Beta
        );
        
        when(mockValutazioneDAO.findTeamRankingByHackathon(HACKATHON_ID)).thenReturn(mockResults);
        
        // Mock valutazioni identiche
        when(mockValutazioneDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createValutazione(TEAM1_ID, JUDGE1_ID, 8),
                createValutazione(TEAM1_ID, JUDGE2_ID, 8),
                createValutazione(TEAM2_ID, JUDGE1_ID, 8),
                createValutazione(TEAM2_ID, JUDGE2_ID, 8)
        ));
        
        // Team con stesso timestamp di creazione
        when(mockTeamDAO.findById(TEAM1_ID)).thenReturn(createTeam(TEAM1_ID, "Team Alpha"));
        when(mockTeamDAO.findById(TEAM2_ID)).thenReturn(createTeam(TEAM2_ID, "Team Beta"));
        when(mockTeamDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createTeam(TEAM1_ID, "Team Alpha"),
                createTeam(TEAM2_ID, "Team Beta")
        ));
        when(mockRegistrationDAO.findGiudici(HACKATHON_ID)).thenReturn(Arrays.asList(
                createRegistration(JUDGE1_ID, HACKATHON_ID, "GIUDICE"),
                createRegistration(JUDGE2_ID, HACKATHON_ID, "GIUDICE")
        ));

        // Act
        RankingService.Preview preview = service.computePreview(HACKATHON_ID, true);

        // Assert
        assertNotNull(preview);
        assertFalse(preview.entries.isEmpty());
        assertTrue(preview.entries.size() >= 2);
        
        // Verifica ordine alfabetico: Alpha prima di Beta
        RankingService.Entry firstEntry = preview.entries.get(0);
        assertEquals("Team Alpha", firstEntry.teamName());
    }

    @Test
    @DisplayName("Verifica voti acquisiti - tutti i giudici hanno votato")
    void testAreAllVotesAcquired_AllVotesPresent() throws Exception {
        // Arrange
        List<Registrazione> judges = Arrays.asList(
                createRegistration(JUDGE1_ID, HACKATHON_ID, "GIUDICE"),
                createRegistration(JUDGE2_ID, HACKATHON_ID, "GIUDICE")
        );
        when(mockRegistrationDAO.findGiudici(HACKATHON_ID)).thenReturn(judges);
        
        List<Team> teams = Arrays.asList(
                createTeam(TEAM1_ID, "Team Alpha"),
                createTeam(TEAM2_ID, "Team Beta")
        );
        when(mockTeamDAO.findByHackathon(HACKATHON_ID)).thenReturn(teams);
        
        // Tutti i giudici hanno votato per tutti i team
        when(mockValutazioneDAO.haGiudiceValutatoTeam(JUDGE1_ID, TEAM1_ID)).thenReturn(true);
        when(mockValutazioneDAO.haGiudiceValutatoTeam(JUDGE1_ID, TEAM2_ID)).thenReturn(true);
        when(mockValutazioneDAO.haGiudiceValutatoTeam(JUDGE2_ID, TEAM1_ID)).thenReturn(true);
        when(mockValutazioneDAO.haGiudiceValutatoTeam(JUDGE2_ID, TEAM2_ID)).thenReturn(true);

        when(mockValutazioneDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createValutazione(TEAM1_ID, JUDGE1_ID, 8),
                createValutazione(TEAM1_ID, JUDGE2_ID, 9),
                createValutazione(TEAM2_ID, JUDGE1_ID, 7),
                createValutazione(TEAM2_ID, JUDGE2_ID, 8)
        ));

        // Act
        RankingService.Preview preview = service.computePreview(HACKATHON_ID, false);
        boolean result = !preview.missingVotes;

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Verifica voti acquisiti - voti mancanti")
    void testAreAllVotesAcquired_MissingVotes() throws Exception {
        // Arrange
        List<Registrazione> judges = Arrays.asList(
                createRegistration(JUDGE1_ID, HACKATHON_ID, "GIUDICE"),
                createRegistration(JUDGE2_ID, HACKATHON_ID, "GIUDICE")
        );
        when(mockRegistrationDAO.findGiudici(HACKATHON_ID)).thenReturn(judges);
        
        List<Team> teams = Arrays.asList(
                createTeam(TEAM1_ID, "Team Alpha"),
                createTeam(TEAM2_ID, "Team Beta")
        );
        when(mockTeamDAO.findByHackathon(HACKATHON_ID)).thenReturn(teams);
        
        // JUDGE2 non ha votato per TEAM2
        when(mockValutazioneDAO.haGiudiceValutatoTeam(JUDGE1_ID, TEAM1_ID)).thenReturn(true);
        when(mockValutazioneDAO.haGiudiceValutatoTeam(JUDGE1_ID, TEAM2_ID)).thenReturn(true);
        when(mockValutazioneDAO.haGiudiceValutatoTeam(JUDGE2_ID, TEAM1_ID)).thenReturn(true);
        when(mockValutazioneDAO.haGiudiceValutatoTeam(JUDGE2_ID, TEAM2_ID)).thenReturn(false); // Voto mancante

        when(mockValutazioneDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createValutazione(TEAM1_ID, JUDGE1_ID, 8),
                createValutazione(TEAM1_ID, JUDGE2_ID, 9),
                createValutazione(TEAM2_ID, JUDGE1_ID, 7)
                // Missing vote for TEAM2_ID, JUDGE2_ID
        ));

        // Act
        RankingService.Preview preview = service.computePreview(HACKATHON_ID, false);
        boolean result = !preview.missingVotes;

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Pubblicazione ranking con successo")
    void testPublishRanking_Success() throws Exception {
        // Arrange
        when(mockSnapshotDAO.findLatestByHackathon(HACKATHON_ID)).thenReturn(null); // Nessun snapshot esistente
        when(mockSnapshotDAO.findMaxVersion(HACKATHON_ID)).thenReturn(0);
        
        List<TeamRankingResult> mockResults = Arrays.asList(
                new TeamRankingResult(TEAM1_ID, 9.0, 2),
                new TeamRankingResult(TEAM2_ID, 8.0, 2)
        );
        when(mockValutazioneDAO.findTeamRankingByHackathon(HACKATHON_ID)).thenReturn(mockResults);
        when(mockTeamDAO.findById(TEAM1_ID)).thenReturn(createTeam(TEAM1_ID, "Team Alpha"));
        when(mockTeamDAO.findById(TEAM2_ID)).thenReturn(createTeam(TEAM2_ID, "Team Beta"));
        when(mockTeamDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createTeam(TEAM1_ID, "Team Alpha"),
                createTeam(TEAM2_ID, "Team Beta")
        ));
        when(mockRegistrationDAO.findGiudici(HACKATHON_ID)).thenReturn(Arrays.asList(
                createRegistration(JUDGE1_ID, HACKATHON_ID, "GIUDICE"),
                createRegistration(JUDGE2_ID, HACKATHON_ID, "GIUDICE")
        ));
        when(mockValutazioneDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createValutazione(TEAM1_ID, JUDGE1_ID, 9),
                createValutazione(TEAM1_ID, JUDGE2_ID, 9),
                createValutazione(TEAM2_ID, JUDGE1_ID, 8),
                createValutazione(TEAM2_ID, JUDGE2_ID, 8)
        ));
        
        when(mockSnapshotDAO.insert(any(RankingSnapshot.class))).thenReturn(1);

        // Act
        RankingService.PublishResult result = service.publish(HACKATHON_ID, false, ORGANIZER_ID, null);

        // Assert
        assertTrue(result.success);
        verify(mockSnapshotDAO).insert(argThat(snapshot -> 
                snapshot.getHackathonId() == HACKATHON_ID &&
                snapshot.getVersion() == 1 &&
                snapshot.getJsonPayload() != null));
    }

    @Test
    @DisplayName("Pubblicazione ranking con override per voti mancanti")
    void testPublishRanking_WithOverride() throws Exception {
        // Arrange
        String overrideReason = "Pubblicazione urgente per deadline";
        
        when(mockSnapshotDAO.findLatestByHackathon(HACKATHON_ID)).thenReturn(null);
        when(mockSnapshotDAO.findMaxVersion(HACKATHON_ID)).thenReturn(0);
        
        List<TeamRankingResult> mockResults = Arrays.asList(
                new TeamRankingResult(TEAM1_ID, 9.0, 1) // Solo un voto invece di due
        );
        when(mockValutazioneDAO.findTeamRankingByHackathon(HACKATHON_ID)).thenReturn(mockResults);
        when(mockTeamDAO.findById(TEAM1_ID)).thenReturn(createTeam(TEAM1_ID, "Team Alpha"));
        when(mockTeamDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createTeam(TEAM1_ID, "Team Alpha")
        ));
        when(mockRegistrationDAO.findGiudici(HACKATHON_ID)).thenReturn(Arrays.asList(
                createRegistration(JUDGE1_ID, HACKATHON_ID, "GIUDICE"),
                createRegistration(JUDGE2_ID, HACKATHON_ID, "GIUDICE")
        ));
        when(mockValutazioneDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createValutazione(TEAM1_ID, JUDGE1_ID, 9)
        ));
        
        when(mockSnapshotDAO.insert(any(RankingSnapshot.class))).thenReturn(1);

        // Act
        RankingService.PublishResult result = service.publish(HACKATHON_ID, true, ORGANIZER_ID, overrideReason);

        // Assert
        assertTrue(result.success);
        verify(mockSnapshotDAO).insert(argThat(snapshot -> 
                snapshot.getJsonPayload().contains(overrideReason)));
    }

    @Test
    @DisplayName("Pubblicazione ranking fallisce - voti mancanti senza override")
    void testPublishRanking_MissingVotesNoOverride() throws Exception {
        // Arrange
        when(mockTeamDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createTeam(TEAM1_ID, "Team Alpha")
        ));
        when(mockRegistrationDAO.findGiudici(HACKATHON_ID)).thenReturn(Arrays.asList(
                createRegistration(JUDGE1_ID, HACKATHON_ID, "GIUDICE"),
                createRegistration(JUDGE2_ID, HACKATHON_ID, "GIUDICE")
        ));
        when(mockValutazioneDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createValutazione(TEAM1_ID, JUDGE1_ID, 9)
                // Missing vote from JUDGE2
        ));

        // Act
        RankingService.PublishResult result = service.publish(HACKATHON_ID, false, ORGANIZER_ID, null);

        // Assert
        assertFalse(result.success);
        assertEquals("MISSING_VOTES", result.code);
        verify(mockSnapshotDAO, never()).insert(any(RankingSnapshot.class));
    }

    @Test
    @DisplayName("Immutabilità snapshot - versioning corretto")
    void testSnapshotImmutability_CorrectVersioning() throws Exception {
        // Arrange
        RankingSnapshot existingSnapshot = new RankingSnapshot();
        existingSnapshot.setVersion(1);
        when(mockSnapshotDAO.findLatestByHackathon(HACKATHON_ID)).thenReturn(existingSnapshot);
        when(mockSnapshotDAO.findMaxVersion(HACKATHON_ID)).thenReturn(1);
        
        List<TeamRankingResult> mockResults = Arrays.asList(
                new TeamRankingResult(TEAM1_ID, 9.0, 2)
        );
        when(mockValutazioneDAO.findTeamRankingByHackathon(HACKATHON_ID)).thenReturn(mockResults);
        when(mockTeamDAO.findById(TEAM1_ID)).thenReturn(createTeam(TEAM1_ID, "Team Alpha"));
        when(mockTeamDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createTeam(TEAM1_ID, "Team Alpha")
        ));
        when(mockRegistrationDAO.findGiudici(HACKATHON_ID)).thenReturn(Arrays.asList(
                createRegistration(JUDGE1_ID, HACKATHON_ID, "GIUDICE"),
                createRegistration(JUDGE2_ID, HACKATHON_ID, "GIUDICE")
        ));
        when(mockValutazioneDAO.findByHackathon(HACKATHON_ID)).thenReturn(Arrays.asList(
                createValutazione(TEAM1_ID, JUDGE1_ID, 9),
                createValutazione(TEAM1_ID, JUDGE2_ID, 9)
        ));
        when(mockSnapshotDAO.insert(any(RankingSnapshot.class))).thenReturn(1);

        // Act
        RankingService.PublishResult result = service.publish(HACKATHON_ID, true, ORGANIZER_ID, "Aggiornamento classifica");

        // Assert
        assertTrue(result.success);
        verify(mockSnapshotDAO).insert(argThat(snapshot -> 
                snapshot.getVersion() == 2)); // Versione incrementata
    }

    // Helper methods
    private Valutazione createValutazione(int teamId, int giudiceId, int voto) {
        Valutazione val = new Valutazione();
        val.setTeamId(teamId);
        val.setGiudiceId(giudiceId);
        val.setVoto(voto);
        val.setDataValutazione(LocalDateTime.now());
        return val;
    }

    private Team createTeam(int id, String nome) {
        Team team = new Team(nome, HACKATHON_ID, 1, 5); // nome, hackathonId, capoTeamId, dimensioneMassima
        team.setId(id);
        return team;
    }

    private Registrazione createRegistration(int utenteId, int hackathonId, String ruolo) {
        Registrazione reg = new Registrazione();
        reg.setUtenteId(utenteId);
        reg.setHackathonId(hackathonId);
        reg.setRuolo(Registrazione.Ruolo.valueOf(ruolo));
        return reg;
    }

    @AfterEach
    void tearDown() throws Exception {
        // Cleanup test data
        if (mockConnectionManager.getConnection() != null) {
            try (Connection conn = mockConnectionManager.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute("TRUNCATE TABLE ranking_snapshot CASCADE");
                stmt.execute("TRUNCATE TABLE valutazione CASCADE");
                stmt.execute("TRUNCATE TABLE registrazione CASCADE");
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