package com.bol.kalaha.unit.mapper;

import com.bol.kalaha.dto.BoardDto;
import com.bol.kalaha.dto.pit.SmallPitDto;
import com.bol.kalaha.entity.BoardEntity;
import com.bol.kalaha.enums.PlayerBoardPositionEnum;
import com.bol.kalaha.mapper.BoardMapper;
import com.bol.kalaha.mapper.BoardMapperImpl;
import com.bol.kalaha.util.PlayerTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {BoardMapperImpl.class})
public class BoardMapperTest {

    public static final Integer FIRST_PLAYER_BIG_PIT = 4;
    public static final Integer SECOND_PLAYER_BIG_PIT = 7;
    public static final String FIRST_PLAYER_SMALL_PITS_VALUES = "2,5,6,2,6,5";
    public static final String SECOND_PLAYER_SMALL_PITS_VALUES = "2,5,7,4,6,5";
    @Autowired
    public BoardMapper boardMapper;

    @Test
    public void testMapDtoToEntity() {
        BoardDto boardDto = new BoardDto();
        boardDto.setId(1);
        boardDto.setFirstPlayer(PlayerTestUtils.getValidPlayerDtoWithPits(1, "Joe Doe", FIRST_PLAYER_BIG_PIT, Arrays.asList(2, 5, 6, 2, 6, 5), PlayerBoardPositionEnum.UP));
        boardDto.setSecondPlayer(PlayerTestUtils.getValidPlayerDtoWithPits(2, "John Doe", SECOND_PLAYER_BIG_PIT, Arrays.asList(2, 5, 7, 4, 6, 5), PlayerBoardPositionEnum.DOWN));

        BoardEntity boardEntity = boardMapper.mapDtoToEntity(boardDto);

        assertEquals(boardDto.getId(), boardEntity.getId());
        assertEquals(boardDto.getFirstPlayer().getId(), boardEntity.getFirstPlayer().getId());
        assertEquals(boardDto.getFirstPlayer().getName(), boardEntity.getFirstPlayer().getName());
        assertEquals(boardDto.getSecondPlayer().getId(), boardEntity.getSecondPlayer().getId());
        assertEquals(boardDto.getSecondPlayer().getName(), boardEntity.getSecondPlayer().getName());
        assertEquals(FIRST_PLAYER_BIG_PIT, boardEntity.getFirstPlayerBigPitValue());
        assertEquals(SECOND_PLAYER_BIG_PIT, boardEntity.getSecondPlayerBigPitValue());
        assertEquals(FIRST_PLAYER_SMALL_PITS_VALUES, boardEntity.getFirstPlayerSmallPitsValues());
        assertEquals(SECOND_PLAYER_SMALL_PITS_VALUES, boardEntity.getSecondPlayerSmallPitsValues());
    }

    @Test
    public void testMapEntityToDto() {
        BoardEntity boardEntity = BoardEntity.builder()
                .id(1)
                .firstPlayer(PlayerTestUtils.getValidPlayerEntity(1, "John Doe"))
                .firstPlayerSmallPitsValues(FIRST_PLAYER_SMALL_PITS_VALUES)
                .firstPlayerBigPitValue(FIRST_PLAYER_BIG_PIT)
                .secondPlayer(PlayerTestUtils.getValidPlayerEntity(2, "Joe Doe"))
                .secondPlayerSmallPitsValues(SECOND_PLAYER_SMALL_PITS_VALUES)
                .secondPlayerBigPitValue(SECOND_PLAYER_BIG_PIT)
                .build();

        BoardDto boardDto = boardMapper.mapEntityToDto(boardEntity);

        assertEquals(boardEntity.getId(), boardDto.getId());
        assertEquals(boardEntity.getFirstPlayer().getId(), boardDto.getFirstPlayer().getId());
        assertEquals(boardEntity.getFirstPlayer().getName(), boardDto.getFirstPlayer().getName());
        assertTrue(equalSmallPitsValues(boardDto.getFirstPlayer().getSmallPits(), boardEntity.getFirstPlayerSmallPitsValues()));
        assertEquals(boardEntity.getFirstPlayerBigPitValue(), boardDto.getFirstPlayer().getBigPitDto().getSeeds());
        assertEquals(boardEntity.getSecondPlayer().getId(), boardDto.getSecondPlayer().getId());
        assertEquals(boardEntity.getSecondPlayer().getName(), boardDto.getSecondPlayer().getName());
        assertTrue(equalSmallPitsValues(boardDto.getSecondPlayer().getSmallPits(), boardEntity.getSecondPlayerSmallPitsValues()));
        assertEquals(boardEntity.getSecondPlayerBigPitValue(), boardDto.getSecondPlayer().getBigPitDto().getSeeds());
    }

    private boolean equalSmallPitsValues(List<SmallPitDto> smallPitDtos, String smallPitsValues) {
        String[] pitsValues = smallPitsValues.split(",");
        for (int i = 0; i < smallPitDtos.size(); i++) {
            if (smallPitDtos.get(i).getSeeds() != Integer.parseInt(pitsValues[i])) {
                return false;
            }
        }
        return true;
    }

}
