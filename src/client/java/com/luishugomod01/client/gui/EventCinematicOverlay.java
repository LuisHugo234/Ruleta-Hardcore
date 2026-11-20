package com.luishugomod01.client.gui;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.client.ritual.ReviveRitualClientManager;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/**
 * Sistema unificado de títulos cinematográficos estilo serie (barras negras tipo película,
 * viñeta temática, flash sutil, emblema animado, líneas ornamentales expansivas y título + subtítulo
 * con paleta dinámica por evento: Blood Moon, Eclipse Solar y Ritual de Revivir).
 */
public class EventCinematicOverlay implements HudElement {

    private static final Identifier EMBLEM_BLOODMOON = Ruleta_Hardcore.id("textures/gui/emblem_bloodmoon.png");
    private static final Identifier EMBLEM_ECLIPSE = Ruleta_Hardcore.id("textures/gui/emblem_eclipse.png");
    private static final Identifier EMBLEM_REVIVE_CELESTIAL = Ruleta_Hardcore.id("textures/gui/emblem_revive_celestial.png");

    public enum Theme {
        BLOOD_MOON(
                EMBLEM_BLOODMOON,
                0xFF2838, // título carmesí intenso
                0xFF9EA6, // subtítulo rojo pálido
                0xC81022, // acento / líneas / borde letterbox
                0x6E040C, // viñeta
                0x990011  // flash
        ),
        ECLIPSE_SOLAR(
                EMBLEM_ECLIPSE,
                0xFFB22E, // título ámbar dorado solar
                0xFFE4B5, // subtítulo crema dorado
                0xFF6A14, // acento naranja fuego
                0x2B0F02, // viñeta oscura cálida
                0xFF8822  // flash solar
        ),
        REVIVE_CELESTIAL(
                EMBLEM_REVIVE_CELESTIAL,
                0xFFE470, // título dorado celestial
                0xFFF8DC, // subtítulo blanco dorado
                0xFFD447, // acento oro puro
                0x6E5314, // viñeta dorada suave
                0xFFF4C2  // flash celestial
        );

        public final Identifier emblem;
        public final int titleRgb;
        public final int subtitleRgb;
        public final int accentRgb;
        public final int vignetteRgb;
        public final int flashRgb;

        Theme(Identifier emblem, int titleRgb, int subtitleRgb, int accentRgb, int vignetteRgb, int flashRgb) {
            this.emblem = emblem;
            this.titleRgb = titleRgb;
            this.subtitleRgb = subtitleRgb;
            this.accentRgb = accentRgb;
            this.vignetteRgb = vignetteRgb;
            this.flashRgb = flashRgb;
        }
    }

    private static Theme activeTheme = null;
    private static String activeTitle = "";
    private static String activeSubtitle = "";
    private static long startTimeMs = 0L;
    private static long totalDurationMs = 6200L;
    private static boolean bottomAnchored = false;
    private static float flashPeakAlpha = 0.35f;

    // Flash independiente (p. ej. anillo de diamante del Eclipse o impacto del Ritual)
    private static long standaloneFlashStartMs = 0L;
    private static long standaloneFlashDurationMs = 0L;
    private static int standaloneFlashRgb = 0xFFFFFF;
    private static float standaloneFlashPeak = 0.0f;

    public static void mostrarBloodMoon() {
        iniciar(
                Theme.BLOOD_MOON,
                "LUNA DE SANGRE",
                "La luna se tiñe de sangre...",
                6400L,
                false,
                0.42f
        );
    }

    public static void mostrarEclipseSolar() {
        iniciar(
                Theme.ECLIPSE_SOLAR,
                "ECLIPSE SOLAR",
                "El sol se extingue bajo la sombra...",
                6400L,
                false,
                0.38f
        );
    }

    public static void mostrarRevivirGlobal(String nombreJugador, boolean esCinematicLocal) {
        String titulo = nombreJugador.toUpperCase() + " HA REGRESADO";
        String subtitulo = "Los cielos abren paso a una nueva vida";
        iniciar(
                Theme.REVIVE_CELESTIAL,
                titulo,
                subtitulo,
                esCinematicLocal ? 4600L : 5800L,
                esCinematicLocal,
                0.52f
        );
    }

    public static void triggerFlash(int rgb, float peakAlpha, long durationMs) {
        standaloneFlashStartMs = System.currentTimeMillis();
        standaloneFlashDurationMs = Math.max(100L, durationMs);
        standaloneFlashRgb = rgb & 0xFFFFFF;
        standaloneFlashPeak = Mth.clamp(peakAlpha, 0.0f, 0.85f);
    }

