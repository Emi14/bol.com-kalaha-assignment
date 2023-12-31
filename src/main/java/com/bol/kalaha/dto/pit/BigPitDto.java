package com.bol.kalaha.dto.pit;

/**
 * Class used to define the attributes and the behaviour of a big pit.
 * On each board there are 2 big pits, one for each player.
 */
public class BigPitDto extends PitDto {

    public BigPitDto(Integer seeds, Integer ownerId) {
        super(null, seeds, ownerId);
    }

    /**
     * If player's turn ended up in his own big pit, he/she can continue playing
     *
     * @return true
     */
    @Override
    public boolean shouldPlayerKeepTurn() {
        return true;
    }

    /**
     * A big pit can sow multiple seeds at one time. This method adds the given seeds to the pit.
     *
     * @param numberOfSeedsToBeSow - number of seeds to be added to the pit
     */
    public void sow(int numberOfSeedsToBeSow) {
        int numberOfSeeds = getSeeds();
        setSeeds(numberOfSeeds + numberOfSeedsToBeSow);
    }

    /**
     * Big pit cannot be emptied
     *
     * @return 0.
     */
    @Override
    public int takeAllSeeds() {
        return 0;
    }
}
