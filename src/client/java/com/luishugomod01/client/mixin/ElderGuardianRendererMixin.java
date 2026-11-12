package com.luishugomod01.client.mixin;

import com.luishugomod01.client.bloodmoon.BloodMoonClientState;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.entity.state.GuardianRenderState;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ElderGuardianRenderer.class)
public abstract class ElderGuardianRendererMixin {

    @Unique
    private static final Identifier REY_PROFUNDO_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/rey_profundo.png");

    @Inject(
            method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/GuardianRenderState;)Lnet/minecraft/resources/Identifier;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ruleta$texturaReyProfundo(GuardianRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (BloodMoonClientState.isReyDeLoProfundoActive()) {
            cir.setReturnValue(REY_PROFUNDO_TEXTURE);
        }
    }
}
