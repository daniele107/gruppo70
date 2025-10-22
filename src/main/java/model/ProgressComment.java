package model;

import java.time.LocalDateTime;

/**
 * Commento di un giudice su un documento/progresso di un team.
 */
public class ProgressComment {
    private int id;
    private int documentId;
    private int judgeId;
    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProgressComment() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public ProgressComment(int documentId, int judgeId, String text) {
        this();
        this.documentId = documentId;
        this.judgeId = judgeId;
        this.text = text;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getDocumentId() { return documentId; }
    public void setDocumentId(int documentId) { this.documentId = documentId; }
    public int getJudgeId() { return judgeId; }
    public void setJudgeId(int judgeId) { this.judgeId = judgeId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}


