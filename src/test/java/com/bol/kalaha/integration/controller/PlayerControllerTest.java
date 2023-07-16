package com.bol.kalaha.integration.controller;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PlayerControllerTest {

    private static final String PLAYER_NAME_JOHN_DOE = "John Doe";
    private static final Integer DUMMY_ID = -1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlayerService playerService;

    @Test
    public void testSavePlayer_whenTheGivenPlayerDtoIsInvalid_thenBadRequestStatusIsReturned() throws Exception {
        mockMvc.perform(post("/player/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(PlayerTestUtils.getInvalidPlayerDto())))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testSavePlayer_whenTheGivenPlayerDtoIsValid_thenSavedEntityIsReturned() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/player/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(PlayerTestUtils.getValidPlayerDtoWithoutId(PLAYER_NAME_JOHN_DOE))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PlayerDto playerDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), PlayerDto.class);

        assertNotNull(playerDto);
        assertEquals(PLAYER_NAME_JOHN_DOE, playerDto.getName(), "Wrong player name was returned");
        assertNotNull(playerDto.getId());
    }

    @Test
    public void testGetById_whenTheGivenIdIsNotFound_thenNotFoundStatusIsReturned() throws Exception {
        mockMvc.perform(get("/player/" + DUMMY_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void testGetById_whenTheGivenIdIsFound_thenTheCorrectEntityIsReturned() throws Exception {
        PlayerDto persistedPlayer = playerService.save(PlayerTestUtils.getValidPlayerDtoWithoutId(PLAYER_NAME_JOHN_DOE));

        MvcResult mvcResult = mockMvc.perform(get("/player/" + persistedPlayer.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(persistedPlayer, objectMapper.readValue(mvcResult.getResponse().getContentAsString(), PlayerDto.class));
    }

    @Test
    public void testGetAll_whenDbIsEmpty_thenTheReturnedListIsNotNullButEmpty() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/player/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<PlayerDto> playerDtos = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertNotNull(playerDtos);
        assertTrue(playerDtos.isEmpty());
    }

    @Test
    public void testGetAll_whenDbContains2Value_thenTheReturnedListContainsPersistedValues() throws Exception {
        PlayerDto persistedPlayer1 = playerService.save(PlayerTestUtils.getValidPlayerDtoWithoutId(PLAYER_NAME_JOHN_DOE));
        PlayerDto persistedPlayer2 = playerService.save(PlayerTestUtils.getValidPlayerDtoWithoutId(PLAYER_NAME_JOHN_DOE));

        MvcResult mvcResult = mockMvc.perform(get("/player/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<PlayerDto> playerDtos = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertNotNull(playerDtos);
        assertTrue(playerDtos.contains(persistedPlayer1));
        assertTrue(playerDtos.contains(persistedPlayer2));
    }
}
