import java.util.ArrayList;

public class Player {
    private String type;
    private ArrayList<Card> cardsWon;
    private ArrayList<Card> lastCardsWon;
    private ArrayList<Card> currentHand;
    private int pointsWon;
    private int tablePoints;

    public Player(String type) {
        this.type = type;
        this.cardsWon = new ArrayList<Card>();
        this.lastCardsWon = new ArrayList<Card>();
        this.currentHand = new ArrayList<Card>();
        this.pointsWon = 0;
        this.tablePoints = 0;
    }

    public String getType() {
        return this.type;
    }

    public ArrayList<Card> getCurrentHand() {
        return this.currentHand;
    }

    public ArrayList<Card> getCardsWon() {
        return this.cardsWon;
    }

    public int getCardsWonSize() {
        return this.cardsWon.size();
    }

    public ArrayList<Card> getLastCardsWon() {
        return this.lastCardsWon;
    }

    public int getPointsWon() {
        return this.pointsWon;
    }

    public void setPoints(int points) {
        this.pointsWon+= points;
    }

    public void addTablePoint() {
        this.tablePoints++;
    }

    public int getTablePoints() {
        return this.tablePoints;
    }

    public void addCardToCurrentHand(Card card) {
        this.currentHand.add(card);
    }

    public void printCurrentHand() {
        System.out.println(this.type + " HAND CARDS: ");
        for(Card card: currentHand) {
            System.out.println(card);
        }
    }

    public void printWonCards() {
        System.out.println(this.type + " WON CARDS: " + lastCardsWon);
    }

    public void playCard(Card card) {
        this.currentHand.remove(card);
    }

    public void winCards(ArrayList<Card> wonCards) {
        this.cardsWon.addAll(wonCards);

        this.lastCardsWon.addAll(wonCards);
    }

    public void clearLastWonCards() {
        this.lastCardsWon.clear();
    }

    public void clearWonCards() {
        this.cardsWon.clear();
    }
}
