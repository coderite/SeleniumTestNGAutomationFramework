package sadilek.exceptions;

/**
 * My custom exception is intended to b thrown when interactions with
 * web elements fail during test execution while providing additional context.
 */
public class ElementInteractionException extends RuntimeException {
    /**
     * Constructs a new ElementInteractionException
     * 
     * @param message Message with exception details are saved for later
     *                use.
     */
    public ElementInteractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
