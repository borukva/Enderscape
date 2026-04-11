package net.bunten.enderscape.entity.ai.goal;

import net.bunten.enderscape.entity.TeleportDodgeMechanics;
import net.bunten.enderscape.entity.wraith.Wraith;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class WraithTeleportGoal extends Goal {

    private static final double TELEPORT_DISTANCE_SQ = 12 * 12;
    private static final int COOLDOWN_TICKS = TeleportDodgeMechanics.DODGE_COOLDOWN_TICKS;

    private final Wraith wraith;

    public WraithTeleportGoal(Wraith wraith) {
        this.wraith = wraith;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (wraith.getTeleportCooldown() > 0) {
            return false;
        }
        LivingEntity target = wraith.getTarget();
        return target != null && target.isAlive() && wraith.distanceToSqr(target) > TELEPORT_DISTANCE_SQ;
    }

    @Override
    public void start() {
        LivingEntity target = wraith.getTarget();
        if (target != null && wraith.teleportTowards(target)) {
            wraith.setTeleportCooldown(COOLDOWN_TICKS);
            wraith.clearMoveTarget();
        }
    }
}
