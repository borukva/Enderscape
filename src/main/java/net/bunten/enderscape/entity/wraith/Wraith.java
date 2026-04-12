package net.bunten.enderscape.entity.wraith;

import io.netty.buffer.ByteBuf;
import net.bunten.enderscape.entity.ai.goal.WraithRandomFlyGoal;
import net.bunten.enderscape.entity.ai.goal.WraithRetreatGoal;
import net.bunten.enderscape.entity.ai.goal.LowestHealthPlayerTargetGoal;
import net.bunten.enderscape.registry.tag.EnderscapeEntityTags;
import net.bunten.enderscape.entity.ai.goal.WraithSlashAttackGoal;
import net.bunten.enderscape.entity.ai.goal.WraithCombatFlyGoal;
import net.bunten.enderscape.entity.ai.goal.WraithSpinSlashGoal;
import net.bunten.enderscape.entity.TeleportDodgeMechanics;
import net.bunten.enderscape.entity.ai.goal.WraithTeleportGoal;
import net.bunten.enderscape.registry.EnderscapeEntities;
import net.bunten.enderscape.registry.EnderscapeEntitySounds;
import net.bunten.enderscape.registry.tag.EnderscapeBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import java.util.function.IntFunction;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("resource")
public class Wraith extends Monster {

    private static final EntityDataAccessor<State> DATA_STATE = SynchedEntityData.defineId(Wraith.class, EnderscapeEntities.WRAITH_STATE);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState rightSlashAnimationState = new AnimationState();
    public final AnimationState leftSlashAnimationState = new AnimationState();
    public final AnimationState spinSlashAnimationState = new AnimationState();

    private int teleportCooldown;
    /** Consecutive successful hurt-dodge teleports without a dodge cooldown being applied. */
    private int consecutiveDodgeTeleports;
    private int attackCooldown;
    private int attackAnimationTicks;
    private int pendingDamageTicks;
    private double pendingDamageRangeSqr = 9.0;
    private float pendingDamageMultiplier = 1.0F;
    private int comboSlashHits;
    private boolean comboStrongAttack;
    private boolean shouldRetreat;

    public Wraith(EntityType<? extends Wraith> type, Level level) {
        super(type, level);
        moveControl = new WraithMoveControl(this);
        setPathfindingMalus(PathType.WATER, -1);
        xpReward = 15;
    }

    public int getTeleportCooldown() {
        return teleportCooldown;
    }

    public void setTeleportCooldown(int ticks) {
        this.teleportCooldown = ticks;
        if (ticks > 0) {
            consecutiveDodgeTeleports = 0;
        }
    }

    public int getAttackCooldown() {
        return attackCooldown;
    }

    public void setAttackCooldown(int ticks) {
        this.attackCooldown = ticks;
    }

    /** Clears the move target so goals can set a fresh direction (e.g. after teleport). */
    public void clearMoveTarget() {
        getMoveControl().setWantedPosition(getX(), getY(), getZ(), 0);
    }

    public int getAttackAnimationTicks() {
        return attackAnimationTicks;
    }

    public void setAttackAnimationTicks(int ticks) {
        this.attackAnimationTicks = ticks;
    }

    public int getPendingDamageTicks() {
        return pendingDamageTicks;
    }

    private boolean isAttackState(State state) {
        return state == State.RIGHT_SLASH || state == State.LEFT_SLASH || state == State.SPIN_SLASH;
    }

    public void startAttackState(State state, int animationTicks) {
        if (getState() == state) {
            setState(State.IDLE);
        }
        setState(state);
        setAttackAnimationTicks(animationTicks);
    }

    public void recordSlashHit() {
        comboSlashHits++;
        if (comboSlashHits >= 2) {
            shouldRetreat = true;
        }
    }

    public void recordStrongHit() {
        comboStrongAttack = true;
        shouldRetreat = true;
    }

    public boolean shouldRetreatNow() {
        return shouldRetreat || comboStrongAttack;
    }

    public void resetComboWindow() {
        comboSlashHits = 0;
        comboStrongAttack = false;
        shouldRetreat = false;
    }

    public float randomPitch() {
        return 0.9F + random.nextFloat() * 0.2F;
    }

    public void playAggroSound() {
        playSound(EnderscapeEntitySounds.WRAITH_AGGRO, 1.6F, randomPitch());
    }

