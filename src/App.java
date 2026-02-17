import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        Player humanPlayer = new Player("player");
        Player humanPlayer2 = new Player("player");
        PlayerCpu cpu = new PlayerCpu("cpu");
        PlayerCpu cpu2 = new PlayerCpu("cpu");
        Player[] players = { humanPlayer, cpu2 };


        Game game = new Game(new CardDeck(), players);
        TerminalUI ui = new TerminalUI(game);
        game.setUi(ui);

        
        ui.start();
    }
}