    private static void iniciar(Theme theme, String title, String subtitle, long durationMs,
                                boolean anchorBottom, float flashAlpha) {
        activeTheme = theme;
        activeTitle = title;
        activeSubtitle = subtitle;
        startTimeMs = System.currentTimeMillis();
        totalDurationMs = Math.max(2000L, durationMs);
        bottomAnchored = anchorBottom;
        flashPeakAlpha = flashAlpha;
    }

    public static void reset() {
        activeTheme = null;
        activeTitle = "";
        activeSubtitle = "";
        startTimeMs = 0L;
        standaloneFlashDurationMs = 0L;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.font == null) {
            return;
        }

        long now = System.currentTimeMillis();
        int width = extractor.guiWidth();
        int height = extractor.guiHeight();

        // 1. Determinar apertura de barras negras tipo película (letterbox)
        float bannerLetterbox = 0.0f;
        float bannerElapsed = 0.0f;
        boolean bannerActive = false;

        if (activeTheme != null) {
            long elapsed = now - startTimeMs;
            if (elapsed >= totalDurationMs) {
                activeTheme = null;
            } else {
                bannerActive = true;
                bannerElapsed = (float) elapsed;
                float inT = Mth.clamp(bannerElapsed / 650.0f, 0.0f, 1.0f);
                float outT = Mth.clamp((totalDurationMs - bannerElapsed) / 850.0f, 0.0f, 1.0f);
                bannerLetterbox = easeOutCubic(inT) * easeOutCubic(outT);
            }
        }

        float ritualLetterbox = ReviveRitualClientManager.getCinematicLetterboxProgress();
        float letterboxProgress = Math.max(bannerLetterbox, ritualLetterbox);

        Theme visualTheme = activeTheme;
        if (visualTheme == null && ritualLetterbox > 0.001f) {
            visualTheme = Theme.REVIVE_CELESTIAL;
        }

        // 2. Viñeta temática suave alrededor de los bordes
        if (visualTheme != null && letterboxProgress > 0.01f) {
            float pulse = 0.85f + 0.15f * Mth.sin((now % 100000L) * 0.004f);
            float vigAlpha = letterboxProgress * 0.42f * pulse;
            renderVignette(extractor, width, height, visualTheme.vignetteRgb, vigAlpha);
        }

        // 3. Barras negras tipo película arriba y abajo + línea fina de acento del color del evento
        if (letterboxProgress > 0.002f) {
            int maxBarH = Math.max(26, (int) (height * 0.115f));
            int barH = Math.round(maxBarH * letterboxProgress);
            int barAlpha = Math.round(238.0f * Mth.clamp(letterboxProgress * 1.15f, 0.0f, 1.0f));
            int barColor = (barAlpha << 24);

            // Barra superior e inferior
            extractor.fill(0, 0, width, barH, barColor);
            extractor.fill(0, height - barH, width, height, barColor);

            // Sombra degradada suave saliendo del borde interior de las barras
            int shadowH = Math.max(10, barH / 3);
            int shadowTopColor = (Math.round(115.0f * letterboxProgress) << 24);
            extractor.fillGradient(0, barH, width, barH + shadowH, shadowTopColor, 0x00000000);
            extractor.fillGradient(0, height - barH - shadowH, width, height - barH, 0x00000000, shadowTopColor);

            // Línea de acento temática sutil en el borde interior de las barras
            if (visualTheme != null) {
                int accentAlpha = Math.round(170.0f * letterboxProgress);
                int accentColor = (accentAlpha << 24) | (visualTheme.accentRgb & 0xFFFFFF);
                extractor.fill(0, Math.max(0, barH - 1), width, barH, accentColor);
                extractor.fill(0, height - barH, width, Math.min(height, height - barH + 1), accentColor);
            }
        }

        // 4. Flash breve de impacto del título
        if (bannerActive && bannerElapsed < 650.0f && flashPeakAlpha > 0.01f) {
            float fp = 1.0f - (bannerElapsed / 650.0f);
            float fa = fp * fp * flashPeakAlpha;
            int flashInt = (Math.round(fa * 255.0f) << 24) | (activeTheme.flashRgb & 0xFFFFFF);
            extractor.fill(0, 0, width, height, flashInt);
        }

