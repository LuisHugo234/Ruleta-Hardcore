package com.luishugomod01.client.vidas;

import com.luishugomod01.Ruleta_Hardcore;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VidasClientState {
    // Estado local para el HUD propio del jugador (100% idéntico y funcional como antes)
    private static int vidasRestantes = 3;
    private static int vidasMaximas = 3;
    private static boolean sincronizado = false;

    // Estado para indicadores overhead de otros jugadores
    private static final Map<UUID, Integer> VIDAS_POR_JUGADOR = new ConcurrentHashMap<>();

    public static final String GLYPH_CORAZON_LLENO = "\uE001";
    public static final String GLYPH_CORAZON_VACIO = "\uE002";

    public static void actualizar(int restantes, int maximas) {
        vidasRestantes = Math.max(0, restantes);
        vidasMaximas = Math.max(1, maximas);
        sincronizado = true;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            VIDAS_POR_JUGADOR.put(mc.player.getUUID(), vidasRestantes);
        }
    }

    public static void actualizarOtroJugador(UUID uuid, int restantes, int maximas) {
        if (uuid == null) return;
        VIDAS_POR_JUGADOR.put(uuid, Math.max(0, restantes));
        vidasMaximas = Math.max(vidasMaximas, maximas);
    }

    public static int getVidasRestantes() {
        return vidasRestantes;
    }

    public static int getVidasMaximas() {
        return vidasMaximas;
    }

    public static boolean isSincronizado() {
        return sincronizado;
    }

    public static void reset() {
        vidasRestantes = 3;
        vidasMaximas = 3;
        sincronizado = false;
        VIDAS_POR_JUGADOR.clear();
    }

    public static int getVidas(UUID uuid) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getUUID().equals(uuid)) {
            return vidasRestantes;
        }
        return VIDAS_POR_JUGADOR.getOrDefault(uuid, vidasMaximas);
    }

    public static Component crearComponenteVidas(UUID uuid) {
        int restantes = getVidas(uuid);
        int maximas = vidasMaximas;

        Style style = Style.EMPTY
                .withColor(0xFFFFFFFF)
                .withFont(new FontDescription.Resource(Ruleta_Hardcore.id("corazones")));

        StringBuilder sb = new StringBuilder();
        if (maximas <= 10) {
            for (int i = 0; i < maximas; i++) {
                if (i < restantes) {
                    sb.append(GLYPH_CORAZON_LLENO);
                } else {
                    sb.append(GLYPH_CORAZON_VACIO);
                }
            }
        } else {
            sb.append(GLYPH_CORAZON_LLENO).append(" ").append(restantes).append(" / ").append(maximas);
        }

        return Component.literal(sb.toString()).withStyle(style);
    }
}
