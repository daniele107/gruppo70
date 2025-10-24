-- =====================================================
-- DATABASE DUMP SEMPLIFICATO - HACKATHON MANAGER
-- =====================================================
-- Versione minima per testing
-- Last Updated: 2025-10-23
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

-- Tabella Team Members
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
    data_validazione TIMESTAMP NULL,
    contenuto BYTEA
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

-- Tabella Notifiche Lette
CREATE TABLE notification_read (
    id SERIAL PRIMARY KEY,
    notifica_id INTEGER REFERENCES notifications(id) ON DELETE CASCADE,
    utente_id INTEGER REFERENCES utente(id) ON DELETE CASCADE,
    data_lettura TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(notifica_id, utente_id)
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

-- Tabella Audit Log (semplificata)
CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    utente_id INTEGER REFERENCES utente(id) ON DELETE CASCADE,
    azione VARCHAR(50) NOT NULL,
    risorsa VARCHAR(50) NOT NULL,
    risorsa_id INTEGER,
    dettagli TEXT,
    risultato VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabella Ranking Snapshot
CREATE TABLE ranking_snapshot (
    id SERIAL PRIMARY KEY,
    hackathon_id INTEGER REFERENCES hackathon(id) ON DELETE CASCADE,
    version INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(hackathon_id, version)
);

-- =====================================================
-- INDICI PER PERFORMANCE
-- =====================================================

-- Indici per tabella utente
CREATE INDEX IF NOT EXISTS idx_utente_login ON utente(login);
CREATE INDEX IF NOT EXISTS idx_utente_ruolo ON utente(ruolo);

-- Indici per tabella hackathon
CREATE INDEX IF NOT EXISTS idx_hackathon_data_inizio ON hackathon(data_inizio);
CREATE INDEX IF NOT EXISTS idx_hackathon_evento_avviato ON hackathon(evento_avviato);
CREATE INDEX IF NOT EXISTS idx_hackathon_evento_concluso ON hackathon(evento_concluso);
CREATE INDEX IF NOT EXISTS idx_hackathon_organizzatore ON hackathon(organizzatore_id);

-- Indici per tabella team
CREATE INDEX IF NOT EXISTS idx_team_hackathon ON team(hackathon_id);
CREATE INDEX IF NOT EXISTS idx_team_capo_team ON team(capo_team_id);

-- Indici per tabella registrazione
CREATE INDEX IF NOT EXISTS idx_registrazione_utente ON registrazione(utente_id);
CREATE INDEX IF NOT EXISTS idx_registrazione_hackathon ON registrazione(hackathon_id);
CREATE INDEX IF NOT EXISTS idx_registrazione_ruolo ON registrazione(ruolo);
CREATE INDEX IF NOT EXISTS idx_registrazione_confermata ON registrazione(confermata);

-- Indici per tabella progress
CREATE INDEX IF NOT EXISTS idx_progress_team ON progress(team_id);
CREATE INDEX IF NOT EXISTS idx_progress_hackathon ON progress(hackathon_id);
CREATE INDEX IF NOT EXISTS idx_progress_data_caricamento ON progress(data_caricamento);

-- Indici per tabella valutazione
CREATE INDEX IF NOT EXISTS idx_valutazione_giudice ON valutazione(giudice_id);
CREATE INDEX IF NOT EXISTS idx_valutazione_team ON valutazione(team_id);
CREATE INDEX IF NOT EXISTS idx_valutazione_hackathon ON valutazione(hackathon_id);
CREATE INDEX IF NOT EXISTS idx_valutazione_voto ON valutazione(voto);
CREATE INDEX IF NOT EXISTS idx_valutazione_data ON valutazione(data_valutazione);

-- Indici per tabella documents
CREATE INDEX IF NOT EXISTS idx_documents_team ON documents(team_id);
CREATE INDEX IF NOT EXISTS idx_documents_hackathon ON documents(hackathon_id);
CREATE INDEX IF NOT EXISTS idx_documents_utente_caricamento ON documents(utente_caricamento);
CREATE INDEX IF NOT EXISTS idx_documents_tipo ON documents(tipo);

-- Indici per tabella notifications
CREATE INDEX IF NOT EXISTS idx_notifications_utente ON notifications(utente_id);
CREATE INDEX IF NOT EXISTS idx_notifications_tipo ON notifications(tipo);
CREATE INDEX IF NOT EXISTS idx_notifications_data ON notifications(data_creazione);
CREATE INDEX IF NOT EXISTS idx_notifications_letta ON notifications(letta);

-- =====================================================
-- DATI DI TEST MINIMALI
-- =====================================================

-- Utenti di test
INSERT INTO utente (login, password, nome, cognome, email, ruolo) VALUES
('admin', 'admin123', 'Admin', 'System', 'admin@hackathon.com', 'ORGANIZZATORE'),
('giudice1', 'giudice123', 'Mario', 'Rossi', 'mario.rossi@hackathon.com', 'GIUDICE'),
('giudice2', 'giudice123', 'Giulia', 'Bianchi', 'giulia.bianchi@hackathon.com', 'GIUDICE'),
('partecipante1', 'part123', 'Luca', 'Verdi', 'luca.verdi@hackathon.com', 'PARTECIPANTE'),
('partecipante2', 'part123', 'Anna', 'Neri', 'anna.neri@hackathon.com', 'PARTECIPANTE');

-- Hackathon CONCLUSO (pronto per le valutazioni)
INSERT INTO hackathon (nome, data_inizio, data_fine, sede, is_virtuale, organizzatore_id, max_partecipanti, max_team, descrizione_problema, evento_avviato, evento_concluso) VALUES
('Hackathon Test 2025', NOW() - INTERVAL '2 days', NOW() - INTERVAL '1 hour', 'Milano, Italia', FALSE, 1, 50, 10, 'Sviluppo di soluzioni innovative per la gestione del tempo', TRUE, TRUE);

-- Team di test
INSERT INTO team (nome, hackathon_id, capo_team_id, dimensione_massima) VALUES
('Team Alpha', 1, 4, 4);

-- Membri del team
INSERT INTO team_members (team_id, utente_id) VALUES
(1, 4), -- Luca Verdi
(1, 5); -- Anna Neri

-- Registrazioni confermate
INSERT INTO registrazione (utente_id, hackathon_id, ruolo, confermata) VALUES
(1, 1, 'ORGANIZZATORE', TRUE),
(2, 1, 'GIUDICE', TRUE),
(3, 1, 'GIUDICE', TRUE),
(4, 1, 'PARTECIPANTE', TRUE),
(5, 1, 'PARTECIPANTE', TRUE);

-- Progresso del team (necessario per la validazione)
INSERT INTO progress (team_id, hackathon_id, titolo, descrizione, documento_path, data_caricamento) VALUES
(1, 1, 'Prototipo Iniziale', 'Primo prototipo del progetto Team Alpha', '/uploads/team1/prototype.pdf', NOW());

-- Documento di test
INSERT INTO documents (team_id, hackathon_id, nome, percorso, tipo, dimensione, utente_caricamento, descrizione, contenuto) VALUES
(1, 1, 'prototipo.pdf', '/uploads/team1/prototype.pdf', 'application/pdf', 1024, 4, 'Prototipo del progetto Team Alpha', 'fake pdf content'::bytea);

-- =====================================================
-- CONFERMA COMPLETAMENTO
-- =====================================================

-- Conteggio tabelle create
SELECT 'Database inizializzato con successo!' AS status;
SELECT 'Tabelle create: ' || count(*) AS tabelle_totali FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE';
SELECT 'Hackathon conclusi (pronti per valutazioni): ' || count(*) AS hackathon_conclusi FROM hackathon WHERE evento_avviato = true AND evento_concluso = true;
