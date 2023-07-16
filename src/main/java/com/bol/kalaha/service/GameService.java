package com.bol.kalaha.service;

import com.bol.kalaha.dto.GameDto;
import com.bol.kalaha.dto.SowSeedsRequestDto;
import com.bol.kalaha.entity.BoardEntity;
import com.bol.kalaha.entity.GameEntity;
import com.bol.kalaha.entity.PlayerEntity;
import com.bol.kalaha.exception.game.GameException;
import com.bol.kalaha.exception.game.GameFinishedException;
import com.bol.kalaha.exception.game.GameNotFoundException;
import com.bol.kalaha.exception.player.PlayerNotFoundException;
import com.bol.kalaha.mapper.GameMapper;
import com.bol.kalaha.repository.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class storing the game business logic
 */
@Service
@Slf4j
public class GameService {

    private final PlayerService playerService;
    private final BoardService boardService;
    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    public GameService(PlayerService playerService, BoardService boardService, GameRepository gameRepository,
                       GameMapper gameMapper) {
        this.playerService = playerService;
        this.boardService = boardService;
        this.gameRepository = gameRepository;
        this.gameMapper = gameMapper;
    }

    /**
     * Creates a new game instance and persists it to DB.
     *
     * @param firstPlayerId  - id of the first player to play the game
     * @param secondPlayerId - id of the second player to play the game
     * @return an instance of GameDto containing details to the newly created game
     * @throws PlayerNotFoundException - when the given player ids are not found in DB
     */
    public GameDto startNewGame(Integer firstPlayerId, Integer secondPlayerId) throws PlayerNotFoundException {
        validatePlayers(firstPlayerId, secondPlayerId);

        PlayerEntity firstPlayer = playerService.findOneById(firstPlayerId);
        PlayerEntity secondPlayer = playerService.findOneById(secondPlayerId);

        BoardEntity newBoardEntity = boardService.createNewBoardEntity(firstPlayer, secondPlayer);

        GameEntity gameEntity = GameEntity.builder().boardEntity(newBoardEntity)
                .startTime(LocalDateTime.now())
                .build();

        gameEntity = gameRepository.save(gameEntity);

        return gameMapper.mapEntityToDto(gameEntity);
    }

    /**
     * Ensures that the given players ids are not null and unique
     *
     * @param firstPlayerId  - id of the first player to play the game
     * @param secondPlayerId - id of the second player to play the game
     */
    private void validatePlayers(Integer firstPlayerId, Integer secondPlayerId) {
        if (firstPlayerId == null || secondPlayerId == null || firstPlayerId.equals(secondPlayerId)) {
            log.error(String.format("Exception occurred while trying to start a new game." +
                    "The given players ids are null or not unique: firstPlayerId: %s, secondPlayerId: %s", firstPlayerId, secondPlayerId));
            throw new IllegalArgumentException("In order to start a game, player ids must be not null and unique.");
        }
    }

    /**
     * Search for a game in DB
     *
     * @param id - value to be searched in DB
     * @return a {@link GameDto} instance
     * @throws GameNotFoundException when the given id was not found in DB
     */
    public GameDto findOneDtoById(int id) throws GameNotFoundException {
        return gameMapper.mapEntityToDto(findOneById(id));
    }

    /**
     * Search for a game in DB
     *
     * @param id - value to be searched in DB
     * @return a {@link GameEntity} instance
     * @throws GameNotFoundException when the given id was not found in DB
     */
    public GameEntity findOneById(int id) throws GameNotFoundException {
        return gameRepository.findById(id)
                .orElseThrow(() -> {
                    log.error(String.format("Game with id %s could not be found.", id));
                    return new GameNotFoundException(id);
                });
    }

    /**
     * Retrieves a list of {@link GameDto} instances, containing all games stored in DB
     *
     * @return a list of {@link GameDto} instances
     */
    public List<GameDto> findAll() {
        return gameRepository.findAll().stream()
                .map(gameMapper::mapEntityToDto)
                .collect(Collectors.toList());
    }

    /**
     * Validates the sowSeedsRequest and starts the sowing process.
     *
     * @param sowSeedsRequest - contains details for sowing seeds
     * @return an instance of {@link GameDto} containing the updated details of the game
     * @throws GameException           when the given id was not found in DB or is not player's turn
     * @throws PlayerNotFoundException when the given player id is not present in DB
     */
    public GameDto playTurn(final SowSeedsRequestDto sowSeedsRequest) throws GameException, PlayerNotFoundException {
        GameEntity gameEntity = findOneById(sowSeedsRequest.getGameId());

        validateSowSeedsRequest(sowSeedsRequest, gameEntity);

        boardService.sowSeeds(gameEntity.getBoardEntity(), sowSeedsRequest.getPlayerId(), sowSeedsRequest.getPitId());

        if (isGameOver(gameEntity)) {
            gameEntity.setEndTime(LocalDateTime.now());
            gameEntity.setWinner(getWinner(gameEntity));
        }

        return gameMapper.mapEntityToDto(gameRepository.save(gameEntity));
    }

    /**
     * Checks which player has more seeds in his/her big pit
     *
     * @param gameEntity - persisted game details
     * @return a persisted player entity representing the game winner
     */
    private PlayerEntity getWinner(GameEntity gameEntity) {
        if (gameEntity.getBoardEntity().getFirstPlayerBigPitValue() > gameEntity.getBoardEntity().getSecondPlayerBigPitValue()) {
            return gameEntity.getBoardEntity().getFirstPlayer();
        }
        return gameEntity.getBoardEntity().getSecondPlayer();
    }

    /**
     * Checks if the game is finished by verifying if any of the player has no seeds remaining
     *
     * @param gameEntity - persisted game details
     * @return true if game is over or false otherwise
     */
    private boolean isGameOver(GameEntity gameEntity) {
        return Arrays.stream(gameEntity.getBoardEntity().getFirstPlayerSmallPitsValues().split(","))
                .allMatch(s -> s.equals("0"))
                || Arrays.stream(gameEntity.getBoardEntity().getSecondPlayerSmallPitsValues().split(","))
                .allMatch(s -> s.equals("0"));
    }

    /**
     * Validates that the received sow request is valid.
     *
     * @param sowSeedsRequest - contains details for sowing seeds
     * @param gameEntity      - persisted game details
     * @throws GameException when the is not player's turn
     */
    private void validateSowSeedsRequest(final SowSeedsRequestDto sowSeedsRequest, final GameEntity gameEntity) throws GameException {
        if (isFinished(gameEntity)) {
            throw new GameFinishedException(gameEntity.getId());
        }

        if (!sowSeedsRequest.getPlayerId().equals(gameEntity.getBoardEntity().getNextTurn().getId())) {
            throw new GameException("Is not your turn.");
        }
    }

    /**
     * A game is considered finished when end time is set to gameEntity
     *
     * @param gameEntity - persisted game details
     * @return true if game is finished
     */
    private boolean isFinished(GameEntity gameEntity) {
        return gameEntity.getEndTime() != null;
    }
}
