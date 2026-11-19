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
 * Modelo 3D del Creeper Antimateria: cuerpo distorsionado con costillas abiertas,
 * núcleo de antimateria giratorio en el pecho, mandíbula desencajada y fragmentos flotantes.
 */
public class CreeperAntimateriaModel extends EntityModel<CreeperRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath("ruleta_hardcore", "creeper_antimateria"), "main");

    private final ModelPart body;
    private final ModelPart core;
    private final ModelPart ribTopL;
    private final ModelPart ribTopR;
    private final ModelPart ribMidL;
    private final ModelPart ribMidR;
    private final ModelPart head;
    private final ModelPart jawLower;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;
    private final ModelPart spikes;
    private final ModelPart shardL;
    private final ModelPart shardR;

    public CreeperAntimateriaModel(ModelPart root) {
        super(root);
        ModelPart rootPart = root.getChild("root");
        this.body = rootPart.getChild("body");
        this.core = this.body.getChild("core");
        this.ribTopL = this.body.getChild("rib_top_l");
        this.ribTopR = this.body.getChild("rib_top_r");
        this.ribMidL = this.body.getChild("rib_mid_l");
        this.ribMidR = this.body.getChild("rib_mid_r");
        this.head = this.body.getChild("head");
        this.jawLower = this.head.getChild("jaw_lower");
        this.leg1 = this.body.getChild("leg1");
        this.leg2 = this.body.getChild("leg2");
        this.leg3 = this.body.getChild("leg3");
        this.leg4 = this.body.getChild("leg4");
        this.spikes = this.body.getChild("spikes");
        this.shardL = this.spikes.getChild("shard_l");
        this.shardR = this.spikes.getChild("shard_r");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition partDef = mesh.getRoot();

        PartDefinition root = partDef.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F),
                PartPose.offset(0.0F, -6.0F, 0.0F));

        body.addOrReplaceChild("rib_top_l", CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-4.3F, -1.8F, -2.3F, 3.1F, 1.8F, 4.6F),
                PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, 0.0F, -0.1396F));

        body.addOrReplaceChild("rib_top_r", CubeListBuilder.create()
                        .texOffs(0, 32).addBox(1.2F, -1.8F, -2.3F, 3.1F, 1.8F, 4.6F),
                PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, 0.0F, 0.1396F));

        body.addOrReplaceChild("rib_mid_l", CubeListBuilder.create()
                        .texOffs(0, 39).addBox(-4.3F, -1.3F, -2.3F, 2.9F, 1.3F, 4.6F),
                PartPose.offsetAndRotation(0.0F, -3.5F, 0.0F, 0.0F, 0.0F, -0.1047F));

        body.addOrReplaceChild("rib_mid_r", CubeListBuilder.create()
                        .texOffs(0, 39).addBox(1.4F, -1.3F, -2.3F, 2.9F, 1.3F, 4.6F),
                PartPose.offsetAndRotation(0.0F, -3.5F, 0.0F, 0.0F, 0.0F, 0.1047F));

        body.addOrReplaceChild("core", CubeListBuilder.create()
                        .texOffs(16, 32).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, -6.0F, -1.0F, 0.7854F, 0.7854F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(25, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, -12.0F, 0.0F));

        head.addOrReplaceChild("jaw_lower", CubeListBuilder.create()
                        .texOffs(25, 16).addBox(-3.6F, -1.0F, -2.7F, 7.2F, 2.0F, 2.7F),
                PartPose.offsetAndRotation(0.0F, 0.0F, -1.5F, 0.2094F, 0.0F, 0.0F));

        body.addOrReplaceChild("leg1", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offset(-2.0F, 0.0F, -4.0F));

        body.addOrReplaceChild("leg2", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offset(2.0F, 0.0F, -4.0F));

        body.addOrReplaceChild("leg3", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offset(-2.0F, 0.0F, 4.0F));

        body.addOrReplaceChild("leg4", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F),
                PartPose.offset(2.0F, 0.0F, 4.0F));

        PartDefinition spikes = body.addOrReplaceChild("spikes", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

        spikes.addOrReplaceChild("spike_back_l", CubeListBuilder.create()
                        .texOffs(40, 24).addBox(-0.75F, -5.0F, -0.75F, 1.5F, 5.0F, 1.5F),
                PartPose.offsetAndRotation(-2.0F, -2.0F, 2.5F, -0.6109F, 0.0F, -0.2618F));

        spikes.addOrReplaceChild("spike_back_r", CubeListBuilder.create()
                        .texOffs(40, 24).addBox(-0.75F, -5.0F, -0.75F, 1.5F, 5.0F, 1.5F),
                PartPose.offsetAndRotation(2.0F, -1.0F, 2.5F, -0.6981F, 0.0F, 0.3142F));

        spikes.addOrReplaceChild("shard_l", CubeListBuilder.create()
                        .texOffs(48, 24).addBox(-0.75F, -1.25F, -0.75F, 1.5F, 2.5F, 1.5F),
                PartPose.offsetAndRotation(-6.0F, -2.0F, 0.0F, 0.2618F, 0.3491F, 0.4363F));

        spikes.addOrReplaceChild("shard_r", CubeListBuilder.create()
                        .texOffs(48, 24).addBox(-0.75F, -1.25F, -0.75F, 1.5F, 2.5F, 1.5F),
                PartPose.offsetAndRotation(5.5F, 0.0F, 0.5F, -0.3491F, -0.2618F, -0.4363F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(CreeperRenderState state) {
        super.setupAnim(state);
        this.resetPose();

        float age = (state instanceof CreeperRenderStateAccessor acc && acc.ruleta$getAgeInTicks() > 0.0F)
                ? acc.ruleta$getAgeInTicks()
                : state.ageInTicks;
        float swell = Mth.clamp(state.swelling, 0.0F, 1.0F);
        float spinBoost = 1.0F + swell * 4.5F;

        this.head.yRot += state.yRot * Mth.DEG_TO_RAD;
        this.head.xRot += state.xRot * Mth.DEG_TO_RAD;

        float walkPos = state.walkAnimationPos;
        float walkSpeed = state.walkAnimationSpeed;
        this.leg1.xRot += Mth.cos(walkPos * 0.6662F) * 1.4F * walkSpeed;
        this.leg4.xRot += Mth.cos(walkPos * 0.6662F) * 1.4F * walkSpeed;
        this.leg2.xRot += Mth.cos(walkPos * 0.6662F + Mth.PI) * 1.4F * walkSpeed;
        this.leg3.xRot += Mth.cos(walkPos * 0.6662F + Mth.PI) * 1.4F * walkSpeed;

        // Núcleo de antimateria giratorio (se acelera violentamente al cargar implosión)
        this.core.yRot += age * 0.14F * spinBoost;
        this.core.xRot += age * 0.10F * spinBoost;
        this.core.zRot += Mth.sin(age * 0.08F) * 0.35F * spinBoost;

        // Costillas abiertas latiendo con inestabilidad gravitacional
        float ribBreath = Mth.sin(age * (0.12F + swell * 0.45F)) * (0.06F + swell * 0.14F);
        this.ribTopL.zRot -= ribBreath;
        this.ribTopR.zRot += ribBreath;
        this.ribMidL.zRot -= ribBreath * 0.85F;
        this.ribMidR.zRot += ribBreath * 0.85F;

        // Mandíbula desencajada que se abre y tiembla al prepararse para implotar
        this.jawLower.xRot += Mth.sin(age * 0.16F) * 0.05F + swell * 0.42F + (swell > 0.1F ? Mth.sin(age * 1.6F) * 0.08F * swell : 0.0F);

        // Púas dorsales y fragmentos flotantes levitando y rotando
        this.spikes.y += Mth.sin(age * 0.11F) * 0.55F;
        this.shardL.y += Mth.sin(age * 0.15F) * 0.9F;
        this.shardL.yRot += age * 0.10F * spinBoost;
        this.shardL.zRot += Mth.cos(age * 0.12F) * 0.25F;
        this.shardR.y += Mth.cos(age * 0.15F + 1.2F) * 0.9F;
        this.shardR.yRot -= age * 0.10F * spinBoost;
        this.shardR.zRot -= Mth.sin(age * 0.12F) * 0.25F;
    }
}
