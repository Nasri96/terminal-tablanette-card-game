import java.util.List;

public record ResultPlayCard(List<Player> updatedPlayers, List<List<Card>> allCombinations, List<Card> updatedCurrentTable) {};