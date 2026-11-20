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

    private static final Identifier TEXTURE_FANTASMA =
            Ruleta_Hardcore.id("textures/gui/fantasma_muerte.png");
    private static final Identifier TEXTURE_SILUETA =
            Ruleta_Hardcore.id("textures/gui/fantasma_muerte_silueta.png");

    private static final Identifier SPRITE_HEART_CONTAINER =
            Identifier.withDefaultNamespace("hud/heart/container_hardcore");
    private static final Identifier SPRITE_HEART_FULL =
            Identifier.withDefaultNamespace("hud/heart/hardcore_full");

    private static final long DURACION_TOTAL_MS = 5100L;
    private static final long DURACION_FLASH_MS = 650L;
    private static final long INICIO_GLITCH_MS = 3900L;

    private static final int GHOST_W = 116;
    private static final int GHOST_H = 114;

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

        renderizarVineta(extractor, width, height, 0.55f + 0.45f * p, flashAlpha, eliminado);

        int flashTint = eliminado ? 0x990018 : 0x5A0A68;
        int flashA = Math.round(145.0f * flashAlpha * flashAlpha);
        if (flashA > 2) {
            extractor.fill(0, 0, width, height, (flashA << 24) | flashTint);
        }

        if (elapsed < 420L) {
            float boltAlpha = 1.0f - (float) elapsed / 420.0f;
            int coreCol = (Math.round(240.0f * boltAlpha) << 24) | 0xFFFFFF;
            int haloCol = (Math.round(165.0f * boltAlpha) << 24) | (eliminado ? 0xFF3355 : 0xC666FF);
            long seed = elapsed / 45L;
            dibujarRayoFino(extractor, 0, (int) (height * 0.18), width, (int) (height * 0.82), haloCol, coreCol, seed + 1L);
            dibujarRayoFino(extractor, (int) (width * 0.84), 0, (int) (width * 0.16), height, haloCol, coreCol, seed + 2L);
            dibujarRayoFino(extractor, (int) (width * 0.36), 0, (int) (width * 0.64), height, haloCol, coreCol, seed + 3L);
        }

        float scaleX;
        float scaleY;
        Identifier tex;
        if (elapsed < 320L) {
            float t = (float) elapsed / 320.0f;
            float zoom = 1.0f + 1.55f * (float) Math.pow(1.0f - t, 2.4);
            scaleX = zoom;
            scaleY = zoom;
            tex = TEXTURE_SILUETA;
        } else {
            float t = (float) (elapsed - 320L) / (float) (DURACION_FLASH_MS - 320L);
            float bounce = Mth.sin(t * Mth.PI) * (1.0f - t * 0.4f);
            scaleX = 1.0f + bounce * 0.18f;
            scaleY = 1.0f - bounce * 0.12f;
            tex = TEXTURE_FANTASMA;
        }

        int ghostY = centerY - 26;
        if (elapsed >= 320L) {
            float revealT = Mth.clamp((float) (elapsed - 320L) / (float) (DURACION_FLASH_MS - 320L), 0.0f, 1.0f);
            renderizarHaloContraste(extractor, centerX, ghostY, revealT, 1.0f - revealT, eliminado);
            renderizarOndaExpansiva(extractor, centerX, ghostY, revealT, eliminado);
        }
        dibujarFantasma(extractor, tex, centerX, ghostY, scaleX, scaleY, 0.0f, 0xFFFFFFFF);
    }

    private void renderizarFaseBurlas(GuiGraphicsExtractor extractor, Minecraft mc,
                                      int width, int height, int centerX, int centerY,
                                      long elapsed, boolean eliminado) {
        long timeInPhase = elapsed - DURACION_FLASH_MS;

        float laughCycle = (timeInPhase % 460L) / 460.0f;
        float laughBeat = Math.max(0.0f, Mth.sin(laughCycle * Mth.PI));
        float laughPeak = laughBeat * laughBeat;

        renderizarVineta(extractor, width, height, 1.0f, laughPeak * 0.45f, eliminado);
        renderizarChispas(extractor, centerX, centerY - 26, elapsed, 1.0f, eliminado);

        float bobbing = Mth.sin(timeInPhase * 0.0052f) * 7.5f - laughPeak * 8.5f;
        float shakeX = laughPeak > 0.35f ? Mth.sin(timeInPhase * 0.11f) * 2.4f * laughPeak : 0.0f;
        float tilt = Mth.sin(timeInPhase * 0.0068f) * 5.2f + (laughPeak * 3.5f * ((timeInPhase / 460L) % 2L == 0L ? 1.0f : -1.0f));
        float scaleX = 1.0f + laughPeak * 0.10f;
        float scaleY = 1.0f - laughPeak * 0.07f + 0.03f * Mth.sin(timeInPhase * 0.012f);

        int ghostCx = Math.round(centerX + shakeX);
        int ghostCy = Math.round(centerY - 28 + bobbing);

        renderizarHaloContraste(extractor, ghostCx, ghostCy, 1.0f, laughPeak, eliminado);

        int ecoRgb = eliminado ? 0xFF2848 : 0xB855FF;
        float ecoSpread = 7.0f + laughPeak * 13.0f;
        int ecoAlpha1 = Math.round(74.0f + laughPeak * 62.0f);
        int ecoAlpha2 = Math.round(44.0f + laughPeak * 40.0f);

        dibujarFantasma(
                extractor,
                TEXTURE_FANTASMA,
                Math.round(ghostCx + ecoSpread),
                Math.round(ghostCy - ecoSpread * 0.7f),
                scaleX * (1.06f + laughPeak * 0.08f),
                scaleY * (1.06f + laughPeak * 0.08f),
                tilt + 4.0f,
                (ecoAlpha1 << 24) | ecoRgb
        );
        dibujarFantasma(
                extractor,
                TEXTURE_FANTASMA,
                Math.round(ghostCx - ecoSpread * 0.65f),
                Math.round(ghostCy - ecoSpread * 0.35f),
                scaleX * 1.03f,
                scaleY * 1.03f,
                tilt - 3.0f,
                (ecoAlpha2 << 24) | ecoRgb
        );

        int tintPrincipal = eliminado && laughPeak > 0.55f ? 0xFFFFD8DE : 0xFFFFFFFF;
        dibujarFantasma(extractor, TEXTURE_FANTASMA, ghostCx, ghostCy, scaleX, scaleY, tilt, tintPrincipal);

        renderizarBannerTextoYCorazones(extractor, mc, centerX, centerY + 44, 1.0f, elapsed, eliminado);
    }

    private void renderizarFaseGlitch(GuiGraphicsExtractor extractor, Minecraft mc,
                                      int width, int height, int centerX, int centerY,
                                      long elapsed, boolean eliminado) {
        long timeInGlitch = elapsed - INICIO_GLITCH_MS;
        long durGlitch = DURACION_TOTAL_MS - INICIO_GLITCH_MS;
        float p = Mth.clamp((float) timeInGlitch / (float) durGlitch, 0.0f, 1.0f);
        float fadeAlpha = (1.0f - p) * (1.0f - p * 0.35f);

        renderizarVineta(extractor, width, height, fadeAlpha, 0.0f, eliminado);

        int ghostY = Math.round(centerY - 28 - p * 12.0f);
        renderizarHaloContraste(extractor, centerX, ghostY, fadeAlpha, 0.0f, eliminado);
        int slices = 8;
        int sliceH = GHOST_H / slices;
        int mainAlpha = Math.round(255.0f * fadeAlpha);
        int chromaAlpha = Math.round(145.0f * fadeAlpha);

        if (mainAlpha > 2) {
            extractor.pose().pushMatrix();
            extractor.pose().translate(centerX, ghostY);

            Identifier tex = TEXTURE_FANTASMA;
            for (int s = 0; s < slices; s++) {
                float uY = s * sliceH;
                float shift = Mth.sin((elapsed * 0.095f) + s * 1.9f) * 14.0f * (0.5f + p * 2.2f);
                int drawY = -GHOST_H / 2 + s * sliceH;
                int drawX = Math.round(-GHOST_W / 2.0f + shift);
                int chromaOff = Math.max(2, Math.round(3.0f + p * 7.0f));

                if (chromaAlpha > 2) {
                    extractor.blit(
                            RenderPipelines.GUI_TEXTURED,
                            tex,
                            drawX - chromaOff, drawY,
                            0.0f, uY,
                            GHOST_W, sliceH,
                            GHOST_W, GHOST_H,
                            (chromaAlpha << 24) | 0x40F8FF
                    );
                    extractor.blit(
                            RenderPipelines.GUI_TEXTURED,
                            tex,
                            drawX + chromaOff, drawY,
                            0.0f, uY,
                            GHOST_W, sliceH,
                            GHOST_W, GHOST_H,
                            (chromaAlpha << 24) | (eliminado ? 0xFF2244 : 0xFF40D8)
                    );
                }

                extractor.blit(
                        RenderPipelines.GUI_TEXTURED,
                        tex,
                        drawX, drawY,
                        0.0f, uY,
                        GHOST_W, sliceH,
                        GHOST_W, GHOST_H,
                        (mainAlpha << 24) | 0xFFFFFF
                );
            }
            extractor.pose().popMatrix();
        }

        Random rnd = new Random(elapsed / 35L);
        int particleCount = 30;
        int pRgb = eliminado ? 0xFF6677 : 0xE2B8FF;
        for (int i = 0; i < particleCount; i++) {
            int px = (int) (centerX - 66 + rnd.nextFloat() * 132);
            int py = (int) (centerY - 82 + rnd.nextFloat() * 116 - p * 50.0f);
            int sz = 2 + rnd.nextInt(3);
            int pAlpha = Math.round(235.0f * fadeAlpha);
            if (pAlpha > 2) {
                extractor.fill(px, py, px + sz, py + sz, (pAlpha << 24) | (i % 3 == 0 ? 0xFFFFFF : pRgb));
            }
        }

        renderizarBannerTextoYCorazones(extractor, mc, centerX, centerY + 44, fadeAlpha, elapsed, eliminado);
    }

    private void dibujarFantasma(GuiGraphicsExtractor extractor, Identifier tex,
                                 int cx, int cy, float scaleX, float scaleY,
                                 float angleDeg, int tintArgb) {
        extractor.pose().pushMatrix();
        extractor.pose().translate(cx, cy);
        if (angleDeg != 0.0f) {
            extractor.pose().rotateAbout((float) Math.toRadians(angleDeg), 0, 0);
        }
        extractor.pose().scale(scaleX, scaleY);
        extractor.blit(
                RenderPipelines.GUI_TEXTURED,
                tex,
                -GHOST_W / 2, -GHOST_H / 2,
                0.0f, 0.0f,
                GHOST_W, GHOST_H,
                GHOST_W, GHOST_H,
                tintArgb
        );
        extractor.pose().popMatrix();
    }

    private void renderizarVineta(GuiGraphicsExtractor extractor, int width, int height,
                                  float alpha, float pulse, boolean eliminado) {
        int rgb = eliminado ? 0x3D040E : 0x1E0632;
        int edgeA = Math.round((128.0f + 38.0f * pulse) * Mth.clamp(alpha, 0.0f, 1.0f));
        if (edgeA <= 2) {
            return;
        }
        int edgeCol = (edgeA << 24) | rgb;
        int clearCol = rgb;
        int bandV = Math.max(30, height / 4);
        int bandH = Math.max(26, width / 9);

        extractor.fillGradient(0, 0, width, bandV, edgeCol, clearCol);
        extractor.fillGradient(0, height - bandV, width, height, clearCol, edgeCol);

        int steps = 5;
        for (int i = 0; i < steps; i++) {
            float f = 1.0f - (float) i / (float) steps;
            int sa = Math.round(edgeA * 0.62f * f * f);
            if (sa <= 2) continue;
            int col = (sa << 24) | rgb;
            int x0 = (bandH * i) / steps;
            int x1 = (bandH * (i + 1)) / steps;
            extractor.fill(x0, 0, x1, height, col);
            extractor.fill(width - x1, 0, width - x0, height, col);
        }
    }

    private void renderizarChispas(GuiGraphicsExtractor extractor, int cx, int cy,
                                   long elapsed, float alpha, boolean eliminado) {
        int count = 18;
        int rgbMain = eliminado ? 0xFF4D6A : 0xC878FF;
        for (int i = 0; i < count; i++) {
            float speed = 0.00055f + (i % 5) * 0.00012f;
            float phase = ((elapsed * speed) + i * 0.19f) % 1.0f;
            float angle = i * 2.39996f;
            float spread = 42.0f + (i % 4) * 14.0f;
            int px = Math.round(cx + Mth.cos(angle) * spread + Mth.sin(elapsed * 0.004f + i) * 6.0f);
            int py = Math.round(cy + 34.0f - phase * 110.0f);
            float env = Mth.sin(phase * Mth.PI);
            int a = Math.round(190.0f * env * alpha);
            if (a <= 3) continue;
            int sz = (i % 3 == 0) ? 3 : 2;
            int col = (a << 24) | ((i % 4 == 0) ? 0xFFFFFF : rgbMain);
            extractor.fill(px, py, px + sz, py + sz, col);
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

    private void renderizarHaloContraste(GuiGraphicsExtractor extractor, int cx, int cy,
                                         float alpha, float pulse, boolean eliminado) {
        if (alpha <= 0.02f) {
            return;
        }
        int baseRgb = eliminado ? 0x26030B : 0x160526;
        int glowRgb = eliminado ? 0xAA1634 : 0x6E22B8;
        int layers = 6;
        int maxR = Math.round(78.0f + pulse * 14.0f);
        for (int i = layers; i >= 1; i--) {
            float frac = (float) i / (float) layers;
            int r = Math.round(maxR * frac);
            int a = Math.round((24.0f + (1.0f - frac) * 28.0f) * alpha);
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
        int ringR = Math.round(34.0f + ease * 88.0f);
        int ringAlpha = Math.round(215.0f * fade);
        int coreCol = (ringAlpha << 24) | 0xFFFFFF;
        int glowCol = (Math.round(150.0f * fade) << 24) | (eliminado ? 0xFF3355 : 0xCC66FF);

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

    private void dibujarRayoFino(GuiGraphicsExtractor extractor, int x1, int y1, int x2, int y2,
                                 int haloCol, int coreCol, long seed) {
        int steps = 12;
        int prevX = x1;
        int prevY = y1;
        Random rnd = new Random(seed * 31L + x1 * 17L);

        for (int i = 1; i <= steps; i++) {
            float frac = (float) i / (float) steps;
            int nextX = Math.round(x1 + (x2 - x1) * frac);
            int nextY = Math.round(y1 + (y2 - y1) * frac);

            if (i < steps) {
                nextX += rnd.nextInt(19) - 9;
                nextY += rnd.nextInt(19) - 9;
            }

            dibujarSegmentoLinea(extractor, prevX, prevY, nextX, nextY, haloCol, 2);
            dibujarSegmentoLinea(extractor, prevX, prevY, nextX, nextY, coreCol, 1);

            prevX = nextX;
            prevY = nextY;
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
