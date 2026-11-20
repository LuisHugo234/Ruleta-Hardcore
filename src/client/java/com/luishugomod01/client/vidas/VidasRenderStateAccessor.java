package com.luishugomod01.client.vidas;

import net.minecraft.network.chat.Component;

public interface VidasRenderStateAccessor {
    Component ruleta$getVidasComponent();
    void ruleta$setVidasComponent(Component component);
    boolean ruleta$isHiddenByRitual();
    void ruleta$setHiddenByRitual(boolean hidden);
}
