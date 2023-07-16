package com.bol.kalaha.util;

import com.bol.kalaha.dto.pit.SmallPitDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PitUtils {

    public static String getPitValuesFromPitList(List<SmallPitDto> smallPitDtos) {
        return smallPitDtos.stream().map(pit -> String.valueOf(pit.getSeeds()))
                .collect(Collectors.joining(","));
    }

    public static List<SmallPitDto> getSmallPitsFromString(String smallPitsValues, Integer ownerId) {
        String[] seedsArray = smallPitsValues.split(",");
        List<SmallPitDto> smallPitDtos = new ArrayList<>();
        for (int i = 0; i < seedsArray.length; i++) {
            int seeds = Integer.parseInt(seedsArray[i]);
            smallPitDtos.add(new SmallPitDto(i, seeds, ownerId));
        }
        return smallPitDtos;
    }

}
