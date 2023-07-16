package com.bol.kalaha.controller.exception;

import com.bol.kalaha.exception.contributor.ExceptionInfoContributor;
import com.bol.kalaha.exception.game.GameException;
import com.bol.kalaha.exception.game.GameFinishedException;
import com.bol.kalaha.exception.game.GameNotFoundException;
import com.bol.kalaha.exception.player.PlayerNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionsHandler extends ResponseEntityExceptionHandler {

    private final ExceptionInfoContributor exceptionInfoContributor;

    public CustomExceptionsHandler(ExceptionInfoContributor exceptionInfoContributor) {
        this.exceptionInfoContributor = exceptionInfoContributor;
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    protected ResponseEntity<Object> handleWrongArguments(RuntimeException ex, WebRequest request) {
        exceptionInfoContributor.incrementIllegalArgumentExceptionCount();
        String responseBody = String.format("Provided arguments do not meet the criteria!\n Exception: %s", ex.getMessage());
        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {GameNotFoundException.class})
    protected ResponseEntity<Object> handleGameNotFoundExceptions(GameNotFoundException ex, WebRequest request) {
        exceptionInfoContributor.incrementGameNotFoundExceptionCount();
        String responseBody = "The requested game could not be found! Please try another game.";
        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {GameFinishedException.class})
    protected ResponseEntity<Object> handleGameFinishedExceptions(GameFinishedException ex, WebRequest request) {
        exceptionInfoContributor.incrementGameFinishedExceptionCount();
        String responseBody = "The requested game is over! Please try another game.";
        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = {GameException.class})
    protected ResponseEntity<Object> handleGameFinishedExceptions(GameException ex, WebRequest request) {
        exceptionInfoContributor.incrementGameExceptionCount();
        String responseBody = String.format("Something went wrong! Exception details: %s", ex.getMessage());
        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {PlayerNotFoundException.class})
    protected ResponseEntity<Object> handlePlayerNotFoundExceptions(PlayerNotFoundException ex, WebRequest request) {
        exceptionInfoContributor.incrementPlayerNotFoundExceptionCount();
        String responseBody = String.format("The requested player with id: %s could not be found!", ex.getPlayerId());
        return handleExceptionInternal(ex, responseBody, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

}
