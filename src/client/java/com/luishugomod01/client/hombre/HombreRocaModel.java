package com.luishugomod01.client.hombre;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;

/**
 * Modelo 3D y animaciones (idle, walk, throw_rock) del Hombre Roca
 * sosteniendo su roca gigante en la mano derecha.
 */
public class HombreRocaModel extends EntityModel<HombreRocaRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("ruleta_hardcore", "hombre_roca"), "main");

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            "ruleta_hardcore", "textures/entity/hombre_roca.png");

    private final ModelPart rootPart;
    private final ModelPart pelvis;
    private final ModelPart torso;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart rightForearm;
    private final ModelPart rightHand;
    private final ModelPart rock;
    private final ModelPart leftArm;
    private final ModelPart leftForearm;
    private final ModelPart leftHand;
    private final ModelPart rightLeg;
    private final ModelPart rightShin;
    private final ModelPart rightFoot;
    private final ModelPart leftLeg;
    private final ModelPart leftShin;
    private final ModelPart leftFoot;

    public final KeyframeAnimation idleAnimation;
    public final KeyframeAnimation walkAnimation;
    public final KeyframeAnimation throwRockAnimation;

    public HombreRocaModel(ModelPart root) {
        super(root);
        this.rootPart = root.getChild("root");
        this.pelvis = this.rootPart.getChild("pelvis");
        this.torso = this.pelvis.getChild("torso");
        this.neck = this.torso.getChild("neck");
        this.head = this.neck.getChild("head");
        this.rightArm = this.torso.getChild("right_arm");
        this.rightForearm = this.rightArm.getChild("right_forearm");
        this.rightHand = this.rightForearm.getChild("right_hand");
        this.rock = this.rightHand.getChild("rock");
        this.leftArm = this.torso.getChild("left_arm");
        this.leftForearm = this.leftArm.getChild("left_forearm");
        this.leftHand = this.leftForearm.getChild("left_hand");
        this.rightLeg = this.pelvis.getChild("right_leg");
        this.rightShin = this.rightLeg.getChild("right_shin");
        this.rightFoot = this.rightShin.getChild("right_foot");
        this.leftLeg = this.pelvis.getChild("left_leg");
        this.leftShin = this.leftLeg.getChild("left_shin");
        this.leftFoot = this.leftShin.getChild("left_foot");

        this.idleAnimation = HombreRocaAnimations.IDLE.bake(root);
        this.walkAnimation = HombreRocaAnimations.WALK.bake(root);
        this.throwRockAnimation = HombreRocaAnimations.THROW_ROCK.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition pelvis = root.addOrReplaceChild("pelvis", CubeListBuilder.create()
                .texOffs(0, 78).addBox(-5.0F, -2.15F, -2.5F, 10.0F, 3.0F, 5.0F, new CubeDeformation(-0.06F)),
                PartPose.offset(0.0F, -15.0F, 0.0F));

        PartDefinition torso = pelvis.addOrReplaceChild("torso", CubeListBuilder.create()
                .texOffs(0, 32).addBox(-5.0F, -13.0F, -2.5F, 10.0F, 13.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(64, 36).addBox(-4.8F, -13.25F, -2.5F, 3.0F, 1.0F, 5.0F, new CubeDeformation(-0.12F))
                .texOffs(80, 36).addBox(1.8F, -13.25F, -2.5F, 3.0F, 1.0F, 5.0F, new CubeDeformation(-0.12F))
                .texOffs(30, 32).addBox(-3.0F, -13.8F, -2.75F, 6.0F, 2.0F, 5.0F, new CubeDeformation(-0.15F))
                .texOffs(102, 36).addBox(1.1F, -10.1F, -2.85F, 3.0F, 3.0F, 1.0F, new CubeDeformation(-0.05F)),
                PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition neck = torso.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0F, -2.7F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(-0.08F)),
                PartPose.offset(0.0F, -13.2F, 0.0F));

        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(44, 0).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(84, 0).addBox(-4.5F, -10.3F, -4.3F, 9.0F, 2.0F, 9.0F, new CubeDeformation(-0.06F))
                .texOffs(84, 11).addBox(-4.0F, -8.3F, 3.65F, 8.0F, 8.0F, 1.0F, new CubeDeformation(0.12F))
                .texOffs(102, 11).addBox(-4.0F, -8.9F, -4.4F, 8.0F, 2.0F, 1.0F, new CubeDeformation(-0.04F))
                .texOffs(120, 11).addBox(-4.6F, -8.4F, 1.0F, 1.0F, 7.0F, 3.0F, new CubeDeformation(-0.05F))
                .texOffs(84, 21).addBox(3.6F, -8.4F, 1.0F, 1.0F, 7.0F, 3.0F, new CubeDeformation(-0.05F))
                .texOffs(102, 60).addBox(-5.0F, -11.0F, -1.0F, 10.0F, 1.0F, 2.0F, new CubeDeformation(-0.05F))
                .texOffs(102, 63).addBox(-5.5F, -6.5F, -2.2F, 2.0F, 4.0F, 4.0F, new CubeDeformation(-0.05F))
                .texOffs(114, 63).addBox(3.5F, -6.5F, -2.2F, 2.0F, 4.0F, 4.0F, new CubeDeformation(-0.05F))
                .texOffs(110, 36).addBox(-5.9F, -5.0F, -0.7F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.12F))
                .texOffs(114, 36).addBox(4.9F, -5.0F, -0.7F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.12F)),
                PartPose.offset(0.0F, -2.3F, 0.0F));

        PartDefinition right_arm = torso.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(0, 50).addBox(-2.3F, -0.2F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(96, 36).addBox(-2.55F, -0.2F, -1.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.12F)),
                PartPose.offset(-6.5F, -12.0F, 0.0F));

        PartDefinition right_forearm = right_arm.addOrReplaceChild("right_forearm", CubeListBuilder.create()
                .texOffs(16, 50).addBox(-2.0F, -0.1F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.08F))
                .texOffs(64, 44).addBox(-2.2F, -0.1F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(-0.14F)),
                PartPose.offset(-0.3F, 6.0F, 0.0F));

        PartDefinition right_hand = right_forearm.addOrReplaceChild("right_hand", CubeListBuilder.create()
                .texOffs(16, 0).addBox(-1.5F, -0.1F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.05F)),
                PartPose.offset(0.0F, 5.0F, 0.0F));

        right_hand.addOrReplaceChild("rock", CubeListBuilder.create()
                .texOffs(64, 94).addBox(-6.4F, -0.4F, -6.7F, 9.0F, 9.0F, 9.0F, new CubeDeformation(-0.05F))
                .texOffs(100, 94).addBox(-5.3F, -1.6F, -5.6F, 7.0F, 3.0F, 7.0F, new CubeDeformation(-0.05F))
                .texOffs(64, 112).addBox(-5.0F, 1.3F, -7.9F, 6.0F, 6.0F, 3.0F, new CubeDeformation(-0.05F))
                .texOffs(82, 112).addBox(-7.7F, 0.9F, -5.1F, 3.0F, 6.0F, 6.0F, new CubeDeformation(-0.05F))
                .texOffs(100, 112).addBox(-4.8F, 6.8F, -5.3F, 6.0F, 3.0F, 6.0F, new CubeDeformation(-0.05F)),
                PartPose.offset(-1.4F, -0.6F, -2.4F));

        PartDefinition left_arm = torso.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(32, 50).addBox(-1.7F, -0.2F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(70, 44).addBox(1.55F, -0.2F, -1.0F, 1.0F, 6.0F, 2.0F, new CubeDeformation(-0.12F)),
                PartPose.offset(6.5F, -12.0F, 0.0F));

        PartDefinition left_forearm = left_arm.addOrReplaceChild("left_forearm", CubeListBuilder.create()
                .texOffs(48, 50).addBox(-2.0F, -0.1F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.08F))
                .texOffs(76, 44).addBox(1.2F, -0.1F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(-0.14F)),
                PartPose.offset(0.3F, 6.0F, 0.0F));

        left_forearm.addOrReplaceChild("left_hand", CubeListBuilder.create()
                .texOffs(28, 0).addBox(-1.5F, -0.1F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.05F)),
                PartPose.offset(0.0F, 5.0F, 0.0F));

        PartDefinition right_leg = pelvis.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(30, 78).addBox(-2.0F, -0.2F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.6F, 0.5F, 0.0F));

        PartDefinition right_shin = right_leg.addOrReplaceChild("right_shin", CubeListBuilder.create()
                .texOffs(46, 78).addBox(-2.0F, -0.2F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(-0.12F)),
                PartPose.offset(0.0F, 6.7F, 0.0F));

        right_shin.addOrReplaceChild("right_foot", CubeListBuilder.create()
                .texOffs(64, 66).addBox(-2.0F, 0.1F, -3.05F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.1F)),
                PartPose.offset(0.0F, 5.7F, 0.0F));

        PartDefinition left_leg = pelvis.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(0, 89).addBox(-2.0F, -0.2F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(2.6F, 0.5F, 0.0F));

        PartDefinition left_shin = left_leg.addOrReplaceChild("left_shin", CubeListBuilder.create()
                .texOffs(16, 89).addBox(-2.0F, -0.2F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(-0.12F)),
                PartPose.offset(0.0F, 6.7F, 0.0F));

        left_shin.addOrReplaceChild("left_foot", CubeListBuilder.create()
                .texOffs(82, 66).addBox(-2.0F, 0.1F, -3.05F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.1F)),
                PartPose.offset(0.0F, 5.7F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(HombreRocaRenderState state) {
        super.setupAnim(state);
        this.resetPose();
        this.idleAnimation.apply(state.idleAnimationState, state.ageInTicks);
        this.walkAnimation.applyWalk(state.walkAnimationPos, state.walkAnimationSpeed, 2.0F, 2.5F);
        this.throwRockAnimation.apply(state.throwAnimationState, state.ageInTicks);
        this.head.yRot += state.yRot * ((float) Math.PI / 180.0F) * 0.5F;
        this.head.xRot += state.xRot * ((float) Math.PI / 180.0F) * 0.5F;
    }
}
