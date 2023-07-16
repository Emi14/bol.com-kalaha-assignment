package com.bol.kalaha.mapper;

import com.bol.kalaha.dto.GameDto;
import com.bol.kalaha.dto.pit.BigPitDto;
import com.bol.kalaha.entity.GameEntity;
import com.bol.kalaha.enums.PlayerBoardPositionEnum;
import com.bol.kalaha.util.PitUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Class used to map different classes that hold game related data
 */
@Mapper(componentModel = "spring")
public interface GameMapper {
    GameEntity mapDtoToEntity(GameDto gameDto);

    @Mapping(source = "boardEntity", target = "boardDto")
    GameDto mapEntityToDto(GameEntity gameEntity);

    @AfterMapping
    default void addPitsValuesToBoardDtoAfterMapping(GameEntity gameEntity, @MappingTarget GameDto gameDto) {
        gameDto.getBoardDto().getFirstPlayer().setBigPitDto(new BigPitDto(gameEntity.getBoardEntity().getFirstPlayerBigPitValue(), gameEntity.getBoardEntity().getFirstPlayer().getId()));
        gameDto.getBoardDto().getFirstPlayer().setSmallPits(PitUtils.getSmallPitsFromString(gameEntity.getBoardEntity().getFirstPlayerSmallPitsValues(), gameEntity.getBoardEntity().getFirstPlayer().getId()));
        gameDto.getBoardDto().getFirstPlayer().setPlayerBoardPosition(PlayerBoardPositionEnum.DOWN);

        gameDto.getBoardDto().getSecondPlayer().setBigPitDto(new BigPitDto(gameEntity.getBoardEntity().getSecondPlayerBigPitValue(), gameEntity.getBoardEntity().getSecondPlayer().getId()));
        gameDto.getBoardDto().getSecondPlayer().setSmallPits(PitUtils.getSmallPitsFromString(gameEntity.getBoardEntity().getSecondPlayerSmallPitsValues(), gameEntity.getBoardEntity().getSecondPlayer().getId()));
        gameDto.getBoardDto().getSecondPlayer().setPlayerBoardPosition(PlayerBoardPositionEnum.UP);
    }
}
