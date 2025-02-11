package tech.monx.game.skills;

import lombok.Builder;
import tech.monx.game.Player;
import tech.monx.game.effects.PoisonEffect;
import tech.monx.websocket.model.enums.battle.BattleSkillsEnum;

@Builder
public class PoisonSkill implements Skill {
    @Override
    public void execute(Player actor, Player target) {
        target.newEffect(PoisonEffect.builder()
                        .damagePerTurn(getDamage(actor, target))
                        .remainingTurns(10)
                        .build());
    }

    @Override
    public BattleSkillsEnum getName() {
        return BattleSkillsEnum.POISON;
    }

    @Override
    public int getCost(Player actor, Player target) {
        return 10;
    }

    @Override
    public int getDamage(Player actor, Player target) {
        return 2;
    }
}
