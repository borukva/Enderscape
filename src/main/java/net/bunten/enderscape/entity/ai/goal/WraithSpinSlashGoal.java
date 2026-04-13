package net.bunten.enderscape.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.bunten.enderscape.entity.wraith.Wraith;
import net.bunten.enderscape.registry.EnderscapeEntitySounds;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class WraithSpinSlashGoal extends Goal {

    private static final double SPIN_START_RANGE_SQ = 4.0 * 4.0;
    private static final double SPIN_RANGE_HORIZONTAL_SQ = 2.1 * 2.1;
    private static final double SPIN_VERTICAL_TOLERANCE = 2.0;
    private static final int SPIN_DURATION_TICKS = 40;
    private static final int DAMAGE_AT_TICK = 6;
    private static final int COOLDOWN_TICKS = 58;
    private static final float DAMAGE_MULTIPLIER = 1.35F;
    private static final double ATTACK_FOLLOW_DISTANCE = 0.85;

    private final Wraith wraith;
    private int spinTicks;
    private boolean dealtDamage;

    public WraithSpinSlashGoal(Wraith wraith) {
        this.wraith = wraith;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (wraith.getAttackCooldown() > 0 || wraith.shouldRetreatNow()) {
            return false;
        }
        LivingEntity target = wraith.getTarget();
        return target != null && target.isAlive()
                && wraith.distanceToSqr(target) < SPIN_START_RANGE_SQ
                && wraith.getRandom().nextInt(reducedTickDelay(5)) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return spinTicks < SPIN_DURATION_TICKS
                && wraith.getTarget() != null
                && wraith.getTarget().isAlive();
    }

    @Override
    public void start() {
        spinTicks = 0;
        dealtDamage = false;
        wraith.startAttackState(Wraith.State.SPIN_SLASH, SPIN_DURATION_TICKS);
        wraith.playSound(EnderscapeEntitySounds.WRAITH_SPIN_SLASH, 1.0F, wraith.randomPitch());
        wraith.clearMoveTarget();
        wraith.setAttackCooldown(COOLDOWN_TICKS);
    }

    @Override
    public void stop() {
        if (wraith.getAttackAnimationTicks() <= 0) {
            wraith.setState(Wraith.State.IDLE);
        }
    }

    @Override
    public void tick() {
        spinTicks++;
        LivingEntity target = wraith.getTarget();
        if (target != null) {
            wraith.getLookControl().setLookAt(target, 35.0F, 35.0F);
            Vec3 toTarget = target.position().subtract(wraith.position());
            Vec3 followDir = toTarget.lengthSqr() > 1.0E-4 ? toTarget.normalize() : new Vec3(0, 0, 0);
            Vec3 desired = target.position().subtract(followDir.scale(ATTACK_FOLLOW_DISTANCE));
            wraith.getMoveControl().setWantedPosition(
                    desired.x,
                    target.getY() + 0.25,
                    desired.z,
                    1.3
            );
        }

        if (!dealtDamage && spinTicks >= DAMAGE_AT_TICK) {
            if (target != null && target.isAlive() && isWithinSpinHitRange(target)) {
                if (wraith.doScaledHurtTarget(getServerLevel(wraith.level()), target, DAMAGE_MULTIPLIER)) {
                    wraith.recordStrongHit();
                    dealtDamage = true;
                }
            }
        }
    }

    private boolean isWithinSpinHitRange(LivingEntity target) {
        double dx = target.getX() - wraith.getX();
        double dz = target.getZ() - wraith.getZ();
        double horizontalSqr = dx * dx + dz * dz;
        double vertical = Math.abs(target.getY() - wraith.getY());
        return horizontalSqr <= SPIN_RANGE_HORIZONTAL_SQ && vertical <= SPIN_VERTICAL_TOLERANCE;
    }
}
