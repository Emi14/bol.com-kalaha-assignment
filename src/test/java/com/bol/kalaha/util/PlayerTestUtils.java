package com.bol.kalaha.util;

import com.bol.kalaha.dto.PlayerDto;
import com.bol.kalaha.dto.pit.BigPitDto;
import com.bol.kalaha.dto.pit.SmallPitDto;
import com.bol.kalaha.entity.PlayerEntity;
import com.bol.kalaha.enums.PlayerBoardPositionEnum;

import java.util.ArrayList;
import java.util.List;

public class PlayerTestUtils {

    public static PlayerDto getInvalidPlayerDto() {
        return new PlayerDto();
    }

    public static PlayerDto getValidPlayerDtoWithoutId(String name) {
        PlayerDto playerDto = new PlayerDto();
        playerDto.setName(name);
        return playerDto;
    }

    public static PlayerDto getValidPlayerDtoWithPits(int id, String name, int bigPit, List<Integer> smallPits, PlayerBoardPositionEnum boardPosition) {
        PlayerDto playerDto = new PlayerDto();
        playerDto.setId(id);
        playerDto.setName(name);
        playerDto.setBigPitDto(new BigPitDto(bigPit, id));
        playerDto.setPlayerBoardPosition(boardPosition);

        List<SmallPitDto> smallPitDtos = new ArrayList<>();
        for (int i = 0; i < smallPits.size(); i++) {
            smallPitDtos.add(new SmallPitDto(i, smallPits.get(i), id));
        }
        playerDto.setSmallPits(smallPitDtos);
        return playerDto;
    }

    public static PlayerEntity getValidPlayerEntity(int id, String name) {
        return new PlayerEntity(id, name);
    }
}
