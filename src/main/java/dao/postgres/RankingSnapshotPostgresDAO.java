package dao.postgres;

import dao.RankingSnapshotDAO;
import database.ConnectionManager;
import database.DataAccessException;
import model.RankingSnapshot;

import java.sql.*;

public class RankingSnapshotPostgresDAO implements RankingSnapshotDAO {
    private static final String INSERT_SQL = "INSERT INTO ranking_snapshot (hackathon_id, version, json_payload, created_at) VALUES (?, ?, ?::jsonb, CURRENT_TIMESTAMP) RETURNING id";
    private static final String FIND_LATEST_SQL = "SELECT id, hackathon_id, version, json_payload, created_at FROM ranking_snapshot WHERE hackathon_id = ? ORDER BY version DESC LIMIT 1";
    private static final String FIND_MAX_VERSION_SQL = "SELECT COALESCE(MAX(version), 0) FROM ranking_snapshot WHERE hackathon_id = ?";
    private final ConnectionManager cm;

    public RankingSnapshotPostgresDAO(ConnectionManager cm) { this.cm = cm; }

    @Override
    public int insert(RankingSnapshot snapshot) {
        try (Connection conn = cm.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setInt(1, snapshot.getHackathonId());
            ps.setInt(2, snapshot.getVersion());
            ps.setString(3, snapshot.getJsonPayload());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new DataAccessException("RankingSnapshot.insert failed", e);
        }
    }

    @Override
    public RankingSnapshot findLatestByHackathon(int hackathonId) {
        try (Connection conn = cm.getConnection(); PreparedStatement ps = conn.prepareStatement(FIND_LATEST_SQL)) {
            ps.setInt(1, hackathonId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RankingSnapshot s = new RankingSnapshot();
                    s.setId(rs.getInt("id"));
                    s.setHackathonId(rs.getInt("hackathon_id"));
                    s.setVersion(rs.getInt("version"));
                    s.setJsonPayload(rs.getString("json_payload"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) s.setCreatedAt(ts.toLocalDateTime());
                    return s;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("RankingSnapshot.findLatestByHackathon failed", e);
        }
    }

    @Override
    public int findMaxVersion(int hackathonId) {
        try (Connection conn = cm.getConnection(); PreparedStatement ps = conn.prepareStatement(FIND_MAX_VERSION_SQL)) {
            ps.setInt(1, hackathonId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("RankingSnapshot.findMaxVersion failed", e);
        }
    }
}


