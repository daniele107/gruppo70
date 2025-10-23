-- =====================================================
-- DATABASE DUMP COMPLETO - HACKATHON MANAGER
-- =====================================================
-- PostgreSQL Database Schema Unificato
-- Version: 2.0
-- Last Updated: 2025-10-22
-- 
-- ISTRUZIONI:
-- 1. Creare il database: CREATE DATABASE hackathon_manager;
-- 2. Eseguire questo script: psql -U postgres -d hackathon_manager -f db_dump.sql
-- =====================================================

-- =====================================================
-- PULIZIA TABELLE ESISTENTI
-- =====================================================
-- Drop in reverse dependency order per evitare errori FK

DROP TABLE IF EXISTS ranking_snapshot CASCADE;
DROP TABLE IF EXISTS judge_comment CASCADE;
DROP TABLE IF EXISTS audit_log CASCADE;
DROP TABLE IF EXISTS notification_read CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS documents CASCADE;
DROP TABLE IF EXISTS valutazione CASCADE;
DROP TABLE IF EXISTS progress CASCADE;
DROP TABLE IF EXISTS richiesta_join CASCADE;
DROP TABLE IF EXISTS team_members CASCADE;
DROP TABLE IF EXISTS registrazione CASCADE;
DROP TABLE IF EXISTS team CASCADE;
DROP TABLE IF EXISTS hackathon CASCADE;
DROP TABLE IF EXISTS utente CASCADE;

-- =====================================================
-- CREAZIONE TABELLE PRINCIPALI
-- =====================================================

