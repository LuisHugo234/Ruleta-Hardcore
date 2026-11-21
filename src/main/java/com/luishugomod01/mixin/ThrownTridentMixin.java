package com.luishugomod01.mixin;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.ruleta.CursosActivos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(ThrownTrident.class)
public abstract class ThrownTridentMixin {

    @Unique
    private float ruleta$vidaAntesImpacto = -1.0f;

    @Unique
    private boolean ruleta$esTridentePykete(ThrownTrident self) {
        if (CursosActivos.isPykete()) {
            return true;
        }
        if (self.entityTags().contains("tridente_pykete")) {
            return true;
        }
        return self.getOwner() instanceof Drowned d
                && (d.entityTags().contains("pykete_arponero") || d.entityTags().contains("pykete"));
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true)
    private void ruleta$onHitEntityHead(EntityHitResult hitResult, CallbackInfo ci) {
        ThrownTrident self = (ThrownTrident) (Object) this;
        this.ruleta$vidaAntesImpacto = -1.0f;

        if (!(self.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (CursosActivos.isCreeperFantasma()
                && hitResult.getEntity() instanceof Creeper creeper
                && creeper.isAlive()
                && creeper.entityTags().contains("creeper_fantasma")) {
            serverLevel.sendParticles(ParticleTypes.SOUL, creeper.getX(), creeper.getY() + 1.0, creeper.getZ(), 12, 0.3, 0.5, 0.3, 0.03);
            serverLevel.sendParticles(ParticleTypes.CLOUD, self.getX(), self.getY(), self.getZ(), 6, 0.15, 0.15, 0.15, 0.02);
            serverLevel.playSound(null, creeper.getX(), creeper.getY(), creeper.getZ(),
                    SoundEvents.SOUL_ESCAPE.value(), SoundSource.HOSTILE, 1.0f, 1.3f);
            ci.cancel();
            return;
        }

        if (CursosActivos.isBrujaReflejante()
                && hitResult.getEntity() instanceof Witch witch
                && witch.isAlive()
                && ThreadLocalRandom.current().nextFloat() < 0.30f) {
            serverLevel.sendParticles(ParticleTypes.WITCH, witch.getX(), witch.getY() + 1.1, witch.getZ(), 35, 0.45, 0.55, 0.45, 0.08);
            serverLevel.sendParticles(ParticleTypes.ENCHANT, witch.getX(), witch.getY() + 1.2, witch.getZ(), 40, 0.5, 0.5, 0.5, 0.4);
            serverLevel.playSound(null, witch.getX(), witch.getY(), witch.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 1.4f, 1.3f);
            serverLevel.playSound(null, witch.getX(), witch.getY(), witch.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.HOSTILE, 1.2f, 1.2f);

            Entity shooter = self.getOwner();
            Vec3 vel = self.getDeltaMovement();
            double speed = Math.max(1.6, vel.length());
            double vx;
            double vy;
            double vz;
            if (shooter instanceof LivingEntity target && target.isAlive()) {
                double dx = target.getX() - self.getX();
                double dy = (target.getEyeY() - 0.15) - self.getY();
                double dz = target.getZ() - self.getZ();
                double dist = Math.max(0.1, Math.sqrt(dx * dx + dy * dy + dz * dz));
                vx = (dx / dist) * speed;
                vy = (dy / dist) * speed + 0.08;
                vz = (dz / dist) * speed;
                self.setPos(self.getX() + (dx / dist) * 0.7, self.getY() + (dy / dist) * 0.4, self.getZ() + (dz / dist) * 0.7);
            } else {
                vx = -vel.x;
                vy = -vel.y + 0.1;
                vz = -vel.z;
                self.setPos(self.getX() + vx * 0.4, self.getY() + vy * 0.4, self.getZ() + vz * 0.4);
            }
            self.setDeltaMovement(vx, vy, vz);
            self.setYRot(self.getYRot() + 180.0f);
            self.setXRot(-self.getXRot());
            serverLevel.getChunkSource().sendToTrackingPlayers(self, new ClientboundSetEntityMotionPacket(self));
            ci.cancel();
            return;
        }

        if (!ruleta$esTridentePykete(self) || !(self.getOwner() instanceof Drowned)) {
            return;
        }

        if (hitResult.getEntity() instanceof AbstractBoat boat && boat.isAlive()) {
            serverLevel.sendParticles(ParticleTypes.CRIT, boat.getX(), boat.getY() + 0.5, boat.getZ(), 30, 0.6, 0.3, 0.6, 0.15);
            serverLevel.sendParticles(ParticleTypes.SPLASH, boat.getX(), boat.getY() + 0.5, boat.getZ(), 40, 0.8, 0.3, 0.8, 0.2);
            serverLevel.playSound(null, boat.getX(), boat.getY(), boat.getZ(),
                    SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.HOSTILE, 1.5f, 0.8f);
            boat.kill(serverLevel);
            ci.cancel();
            return;
        }

        if (hitResult.getEntity() instanceof ServerPlayer player) {
            this.ruleta$vidaAntesImpacto = player.getHealth() + player.getAbsorptionAmount();
        }
    }

    @Inject(method = "onHitEntity", at = @At("TAIL"))
    private void ruleta$onHitEntityTail(EntityHitResult hitResult, CallbackInfo ci) {
        ThrownTrident self = (ThrownTrident) (Object) this;
        if (!(self.level() instanceof ServerLevel)) {
            return;
        }
        if (!ruleta$esTridentePykete(self)) {
            return;
        }
        if (!(self.getOwner() instanceof Drowned drowned) || !drowned.isAlive()) {
            return;
        }
        if (!(hitResult.getEntity() instanceof ServerPlayer player)
                || !player.isAlive()
                || player.isCreative()
                || player.isSpectator()) {
            return;
        }
        float vidaDespues = player.getHealth() + player.getAbsorptionAmount();
        boolean recibioDano = this.ruleta$vidaAntesImpacto > 0.0f && vidaDespues < this.ruleta$vidaAntesImpacto - 0.01f;
        if (!recibioDano) {
            return;
        }

        Ruleta_Hardcore.iniciarArrastrePykete(player, drowned);
    }
}
