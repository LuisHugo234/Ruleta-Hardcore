package com.luishugomod01.client.mixin;

import com.luishugomod01.client.bloodmoon.BloodMoonClientState;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapRenderStateExtractor.class)
public abstract class LightmapRenderStateExtractorMixin {

    @Inject(method = "extract", at = @At("TAIL"))
    private void ruleta$aplicarReflejoRojoLightmap(
            LightmapRenderState state,
            float partialTick,
            CallbackInfo ci
    ) {
        if (!state.needsUpdate) {
            return;
        }
        float intensity = BloodMoonClientState.getIntensity(partialTick);
        if (intensity <= 0.001f) {
            return;
        }

        // Bañar la luz del cielo nocturno con un reflejo rojo carmesí tenue sobre bloques, agua y entidades
        if (state.skyLightColor != null) {
            float skyBlend = intensity * 0.75f;
            float sr = Mth.lerp(skyBlend, state.skyLightColor.x(), 0.90f);
            float sg = Mth.lerp(skyBlend, state.skyLightColor.y(), 0.14f);
            float sb = Mth.lerp(skyBlend, state.skyLightColor.z(), 0.18f);
            state.skyLightColor = new Vector3f(sr, sg, sb);
        }

        // Ligero tinte carmesí en la luz ambiental mínima
        if (state.ambientColor != null) {
            float ambBlend = intensity * 0.55f;
            float ar = Mth.lerp(ambBlend, state.ambientColor.x(), 0.16f);
            float ag = Mth.lerp(ambBlend, state.ambientColor.y(), 0.02f);
            float ab = Mth.lerp(ambBlend, state.ambientColor.z(), 0.03f);
            state.ambientColor = new Vector3f(ar, ag, ab);
        }

        // Ligero matiz rojizo cálido en las fuentes de luz de bloques
        if (state.blockLightTint != null) {
            float blkBlend = intensity * 0.25f;
            float br = Mth.lerp(blkBlend, state.blockLightTint.x(), 1.0f);
            float bg = Mth.lerp(blkBlend, state.blockLightTint.y(), 0.80f);
            float bb = Mth.lerp(blkBlend, state.blockLightTint.z(), 0.80f);
            state.blockLightTint = new Vector3f(br, bg, bb);
        }
    }
}
