package com.luishugomod01.client.mixin;

import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperRenderer.class)
public abstract class CreeperRendererMixin {

    @Unique
    private static final Identifier ANTIMATERIA_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/creeper_antimateria.png");

    @Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/CreeperRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("HEAD"), cancellable = true)
    private void ruleta$texturaCreeperAntimateria(CreeperRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if (state.scale > 1.06f && state.scale < 1.12f) {
            cir.setReturnValue(ANTIMATERIA_TEXTURE);
        }
    }
}
