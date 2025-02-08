package tech.monx.websocket.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import tech.monx.websocket.model.dto.enums.WebsocketEventEnum;
import tech.monx.websocket.model.dto.enums.WebsocketServerEventEnum;

@SuperBuilder
@Getter
public class ServerEvent <T> extends BaseWebsocketEvent<WebsocketServerEventEnum, T> {
    private WebsocketEventEnum response;
}
