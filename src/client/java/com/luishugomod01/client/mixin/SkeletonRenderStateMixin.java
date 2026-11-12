package com.luishugomod01.client.mixin;

import com.luishugomod01.client.nigromante.NigromanteRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SkeletonRenderState.class)
public class SkeletonRenderStateMixin implements NigromanteRenderStateAccessor {

    @Unique
    private boolean ruleta$isNigromante = false;

    @Override
    public boolean ruleta$isNigromante() {
        return this.ruleta$isNigromante;
    }

    @Override
    public void ruleta$setNigromante(boolean value) {
        this.ruleta$isNigromante = value;
    }
}
