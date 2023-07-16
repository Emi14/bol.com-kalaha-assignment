package com.bol.kalaha.mapper;

import com.bol.kalaha.dto.PlayerDto;
import com.bol.kalaha.entity.PlayerEntity;
import org.mapstruct.Mapper;

/**
 * Class used to map different classes that hold player data
 */
@Mapper(componentModel = "spring")
public interface PlayerMapper {

    PlayerEntity mapDtoToEntity(PlayerDto playerDto);

    PlayerDto mapEntityToDto(PlayerEntity playerEntity);

}
