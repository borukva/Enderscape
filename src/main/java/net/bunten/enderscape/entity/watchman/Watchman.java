package net.bunten.enderscape.entity.watchman;

import io.netty.buffer.ByteBuf;
import net.bunten.enderscape.entity.ai.goal.WatchmanKeepDistanceGoal;
import net.bunten.enderscape.entity.ai.goal.WatchmanLanternPushGoal;
import net.bunten.enderscape.entity.ai.goal.WatchmanLanternSmackGoal;
import net.bunten.enderscape.entity.ai.goal.WatchmanSummonWraithsGoal;
import net.bunten.enderscape.registry.EnderscapeEntities;
import net.bunten.enderscape.registry.EnderscapeEntitySounds;
import net.bunten.enderscape.registry.tag.EnderscapeBlockTags;
import net.bunten.enderscape.util.IDragonFireball;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.bunten.enderscape.entity.ai.goal.LowestHealthPlayerTargetGoal;
import net.bunten.enderscape.registry.tag.EnderscapeEntityTags;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.bunten.enderscape.entity.wraith.Wraith;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.IntFunction;

@SuppressWarnings("resource")
public class Watchman extends Monster {

    public static final int SMACK_DURATION_TICKS = 50;
    public static final int SMACK_HIT_REMAINING_TICKS = 34;
    private static final double SMACK_HIT_REACH = 5.25;
    private static final double SMACK_HIT_REACH_SQ = SMACK_HIT_REACH * SMACK_HIT_REACH;
    public static final int LANTERN_PUSH_DURATION_TICKS = 40;
    public static final int LANTERN_PUSH_FIRE_REMAINING_TICKS = 28;
    public static final int SUMMON_DURATION_TICKS = 100;

    private static final EntityDataAccessor<State> DATA_STATE = SynchedEntityData.defineId(Watchman.class, EnderscapeEntities.WATCHMAN_STATE);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState lanternSmackAnimationState = new AnimationState();
    public final AnimationState summonWraithsAnimationState = new AnimationState();
    public final AnimationState lanternPushAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();

    private int attackAnimationTicks;
    private int attackCooldown;
    private int summonCooldown;
    private int fireballCooldown;

    public Watchman(EntityType<? extends Watchman> type, Level level) {
        super(type, level);
        xpReward = 18;
    }

    public int getAttackAnimationTicks() {
        return attackAnimationTicks;
    }

    public void setAttackAnimationTicks(int ticks) {
        this.attackAnimationTicks = ticks;
    }

    public int getAttackCooldown() {
        return attackCooldown;
    }

