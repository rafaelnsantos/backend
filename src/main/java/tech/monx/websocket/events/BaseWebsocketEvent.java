package tech.monx.websocket.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tech.monx.websocket.model.dto.enums.WebsocketEventEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseWebsocketEvent<A extends WebsocketEventEnum, T> implements WebsocketEvent<A, T> {
    private A event;
    private T payload;
}
