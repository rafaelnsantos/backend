package tech.monx.game.skills;

import lombok.Builder;
import tech.monx.game.Player;
import tech.monx.game.effects.buffs.BuffEvasionEffect;
import tech.monx.game.exceptions.GameOverException;
import tech.monx.websocket.model.enums.battle.BattleSkillsEnum;

@Builder
public class EvasionSkill implements Skill {
    @Override
    public void execute(Player actor, Player target) throws GameOverException {
        target.newEffect(BuffEvasionEffect.builder()
                        .remainingTurns(2)
                        .originalEvasion(target.getEvasion())
                        .build());
    }

    @Override
    public BattleSkillsEnum getName() {
        return BattleSkillsEnum.EVASION;
    }

    @Override
    public int getCost(Player actor, Player target) {
        return 10;
    }

    @Override
    public int getDamage(Player actor, Player target) {
        return 0;
    }
}
