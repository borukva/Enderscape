package net.bunten.enderscape.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.bunten.enderscape.util.IDragonFireball;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.projectile.DragonFireball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DragonFireball.class)
public class DragonFireballMixin implements IDragonFireball {
  @Unique private float CLOUD_RADIUS = 3.0F;
  @Unique private int CLOUD_DURATION = 600;
  @Unique private boolean CLOUD_TICK_RADIUS = true;
  @Unique private float CLOUD_POTION_DURATION_SCALE = 0.25F;
  /** {@code -1}: use vanilla {@link MobEffectInstance} from {@code onHit}. */
  @Unique private int CLOUD_INSTANT_DAMAGE_AMPLIFIER = -1;

  @Override
  public void enderscape$setCloudRadius(float radius) {
    this.CLOUD_RADIUS = radius;
  }

  @Override
  public void enderscape$setCloudDuration(int duration) {
    this.CLOUD_DURATION = duration;
  }

  @Override
  public void enderscape$setCloudTickRadius(boolean tick) {
    this.CLOUD_TICK_RADIUS = tick;
  }

  @Override
  public void enderscape$setCloudPotionDurationScale(float scale) {
    this.CLOUD_POTION_DURATION_SCALE = scale;
  }

  @Override
  public void enderscape$setCloudInstantDamageAmplifier(int amplifier) {
    this.CLOUD_INSTANT_DAMAGE_AMPLIFIER = amplifier;
  }

  @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setRadius(F)V"))
  private void updateCloudRadius(AreaEffectCloud instance, float f, Operation<Void> original){
    original.call(instance, this.CLOUD_RADIUS);
  }

  @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setDuration(I)V"))
  private void updateCloudDuration(AreaEffectCloud instance, int i, Operation<Void> original){
    original.call(instance, this.CLOUD_DURATION);
  }

  /**
   * Vanilla {@code DragonFireball} sets radius-per-tick to {@code (7 - radius) / duration}, so the cloud always grows
   * toward radius 7. Intercepting only this call is not enough if {@link #CLOUD_TICK_RADIUS} stays true: the mixin
   * would still forward that positive value. Call {@code IDragonFireball.enderscape$setCloudTickRadius(false)} on the fireball
   * when a fixed cloud size is desired (e.g. Watchman).
   */
  @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setRadiusPerTick(F)V"))
  private void updateCloudTickRadius(AreaEffectCloud instance, float f, Operation<Void> original) {
    if (CLOUD_TICK_RADIUS) {
      original.call(instance, f);
    } else {
      original.call(instance, 0.0F);
    }
  }

  @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setPotionDurationScale(F)V"))
  private void updateCloudPotionDurationScale(AreaEffectCloud instance, float vanilla, Operation<Void> original) {
    original.call(instance, CLOUD_POTION_DURATION_SCALE);
  }

  @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)V"))
  private void updateCloudAddEffect(AreaEffectCloud instance, MobEffectInstance effect, Operation<Void> original) {
    if (CLOUD_INSTANT_DAMAGE_AMPLIFIER < 0) {
      original.call(instance, effect);
    } else {
      original.call(instance, new MobEffectInstance(effect.getEffect(), effect.getDuration(), CLOUD_INSTANT_DAMAGE_AMPLIFIER));
    }
  }
}
