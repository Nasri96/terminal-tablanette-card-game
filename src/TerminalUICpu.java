import java.util.ArrayList;
import java.util.List;

public class TerminalUICpu extends TerminalUI {
    
    public TerminalUICpu(Game game, TerminalUIPhase phase) {
        super(game);
        this.phase = phase;
    }


    protected TerminalUIPhase processCpuInputs() {
            GameState state = this.game.getGameState();
            GamePhase gamePhase = this.game.getGamePhase();
            Player cpu = state.getCurrentPlayerMove();

            if(gamePhase == GamePhase.GAME_SETUP) {
                this.actionController.dispatch(cpu.getId(), GameAction.START, null);
                // cpu.actionStart(this.game);
            }

            if(gamePhase == GamePhase.TURN_PLAY_CARD) {
                wait(500);
                System.out.println("");
                this.printTable(state);
                wait(500);
                System.out.println("------------------------ TURN_PLAY_CARD");
                wait(500);
                System.out.println(cpu.getName() + " is playing card...");
                wait(2000);

                this.actionController.dispatch(cpu.getId(), GameAction.PLAY_CARD, payloadPlayCard(state));
                // cpu.actionPlayCard(this.game, null);
            }

            if(gamePhase == GamePhase.TURN_PICK_COMBINATION) {
                this.printTable(state);
                wait(500);
                System.out.println("------------------------ TURN_PICK_COMBINATION");
                wait(500);
                List<Card> table = state.getCurrentTable();
                System.out.println(cpu.getName() + " played card: " + table.get(table.size() - 1));
                System.out.println(cpu.getName() + " is picking card combination...");
                this.wait(2000);

                this.actionController.dispatch(cpu.getId(), GameAction.PICK_COMBINATION, payloadPickCombination(state));
            }

            if(gamePhase == GamePhase.TURN_RESOLVE) {
                this.wait(500);
                System.out.println("------------------------ TURN_RESOLVE");
                this.printWonCards(state);
                wait(500);
                this.printPointsAwarded(state);
                this.wait(1000);
                
                this.actionController.dispatch(cpu.getId(), GameAction.CONTINUE, null);
            }

            if(gamePhase == GamePhase.ROUND_END) {
                this.wait(500);
                System.out.println("======== ROUND_END ========");
                this.wait(1000);

                this.actionController.dispatch(cpu.getId(), GameAction.CONTINUE, null);
            }

            if(gamePhase == GamePhase.ROUND_START) {
                this.printLastWinnerInRound(state);
                this.wait(500);
                System.out.println("======== ROUND_START ========");
                this.wait(1000);
                System.out.println("--- SWITCHING PLAYERS ---");

                this.actionController.dispatch(cpu.getId(), GameAction.CONTINUE, null);
            }

            if(gamePhase == GamePhase.NEXT_TURN) {
                this.wait(500);
                System.out.println("--- NEXT_TURN ---");
                this.wait(1000);

                this.actionController.dispatch(cpu.getId(), GameAction.CONTINUE, null);
            }

            if(gamePhase == GamePhase.DEAL_CARDS) {
                game.ui.wait(500);
                System.out.println("--- DEAL_CARDS ---");
                game.ui.wait(1000);

                this.actionController.dispatch(cpu.getId(), GameAction.CONTINUE, null);
            }

            if(gamePhase == GamePhase.GAME_OVER) {
                wait(500);
                System.out.println("--- GAME_OVER ---");
                wait(500);
                
                this.actionController.dispatch(cpu.getId(), GameAction.CONTINUE, null);
            }

            if(gamePhase == GamePhase.GAME_END) {
                printLastWinnerInRound(state);
                wait(500);
                System.out.println("--- GAME_END ---");
                wait(500);
                printGameEnd(state);
                System.out.println("Type 'continue' to start new game");
                System.out.print("> ");
                validateTextInput("continue");
                return TerminalUIPhase.MAIN_MENU;
            }

            return this.phase;

    }

    private int payloadPlayCard(GameState state) {
        Player cpu = state.getCurrentPlayerMove();
        List<Card> cpuHand = cpu.getCurrentHand();
        int max = cpuHand.size() - 1;
        int min = 0;

        int randomIndex = (int) (Math.random() * (max + 1));


        return randomIndex;
    }

    private Integer payloadPickCombination(GameState state) {
        List<List<Card>> combinations = new ArrayList<>(state.getAllCombinations());

        // no combinations to pick
        if(combinations.size() == 0) {
            return null;
        } 
        // one combination only
        else if(combinations.size() == 1) {
            return 0;
        }
        // pick combination which has highest points 
        else {
            combinations.sort((a,b) -> {
                // get total points of a and b (card.getPoints())
                int aTotal = 0;
                int bTotal = 0;
                for(Card currentCard: a) {
                    aTotal+= currentCard.getPoints();
                }

                for(Card currentCard: b) {
                    bTotal+= currentCard.getPoints();
                }

                return bTotal - aTotal;
            });

            List<Card> highestPointCombination = combinations.get(0);
            int indexOfHighestPoint = state.getAllCombinations().indexOf(highestPointCombination);

            return indexOfHighestPoint;
        }
    }
    
}
