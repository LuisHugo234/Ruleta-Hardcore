package com.luishugomod01.client.frogzilla;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class RanaCamaraClientState {

    private static float targetRollDegrees = 0.0F;
    private static float currentRollDegrees = 0.0F;
    private static float prevRollDegrees = 0.0F;
    private static int remainingTicks = 0;

    public static void aplicarRotacion(float degrees, int durationTicks) {
        targetRollDegrees = degrees;
        remainingTicks = Math.max(remainingTicks, durationTicks);
    }

    public static void tick(Minecraft client) {
        prevRollDegrees = currentRollDegrees;
        if (remainingTicks > 0) {
            remainingTicks--;
            if (remainingTicks == 0) {
                targetRollDegrees = 0.0F;
            }
        }
        // Transición suave pero ágil hacia el ángulo objetivo (90, 180, -90 o 0)
        float diff = targetRollDegrees - currentRollDegrees;
        if (Math.abs(diff) < 0.25F) {
            currentRollDegrees = targetRollDegrees;
        } else {
            currentRollDegrees += diff * 0.28F;
        }
    }

    public static float getInterpolatedRollDegrees(float partialTick) {
        return Mth.lerp(partialTick, prevRollDegrees, currentRollDegrees);
    }

    public static boolean hasActiveRoll() {
        return Math.abs(currentRollDegrees) > 0.05F || Math.abs(prevRollDegrees) > 0.05F;
    }

    public static void reset() {
        targetRollDegrees = 0.0F;
        currentRollDegrees = 0.0F;
        prevRollDegrees = 0.0F;
        remainingTicks = 0;
    }
}
