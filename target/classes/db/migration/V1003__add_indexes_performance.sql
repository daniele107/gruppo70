-- Add performance indexes for comment and ranking queries
-- These indexes optimize the most common queries in DocumentReviewService and RankingService

-- Index for finding comments by document (most common query)
CREATE INDEX IF NOT EXISTS idx_judge_comment_document_judge ON judge_comment(document_id, judge_id);

-- Index for rate limiting queries (comments by judge and team in time window)
CREATE INDEX IF NOT EXISTS idx_judge_comment_judge_created ON judge_comment(judge_id, created_at);

-- Composite index for valutazione queries used in ranking calculation
CREATE INDEX IF NOT EXISTS idx_valutazione_hackathon_team ON valutazione(hackathon_id, team_id);
CREATE INDEX IF NOT EXISTS idx_valutazione_judge_team ON valutazione(giudice_id, team_id);

-- Index for team creation timestamp (used in tie-breaker)
CREATE INDEX IF NOT EXISTS idx_team_data_creazione ON team(data_creazione);

-- Index for hackathon-team relationship queries
CREATE INDEX IF NOT EXISTS idx_team_hackathon ON team(hackathon_id) WHERE hackathon_id IS NOT NULL;

-- Partial index for active registrations (performance optimization)
CREATE INDEX IF NOT EXISTS idx_registrazione_active_judges 
ON registrazione(hackathon_id, utente_id) 
WHERE ruolo = 'GIUDICE';
