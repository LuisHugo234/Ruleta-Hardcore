package com.luishugomod01.ruleta;

public enum NivelRuleta {

    NIVEL_1("Novato",      0.35f, 0x55FF55),
    NIVEL_2("Aprendiz",   0.25f, 0xFFFF55),
    NIVEL_3("Experto",    0.18f, 0xFF9900),
    NIVEL_4("Maestro",    0.12f, 0xFF5555),
    NIVEL_5("Legendario", 0.07f, 0xFF55FF),
    NIVEL_6("Dios",       0.03f, 0xFF0000);

    private final String nombreVisible;
    private final float probabilidad;
    private final int colorHex;

    NivelRuleta(String nombreVisible, float probabilidad, int colorHex) {
        this.nombreVisible = nombreVisible;
        this.probabilidad  = probabilidad;
        this.colorHex      = colorHex;
    }

    public String getNombreVisible() { return nombreVisible; }
    public float  getProbabilidad()  { return probabilidad;  }
    public int    getColorHex()      { return colorHex;      }
}
