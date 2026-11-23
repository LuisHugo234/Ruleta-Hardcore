package com.luishugomod01.client.gui;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.client.ritual.ReviveRitualClientManager;
import com.luishugomod01.network.MuerteFantasmaPayload;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

import java.util.Random;

public class FantasmaMuerteHudOverlay implements HudElement {

    private static final Identifier TEXTURE_SHEET =
            Ruleta_Hardcore.id("textures/gui/fantasma_muerte_sheet.png");
    private static final Identifier TEXTURE_SILUETA =
            Ruleta_Hardcore.id("textures/gui/fantasma_muerte_silueta.png");
    private static final Identifier TEXTURE_HUMO =
            Ruleta_Hardcore.id("textures/gui/humo_espectral.png");

    private static final Identifier SPRITE_HEART_CONTAINER =
            Identifier.withDefaultNamespace("hud/heart/container_hardcore");
    private static final Identifier SPRITE_HEART_FULL =
            Identifier.withDefaultNamespace("hud/heart/hardcore_full");

    private static final long DURACION_TOTAL_MS = 5100L;
    private static final long DURACION_FLASH_MS = 650L;
    private static final long INICIO_GLITCH_MS = 3900L;

    private static final int TOTAL_FRAMES = 24;
    private static final float BASE_FRAME_MS = 60.0f;

    /**
     * Tiempo de animación integrado con desaceleración suave: inicia enérgico tras el impacto
     * y reduce progresivamente la velocidad del ladeo y la risa hasta volverse lento y amenazante.
     */
    private static float calcularTiempoAnimacionMs(long elapsed) {
        long timeSinceBurlas = Math.max(0L, elapsed - DURACION_FLASH_MS);
        float durBurlas = (float) (INICIO_GLITCH_MS - DURACION_FLASH_MS);
        float t = Math.min((float) timeSinceBurlas, durBurlas);
        float u = Mth.clamp(t / durBurlas, 0.0f, 1.0f);
        float warpedBurlas = t * (1.0f - 0.37f * u);
        if (timeSinceBurlas <= durBurlas) {
            return warpedBurlas;
        }
        // En fase glitch continúa a la velocidad lenta final (0.26x) sin saltos
        return warpedBurlas + (timeSinceBurlas - durBurlas) * 0.26f;
    }

    private static int calcularFrameActual(long elapsed) {
        float animMs = calcularTiempoAnimacionMs(elapsed);
        return ((int) (animMs / BASE_FRAME_MS)) % TOTAL_FRAMES;
    }

    private static int calcularTamanoCabeza(int screenHeight) {
        int raw = Math.round(screenHeight * 0.63f);
        int clamped = Mth.clamp(raw, 192, 272);
        return (clamped / 8) * 8;
    }

    private static MuerteFantasmaPayload payloadActual = null;
    private static long startTime = 0L;
    private static boolean activa = false;
    private static boolean glitchSoundPlayed = false;

    public static void iniciarAnimacion(MuerteFantasmaPayload payload) {
        payloadActual = payload;
        startTime = System.currentTimeMillis();
        activa = true;
        glitchSoundPlayed = false;

        ReviveRitualClientManager.triggerShake(payload.esEliminado() ? 2.2f : 1.4f, 520L);

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
        boolean eliminado = payloadActual.esEliminado();

        if (elapsed >= INICIO_GLITCH_MS && !glitchSoundPlayed) {
            glitchSoundPlayed = true;
            if (mc.getSoundManager() != null) {
                mc.getSoundManager().play(
                        SimpleSoundInstance.forUI(SoundEvents.FIRE_EXTINGUISH, 1.4f)
                );
            }
        }

        if (elapsed < DURACION_FLASH_MS) {
            renderizarFaseImpacto(extractor, width, height, centerX, centerY, elapsed, eliminado);
        } else if (elapsed < INICIO_GLITCH_MS) {
            renderizarFaseBurlas(extractor, mc, width, height, centerX, centerY, elapsed, eliminado);
        } else {
            renderizarFaseGlitch(extractor, mc, width, height, centerX, centerY, elapsed, eliminado);
        }
    }

