import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {
    private CardDeck deck;
    private int playerMoveIndex;
    private int roundsPlayed;
    private boolean roundChanged;
    private int winningScore;
    private Player lastWinnerInRound;
    private Player lastWinnerOfMoreCards;
    private Player lastWinnerOfTablePoint;
    private Map<String, Player> gameOverPlayers;
    private List<Player> playersList;
    private List<Card> currentTable;
    private List<List<Card>> allCombinations;
    private GamePhase gamePhase;


    public GameState(
        CardDeck deck, 
        List<Player> playersList,
        int playerMoveIndex, 
        int roundsPlayed, 
        boolean roundChanged, 
        int winningScore, 
        Player lastWinnerInRound,
        Player lastWinnerOfMoreCards,
        Player lastWinnerOfTablePoint,
        Map<String, Player> gameOverPlayers,
        List<Card> currentTable,
        List<List<Card>> allCombinations,
        GamePhase gamePhase
    ) {
        this.deck = deck;
        this.playersList = List.copyOf(playersList);
        this.playerMoveIndex = playerMoveIndex;
        this.roundsPlayed = roundsPlayed;
        this.roundChanged = roundChanged;
        this.winningScore = winningScore;
        this.lastWinnerInRound = lastWinnerInRound;
        this.lastWinnerOfMoreCards = lastWinnerOfMoreCards;
        this.lastWinnerOfTablePoint = lastWinnerOfTablePoint;
        this.gameOverPlayers = Map.copyOf(gameOverPlayers);
        this.currentTable = List.copyOf(currentTable);
        this.allCombinations = List.copyOf(allCombinations);
        this.gamePhase = gamePhase;
    }

    public static GameState initial(List<Player> playerList) {
        return new GameState(
            CardDeck.initial(), playerList, 0, 0, 
            false, 0, null, 
            null, null, 
            new HashMap<>(), new ArrayList<>(), new ArrayList<>(), GamePhase.GAME_SETUP);
    }

    public GameState withDeck(CardDeck updatedDeck) {
        return new GameState(
            updatedDeck, 
            this.playersList,
            this.playerMoveIndex, 
            this.roundsPlayed, 
            this.roundChanged, 
            this.winningScore, 
            this.lastWinnerInRound, 
            this.lastWinnerOfMoreCards, 
            this.lastWinnerOfTablePoint, 
            this.gameOverPlayers, 
            this.currentTable, 
            this.allCombinations, 
            this.gamePhase);
    }

    public GameState withPlayers(List<Player> updatedListPlayers) {
        return new GameState(
            this.deck, 
            updatedListPlayers,
            this.playerMoveIndex, 
            this.roundsPlayed, 
            this.roundChanged, 
            this.winningScore, 
            this.lastWinnerInRound, 
            this.lastWinnerOfMoreCards, 
            this.lastWinnerOfTablePoint, 
            this.gameOverPlayers, 
            this.currentTable, 
            this.allCombinations, 
            this.gamePhase);
    }

    public GameState withGamePhase(GamePhase nextPhase) {
        return new GameState(
            this.deck, 
            this.playersList,
            this.playerMoveIndex, 
            this.roundsPlayed, 
            this.roundChanged, 
            this.winningScore, 
            this.lastWinnerInRound, 
            this.lastWinnerOfMoreCards, 
            this.lastWinnerOfTablePoint, 
            this.gameOverPlayers, 
            this.currentTable, 
            this.allCombinations, 
            nextPhase);
    }

    public GameState withCurrentTable(List<Card> updatedCurrentTable) {
        return new GameState(
            this.deck,
            this.playersList,
            this.playerMoveIndex, 
            this.roundsPlayed, 
            this.roundChanged, 
            this.winningScore, 
            this.lastWinnerInRound, 
            this.lastWinnerOfMoreCards, 
            this.lastWinnerOfTablePoint, 
            this.gameOverPlayers, 
            updatedCurrentTable, 
            this.allCombinations, 
            this.gamePhase);
    }

    public GameState withAllCombinations(List<List<Card>> allCombinations) {
        return new GameState(
            this.deck, 
            this.playersList, 
            this.playerMoveIndex, 
            this.roundsPlayed, 
            this.roundChanged, 
            this.winningScore, 
            this.lastWinnerInRound, 
            this.lastWinnerOfMoreCards, 
            this.lastWinnerOfTablePoint, 
            this.gameOverPlayers, 
            this.currentTable, 
            allCombinations, 
            this.gamePhase);
    }

    public GameState withLastWinnerInRound(Player lastWinner) {
        return new GameState(
            this.deck, 
            this.playersList, 
            this.playerMoveIndex, 
            this.roundsPlayed, 
            this.roundChanged, 
            this.winningScore, 
            lastWinner, 
            this.lastWinnerOfMoreCards, 
            this.lastWinnerOfTablePoint, 
            this.gameOverPlayers, 
            this.currentTable, 
            this.allCombinations, 
            this.gamePhase);
    }

    public GameState withLastWinnnerOfTablePoint(Player lastWinner) {
        return new GameState(
            this.deck, 
            this.playersList, 
            this.playerMoveIndex, 
            this.roundsPlayed, 
            this.roundChanged, 
            this.winningScore, 
            this.lastWinnerInRound, 
            this.lastWinnerOfMoreCards, 
            lastWinner, 
            this.gameOverPlayers, 
            this.currentTable, 
            this.allCombinations, 
            this.gamePhase);
    }

    public CardDeck getDeck() {
        return this.deck;
    }

    public List<Player> getPlayersList() {
        return this.playersList;
    }

    public int getPlayerMoveIndex() {
        return this.playerMoveIndex;
    }

    public void setPlayerMoveIndex(int newIndex) {
        this.playerMoveIndex = newIndex;
    }

    public Player getCurrentPlayerMove() {
        return this.playersList.get(this.playerMoveIndex);
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

    public Map<String, Player> getGameOverPlayers() {
        return this.gameOverPlayers;
    }

    public void setGameOverPlayers(String gameWinnerLoser, Player player) {
        this.gameOverPlayers.put(gameWinnerLoser, player);
    }

    public List<Card> getCurrentTable() {
        return this.currentTable;
    }

    public void setCurrentTable(Card card) {
        this.currentTable = new ArrayList<>(this.currentTable);
        this.currentTable.add(card);
    }
    
    public List<List<Card>> getAllCombinations() {
        return this.allCombinations;
    }

    public void setAllCombinations(List<List<Card>> combinations) {
        this.allCombinations = combinations;
    }

    public GamePhase getGamePhase() {
        return this.gamePhase;
    }

    public void setGamePhase(GamePhase nextGamePhase) {
        this.gamePhase = nextGamePhase;
    }

    public String toString() {
        String output = 
                """
                %s,
                playerMoveIndex: %d,
                roundsPlayed: %d,
                roundChanged: %b,
                winningScore: %d,
                lastWinnerInRound: %s,
                lastWinnerOfMoreCards: %s,
                lastWinnerOfTablePoint: %s,
                gameOverPlayers: %s,
                players: %s,
                currentTable: %s,
                allCombinations: %s
                gamePhase: %s
                """.formatted(deck, playerMoveIndex, roundsPlayed, roundChanged, winningScore, lastWinnerInRound, 
                    lastWinnerOfMoreCards, lastWinnerOfTablePoint, gameOverPlayers, playersList, currentTable, allCombinations, gamePhase);
        
        return output;
    }
}
