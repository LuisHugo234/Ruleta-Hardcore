package com.luishugomod01.client.gui;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.network.AccionAdminPayload;
import com.luishugomod01.network.GuardarConfigAdminPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Panel de Control Administrativo de PermaRuleta (/ruleta config):
 * - Pestaña 1 (Configuración): Presets rápidos, Modo de Giro con descripción dinámica,
 *   Vidas Iniciales con vista previa real de corazones Hardcore, opción de aplicar a jugadores
 *   conectados, y botones Guardar / Cancelar.
 * - Pestaña 2 (Control Rápido): Acciones en 1 clic para girar la ruleta, abrir Maldiciones Activas,
 *   e iniciar/detener Blood Moon y Eclipse Solar.
 */
public class AdminConfigScreen extends Screen {

    private static final Identifier ICONO_RULETA =
            Ruleta_Hardcore.id("textures/gui/ruleta_disco.png");
    private static final Identifier SPRITE_HEART_CONTAINER =
            Identifier.withDefaultNamespace("hud/heart/container_hardcore");
    private static final Identifier SPRITE_HEART_FULL =
            Identifier.withDefaultNamespace("hud/heart/hardcore_full");

    private static final int PANEL_W = 348;
    private static final int PANEL_H = 258;

    private enum Tab {
        CONFIG,
        CONTROL
    }

    private Tab activeTab = Tab.CONFIG;

    private boolean ruletaAutomatica;
    private int vidasIniciales;
    private final boolean mundoConfigurado;
    private boolean aplicarAJugadoresActuales;
    private boolean bloodMoonActiva;
    private boolean eclipseActivo;

    private Button toggleRuletaButton;
    private Button menosVidasButton;
    private Button masVidasButton;
    private Button toggleAplicarActualesButton;
    private Button bloodMoonButton;
    private Button eclipseButton;

    public AdminConfigScreen(
            boolean ruletaAutomaticaInicial,
            int vidasInicialesActuales,
            boolean mundoConfigurado,
            boolean bloodMoonActiva,
            boolean eclipseActivo
    ) {
        super(Component.literal("Panel de Control - PermaRuleta"));
        this.ruletaAutomatica = ruletaAutomaticaInicial;
        this.vidasIniciales = Math.max(1, Math.min(20, vidasInicialesActuales));
        this.mundoConfigurado = mundoConfigurado;
        this.aplicarAJugadoresActuales = !mundoConfigurado;
        this.bloodMoonActiva = bloodMoonActiva;
        this.eclipseActivo = eclipseActivo;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int panelX = centerX - PANEL_W / 2;
        int panelY = this.height / 2 - PANEL_H / 2;

        // Pestañas superiores (Configuración | Control Rápido)
        int tabY = panelY + 38;
        int tabW = 154;
        Button tabConfigBtn = Button.builder(
                Component.literal(this.activeTab == Tab.CONFIG ? "§6§l⚙ Configuración" : "§7⚙ Configuración"),
                btn -> {
                    if (this.activeTab != Tab.CONFIG) {
                        this.activeTab = Tab.CONFIG;
                        this.rebuildWidgets();
                    }
                }
        ).bounds(panelX + 16, tabY, tabW, 18).build();
        tabConfigBtn.active = (this.activeTab != Tab.CONFIG);
        this.addRenderableWidget(tabConfigBtn);

        Button tabControlBtn = Button.builder(
                Component.literal(this.activeTab == Tab.CONTROL ? "§6§l⚡ Control Rápido" : "§7⚡ Control Rápido"),
                btn -> {
                    if (this.activeTab != Tab.CONTROL) {
                        this.activeTab = Tab.CONTROL;
                        this.rebuildWidgets();
                    }
                }
        ).bounds(panelX + PANEL_W - 16 - tabW, tabY, tabW, 18).build();
        tabControlBtn.active = (this.activeTab != Tab.CONTROL);
        this.addRenderableWidget(tabControlBtn);

        if (this.activeTab == Tab.CONFIG) {
            initConfigTab(centerX, panelX, panelY);
        } else {
            initControlTab(centerX, panelX, panelY);
        }
    }

