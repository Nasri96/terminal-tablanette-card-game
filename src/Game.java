import java.util.ArrayList;

public class Game {
    private GameCombinationsService combinationsService;
    private GameState gameState;
    public TerminalUI ui;
    
    public Game(Player[] players) {
        this.combinationsService = new GameCombinationsService();
        this.gameState = new GameState(players);
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

    public void updateGame(GameInput input) {
        GamePhase gamePhase = this.gameState.getGamePhase();
        switch(gamePhase) {
            case GAME_SETUP:
                if(input.action == GameAction.START) {
                    handleStart();
                    // this.gamePhase = GamePhase.TURN_PLAY_CARD;
                    this.gameState.setGamePhase(GamePhase.TURN_PLAY_CARD);
                }

                break;

            case TURN_PLAY_CARD:
                if(input.action == GameAction.PLAY_CARD) {
                    handlePlayCard(input.payload);
                    this.gameState.setGamePhase(GamePhase.TURN_PICK_COMBINATION);
                }

                break;

            case TURN_PICK_COMBINATION:
                if(input.action == GameAction.PICK_COMBINATION) {
                    handlePickCombination(input.payload);
                    this.gameState.setGamePhase(GamePhase.TURN_RESOLVE);
                }

                break;
                
            case TURN_RESOLVE:
                if(input.action == GameAction.CONTINUE) {
                    GamePhase turnResult = handleResolveTurn();
                    this.gameState.setGamePhase(turnResult);
                }

                break;

            case ROUND_END:
                if(input.action == GameAction.CONTINUE) {
                    handleRoundEnd();
                    // game over possible also possible after round end
                    if(this.isGameOver()) {
                        this.gameState.setGamePhase(GamePhase.GAME_OVER);
                        break;
                    }

                    this.gameState.setGamePhase(GamePhase.ROUND_START);
                }

                break;

            case ROUND_START:
                if(input.action == GameAction.CONTINUE) {
                    handleRoundStart();
                    this.gameState.setGamePhase(GamePhase.DEAL_CARDS);
                }

                break;

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

                break;

            case DEAL_CARDS:
                if(input.action == GameAction.CONTINUE) {
                    handleDealCards();
                    this.gameState.setGamePhase(GamePhase.TURN_PLAY_CARD);
                }

                break;
            
            case GAME_OVER:
                if(input.action == GameAction.CONTINUE) {
                    handleGameOver();
                    this.gameState.setGamePhase(GamePhase.GAME_END);
                }
                
                break;
            
            case GAME_END:
                if(input.action == GameAction.CONTINUE) {
                    handleGameEnd();
                    this.gameState.setGamePhase(GamePhase.GAME_SETUP);
                }

                break;
                
        }

    }

    private void handleStart() {
        // shuffle the deck
        this.gameState.getDeck().shuffleDeck();
        // deal cards, three cards to each player in two rounds, then four cards to the table
        this.dealCardsToPlayers();
        this.dealCardsToPlayers();
        // this.dealCardsToPlayers();
        this.dealCardsToTable();
    }

    private void handlePlayCard(Object payload) {
        Player player = this.gameState.getCurrentPlayerMove();
        // find which card is played
        Card playedCard = findPlayedCard(String.valueOf(payload));
        // remove card from player's current hand
        player.playCard(playedCard);
        // find winning combinations
        this.gameState.setAllCombinations(this.combinationsService.getCombinations(playedCard, gameState.getCurrentTable()));
        // add played card to table
        this.playCardToTable(playedCard);
    }

    private void handlePickCombination(Object payload) {
        Player player = gameState.getCurrentPlayerMove();
        // no combinatinos can be won => move to next phase
        if(gameState.getAllCombinations().size() == 0) {
            player.clearLastWonCards();
        } else {
            // pick a correct combination that player choose and remove those cards from table and add them to player's won cards or just play card and move to next phase
            // if player win all cards from table he gets one 'table point' which adds one total points
            ArrayList<Card> pickedCombinations = findPickedCombination(String.valueOf(payload));
            if(pickedCombinations.size() == 0) {
                player.clearLastWonCards();
            } else {
                // give won cards to player
                player.clearLastWonCards();
                player.winCards(pickedCombinations);
                addPointsToPlayer(player);
                gameState.setLastWinnerInRound(player);
                // remove won cards from table
                removeWonCardsFromTable(pickedCombinations);
                // check if player won all cards from table
                boolean tablePoint = checkTablePoint();
                if(tablePoint) {
                    player.addTablePoint();
                    addPointsToPlayer(player, 1);
                    gameState.setLastWinnerOfTablePoint(player);
                }
            }
        }
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

    private void handleDealCards() {
        this.dealCardsToPlayers();
        this.dealCardsToPlayers();
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
        ArrayList<Card> playerCards = gameState.getPlayers()[0].getCardsWon();
        ArrayList<Card> cpuCards = gameState.getPlayers()[1].getCardsWon();
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
        this.combinationsService.clearCombinations();
        gameState.setAllCombinations(null);

        // reset players
        for(Player player: gameState.getPlayers()) {
            player.reset();
        }

        // reset deck
        gameState.getDeck().resetDeck();


    }

    private void dealCardsToPlayers() {
        Player[] players = this.gameState.getPlayers();
        CardDeck deck = this.gameState.getDeck();
        for(int i = 0; i < players.length; i++) {
            // remove three cards from deck and give it to player
            for(int j = 0; j < 1; j++) {
                players[i].addCardToCurrentHand(deck.dealCard(0));
            }
        }
    }

    private void dealCardsToTable() {
        ArrayList<Card> gameTable = this.gameState.getCurrentTable();
        for(int i = 0; i < 1; i++) {
            gameTable.add(this.gameState.getDeck().dealCard(0));
        }

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

    private void addPointsToPlayer(Player player) {
        int newPoints = 0;
        ArrayList<Card> wonCards = player.getLastCardsWon();

        for(Card card: wonCards) {
            newPoints+= card.getPoints();
        }

        player.setPoints(newPoints);
    }

    private void addPointsToPlayer(Player player, int points) {
        player.setPoints(points);
    }

    private boolean checkTablePoint() {
        return gameState.getCurrentTable().isEmpty();
    }

    private void removeWonCardsFromTable(ArrayList<Card> wonCards) {
        ArrayList<Card> currentTableCopy = new ArrayList<>(gameState.getCurrentTable());
        for(int i = 0; i < currentTableCopy.size(); i++) {
            Card currentCard = currentTableCopy.get(i);
            for(int j = 0; j < wonCards.size(); j++) {
                if(wonCards.get(j).equals(currentCard)) {
                    gameState.getCurrentTable().remove(currentCard);
                    // System.out.println("Removed from table: " + wonCards.get(j));
                }
            }
        }
    }

    private Card findPlayedCard(String playedCardIndex) {
        int index = Integer.valueOf(playedCardIndex);
        Player player = this.gameState.getCurrentPlayerMove();
        ArrayList<Card> playerHand = player.getCurrentHand();

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

    private ArrayList<Card> findPickedCombination(String allCombinationsInputIndex) {
        int inputIndex = Integer.valueOf(allCombinationsInputIndex);

        // return empty list if player choose to play card only
        if(inputIndex == -1) {
            return new ArrayList<Card>();
        }

        return gameState.getAllCombinations().get(inputIndex);
    }
}