public class GameInput {
    public final String id;
    public final GameAction action;
    public final Object payload;


    public GameInput(String id, GameAction action, Object payload) {
        this.id = id;
        this.action = action;
        this.payload = payload;
    }

    public String toString() {
        return 
        """
        id: %s,
        action: %s,
        payload: %s
        """.formatted(id, action, payload);
    }

}
