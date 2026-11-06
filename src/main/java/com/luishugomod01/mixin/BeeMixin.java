package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.CursosActivos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.bee.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Bee.class)
public abstract class BeeMixin {

    @Shadow
    private void setHasStung(boolean hasStung) {
        throw new AssertionError();
    }

    @Inject(method = "doHurtTarget", at = @At("RETURN"))
    private void ruleta$abejasAsesinasDescomposicion(ServerLevel level, Entity target, CallbackInfoReturnable<Boolean> cir) {
        if (!CursosActivos.isAbejasAsesinas()) return;
        if (cir.getReturnValueZ() && target instanceof LivingEntity living) {
            Bee self = (Bee) (Object) this;
            living.removeEffect(MobEffects.POISON);
            living.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1), self);
            this.setHasStung(false);
        }
    }
}
