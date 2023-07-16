package com.bol.kalaha.exception.game;

/**
 * Custom exception to be used when something goes wrong at game level
 */
public class GameException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public GameException(String message) {
        super(message);
    }
}
