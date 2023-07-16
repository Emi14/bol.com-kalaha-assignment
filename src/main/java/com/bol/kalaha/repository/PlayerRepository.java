package com.bol.kalaha.repository;

import com.bol.kalaha.entity.PlayerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends CrudRepository<PlayerEntity, Integer> {

    List<PlayerEntity> findAll();
}
