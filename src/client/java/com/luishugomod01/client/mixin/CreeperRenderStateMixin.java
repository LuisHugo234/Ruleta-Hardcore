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
    private boolean ruleta$isFantasma;

    @Unique
    private boolean ruleta$isTermonuclear;

    @Unique
    private boolean ruleta$isCosmico;

    @Unique
    private boolean ruleta$isAntimateria;

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
    public boolean ruleta$isFantasma() {
        return this.ruleta$isFantasma;
    }

    @Override
    public void ruleta$setFantasma(boolean fantasma) {
        this.ruleta$isFantasma = fantasma;
    }

    @Override
    public boolean ruleta$isTermonuclear() {
        return this.ruleta$isTermonuclear;
    }

    @Override
    public void ruleta$setTermonuclear(boolean termonuclear) {
        this.ruleta$isTermonuclear = termonuclear;
    }

    @Override
    public boolean ruleta$isCosmico() {
        return this.ruleta$isCosmico;
    }

    @Override
    public void ruleta$setCosmico(boolean cosmico) {
        this.ruleta$isCosmico = cosmico;
    }

    @Override
    public boolean ruleta$isAntimateria() {
        return this.ruleta$isAntimateria;
    }

    @Override
    public void ruleta$setAntimateria(boolean antimateria) {
        this.ruleta$isAntimateria = antimateria;
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
