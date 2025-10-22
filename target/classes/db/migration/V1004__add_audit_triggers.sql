-- Add audit triggers for automatic logging of comment and ranking changes
-- These triggers ensure comprehensive audit logging without requiring application-level code

-- Function to log comment changes
CREATE OR REPLACE FUNCTION log_comment_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit_log (
            action, 
            table_name, 
            record_id, 
            user_id, 
            details, 
            timestamp
        ) VALUES (
            'COMMENT_ADDED',
            'judge_comment',
            NEW.id,
            NEW.judge_id,
            json_build_object(
                'document_id', NEW.document_id,
                'text_length', length(NEW.text),
                'created_at', NEW.created_at
            )::text,
            CURRENT_TIMESTAMP
        );
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit_log (
            action, 
            table_name, 
            record_id, 
            user_id, 
            details, 
            timestamp
        ) VALUES (
            'COMMENT_UPDATED',
            'judge_comment',
            NEW.id,
            NEW.judge_id,
            json_build_object(
                'document_id', NEW.document_id,
                'old_text_length', length(OLD.text),
                'new_text_length', length(NEW.text),
                'updated_at', NEW.updated_at
            )::text,
            CURRENT_TIMESTAMP
        );
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO audit_log (
            action, 
            table_name, 
            record_id, 
            user_id, 
            details, 
            timestamp
        ) VALUES (
            'COMMENT_DELETED',
            'judge_comment',
            OLD.id,
            OLD.judge_id,
            json_build_object(
                'document_id', OLD.document_id,
                'text_length', length(OLD.text),
                'created_at', OLD.created_at
            )::text,
            CURRENT_TIMESTAMP
        );
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Function to log ranking snapshot creation
CREATE OR REPLACE FUNCTION log_ranking_snapshot_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit_log (
            action, 
            table_name, 
            record_id, 
            user_id, 
            details, 
            timestamp
        ) VALUES (
            'RANKING_PUBLISHED',
            'ranking_snapshot',
            NEW.id,
            NULL, -- User ID will be set by application
            json_build_object(
                'hackathon_id', NEW.hackathon_id,
                'version', NEW.version,
                'payload_size', length(NEW.json_payload::text),
                'created_at', NEW.created_at
            )::text,
            CURRENT_TIMESTAMP
        );
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create triggers only if audit_log table exists
DO $$
BEGIN
    -- Check if audit_log table exists
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'audit_log') THEN
        -- Drop existing triggers if they exist
        DROP TRIGGER IF EXISTS trigger_comment_audit ON judge_comment;
        DROP TRIGGER IF EXISTS trigger_ranking_audit ON ranking_snapshot;
        
        -- Create comment audit trigger
        CREATE TRIGGER trigger_comment_audit
            AFTER INSERT OR UPDATE OR DELETE ON judge_comment
            FOR EACH ROW EXECUTE FUNCTION log_comment_changes();
            
        -- Create ranking audit trigger  
        CREATE TRIGGER trigger_ranking_audit
            AFTER INSERT ON ranking_snapshot
            FOR EACH ROW EXECUTE FUNCTION log_ranking_snapshot_changes();
            
        RAISE NOTICE 'Audit triggers created successfully';
    ELSE
        RAISE NOTICE 'audit_log table not found, skipping trigger creation';
    END IF;
END
$$;
