package net.bunten.enderscape.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.bunten.enderscape.entity.wraith.Wraith;

import java.util.EnumSet;

public class WraithRandomFlyGoal extends Goal {

    private final Wraith wraith;

    public WraithRandomFlyGoal(Wraith wraith) {
        this.wraith = wraith;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return wraith.getTarget() == null
                && !wraith.getMoveControl().hasWanted()
                && wraith.getRandom().nextInt(reducedTickDelay(7)) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void tick() {
        if (!(wraith.level() instanceof net.minecraft.server.level.ServerLevel level)) {
            return;
        }
        double anchorY = wraith.getY();
        for (int i = 0; i < 3; i++) {
            double tx = wraith.getX() + (wraith.getRandom().nextInt(15) - 7);
            double ty = anchorY + (wraith.getRandom().nextDouble() * 2.0 - 1.0);
            double tz = wraith.getZ() + (wraith.getRandom().nextInt(15) - 7);
            BlockPos target = BlockPos.containing(tx, ty, tz);
            if (level.isEmptyBlock(target)) {
                wraith.getMoveControl().setWantedPosition(tx, ty, tz, 0.35);
                if (wraith.getTarget() == null) {
                    wraith.getLookControl().setLookAt(tx, ty, tz, 180, 20);
                }
                break;
            }
        }
    }
}
