package net.bunten.enderscape.client.entity.watchman;

import net.bunten.enderscape.Enderscape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;

@Environment(EnvType.CLIENT)
public class WatchmanRenderState extends LivingEntityRenderState {

    public static final ResourceLocation TEXTURE = Enderscape.id("textures/entity/watchman/watchman.png");

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState lanternSmackAnimationState = new AnimationState();
    public final AnimationState summonWraithsAnimationState = new AnimationState();
    public final AnimationState lanternPushAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();
}
