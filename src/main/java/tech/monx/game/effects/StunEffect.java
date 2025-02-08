package tech.monx.game.effects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.monx.game.Player;
import tech.monx.game.exceptions.PlayerStunnedException;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StunEffect implements Effect {
    private int remainingTurns;

    @Override
    public String getName() {
        return "Stun";
    }

    @Override
    public boolean isActive() {
        return remainingTurns > 0;
    }

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    public void tick(Player player) throws RuntimeException {
        remainingTurns--;
        log.info("Stunned, remaining turns: {}", remainingTurns);
        throw new PlayerStunnedException();
    }

}
