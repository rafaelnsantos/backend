package tech.monx.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import tech.monx.matchmaking.Queue;
import tech.monx.websocket.events.ClientEvent;
import tech.monx.websocket.model.dto.enums.WebsocketClientEventEnum;

@Slf4j
@WebSocket(path = "/matchmaking")
@RequestScoped
public class MatchMakingWebsocket {

    @Inject
    Queue matchMaking;

    @Inject
    WebSocketConnection connection;

    @Inject
    @Claim(standard = Claims.sub)
    String playerId;

    @Inject
    ObjectMapper objectMapper;

    @OnOpen
    @RolesAllowed("player")
    public void joinQueue() {
        matchMaking.joinQueue(playerId, connection);
    }

    @OnTextMessage
    public void onTextMessage(ClientEvent event) {
        assert event.getEvent() != null;

        switch (event.getEvent()) {
            case WebsocketClientEventEnum.CHECK -> matchMaking.checkQueue(connection);
            default -> throw new IllegalStateException("Unexpected value: " + event.getEvent());
        }
    }

    @OnClose
    public void onClose() {
        matchMaking.exitQueue(playerId);
    }

}
