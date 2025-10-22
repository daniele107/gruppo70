package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.*;
import model.RankingSnapshot;
import model.Team;
import model.Valutazione;
import model.Documento;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servizio per calcolare anteprima classifica e pubblicare snapshot con tie-breakers.
 */
public class RankingService {
    private final TeamDAO teamDAO;
    private final ValutazioneDAO valutazioneDAO;
    private final RegistrazioneDAO registrazioneDAO;
    private final DocumentoDAO documentoDAO;
    private final RankingSnapshotDAO snapshotDAO;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RankingService(TeamDAO teamDAO, ValutazioneDAO valutazioneDAO, RegistrazioneDAO registrazioneDAO,
                          DocumentoDAO documentoDAO, RankingSnapshotDAO snapshotDAO) {
        this.teamDAO = teamDAO;
        this.valutazioneDAO = valutazioneDAO;
        this.registrazioneDAO = registrazioneDAO;
        this.documentoDAO = documentoDAO;
        this.snapshotDAO = snapshotDAO;
    }

    public Preview computePreview(int hackathonId, boolean allowMissingVotes) {
        List<Team> teams = teamDAO.findByHackathon(hackathonId);
        List<Valutazione> allVotes = valutazioneDAO.findByHackathon(hackathonId);
        int judgeCount = registrazioneDAO.findGiudici(hackathonId).size();

        List<Entry> entries = new ArrayList<>();
        boolean missingVotes = false;

        Map<Integer, List<Valutazione>> votesByTeam = allVotes.stream().collect(Collectors.groupingBy(Valutazione::getTeamId));

        for (Team team : teams) {
            List<Valutazione> votes = votesByTeam.getOrDefault(team.getId(), Collections.emptyList());
            double avg = votes.stream().mapToInt(Valutazione::getVoto).average().orElse(0.0);
            double std = stdDev(votes.stream().map(Valutazione::getVoto).mapToDouble(Integer::doubleValue).toArray(), avg);
            LocalDateTime earliest = earliestSubmission(hackathonId, team.getId());
            boolean teamMissing = votes.size() < judgeCount;
            missingVotes = missingVotes || teamMissing;
            entries.add(new Entry(team.getId(), team.getNome(), avg, std, votes.size(), earliest, teamMissing));
        }

        // Ordinamento: avg DESC, std ASC, earliest ASC, teamName ASC
        entries.sort(Comparator
            .comparingDouble(Entry::average).reversed()
            .thenComparingDouble(Entry::stdDev)
            .thenComparing(Entry::earliestSubmission, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Entry::teamName, String.CASE_INSENSITIVE_ORDER)
        );

        for (int i = 0; i < entries.size(); i++) entries.get(i).setPosition(i + 1);

        if (!allowMissingVotes && missingVotes) {
            return new Preview(entries, true);
        }
        return new Preview(entries, false);
    }

    public PublishResult publish(int hackathonId, boolean allowMissingVotes, int organizerUserId, String motivation) {
        // Calcola anteprima
        Preview preview = computePreview(hackathonId, allowMissingVotes);
        if (preview.missingVotes && !allowMissingVotes) {
            return PublishResult.error("MISSING_VOTES", "Mancano voti: pubblicazione bloccata");
        }

        try {
            int nextVersion = snapshotDAO.findMaxVersion(hackathonId) + 1;
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("hackathonId", hackathonId);
            payload.put("version", nextVersion);
            payload.put("generatedAt", LocalDateTime.now().toString());
            payload.put("missingVotes", preview.missingVotes);
            if (motivation != null) payload.put("motivation", motivation);
            payload.put("entries", preview.entries.stream().map(Entry::toMap).collect(Collectors.toList()));
            String json = objectMapper.writeValueAsString(payload);
            RankingSnapshot snapshot = new RankingSnapshot(hackathonId, nextVersion, json);
            int id = snapshotDAO.insert(snapshot);
            return PublishResult.ok(id, nextVersion);
        } catch (Exception e) {
            return PublishResult.error("INTERNAL_ERROR", e.getMessage());
        }
    }

    private double stdDev(double[] arr, double avg) {
        if (arr.length == 0) return Double.MAX_VALUE; // penalizza l'assenza voti nei tie-breakers
        double sum = 0.0;
        for (double v : arr) { double d = v - avg; sum += d * d; }
        return Math.sqrt(sum / arr.length);
    }

    private LocalDateTime earliestSubmission(int hackathonId, int teamId) {
        // Ricava earliest upload tra i documenti del team nell'hackathon
        List<Documento> docs = documentoDAO.findByTeam(teamId);
        return docs.stream()
            .filter(d -> d.getHackathonId() == hackathonId)
            .map(Documento::getDataCaricamento)
            .filter(Objects::nonNull)
            .min(LocalDateTime::compareTo)
            .orElse(null);
    }

    // DTOs
    public static final class Preview {
        public final List<Entry> entries;
        public final boolean missingVotes;
        public Preview(List<Entry> entries, boolean missingVotes) { this.entries = entries; this.missingVotes = missingVotes; }
    }

    public static final class PublishResult {
        public final boolean success; public final String code; public final String message; public final Integer snapshotId; public final Integer version;
        private PublishResult(boolean success, String code, String message, Integer id, Integer version) { this.success = success; this.code = code; this.message = message; this.snapshotId = id; this.version = version; }
        public static PublishResult ok(int id, int version) { return new PublishResult(true, null, null, id, version); }
        public static PublishResult error(String code, String message) { return new PublishResult(false, code, message, null, null); }
    }

    public static final class Entry {
        private int position;
        private final int teamId;
        private final String teamName;
        private final double average;
        private final double stdDev;
        private final int votes;
        private final LocalDateTime earliestSubmission;
        private final boolean missingVotes;
        public Entry(int teamId, String teamName, double average, double stdDev, int votes, LocalDateTime earliestSubmission, boolean missingVotes) {
            this.teamId = teamId; this.teamName = teamName; this.average = average; this.stdDev = stdDev; this.votes = votes; this.earliestSubmission = earliestSubmission; this.missingVotes = missingVotes;
        }
        public void setPosition(int p) { this.position = p; }
        public int position() { return position; }
        public int teamId() { return teamId; }
        public String teamName() { return teamName; }
        public double average() { return average; }
        public double stdDev() { return stdDev; }
        public int votes() { return votes; }
        public LocalDateTime earliestSubmission() { return earliestSubmission; }
        public boolean missingVotes() { return missingVotes; }
        public Map<String, Object> toMap() {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("position", position);
            m.put("teamId", teamId);
            m.put("teamName", teamName);
            m.put("average", average);
            m.put("stdDev", stdDev);
            m.put("votes", votes);
            m.put("earliestSubmission", earliestSubmission != null ? earliestSubmission.toString() : null);
            m.put("missingVotes", missingVotes);
            return m;
        }
    }
}



