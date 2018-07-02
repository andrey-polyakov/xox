package t3.core;

/**
 * Indicates that a particular move is invalid as per specific game rules. The situation is
 * totally preventable if corresponding game methods used to determine move validity.
 */
public class InvalidMoveException extends Error {
    public InvalidMoveException(String message) {
        super(message);
    }
}
