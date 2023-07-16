package com.bol.kalaha.dto;

import com.bol.kalaha.dto.pit.BigPitDto;
import com.bol.kalaha.dto.pit.SmallPitDto;
import com.bol.kalaha.enums.PlayerBoardPositionEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PlayerDto {

    private Integer id;

    @NotEmpty(message = "Player's name cannot be null nor empty.")
    private String name;

    private List<SmallPitDto> smallPits;

    private BigPitDto bigPitDto;

    private PlayerBoardPositionEnum playerBoardPosition;

}
