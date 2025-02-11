package tech.monx.game.skills;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import tech.monx.RandomWrapper;
import tech.monx.game.Player;
import tech.monx.game.exceptions.GameOverException;
import tech.monx.websocket.model.enums.battle.BattleSkillsEnum;

@Slf4j
@Builder
public class AttackSkill implements Skill {

    @Override
    public void execute(Player actor, Player target) throws GameOverException {
        if (attackHits(actor.getAccuracy(), target.getEvasion())) {
            int damage = Math.max(0, actor.getAttack() - target.getDefense());

            if (critHits(actor.getCritChance())) {
                log.info("Critical! 2x");
                damage *= 2;
            }

            target.takeDamage(getDamage(actor, target) + damage);
        } else {
            log.info("Missed");
        }
    }

    @Override
    public BattleSkillsEnum getName() {
        return BattleSkillsEnum.ATTACK;
    }

    @Override
    public int getCost(Player actor, Player target) {
        return 0;
    }

    @Override
    public int getDamage(Player actor, Player target) {
        return 10;
    }

    private boolean attackHits(double accuracy, double evasion) {
        double hitChance = accuracy - evasion; // Higher accuracy increases hit chance, higher evasion decreases it
        double roll = RandomWrapper.random.nextDouble(1); // Generate a random number from 0 to 1
        log.info("hitChance {}\nroll {}", hitChance, roll);

        return roll < hitChance; // Attack hits if roll is less than the calculated hit chance
    }

    private boolean critHits(double critChance) {
        double roll = RandomWrapper.random.nextDouble(1);

        return roll < critChance;
    }
}
