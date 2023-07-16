package com.bol.kalaha.controller;

import com.bol.kalaha.dto.PlayerDto;
import com.bol.kalaha.exception.player.PlayerNotFoundException;
import com.bol.kalaha.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class storing endpoints used to interact with player entities
 */
@RestController
@RequestMapping("/player")
@CrossOrigin
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * Persisting a player entity with the given details to DB
     *
     * @param playerDto - contains player details to be persisted as a new entity
     * @return the persisted entity having the auto-generated id
     */
    @PostMapping("/save")
    public PlayerDto save(@Valid @RequestBody PlayerDto playerDto) {
        return playerService.save(playerDto);
    }

    /**
     * Retrieves the player entity identified by the given id.
     *
     * @param id - player identifier used for DB search
     * @return the player entity with the given id or custom String message when no data was found
     */
    @GetMapping("/{id}")
    public PlayerDto getById(@PathVariable int id) throws PlayerNotFoundException {
        return playerService.findOneDtoById(id);
    }

    /**
     * Retrieves all players from DB.
     *
     * @return a list of persisted players. List can be empty when no players are present in DB.
     */
    @GetMapping("/all")
    public List<PlayerDto> getAll() {
        return playerService.findAll();
    }

}
