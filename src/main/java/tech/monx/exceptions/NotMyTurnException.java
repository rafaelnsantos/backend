package tech.monx.exceptions;

public class NotMyTurnException extends RuntimeException {

    public NotMyTurnException(String playerId) {
        super(playerId);
    }
}
