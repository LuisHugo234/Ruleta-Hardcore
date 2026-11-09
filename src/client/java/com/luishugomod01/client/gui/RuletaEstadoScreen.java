package com.luishugomod01.client.gui;

import com.luishugomod01.network.AbrirMenuEstadoPayload;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class RuletaEstadoScreen extends Screen {

    private final List<AbrirMenuEstadoPayload.MaldicionInfo> todasMaldiciones;
    private final boolean bloodMoonActiva;
    private final boolean eclipseActivo;

    private int filtroNivel = 0; // 0 = Todas, 1 a 6 = Nivel específico
    private double scrollY = 0.0;
    private int maxScroll = 0;

    private final List<Button> tabButtons = new ArrayList<>();

    public RuletaEstadoScreen(List<AbrirMenuEstadoPayload.MaldicionInfo> maldiciones, boolean bloodMoon, boolean eclipse) {
        super(Component.literal("Estado de la Ruleta - Maldiciones Activas"));
        this.todasMaldiciones = maldiciones != null ? maldiciones : List.of();
        this.bloodMoonActiva = bloodMoon;
        this.eclipseActivo = eclipse;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        this.tabButtons.clear();

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelW = 430;
        int panelH = 250;
        int panelX = centerX - panelW / 2;
        int panelY = centerY - panelH / 2;

        // Pestañas superiores de filtrado (Todas, Nivel 1, Nivel 2, etc.)
        int tabH = 16;
        int startTabX = panelX + 9;
        int tabY = panelY + 28;

        for (int i = 0; i <= 6; i++) {
            final int nivel = i;
            long count = (nivel == 0)
                    ? this.todasMaldiciones.size()
                    : this.todasMaldiciones.stream().filter(m -> m.nivel() == nivel).count();

            String label = (nivel == 0) ? ("Todas (" + count + ")") : ("Nivel " + nivel + " (" + count + ")");
            int btnWidth = (nivel == 0) ? 62 : 57;

            Button btn = Button.builder(Component.literal(label), b -> {
                this.filtroNivel = nivel;
                this.scrollY = 0.0;
                actualizarEstadoTabs();
            }).bounds(startTabX, tabY, btnWidth, tabH).build();

            startTabX += btnWidth + 2;
            this.tabButtons.add(btn);
            this.addRenderableWidget(btn);
        }

        // Botón Cerrar centrado en la parte inferior
        int footerY = panelY + panelH - 24;
        Button btnCerrar = Button.builder(Component.literal("§cCerrar"), b -> this.onClose())
                .bounds(centerX - 45, footerY, 90, 18)
                .build();
        this.addRenderableWidget(btnCerrar);

        actualizarEstadoTabs();
    }

    private void actualizarEstadoTabs() {
        for (int i = 0; i < this.tabButtons.size(); i++) {
            this.tabButtons.get(i).active = (i != this.filtroNivel);
        }
    }

    private int obtenerColorNivel(int nivel) {
        return switch (nivel) {
            case 1 -> 0xFF55FF55; // Verde
            case 2 -> 0xFFFFFF55; // Amarillo
            case 3 -> 0xFFFF9900; // Naranja
            case 4 -> 0xFFFF5555; // Salmón / Coral
            case 5 -> 0xFFFF55FF; // Magenta
            case 6 -> 0xFFFF2222; // Carmesí
            default -> 0xFFFFFFFF;
        };
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.maxScroll > 0) {
            this.scrollY -= scrollY * 18.0;
            this.scrollY = Math.max(0.0, Math.min(this.maxScroll, this.scrollY));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelW = 430;
        int panelH = 250;
        int panelX = centerX - panelW / 2;
        int panelY = centerY - panelH / 2;

        // Fondo del panel principal
        extractor.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xEE101018);
        extractor.outline(panelX, panelY, panelW, panelH, 0xFFFFAA00);

        // Renderizado de botones superiores e inferiores
        super.extractRenderState(extractor, mouseX, mouseY, partialTick);

        // Título principal
        extractor.text(
                this.font,
                Component.literal("§6§lPERMARULETA §7| §fMaldiciones Activas"),
                panelX + 14,
                panelY + 12,
                0xFFFFFFFF,
                true
        );

        // Badges de eventos especiales en cabecera
        if (this.bloodMoonActiva) {
            extractor.text(this.font, Component.literal("§4§l☾ LUNA ROJA"), panelX + panelW - 105, panelY + 12, 0xFFFF4444, true);
        } else if (this.eclipseActivo) {
            extractor.text(this.font, Component.literal("§5§l☼ ECLIPSE"), panelX + panelW - 95, panelY + 12, 0xFFDD55FF, true);
        }

        // Área de contenido con scroll
        int viewTop = panelY + 48;
        int viewBottom = panelY + panelH - 28;
        int viewH = viewBottom - viewTop;
        int cardW = panelW - 32;

        // Filtrar lista
        List<AbrirMenuEstadoPayload.MaldicionInfo> activas = (this.filtroNivel == 0)
                ? this.todasMaldiciones
                : this.todasMaldiciones.stream().filter(m -> m.nivel() == this.filtroNivel).toList();

        if (activas.isEmpty()) {
            extractor.centeredText(
                    this.font,
                    Component.literal("§a✔ No hay maldiciones activas en este nivel"),
                    centerX,
                    centerY - 5,
                    0xFF88FF88
            );
            this.maxScroll = 0;
            return;
        }

        extractor.enableScissor(panelX + 8, viewTop, panelX + panelW - 8, viewBottom);

        int drawY = viewTop + 2 - (int) this.scrollY;

        if (this.filtroNivel == 0) {
            // Pestaña TODAS: Estructurada por encabezados de Nivel
            for (int lvl = 1; lvl <= 6; lvl++) {
                final int l = lvl;
                List<AbrirMenuEstadoPayload.MaldicionInfo> porNivel = activas.stream().filter(m -> m.nivel() == l).toList();
                if (porNivel.isEmpty()) continue;

                int colorLvl = obtenerColorNivel(l);

                // Encabezado del Nivel
                extractor.text(
                        this.font,
                        Component.literal("§l― NIVEL " + l + " ―"),
                        panelX + 16,
                        drawY + 2,
                        colorLvl,
                        true
                );
                extractor.fill(panelX + 16, drawY + 12, panelX + 16 + cardW, drawY + 13, (colorLvl & 0x00FFFFFF) | 0x44000000);
                drawY += 16;

                // Tarjetas del nivel
                for (AbrirMenuEstadoPayload.MaldicionInfo info : porNivel) {
                    drawY = dibujarTarjeta(extractor, info, panelX + 16, drawY, cardW, colorLvl, false);
                }
                drawY += 4; // Espacio entre niveles
            }
        } else {
            // Pestaña de Nivel específico: Tarjetas directas sin prefijo redundante
            int colorLvl = obtenerColorNivel(this.filtroNivel);
            for (AbrirMenuEstadoPayload.MaldicionInfo info : activas) {
                drawY = dibujarTarjeta(extractor, info, panelX + 16, drawY, cardW, colorLvl, false);
            }
        }

        extractor.disableScissor();

        // Cálculo de scroll máximo
        int totalContentH = (drawY + (int) this.scrollY) - (viewTop + 2);
        this.maxScroll = Math.max(0, totalContentH - viewH);

        // Barra de desplazamiento lateral si el contenido excede la vista
        if (this.maxScroll > 0) {
            int scrollbarX = panelX + panelW - 10;
            extractor.fill(scrollbarX, viewTop, scrollbarX + 3, viewBottom, 0x44000000);
            float ratio = (float) viewH / (float) totalContentH;
            int thumbH = Math.max(16, (int) (viewH * ratio));
            int thumbY = viewTop + (int) ((viewH - thumbH) * (this.scrollY / (double) this.maxScroll));
            extractor.fill(scrollbarX, thumbY, scrollbarX + 3, thumbY + thumbH, 0xCCFFAA00);
        }
    }

    private int dibujarTarjeta(GuiGraphicsExtractor extractor, AbrirMenuEstadoPayload.MaldicionInfo info,
                               int x, int y, int width, int colorNivel, boolean incluirTagNivel) {
        List<FormattedCharSequence> lineas = this.font.split(Component.literal("§7" + info.descripcion()), width - 16);
        int cardH = 15 + Math.min(3, lineas.size()) * 9 + 4;

        // Fondo y borde de tarjeta
        extractor.fill(x, y, x + width, y + cardH, 0x66181826);
        extractor.outline(x, y, width, cardH, 0x33555566);
        extractor.fill(x, y, x + 3, y + cardH, colorNivel);

        // Título (solo el nombre si está filtrado o con tag si se requiere)
        String titulo = incluirTagNivel ? ("§l[Nivel " + info.nivel() + "] §f§l" + info.nombre()) : ("§f§l" + info.nombre());
        extractor.text(this.font, Component.literal(titulo), x + 8, y + 4, colorNivel, true);

        // Descripción envuelta
        int textY = y + 15;
        for (int l = 0; l < Math.min(3, lineas.size()); l++) {
            extractor.text(this.font, lineas.get(l), x + 8, textY, 0xFFCCCCCC, true);
            textY += 9;
        }

        return y + cardH + 4;
    }
}
