package com.luishugomod01.client.hombre;

import com.luishugomod01.entity.RocaProyectilEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

/**
 * Renderiza la roca gigante 3D girando en el aire tras ser arrojada por el Hombre Roca.
 */
public class RocaProyectilRenderer extends EntityRenderer<RocaProyectilEntity, EntityRenderState> {

    private final RockProjectileModel rockModel;

    public RocaProyectilRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.rockModel = new RockProjectileModel(createRockLayer().bakeRoot());
    }

    private static LayerDefinition createRockLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("rock", CubeListBuilder.create()
                .texOffs(64, 94).addBox(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F, new CubeDeformation(-0.05F))
                .texOffs(100, 94).addBox(-3.5F, -5.7F, -3.5F, 7.0F, 3.0F, 7.0F, new CubeDeformation(-0.05F))
                .texOffs(64, 112).addBox(-3.0F, -3.0F, -5.8F, 6.0F, 6.0F, 3.0F, new CubeDeformation(-0.05F))
                .texOffs(82, 112).addBox(-5.8F, -3.0F, -3.0F, 3.0F, 6.0F, 6.0F, new CubeDeformation(-0.05F))
                .texOffs(100, 112).addBox(-3.0F, 2.7F, -3.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(-0.05F)),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.35F, 0.0F);
        collector.submitModel(
                this.rockModel,
                state,
                poseStack,
                this.rockModel.renderType(HombreRocaModel.TEXTURE),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                -1,
                null,
                state.outlineColor
        );
        poseStack.popPose();
        super.submit(state, poseStack, collector, cameraState);
    }

    private static final class RockProjectileModel extends EntityModel<EntityRenderState> {
        private final ModelPart rock;

        private RockProjectileModel(ModelPart root) {
            super(root);
            this.rock = root.getChild("rock");
        }

        @Override
        public void setupAnim(EntityRenderState state) {
            super.setupAnim(state);
            float spin = state.ageInTicks * 0.38F;
            this.rock.yRot = spin;
            this.rock.xRot = spin * 1.3F;
        }
    }
}
