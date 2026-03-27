package net.bunten.enderscape.client.entity.wraith;

import net.bunten.enderscape.client.registry.EnderscapeModelLayers;
import net.bunten.enderscape.entity.wraith.Wraith;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class WraithRenderer extends MobRenderer<Wraith, WraithRenderState, WraithModel> {

    public WraithRenderer(EntityRendererProvider.Context context) {
        super(context, new WraithModel(context.bakeLayer(EnderscapeModelLayers.WRAITH)), 0.5F);
    }

    @Override
    protected float getFlipDegrees() {
        return 180.0F;
    }

    @Override
    public @NotNull WraithRenderState createRenderState() {
        return new WraithRenderState();
    }

    @Override
    public void extractRenderState(Wraith mob, WraithRenderState state, float partialTick) {
        super.extractRenderState(mob, state, partialTick);
        state.idleAnimationState.copyFrom(mob.idleAnimationState);
        state.walkAnimationState.copyFrom(mob.walkAnimationState);
        state.rightSlashAnimationState.copyFrom(mob.rightSlashAnimationState);
        state.leftSlashAnimationState.copyFrom(mob.leftSlashAnimationState);
        state.spinSlashAnimationState.copyFrom(mob.spinSlashAnimationState);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(WraithRenderState state) {
        return WraithRenderState.TEXTURE;
    }
}
