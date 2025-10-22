-- Create ranking_snapshot table with unique version per hackathon
CREATE TABLE IF NOT EXISTS ranking_snapshot (
    id SERIAL PRIMARY KEY,
    hackathon_id INTEGER NOT NULL REFERENCES hackathon(id) ON DELETE CASCADE,
    version INTEGER NOT NULL,
    json_payload JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(hackathon_id, version)
);

CREATE INDEX IF NOT EXISTS idx_ranking_snapshot_hackathon ON ranking_snapshot(hackathon_id);
CREATE INDEX IF NOT EXISTS idx_ranking_snapshot_created ON ranking_snapshot(created_at);



