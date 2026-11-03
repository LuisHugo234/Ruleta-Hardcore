package com.luishugomod01.ruleta;

public enum NivelRuleta {

    NIVEL_1(1, "Nivel 1", 0.35f, 0x55FF55),
    NIVEL_2(2, "Nivel 2", 0.25f, 0xFFFF55),
    NIVEL_3(3, "Nivel 3", 0.18f, 0xFF9900),
    NIVEL_4(4, "Nivel 4", 0.12f, 0xFF5555),
    NIVEL_5(5, "Nivel 5", 0.07f, 0xFF55FF),
    NIVEL_6(6, "Nivel 6", 0.03f, 0xFF0000);

    private final int numero;
    private final String nombreVisible;
    private final float probabilidad;
    private final int colorHex;

    NivelRuleta(int numero, String nombreVisible, float probabilidad, int colorHex) {
        this.numero        = numero;
        this.nombreVisible = nombreVisible;
        this.probabilidad  = probabilidad;
        this.colorHex      = colorHex;
    }

    public int    getNumero()        { return numero; }
    public String getNombreVisible() { return nombreVisible; }
    public float  getProbabilidad()  { return probabilidad;  }
    public int    getColorHex()      { return colorHex;      }
}
