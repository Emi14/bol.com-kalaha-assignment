package com.bol.kalaha.dto.pit;

/**
 * Class used to define the attributes and the behaviour of a small pit.
 * On each board there are 6 small pits, one for each player.
 */
public class SmallPitDto extends PitDto {

    public SmallPitDto(int pitId, int seeds, int ownerId) {
        super(pitId, seeds, ownerId);
    }

    /**
     * If player's turn ended up in a small pit, his/her turn is finished
     *
     * @return false
     */
    @Override
    public boolean shouldPlayerKeepTurn() {
        return false;
    }
}