    private void renderizarFaseImpacto(GuiGraphicsExtractor extractor, int width, int height,
                                       int centerX, int centerY, long elapsed, boolean eliminado) {
        float p = (float) elapsed / (float) DURACION_FLASH_MS;
        float flashAlpha = 1.0f - p;
        float elimBoost = eliminado ? 1.25f : 1.0f;

        renderizarVineta(extractor, width, height, 0.65f + 0.35f * p, flashAlpha * 0.9f, eliminado);

        // Impact frame estilo anime/terror: destello breve negro vacío + carmesí sangre (0% morado)
        if (elapsed < 95L) {
            float cutA = 1.0f - (float) elapsed / 95.0f;
            int darkA = Math.min(235, Math.round(185.0f * cutA * elimBoost));
            extractor.fill(0, 0, width, height, (darkA << 24) | 0x090104);
        }
        int flashA = Math.min(210, Math.round(135.0f * flashAlpha * flashAlpha * elimBoost));
        if (flashA > 2) {
            extractor.fill(0, 0, width, height, (flashA << 24) | (eliminado ? 0xB80824 : 0x8F061C));
        }

        int skullSize = calcularTamanoCabeza(height);
        float scaleX;
        float scaleY;
        boolean esSilueta;
        if (elapsed < 320L) {
            float t = (float) elapsed / 320.0f;
            float zoom = 1.0f + 1.45f * (float) Math.pow(1.0f - t, 2.4);
            scaleX = zoom;
            scaleY = zoom;
            esSilueta = true;
        } else {
            float t = (float) (elapsed - 320L) / (float) (DURACION_FLASH_MS - 320L);
            float bounce = Mth.sin(t * Mth.PI) * (1.0f - t * 0.4f);
            scaleX = 1.0f + bounce * 0.14f;
            scaleY = 1.0f - bounce * 0.10f;
            esSilueta = false;
        }

        int ghostY = centerY - Math.round(skullSize * 0.06f);
        if (!esSilueta) {
            float revealT = Mth.clamp((float) (elapsed - 320L) / (float) (DURACION_FLASH_MS - 320L), 0.0f, 1.0f);
            renderizarHaloContraste(extractor, centerX, ghostY, skullSize, revealT, (1.0f - revealT) * 1.15f, eliminado);
            renderizarOndaExpansiva(extractor, centerX, ghostY, revealT, eliminado);
            dibujarFrameCalavera(extractor, 11, centerX, ghostY, skullSize, scaleX, scaleY, 0.0f, 0xFFFFFFFF);
        } else {
            // Líneas radiales de impacto en blanco hueso y carmesí detrás de la silueta haciendo slam
            float slamT = (float) elapsed / 320.0f;
            renderizarLineasImpacto(extractor, centerX, ghostY, width, height, 1.0f - slamT * 0.5f, eliminado);
            dibujarSilueta(extractor, centerX, ghostY, skullSize, scaleX, scaleY, 0xFFFFFFFF);
            dibujarOjosImpacto(extractor, centerX, ghostY, skullSize, scaleX, 0.85f + 0.15f * Mth.sin(slamT * Mth.PI));
        }
        renderizarFiltroVHS(extractor, width, height, elapsed, 0.75f + 0.25f * p);
    }

