package com.luishugomod01.client.mixin;

import com.luishugomod01.client.bloodmoon.BloodMoonClientState;
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
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
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

    @Unique
    private static final int HORIZON_SEGMENTS = 32;
    @Unique
    private static final int HORIZON_VERTEX_COUNT = HORIZON_SEGMENTS + 2;

    @Unique
    private static final int AURA_SEGMENTS = 24;
    @Unique
    private static final int AURA_VERTEX_COUNT = AURA_SEGMENTS + 2;

    @Shadow
    @Final
    private RenderSystem.AutoStorageIndexBuffer quadIndices;

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

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void ruleta$tintarZenithLunaSangre(
            ClientLevel level,
            float partialTick,
            Camera camera,
            SkyRenderState state,
            CallbackInfo ci
    ) {
        if (!level.dimension().equals(Level.OVERWORLD)) {
            return;
        }
        float intensity = BloodMoonClientState.getIntensity(partialTick);
        if (intensity <= 0.001f || state.skyColor == null) {
            return;
        }

        float r = Mth.lerp(intensity, state.skyColor.x(), ZENITH_R);
        float g = Mth.lerp(intensity, state.skyColor.y(), ZENITH_G);
        float b = Mth.lerp(intensity, state.skyColor.z(), ZENITH_B);
        state.skyColor = new Vector3f(r, g, b);
    }

    @Inject(method = "renderSkyDisc", at = @At("TAIL"))
    private void ruleta$renderizarResplandorHorizonteCarmesi(
            RenderPass renderPass,
            Vector3fc skyColor,
            CallbackInfo ci
    ) {
        float intensity = BloodMoonClientState.getCurrentRenderIntensity();
        if (intensity <= 0.001f) {
            return;
        }

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

        // Pulso senoidal leve basado en el tiempo del mundo: Math.sin(worldTime * 0.05)
        double worldTime = BloodMoonClientState.getSmoothWorldTime();
        float pulse = 1.0f + 0.07f * (float) Math.sin(worldTime * 0.05);

        // 1. Pase de resplandor / aura radial detrás de la luna (alpha 0.3f, aditivo)
        GpuBuffer auraBuffer = ruleta$getOrCreateMoonAuraBuffer();
        float auraRadius = moonSize * 1.95f * pulse;
        Matrix4fStack mvStack = RenderSystem.getModelViewStack();
        mvStack.pushMatrix();
        mvStack.mul(poseStack.last().pose());
        mvStack.translate(0.0f, 100.4f, 0.0f);
        mvStack.scale(auraRadius, 1.0f, auraRadius);
        Matrix4f auraMat = new Matrix4f(mvStack);
        mvStack.popMatrix();

        GpuBufferSlice radialAuraSlice = RenderSystem.getDynamicUniforms().writeTransform(
                auraMat,
                new Vector4f(1.0f, 0.08f, 0.10f, 0.30f * intensity * rainBrightness)
        );
        renderPass.pushDebugGroup(() -> "Blood Moon Radial Aura");
        renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.SUNRISE_SUNSET));
        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setUniform("DynamicTransforms", radialAuraSlice);
        renderPass.setVertexBuffer(0, auraBuffer.slice());
        renderPass.draw(AURA_VERTEX_COUNT, 1, 0, 0);
        renderPass.popDebugGroup();

        // Segundo pase de la textura de la luna detrás de ella con blending aditivo (CELESTIAL = SRC_ALPHA, ONE) y alpha 0.3f
        float haloQuadSize = moonSize * 1.28f * pulse;
        Matrix4f haloQuadMat = this.applyCelestialBodyTransform(poseStack, 100.2f, haloQuadSize);
        GpuBufferSlice haloQuadSlice = RenderSystem.getDynamicUniforms().writeTransform(
                haloQuadMat,
                new Vector4f(1.0f, 0.12f, 0.12f, 0.30f * intensity * rainBrightness)
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
}
