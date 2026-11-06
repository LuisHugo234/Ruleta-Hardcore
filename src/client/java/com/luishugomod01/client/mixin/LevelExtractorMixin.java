package com.luishugomod01.client.mixin;

import com.luishugomod01.client.bloodmoon.BloodMoonClientState;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelExtractor.class)
public abstract class LevelExtractorMixin {

    // Nubes gris ceniza oscuro con tinte carmesí: #3B0B0E (RGB: 59, 11, 14)
    @Unique
    private static final int CLOUD_TARGET_R = 59;
    @Unique
    private static final int CLOUD_TARGET_G = 11;
    @Unique
    private static final int CLOUD_TARGET_B = 14;

    @Shadow
    @Final
    private LevelRenderState levelRenderState;

    @Inject(method = "extract", at = @At("TAIL"))
    private void ruleta$tenirNubesLunaSangre(
            DeltaTracker deltaTracker,
            Camera camera,
            float partialTick,
            CallbackInfo ci
    ) {
        int current = this.levelRenderState.cloudColor;
        int alpha = ARGB.alpha(current);
        if (alpha <= 0) {
            return;
        }

        float eclipseIntensity = BloodMoonClientState.getEclipseIntensity(partialTick);
        if (eclipseIntensity > 0.001f) {
            int er = Math.round(Mth.lerp(eclipseIntensity, (float) ARGB.red(current), 12.0f));
            int eg = Math.round(Mth.lerp(eclipseIntensity, (float) ARGB.green(current), 12.0f));
            int eb = Math.round(Mth.lerp(eclipseIntensity, (float) ARGB.blue(current), 14.0f));
            current = ARGB.color(alpha, er, eg, eb);
            this.levelRenderState.cloudColor = current;
        }

        float intensity = BloodMoonClientState.getIntensity(partialTick);
        if (intensity <= 0.001f) {
            return;
        }

        int r = Math.round(Mth.lerp(intensity, (float) ARGB.red(current), (float) CLOUD_TARGET_R));
        int g = Math.round(Mth.lerp(intensity, (float) ARGB.green(current), (float) CLOUD_TARGET_G));
        int b = Math.round(Mth.lerp(intensity, (float) ARGB.blue(current), (float) CLOUD_TARGET_B));
        this.levelRenderState.cloudColor = ARGB.color(alpha, r, g, b);
    }
}
