package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.CursosActivos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void ruleta$hiloEspectralPykete(CallbackInfo ci) {
        if (!CursosActivos.isPykete()) return;
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (!(self instanceof ThrownTrident) || self.onGround()) return;
        if (!(self.level() instanceof ServerLevel serverLevel)) return;
        if (self.tickCount % 2 != 0) return;

        if (self.getOwner() instanceof Drowned drowned && drowned.isAlive()) {
            double dx = drowned.getX() - self.getX();
            double dy = (drowned.getY() + 1.2) - self.getY();
            double dz = drowned.getZ() - self.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > 0.5 && dist < 40.0) {
                int points = Math.min(12, Math.max(3, (int) (dist * 1.5)));
                for (int i = 1; i < points; i++) {
                    double t = (double) i / points;
                    serverLevel.sendParticles(
                            ParticleTypes.SOUL_FIRE_FLAME,
                            self.getX() + dx * t,
                            self.getY() + dy * t,
                            self.getZ() + dz * t,
                            1, 0.01, 0.01, 0.01, 0.0
                    );
                }
            }
        }
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true)
    private void ruleta$brujaReflejante(EntityHitResult hitResult, CallbackInfo ci) {
        if (!CursosActivos.isBrujaReflejante()) return;
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (!(self.level() instanceof ServerLevel serverLevel)) return;

        if (hitResult.getEntity() instanceof Witch witch && witch.isAlive()) {
            if (ThreadLocalRandom.current().nextFloat() < 0.30f) {
                serverLevel.sendParticles(ParticleTypes.WITCH, witch.getX(), witch.getY() + 1.1, witch.getZ(), 35, 0.45, 0.55, 0.45, 0.08);
                serverLevel.sendParticles(ParticleTypes.ENCHANT, witch.getX(), witch.getY() + 1.2, witch.getZ(), 40, 0.5, 0.5, 0.5, 0.4);
                serverLevel.sendParticles(ParticleTypes.END_ROD, self.getX(), self.getY(), self.getZ(), 18, 0.25, 0.25, 0.25, 0.05);
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

                if (!(self instanceof ThrownTrident)) {
                    self.setOwner(witch);
                }
                self.setDeltaMovement(vx, vy, vz);
                self.setYRot(self.getYRot() + 180.0f);
                self.setXRot(-self.getXRot());
                serverLevel.getChunkSource().sendToTrackingPlayers(self, new ClientboundSetEntityMotionPacket(self));
                ci.cancel();
            }
        }
    }

    @Inject(method = "onHitEntity", at = @At("TAIL"))
    private void ruleta$efectosImpactoProyectil(EntityHitResult hitResult, CallbackInfo ci) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (!(self.level() instanceof ServerLevel serverLevel)) return;

        // Pykete (Nivel 3): tridente de Ahogado arrastra al jugador 5 bloques hacia el Ahogado
        if (CursosActivos.isPykete() && self instanceof ThrownTrident
                && self.getOwner() instanceof Drowned drowned && drowned.isAlive()
                && hitResult.getEntity() instanceof ServerPlayer player && player.isAlive()) {
            double dx = drowned.getX() - player.getX();
            double dy = drowned.getY() - player.getY();
            double dz = drowned.getZ() - player.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > 0.5) {
                double pullBlocks = Math.min(5.0, dist);
                double nx = dx / dist;
                double ny = dy / dist;
                double nz = dz / dist;

                int steps = Math.min(28, Math.max(8, (int) (dist * 2.0)));
                for (int i = 0; i <= steps; i++) {
                    double t = (double) i / steps;
                    serverLevel.sendParticles(
                            ParticleTypes.SCULK_SOUL,
                            player.getX() + dx * t,
                            player.getY() + 0.9 + dy * t,
                            player.getZ() + dz * t,
                            1, 0.02, 0.02, 0.02, 0.0
                    );
                }
                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.HOSTILE, 1.5f, 0.6f);

                double pullSpeed = Math.min(2.1, 0.42 * pullBlocks);
                double downPull = (player.isInWater() || drowned.isInWater()) ? -0.22 : 0.15;
                player.setDeltaMovement(nx * pullSpeed, ny * (pullSpeed * 0.6) + downPull, nz * pullSpeed);
                player.connection.send(new ClientboundSetEntityMotionPacket(player));
            }
        }

        // Boogie Woogie (Nivel 3): flecha de esqueleto intercambia lugares con el jugador
        if (CursosActivos.isBoogieWoogie() && !(self instanceof ThrownTrident)) {
            if (self.getOwner() instanceof AbstractSkeleton skeleton && skeleton.isAlive()
                    && hitResult.getEntity() instanceof ServerPlayer player && player.isAlive()) {

                if (skeleton.level() != player.level()) return;

                double sx = skeleton.getX();
                double sy = skeleton.getY();
                double sz = skeleton.getZ();

                double px = player.getX();
                double py = player.getY();
                double pz = player.getZ();

                skeleton.stopRiding();
                player.stopRiding();

                skeleton.teleportTo(px, py, pz);
                player.teleportTo(sx, sy, sz);

                serverLevel.playSound(null, sx, sy, sz, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 1.2f);
                serverLevel.playSound(null, px, py, pz, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.2f);
                serverLevel.sendParticles(ParticleTypes.PORTAL, sx, sy + 1.0, sz, 25, 0.4, 0.8, 0.4, 0.2);
                serverLevel.sendParticles(ParticleTypes.PORTAL, px, py + 1.0, pz, 25, 0.4, 0.8, 0.4, 0.2);
            }
        }
    }
}
