package net.bunten.enderscape.client.entity.wraith;

import net.bunten.enderscape.Enderscape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;

@Environment(EnvType.CLIENT)
public class WraithRenderState extends LivingEntityRenderState {

    public static final ResourceLocation TEXTURE = Enderscape.id("textures/entity/wraith/wraith.png");

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState rightSlashAnimationState = new AnimationState();
    public final AnimationState leftSlashAnimationState = new AnimationState();
    public final AnimationState spinSlashAnimationState = new AnimationState();
}
