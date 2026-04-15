package net.bunten.enderscape.entity.enderling;

import io.netty.buffer.ByteBuf;
import net.bunten.enderscape.entity.TeleportDodgeMechanics;
import net.bunten.enderscape.entity.ai.goal.EnderlingChaseGoal;
import net.bunten.enderscape.entity.ai.goal.LowestHealthPlayerTargetGoal;
import net.bunten.enderscape.registry.tag.EnderscapeEntityTags;
import net.bunten.enderscape.entity.ai.goal.EnderlingSlashAttackGoal;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntFunction;

@SuppressWarnings("resource")
public class Enderling extends Monster {
    private static final EntityDataAccessor<State> DATA_STATE = SynchedEntityData.defineId(Enderling.class, EnderscapeEntities.ENDERLING_STATE);
    private static final double AGGRO_TELEPORT_TRIGGER_DISTANCE = 14.0;
    private static final double AGGRO_TELEPORT_TRIGGER_DISTANCE_SQR = AGGRO_TELEPORT_TRIGGER_DISTANCE * AGGRO_TELEPORT_TRIGGER_DISTANCE;
    private static final int AGGRO_TELEPORT_ATTEMPTS = 20;
    private static final int AGGRO_TELEPORT_COOLDOWN_TICKS = 30;
    private static final double AGGRO_TELEPORT_MIN_RADIUS = 1.5;
    private static final double AGGRO_TELEPORT_MAX_RADIUS = 3.5;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState chaseAnimationState = new AnimationState();
    public final AnimationState rightAttackAnimationState = new AnimationState();
    public final AnimationState leftAttackAnimationState = new AnimationState();

    private int teleportCooldown;
    /** Consecutive successful hurt-dodge teleports without a dodge cooldown being applied. */
    private int consecutiveDodgeTeleports;
    private int attackCooldown;
    private int attackAnimationTicks;
    private boolean useRightAttack = true;

    public Enderling(EntityType<? extends Enderling> type, Level level) {
        super(type, level);
        xpReward = 12;
        setCanPickUpLoot(true);
    }

    public void setTeleportCooldown(int ticks) {
        teleportCooldown = ticks;
        if (ticks > 0) {
            consecutiveDodgeTeleports = 0;
        }
    }

    public int getAttackCooldown() {
        return attackCooldown;
    }

    public void setAttackCooldown(int ticks) {
        attackCooldown = ticks;
    }

    public int getAttackAnimationTicks() {
        return attackAnimationTicks;
    }

    public void setAttackAnimationTicks(int ticks) {
        attackAnimationTicks = ticks;
    }

    public State nextAttackState() {
        State state = useRightAttack ? State.RIGHT_SLASH : State.LEFT_SLASH;
        useRightAttack = !useRightAttack;
        return state;
    }

    private boolean isAttackState(State state) {
        return state == State.RIGHT_SLASH || state == State.LEFT_SLASH;
    }

    public void startAttackState(State state, int animationTicks) {
        if (getState() == state) {
            setState(State.IDLE);
        }
        setState(state);
        setAttackAnimationTicks(animationTicks);
    }

    public boolean isInAttackState() {
        return isAttackState(getState()) && getAttackAnimationTicks() > 0;
    }

    private void cancelAttackState() {
        setAttackAnimationTicks(0);
        if (isAttackState(getState())) {
            setState(State.IDLE);
        }
    }

