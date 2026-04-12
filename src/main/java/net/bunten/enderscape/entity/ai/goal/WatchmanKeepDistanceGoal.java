package net.bunten.enderscape.entity.ai.goal;

import net.bunten.enderscape.entity.watchman.Watchman;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Backs away from the target when closer than the preferred range (kiting).
 * <p>Facing matches {@link net.minecraft.world.entity.ai.goal.RangedBowAttackGoal}: while repositioning,
 * call {@link net.minecraft.world.entity.Mob#lookAt} on the server so {@code yRot} syncs to clients and
 * {@link net.minecraft.world.entity.ai.control.BodyRotationControl} keeps the body toward the real target.
 */
public class WatchmanKeepDistanceGoal extends Goal {

    private static final double MIN_COMFORT_DISTANCE = 7.0;
    private static final double MIN_COMFORT_DISTANCE_SQ = MIN_COMFORT_DISTANCE * MIN_COMFORT_DISTANCE;

    private final Watchman watchman;

    public WatchmanKeepDistanceGoal(Watchman watchman) {
        this.watchman = watchman;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (watchman.getAttackAnimationTicks() > 0) {
            return false;
        }
        LivingEntity target = watchman.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        double d = watchman.distanceToSqr(target);
        return d < MIN_COMFORT_DISTANCE_SQ && d > WatchmanLanternSmackGoal.MELEE_RANGE_SQ;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        LivingEntity target = watchman.getTarget();
        if (target == null) {
            return;
        }
        watchman.lookAt(target, 30.0F, 30.0F);
        Vec3 away = watchman.position().subtract(target.position());
        if (away.lengthSqr() < 1e-4) {
            away = new Vec3(watchman.getRandom().nextGaussian(), 0, watchman.getRandom().nextGaussian());
        }
        away = new Vec3(away.x, 0, away.z).normalize().scale(6.0);
        double x = watchman.getX() + away.x;
        double y = watchman.getY();
        double z = watchman.getZ() + away.z;
        watchman.getMoveControl().setWantedPosition(x, y, z, 1.2);
    }
}
