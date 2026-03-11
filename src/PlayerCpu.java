import java.util.ArrayList;
import java.util.List;

public class PlayerCpu extends Player {
    
    public PlayerCpu(String id, String name, List<Card> cardsWon, List<Card> lastCardsWon, List<Card> currentHand, int pointsWon, int tablePointsWon, boolean isCpu) {
        super(id, name, cardsWon, lastCardsWon, currentHand, pointsWon, tablePointsWon, isCpu);
    }

    public static PlayerCpu initial(String id, String name, boolean isCpu) {
        return new PlayerCpu(id, name, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0, 0, isCpu);
    }


    public void actionStart(Game game) {
        super.actionStart(game);
    }

    public void actionPlayCard(Game game, Object payload) {
        List<Card> cpuHand = this.getCurrentHand();
        int max = cpuHand.size() - 1;
        int min = 0;

        int randomIndex = (int) (Math.random() * (max + 1));


        super.actionPlayCard(game, randomIndex);
    }

    public void actionPickCombination(Game game, Object payload) {
        GameState gameState = game.getGameState();
        List<List<Card>> combinations = new ArrayList<>(gameState.getAllCombinations());

        // no combinations to pick
        if(combinations.size() == 0) {
            super.actionPickCombination(game, null);
        } 
        // one combination only
        else if(combinations.size() == 1) {
            super.actionPickCombination(game, "0");
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
            int indexOfHighestPoint = gameState.getAllCombinations().indexOf(highestPointCombination);

            super.actionPickCombination(game, indexOfHighestPoint);
        }
        
    }

    public void actionResolveTurn(Game game) {
        super.actionResolveTurn(game);
    }

    public void actionRoundEnd(Game game) {
        super.actionRoundEnd(game);
    }

    public void actionRoundStart(Game game) {
        super.actionRoundStart(game);
    }

    public void actionNextTurn(Game game) {
        super.actionNextTurn(game);
    }

    public void actionDealCards(Game game) {
        super.actionDealCards(game);
    }

    public void actionGameOver(Game game) {
        super.actionGameOver(game);
    }

    public void actionGameEnd(Game game) {
        super.actionGameEnd(game);
    }

}
