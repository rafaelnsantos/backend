package tech.monx.game.effects;

import tech.monx.game.Player;

public interface Effect extends Comparable<Effect> {
    String getName();
    boolean isActive();
    int getPriority();

    void tick(Player player) throws RuntimeException;

    default int compareTo(Effect other) {
        return Integer.compare(getPriority(), other.getPriority());
    }
}
