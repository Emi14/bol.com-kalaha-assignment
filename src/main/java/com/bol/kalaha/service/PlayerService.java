package com.bol.kalaha.service;

import com.bol.kalaha.dto.PlayerDto;
import com.bol.kalaha.entity.PlayerEntity;
import com.bol.kalaha.exception.player.PlayerNotFoundException;
import com.bol.kalaha.mapper.PlayerMapper;
import com.bol.kalaha.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class storing the players related business logic
 */
@Slf4j
@Service
public class PlayerService {

    private static final String PLAYER_WITH_ID_COULD_NOT_BE_FOUND = "Player with id %s could not be found.";

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    public PlayerService(PlayerRepository playerRepository, PlayerMapper playerMapper) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
    }

    /**
     * Saves the given player to DB
     *
     * @param playerDto - player to be saved to DB
     * @return the persisted instance of {@link PlayerDto}
     */
    public PlayerDto save(PlayerDto playerDto) {
        PlayerEntity savedEntity = playerRepository.save(playerMapper.mapDtoToEntity(playerDto));
        return playerMapper.mapEntityToDto(savedEntity);
    }

    /**
     * Search for a player in DB
     *
     * @param id - value to be searched in DB
     * @return a {@link PlayerDto} instance
     * @throws PlayerNotFoundException when the given id is not present in DB
     */
    public PlayerDto findOneDtoById(int id) throws PlayerNotFoundException {
        return playerMapper.mapEntityToDto(findOneById(id));
    }

    /**
     * Search for a player in DB
     *
     * @param id - value to be searched in DB
     * @return a {@link PlayerEntity} instance
     * @throws PlayerNotFoundException when the given id is not present in DB
     */
    public PlayerEntity findOneById(int id) throws PlayerNotFoundException {
        return playerRepository.findById(id).orElseThrow(() -> {
            log.error(String.format(PLAYER_WITH_ID_COULD_NOT_BE_FOUND, id));
            return new PlayerNotFoundException(id);
        });
    }

    /**
     * Retrieves a list of {@link PlayerDto} instances, containing all players stored in DB
     *
     * @return a list of {@link PlayerDto} instances
     */
    public List<PlayerDto> findAll() {
        return playerRepository.findAll().stream()
                .map(playerMapper::mapEntityToDto)
                .collect(Collectors.toList());
    }
}
