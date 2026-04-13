package net.bunten.enderscape.client.entity.enderling;

import net.bunten.enderscape.client.registry.EnderscapeModelLayers;
import net.bunten.enderscape.entity.enderling.Enderling;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class EnderlingRenderer extends MobRenderer<Enderling, EnderlingRenderState, EnderlingModel> {
    public EnderlingRenderer(EntityRendererProvider.Context context) {
        super(context, new EnderlingModel(context.bakeLayer(EnderscapeModelLayers.ENDERLING)), 0.5F);
    }

    @Override
    protected float getFlipDegrees() {
        return 180.0F;
    }

    @Override
    public @NotNull EnderlingRenderState createRenderState() {
        return new EnderlingRenderState();
    }

    @Override
    public void extractRenderState(Enderling mob, EnderlingRenderState state, float partialTick) {
        super.extractRenderState(mob, state, partialTick);
        state.idleAnimationState.copyFrom(mob.idleAnimationState);
        state.walkAnimationState.copyFrom(mob.walkAnimationState);
        state.chaseAnimationState.copyFrom(mob.chaseAnimationState);
        state.rightAttackAnimationState.copyFrom(mob.rightAttackAnimationState);
        state.leftAttackAnimationState.copyFrom(mob.leftAttackAnimationState);
        Enderling.State s = mob.getState();
        state.attacking = s == Enderling.State.RIGHT_SLASH || s == Enderling.State.LEFT_SLASH;
        state.rightSlash = s == Enderling.State.RIGHT_SLASH;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(EnderlingRenderState state) {
        return EnderlingRenderState.TEXTURE;
    }
}
