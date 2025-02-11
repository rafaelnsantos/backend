package tech.monx.game;

import io.quarkus.websockets.next.WebSocketConnection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.monx.RandomWrapper;
import tech.monx.game.skills.Skill;
import tech.monx.game.exceptions.PlayerStunnedException;
import tech.monx.websocket.model.enums.battle.BattleSkillsEnum;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class GameState {
    private String id;

    @Builder.Default
    private List<Player> players = new ArrayList<>();

    @Builder.Default
    private GameQueue queue = GameQueue.builder().build();

    private String playerIdTurn;

    @Builder.Default
    private Boolean started = false;

    @Builder.Default
    private Boolean ended = false;

    private String winner;

    public void newConnection(WebSocketConnection connection, String playerId) {
        if (ended) {
            log.info("The game is over");
            return;
        }

        var player = players.stream().filter(p -> p.getId().equals(playerId)).findFirst();

        if (player.isPresent()) {
            if (player.get().isConnected()) {
                log.info("Already connected");
                connection.closeAndAwait();
            } else {
                log.info("Player {} reconnected", playerId );
                player.get().setConnection(connection.id());
            }

            return;
        }

        if (started) {
            log.info("Cannot enter started game");
            connection.closeAndAwait();
            return;
        }

        int randomSpeed = RandomWrapper.random.nextInt(5);

        log.info("New player {}", playerId);
        Player newPlayer = Player.builder()
                .id(playerId)
                .speed(randomSpeed)
                .health(100)
                .connection(connection.id())
                .build();

        players.add(newPlayer);
    }

    public void onPlayerDisconnected(String playerId) {
        var player = players.stream().filter(p -> p.getId().equals(playerId)).findFirst();

        assert player.isPresent();

        log.info("removing player {}", player);
        player.get().setConnection(null);
    }

    public Boolean isConnected(WebSocketConnection connection) {
        var player = players.stream().filter(p -> p.getConnection() != null && p.getConnection().equals(connection.id())).findFirst();

        return player.isPresent() && player.get().isConnected();
    }

    public void startGame() {
        started = true;
        addAllPlayerToQueue();
        startTurn();
    }

    private void addAllPlayerToQueue() {
        players.forEach(queue::add);
    }

    private void startTurn() {
        this.playerIdTurn = queue.poll();

        if (playerIdTurn == null) {
            addAllPlayerToQueue();
            startTurn();
            return;
        }

        var player = getPlayerById(playerIdTurn);

        try {
            player.onTurnStart();
        } catch (PlayerStunnedException e) {
            startTurn();
        }
    }

    private void playerAction(Skill skill, Player target) {
        var player = getCurrentPlayer();

        skill.run(player, target);

        startTurn();
    }

    private Player getCurrentPlayer() {
        var player = players.stream().filter(p -> p.getId().equals(playerIdTurn)).findFirst();
        assert player.isPresent();
        return player.get();
    }

    public void playerAction(BattleSkillsEnum skillEnum, String targetPlayerId) {
        var skill = getCurrentPlayer().getSkills().stream().filter(s -> s.getName().equals(skillEnum)).findFirst();

        log.info("skill {} target {}", skill, targetPlayerId);
        assert skill.isPresent();

        var targetPlayer = players.stream().filter(p -> p.getId().equals(targetPlayerId)).findFirst();

        assert targetPlayer.isPresent();

        playerAction(skill.get(), targetPlayer.get());
    }

    private Player getPlayerById(String playerId) {
        return players.stream().filter(p -> p.getId().equals(playerId)).findFirst().orElse(null);
    }

}
