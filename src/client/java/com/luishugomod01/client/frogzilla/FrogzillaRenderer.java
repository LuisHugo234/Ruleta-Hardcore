package com.luishugomod01.client.frogzilla;

import com.luishugomod01.entity.FrogzillaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class FrogzillaRenderer extends MobRenderer<FrogzillaEntity, FrogzillaRenderState, FrogzillaModel> {

    private static final float VISUAL_SCALE = 1.75F;

    public FrogzillaRenderer(EntityRendererProvider.Context context) {
        super(context, new FrogzillaModel(context.bakeLayer(FrogzillaModel.LAYER_LOCATION)), 1.15F);
    }

    @Override
    public FrogzillaRenderState createRenderState() {
        return new FrogzillaRenderState();
    }

    @Override
    public void extractRenderState(FrogzillaEntity entity, FrogzillaRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.mouthOpen = Mth.lerp(partialTicks, entity.clientPrevMouthOpen, entity.clientMouthOpen);
        state.tongueBlocks = Mth.lerp(partialTicks, entity.clientPrevTongueBlocks, entity.clientTongueBlocks);
        state.tonguePitch = entity.getTonguePitch();
        state.hasSwallowed = entity.hasSwallowedPlayer();
        if (entity.isTongueLocked()) {
            state.bodyRot = entity.getTongueYaw();
            state.yRot = 0.0F;
        }
    }

    @Override
    protected void scale(FrogzillaRenderState state, PoseStack poseStack) {
        poseStack.scale(VISUAL_SCALE, VISUAL_SCALE, VISUAL_SCALE);
    }

    @Override
    public Identifier getTextureLocation(FrogzillaRenderState state) {
        return FrogzillaModel.TEXTURE;
    }
}
