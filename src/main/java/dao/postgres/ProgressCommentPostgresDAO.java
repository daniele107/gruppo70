package dao.postgres;

import dao.ProgressCommentDAO;
import database.ConnectionManager;
import database.DataAccessException;
import model.ProgressComment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgressCommentPostgresDAO implements ProgressCommentDAO {
    // Using judge_comment table
    private static final String INSERT_SQL = "INSERT INTO judge_comment (document_id, judge_id, text, created_at, updated_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) RETURNING id";
    private static final String UPDATE_SQL = "UPDATE judge_comment SET text = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND judge_id = ?";
    private static final String DELETE_SQL = "DELETE FROM judge_comment WHERE id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT id, document_id, judge_id, text, created_at, updated_at FROM judge_comment WHERE id = ?";
    private static final String FIND_BY_DOC_SQL = "SELECT id, document_id, judge_id, text, created_at, updated_at FROM judge_comment WHERE document_id = ? ORDER BY created_at DESC";
    private static final String FIND_BY_DOC_AND_JUDGE_SQL = "SELECT id, document_id, judge_id, text, created_at, updated_at FROM judge_comment WHERE document_id = ? AND judge_id = ?";
    private static final String COUNT_BY_DOC_SQL = "SELECT COUNT(*) FROM judge_comment WHERE document_id = ?";
    private static final String COUNT_RATE_LIMIT_SQL = "SELECT COUNT(*) FROM judge_comment c JOIN documents d ON c.document_id = d.id WHERE c.judge_id = ? AND d.team_id = ? AND c.created_at > NOW() - INTERVAL '1 hour'";

    private final ConnectionManager cm;

    public ProgressCommentPostgresDAO(ConnectionManager cm) {
        this.cm = cm;
    }

    @Override
    public boolean insert(ProgressComment comment) {
        try (Connection conn = cm.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setInt(1, comment.getDocumentId());
            ps.setInt(2, comment.getJudgeId());
            ps.setString(3, comment.getText());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
            
            
        } catch (SQLException e) {
            throw new DataAccessException("ProgressComment.insert failed", e);
        }
    }

    @Override
    public boolean update(ProgressComment comment) {
        try (Connection conn = cm.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, comment.getText());
            ps.setInt(2, comment.getId());
            ps.setInt(3, comment.getJudgeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("ProgressComment.update failed", e);
        }
    }

    @Override
    public boolean delete(int id) {
        try (Connection conn = cm.getConnection(); PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("ProgressComment.delete failed", e);
        }
    }

    @Override
    public ProgressComment findById(int id) {
        try (Connection conn = cm.getConnection(); PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("ProgressComment.findById failed", e);
        }
    }

    @Override
    public List<ProgressComment> findByDocument(int documentId) {
        List<ProgressComment> list = new ArrayList<>();
        try (Connection conn = cm.getConnection(); PreparedStatement ps = conn.prepareStatement(FIND_BY_DOC_SQL)) {
            ps.setInt(1, documentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("ProgressComment.findByDocument failed", e);
        }
    }

    @Override
    public ProgressComment findByDocumentAndJudge(int documentId, int judgeId) {
        try (Connection conn = cm.getConnection(); PreparedStatement ps = conn.prepareStatement(FIND_BY_DOC_AND_JUDGE_SQL)) {
            ps.setInt(1, documentId);
            ps.setInt(2, judgeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("ProgressComment.findByDocumentAndJudge failed", e);
        }
    }

    @Override
    public int countByDocument(int documentId) {
        try (Connection conn = cm.getConnection(); PreparedStatement ps = conn.prepareStatement(COUNT_BY_DOC_SQL)) {
            ps.setInt(1, documentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("ProgressComment.countByDocument failed", e);
        }
    }

    @Override
    public int countByJudgeAndTeamInLastHour(int judgeId, int teamId) {
        try (Connection conn = cm.getConnection(); PreparedStatement ps = conn.prepareStatement(COUNT_RATE_LIMIT_SQL)) {
            ps.setInt(1, judgeId);
            ps.setInt(2, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException("ProgressComment.countByJudgeAndTeamInLastHour failed", e);
        }
    }

    private ProgressComment map(ResultSet rs) throws SQLException {
        ProgressComment c = new ProgressComment();
        c.setId(rs.getInt("id"));
        c.setDocumentId(rs.getInt("document_id"));
        c.setJudgeId(rs.getInt("judge_id"));
        c.setText(rs.getString("text"));
        Timestamp cAt = rs.getTimestamp("created_at");
        if (cAt != null) c.setCreatedAt(cAt.toLocalDateTime());
        Timestamp uAt = rs.getTimestamp("updated_at");
        if (uAt != null) c.setUpdatedAt(uAt.toLocalDateTime());
        return c;
    }
}


