package com.luishugomod01.client.creeper;

import com.luishugomod01.client.CreeperRenderStateAccessor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/**
 * Modelo 3D del Creeper Cósmico: textura de nebulosa espacial, anillo planetario
 * translúcido girando alrededor de su cabeza y una estrella luminosa orbitando.
 */
public class CreeperCosmicoModel extends EntityModel<CreeperRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("ruleta_hardcore", "creeper_cosmico"), "main");

    private final ModelPart head;
    private final ModelPart haloTilt;
    private final ModelPart ringSpin;
    private final ModelPart starOrbit;
    private final ModelPart orbitStar;
    private final ModelPart legFr;
    private final ModelPart legFl;
    private final ModelPart legBr;
    private final ModelPart legBl;

    public CreeperCosmicoModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        ModelPart rootPart = root.getChild("root");
        ModelPart body = rootPart.getChild("body");
        this.head = body.getChild("head");
        this.haloTilt = this.head.getChild("halo_tilt");
        this.ringSpin = this.haloTilt.getChild("ring_spin");
        this.starOrbit = this.haloTilt.getChild("star_orbit");
        this.orbitStar = this.starOrbit.getChild("orbit_star");
        this.legFr = body.getChild("leg_fr");
        this.legFl = body.getChild("leg_fl");
        this.legBr = body.getChild("leg_br");
        this.legBl = body.getChild("leg_bl");
    }

    public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -6.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(24, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(56, 0).addBox(-3.0F, -6.8F, -4.25F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(56, 28).addBox(-3.0F, -8.8F, -2.8F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

		PartDefinition halo_tilt = head.addOrReplaceChild("halo_tilt", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, -0.2793F, 0.0F, 0.192F));

		PartDefinition ring_spin = halo_tilt.addOrReplaceChild("ring_spin", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		ring_spin.addOrReplaceChild("ring_seg_0", CubeListBuilder.create().texOffs(0, 56).addBox(-2.5F, -0.5F, -9.4F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 100).addBox(-0.5F, -0.55F, -10.55F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		ring_spin.addOrReplaceChild("ring_seg_1", CubeListBuilder.create().texOffs(72, 68).addBox(-2.5F, -0.65F, -9.4F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.5236F, 0.0F));

		ring_spin.addOrReplaceChild("ring_seg_2", CubeListBuilder.create().texOffs(14, 56).addBox(-2.5F, -0.5F, -9.4F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));

		ring_spin.addOrReplaceChild("ring_seg_3", CubeListBuilder.create().texOffs(86, 68).addBox(-2.5F, -0.65F, -9.4F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(4, 100).addBox(-0.5F, -0.55F, -10.55F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ring_spin.addOrReplaceChild("ring_seg_4", CubeListBuilder.create().texOffs(28, 56).addBox(-2.5F, -0.5F, -9.4F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));

		ring_spin.addOrReplaceChild("ring_seg_5", CubeListBuilder.create().texOffs(100, 68).addBox(-2.5F, -0.65F, -9.4F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.618F, 0.0F));

		ring_spin.addOrReplaceChild("ring_seg_6", CubeListBuilder.create().texOffs(42, 56).addBox(-2.5F, -0.5F, -9.4F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(8, 100).addBox(-0.5F, -0.55F, -10.55F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -3.1416F, 0.0F));

		ring_spin.addOrReplaceChild("ring_seg_7", CubeListBuilder.create().texOffs(114, 68).addBox(-2.5F, -0.65F, -9.4F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -3.6652F, 0.0F));

		ring_spin.addOrReplaceChild("ring_seg_8", CubeListBuilder.create().texOffs(56, 56).addBox(-2.5F, -0.5F, -9.4F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -4.1888F, 0.0F));

		ring_spin.addOrReplaceChild("ring_seg_9", CubeListBuilder.create().texOffs(72, 71).addBox(-2.5F, -0.65F, -9.4F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(12, 100).addBox(-0.5F, -0.55F, -10.55F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -4.7124F, 0.0F));

		ring_spin.addOrReplaceChild("ring_seg_10", CubeListBuilder.create().texOffs(0, 59).addBox(-2.5F, -0.5F, -9.4F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -5.236F, 0.0F));

		ring_spin.addOrReplaceChild("ring_seg_11", CubeListBuilder.create().texOffs(86, 71).addBox(-2.5F, -0.65F, -9.4F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -5.7596F, 0.0F));

		PartDefinition star_orbit = halo_tilt.addOrReplaceChild("star_orbit", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		star_orbit.addOrReplaceChild("orbit_star", CubeListBuilder.create().texOffs(16, 100).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(24, 100).addBox(-2.8F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 100).addBox(0.8F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(36, 100).addBox(-0.5F, -2.8F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(40, 100).addBox(-0.5F, 0.8F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(44, 100).addBox(-0.5F, -0.5F, 0.8F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(50, 100).addBox(-0.5F, -0.5F, -2.8F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, -13.6F));

		body.addOrReplaceChild("leg_fr", CubeListBuilder.create().texOffs(64, 100).addBox(-2.0F, 0.2F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(80, 28).addBox(-2.5F, 4.0F, -2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.7F, 5.5F, -3.3F));

		body.addOrReplaceChild("leg_fl", CubeListBuilder.create().texOffs(80, 100).addBox(-2.0F, 0.2F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(100, 28).addBox(-2.5F, 4.0F, -2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.7F, 5.5F, -3.3F));

		body.addOrReplaceChild("leg_br", CubeListBuilder.create().texOffs(96, 100).addBox(-2.0F, 0.2F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(56, 35).addBox(-2.5F, 4.0F, -2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.9F, 5.5F, 3.3F));

		body.addOrReplaceChild("leg_bl", CubeListBuilder.create().texOffs(112, 100).addBox(-2.0F, 0.2F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(76, 35).addBox(-2.5F, 4.0F, -2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.9F, 5.5F, 3.3F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

    @Override
    public void setupAnim(CreeperRenderState state) {
        super.setupAnim(state);
        this.resetPose();

        float t = (state instanceof CreeperRenderStateAccessor acc && acc.ruleta$getAgeInTicks() > 0.0F)
                ? acc.ruleta$getAgeInTicks()
                : state.ageInTicks;
        float pos = state.walkAnimationPos;
        float speed = state.walkAnimationSpeed;
        float swell = state.swelling;

        // Mirada del Creeper
        this.head.yRot += state.yRot * Mth.DEG_TO_RAD;
        this.head.xRot += state.xRot * Mth.DEG_TO_RAD;

        // Marcha clásica pesada del Creeper Cósmico
        float swingA = Mth.cos(pos * 0.6662F) * 1.4F * speed;
        float swingB = Mth.cos(pos * 0.6662F + Mth.PI) * 1.4F * speed;
        this.legFl.xRot += swingA;
        this.legBr.xRot += swingA;
        this.legFr.xRot += swingB;
        this.legBl.xRot += swingB;

        // Anillo de nebulosa giratorio sobre la cabeza + estrella orbitando (se acelera si está por detonar)
        float boost = 1.0F + swell * 2.2F;
        this.haloTilt.xRot += Mth.sin(t * 0.045F) * 0.07F;
        this.haloTilt.zRot += Mth.cos(t * 0.04F) * 0.06F;
        this.ringSpin.yRot = t * 0.045F * boost;
        this.starOrbit.yRot = -t * 0.135F * boost;

        // Rotación propia de la estrella brillante mientras orbita
        this.orbitStar.yRot = t * 0.18F;
        this.orbitStar.zRot = t * 0.12F;
    }
}
