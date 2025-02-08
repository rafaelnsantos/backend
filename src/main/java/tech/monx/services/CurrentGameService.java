package tech.monx.services;

import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import tech.monx.game.GameState;
import tech.monx.game.Player;

import java.util.HashMap;
import java.util.Map;

@RequestScoped
@Slf4j
public class CurrentGameService {
    private static final Map<String, GameState> games = new HashMap<>();

    @Getter
    private GameState game;
    
    @Inject
    @Claim(standard = Claims.address)
    String gameId;
    
    @PostConstruct
    public void init() {
        if (games.containsKey(gameId)) {
            log.info("Game {} found", gameId);
            game = games.get(gameId);
        } else {
            log.info("Creating new game state {}", gameId);
            game = GameState.builder()
                    .id(gameId)
                    .build();

            games.put(gameId, game);
        }
    }

    public void newPlayer(WebSocketConnection connection, String playerId) {
        game.newConnection(connection, playerId);
    }

    public void removePlayer(String playerId) {
        log.info("Removing player {} from {}", playerId, gameId);
        game.onPlayerDisconnected(playerId);

        // if there are no more connections
        if (this.game.getPlayers().stream().noneMatch(Player::isConnected)) {
            log.info("Removing game state");
            games.remove(gameId);
            log.info("games {}", games.size());
        }
    }

}
