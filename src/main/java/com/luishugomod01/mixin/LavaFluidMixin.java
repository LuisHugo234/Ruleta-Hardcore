package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.CursosActivos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LavaFluid.class)
public abstract class LavaFluidMixin {

    @Inject(method = "isFastLava", at = @At("HEAD"), cancellable = true)
    private static void ruleta$lavaActivaEnOverworld(LevelReader level, CallbackInfoReturnable<Boolean> cir) {
        if (CursosActivos.isLavaActiva()) {
            cir.setReturnValue(true);
        }
    }
}
