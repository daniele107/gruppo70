package dao.postgres;

import dao.DocumentoDAO;
import database.ConnectionManager;
import database.DataAccessException;
import model.Documento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Implementazione PostgreSQL del DAO per i documenti.
 * Gestisce tutte le operazioni di accesso ai dati per i documenti.
 */
public class DocumentoPostgresDAO implements DocumentoDAO {
    
    private static final Logger LOGGER = Logger.getLogger(DocumentoPostgresDAO.class.getName());
    private final ConnectionManager connectionManager;
    
    // SQL Queries
    private static final String SELECT_FIELDS = 
        "SELECT id, team_id, hackathon_id, nome, percorso, tipo, dimensione, hash, " +
        "data_caricamento, utente_caricamento, descrizione, validato, validatore_id, data_validazione ";
    private static final String INSERT_DOCUMENTO = 
        "INSERT INTO documents (team_id, hackathon_id, nome, percorso, tipo, dimensione, hash, " +
        "data_caricamento, utente_caricamento, descrizione, validato) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
    
    private static final String FIND_BY_ID = 
        SELECT_FIELDS + "FROM documents WHERE id = ?";
    
    private static final String FIND_BY_TEAM = 
        SELECT_FIELDS + "FROM documents WHERE team_id = ? ORDER BY data_caricamento DESC";
    
    private static final String FIND_BY_HACKATHON = 
        SELECT_FIELDS + "FROM documents WHERE hackathon_id = ? ORDER BY data_caricamento DESC";
    
    private static final String FIND_BY_UTENTE = 
        SELECT_FIELDS + "FROM documents WHERE utente_caricamento = ? ORDER BY data_caricamento DESC";
    
    private static final String FIND_BY_TIPO = 
        SELECT_FIELDS + "FROM documents WHERE tipo = ? ORDER BY data_caricamento DESC";
    
    private static final String FIND_BY_VALIDATO = 
        SELECT_FIELDS + "FROM documents WHERE validato = ? ORDER BY data_caricamento DESC";
    
    private static final String UPDATE_DOCUMENTO = 
        "UPDATE documents SET nome = ?, descrizione = ? WHERE id = ?";
    
    private static final String VALIDA_DOCUMENTO = 
        "UPDATE documents SET validato = TRUE, validatore_id = ?, data_validazione = CURRENT_TIMESTAMP WHERE id = ?";
    
    private static final String RIMUOVI_VALIDAZIONE = 
        "UPDATE documents SET validato = FALSE, validatore_id = NULL, data_validazione = NULL WHERE id = ?";
    
    private static final String DELETE_DOCUMENTO = 
        "DELETE FROM documents WHERE id = ?";
    
    private static final String DELETE_BY_TEAM = 
        "DELETE FROM documents WHERE team_id = ?";
    
    private static final String DELETE_BY_HACKATHON = 
        "DELETE FROM documents WHERE hackathon_id = ?";
    
    private static final String COUNT_BY_TEAM = 
        "SELECT COUNT(*) FROM documents WHERE team_id = ?";
    
    private static final String COUNT_BY_HACKATHON = 
        "SELECT COUNT(*) FROM documents WHERE hackathon_id = ?";
    
    private static final String TOTAL_SIZE_BY_TEAM = 
        "SELECT COALESCE(SUM(dimensione), 0) FROM documents WHERE team_id = ?";
    
    private static final String TOTAL_SIZE_BY_HACKATHON = 
        "SELECT COALESCE(SUM(dimensione), 0) FROM documents WHERE hackathon_id = ?";
    
    private static final String FIND_ALL = 
        SELECT_FIELDS + "FROM documents ORDER BY data_caricamento DESC";
    
    private static final String EXISTS_BY_HASH = 
        "SELECT COUNT(*) FROM documents WHERE hash = ?";
    
    private static final String FIND_BY_HASH = 
        SELECT_FIELDS + "FROM documents WHERE hash = ? ORDER BY data_caricamento DESC";
    
