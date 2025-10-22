# 🗄️ Configurazione Database - Hackathon Manager

## 📋 Prerequisiti

1. **PostgreSQL 17** (o versione compatibile) installato
2. **Servizio PostgreSQL** in esecuzione
3. **Credenziali amministrative** (default: `postgres`)

---

## 🚀 Inizializzazione Database (UNICO COMANDO)

### Windows (PowerShell)

```powershell
# 1. Creare il database
$env:PGPASSWORD = "TUA_PASSWORD_POSTGRES"; psql -U postgres -c "CREATE DATABASE hackathon_manager;"

# 2. Eseguire il dump unificato
$env:PGPASSWORD = "TUA_PASSWORD_POSTGRES"; Get-Content "db_dump.sql" | psql -U postgres -d hackathon_manager
```

### Linux/Mac (Bash)

```bash
# 1. Creare il database
PGPASSWORD="TUA_PASSWORD_POSTGRES" psql -U postgres -c "CREATE DATABASE hackathon_manager;"

# 2. Eseguire il dump unificato
PGPASSWORD="TUA_PASSWORD_POSTGRES" psql -U postgres -d hackathon_manager -f db_dump.sql
```

**Sostituisci `TUA_PASSWORD_POSTGRES` con la tua password PostgreSQL!**

---

## ⚙️ Configurazione File `db.properties`

Modifica il file `src/main/resources/db.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/hackathon_manager
db.username=postgres
db.password=TUA_PASSWORD_POSTGRES  # ← CAMBIA QUESTO!
db.driver=org.postgresql.Driver
```

---

## ✅ Verifica Installazione

Dopo aver eseguito il dump, verifica che tutto sia corretto:

```sql
-- Verifica tabelle create
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;

-- Verifica dati di test
SELECT login, nome, cognome, ruolo FROM utente;
```

Dovresti vedere:
- ✅ 14 tabelle create
- ✅ 74 indici per performance
- ✅ 2 trigger per audit automatico
- ✅ 5 utenti di test
- ✅ 1 hackathon di esempio
- ✅ 1 team con 2 membri

---

## 🔐 Credenziali Default (DA CAMBIARE IN PRODUZIONE!)

| Login | Password | Ruolo |
|-------|----------|-------|
| `admin` | `admin123` | ORGANIZZATORE |
| `giudice1` | `giudice123` | GIUDICE |
| `giudice2` | `giudice123` | GIUDICE |
| `partecipante1` | `part123` | PARTECIPANTE |
| `partecipante2` | `part123` | PARTECIPANTE |

---

## 📊 Struttura Database

### Tabelle Principali
- **`utente`** - Gestione utenti (organizzatori, giudici, partecipanti)
- **`hackathon`** - Eventi hackathon
- **`team`** - Team partecipanti
- **`registrazione`** - Iscrizioni agli eventi
- **`valutazione`** - Valutazioni dei giudici
- **`progress`** - Avanzamento progetti team
- **`documents`** - Gestione file caricati
- **`notifications`** - Sistema notifiche
- **`judge_comment`** - Commenti giudici sui documenti
- **`ranking_snapshot`** - Snapshot immutabili delle classifiche
- **`audit_log`** - Log completo delle azioni

### Trigger Automatici
- **`trigger_comment_audit`** - Log automatico commenti giudici
- **`trigger_ranking_audit`** - Log automatico pubblicazione classifiche

---

## 🔄 Reset Database (se necessario)

Per resettare completamente il database:

```powershell
# Windows
$env:PGPASSWORD = "TUA_PASSWORD"; psql -U postgres -c "DROP DATABASE IF EXISTS hackathon_manager;"
$env:PGPASSWORD = "TUA_PASSWORD"; psql -U postgres -c "CREATE DATABASE hackathon_manager;"
$env:PGPASSWORD = "TUA_PASSWORD"; Get-Content "db_dump.sql" | psql -U postgres -d hackathon_manager
```

---

## ⚠️ Note Importanti

1. **NON committare mai** `db.properties` con password reali
2. **Cambia le password** degli utenti di test in produzione
3. Il file `db_dump.sql` è **idempotente**: può essere eseguito più volte
4. I trigger di audit sono **automatici**: registrano modifiche senza codice applicativo
5. Gli indici sono **ottimizzati** per le query più comuni dell'applicativo

---

## 🆘 Troubleshooting

### Errore: "database is being accessed by other users"
```powershell
$env:PGPASSWORD = "TUA_PASSWORD"; psql -U postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'hackathon_manager' AND pid != pg_backend_pid();"
```

### Errore: "role 'postgres' does not exist"
Crea l'utente postgres o usa un altro utente amministrativo.

### Errore: "psql: command not found"
Aggiungi PostgreSQL al PATH:
```powershell
$env:Path += ";C:\Program Files\PostgreSQL\17\bin"
```

---

## 📧 Supporto

Per problemi con il database, verifica:
1. PostgreSQL è in esecuzione? → `Get-Service postgresql*`
2. Le credenziali sono corrette?
3. Il database esiste? → `psql -U postgres -l`
4. Il file `db_dump.sql` è accessibile?

---

**Ultima modifica:** 22 Ottobre 2025  
**Versione Database:** 2.0  
**Compatibilità:** PostgreSQL 12+

