package com.luishugomod01.client.creeper;

import com.luishugomod01.client.CreeperRenderStateAccessor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/**
 * Modelo 3D del Creeper Termonuclear: armadura pesada de obsidiana llorosa y basalto
 * con reactor central giratorio, celdas de combustible dorsales y escapes térmicos.
 */
public class CreeperTermonuclearModel extends EntityModel<CreeperRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("ruleta_hardcore", "creeper_termonuclear"), "main");

    private final ModelPart body;
    private final ModelPart reactorRotor;
    private final ModelPart pauldronRight;
    private final ModelPart pauldronLeft;
    private final ModelPart reactorPack;
    private final ModelPart head;
    private final ModelPart headVentR;
    private final ModelPart headVentL;
    private final ModelPart legFr;
    private final ModelPart legFl;
    private final ModelPart legBr;
    private final ModelPart legBl;

    public CreeperTermonuclearModel(ModelPart root) {
        super(root);
        ModelPart rootPart = root.getChild("root");
        this.body = rootPart.getChild("body");
        this.reactorRotor = this.body.getChild("reactor_rotor");
        this.pauldronRight = this.body.getChild("pauldron_right");
        this.pauldronLeft = this.body.getChild("pauldron_left");
        this.reactorPack = this.body.getChild("reactor_pack");
        this.head = this.body.getChild("head");
        this.headVentR = this.head.getChild("head_vent_r");
        this.headVentL = this.head.getChild("head_vent_l");
        this.legFr = this.body.getChild("leg_fr");
        this.legFl = this.body.getChild("leg_fl");
        this.legBr = this.body.getChild("leg_br");
        this.legBl = this.body.getChild("leg_bl");
    }

    public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(64, 48).addBox(-4.0F, -6.0F, -2.5F, 8.0F, 11.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.6F, -5.5F, -3.05F, 4.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(10, 0).addBox(0.6F, -5.5F, -3.05F, 4.0F, 10.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 48).addBox(-4.5F, 3.2F, -3.15F, 9.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(-5.0F, -6.2F, -3.35F, 10.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(20, 48).addBox(-2.0F, -2.8F, -3.35F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 48).addBox(-2.0F, 0.9F, -3.35F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(88, 0).addBox(-5.0F, 3.8F, -3.5F, 10.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(64, 9).addBox(-4.85F, -4.7F, -2.0F, 1.0F, 9.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(74, 9).addBox(3.85F, -4.7F, -2.0F, 1.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition reactor_rotor = body.addOrReplaceChild("reactor_rotor", CubeListBuilder.create().texOffs(64, 108).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(40, 48).addBox(-2.0F, -0.5F, -0.35F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(50, 48).addBox(-0.5F, -1.5F, -0.25F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, -2.4F));

		PartDefinition pauldron_right = body.addOrReplaceChild("pauldron_right", CubeListBuilder.create().texOffs(20, 0).addBox(-2.8F, -1.5F, -3.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(84, 9).addBox(-3.1F, 0.4F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 52).addBox(-2.7F, -2.2F, -0.8F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(70, 108).addBox(-2.2F, -4.3F, -0.3F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -5.0F, 0.0F, 0.0F, 0.0F, -0.2793F));

		PartDefinition pauldron_left = body.addOrReplaceChild("pauldron_left", CubeListBuilder.create().texOffs(40, 0).addBox(-1.2F, -1.5F, -3.0F, 4.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(98, 9).addBox(1.1F, 0.4F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(12, 52).addBox(-0.3F, -2.2F, -0.8F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(78, 108).addBox(0.2F, -4.3F, -0.3F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.2793F));

		PartDefinition reactor_pack = body.addOrReplaceChild("reactor_pack", CubeListBuilder.create().texOffs(64, 22).addBox(-3.5F, -4.5F, -0.9F, 7.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(90, 48).addBox(-3.3F, -4.0F, 0.1F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(98, 48).addBox(1.3F, -4.0F, 0.1F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(24, 52).addBox(-3.8F, -4.8F, -0.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(36, 52).addBox(0.8F, -4.8F, -0.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(48, 52).addBox(-3.8F, 2.8F, -0.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(0, 56).addBox(0.8F, 2.8F, -0.4F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(86, 108).addBox(-1.0F, -3.5F, 0.5F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 3.2F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 84).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(64, 84).addBox(-3.0F, -6.7F, -4.25F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 11).addBox(-5.0F, -9.2F, -4.8F, 10.0F, 2.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(82, 22).addBox(-2.0F, -10.6F, -3.5F, 4.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(12, 56).addBox(-4.5F, -8.2F, -5.15F, 9.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(40, 11).addBox(-5.2F, -7.1F, -3.8F, 2.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 25).addBox(3.2F, -7.1F, -3.8F, 2.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(106, 22).addBox(-4.5F, -7.3F, 3.15F, 9.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));

		PartDefinition head_vent_r = head.addOrReplaceChild("head_vent_r", CubeListBuilder.create().texOffs(34, 56).addBox(-1.1F, -2.9F, -0.9F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(92, 108).addBox(-0.6F, -4.4F, -0.4F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -8.0F, 2.5F, 0.384F, 0.0F, -0.2443F));

		PartDefinition head_vent_l = head.addOrReplaceChild("head_vent_l", CubeListBuilder.create().texOffs(42, 56).addBox(-0.9F, -2.9F, -0.9F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(96, 108).addBox(-0.4F, -4.4F, -0.4F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -8.0F, 2.5F, 0.384F, 0.0F, 0.2443F));

		PartDefinition leg_fr = body.addOrReplaceChild("leg_fr", CubeListBuilder.create().texOffs(32, 84).addBox(-2.0F, -0.3F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(18, 25).addBox(-2.5F, 0.6F, -2.6F, 5.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 61).addBox(-2.5F, 5.0F, -2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.7F, 5.0F, -3.3F));

		PartDefinition leg_fl = body.addOrReplaceChild("leg_fl", CubeListBuilder.create().texOffs(48, 84).addBox(-2.0F, -0.3F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(36, 25).addBox(-2.5F, 0.6F, -2.6F, 5.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(20, 61).addBox(-2.5F, 5.0F, -2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(2.7F, 5.0F, -3.3F));

		PartDefinition leg_br = body.addOrReplaceChild("leg_br", CubeListBuilder.create().texOffs(0, 100).addBox(-2.0F, -0.3F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 39).addBox(-2.5F, 0.6F, -1.4F, 5.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(40, 61).addBox(-2.5F, 5.0F, -2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.1F, 5.0F, 3.3F));

		PartDefinition leg_bl = body.addOrReplaceChild("leg_bl", CubeListBuilder.create().texOffs(16, 100).addBox(-2.0F, -0.3F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(18, 39).addBox(-2.5F, 0.6F, -1.4F, 5.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 68).addBox(-2.5F, 5.0F, -2.5F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.1F, 5.0F, 3.3F));

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

        // Seguimiento pesado de cabeza
        this.head.yRot += state.yRot * Mth.DEG_TO_RAD;
        this.head.xRot += state.xRot * Mth.DEG_TO_RAD * 0.85F;

        // Marcha pesada tipo Creeper blindado en las 4 patas
        float swingA = Mth.cos(pos * 0.6662F) * 1.15F * speed;
        float swingB = Mth.cos(pos * 0.6662F + Mth.PI) * 1.15F * speed;
        this.legFl.xRot += swingA;
        this.legBr.xRot += swingA;
        this.legFr.xRot += swingB;
        this.legBl.xRot += swingB;

        // Balanceo pesado del chasis y hombreras al caminar
        this.body.y += Math.abs(Mth.sin(pos * 0.6662F)) * 0.65F * speed;
        this.body.zRot += Mth.sin(pos * 0.6662F) * 0.04F * speed;

        // Rotor del reactor termonuclear (se acelera cuando avanza o carga calor)
        float intensity = Mth.clamp(speed + state.swelling + (state.isPowered ? 0.6F : 0.0F), 0.0F, 1.4F);
        float heatRate = 0.12F + intensity * 0.22F;
        this.reactorRotor.zRot = t * heatRate;

        // Pulsación térmica en hombreras, mochila y conductos
        float pulse = Mth.sin(t * (0.10F + intensity * 0.22F)) * (0.025F + intensity * 0.045F);
        this.pauldronRight.zRot += -pulse;
        this.pauldronLeft.zRot += pulse;
        this.reactorPack.z += intensity > 0.2F ? Mth.sin(t * 0.40F) * 0.16F * intensity : 0.0F;
        this.headVentR.xRot += pulse * 0.7F;
        this.headVentL.xRot += pulse * 0.7F;
    }
}