    /**
     * Costruttore che inizializza il DAO con il ConnectionManager
     * 
     * @param connectionManager il manager delle connessioni al database
     */
    public DocumentoPostgresDAO(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    @Override
    public int insert(Documento documento) {
        if (documento == null) {
            throw new IllegalArgumentException("Il documento non può essere null");
        }
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_DOCUMENTO)) {
            
            statement.setInt(1, documento.getTeamId());
            statement.setInt(2, documento.getHackathonId());
            statement.setString(3, documento.getNome());
            statement.setString(4, documento.getPercorso());
            statement.setString(5, documento.getTipo());
            statement.setLong(6, documento.getDimensione());
            statement.setString(7, documento.getHash());
            statement.setTimestamp(8, Timestamp.valueOf(documento.getDataCaricamento()));
            statement.setInt(9, documento.getUtenteCaricamento());
            statement.setString(10, documento.getDescrizione());
            statement.setBoolean(11, documento.isValidato());
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    documento.setId(id);
                    LOGGER.log(Level.INFO, "Documento inserito con successo. ID: {0}", id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert document for team " + documento.getTeamId() + 
                ", hackathon " + documento.getHackathonId() + ": " + e.getMessage(), e);
        }
        return -1;
    }
    
    @Override
    public Documento findById(int id) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToDocumento(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find document with ID " + id + ": " + e.getMessage(), e);
        }
        return null;
    }
    
    @Override
    public List<Documento> findByTeam(int teamId) {
        return findDocumenti(FIND_BY_TEAM, teamId);
    }
    
    @Override
    public List<Documento> findByHackathon(int hackathonId) {
        return findDocumenti(FIND_BY_HACKATHON, hackathonId);
    }
    
    @Override
    public List<Documento> findByUtente(int utenteId) {
        return findDocumenti(FIND_BY_UTENTE, utenteId);
    }
    
    @Override
    public List<Documento> findByTipo(String tipo) {
        return findDocumentiByString(FIND_BY_TIPO, tipo);
    }
    
    @Override
    public List<Documento> findByValidato(boolean validato) {
        List<Documento> documenti = new ArrayList<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_VALIDATO)) {
            
            statement.setBoolean(1, validato);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    documenti.add(mapResultSetToDocumento(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find documents by validation status " + validato + ": " + e.getMessage(), e);
        }
        
        return documenti;
    }
    
    @Override
    public boolean update(Documento documento) {
        if (documento == null) {
            throw new IllegalArgumentException("Il documento non può essere null");
        }
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_DOCUMENTO)) {
            
            statement.setString(1, documento.getNome());
            statement.setString(2, documento.getDescrizione());
            statement.setInt(3, documento.getId());
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                LOGGER.log(Level.INFO, "Documento {0} aggiornato con successo", documento.getId());
            }
            
            return success;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update document with ID " + documento.getId() + ": " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean valida(int documentoId, int validatoreId) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(VALIDA_DOCUMENTO)) {
            
            statement.setInt(1, validatoreId);
            statement.setInt(2, documentoId);
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                LOGGER.log(Level.INFO, "Documento {0} validato da utente {1}", new Object[]{documentoId, validatoreId});
            }
            
            return success;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to validate document ID " + documentoId + " by user " + validatoreId + ": " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean rimuoviValidazione(int documentoId) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(RIMUOVI_VALIDAZIONE)) {
            
            statement.setInt(1, documentoId);
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                LOGGER.log(Level.INFO, "Validazione rimossa per documento {0}", documentoId);
            }
            
            return success;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to remove validation for document ID " + documentoId + ": " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean delete(int id) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_DOCUMENTO)) {
            
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                LOGGER.log(Level.INFO, "Documento {0} eliminato con successo", id);
            }
            
            return success;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete document with ID " + id + ": " + e.getMessage(), e);
        }
    }
    
    @Override
    public int deleteByTeam(int teamId) {
        return deleteDocumenti(DELETE_BY_TEAM, teamId, "team " + teamId);
    }
    
    @Override
    public int deleteByHackathon(int hackathonId) {
        return deleteDocumenti(DELETE_BY_HACKATHON, hackathonId, "hackathon " + hackathonId);
    }
    
    @Override
    public int countByTeam(int teamId) {
        return countDocumenti(COUNT_BY_TEAM, teamId);
    }
    
    @Override
    public int countByHackathon(int hackathonId) {
        return countDocumenti(COUNT_BY_HACKATHON, hackathonId);
    }
    
    @Override
    public long getTotalSizeByTeam(int teamId) {
        return getTotalSize(TOTAL_SIZE_BY_TEAM, teamId);
    }
    
    @Override
    public long getTotalSizeByHackathon(int hackathonId) {
        return getTotalSize(TOTAL_SIZE_BY_HACKATHON, hackathonId);
    }
    
    @Override
    public List<Documento> findAll() {
        List<Documento> documenti = new ArrayList<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                documenti.add(mapResultSetToDocumento(resultSet));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find all documents: " + e.getMessage(), e);
        }
        
        return documenti;
    }
    
    @Override
    public boolean existsByHash(String hash) {
        if (hash == null || hash.trim().isEmpty()) {
            return false;
        }
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(EXISTS_BY_HASH)) {
            
            statement.setString(1, hash);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to check existence for hash '" + hash + "': " + e.getMessage(), e);
        }
        return false;
    }
    
    @Override
    public List<Documento> findByHash(String hash) {
        return findDocumentiByString(FIND_BY_HASH, hash);
    }
    
    // Metodi di utilità privati
    
    private List<Documento> findDocumenti(String query, int parametro) {
        List<Documento> documenti = new ArrayList<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, parametro);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    documenti.add(mapResultSetToDocumento(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find documents with parameter " + parametro + ": " + e.getMessage(), e);
        }
        
        return documenti;
    }
    
    private List<Documento> findDocumentiByString(String query, String parametro) {
        List<Documento> documenti = new ArrayList<>();
        
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, parametro);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    documenti.add(mapResultSetToDocumento(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to find documents with string parameter '" + parametro + "': " + e.getMessage(), e);
        }
        
        return documenti;
    }
    
    private int deleteDocumenti(String query, int parametro, String descrizione) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, parametro);
            
            int rowsAffected = statement.executeUpdate();
            LOGGER.log(Level.INFO, "Eliminati {0} documenti per {1}", new Object[]{rowsAffected, descrizione});
            
            return rowsAffected;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete documents for " + descrizione + ": " + e.getMessage(), e);
        }
    }
    
    private int countDocumenti(String query, int parametro) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, parametro);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to count documents for parameter " + parametro + ": " + e.getMessage(), e);
        }
        return 0;
    }
    
    private long getTotalSize(String query, int parametro) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, parametro);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to calculate total size for parameter " + parametro + ": " + e.getMessage(), e);
        }
        return 0;
    }
    
    /**
     * Mappa un ResultSet a un oggetto Documento
     * 
     * @param resultSet il ResultSet da mappare
     * @return l'oggetto Documento
     * @throws SQLException se si verifica un errore SQL
     */
    private Documento mapResultSetToDocumento(ResultSet resultSet) throws SQLException {
        Documento documento = new Documento();
        
        documento.setId(resultSet.getInt("id"));
        documento.setTeamId(resultSet.getInt("team_id"));
        documento.setHackathonId(resultSet.getInt("hackathon_id"));
        documento.setNome(resultSet.getString("nome"));
        documento.setPercorso(resultSet.getString("percorso"));
        documento.setTipo(resultSet.getString("tipo"));
        documento.setDimensione(resultSet.getLong("dimensione"));
        documento.setHash(resultSet.getString("hash"));
        
        Timestamp dataCaricamento = resultSet.getTimestamp("data_caricamento");
        if (dataCaricamento != null) {
            documento.setDataCaricamento(dataCaricamento.toLocalDateTime());
        }
        
        documento.setUtenteCaricamento(resultSet.getInt("utente_caricamento"));
        documento.setDescrizione(resultSet.getString("descrizione"));
        documento.setValidato(resultSet.getBoolean("validato"));
        documento.setValidatoreId(resultSet.getInt("validatore_id"));
        
        Timestamp dataValidazione = resultSet.getTimestamp("data_validazione");
        if (dataValidazione != null) {
            documento.setDataValidazione(dataValidazione.toLocalDateTime());
        }
        
        return documento;
    }
}
