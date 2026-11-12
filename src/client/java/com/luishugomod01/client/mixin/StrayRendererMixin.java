package com.luishugomod01.client.mixin;

import com.luishugomod01.client.nigromante.NigromanteRenderStateAccessor;
import net.minecraft.client.renderer.entity.StrayRenderer;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StrayRenderer.class)
public abstract class StrayRendererMixin {

    @Unique
    private static final Identifier NIGROMANTE_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/nigromante_criptas.png");

    @Inject(
            method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/SkeletonRenderState;)Lnet/minecraft/resources/Identifier;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ruleta$texturaNigromante(SkeletonRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (state instanceof NigromanteRenderStateAccessor acc && acc.ruleta$isNigromante()) {
            cir.setReturnValue(NIGROMANTE_TEXTURE);
        }
    }
}
