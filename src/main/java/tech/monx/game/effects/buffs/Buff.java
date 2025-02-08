package tech.monx.game.effects.buffs;

import tech.monx.game.Player;
import tech.monx.game.effects.Effect;

public interface Buff extends Effect {
    void apply(Player player) throws RuntimeException;
    void remove(Player player);
}
