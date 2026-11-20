package com.luishugomod01.client.mixin;

import com.luishugomod01.client.ritual.ReviveRitualWorldRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Inject(method = "submitFeatures", at = @At("TAIL"))
    private void ruleta$renderizarRitualRevivir(
            LevelRenderState levelRenderState,
            SubmitNodeCollector collector,
            boolean renderOutline,
            CallbackInfo ci
    ) {
        if (levelRenderState == null || levelRenderState.cameraRenderState == null) {
            return;
        }
        PoseStack poseStack = new PoseStack();
        ReviveRitualWorldRenderer.submit(poseStack, collector, levelRenderState.cameraRenderState);
    }
}
