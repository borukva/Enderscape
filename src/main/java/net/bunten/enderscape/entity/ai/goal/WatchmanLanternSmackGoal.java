package net.bunten.enderscape.entity.ai.goal;

import net.bunten.enderscape.entity.watchman.Watchman;
import net.bunten.enderscape.registry.EnderscapeEntitySounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class WatchmanLanternSmackGoal extends Goal {

    public static final double MELEE_RANGE = 3.5;
    public static final double MELEE_RANGE_SQ = MELEE_RANGE * MELEE_RANGE;

    private final Watchman watchman;

    public WatchmanLanternSmackGoal(Watchman watchman) {
        this.watchman = watchman;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (watchman.getAttackCooldown() > 0 || watchman.getAttackAnimationTicks() > 0) {
            return false;
        }
        return watchman.findClosestLanternSmackVictim(MELEE_RANGE_SQ) != null;
    }

    @Override
    public boolean canContinueToUse() {
        return watchman.getAttackAnimationTicks() > 0 && watchman.getState() == Watchman.State.LANTERN_SMACK;
    }

    @Override
    public void start() {
        watchman.getNavigation().stop();
        watchman.startActionState(Watchman.State.LANTERN_SMACK, Watchman.SMACK_DURATION_TICKS);
        watchman.setAttackCooldown(35);
        watchman.playSound(EnderscapeEntitySounds.WATCHMAN_LANTERN_SMACK, 1.0F, watchman.randomPitch());
        LivingEntity focus = watchman.findClosestLanternSmackVictim(MELEE_RANGE_SQ);
        if (focus != null) {
            watchman.faceForAttack(focus);
        }
    }

    @Override
    public void tick() {
        LivingEntity focus = watchman.findClosestLanternSmackVictim(MELEE_RANGE_SQ);
        if (focus != null) {
            watchman.faceForAttack(focus);
        }
    }
}
