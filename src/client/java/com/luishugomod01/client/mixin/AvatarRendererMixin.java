package com.luishugomod01.client.mixin;

import com.luishugomod01.client.vidas.VidasClientState;
import com.luishugomod01.client.vidas.VidasRenderStateAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void ruleta$extractVidas(Avatar avatar, AvatarRenderState state, float partialTick, CallbackInfo ci) {
        if (state instanceof VidasRenderStateAccessor accessor) {
            Component vidasComp = VidasClientState.crearComponenteVidas(avatar.getUUID());
            accessor.ruleta$setVidasComponent(vidasComp);
        }
    }

    @Inject(
            method = "submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ruleta$submitNameDisplay(
            AvatarRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera,
            CallbackInfo ci
    ) {
        Component vidasComp = null;
        if (state instanceof VidasRenderStateAccessor accessor) {
            vidasComp = accessor.ruleta$getVidasComponent();
        }

        if (vidasComp == null && state.nameTag == null && state.scoreText == null) {
            return;
        }

        if (state.nameTagAttachment == null) {
            return;
        }

        ci.cancel();

        poseStack.pushPose();
        int offset = state.showExtraEars ? -10 : 0;
        float lineStep = 9.0f * 1.15f * 0.025f;

        // 1. Línea inferior: Indicador de vidas con corazones hardcore (directamente sobre la cabeza)
        if (vidasComp != null) {
            submitNodeCollector.submitNameTag(poseStack, state.nameTagAttachment, offset, vidasComp, !state.isDiscrete, state.lightCoords, camera);
            poseStack.translate(0.0f, lineStep, 0.0f);
        }

        // 2. Línea media: Nombre del jugador
        if (state.nameTag != null) {
            submitNodeCollector.submitNameTag(poseStack, state.nameTagAttachment, offset, state.nameTag, !state.isDiscrete, state.lightCoords, camera);
        }

        // 3. Línea superior: Puntuación de scoreboard si existe (ej. salud u objetivo belowName)
        if (state.scoreText != null) {
            poseStack.translate(0.0f, lineStep, 0.0f);
            submitNodeCollector.submitNameTag(poseStack, state.nameTagAttachment, offset, state.scoreText, !state.isDiscrete, state.lightCoords, camera);
        }

        poseStack.popPose();
    }
}
