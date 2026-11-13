package com.luishugomod01.client.gui;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.client.vidas.VidasClientState;
import com.luishugomod01.network.RuletaAnimacionPayload;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class RuletaHudOverlay implements HudElement {

    private static final Identifier TEXTURE_DISCO =
            Ruleta_Hardcore.id("textures/gui/ruleta_disco.png");
    private static final Identifier TEXTURE_AGUJA =
            Ruleta_Hardcore.id("textures/gui/ruleta_aguja.png");

    // Matriz de texturas para la animación de barrido horario al detenerse
    // TEXTURAS_BARRIDO[nivel - 1][paso - 1] (pasos 1 a 4 intermedios, paso 5 [índice 4] es el disco 100% monocromático)
    private static final Identifier[][] TEXTURAS_BARRIDO = new Identifier[6][5];
    static {
        for (int lvl = 1; lvl <= 6; lvl++) {
            for (int step = 1; step <= 4; step++) {
                TEXTURAS_BARRIDO[lvl - 1][step - 1] =
                        Ruleta_Hardcore.id("textures/gui/ruleta_disco_nivel_" + lvl + "_paso_" + step + ".png");
            }
            TEXTURAS_BARRIDO[lvl - 1][4] =
                    Ruleta_Hardcore.id("textures/gui/ruleta_disco_nivel_" + lvl + ".png");
        }
    }

    private static final Identifier SPRITE_HEART_CONTAINER =
            Identifier.withDefaultNamespace("hud/heart/container_hardcore");
    private static final Identifier SPRITE_HEART_FULL =
            Identifier.withDefaultNamespace("hud/heart/hardcore_full");

    private static RuletaAnimacionPayload payloadActual = null;
    private static long startTime = 0L;
    private static long totalDurationMs = 0L;
    private static int lastPegIndex = -1;
    private static boolean landed = false;
    private static long landedTime = 0L;
    private static boolean activa = false;

    public static void iniciarAnimacion(RuletaAnimacionPayload payload) {
        payloadActual = payload;
        startTime = System.currentTimeMillis();
        totalDurationMs = Math.max(1000L, payload.duracionTicks() * 50L);
        lastPegIndex = -1;
        landed = false;
        landedTime = 0L;
        activa = true;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) {
            return;
        }

        renderizarContadorVidas(extractor, mc);
        renderizarIndicadoresPeligro(extractor, mc);

        if (!activa || payloadActual == null) {
            return;
        }

        long now = System.currentTimeMillis();
        long elapsed = now - startTime;
        float progress = Math.clamp((float) elapsed / (float) totalDurationMs, 0.0f, 1.0f);

        // Curva cúbica suave de desaceleración (ease-out)
        float eased = 1.0f - (float) Math.pow(1.0 - progress, 3.5);
        float currentAngle = payloadActual.targetAngle() * eased;

        // Clics rítmicos de audio al cruzar remaches (el nuevo disco tiene 24 remaches exteriores = cada 15°)
        int pegInterval = 15;
        int currentPeg = (int) (currentAngle / pegInterval);
        if (currentPeg > lastPegIndex) {
            lastPegIndex = currentPeg;
            if (progress < 1.0f && mc.getSoundManager() != null) {
                float speedRatio = 1.0f - progress;
                float pitch = 0.85f + speedRatio * 0.5f;
                mc.getSoundManager().play(
                        SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, pitch)
                );
            }
        }

        // Celebración sonora de parada
        if (progress >= 1.0f && !landed) {
            landed = true;
            landedTime = now;
            if (mc.getSoundManager() != null) {
                mc.getSoundManager().play(
                        SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 1.0f)
                );
            }
        }

        // La notificación termina exactamente 10 segundos después de detenerse
        if (landed && (now - landedTime >= 10000L)) {
            activa = false;
            payloadActual = null;
            return;
        }

        int width = extractor.guiWidth();
        int height = extractor.guiHeight();

        boolean mostrarRueda = !landed || (now - landedTime < 2000L);
        if (mostrarRueda) {
            float totalScale = 1.0f;
            float totalOffsetY = 0.0f;

            // Animacion elastica: entrada, impacto al caer y salida
            if (elapsed < 500L) {
                float p = (float) elapsed / 500.0f;
                if (p < 0.6f) {
                    float sub = p / 0.6f;
                    totalScale = (float) Math.sin(sub * Math.PI * 0.5) * 1.25f;
                    totalOffsetY = -18.0f * (float) Math.sin(sub * Math.PI);
                } else {
                    float sub = (p - 0.6f) / 0.4f;
                    totalScale = 1.0f + 0.25f * (float) Math.cos(sub * Math.PI * 0.5) * (1.0f - sub);
                    totalOffsetY = 6.0f * (float) Math.sin(sub * Math.PI) * (1.0f - sub);
                }
            } else if (landed) {
                long elapsedLanded = now - landedTime;
                if (elapsedLanded < 250L) {
                    float p = (float) elapsedLanded / 250.0f;
                    totalScale += 0.10f * (float) Math.sin(p * Math.PI) * (1.0f - p);
                    totalOffsetY -= 6.0f * (float) Math.sin(p * Math.PI) * (1.0f - p);
                } else if (elapsedLanded >= 1700L && elapsedLanded < 2000L) {
                    float p = (float) (elapsedLanded - 1700L) / 300.0f;
                    if (p < 0.25f) {
                        totalScale += 0.08f * (float) Math.sin((p / 0.25f) * Math.PI);
                    } else {
                        float sub = (p - 0.25f) / 0.75f;
                        totalScale *= Math.max(0.0f, 1.0f - sub * sub);
                        totalOffsetY -= 12.0f * sub;
                    }
                }
            }

            if (totalScale > 0.001f) {
                int wheelDiameter = 160;
                int centerX = width / 2;
                int centerY = height / 2;
                int wheelLeft = centerX - wheelDiameter / 2;
                int wheelTop = centerY - wheelDiameter / 2;

                extractor.pose().pushMatrix();
                extractor.pose().translate(centerX, centerY + totalOffsetY);
                extractor.pose().scale(totalScale, totalScale);
                extractor.pose().translate(-centerX, -centerY);

                Identifier texturaDisco = obtenerTexturaDisco(payloadActual, landed, now);

                extractor.pose().pushMatrix();
                extractor.pose().rotateAbout((float) Math.toRadians(currentAngle), centerX, centerY);
                extractor.blit(texturaDisco, wheelLeft, wheelTop, wheelLeft + wheelDiameter, wheelTop + wheelDiameter, 0.0f, 1.0f, 0.0f, 1.0f);
                extractor.pose().popMatrix();

                int needleW = 24;
                int needleH = 38;
                int needleX = centerX - needleW / 2;
                int needleY = wheelTop - 12;
                int needlePivotX = centerX;
                int needlePivotY = needleY + 8;

                float needleAngle = 0.0f;
                if (progress < 1.0f) {
                    float pegFraction = (currentAngle % pegInterval) / (float) pegInterval;
                    needleAngle = (float) Math.sin(pegFraction * Math.PI) * 7.5f;
                }

                extractor.pose().pushMatrix();
                if (needleAngle != 0.0f) {
                    extractor.pose().rotateAbout((float) Math.toRadians(needleAngle), needlePivotX, needlePivotY);
                }
                extractor.blit(TEXTURE_AGUJA, needleX, needleY, needleX + needleW, needleY + needleH, 0.0f, 1.0f, 0.0f, 1.0f);
                extractor.pose().popMatrix();

                extractor.pose().popMatrix();
            }
        }

        // 2. MENSAJE EN PANTALLA ARRIBA A LA IZQUIERDA POR 10 SEGUNDOS TRAS CAER
        if (landed && (now - landedTime < 10000L)) {
            int boxX = 10;
            int boxY = 10;
            int maxLineWidth = 210;

            Component titulo = Component.literal("§c§l" + payloadActual.castigoNombre());
            Component explicacion = Component.literal("§f" + payloadActual.explicacion());

            List<FormattedCharSequence> lineasExplicacion = mc.font.split(explicacion, maxLineWidth);

            int maxW = mc.font.width(titulo);
            for (FormattedCharSequence line : lineasExplicacion) {
                int lw = mc.font.width(line);
                if (lw > maxW) {
                    maxW = lw;
                }
            }

            int pad = 6;
            int boxW = maxW + pad * 2 + 4;
            int lineHeight = 10;
            int boxH = pad * 2 + 13 + lineasExplicacion.size() * lineHeight;

            // Fondo sutil traslúcido y barra vertical lateral dorada
            extractor.fill(boxX, boxY, boxX + boxW, boxY + boxH, 0x77000000);
            extractor.fill(boxX, boxY, boxX + 2, boxY + boxH, 0xFFFFCC00);

            // Título del castigo
            extractor.text(mc.font, titulo, boxX + pad + 3, boxY + pad, 0xFFFFFFFF, true);

            // Explicación del efecto
            int currentY = boxY + pad + 13;
            for (FormattedCharSequence line : lineasExplicacion) {
                extractor.text(mc.font, line, boxX + pad + 3, currentY, 0xFFFFFFFF, true);
                currentY += lineHeight;
            }
        }
    }

    private void renderizarContadorVidas(GuiGraphicsExtractor extractor, Minecraft mc) {
        if (!VidasClientState.isSincronizado()) {
            return;
        }
        int restantes = VidasClientState.getVidasRestantes();
        int maximas = VidasClientState.getVidasMaximas();

        int screenWidth = extractor.guiWidth();
        int centerX = screenWidth / 2;

        double guiScale = mc.getWindow().getGuiScale();
        if (guiScale <= 0) guiScale = 1.0;

        int topY = Math.max(8, (int) Math.round(10.0 / guiScale * 2.0));

        long now = System.currentTimeMillis();

        int tensionOffsetX = 0;
        int tensionOffsetY = 0;
        if (restantes == 1) {
            long beatCycle = now % 800L;
            if (beatCycle < 80L || (beatCycle >= 130L && beatCycle < 210L)) {
                tensionOffsetY = -1;
            } else {
                tensionOffsetX = (int) Math.round(Math.sin(now * 0.04) * 0.7);
            }
        }

        if (maximas <= 5) {
            // Escala visual fija para corazones (~34px en pantalla independiente del GuiScale)
            int heartW = Math.max(10, (int) Math.round(34.0 / guiScale));
            int heartH = heartW;
            int gap = Math.max(2, (int) Math.round(5.0 / guiScale));
            int spacing = heartW + gap;
            int totalW = maximas * spacing - gap;
            int startX = centerX - totalW / 2;

            for (int i = 0; i < maximas; i++) {
                int hx = startX + i * spacing;
                int hy = topY;

                if (restantes == 1 && i == 0) {
                    hx += tensionOffsetX;
                    hy += tensionOffsetY;
                }

                extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_HEART_CONTAINER, hx, hy, heartW, heartH);

                if (i < restantes) {
                    extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_HEART_FULL, hx, hy, heartW, heartH);
                }
            }
        } else {
            int heartW = Math.max(18, (int) Math.round(48.0 / guiScale));
            int heartH = heartW;
            int hx = centerX - heartW / 2 + (restantes == 1 ? tensionOffsetX : 0);
            int hy = topY + (restantes == 1 ? tensionOffsetY : 0);

            extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_HEART_CONTAINER, hx, hy, heartW, heartH);

            if (restantes > 0) {
                extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_HEART_FULL, hx, hy, heartW, heartH);
            }

            String vidasStr = String.valueOf(restantes);
            int numW = mc.font.width(vidasStr);
            int textX = centerX - numW / 2 + (restantes == 1 ? tensionOffsetX : 0);
            int textY = hy + (heartH / 2) - 4;

            int textColor;
            if (restantes <= 1) {
                textColor = ((now % 400L) < 200L) ? 0xFFFF4444 : 0xFFFFFFFF;
            } else if (restantes == 2) {
                textColor = 0xFFFFAA00;
            } else {
                textColor = 0xFFFFFFFF;
            }
            extractor.text(mc.font, vidasStr, textX, textY, textColor, true);

            Component subTexto = Component.literal("§7" + restantes + " / " + maximas);
            int subW = mc.font.width(subTexto);
            int subX = centerX - subW / 2;
            int subY = hy + heartH + 2;
            extractor.text(mc.font, subTexto, subX, subY, 0xFFFFFFFF, true);
        }
    }

    private void renderizarIndicadoresPeligro(GuiGraphicsExtractor extractor, Minecraft mc) {
        if (!VidasClientState.isSincronizado()) {
            return;
        }
        int insolacion = VidasClientState.getInsolacionPct();
        int congelacion = VidasClientState.getCongelacionPct();
        int cordura = VidasClientState.getCorduraPct();
        int avisoRayo = VidasClientState.getAvisoRayoSegundos();

        int x = 8;
        int y = 8;
        int barW = 86;
        int barH = 12;

        if (avisoRayo > 0) {
            boolean blink = (System.currentTimeMillis() / 200L) % 2L == 0L;
            int bg = blink ? 0xDD442200 : 0xCC1A1205;
            int border = blink ? 0xFFFFEE33 : 0xFFFFAA00;
            extractor.fill(x, y, x + barW + 24, y + barH + 2, bg);
            extractor.outline(x, y, barW + 24, barH + 2, border);
            String txt = "§e§l⚡ RAYO EN: §f§l" + avisoRayo + "s";
            extractor.text(mc.font, Component.literal(txt), x + 4, y + 3, 0xFFFFFFFF, true);
            y += barH + 5;
        }

        if (insolacion > 0) {
            dibujarBarraPeligro(extractor, mc, x, y, barW, barH,
                    "§6☀ Insolación " + insolacion + "%", insolacion / 100.0f, 0xFFFF8800, 0xFFFF3300);
            y += barH + 4;
        }

        if (congelacion > 0) {
            dibujarBarraPeligro(extractor, mc, x, y, barW, barH,
                    "§b❄ Hipotermia " + congelacion + "%", congelacion / 100.0f, 0xFF33BBFF, 0xFF0066FF);
            y += barH + 4;
        }

        if (cordura >= 0) {
            dibujarMedidorCorduraCircular(extractor, mc, x + 24, y + 22, cordura);
        }
    }

    private static void dibujarMedidorCorduraCircular(
            GuiGraphicsExtractor extractor,
            Minecraft mc,
            int cx,
            int cy,
            int cordura) {
        int outerR = 19;
        int innerR = 15;
        long timeMs = System.currentTimeMillis();
        int jitterX = (cordura == 0 && (timeMs / 90L) % 5L == 0L) ? ((timeMs % 2 == 0) ? 1 : -1) : 0;
        int drawCx = cx + jitterX;

        // Fondo circular oscuro (medallón)
        int bgCol = cordura == 0 ? 0xD81A0610 : 0xCC110B1C;
        for (int dy = -outerR - 1; dy <= outerR + 1; dy++) {
            int span = (int) Math.floor(Math.sqrt((outerR + 1) * (outerR + 1) - dy * dy));
            extractor.fill(drawCx - span, cy + dy, drawCx + span + 1, cy + dy + 1, bgCol);
        }

        // Color del anillo según estado de cordura
        int ringColor;
        if (cordura > 50) {
            ringColor = 0xFF3BD6C6; // Cian lúcido
        } else if (cordura > 0) {
            ringColor = 0xFFD9822B; // Ámbar inquieto
        } else {
            boolean pulse = (timeMs / 250L) % 2L == 0L;
            ringColor = pulse ? 0xFFE62E4D : 0xFF99112B; // Carmesí pesadilla
        }
        int trackColor = 0xFF281C38;

        // Anillo circular (pista + progreso horario desde arriba)
        double maxAngle = (cordura / 100.0) * (Math.PI * 2.0);
        for (int dy = -outerR; dy <= outerR; dy++) {
            for (int dx = -outerR; dx <= outerR; dx++) {
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist >= innerR && dist <= outerR) {
                    double angle = Math.atan2(dx, -dy);
                    if (angle < 0) angle += Math.PI * 2.0;
                    int col = (cordura > 0 && angle <= maxAngle) ? ringColor
                            : (cordura == 0 ? ringColor : trackColor);
                    extractor.fill(drawCx + dx, cy + dy, drawCx + dx + 1, cy + dy + 1, col);
                }
            }
        }

        // Ojo de Lucidez reactivo en la parte superior del círculo
        int eyeY = cy - 7;
        int irisCol = cordura > 50 ? 0xFF3BD6C6 : (cordura > 0 ? 0xFFE89A3C : 0xFFFF3355);
        if (cordura > 50) {
            // Ojo sereno abierto
            extractor.fill(drawCx - 6, eyeY - 1, drawCx + 7, eyeY + 2, 0xFFEAE6F5);
            extractor.fill(drawCx - 4, eyeY - 2, drawCx + 5, eyeY + 3, 0xFFEAE6F5);
            extractor.fill(drawCx - 2, eyeY - 2, drawCx + 3, eyeY + 3, irisCol);
            extractor.fill(drawCx - 1, eyeY - 1, drawCx + 2, eyeY + 2, 0xFF0E0818);
        } else if (cordura > 0) {
            // Ojo entrecerrado en tensión
            extractor.fill(drawCx - 6, eyeY, drawCx + 7, eyeY + 2, 0xFFE0D8EA);
            extractor.fill(drawCx - 2, eyeY - 1, drawCx + 3, eyeY + 2, irisCol);
            extractor.fill(drawCx, eyeY - 1, drawCx + 1, eyeY + 2, 0xFF0E0818);
        } else {
            // Ojo de pesadilla muy abierto con pupila vertical
            extractor.fill(drawCx - 6, eyeY - 2, drawCx + 7, eyeY + 3, 0xFF380914);
            extractor.fill(drawCx - 3, eyeY - 2, drawCx + 4, eyeY + 3, irisCol);
            extractor.fill(drawCx, eyeY - 2, drawCx + 1, eyeY + 3, 0xFF080205);
        }

        // Porcentaje centrado dentro del círculo
        String pctText = cordura + "%";
        int textCol = cordura == 0 ? 0xFFFF6677 : (cordura <= 50 ? 0xFFFFCC88 : 0xFFE8F8F5);
        int textW = mc.font.width(pctText);
        extractor.text(mc.font, pctText, drawCx - textW / 2, cy + 2, textCol, true);

        // Etiqueta inferior
        String label = "CORDURA";
        int labelCol = cordura == 0 ? 0xFFCC3355 : 0xFFBCA8D9;
        int labelW = mc.font.width(label);
        extractor.text(mc.font, label, cx - labelW / 2, cy + outerR + 3, labelCol, true);
    }

    private void dibujarBarraPeligro(
            GuiGraphicsExtractor extractor,
            Minecraft mc,
            int x,
            int y,
            int w,
            int h,
            String label,
            float ratio,
            int fillColor,
            int borderColor
    ) {
        extractor.fill(x, y, x + w, y + h, 0xCC101016);
        int fillW = Math.clamp((int) Math.round((w - 2) * ratio), 0, w - 2);
        if (fillW > 0) {
            extractor.fill(x + 1, y + 1, x + 1 + fillW, y + h - 1, fillColor);
        }
        extractor.outline(x, y, w, h, borderColor);
        extractor.text(mc.font, Component.literal(label), x + 4, y + 2, 0xFFFFFFFF, true);
    }

    private static int parseNivel(String nivelNombre) {
        if (nivelNombre == null) return 1;
        for (int i = 1; i <= 6; i++) {
            if (nivelNombre.contains(String.valueOf(i))) return i;
        }
        return 1;
    }

    private static Identifier obtenerTexturaDisco(RuletaAnimacionPayload payload, boolean landed, long now) {
        if (!landed || payload == null || payload.nivelNombre() == null) {
            return TEXTURE_DISCO;
        }
        long elapsedLanded = now - landedTime;
        long delayMs = 120L;       // Breve pausa estática en el gajo ganador (~120ms)
        long stepDurationMs = 65L; // Duración de cada avance de gajo hacia la derecha (~65ms)

        if (elapsedLanded < delayMs) {
            return TEXTURE_DISCO;
        }

        int lvl = parseNivel(payload.nivelNombre());
        int stepIndex = (int) ((elapsedLanded - delayMs) / stepDurationMs);
        if (stepIndex >= 4) {
            return TEXTURAS_BARRIDO[lvl - 1][4]; // Paso 5: Todo el disco lleno del color ganador
        }
        return TEXTURAS_BARRIDO[lvl - 1][stepIndex]; // Pasos 1 a 4: Barrido progresivo a la derecha
    }
}
