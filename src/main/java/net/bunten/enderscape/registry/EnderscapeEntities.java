package net.bunten.enderscape.registry;

import com.google.common.reflect.Reflection;
import net.bunten.enderscape.Enderscape;
import net.bunten.enderscape.entity.ai.EnderscapeMemory;
import net.bunten.enderscape.entity.ai.EnderscapeSensors;
import net.bunten.enderscape.entity.enderling.Enderling;
import net.bunten.enderscape.entity.drifter.Drifter;
import net.bunten.enderscape.entity.rubblemite.Rubblemite;
import net.bunten.enderscape.entity.rustle.Rustle;
import net.bunten.enderscape.entity.watchman.Watchman;
import net.bunten.enderscape.entity.wraith.Wraith;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.NotNull;

public class EnderscapeEntities {

    public static final EntityType<Drifter> DRIFTER = register("drifter", EntityType.Builder.of(Drifter::new, MobCategory.CREATURE)
            .sized(1.9F, 2.9F)
            .eyeHeight(1.08F)
            .passengerAttachments(3.0F)
            .ridingOffset(0.1F)
            .clientTrackingRange(8)
    );

    public static final EntityType<Rubblemite> RUBBLEMITE = register("rubblemite", EntityType.Builder.of(Rubblemite::new, MobCategory.MONSTER)
            .sized(0.55F, 0.4F)
            .eyeHeight(0.13F)
            .passengerAttachments(0.3F)
            .ridingOffset(0.1F)
            .clientTrackingRange(8)
            .notInPeaceful()
    );

    public static final EntityType<Wraith> WRAITH = register("wraith", EntityType.Builder.of(Wraith::new, MobCategory.MONSTER)
            .sized(0.8F, 2.0F)
            .eyeHeight(1.4F)
            .passengerAttachments(0.3F)
            .ridingOffset(0.1F)
            .clientTrackingRange(8)
            .notInPeaceful()
    );

    public static final EntityType<Enderling> ENDERLING = register("enderling", EntityType.Builder.of(Enderling::new, MobCategory.MONSTER)
            .sized(0.8F, 2.0F)
            .eyeHeight(1.74F)
            .passengerAttachments(0.3F)
            .ridingOffset(0.1F)
            .clientTrackingRange(8)
            .notInPeaceful()
    );

    public static final EntityType<Watchman> WATCHMAN = register("watchman", EntityType.Builder.of(Watchman::new, MobCategory.MONSTER)
            .sized(0.75F, 2.1F)
            .eyeHeight(1.75F)
            .passengerAttachments(0.3F)
            .ridingOffset(0.1F)
            .clientTrackingRange(8)
            .notInPeaceful()
    );

    public static final EntityType<Rustle> RUSTLE = register("rustle", EntityType.Builder.of(Rustle::new, MobCategory.CREATURE)
            .sized(0.6F, 0.5F)
            .eyeHeight(0.13F)
            .passengerAttachments(0.3F)
            .ridingOffset(0.1F)
            .clientTrackingRange(8)
    );

    public static final EntityDataSerializer<Rubblemite.State> RUBBLEMITE_STATE = EntityDataSerializer.forValueType(Rubblemite.State.STREAM_CODEC);
    public static final EntityDataSerializer<Wraith.State> WRAITH_STATE = EntityDataSerializer.forValueType(Wraith.State.STREAM_CODEC);
    public static final EntityDataSerializer<Enderling.State> ENDERLING_STATE = EntityDataSerializer.forValueType(Enderling.State.STREAM_CODEC);
    public static final EntityDataSerializer<Watchman.State> WATCHMAN_STATE = EntityDataSerializer.forValueType(Watchman.State.STREAM_CODEC);

    static {
        Reflection.initialize(
                EnderscapeMemory.class,
                EnderscapeSensors.class
        );

        SpawnPlacements.register(DRIFTER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Drifter::canSpawn);
        SpawnPlacements.register(RUBBLEMITE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Rubblemite::canSpawn);
        SpawnPlacements.register(WRAITH, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Wraith::canSpawn);
        SpawnPlacements.register(ENDERLING, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Enderling::canSpawn);
        SpawnPlacements.register(WATCHMAN, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Watchman::canSpawn);
        SpawnPlacements.register(RUSTLE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Rustle::canSpawn);

        FabricDefaultAttributeRegistry.register(DRIFTER, Drifter.createAttributes());
        FabricDefaultAttributeRegistry.register(RUBBLEMITE, Rubblemite.createAttributes());
        FabricDefaultAttributeRegistry.register(WRAITH, Wraith.createAttributes());
        FabricDefaultAttributeRegistry.register(ENDERLING, Enderling.createAttributes());
        FabricDefaultAttributeRegistry.register(WATCHMAN, Watchman.createAttributes());
        FabricDefaultAttributeRegistry.register(RUSTLE, Rustle.createAttributes());

        FabricTrackedDataRegistry.register(Enderscape.id("rubblemite_state"), RUBBLEMITE_STATE);
        FabricTrackedDataRegistry.register(Enderscape.id("wraith_state"), WRAITH_STATE);
        FabricTrackedDataRegistry.register(Enderscape.id("enderling_state"), ENDERLING_STATE);
        FabricTrackedDataRegistry.register(Enderscape.id("watchman_state"), WATCHMAN_STATE);
    }

    private static @NotNull <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        return register(ResourceKey.create(Registries.ENTITY_TYPE, Enderscape.id(name)), builder);
    }

    private static @NotNull <T extends Entity> EntityType<T> register(ResourceKey<EntityType<?>> resourceKey, EntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, resourceKey, builder.build(resourceKey));
    }
}