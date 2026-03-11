import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        Player humanPlayer =  Player.initial("p1", "player1", false);
        Player humanPlayer2 = Player.initial("p2", "player2", false);
        
        List<Player> playerList = new ArrayList<>();
        playerList.add(humanPlayer);
        playerList.add(humanPlayer2);
        Player[] players = { humanPlayer, humanPlayer2 };

        Game game = new Game();
        TerminalUI ui = new TerminalUI(game);
        game.setUi(ui);

        
        ui.start();
    }
}
