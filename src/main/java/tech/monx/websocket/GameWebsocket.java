package tech.monx.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnError;

import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocketConnection;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import tech.monx.exceptions.NotMyTurnException;
import tech.monx.services.CurrentGameService;
import tech.monx.game.GameState;
import tech.monx.websocket.events.ClientEvent;
import tech.monx.websocket.events.ServerEvent;
import tech.monx.websocket.model.dto.BattleEventData;
import tech.monx.websocket.model.dto.enums.WebsocketClientEventEnum;
import tech.monx.websocket.model.dto.enums.WebsocketEventEnum;
import tech.monx.websocket.model.dto.enums.WebsocketServerEventEnum;
import tech.monx.websocket.model.enums.battle.BattleSkillsEnum;

@WebSocket(path = "/game/{gameId}")
@RequestScoped
@Slf4j
public class GameWebsocket {

    @Inject
    WebSocketConnection connection;

    @Inject
    @Claim(standard = Claims.sub)
    String playerId;

    @Inject
    CurrentGameService currentGameService;
    
    @Inject
    ObjectMapper objectMapper;

    @OnOpen
    @RolesAllowed("game")
    public void onOpen() {
        currentGameService.newPlayer(connection, playerId);

        broadcast(WebsocketServerEventEnum.PLAYER_JOINED);
    }

    @OnClose
    public void onClose() {
        currentGameService.removePlayer(playerId);

        broadcast(WebsocketServerEventEnum.PLAYER_LEFT);
    }

    @OnError
    public void onError(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
    }

    @OnTextMessage
    @RolesAllowed("game")
    public void onMessage(ClientEvent<BattleEventData> event) {
        log.info("Received battle event: {}", event);
        WebsocketEventEnum response;

        switch (event.getEvent()) {
            case WebsocketClientEventEnum.START -> {
                if (currentGameService.getGame().getStarted() || currentGameService.getGame().getPlayers().size() < 2) {
                    log.info("You cannot start the game");
                    return;
                }

                log.info("Game started");
                currentGameService.getGame().startGame();
                response = WebsocketServerEventEnum.GAME_START;
            }
            case WebsocketClientEventEnum.SKILL -> {
                checkTurn();

                var skill = BattleSkillsEnum.valueOf(event.getPayload().getSkill());
                currentGameService.getGame().playerAction(skill, event.getPayload().getTarget());
                response = skill;
            }
            default -> throw new IllegalStateException("Unexpected value: " + event.getEvent());
        }

        broadcast(response);
    }

    private void broadcast(WebsocketEventEnum response) {
        var data = ServerEvent.<GameState>builder()
                .response(response)
                .event(WebsocketServerEventEnum.GAME_STATE)
                .payload(currentGameService.getGame())
                .build();

        connection.broadcast()
                .filter(currentGameService.getGame()::isConnected)
                .sendTextAndAwait(data);
    }

    private void checkTurn() {
        if (!currentGameService.getGame().getStarted() || !currentGameService.getGame().getPlayerIdTurn().equals(playerId)) {
            throw new NotMyTurnException(playerId);
        }
    }
}
