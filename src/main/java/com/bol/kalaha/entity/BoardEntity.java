package com.bol.kalaha.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Entity(name = "boards")
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "first_player_id", referencedColumnName = "id")
    private PlayerEntity firstPlayer;

    @ManyToOne
    @JoinColumn(name = "second_player_id", referencedColumnName = "id")
    private PlayerEntity secondPlayer;

    @ManyToOne
    @JoinColumn(name = "next_turn_player_id", referencedColumnName = "id")
    private PlayerEntity nextTurn;

    private String firstPlayerSmallPitsValues;

    private String secondPlayerSmallPitsValues;

    private Integer firstPlayerBigPitValue;

    private Integer secondPlayerBigPitValue;

    public BoardEntity() {
    }
}
