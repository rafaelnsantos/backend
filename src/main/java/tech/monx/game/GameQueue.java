package tech.monx.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.PriorityQueue;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameQueue {
    private static final double TURN_TIME_MULTIPLIER = 10;

    @Builder.Default
    private PriorityQueue<PlayerQueue> queue = new PriorityQueue<>();

    @Builder.Default
    private double currentTurn = 0;

    public void add(Player player) {
        queue.add(PlayerQueue.builder()
                        .playerId(player.getId())
                        .speed(player.getSpeed())
                        .build());
    }

    public String poll() {
        var player = queue.poll();

        currentTurn++;
        return player == null ? null : player.getPlayerId();
    }
}
