package com.bol.kalaha.mapper;

import com.bol.kalaha.dto.BoardDto;
import com.bol.kalaha.dto.pit.BigPitDto;
import com.bol.kalaha.entity.BoardEntity;
import com.bol.kalaha.enums.PlayerBoardPositionEnum;
import com.bol.kalaha.util.PitUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BoardMapper {

    @Mapping(target = "firstPlayerSmallPitsValues",
            expression = "java(com.bol.kalaha.util.PitUtils.getPitValuesFromPitList(boardDto.getFirstPlayer().getSmallPits()))")
    @Mapping(target = "firstPlayerBigPitValue", source = "firstPlayer.bigPitDto.seeds")
    @Mapping(target = "secondPlayerSmallPitsValues",
            expression = "java(com.bol.kalaha.util.PitUtils.getPitValuesFromPitList(boardDto.getSecondPlayer().getSmallPits()))")
    @Mapping(target = "secondPlayerBigPitValue", source = "secondPlayer.bigPitDto.seeds")
    BoardEntity mapDtoToEntity(BoardDto boardDto);

    @Mapping(source = "nextTurn", target = "nextTurn")
    BoardDto mapEntityToDto(BoardEntity boardEntity);

    @AfterMapping
    default void addPitsValuesToBoardDtoAfterMapping(BoardEntity boardEntity, @MappingTarget BoardDto boardDto) {
        boardDto.getFirstPlayer().setBigPitDto(new BigPitDto(boardEntity.getFirstPlayerBigPitValue(), boardEntity.getFirstPlayer().getId()));
        boardDto.getFirstPlayer().setSmallPits(PitUtils.getSmallPitsFromString(boardEntity.getFirstPlayerSmallPitsValues(), boardEntity.getFirstPlayer().getId()));
        boardDto.getFirstPlayer().setPlayerBoardPosition(PlayerBoardPositionEnum.DOWN);

        boardDto.getSecondPlayer().setBigPitDto(new BigPitDto(boardEntity.getSecondPlayerBigPitValue(), boardEntity.getSecondPlayer().getId()));
        boardDto.getSecondPlayer().setSmallPits(PitUtils.getSmallPitsFromString(boardEntity.getSecondPlayerSmallPitsValues(), boardEntity.getSecondPlayer().getId()));
        boardDto.getSecondPlayer().setPlayerBoardPosition(PlayerBoardPositionEnum.UP);
    }

}
