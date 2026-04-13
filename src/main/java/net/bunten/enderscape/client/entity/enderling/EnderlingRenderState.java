package net.bunten.enderscape.client.entity.enderling;

import net.bunten.enderscape.Enderscape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;

@Environment(EnvType.CLIENT)
public class EnderlingRenderState extends LivingEntityRenderState {
    public static final ResourceLocation TEXTURE = Enderscape.id("textures/entity/enderling/enderling.png");

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState chaseAnimationState = new AnimationState();
    public final AnimationState rightAttackAnimationState = new AnimationState();
    public final AnimationState leftAttackAnimationState = new AnimationState();
    /** When true, only attack layers run so leg walk/chase does not blend in. */
    public boolean attacking;
    public boolean rightSlash;
}
