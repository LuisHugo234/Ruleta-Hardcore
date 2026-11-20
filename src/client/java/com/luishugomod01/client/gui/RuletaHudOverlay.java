package com.luishugomod01.client.gui;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.client.ritual.ReviveRitualClientManager;
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

    // Paleta RGB de los 6 niveles para halo, gema de la aguja y barra indicadora inferior
    private static final int[] LEVEL_RGB = {
            0x2EE034, // Nivel 1: Verde
            0xFAE026, // Nivel 2: Amarillo
            0xF57612, // Nivel 3: Naranja
            0xEB4C56, // Nivel 4: Coral
            0xBC1CDE, // Nivel 5: Púrpura
            0xD9162C  // Nivel 6: Carmesí
    };

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

        // Celebración sonora diferenciada por nivel de peligro + screen shake en Niveles 5 y 6
        if (progress >= 1.0f && !landed) {
            landed = true;
            landedTime = now;
            int winLvl = parseNivel(payloadActual.nivelNombre());
            if (mc.getSoundManager() != null) {
                if (winLvl <= 2) {
                    mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.15f, 0.9f));
                    mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.AMETHYST_BLOCK_CHIME, 1.25f, 0.85f));
                } else if (winLvl <= 4) {
                    mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ANVIL_LAND, 0.85f, 0.45f));
                    mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BELL_BLOCK, 0.82f, 0.9f));
                } else {
                    mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WITHER_SPAWN, winLvl == 6 ? 0.58f : 0.72f, 0.55f));
                    mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.70f, 0.45f));
                    ReviveRitualClientManager.triggerShake(winLvl == 6 ? 2.4f : 1.45f, winLvl == 6 ? 700L : 480L);
                }
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

                int activeLevel = landed
                        ? parseNivel(payloadActual.nivelNombre())
                        : getNivelBajoAguja(currentAngle);
                int activeRgb = LEVEL_RGB[Math.clamp(activeLevel - 1, 0, 5)];

                extractor.pose().pushMatrix();
                extractor.pose().translate(centerX, centerY + totalOffsetY);
                extractor.pose().scale(totalScale, totalScale);
                extractor.pose().translate(-centerX, -centerY);

                // 1. Contraste radial suave detrás de la ruleta + destello del color ganador al detenerse
                dibujarHaloFondoRuleta(extractor, centerX, centerY, wheelDiameter / 2, activeRgb, landed, now);

                // 2. Disco rotatorio de la ruleta
                Identifier texturaDisco = obtenerTexturaDisco(payloadActual, landed, now);
                extractor.pose().pushMatrix();
                extractor.pose().rotateAbout((float) Math.toRadians(currentAngle), centerX, centerY);
                extractor.blit(texturaDisco, wheelLeft, wheelTop, wheelLeft + wheelDiameter, wheelTop + wheelDiameter, 0.0f, 1.0f, 0.0f, 1.0f);
                extractor.pose().popMatrix();

                // 3. Aguja con física de tensión/"clack" al cruzar remaches y gema central iluminada por nivel
                int needleW = 24;
                int needleH = 38;
                int needleX = centerX - needleW / 2;
                int needleY = wheelTop - 12;
                int needlePivotX = centerX;
                int needlePivotY = needleY + 8;

                float needleAngle = calcularAnguloAgujaConTension(currentAngle, pegInterval, progress, landed, now);

                extractor.pose().pushMatrix();
                if (needleAngle != 0.0f) {
                    extractor.pose().rotateAbout((float) Math.toRadians(needleAngle), needlePivotX, needlePivotY);
                }
                extractor.blit(TEXTURE_AGUJA, needleX, needleY, needleX + needleW, needleY + needleH, 0.0f, 1.0f, 0.0f, 1.0f);
                dibujarGemaAguja(extractor, needlePivotX, needlePivotY, activeRgb, landed, now);
                extractor.pose().popMatrix();

                // 4. Barra de 6 indicadores luminosos de peligro (Opción B) debajo del disco
                dibujarBarraIndicadoresNivel(extractor, centerX, wheelTop + wheelDiameter + 10, activeLevel, landed, now);

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

    private static int getNivelBajoAguja(float currentAngle) {
        float wheelAngle = ((360.0f - (currentAngle % 360.0f)) % 360.0f + 360.0f) % 360.0f;
        return Math.clamp((int) (wheelAngle / 60.0f) + 1, 1, 6);
    }

    /**
     * Física de aguja con tensión real al chocar contra cada remache y latigazo seco ("clack") al soltarse.
     * Más marcada cuando la ruleta va frenando en los últimos remaches para maximizar el suspenso.
     */
    private static float calcularAnguloAgujaConTension(float currentAngle, int pegInterval, float progress, boolean landed, long now) {
        if (landed) {
            long dt = now - landedTime;
            if (dt < 260L) {
                float t = dt / 260.0f;
                return (float) Math.sin(t * Math.PI * 2.5) * 4.2f * (1.0f - t);
            }
            return 0.0f;
        }
        float pegFraction = (currentAngle % pegInterval) / (float) pegInterval;
        // Más tensión conforme la ruleta desacelera (progress 0 -> 1)
        float maxBend = 10.5f + 6.5f * progress;
        if (pegFraction < 0.78f) {
            float push = pegFraction / 0.78f;
            return (float) Math.pow(push, 1.35) * maxBend;
        } else {
            // Latigazo seco de regreso con ligero sobreimpulso negativo (-2.8°)
            float snap = (pegFraction - 0.78f) / 0.22f;
            return maxBend * (1.0f - snap) * (1.0f - snap) - (float) Math.sin(snap * Math.PI) * 2.8f;
        }
    }

    private static void dibujarHaloFondoRuleta(
            GuiGraphicsExtractor extractor,
            int cx, int cy, int wheelRadius,
            int activeRgb, boolean landed, long now
    ) {
        // Sombra radial suave detrás de la ruleta para darle contraste sobre cualquier fondo
        int[] shadowRadii = { wheelRadius + 24, wheelRadius + 16, wheelRadius + 9, wheelRadius + 3 };
        int[] shadowAlphas = { 0x1A, 0x2A, 0x3C, 0x52 };
        for (int i = 0; i < shadowRadii.length; i++) {
            dibujarDiscoSuave(extractor, cx, cy, shadowRadii[i], (shadowAlphas[i] << 24));
        }

        // Halo suave del color del nivel activo + destello expansivo al detenerse
        if (landed) {
            long dt = Math.max(0L, now - landedTime);
            if (dt < 1100L) {
                float t = dt / 1100.0f;
                float ease = 1.0f - (1.0f - t) * (1.0f - t);
                int burstR = wheelRadius + 6 + Math.round(ease * 30.0f);
                int burstAlpha = Math.round((1.0f - t) * (1.0f - t) * 115.0f);
                if (burstAlpha > 2) {
                    dibujarDiscoSuave(extractor, cx, cy, burstR, (burstAlpha << 24) | activeRgb);
                }
            }
            float pulse = 0.75f + 0.25f * (float) Math.sin((now - landedTime) * 0.012);
            int glowA = Math.round(58.0f * pulse);
            dibujarDiscoSuave(extractor, cx, cy, wheelRadius + 10, (glowA << 24) | activeRgb);
        } else {
            dibujarDiscoSuave(extractor, cx, cy, wheelRadius + 7, (0x24 << 24) | activeRgb);
        }
    }

    private static void dibujarDiscoSuave(GuiGraphicsExtractor extractor, int cx, int cy, int radius, int argb) {
        int r2 = radius * radius;
        for (int dy = -radius; dy <= radius; dy += 2) {
            int span = (int) Math.floor(Math.sqrt(Math.max(0, r2 - dy * dy)));
            if (span > 0) {
                extractor.fill(cx - span, cy + dy, cx + span, cy + dy + 2, argb);
            }
        }
    }

    private static void dibujarGemaAguja(
            GuiGraphicsExtractor extractor,
            int px, int py,
            int rgb, boolean landed, long now
    ) {
        float pulse = landed ? (0.85f + 0.15f * (float) Math.sin((now - landedTime) * 0.015)) : 0.72f;
        int glowA = Math.round((landed ? 120.0f : 55.0f) * pulse);
        // Pequeño halo alrededor de la gema
        extractor.fill(px - 3, py - 2, px + 3, py + 2, (glowA << 24) | rgb);
        extractor.fill(px - 2, py - 3, px + 2, py + 3, (glowA << 24) | rgb);
        // Núcleo sólido de la gema en el centro del engaste dorado
        extractor.fill(px - 2, py - 2, px + 2, py + 2, 0xFF000000 | rgb);
        // Brillo especular blanco arriba-izquierda
        extractor.fill(px - 1, py - 1, px, py, 0xDDFFFFFF);
    }

    /**
     * Barra de 6 indicadores luminosos de peligro (Opción B) debajo de la ruleta.
     * Ilumina en tiempo real el nivel que pasa bajo la aguja y resalta el ganador al detenerse.
     */
    private static void dibujarBarraIndicadoresNivel(
            GuiGraphicsExtractor extractor,
            int centerX, int topY,
            int activeLevel, boolean landed, long now
    ) {
        int segW = 14;
        int gap = 4;
        int totalW = 6 * segW + 5 * gap;
        int startX = centerX - totalW / 2;

        // Fondo discreto oscuro redondeado detrás de los 6 indicadores
        extractor.fill(startX - 5, topY - 3, startX + totalW + 5, topY + 10, 0xAA0A0C12);
        extractor.fill(startX - 4, topY - 4, startX + totalW + 4, topY - 3, 0x662A3040);
        extractor.fill(startX - 4, topY + 10, startX + totalW + 4, topY + 11, 0x662A3040);

        for (int i = 0; i < 6; i++) {
            int lvl = i + 1;
            boolean isActive = (lvl == activeLevel);
            int rgb = LEVEL_RGB[i];
            int sx = startX + i * (segW + gap);

            if (isActive) {
                float pulse = landed ? (0.85f + 0.15f * (float) Math.sin((now - landedTime) * 0.015)) : 1.0f;
                int glowA = Math.round((landed ? 125.0f : 80.0f) * pulse);
                // Aura suave alrededor del indicador activo
                extractor.fill(sx - 2, topY - 3, sx + segW + 2, topY + 10, (glowA << 24) | rgb);
                // Barrita iluminada ligeramente más alta (+2px arriba y abajo)
                extractor.fill(sx, topY - 1, sx + segW, topY + 8, 0xFF000000 | rgb);
                // Línea superior brillante tipo gema
                extractor.fill(sx + 1, topY - 1, sx + segW - 1, topY, 0xBBFFFFFF);
            } else {
                // Indicador inactivo atenuado
                int dimA = landed ? 0x38 : 0x66;
                extractor.fill(sx, topY + 1, sx + segW, topY + 6, (dimA << 24) | rgb);
            }
        }
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