    public boolean doScaledHurtTarget(ServerLevel serverLevel, LivingEntity target, float damageMultiplier) {
        if (damageMultiplier <= 1.0F) {
            return doHurtTarget(serverLevel, target);
        }
        double baseDamage = getAttributeValue(Attributes.ATTACK_DAMAGE);
        float scaledDamage = (float) (baseDamage * damageMultiplier);
        return target.hurtServer(serverLevel, damageSources().mobAttack(this), scaledDamage);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new net.minecraft.world.entity.ai.goal.FloatGoal(this));
        goalSelector.addGoal(3, new WraithTeleportGoal(this));
        goalSelector.addGoal(4, new WraithRetreatGoal(this));
        goalSelector.addGoal(5, new WraithSpinSlashGoal(this));
        goalSelector.addGoal(5, new WraithSlashAttackGoal(this));
        goalSelector.addGoal(6, new WraithCombatFlyGoal(this));
        goalSelector.addGoal(8, new WraithRandomFlyGoal(this));
        goalSelector.addGoal(9, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this, net.minecraft.world.entity.player.Player.class, 8.0F, 1.0F));
        targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal(this));
        targetSelector.addGoal(2, new LowestHealthPlayerTargetGoal(this, this::playAggroSound, EnderscapeEntityTags.WRAITH_HOSTILE_TOWARDS));
    }

    public enum State {
        IDLE(0),
        WALK(1),
        RIGHT_SLASH(2),
        LEFT_SLASH(3),
        SPIN_SLASH(4);

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

    public Wraith setState(State state) {
        entityData.set(DATA_STATE, state);
        return this;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.ATTACK_DAMAGE, 15)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 32);
    }

    @Override
    protected boolean isAffectedByBlocks() {
        return !isRemoved();
    }

    @Override
    public void tick() {
        noPhysics = true;
        super.tick();
        noPhysics = false;
        setNoGravity(true);
        if (teleportCooldown > 0) {
            teleportCooldown--;
        }
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        if (attackAnimationTicks > 0) {
            attackAnimationTicks--;
            if (attackAnimationTicks == 0 && !level().isClientSide() && pendingDamageTicks <= 0 && isAttackState(getState())) {
                setState(State.IDLE);
            }
        }
        if (pendingDamageTicks > 0) {
            pendingDamageTicks--;
            if (pendingDamageTicks == 0 && !level().isClientSide()) {
                LivingEntity target = getTarget();
                if (target != null && target.isAlive() && distanceToSqr(target) <= pendingDamageRangeSqr && level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    doScaledHurtTarget(serverLevel, target, pendingDamageMultiplier);
                }
                pendingDamageMultiplier = 1.0F;
                pendingDamageRangeSqr = 9.0;
            }
        }
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, State.IDLE);
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel serverLevel, DamageSource damageSource) {
        if (damageSource.is(DamageTypes.INDIRECT_MAGIC)) {
            return true;
        }
        return super.isInvulnerableTo(serverLevel, damageSource);
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float amount) {
        if (isInvulnerableTo(serverLevel, damageSource)) {
            return false;
        }

        if (teleportCooldown <= 0) {
            for (int i = 0; i < 64; i++) {
                if (teleportOnCircleWithRadius(WraithCombatFlyGoal.ORBIT_RADIUS_MAX)) {
                    consecutiveDodgeTeleports = Math.min(3, consecutiveDodgeTeleports + 1);
                    if (TeleportDodgeMechanics.shouldApplyCooldownAfterDodge(random, consecutiveDodgeTeleports)) {
                        setTeleportCooldown(TeleportDodgeMechanics.DODGE_COOLDOWN_TICKS);
                    }
                    return true;
                }
            }
        }
        return super.hurtServer(serverLevel, damageSource, amount);
    }

    private boolean teleportOnCircleWithRadius(double radius) {
        Entity anchor = getTarget() != null ? getTarget() : this;
        double angle = random.nextDouble() * Mth.TWO_PI;
        double safeRadius = Math.max(0.1, radius);
        double x = anchor.getX() + Math.cos(angle) * safeRadius;
        double y = Mth.clamp(
                anchor.getY() + (random.nextDouble() - 0.5),
                anchor.getY() - 0.5,
                anchor.getY() + 0.5
        );
        double z = anchor.getZ() + Math.sin(angle) * safeRadius;
        return teleport(x, y, z);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (DATA_STATE.equals(accessor)) {
            resetAnimations();
            switch (getState()) {
                case IDLE -> idleAnimationState.startIfStopped(tickCount);
                case WALK -> walkAnimationState.startIfStopped(tickCount);
                case RIGHT_SLASH -> rightSlashAnimationState.startIfStopped(tickCount);
                case LEFT_SLASH -> leftSlashAnimationState.startIfStopped(tickCount);
                case SPIN_SLASH -> spinSlashAnimationState.startIfStopped(tickCount);
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    private void resetAnimations() {
        idleAnimationState.stop();
        walkAnimationState.stop();
        rightSlashAnimationState.stop();
        leftSlashAnimationState.stop();
        spinSlashAnimationState.stop();
    }

    public boolean teleport() {
        if (!level().isClientSide() && isAlive()) {
            double x = getX() + (random.nextDouble() - 0.5) * 64;
            double y = getY() + (random.nextInt(64) - 32);
            double z = getZ() + (random.nextDouble() - 0.5) * 64;
            return teleport(x, y, z);
        }
        return false;
    }

    public boolean teleportTowards(Entity entity) {
        Vec3 vec3 = new Vec3(getX() - entity.getX(), getY(0.5) - entity.getEyeY(), getZ() - entity.getZ());
        vec3 = vec3.normalize();
        double x = getX() + (random.nextDouble() - 0.5) * 8 - vec3.x * 16;
        double y = getY() + (random.nextInt(16) - 8) - vec3.y * 16;
        double z = getZ() + (random.nextDouble() - 0.5) * 8 - vec3.z * 16;
        return teleport(x, y, z);
    }

    private boolean teleport(double x, double y, double z) {
        // randomTeleport() ALWAYS requires solid ground - use direct teleportTo for air
        Level level = level();
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockState state = level.getBlockState(pos);

        // Wraith can teleport to air - check that position is passable
        if (!state.isCollisionShapeFullBlock(level, pos) && !state.getFluidState().is(FluidTags.WATER)) {
            return tryTeleportTo(x, y, z);
        }
        // Fallback: try position above if inside block
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(x, y, z);
        while (mutable.getY() < level.getMaxY() && level.getBlockState(mutable).isCollisionShapeFullBlock(level, mutable)) {
            mutable.move(Direction.UP);
        }
        BlockState fallbackState = level.getBlockState(mutable);
        if (!fallbackState.isCollisionShapeFullBlock(level, mutable) && !fallbackState.getFluidState().is(FluidTags.WATER)) {
            return tryTeleportTo(mutable.getX() + 0.5, mutable.getY(), mutable.getZ() + 0.5);
        }
        return false;
    }

    private boolean tryTeleportTo(double x, double y, double z) {
        Vec3 oldPos = position();
        teleportTo(x, y, z);
        Level level = level();
        if (level.noCollision(this) && !level.containsAnyLiquid(getBoundingBox())) {
            getNavigation().stop();
            clearMoveTarget();
            level.gameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Context.of(this));
            if (!isSilent()) {
                level.playSound(null, xo, yo, zo, SoundEvents.ENDERMAN_TELEPORT, getSoundSource(), 1, 1);
                playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);
            }
            return true;
        }
        // Revert if collision
        teleportTo(oldPos.x, oldPos.y, oldPos.z);
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return switch (random.nextInt(3)) {
            case 0 -> EnderscapeEntitySounds.WRAITH_IDLE_1;
            case 1 -> EnderscapeEntitySounds.WRAITH_IDLE_2;
            default -> EnderscapeEntitySounds.WRAITH_IDLE_3;
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

    public static boolean canSpawn(EntityType<Wraith> ignoredType, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && (EntitySpawnReason.isSpawner(reason) || level.getBlockState(pos.below()).is(EnderscapeBlockTags.RUBBLEMITE_SPAWNABLE_ON))
                && (EntitySpawnReason.ignoresLightRequirements(reason) || isDarkEnoughToSpawn(level, pos, random));
    }

    static class WraithMoveControl extends MoveControl {
        private final Wraith wraith;

        WraithMoveControl(Wraith wraith) {
            super(wraith);
            this.wraith = wraith;
        }

        @Override
        public void tick() {
            if (operation == Operation.MOVE_TO) {
                Vec3 vec3 = new Vec3(wantedX - wraith.getX(), wantedY - wraith.getY(), wantedZ - wraith.getZ());
                double d = vec3.length();
                if (d < wraith.getBoundingBox().getSize()) {
                    operation = Operation.WAIT;
                    wraith.setDeltaMovement(wraith.getDeltaMovement().scale(0.5));
                    if (wraith.getAttackAnimationTicks() <= 0) {
                        wraith.setState(State.IDLE);
                    }
                } else {
                    if (wraith.getAttackAnimationTicks() <= 0) {
                        wraith.setState(State.WALK);
                    }
                    Vec3 movement = wraith.getDeltaMovement().add(vec3.scale(speedModifier * 0.06 / d));
                    wraith.setDeltaMovement(movement);
                    LivingEntity target = wraith.getTarget();
                    if (target == null) {
                        Vec3 delta = wraith.getDeltaMovement();
                        wraith.setYRot(-((float) Mth.atan2(delta.x, delta.z)) * (180 / (float) Math.PI));
                    } else {
                        double e = target.getX() - wraith.getX();
                        double f = target.getZ() - wraith.getZ();
                        wraith.setYRot(-((float) Mth.atan2(e, f)) * (180 / (float) Math.PI));
                    }
                    wraith.yBodyRot = wraith.getYRot();
                }
            }
        }
    }
}
