package com.luishugomod01.client.mixin;

import com.luishugomod01.client.ritual.ReviveRitualClientManager;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void ruleta$bloquearGiroCamaraEnCinematica(double movementTime, CallbackInfo ci) {
        if (ReviveRitualClientManager.isLocalPlayerInCinematic()) {
            ci.cancel();
        }
    }
}
