package com.bol.kalaha.controller;

import com.bol.kalaha.dto.GameDto;
import com.bol.kalaha.dto.SowSeedsRequestDto;
import com.bol.kalaha.exception.game.GameException;
import com.bol.kalaha.exception.game.GameNotFoundException;
import com.bol.kalaha.exception.player.PlayerNotFoundException;
import com.bol.kalaha.service.GameService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class storing endpoints used to interact with game entities
 */
@RestController
@RequestMapping("/game")
@CrossOrigin
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Starts a new game for the given players ids
     *
     * @param firstPlayerId  - id of the first player to play the game
     * @param secondPlayerId - id of the second player to play the game
     * @return an instance of GameDto containing details to the newly created game to the newly created game
     * @throws PlayerNotFoundException - when the given player ids are not found in DB
     */
    @PostMapping("/start")
    public GameDto startNewGame(@RequestParam(name = "firstPlayerId") Integer firstPlayerId,
                                @RequestParam(name = "secondPlayerId") Integer secondPlayerId) throws PlayerNotFoundException {
        return gameService.startNewGame(firstPlayerId, secondPlayerId);
    }

    /**
     * Retrieves an existing game from DB by ID.
     *
     * @param id - value to be used to identify the game in DB
     * @return an instance of GameDto containing details of the requested game or a string message when game not found
     */
    @GetMapping("/{id}")
    public GameDto getById(@PathVariable int id) throws GameNotFoundException {
        return gameService.findOneDtoById(id);
    }

    /**
     * Retrieves all existing games from DB
     *
     * @return a list of GameDto instances
     */
    @GetMapping("/all")
    public List<GameDto> getAll() {
        return gameService.findAll();
    }

    /**
     * Plays the next turn.
     *
     * @param sowSeedsRequest - object containing the data for playing the turn
     * @return an instance of GameDto containing details of the game after the turn was played
     * @throws GameException           when the given id was not found in DB
     * @throws PlayerNotFoundException when the given player id is not present in DB
     */
    @PostMapping("/sow")
    public GameDto sowSeeds(@Valid @RequestBody SowSeedsRequestDto sowSeedsRequest) throws GameException, PlayerNotFoundException {
        return gameService.playTurn(sowSeedsRequest);
    }
}
