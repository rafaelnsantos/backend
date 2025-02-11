package tech.monx.game.skills;

import tech.monx.game.Player;
import tech.monx.game.exceptions.GameOverException;
import tech.monx.game.exceptions.OutOfManaException;
import tech.monx.websocket.model.enums.battle.BattleSkillsEnum;

public interface Skill {
    void execute(Player actor, Player target) throws GameOverException;
    BattleSkillsEnum getName();
    int getCost(Player actor, Player target);
    int getDamage(Player actor, Player target);

    default void run(Player actor, Player target) {
        try {
            actor.useMana(getCost(actor, target));
            execute(actor, target);
        } catch (OutOfManaException ignored) {
        }
    }
}
