package service;

import dao.*;
import model.AuditLog;
import model.Documento;
import model.ProgressComment;
import model.Registrazione;
import database.DataAccessException;

import java.util.List;
import java.util.Objects;

/**
 * Servizio per la gestione dei commenti dei giudici sui documenti dei progressi.
 * Applica ACL, rate limit e sanitizzazione input.
 */
public class DocumentReviewService {
    private static final int MAX_COMMENTS_PER_HOUR = 30;

    private final DocumentoDAO documentoDAO;
    private final ProgressCommentDAO commentDAO;
    private final RegistrazioneDAO registrazioneDAO;
    private final AuditLogDAO auditLogDAO;

    public enum ErrorCode { FORBIDDEN, VALIDATION_ERROR, MISSING_RESOURCE, RATE_LIMIT }

    public DocumentReviewService(DocumentoDAO documentoDAO, ProgressCommentDAO commentDAO,
                                 RegistrazioneDAO registrazioneDAO, AuditLogDAO auditLogDAO) {
        this.documentoDAO = documentoDAO;
        this.commentDAO = commentDAO;
        this.registrazioneDAO = registrazioneDAO;
        this.auditLogDAO = auditLogDAO;
    }

    // Overload matching tests order (commentDAO, documentoDAO, registrazioneDAO, auditLogDAO)
    public DocumentReviewService(ProgressCommentDAO commentDAO, DocumentoDAO documentoDAO,
                                 RegistrazioneDAO registrazioneDAO, AuditLogDAO auditLogDAO) {
        this(documentoDAO, commentDAO, registrazioneDAO, auditLogDAO);
    }

    public List<ProgressComment> listComments(int documentId, int currentUserId, boolean isJudge) {
        ensureDocumentExists(documentId);
        return commentDAO.findByDocument(documentId);
    }

    /**
     * Ritorna il numero di commenti associati a un documento.
     */
    public int countComments(int documentId) {
        ensureDocumentExists(documentId);
        return commentDAO.countByDocument(documentId);
    }

    public Result<Boolean> addComment(int documentId, int currentUserId, boolean isJudge, String rawText) {
        Documento doc = ensureDocumentExists(documentId);
        // ACL: solo giudici assegnati (registrati confermati) all'hackathon
        if (!isJudge || !isUserJudgeOfHackathon(currentUserId, doc.getHackathonId())) {
            logAudit(currentUserId, AuditLog.AuditAction.ACCESS_DENIED, "DOCUMENT", documentId,
                "Tentativo commento non autorizzato");
            return Result.error(ErrorCode.FORBIDDEN, "Solo i giudici possono commentare");
        }
        // Rate limit
        int commentsLastHour = commentDAO.countByJudgeAndTeamInLastHour(currentUserId, doc.getTeamId());
        if (commentsLastHour >= MAX_COMMENTS_PER_HOUR) {
            return Result.error(ErrorCode.RATE_LIMIT, "Limite commenti/ora raggiunto");
        }
        // Validazione + sanitizzazione
        String text = sanitize(rawText);
        if (text == null || text.length() < 5 || text.length() > 2000) {
            return Result.error(ErrorCode.VALIDATION_ERROR, "Lunghezza commento 5-2000 caratteri");
        }
        ProgressComment c = new ProgressComment(documentId, currentUserId, text);
        boolean ok = commentDAO.insert(c);
        if (ok) {
            logAudit(currentUserId, AuditLog.AuditAction.SEND_NOTIFICATION, "DOCUMENT", documentId,
                "Nuovo commento giudice");
        }
        return Result.ok(ok);
    }

    // Overload used by tests: throws exceptions on ACL/rate-limit, returns boolean on success
    public boolean addComment(int documentId, int judgeUserId, String rawText) {
        Documento doc = ensureDocumentExists(documentId);
        // ACL
        if (!isUserJudgeOfHackathon(judgeUserId, doc.getHackathonId())) {
            throw new DataAccessException("Utente non autorizzato a commentare");
        }
        // Rate limit
        int commentsLastHour = commentDAO.countByJudgeAndTeamInLastHour(judgeUserId, doc.getTeamId());
        if (commentsLastHour >= MAX_COMMENTS_PER_HOUR) {
            throw new DataAccessException("Rate limit superato");
        }
        // Validazione + sanitizzazione
        String text = sanitize(rawText);
        if (text == null || text.length() < 5 || text.length() > 2000) {
            throw new IllegalArgumentException("Lunghezza commento 5-2000 caratteri");
        }
        ProgressComment c = new ProgressComment(documentId, judgeUserId, text);
        boolean ok = commentDAO.insert(c);
        if (ok) {
            logAudit(judgeUserId, AuditLog.AuditAction.SEND_NOTIFICATION, "DOCUMENT", documentId,
                "Nuovo commento giudice");
        }
        return ok;
    }

