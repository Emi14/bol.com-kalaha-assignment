package com.bol.kalaha.integration.controller;

import com.bol.kalaha.dto.GameDto;
import com.bol.kalaha.dto.PlayerDto;
import com.bol.kalaha.service.PlayerService;
import com.bol.kalaha.util.PlayerTestUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GameControllerTest {

    private static final String PLAYER_NAME_JOHN_DOE = "John Doe";
    private static final String PLAYER_NAME_JOE_DOE = "Joe Doe";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerService playerService;

    @Test
    void testStartNewGame_whenNotUniquePlayersIds_thenBadRequestStatusCodeIsReturned() throws Exception {
        int playerId = 1;
        mockMvc.perform(post(String.format("/game/start?firstPlayerId=%s&secondPlayerId=%s", playerId, playerId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void testStartNewGame_whenUniquePlayersIds_thenGameShouldBeCreated() throws Exception {
        PlayerDto firstPlayer = playerService.save(PlayerTestUtils.getValidPlayerDtoWithoutId(PLAYER_NAME_JOHN_DOE));
        PlayerDto secondPlayer = playerService.save(PlayerTestUtils.getValidPlayerDtoWithoutId(PLAYER_NAME_JOE_DOE));

        MvcResult mvcResult = mockMvc.perform(post(String.format("/game/start?firstPlayerId=%s&secondPlayerId=%s", firstPlayer.getId(), secondPlayer.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        GameDto gameDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), GameDto.class);

        assertNotNull(gameDto);
        assertNotNull(gameDto.getId());
        assertNotNull(gameDto.getStartTime());
    }

    @Test
    void testGetById_whenTheGivenIdIsNotFoundInDb_thenNotFoundStatusCodeIsReturned() throws Exception {
        mockMvc.perform(get("/game/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void testGetAll_whenDbIsEmpty_thenEmptyListIsReturned() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/game/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<GameDto> gameDtos = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertNotNull(gameDtos);
        assertTrue(gameDtos.isEmpty());
    }

    @Test
    void sowSeeds() {
    }
}