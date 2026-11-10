package com.luishugomod01.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class CreeperAvisponWingsLayer extends RenderLayer<CreeperRenderState, CreeperModel> {
    private static final Identifier BEE_TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/bee/bee.png");
    private final WingsModel wingsModel;

    public CreeperAvisponWingsLayer(RenderLayerParent<CreeperRenderState, CreeperModel> parent) {
        super(parent);
        this.wingsModel = new WingsModel(createWingsMesh().bakeRoot());
    }

    private static LayerDefinition createWingsMesh() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        // Ubicado exactamente en la parte superior trasera del torso del creeper (Y=9.0, Z=2.0)
        PartDefinition bone = root.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0f, 9.0f, 2.0f, 0.2f, 0.0f, 0.0f));
        CubeDeformation def = new CubeDeformation(0.001f);
        // Alas de abeja mapeadas a UV (0, 18) de la textura vanilla (64x64)
        bone.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 18).addBox(-9.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, def), PartPose.offsetAndRotation(-1.5f, 0.0f, 0.0f, 0.0f, -0.2618f, 0.0f));
        bone.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 18).mirror().addBox(0.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, def), PartPose.offsetAndRotation(1.5f, 0.0f, 0.0f, 0.0f, 0.2618f, 0.0f));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, CreeperRenderState state, float yRot, float xRot) {
        boolean esAvispon = (state instanceof CreeperRenderStateAccessor acc && acc.ruleta$isAvispon())
                || (state.scale > 0.25f && state.scale < 0.31f);
        if (!esAvispon) {
            return;
        }

        this.wingsModel.setupAnim(state);
        coloredCutoutModelCopyLayerRender(
                this.wingsModel,
                BEE_TEXTURE,
                poseStack,
                submitNodeCollector,
                packedLight,
                state,
                -1,
                1
        );
    }

    public static class WingsModel extends Model<CreeperRenderState> {
        private final ModelPart rightWing;
        private final ModelPart leftWing;

        public WingsModel(ModelPart root) {
            super(root, RenderTypes::entityCutout);
            ModelPart bone = root.getChild("bone");
            this.rightWing = bone.getChild("right_wing");
            this.leftWing = bone.getChild("left_wing");
        }

        @Override
        public void setupAnim(CreeperRenderState state) {
            super.setupAnim(state);
            float age = state.ageInTicks;
            if (state instanceof CreeperRenderStateAccessor acc && acc.ruleta$getAgeInTicks() > 0) {
                age = acc.ruleta$getAgeInTicks();
            }
            float flap = Mth.cos(age * 2.3f) * (float) Math.PI * 0.22f;
            this.rightWing.zRot = flap;
            this.leftWing.zRot = -flap;
            this.rightWing.xRot = 0.25f;
            this.leftWing.xRot = 0.25f;
        }
    }
}
