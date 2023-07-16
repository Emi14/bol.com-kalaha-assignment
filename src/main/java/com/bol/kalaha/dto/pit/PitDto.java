package com.bol.kalaha.dto.pit;

import lombok.Getter;
import lombok.Setter;

/**
 * Class used to define the attributes and the behaviour of an abstract pit.
 * All pits can contain seeds.
 */
@Getter
@Setter
public abstract class PitDto {

    private int pitId;
    private int seeds;
    private int ownerId;

    public PitDto(int pitId, int seeds, int ownerId) {
        this.pitId = pitId;
        this.seeds = seeds;
        this.ownerId = ownerId;
    }

    /**
     * Sows one more seed to the pit
     */
    public void sow() {
        this.seeds++;
    }

    /**
     * Empties the pit.
     *
     * @return the number of seeds contained by the pit before being emptied.
     */
    public int takeAllSeeds() {
        int numberOfSeeds = getSeeds();
        setSeeds(0);
        return numberOfSeeds;
    }

    /**
     * @return true if the player that made the move should keep his turn. false otherwise.
     */
    public abstract boolean shouldPlayerKeepTurn();
}
