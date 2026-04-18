package net.bunten.enderscape.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

/**
 * Plays a mob aggro sound when {@link Monster#setTarget} assigns a new {@link Player} target.
 */
public final class MobPlayerAggroSound {

    private MobPlayerAggroSound() {
    }

    public static void ifNewPlayerTarget(Monster mob, LivingEntity previousTarget, LivingEntity newTarget, Runnable playAggroSound) {
        if (!mob.level().isClientSide() && newTarget instanceof Player && newTarget != previousTarget) {
            playAggroSound.run();
        }
    }
}
