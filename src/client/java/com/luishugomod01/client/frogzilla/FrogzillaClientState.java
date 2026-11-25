package com.luishugomod01.client.frogzilla;

import com.luishugomod01.network.FrogzillaDevourSyncPayload;
import com.luishugomod01.network.FrogzillaEscapeTapPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

public class FrogzillaClientState {

    private static boolean devoured = false;
    private static int progressPct = 0;
    private static int ticksRemaining = 0;
    private static int frogEntityId = -1;
    private static boolean spaceWasDown = false;
    private static int pulseTicks = 0;

    public static void updateFromPayload(FrogzillaDevourSyncPayload payload) {
        boolean wasDevoured = devoured;
        devoured = payload.active();
        progressPct = Math.max(0, Math.min(100, payload.progressPct()));
        ticksRemaining = Math.max(0, payload.ticksRemaining());
        frogEntityId = payload.frogEntityId();
        if (!devoured) {
            spaceWasDown = false;
            pulseTicks = 0;
        } else if (!wasDevoured) {
            Minecraft mc = Minecraft.getInstance();
            spaceWasDown = mc != null && mc.options != null && mc.options.keyJump.isDown();
            pulseTicks = 0;
        }
    }

    public static void tick(Minecraft client) {
        if (!devoured) {
            spaceWasDown = false;
            return;
        }
        pulseTicks++;
        if (ticksRemaining > 0) {
            ticksRemaining--;
        }
        if (client == null || client.player == null || client.isPaused()) {
            return;
        }
        boolean spaceDown = client.options.keyJump.isDown();
        if (spaceDown && !spaceWasDown) {
            progressPct = Math.min(100, progressPct + 5);
            ClientPlayNetworking.send(FrogzillaEscapeTapPayload.INSTANCE);
        }
        spaceWasDown = spaceDown;
    }

    public static boolean isDevoured() {
        return devoured;
    }

    public static int getProgressPct() {
        return progressPct;
    }

    public static int getTicksRemaining() {
        return ticksRemaining;
    }

    public static int getFrogEntityId() {
        return frogEntityId;
    }

    public static int getPulseTicks() {
        return pulseTicks;
    }

    public static void reset() {
        devoured = false;
        progressPct = 0;
        ticksRemaining = 0;
        frogEntityId = -1;
        spaceWasDown = false;
        pulseTicks = 0;
    }
}
