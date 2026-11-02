package com.luishugomod01.client.gui;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.network.RuletaAnimacionPayload;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
        if (!activa || payloadActual == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) {
            return;
        }

        long now = System.currentTimeMillis();
        long elapsed = now - startTime;
        float progress = Math.clamp((float) elapsed / (float) totalDurationMs, 0.0f, 1.0f);

        // Curva cúbica suave de desaceleración (ease-out)
        float eased = 1.0f - (float) Math.pow(1.0 - progress, 3.5);
        float currentAngle = payloadActual.targetAngle() * eased;

        // Clics rítmicos de audio al cruzar remaches
        int pegInterval = 20;
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

        // 1. RULETA FLOTANTE LIMPIA EN EL CENTRO
        // Se muestra mientras gira y permanece 1.5s detenida en el gajo ganador
        boolean mostrarRueda = !landed || (now - landedTime < 1500L);
        if (mostrarRueda) {
            int wheelDiameter = 160;
            int centerX = width / 2;
            int centerY = height / 2;
            int wheelLeft = centerX - wheelDiameter / 2;
            int wheelTop = centerY - wheelDiameter / 2;

            // Disco giratorio
            extractor.pose().pushMatrix();
            extractor.pose().rotateAbout((float) Math.toRadians(currentAngle), centerX, centerY);
            extractor.blit(TEXTURE_DISCO, wheelLeft, wheelTop, wheelLeft + wheelDiameter, wheelTop + wheelDiameter, 0.0f, 1.0f, 0.0f, 1.0f);
            extractor.pose().popMatrix();

            // Aguja con oscilación física al pasar por remaches
            int needleW = 24;
            int needleH = 38;
            int needleX = centerX - needleW / 2;
            int needleY = wheelTop - 24;
            int needlePivotX = centerX;
            int needlePivotY = needleY + 5;

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
}
