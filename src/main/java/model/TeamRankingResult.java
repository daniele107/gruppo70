package model;

/**
 * DTO that represents a team's ranking entry within an hackathon.
 * Contains aggregated score data computed from judges' evaluations.
 */
public class TeamRankingResult {
    private final int teamId;
    private final double averageScore;
    private final int votesCount;

    public TeamRankingResult(int teamId, double averageScore, int votesCount) {
        this.teamId = teamId;
        this.averageScore = averageScore;
        this.votesCount = votesCount;
    }

    public int getTeamId() {
        return teamId;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public int getVotesCount() {
        return votesCount;
    }
}


