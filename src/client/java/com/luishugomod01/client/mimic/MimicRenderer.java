package com.luishugomod01.client.mimic;

import com.luishugomod01.entity.MimicEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class MimicRenderer extends MobRenderer<MimicEntity, MimicRenderState, MimicModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/mimic/mimic.png");

    public MimicRenderer(EntityRendererProvider.Context context) {
        super(context, new MimicModel(context.bakeLayer(MimicModel.LAYER_LOCATION)), 0.6F);
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
