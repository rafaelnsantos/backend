package tech.monx.game.effects.buffs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.monx.game.Player;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class BuffEvasionEffect implements Buff {
    private int remainingTurns;

    private double originalEvasion;

    @Override
    public String getName() {
        return "Buff Evasion";
    }

    @Override
    public void apply(Player player) throws RuntimeException {
        originalEvasion = player.getEvasion();

        player.setEvasion(100);
    }

    @Override
    public void remove(Player player) {
        player.setEvasion(originalEvasion);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public void tick(Player player) throws RuntimeException {
        remainingTurns--;
    }

}
