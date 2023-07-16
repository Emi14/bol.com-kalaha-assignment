package com.bol.kalaha.exception.contributor;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExceptionInfoContributor implements InfoContributor {

    private int gameExceptionCount;
    private int gameFinishedExceptionCount;
    private int gameNotFoundExceptionCount;
    private int playerNotFoundExceptionCount;
    private int illegalArgumentExceptionCount;

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Integer> exceptionStats = new HashMap<>();
        exceptionStats.put("gameExceptionCount", gameExceptionCount);
        exceptionStats.put("gameFinishedExceptionCount", gameFinishedExceptionCount);
        exceptionStats.put("gameNotFoundExceptionCount", gameNotFoundExceptionCount);
        exceptionStats.put("playerNotFoundExceptionCount", playerNotFoundExceptionCount);
        exceptionStats.put("illegalArgumentExceptionCount", illegalArgumentExceptionCount);

        builder.withDetail("exceptionCounters", exceptionStats);
    }

    public void incrementGameExceptionCount() {
        gameExceptionCount++;
    }

    public void incrementGameFinishedExceptionCount() {
        gameFinishedExceptionCount++;
    }

    public void incrementGameNotFoundExceptionCount() {
        gameNotFoundExceptionCount++;
    }

    public void incrementPlayerNotFoundExceptionCount() {
        playerNotFoundExceptionCount++;
    }

    public void incrementIllegalArgumentExceptionCount() {
        illegalArgumentExceptionCount++;
    }
}
