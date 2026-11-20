package com.luishugomod01.client.ritual;

import com.luishugomod01.client.gui.EventCinematicOverlay;
import com.luishugomod01.network.RitualRevivirPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Orquestador cliente del Ritual de Revivir Celestial y del sistema global de Screen Shake.
 *
 * Coreografía de 11.5 segundos (11,500 ms):
 * - 0.0s .. 2.2s: Apertura de la Grieta Celestial 3D en el cielo (Y + 32) y cielo dorado.
 * - 2.2s .. 6.0s: El jugador real (con su propia skin) desciende desde la grieta aleteando con sus
 *                 4 alas celestiales + halo giratorio, envuelto en el pilar de luz y anillos giratorios.
 * - 6.0s .. 7.0s: Frenado de aterrizaje: el jugador endereza el cuerpo/piernas y da un aletazo potente
 *                 de frenado que levanta un anillo de polvo dorado del suelo.
 * - 7.0s (140 ticks): Aterrizaje exacto en (x, y, z)! El servidor spawnea/revive al jugador real en Survival,
 *                 las cadenas estallan, onda expansiva, screen shake y título global "<Jugador> HA REGRESADO".
 * - 7.0s .. 8.8s: Las alas y el halo permanecen brevemente en la espalda del jugador real y se disuelven en partículas.
 * - 9.4s .. 11.5s: Cierre de la grieta celestial y retorno suave de la cámara a primera persona.
 */
public final class ReviveRitualClientManager {

    public static final long TOTAL_DURATION_MS = 11500L;
    public static final long PHASE_DESCENT_START_MS = 2200L;
    public static final long PHASE_BRAKE_START_MS = 6000L;
    public static final long CHAIN_SHATTER_MS = 7000L;
    public static final long WINGS_FADE_END_MS = 8800L;
    public static final long PHASE_FADE_START_MS = 9400L;
    public static final double RIFT_HEIGHT_OFFSET = 32.0;

    public static final class ActiveRitual {
        public final UUID playerUuid;
        public final String playerName;
        public final String dimensionId;
        public final double x;
        public final double y;
        public final double z;
        public final long startTimeMs;
        public final float baseYawDeg;

        public boolean playedDescentSound = false;
        public boolean playedBrakeSound = false;
        public boolean playedClimaxEffects = false;
        public boolean playedCloseSound = false;
        public long lastWingFlapSoundMs = -1L;

        ActiveRitual(RitualRevivirPayload payload, float baseYawDeg) {
            this.playerUuid = payload.jugadorUuid();
            this.playerName = payload.nombreJugador();
            this.dimensionId = payload.dimensionId();
            this.x = payload.x();
            this.y = payload.y();
            this.z = payload.z();
            this.startTimeMs = System.currentTimeMillis();
            this.baseYawDeg = baseYawDeg;
        }

        public long elapsedMs(long now) {
            return Math.max(0L, now - startTimeMs);
        }

        public boolean isExpired(long now) {
            return elapsedMs(now) >= TOTAL_DURATION_MS;
        }

        /** Altura Y actual del jugador descendiendo desde la grieta hasta aterrizar exactamente en y a los 7.0s. */
        public double getPlayerWorldY(long elapsed) {
            double topY = y + RIFT_HEIGHT_OFFSET - 1.5;
            if (elapsed <= PHASE_DESCENT_START_MS) {
                return topY;
            }
            if (elapsed >= CHAIN_SHATTER_MS) {
                return y;
            }
            float t = (float) (elapsed - PHASE_DESCENT_START_MS) / (float) (CHAIN_SHATTER_MS - PHASE_DESCENT_START_MS);
            // Curva suave con desaceleración progresiva en el último segundo (frenado de aterrizaje)
            float eased = easeInOutCubic(t);
            return Mth.lerp(eased, topY, y);
        }

        /** Factor 0..1 de visibilidad del doble animado del jugador (solo durante el descenso 2.2s..7.0s). */
        public float getDescendingPlayerAlpha(long elapsed) {
            if (elapsed < PHASE_DESCENT_START_MS || elapsed >= CHAIN_SHATTER_MS) {
                return 0.0f;
            }
            if (elapsed < PHASE_DESCENT_START_MS + 550L) {
                return easeOutCubic((float) (elapsed - PHASE_DESCENT_START_MS) / 550.0f);
            }
            return 1.0f;
        }

