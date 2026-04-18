package net.bunten.enderscape.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

/**
 * Shared enderman-style teleport feedback (portal trail, sound, game event) for mod mobs.
 */
public final class MonsterTeleportFeedback {

    private MonsterTeleportFeedback() {
    }

    /**
     * Call on the server after a successful teleport; {@code from} is the position before the move.
     */
    public static void playEnderStyleFrom(Mob mob, Vec3 from) {
        if (mob.level().isClientSide() || !(mob.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        serverLevel.gameEvent(GameEvent.TELEPORT, from, GameEvent.Context.of(mob));
        serverLevel.broadcastEntityEvent(mob, EntityEvent.TELEPORT);
        if (!mob.isSilent()) {
            serverLevel.playSound(null, from.x, from.y, from.z, SoundEvents.ENDERMAN_TELEPORT, mob.getSoundSource(), 1.0F, 1.0F);
            mob.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }
}
