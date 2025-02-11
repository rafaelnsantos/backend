package tech.monx.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tech.monx.game.effects.Effect;
import tech.monx.game.effects.buffs.Buff;
import tech.monx.game.exceptions.OutOfManaException;
import tech.monx.game.skills.AttackSkill;
import tech.monx.game.skills.PoisonSkill;
import tech.monx.game.skills.Skill;
import tech.monx.game.skills.StunSkill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Player implements Serializable {
    private String id;
    @Builder.Default
    private int speed = 1;
    @Builder.Default
    private int health = 100;
    @Builder.Default
    private int attack = 20;
    @Builder.Default
    private int defense = 7;
    @Builder.Default
    private int mana = 10;
    @Builder.Default
    private double critChance = 0.1;
    @Builder.Default
    private double accuracy = 0.9;
    @Builder.Default
    private double evasion = 0.1;
    @Builder.Default
    private List<Effect> effects = new ArrayList<>();

    @Builder.Default
    private List<Skill> skills = List.of(
            AttackSkill.builder().build(),
            PoisonSkill.builder().build(),
            StunSkill.builder().build()
    );

    private String connection;

    public boolean isConnected() {
        return connection != null;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
        }
    }

    public void newEffect(Effect effect) {
        if (effect instanceof Buff buff) {
            buff.apply(this);
        }
        effects.add(effect);
    }

    @SneakyThrows
    private void runEffects() throws RuntimeException {
        effects.stream().sorted().forEach(this::runEffect);
    }

    private boolean haveEffect(Class<? extends Effect> effectClass) {
        return effects.stream().anyMatch(effect -> effect.getClass().equals(effectClass));
    }

    private void runEffect(Effect effect) throws RuntimeException {
        if (effect.isActive()) {
            effect.tick(this);
            return;
        }

        if (effect instanceof Buff buff) {
            buff.remove(this);
        }

        log.info("Effect {} is over", effect.getName());
        effects.remove(effect);
    }

    public void onTurnStart() {
        runEffects();
    }

    public void useMana(int mana) throws OutOfManaException {
        if (mana > this.mana) {
            throw new OutOfManaException();
        };

        this.mana -= mana;
    }
}
