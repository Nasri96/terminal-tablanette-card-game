public class GameInput {
    public GameAction action;
    public Object payload;


    public GameInput(GameAction action, Object payload) {
        this.action = action;
        this.payload = payload;
    }

}