    private void renderizarFaseBurlas(GuiGraphicsExtractor extractor, Minecraft mc,
                                      int width, int height, int centerX, int centerY,
                                      long elapsed, boolean eliminado) {
        long timeInPhase = elapsed - DURACION_FLASH_MS;
        float phaseProgress = Mth.clamp((float) timeInPhase / (float) (INICIO_GLITCH_MS - DURACION_FLASH_MS), 0.0f, 1.0f);
        float animMs = calcularTiempoAnimacionMs(elapsed);
        int skullSize = calcularTamanoCabeza(height);
        int frameIdx = calcularFrameActual(elapsed);

        boolean vhsBurst = (elapsed >= 2100L && elapsed <= 2650L);
        boolean pulseFlash = (elapsed >= 1200L && elapsed <= 1520L) || (elapsed >= 3050L && elapsed <= 3450L);
        float pulse = 0.5f + 0.5f * Mth.sin(timeInPhase * 0.0045f);

        // Coreografía de risa y flotación en arco sincronizada con el giro 3D (inicia enérgica y desacelera)
        float decay = 1.0f - 0.58f * phaseProgress;
        float frameContinuous = (animMs / BASE_FRAME_MS) % TOTAL_FRAMES;
        float swayAngle = frameContinuous * (2.0f * Mth.PI / (float) TOTAL_FRAMES);
        float swaySin = Mth.sin(swayAngle);

        float laughWave = Math.abs(Mth.sin(animMs * 0.0135f));
        float laughSnap = laughWave * laughWave;
        float laughBounce = -laughSnap * (10.5f * decay);
        float arcX = swaySin * (14.0f * decay);
        float arcY = Mth.cos(swayAngle * 2.0f) * (4.5f * decay);
        float tiltDeg = swaySin * (9.0f * decay);

        int baseY = centerY - Math.round(skullSize * 0.06f);
        // Viñeta carmesí latiendo al ritmo de cada carcajada + cenizas/brasas por toda la pantalla
        float vignettePulse = pulse * 0.35f + laughSnap * 0.65f * decay + (vhsBurst ? 0.30f : 0.0f);
        renderizarVineta(extractor, width, height, 1.0f, vignettePulse, eliminado);
        renderizarCenizasYBrasas(extractor, width, height, elapsed, 1.0f, eliminado);

        float laughShakeX = Mth.sin(elapsed * 0.085f) * (2.2f * (1.0f - phaseProgress)) * laughSnap;
        float jitterX = vhsBurst ? Mth.sin(elapsed * 0.19f) * 6.5f : (pulseFlash ? Mth.sin(elapsed * 0.14f) * 2.0f : laughShakeX);

        float breathe = 1.0f + Mth.sin(timeInPhase * 0.0042f) * 0.022f;
        float scaleX = breathe * (1.0f + laughSnap * 0.055f * decay);
        float scaleY = breathe * (1.0f - laughSnap * 0.028f * decay + (1.0f - laughWave) * 0.022f * decay);

        int ghostCx = Math.round(centerX + arcX + jitterX);
        int ghostCy = Math.round(baseY + arcY + laughBounce);

        // Aura carmesí detrás de la calavera latiendo en sincronía exacta con cada carcajada
        renderizarHaloContraste(extractor, ghostCx, ghostCy, skullSize, 1.0f, pulse * 0.45f + laughSnap * 0.95f * decay, eliminado);

        if (vhsBurst) {
            int slices = 8;
            int sliceH = skullSize / slices;
            int sheetH = skullSize * TOTAL_FRAMES;
            float baseV = frameIdx * (float) skullSize;
            extractor.pose().pushMatrix();
            extractor.pose().translate(ghostCx, ghostCy);
            extractor.pose().rotateAbout((float) Math.toRadians(tiltDeg), 0, 0);
            extractor.pose().scale(scaleX, scaleY);
            for (int s = 0; s < slices; s++) {
                float uY = baseV + s * sliceH;
                float shift = Mth.sin(elapsed * 0.14f + s * 2.3f) * 12.0f;
                int drawY = -skullSize / 2 + s * sliceH;
                int drawX = Math.round(-skullSize / 2.0f + shift);
                extractor.blit(RenderPipelines.GUI_TEXTURED, TEXTURE_SHEET, drawX - 5, drawY, 0.0f, uY, skullSize, sliceH, skullSize, sheetH, 0x8CEFE4E6);
                extractor.blit(RenderPipelines.GUI_TEXTURED, TEXTURE_SHEET, drawX + 5, drawY, 0.0f, uY, skullSize, sliceH, skullSize, sheetH, 0xB8FF1A38);
                extractor.blit(RenderPipelines.GUI_TEXTURED, TEXTURE_SHEET, drawX, drawY, 0.0f, uY, skullSize, sliceH, skullSize, sheetH, 0xFFFFFFFF);
            }
            extractor.pose().popMatrix();
        } else {
            int chromaOff = Math.round(2.5f + laughSnap * 2.2f * decay);
            dibujarFrameCalavera(extractor, frameIdx, ghostCx - chromaOff, ghostCy, skullSize, scaleX * 1.02f, scaleY * 1.02f, tiltDeg, 0x38EFE4E6);
            dibujarFrameCalavera(extractor, frameIdx, ghostCx + chromaOff, ghostCy, skullSize, scaleX * 1.02f, scaleY * 1.02f, tiltDeg, 0x62FF1A36);
            int tintPrincipal = pulseFlash ? 0xFFFFDCE2 : 0xFFFFFFFF;
            dibujarFrameCalavera(extractor, frameIdx, ghostCx, ghostCy, skullSize, scaleX, scaleY, tiltDeg, tintPrincipal);
        }

        // Humo espectral detallado saliendo directamente desde las cuencas de ambos ojos (rotando con el ladeo)
        renderizarHumoOjos(extractor, ghostCx, ghostCy, skullSize, frameIdx, tiltDeg, elapsed, 0.88f + 0.24f * laughSnap);

        renderizarFiltroVHS(extractor, width, height, elapsed, vhsBurst ? 1.0f : 0.72f);
        int textY = Math.min(height - 32, baseY + Math.round(skullSize * 0.49f));
        renderizarBannerTextoYCorazones(extractor, mc, centerX, textY, 1.0f, elapsed, eliminado);
    }

