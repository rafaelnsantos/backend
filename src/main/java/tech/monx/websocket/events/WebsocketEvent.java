package tech.monx.websocket.events;

import tech.monx.websocket.model.dto.enums.WebsocketEventEnum;

import java.io.Serializable;

public interface WebsocketEvent<A extends WebsocketEventEnum, T> extends Serializable {
    A getEvent();
    T getPayload();
}
