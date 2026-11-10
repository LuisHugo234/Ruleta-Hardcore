package com.luishugomod01.client.mixin;

import com.luishugomod01.client.IronGolemRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(IronGolemRenderState.class)
public class IronGolemRenderStateMixin implements IronGolemRenderStateAccessor {
    @Unique
    private boolean ruleta$isColosoRuina = false;
    @Unique
    private float ruleta$slamProgress = 0.0f;

    @Override
    public boolean ruleta$isColosoRuina() {
        return this.ruleta$isColosoRuina;
    }

    @Override
    public void ruleta$setColosoRuina(boolean coloso) {
        this.ruleta$isColosoRuina = coloso;
    }

    @Override
    public float ruleta$getSlamProgress() {
        return this.ruleta$slamProgress;
    }

    @Override
    public void ruleta$setSlamProgress(float progress) {
        this.ruleta$slamProgress = progress;
    }
}
