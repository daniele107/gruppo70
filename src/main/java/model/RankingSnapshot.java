package model;

import java.time.LocalDateTime;

/**
 * Snapshot immutabile della classifica pubblicata per un hackathon.
 */
public class RankingSnapshot {
    private int id;
    private int hackathonId;
    private int version;
    private String jsonPayload;
    private LocalDateTime createdAt;

    public RankingSnapshot() {
        this.createdAt = LocalDateTime.now();
    }

    public RankingSnapshot(int hackathonId, int version, String jsonPayload) {
        this();
        this.hackathonId = hackathonId;
        this.version = version;
        this.jsonPayload = jsonPayload;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getHackathonId() { return hackathonId; }
    public void setHackathonId(int hackathonId) { this.hackathonId = hackathonId; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public String getJsonPayload() { return jsonPayload; }
    public void setJsonPayload(String jsonPayload) { this.jsonPayload = jsonPayload; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}


