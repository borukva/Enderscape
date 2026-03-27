package net.bunten.enderscape.entity.ai.goal;

import net.bunten.enderscape.entity.wraith.Wraith;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class WraithCombatFlyGoal extends Goal {
    public static final double ORBIT_RADIUS_MIN = 3.8;
    public static final double ORBIT_RADIUS_MAX = 7.0;
    private static final double ATTACK_APPROACH_DISTANCE = 3.0;
    private static final double ATTACK_ENGAGE_RADIUS = 1.2;

    private final Wraith wraith;
    private float orbitAngle;
    private float orbitDirection = 1.0F;

    public WraithCombatFlyGoal(Wraith wraith) {
        this.wraith = wraith;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = wraith.getTarget();
        return target != null && target.isAlive() && wraith.getAttackAnimationTicks() <= 0;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = wraith.getTarget();
        return target != null && target.isAlive() && wraith.getAttackAnimationTicks() <= 0;
    }

    @Override
    public void start() {
        orbitAngle = wraith.getRandom().nextFloat() * Mth.TWO_PI;
        orbitDirection = wraith.getRandom().nextBoolean() ? 1.0F : -1.0F;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = wraith.getTarget();
        if (target == null) {
            return;
        }

        wraith.getLookControl().setLookAt(target, 30.0F, 30.0F);

        orbitAngle += 0.09F * orbitDirection;
        if (wraith.getRandom().nextInt(reducedTickDelay(80)) == 0) {
            orbitDirection *= -1.0F;
        }

        double distance = Math.sqrt(wraith.distanceToSqr(target));
        double desiredRadius = Mth.clamp(distance, ORBIT_RADIUS_MIN, ORBIT_RADIUS_MAX);
        if (wraith.shouldRetreatNow()) {
            desiredRadius = ORBIT_RADIUS_MAX;
        } else if (wraith.getAttackCooldown() <= 0 && wraith.getPendingDamageTicks() <= 0) {
            // Force a close-in approach when attack window opens, instead of pure orbiting.
            desiredRadius = ATTACK_ENGAGE_RADIUS;
        } else if (distance < ATTACK_APPROACH_DISTANCE) {
            // Keep out of player melee reach while not actively attacking.
            desiredRadius = ORBIT_RADIUS_MIN + 0.4;
        }

        double targetX = target.getX() + Math.cos(orbitAngle) * desiredRadius;
        double targetY = Mth.clamp(wraith.getY(), target.getY() - 0.5, target.getY() + 0.5);
        double targetZ = target.getZ() + Math.sin(orbitAngle) * desiredRadius;
        double speed = wraith.getAttackCooldown() <= 0 ? 1.15 : 0.72;
        wraith.getMoveControl().setWantedPosition(targetX, targetY, targetZ, speed);
    }
}
