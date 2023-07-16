package com.bol.kalaha.exception.player;

import lombok.Getter;

/**
 * This exception type is thrown when a player is not found in DB.
 */
@Getter
public class PlayerNotFoundException extends Exception {

    private final Integer playerId;

    public PlayerNotFoundException(Integer playerId) {
        super(String.format("Player with id %s could not be found", playerId));
        this.playerId = playerId;
    }
}
