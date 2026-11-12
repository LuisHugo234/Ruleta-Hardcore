package com.luishugomod01.client.coloso;

import com.luishugomod01.client.IronGolemRenderStateAccessor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/**
 * Modelo 3D del Coloso de Ruina: titán de basalto y piedra musgosa con armadura
 * frontal sellada, fisura dorsal de magma expuesta y animación articulada de Slam.
 */
public class ColosoRuinaModel extends EntityModel<IronGolemRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("ruleta_hardcore", "coloso_ruina"), "main");

    private final ModelPart pelvis;
    private final ModelPart torso;
    private final ModelPart backSpikes;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart rightForearm;
    private final ModelPart leftArm;
    private final ModelPart leftForearm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public ColosoRuinaModel(ModelPart root) {
        super(root);
        ModelPart rootPart = root.getChild("root");
        this.pelvis = rootPart.getChild("pelvis");
        this.torso = this.pelvis.getChild("torso");
        this.backSpikes = this.torso.getChild("back_spikes");
        this.head = this.torso.getChild("head");
        this.rightArm = this.torso.getChild("right_arm");
        this.rightForearm = this.rightArm.getChild("right_forearm");
        this.leftArm = this.torso.getChild("left_arm");
        this.leftForearm = this.leftArm.getChild("left_forearm");
        this.rightLeg = rootPart.getChild("right_leg");
        this.leftLeg = rootPart.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition pelvis = root.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, -3.5F, -3.5F, 11.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 96).addBox(-2.5F, -2.3F, -4.2F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(128, 0).addBox(-3.5F, 0.0F, -4.1F, 7.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 96).addBox(-6.9F, -1.3F, -2.7F, 2.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(126, 96).addBox(4.9F, -1.3F, -2.7F, 2.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -17.0F, 0.0F));

		PartDefinition torso = pelvis.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(36, 0).addBox(-6.0F, -5.5F, -4.0F, 12.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(144, 0).addBox(-9.0F, -14.5F, -5.3F, 18.0F, 10.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(76, 0).addBox(-7.0F, -13.2F, -6.45F, 14.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(12, 96).addBox(-8.0F, -14.3F, -6.85F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(128, 224).addBox(-2.0F, -11.5F, -6.8F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(140, 96).addBox(-5.0F, -16.8F, -4.2F, 10.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(200, 96).addBox(-3.0F, -13.5F, 3.9F, 6.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(108, 0).addBox(-8.3F, -14.0F, 3.8F, 6.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 14).addBox(2.3F, -14.0F, 3.8F, 6.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.5F, 0.0F));

		torso.addOrReplaceChild("back_spikes", CubeListBuilder.create().texOffs(216, 96).addBox(-1.5F, -5.3F, -0.2F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(138, 224).addBox(-1.0F, -0.5F, -0.1F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, 5.4F, 0.2793F, 0.0F, 0.0F));

		PartDefinition head = torso.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 168).addBox(-4.0F, -7.3F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(128, 168).addBox(-3.0F, -5.1F, -4.3F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(16, 14).addBox(-4.5F, -6.9F, -5.05F, 9.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(48, 96).addBox(-3.5F, -1.6F, -4.6F, 7.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(112, 107).addBox(-5.0F, -8.6F, -3.9F, 10.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -15.0F, -1.8F));

		head.addOrReplaceChild("horn_right", CubeListBuilder.create().texOffs(38, 14).addBox(-1.9F, -4.8F, -1.4F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(146, 224).addBox(-1.4F, -7.0F, -0.9F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.4F, -6.0F, 0.4F, -0.1745F, 0.0F, -0.384F));

		head.addOrReplaceChild("horn_left", CubeListBuilder.create().texOffs(50, 14).addBox(-1.1F, -3.8F, -1.4F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(154, 224).addBox(-0.6F, -4.6F, -0.9F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.4F, -6.0F, 0.4F, -0.1745F, 0.0F, 0.384F));

		PartDefinition right_arm = torso.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(62, 14).addBox(-5.4F, -3.8F, -5.5F, 9.0F, 7.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(150, 107).addBox(-4.1F, -5.3F, -3.8F, 6.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(66, 96).addBox(-4.8F, -6.7F, -2.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(200, 0).addBox(-3.0F, 2.8F, -2.5F, 5.0F, 9.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.5F, -13.0F, -0.3F));

		right_arm.addOrReplaceChild("right_forearm", CubeListBuilder.create().texOffs(0, 32).addBox(-3.7F, -0.5F, -3.7F, 7.0F, 13.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(80, 96).addBox(-4.2F, 9.0F, -4.2F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(162, 224).addBox(-3.2F, 11.4F, -2.8F, 6.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 11.5F, 0.0F, -0.2094F, 0.0F, 0.0F));

		PartDefinition left_arm = torso.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(28, 32).addBox(-3.6F, -3.8F, -5.5F, 9.0F, 7.0F, 11.0F, new CubeDeformation(0.0F))
		.texOffs(112, 118).addBox(-1.9F, -5.3F, -3.8F, 6.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 107).addBox(1.8F, -6.7F, -2.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(220, 0).addBox(-2.0F, 2.8F, -2.5F, 5.0F, 9.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(11.5F, -13.0F, -0.3F));

		left_arm.addOrReplaceChild("left_forearm", CubeListBuilder.create().texOffs(68, 32).addBox(-3.3F, -0.5F, -3.7F, 7.0F, 13.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(14, 107).addBox(-3.8F, 9.0F, -4.2F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(180, 224).addBox(-2.8F, 11.4F, -2.8F, 6.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 11.5F, 0.0F, -0.2094F, 0.0F, 0.0F));

		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(128, 20).addBox(-3.0F, 0.5F, -3.0F, 6.0F, 13.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(96, 32).addBox(-3.5F, 4.0F, -4.0F, 7.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(46, 107).addBox(-3.5F, 13.5F, -4.6F, 7.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.8F, -16.5F, 0.0F));

		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(152, 20).addBox(-3.0F, 0.5F, -3.0F, 6.0F, 13.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(0, 52).addBox(-3.5F, 4.0F, -4.0F, 7.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(76, 107).addBox(-3.5F, 13.5F, -4.6F, 7.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(4.8F, -16.5F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

    @Override
    public void setupAnim(IronGolemRenderState state) {
        super.setupAnim(state);
        this.resetPose();

        float pos = state.walkAnimationPos;
        float speed = state.walkAnimationSpeed;
        float t = state.ageInTicks;

        // Mirada severa del guardián
        this.head.yRot += state.yRot * Mth.DEG_TO_RAD * 0.8F;
        this.head.xRot += state.xRot * Mth.DEG_TO_RAD * 0.65F;

        // Marcha pesada de titán (paso lento y aplastante)
        float legSwing = Mth.triangleWave(pos, 13.0F) * 1.15F * speed;
        this.rightLeg.xRot += legSwing;
        this.leftLeg.xRot -= legSwing;
        this.pelvis.y += Math.abs(Mth.cos(pos * 0.24F)) * 0.8F * speed;
        this.torso.zRot += Mth.triangleWave(pos, 13.0F) * 0.06F * speed;
        this.torso.yRot += Mth.triangleWave(pos, 13.0F) * 0.08F * speed;

        // Respiración volcánica sutil de los cristales dorsales
        this.backSpikes.xRot += Mth.sin(t * 0.08F) * 0.05F;

        float slam = (state instanceof IronGolemRenderStateAccessor acc) ? acc.ruleta$getSlamProgress() : 0.0F;
        if (slam > 0.0F && slam <= 1.0F) {
            if (slam < 0.70F) {
                // Wind-up colosal: echa atrás el torso y alza ambos puños martillo hacia el cielo
                float eased = Mth.sin((slam / 0.70F) * (Mth.PI / 2.0F));
                this.torso.xRot += -0.26F * eased;
                this.rightArm.xRot += -2.45F * eased;
                this.leftArm.xRot += -2.45F * eased;
                this.rightArm.zRot += 0.30F * eased;
                this.leftArm.zRot -= 0.30F * eased;
                this.rightForearm.xRot += -0.35F * eased;
                this.leftForearm.xRot += -0.35F * eased;
                this.head.xRot += -0.35F * eased;
            } else {
                // Impacto sísmico contra el suelo
                float impact = Mth.sin(((slam - 0.70F) / 0.30F) * (Mth.PI / 2.0F));
                this.torso.xRot += -0.26F + 0.66F * impact;
                this.rightArm.xRot += -2.45F + 3.15F * impact;
                this.leftArm.xRot += -2.45F + 3.15F * impact;
                this.rightArm.zRot += 0.30F * (1.0F - impact);
                this.leftArm.zRot -= 0.30F * (1.0F - impact);
                this.rightForearm.xRot += -0.35F * (1.0F - impact);
                this.leftForearm.xRot += -0.35F * (1.0F - impact);
                this.head.xRot += -0.35F + 0.75F * impact;
            }
            return;
        }

        // Ataque cuerpo a cuerpo vanilla (lanzar al jugador por los aires)
        float attack = state.attackTicksRemaining;
        if (attack > 0.0F) {
            float swing = -2.0F + 1.5F * Mth.triangleWave(attack, 10.0F);
            this.rightArm.xRot += swing;
            this.leftArm.xRot += swing;
            this.rightForearm.xRot += -0.25F;
            this.leftForearm.xRot += -0.25F;
            this.torso.xRot += 0.14F * Mth.sin((attack / 10.0F) * Mth.PI);
        } else {
            // Balanceo pesado de brazos articulados al caminar o reposar
            float armWalk = (-0.2F + 1.3F * Mth.triangleWave(pos, 13.0F)) * speed;
            float idleSway = Mth.sin(t * 0.06F) * 0.04F;
            this.rightArm.xRot += -armWalk + idleSway;
            this.leftArm.xRot += armWalk - idleSway;
            this.rightForearm.xRot += -0.15F * speed;
            this.leftForearm.xRot += -0.15F * speed;
        }
    }
}
