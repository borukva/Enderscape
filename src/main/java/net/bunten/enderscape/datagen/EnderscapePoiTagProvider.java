package net.bunten.enderscape.datagen;

import net.bunten.enderscape.Enderscape;
import net.bunten.enderscape.registry.EnderscapePoi;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import java.util.concurrent.CompletableFuture;

public class EnderscapePoiTagProvider extends FabricTagProvider<PoiType> {

    private static final TagKey<PoiType> DRIFTER_HOME = TagKey.create(Registries.POINT_OF_INTEREST_TYPE, Enderscape.id("drifter_home"));
    private static final TagKey<PoiType> RUSTLE_SLEEPING_SPOT = TagKey.create(Registries.POINT_OF_INTEREST_TYPE, Enderscape.id("rustle_sleeping_spot"));

    public EnderscapePoiTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> future) {
        super(output, Registries.POINT_OF_INTEREST_TYPE, future);
    }

    protected TagAppender<ResourceKey<PoiType>, PoiType> tag(TagKey<PoiType> key) {
        return TagAppender.forBuilder(getOrCreateRawBuilder(key));
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(DRIFTER_HOME).add(EnderscapePoi.DRIFTER_HOME);
        tag(RUSTLE_SLEEPING_SPOT).add(EnderscapePoi.RUSTLE_SLEEPING_SPOT);
    }
}