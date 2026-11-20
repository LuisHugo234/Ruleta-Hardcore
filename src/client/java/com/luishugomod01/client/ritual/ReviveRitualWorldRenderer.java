package com.luishugomod01.client.ritual;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.List;

/**
 * Renderizador 3D en el mundo para el Ritual de Revivir Celestial:
 * - Grieta Dimensional 3D (modelo Blockbench ritual_revivir_vfx.bbmodel) en el cielo (Y + 32)
 * - Rayos divinos descendiendo desde la grieta
 * - Pilar de luz volumétrico doble (BeaconRenderer.submitBeaconBeam)
 * - Jugador real (con su propia skin Wide/Slim y capas exteriores) descendiendo desde el cielo
 *   mientras aletea con 4 Alas Celestiales + Halo giratorio (AngelRevivirModel)
 * - Frenado de aterrizaje y disolución suave de las alas sobre el jugador real al aterrizar
 * - 3 Anillos Giratorios + 4 Cadenas 3D rompiéndose alrededor del punto de aterrizaje
 */
public final class ReviveRitualWorldRenderer {

    private static final int FULL_BRIGHT = 15728880; // 0x00F000F0

    private static AngelRevivirModel wingsHaloModel;
    private static RitualRevivirVfxModel vfxModel;
    private static DescendingPlayerModel playerModelWide;
    private static DescendingPlayerModel playerModelSlim;
    private static EntityModelSet cachedModelSet;

    private ReviveRitualWorldRenderer() {}

    /**
     * Subclase de PlayerModel que aplica una pose cinematográfica de vuelo celestial y frenado de aterrizaje
     * de forma puramente funcional leyendo ageInTicks (tiempo en seg) y fallFlyingTimeInTicks (progreso de frenado 0..1).
     */
    private static final class DescendingPlayerModel extends PlayerModel {
        DescendingPlayerModel(ModelPart root, boolean slim) {
            super(root, slim);
        }

        @Override
        public void setupAnim(AvatarRenderState state) {
            super.setupAnim(state);

            float tSec = state.ageInTicks;
            float brake = Mth.clamp(state.fallFlyingTimeInTicks, 0.0f, 1.0f);
            float flight = 1.0f - brake;

            // Ritmo sincronizado con el aleteo de 1.6s de AngelRevivirAnimations.DESCEND
            float flapWave = Mth.sin(tSec * (Mth.TWO_PI / 1.6f));
            float gentleSway = Mth.sin(tSec * 2.4f);

            // Cabeza: mirando ligeramente hacia abajo durante el descenso y levantándose al aterrizar
            this.head.xRot = Mth.lerp(brake, 0.18f + 0.04f * flapWave, 0.02f);
            this.head.yRot = 0.0f;
            this.head.zRot = 0.0f;

            // Brazos abiertos con elegancia en vuelo, descendiendo suavemente al frenar para aterrizar
            float armOutZ = Mth.lerp(brake, 0.42f + 0.10f * flapWave, 0.14f);
            float armPitchX = Mth.lerp(brake, 0.20f - 0.08f * flapWave, 0.05f);
            this.rightArm.xRot = armPitchX;
            this.rightArm.yRot = 0.0f;
            this.rightArm.zRot = armOutZ;

            this.leftArm.xRot = armPitchX;
            this.leftArm.yRot = 0.0f;
            this.leftArm.zRot = -armOutZ;

            // Piernas suspendidas en el aire durante el descenso, enderezándose firmes para pisar el suelo
            this.rightLeg.xRot = (0.38f + 0.08f * gentleSway) * flight;
            this.rightLeg.yRot = 0.0f;
            this.rightLeg.zRot = 0.04f * flight;

            this.leftLeg.xRot = (0.18f - 0.08f * gentleSway) * flight;
            this.leftLeg.yRot = 0.0f;
            this.leftLeg.zRot = -0.04f * flight;
        }
    }

