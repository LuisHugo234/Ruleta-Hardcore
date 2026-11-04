package com.luishugomod01.client.gui;

import com.luishugomod01.network.GuardarConfigAdminPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AdminConfigScreen extends Screen {

    private boolean ruletaAutomatica;
    private int vidasIniciales;
    private Button toggleRuletaButton;
    private Button menosVidasButton;
    private Button masVidasButton;

    public AdminConfigScreen(boolean ruletaAutomaticaInicial, int vidasInicialesActuales) {
        super(Component.literal("Configuración de Administrador - PermaRuleta"));
        this.ruletaAutomatica = ruletaAutomaticaInicial;
        this.vidasIniciales = Math.max(1, vidasInicialesActuales);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.toggleRuletaButton = Button.builder(
                obtenerTextoModoRuleta(),
                btn -> {
                    this.ruletaAutomatica = !this.ruletaAutomatica;
                    btn.setMessage(obtenerTextoModoRuleta());
                }
        ).bounds(centerX - 110, centerY - 35, 220, 20).build();
        this.addRenderableWidget(this.toggleRuletaButton);

        this.menosVidasButton = Button.builder(
                Component.literal("§c-"),
                btn -> {
                    if (this.vidasIniciales > 1) {
                        this.vidasIniciales--;
                        actualizarBotonesVidas();
                    }
                }
        ).bounds(centerX - 80, centerY + 10, 30, 20).build();
        this.addRenderableWidget(this.menosVidasButton);

        this.masVidasButton = Button.builder(
                Component.literal("§a+"),
                btn -> {
                    if (this.vidasIniciales < 99) {
                        this.vidasIniciales++;
                        actualizarBotonesVidas();
                    }
                }
        ).bounds(centerX + 50, centerY + 10, 30, 20).build();
        this.addRenderableWidget(this.masVidasButton);

        Button guardarButton = Button.builder(
                Component.literal("§a§lGuardar y Comenzar"),
                btn -> {
                    ClientPlayNetworking.send(new GuardarConfigAdminPayload(this.ruletaAutomatica, this.vidasIniciales));
                    this.onClose();
                }
        ).bounds(centerX - 110, centerY + 50, 220, 20).build();
        this.addRenderableWidget(guardarButton);

        actualizarBotonesVidas();
    }

    private void actualizarBotonesVidas() {
        if (this.menosVidasButton != null) {
            this.menosVidasButton.active = this.vidasIniciales > 1;
        }
        if (this.masVidasButton != null) {
            this.masVidasButton.active = this.vidasIniciales < 99;
        }
    }

    private Component obtenerTextoModoRuleta() {
        return Component.literal("Modo de Giro: " + (this.ruletaAutomatica ? "§a§lAUTOMÁTICO" : "§e§lMANUAL"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int panelW = 260;
        int panelH = 170;
        int panelX = centerX - panelW / 2;
        int panelY = centerY - panelH / 2;

        extractor.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xCC101015);
        extractor.outline(panelX, panelY, panelW, panelH, 0xFFFFAA00);

        super.extractRenderState(extractor, mouseX, mouseY, partialTick);

        extractor.centeredText(
                this.font,
                Component.literal("§6§lCONFIGURACIÓN PERMARULETA"),
                centerX,
                panelY + 14,
                0xFFFFFFFF
        );

        extractor.centeredText(
                this.font,
                Component.literal("§7Vidas Iniciales por Jugador:"),
                centerX,
                centerY - 4,
                0xFFCCCCCC
        );

        extractor.centeredText(
                this.font,
                Component.literal("§c♥ §f§l" + this.vidasIniciales),
                centerX,
                centerY + 16,
                0xFFFFFFFF
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
