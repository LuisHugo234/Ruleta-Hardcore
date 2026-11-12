package com.luishugomod01.entity;

import com.luishugomod01.Ruleta_Hardcore;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Hombre Roca (Nivel 3): aparece de día o de noche en cualquier bioma del Overworld
 * para atormentar a los jugadores arrojándoles rocas gigantes.
 */
public class HombreRocaEntity extends Monster {

    protected static final EntityDataAccessor<Boolean> DATA_THROWING =
            SynchedEntityData.defineId(HombreRocaEntity.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState throwAnimationState = new AnimationState();

    private int throwTicks = 0;
    private int throwCooldown = 35;
    private int meleeCooldown = 0;

    public HombreRocaEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_THROWING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new HombreRocaCombatGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.95));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public boolean isThrowing() {
        return this.entityData.get(DATA_THROWING);
    }

    public void setThrowing(boolean throwing) {
        this.entityData.set(DATA_THROWING, throwing);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.idleAnimationState.startIfStopped(this.tickCount);
            if (this.isThrowing()) {
                this.throwAnimationState.startIfStopped(this.tickCount);
            } else {
                this.throwAnimationState.stop();
            }
        } else {
            if (this.throwCooldown > 0) {
                this.throwCooldown--;
            }
            if (this.meleeCooldown > 0) {
                this.meleeCooldown--;
            }

            if (this.isThrowing()) {
                this.throwTicks--;
                LivingEntity target = this.getTarget();
                if (target != null && target.isAlive()) {
                    this.getLookControl().setLookAt(target, 30.0F, 30.0F);
                }
                // En el tick 14 restante (0.65s transcurridos de 1.35s) el brazo suelta la roca gigante
                if (this.throwTicks == 14) {
                    this.launchRockProjectile();
                }
                if (this.throwTicks <= 0) {
                    this.setThrowing(false);
                }
            }
        }
    }

    public void startThrowAttack() {
        this.setThrowing(true);
        this.throwTicks = 27; // 1.35s == duración exacta de THROW_ROCK
        this.throwCooldown = 68; // ~3.4s entre lanzamientos
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(
                    null,
                    this.getX(), this.getY(), this.getZ(),
                    Ruleta_Hardcore.HOMBRE_ROCA_THROW_SOUND,
                    SoundSource.HOSTILE,
                    1.8F,
                    1.0F
            );
        }
    }

    private void launchRockProjectile() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        LivingEntity target = this.getTarget();
        RocaProyectilEntity rock = new RocaProyectilEntity(serverLevel, this);

        float yawRad = this.getYRot() * ((float) Math.PI / 180.0F);
        double handX = this.getX() - Mth.sin(yawRad) * 0.45 - Mth.cos(yawRad) * 0.45;
        double handY = this.getY() + 1.45;
        double handZ = this.getZ() + Mth.cos(yawRad) * 0.45 - Mth.sin(yawRad) * 0.45;
        rock.snapTo(handX, handY, handZ, this.getYRot(), this.getXRot());

        if (target != null && target.isAlive()) {
            double dx = target.getX() - handX;
            double dy = target.getY(0.45) - handY;
            double dz = target.getZ() - handZ;
            double horizDist = Math.sqrt(dx * dx + dz * dz);
            rock.shoot(dx, dy + horizDist * 0.16, dz, 1.45F, 1.2F);
        } else {
            Vec3 look = this.getLookAngle();
            rock.shoot(look.x, look.y + 0.12, look.z, 1.45F, 1.2F);
        }

        serverLevel.addFreshEntity(rock);
        serverLevel.playSound(
                null,
                this.getX(), this.getY(), this.getZ(),
                SoundEvents.SNOWBALL_THROW,
                SoundSource.HOSTILE,
                1.5F,
                0.5F
        );
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hurt = super.doHurtTarget(level, target);
        if (hurt && target instanceof LivingEntity living) {
            Vec3 push = this.getLookAngle().multiply(1.0, 0.0, 1.0);
            if (push.lengthSqr() > 1.0E-4) {
                push = push.normalize().scale(1.15);
            }
            living.push(push.x, 0.32, push.z);
            if (living instanceof ServerPlayer sp) {
                sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
            }
        }
        return hurt;
    }

    @Override
    public boolean killedEntity(ServerLevel level, LivingEntity victim, DamageSource damageSource) {
        boolean res = super.killedEntity(level, victim, damageSource);
        if (victim instanceof Player) {
            level.playSound(
                    null,
                    this.getX(), this.getY(), this.getZ(),
                    Ruleta_Hardcore.HOMBRE_ROCA_KILL_SOUND,
                    SoundSource.HOSTILE,
                    2.0F,
                    1.0F
            );
        }
        return res;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.spawnAtLocation(level, new ItemStack(Items.COBBLESTONE, this.random.nextInt(5) + 4));
        this.spawnAtLocation(level, new ItemStack(Items.ANDESITE, this.random.nextInt(4) + 2));
        if (this.random.nextFloat() < 0.35F) {
            this.spawnAtLocation(level, new ItemStack(Items.IRON_INGOT, this.random.nextInt(2) + 1));
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    static class HombreRocaCombatGoal extends Goal {
        private final HombreRocaEntity mob;
        private int strafeTime = 0;
        private boolean strafeClockwise = false;

        public HombreRocaCombatGoal(HombreRocaEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void stop() {
            this.mob.getNavigation().stop();
            this.mob.getMoveControl().strafe(0.0F, 0.0F);
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target == null) {
                return;
            }

            double distSqr = this.mob.distanceToSqr(target);
            boolean canSee = this.mob.getSensing().hasLineOfSight(target);
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

            if (this.mob.isThrowing()) {
                this.mob.getNavigation().stop();
                this.mob.getMoveControl().strafe(0.0F, 0.0F);
                return;
            }

            // Si el jugador se pega cuerpo a cuerpo (< 3.1 bloques), da un golpe seco para empujarlo
            if (distSqr <= 9.6 && canSee && this.mob.meleeCooldown <= 0) {
                this.mob.meleeCooldown = 28;
                if (this.mob.level() instanceof ServerLevel serverLevel) {
                    this.mob.doHurtTarget(serverLevel, target);
                }
                return;
            }

            // Mantener distancia media (7 a 20 bloques) para bombardear con rocas
            if (!canSee || distSqr > 324.0) { // > 18 bloques
                this.mob.getMoveControl().strafe(0.0F, 0.0F);
                this.mob.getNavigation().moveTo(target, 1.05);
            } else {
                this.mob.getNavigation().stop();
                this.strafeTime++;
                if (this.strafeTime >= 25) {
                    if (this.mob.getRandom().nextFloat() < 0.35F) {
                        this.strafeClockwise = !this.strafeClockwise;
                    }
                    this.strafeTime = 0;
                }
                float forward = distSqr < 56.0 ? -0.45F : (distSqr > 196.0 ? 0.45F : 0.0F);
                float sideways = this.strafeClockwise ? 0.35F : -0.35F;
                this.mob.getMoveControl().strafe(forward, sideways);
                this.mob.setYRot(this.mob.yHeadRot);
            }

            // Lanzar la roca gigante si está en rango (3.1 a 26 bloques) y con línea de visión
            if (canSee && distSqr >= 9.6 && distSqr <= 676.0 && this.mob.throwCooldown <= 0) {
                this.mob.startThrowAttack();
            }
        }
    }
}
