import java.util.ArrayList;
import java.util.List;

public class TerminalUICpu extends TerminalUI {
    
    public TerminalUICpu(Game game) {
        super(game);
    }


    public void processCpuInputs() {
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

                this.actionController.dispatch(cpu.getId(), GameAction.PLAY_CARD, payloadPlayCard(state));
                //cpu.actionPickCombination(this.game, null);
            }

            if(gamePhase == GamePhase.TURN_RESOLVE) {
                this.wait(500);
                System.out.println("------------------------ TURN_RESOLVE");
                this.printWonCards(state);
                wait(500);
                this.printPointsAwarded(state);
                this.wait(1000);
                
                cpu.actionResolveTurn(game);
            }

            if(gamePhase == GamePhase.ROUND_END) {
                this.wait(500);
                System.out.println("======== ROUND_END ========");
                this.wait(1000);

                cpu.actionRoundEnd(game);
            }

            if(gamePhase == GamePhase.ROUND_START) {
                this.printLastWinnerInRound(state);
                this.wait(500);
                System.out.println("======== ROUND_START ========");
                this.wait(1000);
                System.out.println("--- SWITCHING PLAYERS ---");

                cpu.actionRoundStart(game);
            }

            if(gamePhase == GamePhase.NEXT_TURN) {
                this.wait(500);
                System.out.println("--- NEXT_TURN ---");
                this.wait(1000);

                cpu.actionNextTurn(game);
            }

            if(gamePhase == GamePhase.DEAL_CARDS) {
                game.ui.wait(500);
                System.out.println("--- DEAL_CARDS ---");
                game.ui.wait(1000);

                cpu.actionDealCards(game);
            }

            if(gamePhase == GamePhase.GAME_OVER) {
                // checks if game was over after the ROUND_END state
                if(state.getRoundChanged()) {
                    printLastWinnerInRound(state);
                }
                wait(500);
                System.out.println("--- GAME_OVER ---");
                wait(500);
                
                cpu.actionGameOver(game);
            }

            if(gamePhase == GamePhase.GAME_END) {
                wait(500);
                System.out.println("--- GAME_END ---");
                wait(500);
                printGameEnd(state);
                wait(2000);
                System.out.println("Ending the game...");
                
                cpu.actionGameEnd(game);
            }

    }

    private int payloadPlayCard(GameState state) {
        Player cpu = state.getCurrentPlayerMove();
        List<Card> cpuHand = cpu.getCurrentHand();
        int max = cpuHand.size() - 1;
        int min = 0;

        int randomIndex = (int) (Math.random() * (max + 1));


        return randomIndex;
    }
    
}