-- Tabella Utenti
CREATE TABLE utente (
    id SERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    cognome VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    ruolo VARCHAR(20) NOT NULL CHECK (ruolo IN ('ORGANIZZATORE', 'GIUDICE', 'PARTECIPANTE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabella Hackathon
CREATE TABLE hackathon (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    data_inizio TIMESTAMP NOT NULL,
    data_fine TIMESTAMP NOT NULL,
    sede VARCHAR(255) NOT NULL,
    is_virtuale BOOLEAN DEFAULT FALSE,
    organizzatore_id INTEGER REFERENCES utente(id),
    max_partecipanti INTEGER DEFAULT 100,
    max_team INTEGER DEFAULT 20,
    registrazioni_aperte BOOLEAN DEFAULT FALSE,
    descrizione_problema TEXT,
    evento_avviato BOOLEAN DEFAULT FALSE,
    evento_concluso BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabella Team
CREATE TABLE team (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    hackathon_id INTEGER REFERENCES hackathon(id) ON DELETE CASCADE,
    capo_team_id INTEGER REFERENCES utente(id),
    dimensione_massima INTEGER DEFAULT 4,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(nome, hackathon_id)
);

-- Tabella Team Members (relazione many-to-many)
CREATE TABLE team_members (
    team_id INTEGER REFERENCES team(id) ON DELETE CASCADE,
    utente_id INTEGER REFERENCES utente(id) ON DELETE CASCADE,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (team_id, utente_id)
);

-- Tabella Registrazioni
CREATE TABLE registrazione (
    id SERIAL PRIMARY KEY,
    utente_id INTEGER REFERENCES utente(id) ON DELETE CASCADE,
    hackathon_id INTEGER REFERENCES hackathon(id) ON DELETE CASCADE,
    data_registrazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ruolo VARCHAR(20) NOT NULL CHECK (ruolo IN ('ORGANIZZATORE', 'GIUDICE', 'PARTECIPANTE')),
    confermata BOOLEAN DEFAULT FALSE,
    UNIQUE(utente_id, hackathon_id)
);

-- Tabella Richieste Join Team
CREATE TABLE richiesta_join (
    id SERIAL PRIMARY KEY,
    utente_id INTEGER REFERENCES utente(id) ON DELETE CASCADE,
    team_id INTEGER REFERENCES team(id) ON DELETE CASCADE,
    messaggio_motivazionale TEXT,
    data_richiesta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    stato VARCHAR(20) DEFAULT 'IN_ATTESA' CHECK (stato IN ('IN_ATTESA', 'ACCETTATA', 'RIFIUTATA')),
    UNIQUE(utente_id, team_id)
);

-- Tabella Progress
CREATE TABLE progress (
    id SERIAL PRIMARY KEY,
    team_id INTEGER REFERENCES team(id) ON DELETE CASCADE,
    hackathon_id INTEGER REFERENCES hackathon(id) ON DELETE CASCADE,
    titolo VARCHAR(200) NOT NULL,
    descrizione TEXT,
    documento_path VARCHAR(500),
    data_caricamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    commento_giudice TEXT,
    giudice_id INTEGER REFERENCES utente(id),
    data_commento TIMESTAMP,
    file_size INTEGER DEFAULT NULL,
    file_type VARCHAR(50) DEFAULT NULL,
    file_hash VARCHAR(255) DEFAULT NULL
);

-- Tabella Valutazioni
CREATE TABLE valutazione (
    id SERIAL PRIMARY KEY,
    giudice_id INTEGER REFERENCES utente(id) ON DELETE CASCADE,
    team_id INTEGER REFERENCES team(id) ON DELETE CASCADE,
    hackathon_id INTEGER REFERENCES hackathon(id) ON DELETE CASCADE,
    voto INTEGER NOT NULL CHECK (voto >= 0 AND voto <= 10),
    commento TEXT,
    data_valutazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(giudice_id, team_id)
);

-- Tabella Notifiche
CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    utente_id INTEGER REFERENCES utente(id) ON DELETE CASCADE,
    titolo VARCHAR(200) NOT NULL,
    messaggio TEXT NOT NULL,
    tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('INFO', 'SUCCESS', 'WARNING', 'ERROR', 'TEAM_JOIN_REQUEST', 'NEW_COMMENT', 'EVENT_UPDATE', 'SYSTEM')),
    data_creazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    letta BOOLEAN DEFAULT FALSE,
    data_lettura TIMESTAMP NULL
);

-- Tabella Notifiche Lette (tracking)
CREATE TABLE notification_read (
    id SERIAL PRIMARY KEY,
    notifica_id INTEGER REFERENCES notifications(id) ON DELETE CASCADE,
    utente_id INTEGER REFERENCES utente(id) ON DELETE CASCADE,
    data_lettura TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(notifica_id, utente_id)
);

-- Tabella Documenti
CREATE TABLE documents (
    id SERIAL PRIMARY KEY,
    team_id INTEGER REFERENCES team(id) ON DELETE CASCADE,
    hackathon_id INTEGER REFERENCES hackathon(id) ON DELETE CASCADE,
    nome VARCHAR(255) NOT NULL,
    percorso VARCHAR(500) NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    dimensione BIGINT NOT NULL,
    hash VARCHAR(255),
    data_caricamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    utente_caricamento INTEGER REFERENCES utente(id),
    descrizione TEXT,
    validato BOOLEAN DEFAULT FALSE,
    validatore_id INTEGER REFERENCES utente(id),
    data_validazione TIMESTAMP NULL
);

-- Tabella Commenti Giudici sui Documenti
CREATE TABLE judge_comment (
    id SERIAL PRIMARY KEY,
    document_id INTEGER NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    judge_id INTEGER NOT NULL REFERENCES utente(id) ON DELETE CASCADE,
    text TEXT NOT NULL CHECK (char_length(text) BETWEEN 5 AND 2000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabella Snapshot Classifica (immutabile)
CREATE TABLE ranking_snapshot (
    id SERIAL PRIMARY KEY,
    hackathon_id INTEGER NOT NULL REFERENCES hackathon(id) ON DELETE CASCADE,
    version INTEGER NOT NULL,
    json_payload JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(hackathon_id, version)
);

-- Tabella Audit Log (logging completo)
CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    utente_id INTEGER REFERENCES utente(id) ON DELETE SET NULL,
    azione VARCHAR(100) NOT NULL,
    risorsa VARCHAR(100) NOT NULL,
    risorsa_id INTEGER,
    dettagli TEXT,
    ip_address INET,
    user_agent TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    session_id VARCHAR(255),
    risultato VARCHAR(20) DEFAULT 'SUCCESS', -- SUCCESS, FAILURE, WARNING
    durata_ms INTEGER,
    metadata JSONB
);

-- =====================================================
-- CREAZIONE INDICI PER PERFORMANCE
-- =====================================================

-- Indici tabella utente
CREATE INDEX idx_utente_login ON utente(login);
CREATE INDEX idx_utente_email ON utente(email);
CREATE INDEX idx_utente_ruolo ON utente(ruolo);

-- Indici tabella hackathon
CREATE INDEX idx_hackathon_organizzatore ON hackathon(organizzatore_id);
CREATE INDEX idx_hackathon_data_inizio ON hackathon(data_inizio);
CREATE INDEX idx_hackathon_registrazioni_aperte ON hackathon(registrazioni_aperte);
CREATE INDEX idx_hackathon_evento_avviato ON hackathon(evento_avviato);

-- Indici tabella team
CREATE INDEX idx_team_hackathon ON team(hackathon_id);
CREATE INDEX idx_team_capo_team ON team(capo_team_id);
CREATE INDEX idx_team_created_at ON team(created_at);

-- Indici tabella team_members
CREATE INDEX idx_team_members_team ON team_members(team_id);
CREATE INDEX idx_team_members_utente ON team_members(utente_id);

-- Indici tabella registrazione
CREATE INDEX idx_registrazione_utente ON registrazione(utente_id);
CREATE INDEX idx_registrazione_hackathon ON registrazione(hackathon_id);
CREATE INDEX idx_registrazione_confermata ON registrazione(confermata);
CREATE INDEX idx_registrazione_active_judges ON registrazione(hackathon_id, utente_id) WHERE ruolo = 'GIUDICE';

-- Indici tabella richiesta_join
CREATE INDEX idx_richiesta_join_utente ON richiesta_join(utente_id);
CREATE INDEX idx_richiesta_join_team ON richiesta_join(team_id);
CREATE INDEX idx_richiesta_join_stato ON richiesta_join(stato);

-- Indici tabella progress
CREATE INDEX idx_progress_team ON progress(team_id);
CREATE INDEX idx_progress_hackathon ON progress(hackathon_id);
CREATE INDEX idx_progress_data_caricamento ON progress(data_caricamento);

-- Indici tabella valutazione
CREATE INDEX idx_valutazione_giudice ON valutazione(giudice_id);
CREATE INDEX idx_valutazione_team ON valutazione(team_id);
CREATE INDEX idx_valutazione_hackathon ON valutazione(hackathon_id);
CREATE INDEX idx_valutazione_voto ON valutazione(voto);
CREATE INDEX idx_valutazione_hackathon_team ON valutazione(hackathon_id, team_id);
CREATE INDEX idx_valutazione_judge_team ON valutazione(giudice_id, team_id);

-- Indici tabella notifications
CREATE INDEX idx_notifications_utente ON notifications(utente_id);
CREATE INDEX idx_notifications_tipo ON notifications(tipo);
CREATE INDEX idx_notifications_letta ON notifications(letta);
CREATE INDEX idx_notifications_data_creazione ON notifications(data_creazione);

-- Indici tabella notification_read
CREATE INDEX idx_notification_read_notifica ON notification_read(notifica_id);
CREATE INDEX idx_notification_read_utente ON notification_read(utente_id);

-- Indici tabella documents
CREATE INDEX idx_documents_team ON documents(team_id);
CREATE INDEX idx_documents_hackathon ON documents(hackathon_id);
CREATE INDEX idx_documents_tipo ON documents(tipo);
CREATE INDEX idx_documents_validato ON documents(validato);
CREATE INDEX idx_documents_data_caricamento ON documents(data_caricamento);
CREATE INDEX idx_documents_utente_caricamento ON documents(utente_caricamento);

-- Indici tabella judge_comment
CREATE INDEX idx_judge_comment_document ON judge_comment(document_id);
CREATE INDEX idx_judge_comment_judge ON judge_comment(judge_id);
CREATE INDEX idx_judge_comment_created ON judge_comment(created_at);
CREATE INDEX idx_judge_comment_document_judge ON judge_comment(document_id, judge_id);
CREATE INDEX idx_judge_comment_judge_created ON judge_comment(judge_id, created_at);

-- Indici tabella ranking_snapshot
CREATE INDEX idx_ranking_snapshot_hackathon ON ranking_snapshot(hackathon_id);
CREATE INDEX idx_ranking_snapshot_created ON ranking_snapshot(created_at);

-- Indici tabella audit_log
CREATE INDEX idx_audit_log_utente ON audit_log(utente_id);
CREATE INDEX idx_audit_log_azione ON audit_log(azione);
CREATE INDEX idx_audit_log_risorsa ON audit_log(risorsa);
CREATE INDEX idx_audit_log_timestamp ON audit_log(timestamp);
CREATE INDEX idx_audit_log_risultato ON audit_log(risultato);

-- =====================================================
-- TRIGGER E FUNZIONI PER AUDIT AUTOMATICO
-- =====================================================

-- Funzione per log dei commenti
CREATE OR REPLACE FUNCTION log_comment_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit_log (
            utente_id, 
            azione, 
            risorsa, 
            risorsa_id, 
            dettagli, 
            timestamp
        ) VALUES (
            NEW.judge_id,
            'COMMENT_ADDED',
            'judge_comment',
            NEW.id,
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
            utente_id, 
            azione, 
            risorsa, 
            risorsa_id, 
            dettagli, 
            timestamp
        ) VALUES (
            NEW.judge_id,
            'COMMENT_UPDATED',
            'judge_comment',
            NEW.id,
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
            utente_id, 
            azione, 
            risorsa, 
            risorsa_id, 
            dettagli, 
            timestamp
        ) VALUES (
            OLD.judge_id,
            'COMMENT_DELETED',
            'judge_comment',
            OLD.id,
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

-- Funzione per log delle pubblicazioni classifica
CREATE OR REPLACE FUNCTION log_ranking_snapshot_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit_log (
            utente_id, 
            azione, 
            risorsa, 
            risorsa_id, 
            dettagli, 
            timestamp
        ) VALUES (
            NULL, -- User ID will be set by application
            'RANKING_PUBLISHED',
            'ranking_snapshot',
            NEW.id,
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

-- Creazione trigger
CREATE TRIGGER trigger_comment_audit
    AFTER INSERT OR UPDATE OR DELETE ON judge_comment
    FOR EACH ROW EXECUTE FUNCTION log_comment_changes();

CREATE TRIGGER trigger_ranking_audit
    AFTER INSERT ON ranking_snapshot
    FOR EACH ROW EXECUTE FUNCTION log_ranking_snapshot_changes();

-- =====================================================
-- INSERIMENTO DATI DI TEST
-- =====================================================

-- Utenti di esempio
INSERT INTO utente (login, password, nome, cognome, email, ruolo) VALUES
('admin', 'admin123', 'Admin', 'System', 'admin@hackathon.com', 'ORGANIZZATORE'),
('giudice1', 'giudice123', 'Mario', 'Rossi', 'mario.rossi@hackathon.com', 'GIUDICE'),
('giudice2', 'giudice123', 'Giulia', 'Bianchi', 'giulia.bianchi@hackathon.com', 'GIUDICE'),
('partecipante1', 'part123', 'Luca', 'Verdi', 'luca.verdi@hackathon.com', 'PARTECIPANTE'),
('partecipante2', 'part123', 'Anna', 'Neri', 'anna.neri@hackathon.com', 'PARTECIPANTE');

-- Hackathon di esempio
INSERT INTO hackathon (nome, data_inizio, data_fine, sede, is_virtuale, organizzatore_id, max_partecipanti, max_team, descrizione_problema) VALUES
('Hackathon 2025', '2025-06-15 09:00:00', '2025-06-17 18:00:00', 'Milano, Italia', FALSE, 1, 50, 10, 'Sviluppo di soluzioni innovative per la sostenibilità ambientale');

-- Registrazioni
INSERT INTO registrazione (utente_id, hackathon_id, ruolo, confermata) VALUES
(2, 1, 'GIUDICE', TRUE),
(3, 1, 'GIUDICE', TRUE),
(4, 1, 'PARTECIPANTE', TRUE),
(5, 1, 'PARTECIPANTE', TRUE);

-- Team di esempio
INSERT INTO team (nome, hackathon_id, capo_team_id, dimensione_massima) VALUES
('Team Alpha', 1, 4, 4);

-- Team members
INSERT INTO team_members (team_id, utente_id) VALUES
(1, 4),
(1, 5);

-- Notifiche di benvenuto
INSERT INTO notifications (utente_id, titolo, messaggio, tipo, letta) VALUES
(4, 'Benvenuto in Hackathon Manager!', 'Ciao Luca! Benvenuto nella piattaforma di gestione hackathon. Inizia esplorando gli eventi disponibili o creando il tuo primo team.', 'SUCCESS', FALSE),
(5, 'Benvenuto in Hackathon Manager!', 'Ciao Anna! Benvenuto nella piattaforma di gestione hackathon. Inizia esplorando gli eventi disponibili o creando il tuo primo team.', 'SUCCESS', FALSE),
(4, 'Team Alpha creato', 'Il tuo team Alpha è stato creato con successo per l''hackathon Hackathon 2025.', 'INFO', TRUE);

-- Documento di esempio
INSERT INTO documents (team_id, hackathon_id, nome, percorso, tipo, dimensione, utente_caricamento, descrizione) VALUES
(1, 1, 'Progetto_Alpha_v1.txt', '/uploads/team1/Progetto_Alpha_v1.txt', 'text/plain', 1024, 4, 'Prima versione del progetto del team Alpha - Documento di esempio per demo');

-- Audit log di esempio
INSERT INTO audit_log (utente_id, azione, risorsa, risorsa_id, dettagli, risultato) VALUES
(1, 'LOGIN', 'USER', 1, 'Admin login successful', 'SUCCESS'),
(4, 'CREATE_TEAM', 'TEAM', 1, 'Team Alpha created for Hackathon 2025', 'SUCCESS'),
(4, 'UPLOAD_DOCUMENT', 'DOCUMENT', 1, 'Uploaded Progetto_Alpha_v1.txt (1KB)', 'SUCCESS');

-- =====================================================
-- CONFERMA COMPLETAMENTO
-- =====================================================

-- Conteggio tabelle create
SELECT 'Database inizializzato con successo!' AS status;
SELECT 'Tabelle create: ' || count(*) AS tabelle_totali FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE';
SELECT 'Indici creati: ' || count(*) AS indici_totali FROM pg_indexes WHERE schemaname = 'public';
SELECT 'Trigger creati: ' || count(*) AS trigger_totali FROM pg_trigger WHERE tgname NOT LIKE 'RI_%' AND tgname NOT LIKE 'pg_%';

-- =====================================================
-- CREDENZIALI DEFAULT
-- =====================================================
-- Login: admin         | Password: admin123      | Ruolo: ORGANIZZATORE
-- Login: giudice1      | Password: giudice123    | Ruolo: GIUDICE
-- Login: giudice2      | Password: giudice123    | Ruolo: GIUDICE
-- Login: partecipante1 | Password: part123       | Ruolo: PARTECIPANTE
-- Login: partecipante2 | Password: part123       | Ruolo: PARTECIPANTE
-- =====================================================

