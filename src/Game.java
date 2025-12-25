import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class Game {
    private CardDeck deck;
    private String state;
    private int playerMoveIndex;
    private int playerMovePhase;
    private int roundsPlayed;
    private int winningScore;
    private Player lastWinnerInRound;
    private Player lastWinnerOfMoreCards;
    private Player lastWinnerOfTablePoint;
    private ArrayList<ArrayList<Card>> allCombinations;
    private HashMap<String, Set<Set<Card>>> mapCombinations;
    private Player[] players;
    private ArrayList<Card> currentTable;

    public Game(CardDeck deck, Player[] players) {
        this.deck = deck;
        this.state = "main-menu";
        // next player move index can be 0 or 1, as two players max can play the game
        this.playerMoveIndex = 0;
        this.playerMovePhase = 0;
        this.roundsPlayed = 0;
        this.winningScore = 20;
        this.lastWinnerInRound = null;
        this.lastWinnerOfMoreCards = null;
        this.lastWinnerOfTablePoint = null;
        this.allCombinations = new ArrayList<>();
        this.mapCombinations = new HashMap<>();
        this.players = players;
        this.currentTable = new ArrayList<Card>();
    }

    public String getState() {
        return this.state;
    }

    public int getPlayerMovePhase() {
        return this.playerMovePhase;
    }

    public CardDeck getDeck() {
        return this.deck;
    }

    public Player[] getPlayers() {
        return this.players;
    }

    public ArrayList<Card> getCurrentTable() {
        return this.currentTable;
    }

    public ArrayList<ArrayList<Card>> getAllCombinations() {
        return this.allCombinations;
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

    public void startGame(TerminalUI ui) {
        ui.start();
    }

    public void getInputFromUI(String input) {
        this.updateGame(input);
    }

    public Player getNextPlayerMove() {
        return this.players[this.playerMoveIndex];
    }

    public void updateGame(String input) {
        // on start, set game state to start, shuffle the deck and deal the cards to the players and table
        if(input.equals("start")) {
            this.state = "player-move";
            // shuffle the deck
            this.deck.shuffleDeck();
            // deal cards, three cards to each player in two rounds, then four cards to the table
            this.dealCardsToPlayers();
            this.dealCardsToPlayers();
            // this.dealCardsToPlayers();
            this.dealCardsToTable();
            // initialize combinations
            this.initializeCombinations();
            return;
        }

        if(input.equals("main-menu")) {
            this.state = "main-menu";
        }

        if(input.equals("quit")) {
            this.state = "game-over";
        }

        if(input.equals("next-turn")) {
            boolean playersCurrentHandsEmpty = checkPlayersCurrentHand();
            boolean deckIsEmpty = checkPlayingDeckIsEmpty();

            if(playersCurrentHandsEmpty && deckIsEmpty) {
                handleNextRound();
                return;
            }

            handleNextTurn();
            return;
        }

        if(input.equals("player-move")) {
            this.state = "player-move";
            return;
        }

        // handle player-move game logic
        if(this.state == "player-move") {
            if(this.playerMovePhase == 0) {
                Player player = this.getNextPlayerMove();
                if(player.getType().equals("player")) {
                    // find which card is played
                    Card playedCard = findPlayedCard(input);
                    // remove card from player's current hand
                    player.playCard(playedCard);
                    // find winning combinations
                    this.createWinningCombinations(playedCard);
                    // add played card to table
                    this.playCardToTable(playedCard);
                    // move to next phase
                    this.playerMovePhase = 1;
                } else if(player.getType().equals("cpu")) {
                    handleCpuMovePhase0();
                }
                

            } else if(this.playerMovePhase == 1) {
                Player player = this.getNextPlayerMove();
                if(player.getType().equals("player")) {
                    // no combinatinos can be won => move to next phase
                    if(this.allCombinations.size() == 0) {
                        player.clearLastWonCards();
                        this.playerMovePhase = 2;
                    } else {
                        // pick a correct combination that player choose and remove those cards from table and add them to player's won cards or just play card and move to next phase
                        // if player win all cards from table he gets one 'table point' which adds one total points
                        ArrayList<Card> pickedCombinations = findPickedCombination(input);
                        if(pickedCombinations.size() == 0) {
                            player.clearLastWonCards();
                            this.playerMovePhase = 2;
                        } else {
                            // give won cards to player
                            player.clearLastWonCards();
                            player.winCards(pickedCombinations);
                            addPointsToPlayer(player);
                            this.lastWinnerInRound = player;
                            // remove won cards from table
                            removeWonCardsFromTable(pickedCombinations);
                            // check if player won all cards from table
                            boolean tablePoint = checkTablePoint();
                            if(tablePoint) {
                                player.addTablePoint();
                                addPointsToPlayer(player, 1);
                                this.lastWinnerOfTablePoint = player;
                            }
                            // move to next phase
                            this.playerMovePhase = 2;
                        }
        
                    }
                } else if(player.getType().equals("cpu")) {
                    handleCpuMovePhase1();
                }
            } 
        } 
        
    }

    public void dealCardsToPlayers() {
        for(int i = 0; i < players.length; i++) {
            // remove three cards from deck and give it to player
            for(int j = 0; j < 2; j++) {
                this.players[i].addCardToCurrentHand(this.deck.dealCard(0));
            }
        }
    }

    public void dealCardsToTable() {
        for(int i = 0; i < 2; i++) {
            this.currentTable.add(this.deck.getDeck().remove(0));
        }
    }

    public void playCardToTable(Card card) {
        this.currentTable.add(card);
    }

    public void addPointsToPlayer(Player player) {
        int newPoints = 0;
        ArrayList<Card> wonCards = player.getLastCardsWon();

        for(Card card: wonCards) {
            newPoints+= card.getPoints();
        }

        player.setPoints(newPoints);
    }

    public void addPointsToPlayer(Player player, int points) {
        player.setPoints(points);
    }

    public boolean checkTablePoint() {
        return this.currentTable.isEmpty();
    }

    public void removeWonCardsFromTable(ArrayList<Card> wonCards) {
        ArrayList<Card> currentTableCopy = new ArrayList<>(this.currentTable);
        for(int i = 0; i < currentTableCopy.size(); i++) {
            Card currentCard = currentTableCopy.get(i);
            for(int j = 0; j < wonCards.size(); j++) {
                if(wonCards.get(j).equals(currentCard)) {
                    this.currentTable.remove(currentCard);
                    // System.out.println("Removed from table: " + wonCards.get(j));
                }
            }
        }
    }

    public Card findPlayedCard(String playedCardIndex) {
        int index = Integer.valueOf(playedCardIndex);
        Player player = this.getNextPlayerMove();
        ArrayList<Card> playerHand = player.getCurrentHand();

        return playerHand.get(index);
    }

    public void handleNextTurn() {
        // check if both players played all cards
        boolean playersCurrentHandsEmpty = checkPlayersCurrentHand();

        if(playersCurrentHandsEmpty) {
            dealCardsToPlayers();
            dealCardsToPlayers();
            this.state = "next-deal-of-cards";
        }
        

        // on every new turn, swap who is playing
        if(this.playerMoveIndex == 0) {
            this.playerMoveIndex = 1;
        } else {
            this.playerMoveIndex = 0;
        }
      

        this.lastWinnerOfTablePoint = null;
        this.playerMovePhase = 0;
    }

    public void handleNextRound() {
        this.roundsPlayed++;
        // reset more cards winner and table point winner after every round
        this.lastWinnerOfMoreCards = null;
        this.lastWinnerOfTablePoint = null;

        // on every new round, swap who should play first
        if(roundsPlayed % 2 == 0) {
            this.playerMoveIndex = 0;
        } else {
            this.playerMoveIndex = 1;
        }

        // give last winner of round remaining cards, if any, from table
        if(this.currentTable.size() > 0) {
            this.lastWinnerInRound.clearLastWonCards();
            this.lastWinnerInRound.winCards(this.currentTable);
            addPointsToPlayer(this.lastWinnerInRound);
            removeWonCardsFromTable(currentTable);
        } else {
            this.lastWinnerInRound = null;
        }

        // check which player has more total cards taken, and award them 3 points
        int playerCardsWon = this.players[0].getCardsWonSize();
        int cpuCardsWon = this.players[1].getCardsWonSize();
        if(playerCardsWon > cpuCardsWon) {
            this.lastWinnerOfMoreCards = players[0];
            addPointsToPlayer(this.players[0], 3);
        } else if(cpuCardsWon > playerCardsWon) {
            this.lastWinnerOfMoreCards = players[1];
            addPointsToPlayer(this.players[1], 3);
        }
        // collect cards to the deck
        ArrayList<Card> playerCards = this.players[0].getCardsWon();
        ArrayList<Card> cpuCards = this.players[1].getCardsWon();
        this.deck.getDeck().addAll(playerCards);
        this.deck.getDeck().addAll(cpuCards);
        // reset player cards won
        this.players[0].clearWonCards();
        this.players[1].clearWonCards();
        // shuffle the deck
        this.deck.shuffleDeck();
        // deal cards to players 
        this.dealCardsToPlayers();
        this.dealCardsToPlayers();
        // deal cards to table
        this.dealCardsToTable();
        // start new round
        this.playerMovePhase = 0;
        this.state = "next-round";
    }

    public void handleCpuMovePhase0() {
        Player cpu = getNextPlayerMove();
        ArrayList<Card> cpuHand = cpu.getCurrentHand();
        int max = cpuHand.size() - 1;
        int min = 0;

        int randomIndex = (int) (Math.random() * (max + 1));

        Card randomCard = cpuHand.get(randomIndex);

        // cpu play random card
        cpu.playCard(randomCard);
        // generate combinations
        this.createWinningCombinations(randomCard);
        // add played card to table
        this.playCardToTable(randomCard);
        // move to next phase
        this.playerMovePhase = 1;

    }

    public void handleCpuMovePhase1() {
        Player cpu = getNextPlayerMove();

        int max = this.allCombinations.size() - 1;
        int min = 0;

        int randomIndex = (int) (Math.random() * (max + 1));

        // no combinatinos can be won => move to next phase
        if(this.allCombinations.size() == 0) {
            this.playerMovePhase = 2;
            cpu.clearLastWonCards();
        } else {
            // pick a correct combination that cpu choose and remove those cards from table and add them to player's won cards
            // if cpu win all cards from table he gets one 'table point' which adds one total points
            ArrayList<Card> pickedCombinations = this.allCombinations.get(randomIndex);
            // give won cards to player
            cpu.clearLastWonCards();
            cpu.winCards(pickedCombinations);
            addPointsToPlayer(cpu);
            this.lastWinnerInRound = cpu;
            // remove won cards from table
            removeWonCardsFromTable(pickedCombinations);
            // check if player won all cards from table
            boolean tablePoint = checkTablePoint();
            if(tablePoint) {
                cpu.addTablePoint();
                addPointsToPlayer(cpu, 1);
                this.lastWinnerOfTablePoint = cpu;
            }
            // move to next phase
            this.playerMovePhase = 2;
        }
    }

    public boolean checkPlayersCurrentHand() {
        Player[] players = this.players;

        boolean bothCurrentHands = true;

        for(Player player: players) {
            if(player.getCurrentHand().size() > 0) {
                bothCurrentHands = false;
            }
        }

        return bothCurrentHands;
    }

    public boolean checkPlayingDeckIsEmpty() {
        if(this.deck.getDeck().size() == 0) {
            return true;
        }

        return false;
    }

    public ArrayList<Card> findPickedCombination(String allCombinationsInputIndex) {
        int inputIndex = Integer.valueOf(allCombinationsInputIndex);

        // return empty list if player choose to play card only
        if(inputIndex == -1) {
            return new ArrayList<Card>();
        }

        return allCombinations.get(inputIndex);
    }

    public void initializeCombinations() {
        this.mapCombinations.put("equals", new LinkedHashSet<>());
        this.mapCombinations.put("additions", new LinkedHashSet<>());
        this.mapCombinations.put("multiAdditions", new LinkedHashSet<>());
        this.mapCombinations.put("equalsCombined", new LinkedHashSet<>());
    }

    public void mergeMapCombinations(Card playedCard) {
        ArrayList<ArrayList<Card>> mergedList = new ArrayList<>();
        // merge 
        for(String combo: this.mapCombinations.keySet()) {
            for(Set<Card> currCombination: this.mapCombinations.get(combo)) {
                mergedList.add(new ArrayList<>(currCombination));
            }
            
        }

        // add played card to all merged
        for(ArrayList<Card> combo: mergedList) {
            combo.add(playedCard);
        } 

        // sort merged
        mergedList.sort((a,b) -> {
            return b.size() - a.size();
        });


        this.allCombinations = mergedList;

        System.out.println("all map combinations");
        System.out.println(allCombinations.toString());
    }

    // manages equal, addition, multiAddition and equalsCombined combinations
    public void addToCombinations(Set<Card> combination, String combinationId) {
        if(combinationId.equals("equals")) {
            Set<Set<Card>> equals = this.mapCombinations.get(combinationId);
            equals.add(combination);
        } else if(combinationId.equals("additions")) {
            Set<Set<Card>> additions = this.mapCombinations.get(combinationId);
            additions.add(combination);
        } else if(combinationId.equals("multiAdditions")) {
            Set<Set<Card>> multiAdditions = this.mapCombinations.get(combinationId);
            multiAdditions.add(combination);
        } else if(combinationId.equals("equalsCombined")) {
            Set<Set<Card>> equalsCombined = this.mapCombinations.get(combinationId);
            equalsCombined.add(combination);
        }
    }

    public void createWinningCombinations(Card playedCard) {
        // reset all combinations
        clearCombinations();

        // check for table card == played card
        for(int i = 0; i < this.currentTable.size(); i++) {
            if(this.currentTable.get(i).getValue() == playedCard.getValue()) {
                LinkedHashSet<Card> oneDuplicates = new LinkedHashSet<>();
                oneDuplicates.add(this.currentTable.get(i));
                this.addToCombinations(new LinkedHashSet<>(oneDuplicates), "equals");
            }
        }

        // check two table cards == played card (player playes K => he should be able to take up to three K-s, two from table and played card)
        for(int i = 0; i < this.currentTable.size(); i++) {
            // find same card values on table
            for(int j = 0; j < this.currentTable.size(); j++) {
                // skip same comparisons as values would be the same
                if(i == j) {
                    continue;
                }
                if(this.currentTable.get(i).getValue() == this.currentTable.get(j).getValue()) {
                    if(this.currentTable.get(i).getValue() == playedCard.getValue()) {
                        LinkedHashSet<Card> twoCard = new LinkedHashSet<>();
                        twoCard.add(this.currentTable.get(i));
                        twoCard.add(this.currentTable.get(j));
                        this.addToCombinations(twoCard, "equals");
                    }
                }
            }
        }

        // check three table cards == played card (player playes K => he should be able to take up to four K-s, three from table and played card)
        int threeDuplicates = 0;
        int[] threeDuplicatesTableIndexes = new int[3];
        for(int i = 0; i < currentTable.size(); i++) {
            if(playedCard.getValue() == currentTable.get(i).getValue()) {
                // save index positions from found duplicates
                threeDuplicatesTableIndexes[threeDuplicates] = i;
                threeDuplicates++;
            }
        }

        if(threeDuplicates == 3) {
            LinkedHashSet<Card> threeCard = new LinkedHashSet<>();
            for(int i = 0; i < 3; i++) {
                threeCard.add(currentTable.get(threeDuplicatesTableIndexes[i]));
            }
            
            this.addToCombinations(threeCard, "equals");
        }


        // generate combinations
        findAdditionCombinations(playedCard.getValue());
        findMultipleAdditionCombinations();
        findEqualsCombinedCombinations();
        mergeMapCombinations(playedCard);

        // System.out.println("EQUAL COMBINATIONS:");
        // System.out.println(this.equalCombinations);
        // System.out.println("ADDITION COMBINATIONS:");
        // System.out.println(this.additionCombinations);
        // System.out.println("MULTI ADDITION COMBINATIONS:");
        // System.out.println(this.multiAdditionCombinations);
        // System.out.println("EQUAL COMBINED COMBINATIONS:");
        // System.out.println(this.equalsCombinedCombinations);
        // System.out.println("ALL COMBINATIONS:");
        // System.out.println(this.allCombinations);
    }

    // generates all unique combinations where value of n cards == played card value
    public void findAdditionCombinations(int targetSum) {
        additionCombinationsRecursion(0, new ArrayList<>(), 0, targetSum);
    }

    public void additionCombinationsRecursion(int currentI, ArrayList<Card> currentCombination, int totalSum, int targetSum) {

        if(totalSum == targetSum) {
            if(currentCombination.size() > 1) {
                this.addToCombinations(new LinkedHashSet<>(currentCombination), "additions");
            }
            return;
        }

        if(currentI >= currentTable.size() || totalSum > targetSum) {
            return;
        }
        
        Card card = currentTable.get(currentI);
        currentCombination.add(card);

        additionCombinationsRecursion(currentI + 1, currentCombination, totalSum + card.getValue(), targetSum);
        currentCombination.remove(currentCombination.size() - 1);
        additionCombinationsRecursion(currentI + 1, currentCombination, totalSum, targetSum);
    }

    // generates all combinations of unique addition combinations: 
    // addition combinations: [5-c, 3-c], [5-c, 3-d], [5-d, 3-d], [5-d, 3-c] => [5-c, 3-c, 5-d, 3-d] + played card
    public void findMultipleAdditionCombinations() {
        int n = this.mapCombinations.get("additions").size();
        int subsetCount = 1 << n; // 2^n subsets

         ArrayList<Set<Card>> additionsList = new ArrayList<>(this.mapCombinations.get("additions"));

        for (int mask = 1; mask < subsetCount; mask++) {
            ArrayList<Card> merged = new ArrayList<>();
            boolean valid = true;

            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {

                    // check if this group overlaps with merged
                    for (Card c : additionsList.get(i)) {
                        if (merged.contains(c)) {
                            valid = false;
                            break;
                        }
                    }

                    if (!valid) break;

                    merged.addAll(additionsList.get(i));
                }
            }

            if (valid && Integer.bitCount(mask) > 1) {
                this.addToCombinations(new LinkedHashSet<>(merged), "multiAdditions");
            }
        }

    }

    // merges equals + additions and equals + multiadditions
    public void findEqualsCombinedCombinations() {
        Set<Set<Card>> equalsCopy = new LinkedHashSet<>(this.mapCombinations.get("equals"));
        Set<Set<Card>> additionsCopy = new LinkedHashSet<>(this.mapCombinations.get("additions"));
        Set<Set<Card>> multiAdditionsCopy = new LinkedHashSet<>(this.mapCombinations.get("multiAdditions"));

        for(Set<Card> equals: equalsCopy) {
            for(Set<Card> addition: additionsCopy) {
                Set<Card> additionEquals = new LinkedHashSet<>(addition);
                additionEquals.addAll(equals);
                this.addToCombinations(additionEquals, "equalsCombined");
            }

            for(Set<Card> multiAddition: multiAdditionsCopy) {
                Set<Card> multiAdditionEquals = new LinkedHashSet<>(multiAddition);
                multiAdditionEquals.addAll(equals);
                this.addToCombinations(multiAdditionEquals, "equalsCombined");
            }
        }

    }

    public void clearCombinations() {
        this.allCombinations.clear();
        this.initializeCombinations();
    }
}