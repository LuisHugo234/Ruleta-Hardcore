package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.CursosActivos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin {

    @Inject(method = "onHitEntity", at = @At("TAIL"))
    private void ruleta$boogieWoogieSwap(EntityHitResult hitResult, CallbackInfo ci) {
        if (!CursosActivos.isBoogieWoogie()) return;

        AbstractArrow self = (AbstractArrow) (Object) this;
        if (self instanceof ThrownTrident) return;
        if (!(self.level() instanceof ServerLevel serverLevel)) return;

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
