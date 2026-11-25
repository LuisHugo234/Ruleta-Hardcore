package com.luishugomod01.entity;

import com.luishugomod01.network.FrogzillaDevourSyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FrogzillaEntity extends Monster {

    private static final EntityDataAccessor<Float> DATA_MOUTH_OPEN =
            SynchedEntityData.defineId(FrogzillaEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TONGUE_BLOCKS =
            SynchedEntityData.defineId(FrogzillaEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TONGUE_YAW =
            SynchedEntityData.defineId(FrogzillaEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_TONGUE_PITCH =
            SynchedEntityData.defineId(FrogzillaEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_TONGUE_LOCKED =
            SynchedEntityData.defineId(FrogzillaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HAS_SWALLOWED =
            SynchedEntityData.defineId(FrogzillaEntity.class, EntityDataSerializers.BOOLEAN);

    private static final Map<UUID, FrogzillaEntity> DEVOUR_BY_PLAYER = new ConcurrentHashMap<>();

    private static final double MOUTH_Y_OFFSET = 1.12;
    private static final double TONGUE_MAX_RANGE = 10.0;
    private static final int TONGUE_WINDUP_TICKS = 6;
    private static final double TONGUE_SPEED_PER_TICK = 1.25;
    private static final double TONGUE_PULL_PER_TICK = 1.45;
    private static final double TONGUE_RETRACT_PER_TICK = 1.85;
    private static final int DEVOUR_COOLDOWN_TICKS = 300; // 15 segundos tras soltar
    private static final int SHIELD_BLOCK_COOLDOWN_TICKS = 80; // 4 segundos si bloquea con escudo
    private static final int MISS_COOLDOWN_TICKS = 55; // 2.75 segundos si falla el proyectil

    private enum TonguePhase { IDLE, WINDUP, EXTENDING, PULLING, RETRACTING }

    private TonguePhase tonguePhase = TonguePhase.IDLE;
    private int windupTicks = 0;
    private Vec3 tongueOrigin = Vec3.ZERO;
    private Vec3 tongueDir = Vec3.ZERO;
    private float lockedYaw = 0.0F;
    private float lockedPitch = 0.0F;
    private double tongueLength = 0.0;
    private UUID pulledPlayerUuid = null;

    private int tongueCooldown = 40;
    private int meleeCooldown = 0;
    private int biteAnimTicks = 0;
    private int hopCooldown = 0;

    private UUID devouredPlayerUuid = null;
    private int devourDamageTicker = 0;
    private int escapeProgressPct = 0;
    private float damageTakenWhileSwallowing = 0.0F;
    private boolean playerWasInvisible = false;

    // Interpolación cliente para suavizar la lengua 3D a 60+ FPS
    public float clientPrevTongueBlocks = 0.0F;
    public float clientTongueBlocks = 0.0F;
    public float clientPrevMouthOpen = 0.0F;
    public float clientMouthOpen = 0.0F;

    public FrogzillaEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 25;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.65)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_MOUTH_OPEN, 0.0F);
        builder.define(DATA_TONGUE_BLOCKS, 0.0F);
        builder.define(DATA_TONGUE_YAW, 0.0F);
        builder.define(DATA_TONGUE_PITCH, 0.0F);
        builder.define(DATA_TONGUE_LOCKED, false);
        builder.define(DATA_HAS_SWALLOWED, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FrogzillaCombatGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.95));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 14.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public float getMouthOpen() {
        return this.entityData.get(DATA_MOUTH_OPEN);
    }

    public float getTongueBlocks() {
        return this.entityData.get(DATA_TONGUE_BLOCKS);
    }

    public float getTongueYaw() {
        return this.entityData.get(DATA_TONGUE_YAW);
    }

    public float getTonguePitch() {
        return this.entityData.get(DATA_TONGUE_PITCH);
    }

    public boolean isTongueLocked() {
        return this.entityData.get(DATA_TONGUE_LOCKED);
    }

    public boolean hasSwallowedPlayer() {
        return this.entityData.get(DATA_HAS_SWALLOWED);
    }

    public static void registrarPulsacionEscape(ServerPlayer player) {
        FrogzillaEntity frog = DEVOUR_BY_PLAYER.get(player.getUUID());
        if (frog != null && frog.isAlive() && player.getUUID().equals(frog.devouredPlayerUuid)) {
            frog.procesarPulsacionEscape(player);
        }
    }

    public static void liberarSiEstaDevorado(ServerPlayer player) {
        FrogzillaEntity frog = DEVOUR_BY_PLAYER.get(player.getUUID());
        if (frog != null) {
            frog.expulsarJugador(true);
        }
    }

    public static boolean estaDevorado(UUID playerUuid) {
        return DEVOUR_BY_PLAYER.containsKey(playerUuid);
    }

    private void procesarPulsacionEscape(ServerPlayer player) {
        this.escapeProgressPct = Math.min(100, this.escapeProgressPct + 5);
        if (this.escapeProgressPct >= 100) {
            expulsarJugador(true);
        } else {
            sincronizarEstadoDevorado(player, true);
        }
    }

    private Vec3 getMouthWorldPos() {
        return new Vec3(this.getX(), this.getY() + MOUTH_Y_OFFSET, this.getZ());
    }

    private boolean tieneLineaFisicaLibre(ServerLevel level, Vec3 from, Vec3 to) {
        HitResult hit = level.clip(new ClipContext(
                from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        return hit.getType() == HitResult.Type.MISS;
    }

    private void fijarRotacionCuerpo(float yaw) {
        this.setYRot(yaw);
        this.yRotO = yaw;
        this.yBodyRot = yaw;
        this.yBodyRotO = yaw;
        this.yHeadRot = yaw;
        this.yHeadRotO = yaw;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.clientPrevTongueBlocks = this.clientTongueBlocks;
            this.clientTongueBlocks = this.getTongueBlocks();
            this.clientPrevMouthOpen = this.clientMouthOpen;
            this.clientMouthOpen = this.getMouthOpen();

            if (this.isTongueLocked()) {
                fijarRotacionCuerpo(this.getTongueYaw());
            }
            return;
        }

        if (this.tongueCooldown > 0) this.tongueCooldown--;
        if (this.meleeCooldown > 0) this.meleeCooldown--;
        if (this.hopCooldown > 0) this.hopCooldown--;

        float targetMouth = 0.0F;
        if (this.biteAnimTicks > 0) {
            this.biteAnimTicks--;
            targetMouth = Mth.sin((this.biteAnimTicks / 10.0F) * (float) Math.PI);
        }

        if (this.tonguePhase != TonguePhase.IDLE) {
            actualizarProyectilLengua();
            this.entityData.set(DATA_TONGUE_BLOCKS, (float) this.tongueLength);
            if (this.tonguePhase == TonguePhase.WINDUP) {
                float w = 1.0F - (this.windupTicks / (float) TONGUE_WINDUP_TICKS);
                targetMouth = Math.max(targetMouth, 0.35F + 0.65F * w);
            } else if (this.tonguePhase == TonguePhase.EXTENDING || this.tonguePhase == TonguePhase.PULLING) {
                targetMouth = 1.0F;
            } else if (this.tonguePhase == TonguePhase.RETRACTING) {
                targetMouth = Math.max(targetMouth, (float) Mth.clamp(this.tongueLength / 2.5, 0.0, 1.0));
            }
        } else {
            if (this.entityData.get(DATA_TONGUE_BLOCKS) > 0.0F) {
                this.entityData.set(DATA_TONGUE_BLOCKS, 0.0F);
            }
            if (this.entityData.get(DATA_TONGUE_LOCKED)) {
                this.entityData.set(DATA_TONGUE_LOCKED, false);
            }
        }

        this.entityData.set(DATA_MOUTH_OPEN, targetMouth);

        if (this.devouredPlayerUuid != null) {
            actualizarJugadorDevorado();
        }
    }

    private void iniciarLenguetazo(ServerPlayer target) {
        Vec3 mouth = getMouthWorldPos();
        Vec3 aimPoint = new Vec3(target.getX(), target.getY() + target.getBbHeight() * 0.55, target.getZ());
        Vec3 delta = aimPoint.subtract(mouth);
        if (delta.lengthSqr() < 1.0E-4) {
            return;
        }
        this.tongueOrigin = mouth;
        this.tongueDir = delta.normalize();
        this.lockedYaw = (float) (Mth.atan2(this.tongueDir.z, this.tongueDir.x) * (180.0 / Math.PI)) - 90.0F;
        double horiz = Math.sqrt(this.tongueDir.x * this.tongueDir.x + this.tongueDir.z * this.tongueDir.z);
        this.lockedPitch = (float) Mth.clamp(Mth.atan2(this.tongueDir.y, horiz), -0.65, 0.65);

        this.tongueLength = 0.0;
        this.windupTicks = TONGUE_WINDUP_TICKS;
        this.pulledPlayerUuid = null;
        this.tonguePhase = TonguePhase.WINDUP;

        this.getNavigation().stop();
        fijarRotacionCuerpo(this.lockedYaw);
        this.entityData.set(DATA_TONGUE_YAW, this.lockedYaw);
        this.entityData.set(DATA_TONGUE_PITCH, this.lockedPitch);
        this.entityData.set(DATA_TONGUE_LOCKED, true);

        // Aviso sonoro breve mientras abre la boca y apunta
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.FROG_AMBIENT, SoundSource.HOSTILE, 1.5F, 0.45F);
    }

    private void actualizarProyectilLengua() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            finalizarCicloLengua();
            return;
        }

        // Mantener el cuerpo alineado con la dirección exacta del disparo para que jamás dispare de lado
        fijarRotacionCuerpo(this.lockedYaw);
        this.tongueOrigin = getMouthWorldPos();

        if (this.tonguePhase == TonguePhase.WINDUP) {
            this.windupTicks--;
            if (this.windupTicks <= 0) {
                // Congelar dirección final justo antes de disparar (solo si el objetivo sigue visible)
                LivingEntity target = this.getTarget();
                if (target instanceof ServerPlayer sp && sp.isAlive() && !sp.isCreative() && !sp.isSpectator()) {
                    Vec3 aimPoint = new Vec3(sp.getX(), sp.getY() + sp.getBbHeight() * 0.55, sp.getZ());
                    Vec3 delta = aimPoint.subtract(this.tongueOrigin);
                    if (delta.lengthSqr() > 1.0E-4) {
                        this.tongueDir = delta.normalize();
                        this.lockedYaw = (float) (Mth.atan2(this.tongueDir.z, this.tongueDir.x) * (180.0 / Math.PI)) - 90.0F;
                        double horiz = Math.sqrt(this.tongueDir.x * this.tongueDir.x + this.tongueDir.z * this.tongueDir.z);
                        this.lockedPitch = (float) Mth.clamp(Mth.atan2(this.tongueDir.y, horiz), -0.65, 0.65);
                        fijarRotacionCuerpo(this.lockedYaw);
                        this.entityData.set(DATA_TONGUE_YAW, this.lockedYaw);
                        this.entityData.set(DATA_TONGUE_PITCH, this.lockedPitch);
                    }
                }
                this.tonguePhase = TonguePhase.EXTENDING;
                serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.FROG_TONGUE, SoundSource.HOSTILE, 1.9F, 0.60F);
            }
            return;
        }

        if (this.tonguePhase == TonguePhase.EXTENDING) {
            double prevLen = this.tongueLength;
            double nextLen = Math.min(TONGUE_MAX_RANGE, this.tongueLength + TONGUE_SPEED_PER_TICK);

            Vec3 prevTip = this.tongueOrigin.add(this.tongueDir.scale(prevLen));
            Vec3 nextTip = this.tongueOrigin.add(this.tongueDir.scale(nextLen));

            // 1. Verificar colisión estricta con bloques sólidos (paredes, puertas, vidrios, cercas)
            HitResult blockHit = serverLevel.clip(new ClipContext(
                    prevTip, nextTip, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            boolean hitBlock = blockHit.getType() == HitResult.Type.BLOCK;
            Vec3 endTip = hitBlock ? blockHit.getLocation() : nextTip;
            double effectiveLen = this.tongueOrigin.distanceTo(endTip);

            serverLevel.sendParticles(ParticleTypes.ITEM_SLIME,
                    endTip.x, endTip.y, endTip.z, 2, 0.06, 0.06, 0.06, 0.01);

            // 2. Verificar colisión del proyectil de lengua SOLO antes de cualquier bloque sólido
            AABB segmentBox = new AABB(prevTip, endTip).inflate(0.28);
            List<ServerPlayer> candidatos = serverLevel.getEntitiesOfClass(
                    ServerPlayer.class,
                    segmentBox,
                    p -> p.isAlive() && !p.isCreative() && !p.isSpectator() && !estaDevorado(p.getUUID())
            );

            ServerPlayer impactado = null;
            double mejorDistSq = Double.MAX_VALUE;
            for (ServerPlayer p : candidatos) {
                AABB pBox = p.getBoundingBox().inflate(0.22);
                if (pBox.clip(prevTip, endTip).isPresent() || pBox.contains(endTip)) {
                    double dSq = p.distanceToSqr(prevTip);
                    if (dSq < mejorDistSq) {
                        mejorDistSq = dSq;
                        impactado = p;
                    }
                }
            }

            if (impactado != null) {
                this.tongueLength = this.tongueOrigin.distanceTo(impactado.position().add(0.0, impactado.getBbHeight() * 0.5, 0.0));
                procesarImpactoLengua(serverLevel, impactado);
                return;
            }

            this.tongueLength = effectiveLen;

            if (hitBlock) {
                serverLevel.sendParticles(ParticleTypes.ITEM_SLIME,
                        endTip.x, endTip.y, endTip.z, 8, 0.14, 0.14, 0.14, 0.05);
                serverLevel.playSound(null, endTip.x, endTip.y, endTip.z,
                        SoundEvents.SLIME_SQUISH_SMALL, SoundSource.HOSTILE, 0.9F, 0.85F);
                this.tonguePhase = TonguePhase.RETRACTING;
                this.tongueCooldown = MISS_COOLDOWN_TICKS;
                return;
            }

            if (this.tongueLength >= TONGUE_MAX_RANGE - 0.01) {
                this.tonguePhase = TonguePhase.RETRACTING;
                this.tongueCooldown = MISS_COOLDOWN_TICKS;
            }
            return;
        }

        if (this.tonguePhase == TonguePhase.PULLING) {
            Entity ent = this.pulledPlayerUuid != null ? serverLevel.getEntity(this.pulledPlayerUuid) : null;
            if (!(ent instanceof ServerPlayer sp) || !sp.isAlive() || sp.isCreative() || sp.isSpectator()) {
                this.pulledPlayerUuid = null;
                this.tonguePhase = TonguePhase.RETRACTING;
                return;
            }

            this.tongueLength = Math.max(0.0, this.tongueLength - TONGUE_PULL_PER_TICK);
            Vec3 pullPoint = this.tongueOrigin.add(this.tongueDir.scale(this.tongueLength));

            // Si un bloque sólido obstruye el arrastre, soltar al jugador en lugar de atravesar paredes
            Vec3 playerCenter = sp.position().add(0.0, sp.getBbHeight() * 0.5, 0.0);
            if (!tieneLineaFisicaLibre(serverLevel, this.tongueOrigin, playerCenter)) {
                this.pulledPlayerUuid = null;
                this.tonguePhase = TonguePhase.RETRACTING;
                this.tongueCooldown = MISS_COOLDOWN_TICKS;
                return;
            }

            // Arrastrar físicamente al jugador pegado a la punta de la lengua hacia la boca abierta
            sp.teleportTo(pullPoint.x, Math.max(this.getY() + 0.15, pullPoint.y - sp.getBbHeight() * 0.45), pullPoint.z);
            sp.setDeltaMovement(Vec3.ZERO);
            sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
            serverLevel.sendParticles(ParticleTypes.ITEM_SLIME,
                    pullPoint.x, pullPoint.y, pullPoint.z, 3, 0.12, 0.12, 0.12, 0.03);

            // Al llegar a la boca de Frogzilla, lo traga orgánicamente
            if (this.tongueLength <= 1.65) {
                this.pulledPlayerUuid = null;
                this.tongueLength = 0.0;
                finalizarCicloLengua();
                if (sp.isAlive() && !estaDevorado(sp.getUUID())) {
                    devorarJugador(sp);
                }
            }
            return;
        }

        if (this.tonguePhase == TonguePhase.RETRACTING) {
            this.tongueLength = Math.max(0.0, this.tongueLength - TONGUE_RETRACT_PER_TICK);
            if (this.tongueLength <= 0.01) {
                finalizarCicloLengua();
            }
        }
    }

    private void finalizarCicloLengua() {
        this.tonguePhase = TonguePhase.IDLE;
        this.tongueLength = 0.0;
        this.pulledPlayerUuid = null;
        this.entityData.set(DATA_TONGUE_BLOCKS, 0.0F);
        this.entityData.set(DATA_TONGUE_LOCKED, false);
    }

    private void procesarImpactoLengua(ServerLevel serverLevel, ServerPlayer target) {
        // Verificar bloqueo con escudo
        if (target.isBlocking()) {
            Vec3 look = target.getLookAngle().normalize();
            Vec3 toFrog = this.position().subtract(target.position()).normalize();
            if (look.dot(toFrog) > 0.20) {
                serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.1F, 0.95F);
                InteractionHand hand = target.getUsedItemHand();
                ItemStack shield = target.getItemInHand(hand);
                if (!shield.isEmpty()) {
                    EquipmentSlot slot = (hand == InteractionHand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                    shield.hurtAndBreak(2, target, slot);
                }
                this.tongueCooldown = SHIELD_BLOCK_COOLDOWN_TICKS;
                this.tonguePhase = TonguePhase.RETRACTING;
                return;
            }
        }

        // Golpe físico del lengüetazo al impactar al jugador + iniciar arrastre hacia la boca
        serverLevel.sendParticles(ParticleTypes.ITEM_SLIME,
                target.getX(), target.getY() + 1.0, target.getZ(),
                18, 0.40, 0.40, 0.40, 0.10);
        serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.SLIME_ATTACK, SoundSource.HOSTILE, 1.5F, 0.72F);
        target.hurtServer(serverLevel, this.damageSources().mobAttack(this), 2.0F);

        if (target.isAlive() && !estaDevorado(target.getUUID())) {
            this.pulledPlayerUuid = target.getUUID();
            this.tonguePhase = TonguePhase.PULLING;
        } else {
            this.tonguePhase = TonguePhase.RETRACTING;
        }
    }

    private void devorarJugador(ServerPlayer player) {
        this.devouredPlayerUuid = player.getUUID();
        this.devourDamageTicker = 0;
        this.escapeProgressPct = 0;
        this.damageTakenWhileSwallowing = 0.0F;
        this.playerWasInvisible = player.isInvisible();

        DEVOUR_BY_PLAYER.put(player.getUUID(), this);
        this.entityData.set(DATA_HAS_SWALLOWED, true);

        player.setInvisible(true);
        player.setDeltaMovement(Vec3.ZERO);
        player.connection.send(new ClientboundSetEntityMotionPacket(player));
        player.teleportTo(this.getX(), this.getY() + 0.45, this.getZ());

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.FROG_EAT, SoundSource.HOSTILE, 2.0F, 0.55F);

        sincronizarEstadoDevorado(player, true);
    }

    private void actualizarJugadorDevorado() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(this.devouredPlayerUuid);
        if (player == null || !player.isAlive() || player.isSpectator() || player.level() != serverLevel) {
            limpiarEstadoDevorado(player);
            return;
        }

        // Mantener al jugador inmovilizado y oculto hasta que escape con ESPACIO o lo rescate un compañero
        player.setInvisible(true);
        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0.0F;
        if (player.distanceToSqr(this.getX(), this.getY() + 0.45, this.getZ()) > 0.08) {
            player.teleportTo(this.getX(), this.getY() + 0.45, this.getZ());
        }

        this.devourDamageTicker++;
        if (this.devourDamageTicker >= 20) {
            this.devourDamageTicker = 0;
            player.hurtServer(serverLevel, this.damageSources().mobAttack(this), 4.0F);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.SLIME_SQUISH, SoundSource.HOSTILE, 1.2F, 0.65F);
            if (!player.isAlive()) {
                limpiarEstadoDevorado(player);
                return;
            }
        }
    }

    public void expulsarJugador(boolean escapoORescatado) {
        if (this.devouredPlayerUuid == null) {
            return;
        }
        ServerPlayer player = null;
        if (this.level() instanceof ServerLevel serverLevel) {
            player = serverLevel.getServer().getPlayerList().getPlayer(this.devouredPlayerUuid);
        }

        limpiarEstadoDevorado(player);
        this.tongueCooldown = DEVOUR_COOLDOWN_TICKS;

        if (player != null && player.isAlive() && this.level() instanceof ServerLevel serverLevel) {
            Vec3 forward = Vec3.directionFromRotation(0.0F, this.getYRot()).normalize();
            double destX = this.getX() + forward.x * 2.2;
            double destY = this.getY() + 0.6;
            double destZ = this.getZ() + forward.z * 2.2;
            // Verificar que el punto de expulsión no meta al jugador dentro de una pared
            if (!tieneLineaFisicaLibre(serverLevel, getMouthWorldPos(), new Vec3(destX, destY + 0.5, destZ))) {
                destX = this.getX();
                destY = this.getY() + 0.5;
                destZ = this.getZ();
            }
            player.teleportTo(destX, destY, destZ);
            Vec3 launch = forward.scale(escapoORescatado ? 0.65 : 0.95).add(0.0, 0.35, 0.0);
            player.setDeltaMovement(launch);
            player.connection.send(new ClientboundSetEntityMotionPacket(player));

            serverLevel.sendParticles(ParticleTypes.ITEM_SLIME,
                    destX, destY + 0.5, destZ, 24, 0.5, 0.4, 0.5, 0.16);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                    escapoORescatado ? SoundEvents.FROG_HURT : SoundEvents.FROG_EAT,
                    SoundSource.HOSTILE, 1.8F, escapoORescatado ? 0.85F : 0.50F);
        }
    }

    private void limpiarEstadoDevorado(ServerPlayer player) {
        if (this.devouredPlayerUuid != null) {
            DEVOUR_BY_PLAYER.remove(this.devouredPlayerUuid);
        }
        this.devouredPlayerUuid = null;
        this.devourDamageTicker = 0;
        this.escapeProgressPct = 0;
        this.damageTakenWhileSwallowing = 0.0F;
        this.entityData.set(DATA_HAS_SWALLOWED, false);

        if (player != null) {
            if (!this.playerWasInvisible) {
                player.setInvisible(false);
            }
            sincronizarEstadoDevorado(player, false);
        }
    }

    private void sincronizarEstadoDevorado(ServerPlayer player, boolean active) {
        ServerPlayNetworking.send(player, new FrogzillaDevourSyncPayload(
                active,
                active ? this.escapeProgressPct : 0,
                0,
                active ? this.getId() : -1
        ));
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        float healthBefore = this.getHealth();
        boolean hurt = super.hurtServer(level, source, amount);
        if (hurt && this.devouredPlayerUuid != null) {
            float dealt = Math.max(0.0F, healthBefore - this.getHealth());
            this.damageTakenWhileSwallowing += dealt;
            // Rescate aliado al quitarle el 25% de su vida máxima (20 HP)
            if (this.damageTakenWhileSwallowing >= this.getMaxHealth() * 0.25F) {
                expulsarJugador(true);
            }
        }
        return hurt;
    }

    @Override
    public void die(DamageSource damageSource) {
        if (!this.level().isClientSide() && this.devouredPlayerUuid != null) {
            expulsarJugador(true);
        }
        super.die(damageSource);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.level().isClientSide() && this.devouredPlayerUuid != null) {
            expulsarJugador(true);
        }
        super.remove(reason);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.FROG_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.FROG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.FROG_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return 0.52F + this.random.nextFloat() * 0.12F;
    }

    private static class FrogzillaCombatGoal extends Goal {
        private final FrogzillaEntity frog;

        FrogzillaCombatGoal(FrogzillaEntity frog) {
            this.frog = frog;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.frog.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.frog.getTarget();
            return target != null && target.isAlive() && this.frog.distanceToSqr(target) < 34.0 * 34.0;
        }

        @Override
        public void tick() {
            LivingEntity target = this.frog.getTarget();
            if (target == null) return;

            if (this.frog.tonguePhase != TonguePhase.IDLE) {
                this.frog.getNavigation().stop();
                return;
            }

            this.frog.getLookControl().setLookAt(target, 30.0F, 30.0F);
            double dist = this.frog.distanceTo(target);

            double moveSpeed = this.frog.hasSwallowedPlayer() ? 0.65 : 1.12;
            if (dist > 2.2) {
                this.frog.getNavigation().moveTo(target, moveSpeed);
                if (!this.frog.hasSwallowedPlayer() && this.frog.onGround() && this.frog.hopCooldown <= 0 && dist > 3.5 && dist < 14.0) {
                    Vec3 dir = target.position().subtract(this.frog.position()).normalize();
                    this.frog.setDeltaMovement(dir.x * 0.42, 0.46, dir.z * 0.42);
                    this.frog.hopCooldown = 36;
                    this.frog.level().playSound(null, this.frog.getX(), this.frog.getY(), this.frog.getZ(),
                            SoundEvents.FROG_LONG_JUMP, SoundSource.HOSTILE, 1.4F, 0.58F);
                }
            } else {
                this.frog.getNavigation().stop();
            }

            // Disparo de lengua proyectil hasta 10 bloques (con verificación estricta de colisión de bloques)
            if (!this.frog.hasSwallowedPlayer()
                    && this.frog.tongueCooldown <= 0
                    && target instanceof ServerPlayer sp
                    && !sp.isCreative() && !sp.isSpectator()
                    && !estaDevorado(sp.getUUID())
                    && dist >= 2.6 && dist <= TONGUE_MAX_RANGE
                    && this.frog.level() instanceof ServerLevel sl
                    && this.frog.tieneLineaFisicaLibre(sl, this.frog.getMouthWorldPos(), sp.position().add(0.0, sp.getBbHeight() * 0.55, 0.0))) {
                this.frog.iniciarLenguetazo(sp);
                return;
            }

            // Ataque cuerpo a cuerpo normal (6 de daño = 3 corazones, sin atravesar paredes)
            if (dist <= 2.9 && this.frog.meleeCooldown <= 0 && this.frog.level() instanceof ServerLevel sl
                    && this.frog.tieneLineaFisicaLibre(sl, this.frog.getMouthWorldPos(), target.position().add(0.0, target.getBbHeight() * 0.5, 0.0))) {
                this.frog.meleeCooldown = 22;
                this.frog.biteAnimTicks = 10;
                target.hurtServer(sl, this.frog.damageSources().mobAttack(this.frog), 6.0F);
                sl.playSound(null, this.frog.getX(), this.frog.getY(), this.frog.getZ(),
                        SoundEvents.FROG_EAT, SoundSource.HOSTILE, 1.4F, 0.62F);
            }
        }
    }
}