        /** Progreso 0..1 del frenado de aterrizaje (6.0s..7.0s). */
        public float getLandingBrakeProgress(long elapsed) {
            if (elapsed <= PHASE_BRAKE_START_MS) {
                return 0.0f;
            }
            if (elapsed >= CHAIN_SHATTER_MS) {
                return 1.0f;
            }
            float t = (float) (elapsed - PHASE_BRAKE_START_MS) / (float) (CHAIN_SHATTER_MS - PHASE_BRAKE_START_MS);
            return easeOutCubic(t);
        }

        /** Factor 0..1 de visibilidad de las alas/halo tras aterrizar (7.0s..8.8s: 1 -> 0). */
        public float getPostLandingWingsAlpha(long elapsed) {
            if (elapsed < CHAIN_SHATTER_MS || elapsed >= WINGS_FADE_END_MS) {
                return 0.0f;
            }
            float t = (float) (elapsed - CHAIN_SHATTER_MS) / (float) (WINGS_FADE_END_MS - CHAIN_SHATTER_MS);
            return Mth.clamp(1.0f - t * t, 0.0f, 1.0f);
        }

        /** Factor 0..1 de escala de la grieta en el cielo. */
        public float getRiftScaleFactor(long elapsed) {
            if (elapsed < 1800L) {
                return easeOutCubic((float) elapsed / 1800.0f);
            }
            if (elapsed < PHASE_FADE_START_MS) {
                return 1.0f + 0.035f * Mth.sin(elapsed * 0.005f);
            }
            float t = (float) (elapsed - PHASE_FADE_START_MS) / (float) (TOTAL_DURATION_MS - PHASE_FADE_START_MS);
            return Mth.clamp(1.0f - easeInOutCubic(t), 0.0f, 1.0f);
        }

        /** Factor 0..1 del cambio temporal de cielo. */
        public float getSkyFactor(long elapsed) {
            if (elapsed < 1500L) {
                return easeOutCubic((float) elapsed / 1500.0f);
            }
            if (elapsed < PHASE_FADE_START_MS) {
                return 1.0f;
            }
            float t = (float) (elapsed - PHASE_FADE_START_MS) / (float) (TOTAL_DURATION_MS - PHASE_FADE_START_MS);
            return Mth.clamp(1.0f - easeInOutCubic(t), 0.0f, 1.0f);
        }
    }

    public record CameraPose(Vec3 position, float yRot, float xRot, float fovDelta) {}

    private static final List<ActiveRitual> ACTIVE_RITUALS = new ArrayList<>();

    // Sistema global de Screen Shake (usado por Blood Moon, Eclipse y Ritual de Revivir)
    private static long shakeStartMs = 0L;
    private static long shakeDurationMs = 0L;
    private static float shakeStrength = 0.0f;

    private ReviveRitualClientManager() {}

