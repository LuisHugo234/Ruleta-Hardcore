package com.luishugomod01.client.mixin;

import com.luishugomod01.client.vidas.VidasClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @ModifyConstant(method = "extractRenderState", constant = @Constant(intValue = 12))
    private int ruleta$desplazarBossBarDebajoDeVidas(int originalY) {
        if (!VidasClientState.isSincronizado() || this.minecraft == null || this.minecraft.getWindow() == null) {
            return originalY;
        }
        double guiScale = this.minecraft.getWindow().getGuiScale();
        if (guiScale <= 0) guiScale = 1.0;

        int topY = Math.max(8, (int) Math.round(10.0 / guiScale * 2.0));
        int heartH = Math.max(10, (int) Math.round(34.0 / guiScale));
        // El nombre del jefe se dibuja 9 px arriba de 'y'; +16 deja un margen limpio debajo de los corazones
        return Math.max(originalY, topY + heartH + 16);
    }
}
