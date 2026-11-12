package com.luishugomod01.client.hombre;

import com.luishugomod01.entity.HombreRocaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class HombreRocaRenderer extends MobRenderer<HombreRocaEntity, HombreRocaRenderState, HombreRocaModel> {

    public HombreRocaRenderer(EntityRendererProvider.Context context) {
        super(context, new HombreRocaModel(context.bakeLayer(HombreRocaModel.LAYER_LOCATION)), 0.65F);
    }

    @Override
    public HombreRocaRenderState createRenderState() {
        return new HombreRocaRenderState();
    }

    @Override
    public void extractRenderState(HombreRocaEntity entity, HombreRocaRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
        state.throwAnimationState.copyFrom(entity.throwAnimationState);
    }

    @Override
    public Identifier getTextureLocation(HombreRocaRenderState state) {
        return HombreRocaModel.TEXTURE;
    }
}
