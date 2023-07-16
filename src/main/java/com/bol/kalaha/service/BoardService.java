package com.bol.kalaha.service;

import com.bol.kalaha.dto.BoardDto;
import com.bol.kalaha.dto.PlayerDto;
import com.bol.kalaha.dto.pit.BigPitDto;
import com.bol.kalaha.dto.pit.PitDto;
import com.bol.kalaha.entity.BoardEntity;
import com.bol.kalaha.entity.PlayerEntity;
import com.bol.kalaha.enums.PlayerBoardPositionEnum;
import com.bol.kalaha.exception.game.GameException;
import com.bol.kalaha.exception.player.PlayerNotFoundException;
import com.bol.kalaha.mapper.BoardMapper;
import com.bol.kalaha.repository.BoardRepository;
import com.google.common.collect.Iterables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class storing the board related business logic
 */
@Slf4j
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardMapper boardMapper;
    @Value("${number.of.small.pits.per.player}")
    private Integer numberOfSmallPits;
    @Value("${number.of.starting.seeds.per.small.pit}")
    private Integer numberOfStartingSeedsPerSmallPits;
    @Value("${number.of.starting.seeds.per.big.pit}")
    private Integer numberOfStartingSeedsPerBigPit;

    public BoardService(BoardRepository boardRepository, BoardMapper boardMapper) {
        this.boardRepository = boardRepository;
        this.boardMapper = boardMapper;
    }

    /**
     * Captures the seeds from the opposite pit and move them to the player's big pit alongside the seed from the
     * last visited pit
     *
     * @param player         - the player making the sow
     * @param opponent       - the opponent of the player making the sow
     * @param lastVisitedPit - the pit in which the last seed was placed
     */
    private void captureOppositeSeeds(final PlayerDto player, final PlayerDto opponent, PitDto lastVisitedPit) {
        int opponentSeedsToBeCaptured = opponent.getSmallPits().get(lastVisitedPit.getPitId()).takeAllSeeds();
        player.getBigPitDto().sow(opponentSeedsToBeCaptured + lastVisitedPit.takeAllSeeds());
    }

    /**
     * Checks if the pit in which the last seed was placed is a small pit owned the player making the sow
     *
     * @param playerId       - the id of the player making the sow
     * @param lastVisitedPit - the pit in which the last seed was placed
     * @return true if the given lastVisitedPit is a small pit owned the player making the sow, else otherwise
     */
    private boolean shouldCaptureOppositeSeeds(Integer playerId, PitDto lastVisitedPit) {
        return lastVisitedPit.getPitId() != BigPitDto.BIG_PIT_DUMMY_ID &&
                lastVisitedPit.getOwnerId() == playerId &&
                lastVisitedPit.getSeeds() == 1;
    }

    /**
     * Creates a new instance of {@link BoardEntity} with the given players.
     *
     * @param firstPlayer  - first player to be added to the newly created board
     * @param secondPlayer - first player to be added to the newly created board
     * @return the newly created board entity
     */
    public BoardEntity createNewBoardEntity(PlayerEntity firstPlayer, PlayerEntity secondPlayer) {
        BoardEntity boardEntity = BoardEntity.builder().firstPlayer(firstPlayer)
                .firstPlayerBigPitValue(numberOfStartingSeedsPerBigPit)
                .firstPlayerSmallPitsValues(getStartingSmallPitsValues())
                .secondPlayer(secondPlayer)
                .secondPlayerBigPitValue(numberOfStartingSeedsPerBigPit)
                .secondPlayerSmallPitsValues(getStartingSmallPitsValues())
                .nextTurn(firstPlayer)
                .build();

        return boardRepository.save(boardEntity);
    }

    /**
     * Builds the default values of the small pits.
     * Default values are declared in properties files.
     *
     * @return a CSV string containing the number of seeds for each pit
     */
    private String getStartingSmallPitsValues() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < numberOfSmallPits; i++) {
            stringBuilder.append(numberOfStartingSeedsPerSmallPits).append(",");
        }
        return stringBuilder.substring(0, stringBuilder.length());
    }

    /**
     * Sow the seeds from the given pit
     *
     * @param boardEntity - the instance of the board
     * @param playerId    - the id of the player making the sow
     * @param pitId       - the pit to be sowed
     * @throws GameException           when the given pit is empty.
     * @throws PlayerNotFoundException when the given player id was not found on the board
     */
    public void sowSeeds(BoardEntity boardEntity, Integer playerId, Integer pitId) throws GameException, PlayerNotFoundException {
        BoardDto boardDto = boardMapper.mapEntityToDto(boardEntity);
        PlayerDto player = boardDto.getPlayerById(playerId);
        PlayerDto opponent = boardDto.getOpponentOf(playerId);

        if (player.getSmallPits().get(pitId).getSeeds() == 0) {
            log.error(String.format("The given pit with id:%s is empty for player with id:%s", pitId, playerId));
            throw new GameException("Pit is empty. Select another pit.");
        }

        PitDto lastVisitedPit = disperseSeeds(pitId, player, opponent);

        if (!lastVisitedPit.shouldPlayerKeepTurn()) {
            boardDto.setNextTurn(opponent);
        }

        if (shouldCaptureOppositeSeeds(playerId, lastVisitedPit)) {
            captureOppositeSeeds(player, opponent, lastVisitedPit);
        }
        boardRepository.save(boardMapper.mapDtoToEntity(boardDto));
    }

    /**
     * Empties the starting pit and moves its seeds to the next pits.
     * Finishes when there are no seeds left to be moved.
     *
     * @param pitId    - the pit to be sowed
     * @param player   - the player making the sow
     * @param opponent - the opponent of the player making the sow
     * @return the last pit that received a seed
     */
    private PitDto disperseSeeds(Integer pitId, final PlayerDto player, final PlayerDto opponent) {
        PitDto lastVisitedPit = null;
        Iterable<PitDto> cyclicIterableOfPits = getCyclicIterableOfPits(pitId, player, opponent);
        int seedsToSow = player.getSmallPits().get(pitId).takeAllSeeds();
        Iterator<PitDto> iterator = cyclicIterableOfPits.iterator();

        while (seedsToSow-- > 0) {
            lastVisitedPit = iterator.next();
            lastVisitedPit.sow();
        }
        return lastVisitedPit;
    }

    /**
     * Builds a cyclic iterable collection of pits in the order that they should be visited.
     *
     * @param pitId    - the pit to be sowed
     * @param player   - the player making the sow
     * @param opponent - the opponent of the player making the sow
     * @return a cyclic iterable collection of pits
     */
    private Iterable<PitDto> getCyclicIterableOfPits(Integer pitId, PlayerDto player, PlayerDto opponent) {
        List<PitDto> pitDtos = new ArrayList<>();

        if (player.getPlayerBoardPosition() == PlayerBoardPositionEnum.DOWN) {
            for (int i = pitId + 1; i < numberOfSmallPits; i++) {
                pitDtos.add(player.getSmallPits().get(i));
            }
            pitDtos.add(player.getBigPitDto());
            for (int i = numberOfSmallPits - 1; i >= 0; i--) {
                pitDtos.add(opponent.getSmallPits().get(i));
            }
            for (int i = 0; i <= pitId; i++) {
                pitDtos.add(player.getSmallPits().get(i));
            }
        } else {
            for (int i = pitId - 1; i >= 0; i--) {
                pitDtos.add(player.getSmallPits().get(i));
            }
            pitDtos.add(player.getBigPitDto());
            for (int i = 0; i < numberOfSmallPits; i++) {
                pitDtos.add(opponent.getSmallPits().get(i));
            }
            for (int i = numberOfSmallPits - 1; i > pitId; i--) {
                pitDtos.add(player.getSmallPits().get(i));
            }
        }
        return Iterables.cycle(pitDtos);
    }
}
