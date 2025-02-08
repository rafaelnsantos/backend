package tech.monx.game.skills;

import tech.monx.game.Player;
import tech.monx.game.exceptions.GameOverException;
import tech.monx.websocket.model.enums.battle.BattleSkillsEnum;

public interface Skill {
    void execute(Player actor, Player target) throws GameOverException;
    BattleSkillsEnum getName();
    int getCost();

    default int getBaseDamage() {
        return 0;
    }
}
