package net.bunten.enderscape.entity;

import net.minecraft.util.RandomSource;

/**
 * Shared dodge-teleport rules for {@link net.bunten.enderscape.entity.wraith.Wraith} and
 * {@link net.bunten.enderscape.entity.enderling.Enderling}: each successful hurt-dodge increments a streak;
 * cooldown chance is streak/3; the third dodge in a row always triggers cooldown.
 */
public final class TeleportDodgeMechanics {

    /** 4 seconds at 20 TPS */
    public static final int DODGE_COOLDOWN_TICKS = 80;

    private TeleportDodgeMechanics() {
    }

    /**
     * @param streakAfterThisDodge 1–3 after incrementing for the current dodge
     * @return whether to apply {@link #DODGE_COOLDOWN_TICKS} and reset the streak
     */
    public static boolean shouldApplyCooldownAfterDodge(RandomSource random, int streakAfterThisDodge) {
        if (streakAfterThisDodge >= 3) {
            return true;
        }
        return random.nextFloat() < streakAfterThisDodge / 3.0f;
    }
}
