package com.luishugomod01.client.mixin;

import com.luishugomod01.entity.MimicEntity;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Oculta completamente la hitbox de depuración (F3 + B) del Mimic mientras está
 * disfrazado como cofre, revelándola únicamente cuando despierta.
 */
@Mixin(EntityHitboxDebugRenderer.class)
public class EntityHitboxDebugRendererMixin {

    @Inject(method = "showHitboxes(Lnet/minecraft/world/entity/Entity;FZ)V", at = @At("HEAD"), cancellable = true)
    private void ruleta$hideDisguisedMimicHitbox(Entity entity, float partialTick, boolean isServer, CallbackInfo ci) {
        if (entity instanceof MimicEntity mimic && mimic.isDisguised()) {
            ci.cancel();
        }
    }
}
