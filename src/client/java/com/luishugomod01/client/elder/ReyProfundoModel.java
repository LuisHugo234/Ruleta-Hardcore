package com.luishugomod01.client.elder;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.GuardianRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Modelo 3D del Rey de lo Profundo (Elder Guardian): soberano abisal blindado
 * en prismarina oscura y oro hundido, con corona real, aletas de leviatán,
 * gran ojo bioluminiscente móvil y cola en 4 segmentos.
 */
public class ReyProfundoModel extends EntityModel<GuardianRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("ruleta_hardcore", "rey_profundo"), "main");

    private final ModelPart head;
    private final ModelPart eye;
    private final ModelPart mandibleRight;
    private final ModelPart mandibleLeft;
    private final ModelPart crown;
    private final ModelPart finRight;
    private final ModelPart finLeft;
    private final ModelPart spikeTopF;
    private final ModelPart spikeTopB;
    private final ModelPart spikeBotF;
    private final ModelPart spikeBotB;
    private final ModelPart spikeRibR;
    private final ModelPart spikeRibL;
    private final ModelPart tail0;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tailFin;

    public ReyProfundoModel(ModelPart root) {
        super(root);
        ModelPart rootPart = root.getChild("root");
        this.head = rootPart.getChild("head");
        this.eye = this.head.getChild("eye");
        this.mandibleRight = this.head.getChild("mandible_right");
        this.mandibleLeft = this.head.getChild("mandible_left");
        this.crown = this.head.getChild("crown");
        this.finRight = this.head.getChild("fin_right");
        this.finLeft = this.head.getChild("fin_left");
        this.spikeTopF = this.head.getChild("spike_top_f");
        this.spikeTopB = this.head.getChild("spike_top_b");
        this.spikeBotF = this.head.getChild("spike_bot_f");
        this.spikeBotB = this.head.getChild("spike_bot_b");
        this.spikeRibR = this.head.getChild("spike_rib_r");
        this.spikeRibL = this.head.getChild("spike_rib_l");
        this.tail0 = this.head.getChild("tail0");
        this.tail1 = this.tail0.getChild("tail1");
        this.tail2 = this.tail1.getChild("tail2");
        this.tailFin = this.tail2.getChild("tail_fin");
    }

    public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -5.0F, -6.5F, 11.0F, 10.0F, 13.0F, new CubeDeformation(0.0F))
		.texOffs(128, 0).addBox(-4.5F, -6.2F, -5.1F, 9.0F, 2.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(168, 0).addBox(-4.5F, 4.3F, -4.8F, 9.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(206, 0).addBox(-6.9F, -3.5F, -4.7F, 2.0F, 7.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(230, 0).addBox(4.9F, -3.5F, -4.7F, 2.0F, 7.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(112, 168).addBox(-4.0F, -3.6F, -7.3F, 8.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 96).addBox(-5.0F, -4.3F, -7.8F, 10.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(24, 96).addBox(-5.4F, -2.0F, -6.8F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(32, 96).addBox(3.4F, -2.0F, -6.8F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.5F, 0.0F));

		head.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(192, 168).addBox(-2.0F, -2.0F, -0.7F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.1F, -7.0F));

		head.addOrReplaceChild("mandible_right", CubeListBuilder.create().texOffs(40, 96).addBox(-1.3F, -0.6F, -2.4F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8F, 3.9F, -5.5F, 0.0F, 0.2443F, -0.1396F));

		head.addOrReplaceChild("mandible_left", CubeListBuilder.create().texOffs(50, 96).addBox(-0.7F, -0.6F, -2.4F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8F, 3.9F, -5.5F, 0.0F, -0.2443F, 0.1396F));

		head.addOrReplaceChild("crown", CubeListBuilder.create().texOffs(60, 96).addBox(-4.0F, -1.7F, -3.2F, 8.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(90, 96).addBox(-1.0F, -5.5F, -2.4F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(98, 96).addBox(-4.3F, -4.3F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 105).addBox(2.3F, -4.3F, -2.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(8, 105).addBox(-4.1F, -3.4F, 1.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(16, 105).addBox(2.1F, -3.4F, 1.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(112, 216).addBox(-1.0F, -2.3F, -3.8F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, -2.5F, 0.1745F, 0.0F, 0.0F));

		head.addOrReplaceChild("fin_right", CubeListBuilder.create().texOffs(24, 105).addBox(-6.8F, -1.4F, -1.0F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(112, 88).addBox(-8.1F, -0.5F, -0.5F, 8.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.4F, 0.3F, -1.0F, 0.0F, 0.4189F, -0.2443F));

		head.addOrReplaceChild("fin_left", CubeListBuilder.create().texOffs(42, 105).addBox(-0.2F, -1.4F, -1.0F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(138, 88).addBox(0.1F, -0.5F, -0.5F, 8.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.4F, 0.3F, -1.0F, 0.0F, -0.4189F, 0.2443F));

		head.addOrReplaceChild("spike_top_f", CubeListBuilder.create().texOffs(0, 168).addBox(-1.0F, -4.4F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.3F, 0.6F, 0.6283F, 0.0F, 0.0F));

		head.addOrReplaceChild("spike_top_b", CubeListBuilder.create().texOffs(8, 168).addBox(-1.0F, -4.4F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.7F, 3.8F, 2.2689F, 0.0F, 0.0F));

		head.addOrReplaceChild("spike_bot_f", CubeListBuilder.create().texOffs(16, 168).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.5F, -2.2F, -2.5307F, 0.0F, 0.0F));

		head.addOrReplaceChild("spike_bot_b", CubeListBuilder.create().texOffs(24, 168).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.3F, 3.5F, -2.1817F, 0.0F, 0.0F));

		head.addOrReplaceChild("spike_rib_r", CubeListBuilder.create().texOffs(32, 168).addBox(-2.8F, -3.6F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.8F, -3.7F, 2.2F, 0.0F, 0.0F, 0.733F));

		head.addOrReplaceChild("spike_rib_l", CubeListBuilder.create().texOffs(40, 168).addBox(0.8F, -3.6F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.8F, -3.7F, 2.2F, 0.0F, 0.0F, -0.733F));

		PartDefinition tail0 = head.addOrReplaceChild("tail0", CubeListBuilder.create().texOffs(48, 0).addBox(-2.5F, -2.5F, -0.4F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(48, 168).addBox(-0.5F, -3.9F, 0.6F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 6.4F));

		PartDefinition tail1 = tail0.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(128, 17).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 4.2F));

		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(68, 0).addBox(-1.5F, -1.5F, -0.2F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 4.2F));

		tail2.addOrReplaceChild("tail_fin", CubeListBuilder.create().texOffs(164, 88).addBox(-0.5F, -5.2F, -0.7F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(176, 88).addBox(-0.5F, 0.2F, -0.7F, 1.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(56, 168).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 3.4F));

		return LayerDefinition.create(meshdefinition, 256, 256)
                .apply(MeshTransformer.scaling(2.35F));
	}

    @Override
    public void setupAnim(GuardianRenderState state) {
        super.setupAnim(state);
        this.resetPose();

        // Orientación estilo Guardian (el cuerpo entero gira con yRot / xRot)
        this.head.yRot += state.yRot * Mth.DEG_TO_RAD;
        this.head.xRot += state.xRot * Mth.DEG_TO_RAD;

        float t = state.ageInTicks;
        float beamCharge = state.attackTime > 0.0F ? Mth.clamp(state.attackTime / 80.0F, 0.0F, 1.0F) : 0.0F;

        // Seguimiento de la pupila hacia el jugador igual que GuardianModel
        if (state.lookAtPosition != null && state.eyePosition != null && state.lookDirection != null) {
            double dy = state.lookAtPosition.y - state.eyePosition.y;
            this.eye.y += Mth.clamp((float) (-dy) * 0.4F, -0.8F, 0.8F);

            Vec3 flatLook = new Vec3(state.lookDirection.x, 0.0, state.lookDirection.z);
            Vec3 toTarget = state.eyePosition.vectorTo(state.lookAtPosition);
            toTarget = new Vec3(toTarget.x, 0.0, toTarget.z);
            if (flatLook.lengthSqr() > 1e-4 && toTarget.lengthSqr() > 1e-4) {
                Vec3 rightVec = flatLook.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
                double sideDot = rightVec.dot(toTarget.normalize());
                this.eye.x += Mth.clamp((float) (-sideDot) * 1.4F, -1.1F, 1.1F);
            }
        }

        // Espinas abisales pulsantes con spikesAnimation y sobrecarga del rayo
        float spikeExtend = (1.0F - state.spikesAnimation) * 0.9F + beamCharge * 0.45F;
        float wobble = Mth.sin(t * 0.14F) * 0.15F;
        this.spikeTopF.y -= (spikeExtend + wobble);
        this.spikeTopB.y -= (spikeExtend - wobble);
        this.spikeBotF.y += (spikeExtend + wobble);
        this.spikeBotB.y += (spikeExtend - wobble);
        this.spikeRibR.x -= spikeExtend * 0.7F;
        this.spikeRibL.x += spikeExtend * 0.7F;

        // Aletas reales de leviatán ondulando al nadar y abriéndose al disparar el rayo
        float swimWave = Mth.sin(t * 0.14F) * (0.14F + beamCharge * 0.12F);
        this.finRight.yRot += -beamCharge * 0.35F + swimWave;
        this.finRight.zRot += -0.10F * Mth.cos(t * 0.14F);
        this.finLeft.yRot += beamCharge * 0.35F - swimWave;
        this.finLeft.zRot += 0.10F * Mth.cos(t * 0.14F);

        // Apertura de mandíbulas y ligero temblor regio al canalizar Debilidad
        this.mandibleRight.yRot += beamCharge * 0.42F + Mth.sin(t * 0.25F) * 0.05F;
        this.mandibleLeft.yRot -= beamCharge * 0.42F + Mth.sin(t * 0.25F) * 0.05F;
        this.crown.zRot += Mth.sin(t * 0.06F) * 0.025F;

        // Onda sinusoidal de nado en los 4 segmentos de la cola
        float tailPhase = state.tailAnimation;
        this.tail0.yRot = Mth.sin(tailPhase) * Mth.PI * 0.055F;
        this.tail1.yRot = Mth.sin(tailPhase + 0.55F) * Mth.PI * 0.105F;
        this.tail2.yRot = Mth.sin(tailPhase + 1.15F) * Mth.PI * 0.15F;
        this.tailFin.yRot = Mth.sin(tailPhase + 1.75F) * Mth.PI * 0.19F;
    }
}
