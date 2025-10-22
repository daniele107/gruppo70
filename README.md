# Hackathon Manager - Applicativo Definitivo

## 📁 Struttura del Progetto

```
Applicativo-Definitivo/
├── 📁 config/                    # File di configurazione
│   ├── db.properties            # Configurazione database
│   └── security-config.json     # Configurazione sicurezza
├── 📁 database/                 # Script e dump database
│   ├── db_dump.sql             # Dump completo del database
│   ├── check_events.sql        # Script di controllo eventi
│   └── update_dates.sql        # Script aggiornamento date
├── 📁 documentazione/           # Documentazione del progetto
│   └── 📁 javadoc/             # Documentazione Javadoc
│       └── index.html          # Pagina principale documentazione
├── 📁 lib/                      # Librerie esterne
│   ├── jackson-*.jar           # Librerie JSON
│   ├── javax.mail.jar          # Libreria email
│   └── postgresql.jar          # Driver PostgreSQL
├── 📁 src/                      # Codice sorgente
│   ├── 📁 main/java/          # Codice Java principale
│   │   ├── 📁 app/            # Classe principale
│   │   ├── 📁 controller/     # Controller MVC
│   │   ├── 📁 dao/            # Data Access Objects
│   │   ├── 📁 database/       # Gestione database
│   │   ├── 📁 gui/            # Interfaccia grafica
│   │   ├── 📁 model/          # Modelli dati
│   │   └── 📁 service/        # Servizi business
│   └── 📁 resources/          # Risorse (icone, configurazioni)
├── 📁 target/                   # File compilati (generati automaticamente)
├── pom.xml                      # Configurazione Maven
├── mvnw.cmd                     # Maven wrapper
├── compile.bat                 # Script di compilazione
└── README_DATABASE.md          # Documentazione database
```

## 🚀 Avvio Rapido

### 1. Configurazione Database
```bash
# Esegui il dump del database
psql -U postgres -f database/db_dump.sql
```

### 2. Configurazione Email
```bash
# Copia e configura il file email
cp src/main/resources/email.properties.example config/email.properties
# Modifica config/email.properties con le tue credenziali
```

### 3. Compilazione
```bash
# Usa lo script di compilazione
./compile.bat
```

### 4. Esecuzione
```bash
# Avvia l'applicazione
java -cp "target/classes;lib/*" app.Main
```

## 📖 Documentazione

- **Javadoc**: Apri `documentazione/javadoc/index.html` nel browser
- **Database**: Vedi `README_DATABASE.md` per dettagli sul database

## 🛠️ Tecnologie Utilizzate

- **Java 17**
- **Maven** per gestione dipendenze
- **PostgreSQL** database
- **Swing** per interfaccia grafica
- **Jackson** per serializzazione JSON
- **JavaMail** per invio email

## 📝 Note

- La cartella `target/` viene generata automaticamente durante la compilazione
- I file di configurazione sono nella cartella `config/`
- Le librerie esterne sono nella cartella `lib/`
- La documentazione Javadoc è in `documentazione/javadoc/`
