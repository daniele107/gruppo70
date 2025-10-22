package dao.postgres;
import dao.ProgressDAO;
import model.Progress;
import database.ConnectionManager;
import dao.impl.ProgressDAOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Implementazione PostgreSQL per ProgressDAO
 * Gestisce le operazioni di accesso ai dati per l'entità Progress
 */
// Package naming convention now follows standard Java conventions
public class ProgressPostgresDAO implements ProgressDAO {
    // Costanti per i nomi delle colonne del database
    private static final String COLUMN_COMMENTO_GIUDICE = "commento_giudice";
    private static final String COLUMN_GIUDICE_ID = "giudice_id";
    private static final String COLUMN_DATA_COMMENTO = "data_commento";
    private static final String COLUMN_TEAM_ID = "team_id";
    private static final String COLUMN_HACKATHON_ID = "hackathon_id";
    private static final String COLUMN_TITOLO = "titolo";
    private static final String COLUMN_DESCRIZIONE = "descrizione";
    private static final String COLUMN_DOCUMENTO_PATH = "documento_path";
    private static final String COLUMN_DATA_CARICAMENTO = "data_caricamento";
    // Costanti per frammenti SQL comuni
    private static final String SQL_SELECT_COUNT_FROM_PROGRESS_WHERE = "SELECT COUNT(*) FROM progress WHERE ";
    private static final String SQL_SELECT_ALL_FROM_PROGRESS_WHERE = "SELECT * FROM progress WHERE ";
    private static final String SQL_UPDATE_PROGRESS_SET = "UPDATE progress SET ";
    private static final String SQL_ORDER_BY_DESC = " DESC";
    private static final String SQL_PARAM_PLACEHOLDER = " = ?, ";
    private static final String SQL_WHERE_ORDER_BY = " = ? ORDER BY ";
    private final ConnectionManager cm;
    public ProgressPostgresDAO(ConnectionManager connectionManager) {
        this.cm = connectionManager;
    }
    @Override
    public int insert(Progress progress) {
        String sql = "INSERT INTO progress (" + COLUMN_TEAM_ID + ", " + COLUMN_HACKATHON_ID + ", " + 
                    COLUMN_TITOLO + ", " + COLUMN_DESCRIZIONE + ", " + COLUMN_DOCUMENTO_PATH + ", " + 
                    COLUMN_DATA_CARICAMENTO + ") VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, progress.getTeamId());
            stmt.setInt(2, progress.getHackathonId());
            stmt.setString(3, progress.getTitolo());
            stmt.setString(4, progress.getDescrizione());
            stmt.setString(5, progress.getDocumentoPath());
            stmt.setTimestamp(6, Timestamp.valueOf(progress.getDataCaricamento()));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating progress failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    progress.setId(id);
                    return id;
                } else {
                    throw new SQLException("Creating progress failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new ProgressDAOException("Error inserting progress", e);
        }
    }
    @Override
    public boolean update(Progress progress) {
        String sql = SQL_UPDATE_PROGRESS_SET + COLUMN_TEAM_ID + SQL_PARAM_PLACEHOLDER + COLUMN_HACKATHON_ID + SQL_PARAM_PLACEHOLDER + 
                    COLUMN_TITOLO + SQL_PARAM_PLACEHOLDER + COLUMN_DESCRIZIONE + SQL_PARAM_PLACEHOLDER + COLUMN_DOCUMENTO_PATH + SQL_PARAM_PLACEHOLDER + 
                    COLUMN_DATA_CARICAMENTO + SQL_PARAM_PLACEHOLDER + COLUMN_COMMENTO_GIUDICE + SQL_PARAM_PLACEHOLDER + COLUMN_GIUDICE_ID + 
                    SQL_PARAM_PLACEHOLDER + COLUMN_DATA_COMMENTO + " = ? WHERE id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, progress.getTeamId());
            stmt.setInt(2, progress.getHackathonId());
            stmt.setString(3, progress.getTitolo());
            stmt.setString(4, progress.getDescrizione());
            stmt.setString(5, progress.getDocumentoPath());
            stmt.setTimestamp(6, Timestamp.valueOf(progress.getDataCaricamento()));
            stmt.setString(7, progress.getCommentoGiudice());
            stmt.setInt(8, progress.getGiudiceId());
            if (progress.getDataCommento() != null) {
                stmt.setTimestamp(9, Timestamp.valueOf(progress.getDataCommento()));
            } else {
                stmt.setNull(9, Types.TIMESTAMP);
            }
            stmt.setInt(10, progress.getId());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error updating progress", e);
        }
    }
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM progress WHERE id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error deleting progress", e);
        }
    }
    @Override
    public Progress findById(int id) {
        String sql = "SELECT * FROM progress WHERE id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProgress(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error finding progress by id", e);
        }
    }
    @Override
    public List<Progress> findAll() {
        String sql = "SELECT * FROM progress ORDER BY " + COLUMN_DATA_CARICAMENTO + SQL_ORDER_BY_DESC;
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Progress> progressList = new ArrayList<>();
            while (rs.next()) {
                progressList.add(mapResultSetToProgress(rs));
            }
            return progressList;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error finding all progress", e);
        }
    }
    @Override
    public List<Progress> findByTeam(int teamId) {
        String sql = SQL_SELECT_ALL_FROM_PROGRESS_WHERE + COLUMN_TEAM_ID + SQL_WHERE_ORDER_BY + COLUMN_DATA_CARICAMENTO + SQL_ORDER_BY_DESC;
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Progress> progressList = new ArrayList<>();
                while (rs.next()) {
                    progressList.add(mapResultSetToProgress(rs));
                }
                return progressList;
            }
        } catch (SQLException e) {
            throw new ProgressDAOException("Error finding progress by team", e);
        }
    }
    @Override
    public List<Progress> findByHackathon(int hackathonId) {
        String sql = SQL_SELECT_ALL_FROM_PROGRESS_WHERE + COLUMN_HACKATHON_ID + SQL_WHERE_ORDER_BY + COLUMN_DATA_CARICAMENTO + SQL_ORDER_BY_DESC;
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hackathonId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Progress> progressList = new ArrayList<>();
                while (rs.next()) {
                    progressList.add(mapResultSetToProgress(rs));
                }
                return progressList;
            }
        } catch (SQLException e) {
            throw new ProgressDAOException("Error finding progress by hackathon", e);
        }
    }
    @Override
    public List<Progress> findByTeamAndHackathon(int teamId, int hackathonId) {
        String sql = SQL_SELECT_ALL_FROM_PROGRESS_WHERE + COLUMN_TEAM_ID + " = ? AND " + COLUMN_HACKATHON_ID + SQL_WHERE_ORDER_BY + COLUMN_DATA_CARICAMENTO + SQL_ORDER_BY_DESC;
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            stmt.setInt(2, hackathonId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Progress> progressList = new ArrayList<>();
                while (rs.next()) {
                    progressList.add(mapResultSetToProgress(rs));
                }
                return progressList;
            }
        } catch (SQLException e) {
            throw new ProgressDAOException("Error finding progress by team and hackathon", e);
        }
    }
    @Override
    public List<Progress> findByGiudice(int giudiceId) {
        String sql = SQL_SELECT_ALL_FROM_PROGRESS_WHERE + COLUMN_GIUDICE_ID + SQL_WHERE_ORDER_BY + COLUMN_DATA_COMMENTO + SQL_ORDER_BY_DESC;
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, giudiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Progress> progressList = new ArrayList<>();
                while (rs.next()) {
                    progressList.add(mapResultSetToProgress(rs));
                }
                return progressList;
            }
        } catch (SQLException e) {
            throw new ProgressDAOException("Error finding progress by giudice", e);
        }
    }
    @Override
    public List<Progress> findSenzaCommenti() {
        String sql = SQL_SELECT_ALL_FROM_PROGRESS_WHERE + COLUMN_COMMENTO_GIUDICE + " IS NULL ORDER BY " + COLUMN_DATA_CARICAMENTO + SQL_ORDER_BY_DESC;
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Progress> progressList = new ArrayList<>();
            while (rs.next()) {
                progressList.add(mapResultSetToProgress(rs));
            }
            return progressList;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error finding progress without comments", e);
        }
    }
    @Override
    public List<Progress> findConCommenti() {
        String sql = SQL_SELECT_ALL_FROM_PROGRESS_WHERE + COLUMN_COMMENTO_GIUDICE + " IS NOT NULL ORDER BY " + COLUMN_DATA_COMMENTO + SQL_ORDER_BY_DESC;
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Progress> progressList = new ArrayList<>();
            while (rs.next()) {
                progressList.add(mapResultSetToProgress(rs));
            }
            return progressList;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error finding progress with comments", e);
        }
    }
    @Override
    public boolean aggiungiCommentoGiudice(int progressId, int giudiceId, String commento) {
        String sql = SQL_UPDATE_PROGRESS_SET + COLUMN_COMMENTO_GIUDICE + SQL_PARAM_PLACEHOLDER + COLUMN_GIUDICE_ID + SQL_PARAM_PLACEHOLDER + COLUMN_DATA_COMMENTO + " = ? WHERE id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, commento);
            stmt.setInt(2, giudiceId);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, progressId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error adding judge comment", e);
        }
    }
    @Override
    public boolean aggiornaCommentoGiudice(int progressId, int giudiceId, String nuovoCommento) {
        String sql = SQL_UPDATE_PROGRESS_SET + COLUMN_COMMENTO_GIUDICE + SQL_PARAM_PLACEHOLDER + COLUMN_DATA_COMMENTO + " = ? WHERE id = ? AND " + COLUMN_GIUDICE_ID + " = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuovoCommento);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, progressId);
            stmt.setInt(4, giudiceId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error updating judge comment", e);
        }
    }
    @Override
    public boolean rimuoviCommentoGiudice(int progressId, int giudiceId) {
        String sql = SQL_UPDATE_PROGRESS_SET + COLUMN_COMMENTO_GIUDICE + " = NULL, " + COLUMN_GIUDICE_ID + " = NULL, " + COLUMN_DATA_COMMENTO + " = NULL WHERE id = ? AND " + COLUMN_GIUDICE_ID + " = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, progressId);
            stmt.setInt(2, giudiceId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error removing judge comment", e);
        }
    }
    @Override
    public boolean haCommentoGiudice(int progressId) {
        String sql = "SELECT " + COLUMN_COMMENTO_GIUDICE + " FROM progress WHERE id = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, progressId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(COLUMN_COMMENTO_GIUDICE) != null;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error checking judge comment", e);
        }
    }
    @Override
    public String findCommentoGiudice(int progressId, int giudiceId) {
        String sql = "SELECT " + COLUMN_COMMENTO_GIUDICE + " FROM progress WHERE id = ? AND " + COLUMN_GIUDICE_ID + " = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, progressId);
            stmt.setInt(2, giudiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(COLUMN_COMMENTO_GIUDICE);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error finding judge comment", e);
        }
    }
    @Override
    public int contaProgressiTeam(int teamId) {
        String sql = SQL_SELECT_COUNT_FROM_PROGRESS_WHERE + COLUMN_TEAM_ID + " = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error counting team progress", e);
        }
    }
    @Override
    public int contaProgressiHackathon(int hackathonId) {
        String sql = SQL_SELECT_COUNT_FROM_PROGRESS_WHERE + COLUMN_HACKATHON_ID + " = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hackathonId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error counting hackathon progress", e);
        }
    }
    @Override
    public int contaProgressiCommentati(int giudiceId) {
        String sql = SQL_SELECT_COUNT_FROM_PROGRESS_WHERE + COLUMN_COMMENTO_GIUDICE + " IS NOT NULL AND " + COLUMN_GIUDICE_ID + " = ?";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, giudiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error counting commented progress", e);
        }
    }
    @Override
    public Progress findUltimoProgressoTeam(int teamId) {
        String sql = SQL_SELECT_ALL_FROM_PROGRESS_WHERE + COLUMN_TEAM_ID + SQL_WHERE_ORDER_BY + COLUMN_DATA_CARICAMENTO + SQL_ORDER_BY_DESC + " LIMIT 1";
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProgress(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error finding latest team progress", e);
        }
    }
    @Override
    public List<Progress> findAllOrderByDataCaricamento() {
        String sql = "SELECT * FROM progress ORDER BY " + COLUMN_DATA_CARICAMENTO + SQL_ORDER_BY_DESC;
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Progress> progressList = new ArrayList<>();
            while (rs.next()) {
                progressList.add(mapResultSetToProgress(rs));
            }
            return progressList;
        } catch (SQLException e) {
            throw new ProgressDAOException("Error finding all progress ordered by upload date", e);
        }
    }
    @Override
    public List<Progress> findByTeamOrderByDataCaricamento(int teamId) {
        String sql = SQL_SELECT_ALL_FROM_PROGRESS_WHERE + COLUMN_TEAM_ID + SQL_WHERE_ORDER_BY + COLUMN_DATA_CARICAMENTO + SQL_ORDER_BY_DESC;
        try (Connection conn = cm.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Progress> progressList = new ArrayList<>();
                while (rs.next()) {
                    progressList.add(mapResultSetToProgress(rs));
                }
                return progressList;
            }
        } catch (SQLException e) {
            throw new ProgressDAOException("Error finding team progress ordered by upload date", e);
        }
    }
    /**
     * Mappa un ResultSet in un oggetto Progress
     */
    private Progress mapResultSetToProgress(ResultSet rs) throws SQLException {
        Progress progress = new Progress();
        progress.setId(rs.getInt("id"));
        progress.setTeamId(rs.getInt(COLUMN_TEAM_ID));
        progress.setHackathonId(rs.getInt(COLUMN_HACKATHON_ID));
        progress.setTitolo(rs.getString(COLUMN_TITOLO));
        progress.setDescrizione(rs.getString(COLUMN_DESCRIZIONE));
        progress.setDocumentoPath(rs.getString(COLUMN_DOCUMENTO_PATH));
        progress.setDataCaricamento(rs.getTimestamp(COLUMN_DATA_CARICAMENTO).toLocalDateTime());
        String commentoGiudice = rs.getString(COLUMN_COMMENTO_GIUDICE);
        if (commentoGiudice != null) {
            progress.setCommentoGiudice(commentoGiudice);
            progress.setGiudiceId(rs.getInt(COLUMN_GIUDICE_ID));
            Timestamp dataCommento = rs.getTimestamp(COLUMN_DATA_COMMENTO);
            if (dataCommento != null) {
                progress.setDataCommento(dataCommento.toLocalDateTime());
            }
        }
        return progress;
    }
} 
