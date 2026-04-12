package net.bunten.enderscape.entity.ai.goal;

import net.bunten.enderscape.entity.watchman.Watchman;
import net.bunten.enderscape.registry.EnderscapeEntitySounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class WatchmanLanternPushGoal extends Goal {

    private static final double MIN_RANGE = 4.0;
    private static final double MIN_RANGE_SQ = MIN_RANGE * MIN_RANGE;
    private static final double MAX_RANGE_SQ = 28.0 * 28.0;

    private final Watchman watchman;

    public WatchmanLanternPushGoal(Watchman watchman) {
        this.watchman = watchman;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (watchman.getAttackCooldown() > 0 || watchman.getAttackAnimationTicks() > 0 || watchman.getFireballCooldown() > 0) {
            return false;
        }
        LivingEntity target = watchman.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        double d = watchman.distanceToSqr(target);
        return d >= MIN_RANGE_SQ && d <= MAX_RANGE_SQ && watchman.getRandom().nextInt(20) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return watchman.getAttackAnimationTicks() > 0 && watchman.getState() == Watchman.State.LANTERN_PUSH;
    }

    @Override
    public void start() {
        watchman.getNavigation().stop();
        watchman.startActionState(Watchman.State.LANTERN_PUSH, Watchman.LANTERN_PUSH_DURATION_TICKS);
        watchman.setAttackCooldown(25);
        watchman.setFireballCooldown(45);
        watchman.playSound(EnderscapeEntitySounds.WATCHMAN_LANTERN_PUSH, 1.0F, watchman.randomPitch());
        LivingEntity target = watchman.getTarget();
        if (target != null) {
            watchman.faceForAttack(target);
        }
    }

    @Override
    public void tick() {
        LivingEntity target = watchman.getTarget();
        if (target != null) {
            watchman.faceForAttack(target);
        }
    }
}
