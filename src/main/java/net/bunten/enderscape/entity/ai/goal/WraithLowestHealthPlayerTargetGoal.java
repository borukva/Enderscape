package net.bunten.enderscape.entity.ai.goal;

import net.bunten.enderscape.entity.wraith.Wraith;
import net.bunten.enderscape.registry.tag.EnderscapeEntityTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class WraithLowestHealthPlayerTargetGoal extends TargetGoal {
    private final Wraith wraith;
    @Nullable
    private Player target;

    public WraithLowestHealthPlayerTargetGoal(Wraith wraith) {
        super(wraith, false);
        this.wraith = wraith;
    }

    @Override
    public boolean canUse() {
        if (!(wraith.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        double followRange = wraith.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB range = wraith.getBoundingBox().inflate(followRange, followRange * 0.5, followRange);
        List<Player> players = serverLevel.getEntitiesOfClass(Player.class, range, this::isCandidate);
        target = players.stream()
                .min(Comparator
                        .comparingDouble(Player::getHealth)
                        .thenComparingDouble(wraith::distanceToSqr))
                .orElse(null);
        return target != null;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity current = mob.getTarget();
        return current instanceof Player player && isCandidate(player);
    }

    @Override
    public void start() {
        mob.setTarget(target);
        wraith.playAggroSound();
        super.start();
    }

    private boolean isCandidate(Player player) {
        return player.isAlive()
                && !player.isSpectator()
                && !player.isCreative()
                && player.getType().is(EnderscapeEntityTags.WRAITH_HOSTILE_TOWARDS);
    }
}
