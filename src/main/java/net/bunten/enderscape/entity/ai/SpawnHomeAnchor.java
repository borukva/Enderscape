package net.bunten.enderscape.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

/**
 * Shared spawn-anchor position for {@link HomeAnchoredMob} monsters: capture, NBT, and first-tick fallback.
 */
public final class SpawnHomeAnchor {

    public static final String NBT_KEY = "EnderscapeSpawnHome";

    private final Monster mob;
    @Nullable
    private BlockPos pos;

    public SpawnHomeAnchor(Monster mob) {
        this.mob = mob;
    }

    public void ensureSpawnHomeCaptured() {
        if (!mob.level().isClientSide() && pos == null) {
            pos = mob.blockPosition();
        }
    }

    public BlockPos getSpawnHomeBlock() {
        ensureSpawnHomeCaptured();
        return pos != null ? pos : mob.blockPosition();
    }

    public void captureOnFinalizeSpawn() {
        if (pos == null) {
            pos = mob.blockPosition();
        }
    }

    /** When {@link Monster#finalizeSpawn} is not used, set home from the first live tick position. */
    public void tickCaptureIfNull() {
        if (!mob.level().isClientSide() && pos == null && mob.isAlive() && mob.deathTime == 0) {
            pos = mob.blockPosition();
        }
    }

    public void addSaveData(ValueOutput output) {
        if (pos != null) {
            output.putLong(NBT_KEY, pos.asLong());
        }
    }

    public void readSaveData(ValueInput input) {
        input.getLong(NBT_KEY).ifPresent(l -> {
            BlockPos p = BlockPos.of(l);
            if (isWithinWorldBounds(mob.level(), p)) {
                pos = p;
            }
        });
    }

    private static boolean isWithinWorldBounds(Level level, BlockPos home) {
        return level != null && level.isInWorldBounds(home);
    }
}
