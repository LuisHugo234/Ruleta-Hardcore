package com.luishugomod01.client.bloodmoon;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.level.Level;

public final class BloodMoonClientState {

    private static volatile boolean serverBloodMoonActive = false;
    private static volatile boolean serverEclipseActive = false;
    private static volatile boolean serverContaminacionActive = false;
    private static volatile boolean serverCuevasTenebrosasActive = false;
    private static volatile boolean serverMaresAcidosActive = false;
    private static volatile boolean serverGritoPrimalActive = false;
    private static volatile boolean serverSombraParpadeanteActive = false;
    private static volatile boolean serverReyDeLoProfundoActive = false;
    private static volatile int sorderaTicksRemaining = 0;
    private static volatile boolean silencioSepulcralActive = false;
    private static boolean fadeOutLatch = false;
    private static boolean eclipseFadeOutLatch = false;

    private static float prevBloodMoonIntensity = 0.0f;
    private static float bloodMoonIntensity = 0.0f;
    private static float currentRenderIntensity = 0.0f;

    private static float prevEclipseIntensity = 0.0f;
    private static float eclipseIntensity = 0.0f;
    private static float currentEclipseRenderIntensity = 0.0f;

    private static double smoothWorldTime = 0.0;

    private static long lastAnnouncedDay = -1L;
    private static long lastAnnouncedEclipseDay = -1L;

    private BloodMoonClientState() {}

