package com.luishugomod01.client.mixin;

import com.luishugomod01.client.ritual.ReviveRitualClientManager;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void ruleta$ocultarManoEnCinematica(
            CameraRenderState cameraRenderState,
            PlayerRenderState playerRenderState,
            GpuTextureView lightmap,
            CallbackInfo ci
    ) {
        if (ReviveRitualClientManager.isLocalPlayerInCinematic()) {
            ci.cancel();
        }
    }
}
