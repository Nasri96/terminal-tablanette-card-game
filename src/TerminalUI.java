import java.util.Scanner;
import java.util.ArrayList;

public class TerminalUI {
    private Scanner scanner;
    private Game game;

    public TerminalUI(Game game) {
        this.scanner = new Scanner(System.in);
        this.game = game;
    }

    public void start() {
        while(!this.game.getState().equals("game-over")) {
            while(this.game.getState().equals("main-menu")) {
                printMainMenu();
                String commandInput = this.scanner.nextLine();

                if(commandInput.equals("quit")) {
                    this.game.getInputFromUI(commandInput);
                }

                if(commandInput.equals("rules")) {
                    printRules();
                }

                if(commandInput.equals("start")) {
                    this.game.getInputFromUI(commandInput);
                }
            }

            while(this.game.getState().equals("player-move")) {
                if(this.game.getPlayerMovePhase() == 0) {
                    Player player = this.game.getNextPlayerMove();
                    if(player.getType().equals("player")) {
                        System.out.println("");
                        printPlayerHand();
                        ArrayList<Card> table = this.game.getCurrentTable();
                        System.out.println("TABLE:");
                        for(Card card: table) {
                            System.out.println(card);
                        }
                        
                        System.out.println("Play your card: (input needs to be same as it is shown in your current hand)");
                        String commandInput = this.scanner.nextLine();

                        if(commandInput.equals("quit")) {
                            this.game.getInputFromUI(commandInput);
                        }

                        if(commandInput.equals("back")) {
                            this.game.getInputFromUI("main-menu");
                        }

                        String playedCardIndex = validatePlayedCardInput(commandInput);
                        if(!playedCardIndex.equals("invalid")) {
                            this.game.getInputFromUI(playedCardIndex);
                        } else {
                            continue;
                        }
                    } else if(player.getType().equals("cpu")) {
                        System.out.println("");
                        printPlayerHand();
                        ArrayList<Card> table = this.game.getCurrentTable();
                        System.out.println("TABLE:");
                        for(Card card: table) {
                            System.out.println(card);
                        }

                        System.out.println("CPU is thinking which card to play...");
                        try {
                            Thread.sleep(1500);
                        } catch(InterruptedException exception) {
                            System.out.println("interrupted...");
                        }
                        this.game.getInputFromUI("cpu-move");
                        
                    }

                } else if(this.game.getPlayerMovePhase() == 1) {
                    Player player = this.game.getNextPlayerMove();
                    if(player.getType().equals("player")) {
                        System.out.println("");
                        printPlayerHand();
                        ArrayList<Card> table = this.game.getCurrentTable();
                        System.out.println("TABLE:");
                        for(int i = 0; i < table.size(); i++) {
                            if(i == table.size() - 1) {
                                System.out.println("played card: " + table.get(i));
                            } else {
                                System.out.println(table.get(i));
                            }
                        }

                        printAllCombinations();
                        if(this.game.getAllCombinations().size() == 0) {
                            System.out.println("You can't win any cards, press enter to continue:");
                            String commandInput = this.scanner.nextLine();

                            if(commandInput.equals("quit")) {
                                this.game.getInputFromUI(commandInput);
                            }

                            if(commandInput.equals("back")) {
                                this.game.getInputFromUI("main-menu");
                            }

                            this.game.getInputFromUI("");

                        } else {
                            while(true) {
                                System.out.println("Type the corresponding number that represent cards that u want to win:");

                                try {
                                    int commandInput = Integer.valueOf(scanner.nextLine());

                                    if(commandInput >= 1 && commandInput <= this.game.getAllCombinations().size()) {
                                        this.game.getInputFromUI(String.valueOf(commandInput - 1));
                                        break;
                                    } 

                                } catch(NumberFormatException msg) {}
                            }
                        }
                    } else if(player.getType().equals("cpu")) {
                        System.out.println("");
                        printPlayerHand();
                        ArrayList<Card> table = this.game.getCurrentTable();
                        System.out.println("TABLE:");
                        for(int i = 0; i < table.size(); i++) {
                            if(i == table.size() - 1) {
                                System.out.println("cpu played card: " + table.get(i));
                            } else {
                                System.out.println(table.get(i));
                            }
                        }
                        
                        System.out.println("cpu is picking combination...");
                        try {
                            Thread.sleep(2000);
                        } catch(InterruptedException exception) {
                            System.out.println("interrupted...");
                        }
                        this.game.getInputFromUI("cpu-move");
                    }
                    
                    
                } else if(this.game.getPlayerMovePhase() == 2) {
                    printWonCards();
                    printPointsAwarded();
                    try {
                        Thread.sleep(3000);
                    } catch(InterruptedException exception) {
                        System.out.println("interrupted...");
                    }
                    this.game.getInputFromUI("next-turn");
                }
            }
            while(this.game.getState().equals("next-deal-of-cards")) {
                System.out.println("Dealing cards to players...");
                try {
                    Thread.sleep(2000);
                } catch(InterruptedException exception) {
                    System.out.println("interrupted...");
                }

                this.game.getInputFromUI("player-move");
            }
            while(this.game.getState().equals("next-round")) {
                printLastWinnerInRound();
                System.out.println("======== END OF THE ROUND ========");
                try {
                    Thread.sleep(1000);
                } catch(InterruptedException exception) {
                    System.out.println("interrupted...");
                }
                this.game.getInputFromUI("player-move");
            }
            
        }
    }

