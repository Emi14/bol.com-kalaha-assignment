package com.bol.kalaha.unit.service;

import com.bol.kalaha.dto.BoardDto;
import com.bol.kalaha.dto.PlayerDto;
import com.bol.kalaha.entity.GameEntity;
import com.bol.kalaha.enums.PlayerBoardPositionEnum;
import com.bol.kalaha.mapper.BoardMapper;
import com.bol.kalaha.repository.BoardRepository;
import com.bol.kalaha.service.BoardService;
import com.bol.kalaha.util.PlayerTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

    @Mock
    private BoardMapper boardMapper;

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(boardService, "numberOfSmallPits", 6);
        ReflectionTestUtils.setField(boardService, "numberOfStartingSeedsPerSmallPits", 6);
        ReflectionTestUtils.setField(boardService, "numberOfStartingSeedsPerBigPit", 0);
    }

    @Test
    public void testSowSeeds_whenFirstPlayerTurn_ThenSowedPitHasZeroSeedsAndBigPitHasOne() throws Exception {
        int pitToBeSowed = 4;
        PlayerDto firstPlayer = PlayerTestUtils.getValidPlayerDtoWithPits(1, "John Doe", 0, Arrays.asList(6, 6, 6, 6, 6, 6), PlayerBoardPositionEnum.DOWN);
        PlayerDto secondPlayer = PlayerTestUtils.getValidPlayerDtoWithPits(2, "Joe Doe", 0, Arrays.asList(6, 6, 6, 6, 6, 6), PlayerBoardPositionEnum.UP);
        BoardDto boardDto = getBoardDto(firstPlayer, secondPlayer, firstPlayer);
        when(boardMapper.mapEntityToDto(any()))
                .thenReturn(boardDto);

        final GameEntity gameEntity = new GameEntity();
        boardService.sowSeeds(gameEntity.getBoardEntity(), firstPlayer.getId(), pitToBeSowed);

        assertEquals(0, firstPlayer.getSmallPits().get(pitToBeSowed).getSeeds());
        assertEquals(1, firstPlayer.getBigPitDto().getSeeds());
        assertEquals(7, secondPlayer.getSmallPits().get(2).getSeeds());
        assertEquals(6, secondPlayer.getSmallPits().get(1).getSeeds());
        assertEquals(secondPlayer, boardDto.getNextTurn());
    }

    @Test
    public void testSowSeeds_whenSecondPlayerTurn_ThenSowedPitHasZeroSeedsAndBigPitHasOne() throws Exception {
        int pitToBeSowed = 3;
        PlayerDto firstPlayer = PlayerTestUtils.getValidPlayerDtoWithPits(1, "John Doe", 0, Arrays.asList(6, 6, 6, 6, 6, 6), PlayerBoardPositionEnum.DOWN);
        PlayerDto secondPlayer = PlayerTestUtils.getValidPlayerDtoWithPits(2, "Joe Doe", 0, Arrays.asList(6, 6, 6, 6, 6, 6), PlayerBoardPositionEnum.UP);
        BoardDto boardDto = getBoardDto(firstPlayer, secondPlayer, secondPlayer);
        when(boardMapper.mapEntityToDto(any()))
                .thenReturn(boardDto);

        final GameEntity gameEntity = new GameEntity();
        boardService.sowSeeds(gameEntity.getBoardEntity(), secondPlayer.getId(), pitToBeSowed);

        assertEquals(0, secondPlayer.getSmallPits().get(pitToBeSowed).getSeeds());
        assertEquals(1, secondPlayer.getBigPitDto().getSeeds());
        assertEquals(7, firstPlayer.getSmallPits().get(1).getSeeds());
        assertEquals(6, firstPlayer.getSmallPits().get(2).getSeeds());
        assertEquals(firstPlayer, boardDto.getNextTurn());
    }

    @Test
    public void testSowSeeds_whenFirstPlayerTurnAndSowEndsInBigPit_ThenSowedPitHasZeroSeedsAndBigPitHasOneAndFirstPlayerKeepsTurn() throws Exception {
        int pitToBeSowed = 3;
        PlayerDto firstPlayer = PlayerTestUtils.getValidPlayerDtoWithPits(1, "John Doe", 0, Arrays.asList(6, 6, 6, 3, 6, 6), PlayerBoardPositionEnum.DOWN);
        PlayerDto secondPlayer = PlayerTestUtils.getValidPlayerDtoWithPits(2, "Joe Doe", 0, Arrays.asList(6, 6, 6, 6, 6, 6), PlayerBoardPositionEnum.UP);
        BoardDto boardDto = getBoardDto(firstPlayer, secondPlayer, firstPlayer);
        when(boardMapper.mapEntityToDto(any()))
                .thenReturn(boardDto);

        final GameEntity gameEntity = new GameEntity();
        boardService.sowSeeds(gameEntity.getBoardEntity(), firstPlayer.getId(), pitToBeSowed);

        assertEquals(0, firstPlayer.getSmallPits().get(pitToBeSowed).getSeeds());
        assertEquals(1, firstPlayer.getBigPitDto().getSeeds());
        assertEquals(6, secondPlayer.getSmallPits().get(5).getSeeds());
        assertEquals(firstPlayer, boardDto.getNextTurn());
    }

    @Test
    public void testSowSeeds_whenFirstPlayerTurnAndSowEndsInHisEmptySmallPit_ThenOpponentSeedsAreCaptured() throws Exception {
        int pitToBeSowed = 0;
        PlayerDto firstPlayer = PlayerTestUtils.getValidPlayerDtoWithPits(1, "John Doe", 0, Arrays.asList(5, 6, 6, 6, 6, 0), PlayerBoardPositionEnum.DOWN);
        PlayerDto secondPlayer = PlayerTestUtils.getValidPlayerDtoWithPits(2, "Joe Doe", 0, Arrays.asList(6, 6, 6, 6, 6, 6), PlayerBoardPositionEnum.UP);
        BoardDto boardDto = getBoardDto(firstPlayer, secondPlayer, firstPlayer);
        when(boardMapper.mapEntityToDto(any()))
                .thenReturn(boardDto);

        final GameEntity gameEntity = new GameEntity();
        boardService.sowSeeds(gameEntity.getBoardEntity(), firstPlayer.getId(), pitToBeSowed);

        assertEquals(0, firstPlayer.getSmallPits().get(pitToBeSowed).getSeeds());
        assertEquals(7, firstPlayer.getSmallPits().get(1).getSeeds());
        assertEquals(0, secondPlayer.getSmallPits().get(5).getSeeds());
        assertEquals(7, firstPlayer.getBigPitDto().getSeeds());
        assertEquals(secondPlayer, boardDto.getNextTurn());
    }

    private BoardDto getBoardDto(PlayerDto firstPlayer, PlayerDto secondPlayer, PlayerDto nextTurn) {
        BoardDto boardDto = new BoardDto();
        boardDto.setId(1);
        boardDto.setFirstPlayer(firstPlayer);
        boardDto.setSecondPlayer(secondPlayer);
        boardDto.setNextTurn(nextTurn);
        return boardDto;
    }
}
