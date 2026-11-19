package com.luishugomod01.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Optional;

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
    private ResourceKey<LootTable> savedLootTable = null;
    private long savedLootTableSeed = 0L;

    public void setSavedLootTable(ResourceKey<LootTable> lootTable, long seed) {
        this.savedLootTable = lootTable;
        this.savedLootTableSeed = seed;
    }

    public ResourceKey<LootTable> getSavedLootTable() {
        return this.savedLootTable;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (this.savedLootTable != null) {
            output.putString("MimicLootTable", this.savedLootTable.identifier().toString());
            output.putLong("MimicLootSeed", this.savedLootTableSeed);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        input.getString("MimicLootTable").ifPresent(str -> {
            Identifier id = Identifier.tryParse(str);
            if (id != null) {
                this.savedLootTable = ResourceKey.create(Registries.LOOT_TABLE, id);
            }
        });
        this.savedLootTableSeed = input.getLongOr("MimicLootSeed", 0L);
    }

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
                    SoundEvents.CHEST_OPEN, SoundSource.HOSTILE, 1.5F, 0.55F);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.HOSTILE, 1.4F, 0.45F);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 1.2F, 1.35F);
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()),
                    this.getX(), this.getY() + 0.5, this.getZ(), 26, 0.38, 0.35, 0.38, 0.18);
            serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE,
                    this.getX(), this.getY() + 0.65, this.getZ(), 18, 0.35, 0.3, 0.35, 0.06);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    this.getX(), this.getY() + 0.4, this.getZ(), 10, 0.3, 0.2, 0.3, 0.04);
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
                    SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.HOSTILE, 1.6F, 0.45F);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.RAVAGER_ATTACK, SoundSource.HOSTILE, 1.4F, 1.15F);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.EVOKER_FANGS_ATTACK, SoundSource.HOSTILE, 1.3F, 0.75F);
            Vec3 forward = this.getLookAngle();
            double jx = this.getX() + forward.x * 0.85;
            double jy = this.getY() + 1.05;
            double jz = this.getZ() + forward.z * 0.85;
            serverLevel.sendParticles(ParticleTypes.CRIT, jx, jy, jz, 12, 0.25, 0.2, 0.25, 0.15);
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()),
                    jx, jy, jz, 8, 0.2, 0.15, 0.2, 0.1);
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
        if (level.getServer() == null) {
            return;
        }

        ResourceKey<LootTable> lootKey = this.savedLootTable != null
                ? this.savedLootTable
                : resolverTablaLootCofrePorUbicacion(level, this.blockPosition());

        LootTable table = level.getServer().reloadableRegistries().getLootTable(lootKey);
        LootParams.Builder paramsBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, this.position());
        if (damageSource.getEntity() instanceof Player killer) {
            paramsBuilder.withLuck(killer.getLuck()).withParameter(LootContextParams.THIS_ENTITY, killer);
        }
        LootParams params = paramsBuilder.create(LootContextParamSets.CHEST);

        ObjectArrayList<ItemStack> items = (this.savedLootTableSeed != 0L)
                ? table.getRandomItems(params, this.savedLootTableSeed)
                : table.getRandomItems(params);

        if (items.isEmpty() && !lootKey.equals(BuiltInLootTables.SIMPLE_DUNGEON)) {
            LootTable fallback = level.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.SIMPLE_DUNGEON);
            items = fallback.getRandomItems(params);
        }

        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                this.spawnAtLocation(level, stack);
            }
        }
    }

    private ResourceKey<LootTable> resolverTablaLootCofrePorUbicacion(ServerLevel level, BlockPos pos) {
        // 1. Si está dentro de una estructura vanilla, usar exactamente el loot de cofre de esa estructura
        Registry<Structure> structureRegistry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        Map<Structure, ?> structuresAt = level.structureManager().getAllStructuresAt(pos);
        for (Structure structure : structuresAt.keySet()) {
            Identifier id = structureRegistry.getKey(structure);
            if (id == null) continue;
            String path = id.getPath();
            if (path.contains("bastion")) {
                return this.random.nextFloat() < 0.25F ? BuiltInLootTables.BASTION_TREASURE : BuiltInLootTables.BASTION_OTHER;
            }
            if (path.contains("fortress")) {
                return BuiltInLootTables.NETHER_BRIDGE;
            }
            if (path.contains("end_city")) {
                return BuiltInLootTables.END_CITY_TREASURE;
            }
            if (path.contains("ancient_city")) {
                return BuiltInLootTables.ANCIENT_CITY;
            }
            if (path.contains("mansion")) {
                return BuiltInLootTables.WOODLAND_MANSION;
            }
            if (path.contains("stronghold")) {
                int roll = this.random.nextInt(3);
                return roll == 0 ? BuiltInLootTables.STRONGHOLD_LIBRARY
                        : (roll == 1 ? BuiltInLootTables.STRONGHOLD_CROSSING : BuiltInLootTables.STRONGHOLD_CORRIDOR);
            }
            if (path.contains("desert_pyramid")) {
                return BuiltInLootTables.DESERT_PYRAMID;
            }
            if (path.contains("jungle_pyramid") || path.contains("jungle_temple")) {
                return BuiltInLootTables.JUNGLE_TEMPLE;
            }
            if (path.contains("mineshaft")) {
                return BuiltInLootTables.ABANDONED_MINESHAFT;
            }
            if (path.contains("ruined_portal")) {
                return BuiltInLootTables.RUINED_PORTAL;
            }
            if (path.contains("shipwreck")) {
                return this.random.nextFloat() < 0.5F ? BuiltInLootTables.SHIPWRECK_TREASURE : BuiltInLootTables.SHIPWRECK_SUPPLY;
            }
            if (path.contains("buried_treasure")) {
                return BuiltInLootTables.BURIED_TREASURE;
            }
            if (path.contains("ocean_ruin")) {
                return BuiltInLootTables.UNDERWATER_RUIN_BIG;
            }
            if (path.contains("pillager_outpost")) {
                return BuiltInLootTables.PILLAGER_OUTPOST;
            }
            if (path.contains("igloo")) {
                return BuiltInLootTables.IGLOO_CHEST;
            }
            if (path.contains("trial_chambers")) {
                return BuiltInLootTables.TRIAL_CHAMBERS_REWARD;
            }
            if (path.contains("village")) {
                return BuiltInLootTables.VILLAGE_TOOLSMITH;
            }
        }

        // 2. Fallback según dimensión
        if (level.dimension().equals(Level.NETHER)) {
            return this.random.nextBoolean() ? BuiltInLootTables.NETHER_BRIDGE : BuiltInLootTables.BASTION_OTHER;
        }
        if (level.dimension().equals(Level.END)) {
            return BuiltInLootTables.END_CITY_TREASURE;
        }

        // 3. Fallback en Overworld según bioma y profundidad
        Optional<ResourceKey<Biome>> biomeKey = level.getBiome(pos).unwrapKey();
        if (biomeKey.isPresent()) {
            String biome = biomeKey.get().identifier().getPath();
            if (biome.contains("deep_dark")) {
                return BuiltInLootTables.ANCIENT_CITY;
            }
            if (pos.getY() >= 50) {
                if (biome.contains("desert") || biome.contains("badlands")) {
                    return BuiltInLootTables.DESERT_PYRAMID;
                }
                if (biome.contains("jungle")) {
                    return BuiltInLootTables.JUNGLE_TEMPLE;
                }
                if (biome.contains("ocean") || biome.contains("beach")) {
                    return BuiltInLootTables.SHIPWRECK_TREASURE;
                }
                if (biome.contains("snow") || biome.contains("ice") || biome.contains("frozen")) {
                    return BuiltInLootTables.IGLOO_CHEST;
                }
            }
        }

        if (pos.getY() < 0) {
            return this.random.nextBoolean() ? BuiltInLootTables.ABANDONED_MINESHAFT : BuiltInLootTables.SIMPLE_DUNGEON;
        }
        if (pos.getY() < 55) {
            return BuiltInLootTables.SIMPLE_DUNGEON;
        }
        return this.random.nextBoolean() ? BuiltInLootTables.RUINED_PORTAL : BuiltInLootTables.VILLAGE_TOOLSMITH;
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
