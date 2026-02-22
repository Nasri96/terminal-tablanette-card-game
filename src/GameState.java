import java.util.ArrayList;
import java.util.HashMap;

public class GameState {
    private CardDeck deck;
    private int playerMoveIndex;
    private int roundsPlayed;
    private boolean roundChanged;
    private int winningScore;
    private Player lastWinnerInRound;
    private Player lastWinnerOfMoreCards;
    private Player lastWinnerOfTablePoint;
    private HashMap<String, Player> gameOverPlayers;
    private Player[] players;
    private ArrayList<Card> currentTable;
    private ArrayList<ArrayList<Card>> allCombinations;
    private GamePhase gamePhase;


    public GameState(Player[] players) {
        this.deck = new CardDeck();
        this.players = players;
        this.playerMoveIndex = 0;
        this.roundsPlayed = 0;
        this.roundChanged = false;
        this.winningScore = 8;
        this.lastWinnerInRound = null;
        this.lastWinnerOfMoreCards = null;
        this.lastWinnerOfTablePoint = null;
        this.gameOverPlayers = new HashMap<>();
        this.currentTable = new ArrayList<>();
        this.allCombinations = new ArrayList<>();
        this.gamePhase = GamePhase.GAME_SETUP;
    }

    public CardDeck getDeck() {
        return this.deck;
    }

    public Player[] getPlayers() {
        return this.players;
    }

    public int getPlayerMoveIndex() {
        return this.playerMoveIndex;
    }

    public void setPlayerMoveIndex(int newIndex) {
        this.playerMoveIndex = newIndex;
    }

    public Player getCurrentPlayerMove() {
        return this.players[this.playerMoveIndex];
    }

    public int getRoundsPlayed() {
        return this.roundsPlayed;
    }

    public void incrementRoundsPlayed() {
        this.roundsPlayed++;
    }

    public void setRoundsPlayed(int val) {
        this.roundsPlayed = val;
    }

    public int getWinningScore() {
        return this.winningScore;
    }

    public boolean getRoundChanged() {
        return this.roundChanged;
    }

    public void setRoundChanged(boolean changed) {
        this.roundChanged = changed;
    }

    public Player getLastWinnerInRound() {
        return this.lastWinnerInRound;
    }

    public Player getLastWinnerOfMoreCards() {
        return this.lastWinnerOfMoreCards;
    }

    public Player getLastWinnerOfTablePoint() {
        return this.lastWinnerOfTablePoint;
    }

    public void setLastWinnerInRound(Player player) {
        this.lastWinnerInRound = player;
    }

    public void setLastWinnerOfMoreCards(Player player) {
        this.lastWinnerOfMoreCards = player;
    }

    public void setLastWinnerOfTablePoint(Player player) {
        this.lastWinnerOfTablePoint = player;
    }

    public HashMap<String, Player> getGameOverPlayers() {
        return this.gameOverPlayers;
    }

    public void setGameOverPlayers(String gameWinnerLoser, Player player) {
        this.gameOverPlayers.put(gameWinnerLoser, player);
    }

    public ArrayList<Card> getCurrentTable() {
        return this.currentTable;
    }

    public void setCurrentTable(Card card) {
        this.currentTable = new ArrayList<>(this.currentTable);
        this.currentTable.add(card);
    }
    
    public ArrayList<ArrayList<Card>> getAllCombinations() {
        return this.allCombinations;
    }

    public void setAllCombinations(ArrayList<ArrayList<Card>> combinations) {
        this.allCombinations = combinations;
    }

    public GamePhase getGamePhase() {
        return this.gamePhase;
    }

    public void setGamePhase(GamePhase nextGamePhase) {
        this.gamePhase = nextGamePhase;
    }

}
