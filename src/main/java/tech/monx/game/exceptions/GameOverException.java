package tech.monx.game.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameOverException extends RuntimeException {
    private String id;
}
