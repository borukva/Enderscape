package net.bunten.enderscape.entity.enderling;

import io.netty.buffer.ByteBuf;
import net.bunten.enderscape.entity.ai.goal.EnderlingChaseGoal;
import net.bunten.enderscape.entity.ai.goal.EnderlingLowestHealthPlayerTargetGoal;
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

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState chaseAnimationState = new AnimationState();
    public final AnimationState rightAttackAnimationState = new AnimationState();
    public final AnimationState leftAttackAnimationState = new AnimationState();

    private int teleportCooldown;
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

    /** Cancel active slash when a dodge teleport succeeds. */
    public void interruptAttackForDodge() {
        setAttackAnimationTicks(0);
        if (isAttackState(getState())) {
            setState(State.IDLE);
        }
        setAttackCooldown(Math.max(getAttackCooldown(), 8));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 34)
                .add(Attributes.ATTACK_DAMAGE, 15)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
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
        targetSelector.addGoal(2, new EnderlingLowestHealthPlayerTargetGoal(this));
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

    private void ensureClientAnimationState() {
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
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float amount) {
        if (isInvulnerableTo(serverLevel, damageSource)) {
            return false;
        }

        if (teleportCooldown <= 0) {
            for (int i = 0; i < 32; i++) {
                if (teleportOnCircleWithRadius(3.0)) {
                    interruptAttackForDodge();
                    setTeleportCooldown(35);
                    return true;
                }
            }
        }
        return super.hurtServer(serverLevel, damageSource, amount);
    }

    private boolean teleportOnCircleWithRadius(double radius) {
        Entity anchor = getTarget() != null ? getTarget() : this;
        double angle = random.nextDouble() * Mth.TWO_PI;
        double x = anchor.getX() + Math.cos(angle) * radius;
        double y = Mth.clamp(anchor.getY() + (random.nextDouble() - 0.5), anchor.getY() - 0.5, anchor.getY() + 0.75);
        double z = anchor.getZ() + Math.sin(angle) * radius;
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
