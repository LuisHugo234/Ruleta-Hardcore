package com.luishugomod01.client.mixin;

import com.luishugomod01.client.CreeperRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CreeperRenderState.class)
public class CreeperRenderStateMixin implements CreeperRenderStateAccessor {
    @Unique
    private boolean ruleta$isAvispon;

    @Unique
    private float ruleta$ageInTicks;

    @Override
    public boolean ruleta$isAvispon() {
        return this.ruleta$isAvispon;
    }

    @Override
    public void ruleta$setAvispon(boolean avispon) {
        this.ruleta$isAvispon = avispon;
    }

    @Override
    public float ruleta$getAgeInTicks() {
        return this.ruleta$ageInTicks;
    }

    @Override
    public void ruleta$setAgeInTicks(float age) {
        this.ruleta$ageInTicks = age;
    }
}
