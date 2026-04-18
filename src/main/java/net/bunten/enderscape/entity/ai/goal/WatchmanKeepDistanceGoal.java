package net.bunten.enderscape.entity.ai.goal;

import net.bunten.enderscape.entity.watchman.Watchman;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

/**
 * Backs away from the target when closer than the preferred range (kiting).
 * <p>Uses {@linkplain net.minecraft.world.entity.ai.navigation.PathNavigation pathfinding} to destinations on the
 * same foot {@link net.minecraft.world.entity.Entity#blockPosition() block Y} with solid support below (End-city
 * style platforms), instead of {@code MoveControl} straight lines that walk off ledges.
 * <p>Facing matches {@link net.minecraft.world.entity.ai.goal.RangedBowAttackGoal}: while repositioning,
 * call {@link net.minecraft.world.entity.Mob#lookAt} on the server so {@code yRot} syncs to clients and
 * {@link net.minecraft.world.entity.ai.control.BodyRotationControl} keeps the body toward the real target.
 */
public class WatchmanKeepDistanceGoal extends Goal {

    private static final double MIN_COMFORT_DISTANCE = 7.0;
    private static final double MIN_COMFORT_DISTANCE_SQ = MIN_COMFORT_DISTANCE * MIN_COMFORT_DISTANCE;
    private static final int REPATH_BASE_TICKS = 14;
    private static final int REPATH_JITTER_TICKS = 10;
    private static final int REPATH_RETRY_ON_FAIL_TICKS = 5;

    private final Watchman watchman;
    private int repathCooldown;

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
    public void start() {
        repathCooldown = 0;
    }

    @Override
    public void stop() {
        watchman.getNavigation().stop();
    }

    @Override
    public void tick() {
        LivingEntity target = watchman.getTarget();
        if (target == null) {
            return;
        }
        watchman.lookAt(target, 30.0F, 30.0F);
        if (repathCooldown > 0) {
            repathCooldown--;
        }
        boolean needRepath = repathCooldown <= 0 || !watchman.getNavigation().isInProgress();
        if (needRepath) {
            if (trySetKitePath(target)) {
                repathCooldown = REPATH_BASE_TICKS + watchman.getRandom().nextInt(REPATH_JITTER_TICKS);
            } else {
                repathCooldown = REPATH_RETRY_ON_FAIL_TICKS;
            }
        }
    }

    /**
     * Picks a horizontal offset on the watchman's current foot layer; prefers directions away from the target but
     * jitters so kiting is not always a straight line. Does not scan down columns (avoids routing onto lower roofs
     * / into caves under thin floors).
     */
    private boolean trySetKitePath(LivingEntity target) {
        Level level = watchman.level();
        int feetY = watchman.blockPosition().getY();
        double awayAngle = Mth.atan2(watchman.getZ() - target.getZ(), watchman.getX() - target.getX());
        for (int attempt = 0; attempt < 28; attempt++) {
            double spread = attempt < 12 ? Mth.PI * 0.65 : Mth.TWO_PI;
            double angle = awayAngle + (watchman.getRandom().nextDouble() - 0.5) * spread;
            double dist = 4.0 + watchman.getRandom().nextDouble() * 3.5;
            int bx = Mth.floor(watchman.getX() + Mth.cos((float) angle) * dist);
            int bz = Mth.floor(watchman.getZ() + Mth.sin((float) angle) * dist);
            if (!isWalkableStandColumn(level, bx, feetY, bz)) {
                continue;
            }
            if (watchman.getNavigation().moveTo(bx + 0.5, feetY, bz + 0.5, 1.2)) {
                return true;
            }
        }
        return false;
    }

    /** Same-layer stand check aligned with {@link net.bunten.enderscape.entity.enderling.Enderling} dodge columns. */
    private static boolean isWalkableStandColumn(Level level, int x, int feetY, int z) {
        BlockPos feetPos = new BlockPos(x, feetY, z);
        BlockPos below = feetPos.below();
        BlockPos headPos = feetPos.above();
        BlockState belowState = level.getBlockState(below);
        BlockState feetState = level.getBlockState(feetPos);
        BlockState headState = level.getBlockState(headPos);
        return belowState.isCollisionShapeFullBlock(level, below)
                && !belowState.getFluidState().is(FluidTags.WATER)
                && !feetState.isCollisionShapeFullBlock(level, feetPos)
                && !headState.isCollisionShapeFullBlock(level, headPos)
                && !feetState.getFluidState().is(FluidTags.WATER)
                && !headState.getFluidState().is(FluidTags.WATER);
    }
}
