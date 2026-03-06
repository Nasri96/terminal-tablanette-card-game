import java.util.ArrayList;
import java.util.List;

public class MatchDetailsManager {
    private static List<MatchDetails> matches = new ArrayList<>();


    public static void addMatch(MatchDetails match) {
        matches.add(match);
    }

    public static MatchDetails getLastMatch() {
        return matches.get(matches.size() - 1);
    }
}