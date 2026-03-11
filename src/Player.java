import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String id;
    private final String name;
    private final List<Card> cardsWon;
    private final List<Card> lastCardsWon;
    private final List<Card> currentHand;
    private final int pointsWon;
    private final int tablePoints;
    private final boolean cpu;

    public Player(String id, String name, List<Card> cardsWon, List<Card> lastCardsWon, List<Card> currentHand, int pointsWon, int tablePointsWon, boolean cpu) {
        this.id = id;
        this.name = name;
        this.cardsWon = List.copyOf(cardsWon);
        this.lastCardsWon = List.copyOf(lastCardsWon);
        this.currentHand = List.copyOf(currentHand);
        this.pointsWon = pointsWon;
        this.tablePoints = tablePointsWon;
        this.cpu = cpu;
    }

    public static Player initial(String id, String name, boolean isCpu) {
        return new Player(id, name, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, 0, isCpu);
    }

    public Player withCurrentHand(List<Card> updatedCurrentHand) {
        return new Player(
            this.id,
            this.name,
            this.cardsWon,
            this.lastCardsWon,
            updatedCurrentHand,
            this.pointsWon,
            this.tablePoints,
            this.cpu
        );
    }

    public Player withLastCardsWon(List<Card> updatedLastCardsWon) {
        return new Player(
            this.id,
            this.name,
            this.cardsWon,
            updatedLastCardsWon,
            this.currentHand,
            this.pointsWon,
            this.tablePoints,
            this.cpu
        );
    }

    public Player withCardsWon(List<Card> updatedCardsWon) {
        return new Player(
            this.id,
            this.name,
            updatedCardsWon,
            this.lastCardsWon,
            this.currentHand,
            this.pointsWon,
            this.tablePoints,
            this.cpu
        );
    }

    public Player withPoints(int updatedPoints) {
        return new Player(
            this.id,
            this.name,
            this.cardsWon,
            this.lastCardsWon,
            this.currentHand,
            updatedPoints,
            this.tablePoints,
            this.cpu
        );
    }

    public Player withTablePoint(int updatedTablePoints) {
        return new Player(
            this.id,
            this.name,
            this.cardsWon,
            this.lastCardsWon,
            this.currentHand,
            this.pointsWon,
            updatedTablePoints,
            this.cpu
        );
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<Card> getCardsWon() {
        return this.cardsWon;
    }

    public int getCardsWonSize() {
        return this.cardsWon.size();
    }

    public List<Card> getLastCardsWon() {
        return this.lastCardsWon;
    }

    public List<Card> getCurrentHand() {
        return this.currentHand;
    }

    public int getPointsWon() {
        return this.pointsWon;
    }

    public int getTablePoints() {
        return this.tablePoints;
    }

    public boolean isCpu() {
        return this.cpu;
    }
    
    public String toString() {
        String output = 
                """
                id: %s
                name: %s,
                cardsWon: %s
                lastCardsWon: %s,
                currentHand: %s,
                pointsWon: %d,
                tablePoints: %d
                """.formatted(id, name, cardsWon, lastCardsWon, currentHand, pointsWon, tablePoints);
        return output;
    }

    public void printCurrentHand() {
        System.out.println(this.name + " HAND CARDS: ");
        for(Card card: currentHand) {
            System.out.println(card);
        }
    }

    public void printWonCards() {
        System.out.println(this.name + " WON CARDS: " + lastCardsWon);
    }

}
