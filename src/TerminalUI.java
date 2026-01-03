import java.util.Scanner;
import java.util.ArrayList;

public class TerminalUI {
    private Scanner scanner;
    protected Game game;

    public TerminalUI(Game game) {
        this.scanner = new Scanner(System.in);
        this.game = game;
    }

    public void start() {
        TerminalUICpu uiCpu = new TerminalUICpu(game);

        while(this.game.gameState != GameState.GAME_OVER) {
            // if current player is cpu, process cpu inputs
            Player currentPlayer = this.game.getCurrentPlayerMove();
            if(currentPlayer instanceof PlayerCpu) {
                uiCpu.processCpuInputs();
                continue;
            }

            if(this.game.gameState == GameState.GAME_SETUP) {
                printMainMenu();

                String commandInput = this.scanner.nextLine();

                if(commandInput.equals("rules")) {
                    printRules();
                }

                if(commandInput.equals("start")) {
                    Player player = this.game.getCurrentPlayerMove();
                    player.actionStart(this.game);
                }
            }

            if(this.game.gameState == GameState.TURN_PLAY_CARD) {
                Player player = this.game.getCurrentPlayerMove();
                printTable(); 
                System.out.println("------------------------");
                System.out.println("Play your card: (input needs to be same as it is shown in 'Your cards:')");
                String commandInput = this.scanner.nextLine();
                String playedCardIndex = validatePlayedCardInput(commandInput);
                if(!playedCardIndex.equals("invalid")) {
                    player.actionPlayCard(this.game, playedCardIndex);
                } else {
                    continue;
                }
            }

            if(this.game.gameState == GameState.TURN_PICK_COMBINATION) {
                Player player = this.game.getCurrentPlayerMove();
                ArrayList<Card> table = this.game.getCurrentTable();
                printTable();
                System.out.println("------------------------");
                System.out.println("Played card: " + table.get(this.game.getCurrentTable().size() - 1));
                printAllCombinations();
                if(this.game.getAllCombinations().size() == 0) {
                    System.out.println("You can't win any cards, press enter to continue:");
                    String commandInput = this.scanner.nextLine();
                    player.actionPickCombination(this.game, null);

                } else {
                    while(true) {
                        System.out.println("Type the corresponding number that represent cards that u want to win:");

                        try {
                            int commandInput = Integer.valueOf(scanner.nextLine());

                            if(commandInput >= 1 && commandInput <= this.game.getAllCombinations().size()) {
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

            if(this.game.gameState == GameState.TURN_RESOLVE) {
                wait(500);
                Player player = this.game.getCurrentPlayerMove();
                printWonCards();
                printPointsAwarded();
                wait(1000);
                player.actionResolveTurn(game);
            }

            if(this.game.gameState == GameState.ROUND_END) {
                wait(500);
                System.out.println("======== END OF THE ROUND ========");
                wait(1000);
                Player player = this.game.getCurrentPlayerMove();
                player.actionRoundEnd(game);
            }

            if(this.game.gameState == GameState.ROUND_START) {
                printLastWinnerInRound();
                wait(500);
                System.out.println("======== START OF THE ROUND ========");
                wait(1000);
                Player player = this.game.getCurrentPlayerMove();
                player.actionRoundStart(game);
            }

            if(this.game.gameState == GameState.NEXT_TURN) {
                wait(500);
                System.out.println("--- SWITCHING PLAYERS ---");
                wait(1000);
                Player player = this.game.getCurrentPlayerMove();
                player.actionNextTurn(game);
            }

            if(this.game.gameState == GameState.DEAL_CARDS) {
                wait(500);
                System.out.println("--- DEALING CARDS TO PLAYERS ---");
                wait(1000);
                Player player = this.game.getCurrentPlayerMove();
                player.actionDealCards(game);
            }


        }
    }

    public void printPlayerHand() {
        Player player = this.game.getCurrentPlayerMove();
        System.out.print(player.getCurrentHand());
    }

    public void printWonCards() {
        Player player = this.game.getCurrentPlayerMove();
        if(player.getLastCardsWon().size() > 0) {
            System.out.println(player.getType() + " won cards: " + player.getLastCardsWon());
        } else {
            System.out.println(player.getType() + " did not win any cards.");
        }
    }

    public void printAllCombinations() {
        if(this.game.getAllCombinations().size() > 0) {
            System.out.println("Pick card combination: ");
            for(int i = 0; i < this.game.getAllCombinations().size() + 1; i++) {
                // after last combination play card only
                if(i == this.game.getAllCombinations().size()) {
                    System.out.println(-1 +  ": Play card only");
                    break;
                }
                ArrayList<Card> currCombination = this.game.getAllCombinations().get(i);
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

    public void printLastWinnerInRound() {
        Player lastWinner = this.game.getLastWinnerInRound();
        
        int newPoints = 0;
        if(lastWinner != null) {
            System.out.println(lastWinner.getType() + " carries the last cards from table " + lastWinner.getLastCardsWon());
            ArrayList<Card> wonCards = lastWinner.getLastCardsWon();
            for(Card card: wonCards) {
                newPoints+= card.getPoints();
            }
            wait(1000);
            System.out.println(lastWinner.getType() + " received " + newPoints + " points");
        }
        wait(1000);
        Player lastWinnerOfMoreCards = this.game.getLastWinnerOfMoreCards();
        if(lastWinnerOfMoreCards != null) {
            System.out.println(lastWinnerOfMoreCards.getType() + " awarded 3 points for winning more cards");
        }
        wait(500);
        System.out.println(lastWinner.getType() + " has a total of " + lastWinner.getPointsWon() + " points");
    }

    public void printPointsAwarded() {
        Player lastTurnWinner = this.game.getCurrentPlayerMove();
        Player lastWinnerOfTablePoint = this.game.getLastWinnerOfTablePoint();

        int newPoints = 0;
        if(lastTurnWinner.getLastCardsWon().size() > 0) {
            // print won cards
            ArrayList<Card> wonCards = lastTurnWinner.getLastCardsWon();
            for(Card card: wonCards) {
                newPoints+= card.getPoints();
            }

            System.out.println(lastTurnWinner.getType() + " received " + newPoints + " points");
            wait(1000);
            // check if player scored table point
            if(lastWinnerOfTablePoint != null) {
                System.out.println(lastWinnerOfTablePoint.getType() + " received " + 1 + " table point");
            }
            wait(1000);
            System.out.println(lastTurnWinner.getType() + " has a total of " + lastTurnWinner.getPointsWon() + " points");
        }
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

    public String validatePlayedCardInput(String input) {
        System.out.println("your input was > " + input);
        // check if input was correct and apply rules
        Player player = this.game.getCurrentPlayerMove();
        ArrayList<Card> playerHand = player.getCurrentHand();
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

    public void printTable() {
        int rows = 9;
        int cols = 40;
        ArrayList<ArrayList<Card>> cardRows = calcCardRows();
        // calc offset for current row
        int offSetStart = 0;
        int offSetRow = cardRows.size() / 2;
        

        String tableTitle = "TABLE";
        String gameStateTitle = "";
        Player player = this.game.getCurrentPlayerMove();
        if(!player.getType().equals("cpu")) {
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
                    ArrayList<Card> cards = cardRows.remove(0);
                    int skippedCols = printCurrentTable(cards);
                    offSetStart++;
                    j+= skippedCols;
                }

                // after table print rest of game state
                if(i == 1 && j == cols -1) {
                    int lengthOfAllCards = getToStringLengthOfCards(this.game.getCurrentPlayerMove().getCurrentHand());
                    printEmptySpaces(cols / 2 - lengthOfAllCards / 2);
                    if(!player.getType().equals("cpu")) {
                        printPlayerHand();
                    }
                }
                
                

            }
            System.out.println("");
        }
    }

    public int getToStringLengthOfCards(ArrayList<Card> cards) {
        int lengthOfAllCards = 0;
        for(Card card: this.game.getCurrentPlayerMove().getCurrentHand()) {
            lengthOfAllCards+= card.getToStringLength();
        }

        return lengthOfAllCards;
    }

    // every four cards create new array list for card rows
    public ArrayList<ArrayList<Card>> calcCardRows() {
        ArrayList<Card> currentTableCopy = new ArrayList<>(this.game.getCurrentTable());
        ArrayList<ArrayList<Card>> cardRows = new ArrayList<>();

        if(currentTableCopy.size() > 0) {
            int cardCurrRow = 0;
            ArrayList<Card> cards = new ArrayList<>();
            cardRows.add(cards);
            for(int i = 1; i <= currentTableCopy.size(); i++) {
                cardRows.get(cardCurrRow).add(currentTableCopy.get(i - 1));

                if(i % 4 == 0 && i < currentTableCopy.size()) {
                    cardCurrRow++;
                    cards = new ArrayList<>();
                    cardRows.add(cards);
                }
            }
        }

        return cardRows;
    }

    public int printCurrentTable(ArrayList<Card> cardsToPrint) {
        ArrayList<Card> table = this.game.getCurrentTable();

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