        // 5. Flash independiente (Anillo de Diamante del Eclipse / ruptura de cadenas)
        if (standaloneFlashDurationMs > 0L) {
            long fElapsed = now - standaloneFlashStartMs;
            if (fElapsed >= standaloneFlashDurationMs) {
                standaloneFlashDurationMs = 0L;
            } else {
                float t = (float) fElapsed / (float) standaloneFlashDurationMs;
                // Subida ultra rápida (0..0.12) y caída suave (0.12..1.0)
                float env = (t < 0.12f) ? (t / 0.12f) : (float) Math.pow(1.0f - (t - 0.12f) / 0.88f, 2.1);
                int a = Math.round(Mth.clamp(env * standaloneFlashPeak, 0.0f, 0.85f) * 255.0f);
                if (a > 1) {
                    extractor.fill(0, 0, width, height, (a << 24) | standaloneFlashRgb);
                }
            }
        }

        // 6. Tarjeta cinematográfica de título + emblema + subtítulo
        if (bannerActive && activeTheme != null) {
            renderTitleCard(extractor, mc, width, height, bannerElapsed, totalDurationMs, activeTheme,
                    activeTitle, activeSubtitle, bottomAnchored);
        }
    }

    private void renderTitleCard(
            GuiGraphicsExtractor extractor,
            Minecraft mc,
            int width,
            int height,
            float elapsedMs,
            float durationMs,
            Theme theme,
            String title,
            String subtitle,
            boolean anchorBottom
    ) {
        float enterT = Mth.clamp((elapsedMs - 140.0f) / 680.0f, 0.0f, 1.0f);
        float exitT = Mth.clamp((durationMs - elapsedMs) / 800.0f, 0.0f, 1.0f);
        float alpha = easeOutCubic(enterT) * easeOutCubic(exitT);
        if (alpha <= 0.02f) {
            return;
        }

        int centerX = width / 2;
        int baseY = anchorBottom
                ? (int) (height * 0.74f)
                : (int) (height * 0.41f);

        float slideY = (1.0f - easeOutCubic(enterT)) * 12.0f;

        // Fondo oscuro translúcido suave detrás de la tarjeta para máxima legibilidad
        int cardHalfW = Math.min(width / 2 - 20, Math.max(135, mc.font.width(title) + 58));
        int bgAlpha = Math.round(125.0f * alpha);
        int bgColor = (bgAlpha << 24) | 0x050307;
        int cardTop = (int) (baseY - 22 + slideY);
        int cardBot = (int) (baseY + 32 + slideY);
        extractor.fillGradient(centerX - cardHalfW, cardTop, centerX + cardHalfW, cardBot, 0x00000000, bgColor);
        extractor.fillGradient(centerX - cardHalfW, cardBot, centerX + cardHalfW, cardBot + 10, bgColor, 0x00000000);

        // Emblema flotante arriba del título
        int emblemSize = anchorBottom ? 34 : 44;
        float emblemScale = 0.78f + 0.22f * easeOutBack(enterT);
        float bob = Mth.sin(elapsedMs * 0.0045f) * 2.2f;
        int emblemCenterY = (int) (baseY - (anchorBottom ? 28 : 38) + slideY + bob);
        int emblemTint = (Math.round(alpha * 255.0f) << 24) | 0xFFFFFF;

        extractor.pose().pushMatrix();
        extractor.pose().translate(centerX, emblemCenterY);
        extractor.pose().scale(emblemScale, emblemScale);
        extractor.blit(
                RenderPipelines.GUI_TEXTURED,
                theme.emblem,
                -emblemSize / 2, -emblemSize / 2,
                0.0f, 0.0f,
                emblemSize, emblemSize,
                emblemSize, emblemSize,
                emblemTint
        );
        extractor.pose().popMatrix();

        // Líneas ornamentales expansivas encima y debajo del título
        float lineExpand = easeOutCubic(Mth.clamp((elapsedMs - 200.0f) / 700.0f, 0.0f, 1.0f)) * exitT;
        int lineHalfW = Math.round(cardHalfW * 0.92f * lineExpand);
        int lineAlpha = Math.round(210.0f * alpha);
        int lineColor = (lineAlpha << 24) | (theme.accentRgb & 0xFFFFFF);
        int lineYTop = (int) (baseY - 12 + slideY);
        int lineYBot = (int) (baseY + 15 + slideY);

        if (lineHalfW > 4) {
            extractor.fill(centerX - lineHalfW, lineYTop, centerX + lineHalfW, lineYTop + 1, lineColor);
            extractor.fill(centerX - lineHalfW, lineYBot, centerX + lineHalfW, lineYBot + 1, lineColor);

            // Pequeños rombos/nodos decorativos en los extremos de la línea superior
            int dotAlpha = Math.round(235.0f * alpha);
            int dotColor = (dotAlpha << 24) | (theme.titleRgb & 0xFFFFFF);
            extractor.fill(centerX - lineHalfW - 2, lineYTop - 1, centerX - lineHalfW + 1, lineYTop + 2, dotColor);
            extractor.fill(centerX + lineHalfW - 1, lineYTop - 1, centerX + lineHalfW + 2, lineYTop + 2, dotColor);
        }

        // Título principal con ligero overshoot elástico y glow suave
        Component titleComp = Component.literal("§l" + title);
        int titleW = mc.font.width(titleComp);
        float baseScale = anchorBottom ? 1.85f : 2.30f;
        // Ajustar escala si el nombre de un jugador es muy largo para que nunca se salga de pantalla
        float maxAllowedScale = (width - 48.0f) / Math.max(1.0f, (float) titleW);
        float titleScale = Math.min(baseScale, maxAllowedScale) * (0.90f + 0.10f * easeOutBack(enterT));

        int titleAlpha = Math.round(255.0f * alpha);
        int titleColor = (titleAlpha << 24) | (theme.titleRgb & 0xFFFFFF);
        int glowAlpha = Math.round(85.0f * alpha * (0.8f + 0.2f * Mth.sin(elapsedMs * 0.008f)));
        int glowColor = (glowAlpha << 24) | (theme.accentRgb & 0xFFFFFF);

        extractor.pose().pushMatrix();
        extractor.pose().translate(centerX, baseY + slideY);
        extractor.pose().scale(titleScale, titleScale);
        if (glowAlpha > 6) {
            extractor.text(mc.font, titleComp, -titleW / 2 - 1, -mc.font.lineHeight / 2, glowColor, false);
            extractor.text(mc.font, titleComp, -titleW / 2 + 1, -mc.font.lineHeight / 2, glowColor, false);
        }
        extractor.text(mc.font, titleComp, -titleW / 2, -mc.font.lineHeight / 2, titleColor, true);
        extractor.pose().popMatrix();

        // Subtítulo con ligera entrada retrasada
        float subEnter = Mth.clamp((elapsedMs - 450.0f) / 600.0f, 0.0f, 1.0f);
        float subAlpha = easeOutCubic(subEnter) * easeOutCubic(exitT);
        if (subAlpha > 0.02f && !subtitle.isEmpty()) {
            Component subComp = Component.literal("§o" + subtitle);
            int subW = mc.font.width(subComp);
            float subScale = anchorBottom ? 1.05f : 1.15f;
            int subColor = (Math.round(240.0f * subAlpha) << 24) | (theme.subtitleRgb & 0xFFFFFF);

            extractor.pose().pushMatrix();
            extractor.pose().translate(centerX, baseY + 23.0f + slideY);
            extractor.pose().scale(subScale, subScale);
            extractor.text(mc.font, subComp, -subW / 2, -mc.font.lineHeight / 2, subColor, true);
            extractor.pose().popMatrix();
        }
    }

    private void renderVignette(GuiGraphicsExtractor extractor, int width, int height, int rgb, float alpha) {
        int a = Math.round(Mth.clamp(alpha, 0.0f, 0.75f) * 255.0f);
        if (a <= 1) {
            return;
        }
        int edgeColor = (a << 24) | (rgb & 0xFFFFFF);
        int clearColor = (rgb & 0xFFFFFF);
        int bandV = Math.max(24, height / 5);
        int bandH = Math.max(20, width / 10);

        // Gradientes arriba y abajo
        extractor.fillGradient(0, 0, width, bandV, edgeColor, clearColor);
        extractor.fillGradient(0, height - bandV, width, height, clearColor, edgeColor);

        // Bordes laterales discretos en franjas escalonadas para rodear toda la pantalla
        int steps = 4;
        for (int i = 0; i < steps; i++) {
            float frac = 1.0f - (float) i / (float) steps;
            int sa = Math.round(a * 0.45f * frac * frac);
            if (sa <= 1) continue;
            int col = (sa << 24) | (rgb & 0xFFFFFF);
            int x0 = (bandH * i) / steps;
            int x1 = (bandH * (i + 1)) / steps;
            extractor.fill(x0, 0, x1, height, col);
            extractor.fill(width - x1, 0, width - x0, height, col);
        }
    }

    private static float easeOutCubic(float t) {
        float u = 1.0f - Mth.clamp(t, 0.0f, 1.0f);
        return 1.0f - u * u * u;
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float u = Mth.clamp(t, 0.0f, 1.0f) - 1.0f;
        return 1.0f + c3 * u * u * u + c1 * u * u;
    }
}
