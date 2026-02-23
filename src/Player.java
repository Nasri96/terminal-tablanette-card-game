import java.util.ArrayList;

public class Player {
    private String name;
    private ArrayList<Card> cardsWon;
    private ArrayList<Card> lastCardsWon;
    private ArrayList<Card> currentHand;
    private int pointsWon;
    private int tablePoints;

    public Player(String name) {
        this.name = name;
        this.cardsWon = new ArrayList<>();
        this.lastCardsWon = new ArrayList<>();
        this.currentHand = new ArrayList<>();
        this.pointsWon = 0;
        this.tablePoints = 0;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Card> getCardsWon() {
        return this.cardsWon;
    }

    public int getCardsWonSize() {
        return this.cardsWon.size();
    }

    public void clearWonCards() {
        this.cardsWon.clear();
    }

    public ArrayList<Card> getLastCardsWon() {
        return this.lastCardsWon;
    }

    public void clearLastWonCards() {
        this.lastCardsWon.clear();
    }

    public ArrayList<Card> getCurrentHand() {
        return this.currentHand;
    }

    public void addCardToCurrentHand(Card card) {
        this.currentHand.add(card);
    }

    public int getPointsWon() {
        return this.pointsWon;
    }

    public int getTablePoints() {
        return this.tablePoints;
    }

    public void setPoints(int points) {
        this.pointsWon+= points;
    }

    public void addTablePoint() {
        this.tablePoints++;
    }

    public void playCard(Card card) {
        this.currentHand.remove(card);
    }

    public void winCards(ArrayList<Card> wonCards) {
        this.cardsWon.addAll(wonCards);

        this.lastCardsWon.addAll(wonCards);
    }

    public void reset() {
        this.cardsWon = new ArrayList<>();
        this.lastCardsWon = new ArrayList<>();
        this.currentHand = new ArrayList<>();
        this.pointsWon = 0;
        this.tablePoints = 0;
    }

    public String toString() {
        String output = 
                """
                name: %s,
                lastCardsWon: %s,
                currentHand: %s,
                pointsWon: %d,
                tablePoints: %d
                """.formatted(name, lastCardsWon, currentHand, pointsWon, tablePoints);
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

    // game actions by player
    public void actionStart(Game game) {
        game.updateGame(new GameInput(GameAction.START, null));
    }

    public void actionPlayCard(Game game, Object payload) {
        game.updateGame(new GameInput(GameAction.PLAY_CARD, payload));
    }

    public void actionPickCombination(Game game, Object payload) {
        game.updateGame(new GameInput(GameAction.PICK_COMBINATION, payload));
    }

    // AUTOMATIC ACTIONS
    public void actionResolveTurn(Game game) {
        game.updateGame(new GameInput(GameAction.CONTINUE, null));
    }

    public void actionRoundEnd(Game game) {
        game.updateGame(new GameInput(GameAction.CONTINUE, null));
    }

    public void actionRoundStart(Game game) {
        game.updateGame(new GameInput(GameAction.CONTINUE, null));
    }

    public void actionNextTurn(Game game) {
        game.updateGame(new GameInput(GameAction.CONTINUE, null));
    }

    public void actionDealCards(Game game) {
        game.updateGame(new GameInput(GameAction.CONTINUE, null));
    }

    public void actionGameOver(Game game) {
        game.updateGame(new GameInput(GameAction.CONTINUE, null));
    }

    public void actionGameEnd(Game game) {
        game.updateGame(new GameInput(GameAction.CONTINUE, null));
    }
}
