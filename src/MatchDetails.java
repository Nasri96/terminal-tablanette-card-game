import java.util.UUID;

public class MatchDetails {
    private final UUID id;
    private final String p1Name;
    private final String p2Name;
    private final int p1Score;
    private final int p2Score;


    public MatchDetails(String p1Name, String p2Name, int p1Score, int p2Score) {
        this.id = UUID.randomUUID();
        this.p1Name = p1Name;
        this.p2Name = p2Name;
        this.p1Score = p1Score;
        this.p2Score = p2Score;
    }

    private String getWinner() {
        String winner = (p1Score > p2Score) ? p1Name : p2Name;
        
        return winner;
    }

    public String toString() {
        return  """
                matchId: %s,
                Player: %s, score: %d,
                Player: %s, score: %d,
                Winner: %s
                """.formatted(this.id.toString(), this.p1Name, this.p1Score, this.p2Name, this.p2Score, getWinner());
    }
}
