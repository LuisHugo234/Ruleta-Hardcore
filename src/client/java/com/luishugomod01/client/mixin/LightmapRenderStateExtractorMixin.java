package com.luishugomod01.client.mixin;

import com.luishugomod01.client.bloodmoon.BloodMoonClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import net.minecraft.client.renderer.state.LightmapRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

        // Cuevas Tenebrosas (Nivel 1): si el jugador está bajo Y=0 y porta un farol en la mano secundaria,
        // añadir un resplandor cálido en la luz ambiental como si llevara una fuente de luz
        LocalPlayer player = Minecraft.getInstance().player;
        if (BloodMoonClientState.isCuevasTenebrosasActive() && player != null && player.getY() < 0.0) {
            ItemStack offhand = player.getItemBySlot(EquipmentSlot.OFFHAND);
            boolean normalLantern = offhand.is(Items.LANTERN);
            boolean soulLantern = offhand.is(Items.SOUL_LANTERN);
            if ((normalLantern || soulLantern) && state.ambientColor != null) {
                float targetR = soulLantern ? 0.14f : 0.24f;
                float targetG = soulLantern ? 0.22f : 0.18f;
                float targetB = soulLantern ? 0.25f : 0.10f;
                state.ambientColor = new Vector3f(
                        Math.max(state.ambientColor.x(), targetR),
                        Math.max(state.ambientColor.y(), targetG),
                        Math.max(state.ambientColor.z(), targetB)
                );
            }
        }

        // Eclipse Solar (Nivel 6): reducir drásticamente el nivel de luz global del Overworld hacia una penumbra fría y lúgubre
        float eclipseIntensity = BloodMoonClientState.getEclipseIntensity(partialTick);
        if (eclipseIntensity > 0.001f) {
            if (state.skyLightColor != null) {
                float sr = Mth.lerp(eclipseIntensity, state.skyLightColor.x(), 0.07f);
                float sg = Mth.lerp(eclipseIntensity, state.skyLightColor.y(), 0.08f);
                float sb = Mth.lerp(eclipseIntensity, state.skyLightColor.z(), 0.13f);
                state.skyLightColor = new Vector3f(sr, sg, sb);
            }
            if (state.ambientColor != null && !(BloodMoonClientState.isCuevasTenebrosasActive() && player != null && player.getY() < 0.0)) {
                float ar = Mth.lerp(eclipseIntensity * 0.85f, state.ambientColor.x(), 0.02f);
                float ag = Mth.lerp(eclipseIntensity * 0.85f, state.ambientColor.y(), 0.02f);
                float ab = Mth.lerp(eclipseIntensity * 0.85f, state.ambientColor.z(), 0.04f);
                state.ambientColor = new Vector3f(ar, ag, ab);
            }
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
