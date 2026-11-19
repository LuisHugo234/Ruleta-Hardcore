package com.luishugomod01.client.mixin;

import com.luishugomod01.client.PhantomRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.PhantomRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PhantomRenderState.class)
public class PhantomRenderStateMixin implements PhantomRenderStateAccessor {

    @Unique
    private boolean ruleta$isPhantomRaptor;

    @Override
    public boolean ruleta$isPhantomRaptor() {
        return this.ruleta$isPhantomRaptor;
    }

    @Override
    public void ruleta$setPhantomRaptor(boolean raptor) {
        this.ruleta$isPhantomRaptor = raptor;
    }
}
