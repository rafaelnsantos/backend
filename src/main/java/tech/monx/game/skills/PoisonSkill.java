package tech.monx.game.skills;

import lombok.Builder;
import tech.monx.game.Player;
import tech.monx.game.effects.PoisonEffect;
import tech.monx.game.exceptions.GameOverException;
import tech.monx.websocket.model.enums.battle.BattleSkillsEnum;

@Builder
public class PoisonSkill implements Skill {
    @Override
    public void execute(Player actor, Player target) throws GameOverException {
        target.newEffect(PoisonEffect.builder()
                        .damagePerTurn(calculateDamage(actor, target))
                        .remainingTurns(10)
                        .build());
    }

    @Override
    public BattleSkillsEnum getName() {
        return BattleSkillsEnum.POISON;
    }

    @Override
    public int getCost() {
        return 10;
    }


    private int calculateDamage(Player actor, Player target) {
        // TODO: calcs
        return 2;
    }
}
