public class GameActionController {
    private final Game game;

    public GameActionController(Game game) {
        this.game = game;
    }


    public GameState dispatch(String id, GameAction action, Object payload) {
        try {
            switch (action) {
                case PLAY_CARD:
                    return  game.updateGame(new GameInput<>(id, action, parsePlayCard(payload)));
                case PICK_COMBINATION:
                    return game.updateGame(new GameInput<>(id, action, parsePickCombination(payload)));
                default:
                    return game.updateGame(new GameInput<>(id, action, null));
            }
        } catch(Exception e) {
            System.out.println(e);
            return this.game.getGameState();
        }
        
    }

    private int parsePlayCard(Object payload) {
        if(!(payload instanceof Integer)) {
            throw new IllegalArgumentException("payload at parsePlayCard did not receive Integer");
        }

        return (Integer) payload;
    }

    private Integer parsePickCombination(Object payload) {
        if(payload == null) {
            return null;
        }

        if(!(payload instanceof Integer)) {
            throw new IllegalArgumentException("payload at parsePickCombination did not receive Integer");
        }

        return (Integer) payload;
    }

    
 }
