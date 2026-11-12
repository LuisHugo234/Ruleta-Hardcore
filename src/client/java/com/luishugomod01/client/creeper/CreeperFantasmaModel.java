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
 * Modelo 3D del Creeper Fantasma: núcleo espectral interior envuelto en una
 * manta translúcida flotante con animación suave estilo espectro/Vex.
 */
public class CreeperFantasmaModel extends EntityModel<CreeperRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("ruleta_hardcore", "creeper_fantasma"), "main");

    private final ModelPart body;
    private final ModelPart legFl;
    private final ModelPart legFlTip;
    private final ModelPart legFr;
    private final ModelPart legFrTip;
    private final ModelPart legBl;
    private final ModelPart legBlTip;
    private final ModelPart legBr;
    private final ModelPart legBrTip;
    private final ModelPart head;
    private final ModelPart hoodTail;
    private final ModelPart wispRight;
    private final ModelPart wispRightTip;
    private final ModelPart wispLeft;
    private final ModelPart wispLeftTip;
    private final ModelPart skirtFront;
    private final ModelPart skirtBack;
    private final ModelPart skirtRight;
    private final ModelPart skirtLeft;

    public CreeperFantasmaModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        ModelPart rootPart = root.getChild("root");
        this.body = rootPart.getChild("body");
        this.legFl = this.body.getChild("leg_fl");
        this.legFlTip = this.legFl.getChild("leg_fl_tip");
        this.legFr = this.body.getChild("leg_fr");
        this.legFrTip = this.legFr.getChild("leg_fr_tip");
        this.legBl = this.body.getChild("leg_bl");
        this.legBlTip = this.legBl.getChild("leg_bl_tip");
        this.legBr = this.body.getChild("leg_br");
        this.legBrTip = this.legBr.getChild("leg_br_tip");
        this.head = this.body.getChild("head");
        this.hoodTail = this.head.getChild("hood_tail");
        this.wispRight = this.body.getChild("wisp_right");
        this.wispRightTip = this.wispRight.getChild("wisp_right_tip");
        this.wispLeft = this.body.getChild("wisp_left");
        this.wispLeftTip = this.wispLeft.getChild("wisp_left_tip");
        this.skirtFront = this.body.getChild("skirt_front");
        this.skirtBack = this.body.getChild("skirt_back");
        this.skirtRight = this.body.getChild("skirt_right");
        this.skirtLeft = this.body.getChild("skirt_left");
    }

    public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -3.5F, -1.5F, 5.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(48, 0).addBox(-1.0F, -1.0F, -2.1F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(64, 24).addBox(-5.5F, -3.8F, -3.4F, 11.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(100, 24).addBox(-4.4F, -2.7F, -3.65F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(108, 24).addBox(1.4F, -2.7F, -3.65F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.5F, 0.0F));

		PartDefinition leg_fl = body.addOrReplaceChild("leg_fl", CubeListBuilder.create().texOffs(16, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.4F, 4.5F, -1.1F, 0.1047F, 0.0F, -0.0873F));

		PartDefinition leg_fl_tip = leg_fl.addOrReplaceChild("leg_fl_tip", CubeListBuilder.create().texOffs(64, 64).addBox(-0.5F, 0.0F, -0.35F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.8F, 0.0F, -0.2793F, 0.0F, 0.0F));

		PartDefinition leg_fr = body.addOrReplaceChild("leg_fr", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4F, 4.5F, -1.1F, 0.1047F, 0.0F, 0.0873F));

		PartDefinition leg_fr_tip = leg_fr.addOrReplaceChild("leg_fr_tip", CubeListBuilder.create().texOffs(68, 64).addBox(-0.5F, 0.0F, -0.35F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.8F, 0.0F, -0.2793F, 0.0F, 0.0F));

		PartDefinition leg_bl = body.addOrReplaceChild("leg_bl", CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8F, 4.5F, 1.2F, -0.2094F, 0.0F, -0.1222F));

		PartDefinition leg_bl_tip = leg_bl.addOrReplaceChild("leg_bl_tip", CubeListBuilder.create().texOffs(72, 64).addBox(-0.5F, 0.0F, -0.35F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.8F, 0.0F, -0.2793F, 0.0F, 0.0F));

		PartDefinition leg_br = body.addOrReplaceChild("leg_br", CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8F, 4.5F, 1.2F, -0.2094F, 0.0F, 0.1222F));

		PartDefinition leg_br_tip = leg_br.addOrReplaceChild("leg_br_tip", CubeListBuilder.create().texOffs(76, 64).addBox(-0.5F, 0.0F, -0.35F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.8F, 0.0F, -0.2793F, 0.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 11).addBox(-3.5F, -7.0F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(72, 0).addBox(-3.0F, -6.2F, -4.05F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 36).addBox(-5.0F, -8.8F, -4.3F, 10.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(38, 36).addBox(-3.0F, -9.8F, -2.1F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 47).addBox(-4.5F, -7.8F, -4.6F, 9.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(20, 47).addBox(-4.5F, -6.8F, 3.3F, 9.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(42, 47).addBox(-5.35F, -6.85F, -4.0F, 2.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 62).addBox(3.35F, -6.85F, -4.0F, 2.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.5F, 0.0F));

		PartDefinition hood_tail = head.addOrReplaceChild("hood_tail", CubeListBuilder.create().texOffs(80, 64).addBox(-3.0F, 0.0F, 0.1F, 6.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(98, 64).addBox(-1.5F, 1.5F, 2.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, 4.8F, -0.3142F, 0.0F, 0.0F));

		PartDefinition wisp_right = body.addOrReplaceChild("wisp_right", CubeListBuilder.create().texOffs(116, 24).addBox(-1.5F, 0.0F, -1.4F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.2F, -2.0F, -0.2F, 0.384F, 0.0F, 0.4538F));

		PartDefinition wisp_right_tip = wisp_right.addOrReplaceChild("wisp_right_tip", CubeListBuilder.create().texOffs(110, 64).addBox(-0.5F, 0.0F, -0.7F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6F, 3.9F, 0.4F, 0.2793F, 0.0F, 0.2042F));

		PartDefinition wisp_left = body.addOrReplaceChild("wisp_left", CubeListBuilder.create().texOffs(64, 33).addBox(-0.5F, 0.0F, -1.4F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.2F, -2.0F, -0.2F, 0.384F, 0.0F, -0.4538F));

		PartDefinition wisp_left_tip = wisp_left.addOrReplaceChild("wisp_left_tip", CubeListBuilder.create().texOffs(116, 64).addBox(-0.5F, 0.0F, -0.7F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6F, 3.9F, 0.4F, 0.2793F, 0.0F, -0.2042F));

		PartDefinition skirt_front = body.addOrReplaceChild("skirt_front", CubeListBuilder.create().texOffs(0, 80).addBox(-4.3F, 0.3F, -0.6F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(10, 80).addBox(0.3F, 0.3F, -0.6F, 4.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(20, 80).addBox(-1.5F, 1.8F, -0.85F, 3.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(122, 64).addBox(-4.1F, 6.8F, -0.55F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(64, 70).addBox(2.1F, 6.8F, -0.55F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(70, 70).addBox(-1.0F, 6.8F, -0.75F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -3.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition skirt_back = body.addOrReplaceChild("skirt_back", CubeListBuilder.create().texOffs(28, 80).addBox(-4.5F, 0.1F, -0.35F, 9.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(76, 70).addBox(-3.9F, 6.9F, -0.2F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 70).addBox(-1.0F, 7.0F, 0.05F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(88, 70).addBox(1.9F, 6.9F, -0.2F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 3.1F, 0.2793F, 0.0F, 0.0F));

		PartDefinition skirt_right = body.addOrReplaceChild("skirt_right", CubeListBuilder.create().texOffs(48, 80).addBox(-0.5F, 0.4F, -3.0F, 1.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(94, 70).addBox(-0.6F, 6.9F, -2.7F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(100, 70).addBox(-0.6F, 6.9F, 0.7F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -1.1F, 0.0F, 0.0F, 0.0F, 0.2443F));

		PartDefinition skirt_left = body.addOrReplaceChild("skirt_left", CubeListBuilder.create().texOffs(0, 93).addBox(-0.5F, 0.4F, -3.0F, 1.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(106, 70).addBox(-0.4F, 6.9F, -2.7F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(112, 70).addBox(-0.4F, 6.9F, 0.7F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.4F, -1.1F, 0.0F, 0.0F, 0.0F, -0.2443F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

    @Override
    public void setupAnim(CreeperRenderState state) {
        super.setupAnim(state);
        this.resetPose();

        float t = (state instanceof CreeperRenderStateAccessor acc && acc.ruleta$getAgeInTicks() > 0.0F)
                ? acc.ruleta$getAgeInTicks()
                : state.ageInTicks;
        float speed = Math.min(1.0F, state.walkAnimationSpeed);
        float swell = state.swelling;

        // Levitación fantasmal tipo Vex y suave cabeceo al avanzar
        this.body.y += Mth.sin(t * 0.11F) * 1.6F;
        this.body.xRot += 0.22F * speed + swell * 0.12F;
        this.body.zRot += Mth.sin(t * 0.065F) * 0.055F;

        // Mirada del Creeper bajo la manta
        this.head.yRot += state.yRot * Mth.DEG_TO_RAD * 0.85F;
        this.head.xRot += state.xRot * Mth.DEG_TO_RAD * 0.75F;
        this.hoodTail.xRot += 0.24F * speed + Mth.sin(t * 0.15F) * 0.10F;

        // Ondulación etérea de la manta translúcida (se agita al inflarse para explotar)
        float flutter = 1.0F + swell * 1.4F;
        this.skirtFront.xRot += -0.10F * speed - swell * 0.18F + Mth.sin(t * 0.16F) * 0.09F * flutter;
        this.skirtBack.xRot += 0.32F * speed + swell * 0.22F + Mth.sin(t * 0.16F + 1.3F) * 0.11F * flutter;
        this.skirtLeft.zRot += -swell * 0.20F + Mth.sin(t * 0.14F + 0.7F) * 0.10F * flutter;
        this.skirtRight.zRot -= -swell * 0.20F + Mth.sin(t * 0.14F + 0.7F) * 0.10F * flutter;

        // Jirones laterales flotantes (estilo alas/brazos fantasmales de Vex)
        this.wispLeft.xRot += 0.25F * speed + Mth.sin(t * 0.14F) * 0.12F;
        this.wispLeft.zRot += -swell * 0.25F + Mth.cos(t * 0.12F) * 0.10F;
        this.wispLeftTip.xRot += Mth.sin(t * 0.18F + 1.1F) * 0.14F;

        this.wispRight.xRot += 0.25F * speed + Mth.sin(t * 0.14F + 0.5F) * 0.12F;
        this.wispRight.zRot -= -swell * 0.25F + Mth.cos(t * 0.12F + 0.5F) * 0.10F;
        this.wispRightTip.xRot += Mth.sin(t * 0.18F + 1.6F) * 0.14F;

        // Zarcillos/patas espectrales ondulando hacia atrás en el aire
        float trail = 0.28F * speed;
        this.legFl.xRot += trail + Mth.sin(t * 0.15F) * 0.12F;
        this.legFr.xRot += trail + Mth.sin(t * 0.15F + 1.2F) * 0.12F;
        this.legBl.xRot += trail + Mth.sin(t * 0.15F + 2.1F) * 0.14F;
        this.legBr.xRot += trail + Mth.sin(t * 0.15F + 3.1F) * 0.14F;
        this.legFlTip.xRot += Mth.sin(t * 0.20F + 0.8F) * 0.16F;
        this.legFrTip.xRot += Mth.sin(t * 0.20F + 2.0F) * 0.16F;
        this.legBlTip.xRot += Mth.sin(t * 0.20F + 2.9F) * 0.18F;
        this.legBrTip.xRot += Mth.sin(t * 0.20F + 3.9F) * 0.18F;
    }
}
