package net.bunten.enderscape.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.bunten.enderscape.entity.wraith.Wraith;

import java.util.EnumSet;

public class WraithRetreatGoal extends Goal {

    private static final double RETREAT_DISTANCE = 6.0;

    private final Wraith wraith;

    public WraithRetreatGoal(Wraith wraith) {
        this.wraith = wraith;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!wraith.shouldRetreatNow()) {
            return false;
        }
        LivingEntity target = wraith.getTarget();
        return target != null && target.isAlive() && wraith.distanceToSqr(target) < RETREAT_DISTANCE * RETREAT_DISTANCE;
    }

    @Override
    public boolean canContinueToUse() {
        return wraith.getTarget() != null
                && wraith.getTarget().isAlive()
                && wraith.getMoveControl().hasWanted();
    }

    @Override
    public void start() {
        LivingEntity target = wraith.getTarget();
        if (target != null) {
            Vec3 away = wraith.position().subtract(target.position()).normalize().scale(RETREAT_DISTANCE);
            double x = wraith.getX() + away.x;
            double y = target.getY() + (wraith.getRandom().nextBoolean() ? 0.5 : -0.5);
            double z = wraith.getZ() + away.z;
            wraith.getMoveControl().setWantedPosition(x, y, z, 0.95);
            wraith.resetComboWindow();
            wraith.setAttackCooldown(Math.max(wraith.getAttackCooldown(), 18));
        }
    }

    @Override
    public void tick() {
        LivingEntity target = wraith.getTarget();
        if (target != null) {
            wraith.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }
    }
}
