package com.luishugomod01.client.gui;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.network.MuerteFantasmaPayload;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

import java.util.Random;

public class FantasmaMuerteHudOverlay implements HudElement {

    private static final Identifier TEXTURE_FANTASMA =
            Ruleta_Hardcore.id("textures/gui/fantasma_muerte.png");
    private static final Identifier TEXTURE_SILUETA =
            Ruleta_Hardcore.id("textures/gui/fantasma_muerte_silueta.png");

    private static final long DURACION_TOTAL_MS = 5500L;
    private static final long DURACION_FLASH_MS = 700L;
    private static final long INICIO_GLITCH_MS = 4200L;

    private static MuerteFantasmaPayload payloadActual = null;
    private static long startTime = 0L;
    private static boolean activa = false;
    private static boolean glitchSoundPlayed = false;

    public static void iniciarAnimacion(MuerteFantasmaPayload payload) {
        payloadActual = payload;
        startTime = System.currentTimeMillis();
        activa = true;
        glitchSoundPlayed = false;

        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.getSoundManager() != null) {
            mc.getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.70f)
            );
            mc.getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.VEX_CHARGE, 1.35f)
            );
            if (payload.esEliminado()) {
                mc.getSoundManager().play(
                        SimpleSoundInstance.forUI(SoundEvents.WITHER_DEATH, 0.85f)
                );
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker) {
        if (!activa || payloadActual == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }

        long now = System.currentTimeMillis();
        long elapsed = now - startTime;

        if (elapsed >= DURACION_TOTAL_MS) {
            activa = false;
            payloadActual = null;
            return;
        }

        int width = extractor.guiWidth();
        int height = extractor.guiHeight();
        int centerX = width / 2;
        int centerY = height / 2;

        if (elapsed >= INICIO_GLITCH_MS && !glitchSoundPlayed) {
            glitchSoundPlayed = true;
            if (mc.getSoundManager() != null) {
                mc.getSoundManager().play(
                        SimpleSoundInstance.forUI(SoundEvents.FIRE_EXTINGUISH, 1.4f)
                );
            }
        }

        if (elapsed < DURACION_FLASH_MS) {
            renderizarFaseImpacto(extractor, width, height, centerX, centerY, elapsed);
        } else if (elapsed < INICIO_GLITCH_MS) {
            renderizarFaseBurlas(extractor, mc, width, height, centerX, centerY, elapsed);
        } else {
            renderizarFaseGlitch(extractor, mc, width, height, centerX, centerY, elapsed);
        }
    }

    private void renderizarFaseImpacto(GuiGraphicsExtractor extractor, int width, int height,
                                       int centerX, int centerY, long elapsed) {
        float p = (float) elapsed / (float) DURACION_FLASH_MS;
        float flashAlpha = 1.0f - p;

        int alpha = (int) (225.0f * flashAlpha);
        extractor.fill(0, 0, width, height, (alpha << 24) | 0x880011);

        int beamAlpha = (int) (255.0f * flashAlpha);
        int beamColor = (beamAlpha << 24) | 0xFFFFFF;

        dibujarRayoJagged(extractor, 0, (int) (height * 0.15), width, (int) (height * 0.85), beamColor);
        dibujarRayoJagged(extractor, (int) (width * 0.85), 0, (int) (width * 0.15), height, beamColor);
        dibujarRayoJagged(extractor, (int) (width * 0.35), 0, (int) (width * 0.65), height, beamColor);

        float zoom = 1.0f + 1.6f * (float) Math.pow(1.0f - Math.min(1.0f, (float) elapsed / 350.0f), 2.5);

        int ghostW = 110;
        int ghostH = 108;

        extractor.pose().pushMatrix();
        extractor.pose().translate(centerX, centerY - 20);
        extractor.pose().scale(zoom, zoom);

        Identifier tex = (elapsed < 350L) ? TEXTURE_SILUETA : TEXTURE_FANTASMA;
        extractor.blit(tex, -ghostW / 2, -ghostH / 2, ghostW / 2, ghostH / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        extractor.pose().popMatrix();
    }

    private void renderizarFaseBurlas(GuiGraphicsExtractor extractor, Minecraft mc,
                                      int width, int height, int centerX, int centerY, long elapsed) {
        extractor.fill(0, 0, width, height, 0x44000000);

        long timeInPhase = elapsed - DURACION_FLASH_MS;
        float bobbing = (float) Math.sin(timeInPhase * 0.005) * 8.0f;
        float tilt = (float) Math.sin(timeInPhase * 0.007) * 4.0f;
        float pulse = 1.0f + 0.035f * (float) Math.sin(timeInPhase * 0.012);

        int ghostW = 110;
        int ghostH = 108;
        int ghostY = (int) (centerY - 30 + bobbing);

        extractor.pose().pushMatrix();
        extractor.pose().translate(centerX, ghostY);
        extractor.pose().scale(pulse, pulse);
        if (tilt != 0.0f) {
            extractor.pose().rotateAbout((float) Math.toRadians(tilt), 0, 0);
        }
        extractor.blit(TEXTURE_FANTASMA, -ghostW / 2, -ghostH / 2, ghostW / 2, ghostH / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        extractor.pose().popMatrix();

        renderizarBannerTexto(extractor, mc, centerX, centerY + 46, 1.0f, elapsed);
    }

    private void renderizarFaseGlitch(GuiGraphicsExtractor extractor, Minecraft mc,
                                      int width, int height, int centerX, int centerY, long elapsed) {
        long timeInGlitch = elapsed - INICIO_GLITCH_MS;
        long durGlitch = DURACION_TOTAL_MS - INICIO_GLITCH_MS;
        float p = (float) timeInGlitch / (float) durGlitch;
        float fadeAlpha = Math.max(0.0f, 1.0f - p);

        int ghostW = 110;
        int ghostH = 108;
        int ghostY = centerY - 30;

        int slices = 6;
        int sliceH = ghostH / slices;

        extractor.pose().pushMatrix();
        extractor.pose().translate(centerX, ghostY);

        for (int s = 0; s < slices; s++) {
            float v1 = (float) s / (float) slices;
            float v2 = (float) (s + 1) / (float) slices;
            float shift = (float) Math.sin((elapsed * 0.09) + s * 1.8) * 15.0f * (1.0f + p * 2.0f);

            int y1 = -ghostH / 2 + s * sliceH;
            int y2 = y1 + sliceH;
            int x1 = (int) (-ghostW / 2 + shift);
            int x2 = (int) (ghostW / 2 + shift);

            extractor.blit(TEXTURE_FANTASMA, x1, y1, x2, y2, 0.0f, 1.0f, v1, v2);
        }
        extractor.pose().popMatrix();

        Random rnd = new Random((elapsed / 35L));
        int particleCount = 28;
        for (int i = 0; i < particleCount; i++) {
            int px = (int) (centerX - 65 + rnd.nextFloat() * 130);
            int py = (int) (centerY - 85 + rnd.nextFloat() * 115 - p * 45.0f);
            int sz = 2 + rnd.nextInt(3);
            int pAlpha = (int) (255.0f * fadeAlpha);
            int pColor = (pAlpha << 24) | 0xFFFFFF;
            extractor.fill(px, py, px + sz, py + sz, pColor);
        }

        renderizarBannerTexto(extractor, mc, centerX, centerY + 46, fadeAlpha, elapsed);
    }

    private void renderizarBannerTexto(GuiGraphicsExtractor extractor, Minecraft mc,
                                       int centerX, int baseY, float alpha, long elapsed) {
        if (payloadActual == null || alpha <= 0.01f || elapsed < DURACION_FLASH_MS) {
            return;
        }

        long tiempoTexto = elapsed - DURACION_FLASH_MS;
        float progresoEntrada = Math.clamp((float) tiempoTexto / 450.0f, 0.0f, 1.0f);
        float eased = 1.0f - (float) Math.pow(1.0f - progresoEntrada, 3.0);
        float desplazamientoY = (1.0f - eased) * 35.0f;
        float inAlpha = Math.min(1.0f, progresoEntrada * 1.5f);

        boolean esMiMuerte = mc.player != null && mc.player.getScoreboardName().equalsIgnoreCase(payloadActual.nombreVictima());
        Component texto = esMiMuerte
                ? Component.literal("§c§lHas Muerto")
                : Component.literal("§c§l" + payloadActual.nombreVictima() + " Ha muerto");

        float glitchShiftX = 0.0f;
        if (elapsed >= INICIO_GLITCH_MS) {
            glitchShiftX = (float) Math.sin(elapsed * 0.12) * 5.0f * (1.0f - alpha);
        }

        int textWidth = mc.font.width(texto);
        float escala = 1.95f;
        float finalY = baseY + desplazamientoY;

        extractor.pose().pushMatrix();
        extractor.pose().translate(centerX + glitchShiftX, finalY);
        extractor.pose().scale(escala, escala);

        int colorAlpha = (int) (255.0f * alpha * inAlpha);
        int textColor = (colorAlpha << 24) | 0xFFFFFF;

        extractor.text(mc.font, texto, -textWidth / 2, -mc.font.lineHeight / 2, textColor, true);
        extractor.pose().popMatrix();
    }

    private void dibujarRayoJagged(GuiGraphicsExtractor extractor, int x1, int y1, int x2, int y2, int color) {
        int steps = 14;
        int prevX = x1;
        int prevY = y1;
        Random rnd = new Random((x1 * 31L + y1 * 17L));

        for (int i = 1; i <= steps; i++) {
            float frac = (float) i / (float) steps;
            int nextX = (int) (x1 + (x2 - x1) * frac);
            int nextY = (int) (y1 + (y2 - y1) * frac);

            if (i < steps) {
                nextX += (rnd.nextInt(17) - 8);
                nextY += (rnd.nextInt(17) - 8);
            }

            int minX = Math.min(prevX, nextX);
            int maxX = Math.max(prevX, nextX) + 2;
            int minY = Math.min(prevY, nextY);
            int maxY = Math.max(prevY, nextY) + 2;

            extractor.fill(minX, minY, maxX, maxY, color);

            prevX = nextX;
            prevY = nextY;
        }
    }
}
