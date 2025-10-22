package dao;

import model.ProgressComment;
import java.util.List;

/**
 * DAO per i commenti dei giudici ai progressi/documenti.
 */
public interface ProgressCommentDAO {
    /**
     * Inserisce un commento. Restituisce true se l'inserimento ha avuto successo.
     * Nota: alcuni test fanno il mock restituendo boolean.
     */
    boolean insert(ProgressComment comment);

    boolean update(ProgressComment comment);

    /**
     * Elimina il commento per id.
     */
    boolean delete(int id);

    ProgressComment findById(int id);

    List<ProgressComment> findByDocument(int documentId);

    /**
     * Cerca il commento di un giudice per un determinato documento.
     */
    ProgressComment findByDocumentAndJudge(int documentId, int judgeId);

    int countByDocument(int documentId);

    int countByJudgeAndTeamInLastHour(int judgeId, int teamId);
}


