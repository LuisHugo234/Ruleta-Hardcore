package com.luishugomod01.client.gui;

import com.luishugomod01.network.AbrirMenuRevivirPayload;
import com.luishugomod01.network.SolicitarRevivirJugadorPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class RevivirJugadorScreen extends Screen {

    private static final int PANEL_W = 286;
    private static final int PANEL_H = 216;
    private static final int ROW_H = 22;
    private static final int ROW_STEP = 26;

    private final boolean manoPrincipal;
    private final List<AbrirMenuRevivirPayload.JugadorEliminado> eliminados;
    private final List<Button> botonesJugadores = new ArrayList<>();
    private double scrollOffset = 0.0;

    public RevivirJugadorScreen(boolean manoPrincipal, List<AbrirMenuRevivirPayload.JugadorEliminado> eliminados) {
        super(Component.literal("Ritual de Resurrección"));
        this.manoPrincipal = manoPrincipal;
        this.eliminados = eliminados;
    }

    private int getListY(int panelY) {
        return panelY + 52;
    }

    private int getListH() {
        return PANEL_H - 92;
    }

    private int getTotalContentH() {
        if (this.eliminados.isEmpty()) {
            return 0;
        }
        return this.eliminados.size() * ROW_STEP + 4;
    }

    private int getMaxScroll() {
        return Math.max(0, getTotalContentH() - getListH());
    }

    @Override
    protected void init() {
        this.clearWidgets();
        this.botonesJugadores.clear();

        int centerX = this.width / 2;
        int panelX = centerX - PANEL_W / 2;
        int panelY = this.height / 2 - PANEL_H / 2;

        this.scrollOffset = Math.clamp(this.scrollOffset, 0.0, getMaxScroll());

        boolean tieneScroll = getMaxScroll() > 0;
        int btnW = PANEL_W - (tieneScroll ? 44 : 36);

        for (int i = 0; i < this.eliminados.size(); i++) {
            AbrirMenuRevivirPayload.JugadorEliminado objetivo = this.eliminados.get(i);
            Button btn = Button.builder(
                    Component.literal("§6§l✦ §fRevivir a §e" + objetivo.nombre()),
                    b -> {
                        ClientPlayNetworking.send(new SolicitarRevivirJugadorPayload(objetivo.uuid(), this.manoPrincipal));
                        this.onClose();
                    }
            ).bounds(panelX + 18, getListY(panelY) + 4 + i * ROW_STEP, btnW, ROW_H).build();
            this.botonesJugadores.add(btn);
            this.addWidget(btn);
        }

        actualizarPosicionesScroll(panelY);

        this.addRenderableWidget(Button.builder(
                Component.literal("§7Cancelar"),
                btn -> this.onClose()
        ).bounds(centerX - 54, panelY + PANEL_H - 32, 108, 20).build());
    }

    private void actualizarPosicionesScroll(int panelY) {
        int listY = getListY(panelY);
        int listBottom = listY + getListH();
        int scrollPx = (int) Math.round(this.scrollOffset);

        for (int i = 0; i < this.botonesJugadores.size(); i++) {
            Button btn = this.botonesJugadores.get(i);
            int y = listY + 4 + i * ROW_STEP - scrollPx;
            btn.setY(y);
            boolean visible = (y + ROW_H > listY) && (y < listBottom);
            btn.visible = visible;
            btn.active = visible;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxScroll = getMaxScroll();
        if (maxScroll <= 0) {
            return false;
        }
        this.scrollOffset = Math.clamp(this.scrollOffset - scrollY * 22.0, 0.0, maxScroll);
        int panelY = this.height / 2 - PANEL_H / 2;
        actualizarPosicionesScroll(panelY);
        return true;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        int centerX = this.width / 2;
        int panelX = centerX - PANEL_W / 2;
        int panelY = this.height / 2 - PANEL_H / 2;

        extractor.fillGradient(panelX, panelY, panelX + PANEL_W, panelY + PANEL_H, 0xEC14111A, 0xF208070D);
        extractor.outline(panelX, panelY, PANEL_W, PANEL_H, 0xFFE5AE32);
        extractor.outline(panelX + 1, panelY + 1, PANEL_W - 2, PANEL_H - 2, 0x55FFE082);

        extractor.fillGradient(panelX + 2, panelY + 2, panelX + PANEL_W - 2, panelY + 34, 0xCC281D0D, 0x44120F16);
        extractor.fill(panelX + 12, panelY + 34, panelX + PANEL_W - 12, panelY + 35, 0x99E5AE32);

        extractor.centeredText(
                this.font,
                Component.literal("§6§l✦ RITUAL DE RESURRECCIÓN ✦"),
                centerX,
                panelY + 14,
                0xFFFFFFFF
        );

        extractor.centeredText(
                this.font,
                Component.literal("§7Elige al compañero eliminado (0 ♥) para revivir con §e1 ♥"),
                centerX,
                panelY + 40,
                0xFFB8B4C6
        );

        int listX = panelX + 12;
        int listY = getListY(panelY);
        int listW = PANEL_W - 24;
        int listH = getListH();

        extractor.fill(listX, listY, listX + listW, listY + listH, 0x5509080E);
        extractor.outline(listX, listY, listW, listH, 0x44E5AE32);

        if (this.eliminados.isEmpty()) {
            extractor.centeredText(
                    this.font,
                    Component.literal("§cNo hay jugadores eliminados conectados."),
                    centerX,
                    listY + listH / 2 - 4,
                    0xFFFF6666
            );
        } else {
            boolean mouseEnLista = mouseX >= listX && mouseX <= listX + listW && mouseY >= listY && mouseY <= listY + listH;
            int effMouseX = mouseEnLista ? mouseX : -1000;
            int effMouseY = mouseEnLista ? mouseY : -1000;

            extractor.enableScissor(listX + 1, listY + 1, listX + listW - 1, listY + listH - 1);
            for (Button btn : this.botonesJugadores) {
                if (btn.visible) {
                    btn.extractRenderState(extractor, effMouseX, effMouseY, partialTick);
                }
            }
            extractor.disableScissor();

            int maxScroll = getMaxScroll();
            int totalH = getTotalContentH();
            if (maxScroll > 0 && totalH > 0) {
                int trackX = listX + listW - 6;
                extractor.fill(trackX, listY + 3, trackX + 4, listY + listH - 3, 0x88111118);
                int trackH = listH - 6;
                int barH = Math.max(20, (int) ((double) listH / totalH * trackH));
                int barY = listY + 3 + (int) ((this.scrollOffset / maxScroll) * (trackH - barH));
                extractor.fill(trackX, barY, trackX + 4, barY + barH, 0xFFE5AE32);
            }
        }

        super.extractRenderState(extractor, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
