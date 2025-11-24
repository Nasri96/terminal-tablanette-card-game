import java.util.Random;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {

        Player humanPlayer = new Player("player");
        Player cpu = new Player("cpu");
        Player[] players = { humanPlayer, cpu };

        
        Game game = new Game(new CardDeck(), players);
        TerminalUI ui = new TerminalUI(game);

        game.startGame(ui);


        // Scanner scanner = new Scanner(System.in);
        // if(scanner.hasNextInt()) {
        //     int myInt = scanner.nextInt();
        //     System.out.println("its next integer!" + myInt);
        // }
    }
}