    public void setAttackCooldown(int attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    public int getSummonCooldown() {
        return summonCooldown;
    }

    public void setSummonCooldown(int summonCooldown) {
        this.summonCooldown = summonCooldown;
    }

    public int getFireballCooldown() {
        return fireballCooldown;
    }

    public void setFireballCooldown(int fireballCooldown) {
        this.fireballCooldown = fireballCooldown;
    }

    public void startActionState(State state, int animationTicks) {
        if (getState() == state) {
            setState(State.IDLE);
        }
        setState(state);
        setAttackAnimationTicks(animationTicks);
    }

    private boolean isActionState(State state) {
        return state == State.LANTERN_SMACK || state == State.SUMMON_WRAITHS || state == State.LANTERN_PUSH;
    }

    public float randomPitch() {
        return 0.9F + random.nextFloat() * 0.2F;
    }

    public void playAggroSound() {
        playSound(EnderscapeEntitySounds.WATCHMAN_AGGRO, 1.6F, randomPitch());
    }

    public enum State {
        IDLE(0),
        WALK(1),
        LANTERN_SMACK(2),
        SUMMON_WRAITHS(3),
        LANTERN_PUSH(4);

        public static final IntFunction<State> BY_ID = ByIdMap.continuous(State::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, State> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, State::id);
        private final int id;

        State(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }
    }

    public State getState() {
        return entityData.get(DATA_STATE);
    }

    public Watchman setState(State state) {
        entityData.set(DATA_STATE, state);
        return this;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50)
                .add(Attributes.ATTACK_DAMAGE, 20)
                .add(Attributes.ATTACK_KNOCKBACK, 3.5)
                .add(Attributes.MOVEMENT_SPEED, 0.34)
                .add(Attributes.FOLLOW_RANGE, 40);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new WatchmanLanternSmackGoal(this));
        goalSelector.addGoal(2, new WatchmanKeepDistanceGoal(this));
        goalSelector.addGoal(3, new WatchmanSummonWraithsGoal(this));
        goalSelector.addGoal(4, new WatchmanLanternPushGoal(this));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.75));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 12.0F, 0.1F));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new LowestHealthPlayerTargetGoal(this, this::playAggroSound, EnderscapeEntityTags.WRAITH_HOSTILE_TOWARDS));
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            ensureClientAnimationState();
            if (deathTime > 0) {
                deathAnimationState.startIfStopped(tickCount);
            } else {
                deathAnimationState.stop();
            }
        }
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        if (summonCooldown > 0) {
            summonCooldown--;
        }
        if (fireballCooldown > 0) {
            fireballCooldown--;
        }
        if (attackAnimationTicks > 0) {
            attackAnimationTicks--;
            if (!level().isClientSide()) {
                tickServerActionFrames();
            }
            if (attackAnimationTicks == 0 && !level().isClientSide() && isActionState(getState())) {
                setState(State.IDLE);
            }
        }
        if (!level().isClientSide() && attackAnimationTicks <= 0 && !isActionState(getState())) {
            if (getNavigation().isInProgress()) {
                setState(State.WALK);
            } else {
                setState(State.IDLE);
            }
        }
    }

    /**
     * Face a target for attacks: {@link #lookAt} updates {@code yRot}; using only the look control does not align the
     * body when navigation is stopped, so body/head yaw are synced for the renderer.
     */
    public void faceForAttack(LivingEntity toward) {
        lookAt(toward, 30.0F, 30.0F);
        float y = getYRot();
        yBodyRot = y;
        setYHeadRot(y);
    }

    /**
     * Nearest living entity within horizontal reach for lantern smack, not only {@link #getTarget}.
     * Ignores spectators and creative-mode players so a non-target player cannot safely ignore aggro range rules.
     */
    @Nullable
    public LivingEntity findClosestLanternSmackVictim(double reachSq) {
        double best = reachSq + 1e-4;
        LivingEntity closest = null;
        double pad = Math.sqrt(reachSq) + 1.0;
        List<LivingEntity> nearby = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(pad));
        for (LivingEntity e : nearby) {
            if (e == this || !e.isAlive()) {
                continue;
            }
            if (e instanceof Player p && (p.isSpectator() || p.isCreative())) {
                continue;
            }
            double d = distanceToSqr(e);
            if (d <= reachSq && d < best) {
                best = d;
                closest = e;
            }
        }
        return closest;
    }

    private void tickServerActionFrames() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        State state = getState();
        int t = attackAnimationTicks;
        if (state == State.LANTERN_SMACK && t == SMACK_HIT_REMAINING_TICKS) {
            doLanternSmackHit(serverLevel);
        } else if (state == State.LANTERN_PUSH && t == LANTERN_PUSH_FIRE_REMAINING_TICKS) {
            shootDragonFireball(serverLevel);
        } else if (state == State.SUMMON_WRAITHS) {
            if (t == 77) {
                spawnSummonedWraith(serverLevel, 0);
            } else if (t == 57) {
                spawnSummonedWraith(serverLevel, 1);
            } else if (t == 37) {
                spawnSummonedWraith(serverLevel, 2);
            }
        }
    }

    private void doLanternSmackHit(ServerLevel serverLevel) {
        LivingEntity victim = findClosestLanternSmackVictim(SMACK_HIT_REACH_SQ);
        if (victim == null) {
            return;
        }
        setTarget(victim);
        float damage = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (victim.hurtServer(serverLevel, damageSources().mobAttack(this), damage)) {
            // knockback(x,z) expects direction victim → damage source (same as hurt pipeline), not attacker → victim
            double kx = getX() - victim.getX();
            double kz = getZ() - victim.getZ();
            double dx = victim.getX() - getX();
            double dz = victim.getZ() - getZ();
            if (dx * dx + dz * dz > 1e-6) {
                victim.knockback(7.5, kx, kz);
                Vec3 push = new Vec3(dx, 0, dz).normalize().scale(1.35);
                victim.addDeltaMovement(push);
                victim.addDeltaMovement(new Vec3(0.0, 0.22, 0.0));
            }
        }
    }

    private void shootDragonFireball(ServerLevel serverLevel) {
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        Vec3 eye = getEyePosition();
        // Aim at a fixed point on the ground where the target stands — not the moving body/head — so the fireball
        // travels toward that spot in space instead of chasing the player.
        Vec3 aimPoint = Vec3.atBottomCenterOf(target.blockPosition());
        Vec3 aim = aimPoint.subtract(eye);
        if (aim.lengthSqr() < 1e-4) {
            return;
        }
        Vec3 velocity = aim.normalize().scale(0.5);
        DragonFireball fireball = new DragonFireball(serverLevel, this, velocity);
        fireball.setPos(eye.x, eye.y, eye.z);
        fireball.setYRot(getYRot());
        fireball.setXRot(getXRot());
        IDragonFireball dragonFireball = (IDragonFireball) fireball;
        dragonFireball.enderscape$setCloudRadius(2.0F);
        dragonFireball.enderscape$setCloudDuration(280);
        dragonFireball.enderscape$setCloudTickRadius(false);
        dragonFireball.enderscape$setCloudPotionDurationScale(0.34F);
        serverLevel.addFreshEntity(fireball);
    }

    private void spawnSummonedWraith(ServerLevel serverLevel, int index) {
        Wraith wraith = EnderscapeEntities.WRAITH.create(serverLevel, EntitySpawnReason.MOB_SUMMONED);
        if (wraith == null) {
            return;
        }
        double angle = (Mth.TWO_PI / 3.0) * index + random.nextDouble() * 0.35;
        double dist = 2.2 + random.nextDouble() * 0.8;
        double x = getX() + Mth.cos((float) angle) * dist;
        double z = getZ() + Mth.sin((float) angle) * dist;
        double y = getY();
        wraith.setPos(x, y, z);
        wraith.setYRot(random.nextFloat() * 360.0F);
        if (getTarget() instanceof LivingEntity le) {
            wraith.setTarget(le);
        }
        if (serverLevel.noCollision(wraith)) {
            serverLevel.addFreshEntity(wraith);
        }
    }

    private void ensureClientAnimationState() {
        switch (getState()) {
            case IDLE -> idleAnimationState.startIfStopped(tickCount);
            case WALK -> walkAnimationState.startIfStopped(tickCount);
            case LANTERN_SMACK -> lanternSmackAnimationState.startIfStopped(tickCount);
            case SUMMON_WRAITHS -> summonWraithsAnimationState.startIfStopped(tickCount);
            case LANTERN_PUSH -> lanternPushAnimationState.startIfStopped(tickCount);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, State.IDLE);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (DATA_STATE.equals(accessor)) {
            resetAnimations();
            switch (getState()) {
                case IDLE -> idleAnimationState.startIfStopped(tickCount);
                case WALK -> walkAnimationState.startIfStopped(tickCount);
                case LANTERN_SMACK -> lanternSmackAnimationState.startIfStopped(tickCount);
                case SUMMON_WRAITHS -> summonWraithsAnimationState.startIfStopped(tickCount);
                case LANTERN_PUSH -> lanternPushAnimationState.startIfStopped(tickCount);
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    private void resetAnimations() {
        idleAnimationState.stop();
        walkAnimationState.stop();
        lanternSmackAnimationState.stop();
        summonWraithsAnimationState.stop();
        lanternPushAnimationState.stop();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return switch (random.nextInt(3)) {
            case 0 -> EnderscapeEntitySounds.WATCHMAN_IDLE_1;
            case 1 -> EnderscapeEntitySounds.WATCHMAN_IDLE_2;
            default -> EnderscapeEntitySounds.WATCHMAN_IDLE_3;
        };
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENDERMAN_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return SoundEvents.ENDERMAN_DEATH;
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel serverLevel, DamageSource damageSource) {
        if (damageSource.is(DamageTypes.INDIRECT_MAGIC)) {
            return true;
        }
        return super.isInvulnerableTo(serverLevel, damageSource);
    }

    public static boolean canSpawn(EntityType<Watchman> ignoredType, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && (EntitySpawnReason.isSpawner(reason) || level.getBlockState(pos.below()).is(EnderscapeBlockTags.RUBBLEMITE_SPAWNABLE_ON))
                && (EntitySpawnReason.ignoresLightRequirements(reason) || isDarkEnoughToSpawn(level, pos, random));
    }
}
