package model;

import database.ConnectionManager;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.zip.*;
import java.util.zip.Deflater;

/**
 * Servizio per il backup fisico del database e dei file.
 * Supporta backup PostgreSQL con pg_dump e backup file system con compressione ZIP.
 */
public class BackupService {
    
    private static final Logger LOGGER = Logger.getLogger(BackupService.class.getName());
    private ConnectionManager connectionManager;
    private ExecutorService executorService;
    private String backupDirectory;
    private String pgDumpPath;
    private boolean isConfigured = false;
    
    // Configurazione di default
    private static final String DEFAULT_BACKUP_DIR = "backups";
    private static final String DEFAULT_PG_DUMP_PATH = "pg_dump"; // Assume che sia nel PATH
    private static final String TIMESTAMP_PATTERN = "yyyyMMdd_HHmmss";
    
    public BackupService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.executorService = Executors.newFixedThreadPool(2);
        this.backupDirectory = DEFAULT_BACKUP_DIR;
        this.pgDumpPath = DEFAULT_PG_DUMP_PATH;
        
        // Crea la directory di backup se non esiste
        createBackupDirectory();
    }
    
    /**
     * Configura il servizio di backup
     */
    public boolean configure(String backupDir, String pgDumpPath) {
        try {
            this.backupDirectory = backupDir != null ? backupDir : DEFAULT_BACKUP_DIR;
            this.pgDumpPath = pgDumpPath != null ? pgDumpPath : DEFAULT_PG_DUMP_PATH;
            
            createBackupDirectory();
            
            // Verifica che pg_dump sia disponibile
            if (testPgDumpAvailability()) {
                this.isConfigured = true;
                LOGGER.info("Servizio backup configurato correttamente");
                return true;
            } else {
                LOGGER.warning("pg_dump non disponibile, backup database non funzionale");
                return false;
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nella configurazione del servizio backup", e);
            return false;
        }
    }
    
    /**
     * Crea la directory di backup
     */
    private void createBackupDirectory() {
        try {
            Path backupPath = Paths.get(backupDirectory);
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
                LOGGER.log(Level.INFO, "Directory backup creata: {0}", backupPath.toAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Impossibile creare directory backup", e);
        }
    }
    
    /**
     * Testa la disponibilità di pg_dump
     */
    private boolean testPgDumpAvailability() {
        try {
            ProcessBuilder pb = new ProcessBuilder(pgDumpPath, "--version");
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                // Leggi la versione per il log
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String version = reader.readLine();
                    LOGGER.log(Level.INFO, "pg_dump disponibile: {0}", version);
                }
                return true;
            }
            
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "Test pg_dump interrotto: {0}", ie.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "pg_dump non disponibile: {0}", e.getMessage());
        }
        return false;
    }
    
    /**
     * Esegue backup del database PostgreSQL
     */
    public CompletableFuture<BackupResult> backupDatabase() {
        return CompletableFuture.supplyAsync(() -> {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN));
            String backupFileName = "hackathon_db_backup_" + timestamp + ".sql";
            Path backupFilePath = Paths.get(backupDirectory, backupFileName);
            
            try {
                // Ottieni parametri di connessione dal ConnectionManager
                String dbUrl = connectionManager.getDatabaseUrl();
                String[] urlParts = parseDbUrl(dbUrl);
                
                if (urlParts.length == 0) {
                    return new BackupResult(false, "Impossibile parsare URL database", backupFileName, 0);
                }
                
                String host = urlParts[0];
                String port = urlParts[1];
                String dbName = urlParts[2];
                String username = connectionManager.getUsername();
                String password = connectionManager.getPassword();
                
                // Costruisci comando pg_dump
                ProcessBuilder pb = new ProcessBuilder(
                    pgDumpPath,
                    "--host=" + host,
                    "--port=" + port,
                    "--username=" + username,
                    "--dbname=" + dbName,
                    "--no-password",
                    "--verbose",
                    "--clean",
                    "--if-exists",
                    "--create",
                    "--file=" + backupFilePath.toAbsolutePath().toString()
                );
                
                // Imposta password tramite variabile d'ambiente
                pb.environment().put("PGPASSWORD", password);
                
                LOGGER.log(Level.INFO, "Avvio backup database: {0}", backupFileName);
                
                return executePgDumpProcess(pb, backupFilePath, backupFileName);
                
            } catch (Exception e) {
                String errorMsg = String.format("Errore durante backup database: %s", e.getMessage());
                LOGGER.log(Level.SEVERE, errorMsg, e);
                return new BackupResult(false, errorMsg, backupFileName, 0);
            }
            
        }, executorService);
    }
    
    /**
     * Esegue il processo pg_dump e gestisce l'output
     */
    private BackupResult executePgDumpProcess(ProcessBuilder pb, Path backupFilePath, String backupFileName) {
        try {
            Process process = pb.start();
            
            // Cattura output per logging
            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();
            
            Thread outputReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Errore lettura output pg_dump", e);
                }
            });
            
            Thread errorReader = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        error.append(line).append("\n");
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Errore lettura error pg_dump", e);
                }
            });
            
            outputReader.start();
            errorReader.start();
            
            int exitCode = process.waitFor();
            try {
                outputReader.join();
                errorReader.join();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new BackupInterruptedException("Backup interrotto", ie);
            }
            
            if (exitCode == 0 && Files.exists(backupFilePath)) {
                long fileSize = Files.size(backupFilePath);
                LOGGER.log(Level.INFO, "Backup database completato: {0} ({1} bytes)", new Object[]{backupFileName, fileSize});
                return new BackupResult(true, "Backup completato con successo", backupFileName, fileSize);
            } else {
                String errorMsg = String.format("Backup fallito (exit code: %d): %s", exitCode, error.toString());
                LOGGER.severe(errorMsg);
                return new BackupResult(false, errorMsg, backupFileName, 0);
            }
        } catch (Exception e) {
            String errorMsg = String.format("Errore durante esecuzione pg_dump: %s", e.getMessage());
            LOGGER.log(Level.SEVERE, errorMsg, e);
            return new BackupResult(false, errorMsg, backupFileName, 0);
        }
    }
    
    /**
     * Esegue backup dei file
     */
    public CompletableFuture<BackupResult> backupFiles(String storageDirectory) {
        return CompletableFuture.supplyAsync(() -> {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN));
            String backupFileName = "hackathon_files_backup_" + timestamp + ".zip";
            Path backupFilePath = Paths.get(backupDirectory, backupFileName);
            
            try {
                Path storagePath = Paths.get(storageDirectory);
                
                if (!Files.exists(storagePath)) {
                    return new BackupResult(false, "Directory storage non trovata: " + storageDirectory, backupFileName, 0);
                }
                
                LOGGER.log(Level.INFO, "Avvio backup file: {0}", backupFileName);
                
                try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(backupFilePath.toFile()))) {
                    zipOut.setLevel(Deflater.BEST_COMPRESSION);
                    
                    Files.walk(storagePath)
                        .filter(Files::isRegularFile)
                        .forEach(file -> {
                            try {
                                String entryName = storagePath.relativize(file).toString().replace("\\", "/");
                                ZipEntry zipEntry = new ZipEntry(entryName);
                                zipOut.putNextEntry(zipEntry);
                                
                                Files.copy(file, zipOut);
                                zipOut.closeEntry();
                                
                            } catch (IOException e) {
                                LOGGER.log(Level.WARNING, "Errore nel backup del file: {0}", file);
                            }
                        });
                }
                
                if (Files.exists(backupFilePath)) {
                    long fileSize = Files.size(backupFilePath);
                    LOGGER.log(Level.INFO, "Backup file completato: {0} ({1} bytes)", new Object[]{backupFileName, fileSize});
                    return new BackupResult(true, "Backup file completato con successo", backupFileName, fileSize);
                } else {
                    return new BackupResult(false, "File backup non creato", backupFileName, 0);
                }
                
            } catch (Exception e) {
                String errorMsg = String.format("Errore durante backup file: %s", e.getMessage());
                LOGGER.log(Level.SEVERE, errorMsg, e);
                return new BackupResult(false, errorMsg, backupFileName, 0);
            }
            
        }, executorService);
    }
    
    /**
     * Esegue backup completo (database + file)
     */
    public CompletableFuture<BackupResult> backupComplete(String storageDirectory) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Esegui backup database
                BackupResult dbResult = backupDatabase().get();
                
                // Esegui backup file
                BackupResult fileResult = backupFiles(storageDirectory).get();
                
                boolean success = dbResult.isSuccess() && fileResult.isSuccess();
                String message = String.format("Database: %s, File: %s", dbResult.getMessage(), fileResult.getMessage());
                long totalSize = dbResult.getFileSize() + fileResult.getFileSize();
                
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN));
                String completeName = "backup_completo_" + timestamp;
                
                return new BackupResult(success, message, completeName, totalSize);
                
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                String errorMsg = "Backup completo interrotto";
                LOGGER.log(Level.WARNING, errorMsg, ie);
                return new BackupResult(false, errorMsg, "backup_completo_error", 0);
            } catch (Exception e) {
                String errorMsg = String.format("Errore durante backup completo: %s", e.getMessage());
                LOGGER.log(Level.SEVERE, errorMsg, e);
                return new BackupResult(false, errorMsg, "backup_completo_error", 0);
            }
        }, executorService);
    }
    
    /**
     * Pulisce backup vecchi
     */
    public int cleanOldBackups(int retentionDays) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
            Path backupPath = Paths.get(backupDirectory);
            
            if (!Files.exists(backupPath)) {
                return 0;
            }
            
            int deletedCount = 0;
            
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(backupPath, "*.{sql,zip}")) {
                for (Path file : stream) {
                    deletedCount += deleteOldBackupFile(file, cutoffDate);
                }
            }
            
            return deletedCount;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nella pulizia backup", e);
            return 0;
        }
    }
    
    /**
     * Elimina un singolo file di backup se è più vecchio della data di cutoff
     */
    private int deleteOldBackupFile(Path file, LocalDateTime cutoffDate) {
        try {
            LocalDateTime fileDate = LocalDateTime.ofInstant(
                Files.getLastModifiedTime(file).toInstant(),
                java.time.ZoneId.systemDefault()
            );
            
            if (fileDate.isBefore(cutoffDate)) {
                Files.delete(file);
                LOGGER.log(Level.INFO, "Backup eliminato: {0}", file.getFileName());
                return 1;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Errore nell''eliminazione backup: {0}", file);
        }
        return 0;
    }
    
    /**
     * Ottiene statistiche backup
     */
    public String getBackupStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== STATISTICHE BACKUP ===%n");
        
        try {
            Path backupPath = Paths.get(backupDirectory);
            stats.append(String.format("Directory: %s%n", backupPath.toAbsolutePath()));
            stats.append(String.format("pg_dump configurato: %s%n", isConfigured ? "✅ Sì" : "❌ No"));
            
            if (Files.exists(backupPath)) {
                BackupStats backupStats = calculateBackupStats(backupPath);
                stats.append(String.format("File backup: %d%n", backupStats.fileCount));
                stats.append(String.format("Spazio utilizzato: %s%n", formatBytes(backupStats.totalSize)));
            } else {
                stats.append("Directory backup non esistente%n");
            }
            
        } catch (Exception e) {
            stats.append(String.format("Errore nel calcolo statistiche: %s%n", e.getMessage()));
        }
        
        return stats.toString();
    }
    
    /**
     * Calcola le statistiche dei file di backup
     */
    private BackupStats calculateBackupStats(Path backupPath) {
        long totalSize = 0;
        int fileCount = 0;
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(backupPath, "*.{sql,zip}")) {
            for (Path file : stream) {
                totalSize += Files.size(file);
                fileCount++;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Errore nel calcolo statistiche backup", e);
        }
        
        return new BackupStats(fileCount, totalSize);
    }
    
    /**
     * Classe per le statistiche di backup
     */
    private static class BackupStats {
        final int fileCount;
        final long totalSize;
        
        BackupStats(int fileCount, long totalSize) {
            this.fileCount = fileCount;
            this.totalSize = totalSize;
        }
    }
    
    /**
     * Parsa URL database PostgreSQL
     */
    private String[] parseDbUrl(String url) {
        try {
            // jdbc:postgresql://localhost:5432/hackathon_db
            if (url.startsWith("jdbc:postgresql://")) {
                String remaining = url.substring("jdbc:postgresql://".length());
                String[] hostPort = remaining.split("/")[0].split(":");
                String host = hostPort[0];
                String port = hostPort.length > 1 ? hostPort[1] : "5432";
                String dbName = remaining.split("/")[1].split("\\?")[0]; // Rimuovi parametri query
                
                return new String[]{host, port, dbName};
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Errore nel parsing URL database", e);
        }
        return new String[0];
    }
    
    /**
     * Formatta bytes in formato leggibile
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.valueOf(Math.round(bytes / 102.4) / 10.0) + " KB";
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.valueOf(Math.round(bytes / (102.4 * 1024.0)) / 10.0) + " MB";
        } else {
            return String.valueOf(Math.round(bytes / (102.4 * 1024.0 * 1024.0)) / 10.0) + " GB";
        }
    }
    
    /**
     * Chiude il servizio
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            LOGGER.info("Servizio backup terminato");
        }
    }
    
    // Getters
    public boolean isConfigured() { return isConfigured; }
    public String getBackupDirectory() { return backupDirectory; }
    
    /**
     * Classe per i risultati del backup
     */
    public static class BackupResult {
        private final boolean success;
        private final String message;
        private final String fileName;
        private final long fileSize;
        
        public BackupResult(boolean success, String message, String fileName, long fileSize) {
            this.success = success;
            this.message = message;
            this.fileName = fileName;
            this.fileSize = fileSize;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getFileName() { return fileName; }
        public long getFileSize() { return fileSize; }
        
        @Override
        public String toString() {
            return "BackupResult{success=" + success + ", file='" + fileName + "', size=" + fileSize + ", message='" + message + "'}";
        }
    }
    
    /**
     * Eccezione dedicata per errori di backup
     */
    public static class BackupInterruptedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        
        public BackupInterruptedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
