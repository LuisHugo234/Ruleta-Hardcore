package com.luishugomod01.client.mixin;

import com.luishugomod01.client.vidas.VidasClientState;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {

    protected DeathScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void ruleta$mostrarVidasEnPantallaMuerte(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!VidasClientState.isSincronizado()) {
            return;
        }
        int restantes = VidasClientState.getVidasRestantes();
        int maximas = VidasClientState.getVidasMaximas();

        Component textoVidas;
        if (restantes <= 0) {
            textoVidas = Component.literal("§4§l☠ ¡HAS PERDIDO TODAS TUS VIDAS! (Modo Espectador Permanente)");
        } else {
            textoVidas = Component.literal("§c♥ Vidas restantes: §e§l" + restantes + " §7/ " + maximas);
        }

        extractor.centeredText(this.font, textoVidas, this.width / 2, 115, 0xFFFFFFFF);
    }
}
