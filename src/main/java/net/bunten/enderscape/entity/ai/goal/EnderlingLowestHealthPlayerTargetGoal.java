package net.bunten.enderscape.entity.ai.goal;

import net.bunten.enderscape.entity.enderling.Enderling;
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

public class EnderlingLowestHealthPlayerTargetGoal extends TargetGoal {
    private final Enderling enderling;
    @Nullable
    private Player target;

    public EnderlingLowestHealthPlayerTargetGoal(Enderling enderling) {
        super(enderling, false);
        this.enderling = enderling;
    }

    @Override
    public boolean canUse() {
        if (!(enderling.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        double followRange = enderling.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB range = enderling.getBoundingBox().inflate(followRange, followRange * 0.5, followRange);
        List<Player> players = serverLevel.getEntitiesOfClass(Player.class, range, this::isCandidate);
        target = players.stream()
                .min(Comparator
                        .comparingDouble(Player::getHealth)
                        .thenComparingDouble(enderling::distanceToSqr))
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
        enderling.playAggroSound();
        super.start();
    }

    private boolean isCandidate(Player player) {
        return player.isAlive()
                && !player.isSpectator()
                && !player.isCreative()
                && player.getType().is(EnderscapeEntityTags.WRAITH_HOSTILE_TOWARDS);
    }
}
