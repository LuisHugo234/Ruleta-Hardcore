package com.luishugomod01.entity;

import com.luishugomod01.Ruleta_Hardcore;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Roca gigante arrojada por el Hombre Roca.
 * Causa 13.0 de daño directo (6.5 corazones), empuje fuerte, Lentitud II por 2.5s,
 * deshabilita el escudo por 2s y provoca 6.0 de daño en área pequeña (2.2 bloques) sin destruir terreno.
 */
public class RocaProyectilEntity extends ThrowableItemProjectile {

    private Entity directHitEntity = null;

    public RocaProyectilEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public RocaProyectilEntity(Level level, LivingEntity shooter) {
        super(Ruleta_Hardcore.ROCA_PROYECTIL, level);
        this.setOwner(shooter);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.COBBLESTONE;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.045;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverLevel && this.tickCount % 2 == 0) {
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState()),
                    this.getX(), this.getY() + 0.25, this.getZ(),
                    2, 0.15, 0.15, 0.15, 0.02
            );
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Entity hit = result.getEntity();
        if (hit instanceof HombreRocaEntity) {
            return;
        }
        this.directHitEntity = hit;

        Entity owner = this.getOwner();
        LivingEntity livingOwner = owner instanceof LivingEntity le ? le : null;
        DamageSource dmgSource = this.damageSources().mobProjectile(this, livingOwner);

        Vec3 horiz = new Vec3(this.getDeltaMovement().x, 0.0, this.getDeltaMovement().z);
        if (horiz.lengthSqr() > 1.0E-4) {
            horiz = horiz.normalize();
        } else if (livingOwner != null) {
            Vec3 look = livingOwner.getLookAngle();
            horiz = new Vec3(look.x, 0.0, look.z).normalize();
        }

        if (hit instanceof Player player && player.isBlocking()) {
            // Desactiva el escudo por 2 segundos (40 ticks) y empuja al jugador
            ItemStack activeStack = player.getUseItem();
            if (!activeStack.isEmpty()) {
                player.getCooldowns().addCooldown(activeStack, 40);
            } else {
                player.getCooldowns().addCooldown(new ItemStack(Items.SHIELD), 40);
            }
            player.stopUsingItem();
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1.2F, 0.85F);

            hit.hurtServer(serverLevel, dmgSource, 4.0F);
            player.push(horiz.x * 0.9, 0.28, horiz.z * 0.9);
            if (player instanceof ServerPlayer sp) {
                sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
            }
            return;
        }

        boolean hurt = hit.hurtServer(serverLevel, dmgSource, 13.0F);
        if (hurt && hit instanceof LivingEntity targetLiving) {
            targetLiving.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 50, 1), livingOwner);
            targetLiving.push(horiz.x * 1.35, 0.38, horiz.z * 1.35);
            if (targetLiving instanceof ServerPlayer sp) {
                sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Entity owner = this.getOwner();
        LivingEntity livingOwner = owner instanceof LivingEntity le ? le : null;
        DamageSource dmgSource = this.damageSources().mobProjectile(this, livingOwner);

        // Daño en área pequeña (radio 2.2 bloques) sin romper bloques
        double radius = 2.2;
        List<LivingEntity> nearby = serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(radius),
                e -> e.isAlive() && e != this.directHitEntity && !(e instanceof HombreRocaEntity)
        );
        for (LivingEntity victim : nearby) {
            if (victim instanceof Player p && (p.isCreative() || p.isSpectator())) {
                continue;
            }
            double dist = victim.position().distanceTo(this.position());
            if (dist <= radius + 0.5) {
                victim.hurtServer(serverLevel, dmgSource, 6.0F);
                Vec3 pushDir = victim.position().subtract(this.position());
                Vec3 flat = new Vec3(pushDir.x, 0.0, pushDir.z);
                if (flat.lengthSqr() > 1.0E-4) {
                    flat = flat.normalize().scale(0.75);
                }
                victim.push(flat.x, 0.25, flat.z);
                if (victim instanceof ServerPlayer sp) {
                    sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
                }
            }
        }

        // Efectos visuales y sonoros de impacto de roca gigante
        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.STONE_BREAK, SoundSource.HOSTILE, 1.8F, 0.55F);
        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 0.45F, 0.65F);
        serverLevel.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState()),
                this.getX(), this.getY() + 0.2, this.getZ(),
                32, 0.45, 0.35, 0.45, 0.18
        );
        serverLevel.sendParticles(
                ParticleTypes.CLOUD,
                this.getX(), this.getY() + 0.2, this.getZ(),
                6, 0.35, 0.2, 0.35, 0.04
        );

        this.discard();
    }
}
