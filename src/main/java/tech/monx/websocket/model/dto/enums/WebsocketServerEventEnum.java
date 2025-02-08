package tech.monx.websocket.model.dto.enums;

import lombok.AllArgsConstructor;

public enum WebsocketServerEventEnum implements WebsocketEventEnum {
    GAME_FOUND,
    PLAYER_JOINED,
    PLAYER_LEFT,
    GAME_START,
    GAME_STATE,
}
