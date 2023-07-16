package com.bol.kalaha.exception.game;

/**
 * This exception type is thrown when a requested game id is not found in DB.
 */
public class GameNotFoundException extends GameException {

    public GameNotFoundException(Integer gameId) {
        super(String.format("Game with id %s could not be found", gameId));
    }
}
