package net.bunten.enderscape.entity.ai.goal;

import net.bunten.enderscape.entity.watchman.Watchman;
import net.bunten.enderscape.registry.EnderscapeEntitySounds;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class WatchmanSummonWraithsGoal extends Goal {

    private final Watchman watchman;

    public WatchmanSummonWraithsGoal(Watchman watchman) {
        this.watchman = watchman;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (watchman.level().getDifficulty() == Difficulty.PEACEFUL) {
            return false;
        }
        if (watchman.getAttackCooldown() > 0 || watchman.getAttackAnimationTicks() > 0 || watchman.getSummonCooldown() > 0) {
            return false;
        }
        LivingEntity target = watchman.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        return watchman.distanceToSqr(target) < 28.0 * 28.0 && watchman.getRandom().nextInt(160) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return watchman.getAttackAnimationTicks() > 0 && watchman.getState() == Watchman.State.SUMMON_WRAITHS;
    }

    @Override
    public void start() {
        watchman.getNavigation().stop();
        LivingEntity target = watchman.getTarget();
        if (target != null) {
            watchman.faceForAttack(target);
        }
        watchman.startActionState(Watchman.State.SUMMON_WRAITHS, Watchman.SUMMON_DURATION_TICKS);
        watchman.setAttackCooldown(120);
        watchman.setSummonCooldown(1000);
        watchman.playSound(EnderscapeEntitySounds.WATCHMAN_SUMMON_WRAITHS, 1.2F, watchman.randomPitch());
    }

    @Override
    public void tick() {
        LivingEntity target = watchman.getTarget();
        if (target != null) {
            watchman.faceForAttack(target);
        }
    }
}
