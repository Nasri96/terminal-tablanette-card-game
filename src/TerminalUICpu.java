import java.util.ArrayList;

public class TerminalUICpu extends TerminalUI {
    
    public TerminalUICpu(Game game) {
        super(game);
    }


    public void processCpuInputs() {
            Player cpu = this.game.getCurrentPlayerMove();

            if(this.game.gameState == GameState.GAME_SETUP) {
                cpu.actionStart(this.game);
            }

            if(this.game.gameState == GameState.TURN_PLAY_CARD) {
                System.out.println("");
                this.printTable();
                this.wait(500);
                System.out.println("------------------------");
                System.out.println("CPU is playing card...");
                this.game.ui.wait(1500);

                cpu.actionPlayCard(this.game, null);
            }

            if(this.game.gameState == GameState.TURN_PICK_COMBINATION) {
                this.printTable();
                System.out.println("------------------------");
                ArrayList<Card> table = game.getCurrentTable();
                System.out.println("CPU played card: " + table.get(table.size() - 1));
                System.out.println("CPU is picking card combination...");
                this.wait(2000);

                cpu.actionPickCombination(this.game, null);
            }

            if(this.game.gameState == GameState.TURN_RESOLVE) {
                this.wait(500);
                this.printWonCards();
                this.printPointsAwarded();
                this.wait(1000);
                
                cpu.actionResolveTurn(game);
            }

            if(this.game.gameState == GameState.ROUND_END) {
                this.wait(500);
                System.out.println("======== END OF THE ROUND ========");
                this.wait(1000);

                cpu.actionRoundEnd(game);
            }

            if(this.game.gameState == GameState.ROUND_START) {
                this.printLastWinnerInRound();
                this.wait(500);
                System.out.println("======== START OF THE ROUND ========");
                this.wait(1000);

                cpu.actionRoundStart(game);
            }

            if(this.game.gameState == GameState.NEXT_TURN) {
                this.wait(500);
                System.out.println("--- SWITCHING PLAYERS ---");
                this.wait(1000);

                cpu.actionNextTurn(game);
            }

            if(this.game.gameState == GameState.DEAL_CARDS) {
                game.ui.wait(500);
                System.out.println("--- DEALING CARDS TO PLAYERS ---");
                game.ui.wait(1000);

                cpu.actionDealCards(game);
            }

    }
    
}
