package net.bunten.enderscape.entity.ai.goal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

/**
 * Picks the lowest-health {@link Player} in follow range (then nearest), instead of vanilla nearest-attackable.
 */
public class LowestHealthPlayerTargetGoal extends TargetGoal {

    private final Runnable onAggro;
    private final TagKey<EntityType<?>> hostileTowardsPlayers;
    @Nullable
    private Player target;

    public LowestHealthPlayerTargetGoal(Mob mob, Runnable onAggro, TagKey<EntityType<?>> hostileTowardsPlayers) {
        super(mob, false);
        this.onAggro = onAggro;
        this.hostileTowardsPlayers = hostileTowardsPlayers;
    }

    @Override
    public boolean canUse() {
        if (!(mob.level() instanceof ServerLevel serverLevel)) {
            return false;
        }
        double followRange = mob.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB range = mob.getBoundingBox().inflate(followRange, followRange * 0.5, followRange);
        List<Player> players = serverLevel.getEntitiesOfClass(Player.class, range, this::isCandidate);
        target = players.stream()
                .min(Comparator.comparingDouble(Player::getHealth).thenComparingDouble(mob::distanceToSqr))
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
        onAggro.run();
        super.start();
    }

    private boolean isCandidate(Player player) {
        return player.isAlive()
                && !player.isSpectator()
                && !player.isCreative()
                && player.getType().is(hostileTowardsPlayers);
    }
}
