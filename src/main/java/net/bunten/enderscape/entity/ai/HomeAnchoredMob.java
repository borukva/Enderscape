package net.bunten.enderscape.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Monster;

/**
 * Mobs that remember their first placement and can path / teleport back when idle,
 * so spawner proximity counts and loot rules stay consistent.
 * <p>
 * Implementations must extend {@link Monster}; {@link #asMonster()} is a cast bridge for AI goals.
 */
public interface HomeAnchoredMob {

    default Monster asMonster() {
        return (Monster) this;
    }

    void ensureSpawnHomeCaptured();

    BlockPos getSpawnHomeBlock();

    /**
     * Move toward home each tick (navigation or fly move control); entity-specific.
     */
    void pathTowardSpawnHome(BlockPos home, double speed);

    /**
     * Snap near {@code home} when pathing is too slow or the mob is very far.
     */
    boolean tryTeleportNearSpawnHome(BlockPos home);

    /** Ground mobs use navigation speed; wraith uses fly move speed. */
    default double spawnHomePathSpeed() {
        return 1.0;
    }
}
