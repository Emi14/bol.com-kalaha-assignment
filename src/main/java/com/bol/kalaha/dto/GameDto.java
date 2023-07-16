package com.bol.kalaha.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GameDto {

    private Integer id;
    private BoardDto boardDto;
    private PlayerDto winner;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
