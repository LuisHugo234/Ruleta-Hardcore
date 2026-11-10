package com.luishugomod01.client.mixin;

import com.luishugomod01.client.IronGolemRenderStateAccessor;
import net.minecraft.client.model.animal.golem.IronGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronGolemModel.class)
public abstract class IronGolemModelMixin {

    @Shadow
    @Final
    private ModelPart head;

    @Shadow
    @Final
    private ModelPart rightArm;

    @Shadow
    @Final
    private ModelPart leftArm;

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/IronGolemRenderState;)V", at = @At("TAIL"))
    private void ruleta$animacionSlamColoso(IronGolemRenderState state, CallbackInfo ci) {
        if (state instanceof IronGolemRenderStateAccessor acc && acc.ruleta$isColosoRuina()) {
            float progress = acc.ruleta$getSlamProgress();
            if (progress > 0.0f && progress <= 1.0f) {
                if (progress < 0.70f) {
                    // Fase 1: Levantamiento amenazante de ambos brazos hacia arriba (wind-up)
                    float levantamiento = progress / 0.70f;
                    float eased = Mth.sin(levantamiento * (float) Math.PI / 2.0f);
                    this.rightArm.xRot = -2.4f * eased;
                    this.leftArm.xRot = -2.4f * eased;
                    this.rightArm.zRot = 0.35f * eased;
                    this.leftArm.zRot = -0.35f * eased;
                    this.head.xRot = -0.4f * eased;
                } else {
                    // Fase 2: Golpe demoledor hacia abajo contra el suelo (slam impact)
                    float caida = (progress - 0.70f) / 0.30f;
                    float slamFactor = Mth.sin(caida * (float) Math.PI / 2.0f);
                    this.rightArm.xRot = -2.4f + 3.1f * slamFactor;
                    this.leftArm.xRot = -2.4f + 3.1f * slamFactor;
                    this.rightArm.zRot = 0.35f * (1.0f - slamFactor);
                    this.leftArm.zRot = -0.35f * (1.0f - slamFactor);
                    this.head.xRot = -0.4f + 0.9f * slamFactor;
                }
            }
        }
    }
}
