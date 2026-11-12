package com.luishugomod01.client.mixin;

import com.luishugomod01.client.nigromante.NigromanteRenderStateAccessor;
import net.minecraft.client.renderer.entity.AbstractSkeletonRenderer;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.skeleton.Stray;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonRenderer.class)
public abstract class AbstractSkeletonRendererMixin {

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/monster/skeleton/AbstractSkeleton;Lnet/minecraft/client/renderer/entity/state/SkeletonRenderState;F)V",
            at = @At("TAIL")
    )
    private void ruleta$extractNigromanteState(AbstractSkeleton entity, SkeletonRenderState state, float partialTicks, CallbackInfo ci) {
        if (state instanceof NigromanteRenderStateAccessor acc) {
            boolean esNigromante = entity instanceof Stray stray && (
                    stray.entityTags().contains("nigromante_criptas")
                    || (Math.abs(stray.getScale() - 1.25F) < 0.04F && stray.getItemBySlot(EquipmentSlot.MAINHAND).is(Items.BLUE_ICE))
            );
            acc.ruleta$setNigromante(esNigromante);
        }
    }
}
