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
 * Modelo 3D de las 4 Alas Celestiales articuladas y Halo giratorio (geometría de angel_revivir.bbmodel)
 * adaptados a las proporciones exactas del jugador (hombros en Y=0, espalda en Z=+2, cabeza en Y=-8)
 * para acoplarse directamente sobre la skin real del jugador que está reviviendo.
 */
public class AngelRevivirModel extends Model<AngelRevivirModel.AnimState> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("ruleta_hardcore", "angel_revivir"), "main");

    public static final Identifier TEXTURE_CELESTIAL = Identifier.fromNamespaceAndPath(
            "ruleta_hardcore", "textures/entity/ritual/angel_celestial.png");

    private final KeyframeAnimation descendAnimation;
    private final KeyframeAnimation brakeAnimation;

    public record AnimState(long descendMs, long brakeMs, boolean braking) {}

    public AngelRevivirModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        this.descendAnimation = AngelRevivirAnimations.DESCEND.bake(root);
        this.brakeAnimation = AngelRevivirAnimations.LANDING_BRAKE.bake(root);
    }

    @Override
    public void setupAnim(AnimState state) {
        super.setupAnim(state);
        this.resetPose();
        if (state.braking()) {
            this.brakeAnimation.apply(Math.min(2800L, Math.max(0L, state.brakeMs())), 1.0F);
        } else {
            this.descendAnimation.apply(Math.max(0L, state.descendMs()), 1.0F);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Pivote al nivel del suelo (Y=24px = 1.5 bloques), igual que PlayerModel
        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        // body en Y=-24px -> Y=0 absoluto en espacio de PlayerModel (hombros/cuello del jugador)
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -24.0F, 0.0F));

        // Halo dorado giratorio flotando sobre la cabeza del jugador (cabeza termina en Y=-8px)
        PartDefinition halo = body.addOrReplaceChild("halo", CubeListBuilder.create()
                .texOffs(142, 62).addBox(-6.8F, -0.6F, -1.366F, 1.6F, 1.2F, 2.732F, new CubeDeformation(0.05F))
                .texOffs(107, 71).addBox(-0.55F, -0.55F, -9.4F, 1.1F, 1.1F, 2.6F, new CubeDeformation(0.0F))
                .texOffs(118, 71).addBox(-0.55F, -0.55F, 6.8F, 1.1F, 1.1F, 2.6F, new CubeDeformation(0.0F))
                .texOffs(152, 71).addBox(-9.4F, -0.55F, -0.55F, 2.6F, 1.1F, 1.1F, new CubeDeformation(0.0F))
                .texOffs(163, 71).addBox(6.8F, -0.55F, -0.55F, 2.6F, 1.1F, 1.1F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -11.8F, 0.2F));

        halo.addOrReplaceChild("halo_spike_diag_b_glow_left_r1", CubeListBuilder.create().texOffs(149, 77).addBox(-1.0F, -0.45F, -0.45F, 2.0F, 0.9F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.6F, 0.0F, 5.6F, 0.0F, -0.7854F, 0.0F));
        halo.addOrReplaceChild("halo_spike_diag_b_glow_right_r1", CubeListBuilder.create().texOffs(142, 77).addBox(-1.0F, -0.45F, -0.45F, 2.0F, 0.9F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.6F, 0.0F, 5.6F, 0.0F, 0.7854F, 0.0F));
        halo.addOrReplaceChild("halo_spike_diag_f_glow_left_r1", CubeListBuilder.create().texOffs(135, 77).addBox(-1.0F, -0.45F, -0.45F, 2.0F, 0.9F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.6F, 0.0F, -5.6F, 0.0F, 0.7854F, 0.0F));
        halo.addOrReplaceChild("halo_spike_diag_f_glow_right_r1", CubeListBuilder.create().texOffs(128, 77).addBox(-1.0F, -0.45F, -0.45F, 2.0F, 0.9F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.6F, 0.0F, -5.6F, 0.0F, -0.7854F, 0.0F));

        halo.addOrReplaceChild("halo_ring_glow_16_r1", CubeListBuilder.create().texOffs(55, 71).addBox(-6.8F, -0.62F, -1.366F, 1.6F, 1.24F, 2.732F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.3927F, 0.0F));
        halo.addOrReplaceChild("halo_ring_glow_15_r1", CubeListBuilder.create().texOffs(44, 71).addBox(-6.8F, -0.6F, -1.366F, 1.6F, 1.2F, 2.732F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
        halo.addOrReplaceChild("halo_ring_glow_14_r1", CubeListBuilder.create().texOffs(33, 71).addBox(-6.8F, -0.62F, -1.366F, 1.6F, 1.24F, 2.732F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.1781F, 0.0F));
        halo.addOrReplaceChild("halo_ring_glow_13_r1", CubeListBuilder.create().texOffs(22, 71).addBox(-6.8F, -0.6F, -1.366F, 1.6F, 1.2F, 2.732F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
        halo.addOrReplaceChild("halo_ring_glow_12_r1", CubeListBuilder.create().texOffs(11, 71).addBox(-6.8F, -0.62F, -1.366F, 1.6F, 1.24F, 2.732F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.9635F, 0.0F));
        halo.addOrReplaceChild("halo_ring_glow_11_r1", CubeListBuilder.create().texOffs(0, 71).addBox(-6.8F, -0.6F, -1.366F, 1.6F, 1.2F, 2.732F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.3562F, 0.0F));
        halo.addOrReplaceChild("halo_ring_glow_10_r1", CubeListBuilder.create().texOffs(241, 62).addBox(-6.8F, -0.62F, -1.366F, 1.6F, 1.24F, 2.732F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -2.7489F, 0.0F));
        halo.addOrReplaceChild("halo_ring_glow_9_r1", CubeListBuilder.create().texOffs(230, 62).addBox(-6.8F, -0.6F, -1.366F, 1.6F, 1.2F, 2.732F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F));
        halo.addOrReplaceChild("halo_ring_glow_8_r1", CubeListBuilder.create().texOffs(219, 62).addBox(-6.8F, -0.62F, -1.366F, 1.6F, 1.24F, 2.732F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.7489F, 0.0F));
        halo.addOrReplaceChild("halo_ring_glow_7_r1", CubeListBuilder.create().texOffs(208, 62).addBox(-6.8F, -0.6F, -1.366F, 1.6F, 1.2F, 2.732F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 2.3562F, 0.0F));
        halo.addOrReplaceChild("halo_ring_glow_6_r1", CubeListBuilder.create().texOffs(197, 62).addBox(-6.8F, -0.62F, -1.366F, 1.6F, 1.24F, 2.732F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.9635F, 0.0F));
        halo.addOrReplaceChild("halo_ring_glow_5_r1", CubeListBuilder.create().texOffs(186, 62).addBox(-6.8F, -0.6F, -1.366F, 1.6F, 1.2F, 2.732F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));
        halo.addOrReplaceChild("halo_ring_glow_4_r1", CubeListBuilder.create().texOffs(175, 62).addBox(-6.8F, -0.62F, -1.366F, 1.6F, 1.24F, 2.732F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.1781F, 0.0F));
        halo.addOrReplaceChild("halo_ring_glow_3_r1", CubeListBuilder.create().texOffs(164, 62).addBox(-6.8F, -0.6F, -1.366F, 1.6F, 1.2F, 2.732F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
        halo.addOrReplaceChild("halo_ring_glow_2_r1", CubeListBuilder.create().texOffs(153, 62).addBox(-6.8F, -0.62F, -1.366F, 1.6F, 1.24F, 2.732F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.3927F, 0.0F));

        // Alas superiores acopladas a los omóplatos del jugador (Z=+2.2 detrás del torso)
        PartDefinition right_wing_upper = body.addOrReplaceChild("right_wing_upper", CubeListBuilder.create(), PartPose.offset(-2.0F, 1.8F, 2.2F));
        right_wing_upper.addOrReplaceChild("wing_upper_feath_3_right_r1", CubeListBuilder.create().texOffs(148, 20).addBox(-2.1F, -6.4F, -0.6F, 4.2F, 12.8F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.1F, 0.8F, 1.7F, 0.0F, 0.1396F, 0.1745F));
        right_wing_upper.addOrReplaceChild("wing_upper_feath_2_right_r1", CubeListBuilder.create().texOffs(178, 20).addBox(-2.2F, -5.75F, -0.6F, 4.4F, 11.5F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.7F, 1.2F, 1.4F, 0.0F, 0.1396F, 0.1396F));
        right_wing_upper.addOrReplaceChild("wing_upper_feath_1_right_r1", CubeListBuilder.create().texOffs(230, 20).addBox(-2.1F, -4.75F, -0.6F, 4.2F, 9.5F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 2.0F, 1.0F, 0.0F, 0.1396F, 0.1047F));
        right_wing_upper.addOrReplaceChild("wing_upper_spar_gold_right_r1", CubeListBuilder.create().texOffs(76, 62).addBox(-6.5F, -1.1F, -1.1F, 13.0F, 2.2F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -1.5F, 0.8F, 0.0F, 0.1396F, 0.2443F));

        PartDefinition right_wing_upper_mid = right_wing_upper.addOrReplaceChild("right_wing_upper_mid", CubeListBuilder.create(), PartPose.offset(-12.0F, -3.0F, 1.6F));
        right_wing_upper_mid.addOrReplaceChild("wing_mid_feath_3_right_r1", CubeListBuilder.create().texOffs(211, 0).addBox(-2.1F, -7.7F, -0.55F, 4.2F, 15.4F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.1F, 4.6F, 1.1F, 0.0F, 0.1047F, 0.0349F));
        right_wing_upper_mid.addOrReplaceChild("wing_mid_feath_2_right_r1", CubeListBuilder.create().texOffs(148, 0).addBox(-2.2F, -8.1F, -0.55F, 4.4F, 16.2F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.5F, 4.8F, 0.8F, 0.0F, 0.1047F, 0.0698F));
        right_wing_upper_mid.addOrReplaceChild("wing_mid_feath_1_right_r1", CubeListBuilder.create().texOffs(35, 20).addBox(-2.1F, -7.25F, -0.55F, 4.2F, 14.5F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.7F, 4.5F, 0.5F, 0.0F, 0.1047F, 0.1047F));
        right_wing_upper_mid.addOrReplaceChild("wing_mid_spar_gold_right_r1", CubeListBuilder.create().texOffs(174, 71).addBox(-6.25F, -1.0F, -1.0F, 12.5F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.7F, -0.3F, 0.6F, 0.0F, 0.1047F, 0.0698F));

        PartDefinition right_wing_upper_tip = right_wing_upper_mid.addOrReplaceChild("right_wing_upper_tip", CubeListBuilder.create(), PartPose.offset(-11.5F, -0.5F, 1.2F));
        right_wing_upper_tip.addOrReplaceChild("wing_tip_feath_4_glow_right_r1", CubeListBuilder.create().texOffs(244, 51).addBox(-1.6F, -3.4F, -0.425F, 3.2F, 6.8F, 0.85F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-12.2F, 0.9F, 0.8F, 0.0F, 0.0698F, -0.3491F));
        right_wing_upper_tip.addOrReplaceChild("wing_tip_feath_3_glow_right_r1", CubeListBuilder.create().texOffs(155, 38).addBox(-1.9F, -4.9F, -0.45F, 3.8F, 9.8F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.2F, 2.0F, 0.6F, 0.0F, 0.0698F, -0.2443F));
        right_wing_upper_tip.addOrReplaceChild("wing_tip_feath_2_glow_right_r1", CubeListBuilder.create().texOffs(193, 20).addBox(-2.0F, -6.25F, -0.475F, 4.0F, 12.5F, 0.95F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-5.8F, 3.1F, 0.4F, 0.0F, 0.0698F, -0.1396F));
        right_wing_upper_tip.addOrReplaceChild("wing_tip_feath_1_glow_right_r1", CubeListBuilder.create().texOffs(111, 20).addBox(-2.1F, -7.25F, -0.5F, 4.2F, 14.5F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, 4.0F, 0.2F, 0.0F, 0.0698F, -0.0698F));
        right_wing_upper_tip.addOrReplaceChild("wing_tip_spar_gold_right_r1", CubeListBuilder.create().texOffs(205, 71).addBox(-5.5F, -0.8F, -0.8F, 11.0F, 1.6F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.2F, 0.3F, 0.4F, 0.0F, 0.0698F, -0.1047F));

        PartDefinition left_wing_upper = body.addOrReplaceChild("left_wing_upper", CubeListBuilder.create(), PartPose.offset(2.0F, 1.8F, 2.2F));
        left_wing_upper.addOrReplaceChild("wing_upper_feath_3_left_r1", CubeListBuilder.create().texOffs(163, 20).addBox(-2.1F, -6.4F, -0.6F, 4.2F, 12.8F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.1F, 0.8F, 1.7F, 0.0F, -0.1396F, -0.1745F));
        left_wing_upper.addOrReplaceChild("wing_upper_feath_2_left_r1", CubeListBuilder.create().texOffs(204, 20).addBox(-2.2F, -5.75F, -0.6F, 4.4F, 11.5F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.7F, 1.2F, 1.4F, 0.0F, -0.1396F, -0.1396F));
        left_wing_upper.addOrReplaceChild("wing_upper_feath_1_left_r1", CubeListBuilder.create().texOffs(26, 38).addBox(-2.1F, -4.75F, -0.6F, 4.2F, 9.5F, 1.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 2.0F, 1.0F, 0.0F, -0.1396F, -0.1047F));
        left_wing_upper.addOrReplaceChild("wing_upper_spar_gold_left_r1", CubeListBuilder.create().texOffs(109, 62).addBox(-6.5F, -1.1F, -1.1F, 13.0F, 2.2F, 2.2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -1.5F, 0.8F, 0.0F, -0.1396F, -0.2443F));

        PartDefinition left_wing_upper_mid = left_wing_upper.addOrReplaceChild("left_wing_upper_mid", CubeListBuilder.create(), PartPose.offset(12.0F, -3.0F, 1.6F));
        left_wing_upper_mid.addOrReplaceChild("wing_mid_feath_3_left_r1", CubeListBuilder.create().texOffs(226, 0).addBox(-2.1F, -7.7F, -0.55F, 4.2F, 15.4F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.1F, 4.6F, 1.1F, 0.0F, -0.1047F, -0.0349F));
        left_wing_upper_mid.addOrReplaceChild("wing_mid_feath_2_left_r1", CubeListBuilder.create().texOffs(163, 0).addBox(-2.2F, -8.1F, -0.55F, 4.4F, 16.2F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.5F, 4.8F, 0.8F, 0.0F, -0.1047F, -0.0698F));
        left_wing_upper_mid.addOrReplaceChild("wing_mid_feath_1_left_r1", CubeListBuilder.create().texOffs(50, 20).addBox(-2.1F, -7.25F, -0.55F, 4.2F, 14.5F, 1.1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.7F, 4.5F, 0.5F, 0.0F, -0.1047F, -0.1047F));
        left_wing_upper_mid.addOrReplaceChild("wing_mid_spar_gold_left_r1", CubeListBuilder.create().texOffs(27, 77).addBox(-6.25F, -1.0F, -1.0F, 12.5F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.7F, -0.3F, 0.6F, 0.0F, -0.1047F, -0.0698F));

        PartDefinition left_wing_upper_tip = left_wing_upper_mid.addOrReplaceChild("left_wing_upper_tip", CubeListBuilder.create(), PartPose.offset(11.5F, -0.5F, 1.2F));
        left_wing_upper_tip.addOrReplaceChild("wing_tip_feath_4_glow_left_r1", CubeListBuilder.create().texOffs(11, 62).addBox(-1.6F, -3.4F, -0.425F, 3.2F, 6.8F, 0.85F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(12.2F, 0.9F, 0.8F, 0.0F, -0.0698F, 0.3491F));
        left_wing_upper_tip.addOrReplaceChild("wing_tip_feath_3_glow_left_r1", CubeListBuilder.create().texOffs(166, 38).addBox(-1.9F, -4.9F, -0.45F, 3.8F, 9.8F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.2F, 2.0F, 0.6F, 0.0F, -0.0698F, 0.2443F));
        left_wing_upper_tip.addOrReplaceChild("wing_tip_feath_2_glow_left_r1", CubeListBuilder.create().texOffs(219, 20).addBox(-2.0F, -6.25F, -0.475F, 4.0F, 12.5F, 0.95F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(5.8F, 3.1F, 0.4F, 0.0F, -0.0698F, 0.1396F));
        left_wing_upper_tip.addOrReplaceChild("wing_tip_feath_1_glow_left_r1", CubeListBuilder.create().texOffs(124, 20).addBox(-2.1F, -7.25F, -0.5F, 4.2F, 14.5F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.2F, 4.0F, 0.2F, 0.0F, -0.0698F, 0.0698F));
        left_wing_upper_tip.addOrReplaceChild("wing_tip_spar_gold_left_r1", CubeListBuilder.create().texOffs(58, 77).addBox(-5.5F, -0.8F, -0.8F, 11.0F, 1.6F, 1.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.2F, 0.3F, 0.4F, 0.0F, -0.0698F, 0.1047F));

        // Alas inferiores secundarias acopladas en la zona dorsal baja
        PartDefinition right_wing_lower = body.addOrReplaceChild("right_wing_lower", CubeListBuilder.create(), PartPose.offsetAndRotation(-1.6F, 6.2F, 2.5F, 0.0F, 0.2793F, 0.0F));
        right_wing_lower.addOrReplaceChild("wing_lower_feath_2_right_r1", CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, -5.1F, -0.5F, 4.0F, 10.2F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.8F, 5.1F, 1.2F, 0.0F, 0.1745F, -0.2094F));
        right_wing_lower.addOrReplaceChild("wing_lower_feath_1_right_r1", CubeListBuilder.create().texOffs(99, 51).addBox(-1.9F, -4.4F, -0.5F, 3.8F, 8.8F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.2F, 4.3F, 0.9F, 0.0F, 0.1745F, -0.1745F));
        right_wing_lower.addOrReplaceChild("wing_lower_spar_gold_right_r1", CubeListBuilder.create().texOffs(0, 77).addBox(-5.25F, -0.8F, -0.8F, 10.5F, 1.6F, 1.6F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-4.8F, 1.3F, 0.7F, 0.0F, 0.1745F, -0.2443F));

        PartDefinition right_wing_lower_tip = right_wing_lower.addOrReplaceChild("right_wing_lower_tip", CubeListBuilder.create(), PartPose.offset(-9.5F, 2.5F, 1.3F));
        right_wing_lower_tip.addOrReplaceChild("wing_lower_tip_3_glow_right_r1", CubeListBuilder.create().texOffs(0, 62).addBox(-1.6F, -3.1F, -0.425F, 3.2F, 6.2F, 0.85F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-9.7F, 1.6F, 0.9F, 0.0F, 0.1396F, -0.5236F));
        right_wing_lower_tip.addOrReplaceChild("wing_lower_tip_2_glow_right_r1", CubeListBuilder.create().texOffs(110, 51).addBox(-1.9F, -4.25F, -0.45F, 3.8F, 8.5F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.7F, 1.8F, 0.7F, 0.0F, 0.1396F, -0.4189F));
        right_wing_lower_tip.addOrReplaceChild("wing_lower_tip_1_glow_right_r1", CubeListBuilder.create().texOffs(13, 38).addBox(-2.1F, -5.25F, -0.475F, 4.2F, 10.5F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.3F, 1.4F, 0.4F, 0.0F, 0.1396F, -0.3142F));

        PartDefinition left_wing_lower = body.addOrReplaceChild("left_wing_lower", CubeListBuilder.create(), PartPose.offsetAndRotation(1.6F, 6.2F, 2.5F, 0.0F, -0.2793F, 0.0F));
        left_wing_lower.addOrReplaceChild("wing_lower_feath_2_left_r1", CubeListBuilder.create().texOffs(41, 38).addBox(-2.0F, -5.1F, -0.5F, 4.0F, 10.2F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.8F, 5.1F, 1.2F, 0.0F, -0.1745F, 0.2094F));
        left_wing_lower.addOrReplaceChild("wing_lower_feath_1_left_r1", CubeListBuilder.create().texOffs(121, 51).addBox(-1.9F, -4.4F, -0.5F, 3.8F, 8.8F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.2F, 4.3F, 0.9F, 0.0F, -0.1745F, 0.1745F));
        left_wing_lower.addOrReplaceChild("wing_lower_spar_gold_left_r1", CubeListBuilder.create().texOffs(87, 77).addBox(-5.25F, -0.8F, -0.8F, 10.5F, 1.6F, 1.6F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(4.8F, 1.3F, 0.7F, 0.0F, -0.1745F, 0.2443F));

        PartDefinition left_wing_lower_tip = left_wing_lower.addOrReplaceChild("left_wing_lower_tip", CubeListBuilder.create(), PartPose.offset(9.5F, 2.5F, 1.3F));
        left_wing_lower_tip.addOrReplaceChild("wing_lower_tip_3_glow_left_r1", CubeListBuilder.create().texOffs(22, 62).addBox(-1.6F, -3.1F, -0.425F, 3.2F, 6.2F, 0.85F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.7F, 1.6F, 0.9F, 0.0F, -0.1396F, 0.5236F));
        left_wing_lower_tip.addOrReplaceChild("wing_lower_tip_2_glow_left_r1", CubeListBuilder.create().texOffs(132, 51).addBox(-1.9F, -4.25F, -0.45F, 3.8F, 8.5F, 0.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.7F, 1.8F, 0.7F, 0.0F, -0.1396F, 0.4189F));
        left_wing_lower_tip.addOrReplaceChild("wing_lower_tip_1_glow_left_r1", CubeListBuilder.create().texOffs(54, 38).addBox(-2.1F, -5.25F, -0.475F, 4.2F, 10.5F, 0.95F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.3F, 1.4F, 0.4F, 0.0F, -0.1396F, 0.3142F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }
}
