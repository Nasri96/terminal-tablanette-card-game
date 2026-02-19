import java.util.ArrayList;

public class TerminalUICpu extends TerminalUI {
    
    public TerminalUICpu(Game game) {
        super(game);
    }


    public void processCpuInputs() {
            Player cpu = this.game.getCurrentPlayerMove();

            if(this.game.gamePhase == GamePhase.GAME_SETUP) {
                cpu.actionStart(this.game);
            }

            if(this.game.gamePhase == GamePhase.TURN_PLAY_CARD) {
                wait(500);
                System.out.println("");
                this.printTable();
                wait(500);
                System.out.println("------------------------ TURN_PLAY_CARD");
                wait(500);
                System.out.println(cpu.getName() + " is playing card...");
                wait(2000);

                cpu.actionPlayCard(this.game, null);
            }

            if(this.game.gamePhase == GamePhase.TURN_PICK_COMBINATION) {
                this.printTable();
                wait(500);
                System.out.println("------------------------ TURN_PICK_COMBINATION");
                wait(500);
                ArrayList<Card> table = game.getCurrentTable();
                System.out.println(cpu.getName() + " played card: " + table.get(table.size() - 1));
                System.out.println(cpu.getName() + " is picking card combination...");
                this.wait(2000);

                cpu.actionPickCombination(this.game, null);
            }

            if(this.game.gamePhase == GamePhase.TURN_RESOLVE) {
                this.wait(500);
                System.out.println("------------------------ TURN_RESOLVE");
                this.printWonCards();
                wait(500);
                this.printPointsAwarded();
                this.wait(1000);
                
                cpu.actionResolveTurn(game);
            }

            if(this.game.gamePhase == GamePhase.ROUND_END) {
                this.wait(500);
                System.out.println("======== ROUND_END ========");
                this.wait(1000);

                cpu.actionRoundEnd(game);
            }

            if(this.game.gamePhase == GamePhase.ROUND_START) {
                this.printLastWinnerInRound();
                this.wait(500);
                System.out.println("======== ROUND_START ========");
                this.wait(1000);
                System.out.println("--- SWITCHING PLAYERS ---");

                cpu.actionRoundStart(game);
            }

            if(this.game.gamePhase == GamePhase.NEXT_TURN) {
                this.wait(500);
                System.out.println("--- NEXT_TURN ---");
                this.wait(1000);

                cpu.actionNextTurn(game);
            }

            if(this.game.gamePhase == GamePhase.DEAL_CARDS) {
                game.ui.wait(500);
                System.out.println("--- DEAL_CARDS ---");
                game.ui.wait(1000);

                cpu.actionDealCards(game);
            }

            if(this.game.gamePhase == GamePhase.GAME_OVER) {
                // checks if game was over after the ROUND_END state
                if(this.game.getRoundChanged()) {
                    printLastWinnerInRound();
                }
                wait(500);
                System.out.println("--- GAME_OVER ---");
                wait(500);
                
                cpu.actionGameOver(game);
            }

            if(this.game.gamePhase == GamePhase.GAME_END) {
                wait(500);
                System.out.println("--- GAME_END ---");
                wait(500);
                printGameEnd();
                wait(2000);
                System.out.println("Ending the game...");
                
                cpu.actionGameEnd(game);
            }

    }
    
}
