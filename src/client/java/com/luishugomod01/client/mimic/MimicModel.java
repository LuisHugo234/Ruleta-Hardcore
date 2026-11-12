package com.luishugomod01.client.mimic;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;

public class MimicModel extends EntityModel<MimicRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("ruleta_hardcore", "mimic"), "main");

    private final KeyframeAnimation chestIdleAnimation;
    private final KeyframeAnimation wakeUpAnimation;
    private final KeyframeAnimation standingIdleAnimation;
    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation biteAnimation;

    public MimicModel(ModelPart root) {
        super(root);
        this.chestIdleAnimation = MimicAnimations.CHEST_IDLE.bake(root);
        this.wakeUpAnimation = MimicAnimations.WAKE_UP.bake(root);
        this.standingIdleAnimation = MimicAnimations.STANDING_IDLE.bake(root);
        this.walkAnimation = MimicAnimations.WALK.bake(root);
        this.biteAnimation = MimicAnimations.BITE.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 19).addBox(-7.0F, -10.0F, -7.0F, 14.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(64, 16).addBox(-6.5F, -9.96F, -6.5F, 13.0F, 0.16F, 13.0F, new CubeDeformation(0.0F))
		.texOffs(64, 0).addBox(5.3F, -10.924F, -6.6F, 1.0F, 0.924F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(64, 8).addBox(5.55F, -12.2F, -6.35F, 0.5F, 1.276F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(68, 0).addBox(4.0F, -10.924F, -6.6F, 1.0F, 0.924F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(66, 8).addBox(4.25F, -12.2F, -6.35F, 0.5F, 1.276F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(72, 0).addBox(2.7F, -11.26F, -6.6F, 1.0F, 1.26F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(68, 8).addBox(2.95F, -13.0F, -6.35F, 0.5F, 1.74F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(76, 0).addBox(-3.7F, -11.134F, -6.6F, 1.0F, 1.134F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(70, 8).addBox(-3.45F, -12.7F, -6.35F, 0.5F, 1.566F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(80, 0).addBox(-5.0F, -10.924F, -6.6F, 1.0F, 0.924F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(72, 8).addBox(-4.75F, -12.2F, -6.35F, 0.5F, 1.276F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(84, 0).addBox(-6.3F, -11.26F, -6.6F, 1.0F, 1.26F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(74, 8).addBox(-6.05F, -13.0F, -6.35F, 0.5F, 1.74F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(88, 0).addBox(5.6F, -10.798F, -4.3F, 1.0F, 0.798F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(76, 8).addBox(5.85F, -11.9F, -4.05F, 0.5F, 1.102F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(92, 0).addBox(-6.6F, -10.798F, -4.3F, 1.0F, 0.798F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(78, 8).addBox(-6.35F, -11.9F, -4.05F, 0.5F, 1.102F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(96, 0).addBox(5.6F, -10.798F, -2.0F, 1.0F, 0.798F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(80, 8).addBox(5.85F, -11.9F, -1.75F, 0.5F, 1.102F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(100, 0).addBox(-6.6F, -10.798F, -2.0F, 1.0F, 0.798F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(82, 8).addBox(-6.35F, -11.9F, -1.75F, 0.5F, 1.102F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(104, 0).addBox(5.6F, -11.092F, 1.0F, 1.0F, 1.092F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(84, 8).addBox(5.85F, -12.6F, 1.25F, 0.5F, 1.508F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(108, 0).addBox(-6.6F, -11.092F, 1.0F, 1.0F, 1.092F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(86, 8).addBox(-6.35F, -12.6F, 1.25F, 0.5F, 1.508F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(112, 0).addBox(5.6F, -11.092F, 3.3F, 1.0F, 1.092F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(88, 8).addBox(5.85F, -12.6F, 3.55F, 0.5F, 1.508F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(116, 0).addBox(-6.6F, -11.092F, 3.3F, 1.0F, 1.092F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(90, 8).addBox(-6.35F, -12.6F, 3.55F, 0.5F, 1.508F, 0.5F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.5F, 0.0F));

		PartDefinition lid = body.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(-7.02F, -5.0F, -14.02F, 14.04F, 5.0F, 14.04F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.0F, -2.0F, -15.02F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(64, 16).addBox(-6.5F, -0.2F, -13.5F, 13.0F, 0.16F, 13.0F, new CubeDeformation(0.0F))
		.texOffs(120, 0).addBox(5.3F, 0.0F, -13.6F, 1.0F, 1.386F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(92, 8).addBox(5.55F, 1.386F, -13.35F, 0.5F, 1.914F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(124, 0).addBox(3.9F, 0.0F, -13.6F, 1.0F, 1.218F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(94, 8).addBox(4.15F, 1.218F, -13.35F, 0.5F, 1.682F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(64, 3).addBox(2.5F, 0.0F, -13.6F, 1.0F, 1.092F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(96, 8).addBox(2.75F, 1.092F, -13.35F, 0.5F, 1.508F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(68, 3).addBox(1.0F, 0.0F, -13.6F, 1.0F, 1.008F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(98, 8).addBox(1.25F, 1.008F, -13.35F, 0.5F, 1.392F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(72, 3).addBox(-0.5F, 0.0F, -13.6F, 1.0F, 1.008F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(100, 8).addBox(-0.25F, 1.008F, -13.35F, 0.5F, 1.392F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(76, 3).addBox(-2.0F, 0.0F, -13.6F, 1.0F, 1.008F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(102, 8).addBox(-1.75F, 1.008F, -13.35F, 0.5F, 1.392F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(80, 3).addBox(-3.5F, 0.0F, -13.6F, 1.0F, 1.386F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(104, 8).addBox(-3.25F, 1.386F, -13.35F, 0.5F, 1.914F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(84, 3).addBox(-4.9F, 0.0F, -13.6F, 1.0F, 1.008F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(106, 8).addBox(-4.65F, 1.008F, -13.35F, 0.5F, 1.392F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(88, 3).addBox(-6.3F, 0.0F, -13.6F, 1.0F, 1.218F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(108, 8).addBox(-6.05F, 1.218F, -13.35F, 0.5F, 1.682F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(92, 3).addBox(5.6F, 0.0F, -11.9F, 1.0F, 0.84F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(110, 8).addBox(5.85F, 0.84F, -11.65F, 0.5F, 1.16F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(96, 3).addBox(-6.6F, 0.0F, -11.9F, 1.0F, 0.84F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(112, 8).addBox(-6.35F, 0.84F, -11.65F, 0.5F, 1.16F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(100, 3).addBox(5.6F, 0.0F, -9.7F, 1.0F, 0.84F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(114, 8).addBox(5.85F, 0.84F, -9.45F, 0.5F, 1.16F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(104, 3).addBox(-6.6F, 0.0F, -9.7F, 1.0F, 0.84F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(116, 8).addBox(-6.35F, 0.84F, -9.45F, 0.5F, 1.16F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(108, 3).addBox(5.6F, 0.0F, -7.5F, 1.0F, 1.05F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(118, 8).addBox(5.85F, 1.05F, -7.25F, 0.5F, 1.45F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(112, 3).addBox(-6.6F, 0.0F, -7.5F, 1.0F, 1.05F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(120, 8).addBox(-6.35F, 1.05F, -7.25F, 0.5F, 1.45F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(116, 3).addBox(5.6F, 0.0F, -5.3F, 1.0F, 1.05F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(122, 8).addBox(5.85F, 1.05F, -5.05F, 0.5F, 1.45F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(120, 3).addBox(-6.6F, 0.0F, -5.3F, 1.0F, 1.05F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(124, 8).addBox(-6.35F, 1.05F, -5.05F, 0.5F, 1.45F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(124, 3).addBox(5.6F, 0.0F, -3.1F, 1.0F, 0.84F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(126, 8).addBox(5.85F, 0.84F, -2.85F, 0.5F, 1.16F, 0.5F, new CubeDeformation(0.0F))
		.texOffs(64, 6).addBox(-6.6F, 0.0F, -3.1F, 1.0F, 0.84F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(64, 11).addBox(-6.35F, 0.84F, -2.85F, 0.5F, 1.16F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, 7.0F, -0.7854F, 0.0F, 0.0F));

		PartDefinition tongue = body.addOrReplaceChild("tongue", CubeListBuilder.create().texOffs(64, 32).addBox(-2.0F, -1.4F, -9.8F, 4.0F, 1.4F, 9.8F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.2F, 2.0F));

		PartDefinition tongue_mid = tongue.addOrReplaceChild("tongue_mid", CubeListBuilder.create().texOffs(92, 32).addBox(-1.7F, -0.65F, -6.0F, 3.4F, 1.3F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.7F, -9.8F, 1.1868F, 0.0F, 0.0F));

		PartDefinition tongue_tip = tongue_mid.addOrReplaceChild("tongue_tip", CubeListBuilder.create().texOffs(111, 32).addBox(-1.25F, -0.55F, -3.8F, 2.5F, 1.1F, 3.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 64).addBox(-1.15F, 0.0F, -1.15F, 2.3F, 11.843F, 2.3F, new CubeDeformation(0.0F))
		.texOffs(10, 64).addBox(-1.35F, -0.45F, -1.35F, 2.7F, 2.1F, 2.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -10.2F, -4.4F, -0.2388F, 0.0F, -1.6055F));

		PartDefinition left_forearm = left_arm.addOrReplaceChild("left_forearm", CubeListBuilder.create().texOffs(21, 64).addBox(-1.45F, -1.45F, -1.45F, 2.9F, 2.9F, 2.9F, new CubeDeformation(0.0F))
		.texOffs(33, 64).addBox(-1.15F, 0.0F, -1.15F, 2.3F, 12.034F, 2.3F, new CubeDeformation(0.0F))
		.texOffs(43, 64).addBox(-0.925F, 12.034F, -0.925F, 1.85F, 9.846F, 1.85F, new CubeDeformation(0.0F))
		.texOffs(96, 116).addBox(-1.35F, 19.23F, -1.35F, 2.7F, 1.7F, 2.7F, new CubeDeformation(0.0F))
		.texOffs(107, 116).addBox(0.95F, 20.63F, -0.25F, 0.8F, 1.5F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.843F, 0.0F, -0.2834F, -0.2365F, 1.4326F));

		PartDefinition left_hand = left_forearm.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(51, 64).addBox(-1.1F, -0.75F, -0.9F, 2.2F, 1.9F, 2.2F, new CubeDeformation(0.0F))
		.texOffs(60, 64).addBox(-1.8F, 0.1F, -3.4F, 3.6F, 1.3F, 4.2F, new CubeDeformation(0.0F))
		.texOffs(76, 64).addBox(-1.9F, 0.025F, -3.7F, 3.8F, 1.05F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 21.88F, 0.0F, 0.3597F, -0.5149F, -0.0054F));

		PartDefinition left_finger0 = left_hand.addOrReplaceChild("left_finger0", CubeListBuilder.create().texOffs(0, 104).addBox(-0.35F, -0.35F, -2.9F, 0.7F, 0.7F, 3.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.35F, 0.65F, -3.5F, -0.0698F, 0.2121F, 0.0F));

		PartDefinition left_finger0_tip = left_finger0.addOrReplaceChild("left_finger0_tip", CubeListBuilder.create().texOffs(8, 104).addBox(-0.28F, -0.28F, -2.262F, 0.56F, 0.56F, 2.412F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.9F, 0.2793F, 0.0F, 0.0F));

		PartDefinition left_claw0 = left_finger0_tip.addOrReplaceChild("left_claw0", CubeListBuilder.create().texOffs(0, 116).addBox(-0.2F, -0.22F, -1.6F, 0.4F, 0.5F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.262F, 0.2094F, 0.0F, 0.0F));

		PartDefinition left_finger1 = left_hand.addOrReplaceChild("left_finger1", CubeListBuilder.create().texOffs(14, 104).addBox(-0.35F, -0.35F, -3.6F, 0.7F, 0.7F, 3.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.45F, 0.65F, -3.5F, -0.0698F, 0.0707F, 0.0F));

		PartDefinition left_finger1_tip = left_finger1.addOrReplaceChild("left_finger1_tip", CubeListBuilder.create().texOffs(23, 104).addBox(-0.28F, -0.28F, -2.808F, 0.56F, 0.56F, 2.958F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.6F, 0.2793F, 0.0F, 0.0F));

		PartDefinition left_claw1 = left_finger1_tip.addOrReplaceChild("left_claw1", CubeListBuilder.create().texOffs(5, 116).addBox(-0.2F, -0.22F, -1.6F, 0.4F, 0.5F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.808F, 0.2094F, 0.0F, 0.0F));

		PartDefinition left_finger2 = left_hand.addOrReplaceChild("left_finger2", CubeListBuilder.create().texOffs(31, 104).addBox(-0.35F, -0.35F, -3.4F, 0.7F, 0.7F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.45F, 0.65F, -3.5F, -0.0698F, -0.0707F, 0.0F));

		PartDefinition left_finger2_tip = left_finger2.addOrReplaceChild("left_finger2_tip", CubeListBuilder.create().texOffs(40, 104).addBox(-0.28F, -0.28F, -2.652F, 0.56F, 0.56F, 2.802F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.4F, 0.2793F, 0.0F, 0.0F));

		PartDefinition left_claw2 = left_finger2_tip.addOrReplaceChild("left_claw2", CubeListBuilder.create().texOffs(10, 116).addBox(-0.2F, -0.22F, -1.6F, 0.4F, 0.5F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.652F, 0.2094F, 0.0F, 0.0F));

		PartDefinition left_finger3 = left_hand.addOrReplaceChild("left_finger3", CubeListBuilder.create().texOffs(47, 104).addBox(-0.35F, -0.35F, -2.7F, 0.7F, 0.7F, 2.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.35F, 0.65F, -3.5F, -0.0698F, -0.2121F, 0.0F));

		PartDefinition left_finger3_tip = left_finger3.addOrReplaceChild("left_finger3_tip", CubeListBuilder.create().texOffs(55, 104).addBox(-0.28F, -0.28F, -2.106F, 0.56F, 0.56F, 2.256F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.7F, 0.2793F, 0.0F, 0.0F));

		PartDefinition left_claw3 = left_finger3_tip.addOrReplaceChild("left_claw3", CubeListBuilder.create().texOffs(15, 116).addBox(-0.2F, -0.22F, -1.6F, 0.4F, 0.5F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.106F, 0.2094F, 0.0F, 0.0F));

		PartDefinition left_thumb = left_hand.addOrReplaceChild("left_thumb", CubeListBuilder.create().texOffs(61, 104).addBox(-0.36F, -0.36F, -2.2F, 0.72F, 0.72F, 2.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.7F, 0.7F, -1.4F, -0.0349F, 0.8378F, 0.0F));

		PartDefinition left_thumb_tip = left_thumb.addOrReplaceChild("left_thumb_tip", CubeListBuilder.create().texOffs(68, 104).addBox(-0.28F, -0.28F, -1.7F, 0.56F, 0.56F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.2F, 0.2443F, 0.0F, 0.0F));

		PartDefinition left_thumb_claw = left_thumb_tip.addOrReplaceChild("left_thumb_claw", CubeListBuilder.create().texOffs(20, 116).addBox(-0.2F, -0.2F, -1.3F, 0.4F, 0.46F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.7F, 0.2094F, 0.0F, 0.0F));

		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(86, 64).addBox(-1.15F, 0.0F, -1.15F, 2.3F, 11.843F, 2.3F, new CubeDeformation(0.0F))
		.texOffs(96, 64).addBox(-1.35F, -0.45F, -1.35F, 2.7F, 2.1F, 2.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -10.2F, -4.4F, -0.2388F, 0.0F, 1.6055F));

		PartDefinition right_forearm = right_arm.addOrReplaceChild("right_forearm", CubeListBuilder.create().texOffs(107, 64).addBox(-1.45F, -1.45F, -1.45F, 2.9F, 2.9F, 2.9F, new CubeDeformation(0.0F))
		.texOffs(0, 79).addBox(-1.15F, 0.0F, -1.15F, 2.3F, 12.034F, 2.3F, new CubeDeformation(0.0F))
		.texOffs(10, 79).addBox(-0.925F, 12.034F, -0.925F, 1.85F, 9.846F, 1.85F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.843F, 0.0F, -0.2834F, 0.2365F, -1.4326F));

		PartDefinition right_hand = right_forearm.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(18, 79).addBox(-1.1F, -0.75F, -0.9F, 2.2F, 1.9F, 2.2F, new CubeDeformation(0.0F))
		.texOffs(27, 79).addBox(-1.8F, 0.1F, -3.4F, 3.6F, 1.3F, 4.2F, new CubeDeformation(0.0F))
		.texOffs(43, 79).addBox(-1.9F, 0.025F, -3.7F, 3.8F, 1.05F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 21.88F, 0.0F, 0.3597F, 0.5149F, 0.0054F));

		PartDefinition right_finger0 = right_hand.addOrReplaceChild("right_finger0", CubeListBuilder.create().texOffs(73, 104).addBox(-0.35F, -0.35F, -2.9F, 0.7F, 0.7F, 3.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.35F, 0.65F, -3.5F, -0.0698F, -0.2121F, 0.0F));

		PartDefinition right_finger0_tip = right_finger0.addOrReplaceChild("right_finger0_tip", CubeListBuilder.create().texOffs(81, 104).addBox(-0.28F, -0.28F, -2.262F, 0.56F, 0.56F, 2.412F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.9F, 0.2793F, 0.0F, 0.0F));

		PartDefinition right_claw0 = right_finger0_tip.addOrReplaceChild("right_claw0", CubeListBuilder.create().texOffs(24, 116).addBox(-0.2F, -0.22F, -1.6F, 0.4F, 0.5F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.262F, 0.2094F, 0.0F, 0.0F));

		PartDefinition right_finger1 = right_hand.addOrReplaceChild("right_finger1", CubeListBuilder.create().texOffs(87, 104).addBox(-0.35F, -0.35F, -3.6F, 0.7F, 0.7F, 3.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.45F, 0.65F, -3.5F, -0.0698F, -0.0707F, 0.0F));

		PartDefinition right_finger1_tip = right_finger1.addOrReplaceChild("right_finger1_tip", CubeListBuilder.create().texOffs(96, 104).addBox(-0.28F, -0.28F, -2.808F, 0.56F, 0.56F, 2.958F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.6F, 0.2793F, 0.0F, 0.0F));

		PartDefinition right_claw1 = right_finger1_tip.addOrReplaceChild("right_claw1", CubeListBuilder.create().texOffs(29, 116).addBox(-0.2F, -0.22F, -1.6F, 0.4F, 0.5F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.808F, 0.2094F, 0.0F, 0.0F));

		PartDefinition right_finger2 = right_hand.addOrReplaceChild("right_finger2", CubeListBuilder.create().texOffs(104, 104).addBox(-0.35F, -0.35F, -3.4F, 0.7F, 0.7F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.45F, 0.65F, -3.5F, -0.0698F, 0.0707F, 0.0F));

		PartDefinition right_finger2_tip = right_finger2.addOrReplaceChild("right_finger2_tip", CubeListBuilder.create().texOffs(113, 104).addBox(-0.28F, -0.28F, -2.652F, 0.56F, 0.56F, 2.802F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -3.4F, 0.2793F, 0.0F, 0.0F));

		PartDefinition right_claw2 = right_finger2_tip.addOrReplaceChild("right_claw2", CubeListBuilder.create().texOffs(34, 116).addBox(-0.2F, -0.22F, -1.6F, 0.4F, 0.5F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.652F, 0.2094F, 0.0F, 0.0F));

		PartDefinition right_finger3 = right_hand.addOrReplaceChild("right_finger3", CubeListBuilder.create().texOffs(120, 104).addBox(-0.35F, -0.35F, -2.7F, 0.7F, 0.7F, 2.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.35F, 0.65F, -3.5F, -0.0698F, 0.2121F, 0.0F));

		PartDefinition right_finger3_tip = right_finger3.addOrReplaceChild("right_finger3_tip", CubeListBuilder.create().texOffs(0, 109).addBox(-0.28F, -0.28F, -2.106F, 0.56F, 0.56F, 2.256F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.7F, 0.2793F, 0.0F, 0.0F));

		PartDefinition right_claw3 = right_finger3_tip.addOrReplaceChild("right_claw3", CubeListBuilder.create().texOffs(39, 116).addBox(-0.2F, -0.22F, -1.6F, 0.4F, 0.5F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.106F, 0.2094F, 0.0F, 0.0F));

		PartDefinition right_thumb = right_hand.addOrReplaceChild("right_thumb", CubeListBuilder.create().texOffs(6, 109).addBox(-0.36F, -0.36F, -2.2F, 0.72F, 0.72F, 2.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.7F, 0.7F, -1.4F, -0.0349F, -0.8378F, 0.0F));

		PartDefinition right_thumb_tip = right_thumb.addOrReplaceChild("right_thumb_tip", CubeListBuilder.create().texOffs(13, 109).addBox(-0.28F, -0.28F, -1.7F, 0.56F, 0.56F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.2F, 0.2443F, 0.0F, 0.0F));

		PartDefinition right_thumb_claw = right_thumb_tip.addOrReplaceChild("right_thumb_claw", CubeListBuilder.create().texOffs(44, 116).addBox(-0.2F, -0.2F, -1.3F, 0.4F, 0.46F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.7F, 0.2094F, 0.0F, 0.0F));

		PartDefinition left_leg = body.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(53, 79).addBox(-1.7F, -0.4F, -1.7F, 3.4F, 2.4F, 3.4F, new CubeDeformation(0.0F))
		.texOffs(67, 79).addBox(-1.45F, 0.0F, -1.45F, 2.9F, 8.76F, 2.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.2F, -1.2F, 4.4F, 0.5262F, 0.0F, -0.8322F));

		PartDefinition left_shin = left_leg.addOrReplaceChild("left_shin", CubeListBuilder.create().texOffs(79, 79).addBox(-1.55F, -1.55F, -1.55F, 3.1F, 3.1F, 3.1F, new CubeDeformation(0.0F))
		.texOffs(92, 79).addBox(-1.125F, 0.0F, -1.125F, 2.25F, 7.826F, 2.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.76F, 0.0F, 0.4037F, 0.4459F, 0.9667F));

		PartDefinition left_foot = left_shin.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(101, 79).addBox(-1.15F, -0.7F, -1.15F, 2.3F, 2.0F, 2.3F, new CubeDeformation(0.0F))
		.texOffs(0, 94).addBox(-1.6F, 0.2F, -4.0F, 3.2F, 1.45F, 5.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.826F, 0.0F, -0.7351F, -0.367F, 0.0553F));

		PartDefinition left_toe0 = left_foot.addOrReplaceChild("left_toe0", CubeListBuilder.create().texOffs(18, 109).addBox(-0.4F, -0.4F, -1.9F, 0.8F, 0.8F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.05F, 0.95F, -4.0F, 0.0349F, -0.1833F, 0.0F));

		PartDefinition left_toe0_claw = left_toe0.addOrReplaceChild("left_toe0_claw", CubeListBuilder.create().texOffs(48, 116).addBox(-0.24F, -0.22F, -1.3F, 0.48F, 0.5F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.9F, 0.3136F, 0.1831F, -0.0065F));

		PartDefinition left_toe1 = left_foot.addOrReplaceChild("left_toe1", CubeListBuilder.create().texOffs(24, 109).addBox(-0.4F, -0.4F, -1.9F, 0.8F, 0.8F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.95F, -4.0F, 0.0349F, 0.0F, 0.0F));

		PartDefinition left_toe1_claw = left_toe1.addOrReplaceChild("left_toe1_claw", CubeListBuilder.create().texOffs(52, 116).addBox(-0.24F, -0.22F, -1.3F, 0.48F, 0.5F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.9F, 0.3142F, 0.0F, 0.0F));

		PartDefinition left_toe2 = left_foot.addOrReplaceChild("left_toe2", CubeListBuilder.create().texOffs(30, 109).addBox(-0.4F, -0.4F, -1.9F, 0.8F, 0.8F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.05F, 0.95F, -4.0F, 0.0349F, 0.1833F, 0.0F));

		PartDefinition left_toe2_claw = left_toe2.addOrReplaceChild("left_toe2_claw", CubeListBuilder.create().texOffs(56, 116).addBox(-0.24F, -0.22F, -1.3F, 0.48F, 0.5F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.9F, 0.3136F, -0.1831F, 0.0065F));

		PartDefinition right_leg = body.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(18, 94).addBox(-1.7F, -0.4F, -1.7F, 3.4F, 2.4F, 3.4F, new CubeDeformation(0.0F))
		.texOffs(0, 64).addBox(-1.45F, 0.0F, -1.45F, 2.9F, 8.76F, 2.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.2F, -1.2F, 4.4F, 0.5262F, 0.0F, 0.8322F));

		PartDefinition right_shin = right_leg.addOrReplaceChild("right_shin", CubeListBuilder.create().texOffs(12, 64).addBox(-1.55F, -1.55F, -1.55F, 3.1F, 3.1F, 3.1F, new CubeDeformation(0.0F))
		.texOffs(25, 64).addBox(-1.125F, 0.0F, -1.125F, 2.25F, 7.826F, 2.25F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.76F, 0.0F, 0.4037F, -0.4459F, -0.9667F));

		PartDefinition right_foot = right_shin.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(34, 64).addBox(-1.15F, -0.7F, -1.15F, 2.3F, 2.0F, 2.3F, new CubeDeformation(0.0F))
		.texOffs(44, 64).addBox(-1.6F, 0.2F, -4.0F, 3.2F, 1.45F, 5.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.826F, 0.0F, -0.7351F, 0.367F, -0.0553F));

		PartDefinition right_toe0 = right_foot.addOrReplaceChild("right_toe0", CubeListBuilder.create().texOffs(36, 109).addBox(-0.4F, -0.4F, -1.9F, 0.8F, 0.8F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.05F, 0.95F, -4.0F, 0.0349F, -0.1833F, 0.0F));

		PartDefinition right_toe0_claw = right_toe0.addOrReplaceChild("right_toe0_claw", CubeListBuilder.create().texOffs(60, 116).addBox(-0.24F, -0.22F, -1.3F, 0.48F, 0.5F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.9F, 0.3136F, 0.1831F, -0.0065F));

		PartDefinition right_toe1 = right_foot.addOrReplaceChild("right_toe1", CubeListBuilder.create().texOffs(42, 109).addBox(-0.4F, -0.4F, -1.9F, 0.8F, 0.8F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.95F, -4.0F, 0.0349F, 0.0F, 0.0F));

		PartDefinition right_toe1_claw = right_toe1.addOrReplaceChild("right_toe1_claw", CubeListBuilder.create().texOffs(64, 116).addBox(-0.24F, -0.22F, -1.3F, 0.48F, 0.5F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.9F, 0.3142F, 0.0F, 0.0F));

		PartDefinition right_toe2 = right_foot.addOrReplaceChild("right_toe2", CubeListBuilder.create().texOffs(48, 109).addBox(-0.4F, -0.4F, -1.9F, 0.8F, 0.8F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.05F, 0.95F, -4.0F, 0.0349F, 0.1833F, 0.0F));

		PartDefinition right_toe2_claw = right_toe2.addOrReplaceChild("right_toe2_claw", CubeListBuilder.create().texOffs(68, 116).addBox(-0.24F, -0.22F, -1.3F, 0.48F, 0.5F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.9F, 0.3136F, -0.1831F, 0.0065F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

    @Override
    public void setupAnim(MimicRenderState state) {
        super.setupAnim(state);
        this.resetPose();
        if (state.isDisguised) {
            this.chestIdleAnimation.apply(state.chestIdleAnimationState, state.ageInTicks);
        } else if (state.isWakingUp) {
            this.wakeUpAnimation.apply(state.wakeUpAnimationState, state.ageInTicks);
        } else {
            this.standingIdleAnimation.apply(state.idleAnimationState, state.ageInTicks);
            this.walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2.0F, 2.5F);
            this.biteAnimation.apply(state.biteAnimationState, state.ageInTicks);
        }
    }
}
