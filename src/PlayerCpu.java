import java.util.ArrayList;

public class PlayerCpu extends Player {
    
    public PlayerCpu(String type) {
        super(type);
    }


    public void actionStart(Game game) {
        super.actionStart(game);
    }

    public void actionPlayCard(Game game, Object payload) {
        ArrayList<Card> cpuHand = this.getCurrentHand();
        int max = cpuHand.size() - 1;
        int min = 0;

        int randomIndex = (int) (Math.random() * (max + 1));


        super.actionPlayCard(game, randomIndex);
    }

    public void actionPickCombination(Game game, Object payload) {
        ArrayList<ArrayList<Card>> combinations = new ArrayList<>(game.getAllCombinations());

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

            ArrayList<Card> highestPointCombination = combinations.get(0);
            int indexOfHighestPoint = game.getAllCombinations().indexOf(highestPointCombination);

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

}
