import java.util.List;

public record ResultStart(CardDeck deck, List<Player> players, List<Card> currentTable) {}