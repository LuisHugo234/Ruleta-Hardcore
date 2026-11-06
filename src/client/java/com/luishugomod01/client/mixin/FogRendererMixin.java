package com.luishugomod01.client.mixin;

import com.luishugomod01.client.bloodmoon.BloodMoonClientState;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
        FogType fogType = this.getFogType(camera);
        if (fogType != FogType.ATMOSPHERIC && fogType != FogType.NONE) {
            return;
        }

        FogData fogData = cir.getReturnValue();
        LocalPlayer player = Minecraft.getInstance().player;

        // Cuevas Tenebrosas (Nivel 1): si el jugador está bajo Y=0 y lleva un farol en la mano secundaria,
        // el efecto de Oscuridad se reduce como si portara una fuente de luz
        if (BloodMoonClientState.isCuevasTenebrosasActive() && player != null && player.getY() < 0.0) {
            ItemStack offhand = player.getItemBySlot(EquipmentSlot.OFFHAND);
            if (offhand.is(Items.LANTERN) || offhand.is(Items.SOUL_LANTERN)) {
                fogData.environmentalStart = Math.max(fogData.environmentalStart, 20.0f);
                fogData.environmentalEnd = Math.max(fogData.environmentalEnd, 44.0f);
                fogData.renderDistanceStart = Math.max(fogData.renderDistanceStart, 20.0f);
                fogData.renderDistanceEnd = Math.max(fogData.renderDistanceEnd, 44.0f);
                fogData.skyEnd = Math.max(fogData.skyEnd, 44.0f);
                fogData.cloudEnd = Math.max(fogData.cloudEnd, 44.0f);
            }
        }

        if (!level.dimension().equals(Level.OVERWORLD)) {
            return;
        }

        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
        float renderDistBlocks = (float) (renderDistanceChunks * 16);

        // ¿Contaminación? (Nivel 4): Niebla verde tóxica sobre la capa Y=160
        if (BloodMoonClientState.isContaminacionActive() && player != null && player.getY() > 152.0) {
            float contamFactor = Mth.clamp((float) ((player.getY() - 152.0) / 10.0), 0.0f, 1.0f);
            float contamStart = Math.min(fogData.environmentalStart, 8.0f);
            float contamEnd = Math.min(fogData.environmentalEnd, 36.0f);
            fogData.environmentalStart = Mth.lerp(contamFactor, fogData.environmentalStart, contamStart);
            fogData.environmentalEnd = Mth.lerp(contamFactor, fogData.environmentalEnd, contamEnd);
            fogData.renderDistanceStart = Mth.lerp(contamFactor, fogData.renderDistanceStart, contamStart);
            fogData.renderDistanceEnd = Mth.lerp(contamFactor, fogData.renderDistanceEnd, contamEnd);

            fogData.color.x = Mth.lerp(contamFactor, fogData.color.x, 0.14f);
            fogData.color.y = Mth.lerp(contamFactor, fogData.color.y, 0.48f);
            fogData.color.z = Mth.lerp(contamFactor, fogData.color.z, 0.12f);
        }

        // Eclipse Solar (Nivel 6): acercar la niebla ligeramente al jugador y darle un tono gris carbón oscuro (#050505)
        float eclipseIntensity = BloodMoonClientState.getEclipseIntensity(partialTick);
        if (eclipseIntensity > 0.001f) {
            float targetStart = renderDistBlocks * 0.30f;
            float targetEnd = renderDistBlocks * 0.70f;
            fogData.environmentalStart = Mth.lerp(eclipseIntensity, fogData.environmentalStart, targetStart);
            fogData.environmentalEnd = Mth.lerp(eclipseIntensity, fogData.environmentalEnd, targetEnd);
            fogData.renderDistanceStart = Mth.lerp(eclipseIntensity, fogData.renderDistanceStart, targetStart);
            fogData.renderDistanceEnd = Mth.lerp(eclipseIntensity, fogData.renderDistanceEnd, targetEnd);

            float darkCoal = 5.0f / 255.0f;
            fogData.color.x = Mth.lerp(eclipseIntensity, fogData.color.x, darkCoal);
            fogData.color.y = Mth.lerp(eclipseIntensity, fogData.color.y, darkCoal);
            fogData.color.z = Mth.lerp(eclipseIntensity, fogData.color.z, darkCoal);
        }

        // Luna de Sangre (Nivel 6)
        float intensity = BloodMoonClientState.getIntensity(partialTick);
        if (intensity <= 0.001f) {
            return;
        }

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
