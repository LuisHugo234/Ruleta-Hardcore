package com.luishugomod01.client.mixin;

import com.luishugomod01.client.bloodmoon.BloodMoonClientState;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    // Color de niebla carmesí oscuro: #4A0409 (RGB: 74, 4, 9)
    @Unique
    private static final float FOG_TARGET_R = 74.0f / 255.0f;
    @Unique
    private static final float FOG_TARGET_G = 4.0f / 255.0f;
    @Unique
    private static final float FOG_TARGET_B = 9.0f / 255.0f;

    @Shadow
    private FogType getFogType(Camera camera) {
        throw new AssertionError();
    }

    @Inject(method = "setupFog", at = @At("RETURN"))
    private void ruleta$aplicarNieblaDensaLunaSangre(
            Camera camera,
            int renderDistanceChunks,
            DeltaTracker deltaTracker,
            float darkenWorldAmount,
            ClientLevel level,
            CallbackInfoReturnable<FogData> cir
    ) {
        if (!level.dimension().equals(Level.OVERWORLD)) {
            return;
        }
        FogType fogType = this.getFogType(camera);
        if (fogType != FogType.ATMOSPHERIC && fogType != FogType.NONE) {
            return;
        }

        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        float intensity = BloodMoonClientState.getIntensity(partialTick);
        if (intensity <= 0.001f) {
            return;
        }

        FogData fogData = cir.getReturnValue();
        float renderDistBlocks = (float) (renderDistanceChunks * 16);

        // FogStart al 25% y FogEnd al 65% de la distancia de renderizado actual
        float targetFogStart = renderDistBlocks * 0.25f;
        float targetFogEnd = renderDistBlocks * 0.65f;

        fogData.environmentalStart = Mth.lerp(intensity, fogData.environmentalStart, targetFogStart);
        fogData.environmentalEnd = Mth.lerp(intensity, fogData.environmentalEnd, targetFogEnd);
        fogData.renderDistanceStart = Mth.lerp(intensity, fogData.renderDistanceStart, targetFogStart);
        fogData.renderDistanceEnd = Mth.lerp(intensity, fogData.renderDistanceEnd, targetFogEnd);

        // Interpolar el color de la niebla hacia rojo carmesí oscuro (#4A0409)
        fogData.color.x = Mth.lerp(intensity, fogData.color.x, FOG_TARGET_R);
        fogData.color.y = Mth.lerp(intensity, fogData.color.y, FOG_TARGET_G);
        fogData.color.z = Mth.lerp(intensity, fogData.color.z, FOG_TARGET_B);
    }
}
