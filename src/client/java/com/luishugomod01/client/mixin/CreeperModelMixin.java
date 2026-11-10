package com.luishugomod01.client.mixin;

import com.luishugomod01.client.CreeperRenderStateAccessor;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperModel.class)
public abstract class CreeperModelMixin {

    @Shadow
    @Final
    private ModelPart head;

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/CreeperRenderState;)V", at = @At("TAIL"))
    private void ruleta$cabezaDobleCreepersEscondidos(CreeperRenderState state, CallbackInfo ci) {
        boolean esAvispon = (state instanceof CreeperRenderStateAccessor acc && acc.ruleta$isAvispon())
                || (state.scale > 0.25f && state.scale < 0.31f);
        if (esAvispon) {
            this.head.xScale = 1.0f;
            this.head.yScale = 1.0f;
            this.head.zScale = 1.0f;
        } else if (state.scale > 0.32f && state.scale < 0.40f) {
            this.head.xScale = 2.0f;
            this.head.yScale = 2.0f;
            this.head.zScale = 2.0f;
        } else {
            this.head.xScale = 1.0f;
            this.head.yScale = 1.0f;
            this.head.zScale = 1.0f;
        }
    }
}
