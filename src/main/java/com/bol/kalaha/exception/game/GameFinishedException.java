package com.bol.kalaha.exception.game;

/**
 * This exception type is thrown when a requested game to be played is over.
 */
public class GameFinishedException extends GameException {

    public GameFinishedException(Integer gameId) {
        super(String.format("You cannot play game with id %s anymore as it was completed.", gameId));
    }
}
