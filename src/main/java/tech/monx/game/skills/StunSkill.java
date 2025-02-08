package tech.monx.game.skills;

import lombok.Builder;
import tech.monx.game.Player;
import tech.monx.game.effects.StunEffect;
import tech.monx.game.exceptions.GameOverException;
import tech.monx.websocket.model.enums.battle.BattleSkillsEnum;

@Builder
public class StunSkill implements Skill {
    @Override
    public void execute(Player actor, Player target) throws GameOverException {
        target.newEffect(StunEffect.builder()
                        .remainingTurns(2)
                        .build());
    }

    @Override
    public BattleSkillsEnum getName() {
        return BattleSkillsEnum.STUN;
    }

    @Override
    public int getCost() {
        return 20;
    }
}
