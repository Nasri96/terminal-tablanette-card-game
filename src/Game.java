import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

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
        System.out.println(state);
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
                    state = handleStart(this.gameState);
                    return this.gameState = state 
                        .withGamePhase(GamePhase.TURN_PLAY_CARD);
                }

                return state;


            case TURN_PLAY_CARD:
                if(input.action == GameAction.PLAY_CARD) {
                    state = handlePlayCard(state, input.payload);
                    return this.gameState = state
                        .withGamePhase(GamePhase.TURN_PICK_COMBINATION);
                }


                return state;

            case TURN_PICK_COMBINATION:
                if(input.action == GameAction.PICK_COMBINATION) {
                    state = handlePickCombination(state, input.payload);
                    this.gameState = state
                        .withGamePhase(GamePhase.TURN_RESOLVE);
                }

                return state;
                
            case TURN_RESOLVE:
                if(input.action == GameAction.CONTINUE) {
                    GamePhase turnResult = handleResolveTurn(state);
                    return this.gameState = state
                        .withGamePhase(turnResult);
                }

                return state;

            case ROUND_END:
                if(input.action == GameAction.CONTINUE) {
                    state = handleRoundEnd(state);
                    return this.gameState = state
                        .withGamePhase(GamePhase.ROUND_START);
                    // game over possible also possible after round end
                    // if(this.isGameOver(state)) {
                    //     this.gameState.setGamePhase(GamePhase.GAME_OVER);
                    //     return state;
                    // }

                    // this.gameState.setGamePhase(GamePhase.ROUND_START);
                }

                return state;

            case ROUND_START:
                if(input.action == GameAction.CONTINUE) {
                    state = handleRoundStart(state);
                    return this.gameState = state
                        .withGamePhase(GamePhase.TURN_PLAY_CARD);
                }

                return state;

            case NEXT_TURN:
                if(input.action == GameAction.CONTINUE) {
                    state = handleNextTurn(state);
                    boolean playersCurrentHandsEmpty = checkPlayersCurrentHand(state);
                    this.gameState = state
                        .withIf(playersCurrentHandsEmpty, s -> s.withGamePhase(GamePhase.DEAL_CARDS))
                        .withIf(!playersCurrentHandsEmpty, s -> s.withGamePhase(GamePhase.TURN_PLAY_CARD));
                }

                return state;

            case DEAL_CARDS:
                if(input.action == GameAction.CONTINUE) {
                    // handleDealCards();
                    state = handleNewDealCards(state);
                    this.gameState = state
                        .withGamePhase(GamePhase.TURN_PLAY_CARD);
                    System.out.println(this.gameState);
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

    private List<String> getOrderIds(GameState state) {
        int playerMoveIndex = state.getPlayerMoveIndex();
        // get the id player order, player at playerMoveIndex is the one who receives the first round of cards
        return state.getPlayersList().stream()
            .map(p -> state.getPlayersList().indexOf(p))
            .sorted((index1, index2) -> {
                if(index1 == playerMoveIndex) {
                    return -1;
                } 
                if(index2 == playerMoveIndex) {
                    return 1;
                }
                if(index1 == index2) {
                    return 0;
                }
                return 0;
            })
            .map(index -> state.getPlayersList().get(index).getId())
            .toList();
    }

    private List<Card> combine(List<Card> r1, List<Card> r2) {
        List<Card> combined = new ArrayList<>(r1);
        combined.addAll(r2);
        return combined;
    }


    private GameState handleNewDealCards(GameState state) {
        GamePhase phase = state.getGamePhase();
        List<Card> deck = state.getCardDeck().getDeck();

        // .get(0) the id of one that receives cards first, .get(1) is the id of the current card dealer
        List<String> orderIds = this.getOrderIds(state);
        System.out.println(orderIds);

        
        List<Card> round1Receiver = deck.subList(0, 1);
        List<Card> round1Dealer = deck.subList(1, 2);
        List<Card> round2Receiver = deck.subList(2, 3);
        List<Card> round2Dealer = deck.subList(3, 4);
        // if game phase is setup or start, deal cards to table also
        List<Card> dealtTableCards = (phase == GamePhase.GAME_SETUP || phase == GamePhase.ROUND_START) ? deck.subList(4, 5) : List.of();
        long countDealtCards = Stream.of(round1Receiver, round1Dealer, round2Receiver, round2Dealer, dealtTableCards)
            .flatMap(Collection::stream)
            .count();
        System.out.println("deck before update: " + deck);
        List<Card> remaining = deck.subList((int) countDealtCards, deck.size());
        System.out.println("deck after update: " + remaining);

        String firstReceiverId = orderIds.get(0);
        String dealerId = orderIds.get(1);

        return state
            .transformPlayer(firstReceiverId, p -> p.withCurrentHand(combine(round1Receiver, round2Receiver)))
            .transformPlayer(dealerId, p -> p.withCurrentHand(combine(round1Dealer, round2Dealer)))
            .transformCardDeck(d -> d.withDeck(remaining))
            .withCurrentTable(combine(state.getCurrentTable(), dealtTableCards));
    }

    private GameState handleStart(GameState state) {
        // shuffle the deck
        // deal cards to players and to table
        // move to PLAY_CARD phase
        return state
            .transformCardDeck(d -> d.shuffled())
            .transform(this::handleNewDealCards);
    }

    private List<Card> removeCardFromPlayerHand(GameState state, Card playedCard) {
        Player player = state.getCurrentPlayerMove();
        List<Card> currentHand = new ArrayList<>(player.getCurrentHand());
        currentHand.remove(playedCard);
        return List.copyOf(currentHand);
    } 

    private List<Card> addCardToTable(GameState state, Card playedCard) {
        List<Card> currentTable = new ArrayList<>(state.getCurrentTable());
        currentTable.add(playedCard);
        return List.copyOf(currentTable);
    }

    private GameState handlePlayCard(GameState state, Object payload) {
        // find which card is played
        // remove card from player's current hand
        // add played card to table
        // get winning combinations
        Player player = state.getCurrentPlayerMove();
        Card playedCard = findPlayedCard(state, String.valueOf(payload));
        
        return state
            .transformPlayer(player.getId(), p -> p.withCurrentHand(removeCardFromPlayerHand(state, playedCard)))
            .withCurrentTable(addCardToTable(state, playedCard))
            .withAllCombinations(this.combinationsService.getCombinations(playedCard, new ArrayList<>(state.getCurrentTable())));
    }

    private GameState handlePickCombination(GameState state, Object payload) {
        Player currentPlayer = state.getCurrentPlayerMove();
        List<Card> pickedCombination = findPickedCombination(state, payload);

        
        // no combinatinos can be won or player chose to not pick any winning combos  => move to next phase
        if(state.getAllCombinations().isEmpty() || pickedCombination.isEmpty()) {
            return state
                .transformPlayer(currentPlayer.getId(), p -> p.withLastCardsWon(List.of()))
                .withAllCombinations(List.of());
        } 
        // pick a correct combination that player chose and remove those cards from table and add them to player's won cards or just play card and move to next phase
        // if player win all cards from table, he gets one 'table point' which adds one total points

        // update the current table
        List<Card> updatedCurrentTable = removeWonCardsFromTable(pickedCombination, new ArrayList<>(state.getCurrentTable()));
        // check if player won all cards from table
        boolean tablePoint = checkTablePoint(updatedCurrentTable);

        // update state
        return state
            .transformPlayer(currentPlayer.getId(), p -> {
                Player updated = p
                    .withLastCardsWon(pickedCombination)
                    .withCardsWon(combine(p.getCardsWon(), pickedCombination));
                return tablePoint ? updated.withTablePoint(updated.getTablePoints() + 1) : updated;
            })
            .withIf(tablePoint, s -> s.withLastWinnnerOfTablePoint(currentPlayer.getId()))
            .withCurrentTable(updatedCurrentTable)
            .withLastWinnerInRound((currentPlayer.getId()))
            .withAllCombinations(List.of());
            
    }

    private GamePhase handleResolveTurn(GameState state) {
        // check for game over first
        if(isGameOver(state)) {
            return GamePhase.GAME_OVER;
        }

        boolean playersCurrentHandsEmpty = checkPlayersCurrentHand(state);
        boolean deckIsEmpty = checkPlayingDeckIsEmpty(state);

        if(playersCurrentHandsEmpty && deckIsEmpty) {
            return GamePhase.ROUND_END;
        }
        
        return GamePhase.NEXT_TURN;
    }

    private boolean isGameOver(GameState state) {
        for(Player player: state.getPlayersList()) {
            if(player.getPointsWon() >= state.getWinningScore()) {
                return true;
            }
        }

        return false;
    }

    private GameState handleNextTurn(GameState state) {
        int currIndex = state.getPlayerMoveIndex();
        return state
            .withIf(currIndex == 0, s -> s.withPlayerMoveIndex(1))
            .withIf(currIndex == 1, s -> s.withPlayerMoveIndex(0))
            .withLastWinnerOfMoreCards(null)
            .withLastWinnnerOfTablePoint(null);
    }

    private GameState handleLastWinnerInRound(GameState state) {
        // check if anyone won any cards in the whole round
        if(state.getLastWinnerInRound() == null) {
            return state;
        }

        // no remaining cards left to award, set last winner in round to null
        if(state.getCurrentTable().size() == 0) {
            return state.withLastWinnerInRound(null);
        }

        // award remaining cards from table to last winner in round
        Player lastWinnerOfCards = state.findPlayerById(state.getLastWinnerInRound());
        return state
            .transformPlayer(lastWinnerOfCards.getId(), p -> {
                return p
                    .withLastCardsWon(new ArrayList<>(state.getCurrentTable()))
                    .withCardsWon(combine(p.getCardsWon(), state.getCurrentTable()));
            });

    }

    private GameState awardPoints(GameState state) {
        // calculate points from cardsWon and roundTablePoints, check whoever won the more cards and add +3 to total points and update the lastWinnerOfMoreCards
        Player p1 = state.findPlayerById(state.getPlayersList().get(0).getId());
        Player p2 = state.findPlayerById(state.getPlayersList().get(1).getId());

        int p1Points = calculatePlayerPoints(p1.getCardsWon(), p1.getTablePoints());
        int p2Points = calculatePlayerPoints(p2.getCardsWon(), p2.getTablePoints());

        int p1Bonus = (p1.getCardsWonSize() > p2.getCardsWonSize()) ? 3 : 0;
        int p2Bonus = (p2.getCardsWonSize() > p1.getCardsWonSize()) ? 3 : 0;

        return state
            .transformPlayer(p1.getId(), p -> p.withPoints(p1Points + p1Bonus))
            .transformPlayer(p2.getId(), p -> p.withPoints(p2Points + p2Bonus))
            .withIf((p1Bonus > p2Bonus), s -> s.withLastWinnerOfMoreCards(p1.getId()))
            .withIf((p2Bonus > p1Bonus), s-> s.withLastWinnerOfMoreCards(p2.getId()));
    }

    private GameState handleRoundEnd(GameState state) {
        return state
            .withRoundsPlayed(state.getRoundsPlayed() + 1)
            .withRoundChanged(true)
            .withLastWinnerOfMoreCards(null)
            .withLastWinnnerOfTablePoint(null)
            .transform(this::handleLastWinnerInRound)
            .transform(this::awardPoints)
            .withCurrentTable(List.of());
    }

    private GameState handleRoundStart(GameState state) {
        // reset cards won, table points, last winner of more cards, last winner of table point, last winner in round
        // generate and shuffle the deck
        // deal cards to players and table
        // swap player turn(player who played last in the last round, plays first in the next round)

        Player p1 = state.getPlayersList().get(0);
        Player p2 = state.getPlayersList().get(1);

        int roundsPlayed = state.getRoundsPlayed();
        int currIndex = state.getPlayerMoveIndex();

        return state
            .transformPlayer(p1.getId(), p -> p.withCardsWon(List.of()).withTablePoint(0))
            .transformPlayer(p2.getId(), p -> p.withCardsWon(List.of()).withTablePoint(0))
            .transformCardDeck(d -> CardDeck.initial().shuffled())
            .transform(this::handleNewDealCards)
            .withLastWinnerOfMoreCards(null)
            .withLastWinnnerOfTablePoint(null)
            .withLastWinnerInRound(null)
            .withIf(currIndex == 0, s -> s.withPlayerMoveIndex(0))
            .withIf(currIndex == 1, s -> s.withPlayerMoveIndex(1))
            .withRoundChanged(false);
    }

    private void handleGameOver() {
        for(Player player: gameState.getPlayersList()) {
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

    // calculates points from all cards won + table points in one round
    private int calculatePlayerPoints(List<Card> cardsWon, int tablePoints) {
        int points = cardsWon.stream()
            .map(c -> c.getPoints())
            .reduce(tablePoints, (a, b) -> {
                return a + b;
            });
            
        return points;
    }

    private List<Card> removeWonCardsFromTable(List<Card> wonCards, List<Card> currentTable) {
        currentTable.removeAll(wonCards);

        Card[] currentTableArray = currentTable.toArray(new Card[0]);
        return List.of(currentTableArray);
    }

    private Card findPlayedCard(GameState state, String playedCardIndex) {
        int index = Integer.valueOf(playedCardIndex);
        Player player = state.getCurrentPlayerMove();
        List<Card> playerHand = player.getCurrentHand();

        return playerHand.get(index);
    }

    private boolean checkPlayersCurrentHand(GameState state) {
        List<Player> players = state.getPlayersList();

        boolean bothCurrentHands = true;

        for(Player player: players) {
            if(player.getCurrentHand().size() > 0) {
                bothCurrentHands = false;
            }
        }

        return bothCurrentHands;
    }

    private boolean checkPlayingDeckIsEmpty(GameState state) {
        if(state.getCardDeck().getDeck().size() == 0) {
            return true;
        }

        return false;
    }

    private List<Card> findPickedCombination(GameState state, Object payload) {
        // check for null first
        if(payload == null) {
            return new ArrayList<>();
        }

        String allCombinationsInputIndex = String.valueOf(payload);
        int inputIndex = Integer.valueOf(allCombinationsInputIndex);

        // return empty list if player choose to play card only
        if(inputIndex == -1 ) {
            return new ArrayList<>();
        }

        return state.getAllCombinations().get(inputIndex);
    }
}