package com.bol.kalaha.dto;

import com.bol.kalaha.exception.player.PlayerNotFoundException;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Defines a game board.
 * Each board can have exactly two players.
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class BoardDto {

    private Integer id;
    private PlayerDto firstPlayer;
    private PlayerDto secondPlayer;
    private PlayerDto nextTurn;

    public PlayerDto getPlayerById(int playerId) throws PlayerNotFoundException {
        if (firstPlayer.getId() == playerId) {
            return firstPlayer;
        }
        if (secondPlayer.getId() == playerId) {
            return secondPlayer;
        }
        throw new PlayerNotFoundException(playerId);
    }

    public PlayerDto getOpponentOf(int playerId) {
        if (firstPlayer.getId() == playerId) {
            return secondPlayer;
        }
        return firstPlayer;
    }
}
