package com.bol.kalaha.repository;

import com.bol.kalaha.entity.GameEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepository extends CrudRepository<GameEntity, Integer> {

    List<GameEntity> findAll();
}