    private static void ensureModelsLoaded() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getEntityModels() == null) {
            return;
        }
        EntityModelSet currentSet = mc.getEntityModels();
        if (cachedModelSet != currentSet) {
            wingsHaloModel = new AngelRevivirModel(currentSet.bakeLayer(AngelRevivirModel.LAYER_LOCATION));
            vfxModel = new RitualRevivirVfxModel(currentSet.bakeLayer(RitualRevivirVfxModel.LAYER_LOCATION));
            playerModelWide = new DescendingPlayerModel(currentSet.bakeLayer(ModelLayers.PLAYER), false);
            playerModelSlim = new DescendingPlayerModel(currentSet.bakeLayer(ModelLayers.PLAYER_SLIM), true);
            cachedModelSet = currentSet;
        }
    }

    public static void submit(PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        List<ReviveRitualClientManager.ActiveRitual> rituals = ReviveRitualClientManager.getActiveRituals();
        if (rituals.isEmpty()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null) {
            return;
        }
        ensureModelsLoaded();
        if (wingsHaloModel == null || vfxModel == null || playerModelWide == null || playerModelSlim == null) {
            return;
        }

        long now = System.currentTimeMillis();
        String currentDim = mc.level.dimension().identifier().toString();
        Vec3 camPos = cameraState.pos;

        for (ReviveRitualClientManager.ActiveRitual r : rituals) {
            if (r.isExpired(now) || !r.dimensionId.equals(currentDim)) {
                continue;
            }
            double dx = r.x - camPos.x;
            double dy = r.y - camPos.y;
            double dz = r.z - camPos.z;
            if (dx * dx + dz * dz > 180.0 * 180.0) {
                continue;
            }

            long elapsed = r.elapsedMs(now);
            Identifier vfxTex = RitualRevivirVfxModel.TEXTURE_CELESTIAL;
            Identifier wingsTex = AngelRevivirModel.TEXTURE_CELESTIAL;

            // 1. Grieta Celestial 3D en el cielo (Y + 32) + rayos divinos perimetrales huecos
            float riftScaleFactor = r.getRiftScaleFactor(elapsed);
            if (riftScaleFactor > 0.01f) {
                submitSkyRift(poseStack, collector, dx, dy + ReviveRitualClientManager.RIFT_HEIGHT_OFFSET, dz,
                        elapsed, riftScaleFactor, r.baseYawDeg, vfxTex);
            }

            // 2. Sello / Círculo Celestial luminoso a ras del suelo marcando el punto exacto de aterrizaje
            float sealIntensity = computeSealIntensity(elapsed);
            if (sealIntensity > 0.01f) {
                submitGroundCelestialSeal(poseStack, collector, dx, dy + 0.06, dz, elapsed, sealIntensity);
            }

            // 3A. Jugador con su skin real + Alas Celestiales + Halo + contraluz detrás de la espalda (2.2s .. 7.0s)
            float playerAlpha = r.getDescendingPlayerAlpha(elapsed);
            if (playerAlpha > 0.01f) {
                double playerDy = r.getPlayerWorldY(elapsed) - camPos.y;
                submitDescendingPlayerWithWings(mc, poseStack, collector, r, dx, playerDy, dz, elapsed, playerAlpha, wingsTex);
            }

            // 3B. Alas Celestiales + Halo disolviéndose sobre el jugador real recién aterrizado (7.0s .. 8.8s)
            float postWingsAlpha = r.getPostLandingWingsAlpha(elapsed);
            if (postWingsAlpha > 0.01f) {
                double wx = dx;
                double wy = dy;
                double wz = dz;
                float yawForWings = -(r.baseYawDeg + 180.0f);

                // Si el jugador real ya está presente en el cliente, seguir su posición/rotación exacta
                Player livePlayer = mc.level.getPlayerByUUID(r.playerUuid);
                if (livePlayer != null && !livePlayer.isSpectator()) {
                    wx = livePlayer.getX() - camPos.x;
                    wy = livePlayer.getY() - camPos.y;
                    wz = livePlayer.getZ() - camPos.z;
                    yawForWings = -livePlayer.yBodyRot;
                }
                submitPostLandingWings(poseStack, collector, wx, wy, wz, elapsed, postWingsAlpha, yawForWings, wingsTex);
            }

            // 4. Anillos Giratorios 3D alrededor del punto de aterrizaje (se hunden en el suelo al aterrizar)
            float ringsAlpha = computeRingsPresence(elapsed);
            if (ringsAlpha > 0.01f) {
                submitRitualRings(poseStack, collector, dx, dy, dz, elapsed, ringsAlpha, vfxTex);
            }

            // 5. Pulso vertical fugaz (0.6s) + onda expansiva luminosa en el suelo al aterrizar (7.0s)
            if (elapsed >= ReviveRitualClientManager.CHAIN_SHATTER_MS && elapsed <= ReviveRitualClientManager.CHAIN_SHATTER_MS + 600L) {
                float pulseT = (float) (elapsed - ReviveRitualClientManager.CHAIN_SHATTER_MS) / 600.0f;
                submitLandingFlashBeam(poseStack, collector, dx, dy, dz, pulseT);
            }
            if (elapsed >= ReviveRitualClientManager.CHAIN_SHATTER_MS && elapsed <= ReviveRitualClientManager.CHAIN_SHATTER_MS + 1800L) {
                float shockT = (float) (elapsed - ReviveRitualClientManager.CHAIN_SHATTER_MS) / 1800.0f;
                submitGroundShockwave(poseStack, collector, dx, dy + 0.15, dz, shockT);
            }
        }
    }

    private static void submitDescendingPlayerWithWings(
            Minecraft mc,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            ReviveRitualClientManager.ActiveRitual r,
            double rx, double ry, double rz,
            long elapsed,
            float alpha,
            Identifier wingsTex
    ) {
        PlayerInfo info = mc.getConnection() != null ? mc.getConnection().getPlayerInfo(r.playerUuid) : null;
        PlayerSkin skin = info != null ? info.getSkin() : DefaultPlayerSkin.get(r.playerUuid);
        Identifier skinTex = skin.body().texturePath();
        boolean isSlim = skin.model() == PlayerModelType.SLIM;

        long descendMs = Math.max(0L, elapsed - ReviveRitualClientManager.PHASE_DESCENT_START_MS);
        float brakeProgress = r.getLandingBrakeProgress(elapsed);
        float flightSec = descendMs * 0.001f;
        float bobY = Mth.sin(flightSec * (Mth.TWO_PI / 1.6f)) * 0.12f * (1.0f - brakeProgress);
        // Ligera inclinación corporal hacia adelante en vuelo que se endereza a 0° al frenar para aterrizar
        float bodyPitchDeg = Mth.lerp(brakeProgress, 11.0f, 0.0f);

        float modelYawDeg = -(r.baseYawDeg + 180.0f);
        int alphaInt = Math.round(Mth.clamp(alpha, 0.0f, 1.0f) * 255.0f);
        int tint = ARGB.color(alphaInt, 255, 255, 255);

        // 1. Jugador real (escala estándar de jugador 0.9375f, igual que AvatarRenderer.scale)
        poseStack.pushPose();
        poseStack.translate(rx, ry + bobY, rz);
        poseStack.rotateDegrees(Axis.YP, modelYawDeg);
        poseStack.rotateDegrees(Axis.XP, bodyPitchDeg);
        float playerScale = 0.9375f;
        poseStack.scale(-playerScale, -playerScale, playerScale);
        poseStack.translate(0.0f, -1.501f, 0.0f);

        AvatarRenderState avatarState = new AvatarRenderState();
        avatarState.skin = skin;
        avatarState.showHat = true;
        avatarState.showJacket = true;
        avatarState.showLeftSleeve = true;
        avatarState.showRightSleeve = true;
        avatarState.showLeftPants = true;
        avatarState.showRightPants = true;
        avatarState.ageInTicks = flightSec;
        avatarState.fallFlyingTimeInTicks = brakeProgress;

        DescendingPlayerModel model = isSlim ? playerModelSlim : playerModelWide;
        collector.submitModel(
                model,
                avatarState,
                poseStack,
                RenderTypes.entityTranslucent(skinTex),
                FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                tint,
                null,
                0
        );
        poseStack.popPose();

        // 2. Alas Celestiales + Halo giratorio acoplados a la espalda y cabeza del jugador
        poseStack.pushPose();
        poseStack.translate(rx, ry + bobY, rz);
        poseStack.rotateDegrees(Axis.YP, modelYawDeg);
        poseStack.rotateDegrees(Axis.XP, bodyPitchDeg);
        float wingsScale = 0.9375f;
        poseStack.scale(-wingsScale, -wingsScale, wingsScale);
        poseStack.translate(0.0f, -1.501f, 0.0f);

        boolean braking = elapsed >= ReviveRitualClientManager.PHASE_BRAKE_START_MS;
        long brakeMs = Math.max(0L, elapsed - ReviveRitualClientManager.PHASE_BRAKE_START_MS);
        AngelRevivirModel.AnimState wingsState = new AngelRevivirModel.AnimState(descendMs, brakeMs, braking);
        collector.submitModel(
                wingsHaloModel,
                wingsState,
                poseStack,
                RenderTypes.entityTranslucent(wingsTex),
                FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                tint,
                null,
                0
        );
        poseStack.popPose();
    }

    private static void submitPostLandingWings(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            double rx, double ry, double rz,
            long elapsed,
            float alpha,
            float modelYawDeg,
            Identifier wingsTex
    ) {
        poseStack.pushPose();
        poseStack.translate(rx, ry, rz);
        poseStack.rotateDegrees(Axis.YP, modelYawDeg);
        // Sutil expansión al disolverse
        float scale = 0.9375f * Mth.lerp(1.0f - alpha, 1.0f, 1.08f);
        poseStack.scale(-scale, -scale, scale);
        poseStack.translate(0.0f, -1.501f, 0.0f);

        long descendMs = Math.max(0L, elapsed - ReviveRitualClientManager.PHASE_DESCENT_START_MS);
        long brakeMs = Math.max(0L, elapsed - ReviveRitualClientManager.PHASE_BRAKE_START_MS);
        int alphaInt = Math.round(Mth.clamp(alpha, 0.0f, 1.0f) * 255.0f);
        int tint = ARGB.color(alphaInt, 255, 255, 255);

        AngelRevivirModel.AnimState wingsState = new AngelRevivirModel.AnimState(descendMs, brakeMs, true);
        collector.submitModel(
                wingsHaloModel,
                wingsState,
                poseStack,
                RenderTypes.entityTranslucent(wingsTex),
                FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                tint,
                null,
                0
        );
        poseStack.popPose();
    }

    private static void submitSkyRift(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            double rx, double ry, double rz,
            long elapsed,
            float scaleFactor,
            float baseYawDeg,
            Identifier vfxTex
    ) {
        poseStack.pushPose();
        poseStack.translate(rx, ry, rz);
        float s = 2.85f * scaleFactor;
        float modelYawDeg = -(baseYawDeg + 180.0f);
        poseStack.rotateDegrees(Axis.YP, modelYawDeg);
        poseStack.scale(-s, -s, s);
        poseStack.translate(0.0f, -1.5f, 0.0f);

        int alpha = Math.round(Mth.clamp(scaleFactor * 1.15f, 0.0f, 1.0f) * 255.0f);
        int tint = ARGB.color(alpha, 255, 255, 255);
        float extraSpin = (elapsed * 0.00055f);

        RitualRevivirVfxModel.AnimState riftState = new RitualRevivirVfxModel.AnimState(
                RitualRevivirVfxModel.PartMode.RIFT,
                elapsed,
                extraSpin
        );
        // La grieta celestial en lo alto sí mantiene brillo/resplandor dorado (outline), mientras alas/jugador/anillos quedan limpios (0)
        int riftGlowOutline = alpha > 8 ? ARGB.color(255, 255, 232, 148) : 0;
        collector.submitModel(
                vfxModel,
                riftState,
                poseStack,
                RenderTypes.entityTranslucent(vfxTex),
                FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                tint,
                null,
                riftGlowOutline
        );
        poseStack.popPose();

        // Cono abierto de rayos divinos perimetrales ("hueco" en el centro para no tapar nunca al jugador)
        poseStack.pushPose();
        poseStack.translate(rx, ry - 0.5, rz);
        float rayR = 1.0f;
        float rayG = 0.90f;
        float rayB = 0.52f;
        float rayAlpha = 0.16f * scaleFactor;
        float spinAngle = elapsed * 0.00035f;

        collector.submitCustomGeometry(poseStack, RenderTypes.lightning(), (pose, vc) -> {
            Matrix4f mat = pose.pose();
            int rays = 12;
            float rayLen = 30.0f * scaleFactor;
            for (int i = 0; i < rays; i++) {
                float a = spinAngle + (float) (i * Math.PI * 2.0 / rays);
                float nextA = a + 0.14f;
                float topR = 2.4f * scaleFactor;
                float botR = (6.4f + (i % 2) * 2.4f) * scaleFactor;

                float x1t = Mth.cos(a) * topR;
                float z1t = Mth.sin(a) * topR;
                float x2t = Mth.cos(nextA) * topR;
                float z2t = Mth.sin(nextA) * topR;

                float x1b = Mth.cos(a) * botR;
                float z1b = Mth.sin(a) * botR;
                float x2b = Mth.cos(nextA) * botR;
                float z2b = Mth.sin(nextA) * botR;

                vc.addVertex(mat, x1t, 0.0f, z1t).setColor(rayR, rayG, rayB, rayAlpha);
                vc.addVertex(mat, x2t, 0.0f, z2t).setColor(rayR, rayG, rayB, rayAlpha);
                vc.addVertex(mat, x2b, -rayLen, z2b).setColor(rayR, rayG, rayB, 0.0f);
                vc.addVertex(mat, x1b, -rayLen, z1b).setColor(rayR, rayG, rayB, 0.0f);

                vc.addVertex(mat, x2t, 0.0f, z2t).setColor(rayR, rayG, rayB, rayAlpha);
                vc.addVertex(mat, x1t, 0.0f, z1t).setColor(rayR, rayG, rayB, rayAlpha);
                vc.addVertex(mat, x1b, -rayLen, z1b).setColor(rayR, rayG, rayB, 0.0f);
                vc.addVertex(mat, x2b, -rayLen, z2b).setColor(rayR, rayG, rayB, 0.0f);
            }
        });
        poseStack.popPose();
    }

    /**
     * Sello / Círculo Celestial luminoso plano a ras del suelo marcando el punto exacto de aterrizaje.
     * Reemplaza el pilar vertical de faro para mantener al jugador y sus alas 100% despejados.
     */
    private static void submitGroundCelestialSeal(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            double rx, double ry, double rz,
            long elapsed,
            float intensity
    ) {
        float pulse = 0.88f + 0.12f * Mth.sin(elapsed * 0.006f);
        float alpha = Mth.clamp(intensity * 0.62f * pulse, 0.0f, 0.85f);
        if (alpha <= 0.01f) {
            return;
        }
        float scale = 0.72f + 0.28f * Mth.clamp(intensity, 0.0f, 1.0f);
        float spinOuter = elapsed * 0.00055f;
        float spinInner = -elapsed * 0.00095f;

        poseStack.pushPose();
        poseStack.translate(rx, ry, rz);
        collector.submitCustomGeometry(poseStack, RenderTypes.lightning(), (pose, vc) -> {
            Matrix4f mat = pose.pose();
            int segs = 40;

            // 1. Anillo exterior brillante
            float outerR = 3.15f * scale;
            float outerIn = 2.75f * scale;
            for (int i = 0; i < segs; i++) {
                float a0 = (float) (i * Math.PI * 2.0 / segs);
                float a1 = (float) ((i + 1) * Math.PI * 2.0 / segs);
                drawHorizontalQuad(vc, mat,
                        Mth.cos(a0) * outerIn, Mth.sin(a0) * outerIn,
                        Mth.cos(a0) * outerR, Mth.sin(a0) * outerR,
                        Mth.cos(a1) * outerR, Mth.sin(a1) * outerR,
                        Mth.cos(a1) * outerIn, Mth.sin(a1) * outerIn,
                        1.0f, 0.86f, 0.38f, alpha * 0.85f);
            }

            // 2. Anillo interior dorado
            float midR = 1.85f * scale;
            float midIn = 1.60f * scale;
            for (int i = 0; i < segs; i++) {
                float a0 = (float) (i * Math.PI * 2.0 / segs);
                float a1 = (float) ((i + 1) * Math.PI * 2.0 / segs);
                drawHorizontalQuad(vc, mat,
                        Mth.cos(a0) * midIn, Mth.sin(a0) * midIn,
                        Mth.cos(a0) * midR, Mth.sin(a0) * midR,
                        Mth.cos(a1) * midR, Mth.sin(a1) * midR,
                        Mth.cos(a1) * midIn, Mth.sin(a1) * midIn,
                        1.0f, 0.95f, 0.65f, alpha);
            }

            // 3. Estrella rúnica de 8 puntas (8 rayos radiales girando suavemente)
            float rayHalfW = 0.085f * scale;
            for (int i = 0; i < 8; i++) {
                float ang = (i % 2 == 0 ? spinOuter : spinInner) + (float) (i * Math.PI / 4.0);
                float cos = Mth.cos(ang), sin = Mth.sin(ang);
                float px = -sin * rayHalfW, pz = cos * rayHalfW;
                float rStart = 0.35f * scale;
                float rEnd = (i % 2 == 0 ? 3.0f : 2.25f) * scale;
                drawHorizontalQuad(vc, mat,
                        cos * rStart - px, sin * rStart - pz,
                        cos * rEnd - px, sin * rEnd - pz,
                        cos * rEnd + px, sin * rEnd + pz,
                        cos * rStart + px, sin * rStart + pz,
                        1.0f, 0.92f, 0.55f, alpha * 0.72f);
            }

            // 4. Resplandor central cálido en el punto de apoyo de los pies
            float centerR = 1.25f * scale;
            for (int i = 0; i < 20; i++) {
                float a0 = (float) (i * Math.PI * 2.0 / 20.0);
                float a1 = (float) ((i + 1) * Math.PI * 2.0 / 20.0);
                float x0 = Mth.cos(a0) * centerR, z0 = Mth.sin(a0) * centerR;
                float x1 = Mth.cos(a1) * centerR, z1 = Mth.sin(a1) * centerR;
                vc.addVertex(mat, 0.0f, 0.01f, 0.0f).setColor(1.0f, 0.96f, 0.72f, alpha * 0.55f);
                vc.addVertex(mat, x0, 0.01f, z0).setColor(1.0f, 0.88f, 0.42f, 0.0f);
                vc.addVertex(mat, x1, 0.01f, z1).setColor(1.0f, 0.88f, 0.42f, 0.0f);
                vc.addVertex(mat, 0.0f, 0.01f, 0.0f).setColor(1.0f, 0.96f, 0.72f, alpha * 0.55f);

                vc.addVertex(mat, 0.0f, 0.01f, 0.0f).setColor(1.0f, 0.96f, 0.72f, alpha * 0.55f);
                vc.addVertex(mat, x1, 0.01f, z1).setColor(1.0f, 0.88f, 0.42f, 0.0f);
                vc.addVertex(mat, x0, 0.01f, z0).setColor(1.0f, 0.88f, 0.42f, 0.0f);
                vc.addVertex(mat, 0.0f, 0.01f, 0.0f).setColor(1.0f, 0.96f, 0.72f, alpha * 0.55f);
            }
        });
        poseStack.popPose();
    }

    /**
     * Pulso vertical fugaz de 0.6s justo en el instante del aterrizaje (7.0s..7.6s) para dar
     * sensación de impacto celestial sin estorbar en ningún momento el descenso.
     */
    private static void submitLandingFlashBeam(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            double rx, double ry, double rz,
            float t
    ) {
        float fade = (1.0f - t) * (1.0f - t);
        float alpha = fade * 0.52f;
        if (alpha <= 0.01f) {
            return;
        }
        float radius = Mth.lerp(t, 0.85f, 0.15f);
        float height = (float) ReviveRitualClientManager.RIFT_HEIGHT_OFFSET;

        poseStack.pushPose();
        poseStack.translate(rx, ry, rz);
        collector.submitCustomGeometry(poseStack, RenderTypes.lightning(), (pose, vc) -> {
            Matrix4f mat = pose.pose();
            int sides = 10;
            for (int i = 0; i < sides; i++) {
                float a0 = (float) (i * Math.PI * 2.0 / sides);
                float a1 = (float) ((i + 1) * Math.PI * 2.0 / sides);
                float x0 = Mth.cos(a0) * radius, z0 = Mth.sin(a0) * radius;
                float x1 = Mth.cos(a1) * radius, z1 = Mth.sin(a1) * radius;

                vc.addVertex(mat, x0, 0.0f, z0).setColor(1.0f, 0.95f, 0.68f, alpha);
                vc.addVertex(mat, x1, 0.0f, z1).setColor(1.0f, 0.95f, 0.68f, alpha);
                vc.addVertex(mat, x1 * 0.55f, height, z1 * 0.55f).setColor(1.0f, 0.88f, 0.45f, 0.0f);
                vc.addVertex(mat, x0 * 0.55f, height, z0 * 0.55f).setColor(1.0f, 0.88f, 0.45f, 0.0f);

                vc.addVertex(mat, x1, 0.0f, z1).setColor(1.0f, 0.95f, 0.68f, alpha);
                vc.addVertex(mat, x0, 0.0f, z0).setColor(1.0f, 0.95f, 0.68f, alpha);
                vc.addVertex(mat, x0 * 0.55f, height, z0 * 0.55f).setColor(1.0f, 0.88f, 0.45f, 0.0f);
                vc.addVertex(mat, x1 * 0.55f, height, z1 * 0.55f).setColor(1.0f, 0.88f, 0.45f, 0.0f);
            }
        });
        poseStack.popPose();
    }

    private static void drawHorizontalQuad(
            com.mojang.blaze3d.vertex.VertexConsumer vc,
            Matrix4f mat,
            float x0, float z0,
            float x1, float z1,
            float x2, float z2,
            float x3, float z3,
            float r, float g, float b, float a
    ) {
        vc.addVertex(mat, x0, 0.0f, z0).setColor(r, g, b, a);
        vc.addVertex(mat, x1, 0.0f, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x2, 0.0f, z2).setColor(r, g, b, a);
        vc.addVertex(mat, x3, 0.0f, z3).setColor(r, g, b, a);

        vc.addVertex(mat, x3, 0.0f, z3).setColor(r, g, b, a);
        vc.addVertex(mat, x2, 0.0f, z2).setColor(r, g, b, a);
        vc.addVertex(mat, x1, 0.0f, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x0, 0.0f, z0).setColor(r, g, b, a);
    }

    private static void submitRitualRings(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            double rx, double ry, double rz,
            long elapsed,
            float presence,
            Identifier vfxTex
    ) {
        // Durante el frenado y aterrizaje (6.2s..7.1s), los 3 anillos se hunden en el suelo y desaparecen bajo tierra
        float sinkT = 0.0f;
        if (elapsed > 6200L) {
            sinkT = Mth.clamp((float) (elapsed - 6200L) / 900.0f, 0.0f, 1.0f);
        }
        float sinkEase = sinkT * sinkT;
        double sinkOffsetY = -2.85 * sinkEase;

        poseStack.pushPose();
        poseStack.translate(rx, ry + 0.12 + sinkOffsetY, rz);
        float scale = 1.35f * (0.85f + 0.15f * presence) * Mth.lerp(sinkEase, 1.0f, 0.88f);
        poseStack.scale(-scale, -scale, scale);
        poseStack.translate(0.0f, -1.5f, 0.0f);

        int alpha = Math.round(Mth.clamp(presence, 0.0f, 1.0f) * 245.0f);
        int tint = ARGB.color(alpha, 255, 255, 255);

        long spinMs = (long) (elapsed * (elapsed >= ReviveRitualClientManager.PHASE_BRAKE_START_MS ? 1.85 : 1.0));
        RitualRevivirVfxModel.AnimState state = new RitualRevivirVfxModel.AnimState(
                RitualRevivirVfxModel.PartMode.RINGS,
                spinMs,
                0.0f
        );
        collector.submitModel(
                vfxModel,
                state,
                poseStack,
                RenderTypes.entityTranslucent(vfxTex),
                FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                tint,
                null,
                0
        );
        poseStack.popPose();
    }

    private static void submitGroundShockwave(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            double rx, double ry, double rz,
            float t
    ) {
        float eased = 1.0f - (1.0f - t) * (1.0f - t);
        float outerR = Mth.lerp(eased, 1.2f, 13.5f);
        float innerR = Math.max(0.2f, outerR - Mth.lerp(t, 1.8f, 0.5f));
        float alpha = (1.0f - t) * (1.0f - t) * 0.72f;
        if (alpha <= 0.01f) {
            return;
        }

        float rCol = 1.0f;
        float gCol = 0.88f;
        float bCol = 0.45f;

        poseStack.pushPose();
        poseStack.translate(rx, ry, rz);
        collector.submitCustomGeometry(poseStack, RenderTypes.lightning(), (pose, vc) -> {
            Matrix4f mat = pose.pose();
            int segs = 36;
            for (int i = 0; i < segs; i++) {
                float a0 = (float) (i * Math.PI * 2.0 / segs);
                float a1 = (float) ((i + 1) * Math.PI * 2.0 / segs);

                float ix0 = Mth.cos(a0) * innerR, iz0 = Mth.sin(a0) * innerR;
                float ox0 = Mth.cos(a0) * outerR, oz0 = Mth.sin(a0) * outerR;
                float ix1 = Mth.cos(a1) * innerR, iz1 = Mth.sin(a1) * innerR;
                float ox1 = Mth.cos(a1) * outerR, oz1 = Mth.sin(a1) * outerR;

                vc.addVertex(mat, ix0, 0.0f, iz0).setColor(rCol, gCol, bCol, 0.0f);
                vc.addVertex(mat, ox0, 0.0f, oz0).setColor(rCol, gCol, bCol, alpha);
                vc.addVertex(mat, ox1, 0.0f, oz1).setColor(rCol, gCol, bCol, alpha);
                vc.addVertex(mat, ix1, 0.0f, iz1).setColor(rCol, gCol, bCol, 0.0f);

                vc.addVertex(mat, ix1, 0.0f, iz1).setColor(rCol, gCol, bCol, 0.0f);
                vc.addVertex(mat, ox1, 0.0f, oz1).setColor(rCol, gCol, bCol, alpha);
                vc.addVertex(mat, ox0, 0.0f, oz0).setColor(rCol, gCol, bCol, alpha);
                vc.addVertex(mat, ix0, 0.0f, iz0).setColor(rCol, gCol, bCol, 0.0f);
            }
        });
        poseStack.popPose();
    }

    private static float computeSealIntensity(long elapsed) {
        if (elapsed < 900L) return 0.0f;
        if (elapsed < 2400L) return (float) (elapsed - 900L) / 1500.0f * 0.75f;
        if (elapsed < ReviveRitualClientManager.CHAIN_SHATTER_MS) {
            float t = (float) (elapsed - 2400L) / (float) (ReviveRitualClientManager.CHAIN_SHATTER_MS - 2400L);
            return Mth.lerp(t, 0.75f, 1.15f);
        }
        if (elapsed < ReviveRitualClientManager.CHAIN_SHATTER_MS + 1200L) {
            float t = (float) (elapsed - ReviveRitualClientManager.CHAIN_SHATTER_MS) / 1200.0f;
            return Mth.clamp((1.0f - t) * (1.0f - t) * 1.15f, 0.0f, 1.15f);
        }
        return 0.0f;
    }

    private static float computeRingsPresence(long elapsed) {
        if (elapsed < 1800L) return 0.0f;
        if (elapsed < 3000L) return (float) (elapsed - 1800L) / 1200.0f;
        if (elapsed < 6200L) return 1.0f;
        if (elapsed < 7100L) {
            // Se hunden bajo tierra y se desvanecen justo al aterrizar (6.2s -> 7.1s)
            float t = (float) (elapsed - 6200L) / 900.0f;
            return Mth.clamp(1.0f - t * t, 0.0f, 1.0f);
        }
        return 0.0f;
    }
}
