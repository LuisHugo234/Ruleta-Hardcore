package com.luishugomod01.entity;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class MimicEntity extends Monster {
    public static final byte STATE_DISGUISED = 0;
    public static final byte STATE_WAKING_UP = 1;
    public static final byte STATE_AWAKE = 2;

    private static final EntityDimensions DISGUISED_DIMENSIONS = EntityDimensions.scalable(0.88F, 0.88F);
    private static final EntityDimensions AWAKE_DIMENSIONS = EntityDimensions.scalable(1.55F, 2.05F);

    protected static final EntityDataAccessor<Byte> DATA_STATE = SynchedEntityData.defineId(MimicEntity.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Boolean> DATA_BITING = SynchedEntityData.defineId(MimicEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState chestIdleAnimationState = new AnimationState();
    public final AnimationState wakeUpAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState biteAnimationState = new AnimationState();

    private int wakeUpTicks = 0;
    private int biteTicks = 0;

    public MimicEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 25;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.ATTACK_DAMAGE, 16.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, STATE_DISGUISED);
        builder.define(DATA_BITING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MimicBiteGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0) {
            @Override
            public boolean canUse() {
                return MimicEntity.this.isAwake() && super.canUse();
            }
        });
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F) {
            @Override
            public boolean canUse() {
                return MimicEntity.this.isAwake() && super.canUse();
            }
        });
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return MimicEntity.this.isAwake() && super.canUse();
            }
        });

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true) {
            @Override
            public boolean canUse() {
                return MimicEntity.this.isAwake() && super.canUse();
            }
        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true) {
            @Override
            public boolean canUse() {
                return MimicEntity.this.isAwake() && super.canUse();
            }
        });
    }

    public byte getMimicState() {
        return this.entityData.get(DATA_STATE);
    }

    public void setMimicState(byte state) {
        this.entityData.set(DATA_STATE, state);
        this.refreshDimensions();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_STATE.equals(key)) {
            this.refreshDimensions();
        }
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        return this.isDisguised() ? DISGUISED_DIMENSIONS : AWAKE_DIMENSIONS;
    }

    public boolean isDisguised() {
        return this.getMimicState() == STATE_DISGUISED;
    }

    public boolean isWakingUp() {
        return this.getMimicState() == STATE_WAKING_UP;
    }

    public boolean isAwake() {
        return this.getMimicState() == STATE_AWAKE;
    }

    public boolean isBiting() {
        return this.entityData.get(DATA_BITING);
    }

    public void setBiting(boolean biting) {
        this.entityData.set(DATA_BITING, biting);
    }

    public void wakeUp() {
        if (!this.isDisguised()) {
            return;
        }
        this.setMimicState(STATE_WAKING_UP);
        this.wakeUpTicks = 40; // 2.0s animation
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.CHEST_OPEN, SoundSource.HOSTILE, 1.5F, 0.7F);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 1.2F, 1.4F);
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()),
                    this.getX(), this.getY() + 0.5, this.getZ(), 20, 0.3, 0.3, 0.3, 0.15);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            byte state = this.getMimicState();
            if (state == STATE_DISGUISED) {
                this.chestIdleAnimationState.startIfStopped(this.tickCount);
                this.wakeUpAnimationState.stop();
                this.idleAnimationState.stop();
                this.biteAnimationState.stop();
            } else if (state == STATE_WAKING_UP) {
                this.chestIdleAnimationState.stop();
                this.wakeUpAnimationState.startIfStopped(this.tickCount);
                this.idleAnimationState.stop();
                this.biteAnimationState.stop();
            } else {
                this.chestIdleAnimationState.stop();
                this.wakeUpAnimationState.stop();
                this.idleAnimationState.startIfStopped(this.tickCount);
                if (this.isBiting()) {
                    this.biteAnimationState.startIfStopped(this.tickCount);
                } else {
                    this.biteAnimationState.stop();
                }
            }
        } else {
            // Server side logic
            if (this.isDisguised()) {
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
                this.setYRot(this.yRotO);
                this.yBodyRot = this.yRotO;

                // Ambush check: wake up if player gets within 2.5 blocks
                Player nearestPlayer = this.level().getNearestPlayer(this, 2.5);
                if (nearestPlayer != null && !nearestPlayer.isCreative() && !nearestPlayer.isSpectator()) {
                    this.wakeUp();
                    this.setTarget(nearestPlayer);
                }
            } else if (this.isWakingUp()) {
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
                this.wakeUpTicks--;
                if (this.wakeUpTicks <= 0) {
                    this.setMimicState(STATE_AWAKE);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                                SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 1.0F, 1.8F);
                    }
                }
            }

            if (this.isBiting()) {
                this.biteTicks--;
                if (this.biteTicks <= 0) {
                    this.setBiting(false);
                }
            }
        }
    }

    public void startBiteAttack() {
        this.setBiting(true);
        this.biteTicks = 20; // 1.0 second animation
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.HOSTILE, 1.6F, 0.5F);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.RAVAGER_ATTACK, SoundSource.HOSTILE, 1.4F, 1.2F);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (this.isDisguised()) {
            this.wakeUp();
            if (source.getEntity() instanceof LivingEntity living) {
                this.setTarget(living);
            }
        }
        return super.hurtServer(level, source, amount);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.isDisguised()) {
            this.wakeUp();
            this.setTarget(player);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hurt = super.doHurtTarget(level, target);
        if (hurt && target instanceof LivingEntity living) {
            this.startBiteAttack();
            // Bear-trap jaw bite: slowness III and knockback
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 2), this);
            Vec3 knockback = this.getLookAngle().scale(0.8);
            living.push(knockback.x, 0.25, knockback.z);
        }
        return hurt;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        // Mimic rewards
        this.spawnAtLocation(level, new ItemStack(Items.GOLD_INGOT, this.random.nextInt(6) + 4));
        this.spawnAtLocation(level, new ItemStack(Items.DIAMOND, this.random.nextInt(3) + 1));
        this.spawnAtLocation(level, new ItemStack(Items.EMERALD, this.random.nextInt(5) + 2));
        if (this.random.nextFloat() < 0.35F) {
            this.spawnAtLocation(level, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));
        } else {
            this.spawnAtLocation(level, new ItemStack(Items.GOLDEN_APPLE, 2));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isDisguised()) {
            return null; // completely silent chest disguise
        }
        return SoundEvents.CHEST_LOCKED;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RAVAGER_DEATH;
    }

    // Custom bite attack goal
    static class MimicBiteGoal extends MeleeAttackGoal {
        private final MimicEntity mimic;

        public MimicBiteGoal(MimicEntity mimic) {
            super(mimic, 1.25, false);
            this.mimic = mimic;
        }

        @Override
        public boolean canUse() {
            return this.mimic.isAwake() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return this.mimic.isAwake() && super.canContinueToUse();
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy) {
            if (this.canPerformAttack(enemy)) {
                this.resetAttackCooldown();
                this.mob.swingForAttack(InteractionHand.MAIN_HAND);
                this.mimic.startBiteAttack();
                if (this.mob.level() instanceof ServerLevel serverLevel) {
                    this.mob.doHurtTarget(serverLevel, enemy);
                }
            }
        }
    }
}
