package com.luishugomod01.client.mixin;

import com.luishugomod01.client.PhantomRenderStateAccessor;
import com.luishugomod01.ruleta.PhantomRaptorAccessor;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.client.renderer.entity.state.PhantomRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Phantom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PhantomRenderer.class)
public abstract class PhantomRendererMixin {

    @Unique
    private static final Identifier PHANTOM_RAPTOR_TEXTURE =
            Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/phantom_raptor.png");

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/monster/Phantom;Lnet/minecraft/client/renderer/entity/state/PhantomRenderState;F)V",
            at = @At("TAIL")
    )
    private void ruleta$extractPhantomRaptorState(Phantom phantom, PhantomRenderState state, float partialTicks, CallbackInfo ci) {
        if (state instanceof PhantomRenderStateAccessor acc) {
            boolean esRaptor = phantom instanceof PhantomRaptorAccessor raptorAcc && raptorAcc.ruleta$isPhantomRaptor();
            acc.ruleta$setPhantomRaptor(esRaptor);
        }
    }

    @Inject(
            method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/PhantomRenderState;)Lnet/minecraft/resources/Identifier;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ruleta$texturaPhantomRaptor(PhantomRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (state instanceof PhantomRenderStateAccessor acc && acc.ruleta$isPhantomRaptor()) {
            cir.setReturnValue(PHANTOM_RAPTOR_TEXTURE);
        }
    }
}
