package com.luishugomod01.client.mixin;

import com.luishugomod01.client.CreeperRenderStateAccessor;
import com.luishugomod01.client.IronGolemRenderStateAccessor;
import com.luishugomod01.client.bloodmoon.BloodMoonClientState;
import com.luishugomod01.client.coloso.ColosoRuinaModel;
import com.luishugomod01.client.creeper.CreeperCosmicoModel;
import com.luishugomod01.client.creeper.CreeperFantasmaModel;
import com.luishugomod01.client.creeper.CreeperTermonuclearModel;
import com.luishugomod01.client.elder.ReyProfundoModel;
import com.luishugomod01.client.nigromante.NigromanteModel;
import com.luishugomod01.client.nigromante.NigromanteRenderStateAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.StrayRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

    @Unique
    private static final Identifier NIGROMANTE_EYES_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/nigromante_criptas_eyes.png");

    @Unique
    private static final Identifier FANTASMA_EYES_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/creeper_fantasma_eyes.png");

    @Unique
    private static final Identifier TERMONUCLEAR_EYES_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/creeper_termonuclear_eyes.png");

    @Unique
    private static final Identifier COSMICO_EYES_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/creeper_cosmico_eyes.png");

    @Unique
    private static final Identifier COLOSO_EYES_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/coloso_ruina_eyes.png");

    @Unique
    private static final Identifier REY_PROFUNDO_EYES_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/rey_profundo_eyes.png");

    @Shadow
    protected M model;

    @Unique
    private M ruleta$savedVanillaModel;

    @Unique
    private Identifier ruleta$activeEyesTexture;

    @Unique
    private NigromanteModel ruleta$nigromanteModel;

    @Unique
    private CreeperFantasmaModel ruleta$creeperFantasmaModel;

    @Unique
    private CreeperTermonuclearModel ruleta$creeperTermonuclearModel;

    @Unique
    private CreeperCosmicoModel ruleta$creeperCosmicoModel;

    @Unique
    private ColosoRuinaModel ruleta$colosoRuinaModel;

    @Unique
    private ReyProfundoModel ruleta$reyProfundoModel;

    @SuppressWarnings("unchecked")
    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("HEAD")
    )
    private void ruleta$swapToCustomModel(S state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState, CallbackInfo ci) {
        if ((Object) this instanceof StrayRenderer && state instanceof NigromanteRenderStateAccessor acc && acc.ruleta$isNigromante()) {
            if (this.ruleta$nigromanteModel == null) {
                this.ruleta$nigromanteModel = new NigromanteModel(
                        Minecraft.getInstance().getEntityModels().bakeLayer(NigromanteModel.LAYER_LOCATION)
                );
            }
            this.ruleta$savedVanillaModel = this.model;
            this.ruleta$activeEyesTexture = NIGROMANTE_EYES_TEXTURE;
            this.model = (M) this.ruleta$nigromanteModel;
        } else if ((Object) this instanceof ElderGuardianRenderer && BloodMoonClientState.isReyDeLoProfundoActive()) {
            if (this.ruleta$reyProfundoModel == null) {
                this.ruleta$reyProfundoModel = new ReyProfundoModel(
                        Minecraft.getInstance().getEntityModels().bakeLayer(ReyProfundoModel.LAYER_LOCATION)
                );
            }
            this.ruleta$savedVanillaModel = this.model;
            this.ruleta$activeEyesTexture = REY_PROFUNDO_EYES_TEXTURE;
            this.model = (M) this.ruleta$reyProfundoModel;
        } else if ((Object) this instanceof IronGolemRenderer
                && ((state instanceof IronGolemRenderStateAccessor acc && acc.ruleta$isColosoRuina())
                || (state.scale > 1.40f && state.scale < 1.55f))) {
            if (this.ruleta$colosoRuinaModel == null) {
                this.ruleta$colosoRuinaModel = new ColosoRuinaModel(
                        Minecraft.getInstance().getEntityModels().bakeLayer(ColosoRuinaModel.LAYER_LOCATION)
                );
            }
            this.ruleta$savedVanillaModel = this.model;
            this.ruleta$activeEyesTexture = COLOSO_EYES_TEXTURE;
            this.model = (M) this.ruleta$colosoRuinaModel;
        } else if ((Object) this instanceof CreeperRenderer) {
            boolean isFantasma = (state instanceof CreeperRenderStateAccessor acc && acc.ruleta$isFantasma())
                    || (state.scale > 1.015f && state.scale < 1.055f);
            boolean isCosmico = (state instanceof CreeperRenderStateAccessor acc && acc.ruleta$isCosmico())
                    || (state.scale > 1.125f && state.scale < 1.165f);
            boolean isTermonuclear = (state instanceof CreeperRenderStateAccessor acc && acc.ruleta$isTermonuclear())
                    || (state.scale > 1.18f && state.scale < 1.26f);
            if (isFantasma) {
                if (this.ruleta$creeperFantasmaModel == null) {
                    this.ruleta$creeperFantasmaModel = new CreeperFantasmaModel(
                            Minecraft.getInstance().getEntityModels().bakeLayer(CreeperFantasmaModel.LAYER_LOCATION)
                    );
                }
                this.ruleta$savedVanillaModel = this.model;
                this.ruleta$activeEyesTexture = FANTASMA_EYES_TEXTURE;
                this.model = (M) this.ruleta$creeperFantasmaModel;
            } else if (isCosmico) {
                if (this.ruleta$creeperCosmicoModel == null) {
                    this.ruleta$creeperCosmicoModel = new CreeperCosmicoModel(
                            Minecraft.getInstance().getEntityModels().bakeLayer(CreeperCosmicoModel.LAYER_LOCATION)
                    );
                }
                this.ruleta$savedVanillaModel = this.model;
                this.ruleta$activeEyesTexture = COSMICO_EYES_TEXTURE;
                this.model = (M) this.ruleta$creeperCosmicoModel;
            } else if (isTermonuclear) {
                if (this.ruleta$creeperTermonuclearModel == null) {
                    this.ruleta$creeperTermonuclearModel = new CreeperTermonuclearModel(
                            Minecraft.getInstance().getEntityModels().bakeLayer(CreeperTermonuclearModel.LAYER_LOCATION)
                    );
                }
                this.ruleta$savedVanillaModel = this.model;
                this.ruleta$activeEyesTexture = TERMONUCLEAR_EYES_TEXTURE;
                this.model = (M) this.ruleta$creeperTermonuclearModel;
            }
        }
    }

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;shouldRenderLayers(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;)Z"
            )
    )
    private void ruleta$submitCustomModelGlow(S state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState, CallbackInfo ci) {
        if (this.ruleta$savedVanillaModel != null && this.ruleta$activeEyesTexture != null && !state.isInvisible) {
            collector.submitModel(
                    this.model,
                    state,
                    poseStack,
                    RenderTypes.eyes(this.ruleta$activeEyesTexture),
                    15728880,
                    OverlayTexture.NO_OVERLAY,
                    -1,
                    null,
                    state.outlineColor
            );
        }
    }

    @Inject(method = "shouldRenderLayers(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;)Z", at = @At("HEAD"), cancellable = true)
    private void ruleta$skipVanillaLayers(S state, CallbackInfoReturnable<Boolean> cir) {
        if (this.ruleta$savedVanillaModel != null) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("RETURN")
    )
    private void ruleta$restoreVanillaModel(S state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState, CallbackInfo ci) {
        if (this.ruleta$savedVanillaModel != null) {
            this.model = this.ruleta$savedVanillaModel;
            this.ruleta$savedVanillaModel = null;
            this.ruleta$activeEyesTexture = null;
        }
    }
}
