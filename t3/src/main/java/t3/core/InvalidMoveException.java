package t3.core;

/**
 * Indicates that a particular move is invalid as per specific game rules.
 */
public class InvalidMoveException extends Exception {
    public InvalidMoveException(String message) {
        super(message);
    }
}
