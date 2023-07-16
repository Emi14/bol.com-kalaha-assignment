package com.bol.kalaha.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SowSeedsRequestDto {

    @NotNull(message = "Game id cannot be null!")
    @Min(value = 0, message = "Game id should be positive!")
    private Integer gameId;

    @NotNull(message = "Player id cannot be null!")
    @Min(value = 0, message = "Player id should be positive!")
    private Integer playerId;

    @NotNull(message = "Pit id cannot be null!")
    @Min(value = 0, message = "Pit id should be positive!")
    @Max(value = 5, message = "Pit id should be lower than 6!")
    private Integer pitId;
}
