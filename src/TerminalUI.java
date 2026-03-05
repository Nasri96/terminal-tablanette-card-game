import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class TerminalUI {
    private Scanner scanner;
    protected Game game;

    public TerminalUI(Game game) {
        this.scanner = new Scanner(System.in);
        this.game = game;
    }

    public void start() {
        TerminalUICpu uiCpu = new TerminalUICpu(game);
        

        while(true) {
            GameState gameState = this.game.getGameState();
            GamePhase gamePhase = gameState.getGamePhase();

            // if current player is cpu, process cpu inputs
            Player player = gameState.getCurrentPlayerMove();
            if(player instanceof PlayerCpu) {
                uiCpu.processCpuInputs();
                continue;
            }

            if(gamePhase == GamePhase.GAME_SETUP) {
                System.out.println("------------------------ GAME_SETUP");
                printMainMenu();

                String commandInput = this.scanner.nextLine();

                if(commandInput.equals("rules")) {
                    printRules();
                }

                if(commandInput.equals("start")) {
                    player.actionStart(this.game);
                }

            }

            if(gamePhase == GamePhase.TURN_PLAY_CARD) {
                printTable(gameState); 
                System.out.println("--- " + player.getName() + " on the move:");
                System.out.println("------------------------ TURN_PLAY_CARD");
                System.out.println("Play your card: (input needs to be same as it is shown in 'Your cards:')");
                String commandInput = this.scanner.nextLine();
                String playedCardIndex = validatePlayedCardInput(commandInput, gameState);
                if(!playedCardIndex.equals("invalid")) {
                    player.actionPlayCard(this.game, playedCardIndex);
                } else {
                    continue;
                }
            }

            if(gamePhase == GamePhase.TURN_PICK_COMBINATION) {
                List<Card> table = gameState.getCurrentTable();
                printTable(gameState);
                System.out.println("------------------------ TURN_PICK_COMBINATION");
                System.out.println("Played card: " + table.get(gameState.getCurrentTable().size() - 1));
                printAllCombinations(gameState);
                if(gameState.getAllCombinations().size() == 0) {
                    System.out.println("You can't win any cards, press enter to continue:");
                    String commandInput = this.scanner.nextLine();
                    player.actionPickCombination(this.game, null);

                } else {
                    while(true) {
                        System.out.println("Type the corresponding number that represent cards that u want to win:");

                        try {
                            int commandInput = Integer.valueOf(scanner.nextLine());

                            if(commandInput >= 1 && commandInput <= gameState.getAllCombinations().size()) {
                                // select index and pick combination
                                player.actionPickCombination(this.game, commandInput - 1);
                                break;
                            } else if(commandInput == -1) {
                                player.actionPickCombination(this.game, commandInput);
                                break;
                            }

                        } catch(NumberFormatException msg) {}
                    }
                }
            }

            if(gamePhase == GamePhase.TURN_RESOLVE) {
                wait(500);
                System.out.println("------------------------ TURN_RESOLVE");
                printWonCards(gameState);
                wait(500);
                printPointsAwarded(gameState);
                wait(1000);
                player.actionResolveTurn(game);
            }

            if(gamePhase == GamePhase.ROUND_END) {
                wait(500);
                System.out.println("======== ROUND_END ========");
                wait(1000);
                player.actionRoundEnd(game);
            }

            if(gamePhase == GamePhase.ROUND_START) {
                printLastWinnerInRound(gameState);
                wait(500);
                System.out.println("======== ROUND_START ========");
                wait(1000);
                System.out.println("--- SWITCHING PLAYERS ---");
                player.actionRoundStart(game);
            }

            if(gamePhase == GamePhase.NEXT_TURN) {
                wait(500);
                System.out.println("--- NEXT_TURN ---");
                wait(1000);
                player.actionNextTurn(game);
            }

            if(gamePhase == GamePhase.DEAL_CARDS) {
                wait(500);
                System.out.println("--- DEAL_CARDS ---");
                wait(1000);
                player.actionDealCards(game);
            }

            if(gamePhase == GamePhase.GAME_OVER) {
                // checks if game was over after the ROUND_END state
                if(gameState.getRoundChanged()) {
                    printLastWinnerInRound(gameState);
                }
                wait(500);
                System.out.println("--- GAME_OVER ---");
                wait(500);
                player.actionGameOver(game);
            }

            if(gamePhase == GamePhase.GAME_END) {
                wait(500);
                System.out.println("--- GAME_END ---");
                wait(500);
                printGameEnd(gameState);
                System.out.println("Type 'continue' to start new game");
                System.out.print("> ");
                validateTextInput("continue");
                player.actionGameEnd(game);
            }


        }
    }

    public void printPlayerHand(GameState gameState) {
        Player player = gameState.getCurrentPlayerMove();
        System.out.print(player.getCurrentHand());
    }

    public void printWonCards(GameState gameState) {
        Player player = gameState.getCurrentPlayerMove();
        if(player.getLastCardsWon().size() > 0) {
            System.out.println(player.getName() + " won cards: " + player.getLastCardsWon());
        } else {
            System.out.println(player.getName() + " did not win any cards.");
        }
    }

    public void printAllCombinations(GameState gameState) {
        if(gameState.getAllCombinations().size() > 0) {
            System.out.println("Pick card combination: ");
            for(int i = 0; i < gameState.getAllCombinations().size() + 1; i++) {
                // after last combination play card only
                if(i == gameState.getAllCombinations().size()) {
                    System.out.println(-1 +  ": Play card only");
                    break;
                }
                List<Card> currCombination = gameState.getAllCombinations().get(i);
                System.out.print(i + 1 + ": [ ");
                for(int j = 0; j < currCombination.size(); j++) {
                    if(currCombination.size() == 2) {
                        if(j == currCombination.size() - 1) {
                            System.out.print(currCombination.get(j) + "");
                        } else {
                            System.out.print(currCombination.get(j) + " = ");
                        }
                        
                    } else {
                        if(j == currCombination.size() - 1) {
                            System.out.print(currCombination.get(j) + "");
                        } else if(j == currCombination.size() - 2) {
                            System.out.print(currCombination.get(j) + " = ");
                        } else {
                            System.out.print(currCombination.get(j) + " + ");
                        }
                    }
                }
                System.out.print(" ]");
                System.out.println("");
            }
        }
        
    }

    public void printLastWinnerInRound(GameState state) {
        String lastWinnerInRoundId = state.getLastWinnerInRound();
        
        if(lastWinnerInRoundId != null) {
            Player lastWinner = state.findPlayerById(lastWinnerInRoundId);
            System.out.println(lastWinner.getName() + " carries the last cards from table " + lastWinner.getLastCardsWon());
            List<Card> wonCards = lastWinner.getLastCardsWon();
            int newPoints = 0;
            for(Card card: wonCards) {
                newPoints+= card.getPoints();
            }
            wait(1000);
            System.out.println(lastWinner.getName() + " received " + newPoints + " points");
        }
        wait(1000);
        String lastWinnerOfMoreCardsId = state.getLastWinnerOfMoreCards();
        if(lastWinnerOfMoreCardsId != null) {
            System.out.println(state.findPlayerById(lastWinnerOfMoreCardsId).getName() + " awarded 3 points for winning more cards");
        } else {
            System.out.println("Both players won equal number of cards. No points points awarded");
        }
        wait(500);
        for(Player player: state.getPlayersList()) {
            System.out.println(player.getName() + " has a total of " + player.getPointsWon() + " points");
            wait(500);
        }
        
    }

    public void printPointsAwarded(GameState state) {
        Player currentPlayer = state.getCurrentPlayerMove();
        String lastWinnerOfTablePointId = state.getLastWinnerOfTablePoint();

        int newPoints = 0;
        if(currentPlayer.getLastCardsWon().size() > 0) {
            // print won cards
            List<Card> wonCards = currentPlayer.getLastCardsWon();
            for(Card card: wonCards) {
                newPoints+= card.getPoints();
            }

            System.out.println(currentPlayer.getName() + " received " + newPoints + " points");
            wait(1000);
            // check if player scored table point
            if(lastWinnerOfTablePointId != null) {
                System.out.println(currentPlayer.getName() + " received " + 1 + " table point");
            }
            wait(1000);
            System.out.println(currentPlayer.getName() + " has a total of " + currentPlayer.getPointsWon() + " points");
        }
    }

    public void printGameEnd(GameState gameState) {
        Player winner = gameState.getGameOverPlayers().get("winner");
        Player loser = gameState.getGameOverPlayers().get("loser");
        System.out.println("Winner: " + winner.getName() + ", points won: " + winner.getPointsWon() + ", cards won: " + winner.getCardsWon());
        System.out.println("Loser: " + loser.getName() + ", points won: " + loser.getPointsWon() + ", cards won: " + loser.getCardsWon());
    }

    public void printMainMenu() {
        System.out.println("---- Tablanette Game ----");
        System.out.println(">> Start Game - type 'start'");
        System.out.println(">> Rules - type 'rules'");
        System.out.println(">> Quit - type 'quit'");
        System.out.println("Your command?");
    }

    public void printRules() {
        while(true) {
            System.out.println("-- Tablanette Game Rules --");
            System.out.println("- CARD VALUES:");
            System.out.println("King (K) = 14");
            System.out.println("Queen (Q) = 13");
            System.out.println("Jack (J) = 12");
            System.out.println("Rest of the cards are valued as their nominals.");
            System.out.println("Ace (A) is valued in a special way. Its value can be 1 or 11, depending on the assessment and desire of the player on the move.");
            System.out.println("");

            System.out.println("- GOAL OF THE GAME:");
            System.out.println("The goal of the game is to pick up as many cards from the talon as possible.");
            System.out.println("Cards are won by picking a card out of hand and using it to pick up the card that is of the same value; or multiple cards, the sum of which is equal to the value of the card thrown.");
            System.out.println("The cards are collected from each player to count the points at the end of the game.");


            System.out.println("type 'back' to go back");
            String command = this.scanner.nextLine();

            if(command.equals("back")) {
                break;
            }
        }
    }

    public String validatePlayedCardInput(String input, GameState gameState) {
        System.out.println("your input was > " + input);
        // check if input was correct and apply rules
        Player player = gameState.getCurrentPlayerMove();
        List<Card> playerHand = player.getCurrentHand();
        String[] cardInput = input.split("-"); // "split input like 5-d to 5 and d"
        
        if(cardInput.length != 2) {
            return "invalid";
        }

        for(int i = 0; i < playerHand.size(); i++) {
            Card nextCard = playerHand.get(i);
            if(nextCard.getSymbol().equals(cardInput[0]) && String.valueOf(nextCard.getSuit()).equals(cardInput[1])) {
                // System.out.println("SYMBOL: " + nextCard.getSymbol() + ", SUIT: " + nextCard.getSuit());
                return "" + i;
            }
        }

        return "invalid";
    }

    public void validateTextInput(String textToValidate) {
        String input = "";

        while(!input.equals(textToValidate)) {
            input = this.scanner.nextLine();
        }
    }

    public void printTable(GameState gameState) {
        int rows = 9;
        int cols = 40;
        List<List<Card>> cardRows = calcCardRows(gameState);
        // calc offset for current row
        int offSetStart = 0;
        int offSetRow = cardRows.size() / 2;
        

        String tableTitle = "TABLE";
        String gameStateTitle = "";
        Player player = gameState.getCurrentPlayerMove();
        System.out.println();
        if(!(player instanceof PlayerCpu)) {
            gameStateTitle = "Your cards:";
        }

        printEmptySpaces((cols / 2) - tableTitle.length() / 2);
        System.out.print("TABLE");
        printEmptySpaces(cols - gameStateTitle.length() / 2);
        System.out.print(gameStateTitle);
        System.out.println("");

        // print table and rest of the game state
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                // print empty table
                if(i != 0 && i != (rows - 1)) {
                    if(j == 0 || j == 1 || j == (cols - 2) || j == (cols - 1)) {
                        System.out.print("*");
                    } else {
                        printEmptySpaces(1);
                    }
                } else {
                    System.out.print("*");
                }

                // start printing cards at the middle of table and apply offset which is based on how many card rows are there
                if(cardRows.size() > 0 && i + offSetRow - offSetStart == rows / 2 && j == ((cols / 2) - (3 * cardRows.get(0).size()) / 2 - 1)) {
                    // remove one list from cardRows and print it
                    List<Card> cards = cardRows.remove(0);
                    int skippedCols = printCurrentTable(cards);
                    offSetStart++;
                    j+= skippedCols;
                }

                // after table print rest of game state
                if(i == 1 && j == cols -1) {
                    int lengthOfAllCards = getToStringLengthOfCards(gameState);
                    printEmptySpaces(cols / 2 - lengthOfAllCards / 2);
                    if(!(player instanceof PlayerCpu)) {
                        printPlayerHand(gameState);
                    }
                }
                
                

            }
            System.out.println("");
        }
    }

    public int getToStringLengthOfCards(GameState gameState) {
        List<Card> cards = gameState.getCurrentPlayerMove().getCurrentHand();
        int lengthOfAllCards = 0;
        for(Card card: cards) {
            lengthOfAllCards+= card.getToStringLength();
        }

        return lengthOfAllCards;
    }

    // every four cards create new array list for card rows
    public List<List<Card>> calcCardRows(GameState gameState) {
        List<Card> table = gameState.getCurrentTable();
        List<List<Card>> cardRows = new ArrayList<>();

        if(table.size() > 0) {
            int cardCurrRow = 0;
            List<Card> cards = new ArrayList<>();
            cardRows.add(cards);
            for(int i = 1; i <= table.size(); i++) {
                cardRows.get(cardCurrRow).add(table.get(i - 1));

                if(i % 4 == 0 && i < table.size()) {
                    cardCurrRow++;
                    cards = new ArrayList<>();
                    cardRows.add(cards);
                }
            }
        }

        return cardRows;
    }

    public int printCurrentTable(List<Card> cardsToPrint) {
        List<Card> table = this.game.getGameState().getCurrentTable();

        // one card is 3 spaces long
        int skippedCols = 0;

        for(Card card: cardsToPrint) {
            System.out.print(card + " ");
            int cardStringLength = card.getToStringLength();

            skippedCols+= cardStringLength + 1;
        }
        

        return skippedCols;
    }

    public void printEmptySpaces(int spaces) {
        for(int i = 0; i < spaces; i++) {
            System.out.print(" ");
        }
    }

    public void wait(int miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch(InterruptedException exception) {
            System.out.println("interrupted...");
        }
    }
}
