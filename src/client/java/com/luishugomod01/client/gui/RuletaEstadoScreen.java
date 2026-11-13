package com.luishugomod01.client.gui;

import com.luishugomod01.network.AbrirMenuEstadoPayload;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RuletaEstadoScreen extends Screen {

    private sealed interface FilaEstado permits FilaHeader, FilaMaldicion, FilaSeparador {
        int altura();
    }

    private record FilaHeader(int nivel) implements FilaEstado {
        @Override
        public int altura() {
            return 18;
        }
    }

    private record FilaMaldicion(AbrirMenuEstadoPayload.MaldicionInfo info) implements FilaEstado {
        @Override
        public int altura() {
            return 38;
        }
    }

    private record FilaSeparador() implements FilaEstado {
        @Override
        public int altura() {
            return 6;
        }
    }

    private final List<AbrirMenuEstadoPayload.MaldicionInfo> todasLasMaldiciones;
    private final boolean bloodMoonActiva;
    private final boolean eclipseActivo;

    private int filtroNivel = 0; // 0 = Todos, 1..6 = Nivel específico
    private String textoBusqueda = "";
    private double scrollOffset = 0.0;
    private final List<Button> botonesFiltro = new ArrayList<>();
    private final int[] conteoPorNivel = new int[7];
    private EditBox campoBusqueda;

    public RuletaEstadoScreen(
            List<AbrirMenuEstadoPayload.MaldicionInfo> maldiciones,
            boolean bloodMoonActiva,
            boolean eclipseActivo
    ) {
        super(Component.literal("Maldiciones Activas - PermaRuleta"));
        this.todasLasMaldiciones = maldiciones != null ? maldiciones : List.of();
        this.bloodMoonActiva = bloodMoonActiva;
        this.eclipseActivo = eclipseActivo;
        for (AbrirMenuEstadoPayload.MaldicionInfo m : this.todasLasMaldiciones) {
            int n = Math.clamp(m.nivel(), 1, 6);
            this.conteoPorNivel[n]++;
        }
    }

    @Override
    protected void init() {
        this.botonesFiltro.clear();
        int centerX = this.width / 2;
        int panelW = Math.min(440, this.width - 24);
        int panelH = Math.min(280, this.height - 24);
        int panelX = centerX - panelW / 2;
        int panelY = (this.height - panelH) / 2;

        // Barra de búsqueda interactiva en la esquina superior derecha del panel
        int searchW = 128;
        this.campoBusqueda = new EditBox(
                this.font,
                panelX + panelW - searchW - 10,
                panelY + 8,
                searchW,
                16,
                Component.literal("Buscar maldición...")
        );
        this.campoBusqueda.setHint(Component.literal("§8🔍 Buscar..."));
        this.campoBusqueda.setValue(this.textoBusqueda);
        this.campoBusqueda.setResponder(val -> {
            this.textoBusqueda = val != null ? val : "";
            this.scrollOffset = 0.0;
        });
        this.addRenderableWidget(this.campoBusqueda);

        // Pestañas de filtro por nivel sin contadores numéricos
        int tabY = panelY + 32;
        int totalTabs = 7;
        int gap = 3;
        int tabW = (panelW - 20 - gap * (totalTabs - 1)) / totalTabs;
        int startX = panelX + 10;

        for (int i = 0; i < totalTabs; i++) {
            final int nivel = i;
            Button btn = Button.builder(
                    obtenerTextoTab(nivel),
                    b -> {
                        this.filtroNivel = nivel;
                        this.scrollOffset = 0.0;
                        actualizarBotonesFiltro();
                    }
            ).bounds(startX + i * (tabW + gap), tabY, tabW, 18).build();
            this.botonesFiltro.add(btn);
            this.addRenderableWidget(btn);
        }

        Button cerrarBtn = Button.builder(
                Component.literal("§c§l✖ Cerrar"),
                b -> this.onClose()
        ).bounds(centerX - 55, panelY + panelH - 25, 110, 18).build();
        this.addRenderableWidget(cerrarBtn);

        actualizarBotonesFiltro();
    }

    private void actualizarBotonesFiltro() {
        for (int i = 0; i < this.botonesFiltro.size(); i++) {
            Button btn = this.botonesFiltro.get(i);
            btn.setMessage(obtenerTextoTab(i));
        }
    }

    private Component obtenerTextoTab(int nivel) {
        boolean seleccionado = (this.filtroNivel == nivel);
        if (nivel == 0) {
            return Component.literal(seleccionado ? "§6§l[Todos]" : "§7Todos");
        }
        String color = obtenerPrefijoColorNivel(nivel);
        return Component.literal(seleccionado ? color + "§l[Nivel " + nivel + "]" : "§7Nivel " + nivel);
    }

    private static String obtenerPrefijoColorNivel(int nivel) {
        return switch (nivel) {
            case 1 -> "§a";
            case 2 -> "§e";
            case 3 -> "§6";
            case 4 -> "§c";
            case 5 -> "§d";
            case 6 -> "§4";
            default -> "§f";
        };
    }

    private static int obtenerColorBordeNivel(int nivel) {
        return switch (nivel) {
            case 1 -> 0xFF44CC55;
            case 2 -> 0xFFFFCC33;
            case 3 -> 0xFFFF8822;
            case 4 -> 0xFFEE3333;
            case 5 -> 0xFFCC44FF;
            case 6 -> 0xFFAA0022;
            default -> 0xFFAAAAAA;
        };
    }

    private List<AbrirMenuEstadoPayload.MaldicionInfo> obtenerMaldicionesFiltradas() {
        String query = this.textoBusqueda == null ? "" : this.textoBusqueda.trim().toLowerCase(Locale.ROOT);
        List<AbrirMenuEstadoPayload.MaldicionInfo> res = new ArrayList<>();
        for (AbrirMenuEstadoPayload.MaldicionInfo m : this.todasLasMaldiciones) {
            if (this.filtroNivel != 0 && m.nivel() != this.filtroNivel) {
                continue;
            }
            if (!query.isEmpty()) {
                boolean matchNombre = m.nombre().toLowerCase(Locale.ROOT).contains(query);
                boolean matchDesc = m.descripcion().toLowerCase(Locale.ROOT).contains(query);
                if (!matchNombre && !matchDesc) {
                    continue;
                }
            }
            res.add(m);
        }
        return res;
    }

    private List<FilaEstado> construirFilasVisibles(List<AbrirMenuEstadoPayload.MaldicionInfo> filtradas) {
        List<FilaEstado> filas = new ArrayList<>();
        if (this.filtroNivel == 0) {
            for (int nivel = 1; nivel <= 6; nivel++) {
                boolean headerAgregado = false;
                for (AbrirMenuEstadoPayload.MaldicionInfo info : filtradas) {
                    if (info.nivel() == nivel) {
                        if (!headerAgregado) {
                            if (!filas.isEmpty()) {
                                filas.add(new FilaSeparador());
                            }
                            filas.add(new FilaHeader(nivel));
                            headerAgregado = true;
                        }
                        filas.add(new FilaMaldicion(info));
                    }
                }
            }
        } else {
            for (AbrirMenuEstadoPayload.MaldicionInfo info : filtradas) {
                filas.add(new FilaMaldicion(info));
            }
        }
        return filas;
    }

    private static int calcularAlturaTotal(List<FilaEstado> filas) {
        int total = 0;
        for (FilaEstado fila : filas) {
            total += fila.altura();
        }
        return total;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        List<FilaEstado> filas = construirFilasVisibles(obtenerMaldicionesFiltradas());
        int panelH = Math.min(280, this.height - 24);
        int listH = panelH - 86;
        int totalH = calcularAlturaTotal(filas);
        int maxScroll = Math.max(0, totalH - listH + 8);
        this.scrollOffset = Math.clamp(this.scrollOffset - scrollY * 22.0, 0.0, maxScroll);
        return true;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        int centerX = this.width / 2;
        int panelW = Math.min(440, this.width - 24);
        int panelH = Math.min(280, this.height - 24);
        int panelX = centerX - panelW / 2;
        int panelY = (this.height - panelH) / 2;

        // Sombra exterior y fondo principal con acabado oscuro/dorado
        extractor.fill(panelX - 3, panelY - 3, panelX + panelW + 3, panelY + panelH + 3, 0x88000000);
        extractor.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xEE0D0D14);
        extractor.fill(panelX + 1, panelY + 1, panelX + panelW - 1, panelY + 28, 0xCC181624);
        extractor.outline(panelX, panelY, panelW, panelH, 0xFFFFAA00);
        extractor.outline(panelX + 2, panelY + 2, panelW - 4, panelH - 4, 0x44FFAA00);

        List<AbrirMenuEstadoPayload.MaldicionInfo> filtradas = obtenerMaldicionesFiltradas();
        List<FilaEstado> filas = construirFilasVisibles(filtradas);

        // Encabezado a la izquierda y resumen de evento mundial
        String titulo = "§6§l✦ MALDICIONES ACTIVAS §7(" + filtradas.size() + "/" + this.todasLasMaldiciones.size() + ")";
        extractor.text(this.font, Component.literal(titulo), panelX + 10, panelY + 10, 0xFFFFFFFF, true);

        if (this.bloodMoonActiva || this.eclipseActivo) {
            long pulse = (System.currentTimeMillis() / 350L) % 2L;
            String evento = this.bloodMoonActiva
                    ? (pulse == 0 ? "§4§l☾ LUNA DE SANGRE ACTIVA" : "§c§l☾ LUNA DE SANGRE ACTIVA")
                    : (pulse == 0 ? "§5§l☼ ECLIPSE SOLAR ACTIVO" : "§d§l☼ ECLIPSE SOLAR ACTIVO");
            extractor.text(this.font, Component.literal(evento), panelX + 10, panelY + 20, 0xFFFFFFFF, true);
        } else {
            // Mini barra de distribución por niveles debajo del título
            int barX = panelX + 10;
            int barY = panelY + 22;
            int barTotalW = Math.max(80, panelW - 160);
            extractor.fill(barX, barY, barX + barTotalW, barY + 4, 0xFF22222C);
            int total = Math.max(1, this.todasLasMaldiciones.size());
            int curX = barX;
            for (int n = 1; n <= 6; n++) {
                if (this.conteoPorNivel[n] <= 0) continue;
                int segW = Math.max(3, (int) Math.round((this.conteoPorNivel[n] / (double) total) * barTotalW));
                if (curX + segW > barX + barTotalW) {
                    segW = (barX + barTotalW) - curX;
                }
                if (segW > 0) {
                    extractor.fill(curX, barY, curX + segW, barY + 4, obtenerColorBordeNivel(n));
                    curX += segW;
                }
            }
        }

        // Área de lista con recorte (scissor)
        int listX = panelX + 10;
        int listY = panelY + 55;
        int listW = panelW - 20;
        int listH = panelH - 86;

        extractor.fill(listX, listY, listX + listW, listY + listH, 0xAA07070B);
        extractor.outline(listX, listY, listW, listH, 0xFF2E2E3E);

        int totalH = calcularAlturaTotal(filas);
        int maxScroll = Math.max(0, totalH - listH + 8);
        this.scrollOffset = Math.clamp(this.scrollOffset, 0.0, maxScroll);

        AbrirMenuEstadoPayload.MaldicionInfo hoveredMaldicion = null;

        extractor.enableScissor(listX + 1, listY + 1, listX + listW - 1, listY + listH - 1);

        if (filas.isEmpty()) {
            String msgVacio = this.todasLasMaldiciones.isEmpty()
                    ? "§a✦ No hay maldiciones activas en el mundo actualmente."
                    : "§7No se encontraron maldiciones con ese filtro o búsqueda.";
            extractor.centeredText(
                    this.font,
                    Component.literal(msgVacio),
                    centerX,
                    listY + listH / 2 - 4,
                    0xFFAAAAAA
            );
        } else {
            int yPos = listY + 4 - (int) this.scrollOffset;
            for (int idx = 0; idx < filas.size(); idx++) {
                FilaEstado fila = filas.get(idx);
                int h = fila.altura();
                if (yPos + h >= listY && yPos <= listY + listH) {
                    if (fila instanceof FilaHeader header) {
                        int borderColor = obtenerColorBordeNivel(header.nivel());
                        String colorPrefix = obtenerPrefijoColorNivel(header.nivel());
                        int hdrX = listX + 4;
                        int hdrW = listW - 14;
                        extractor.fill(hdrX, yPos + 1, hdrX + hdrW, yPos + h - 2, 0xBB191926);
                        // Barra izquierda continua hacia abajo para conectar con la primera tarjeta del nivel
                        extractor.fill(hdrX, yPos + 1, hdrX + 4, yPos + h, borderColor);
                        extractor.text(
                                this.font,
                                Component.literal(colorPrefix + "§lNivel " + header.nivel()),
                                hdrX + 9,
                                yPos + 4,
                                0xFFFFFFFF,
                                true
                        );
                    } else if (fila instanceof FilaMaldicion item) {
                        AbrirMenuEstadoPayload.MaldicionInfo info = item.info();
                        int cardX = listX + 4;
                        int cardW = listW - 14;
                        int cardH = h - 4;
                        int borderColor = obtenerColorBordeNivel(info.nivel());
                        String colorPrefix = obtenerPrefijoColorNivel(info.nivel());

                        boolean sigueMismoNivel = (idx + 1 < filas.size())
                                && (filas.get(idx + 1) instanceof FilaMaldicion nextCard)
                                && (nextCard.info().nivel() == info.nivel());
                        boolean vieneDelMismoNivel = (idx - 1 >= 0)
                                && ((filas.get(idx - 1) instanceof FilaHeader prevHdr && prevHdr.nivel() == info.nivel())
                                || (filas.get(idx - 1) instanceof FilaMaldicion prevCard && prevCard.info().nivel() == info.nivel()));

                        boolean hovered = mouseX >= cardX && mouseX <= cardX + cardW
                                && mouseY >= Math.max(listY, yPos) && mouseY <= Math.min(listY + listH, yPos + cardH);
                        if (hovered) {
                            hoveredMaldicion = info;
                        }

                        int cardBg = hovered ? 0xEE222230 : 0xCC161620;
                        extractor.fill(cardX, yPos, cardX + cardW, yPos + cardH, cardBg);

                        // Barra izquierda continua dentro del mismo nivel; corta solo al finalizar el bloque del nivel
                        int barTop = vieneDelMismoNivel ? yPos : yPos + 1;
                        int barBottom = sigueMismoNivel ? (yPos + h) : (yPos + cardH);
                        extractor.fill(cardX, barTop, cardX + 4, barBottom, borderColor);

                        if (hovered) {
                            extractor.outline(cardX, yPos, cardW, cardH, borderColor);
                        }

                        // Título de la maldición (sin etiqueta individual de nivel)
                        extractor.text(
                                this.font,
                                Component.literal(colorPrefix + "§l" + info.nombre()),
                                cardX + 9,
                                yPos + 4,
                                0xFFFFFFFF,
                                true
                        );

                        // Descripción ajustada a 2 líneas dentro de la tarjeta
                        List<FormattedCharSequence> lines = this.font.split(Component.literal("§7" + info.descripcion()), cardW - 18);
                        int maxLines = Math.min(2, lines.size());
                        for (int l = 0; l < maxLines; l++) {
                            extractor.text(this.font, lines.get(l), cardX + 9, yPos + 15 + l * 9, 0xFFCCCCCC, false);
                        }
                    }
                }
                yPos += h;
            }
        }

        extractor.disableScissor();

        // Barra de desplazamiento lateral estilizada
        if (maxScroll > 0 && totalH > 0) {
            int trackX = listX + listW - 6;
            extractor.fill(trackX, listY + 2, trackX + 4, listY + listH - 2, 0x88111118);
            int barH = Math.max(20, (int) ((double) listH / totalH * (listH - 4)));
            int barY = listY + 2 + (int) ((this.scrollOffset / maxScroll) * (listH - 4 - barH));
            extractor.fill(trackX, barY, trackX + 4, barY + barH, 0xFFFFAA00);
        }

        super.extractRenderState(extractor, mouseX, mouseY, partialTick);

        // Tooltip flotante completo al pasar el cursor sobre cualquier tarjeta
        if (hoveredMaldicion != null) {
            int tipMaxW = Math.min(280, this.width - 20);
            String colorPrefix = obtenerPrefijoColorNivel(hoveredMaldicion.nivel());
            List<FormattedCharSequence> tipLines = new ArrayList<>();
            tipLines.addAll(this.font.split(
                    Component.literal(colorPrefix + "§l" + hoveredMaldicion.nombre()),
                    tipMaxW
            ));
            tipLines.addAll(this.font.split(
                    Component.literal("§f" + hoveredMaldicion.descripcion()),
                    tipMaxW
            ));

            if (tipLines.size() > 2) {
                int boxW = tipMaxW + 12;
                int boxH = tipLines.size() * 10 + 10;
                int boxX = Math.clamp(mouseX + 10, 6, this.width - boxW - 6);
                int boxY = Math.clamp(mouseY - boxH - 6, 6, this.height - boxH - 6);
                int borderColor = obtenerColorBordeNivel(hoveredMaldicion.nivel());

                extractor.fill(boxX - 2, boxY - 2, boxX + boxW + 2, boxY + boxH + 2, 0xAA000000);
                extractor.fill(boxX, boxY, boxX + boxW, boxY + boxH, 0xF4101018);
                extractor.outline(boxX, boxY, boxW, boxH, borderColor);
                for (int i = 0; i < tipLines.size(); i++) {
                    extractor.text(this.font, tipLines.get(i), boxX + 6, boxY + 5 + i * 10, 0xFFFFFFFF, true);
                }
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
