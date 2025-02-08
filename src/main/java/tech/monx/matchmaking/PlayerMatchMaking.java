package tech.monx.matchmaking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerMatchMaking implements Comparable<PlayerMatchMaking> {
    private String id;
    private int power;
    private String connectionId;

    @Override
    public int compareTo(PlayerMatchMaking o) {
        return Integer.compare(power, o.power);
    }
}
