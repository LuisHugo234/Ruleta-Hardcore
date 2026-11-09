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

    // Matriz de texturas para la animación de barrido horario al detenerse estilo DEDsafio 4
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
            // Escala visual fija estilo Extremo 3 (~34px en pantalla independiente del GuiScale)
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
