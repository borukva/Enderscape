package net.bunten.enderscape.entity.ai.goal;

import net.bunten.enderscape.entity.enderling.Enderling;
import net.bunten.enderscape.registry.EnderscapeEntitySounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class EnderlingSlashAttackGoal extends Goal {
    /** Small extra window so adjacent-height targets do not deadlock chase/attack transitions. */
    private static final double START_RANGE_BUFFER = 0.35;
    private static final double VERTICAL_TOLERANCE = 2.0;
    private static final int SLASH_DURATION_TICKS = 30;
    private static final int DAMAGE_AT_TICK = 10;
    private static final int COOLDOWN_TICKS = 24;

    private final Enderling enderling;
    private int slashTicks;
    private boolean dealtDamage;

    public EnderlingSlashAttackGoal(Enderling enderling) {
        this.enderling = enderling;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (enderling.getAttackCooldown() > 0) {
            return false;
        }
        LivingEntity target = enderling.getTarget();
        return target != null
                && target.isAlive()
                && isWithinStartRange(target);
    }

    @Override
    public boolean canContinueToUse() {
        return slashTicks < SLASH_DURATION_TICKS
                && enderling.isInAttackState()
                && enderling.getTarget() != null
                && enderling.getTarget().isAlive();
    }

    @Override
    public void start() {
        slashTicks = 0;
        dealtDamage = false;
        enderling.setAttackCooldown(COOLDOWN_TICKS);
        Enderling.State attackState = enderling.nextAttackState();
        enderling.startAttackState(attackState, SLASH_DURATION_TICKS);
        enderling.playSound(EnderscapeEntitySounds.ENDERLING_ATTACK, 1.0F, 0.95F + enderling.getRandom().nextFloat() * 0.1F);
    }

    @Override
    public void stop() {
        if (enderling.getAttackAnimationTicks() <= 0) {
            enderling.setState(Enderling.State.IDLE);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (!enderling.isInAttackState()) {
            return;
        }
        slashTicks++;
        LivingEntity target = enderling.getTarget();
        if (target == null) {
            return;
        }

        enderling.getLookControl().setLookAt(target, 35.0F, 35.0F);
        enderling.getNavigation().stop();

        if (!dealtDamage && slashTicks >= DAMAGE_AT_TICK && isInMeleeRange(target)) {
            if (enderling.level() instanceof ServerLevel serverLevel && enderling.doHurtTarget(serverLevel, target)) {
                dealtDamage = true;
            }
        }
    }

    private boolean isInMeleeRange(LivingEntity target) {
        return isWithinHorizontalRange(target, getAttackReachSqr(target))
                && Math.abs(target.getY() - enderling.getY()) <= VERTICAL_TOLERANCE;
    }

    private boolean isWithinStartRange(LivingEntity target) {
        double startRangeSqr = getAttackReachSqr(target) + START_RANGE_BUFFER * START_RANGE_BUFFER;
        return isWithinHorizontalRange(target, startRangeSqr)
                && Math.abs(target.getY() - enderling.getY()) <= VERTICAL_TOLERANCE;
    }

    private boolean isWithinHorizontalRange(LivingEntity target, double rangeSqr) {
        double dx = target.getX() - enderling.getX();
        double dz = target.getZ() - enderling.getZ();
        return (dx * dx + dz * dz) <= rangeSqr;
    }

    private double getAttackReachSqr(LivingEntity target) {
        double selfWidth = enderling.getBbWidth();
        double targetWidth = target.getBbWidth();
        double reach = 1.35 + (selfWidth + targetWidth) * 0.5;
        return reach * reach;
    }
}
