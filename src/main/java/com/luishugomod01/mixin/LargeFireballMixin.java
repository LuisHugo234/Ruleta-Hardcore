package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.CursosActivos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LargeFireball.class)
public abstract class LargeFireballMixin {

    @Shadow
    private int explosionPower;

    @Inject(
            method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/phys/Vec3;I)V",
            at = @At("TAIL")
    )
    private void ruleta$triplicarPoderExplosivoEnderGhast(
            Level level,
            LivingEntity shooter,
            Vec3 movement,
            int explosionPower,
            CallbackInfo ci
    ) {
        if (CursosActivos.isEnderGhast() && shooter instanceof Ghast) {
            this.explosionPower = 20;
        }
    }
}
