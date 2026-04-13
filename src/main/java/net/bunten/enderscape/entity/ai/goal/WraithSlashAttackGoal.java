package net.bunten.enderscape.entity.ai.goal;

import net.bunten.enderscape.entity.wraith.Wraith;
import net.bunten.enderscape.registry.EnderscapeEntitySounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class WraithSlashAttackGoal extends Goal {

    private static final double SLASH_START_RANGE_SQ = 4.5 * 4.5;
    private static final double SLASH_RANGE_HORIZONTAL_SQ = 2.1 * 2.1;
    private static final double SLASH_VERTICAL_TOLERANCE = 2.0;
    private static final int SLASH_DURATION_TICKS = 24;
    private static final int DAMAGE_AT_TICK = 8;
    private static final int COOLDOWN_TICKS = 24;
    private static final double ATTACK_FOLLOW_DISTANCE = 0.9;

    private final Wraith wraith;
    private int slashTicks;
    private boolean useRightSlash = true;
    private boolean dealtDamage;

    public WraithSlashAttackGoal(Wraith wraith) {
        this.wraith = wraith;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (wraith.getAttackCooldown() > 0 || wraith.shouldRetreatNow()) {
            return false;
        }
        LivingEntity target = wraith.getTarget();
        return target != null
                && target.isAlive()
                && wraith.distanceToSqr(target) <= SLASH_START_RANGE_SQ;
    }

    @Override
    public boolean canContinueToUse() {
        return slashTicks < SLASH_DURATION_TICKS
                && wraith.getTarget() != null
                && wraith.getTarget().isAlive();
    }

    @Override
    public void start() {
        slashTicks = 0;
        dealtDamage = false;
        wraith.clearMoveTarget();
        wraith.setAttackCooldown(COOLDOWN_TICKS);
        if (useRightSlash) {
            wraith.startAttackState(Wraith.State.RIGHT_SLASH, SLASH_DURATION_TICKS);
            wraith.playSound(EnderscapeEntitySounds.WRAITH_RIGHT_SLASH, 1.0F, wraith.randomPitch());
        } else {
            wraith.startAttackState(Wraith.State.LEFT_SLASH, SLASH_DURATION_TICKS);
            wraith.playSound(EnderscapeEntitySounds.WRAITH_LEFT_SLASH, 1.0F, wraith.randomPitch());
        }
        useRightSlash = !useRightSlash;
    }

    @Override
    public void stop() {
        if (wraith.getAttackAnimationTicks() <= 0) {
            wraith.setState(Wraith.State.IDLE);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        slashTicks++;
        LivingEntity target = wraith.getTarget();
        if (target != null) {
            wraith.getLookControl().setLookAt(target, 30.0F, 30.0F);
            Vec3 toTarget = target.position().subtract(wraith.position());
            Vec3 followDir = toTarget.lengthSqr() > 1.0E-4 ? toTarget.normalize() : new Vec3(0, 0, 0);
            Vec3 desired = target.position().subtract(followDir.scale(ATTACK_FOLLOW_DISTANCE));
            wraith.getMoveControl().setWantedPosition(
                    desired.x,
                    target.getY() + 0.25,
                    desired.z,
                    1.35
            );
            if (!dealtDamage && slashTicks >= DAMAGE_AT_TICK && target.isAlive() && isWithinSlashHitRange(target)) {
                if (wraith.doHurtTarget(getServerLevel(wraith.level()), target)) {
                    wraith.recordSlashHit();
                    dealtDamage = true;
                }
            }
        }
    }

    private boolean isWithinSlashHitRange(LivingEntity target) {
        double dx = target.getX() - wraith.getX();
        double dz = target.getZ() - wraith.getZ();
        double horizontalSqr = dx * dx + dz * dz;
        double vertical = Math.abs(target.getY() - wraith.getY());
        return horizontalSqr <= SLASH_RANGE_HORIZONTAL_SQ && vertical <= SLASH_VERTICAL_TOLERANCE;
    }
}
