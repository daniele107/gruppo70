package dao;

import model.RankingSnapshot;

public interface RankingSnapshotDAO {
    int insert(RankingSnapshot snapshot);
    RankingSnapshot findLatestByHackathon(int hackathonId);
    int findMaxVersion(int hackathonId);
}


