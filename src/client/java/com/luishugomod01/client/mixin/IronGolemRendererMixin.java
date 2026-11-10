package com.luishugomod01.client.mixin;

import com.luishugomod01.client.IronGolemRenderStateAccessor;
import com.luishugomod01.ruleta.ColosoRuinaAccessor;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.golem.IronGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolemRenderer.class)
public abstract class IronGolemRendererMixin {

    @Unique
    private static final Identifier COLOSO_RUINA_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/coloso_ruina.png");

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/golem/IronGolem;Lnet/minecraft/client/renderer/entity/state/IronGolemRenderState;F)V", at = @At("TAIL"))
    private void ruleta$extractColosoState(IronGolem golem, IronGolemRenderState state, float partialTicks, CallbackInfo ci) {
        boolean esColoso = golem.entityTags().contains("coloso_ruina") || (state.scale > 1.40f && state.scale < 1.55f);
        if (state instanceof IronGolemRenderStateAccessor acc) {
            acc.ruleta$setColosoRuina(esColoso);
            if (esColoso && golem instanceof ColosoRuinaAccessor colosoAcc) {
                acc.ruleta$setSlamProgress(colosoAcc.ruleta$getSlamProgress(partialTicks));
            } else {
                acc.ruleta$setSlamProgress(0.0f);
            }
        }
    }

    @Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/IronGolemRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("HEAD"), cancellable = true)
    private void ruleta$texturaColosoRuina(IronGolemRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (state instanceof IronGolemRenderStateAccessor acc && acc.ruleta$isColosoRuina()) {
            cir.setReturnValue(COLOSO_RUINA_TEXTURE);
        } else if (state.scale > 1.40f && state.scale < 1.55f) {
            cir.setReturnValue(COLOSO_RUINA_TEXTURE);
        }
    }
}
