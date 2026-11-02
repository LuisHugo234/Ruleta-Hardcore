package com.luishugomod01.ruleta;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface Castigo {
    void ejecutar(ServerLevel level, ServerPlayer player);
}
