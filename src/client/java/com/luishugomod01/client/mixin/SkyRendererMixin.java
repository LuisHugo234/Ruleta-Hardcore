package com.luishugomod01.client.mixin;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.client.bloodmoon.BloodMoonClientState;
import com.luishugomod01.client.ritual.ReviveRitualClientManager;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import java.util.function.Supplier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.MoonPhase;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {

    // Zenith: #1A0003 (RGB: 26, 0, 3)
    @Unique
    private static final float ZENITH_R = 26.0f / 255.0f;
    @Unique
    private static final float ZENITH_G = 0.0f / 255.0f;
    @Unique
    private static final float ZENITH_B = 3.0f / 255.0f;

    // Horizonte carmesí intenso: #6E0711 (RGB: 110, 7, 17)
    @Unique
    private static final float HORIZON_R = 110.0f / 255.0f;
    @Unique
    private static final float HORIZON_G = 7.0f / 255.0f;
    @Unique
    private static final float HORIZON_B = 17.0f / 255.0f;

    // Cielo Eclipse Solar: Negro profundo / Gris carbón (#050505 / RGB: 5, 5, 5)
    @Unique
    private static final float ECLIPSE_ZENITH_R = 5.0f / 255.0f;
    @Unique
    private static final float ECLIPSE_ZENITH_G = 5.0f / 255.0f;
    @Unique
    private static final float ECLIPSE_ZENITH_B = 5.0f / 255.0f;

    // Texturas VFX para Blood Moon (nebulosa + anillo rúnico) y Eclipse Solar (rayos de corona + anillo de diamante)
    @Unique
    private static final Identifier TEX_BLOOD_NEBULA = Ruleta_Hardcore.id("textures/sky/blood_moon_nebula.png");
    @Unique
    private static final Identifier TEX_BLOOD_RUNES = Ruleta_Hardcore.id("textures/sky/blood_moon_runes.png");
    @Unique
    private static final Identifier TEX_ECLIPSE_RAYS = Ruleta_Hardcore.id("textures/sky/eclipse_corona_rays.png");
    @Unique
    private static final Identifier TEX_ECLIPSE_DIAMOND = Ruleta_Hardcore.id("textures/sky/eclipse_diamond_flare.png");

    @Unique
    private static final int HORIZON_SEGMENTS = 32;
    @Unique
    private static final int HORIZON_VERTEX_COUNT = HORIZON_SEGMENTS + 2;

    @Unique
    private static final int AURA_SEGMENTS = 24;
    @Unique
    private static final int AURA_VERTEX_COUNT = AURA_SEGMENTS + 2;

    @Unique
    private static final int ECLIPSE_SEGMENTS = 48;
    @Unique
    private static final int ECLIPSE_DISC_VERTEX_COUNT = ECLIPSE_SEGMENTS + 2;

    @Shadow
    @Final
    private RenderSystem.AutoStorageIndexBuffer quadIndices;

    @Shadow
    @Final
    private GpuBuffer sunBuffer;

    @Shadow
    @Final
    private GpuBuffer moonBuffer;

    @Shadow
    private Matrix4f applyCelestialBodyTransform(PoseStack poseStack, float height, float size) {
        throw new AssertionError();
    }

    @Shadow
    private void drawCelestialBody(
            Supplier<String> debugLabel,
            RenderPass renderPass,
            GpuBufferSlice dynamicTransforms,
            GpuBuffer indexBuffer,
            GpuBuffer vertexBuffer,
            int baseVertex
    ) {
        throw new AssertionError();
    }

    @Unique
    private GpuBuffer ruleta$horizonGlowBuffer;

    @Unique
    private GpuBuffer ruleta$moonAuraBuffer;

    @Unique
    private GpuBuffer ruleta$eclipseCoronaBuffer;

    @Unique
    private GpuBuffer ruleta$eclipseFireRingBuffer;

    @Unique
    private GpuBuffer ruleta$eclipseBlackDiscBuffer;

    @Unique
    private GpuBuffer ruleta$unitTexturedQuadBuffer;

    @Unique
    private AbstractTexture ruleta$bloodNebulaTexture;

    @Unique
    private AbstractTexture ruleta$bloodRunesTexture;

    @Unique
    private AbstractTexture ruleta$eclipseRaysTexture;

    @Unique
    private AbstractTexture ruleta$eclipseDiamondTexture;

    @Unique
    private Matrix4f ruleta$lastEclipseBlackDiscMat;

    @Unique
    private float ruleta$lastEclipseDiscAlpha;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void ruleta$precargarTexturasVfxCielo(
            TextureManager textureManager,
            AtlasManager atlasManager,
            RenderTarget renderTarget,
            CallbackInfo ci
    ) {
        ruleta$asegurarRecursosCielo(textureManager);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void ruleta$asegurarRecursosAntesDeRenderPass(
            GpuBufferSlice fogBuffer,
            SkyRenderState state,
            CallbackInfo ci
    ) {
        this.ruleta$lastEclipseBlackDiscMat = null;
        this.ruleta$lastEclipseDiscAlpha = 0.0f;
        if (this.ruleta$bloodNebulaTexture == null
                || this.ruleta$bloodRunesTexture == null
                || this.ruleta$eclipseRaysTexture == null
                || this.ruleta$eclipseDiamondTexture == null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.getTextureManager() != null) {
                ruleta$asegurarRecursosCielo(mc.getTextureManager());
            }
        }
        if (this.ruleta$unitTexturedQuadBuffer == null) {
            ruleta$getOrCreateUnitTexturedQuadBuffer();
        }
        if (this.ruleta$horizonGlowBuffer == null) {
            ruleta$getOrCreateHorizonBuffer();
        }
        if (this.ruleta$moonAuraBuffer == null) {
            ruleta$getOrCreateMoonAuraBuffer();
        }
        if (this.ruleta$eclipseCoronaBuffer == null) {
            ruleta$getOrCreateEclipseCoronaBuffer();
        }
        if (this.ruleta$eclipseFireRingBuffer == null) {
            ruleta$getOrCreateEclipseFireRingBuffer();
        }
        if (this.ruleta$eclipseBlackDiscBuffer == null) {
            ruleta$getOrCreateEclipseBlackDiscBuffer();
        }
    }

    @Unique
    private void ruleta$asegurarRecursosCielo(TextureManager textureManager) {
        if (this.ruleta$bloodNebulaTexture == null) {
            this.ruleta$bloodNebulaTexture = textureManager.getTexture(TEX_BLOOD_NEBULA);
        }
        if (this.ruleta$bloodRunesTexture == null) {
            this.ruleta$bloodRunesTexture = textureManager.getTexture(TEX_BLOOD_RUNES);
        }
        if (this.ruleta$eclipseRaysTexture == null) {
            this.ruleta$eclipseRaysTexture = textureManager.getTexture(TEX_ECLIPSE_RAYS);
        }
        if (this.ruleta$eclipseDiamondTexture == null) {
            this.ruleta$eclipseDiamondTexture = textureManager.getTexture(TEX_ECLIPSE_DIAMOND);
        }
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void ruleta$tintarZenithLunaSangre(
            ClientLevel level,
            float partialTick,
            Camera camera,
            SkyRenderState state,
            CallbackInfo ci
    ) {
        if (state.skyColor == null) {
            return;
        }
        if (level.dimension().equals(Level.OVERWORLD)) {
            float eclipseIntensity = BloodMoonClientState.getEclipseIntensity(partialTick);
            if (eclipseIntensity > 0.001f) {
                float er = Mth.lerp(eclipseIntensity, state.skyColor.x(), ECLIPSE_ZENITH_R);
                float eg = Mth.lerp(eclipseIntensity, state.skyColor.y(), ECLIPSE_ZENITH_G);
                float eb = Mth.lerp(eclipseIntensity, state.skyColor.z(), ECLIPSE_ZENITH_B);
                state.skyColor = new Vector3f(er, eg, eb);

                // Estrellas visibles en pleno día conforme el Eclipse entra en totalidad (>55%)
                if (eclipseIntensity > 0.55f) {
                    float starFactor = Mth.clamp((eclipseIntensity - 0.55f) / 0.45f, 0.0f, 1.0f);
                    float smoothStar = starFactor * starFactor * (3.0f - 2.0f * starFactor);
                    state.starBrightness = Math.max(state.starBrightness, smoothStar * 0.85f * state.rainBrightness);
                }
            }

            float intensity = BloodMoonClientState.getIntensity(partialTick);
            if (intensity > 0.001f) {
                float r = Mth.lerp(intensity, state.skyColor.x(), ZENITH_R);
                float g = Mth.lerp(intensity, state.skyColor.y(), ZENITH_G);
                float b = Mth.lerp(intensity, state.skyColor.z(), ZENITH_B);
                state.skyColor = new Vector3f(r, g, b);
            }
        }

        // Cambio temporal de cielo durante el Ritual de Revivir (Dorado Celestial)
        float ritualSky = ReviveRitualClientManager.getSkyIntensity();
        if (ritualSky > 0.001f) {
            float tr = 98.0f / 255.0f;
            float tg = 78.0f / 255.0f;
            float tb = 32.0f / 255.0f;
            float blend = ritualSky * 0.88f;
            state.skyColor = new Vector3f(
                    Mth.lerp(blend, state.skyColor.x(), tr),
                    Mth.lerp(blend, state.skyColor.y(), tg),
                    Mth.lerp(blend, state.skyColor.z(), tb)
            );
        }
    }

    @Inject(method = "renderSkyDisc", at = @At("TAIL"))
    private void ruleta$renderizarResplandorHorizonteCarmesi(
            RenderPass renderPass,
            Vector3fc skyColor,
            CallbackInfo ci
    ) {
        float intensity = BloodMoonClientState.getCurrentRenderIntensity();
        if (intensity > 0.001f) {
            GpuBuffer horizonBuffer = ruleta$getOrCreateHorizonBuffer();
            GpuBufferSlice transformSlice = RenderSystem.getDynamicUniforms().writeTransform(
                    RenderSystem.getModelViewMatrixCopy(),
                    new Vector4f(HORIZON_R, HORIZON_G, HORIZON_B, 0.85f * intensity)
            );

            renderPass.pushDebugGroup(() -> "Blood Moon Horizon Glow");
            renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.SUNRISE_SUNSET));
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", transformSlice);
            renderPass.setVertexBuffer(0, horizonBuffer.slice());
            renderPass.draw(HORIZON_VERTEX_COUNT, 1, 0, 0);
            renderPass.popDebugGroup();

            // Nebulosa / vórtice carmesí giratorio en el cenit (2 capas contra-rotantes tipo CustomSkyboxes)
            double worldTime = BloodMoonClientState.getSmoothWorldTime();
            float breathe = 0.84f + 0.16f * (float) Math.sin(worldTime * 0.035);
            float spinA = (float) (worldTime * 0.0022);
            float spinB = (float) (-worldTime * 0.0014 + 1.1);

            Matrix4f baseMv = RenderSystem.getModelViewMatrixCopy();
            Matrix4f nebulaMat1 = new Matrix4f(baseMv)
                    .translate(0.0f, 88.0f, 0.0f)
                    .rotateY(spinA)
                    .scale(195.0f, 1.0f, 195.0f);
            ruleta$drawTexturedSkyQuad(
                    renderPass,
                    "Blood Moon Zenith Nebula Primary",
                    this.ruleta$bloodNebulaTexture,
                    nebulaMat1,
                    new Vector4f(1.0f, 0.22f, 0.28f, 0.42f * intensity * breathe)
            );

            Matrix4f nebulaMat2 = new Matrix4f(baseMv)
                    .translate(0.0f, 84.0f, 0.0f)
                    .rotateY(spinB)
                    .scale(140.0f, 1.0f, 140.0f);
            ruleta$drawTexturedSkyQuad(
                    renderPass,
                    "Blood Moon Zenith Nebula Secondary",
                    this.ruleta$bloodNebulaTexture,
                    nebulaMat2,
                    new Vector4f(0.95f, 0.10f, 0.20f, 0.28f * intensity * (1.12f - breathe * 0.25f))
            );
        }

        // Horizonte "atardecer 360°" en tono ámbar/cobre quemado durante el Eclipse Solar
        float eclipseHorizon = BloodMoonClientState.getCurrentEclipseRenderIntensity();
        if (eclipseHorizon > 0.01f && intensity <= 0.001f) {
            GpuBuffer horizonBuffer = ruleta$getOrCreateHorizonBuffer();
            GpuBufferSlice eclipseHorizonSlice = RenderSystem.getDynamicUniforms().writeTransform(
                    RenderSystem.getModelViewMatrixCopy(),
                    new Vector4f(0.84f, 0.30f, 0.06f, 0.60f * eclipseHorizon)
            );
            renderPass.pushDebugGroup(() -> "Solar Eclipse 360 Horizon Glow");
            renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.SUNRISE_SUNSET));
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", eclipseHorizonSlice);
            renderPass.setVertexBuffer(0, horizonBuffer.slice());
            renderPass.draw(HORIZON_VERTEX_COUNT, 1, 0, 0);
            renderPass.popDebugGroup();
        }

        // Resplandor temporal del horizonte durante el Ritual de Revivir (Dorado Celestial)
        float ritualSky = ReviveRitualClientManager.getSkyIntensity();
        if (ritualSky > 0.001f) {
            float hr = 255.0f / 255.0f;
            float hg = 214.0f / 255.0f;
            float hb = 102.0f / 255.0f;

            GpuBuffer horizonBuffer = ruleta$getOrCreateHorizonBuffer();
            GpuBufferSlice ritualHorizonSlice = RenderSystem.getDynamicUniforms().writeTransform(
                    RenderSystem.getModelViewMatrixCopy(),
                    new Vector4f(hr, hg, hb, 0.78f * ritualSky)
            );

            renderPass.pushDebugGroup(() -> "Revive Ritual Horizon Glow");
            renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.SUNRISE_SUNSET));
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", ritualHorizonSlice);
            renderPass.setVertexBuffer(0, horizonBuffer.slice());
            renderPass.draw(HORIZON_VERTEX_COUNT, 1, 0, 0);
            renderPass.popDebugGroup();
        }
    }

    @ModifyArg(
            method = "renderStars",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/DynamicGpuData;writeTransform(Lorg/joml/Matrix4f;Lorg/joml/Vector4f;)Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;"
            ),
            index = 1
    )
    private Vector4f ruleta$atenuarYTenirEstrellas(Vector4f original) {
        float intensity = BloodMoonClientState.getCurrentRenderIntensity();
        if (intensity <= 0.001f) {
            return original;
        }

        float baseBrightness = original.w;
        // Reducción del 60% en la opacidad (queda en 40%) + ligero tinte rojizo
        float alpha = baseBrightness * Mth.lerp(intensity, 1.0f, 0.40f);
        float r = baseBrightness * Mth.lerp(intensity, 1.0f, 1.0f);
        float g = baseBrightness * Mth.lerp(intensity, 1.0f, 0.45f);
        float b = baseBrightness * Mth.lerp(intensity, 1.0f, 0.48f);
        return new Vector4f(r, g, b, alpha);
    }

    @Inject(method = "renderStars", at = @At("TAIL"))
    private void ruleta$ocultarEstrellasDetrasDelEclipse(
            RenderPass renderPass,
            float starBrightness,
            PoseStack poseStack,
            CallbackInfo ci
    ) {
        if (this.ruleta$lastEclipseBlackDiscMat == null || this.ruleta$lastEclipseDiscAlpha <= 0.005f) {
            return;
        }
        GpuBuffer blackDiscBuffer = ruleta$getOrCreateEclipseBlackDiscBuffer();
        GpuBufferSlice blackDiscSlice = RenderSystem.getDynamicUniforms().writeTransform(
                this.ruleta$lastEclipseBlackDiscMat,
                new Vector4f(1.0f, 1.0f, 1.0f, this.ruleta$lastEclipseDiscAlpha)
        );
        renderPass.pushDebugGroup(() -> "Solar Eclipse Star Occlusion Disc");
        renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.SUNRISE_SUNSET));
        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setUniform("DynamicTransforms", blackDiscSlice);
        renderPass.setVertexBuffer(0, blackDiscBuffer.slice());
        renderPass.draw(ECLIPSE_DISC_VERTEX_COUNT, 1, 0, 0);
        renderPass.popDebugGroup();
    }

    @Inject(method = "renderMoon", at = @At("HEAD"), cancellable = true)
    private void ruleta$renderizarLunaDeSangre(
            RenderPass renderPass,
            MoonPhase moonPhase,
            float rainBrightness,
            PoseStack poseStack,
            CallbackInfo ci
    ) {
        float intensity = BloodMoonClientState.getCurrentRenderIntensity();
        if (intensity <= 0.001f) {
            return;
        }

        int baseVertex = moonPhase.index() * 4;
        float baseMoonScale = Mth.lerp(intensity, 1.0f, 1.6f);
        float moonSize = 20.0f * baseMoonScale;

        // Doble latido cardíaco orgánico (lub-dub) para el aura lunar en escala y opacidad
        double worldTime = BloodMoonClientState.getSmoothWorldTime();
        float heartbeat = ruleta$calcularHeartbeat(worldTime);
        float pulse = 1.0f + 0.14f * heartbeat;
        float auraAlphaPulse = 0.28f + 0.22f * heartbeat;

        // 1. Pase de resplandor / aura radial detrás de la luna con latido cardíaco
        GpuBuffer auraBuffer = ruleta$getOrCreateMoonAuraBuffer();
        float auraRadius = moonSize * 2.05f * pulse;
        Matrix4fStack mvStack = RenderSystem.getModelViewStack();
        mvStack.pushMatrix();
        mvStack.mul(poseStack.last().pose());
        mvStack.translate(0.0f, 100.5f, 0.0f);
        mvStack.scale(auraRadius, 1.0f, auraRadius);
        Matrix4f auraMat = new Matrix4f(mvStack);
        mvStack.popMatrix();

        GpuBufferSlice radialAuraSlice = RenderSystem.getDynamicUniforms().writeTransform(
                auraMat,
                new Vector4f(1.0f, 0.08f, 0.10f, auraAlphaPulse * intensity * rainBrightness)
        );
        renderPass.pushDebugGroup(() -> "Blood Moon Radial Aura");
        renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.SUNRISE_SUNSET));
        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setUniform("DynamicTransforms", radialAuraSlice);
        renderPass.setVertexBuffer(0, auraBuffer.slice());
        renderPass.draw(AURA_VERTEX_COUNT, 1, 0, 0);
        renderPass.popDebugGroup();

        // 1b. Anillos rúnicos arcanos giratorios alrededor de la Luna de Sangre (2 capas contra-rotantes)
        float runeAngleOuter = (float) (worldTime * 0.0085);
        float runeAngleInner = (float) (-worldTime * 0.0135);
        float outerRuneSize = moonSize * 1.48f * (1.0f + 0.05f * heartbeat);
        float innerRuneSize = moonSize * 1.15f * (1.0f + 0.03f * heartbeat);

        mvStack.pushMatrix();
        mvStack.mul(poseStack.last().pose());
        mvStack.translate(0.0f, 100.35f, 0.0f);
        mvStack.rotateY(runeAngleOuter);
        mvStack.scale(outerRuneSize, 1.0f, outerRuneSize);
        Matrix4f runeOuterMat = new Matrix4f(mvStack);
        mvStack.popMatrix();
        ruleta$drawTexturedSkyQuad(
                renderPass,
                "Blood Moon Runic Seal Outer",
                this.ruleta$bloodRunesTexture,
                runeOuterMat,
                new Vector4f(1.0f, 0.24f, 0.24f, (0.52f + 0.24f * heartbeat) * intensity * rainBrightness)
        );

        mvStack.pushMatrix();
        mvStack.mul(poseStack.last().pose());
        mvStack.translate(0.0f, 100.30f, 0.0f);
        mvStack.rotateY(runeAngleInner);
        mvStack.scale(innerRuneSize, 1.0f, innerRuneSize);
        Matrix4f runeInnerMat = new Matrix4f(mvStack);
        mvStack.popMatrix();
        ruleta$drawTexturedSkyQuad(
                renderPass,
                "Blood Moon Runic Seal Inner",
                this.ruleta$bloodRunesTexture,
                runeInnerMat,
                new Vector4f(1.0f, 0.45f, 0.25f, (0.36f + 0.18f * heartbeat) * intensity * rainBrightness)
        );

        // Segundo pase de la textura de la luna detrás de ella con blending aditivo (CELESTIAL = SRC_ALPHA, ONE)
        float haloQuadSize = moonSize * 1.30f * pulse;
        Matrix4f haloQuadMat = this.applyCelestialBodyTransform(poseStack, 100.2f, haloQuadSize);
        GpuBufferSlice haloQuadSlice = RenderSystem.getDynamicUniforms().writeTransform(
                haloQuadMat,
                new Vector4f(1.0f, 0.12f, 0.12f, (0.28f + 0.20f * heartbeat) * intensity * rainBrightness)
        );
        this.drawCelestialBody(
                () -> "Blood Moon Halo",
                renderPass,
                haloQuadSlice,
                this.quadIndices.getBuffer(6),
                this.moonBuffer,
                baseVertex
        );

        // 2. Pase principal de la Luna de Sangre (escala 1.6x y multiplicador RGB 1.0f, 0.15f, 0.15f interpolado)
        Matrix4f moonMat = this.applyCelestialBodyTransform(poseStack, 100.0f, moonSize);
        float moonR = Mth.lerp(intensity, 1.0f, 1.0f);
        float moonG = Mth.lerp(intensity, 1.0f, 0.15f);
        float moonB = Mth.lerp(intensity, 1.0f, 0.15f);
        GpuBufferSlice moonSlice = RenderSystem.getDynamicUniforms().writeTransform(
                moonMat,
                new Vector4f(moonR, moonG, moonB, rainBrightness)
        );
        this.drawCelestialBody(
                () -> "Blood Moon",
                renderPass,
                moonSlice,
                this.quadIndices.getBuffer(6),
                this.moonBuffer,
                baseVertex
        );

        ci.cancel();
    }

    @Inject(method = "renderSun", at = @At("HEAD"), cancellable = true)
    private void ruleta$renderizarEclipseSolar(
            RenderPass renderPass,
            float rainBrightness,
            PoseStack poseStack,
            CallbackInfo ci
    ) {
        float eclipseIntensity = BloodMoonClientState.getCurrentEclipseRenderIntensity();
        if (eclipseIntensity <= 0.001f) {
            return;
        }

        // Escala de la matriz de renderizado del sol aumentada a 2.8x (entre 2.5x y 3.0x) respecto al tamaño normal (30.0f)
        float scaleMultiplier = Mth.lerp(eclipseIntensity, 1.0f, 2.8f);
        float sunSize = 30.0f * scaleMultiplier;

        double worldTime = BloodMoonClientState.getSmoothWorldTime();
        float pulse = 1.0f + 0.045f * (float) Math.sin(worldTime * 0.06);

        Matrix4fStack mvStack = RenderSystem.getModelViewStack();

        // 1. Corona exterior amplia (resplandor naranja rojizo difuso alrededor del eclipse)
        GpuBuffer coronaBuffer = ruleta$getOrCreateEclipseCoronaBuffer();
        float outerCoronaRadius = sunSize * 1.35f * pulse;
        mvStack.pushMatrix();
        mvStack.mul(poseStack.last().pose());
        mvStack.translate(0.0f, 100.4f, 0.0f);
        mvStack.scale(outerCoronaRadius, 1.0f, outerCoronaRadius);
        Matrix4f outerCoronaMat = new Matrix4f(mvStack);
        mvStack.popMatrix();

        GpuBufferSlice outerCoronaSlice = RenderSystem.getDynamicUniforms().writeTransform(
                outerCoronaMat,
                new Vector4f(1.0f, 0.24f, 0.04f, 0.65f * eclipseIntensity * rainBrightness)
        );
        renderPass.pushDebugGroup(() -> "Solar Eclipse Outer Corona");
        renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.SUNRISE_SUNSET));
        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setUniform("DynamicTransforms", outerCoronaSlice);
        renderPass.setVertexBuffer(0, coronaBuffer.slice());
        renderPass.draw(ECLIPSE_DISC_VERTEX_COUNT, 1, 0, 0);
        renderPass.popDebugGroup();

        // 1b. Rayos de corona solar giratorios detrás del eclipse (2 capas contra-rotantes)
        float raySpinA = (float) (worldTime * 0.0065);
        float raySpinB = (float) (-worldTime * 0.0042 + 0.7);
        float raySizeA = sunSize * 1.65f * pulse;
        float raySizeB = sunSize * 1.38f * (1.0f + 0.03f * (float) Math.cos(worldTime * 0.08));

        mvStack.pushMatrix();
        mvStack.mul(poseStack.last().pose());
        mvStack.translate(0.0f, 100.32f, 0.0f);
        mvStack.rotateY(raySpinA);
        mvStack.scale(raySizeA, 1.0f, raySizeA);
        Matrix4f raysMatA = new Matrix4f(mvStack);
        mvStack.popMatrix();
        ruleta$drawTexturedSkyQuad(
                renderPass,
                "Solar Eclipse Corona Rays Primary",
                this.ruleta$eclipseRaysTexture,
                raysMatA,
                new Vector4f(1.0f, 0.52f, 0.12f, 0.78f * eclipseIntensity * rainBrightness)
        );

        mvStack.pushMatrix();
        mvStack.mul(poseStack.last().pose());
        mvStack.translate(0.0f, 100.26f, 0.0f);
        mvStack.rotateY(raySpinB);
        mvStack.scale(raySizeB, 1.0f, raySizeB);
        Matrix4f raysMatB = new Matrix4f(mvStack);
        mvStack.popMatrix();
        ruleta$drawTexturedSkyQuad(
                renderPass,
                "Solar Eclipse Corona Rays Secondary",
                this.ruleta$eclipseRaysTexture,
                raysMatB,
                new Vector4f(1.0f, 0.76f, 0.30f, 0.52f * eclipseIntensity * rainBrightness)
        );

        // 2. Transición suave del sol cuadrado vanilla: se desvanece rápidamente (0.0 -> 0.40) para que nunca se vean
        //    esquinas cuadradas asomándose detrás del disco circular del eclipse
        float vanillaSunFade = Mth.clamp(1.0f - eclipseIntensity * 2.5f, 0.0f, 1.0f);
        if (vanillaSunFade > 0.005f) {
            Matrix4f sunGlowMat = this.applyCelestialBodyTransform(poseStack, 100.2f, sunSize * 0.92f * pulse);
            float sunG = Mth.lerp(eclipseIntensity, 1.0f, 0.45f);
            float sunB = Mth.lerp(eclipseIntensity, 1.0f, 0.12f);
            GpuBufferSlice sunGlowSlice = RenderSystem.getDynamicUniforms().writeTransform(
                    sunGlowMat,
                    new Vector4f(1.0f, sunG, sunB, vanillaSunFade * rainBrightness)
            );
            this.drawCelestialBody(
                    () -> "Solar Eclipse Sun Fadeout",
                    renderPass,
                    sunGlowSlice,
                    this.quadIndices.getBuffer(6),
                    this.sunBuffer,
                    0
            );
        }

        // 3. Anillo de fuego incandescente circular (reemplaza suavemente al sol vanilla y rodea la luna negra)
        GpuBuffer fireRingBuffer = ruleta$getOrCreateEclipseFireRingBuffer();
        float firePresence = Mth.clamp(eclipseIntensity * 1.45f, 0.0f, 1.0f);
        float fireRingRadius = sunSize * 0.54f * (1.0f + 0.02f * (float) Math.sin(worldTime * 0.09));
        mvStack.pushMatrix();
        mvStack.mul(poseStack.last().pose());
        mvStack.translate(0.0f, 99.95f, 0.0f);
        mvStack.scale(fireRingRadius, 1.0f, fireRingRadius);
        Matrix4f fireRingMat = new Matrix4f(mvStack);
        mvStack.popMatrix();

        GpuBufferSlice fireRingSlice = RenderSystem.getDynamicUniforms().writeTransform(
                fireRingMat,
                new Vector4f(1.0f, 0.52f, 0.10f, 0.96f * firePresence * rainBrightness)
        );
        renderPass.pushDebugGroup(() -> "Solar Eclipse Ring of Fire");
        renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.SUNRISE_SUNSET));
        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setUniform("DynamicTransforms", fireRingSlice);
        renderPass.setVertexBuffer(0, fireRingBuffer.slice());
        renderPass.draw(ECLIPSE_DISC_VERTEX_COUNT, 1, 0, 0);
        renderPass.popDebugGroup();

        // 4. Disco lunar negro entrando en tránsito astronómico lento y fluido desde un costado hasta encajar en el centro
        GpuBuffer blackDiscBuffer = ruleta$getOrCreateEclipseBlackDiscBuffer();
        float transitT = Mth.clamp((eclipseIntensity - 0.05f) / 0.93f, 0.0f, 1.0f);
        // Smoothstep cúbico para entrada y cierre suaves con precisión astronómica
        float transitEase = transitT * transitT * (3.0f - 2.0f * transitT);
        float blackDiscRadius = sunSize * 0.395f;
        float slideOffsetX = Mth.lerp(transitEase, sunSize * 0.92f, 0.0f);
        float slideOffsetZ = Mth.lerp(transitEase, -sunSize * 0.34f, 0.0f);
        float discAlpha = Mth.clamp(eclipseIntensity * 2.2f, 0.0f, 1.0f) * rainBrightness;

        mvStack.pushMatrix();
        mvStack.mul(poseStack.last().pose());
        mvStack.translate(slideOffsetX, 99.7f, slideOffsetZ);
        mvStack.scale(blackDiscRadius, 1.0f, blackDiscRadius);
        Matrix4f blackDiscMat = new Matrix4f(mvStack);
        mvStack.popMatrix();

        // Guardar matriz y opacidad para ocluir las estrellas que Minecraft dibuja después en renderStars()
        this.ruleta$lastEclipseBlackDiscMat = blackDiscMat;
        this.ruleta$lastEclipseDiscAlpha = discAlpha;

        GpuBufferSlice blackDiscSlice = RenderSystem.getDynamicUniforms().writeTransform(
                blackDiscMat,
                new Vector4f(1.0f, 1.0f, 1.0f, discAlpha)
        );
        renderPass.pushDebugGroup(() -> "Solar Eclipse Black Lunar Disc");
        renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.SUNRISE_SUNSET));
        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setUniform("DynamicTransforms", blackDiscSlice);
        renderPass.setVertexBuffer(0, blackDiscBuffer.slice());
        renderPass.draw(ECLIPSE_DISC_VERTEX_COUNT, 1, 0, 0);
        renderPass.popDebugGroup();

        // 5. Efecto "Anillo de Diamante" alineado exactamente en el último borde (-X, +Z) que termina de cubrir la luna
        float diamondFlash = BloodMoonClientState.getDiamondRingFlash();
        if (diamondFlash > 0.005f) {
            float flareSize = sunSize * (0.58f + 0.68f * diamondFlash);
            float rimX = -blackDiscRadius * 0.88f;
            float rimZ = blackDiscRadius * 0.33f;
            mvStack.pushMatrix();
            mvStack.mul(poseStack.last().pose());
            mvStack.translate(rimX, 99.55f, rimZ);
            mvStack.rotateY((float) (worldTime * 0.03));
            mvStack.scale(flareSize, 1.0f, flareSize);
            Matrix4f diamondMat = new Matrix4f(mvStack);
            mvStack.popMatrix();

            ruleta$drawTexturedSkyQuad(
                    renderPass,
                    "Solar Eclipse Diamond Ring Flare",
                    this.ruleta$eclipseDiamondTexture,
                    diamondMat,
                    new Vector4f(1.0f, 0.92f, 0.72f, diamondFlash * rainBrightness)
            );
        }

        ci.cancel();
    }

    @Inject(method = "close", at = @At("TAIL"))
    private void ruleta$liberarBuffersLunaSangre(CallbackInfo ci) {
        if (this.ruleta$horizonGlowBuffer != null) {
            this.ruleta$horizonGlowBuffer.close();
            this.ruleta$horizonGlowBuffer = null;
        }
        if (this.ruleta$moonAuraBuffer != null) {
            this.ruleta$moonAuraBuffer.close();
            this.ruleta$moonAuraBuffer = null;
        }
        if (this.ruleta$eclipseCoronaBuffer != null) {
            this.ruleta$eclipseCoronaBuffer.close();
            this.ruleta$eclipseCoronaBuffer = null;
        }
        if (this.ruleta$eclipseFireRingBuffer != null) {
            this.ruleta$eclipseFireRingBuffer.close();
            this.ruleta$eclipseFireRingBuffer = null;
        }
        if (this.ruleta$eclipseBlackDiscBuffer != null) {
            this.ruleta$eclipseBlackDiscBuffer.close();
            this.ruleta$eclipseBlackDiscBuffer = null;
        }
        if (this.ruleta$unitTexturedQuadBuffer != null) {
            this.ruleta$unitTexturedQuadBuffer.close();
            this.ruleta$unitTexturedQuadBuffer = null;
        }
        this.ruleta$bloodNebulaTexture = null;
        this.ruleta$bloodRunesTexture = null;
        this.ruleta$eclipseRaysTexture = null;
        this.ruleta$eclipseDiamondTexture = null;
    }

    /** Curva orgánica de doble latido cardíaco (lub-dub) en rango [0, 1]. */
    @Unique
    private static float ruleta$calcularHeartbeat(double worldTime) {
        double phase = (worldTime * 0.028) % 1.0;
        if (phase < 0.0) phase += 1.0;
        double beat1 = Math.exp(-Math.pow((phase - 0.14) / 0.055, 2.0));
        double beat2 = 0.65 * Math.exp(-Math.pow((phase - 0.30) / 0.065, 2.0));
        return Mth.clamp((float) (beat1 + beat2), 0.0f, 1.0f);
    }

    @Unique
    private void ruleta$drawTexturedSkyQuad(
            RenderPass renderPass,
            String debugLabel,
            AbstractTexture tex,
            Matrix4f transform,
            Vector4f colorTint
    ) {
        if (colorTint.w <= 0.002f || tex == null || this.ruleta$unitTexturedQuadBuffer == null) {
            return;
        }
        GpuBuffer quadBuf = this.ruleta$unitTexturedQuadBuffer;
        GpuBufferSlice transformSlice = RenderSystem.getDynamicUniforms().writeTransform(transform, colorTint);

        renderPass.pushDebugGroup(() -> debugLabel);
        renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.CELESTIAL));
        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setUniform("DynamicTransforms", transformSlice);
        renderPass.setUniform("Sampler0", tex.getTextureView(), tex.getSampler());
        renderPass.setVertexBuffer(0, quadBuf.slice());
        renderPass.setIndexBuffer(this.quadIndices.getBuffer(6), this.quadIndices.type());
        renderPass.drawIndexed(6, 1, 0, 0, 0);
        renderPass.popDebugGroup();
    }

    @Unique
    private GpuBuffer ruleta$getOrCreateUnitTexturedQuadBuffer() {
        if (this.ruleta$unitTexturedQuadBuffer != null) {
            return this.ruleta$unitTexturedQuadBuffer;
        }
        int vertexSize = DefaultVertexFormat.POSITION_TEX.getVertexSize();
        try (ByteBufferBuilder byteBuilder = ByteBufferBuilder.exactlySized(4 * vertexSize)) {
            BufferBuilder builder = new BufferBuilder(
                    byteBuilder,
                    PrimitiveTopology.QUADS,
                    DefaultVertexFormat.POSITION_TEX
            );
            builder.addVertex(-1.0f, 0.0f, -1.0f).setUv(0.0f, 0.0f);
            builder.addVertex(1.0f, 0.0f, -1.0f).setUv(1.0f, 0.0f);
            builder.addVertex(1.0f, 0.0f, 1.0f).setUv(1.0f, 1.0f);
            builder.addVertex(-1.0f, 0.0f, 1.0f).setUv(0.0f, 1.0f);

            try (MeshData meshData = builder.buildOrThrow()) {
                this.ruleta$unitTexturedQuadBuffer = RenderSystem.getDevice().createBuffer(
                        () -> "PermaRuleta Sky VFX Quad VBO",
                        32,
                        meshData.vertexBuffer()
                );
            }
        }
        return this.ruleta$unitTexturedQuadBuffer;
    }

    @Unique
    private GpuBuffer ruleta$getOrCreateHorizonBuffer() {
        if (this.ruleta$horizonGlowBuffer != null) {
            return this.ruleta$horizonGlowBuffer;
        }
        int vertexSize = DefaultVertexFormat.POSITION_COLOR.getVertexSize();
        try (ByteBufferBuilder byteBuilder = ByteBufferBuilder.exactlySized(HORIZON_VERTEX_COUNT * vertexSize)) {
            BufferBuilder builder = new BufferBuilder(
                    byteBuilder,
                    PrimitiveTopology.TRIANGLE_FAN,
                    DefaultVertexFormat.POSITION_COLOR
            );
            int opaqueWhite = ARGB.white(1.0f);
            int transparentWhite = ARGB.white(0.0f);

            // Centro inferior para que en el horizonte (Y=0) brille intenso y se difumine hacia el zenith
            builder.addVertex(0.0f, -35.0f, 0.0f).setColor(opaqueWhite);
            for (int i = 0; i <= HORIZON_SEGMENTS; i++) {
                float angle = (float) i * ((float) Math.PI * 2.0f) / (float) HORIZON_SEGMENTS;
                float x = 100.0f * Mth.cos(angle);
                float z = 100.0f * Mth.sin(angle);
                builder.addVertex(x, 55.0f, z).setColor(transparentWhite);
            }

            try (MeshData meshData = builder.buildOrThrow()) {
                this.ruleta$horizonGlowBuffer = RenderSystem.getDevice().createBuffer(
                        () -> "Blood Moon Horizon Glow VBO",
                        32,
                        meshData.vertexBuffer()
                );
            }
        }
        return this.ruleta$horizonGlowBuffer;
    }

    @Unique
    private GpuBuffer ruleta$getOrCreateMoonAuraBuffer() {
        if (this.ruleta$moonAuraBuffer != null) {
            return this.ruleta$moonAuraBuffer;
        }
        int vertexSize = DefaultVertexFormat.POSITION_COLOR.getVertexSize();
        try (ByteBufferBuilder byteBuilder = ByteBufferBuilder.exactlySized(AURA_VERTEX_COUNT * vertexSize)) {
            BufferBuilder builder = new BufferBuilder(
                    byteBuilder,
                    PrimitiveTopology.TRIANGLE_FAN,
                    DefaultVertexFormat.POSITION_COLOR
            );
            int centerColor = ARGB.white(1.0f);
            int edgeColor = ARGB.white(0.0f);

            builder.addVertex(0.0f, 0.0f, 0.0f).setColor(centerColor);
            for (int i = 0; i <= AURA_SEGMENTS; i++) {
                float angle = (float) i * ((float) Math.PI * 2.0f) / (float) AURA_SEGMENTS;
                builder.addVertex(Mth.cos(angle), 0.0f, Mth.sin(angle)).setColor(edgeColor);
            }

            try (MeshData meshData = builder.buildOrThrow()) {
                this.ruleta$moonAuraBuffer = RenderSystem.getDevice().createBuffer(
                        () -> "Blood Moon Radial Aura VBO",
                        32,
                        meshData.vertexBuffer()
                );
            }
        }
        return this.ruleta$moonAuraBuffer;
    }

    @Unique
    private GpuBuffer ruleta$getOrCreateEclipseCoronaBuffer() {
        if (this.ruleta$eclipseCoronaBuffer != null) {
            return this.ruleta$eclipseCoronaBuffer;
        }
        int vertexSize = DefaultVertexFormat.POSITION_COLOR.getVertexSize();
        try (ByteBufferBuilder byteBuilder = ByteBufferBuilder.exactlySized(ECLIPSE_DISC_VERTEX_COUNT * vertexSize)) {
            BufferBuilder builder = new BufferBuilder(
                    byteBuilder,
                    PrimitiveTopology.TRIANGLE_FAN,
                    DefaultVertexFormat.POSITION_COLOR
            );
            int centerColor = ARGB.color(255, 255, 160, 50);
            int edgeColor = ARGB.color(0, 180, 20, 0);

            builder.addVertex(0.0f, 0.0f, 0.0f).setColor(centerColor);
            for (int i = 0; i <= ECLIPSE_SEGMENTS; i++) {
                float angle = (float) i * ((float) Math.PI * 2.0f) / (float) ECLIPSE_SEGMENTS;
                builder.addVertex(Mth.cos(angle), 0.0f, Mth.sin(angle)).setColor(edgeColor);
            }

            try (MeshData meshData = builder.buildOrThrow()) {
                this.ruleta$eclipseCoronaBuffer = RenderSystem.getDevice().createBuffer(
                        () -> "Solar Eclipse Outer Corona VBO",
                        32,
                        meshData.vertexBuffer()
                );
            }
        }
        return this.ruleta$eclipseCoronaBuffer;
    }

    @Unique
    private GpuBuffer ruleta$getOrCreateEclipseFireRingBuffer() {
        if (this.ruleta$eclipseFireRingBuffer != null) {
            return this.ruleta$eclipseFireRingBuffer;
        }
        int vertexSize = DefaultVertexFormat.POSITION_COLOR.getVertexSize();
        try (ByteBufferBuilder byteBuilder = ByteBufferBuilder.exactlySized(ECLIPSE_DISC_VERTEX_COUNT * vertexSize)) {
            BufferBuilder builder = new BufferBuilder(
                    byteBuilder,
                    PrimitiveTopology.TRIANGLE_FAN,
                    DefaultVertexFormat.POSITION_COLOR
            );
            // Núcleo incandescente blanco-anaranjado que se funde hacia naranja rojizo en el borde del anillo
            int centerColor = ARGB.color(255, 255, 230, 160);
            int edgeColor = ARGB.color(0, 255, 60, 10);

            builder.addVertex(0.0f, 0.0f, 0.0f).setColor(centerColor);
            for (int i = 0; i <= ECLIPSE_SEGMENTS; i++) {
                float angle = (float) i * ((float) Math.PI * 2.0f) / (float) ECLIPSE_SEGMENTS;
                builder.addVertex(Mth.cos(angle), 0.0f, Mth.sin(angle)).setColor(edgeColor);
            }

            try (MeshData meshData = builder.buildOrThrow()) {
                this.ruleta$eclipseFireRingBuffer = RenderSystem.getDevice().createBuffer(
                        () -> "Solar Eclipse Fire Ring VBO",
                        32,
                        meshData.vertexBuffer()
                );
            }
        }
        return this.ruleta$eclipseFireRingBuffer;
    }

    @Unique
    private GpuBuffer ruleta$getOrCreateEclipseBlackDiscBuffer() {
        if (this.ruleta$eclipseBlackDiscBuffer != null) {
            return this.ruleta$eclipseBlackDiscBuffer;
        }
        int vertexSize = DefaultVertexFormat.POSITION_COLOR.getVertexSize();
        try (ByteBufferBuilder byteBuilder = ByteBufferBuilder.exactlySized(ECLIPSE_DISC_VERTEX_COUNT * vertexSize)) {
            BufferBuilder builder = new BufferBuilder(
                    byteBuilder,
                    PrimitiveTopology.TRIANGLE_FAN,
                    DefaultVertexFormat.POSITION_COLOR
            );
            // Disco central completamente negro opaco (#020202) para bloquear la luz solar en el centro
            int pitchBlack = ARGB.color(255, 2, 2, 2);

            builder.addVertex(0.0f, 0.0f, 0.0f).setColor(pitchBlack);
            for (int i = 0; i <= ECLIPSE_SEGMENTS; i++) {
                float angle = (float) i * ((float) Math.PI * 2.0f) / (float) ECLIPSE_SEGMENTS;
                builder.addVertex(Mth.cos(angle), 0.0f, Mth.sin(angle)).setColor(pitchBlack);
            }

            try (MeshData meshData = builder.buildOrThrow()) {
                this.ruleta$eclipseBlackDiscBuffer = RenderSystem.getDevice().createBuffer(
                        () -> "Solar Eclipse Black Disc VBO",
                        32,
                        meshData.vertexBuffer()
                );
            }
        }
        return this.ruleta$eclipseBlackDiscBuffer;
    }
}