    /** Cancel active slash when a dodge teleport succeeds. */
    public void interruptAttackForDodge() {
        cancelAttackState();
        setAttackCooldown(Math.max(getAttackCooldown(), 8));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15)
                .add(Attributes.ATTACK_DAMAGE, 6)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 32);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new net.minecraft.world.entity.ai.goal.FloatGoal(this));
        goalSelector.addGoal(3, new EnderlingSlashAttackGoal(this));
        goalSelector.addGoal(4, new EnderlingChaseGoal(this));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.85));
        goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        goalSelector.addGoal(9, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this, net.minecraft.world.entity.player.Player.class, 8.0F, 1.0F));
        targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal(this));
        targetSelector.addGoal(2, new LowestHealthPlayerTargetGoal(this, this::playAggroSound, EnderscapeEntityTags.WRAITH_HOSTILE_TOWARDS, true));
    }

    public enum State {
        IDLE(0),
        WALK(1),
        CHASE(2),
        RIGHT_SLASH(3),
        LEFT_SLASH(4);

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

    public Enderling setState(State state) {
        entityData.set(DATA_STATE, state);
        return this;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            ensureClientAnimationState();
        }
        if (!isAlive() || deathTime > 0) {
            if (attackAnimationTicks > 0 || isAttackState(getState())) {
                cancelAttackState();
            }
            return;
        }
        if (teleportCooldown > 0) {
            teleportCooldown--;
        }
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        if (attackAnimationTicks > 0) {
            attackAnimationTicks--;
            if (attackAnimationTicks == 0 && !level().isClientSide() && isAttackState(getState())) {
                setState(State.IDLE);
            }
        }
        if (!level().isClientSide()) {
            tryTeleportToAggroTargetIfFar();
        }
        if (!level().isClientSide() && getAttackAnimationTicks() <= 0 && !isAttackState(getState())) {
            if (getTarget() == null) {
                if (getNavigation().isInProgress()) {
                    setState(State.WALK);
                } else {
                    setState(State.IDLE);
                }
            }
        }
    }

    public void ensureClientAnimationState() {
        switch (getState()) {
            case IDLE -> idleAnimationState.startIfStopped(tickCount);
            case WALK -> walkAnimationState.startIfStopped(tickCount);
            case CHASE -> chaseAnimationState.startIfStopped(tickCount);
            case RIGHT_SLASH -> rightAttackAnimationState.startIfStopped(tickCount);
            case LEFT_SLASH -> leftAttackAnimationState.startIfStopped(tickCount);
        }
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
            for (int i = 0; i < 32; i++) {
                if (teleportOnCircleForDodge(0.6, damageSource, i)) {
                    interruptAttackForDodge();
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

    private static final double DODGE_MIN_HORIZONTAL_MOVE = 1.4;

    private void tryTeleportToAggroTargetIfFar() {
        if (teleportCooldown > 0 || isInAttackState()) {
            return;
        }

        Entity target = getTarget();
        if (target == null || !target.isAlive() || distanceToSqr(target) < AGGRO_TELEPORT_TRIGGER_DISTANCE_SQR) {
            return;
        }

        for (int attempt = 0; attempt < AGGRO_TELEPORT_ATTEMPTS; attempt++) {
            double angle = random.nextDouble() * Mth.TWO_PI;
            double radius = Mth.lerp(random.nextDouble(), AGGRO_TELEPORT_MIN_RADIUS, AGGRO_TELEPORT_MAX_RADIUS);
            double x = target.getX() + Math.cos(angle) * radius;
            double y = target.getY() + (random.nextDouble() - 0.5) * 2.0;
            double z = target.getZ() + Math.sin(angle) * radius;

            if (teleport(x, y, z)) {
                setTeleportCooldown(AGGRO_TELEPORT_COOLDOWN_TICKS);
                break;
            }
        }
    }

    /**
     * Dodge teleport: stay on a ring around the target (or around self if no target), but bias toward
     * "behind" the target (opposite side from this mob) so the mob does not barely shuffle in melee range.
     */
    private boolean teleportOnCircleForDodge(double radius, DamageSource damageSource, int attemptIndex) {
        Entity target = getTarget();
        Entity attacker = damageSource.getEntity();

        Entity anchor;
        Double preferredAngle = null;

        if (target != null) {
            anchor = target;
            double tdx = getX() - target.getX();
            double tdz = getZ() - target.getZ();
            if (tdx * tdx + tdz * tdz > 1e-4) {
                // On the ring around the target: prefer the arc opposite this mob ("behind" the target).
                preferredAngle = Mth.atan2(target.getZ() - getZ(), target.getX() - getX());
            }
        } else {
            anchor = this;
            if (attacker != null) {
                double adx = getX() - attacker.getX();
                double adz = getZ() - attacker.getZ();
                if (adx * adx + adz * adz > 1e-4) {
                    preferredAngle = Mth.atan2(adz, adx);
                }
            }
        }

        double angle;
        if (preferredAngle != null) {
            // Tight jitter first, then widen so retries can still find a valid floor.
            double maxJitter = attemptIndex < 12 ? 0.65 : (attemptIndex < 24 ? 1.4 : Mth.PI);
            angle = preferredAngle + (random.nextDouble() * 2 - 1) * maxJitter;
        } else {
            angle = random.nextDouble() * Mth.TWO_PI;
        }

        double x = anchor.getX() + Math.cos(angle) * radius;
        double y = Mth.clamp(anchor.getY() + (random.nextDouble() - 0.5), anchor.getY() - 0.5, anchor.getY() + 0.75);
        double z = anchor.getZ() + Math.sin(angle) * radius;

        double hDistSq = Mth.square(x - getX()) + Mth.square(z - getZ());
        if (preferredAngle != null && attemptIndex < 28 && hDistSq < DODGE_MIN_HORIZONTAL_MOVE * DODGE_MIN_HORIZONTAL_MOVE) {
            return false;
        }

        return teleport(x, y, z);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (DATA_STATE.equals(accessor)) {
            resetAnimations();
            switch (getState()) {
                case IDLE -> idleAnimationState.startIfStopped(tickCount);
                case WALK -> walkAnimationState.startIfStopped(tickCount);
                case CHASE -> chaseAnimationState.startIfStopped(tickCount);
                case RIGHT_SLASH -> rightAttackAnimationState.startIfStopped(tickCount);
                case LEFT_SLASH -> leftAttackAnimationState.startIfStopped(tickCount);
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    private void resetAnimations() {
        idleAnimationState.stop();
        walkAnimationState.stop();
        chaseAnimationState.stop();
        rightAttackAnimationState.stop();
        leftAttackAnimationState.stop();
    }

    private boolean teleport(double x, double y, double z) {
        Level level = level();
        // Enderling is ground-based: destination must have solid support below.
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(x, y, z);
        while (mutable.getY() > level.getMinY()) {
            BlockPos below = mutable.below();
            BlockState belowState = level.getBlockState(below);
            if (belowState.isCollisionShapeFullBlock(level, below)) {
                break;
            }
            mutable.move(Direction.DOWN);
        }

        BlockPos below = mutable.below();
        BlockState belowState = level.getBlockState(below);
        BlockState feetState = level.getBlockState(mutable);
        BlockState headState = level.getBlockState(mutable.above());
        if (belowState.isCollisionShapeFullBlock(level, below)
                && !belowState.getFluidState().is(FluidTags.WATER)
                && !feetState.isCollisionShapeFullBlock(level, mutable)
                && !headState.isCollisionShapeFullBlock(level, mutable.above())
                && !feetState.getFluidState().is(FluidTags.WATER)
                && !headState.getFluidState().is(FluidTags.WATER)) {
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
            level.gameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Context.of(this));
            if (!isSilent()) {
                level.playSound(null, xo, yo, zo, SoundEvents.ENDERMAN_TELEPORT, getSoundSource(), 1, 1);
                playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);
            }
            return true;
        }

        teleportTo(oldPos.x, oldPos.y, oldPos.z);
        return false;
    }

    public float randomPitch() {
        return 0.9F + random.nextFloat() * 0.2F;
    }

    public void playAggroSound() {
        playSound(EnderscapeEntitySounds.ENDERLING_AGGRO, 1.6F, randomPitch());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return EnderscapeEntitySounds.ENDERLING_AMBIENT;
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENDERMAN_HURT;
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return EnderscapeEntitySounds.ENDERLING_DEATH;
    }

    @Override
    public boolean canHoldItem(ItemStack stack) {
        return super.canHoldItem(stack) || !stack.isEmpty();
    }

    @Override
    protected boolean canReplaceCurrentItem(ItemStack candidate, ItemStack existing, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
            return true;
        }
        return super.canReplaceCurrentItem(candidate, existing, slot);
    }

    public static boolean canSpawn(EntityType<Enderling> ignoredType, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && (EntitySpawnReason.isSpawner(reason) || level.getBlockState(pos.below()).is(EnderscapeBlockTags.RUBBLEMITE_SPAWNABLE_ON))
                && (EntitySpawnReason.ignoresLightRequirements(reason) || isDarkEnoughToSpawn(level, pos, random));
    }
}
