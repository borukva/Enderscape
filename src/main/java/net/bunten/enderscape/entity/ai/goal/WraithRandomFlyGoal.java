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
        BlockPos pos = wraith.blockPosition();
        for (int i = 0; i < 3; i++) {
            BlockPos target = pos.offset(
                    wraith.getRandom().nextInt(15) - 7,
                    wraith.getRandom().nextInt(11) - 5,
                    wraith.getRandom().nextInt(15) - 7
            );
            if (wraith.level().isEmptyBlock(target)) {
                wraith.getMoveControl().setWantedPosition(
                        target.getX() + 0.5,
                        target.getY() + 0.5,
                        target.getZ() + 0.5,
                        0.35
                );
                if (wraith.getTarget() == null) {
                    wraith.getLookControl().setLookAt(
                            target.getX() + 0.5,
                            target.getY() + 0.5,
                            target.getZ() + 0.5,
                            180,
                            20
                    );
                }
                break;
            }
        }
    }
}
