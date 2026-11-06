package com.luishugomod01.mixin;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.ruleta.CursosActivos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.breeze.Shoot;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.BreezeWindCharge;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Shoot.class)
public abstract class BreezeShootMixin {

    @Inject(
            method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/monster/breeze/Breeze;J)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectileUsingShoot(Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;DDDFF)Lnet/minecraft/world/entity/projectile/Projectile;"
            )
    )
    private void ruleta$rafagaCorrientesAereas(ServerLevel level, Breeze breeze, long gameTime, CallbackInfo ci) {
        if (!CursosActivos.isCorrientesAereas()) {
            return;
        }
        var srv = level.getServer();
        if (srv == null) {
            return;
        }
        long baseTick = srv.getTickCount();
        float inaccuracy = (float) (5 - level.getDifficulty().getId() * 4);

        // Disparar 5 Wind Charges adicionales en ráfaga (total 6 como los Blazes)
        for (int i = 1; i <= 5; i++) {
            long fireTick = baseTick + (i * 3L);
            Ruleta_Hardcore.programarTarea(fireTick, () -> {
                if (!breeze.isAlive()) {
                    return;
                }
                LivingEntity target = breeze.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
                if (target == null || !target.isAlive()) {
                    return;
                }
                double dx = target.getX() - breeze.getX();
                double dy = target.getY(target.isPassenger() ? 0.8 : 0.3) - breeze.getFiringYPosition();
                double dz = target.getZ() - breeze.getZ();

                Projectile.spawnProjectileUsingShoot(
                        new BreezeWindCharge(breeze, level),
                        level,
                        ItemStack.EMPTY,
                        dx, dy, dz,
                        0.7f,
                        inaccuracy
                );
                breeze.playSound(SoundEvents.BREEZE_SHOOT, 1.5f, 1.0f);
            });
        }
    }
}
