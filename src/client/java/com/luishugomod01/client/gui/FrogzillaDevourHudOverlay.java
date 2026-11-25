package com.luishugomod01.client.gui;

import com.luishugomod01.client.frogzilla.FrogzillaClientState;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class FrogzillaDevourHudOverlay implements HudElement {

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker) {
        if (!FrogzillaClientState.isDevoured()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.font == null) {
            return;
        }

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();
        int pulseTicks = FrogzillaClientState.getPulseTicks();
        float pulse = 0.5F + 0.5F * Mth.sin(pulseTicks * 0.35F);

        // Viñeta oscura/viscosa orgánica (interior del estómago de Frogzilla)
        int edgeAlpha = (int) (175 + pulse * 45);
        int edgeColor = (edgeAlpha << 24) | 0x260A14;
        int midColor = (95 << 24) | 0x16070C;
        int bandH = Math.max(42, sh / 4);
        int bandW = Math.max(56, sw / 5);

        extractor.fillGradient(0, 0, sw, bandH, edgeColor, 0x0016070C);
        extractor.fillGradient(0, sh - bandH, sw, sh, 0x0016070C, edgeColor);
        extractor.fill(0, 0, bandW / 2, sh, midColor);
        extractor.fill(sw - bandW / 2, 0, sw, sh, midColor);
        extractor.fill(0, 0, sw, sh, (72 << 24) | 0x14080D);

        int pct = FrogzillaClientState.getProgressPct();

        int centerX = sw / 2;
        int barW = 210;
        int barH = 14;
        int barX = centerX - barW / 2;
        int barY = sh / 2 + 24;

        // Título llamativo
        Component titulo = Component.literal("¡PRESIONA ESPACIO PARA ESCAPAR!");
        int titleColor = (pulse > 0.55F) ? 0xFFFFE066 : 0xFFFF5555;
        extractor.centeredText(mc.font, titulo, centerX, barY - 22, titleColor);

        // Fondo y borde de barra de escape (solo barra limpia que se llena, sin porcentaje)
        extractor.fill(barX - 2, barY - 2, barX + barW + 2, barY + barH + 2, 0xE0100A0D);
        extractor.outline(barX - 2, barY - 2, barW + 4, barH + 4, 0xFFD9A836);

        int fillW = (int) ((barW * pct) / 100.0F);
        if (fillW > 0) {
            int barTop = pct >= 70 ? 0xFF66EE77 : 0xFFE8B438;
            int barBot = pct >= 70 ? 0xFF28A844 : 0xFFC23B2B;
            extractor.fillGradient(barX, barY, barX + fillW, barY + barH, barTop, barBot);
        }
    }
}
