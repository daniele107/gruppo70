-- Create judge_comment table with constraints and indexes
CREATE TABLE IF NOT EXISTS judge_comment (
    id SERIAL PRIMARY KEY,
    document_id INTEGER NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    judge_id INTEGER NOT NULL REFERENCES utente(id) ON DELETE CASCADE,
    text TEXT NOT NULL CHECK (char_length(text) BETWEEN 5 AND 2000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_judge_comment_document ON judge_comment(document_id);
CREATE INDEX IF NOT EXISTS idx_judge_comment_judge ON judge_comment(judge_id);
CREATE INDEX IF NOT EXISTS idx_judge_comment_created ON judge_comment(created_at);