    private void renderizarFaseGlitch(GuiGraphicsExtractor extractor, Minecraft mc,
                                      int width, int height, int centerX, int centerY,
                                      long elapsed, boolean eliminado) {
        long timeInGlitch = elapsed - INICIO_GLITCH_MS;
        long durGlitch = DURACION_TOTAL_MS - INICIO_GLITCH_MS;
        float p = Mth.clamp((float) timeInGlitch / (float) durGlitch, 0.0f, 1.0f);
        float fadeAlpha = (1.0f - p) * (1.0f - p * 0.35f);

        renderizarVineta(extractor, width, height, fadeAlpha, 0.0f, eliminado);
        renderizarCenizasYBrasas(extractor, width, height, elapsed, fadeAlpha, eliminado);

        int skullSize = calcularTamanoCabeza(height);
        int baseY = centerY - Math.round(skullSize * 0.06f);
        int ghostY = Math.round(baseY - p * 12.0f);
        renderizarHaloContraste(extractor, centerX, ghostY, skullSize, fadeAlpha, 0.0f, eliminado);
        int slices = 8;
        int sliceH = skullSize / slices;
        int sheetH = skullSize * TOTAL_FRAMES;
        int frameIdx = calcularFrameActual(elapsed);
        float baseV = frameIdx * (float) skullSize;
        int mainAlpha = Math.round(255.0f * fadeAlpha);
        int chromaAlpha = Math.round(145.0f * fadeAlpha);

        if (mainAlpha > 2) {
            extractor.pose().pushMatrix();
            extractor.pose().translate(centerX, ghostY);

            for (int s = 0; s < slices; s++) {
                float uY = baseV + s * sliceH;
                float shift = Mth.sin((elapsed * 0.095f) + s * 1.9f) * 14.0f * (0.5f + p * 2.2f);
                int drawY = -skullSize / 2 + s * sliceH;
                int drawX = Math.round(-skullSize / 2.0f + shift);
                int chromaOff = Math.max(2, Math.round(3.0f + p * 7.0f));

                if (chromaAlpha > 2) {
                    extractor.blit(
                            RenderPipelines.GUI_TEXTURED,
                            TEXTURE_SHEET,
                            drawX - chromaOff, drawY,
                            0.0f, uY,
                            skullSize, sliceH,
                            skullSize, sheetH,
                            (chromaAlpha << 24) | 0xEFE4E6
                    );
                    extractor.blit(
                            RenderPipelines.GUI_TEXTURED,
                            TEXTURE_SHEET,
                            drawX + chromaOff, drawY,
                            0.0f, uY,
                            skullSize, sliceH,
                            skullSize, sheetH,
                            (chromaAlpha << 24) | 0xFF1E3C
                    );
                }

                extractor.blit(
                        RenderPipelines.GUI_TEXTURED,
                        TEXTURE_SHEET,
                        drawX, drawY,
                        0.0f, uY,
                        skullSize, sliceH,
                        skullSize, sheetH,
                        (mainAlpha << 24) | 0xFFFFFF
                );
            }
            extractor.pose().popMatrix();
        }

        Random rnd = new Random(elapsed / 35L);
        int particleCount = 30;
        int pRgb = eliminado ? 0xFF6677 : 0xFF334C;
        for (int i = 0; i < particleCount; i++) {
            int px = (int) (centerX - skullSize * 0.45f + rnd.nextFloat() * skullSize * 0.9f);
            int py = (int) (centerY - skullSize * 0.5f + rnd.nextFloat() * skullSize * 0.8f - p * 50.0f);
            int sz = 2 + rnd.nextInt(3);
            int pAlpha = Math.round(235.0f * fadeAlpha);
            if (pAlpha > 2) {
                extractor.fill(px, py, px + sz, py + sz, (pAlpha << 24) | (i % 3 == 0 ? 0xFFFFFF : pRgb));
            }
        }

        renderizarFiltroVHS(extractor, width, height, elapsed, fadeAlpha * 0.85f);
        int textY = Math.min(height - 32, baseY + Math.round(skullSize * 0.49f));
        renderizarBannerTextoYCorazones(extractor, mc, centerX, textY, fadeAlpha, elapsed, eliminado);
    }

