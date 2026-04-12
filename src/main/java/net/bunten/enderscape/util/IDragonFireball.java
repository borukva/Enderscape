package net.bunten.enderscape.util;

public interface IDragonFireball {
  void enderscape$setCloudRadius(float radius);

  void enderscape$setCloudDuration(int duration);

  void enderscape$setCloudTickRadius(boolean tick);

  /** Default 0.25 (vanilla); higher = stronger per-tick application scaling for cloud effects. */
  void enderscape$setCloudPotionDurationScale(float scale);

  /**
   * Instant damage amplifier for the dragon-breath cloud. Vanilla uses {@code 1}. Use {@code -1} to leave the vanilla
   * effect unchanged.
   */
  void enderscape$setCloudInstantDamageAmplifier(int amplifier);
}
