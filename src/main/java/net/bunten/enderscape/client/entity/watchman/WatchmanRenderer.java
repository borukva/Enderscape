package net.bunten.enderscape.client.entity.watchman;

import net.bunten.enderscape.client.registry.EnderscapeModelLayers;
import net.bunten.enderscape.entity.watchman.Watchman;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class WatchmanRenderer extends MobRenderer<Watchman, WatchmanRenderState, WatchmanModel> {

    public WatchmanRenderer(EntityRendererProvider.Context context) {
        super(context, new WatchmanModel(context.bakeLayer(EnderscapeModelLayers.WATCHMAN)), 0.5F);
    }

    @Override
    public @NotNull WatchmanRenderState createRenderState() {
        return new WatchmanRenderState();
    }

    @Override
    public void extractRenderState(Watchman mob, WatchmanRenderState state, float partialTick) {
        mob.ensureClientAnimationState();
        super.extractRenderState(mob, state, partialTick);
        state.idleAnimationState.copyFrom(mob.idleAnimationState);
        state.walkAnimationState.copyFrom(mob.walkAnimationState);
        state.lanternSmackAnimationState.copyFrom(mob.lanternSmackAnimationState);
        state.summonWraithsAnimationState.copyFrom(mob.summonWraithsAnimationState);
        state.lanternPushAnimationState.copyFrom(mob.lanternPushAnimationState);
        state.deathAnimationState.copyFrom(mob.deathAnimationState);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(WatchmanRenderState state) {
        return WatchmanRenderState.TEXTURE;
    }
}
