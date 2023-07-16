package com.bol.kalaha.unit.service;

import com.bol.kalaha.entity.BoardEntity;
import com.bol.kalaha.entity.GameEntity;
import com.bol.kalaha.entity.PlayerEntity;
import com.bol.kalaha.exception.game.GameException;
import com.bol.kalaha.exception.game.GameFinishedException;
import com.bol.kalaha.exception.game.GameNotFoundException;
import com.bol.kalaha.exception.player.PlayerNotFoundException;
import com.bol.kalaha.mapper.GameMapper;
import com.bol.kalaha.repository.GameRepository;
import com.bol.kalaha.service.BoardService;
import com.bol.kalaha.service.GameService;
import com.bol.kalaha.service.PlayerService;
import com.bol.kalaha.util.GameTestUtils;
import com.bol.kalaha.util.PlayerTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    private static final String PLAYER_NAME_JOHN_DOE = "John Doe";
    private static final String PLAYER_NAME_JOE_DOE = "Joe Doe";

    @Mock
    private PlayerService playerService;
    @Mock
    private BoardService boardService;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private GameMapper gameMapper;

    @InjectMocks
    private GameService gameService;

    @Test
    public void testPlayTurn_whenGameNotFound_thenGameNotFoundExceptionIsThrown() {
        int gameId = 1;

        when(gameRepository.findById(eq(gameId))).thenReturn(Optional.empty());

        GameNotFoundException thrownException = assertThrows(GameNotFoundException.class,
                () -> gameService.playTurn(GameTestUtils.getSowSeedsRequestDto(gameId, 1, 1)));

        assertEquals(String.format("Game with id %s could not be found", gameId), thrownException.getMessage());
        verifyNoInteractions(boardService);
    }

    @Test
    public void testPlayTurn_whenGameIsFinished_thenGameFinishedExceptionIsThrown() {
        int gameId = 1;

        when(gameRepository.findById(eq(gameId)))
                .thenReturn(Optional.of(GameTestUtils.getGameEntity(gameId, BoardEntity.builder().build(), LocalDateTime.now())));

        GameFinishedException thrownException = assertThrows(GameFinishedException.class,
                () -> gameService.playTurn(GameTestUtils.getSowSeedsRequestDto(gameId, 1, 1)));

        assertEquals(String.format("You cannot play game with id %s anymore as it was completed.", gameId), thrownException.getMessage());
        verifyNoInteractions(boardService);
    }

    @Test
    public void testPlayTurn_whenIsNotGivenPlayerTurn_thenGameExceptionIsThrown() {
        int gameId = 1;
        int playerId = 1;
        BoardEntity boardEntity = BoardEntity.builder()
                .nextTurn(PlayerTestUtils.getValidPlayerEntity(playerId + 1, PLAYER_NAME_JOHN_DOE))
                .build();

        when(gameRepository.findById(eq(gameId)))
                .thenReturn(Optional.of(GameTestUtils.getGameEntity(gameId, boardEntity, null)));

        GameException thrownException = assertThrows(GameException.class,
                () -> gameService.playTurn(GameTestUtils.getSowSeedsRequestDto(gameId, playerId, 1)));

        assertEquals("Is not your turn.", thrownException.getMessage());
        verifyNoInteractions(boardService);
    }

    @Test
    public void testPlayTurn_whenPlayerCompletedTheGame_thenGameDetailsAreUpdatedAndFirstPlayerIsTheWinner() throws PlayerNotFoundException, GameException {
        int pitId = 1;
        int gameId = 1;
        int playerId = 1;
        PlayerEntity firstPlayerEntity = PlayerTestUtils.getValidPlayerEntity(playerId, PLAYER_NAME_JOHN_DOE);
        BoardEntity boardEntity = BoardEntity.builder()
                .nextTurn(firstPlayerEntity)
                .firstPlayer(firstPlayerEntity)
                .firstPlayerSmallPitsValues("0,0,0,0,0,0")
                .firstPlayerBigPitValue(24)
                .secondPlayer(PlayerTestUtils.getValidPlayerEntity(playerId + 1, PLAYER_NAME_JOE_DOE))
                .secondPlayerBigPitValue(12)
                .build();
        GameEntity gameEntity = GameTestUtils.getGameEntity(gameId, boardEntity, null);

        when(gameRepository.findById(eq(gameId))).thenReturn(Optional.of(gameEntity));

        gameService.playTurn(GameTestUtils.getSowSeedsRequestDto(gameId, playerId, pitId));

        assertNotNull(gameEntity.getEndTime());
        assertEquals(firstPlayerEntity.getId(), gameEntity.getWinner().getId());
        assertEquals(firstPlayerEntity.getName(), gameEntity.getWinner().getName());
        verify(gameRepository, times(1)).save(eq(gameEntity));
    }

    @Test
    public void testPlayTurn_whenPlayerCompletedTheGame_thenGameDetailsAreUpdatedAndSecondPlayerIsTheWinner() throws PlayerNotFoundException, GameException {
        int pitId = 1;
        int gameId = 1;
        int playerId = 1;
        PlayerEntity firstPlayerEntity = PlayerTestUtils.getValidPlayerEntity(playerId, PLAYER_NAME_JOHN_DOE);
        PlayerEntity secondPlayerEntity = PlayerTestUtils.getValidPlayerEntity(playerId + 1, PLAYER_NAME_JOE_DOE);
        BoardEntity boardEntity = BoardEntity.builder()
                .nextTurn(firstPlayerEntity)
                .firstPlayer(firstPlayerEntity)
                .firstPlayerSmallPitsValues("0,0,0,0,0,0")
                .firstPlayerBigPitValue(24)
                .secondPlayer(secondPlayerEntity)
                .secondPlayerBigPitValue(35)
                .build();
        GameEntity gameEntity = GameTestUtils.getGameEntity(gameId, boardEntity, null);

        when(gameRepository.findById(eq(gameId))).thenReturn(Optional.of(gameEntity));

        gameService.playTurn(GameTestUtils.getSowSeedsRequestDto(gameId, playerId, pitId));

        assertNotNull(gameEntity.getEndTime());
        assertEquals(secondPlayerEntity.getId(), gameEntity.getWinner().getId());
        assertEquals(secondPlayerEntity.getName(), gameEntity.getWinner().getName());
        verify(gameRepository, times(1)).save(eq(gameEntity));
    }

    @Test
    public void testPlayTurn_whenTheGameIsNotCompleted_thenGameHasNoEndDate() throws PlayerNotFoundException, GameException {
        int pitId = 1;
        int gameId = 1;
        int playerId = 1;
        PlayerEntity firstPlayerEntity = PlayerTestUtils.getValidPlayerEntity(playerId, PLAYER_NAME_JOHN_DOE);
        BoardEntity boardEntity = BoardEntity.builder()
                .nextTurn(firstPlayerEntity)
                .firstPlayer(firstPlayerEntity)
                .firstPlayerSmallPitsValues("0,0,0,6,0,0")
                .firstPlayerBigPitValue(24)
                .secondPlayer(PlayerTestUtils.getValidPlayerEntity(playerId + 1, PLAYER_NAME_JOE_DOE))
                .secondPlayerBigPitValue(12)
                .secondPlayerSmallPitsValues("0,2,0,1,0,0")
                .build();
        GameEntity gameEntity = GameTestUtils.getGameEntity(gameId, boardEntity, null);

        when(gameRepository.findById(eq(gameId))).thenReturn(Optional.of(gameEntity));

        gameService.playTurn(GameTestUtils.getSowSeedsRequestDto(gameId, playerId, pitId));

        assertNull(gameEntity.getEndTime());
        assertNull(gameEntity.getWinner());
        verify(gameRepository, times(1)).save(eq(gameEntity));
    }
}