    private void renderizarHumoOjos(GuiGraphicsExtractor extractor, int cx, int cy, int size,
                                    int frameIdx, float tiltDeg, long elapsed, float intensity) {
        // Seguimiento del giro 3D horizontal y del ladeo 2D para mantener el humo anclado exactamente a las cuencas
        float yawPhase = Mth.sin(frameIdx * (2.0f * Mth.PI / (float) TOTAL_FRAMES));
        float yawOffX = yawPhase * size * 0.032f;
        float rad = (float) Math.toRadians(tiltDeg);
        float cosT = Mth.cos(rad);
        float sinT = Mth.sin(rad);

        // Coordenadas locales exactas de las cuencas oculares respecto al centro del sprite
        float localEyeY = size * 0.18f;
        float localEyeOffX = size * 0.14f;

        for (int side = -1; side <= 1; side += 2) {
            float lx = yawOffX + side * localEyeOffX;
            float ly = localEyeY + yawPhase * side * size * 0.012f;
            int originX = Math.round(cx + lx * cosT - ly * sinT);
            int originY = Math.round(cy + lx * sinT + ly * cosT);

            // Capa 1: Sombra/volumen oscuro de vacío detrás del humo carmesí (da profundidad y contraste contra cielos/fondos claros)
            int darkPuffs = 9;
            for (int i = 0; i < darkPuffs; i++) {
                float cycle = ((elapsed * 0.00095f) + i / (float) darkPuffs + (side < 0 ? 0.43f : 0.0f)) % 1.0f;
                float fadeIn = Mth.clamp(cycle / 0.16f, 0.0f, 1.0f);
                float fadeOut = (1.0f - cycle) * (1.0f - cycle * 0.25f);
                int a = Math.round(165.0f * fadeIn * fadeOut * intensity);
                if (a <= 4) continue;
                float dx = side * (size * 0.02f + cycle * (size * 0.46f));
                float dy = -cycle * (size * 0.08f) + Mth.sin(cycle * 5.0f + i * 1.6f) * (size * 0.040f) + size * 0.012f;
                int puffSize = Math.round(size * (0.10f + cycle * 0.25f));
                float rotDeg = side * (cycle * 75.0f + i * 31.0f);
                int rgb = (i % 2 == 0) ? 0x22030A : 0x4A0616;
                dibujarNubeHumo(extractor, Math.round(originX + dx), Math.round(originY + dy), puffSize, rotDeg, (a << 24) | rgb);
            }

            // Capa 2: Volutas densas de humo carmesí intenso fluyendo en espiral desde el interior de la cuenca
            int eyePuffs = 14;
            for (int i = 0; i < eyePuffs; i++) {
                float cycle = ((elapsed * 0.00112f) + i / (float) eyePuffs + (side < 0 ? 0.37f : 0.0f)) % 1.0f;
                float fadeIn = Mth.clamp(cycle / 0.12f, 0.0f, 1.0f);
                float fadeOut = (1.0f - cycle) * (1.0f - cycle * 0.18f);
                int a = Math.round(235.0f * fadeIn * fadeOut * intensity);
                if (a <= 4) continue;
                float dx = side * (cycle * (size * 0.48f));
                float wave = Mth.sin(cycle * 5.8f + i * 1.25f) * (size * 0.036f);
                float dy = -cycle * (size * 0.09f) + wave;
                int puffSize = Math.round(size * (0.075f + cycle * 0.23f));
                float rotDeg = side * (cycle * 85.0f + i * 27.0f);
                int rgb = (i % 3 == 0) ? 0xFF1A3C : ((i % 2 == 0) ? 0xE01234 : 0xA80B24);
                dibujarNubeHumo(extractor, Math.round(originX + dx), Math.round(originY + dy), puffSize, rotDeg, (a << 24) | rgb);
            }

            // Capa 3: Núcleo incandescente (energía rosada-blanca brotando directamente del ojo brillante)
            int corePuffs = 8;
            for (int i = 0; i < corePuffs; i++) {
                float cycle = ((elapsed * 0.00140f) + i / (float) corePuffs + (side < 0 ? 0.21f : 0.0f)) % 1.0f;
                float fadeIn = Mth.clamp(cycle / 0.10f, 0.0f, 1.0f);
                float fadeOut = (1.0f - cycle) * (1.0f - cycle);
                int a = Math.round(220.0f * fadeIn * fadeOut * intensity);
                if (a <= 4) continue;
                float dx = side * (cycle * (size * 0.26f));
                float dy = -cycle * (size * 0.045f) + Mth.sin(cycle * 6.4f + i * 1.7f) * (size * 0.018f);
                int puffSize = Math.round(size * (0.055f + cycle * 0.12f));
                float rotDeg = -side * (cycle * 60.0f + i * 37.0f);
                int rgb = (cycle < 0.32f) ? 0xFFD0DC : 0xFF4D72;
                dibujarNubeHumo(extractor, Math.round(originX + dx), Math.round(originY + dy), puffSize, rotDeg, (a << 24) | rgb);
            }

            // Capa 4: Chispas y brasas pixeladas blancas/carmesí desprendiéndose del chorro de humo
            int sparks = 10;
            for (int i = 0; i < sparks; i++) {
                float cycle = ((elapsed * 0.00128f) + i / (float) sparks + (side < 0 ? 0.5f : 0.0f)) % 1.0f;
                float env = Mth.sin(cycle * Mth.PI);
                int a = Math.round(245.0f * env * intensity);
                if (a <= 5) continue;
                float sx = originX + side * (size * 0.06f + cycle * size * 0.44f);
                float sy = originY + Mth.sin(cycle * 4.5f + i * 2.1f) * (size * 0.13f) - cycle * (size * 0.09f);
                int sz = (i % 3 == 0) ? 3 : 2;
                int rgb = (i % 3 == 0) ? 0xFFFFFF : ((i % 2 == 0) ? 0xFF708D : 0xFF2244);
                extractor.fill(Math.round(sx), Math.round(sy), Math.round(sx) + sz, Math.round(sy) + sz, (a << 24) | rgb);
            }
        }
    }

