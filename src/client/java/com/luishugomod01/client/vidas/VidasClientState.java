package com.luishugomod01.client.vidas;

public class VidasClientState {
    private static int vidasRestantes = 3;
    private static int vidasMaximas = 3;
    private static boolean sincronizado = false;

    public static void actualizar(int restantes, int maximas) {
        vidasRestantes = Math.max(0, restantes);
        vidasMaximas = Math.max(1, maximas);
        sincronizado = true;
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
    }
}
