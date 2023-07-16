package com.bol.kalaha.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@Entity(name = "games")
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "board_id", referencedColumnName = "id")
    private BoardEntity boardEntity;

    @ManyToOne
    @JoinColumn(name = "winner_id", referencedColumnName = "id")
    private PlayerEntity winner;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public GameEntity() {

    }
}