    private void initConfigTab(int centerX, int panelX, int panelY) {
        int contentLeft = panelX + 18;
        int contentW = PANEL_W - 36;

        // 1. Presets rápidos en 1 clic
        int presetY = panelY + 74;
        int presetW = (contentW - 12) / 3;
        this.addRenderableWidget(Button.builder(
                Component.literal("§aEstándar §7(3♥ Auto)"),
                btn -> aplicarPreset(true, 3)
        ).bounds(contentLeft, presetY, presetW, 18).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("§cExtremo §7(1♥ Auto)"),
                btn -> aplicarPreset(true, 1)
        ).bounds(contentLeft + presetW + 6, presetY, presetW, 18).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("§eSerie §7(5♥ Manual)"),
                btn -> aplicarPreset(false, 5)
        ).bounds(contentLeft + (presetW + 6) * 2, presetY, presetW, 18).build());

        // 2. Modo de Giro
        int modeY = panelY + 108;
        this.toggleRuletaButton = Button.builder(
                obtenerTextoModoRuleta(),
                btn -> {
                    this.ruletaAutomatica = !this.ruletaAutomatica;
                    btn.setMessage(obtenerTextoModoRuleta());
                }
        ).bounds(contentLeft + 8, modeY, contentW - 16, 20).build();
        this.addRenderableWidget(this.toggleRuletaButton);

        // 3. Vidas Iniciales (- / +) + vista previa de corazones en medio
        int livesRowY = panelY + 162;
        this.menosVidasButton = Button.builder(
                Component.literal("§c§l-"),
                btn -> {
                    if (this.vidasIniciales > 1) {
                        this.vidasIniciales--;
                        actualizarBotonesVidas();
                    }
                }
        ).bounds(contentLeft + 10, livesRowY, 26, 20).build();
        this.addRenderableWidget(this.menosVidasButton);

        this.masVidasButton = Button.builder(
                Component.literal("§a§l+"),
                btn -> {
                    if (this.vidasIniciales < 20) {
                        this.vidasIniciales++;
                        actualizarBotonesVidas();
                    }
                }
        ).bounds(contentLeft + contentW - 36, livesRowY, 26, 20).build();
        this.addRenderableWidget(this.masVidasButton);

        // Toggle opcional para aplicar también a jugadores conectados (visible cuando el mundo ya estaba configurado)
        if (this.mundoConfigurado) {
            this.toggleAplicarActualesButton = Button.builder(
                    obtenerTextoAplicarActuales(),
                    btn -> {
                        this.aplicarAJugadoresActuales = !this.aplicarAJugadoresActuales;
                        btn.setMessage(obtenerTextoAplicarActuales());
                    }
            ).bounds(contentLeft + 8, panelY + 190, contentW - 16, 18).build();
            this.addRenderableWidget(this.toggleAplicarActualesButton);
        } else {
            this.toggleAplicarActualesButton = null;
        }

        // 4. Pie: Guardar + Cancelar
        int footerY = panelY + PANEL_H - 30;
        int saveW = 196;
        int cancelW = contentW - saveW - 8;

        String textoGuardar = this.mundoConfigurado ? "§a§lGuardar Cambios" : "§a§lGuardar y Comenzar";
        this.addRenderableWidget(Button.builder(
                Component.literal(textoGuardar),
                btn -> {
                    ClientPlayNetworking.send(new GuardarConfigAdminPayload(
                            this.ruletaAutomatica,
                            this.vidasIniciales,
                            this.aplicarAJugadoresActuales
                    ));
                    this.onClose();
                }
        ).bounds(contentLeft, footerY, saveW, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("§7Cancelar"),
                btn -> this.onClose()
        ).bounds(contentLeft + saveW + 8, footerY, cancelW, 20).build());

        actualizarBotonesVidas();
    }

    private void initControlTab(int centerX, int panelX, int panelY) {
        int contentLeft = panelX + 22;
        int contentW = PANEL_W - 44;

        // Sección Ruleta
        this.addRenderableWidget(Button.builder(
                Component.literal("§6§l✦ Girar Ruleta Ahora"),
                btn -> {
                    ClientPlayNetworking.send(new AccionAdminPayload("girar"));
                    this.onClose();
                }
        ).bounds(contentLeft, panelY + 84, contentW, 22).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("§eVer Maldiciones Activas"),
                btn -> {
                    ClientPlayNetworking.send(new AccionAdminPayload("ver_estado"));
                    this.onClose();
                }
        ).bounds(contentLeft, panelY + 112, contentW, 20).build());

        // Sección Eventos del Mundo
        this.bloodMoonButton = Button.builder(
                obtenerTextoBloodMoon(),
                btn -> {
                    ClientPlayNetworking.send(new AccionAdminPayload("bloodmoon_toggle"));
                    this.bloodMoonActiva = !this.bloodMoonActiva;
                    btn.setMessage(obtenerTextoBloodMoon());
                }
        ).bounds(contentLeft, panelY + 158, contentW, 22).build();
        this.addRenderableWidget(this.bloodMoonButton);

        this.eclipseButton = Button.builder(
                obtenerTextoEclipse(),
                btn -> {
                    ClientPlayNetworking.send(new AccionAdminPayload("eclipse_toggle"));
                    this.eclipseActivo = !this.eclipseActivo;
                    btn.setMessage(obtenerTextoEclipse());
                }
        ).bounds(contentLeft, panelY + 186, contentW, 22).build();
        this.addRenderableWidget(this.eclipseButton);

        // Botón Cerrar
        this.addRenderableWidget(Button.builder(
                Component.literal("§7Cerrar"),
                btn -> this.onClose()
        ).bounds(centerX - 60, panelY + PANEL_H - 30, 120, 20).build());
    }

    private void aplicarPreset(boolean auto, int vidas) {
        this.ruletaAutomatica = auto;
        this.vidasIniciales = vidas;
        if (this.toggleRuletaButton != null) {
            this.toggleRuletaButton.setMessage(obtenerTextoModoRuleta());
        }
        actualizarBotonesVidas();
    }

    private void actualizarBotonesVidas() {
        if (this.menosVidasButton != null) {
            this.menosVidasButton.active = this.vidasIniciales > 1;
        }
        if (this.masVidasButton != null) {
            this.masVidasButton.active = this.vidasIniciales < 20;
        }
    }

    private Component obtenerTextoModoRuleta() {
        return Component.literal("Modo de Giro: " + (this.ruletaAutomatica ? "§a§lAUTOMÁTICO" : "§e§lMANUAL"));
    }

    private Component obtenerTextoAplicarActuales() {
        return Component.literal("Aplicar vidas a jugadores conectados: "
                + (this.aplicarAJugadoresActuales ? "§a§lSÍ" : "§cNO"));
    }

    private Component obtenerTextoBloodMoon() {
        return this.bloodMoonActiva
                ? Component.literal("§c☾ Luna de Sangre: §4§lACTIVA §7(Clic para Detener)")
                : Component.literal("§4☾ Luna de Sangre: §7Inactiva §a(Clic para Iniciar)");
    }

    private Component obtenerTextoEclipse() {
        return this.eclipseActivo
                ? Component.literal("§6☀ Eclipse Solar: §e§lACTIVO §7(Clic para Detener)")
                : Component.literal("§6☀ Eclipse Solar: §7Inactivo §a(Clic para Iniciar)");
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        int centerX = this.width / 2;
        int panelX = centerX - PANEL_W / 2;
        int panelY = this.height / 2 - PANEL_H / 2;

        // Fondo oscuro degradado con borde dorado elegante
        extractor.fillGradient(panelX, panelY, panelX + PANEL_W, panelY + PANEL_H, 0xE611131C, 0xEE08090F);
        extractor.outline(panelX, panelY, PANEL_W, PANEL_H, 0xFFD99B26);
        extractor.outline(panelX + 1, panelY + 1, PANEL_W - 2, PANEL_H - 2, 0x44FFD466);

        // Barra superior de cabecera
        extractor.fillGradient(panelX + 2, panelY + 2, panelX + PANEL_W - 2, panelY + 34, 0xCC1C1610, 0x44100E14);
        extractor.fill(panelX + 12, panelY + 34, panelX + PANEL_W - 12, panelY + 35, 0x88D99B26);

        // Mini-ícono de la ruleta en la cabecera
        extractor.blit(
                RenderPipelines.GUI_TEXTURED,
                ICONO_RULETA,
                panelX + 14, panelY + 6,
                0.0f, 0.0f,
                22, 22,
                22, 22
        );

        // Título y subtítulo de estado del mundo
        extractor.text(this.font, Component.literal("§6§lPANEL DE CONTROL · PERMARULETA"), panelX + 42, panelY + 8, 0xFFFFFFFF, true);
        String estadoMundo = this.mundoConfigurado
                ? "§a● Mundo Activo §7— Ajusta reglas o controla eventos"
                : "§e● Configuración Inicial §7— Elige las reglas de la partida";
        extractor.text(this.font, Component.literal(estadoMundo), panelX + 42, panelY + 20, 0xFFCCCCCC, false);

        // Sub-tarjetas decorativas según la pestaña activa
        if (this.activeTab == Tab.CONFIG) {
            drawSectionCard(extractor, panelX + 14, panelY + 61, PANEL_W - 28, 36, "§ePresets Rápidos");
            drawSectionCard(extractor, panelX + 14, panelY + 101, PANEL_W - 28, 43, null);
            int livesCardH = this.mundoConfigurado ? 64 : 42;
            drawSectionCard(extractor, panelX + 14, panelY + 148, PANEL_W - 28, livesCardH, "§cVidas Iniciales por Jugador");
        } else {
            drawSectionCard(extractor, panelX + 14, panelY + 68, PANEL_W - 28, 70, "§6Control de Ruleta");
            drawSectionCard(extractor, panelX + 14, panelY + 144, PANEL_W - 28, 70, "§dEventos del Mundo");
        }

        // Renderizar botones encima de las tarjetas
        super.extractRenderState(extractor, mouseX, mouseY, partialTick);

        // Textos dinámicos y vista previa de corazones Hardcore
        if (this.activeTab == Tab.CONFIG) {
            String descModo = this.ruletaAutomatica
                    ? "§7La ruleta girará automáticamente 6s después de que muera un jugador."
                    : "§7Solo girará cuando un administrador use §f/ruleta girar§7.";
            extractor.centeredText(this.font, Component.literal(descModo), centerX, panelY + 132, 0xFFBBBBBB);

            dibujarVistaPreviaCorazones(extractor, centerX, panelY + 167);
        }
    }

    private void drawSectionCard(GuiGraphicsExtractor extractor, int x, int y, int w, int h, String label) {
        extractor.fill(x, y, x + w, y + h, 0x66161A26);
        extractor.outline(x, y, w, h, 0x554A556E);
        if (label != null) {
            extractor.text(this.font, Component.literal(label), x + 6, y + 3, 0xFFFFFFFF, false);
        }
    }

    private void dibujarVistaPreviaCorazones(GuiGraphicsExtractor extractor, int centerX, int y) {
        if (this.vidasIniciales <= 10) {
            int heartSize = 11;
            int gap = 2;
            int totalW = this.vidasIniciales * (heartSize + gap) - gap;
            int startX = centerX - totalW / 2;
            for (int i = 0; i < this.vidasIniciales; i++) {
                int hx = startX + i * (heartSize + gap);
                extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_HEART_CONTAINER, hx, y, heartSize, heartSize);
                extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_HEART_FULL, hx, y, heartSize, heartSize);
            }
        } else {
            // Para 11..20 vidas: 10 corazones compactos + etiqueta "+N" o corazón grande con número
            int heartSize = 13;
            Component label = Component.literal("§f§l× " + this.vidasIniciales + " §7vidas");
            int textW = this.font.width(label);
            int totalW = heartSize + 5 + textW;
            int startX = centerX - totalW / 2;
            extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_HEART_CONTAINER, startX, y - 1, heartSize, heartSize);
            extractor.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE_HEART_FULL, startX, y - 1, heartSize, heartSize);
            extractor.text(this.font, label, startX + heartSize + 5, y + 2, 0xFFFFFFFF, true);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
