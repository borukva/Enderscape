package net.bunten.enderscape.entity.ai.goal;

import net.bunten.enderscape.entity.enderling.Enderling;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class EnderlingChaseGoal extends Goal {
    /** Stop moving when this close; slash goal uses a slightly larger range so attacks start near melee. */
    private static final double STOP_DISTANCE_SQ = 1.15 * 1.15;
    private static final double CHASE_SPEED = 1.28;
    private static final double MOVING_THRESHOLD_SQ = 1.0E-5;

    private final Enderling enderling;

    public EnderlingChaseGoal(Enderling enderling) {
        this.enderling = enderling;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = enderling.getTarget();
        return target != null && target.isAlive() && enderling.getAttackAnimationTicks() <= 0;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = enderling.getTarget();
        return target != null && target.isAlive() && enderling.getAttackAnimationTicks() <= 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void stop() {
        enderling.getNavigation().stop();
        if (enderling.getAttackAnimationTicks() <= 0) {
            enderling.setState(Enderling.State.IDLE);
        }
    }

    @Override
    public void tick() {
        LivingEntity target = enderling.getTarget();
        if (target == null) {
            enderling.getNavigation().stop();
            enderling.setState(Enderling.State.IDLE);
            return;
        }

        enderling.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double distanceSqr = enderling.distanceToSqr(target);
        if (distanceSqr <= STOP_DISTANCE_SQ) {
            enderling.getNavigation().stop();
            enderling.setState(Enderling.State.IDLE);
            return;
        }

        enderling.getNavigation().moveTo(target, CHASE_SPEED);
        double motionX = enderling.getDeltaMovement().x;
        double motionZ = enderling.getDeltaMovement().z;
        if ((motionX * motionX + motionZ * motionZ) > MOVING_THRESHOLD_SQ) {
            enderling.setState(Enderling.State.CHASE);
        } else {
            enderling.setState(Enderling.State.IDLE);
        }
    }
}