    public static void iniciarRitual(RitualRevivirPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        float baseYaw = 0.0f;
        boolean isLocalTarget = false;
        if (mc != null && mc.player != null) {
            isLocalTarget = mc.player.getUUID().equals(payload.jugadorUuid());
            if (isLocalTarget) {
                baseYaw = mc.player.getYRot();
            } else {
                double dx = mc.player.getX() - payload.x();
                double dz = mc.player.getZ() - payload.z();
                baseYaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0f;
            }
        }

        ACTIVE_RITUALS.removeIf(r -> r.playerUuid.equals(payload.jugadorUuid()));
        ActiveRitual ritual = new ActiveRitual(payload, baseYaw);
        ACTIVE_RITUALS.add(ritual);

        // Sonido inicial de apertura de grieta en el cielo
        if (mc != null && mc.getSoundManager() != null) {
            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BEACON_ACTIVATE, 0.85f, 0.9f));
            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.AMETHYST_BLOCK_CHIME, 1.15f, 0.95f));
            mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.60f, 0.45f));
        }

        if (isLocalTarget) {
            triggerShake(1.35f, 750L);
            EventCinematicOverlay.triggerFlash(0xFFF2B2, 0.28f, 600L);
        }
    }

    /**
     * Oculta visualmente al jugador real en el suelo mientras dura el descenso (0..7.0s)
     * para evitar que se vea un duplicado abajo antes de aterrizar.
     */
    public static boolean shouldHideRealPlayer(UUID uuid) {
        if (uuid == null || ACTIVE_RITUALS.isEmpty()) {
            return false;
        }
        long now = System.currentTimeMillis();
        for (ActiveRitual r : ACTIVE_RITUALS) {
            if (r.playerUuid.equals(uuid) && !r.isExpired(now)) {
                return r.elapsedMs(now) < CHAIN_SHATTER_MS;
            }
        }
        return false;
    }

    public static void triggerShake(float strengthDeg, long durationMs) {
        long now = System.currentTimeMillis();
        float currentRemaining = getCurrentShakeAmplitude(now);
        if (strengthDeg >= currentRemaining) {
            shakeStartMs = now;
            shakeDurationMs = Math.max(80L, durationMs);
            shakeStrength = Mth.clamp(strengthDeg, 0.0f, 6.0f);
        }
    }

    public static float getCurrentShakeAmplitude(long now) {
        if (shakeDurationMs <= 0L) {
            return 0.0f;
        }
        long elapsed = now - shakeStartMs;
        if (elapsed < 0L || elapsed >= shakeDurationMs) {
            shakeDurationMs = 0L;
            return 0.0f;
        }
        float t = (float) elapsed / (float) shakeDurationMs;
        return shakeStrength * (1.0f - t) * (1.0f - t);
    }

    public static float getShakeYawOffset() {
        long now = System.currentTimeMillis();
        float amp = getCurrentShakeAmplitude(now);
        if (amp <= 0.001f) return 0.0f;
        return Mth.sin(now * 0.073f) * amp + Mth.cos(now * 0.131f) * amp * 0.45f;
    }

    public static float getShakePitchOffset() {
        long now = System.currentTimeMillis();
        float amp = getCurrentShakeAmplitude(now);
        if (amp <= 0.001f) return 0.0f;
        return Mth.cos(now * 0.089f) * amp * 0.75f + Mth.sin(now * 0.157f) * amp * 0.35f;
    }

    public static List<ActiveRitual> getActiveRituals() {
        return ACTIVE_RITUALS;
    }

    public static ActiveRitual getLocalCinematicRitual() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.level == null) {
            return null;
        }
        long now = System.currentTimeMillis();
        String currentDim = mc.level.dimension().identifier().toString();
        UUID localId = mc.player.getUUID();
        for (ActiveRitual r : ACTIVE_RITUALS) {
            if (!r.isExpired(now) && r.playerUuid.equals(localId) && r.dimensionId.equals(currentDim)) {
                return r;
            }
        }
        return null;
    }

    public static boolean isLocalPlayerInCinematic() {
        return getLocalCinematicRitual() != null;
    }

    /** Progreso 0..1 de las barras negras de película mientras el jugador local está viendo la cinemática. */
    public static float getCinematicLetterboxProgress() {
        ActiveRitual r = getLocalCinematicRitual();
        if (r == null) {
            return 0.0f;
        }
        long elapsed = r.elapsedMs(System.currentTimeMillis());
        float inT = Mth.clamp((float) elapsed / 700.0f, 0.0f, 1.0f);
        float outT = Mth.clamp((float) (TOTAL_DURATION_MS - elapsed) / 900.0f, 0.0f, 1.0f);
        return easeOutCubic(inT) * easeOutCubic(outT);
    }

    /** Intensidad 0..1 del cambio temporal del cielo en la dimensión actual. */
    public static float getSkyIntensity() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null) {
            return 0.0f;
        }
        long now = System.currentTimeMillis();
        String dim = mc.level.dimension().identifier().toString();
        float max = 0.0f;
        for (ActiveRitual r : ACTIVE_RITUALS) {
            if (!r.isExpired(now) && r.dimensionId.equals(dim)) {
                max = Math.max(max, r.getSkyFactor(r.elapsedMs(now)));
            }
        }
        return max;
    }

    /**
     * Calcula la pose de cámara cinematográfica para el jugador que está reviviendo,
     * manteniendo la trayectoria aprobada y regresando con suavidad a primera persona al final.
     */
    public static CameraPose computeCinematicCamera(LocalPlayer player, float partialTick) {
        ActiveRitual r = getLocalCinematicRitual();
        if (r == null) {
            return null;
        }
        long elapsed = r.elapsedMs(System.currentTimeMillis());

        float orbitAngleDeg = r.baseYawDeg + 90.0f;
        float extraOrbit;
        double radius;
        double camHeight;
        double lookY;
        float fovDelta;

        if (elapsed < PHASE_DESCENT_START_MS) {
            // Fase 1: plano contrapicado mirando hacia la grieta en el cielo mientras se abre
            float t = (float) elapsed / (float) PHASE_DESCENT_START_MS;
            float e = easeInOutCubic(t);
            extraOrbit = Mth.lerp(e, -26.0f, -8.0f);
            radius = Mth.lerp(e, 6.4, 7.6);
            camHeight = Mth.lerp(e, 1.5, 2.3);
            lookY = Mth.lerp(e, r.y + RIFT_HEIGHT_OFFSET - 4.0, r.y + RIFT_HEIGHT_OFFSET - 0.5);
            fovDelta = Mth.lerp(e, 0.0f, -8.5f);
        } else if (elapsed < CHAIN_SHATTER_MS) {
            // Fase 2: tracking shot siguiendo al jugador con sus alas mientras desciende del cielo a la tierra
            float t = (float) (elapsed - PHASE_DESCENT_START_MS) / (float) (CHAIN_SHATTER_MS - PHASE_DESCENT_START_MS);
            float e = easeInOutCubic(t);
            extraOrbit = Mth.lerp(e, -8.0f, 16.0f);
            radius = Mth.lerp(e, 7.6, 8.2);
            camHeight = Mth.lerp(e, 2.3, 3.0);
            double playerY = r.getPlayerWorldY(elapsed);
            lookY = Mth.lerp(e, playerY + 1.1, r.y + 1.45);
            fovDelta = Mth.lerp(e, -8.5f, -3.0f);
        } else if (elapsed < PHASE_FADE_START_MS) {
            // Fase 3: plano heroico tras el aterrizaje (ruptura de cadenas + alas disolviéndose + título global)
            float t = (float) (elapsed - CHAIN_SHATTER_MS) / (float) (PHASE_FADE_START_MS - CHAIN_SHATTER_MS);
            float e = easeOutCubic(t);
            extraOrbit = Mth.lerp(e, 16.0f, 34.0f);
            radius = Mth.lerp(e, 8.2, 10.2);
            camHeight = Mth.lerp(e, 3.0, 3.8);
            lookY = r.y + 1.45;
            fovDelta = Mth.lerp(e, -3.0f, 5.0f);
        } else {
            // Fase 4: retorno suave hacia los ojos en primera persona del jugador
            float t = (float) (elapsed - PHASE_FADE_START_MS) / (float) (TOTAL_DURATION_MS - PHASE_FADE_START_MS);
            float e = easeInOutCubic(t);
            extraOrbit = Mth.lerp(e, 34.0f, 42.0f);
            radius = 10.2;
            camHeight = 3.8;
            lookY = r.y + 1.45;
            fovDelta = Mth.lerp(e, 5.0f, 0.0f);

            double rad = Math.toRadians(orbitAngleDeg + extraOrbit);
            Vec3 desiredCinePos = new Vec3(
                    r.x + Math.cos(rad) * radius,
                    r.y + camHeight,
                    r.z + Math.sin(rad) * radius
            );
            Vec3 cinePos = clipCameraToWorld(player, new Vec3(r.x, r.y + 1.8, r.z), desiredCinePos);
            float[] cineRot = computeLookAt(cinePos, new Vec3(r.x, lookY, r.z));

            Vec3 eyePos = player.getEyePosition(partialTick);
            Vec3 blendedPos = cinePos.lerp(eyePos, e);
            float targetYaw = player.getViewYRot(partialTick);
            float targetPitch = player.getViewXRot(partialTick);
            float blendedYaw = Mth.rotLerp(e, cineRot[0], targetYaw);
            float blendedPitch = Mth.lerp(e, cineRot[1], targetPitch);
            return new CameraPose(blendedPos, blendedYaw, blendedPitch, fovDelta);
        }

        double rad = Math.toRadians(orbitAngleDeg + extraOrbit);
        Vec3 desiredCamPos = new Vec3(
                r.x + Math.cos(rad) * radius,
                r.y + camHeight,
                r.z + Math.sin(rad) * radius
        );
        Vec3 camPos = clipCameraToWorld(player, new Vec3(r.x, r.y + 1.8, r.z), desiredCamPos);
        float[] rot = computeLookAt(camPos, new Vec3(r.x, lookY, r.z));
        return new CameraPose(camPos, rot[0], rot[1], fovDelta);
    }

    private static Vec3 clipCameraToWorld(LocalPlayer player, Vec3 anchor, Vec3 desired) {
        if (player.level() == null) {
            return desired;
        }
        HitResult hit = player.level().clip(new ClipContext(
                anchor,
                desired,
                ClipContext.Block.VISUAL,
                ClipContext.Fluid.NONE,
                player
        ));
        if (hit.getType() == HitResult.Type.BLOCK) {
            Vec3 hitPos = hit.getLocation();
            Vec3 dir = desired.subtract(anchor);
            double len = dir.length();
            if (len > 0.5) {
                double safeDist = Math.max(1.8, anchor.distanceTo(hitPos) - 0.45);
                return anchor.add(dir.scale(safeDist / len));
            }
        }
        return desired;
    }

    private static float[] computeLookAt(Vec3 from, Vec3 to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        float yRot = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0f;
        float xRot = (float) -(Mth.atan2(dy, horiz) * Mth.RAD_TO_DEG);
        return new float[]{yRot, Mth.clamp(xRot, -85.0f, 85.0f)};
    }

    public static void tick(Minecraft client) {
        if (ACTIVE_RITUALS.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        ClientLevel level = client.level;
        LocalPlayer localPlayer = client.player;
        String currentDim = (level != null) ? level.dimension().identifier().toString() : "";

        Iterator<ActiveRitual> it = ACTIVE_RITUALS.iterator();
        while (it.hasNext()) {
            ActiveRitual r = it.next();
            long elapsed = r.elapsedMs(now);
            if (elapsed >= TOTAL_DURATION_MS) {
                it.remove();
                continue;
            }

            boolean isLocalTarget = localPlayer != null && localPlayer.getUUID().equals(r.playerUuid);
            boolean sameDim = level != null && r.dimensionId.equals(currentDim);

            // Disparo de audio de inicio de descenso
            if (!r.playedDescentSound && elapsed >= PHASE_DESCENT_START_MS) {
                r.playedDescentSound = true;
                if (sameDim) {
                    level.playLocalSound(
                            r.x, r.y + 8.0, r.z,
                            SoundEvents.ENCHANTMENT_TABLE_USE,
                            SoundSource.AMBIENT,
                            1.6f,
                            0.95f,
                            false
                    );
                }
            }

            // Aleteo suave y grave sincronizado cada 1.6s durante el descenso
            if (sameDim && elapsed >= PHASE_DESCENT_START_MS && elapsed < PHASE_BRAKE_START_MS) {
                if (r.lastWingFlapSoundMs < 0L || (elapsed - r.lastWingFlapSoundMs) >= 1600L) {
                    r.lastWingFlapSoundMs = elapsed;
                    level.playLocalSound(
                            r.x, r.getPlayerWorldY(elapsed), r.z,
                            SoundEvents.PHANTOM_FLAP,
                            SoundSource.AMBIENT,
                            0.72f,
                            0.58f,
                            false
                    );
                }
            }

            // Aletazo sutil de frenado al acercarse al suelo (6.0s)
            if (!r.playedBrakeSound && elapsed >= PHASE_BRAKE_START_MS) {
                r.playedBrakeSound = true;
                if (sameDim) {
                    level.playLocalSound(
                            r.x, r.y + 2.0, r.z,
                            SoundEvents.PHANTOM_FLAP,
                            SoundSource.AMBIENT,
                            1.15f,
                            0.72f,
                            false
                    );
                }
            }

            // Clímax / Aterrizaje (7.0s): ruptura de cadenas, explosión de luz y título global "<Jugador> HA REGRESADO"
            if (!r.playedClimaxEffects && elapsed >= CHAIN_SHATTER_MS) {
                r.playedClimaxEffects = true;
                EventCinematicOverlay.mostrarRevivirGlobal(r.playerName, isLocalTarget && sameDim);
                if (isLocalTarget || (sameDim && localPlayer != null && localPlayer.distanceToSqr(r.x, r.y, r.z) < 6400.0)) {
                    triggerShake(isLocalTarget ? 3.2f : 1.6f, 950L);
                }
                if (client.getSoundManager() != null) {
                    client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.TOTEM_USE, 1.05f, 0.85f));
                    client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BEACON_POWER_SELECT, 1.25f, 0.9f));
                }
                if (sameDim) {
                    spawnClimaxShockwaveParticles(level, r);
                }
            }

            // Sonido de cierre de la grieta
            if (!r.playedCloseSound && elapsed >= PHASE_FADE_START_MS) {
                r.playedCloseSound = true;
                if (sameDim) {
                    level.playLocalSound(
                            r.x, r.y + 12.0, r.z,
                            SoundEvents.BEACON_DEACTIVATE,
                            SoundSource.AMBIENT,
                            1.2f,
                            1.1f,
                            false
                    );
                }
            }

            // Partículas ambientales del ritual en el mundo
            if (sameDim && !client.isPaused()) {
                spawnRitualParticles(level, r, elapsed);
            }
        }
    }

    private static void spawnRitualParticles(ClientLevel level, ActiveRitual r, long elapsed) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        // 1. Lluvia de partículas doradas descendiendo desde la Grieta en el cielo
        if (elapsed < PHASE_FADE_START_MS + 1000L) {
            for (int i = 0; i < 6; i++) {
                double ang = rng.nextDouble() * Math.PI * 2.0;
                double rad = rng.nextDouble() * 4.5;
                double px = r.x + Math.cos(ang) * rad;
                double py = r.y + RIFT_HEIGHT_OFFSET - rng.nextDouble() * 2.5;
                double pz = r.z + Math.sin(ang) * rad;
                var type = rng.nextBoolean() ? ParticleTypes.END_ROD : ParticleTypes.WAX_ON;
                level.addParticle(type, px, py, pz, (rng.nextDouble() - 0.5) * 0.06, -0.14 - rng.nextDouble() * 0.10, (rng.nextDouble() - 0.5) * 0.06);
            }
        }

        // 2. Doble estela brillante de luz siguiendo las puntas de las alas (izquierda y derecha) durante el descenso (2.2s..7.0s)
        if (elapsed >= PHASE_DESCENT_START_MS && elapsed < CHAIN_SHATTER_MS) {
            double playerY = r.getPlayerWorldY(elapsed);
            double flightSec = (elapsed - PHASE_DESCENT_START_MS) * 0.001;
            double flap = Math.sin(flightSec * (Math.PI * 2.0 / 1.6));
            // Vector lateral (alas izq/der) y ligero desplazamiento hacia atrás respecto a la cámara
            double yawRad = Math.toRadians(r.baseYawDeg + 90.0);
            double backX = -Math.cos(yawRad) * 0.45;
            double backZ = -Math.sin(yawRad) * 0.45;
            double sideX = -Math.sin(yawRad);
            double sideZ = Math.cos(yawRad);

            for (int side = -1; side <= 1; side += 2) {
                double span = 1.35 + rng.nextDouble() * 0.85;
                double px = r.x + backX + sideX * side * span;
                double py = playerY + 1.15 + flap * 0.42 + (rng.nextDouble() - 0.5) * 0.25;
                double pz = r.z + backZ + sideZ * side * span;
                var type = rng.nextInt(3) == 0 ? ParticleTypes.WAX_ON : ParticleTypes.END_ROD;
                level.addParticle(type, px, py, pz, sideX * side * 0.025, 0.05 + rng.nextDouble() * 0.05, sideZ * side * 0.025);
            }
        }

        // 2b. Anillo de polvo dorado levantado del suelo durante el frenado de aterrizaje (6.0s..7.0s)
        if (elapsed >= PHASE_BRAKE_START_MS && elapsed < CHAIN_SHATTER_MS) {
            for (int i = 0; i < 6; i++) {
                double ang = rng.nextDouble() * Math.PI * 2.0;
                double rad = 0.8 + rng.nextDouble() * 1.6;
                double px = r.x + Math.cos(ang) * rad;
                double pz = r.z + Math.sin(ang) * rad;
                double vx = Math.cos(ang) * (0.09 + rng.nextDouble() * 0.08);
                double vz = Math.sin(ang) * (0.09 + rng.nextDouble() * 0.08);
                level.addParticle(ParticleTypes.WAX_ON, px, r.y + 0.15, pz, vx, 0.05 + rng.nextDouble() * 0.06, vz);
            }
        }

        // 2c. Partículas de disolución de las alas tras aterrizar (7.0s..8.8s)
        if (elapsed >= CHAIN_SHATTER_MS && elapsed < WINGS_FADE_END_MS) {
            for (int i = 0; i < 4; i++) {
                double ang = rng.nextDouble() * Math.PI * 2.0;
                double rad = 0.6 + rng.nextDouble() * 1.8;
                double px = r.x + Math.cos(ang) * rad;
                double py = r.y + 0.8 + rng.nextDouble() * 1.4;
                double pz = r.z + Math.sin(ang) * rad;
                level.addParticle(ParticleTypes.END_ROD, px, py, pz, (rng.nextDouble() - 0.5) * 0.04, 0.07 + rng.nextDouble() * 0.05, (rng.nextDouble() - 0.5) * 0.04);
            }
        }

        // 3. Espiral ascendente alrededor del punto de aterrizaje y los anillos
        if (elapsed >= 1200L && elapsed < PHASE_FADE_START_MS + 600L) {
            double spiralAngle = (elapsed * 0.008);
            for (int arm = 0; arm < 2; arm++) {
                double a = spiralAngle + arm * Math.PI;
                double rad = 2.1 + 0.3 * Math.sin(elapsed * 0.004);
                double px = r.x + Math.cos(a) * rad;
                double py = r.y + 0.3 + ((elapsed * 0.004 + arm * 2.0) % 4.5);
                double pz = r.z + Math.sin(a) * rad;
                level.addParticle(ParticleTypes.ENCHANT, px, py, pz, 0.0, 0.09, 0.0);
            }
        }
    }

    private static void spawnClimaxShockwaveParticles(ClientLevel level, ActiveRitual r) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        int count = 88;
        for (int i = 0; i < count; i++) {
            double ang = (Math.PI * 2.0 * i) / count + rng.nextDouble() * 0.1;
            double speed = 0.25 + rng.nextDouble() * 0.38;
            double vx = Math.cos(ang) * speed;
            double vz = Math.sin(ang) * speed;
            double vy = 0.08 + rng.nextDouble() * 0.35;
            level.addParticle(ParticleTypes.TOTEM_OF_UNDYING, r.x, r.y + 1.0, r.z, vx, vy, vz);
            if (i % 2 == 0) {
                level.addParticle(ParticleTypes.END_ROD, r.x, r.y + 1.6, r.z, vx * 0.7, vy + 0.18, vz * 0.7);
            }
        }
    }

    public static void reset() {
        ACTIVE_RITUALS.clear();
        shakeDurationMs = 0L;
        shakeStrength = 0.0f;
    }

    private static float easeOutCubic(float t) {
        float u = 1.0f - Mth.clamp(t, 0.0f, 1.0f);
        return 1.0f - u * u * u;
    }

    private static float easeInOutCubic(float t) {
        float c = Mth.clamp(t, 0.0f, 1.0f);
        return c < 0.5f ? 4.0f * c * c * c : 1.0f - (float) Math.pow(-2.0f * c + 2.0f, 3.0) * 0.5f;
    }
}