    public Result<Boolean> updateCommentById(int commentId, int currentUserId, String rawText) {
        ProgressComment existing = commentDAO.findById(commentId);
        if (existing == null) return Result.error(ErrorCode.MISSING_RESOURCE, "Commento non trovato");
        if (existing.getJudgeId() != currentUserId) {
            logAudit(currentUserId, AuditLog.AuditAction.ACCESS_DENIED, "COMMENT", commentId,
                "Tentativo modifica commento altrui");
            return Result.error(ErrorCode.FORBIDDEN, "Puoi modificare solo i tuoi commenti");
        }
        String text = sanitize(rawText);
        if (text == null || text.length() < 5 || text.length() > 2000) {
            return Result.error(ErrorCode.VALIDATION_ERROR, "Lunghezza commento 5-2000 caratteri");
        }
        existing.setText(text);
        boolean ok = commentDAO.update(existing);
        return Result.ok(ok);
    }

    public Result<Boolean> deleteComment(int commentId, int currentUserId) {
        ProgressComment existing = commentDAO.findById(commentId);
        if (existing == null) return Result.error(ErrorCode.MISSING_RESOURCE, "Commento non trovato");
        if (existing.getJudgeId() != currentUserId) {
            logAudit(currentUserId, AuditLog.AuditAction.ACCESS_DENIED, "COMMENT", commentId,
                "Tentativo cancellazione commento altrui");
            return Result.error(ErrorCode.FORBIDDEN, "Puoi eliminare solo i tuoi commenti");
        }
        boolean ok = commentDAO.delete(commentId);
        return Result.ok(ok);
    }

    // Overload used by tests
    public boolean updateComment(int documentId, int judgeUserId, String newText) {
        ProgressComment existing = commentDAO.findByDocumentAndJudge(documentId, judgeUserId);
        if (existing == null) return false;
        String text = sanitize(newText);
        if (text == null || text.length() < 5 || text.length() > 2000) {
            throw new IllegalArgumentException("Lunghezza commento 5-2000 caratteri");
        }
        existing.setText(text);
        existing.setUpdatedAt(java.time.LocalDateTime.now());
        boolean ok = commentDAO.update(existing);
        if (ok) {
            logAudit(judgeUserId, AuditLog.AuditAction.UPDATE_EVALUATION, "COMMENT", existing.getId(), "Aggiornato commento");
        }
        return ok;
    }

    // Overload used by tests
    public boolean removeComment(int documentId, int judgeUserId) {
        ProgressComment existing = commentDAO.findByDocumentAndJudge(documentId, judgeUserId);
        if (existing == null) return false;
        boolean ok = commentDAO.delete(existing.getId());
        if (ok) {
            logAudit(judgeUserId, AuditLog.AuditAction.DELETE_EVALUATION, "COMMENT", existing.getId(), "Rimosso commento");
        }
        return ok;
    }

    // Used in tests to cascade delete comments when a document is removed
    public void handleDocumentDeletion(int documentId) {
        List<ProgressComment> list = commentDAO.findByDocument(documentId);
        for (ProgressComment c : list) {
            if (commentDAO.delete(c.getId())) {
                logAudit(c.getJudgeId(), AuditLog.AuditAction.DELETE_EVALUATION, "COMMENT", c.getId(), "Cancellazione per rimozione documento");
            }
        }
    }

    private Documento ensureDocumentExists(int documentId) {
        Documento d = documentoDAO.findById(documentId);
        if (d == null) throw new IllegalArgumentException("Documento non trovato");
        return d;
    }

    private boolean isUserJudgeOfHackathon(int userId, int hackathonId) {
        Registrazione r = registrazioneDAO.findByUtenteAndHackathon(userId, hackathonId);
        return r != null && r.getRuolo() == Registrazione.Ruolo.GIUDICE;
    }

    private String sanitize(String raw) {
        if (raw == null) return null;
        String s = raw;
        // Rimuove blocchi <script> e il loro contenuto
        s = s.replaceAll("(?is)<script[^>]*>.*?</script>", "");
        // Rimuove tutti i tag HTML rimanenti
        s = s.replaceAll("(?is)<[^>]+>", "");
        // Trim e normalizza spazi
        s = s.trim().replaceAll("\\s+", " ");
        return s;
    }

    private void logAudit(int userId, AuditLog.AuditAction action, String resource, int resourceId, String details) {
        try {
            AuditLog log = new AuditLog.Builder(userId, action, resource)
                .risorsaId(resourceId)
                .dettagli(details)
                .build();
            auditLogDAO.insert(log);
        } catch (Exception ignored) {
            // non blocca il flusso applicativo
        }
    }

    public static final class Result<T> {
        public final boolean success;
        public final T value;
        public final ErrorCode error;
        public final String message;

        private Result(boolean success, T value, ErrorCode error, String message) {
            this.success = success; this.value = value; this.error = error; this.message = message;
        }
        public static <T> Result<T> ok(T value) { return new Result<>(true, value, null, null); }
        public static <T> Result<T> error(ErrorCode e, String msg) { return new Result<>(false, null, e, msg); }
        @Override public String toString() {
            return success ? ("OK:" + Objects.toString(value)) : ("ERR:" + error + " " + message);
        }
    }
}


