package com.luishugomod01.mixin;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.ruleta.CursosActivos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
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
        AbstractArrow self = (AbstractArrow) (Object) this;
        if (self.onGround()) return;
        if (!(self.level() instanceof ServerLevel serverLevel)) return;

        // Cazador Ígneo (Nivel 4): Flechas de fuego fatuo azul disparadas por Wither Skeletons
        if (CursosActivos.isCazadorIgneo() && !(self instanceof ThrownTrident)
                && self.getOwner() instanceof WitherSkeleton ws && ws.entityTags().contains("cazador_igneo")) {
            serverLevel.sendParticles(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    self.getX(), self.getY(), self.getZ(),
                    2, 0.03, 0.03, 0.03, 0.01
            );
        }

        boolean isPykete = CursosActivos.isPykete()
                || self.entityTags().contains("tridente_pykete")
                || (self.getOwner() instanceof Drowned d && (d.entityTags().contains("pykete_arponero") || d.entityTags().contains("pykete")));
        if (!isPykete) return;
        if (!(self instanceof ThrownTrident)) return;
        if (self.tickCount % 2 != 0) return;

        if (self.getOwner() instanceof Drowned drowned && drowned.isAlive()) {
            double dx = drowned.getX() - self.getX();
            double dy = (drowned.getY() + 1.2) - self.getY();
            double dz = drowned.getZ() - self.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > 0.5 && dist < 40.0) {
                int points = Math.min(14, Math.max(3, (int) (dist * 1.5)));
                for (int i = 1; i < points; i++) {
                    double t = (double) i / points;
                    serverLevel.sendParticles(
                            ParticleTypes.COMPOSTER,
                            self.getX() + dx * t,
                            self.getY() + dy * t,
                            self.getZ() + dz * t,
                            1, 0.01, 0.01, 0.01, 0.0
                    );
                }
            }
        }
    }

    @org.spongepowered.asm.mixin.Unique
    private float ruleta$vidaAntesFlecha = -1.0f;

    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true)
    private void ruleta$brujaReflejante(EntityHitResult hitResult, CallbackInfo ci) {
        AbstractArrow self = (AbstractArrow) (Object) this;
        this.ruleta$vidaAntesFlecha = -1.0f;
        if (!(self.level() instanceof ServerLevel serverLevel)) return;
        if (hitResult.getEntity() instanceof LivingEntity living) {
            this.ruleta$vidaAntesFlecha = living.getHealth() + living.getAbsorptionAmount();
        }

        // Creeper Fantasma (Nivel 5): completamente inmune a proyectiles (las flechas y tridentes lo atraviesan sin dañarlo)
        if (CursosActivos.isCreeperFantasma()
                && hitResult.getEntity() instanceof Creeper creeper
                && creeper.isAlive()
                && creeper.entityTags().contains("creeper_fantasma")) {
            serverLevel.sendParticles(ParticleTypes.SOUL, creeper.getX(), creeper.getY() + 1.0, creeper.getZ(), 12, 0.3, 0.5, 0.3, 0.03);
            serverLevel.sendParticles(ParticleTypes.CLOUD, self.getX(), self.getY(), self.getZ(), 6, 0.15, 0.15, 0.15, 0.02);
            serverLevel.playSound(null, creeper.getX(), creeper.getY(), creeper.getZ(), SoundEvents.SOUL_ESCAPE.value(), SoundSource.HOSTILE, 1.0f, 1.3f);
            ci.cancel();
            return;
        }

        if (!CursosActivos.isBrujaReflejante()) return;

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

        boolean impactoSinBloqueo = !(hitResult.getEntity() instanceof LivingEntity livingHit)
                || this.ruleta$vidaAntesFlecha < 0.0f
                || (livingHit.getHealth() + livingHit.getAbsorptionAmount()) < this.ruleta$vidaAntesFlecha - 0.01f;

        if (CursosActivos.isCazadorIgneo() && impactoSinBloqueo && !(self instanceof ThrownTrident)
                && self.getOwner() instanceof WitherSkeleton ws && ws.entityTags().contains("cazador_igneo")
                && hitResult.getEntity() instanceof LivingEntity target && target.isAlive()) {
            target.igniteForTicks(120);
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 120, 0), ws);
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    28, 0.35, 0.5, 0.35, 0.06);
            serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.SOUL_ESCAPE.value(), SoundSource.HOSTILE, 1.4f, 0.8f);
        }

        if (hitResult.getEntity() instanceof Creeper creeper && creeper.isAlive()
                && creeper.entityTags().contains("creeper_termonuclear")) {
            Ruleta_Hardcore.enfriarCreeperTermonuclear(creeper, serverLevel, 35);
        }

        if (CursosActivos.isBoogieWoogie() && impactoSinBloqueo && !(self instanceof ThrownTrident)) {
            if (self.getOwner() instanceof AbstractSkeleton skeleton && skeleton.isAlive()
                    && !skeleton.entityTags().contains("cazador_igneo")
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
