package t3.core;

/**
 * Public method of any board based game where stones are not to be moved.
 */
public interface GameEngine {


    /**
     * Put a stone to a spot denoted by two coordinates.
     * @param player
     * @param x
     * @param y
     */
    void playerMove(int player, int x, int y) throws InvalidMoveException;

    void computerMove() throws InvalidMoveException;

    GameState getState();


}
