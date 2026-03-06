import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class GameState {
    private CardDeck cardDeck;
    private int playerMoveIndex;
    private int roundsPlayed;
    private boolean roundChanged;
    private int winningScore;
    private String lastWinnerInRound;
    private String lastWinnerOfMoreCards;
    private String lastWinnerOfTablePoint;
    private List<Player> playersList;
    private List<Card> currentTable;
    private List<List<Card>> allCombinations;
    private GamePhase gamePhase;


    public GameState(
        CardDeck cardDeck, 
        List<Player> playersList,
        int playerMoveIndex, 
        int roundsPlayed, 
        boolean roundChanged, 
        int winningScore, 
        String lastWinnerInRound,
        String lastWinnerOfMoreCards,
        String lastWinnerOfTablePoint,
        List<Card> currentTable,
        List<List<Card>> allCombinations,
        GamePhase gamePhase
    ) {
        this.cardDeck = cardDeck;
        this.playersList = List.copyOf(playersList);
        this.playerMoveIndex = playerMoveIndex;
        this.roundsPlayed = roundsPlayed;
        this.roundChanged = roundChanged;
        this.winningScore = winningScore;
        this.lastWinnerInRound = lastWinnerInRound;
        this.lastWinnerOfMoreCards = lastWinnerOfMoreCards;
        this.lastWinnerOfTablePoint = lastWinnerOfTablePoint;
        this.currentTable = List.copyOf(currentTable);
        this.allCombinations = List.copyOf(allCombinations);
        this.gamePhase = gamePhase;
    }

    public static GameState initial(List<Player> playerList) {
        return new GameState(
            CardDeck.initial(), playerList, 0, 0, 
            false, 5, null, 
            null, null, 
            new ArrayList<>(), new ArrayList<>(), GamePhase.GAME_SETUP);
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
            this.currentTable, 
            this.allCombinations, 
            this.gamePhase);
    }

    public GameState withPlayers(List<Player> updatedListPlayers) {
        return new GameState(
            this.cardDeck, 
            updatedListPlayers,
            this.playerMoveIndex,
            this.roundsPlayed,
            this.roundChanged,
            this.winningScore,
            this.lastWinnerInRound,
            this.lastWinnerOfMoreCards,
            this.lastWinnerOfTablePoint,
            this.currentTable,
            this.allCombinations,
            this.gamePhase);
    }

    public GameState withGamePhase(GamePhase nextPhase) {
        return new GameState(
            this.cardDeck, 
            this.playersList,
            this.playerMoveIndex, 
            this.roundsPlayed, 
            this.roundChanged, 
            this.winningScore, 
            this.lastWinnerInRound, 
            this.lastWinnerOfMoreCards, 
            this.lastWinnerOfTablePoint,
            this.currentTable, 
            this.allCombinations, 
            nextPhase);
    }

    public GameState withCurrentTable(List<Card> updatedCurrentTable) {
        return new GameState(
            this.cardDeck,
            this.playersList,
            this.playerMoveIndex, 
            this.roundsPlayed, 
            this.roundChanged, 
            this.winningScore, 
            this.lastWinnerInRound, 
            this.lastWinnerOfMoreCards, 
            this.lastWinnerOfTablePoint,
            updatedCurrentTable, 
            this.allCombinations, 
            this.gamePhase);
    }

    public GameState withAllCombinations(List<List<Card>> allCombinations) {
        return new GameState(
            this.cardDeck, 
            this.playersList, 
            this.playerMoveIndex, 
            this.roundsPlayed, 
            this.roundChanged, 
            this.winningScore, 
            this.lastWinnerInRound, 
            this.lastWinnerOfMoreCards, 
            this.lastWinnerOfTablePoint, 
            this.currentTable, 
            allCombinations, 
            this.gamePhase);
    }

    public GameState withLastWinnerInRound(String lastWinner) {
        return new GameState(
            this.cardDeck, 
            this.playersList, 
            this.playerMoveIndex, 
            this.roundsPlayed, 
            this.roundChanged, 
            this.winningScore, 
            lastWinner, 
            this.lastWinnerOfMoreCards, 
            this.lastWinnerOfTablePoint, 
            this.currentTable, 
            this.allCombinations, 
            this.gamePhase);
    }

    public GameState withLastWinnnerOfTablePoint(String lastWinner) {
        return new GameState(
            this.cardDeck, 
            this.playersList, 
            this.playerMoveIndex, 
            this.roundsPlayed, 
            this.roundChanged, 
            this.winningScore, 
            this.lastWinnerInRound, 
            this.lastWinnerOfMoreCards, 
            lastWinner, 
            this.currentTable, 
            this.allCombinations, 
            this.gamePhase);
    }

    public GameState withLastWinnerOfMoreCards(String lastWinner) {
        return new GameState(
            this.cardDeck, 
            this.playersList, 
            this.playerMoveIndex, 
            this.roundsPlayed, 
            this.roundChanged, 
            this.winningScore, 
            this.lastWinnerInRound, 
            lastWinner, 
            this.lastWinnerOfTablePoint,
            this.currentTable, 
            this.allCombinations, 
            this.gamePhase);
    }

    public GameState withPlayerMoveIndex(int newIndex) {
        return new GameState(
            this.cardDeck, 
            this.playersList, 
            newIndex, 
            this.roundsPlayed, 
            this.roundChanged, 
            this.winningScore, 
            this.lastWinnerInRound, 
            this.lastWinnerOfMoreCards, 
            this.lastWinnerOfTablePoint, 
            this.currentTable, 
            this.allCombinations, 
            this.gamePhase);
    }

    public GameState withRoundsPlayed(int updatedRoundsPlayed) {
        return new GameState(
            this.cardDeck, 
            this.playersList, 
            this.playerMoveIndex, 
            updatedRoundsPlayed, 
            this.roundChanged, 
            this.winningScore, 
            this.lastWinnerInRound, 
            this.lastWinnerOfMoreCards, 
            this.lastWinnerOfTablePoint,
            this.currentTable, 
            this.allCombinations, 
            this.gamePhase);
    }

    public GameState withRoundChanged(boolean updatedRoundChanged) {
        return new GameState(
            this.cardDeck, 
            this.playersList, 
            this.playerMoveIndex, 
            this.roundsPlayed, 
            updatedRoundChanged, 
            this.winningScore, 
            this.lastWinnerInRound, 
            this.lastWinnerOfMoreCards, 
            this.lastWinnerOfTablePoint,
            this.currentTable, 
            this.allCombinations, 
            this.gamePhase);
    }

    public Player findPlayerById(String id) {
        return this.playersList.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .get();
    }

    // will be used later...
    // public GameState transformCurrentTable(UnaryOperator<List<Card>> updater) {
    //     List<Card> updatedCurrentTable = updater.apply(new ArrayList<>(this.currentTable));

    //     return withCurrentTable(List.copyOf(updatedCurrentTable));
    // }

    public GameState transformCardDeck(UnaryOperator<CardDeck> updater) {
        CardDeck updatedDeck = updater.apply(this.cardDeck);

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
            this.currentTable, 
            this.allCombinations, 
            this.gamePhase);
    }

    public GameState transformPlayer(String playerId, UnaryOperator<Player> updater) {
        List<Player> nextPlayers = this.getPlayersList().stream()
            .map(p -> p.getId().equals(playerId) ? updater.apply(p) : p)
            .toList();

        return this.withPlayers(nextPlayers);
            
    } 

    public GameState withIf(boolean condition, UnaryOperator<GameState> updater) {
        return condition ? updater.apply(this) : this;
    }

    public GameState transform(UnaryOperator<GameState> updater) {
        return updater.apply(this);
    }

    public CardDeck getCardDeck() {
        return this.cardDeck;
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

    public String getLastWinnerInRound() {
        return this.lastWinnerInRound;
    }

    public String getLastWinnerOfMoreCards() {
        return this.lastWinnerOfMoreCards;
    }

    public String getLastWinnerOfTablePoint() {
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
                lastWinnerInRound id: %s
                lastWinnerOfMoreCards id: %s
                lastWinnerOfTablePoint id: %s,
                player list index0: {
                    %s
                },
                player list index1: {
                    %s
                }
                currentTable: %s,
                allCombinations: %s
                gamePhase: %s
                """.formatted(cardDeck, playerMoveIndex, roundsPlayed, roundChanged, winningScore, lastWinnerInRound, 
                    lastWinnerOfMoreCards, lastWinnerOfTablePoint, playersList.get(0), playersList.get(1), currentTable, allCombinations, gamePhase);
        
        return output;
    }
}
