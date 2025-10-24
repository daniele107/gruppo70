-- Add contenuto column to documents table for file content storage
ALTER TABLE documents ADD COLUMN IF NOT EXISTS contenuto BYTEA;

-- Add comment to explain the column purpose
COMMENT ON COLUMN documents.contenuto IS 'Binary content of the uploaded file';

-- Create index for content-based queries if needed in the future
CREATE INDEX IF NOT EXISTS idx_documents_contenuto ON documents(id) WHERE contenuto IS NOT NULL;

