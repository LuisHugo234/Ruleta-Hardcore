package com.luishugomod01.client.mixin;

import com.luishugomod01.client.vidas.VidasRenderStateAccessor;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements VidasRenderStateAccessor {
    @Unique
    private Component ruleta$vidasComponent;

    @Override
    public Component ruleta$getVidasComponent() {
        return this.ruleta$vidasComponent;
    }

    @Override
    public void ruleta$setVidasComponent(Component component) {
        this.ruleta$vidasComponent = component;
    }
}