    private void dibujarNubeHumo(GuiGraphicsExtractor extractor, int cx, int cy, int size,
                                 float angleDeg, int tintArgb) {
        extractor.pose().pushMatrix();
        extractor.pose().translate(cx, cy);
        extractor.pose().rotateAbout((float) Math.toRadians(angleDeg), 0, 0);
        extractor.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE_HUMO,
                -size / 2, -size / 2,
                0.0f, 0.0f,
                size, size,
                size, size,
                tintArgb
        );
        extractor.pose().popMatrix();
    }

    private void renderizarFiltroVHS(GuiGraphicsExtractor extractor, int width, int height,
                                     long elapsed, float intensity) {
        float clamped = Mth.clamp(intensity, 0.0f, 1.0f);
        if (clamped <= 0.02f) {
            return;
        }
        int veilA = Math.round(46.0f * clamped);
        if (veilA > 1) {
            extractor.fill(0, 0, width, height, (veilA << 24) | 0x20030A);
        }
        int scanA = Math.round(34.0f * clamped);
        if (scanA > 1) {
            int scanCol = (scanA << 24) | 0x050206;
            for (int y = 0; y < height; y += 3) {
                extractor.fill(0, y, width, y + 1, scanCol);
            }
        }
        int bandY1 = (int) ((elapsed * 0.14f) % Math.max(1, height));
        int bandY2 = (int) ((elapsed * 0.09f + height * 0.45f) % Math.max(1, height));
        int trackA = Math.round(28.0f * clamped);
        if (trackA > 1) {
            extractor.fill(0, bandY1, width, Math.min(height, bandY1 + 4), (trackA << 24) | 0xFF2240);
            extractor.fill(0, bandY2, width, Math.min(height, bandY2 + 2), (trackA << 24) | 0xEFE4E6);
        }
    }

    private void dibujarFrameCalavera(GuiGraphicsExtractor extractor, int frameIdx,
                                      int cx, int cy, int size, float scaleX, float scaleY,
                                      float angleDeg, int tintArgb) {
        extractor.pose().pushMatrix();
        extractor.pose().translate(cx, cy);
        if (angleDeg != 0.0f) {
            extractor.pose().rotateAbout((float) Math.toRadians(angleDeg), 0, 0);
        }
        extractor.pose().scale(scaleX, scaleY);
        extractor.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE_SHEET,
                -size / 2, -size / 2,
                0.0f, frameIdx * (float) size,
                size, size,
                size, size * TOTAL_FRAMES,
                tintArgb
        );
        extractor.pose().popMatrix();
    }

    private void dibujarSilueta(GuiGraphicsExtractor extractor, int cx, int cy, int size,
                                float scaleX, float scaleY, int tintArgb) {
        extractor.pose().pushMatrix();
        extractor.pose().translate(cx, cy);
        extractor.pose().scale(scaleX, scaleY);
        extractor.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE_SILUETA,
                -size / 2, -size / 2,
                0.0f, 0.0f,
                size, size,
                size, size,
                tintArgb
        );
        extractor.pose().popMatrix();
    }

    private void dibujarOjosImpacto(GuiGraphicsExtractor extractor, int cx, int cy, int size,
                                    float scale, float intensity) {
        int eyeOffX = Math.round(size * 0.14f * scale);
        int eyeY = cy + Math.round(size * 0.18f * scale);
        int haloW = Math.max(5, Math.round(size * 0.055f * scale));
        int haloH = Math.max(3, Math.round(size * 0.026f * scale));
        int coreW = Math.max(3, Math.round(haloW * 0.52f));
        int coreH = Math.max(2, Math.round(haloH * 0.52f));
        int haloA = Math.min(255, Math.round(235.0f * intensity));
        int coreA = Math.min(255, Math.round(255.0f * intensity));

        for (int side = -1; side <= 1; side += 2) {
            int ex = cx + side * eyeOffX;
            extractor.fill(ex - haloW - 3, eyeY - haloH - 2, ex + haloW + 3, eyeY + haloH + 2, ((haloA / 2) << 24) | 0xB80A26);
            extractor.fill(ex - haloW, eyeY - haloH, ex + haloW, eyeY + haloH, (haloA << 24) | 0xFF1A3C);
            extractor.fill(ex - coreW, eyeY - coreH, ex + coreW, eyeY + coreH, (coreA << 24) | 0xFFF5F7);
        }
    }

    private void renderizarLineasImpacto(GuiGraphicsExtractor extractor, int cx, int cy,
                                         int width, int height, float alpha, boolean eliminado) {
        if (alpha <= 0.03f) {
            return;
        }
        int rays = 12;
        int outerR = Math.max(width, height) / 2 + 24;
        int innerR = Math.min(width, height) / 5;
        int colRed = (Math.round((eliminado ? 170.0f : 135.0f) * alpha) << 24) | 0xFF1E3E;
        int colWhite = (Math.round(115.0f * alpha) << 24) | 0xF5ECEE;
        for (int i = 0; i < rays; i++) {
            float ang = (float) (i * Math.PI * 2.0 / rays) + 0.18f;
            int x0 = Math.round(cx + Mth.cos(ang) * innerR);
            int y0 = Math.round(cy + Mth.sin(ang) * innerR);
            int x1 = Math.round(cx + Mth.cos(ang) * outerR);
            int y1 = Math.round(cy + Mth.sin(ang) * outerR);
            dibujarSegmentoLinea(extractor, x0, y0, x1, y1, (i % 3 == 0) ? colWhite : colRed, (i % 2 == 0) ? 2 : 1);
        }
    }

    private void renderizarVineta(GuiGraphicsExtractor extractor, int width, int height,
                                  float alpha, float pulse, boolean eliminado) {
        // Siempre paleta carmesí oscuro (más profundo e intenso en eliminación definitiva)
        int rgb = eliminado ? 0x4A0310 : 0x34040D;
        float baseEdge = eliminado ? 148.0f : 118.0f;
        float pulseEdge = eliminado ? 52.0f : 42.0f;
        int edgeA = Math.min(235, Math.round((baseEdge + pulseEdge * pulse) * Mth.clamp(alpha, 0.0f, 1.0f)));
        if (edgeA <= 2) {
            return;
        }
        int edgeCol = (edgeA << 24) | rgb;
        int clearCol = rgb;
        int bandV = Math.max(32, height / 4);
        int bandH = Math.max(28, width / 8);

        extractor.fillGradient(0, 0, width, bandV, edgeCol, clearCol);
        extractor.fillGradient(0, height - bandV, width, height, clearCol, edgeCol);

        int steps = 5;
        for (int i = 0; i < steps; i++) {
            float f = 1.0f - (float) i / (float) steps;
            int sa = Math.round(edgeA * 0.64f * f * f);
            if (sa <= 2) continue;
            int col = (sa << 24) | rgb;
            int x0 = (bandH * i) / steps;
            int x1 = (bandH * (i + 1)) / steps;
            extractor.fill(x0, 0, x1, height, col);
            extractor.fill(width - x1, 0, width - x0, height, col);
        }
    }

    private void renderizarCenizasYBrasas(GuiGraphicsExtractor extractor, int width, int height,
                                          long elapsed, float alpha, boolean eliminado) {
        int count = eliminado ? 44 : 28;
        for (int i = 0; i < count; i++) {
            float speed = 0.00024f + (i % 6) * 0.00008f;
            float phase = ((elapsed * speed) + (i * 0.173f)) % 1.0f;
            float xNorm = ((i * 0.6180339f) % 1.0f);
            float drift = Mth.sin(elapsed * 0.0022f + i * 1.7f) * (12.0f + (i % 4) * 5.0f);
            int px = Math.round(xNorm * width + drift);
            int py = Math.round((1.05f - phase * 1.15f) * height);
            float env = Mth.sin(phase * Mth.PI);
            int a = Math.round((eliminado ? 225.0f : 190.0f) * env * alpha);
            if (a <= 4) continue;
            int sz = (i % 5 == 0) ? 3 : 2;
            int rgb;
            if (i % 5 == 0) {
                rgb = 0xFFF4F6; // Chispa blanco hueso
            } else if (i % 3 == 0) {
                rgb = 0xB8AEB2; // Ceniza gris claro
            } else if (i % 2 == 0) {
                rgb = 0xFF2848; // Brasa rojo vivo
            } else {
                rgb = 0xC8102E; // Brasa carmesí profundo
            }
            extractor.fill(px, py, px + sz, py + sz, (a << 24) | rgb);
        }
    }

    private void renderizarBannerTextoYCorazones(GuiGraphicsExtractor extractor, Minecraft mc,
                                                 int centerX, int baseY, float alpha,
                                                 long elapsed, boolean eliminado) {
        if (payloadActual == null || alpha <= 0.01f || elapsed < DURACION_FLASH_MS) {
            return;
        }

        long tiempoTexto = elapsed - DURACION_FLASH_MS;
        float progresoEntrada = Mth.clamp((float) tiempoTexto / 420.0f, 0.0f, 1.0f);
        float eased = 1.0f - (float) Math.pow(1.0f - progresoEntrada, 3.0);
        float desplazamientoY = (1.0f - eased) * 28.0f;
        float inAlpha = Math.min(1.0f, progresoEntrada * 1.6f);

        boolean esMiMuerte = mc.player != null
                && mc.player.getScoreboardName().equalsIgnoreCase(payloadActual.nombreVictima());
        String prefijo = eliminado ? "§4§l☠ " : "§c§l";
        String textoCompleto;
        if (eliminado) {
            textoCompleto = esMiMuerte
                    ? "Has sido eliminado"
                    : payloadActual.nombreVictima() + " Ha sido eliminado";
        } else {
            textoCompleto = esMiMuerte
                    ? "Has Muerto"
                    : payloadActual.nombreVictima() + " Ha muerto";
        }
        int charsVisibles = Math.min(textoCompleto.length(), Math.max(1, (int) (tiempoTexto / 28L) + 1));
        Component textoFull = Component.literal(prefijo + textoCompleto);
        Component textoMostrado = Component.literal(prefijo + textoCompleto.substring(0, charsVisibles));

        float glitchShiftX = 0.0f;
        if (elapsed >= INICIO_GLITCH_MS) {
            glitchShiftX = Mth.sin(elapsed * 0.13f) * 5.0f * (1.0f - alpha);
        }

        int textWidth = mc.font.width(textoFull);
        float escala = 1.85f + (1.0f - eased) * 0.25f;
        float finalY = baseY + desplazamientoY;

        extractor.pose().pushMatrix();
        extractor.pose().translate(centerX + glitchShiftX, finalY);
        extractor.pose().scale(escala, escala);

        int colorAlpha = Math.round(255.0f * alpha * inAlpha);
        int textColor = (colorAlpha << 24) | 0xFFFFFF;
        extractor.text(mc.font, textoMostrado, -textWidth / 2, -mc.font.lineHeight / 2, textColor, true);
        extractor.pose().popMatrix();

        int restantes = Math.max(0, payloadActual.vidasRestantes());
        int maximas = Math.max(1, payloadActual.vidasMaximas());
        if (maximas <= 10 && tiempoTexto >= 140L && alpha > 0.25f) {
            int heartSz = 9;
            int gap = 2;
            int totalW = maximas * (heartSz + gap) - gap;
            int hxStart = centerX - totalW / 2;
            int hy = Math.round(finalY + 18);
            long heartElapsed = tiempoTexto - 140L;

            for (int i = 0; i < maximas; i++) {
                int hx = hxStart + i * (heartSz + gap);
                boolean esPerdido = (i == restantes);
                if (esPerdido && heartElapsed < 1300L) {
                    float bt = (float) heartElapsed / 1300.0f;
                    int sx = Math.round(Mth.sin(heartElapsed * 0.07f) * 2.0f * (1.0f - bt));
                    extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_HEART_CONTAINER, hx + sx, hy, heartSz, heartSz);
                    if ((heartElapsed / 85L) % 2L == 0L && bt < 0.72f) {
                        extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_HEART_FULL, hx + sx, hy, heartSz, heartSz);
                    }
                } else {
                    extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_HEART_CONTAINER, hx, hy, heartSz, heartSz);
                    if (i < restantes) {
                        extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_HEART_FULL, hx, hy, heartSz, heartSz);
                    }
                }
            }
        }
    }

    private void renderizarHaloContraste(GuiGraphicsExtractor extractor, int cx, int cy, int skullSize,
                                         float alpha, float pulse, boolean eliminado) {
        if (alpha <= 0.02f) {
            return;
        }
        int baseRgb = 0x22030A;
        int glowRgb = eliminado ? 0xD81438 : 0xB81232;
        int layers = 6;
        int maxR = Math.round(skullSize * 0.44f + pulse * 22.0f);
        float intensityBoost = eliminado ? 1.18f : 1.0f;
        for (int i = layers; i >= 1; i--) {
            float frac = (float) i / (float) layers;
            int r = Math.round(maxR * frac);
            int a = Math.min(200, Math.round((24.0f + (1.0f - frac) * (30.0f + pulse * 18.0f)) * alpha * intensityBoost));
            if (a <= 2) continue;
            int rgb = (i <= 2) ? glowRgb : baseRgb;
            dibujarDiscoSuave(extractor, cx, cy, r, (a << 24) | rgb);
        }
    }

    private void renderizarOndaExpansiva(GuiGraphicsExtractor extractor, int cx, int cy,
                                         float t, boolean eliminado) {
        float fade = (1.0f - t) * (1.0f - t);
        if (fade <= 0.02f) {
            return;
        }
        float ease = 1.0f - (1.0f - t) * (1.0f - t) * (1.0f - t);
        int ringR = Math.round(34.0f + ease * (eliminado ? 105.0f : 90.0f));
        int ringAlpha = Math.round(220.0f * fade);
        int coreCol = (ringAlpha << 24) | 0xFFF4F6;
        int glowCol = (Math.round(165.0f * fade) << 24) | 0xFF1E3E;

        int segments = 28;
        for (int i = 0; i < segments; i++) {
            float ang = (float) (i * Math.PI * 2.0 / segments);
            int px = Math.round(cx + Mth.cos(ang) * ringR);
            int py = Math.round(cy + Mth.sin(ang) * ringR);
            extractor.fill(px - 2, py - 2, px + 3, py + 3, glowCol);
            extractor.fill(px - 1, py - 1, px + 2, py + 2, coreCol);
        }

        int rays = 8;
        for (int i = 0; i < rays; i++) {
            float ang = (float) (i * Math.PI * 2.0 / rays) + 0.25f;
            int r0 = Math.round(28.0f + ease * 36.0f);
            int r1 = Math.round(48.0f + ease * 72.0f);
            int x0 = Math.round(cx + Mth.cos(ang) * r0);
            int y0 = Math.round(cy + Mth.sin(ang) * r0);
            int x1 = Math.round(cx + Mth.cos(ang) * r1);
            int y1 = Math.round(cy + Mth.sin(ang) * r1);
            dibujarSegmentoLinea(extractor, x0, y0, x1, y1, glowCol, 2);
            dibujarSegmentoLinea(extractor, x0, y0, x1, y1, coreCol, 1);
        }
    }

    private void dibujarDiscoSuave(GuiGraphicsExtractor extractor, int cx, int cy, int r, int color) {
        int step = 3;
        for (int dy = -r; dy <= r; dy += step) {
            int halfW = (int) Math.sqrt(Math.max(0, r * r - dy * dy));
            if (halfW > 0) {
                extractor.fill(cx - halfW, cy + dy, cx + halfW, cy + dy + step, color);
            }
        }
    }

    private void dibujarSegmentoLinea(GuiGraphicsExtractor extractor, int x0, int y0, int x1, int y1,
                                      int color, int radius) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int dist = Math.max(Math.abs(dx), Math.abs(dy));
        int samples = Math.max(1, dist / 2);
        for (int i = 0; i <= samples; i++) {
            float t = (float) i / (float) samples;
            int px = Math.round(x0 + dx * t);
            int py = Math.round(y0 + dy * t);
            extractor.fill(px - radius, py - radius, px + radius, py + radius, color);
        }
    }
}