    public void printPlayerHand() {
        Player player = this.game.getNextPlayerMove();
        System.out.println(player.getType() + " HAND CARDS: ");
        for(Card card: player.getCurrentHand()) {
            System.out.println(card);
        }
    }

    public void printWonCards() {
        Player player = this.game.getNextPlayerMove();
        if(player.getLastCardsWon().size() > 0) {
            System.out.println(player.getType() + " won cards: " + player.getLastCardsWon());
        } else {
            System.out.println(player.getType() + " did not win any cards.");
        }
    }

    public void printAllCombinations() {
        System.out.println("combinations: ");
        for(int i = 0; i < this.game.getAllCombinations().size(); i++) {
            System.out.print(i + 1 + ": [ ");
            for(int j = 0; j < this.game.getAllCombinations().get(i).size(); j++) {
                if(j == this.game.getAllCombinations().get(i).size() - 1) {
                    System.out.print(" = " + this.game.getAllCombinations().get(i).get(j));
                } else {
                    if(this.game.getAllCombinations().get(i).size() > 2) {
                        System.out.print(this.game.getAllCombinations().get(i).get(j) + " + ");
                    } else {
                        System.out.print(this.game.getAllCombinations().get(i).get(j) + "");
                    }
                    
                }
            }
            System.out.print(" ]");
            System.out.println("");
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
            try {
                Thread.sleep(1000);
            } catch(InterruptedException exception) {
                System.out.println("interrupted...");
            }
            System.out.println(lastWinner.getType() + " received " + newPoints + " points");
        }
        try {
            Thread.sleep(1000);
        } catch(InterruptedException exception) {
            System.out.println("interrupted...");
        }

        Player lastWinnerOfMoreCards = this.game.getLastWinnerOfMoreCards();
        if(lastWinnerOfMoreCards != null) {
            System.out.println(lastWinnerOfMoreCards.getType() + " awarded 3 points for winning more cards");
        }
        

    }

    public void printPointsAwarded() {
        Player lastTurnWinner = this.game.getNextPlayerMove();
        int newPoints = 0;
        if(lastTurnWinner.getLastCardsWon().size() > 0) {
            ArrayList<Card> wonCards = lastTurnWinner.getLastCardsWon();
            for(Card card: wonCards) {
                newPoints+= card.getPoints();
            }

            System.out.println(lastTurnWinner.getType() + " received " + newPoints + " points");
            try {
                Thread.sleep(1000);
            } catch(InterruptedException exception) {
                System.out.println("interrupted...");
            }
            System.out.println(lastTurnWinner.getType() + " has a total of " + lastTurnWinner.getPointsWon() + " points");
            try {
                Thread.sleep(1000);
            } catch(InterruptedException exception) {
                System.out.println("interrupted...");
            }
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
        Player player = this.game.getNextPlayerMove();
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
        System.out.println("Your hand: ");
        printCardTable(7, 40);
        

    }

    public void printCardTable(int rows, int cols) {
        printEmptySpaces((cols / 2) - 2);
        System.out.println("TABLE");
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                if(i != 0 && i != (rows - 1)) {
                    if(j == 0 || j == 1 || j == (cols - 2) || j == (cols - 1)) {
                        System.out.print("*");
                    } else {
                        printEmptySpaces(1);
                    }
                } else {
                    System.out.print("*");
                }
            }
            System.out.println("");
        }
    }

    public void printEmptySpaces(int spaces) {
        for(int i = 0; i < spaces; i++) {
            System.out.print(" ");
        }
    }
}
