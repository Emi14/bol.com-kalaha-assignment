package com.bol.kalaha.util;

import com.bol.kalaha.dto.SowSeedsRequestDto;
import com.bol.kalaha.entity.BoardEntity;
import com.bol.kalaha.entity.GameEntity;

import java.time.LocalDateTime;

public class GameTestUtils {

    public static GameEntity getGameEntity(Integer gameId, BoardEntity boardEntity, LocalDateTime endTime) {
        GameEntity gameEntity = new GameEntity();
        gameEntity.setId(gameId);
        gameEntity.setBoardEntity(boardEntity);
        gameEntity.setEndTime(endTime);

        return gameEntity;
    }

    public static SowSeedsRequestDto getSowSeedsRequestDto(int gameId, int playerId, int pitId) {
        SowSeedsRequestDto sowSeedsRequestDto = new SowSeedsRequestDto();
        sowSeedsRequestDto.setGameId(gameId);
        sowSeedsRequestDto.setPlayerId(playerId);
        sowSeedsRequestDto.setPitId(pitId);

        return sowSeedsRequestDto;
    }
}
