package tech.monx.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerQueue implements Comparable<PlayerQueue> {
    private String playerId;
    private int speed;

    @Override
    public int compareTo(PlayerQueue o) {
        return Double.compare(o.getSpeed(), speed);
    }
}
