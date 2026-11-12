package com.luishugomod01.client.nigromante;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class NigromanteModel extends EntityModel<SkeletonRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("ruleta_hardcore", "nigromante_criptas"), "main");

    private final ModelPart body;
    private final ModelPart mantleL;
    private final ModelPart mantleR;
    private final ModelPart cloakFront;
    private final ModelPart cloakBack;
    private final ModelPart cloakLeft;
    private final ModelPart cloakRight;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart staff;
    private final ModelPart staffLantern;
    private final ModelPart iceCrystal;

    public NigromanteModel(ModelPart root) {
        super(root);
        ModelPart rootPart = root.getChild("root");
        this.body = rootPart.getChild("body");
        this.mantleL = this.body.getChild("mantle_l");
        this.mantleR = this.body.getChild("mantle_r");
        this.cloakFront = this.body.getChild("cloak_front");
        this.cloakBack = this.body.getChild("cloak_back");
        this.cloakLeft = this.body.getChild("cloak_left");
        this.cloakRight = this.body.getChild("cloak_right");
        this.head = this.body.getChild("head");
        this.leftArm = this.body.getChild("left_arm");
        this.rightArm = this.body.getChild("right_arm");
        this.staff = this.rightArm.getChild("staff");
        this.staffLantern = this.staff.getChild("staff_lantern");
        this.iceCrystal = this.staffLantern.getChild("ice_crystal");
    }

    public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -5.0F, -3.5F, 9.0F, 9.0F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(0, 16).addBox(-6.3F, -5.7F, -4.1F, 12.6F, 3.2F, 8.4F, new CubeDeformation(0.0F))
		.texOffs(48, 42).addBox(-3.1F, -4.55F, -4.45F, 6.2F, 1.5F, 1.1F, new CubeDeformation(0.0F))
		.texOffs(63, 42).addBox(1.85F, -3.5F, -4.5F, 1.1F, 2.2F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(68, 42).addBox(-2.95F, -3.5F, -4.5F, 1.1F, 2.2F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 76).addBox(-0.9F, -2.9F, -4.5F, 1.8F, 2.4F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.5F, 0.0F));

		PartDefinition mantle_l = body.addOrReplaceChild("mantle_l", CubeListBuilder.create().texOffs(64, 0).addBox(-0.6F, -0.1F, -3.6F, 1.4F, 5.4F, 7.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -3.9F, 0.2F, 0.0F, 0.0F, 0.2094F));

		PartDefinition mantle_r = body.addOrReplaceChild("mantle_r", CubeListBuilder.create().texOffs(82, 0).addBox(-0.8F, -0.1F, -3.6F, 1.4F, 5.4F, 7.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -3.9F, 0.2F, 0.0F, 0.0F, -0.2094F));

		PartDefinition cloak_front = body.addOrReplaceChild("cloak_front", CubeListBuilder.create().texOffs(100, 0).addBox(0.6F, 0.0F, -0.55F, 3.8F, 6.4F, 1.1F, new CubeDeformation(0.0F))
		.texOffs(110, 0).addBox(-4.4F, 0.0F, -0.55F, 3.8F, 6.4F, 1.1F, new CubeDeformation(0.0F))
		.texOffs(120, 0).addBox(-1.1F, 0.2F, -0.7F, 2.2F, 8.0F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.1F, -3.1F, 0.1745F, 0.0F, 0.0F));

		PartDefinition cloak_back = body.addOrReplaceChild("cloak_back", CubeListBuilder.create().texOffs(64, 13).addBox(-3.9F, 0.0F, -0.6F, 7.8F, 7.6F, 1.2F, new CubeDeformation(0.0F))
		.texOffs(82, 13).addBox(1.8F, 3.2F, -0.25F, 2.4F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(89, 13).addBox(-4.2F, 3.2F, -0.25F, 2.4F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.1F, 3.1F, -0.2443F, 0.0F, 0.0F));

		PartDefinition cloak_left = body.addOrReplaceChild("cloak_left", CubeListBuilder.create().texOffs(96, 13).addBox(-0.5F, 0.0F, -3.0F, 1.2F, 7.6F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(111, 13).addBox(-0.15F, 4.7F, -0.3F, 1.0F, 4.2F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.2F, 3.1F, 0.0F, 0.0F, 0.0F, 0.1745F));

		PartDefinition cloak_right = body.addOrReplaceChild("cloak_right", CubeListBuilder.create().texOffs(64, 27).addBox(-0.7F, 0.0F, -3.0F, 1.2F, 7.6F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(79, 27).addBox(-0.85F, 4.7F, -0.3F, 1.0F, 4.2F, 2.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.2F, 3.1F, 0.0F, 0.0F, 0.0F, -0.1745F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 42).addBox(-3.6F, -7.3F, -3.5F, 7.2F, 7.4F, 7.0F, new CubeDeformation(0.0F))
		.texOffs(48, 76).addBox(-3.2F, -5.2F, -3.7F, 6.4F, 3.2F, 0.3F, new CubeDeformation(0.0F))
		.texOffs(0, 28).addBox(-4.9F, -8.8F, -4.6F, 9.8F, 2.4F, 9.6F, new CubeDeformation(0.0F))
		.texOffs(39, 28).addBox(-4.7F, -7.8F, 3.4F, 9.4F, 8.0F, 1.8F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(3.4F, -7.45F, -4.2F, 1.6F, 7.7F, 7.8F, new CubeDeformation(0.0F))
		.texOffs(19, 0).addBox(-5.0F, -7.45F, -4.2F, 1.6F, 7.7F, 7.8F, new CubeDeformation(0.0F))
		.texOffs(38, 0).addBox(-4.7F, -7.6F, -5.4F, 9.4F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(6, 76).addBox(-1.1F, -8.4F, -5.55F, 2.2F, 2.2F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.5F, 0.0F));

		PartDefinition hood_tail = head.addOrReplaceChild("hood_tail", CubeListBuilder.create().texOffs(0, 16).addBox(-3.0F, -1.5F, -0.7F, 6.0F, 3.0F, 3.8F, new CubeDeformation(0.0F))
		.texOffs(87, 27).addBox(-1.8F, -0.4F, 2.2F, 3.6F, 2.4F, 3.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 4.6F, 0.4887F, 0.0F, 0.0F));

		PartDefinition horns = head.addOrReplaceChild("horns", CubeListBuilder.create().texOffs(73, 42).addBox(3.9F, -0.9F, -0.7F, 2.2F, 1.4F, 1.4F, new CubeDeformation(0.0F))
		.texOffs(81, 42).addBox(5.05F, -3.3F, -0.85F, 1.3F, 2.8F, 1.3F, new CubeDeformation(0.0F))
		.texOffs(87, 42).addBox(4.85F, -5.0F, -1.15F, 0.9F, 2.2F, 0.9F, new CubeDeformation(0.0F))
		.texOffs(48, 47).addBox(-6.1F, -0.9F, -0.7F, 2.2F, 1.4F, 1.4F, new CubeDeformation(0.0F))
		.texOffs(56, 47).addBox(-6.35F, -3.3F, -0.85F, 1.3F, 2.8F, 1.3F, new CubeDeformation(0.0F))
		.texOffs(62, 47).addBox(-5.75F, -5.0F, -1.15F, 0.9F, 2.2F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.4F, -1.2F, 0.2443F, 0.0F, 0.0F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(20, 16).addBox(-1.3F, -0.2F, -1.7F, 3.4F, 5.6F, 3.4F, new CubeDeformation(0.0F))
		.texOffs(101, 27).addBox(-0.7F, 4.5F, -1.0F, 2.6F, 3.4F, 3.2F, new CubeDeformation(0.0F))
		.texOffs(29, 42).addBox(-0.5F, 0.5F, -0.9F, 1.8F, 8.6F, 1.8F, new CubeDeformation(0.0F))
		.texOffs(37, 42).addBox(-0.7F, 8.4F, -1.35F, 2.2F, 2.0F, 2.3F, new CubeDeformation(0.0F))
		.texOffs(66, 47).addBox(0.8F, 9.8F, -1.2F, 0.6F, 2.0F, 0.8F, new CubeDeformation(0.0F))
		.texOffs(69, 47).addBox(0.1F, 9.9F, -1.4F, 0.6F, 2.4F, 0.8F, new CubeDeformation(0.0F))
		.texOffs(72, 47).addBox(-0.6F, 9.8F, -1.2F, 0.6F, 2.0F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.8F, -3.9F, 0.0F, -0.6632F, 0.1745F, -0.2443F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(34, 16).addBox(-2.1F, -0.2F, -1.7F, 3.4F, 5.6F, 3.4F, new CubeDeformation(0.0F))
		.texOffs(113, 27).addBox(-1.9F, 4.5F, -1.0F, 2.6F, 3.4F, 3.2F, new CubeDeformation(0.0F))
		.texOffs(0, 57).addBox(-1.3F, 0.5F, -0.9F, 1.8F, 8.6F, 1.8F, new CubeDeformation(0.0F))
		.texOffs(8, 57).addBox(-1.55F, 8.3F, -1.35F, 2.3F, 2.2F, 2.3F, new CubeDeformation(0.0F))
		.texOffs(75, 47).addBox(-0.1F, 9.3F, -1.9F, 0.6F, 1.4F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(79, 47).addBox(-0.7F, 9.4F, -2.0F, 0.6F, 1.6F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(83, 47).addBox(-1.3F, 9.3F, -1.9F, 0.6F, 1.4F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.8F, -3.9F, 0.0F, -0.8727F, -0.2094F, 0.3491F));

		PartDefinition staff = right_arm.addOrReplaceChild("staff", CubeListBuilder.create().texOffs(96, 42).addBox(-0.65F, -14.7F, -0.65F, 1.3F, 22.0F, 1.3F, new CubeDeformation(0.0F))
		.texOffs(48, 52).addBox(-1.3F, -13.95F, -1.3F, 2.6F, 1.5F, 2.6F, new CubeDeformation(0.0F))
		.texOffs(102, 42).addBox(-0.7F, -18.9F, -1.2F, 1.4F, 5.4F, 1.6F, new CubeDeformation(0.0F))
		.texOffs(59, 52).addBox(-0.85F, -19.1F, -4.6F, 1.7F, 1.4F, 4.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4F, 9.6F, -0.8F, 0.6981F, 0.0F, -0.2443F));

		PartDefinition staff_lantern = staff.addOrReplaceChild("staff_lantern", CubeListBuilder.create().texOffs(109, 42).addBox(-1.8F, 0.5F, -1.8F, 3.6F, 1.0F, 3.6F, new CubeDeformation(0.0F))
		.texOffs(96, 66).addBox(-1.8F, 5.9F, -1.8F, 3.6F, 1.0F, 3.6F, new CubeDeformation(0.0F))
		.texOffs(111, 66).addBox(1.025F, 1.4F, -1.675F, 0.65F, 4.6F, 0.65F, new CubeDeformation(0.0F))
		.texOffs(114, 66).addBox(-1.675F, 1.4F, -1.675F, 0.65F, 4.6F, 0.65F, new CubeDeformation(0.0F))
		.texOffs(117, 66).addBox(1.025F, 1.4F, 1.025F, 0.65F, 4.6F, 0.65F, new CubeDeformation(0.0F))
		.texOffs(120, 66).addBox(-1.675F, 1.4F, 1.025F, 0.65F, 4.6F, 0.65F, new CubeDeformation(0.0F))
		.texOffs(72, 52).addBox(-0.6F, 6.7F, -0.6F, 1.2F, 2.4F, 1.2F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, -4.2F));

		PartDefinition ice_crystal = staff_lantern.addOrReplaceChild("ice_crystal", CubeListBuilder.create().texOffs(13, 76).addBox(-1.1F, -1.7F, -1.1F, 2.2F, 3.4F, 2.2F, new CubeDeformation(0.0F))
		.texOffs(22, 76).addBox(-1.3F, -0.9F, -1.3F, 2.6F, 1.8F, 2.6F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.7F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

    @Override
    public void setupAnim(SkeletonRenderState state) {
        super.setupAnim(state);
        this.resetPose();

        float t = state.ageInTicks;
        float speed = state.walkAnimationSpeed;
        float hover = Mth.sin(t * 0.10F) * 1.4F;

        // Levitación espectral e inclinación al deslizarse hacia el jugador
        this.body.y += hover;
        this.body.xRot += 0.22F * speed;
        this.body.zRot += Mth.sin(t * 0.06F) * 0.045F;

        // Mirada sombría bajo la capucha
        this.head.yRot += state.yRot * Mth.DEG_TO_RAD * 0.75F;
        this.head.xRot += state.xRot * Mth.DEG_TO_RAD * 0.65F;

        // Túnica hecha jirones flotando en aire frío
        this.cloakFront.xRot += -0.10F * speed + Mth.sin(t * 0.14F) * 0.08F;
        this.cloakBack.xRot += 0.36F * speed + Mth.sin(t * 0.14F + 1.4F) * 0.10F;
        this.cloakLeft.zRot += Mth.sin(t * 0.12F + 0.7F) * 0.09F;
        this.cloakRight.zRot -= Mth.sin(t * 0.12F + 0.7F) * 0.09F;
        this.mantleL.zRot += Mth.sin(t * 0.10F) * 0.06F;
        this.mantleR.zRot -= Mth.sin(t * 0.10F) * 0.06F;

        // Báculo y gesto chamánico (canaliza rayo gélido cuando está agresivo)
        if (state.isAggressive) {
            this.rightArm.xRot += -0.22F + Mth.sin(t * 0.20F) * 0.09F;
            this.leftArm.xRot += -0.28F + Mth.cos(t * 0.22F) * 0.16F;
            this.leftArm.zRot += Mth.sin(t * 0.16F) * 0.12F;
        } else {
            this.rightArm.xRot += Mth.sin(t * 0.08F) * 0.07F;
            this.leftArm.xRot += Mth.cos(t * 0.08F) * 0.09F;
            this.leftArm.zRot += Mth.sin(t * 0.07F) * 0.06F;
        }

        // Balanceo del farol en el báculo y giro continuo del cristal de hielo
        this.staffLantern.xRot += -0.14F * speed + Mth.sin(t * 0.11F) * 0.08F;
        this.staffLantern.zRot += Mth.cos(t * 0.09F) * 0.06F;
        this.iceCrystal.yRot = t * 0.14F;
    }
}
