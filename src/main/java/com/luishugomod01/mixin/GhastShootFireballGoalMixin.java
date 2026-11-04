package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.CursosActivos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.entity.monster.Ghast$GhastShootFireballGoal")
public abstract class GhastShootFireballGoalMixin {

    @Shadow
    public int chargeTime;

    @Inject(method = "tick", at = @At("TAIL"))
    private void ruleta$acelerarEnderGhast(CallbackInfo ci) {
        if (!CursosActivos.isEnderGhast()) return;

        // En vanilla, tras disparar chargeTime se reinicia a -40; lo reducimos a -10 para mucha mayor agresividad
        if (this.chargeTime == -40) {
            this.chargeTime = -10;
        } else if (this.chargeTime > 0 && this.chargeTime < 19) {
            // Carga el disparo el doble de rápido
            this.chargeTime++;
        }
    }
}
