package com.luishugomod01.client.mimic;

import com.luishugomod01.entity.MimicEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class MimicRenderer extends MobRenderer<MimicEntity, MimicRenderState, MimicModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/mimic/mimic.png");
    private static final Identifier EYES_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/mimic/mimic_eyes.png");

    public MimicRenderer(EntityRendererProvider.Context context) {
        super(context, new MimicModel(context.bakeLayer(MimicModel.LAYER_LOCATION)), 0.6F);
        this.addLayer(new EyesLayer<>(this) {
            private final RenderType eyesType = RenderTypes.eyes(EYES_TEXTURE);

            @Override
            public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, MimicRenderState state, float yRot, float xRot) {
                if (!state.isDisguised) {
                    super.submit(poseStack, collector, packedLight, state, yRot, xRot);
                }
            }

            @Override
            public RenderType renderType() {
                return this.eyesType;
            }
        });
    }

    @Override
    public MimicRenderState createRenderState() {
        return new MimicRenderState();
    }

    @Override
    public void extractRenderState(MimicEntity entity, MimicRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isDisguised = entity.isDisguised();
        state.isWakingUp = entity.isWakingUp();

        state.chestIdleAnimationState.copyFrom(entity.chestIdleAnimationState);
        state.wakeUpAnimationState.copyFrom(entity.wakeUpAnimationState);
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
        state.biteAnimationState.copyFrom(entity.biteAnimationState);
    }

    @Override
    public Identifier getTextureLocation(MimicRenderState state) {
        return TEXTURE;
    }

    @Override
    protected float getShadowRadius(MimicRenderState state) {
        return state.isDisguised ? 0.0F : 0.9F;
    }

    @Override
    protected boolean shouldShowName(MimicEntity entity, double distance) {
        if (entity.isDisguised()) {
            return false;
        }
        return super.shouldShowName(entity, distance);
    }
}
