package com.luishugomod01.client;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.client.bloodmoon.BloodMoonClientState;
import com.luishugomod01.client.gui.AdminConfigScreen;
import com.luishugomod01.client.gui.FantasmaMuerteHudOverlay;
import com.luishugomod01.client.gui.RuletaEstadoScreen;
import com.luishugomod01.client.gui.RuletaHudOverlay;
import com.luishugomod01.client.vidas.VidasClientState;
import com.luishugomod01.network.AbrirMenuAdminPayload;
import com.luishugomod01.network.AbrirMenuEstadoPayload;
import com.luishugomod01.network.BloodMoonSyncPayload;
import com.luishugomod01.network.MuerteFantasmaPayload;
import com.luishugomod01.network.RuletaAnimacionPayload;
import com.luishugomod01.network.VidasJugadorPayload;
import com.luishugomod01.network.VidasSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

public class Ruleta_HardcoreClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudElementRegistry.addLast(
                Ruleta_Hardcore.id("ruleta_hud"),
                new RuletaHudOverlay()
        );

        HudElementRegistry.addLast(
                Ruleta_Hardcore.id("fantasma_muerte_hud"),
                new FantasmaMuerteHudOverlay()
        );

        ClientPlayNetworking.registerGlobalReceiver(MuerteFantasmaPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                FantasmaMuerteHudOverlay.iniciarAnimacion(payload);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(RuletaAnimacionPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                RuletaHudOverlay.iniciarAnimacion(payload);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(BloodMoonSyncPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                BloodMoonClientState.updateFromPayload(
                        payload.active(),
                        payload.eclipseActive(),
                        payload.contaminacionActive(),
                        payload.cuevasTenebrosasActive(),
                        payload.maresAcidosActive(),
                        payload.gritoPrimalActive(),
                        payload.sombraParpadeanteActive(),
                        payload.sorderaTicks()
                );
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(VidasSyncPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                VidasClientState.actualizar(payload.vidasRestantes(), payload.vidasMaximas());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(VidasJugadorPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                VidasClientState.actualizarOtroJugador(payload.playerUuid(), payload.vidasRestantes(), payload.vidasMaximas());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AbrirMenuAdminPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                context.client().setScreenAndShow(new AdminConfigScreen(payload.ruletaAutomatica(), payload.vidasIniciales()));
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AbrirMenuEstadoPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                context.client().setScreenAndShow(new RuletaEstadoScreen(payload.maldiciones(), payload.bloodMoon(), payload.eclipse()));
            });
        });

        ClientTickEvents.END_CLIENT_TICK.register(BloodMoonClientState::tick);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            BloodMoonClientState.reset();
            VidasClientState.reset();
        });
    }
}
