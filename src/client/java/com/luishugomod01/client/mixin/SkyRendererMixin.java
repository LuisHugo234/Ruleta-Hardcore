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

    // Cielo Eclipse Solar: Negro profundo / Gris carbón (#050505 / RGB: 5, 5, 5)
    @Unique
    private static final float ECLIPSE_ZENITH_R = 5.0f / 255.0f;
    @Unique
    private static final float ECLIPSE_ZENITH_G = 5.0f / 255.0f;
    @Unique
    private static final float ECLIPSE_ZENITH_B = 5.0f / 255.0f;

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

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void ruleta$tintarZenithLunaSangre(
            ClientLevel level,
            float partialTick,
            Camera camera,
            SkyRenderState state,
            CallbackInfo ci
    ) {
        if (!level.dimension().equals(Level.OVERWORLD) || state.skyColor == null) {
            return;
        }
        float eclipseIntensity = BloodMoonClientState.getEclipseIntensity(partialTick);
        if (eclipseIntensity > 0.001f) {
            float er = Mth.lerp(eclipseIntensity, state.skyColor.x(), ECLIPSE_ZENITH_R);
            float eg = Mth.lerp(eclipseIntensity, state.skyColor.y(), ECLIPSE_ZENITH_G);
            float eb = Mth.lerp(eclipseIntensity, state.skyColor.z(), ECLIPSE_ZENITH_B);
            state.skyColor = new Vector3f(er, eg, eb);
        }

        float intensity = BloodMoonClientState.getIntensity(partialTick);
        if (intensity <= 0.001f) {
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

        // 2. Resplandor solar con blending aditivo (RenderPipelines.CELESTIAL) teñido de naranja rojizo brillante
        Matrix4f sunGlowMat = this.applyCelestialBodyTransform(poseStack, 100.2f, sunSize * pulse);
        float sunR = Mth.lerp(eclipseIntensity, 1.0f, 1.0f);
        float sunG = Mth.lerp(eclipseIntensity, 1.0f, 0.36f);
        float sunB = Mth.lerp(eclipseIntensity, 1.0f, 0.06f);
        GpuBufferSlice sunGlowSlice = RenderSystem.getDynamicUniforms().writeTransform(
                sunGlowMat,
                new Vector4f(sunR, sunG, sunB, rainBrightness)
        );
        this.drawCelestialBody(
                () -> "Solar Eclipse Sun Glow",
                renderPass,
                sunGlowSlice,
                this.quadIndices.getBuffer(6),
                this.sunBuffer,
                0
        );

        // Segundo pase aditivo más concentrado para intensificar el anillo de fuego
        Matrix4f innerGlowMat = this.applyCelestialBodyTransform(poseStack, 100.1f, sunSize * 0.72f);
        GpuBufferSlice innerGlowSlice = RenderSystem.getDynamicUniforms().writeTransform(
                innerGlowMat,
                new Vector4f(1.0f, 0.58f, 0.14f, 0.90f * eclipseIntensity * rainBrightness)
        );
        this.drawCelestialBody(
                () -> "Solar Eclipse Inner Additive Ring",
                renderPass,
                innerGlowSlice,
                this.quadIndices.getBuffer(6),
                this.sunBuffer,
                0
        );

        // 3. Anillo de fuego incandescente (borde naranja-rojizo resplandeciente justo alrededor del disco oscuro)
        GpuBuffer fireRingBuffer = ruleta$getOrCreateEclipseFireRingBuffer();
        float fireRingRadius = sunSize * 0.54f * (1.0f + 0.02f * (float) Math.sin(worldTime * 0.09));
        mvStack.pushMatrix();
        mvStack.mul(poseStack.last().pose());
        mvStack.translate(0.0f, 99.9f, 0.0f);
        mvStack.scale(fireRingRadius, 1.0f, fireRingRadius);
        Matrix4f fireRingMat = new Matrix4f(mvStack);
        mvStack.popMatrix();

        GpuBufferSlice fireRingSlice = RenderSystem.getDynamicUniforms().writeTransform(
                fireRingMat,
                new Vector4f(1.0f, 0.45f, 0.08f, 0.95f * eclipseIntensity * rainBrightness)
        );
        renderPass.pushDebugGroup(() -> "Solar Eclipse Ring of Fire");
        renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.SUNRISE_SUNSET));
        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setUniform("DynamicTransforms", fireRingSlice);
        renderPass.setVertexBuffer(0, fireRingBuffer.slice());
        renderPass.draw(ECLIPSE_DISC_VERTEX_COUNT, 1, 0, 0);
        renderPass.popDebugGroup();

        // 4. Disco central completamente oscuro (luna cubriendo el sol gradualmente con eclipseIntensity)
        GpuBuffer blackDiscBuffer = ruleta$getOrCreateEclipseBlackDiscBuffer();
        float blackDiscRadius = sunSize * 0.39f * Mth.lerp(eclipseIntensity, 0.15f, 1.0f);
        mvStack.pushMatrix();
        mvStack.mul(poseStack.last().pose());
        mvStack.translate(0.0f, 99.7f, 0.0f);
        mvStack.scale(blackDiscRadius, 1.0f, blackDiscRadius);
        Matrix4f blackDiscMat = new Matrix4f(mvStack);
        mvStack.popMatrix();

        GpuBufferSlice blackDiscSlice = RenderSystem.getDynamicUniforms().writeTransform(
                blackDiscMat,
                new Vector4f(1.0f, 1.0f, 1.0f, Mth.clamp(eclipseIntensity * 1.15f, 0.0f, 1.0f) * rainBrightness)
        );
        renderPass.pushDebugGroup(() -> "Solar Eclipse Black Lunar Disc");
        renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.SUNRISE_SUNSET));
        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setUniform("DynamicTransforms", blackDiscSlice);
        renderPass.setVertexBuffer(0, blackDiscBuffer.slice());
        renderPass.draw(ECLIPSE_DISC_VERTEX_COUNT, 1, 0, 0);
        renderPass.popDebugGroup();

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
