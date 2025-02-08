package tech.monx.game.effects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.monx.game.Player;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PoisonEffect implements Effect {
    private int remainingTurns;
    private int damagePerTurn;

    @Override
    public String getName() {
        return "Poison";
    }

    @Override
    public boolean isActive() {
        return remainingTurns > 0;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void tick(Player player) throws RuntimeException {
        remainingTurns--;
        log.info("{} lost {} to poison, remaining {} turns", player.getId(), damagePerTurn, remainingTurns);
        player.takeDamage(damagePerTurn);
    }
}
