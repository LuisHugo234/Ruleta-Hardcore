package com.luishugomod01.ruleta;

public interface CreeperCustomAccessor {
    byte TIPO_NORMAL = 0;
    byte TIPO_FANTASMA = 1;
    byte TIPO_ANTIMATERIA = 2;
    byte TIPO_COSMICO = 3;
    byte TIPO_TERMONUCLEAR = 4;
    byte TIPO_AVISPON = 5;

    void ruleta$setPowered(boolean powered);
    void ruleta$setExplosionRadius(int radius);
    void ruleta$setMaxSwell(int maxSwell);

    byte ruleta$getTipoCreeper();
    void ruleta$setTipoCreeper(byte tipo);

    int ruleta$getTemperaturaTermonuclear();
    void ruleta$setTemperaturaTermonuclear(int temperatura);
}
