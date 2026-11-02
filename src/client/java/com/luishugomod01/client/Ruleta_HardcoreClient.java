package com.luishugomod01.client;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.client.gui.RuletaHudOverlay;
import com.luishugomod01.network.RuletaAnimacionPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

public class Ruleta_HardcoreClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudElementRegistry.addLast(
                Ruleta_Hardcore.id("ruleta_hud"),
                new RuletaHudOverlay()
        );

        ClientPlayNetworking.registerGlobalReceiver(RuletaAnimacionPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                RuletaHudOverlay.iniciarAnimacion(payload);
            });
        });
    }
}
