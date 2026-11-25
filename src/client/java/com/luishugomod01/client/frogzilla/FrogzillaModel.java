package com.luishugomod01.client.frogzilla;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class FrogzillaModel extends EntityModel<FrogzillaRenderState> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("ruleta_hardcore", "frogzilla"), "main");

    public static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/frogzilla.png");

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart topHat;
    private final ModelPart whiskerL;
    private final ModelPart whiskerR;
    private final ModelPart tongue;
    private final ModelPart swallowBelly;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public FrogzillaModel(ModelPart root) {
        super(root);
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.head = this.body.getChild("head");
        this.topHat = this.head.getChild("top_hat");
        this.whiskerL = this.head.getChild("whisker_l");
        this.whiskerR = this.head.getChild("whisker_r");
        this.tongue = this.body.getChild("tongue");
        this.swallowBelly = this.body.getChild("swallow_belly");
        this.leftArm = this.body.getChild("left_arm");
        this.rightArm = this.body.getChild("right_arm");
        this.leftLeg = this.root.getChild("left_leg");
        this.rightLeg = this.root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-7.0F, -6.0F, -17.0F, 14.0F, 6.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(65, 0).addBox(-5.4F, -5.95F, -16.2F, 1.0F, 0.95F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(70, 0).addBox(4.4F, -5.95F, -16.2F, 1.0F, 0.95F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 8.0F));
        PartDefinition swallow_belly = body.addOrReplaceChild("swallow_belly", CubeListBuilder.create()
                .texOffs(75, 0).addBox(-6.0F, -2.6F, -6.2F, 12.0F, 5.0F, 12.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -9.0F));
        PartDefinition coat = body.addOrReplaceChild("coat", CubeListBuilder.create()
                .texOffs(126, 0).addBox(-7.5F, -5.1F, -9.5F, 15.0F, 4.6F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(179, 0).addBox(-7.5F, -5.0F, -17.35F, 3.7F, 4.6F, 8.15F, new CubeDeformation(0.0F))
                .texOffs(206, 0).addBox(3.8F, -5.0F, -17.35F, 3.7F, 4.6F, 8.15F, new CubeDeformation(0.0F))
                .texOffs(35, 25).addBox(-3.5F, -2.5F, -17.3F, 7.0F, 1.3F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        coat.addOrReplaceChild("coat_collar_back", CubeListBuilder.create().texOffs(0, 25).addBox(-7.4F, -3.2F, -0.3F, 14.8F, 3.4F, 1.4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 1.5F, 0.2793F, 0.0F, 0.0F));
        coat.addOrReplaceChild("coattail_l", CubeListBuilder.create().texOffs(52, 25).addBox(-2.9F, 0.0F, -0.6F, 5.8F, 3.8F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.7F, -2.0F, 1.2F, -0.2793F, 0.0F, 0.0F));
        coat.addOrReplaceChild("coattail_r", CubeListBuilder.create().texOffs(69, 25).addBox(-2.9F, 0.0F, -0.6F, 5.8F, 3.8F, 1.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7F, -2.0F, 1.2F, -0.2793F, 0.0F, 0.0F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(86, 25).addBox(-7.0F, -6.0F, -17.0F, 14.0F, 6.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(151, 25).addBox(-7.7F, -2.6F, -17.2F, 2.9F, 2.8F, 6.7F, new CubeDeformation(0.0F))
                .texOffs(172, 25).addBox(4.8F, -2.6F, -17.2F, 2.9F, 2.8F, 6.7F, new CubeDeformation(0.0F))
                .texOffs(193, 25).addBox(-5.4F, -0.3F, -16.0F, 1.0F, 0.9F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(198, 25).addBox(4.4F, -0.3F, -16.0F, 1.0F, 0.9F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));
        PartDefinition eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create()
                .texOffs(203, 25).addBox(-7.0F, -4.0F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(228, 25).addBox(1.0F, -4.0F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -13.0F));
        PartDefinition top_hat = head.addOrReplaceChild("top_hat", CubeListBuilder.create()
                .texOffs(0, 50).addBox(-3.6F, -1.0F, -3.6F, 7.2F, 1.0F, 7.2F, new CubeDeformation(0.0F))
                .texOffs(33, 50).addBox(-2.6F, -2.7F, -2.6F, 5.2F, 1.7F, 5.2F, new CubeDeformation(0.0F))
                .texOffs(58, 50).addBox(-2.4F, -8.0F, -2.4F, 4.8F, 5.3F, 4.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.0F, -7.9F, -0.1047F, 0.1745F, 0.2443F));
        PartDefinition whisker_l = head.addOrReplaceChild("whisker_l", CubeListBuilder.create()
                .texOffs(79, 50).addBox(-6.2F, -0.6F, -0.6F, 6.2F, 1.2F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, -1.2F, -15.6F, 0.0F, 0.384F, -0.1396F));
        PartDefinition whisker_l_tip_grp = whisker_l.addOrReplaceChild("whisker_l_tip_grp", CubeListBuilder.create()
                .texOffs(98, 50).addBox(-6.2F, -0.4F, -0.4F, 6.2F, 0.8F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, 0.0F, 0.4189F, -0.2443F));
        PartDefinition whisker_r = head.addOrReplaceChild("whisker_r", CubeListBuilder.create()
                .texOffs(115, 50).addBox(0.0F, -0.6F, -0.6F, 6.2F, 1.2F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, -1.2F, -15.6F, 0.0F, -0.384F, 0.1396F));
        PartDefinition whisker_r_tip_grp = whisker_r.addOrReplaceChild("whisker_r_tip_grp", CubeListBuilder.create()
                .texOffs(134, 50).addBox(0.0F, -0.4F, -0.4F, 6.2F, 0.8F, 0.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, 0.0F, -0.4189F, 0.2443F));
        PartDefinition tongue = body.addOrReplaceChild("tongue", CubeListBuilder.create()
                .texOffs(151, 50).addBox(-2.0F, -0.7F, -13.0F, 4.0F, 1.4F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(186, 50).addBox(-2.6F, -1.0F, -15.7F, 5.2F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.2F, -2.5F));
        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(205, 50).addBox(-2.4F, -1.2F, -2.4F, 3.6F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(224, 50).addBox(-2.0F, 2.6F, -1.9F, 3.0F, 3.6F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 66).addBox(-4.5F, 6.0F, -6.2F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -3.0F, -13.2F));
        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(33, 66).addBox(-1.2F, -1.2F, -2.4F, 3.6F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(52, 66).addBox(-1.0F, 2.6F, -1.9F, 3.0F, 3.6F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(67, 66).addBox(-3.5F, 6.0F, -6.2F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -3.0F, -13.2F));
        PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(100, 66).addBox(-5.4F, -1.0F, -5.5F, 5.8F, 6.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(129, 66).addBox(-7.8F, 5.0F, -8.4F, 9.6F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, -6.0F, 6.0F));
        PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(170, 66).addBox(-0.4F, -1.0F, -5.5F, 5.8F, 6.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(199, 66).addBox(-1.8F, 5.0F, -8.4F, 9.6F, 1.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, -6.0F, 6.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(FrogzillaRenderState state) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float age = state.ageInTicks;
        float walkPos = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;

        // Respiracion pesada y balanceo de bigotes + sombrero
        float breathe = Mth.sin(age * 0.14F);
        this.body.y += breathe * 0.35F;
        this.topHat.zRot += Mth.sin(age * 0.14F + 0.6F) * 0.04F;
        this.whiskerL.yRot += Mth.sin(age * 0.18F) * 0.10F;
        this.whiskerR.yRot -= Mth.sin(age * 0.18F) * 0.10F;

        // Caminata / salto anfibio
        float hopWave = Mth.sin(walkPos * 0.75F) * walkSpeed;
        float hopLift = Math.abs(Mth.cos(walkPos * 0.75F)) * walkSpeed;
        this.root.y -= hopLift * 2.2F;
        this.body.xRot += hopWave * 0.12F;
        this.leftArm.xRot += hopWave * 0.65F;
        this.rightArm.xRot -= hopWave * 0.65F;
        this.leftLeg.xRot -= hopWave * 0.85F;
        this.rightLeg.xRot += hopWave * 0.85F;

        // Apertura de boca (mordida / lenguetazo / devorar)
        float mouth = Mth.clamp(state.mouthOpen, 0.0F, 1.0F);
        if (state.tongueBlocks > 0.05F) {
            mouth = Math.max(mouth, 0.92F);
        }
        this.head.xRot -= mouth * 0.86F;

        // Lengua 3D sincronizada 1:1 en longitud y elevación con el proyectil del servidor
        // Pivote de lengua en z=+5.5 (~0.60 bloques detrás del centro); longitud base = 15.7/16 * 1.75 = 1.717 bloques
        if (state.tongueBlocks > 0.05F) {
            this.tongue.visible = true;
            this.tongue.xRot = -state.tonguePitch;
            this.tongue.zScale = (state.tongueBlocks + 0.60F) / 1.717F;
        } else {
            this.tongue.visible = false;
        }

        // Panza hinchada mientras tiene un jugador devorado dentro
        if (state.hasSwallowed) {
            float pulse = 1.0F + Mth.sin(age * 0.42F) * 0.08F;
            this.swallowBelly.visible = true;
            this.swallowBelly.xScale = 1.32F * pulse;
            this.swallowBelly.yScale = 1.36F * pulse;
            this.swallowBelly.zScale = 1.28F * pulse;
            this.body.xScale = 1.08F;
            this.body.yScale = 1.06F;
        } else {
            this.swallowBelly.visible = false;
        }
    }
}
