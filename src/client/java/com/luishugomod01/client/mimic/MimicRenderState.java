package com.luishugomod01.client.mimic;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class MimicRenderState extends LivingEntityRenderState {
    public boolean isDisguised;
    public boolean isWakingUp;
    public final AnimationState chestIdleAnimationState = new AnimationState();
    public final AnimationState wakeUpAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState biteAnimationState = new AnimationState();
}
