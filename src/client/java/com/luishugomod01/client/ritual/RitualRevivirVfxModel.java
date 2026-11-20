package com.luishugomod01.client.ritual;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

/**
 * Modelo VFX 3D del Ritual de Revivir (exportado de ritual_revivir_vfx.bbmodel en Blockbench 5.2.2):
 * Contiene la Grieta Celestial (rift_root) y los 3 Anillos Giratorios (rings_root).
 */
public class RitualRevivirVfxModel extends Model<RitualRevivirVfxModel.AnimState> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("ruleta_hardcore", "ritual_revivir_vfx"), "main");

    public static final Identifier TEXTURE_CELESTIAL = Identifier.fromNamespaceAndPath(
            "ruleta_hardcore", "textures/entity/ritual/ritual_vfx_celestial.png");

    public enum PartMode {
        RIFT,
        RINGS
    }

    public record AnimState(PartMode mode, long animMs, float extraSpinRad) {}

    private final ModelPart rootPart;
    private final ModelPart riftRoot;
    private final ModelPart riftOuterRing;
    private final ModelPart ringsRoot;

    private final KeyframeAnimation riftOpenAnimation;
    private final KeyframeAnimation ringsSpinAnimation;

    public RitualRevivirVfxModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        this.rootPart = root.getChild("root");
        this.riftRoot = this.rootPart.getChild("rift_root");
        this.riftOuterRing = this.riftRoot.getChild("rift_outer_ring");
        this.ringsRoot = this.rootPart.getChild("rings_root");

        this.riftOpenAnimation = RitualRevivirVfxAnimations.RIFT_OPEN.bake(root);
        this.ringsSpinAnimation = RitualRevivirVfxAnimations.RINGS_SPIN.bake(root);
    }

    @Override
    public void setupAnim(AnimState state) {
        super.setupAnim(state);
        this.resetPose();
        this.riftRoot.visible = (state.mode() == PartMode.RIFT);
        this.ringsRoot.visible = (state.mode() == PartMode.RINGS);

        long ms = Math.max(0L, state.animMs());
        switch (state.mode()) {
            case RIFT -> {
                this.riftOpenAnimation.apply(Math.min(ms, 2500L), 1.0F);
                this.riftOuterRing.yRot += state.extraSpinRad();
            }
            case RINGS -> this.ringsSpinAnimation.apply(ms, 1.0F);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition rift_root = root.addOrReplaceChild("rift_root", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        rift_root.addOrReplaceChild("rift_core", CubeListBuilder.create()
                .texOffs(93, 0).addBox(-7.0F, -1.1F, -17.0F, 14.0F, 2.2F, 34.0F, new CubeDeformation(0.0F))
                .texOffs(134, 47).addBox(-4.0F, -1.1F, -12.0F, 8.0F, 3.0F, 24.0F, new CubeDeformation(0.05F))
                .texOffs(0, 0).addBox(-1.75F, -1.3F, -21.0F, 3.5F, 3.8F, 42.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition rift_jaw_right = rift_root.addOrReplaceChild("rift_jaw_right", CubeListBuilder.create()
                .texOffs(0, 47).addBox(-7.5F, -1.6F, -14.0F, 5.0F, 3.2F, 28.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-4.0F, 0.0F, 0.0F));
        rift_jaw_right.addOrReplaceChild("rift_jaw_r_spike_gold_r1", CubeListBuilder.create().texOffs(105, 157).addBox(-3.0F, -1.1F, -3.0F, 6.0F, 2.2F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, -0.4F, 0.0F, 0.0F, 0.0F, 0.3142F));
        rift_jaw_right.addOrReplaceChild("rift_jaw_r_shard2_glow_r1", CubeListBuilder.create().texOffs(0, 80).addBox(-2.0F, -1.2F, -6.0F, 4.0F, 2.4F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 0.5F, -8.0F, 0.0F, 0.384F, 0.2094F));
        rift_jaw_right.addOrReplaceChild("rift_jaw_r_shard1_glow_r1", CubeListBuilder.create().texOffs(199, 47).addBox(-2.0F, -1.2F, -6.0F, 4.0F, 2.4F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 0.5F, 8.0F, 0.0F, -0.384F, 0.2094F));

        PartDefinition rift_jaw_left = rift_root.addOrReplaceChild("rift_jaw_left", CubeListBuilder.create()
                .texOffs(67, 47).addBox(2.5F, -1.6F, -14.0F, 5.0F, 3.2F, 28.0F, new CubeDeformation(0.0F)),
                PartPose.offset(4.0F, 0.0F, 0.0F));
        rift_jaw_left.addOrReplaceChild("rift_jaw_l_spike_gold_r1", CubeListBuilder.create().texOffs(130, 157).addBox(-3.0F, -1.1F, -3.0F, 6.0F, 2.2F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, -0.4F, 0.0F, 0.0F, 0.0F, -0.3142F));
        rift_jaw_left.addOrReplaceChild("rift_jaw_l_shard2_glow_r1", CubeListBuilder.create().texOffs(66, 80).addBox(-2.0F, -1.2F, -6.0F, 4.0F, 2.4F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 0.5F, -8.0F, 0.0F, -0.384F, -0.2094F));
        rift_jaw_left.addOrReplaceChild("rift_jaw_l_shard1_glow_r1", CubeListBuilder.create().texOffs(33, 80).addBox(-2.0F, -1.2F, -6.0F, 4.0F, 2.4F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 0.5F, 8.0F, 0.0F, 0.384F, -0.2094F));

        PartDefinition rift_outer_ring = rift_root.addOrReplaceChild("rift_outer_ring", CubeListBuilder.create()
                .texOffs(116, 96).addBox(-26.0F, -1.0F, -4.63F, 4.0F, 2.0F, 9.26F, new CubeDeformation(0.0F))
                .texOffs(239, 176).addBox(-30.25F, -1.2F, -1.4F, 4.5F, 2.4F, 2.8F, new CubeDeformation(0.0F))
                .texOffs(0, 185).addBox(25.75F, -1.2F, -1.4F, 4.5F, 2.4F, 2.8F, new CubeDeformation(0.0F))
                .texOffs(153, 176).addBox(-1.4F, -1.2F, 25.75F, 2.8F, 2.4F, 4.5F, new CubeDeformation(0.0F))
                .texOffs(170, 176).addBox(-1.4F, -1.2F, -30.25F, 2.8F, 2.4F, 4.5F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_18_r1", CubeListBuilder.create().texOffs(87, 96).addBox(-26.0F, -1.02F, -4.63F, 4.0F, 2.04F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.3491F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_17_r1", CubeListBuilder.create().texOffs(116, 110).addBox(-26.0F, -1.0F, -4.63F, 4.0F, 2.0F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.6981F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_16_r1", CubeListBuilder.create().texOffs(58, 96).addBox(-26.0F, -1.02F, -4.63F, 4.0F, 2.04F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_15_r1", CubeListBuilder.create().texOffs(87, 110).addBox(-26.0F, -1.0F, -4.63F, 4.0F, 2.0F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.3963F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_14_r1", CubeListBuilder.create().texOffs(29, 96).addBox(-26.0F, -1.02F, -4.63F, 4.0F, 2.04F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.7453F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_13_r1", CubeListBuilder.create().texOffs(58, 110).addBox(-26.0F, -1.0F, -4.63F, 4.0F, 2.0F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_12_r1", CubeListBuilder.create().texOffs(0, 96).addBox(-26.0F, -1.02F, -4.63F, 4.0F, 2.04F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.4435F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_11_r1", CubeListBuilder.create().texOffs(29, 110).addBox(-26.0F, -1.0F, -4.63F, 4.0F, 2.0F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.7925F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_10_r1", CubeListBuilder.create().texOffs(215, 80).addBox(-26.0F, -1.02F, -4.63F, 4.0F, 2.04F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_9_r1", CubeListBuilder.create().texOffs(0, 110).addBox(-26.0F, -1.0F, -4.63F, 4.0F, 2.0F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.7925F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_8_r1", CubeListBuilder.create().texOffs(186, 80).addBox(-26.0F, -1.02F, -4.63F, 4.0F, 2.04F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.4435F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_7_r1", CubeListBuilder.create().texOffs(203, 96).addBox(-26.0F, -1.0F, -4.63F, 4.0F, 2.0F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_6_r1", CubeListBuilder.create().texOffs(157, 80).addBox(-26.0F, -1.02F, -4.63F, 4.0F, 2.04F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.7453F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_5_r1", CubeListBuilder.create().texOffs(174, 96).addBox(-26.0F, -1.0F, -4.63F, 4.0F, 2.0F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.3963F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_4_r1", CubeListBuilder.create().texOffs(128, 80).addBox(-26.0F, -1.02F, -4.63F, 4.0F, 2.04F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_3_r1", CubeListBuilder.create().texOffs(145, 96).addBox(-26.0F, -1.0F, -4.63F, 4.0F, 2.0F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.6981F, 0.0F));
        rift_outer_ring.addOrReplaceChild("rift_ring_gold_2_r1", CubeListBuilder.create().texOffs(99, 80).addBox(-26.0F, -1.02F, -4.63F, 4.0F, 2.04F, 9.26F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.3491F, 0.0F));

        PartDefinition rings_root = root.addOrReplaceChild("rings_root", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition ring_outer = rings_root.addOrReplaceChild("ring_outer", CubeListBuilder.create()
                .texOffs(145, 110).addBox(-25.5F, -0.9F, -4.079F, 3.0F, 1.8F, 8.158F, new CubeDeformation(0.05F))
                .texOffs(187, 176).addBox(-25.1F, -4.75F, -1.1F, 2.2F, 4.5F, 2.2F, new CubeDeformation(0.0F))
                .texOffs(200, 176).addBox(22.9F, -4.75F, -1.1F, 2.2F, 4.5F, 2.2F, new CubeDeformation(0.0F))
                .texOffs(213, 176).addBox(-1.1F, -4.75F, 22.9F, 2.2F, 4.5F, 2.2F, new CubeDeformation(0.0F))
                .texOffs(226, 176).addBox(-1.1F, -4.75F, -25.1F, 2.2F, 4.5F, 2.2F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -4.0F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_20_r1", CubeListBuilder.create().texOffs(125, 135).addBox(-25.5F, -0.92F, -4.079F, 3.0F, 1.84F, 8.158F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.3142F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_19_r1", CubeListBuilder.create().texOffs(100, 135).addBox(-25.5F, -0.9F, -4.079F, 3.0F, 1.8F, 8.158F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.6283F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_18_r1", CubeListBuilder.create().texOffs(75, 135).addBox(-25.5F, -0.92F, -4.079F, 3.0F, 1.84F, 8.158F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.9425F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_17_r1", CubeListBuilder.create().texOffs(50, 135).addBox(-25.5F, -0.9F, -4.079F, 3.0F, 1.8F, 8.158F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.2566F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_16_r1", CubeListBuilder.create().texOffs(25, 135).addBox(-25.5F, -0.92F, -4.079F, 3.0F, 1.84F, 8.158F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_15_r1", CubeListBuilder.create().texOffs(0, 135).addBox(-25.5F, -0.9F, -4.079F, 3.0F, 1.8F, 8.158F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.885F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_14_r1", CubeListBuilder.create().texOffs(225, 123).addBox(-25.5F, -0.92F, -4.079F, 3.0F, 1.84F, 8.158F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.1991F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_13_r1", CubeListBuilder.create().texOffs(200, 123).addBox(-25.5F, -0.9F, -4.079F, 3.0F, 1.8F, 8.158F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.5133F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_12_r1", CubeListBuilder.create().texOffs(175, 123).addBox(-25.5F, -0.92F, -4.079F, 3.0F, 1.84F, 8.158F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.8274F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_11_r1", CubeListBuilder.create().texOffs(150, 123).addBox(-25.5F, -0.9F, -4.079F, 3.0F, 1.8F, 8.158F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_10_r1", CubeListBuilder.create().texOffs(125, 123).addBox(-25.5F, -0.92F, -4.079F, 3.0F, 1.84F, 8.158F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.8274F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_9_r1", CubeListBuilder.create().texOffs(100, 123).addBox(-25.5F, -0.9F, -4.079F, 3.0F, 1.8F, 8.158F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.5133F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_8_r1", CubeListBuilder.create().texOffs(75, 123).addBox(-25.5F, -0.92F, -4.079F, 3.0F, 1.84F, 8.158F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.1991F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_7_r1", CubeListBuilder.create().texOffs(50, 123).addBox(-25.5F, -0.9F, -4.079F, 3.0F, 1.8F, 8.158F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.885F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_6_r1", CubeListBuilder.create().texOffs(25, 123).addBox(-25.5F, -0.92F, -4.079F, 3.0F, 1.84F, 8.158F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_5_r1", CubeListBuilder.create().texOffs(0, 123).addBox(-25.5F, -0.9F, -4.079F, 3.0F, 1.8F, 8.158F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.2566F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_4_r1", CubeListBuilder.create().texOffs(220, 110).addBox(-25.5F, -0.92F, -4.079F, 3.0F, 1.84F, 8.158F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.9425F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_3_r1", CubeListBuilder.create().texOffs(195, 110).addBox(-25.5F, -0.9F, -4.079F, 3.0F, 1.8F, 8.158F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.6283F, 0.0F));
        ring_outer.addOrReplaceChild("ritual_ring_outer_gold_2_r1", CubeListBuilder.create().texOffs(170, 110).addBox(-25.5F, -0.92F, -4.079F, 3.0F, 1.84F, 8.158F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.3142F, 0.0F));

        PartDefinition ring_mid = rings_root.addOrReplaceChild("ring_mid", CubeListBuilder.create()
                .texOffs(234, 135).addBox(-18.5F, -0.8F, -3.295F, 2.5F, 1.6F, 6.59F, new CubeDeformation(0.05F)),
                PartPose.offset(0.0F, -14.0F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_18_r1", CubeListBuilder.create().texOffs(84, 157).addBox(-18.5F, -0.82F, -3.295F, 2.5F, 1.64F, 6.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.3491F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_17_r1", CubeListBuilder.create().texOffs(63, 157).addBox(-18.5F, -0.8F, -3.295F, 2.5F, 1.6F, 6.59F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.6981F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_16_r1", CubeListBuilder.create().texOffs(42, 157).addBox(-18.5F, -0.82F, -3.295F, 2.5F, 1.64F, 6.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.0472F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_15_r1", CubeListBuilder.create().texOffs(21, 157).addBox(-18.5F, -0.8F, -3.295F, 2.5F, 1.6F, 6.59F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.3963F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_14_r1", CubeListBuilder.create().texOffs(0, 157).addBox(-18.5F, -0.82F, -3.295F, 2.5F, 1.64F, 6.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.7453F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_13_r1", CubeListBuilder.create().texOffs(231, 147).addBox(-18.5F, -0.8F, -3.295F, 2.5F, 1.6F, 6.59F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.0944F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_12_r1", CubeListBuilder.create().texOffs(210, 147).addBox(-18.5F, -0.82F, -3.295F, 2.5F, 1.64F, 6.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.4435F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_11_r1", CubeListBuilder.create().texOffs(189, 147).addBox(-18.5F, -0.8F, -3.295F, 2.5F, 1.6F, 6.59F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.7925F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_10_r1", CubeListBuilder.create().texOffs(168, 147).addBox(-18.5F, -0.82F, -3.295F, 2.5F, 1.64F, 6.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_9_r1", CubeListBuilder.create().texOffs(147, 147).addBox(-18.5F, -0.8F, -3.295F, 2.5F, 1.6F, 6.59F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.7925F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_8_r1", CubeListBuilder.create().texOffs(126, 147).addBox(-18.5F, -0.82F, -3.295F, 2.5F, 1.64F, 6.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.4435F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_7_r1", CubeListBuilder.create().texOffs(105, 147).addBox(-18.5F, -0.8F, -3.295F, 2.5F, 1.6F, 6.59F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.0944F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_6_r1", CubeListBuilder.create().texOffs(84, 147).addBox(-18.5F, -0.82F, -3.295F, 2.5F, 1.64F, 6.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.7453F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_5_r1", CubeListBuilder.create().texOffs(63, 147).addBox(-18.5F, -0.8F, -3.295F, 2.5F, 1.6F, 6.59F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.3963F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_4_r1", CubeListBuilder.create().texOffs(42, 147).addBox(-18.5F, -0.82F, -3.295F, 2.5F, 1.64F, 6.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.0472F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_3_r1", CubeListBuilder.create().texOffs(21, 147).addBox(-18.5F, -0.8F, -3.295F, 2.5F, 1.6F, 6.59F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.6981F, 0.0F));
        ring_mid.addOrReplaceChild("ritual_ring_mid_glow_2_r1", CubeListBuilder.create().texOffs(0, 147).addBox(-18.5F, -0.82F, -3.295F, 2.5F, 1.64F, 6.59F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.3491F, 0.0F));

        PartDefinition ring_inner = rings_root.addOrReplaceChild("ring_inner", CubeListBuilder.create()
                .texOffs(169, 167).addBox(-12.5F, -0.7F, -2.882F, 2.0F, 1.4F, 5.764F, new CubeDeformation(0.05F)),
                PartPose.offset(0.0F, -24.0F, 0.0F));
        ring_inner.addOrReplaceChild("ritual_ring_inner_gold_14_r1", CubeListBuilder.create().texOffs(136, 176).addBox(-12.5F, -0.72F, -2.882F, 2.0F, 1.44F, 5.764F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.4488F, 0.0F));
        ring_inner.addOrReplaceChild("ritual_ring_inner_gold_13_r1", CubeListBuilder.create().texOffs(119, 176).addBox(-12.5F, -0.7F, -2.882F, 2.0F, 1.4F, 5.764F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.8976F, 0.0F));
        ring_inner.addOrReplaceChild("ritual_ring_inner_gold_12_r1", CubeListBuilder.create().texOffs(102, 176).addBox(-12.5F, -0.72F, -2.882F, 2.0F, 1.44F, 5.764F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.3464F, 0.0F));
        ring_inner.addOrReplaceChild("ritual_ring_inner_gold_11_r1", CubeListBuilder.create().texOffs(85, 176).addBox(-12.5F, -0.7F, -2.882F, 2.0F, 1.4F, 5.764F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.7952F, 0.0F));
        ring_inner.addOrReplaceChild("ritual_ring_inner_gold_10_r1", CubeListBuilder.create().texOffs(68, 176).addBox(-12.5F, -0.72F, -2.882F, 2.0F, 1.44F, 5.764F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.244F, 0.0F));
        ring_inner.addOrReplaceChild("ritual_ring_inner_gold_9_r1", CubeListBuilder.create().texOffs(51, 176).addBox(-12.5F, -0.7F, -2.882F, 2.0F, 1.4F, 5.764F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.6928F, 0.0F));
        ring_inner.addOrReplaceChild("ritual_ring_inner_gold_8_r1", CubeListBuilder.create().texOffs(34, 176).addBox(-12.5F, -0.72F, -2.882F, 2.0F, 1.44F, 5.764F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
        ring_inner.addOrReplaceChild("ritual_ring_inner_gold_7_r1", CubeListBuilder.create().texOffs(17, 176).addBox(-12.5F, -0.7F, -2.882F, 2.0F, 1.4F, 5.764F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.6928F, 0.0F));
        ring_inner.addOrReplaceChild("ritual_ring_inner_gold_6_r1", CubeListBuilder.create().texOffs(0, 176).addBox(-12.5F, -0.72F, -2.882F, 2.0F, 1.44F, 5.764F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.244F, 0.0F));
        ring_inner.addOrReplaceChild("ritual_ring_inner_gold_5_r1", CubeListBuilder.create().texOffs(237, 167).addBox(-12.5F, -0.7F, -2.882F, 2.0F, 1.4F, 5.764F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.7952F, 0.0F));
        ring_inner.addOrReplaceChild("ritual_ring_inner_gold_4_r1", CubeListBuilder.create().texOffs(220, 167).addBox(-12.5F, -0.72F, -2.882F, 2.0F, 1.44F, 5.764F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.3464F, 0.0F));
        ring_inner.addOrReplaceChild("ritual_ring_inner_gold_3_r1", CubeListBuilder.create().texOffs(203, 167).addBox(-12.5F, -0.7F, -2.882F, 2.0F, 1.4F, 5.764F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.8976F, 0.0F));
        ring_inner.addOrReplaceChild("ritual_ring_inner_gold_2_r1", CubeListBuilder.create().texOffs(186, 167).addBox(-12.5F, -0.72F, -2.882F, 2.0F, 1.44F, 5.764F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.4488F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }
}
