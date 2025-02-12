package tech.monx.matchmaking;

import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import tech.monx.websocket.events.ServerEvent;
import tech.monx.websocket.model.dto.enums.WebsocketServerEventEnum;

import java.util.PriorityQueue;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class Queue {
    private static final PriorityQueue<PlayerMatchMaking> queue = new PriorityQueue<>();
    private static String gameId = null;
    private static int playerCount = 0;

    public void joinQueue(String playerId, WebSocketConnection connection) {
        log.debug("Joining {} to queue", playerId);

        queue.add(PlayerMatchMaking.builder()
                .id(playerId)
                .power(calcPlayerPower(playerId))
                .connectionId(connection.id())
                .build());
    }

    public void exitQueue(String playerId) {
        log.debug("Removing {} from queue", playerId);
        queue.removeIf(p -> p.getId().equals(playerId));
    }

    public void checkQueue(WebSocketConnection connection) {
        final int ROOM_SIZE = 2;
        if (queue.isEmpty() || (!isCreatingGame() && queue.size() < ROOM_SIZE)) {
            log.info("waiting players...");
            return;
        }

        if (gameId == null) {
            gameId = UUID.randomUUID().toString();
            log.info("Creating game {}", gameId);
        }

        var nextPlayer = queue.peek();

        assert nextPlayer != null;

        if (nextPlayer.getConnectionId().equals(connection.id())) {
            var event = ServerEvent.<String>builder()
                    .event(WebsocketServerEventEnum.GAME_FOUND)
                    .payload(gameId)
                    .build();

            connection.sendTextAndAwait(event);

            queue.poll();
            connection.closeAndAwait();
            playerCount++;
        }

        if (playerCount == ROOM_SIZE) {
            log.info("Game {} full", gameId);
            playerCount = 0;
            gameId = null;
        }
    }

    private boolean isCreatingGame() {
        return gameId != null;
    }

    private int calcPlayerPower(String playerId) {
        // TODO: calcular poder para matchmaking
        log.debug("Calculating power for player {}", playerId);
        return playerId.length() - playerId.replace("-", "").length();
    }
}