    public static void updateFromPayload(
            boolean active,
            boolean eclipseActive,
            boolean contaminacionActive,
            boolean cuevasTenebrosasActive,
            boolean maresAcidosActive,
            boolean gritoPrimalActive,
            boolean sombraParpadeanteActive,
            boolean reyDeLoProfundoActive,
            int sorderaTicks
    ) {
        setServerActive(active);
        setServerEclipseActive(eclipseActive);
        serverContaminacionActive = contaminacionActive;
        serverCuevasTenebrosasActive = cuevasTenebrosasActive;
        serverMaresAcidosActive = maresAcidosActive;
        serverGritoPrimalActive = gritoPrimalActive;
        serverSombraParpadeanteActive = sombraParpadeanteActive;
        serverReyDeLoProfundoActive = reyDeLoProfundoActive;
        if (sorderaTicks > 0) {
            sorderaTicksRemaining = Math.max(sorderaTicksRemaining, sorderaTicks);
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.getSoundManager() != null) {
                mc.getSoundManager().stop();
            }
        }
    }

    public static void setServerEclipseActive(boolean active) {
        if (serverEclipseActive && !active) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null && mc.level.dimension().equals(Level.OVERWORLD)) {
                long dayTime = ((mc.level.getOverworldClockTime() % 24000L) + 24000L) % 24000L;
                if (dayTime >= 11000L && dayTime < 12500L) {
                    eclipseFadeOutLatch = true;
                } else {
                    eclipseFadeOutLatch = false;
                }
            }
        } else if (active) {
            eclipseFadeOutLatch = false;
        }
        serverEclipseActive = active;
    }

    public static void setServerActive(boolean active) {
        if (serverBloodMoonActive && !active) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null && mc.level.dimension().equals(Level.OVERWORLD)) {
                long dayTime = ((mc.level.getOverworldClockTime() % 24000L) + 24000L) % 24000L;
                // Si el servidor desactiva en el amanecer (tick 23000), mantener el cierre suave hasta 23500
                if (dayTime >= 22500L && dayTime < 23500L) {
                    fadeOutLatch = true;
                } else {
                    fadeOutLatch = false;
                }
            }
        } else if (active) {
            fadeOutLatch = false;
        }
        serverBloodMoonActive = active;
    }

    public static boolean isServerActive() {
        return serverBloodMoonActive;
    }

    public static boolean isServerEclipseActive() {
        return serverEclipseActive;
    }

    public static boolean isContaminacionActive() {
        return serverContaminacionActive;
    }

    public static boolean isCuevasTenebrosasActive() {
        return serverCuevasTenebrosasActive;
    }

    public static boolean isMaresAcidosActive() {
        return serverMaresAcidosActive;
    }

    public static boolean isGritoPrimalActive() {
        return serverGritoPrimalActive;
    }

    public static boolean isPlayerDeafened() {
        return sorderaTicksRemaining > 0;
    }

    public static boolean isSilencioSepulcralActive() {
        return silencioSepulcralActive;
    }

    /**
     * Curva gradual diurna del Eclipse Solar (0.0f a 1.0f):
     * - Fade-in gradual entre tick 500 y 1500 a medida que la luna cubre el sol
     * - Fase máxima total (1.0f) entre tick 1500 y 11000
     * - Fade-out gradual entre tick 11000 y 12000 al ponerse el sol
     */
    private static float calcularCurvaEclipsePorTick(float exactDayTime) {
        if (exactDayTime < 500.0f || exactDayTime >= 12000.0f) {
            return 0.0f;
        }
        if (exactDayTime <= 1500.0f) {
            float t = Mth.clamp((exactDayTime - 500.0f) / 1000.0f, 0.0f, 1.0f);
            return Mth.lerp(t, 0.0f, 1.0f);
        }
        if (exactDayTime < 11000.0f) {
            return 1.0f;
        }
        float t = Mth.clamp((exactDayTime - 11000.0f) / 1000.0f, 0.0f, 1.0f);
        return Mth.lerp(t, 1.0f, 0.0f);
    }

    public static float getEclipseIntensity(float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || !mc.level.dimension().equals(Level.OVERWORLD)) {
            currentEclipseRenderIntensity = 0.0f;
            return 0.0f;
        }
        smoothWorldTime = (double) mc.level.getGameTime() + (double) tickDelta;

        float lerpedTickIntensity = Mth.lerp(tickDelta, prevEclipseIntensity, eclipseIntensity);
        if (serverEclipseActive || eclipseFadeOutLatch) {
            long clockTime = mc.level.getOverworldClockTime();
            float exactDayTime = (float) (((clockTime % 24000L) + 24000L) % 24000L) + tickDelta;
            float exactCurve = calcularCurvaEclipsePorTick(exactDayTime);
            if (Math.abs(exactCurve - lerpedTickIntensity) < 0.025f) {
                currentEclipseRenderIntensity = exactCurve;
                return exactCurve;
            }
        }
        currentEclipseRenderIntensity = lerpedTickIntensity;
        return currentEclipseRenderIntensity;
    }

    public static float getCurrentEclipseRenderIntensity() {
        return currentEclipseRenderIntensity;
    }

    public static boolean isReyDeLoProfundoActive() {
        return serverReyDeLoProfundoActive;
    }

    public static void reset() {
        serverBloodMoonActive = false;
        serverEclipseActive = false;
        serverContaminacionActive = false;
        serverCuevasTenebrosasActive = false;
        serverMaresAcidosActive = false;
        serverGritoPrimalActive = false;
        serverSombraParpadeanteActive = false;
        serverReyDeLoProfundoActive = false;
        sorderaTicksRemaining = 0;
        silencioSepulcralActive = false;
        fadeOutLatch = false;
        eclipseFadeOutLatch = false;
        prevBloodMoonIntensity = 0.0f;
        bloodMoonIntensity = 0.0f;
        currentRenderIntensity = 0.0f;
        prevEclipseIntensity = 0.0f;
        eclipseIntensity = 0.0f;
        currentEclipseRenderIntensity = 0.0f;
        lastAnnouncedDay = -1L;
        lastAnnouncedEclipseDay = -1L;
    }

    /**
     * Calcula la intensidad teórica según la curva horaria solicitada:
     * - Fade-in lineal entre 12500 y 13500
     * - 1.0f sostenido entre 13500 y 22500
     * - Fade-out lineal entre 22500 y 23500
     */
    private static float calcularCurvaPorTick(float exactDayTime) {
        if (exactDayTime < 12500.0f || exactDayTime >= 23500.0f) {
            return 0.0f;
        }
        if (exactDayTime <= 13500.0f) {
            float t = Mth.clamp((exactDayTime - 12500.0f) / 1000.0f, 0.0f, 1.0f);
            return Mth.lerp(t, 0.0f, 1.0f);
        }
        if (exactDayTime < 22500.0f) {
            return 1.0f;
        }
        float t = Mth.clamp((exactDayTime - 22500.0f) / 1000.0f, 0.0f, 1.0f);
        return Mth.lerp(t, 1.0f, 0.0f);
    }

    public static float getIntensity(float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || !mc.level.dimension().equals(Level.OVERWORLD)) {
            currentRenderIntensity = 0.0f;
            return 0.0f;
        }
        smoothWorldTime = (double) mc.level.getGameTime() + (double) tickDelta;

        float lerpedTickIntensity = Mth.lerp(tickDelta, prevBloodMoonIntensity, bloodMoonIntensity);
        if (serverBloodMoonActive || fadeOutLatch) {
            long clockTime = mc.level.getOverworldClockTime();
            float exactDayTime = (float) (((clockTime % 24000L) + 24000L) % 24000L) + tickDelta;
            float exactCurve = calcularCurvaPorTick(exactDayTime);
            // Si ya alcanzó la curva natural, usar la interpolación sub-tick exacta con tickDelta
            if (Math.abs(exactCurve - lerpedTickIntensity) < 0.02f) {
                currentRenderIntensity = exactCurve;
                return exactCurve;
            }
        }
        currentRenderIntensity = lerpedTickIntensity;
        return lerpedTickIntensity;
    }

    public static float getCurrentRenderIntensity() {
        return currentRenderIntensity;
    }

    public static double getSmoothWorldTime() {
        return smoothWorldTime;
    }

    public static void tick(Minecraft client) {
        prevBloodMoonIntensity = bloodMoonIntensity;
        prevEclipseIntensity = eclipseIntensity;

        ClientLevel level = client.level;
        LocalPlayer player = client.player;
        if (level == null || player == null) {
            bloodMoonIntensity = 0.0f;
            prevBloodMoonIntensity = 0.0f;
            currentRenderIntensity = 0.0f;
            eclipseIntensity = 0.0f;
            prevEclipseIntensity = 0.0f;
            currentEclipseRenderIntensity = 0.0f;
            silencioSepulcralActive = false;
            return;
        }

        if (!client.isPaused()) {
            if (sorderaTicksRemaining > 0) {
                sorderaTicksRemaining--;
            }
            if (serverSombraParpadeanteActive && !player.isCreative() && !player.isSpectator()) {
                var nearbyAngryEndermen = level.getEntitiesOfClass(
                        Enderman.class,
                        player.getBoundingBox().inflate(12.0),
                        e -> e.isAlive() && e.isCreepy() && e.distanceToSqr(player) <= 144.0
                );
                silencioSepulcralActive = !nearbyAngryEndermen.isEmpty();
            } else {
                silencioSepulcralActive = false;
            }
        }

        if (!level.dimension().equals(Level.OVERWORLD)) {
            bloodMoonIntensity = 0.0f;
            prevBloodMoonIntensity = 0.0f;
            currentRenderIntensity = 0.0f;
            eclipseIntensity = 0.0f;
            prevEclipseIntensity = 0.0f;
            currentEclipseRenderIntensity = 0.0f;
            return;
        }

        long clockTime = level.getOverworldClockTime();
        long dayTime = ((clockTime % 24000L) + 24000L) % 24000L;
        long dayNumber = Math.floorDiv(clockTime, 24000L);

        if (dayTime >= 23500L || dayTime < 12000L) {
            fadeOutLatch = false;
        }
        if (dayTime >= 12500L || dayTime < 500L) {
            eclipseFadeOutLatch = false;
        }

        float target = (serverBloodMoonActive || fadeOutLatch)
                ? calcularCurvaPorTick((float) dayTime)
                : 0.0f;

        // Suavizado máximo de 0.025f por tick en caso de activación manual a mitad de la noche
        float maxStep = 0.025f;
        if (Math.abs(target - bloodMoonIntensity) <= maxStep) {
            bloodMoonIntensity = target;
        } else {
            bloodMoonIntensity += Math.signum(target - bloodMoonIntensity) * maxStep;
        }
        bloodMoonIntensity = Mth.clamp(bloodMoonIntensity, 0.0f, 1.0f);

        // Intensidad del Eclipse Solar durante el día con transición gradual suave (0.0f -> 1.0f)
        float eclipseTarget = 0.0f;
        if (serverEclipseActive && dayTime < 12500L) {
            // Si el eclipse se inicia manualmente antes del tick 500, usar 1.0f; de lo contrario seguir la curva solar
            eclipseTarget = (dayTime < 500L) ? 1.0f : calcularCurvaEclipsePorTick((float) dayTime);
        } else if (eclipseFadeOutLatch) {
            eclipseTarget = calcularCurvaEclipsePorTick((float) dayTime);
        }
        float eclipseStep = 0.02f;
        if (Math.abs(eclipseTarget - eclipseIntensity) <= eclipseStep) {
            eclipseIntensity = eclipseTarget;
        } else {
            eclipseIntensity += Math.signum(eclipseTarget - eclipseIntensity) * eclipseStep;
        }
        eclipseIntensity = Mth.clamp(eclipseIntensity, 0.0f, 1.0f);

        // Al alcanzar el tick 13000: sonido grave y ominoso + título/actionbar rojo oscuro
        if (serverBloodMoonActive && dayTime >= 13000L && dayTime < 22500L && lastAnnouncedDay != dayNumber) {
            lastAnnouncedDay = dayNumber;
            reproducirInicioOminoso(client, level, player);
        }

        // Anuncio ominoso del Eclipse Solar
        if (serverEclipseActive && dayTime < 12000L && lastAnnouncedEclipseDay != dayNumber) {
            lastAnnouncedEclipseDay = dayNumber;
            reproducirInicioEclipse(client, level, player);
        }

        // Partículas flotantes cuando bloodMoonIntensity > 0.5f y el jugador está al aire libre
        if (!client.isPaused() && bloodMoonIntensity > 0.5f && estaAlAireLibre(level, player)) {
            generarParticulasAmbientales(level, player);
        }

        // Partículas de humo y esporas tóxicas para ¿Contaminación? sobre la capa Y=160
        if (!client.isPaused() && serverContaminacionActive && player.getY() > 155.0) {
            generarParticulasContaminacion(level, player);
        }
    }

    private static void reproducirInicioEclipse(Minecraft client, ClientLevel level, LocalPlayer player) {
        level.playLocalSound(
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.WITHER_SPAWN,
                SoundSource.AMBIENT,
                0.7f,
                0.5f,
                false
        );
        client.gui.hud.setTimes(15, 70, 25);
        client.gui.hud.setTitle(
                Component.literal("ECLIPSE SOLAR")
                        .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD)
        );
        client.gui.hud.setSubtitle(
                Component.literal("El sol se oscurece y los cielos cobran vida...")
                        .withStyle(ChatFormatting.LIGHT_PURPLE)
        );
        client.gui.hud.setOverlayMessage(
                Component.literal("☼ ¡Un Eclipse Solar cubre el mundo! ☼")
                        .withStyle(ChatFormatting.DARK_PURPLE),
                false
        );
    }

    private static void generarParticulasContaminacion(ClientLevel level, LocalPlayer player) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int i = 0; i < 10; i++) {
            double ox = (rng.nextDouble() * 2.0 - 1.0) * 14.0;
            double oz = (rng.nextDouble() * 2.0 - 1.0) * 14.0;
            double px = player.getX() + ox;
            double py = Math.max(160.0, player.getY() + (rng.nextDouble() * 8.0 - 2.0));
            double pz = player.getZ() + oz;
            var pType = rng.nextBoolean() ? ParticleTypes.SPORE_BLOSSOM_AIR : ParticleTypes.CAMPFIRE_COSY_SMOKE;
            level.addParticle(pType, px, py, pz, (rng.nextDouble() - 0.5) * 0.01, 0.01, (rng.nextDouble() - 0.5) * 0.01);
        }
    }

    private static void reproducirInicioOminoso(Minecraft client, ClientLevel level, LocalPlayer player) {
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        level.playLocalSound(
                x, y, z,
                SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD.value(),
                SoundSource.AMBIENT,
                1.5f,
                0.5f,
                false
        );
        level.playLocalSound(
                x, y, z,
                SoundEvents.WITHER_SPAWN,
                SoundSource.AMBIENT,
                0.55f,
                0.5f,
                false
        );
        level.playLocalSound(
                x, y, z,
                SoundEvents.LIGHTNING_BOLT_THUNDER,
                SoundSource.AMBIENT,
                0.65f,
                0.5f,
                false
        );

        client.gui.hud.setTimes(15, 70, 25);
        client.gui.hud.setTitle(
                Component.literal("LUNA DE SANGRE")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD)
        );
        client.gui.hud.setSubtitle(
                Component.literal("La noche se tiñe de sangre...")
                        .withStyle(ChatFormatting.RED)
        );
        client.gui.hud.setOverlayMessage(
                Component.literal("☠ La Luna de Sangre se alza sobre el mundo... ☠")
                        .withStyle(ChatFormatting.DARK_RED),
                false
        );
    }

    private static boolean estaAlAireLibre(ClientLevel level, LocalPlayer player) {
        BlockPos eyePos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        if (level.canSeeSky(eyePos)) return true;
        // También permitir si hay cielo abierto inmediatamente alrededor (p. ej. bajo hojas o aleros)
        for (int dx = -2; dx <= 2; dx += 2) {
            for (int dz = -2; dz <= 2; dz += 2) {
                if (level.canSeeSky(eyePos.offset(dx, 1, dz))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void generarParticulasAmbientales(ClientLevel level, LocalPlayer player) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        int cantidad = Math.round(10.0f * bloodMoonIntensity);

        for (int i = 0; i < cantidad; i++) {
            double offsetX = (rng.nextDouble() * 2.0 - 1.0) * 16.0;
            double offsetZ = (rng.nextDouble() * 2.0 - 1.0) * 16.0;
            if (offsetX * offsetX + offsetZ * offsetZ > 256.0) continue;

            double px = player.getX() + offsetX;
            double py = player.getY() + rng.nextDouble() * 10.0 - 1.0;
            double pz = player.getZ() + offsetZ;

            BlockPos samplePos = BlockPos.containing(px, py, pz);
            if (!level.canSeeSky(samplePos) || !level.getBlockState(samplePos).isAir()) {
                continue;
            }

            var particle = (rng.nextFloat() < 0.65f) ? ParticleTypes.CRIMSON_SPORE : ParticleTypes.ASH;
            double vx = (rng.nextDouble() - 0.5) * 0.01;
            double vy = -0.008 - rng.nextDouble() * 0.012;
            double vz = (rng.nextDouble() - 0.5) * 0.01;
            level.addParticle(particle, px, py, pz, vx, vy, vz);
        }
    }
}
