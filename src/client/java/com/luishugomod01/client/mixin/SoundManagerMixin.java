package com.luishugomod01.client.mixin;

import com.luishugomod01.client.bloodmoon.BloodMoonClientState;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public abstract class SoundManagerMixin {

    @Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;", at = @At("HEAD"), cancellable = true)
    private void ruleta$bloquearSonidosSorderaYSombra(SoundInstance sound, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
        if (BloodMoonClientState.isPlayerDeafened()) {
            cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
            return;
        }
        if (BloodMoonClientState.isSilencioSepulcralActive() && sound != null) {
            SoundSource source = sound.getSource();
            if (source == SoundSource.AMBIENT || source == SoundSource.MUSIC || source == SoundSource.WEATHER) {
                cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
            }
        }
    }

    @Inject(method = "playDelayed(Lnet/minecraft/client/resources/sounds/SoundInstance;I)V", at = @At("HEAD"), cancellable = true)
    private void ruleta$bloquearSonidosDiferidosSorderaYSombra(SoundInstance sound, int delay, CallbackInfo ci) {
        if (BloodMoonClientState.isPlayerDeafened()) {
            ci.cancel();
            return;
        }
        if (BloodMoonClientState.isSilencioSepulcralActive() && sound != null) {
            SoundSource source = sound.getSource();
            if (source == SoundSource.AMBIENT || source == SoundSource.MUSIC || source == SoundSource.WEATHER) {
                ci.cancel();
            }
        }
    }
}
