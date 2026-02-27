import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {
    private GameCombinationsService combinationsService;
    private GameState gameState;
    public TerminalUI ui;
    
    public Game(List<Player> playersList) {
        this.combinationsService = new GameCombinationsService();
        this.gameState = GameState.initial(playersList);
        this.ui = null;
    }

    public void setUi(TerminalUI ui) {
        this.ui = ui;
    }

    public GamePhase getGamePhase() {
        return this.gameState.getGamePhase();
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void startGame(TerminalUI ui) {
        ui.start();
    }

    public GameState updateGame(GameInput input) {
        GameState state = this.gameState;
        // System.out.println(state);
        GamePhase gamePhase = state.getGamePhase();
        Player nextPlayerTurn = state.getCurrentPlayerMove();
        // check turn ownership
        if(!nextPlayerTurn.getId().equals(input.id)) {
            System.out.println("not your turn");
            return state;
        }
        
        switch(gamePhase) {
            default: return state;

            case GAME_SETUP:
                if(input.action == GameAction.START) {
                    ResultStart result = handleStart(this.gameState);
                    // this.gamePhase = GamePhase.TURN_PLAY_CARD;
                    this.gameState = state = state
                        .withDeck(result.deck())
                        .withCurrentTable(result.currentTable())
                        .withPlayers(result.players())
                        .withGamePhase(GamePhase.TURN_PLAY_CARD);
                       
                }

                // System.out.println(state.getGamePhase() + " INSIDE GAME SETUP");
                return state;


            case TURN_PLAY_CARD:
                if(input.action == GameAction.PLAY_CARD) {
                    ResultPlayCard result = handlePlayCard(state, input.payload);
                    System.out.println(result);
                    this.gameState = state = state
                        .withPlayers(result.updatedPlayers())
                        .withAllCombinations(result.allCombinations())
                        .withCurrentTable(result.updatedCurrentTable())
                        .withGamePhase(GamePhase.TURN_PICK_COMBINATION);
                }

                return state;

            case TURN_PICK_COMBINATION:
                if(input.action == GameAction.PICK_COMBINATION) {
                    state = handlePickCombination(state, input.payload);
                    state = state
                        .withGamePhase(GamePhase.TURN_RESOLVE);
                    this.gameState = state;
                    System.out.println(this.gameState);
                }

                return state;
                
            case TURN_RESOLVE:
                if(input.action == GameAction.CONTINUE) {
                    GamePhase turnResult = handleResolveTurn();
                    this.gameState.setGamePhase(turnResult);
                }

                return state;

            case ROUND_END:
                if(input.action == GameAction.CONTINUE) {
                    handleRoundEnd();
                    // game over possible also possible after round end
                    if(this.isGameOver()) {
                        this.gameState.setGamePhase(GamePhase.GAME_OVER);
                        return state;
                    }

                    this.gameState.setGamePhase(GamePhase.ROUND_START);
                }

                return state;

            case ROUND_START:
                if(input.action == GameAction.CONTINUE) {
                    handleRoundStart();
                    this.gameState.setGamePhase(GamePhase.DEAL_CARDS);
                }

                return state;

            case NEXT_TURN:
                if(input.action == GameAction.CONTINUE) {
                    handleNextTurn();

                    boolean playersCurrentHandsEmpty = checkPlayersCurrentHand();
                    if(playersCurrentHandsEmpty) {
                        this.gameState.setGamePhase(GamePhase.DEAL_CARDS);
                    } else {
                        this.gameState.setGamePhase(GamePhase.TURN_PLAY_CARD);
                    }
                }

                return state;

            case DEAL_CARDS:
                if(input.action == GameAction.CONTINUE) {
                    // handleDealCards();
                    this.gameState.setGamePhase(GamePhase.TURN_PLAY_CARD);
                }

                return state;
            
            case GAME_OVER:
                if(input.action == GameAction.CONTINUE) {
                    handleGameOver();
                    this.gameState.setGamePhase(GamePhase.GAME_END);
                }
                
                return state;
            
            case GAME_END:
                if(input.action == GameAction.CONTINUE) {
                    handleGameEnd();
                    this.gameState.setGamePhase(GamePhase.GAME_SETUP);
                }

                return state;
                
        }

    }

    private ResultStart handleStart(GameState state) {
        // shuffle the deck
        CardDeck shuffledDeck = state.getDeck().shuffled();
        List<Player> playersList = state.getPlayersList();
        List<Card> currentTable = state.getCurrentTable();

        // deal cards, three cards to each player in two rounds, then four cards to the table
        DealCardsToPlayersResult afterDealCards = handleDealCards(shuffledDeck, playersList);
        DealCardsToTableResult afterDealToTable = dealCardsToTable(afterDealCards.deck(), currentTable);
        return new ResultStart(afterDealCards.deck(), afterDealCards.players(), afterDealToTable.currentTable());

    }

    private ResultPlayCard handlePlayCard(GameState state, Object payload) {
        Player player = state.getCurrentPlayerMove();
        List<List<Card>> allCombinations = new ArrayList<>();
        List<Card> currentTable = new ArrayList<>(state.getCurrentTable());

        // find which card is played
        Card playedCard = findPlayedCard(state, String.valueOf(payload));
        Player updatedPlayer = player.withRemovedCard(playedCard);
        // remove card from player's current hand
        // player.playCard(playedCard);
        // find winning combinations
        allCombinations = this.combinationsService.getCombinations(playedCard, new ArrayList<>(state.getCurrentTable()));
        // add played card to table
        currentTable.add(playedCard);
        // this.playCardToTable(playedCard);
        List<Player> updatedPlayers = state.getPlayersList().stream()
        .map(p -> {
            return p.getId().equals(updatedPlayer.getId()) ? updatedPlayer : p;
        })
        .toList();
        return new ResultPlayCard(updatedPlayers, allCombinations, currentTable);
    }

    private GameState handlePickCombination(GameState state, Object payload) {
        GameState result = state;

        List<Player> players = state.getPlayersList();
        Player currentPlayer = state.getCurrentPlayerMove();
        int combinationsSize = state.getAllCombinations().size();
        List<Card> pickedCombination = findPickedCombination(result, String.valueOf(payload));
        List<Card> updatedCurrentTable = new ArrayList<>();

        List<Player> updatedPlayers = new ArrayList<>();

        for(Player player: players) {
            if(player.getId().equals(currentPlayer.getId())) {
                // no combinatinos can be won or player chose to not pick any winning combos  => move to next phase
                if(combinationsSize == 0 || pickedCombination.size() == 0) {
                    updatedPlayers.add(currentPlayer.withLastCardsWon(new ArrayList<>()));

                    result = result
                    .withPlayers(updatedPlayers)
                    .withAllCombinations(new ArrayList<>());
                    return result;
                    // player.clearLastWonCards();
                } else {
                    // pick a correct combination that player chose and remove those cards from table and add them to player's won cards or just play card and move to next phase
                    // if player win all cards from table he gets one 'table point' which adds one total points
                    // List<Card> pickedCombinations = findPickedCombination(state, String.valueOf(payload));
                    int newPoints = pointsFromCombo(pickedCombination);
                    List<Card> totalCardsWon = new ArrayList<>(currentPlayer.getCardsWon());
                    totalCardsWon.addAll(pickedCombination);

                    // update the player
                    currentPlayer = currentPlayer
                    .withLastCardsWon(new ArrayList<>(pickedCombination))
                    .withCardsWon(new ArrayList<>(totalCardsWon))
                    .withPoints(newPoints);

                    // update the current table
                    updatedCurrentTable = removeWonCardsFromTable(pickedCombination, new ArrayList<>(state.getCurrentTable()));
                    // check if player won all cards from table
                    boolean tablePoint = checkTablePoint(updatedCurrentTable);

                    if(tablePoint) {
                        currentPlayer = currentPlayer
                        .withTablePoint(currentPlayer.getTablePoints() + 1)
                        .withPoints(currentPlayer.getPointsWon() + 1);
                        //player.addTablePoint();
                        //addPointsToPlayer(player, 1);
                        //gameState.setLastWinnerOfTablePoint(player);
                        result = result.withLastWinnnerOfTablePoint(currentPlayer);  
                    }
                }
            } else {
                updatedPlayers.add(player);
            }
        }

        result = result
            .withPlayers(updatedPlayers)
            .withCurrentTable(updatedCurrentTable)
            .withLastWinnerInRound(currentPlayer)
            .withAllCombinations(new ArrayList<>());

        return result;
    }

    private GamePhase handleResolveTurn() {
        // check for game over first
        if(isGameOver()) {
            return GamePhase.GAME_OVER;
        }

        boolean playersCurrentHandsEmpty = checkPlayersCurrentHand();
        boolean deckIsEmpty = checkPlayingDeckIsEmpty();

        if(playersCurrentHandsEmpty && deckIsEmpty) {
            return GamePhase.ROUND_END;
        }
        
        return GamePhase.NEXT_TURN;
    }

    private boolean isGameOver() {
        for(Player player: gameState.getPlayers()) {
            if(player.getPointsWon() >= gameState.getWinningScore()) {
                return true;
            }
        }

        return false;
    }

    private DealCardsToPlayersResult handleDealCards(CardDeck deck, List<Player> players) {

        DealCardsToPlayersResult resultAfterFirst = dealCardsToPlayers(deck, players);
        DealCardsToPlayersResult resultAfterSecond = dealCardsToPlayers(resultAfterFirst.deck(), resultAfterFirst.players());

        return new DealCardsToPlayersResult(resultAfterSecond.deck(), resultAfterSecond.players());
    }

    private void handleNextTurn() {
        // on every new turn, swap who is playing
        if(gameState.getPlayerMoveIndex() == 0) {
            System.out.println("switching players: player 0 > player 1");
            gameState.setPlayerMoveIndex(1);
        } else {
            System.out.println("switching players: player 1 > player 0");
            gameState.setPlayerMoveIndex(0);
        }

        this.gameState.setLastWinnerOfMoreCards(null);
    }

    private void handleRoundEnd() {
        gameState.incrementRoundsPlayed();
        gameState.setRoundChanged(true);
        // reset more cards winner and table point winner after every round
        gameState.setLastWinnerOfMoreCards(null);
        gameState.setLastWinnerOfTablePoint(null);

        if(gameState.getLastWinnerInRound() != null) {
            // give last winner of round remaining cards, if any, from table
            if(gameState.getCurrentTable().size() > 0) {
                gameState.getLastWinnerInRound().clearLastWonCards();
                gameState.getLastWinnerInRound().winCards(gameState.getCurrentTable());
                addPointsToPlayer(gameState.getLastWinnerInRound());
                removeWonCardsFromTable(gameState.getCurrentTable());
            } else {
                gameState.setLastWinnerInRound(null);
            }

        }

        // check which player has more total cards taken, and award them 3 points
        int player1CardsWon = gameState.getPlayers()[0].getCardsWonSize();
        int player2CardsWon = gameState.getPlayers()[1].getCardsWonSize();
        if(player1CardsWon > player2CardsWon) {
            gameState.setLastWinnerOfMoreCards(gameState.getPlayers()[0]);
            addPointsToPlayer(gameState.getPlayers()[0], 3);
        } else if(player2CardsWon > player1CardsWon) {
            gameState.setLastWinnerOfMoreCards(gameState.getPlayers()[1]);
            addPointsToPlayer(gameState.getPlayers()[1], 3);
        }
        
    }

    private void handleRoundStart() {
        // collect cards to the deck
        List<Card> playerCards = gameState.getPlayers()[0].getCardsWon();
        List<Card> cpuCards = gameState.getPlayers()[1].getCardsWon();
        gameState.getDeck().getDeck().addAll(playerCards);
        gameState.getDeck().getDeck().addAll(cpuCards);
        // reset player cards won
        gameState.getPlayers()[0].clearWonCards();
        gameState.getPlayers()[1].clearWonCards();
        // shuffle the deck
        gameState.getDeck().shuffleDeck();
        // deal cards to table
        this.dealCardsToTable();
        // on every new round, swap who should play first
        if(gameState.getRoundsPlayed() % 2 == 0) {
            gameState.setPlayerMoveIndex(0);
        } else {
            gameState.setPlayerMoveIndex(1);
        }

        gameState.setRoundChanged(false);
       
    }

    private void handleGameOver() {
        for(Player player: gameState.getPlayers()) {
            if(player.getPointsWon() >= gameState.getWinningScore()) {
                gameState.setGameOverPlayers("winner", player);
            } else {
                gameState.setGameOverPlayers("loser", player);
            }
        }
    }

    private void handleGameEnd() {
        // reset game
        gameState.setPlayerMoveIndex(0);
        gameState.setRoundsPlayed(0);
        gameState.setRoundChanged(false);
        gameState.setLastWinnerInRound(null);
        gameState.setLastWinnerOfMoreCards(null);
        gameState.setLastWinnerOfTablePoint(null);
        gameState.getGameOverPlayers().clear();
        gameState.setAllCombinations(null);

        // reset players
        for(Player player: gameState.getPlayers()) {
            player.reset();
        }

        // reset deck
        gameState.getDeck().resetDeck();


    }

    private DealCardsToPlayersResult dealCardsToPlayers(CardDeck deck, List<Player> playersList) { //Player[] players) {
        List<Card> deckList = new ArrayList<>(deck.getDeck());
        // Player[] playersWithAddedCards = new Player[players.length];
        List<Player> playersListWithAddedCards = new ArrayList<>();

        int cursor = 0; // where we are in the deck
        int cardsPerPlayer = 3;

        for(Player currentPlayer: playersList) {
            // extract cards to deal
            List<Card> cardsToDeal = new ArrayList<>(deckList.subList(cursor, cardsPerPlayer + cursor));
            // update player
            playersListWithAddedCards.add(currentPlayer.withAddedCards(cardsToDeal));

            cursor+= cardsPerPlayer;
        }
        CardDeck deckWithRemovedCards = deck.withRemovedCards(cursor);

        return new DealCardsToPlayersResult(deckWithRemovedCards, playersListWithAddedCards);
    }

    private DealCardsToTableResult dealCardsToTable(CardDeck deck, List<Card> currentTable) {
        List<Card> deckList = new ArrayList<>(deck.getDeck());
        List<Card> currentTableWithAddedCards = new ArrayList<>(currentTable);

        // deal cards to table
        int cardsToDeal = 4;
        for(int i = 0; i < cardsToDeal; i++) {
            currentTableWithAddedCards.add(deckList.get(i));
        }
        CardDeck deckWithRemovedCards = deck.withRemovedCards(cardsToDeal);

        return new DealCardsToTableResult(deckWithRemovedCards, currentTableWithAddedCards);



        // force ACE into table
        // this.currentTable.add(this.deck.dealCard("10"));
        // this.currentTable.add(this.deck.dealCard("Q"));
        // this.currentTable.add(this.deck.dealCard("Q"));
        // this.currentTable.add(this.deck.dealCard("3"));
        // this.currentTable.add(this.deck.dealCard("3"));
    }

    private void playCardToTable(Card card) {
        this.gameState.setCurrentTable(card);
    }

    private int pointsFromCombo(List<Card> combo) {
        int newPoints = 0;

        for(Card card: combo) {
            newPoints+= card.getPoints();
        }

        return newPoints;
    }

    private void addPointsToPlayer(Player player) {
        int newPoints = 0;
        List<Card> wonCards = player.getLastCardsWon();

        for(Card card: wonCards) {
            newPoints+= card.getPoints();
        }

        player.setPoints(newPoints);
    }

    private void addPointsToPlayer(Player player, int points) {
        player.setPoints(points);
    }

    private boolean checkTablePoint(List<Card> currentTable) {
        return currentTable.isEmpty();
    }

    private List<Card> removeWonCardsFromTable(List<Card> wonCards, List<Card> currentTable) {
        Iterator<Card> tableIterator = currentTable.iterator();
        Iterator<Card> wonCardsIterator = wonCards.iterator();
        while(tableIterator.hasNext()) {
            Card next = tableIterator.next();
            while(wonCardsIterator.hasNext()) {
                Card nextWon = wonCardsIterator.next();
                if(next.equals(nextWon)) {
                    tableIterator.remove();
                }
            }
        }

        return new ArrayList<>(currentTable);
        // List<Card> currentTableCopy = new ArrayList<>(gameState.getCurrentTable());
        // for(int i = 0; i < currentTableCopy.size(); i++) {
        //     Card currentCard = currentTableCopy.get(i);
        //     for(int j = 0; j < wonCards.size(); j++) {
        //         if(wonCards.get(j).equals(currentCard)) {
        //             gameState.getCurrentTable().remove(currentCard);
        //             // System.out.println("Removed from table: " + wonCards.get(j));
        //         }
        //     }
        // }
    }

    private Card findPlayedCard(GameState state, String playedCardIndex) {
        int index = Integer.valueOf(playedCardIndex);
        Player player = state.getCurrentPlayerMove();
        List<Card> playerHand = player.getCurrentHand();

        return playerHand.get(index);
    }

    private boolean checkPlayersCurrentHand() {
        Player[] players = gameState.getPlayers();

        boolean bothCurrentHands = true;

        for(Player player: players) {
            if(player.getCurrentHand().size() > 0) {
                bothCurrentHands = false;
            }
        }

        return bothCurrentHands;
    }

    private boolean checkPlayingDeckIsEmpty() {
        if(gameState.getDeck().getDeck().size() == 0) {
            return true;
        }

        return false;
    }

    private List<Card> findPickedCombination(GameState state, String allCombinationsInputIndex) {
        int inputIndex = Integer.valueOf(allCombinationsInputIndex);

        // return empty list if player choose to play card only
        if(inputIndex == -1) {
            return new ArrayList<>();
        }

        return state.getAllCombinations().get(inputIndex);
    }
}