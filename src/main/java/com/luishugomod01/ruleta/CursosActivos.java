package com.luishugomod01.ruleta;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Gestor del estado global de maldiciones activas en el mundo.
 */
public class CursosActivos {

    // Nivel 1
    private static volatile boolean creepersEnPerla           = false;
    private static volatile boolean fuegoFatuo                = false;
    private static volatile boolean apocalipsisZombie         = false;
    private static volatile boolean wardensDeepDark           = false;
    private static volatile boolean cuevasTenebrosas          = false;
    private static volatile boolean defensaVillera            = false;

    // Nivel 2
    private static volatile boolean speedpers                 = false;
    private static volatile boolean filoMortal                = false;
    private static volatile boolean muerteEspinosa            = false;

    // Nivel 3
    private static volatile boolean errorTotemico             = false;
    private static volatile boolean artesMaritimos            = false;
    private static volatile boolean boogieWoogie              = false;

    // Nivel 4
    private static volatile boolean danoColateral             = false;
    private static volatile boolean ultraFundido              = false;
    private static volatile boolean poderEnLaMano             = false;

    // Nivel 5
    private static volatile boolean intrusosCalavericos       = false;
    private static volatile boolean spiderman                 = false;
    private static volatile boolean enderGhast                = false;
    private static volatile boolean kaBoom                    = false;
    private static volatile boolean ataqueSlimes              = false;
    private static volatile boolean gigaEndermite             = false;
    private static volatile boolean megaHoglin                = false;

    // Nivel 6
    private static volatile boolean permadeath                = false;
    private static volatile boolean apocalipsisMinecraftTotal = false;
    private static volatile boolean maldicionLunaDeSangre     = false;
    private static volatile boolean monturaAcuatica           = false;

    // Estado dinámico de la Luna de Sangre
    private static volatile boolean isBloodMoonActive         = false;
    private static volatile long ultimoDiaBloodMoon           = -1L;

    private static volatile boolean ruletaAutomatica          = false;
    private static volatile int vidasIniciales                = 3;
    private static volatile boolean mundoConfigurado          = false;

    public static void setRuletaAutomatica(boolean automatica) { ruletaAutomatica = automatica; }
    public static boolean isRuletaAutomatica() { return ruletaAutomatica; }

    public static void setVidasIniciales(int vidas) { vidasIniciales = Math.max(1, vidas); }
    public static int getVidasIniciales() { return vidasIniciales; }

    public static void setMundoConfigurado(boolean configurado) { mundoConfigurado = configurado; }
    public static boolean isMundoConfigurado() { return mundoConfigurado; }

    // --- Nivel 1 ---
    public static void activarCreepersEnPerla() { creepersEnPerla = true; }
    public static boolean isCreepersEnPerla() { return creepersEnPerla; }
    public static boolean tieneCreepersEnPerla(UUID playerId) { return creepersEnPerla; }

    public static void activarFuegoFatuo() { fuegoFatuo = true; }
    public static boolean isFuegoFatuo() { return fuegoFatuo; }
    public static boolean tieneFuegoFatuo(UUID playerId) { return fuegoFatuo; }

    public static void activarApocalipsisZombie() { apocalipsisZombie = true; }
    public static boolean isApocalipsisZombie() { return apocalipsisZombie; }
    public static boolean tieneApocalipsisZombie(UUID playerId) { return apocalipsisZombie; }

    public static void activarWardensDeepDark() { wardensDeepDark = true; }
    public static boolean isWardensDeepDark() { return wardensDeepDark; }
    public static boolean tieneWardensDeepDark(UUID playerId) { return wardensDeepDark; }

    public static void activarCuevasTenebrosas() { cuevasTenebrosas = true; }
    public static boolean isCuevasTenebrosas() { return cuevasTenebrosas; }

    public static void activarDefensaVillera() { defensaVillera = true; }
    public static boolean isDefensaVillera() { return defensaVillera; }

    // --- Nivel 2 ---
    public static void activarSpeedpers() { speedpers = true; }
    public static boolean isSpeedpers() { return speedpers; }

    public static void activarFiloMortal() { filoMortal = true; }
    public static boolean isFiloMortal() { return filoMortal; }
    public static boolean tieneFiloMortal(UUID playerId) { return filoMortal; }

    public static void activarMuerteEspinosa() { muerteEspinosa = true; }
    public static boolean isMuerteEspinosa() { return muerteEspinosa; }

    // --- Nivel 3 ---
    public static void activarErrorTotemico() { errorTotemico = true; }
    public static boolean isErrorTotemico() { return errorTotemico; }
    public static boolean tieneErrorTotemico(UUID playerId) { return errorTotemico; }

    public static void activarArtesMaritimos() { artesMaritimos = true; }
    public static boolean isArtesMaritimos() { return artesMaritimos; }

    public static void activarBoogieWoogie() { boogieWoogie = true; }
    public static boolean isBoogieWoogie() { return boogieWoogie; }

    // --- Nivel 4 ---
    public static void activarDanoColateral() { danoColateral = true; }
    public static boolean isDanoColateral() { return danoColateral; }
    public static boolean tieneDanoColateral(UUID playerId) { return danoColateral; }

    public static void activarUltraFundido() { ultraFundido = true; }
    public static boolean isUltraFundido() { return ultraFundido; }
    public static boolean tieneUltraFundido(UUID playerId) { return ultraFundido; }

    public static void activarPoderEnLaMano() { poderEnLaMano = true; }
    public static boolean isPoderEnLaMano() { return poderEnLaMano; }

    // --- Nivel 5 ---
    public static void activarIntrusosCalavericos() { intrusosCalavericos = true; }
    public static boolean isIntrusosCalavericos() { return intrusosCalavericos; }
    public static boolean tieneIntrusosCalavericos(UUID playerId) { return intrusosCalavericos; }

    public static void activarSpiderman() { spiderman = true; }
    public static boolean isSpiderman() { return spiderman; }
    public static boolean tieneSpiderman(UUID playerId) { return spiderman; }

    public static void activarEnderGhast() { enderGhast = true; }
    public static boolean isEnderGhast() { return enderGhast; }

    public static void activarKaBoom() { kaBoom = true; }
    public static boolean isKaBoom() { return kaBoom; }

    public static void activarAtaqueSlimes() { ataqueSlimes = true; }
    public static boolean isAtaqueSlimes() { return ataqueSlimes; }

    public static void activarGigaEndermite() { gigaEndermite = true; }
    public static boolean isGigaEndermite() { return gigaEndermite; }

    public static void activarMegaHoglin() { megaHoglin = true; }
    public static boolean isMegaHoglin() { return megaHoglin; }

    // --- Nivel 6 ---
    public static void activarPermadeath() { permadeath = true; }
    public static boolean isPermadeath() { return permadeath; }
    public static boolean tienePermadeath(UUID playerId) { return permadeath; }

    public static void activarApocalipsisMinecraftTotal() { apocalipsisMinecraftTotal = true; }
    public static boolean isApocalipsisMinecraftTotal() { return apocalipsisMinecraftTotal; }

    public static void activarLunaDeSangre() { maldicionLunaDeSangre = true; }
    public static boolean isMaldicionLunaDeSangre() { return maldicionLunaDeSangre; }

    public static void activarMonturaAcuatica() { monturaAcuatica = true; }
    public static boolean isMonturaAcuatica() { return monturaAcuatica; }

    public static void setBloodMoonActive(boolean active) { isBloodMoonActive = active; }
    public static boolean isBloodMoonActive() { return isBloodMoonActive; }

    public static void setUltimoDiaBloodMoon(long dia) { ultimoDiaBloodMoon = dia; }
    public static long getUltimoDiaBloodMoon() { return ultimoDiaBloodMoon; }

    public static boolean hayMaldicionesActivas() {
        return fuegoFatuo || creepersEnPerla || speedpers || wardensDeepDark
                || apocalipsisZombie || cuevasTenebrosas || defensaVillera
                || filoMortal || muerteEspinosa || errorTotemico || artesMaritimos
                || boogieWoogie || danoColateral || ultraFundido || poderEnLaMano
                || intrusosCalavericos || spiderman || enderGhast || kaBoom
                || ataqueSlimes || gigaEndermite || megaHoglin || permadeath
                || apocalipsisMinecraftTotal || maldicionLunaDeSangre || monturaAcuatica;
    }

    public static List<String> obtenerIdsActivas() {
        List<String> ids = new ArrayList<>();
        if (creepersEnPerla) ids.add("creepersperla");
        if (fuegoFatuo) ids.add("fuegofatuo");
        if (apocalipsisZombie) ids.add("apocalipsis");
        if (wardensDeepDark) ids.add("wardens");
        if (cuevasTenebrosas) ids.add("cuevastenebrosas");
        if (defensaVillera) ids.add("defensavillera");
        if (speedpers) ids.add("speedpers");
        if (filoMortal) ids.add("filomortal");
        if (muerteEspinosa) ids.add("muerteespinosa");
        if (errorTotemico) ids.add("errortotemico");
        if (artesMaritimos) ids.add("artesmaritimos");
        if (boogieWoogie) ids.add("boogiewoogie");
        if (danoColateral) ids.add("danocolateral");
        if (ultraFundido) ids.add("ultrafundido");
        if (poderEnLaMano) ids.add("poderenlamano");
        if (intrusosCalavericos) ids.add("intrusoscalavericos");
        if (spiderman) ids.add("spiderman");
        if (enderGhast) ids.add("enderghast");
        if (kaBoom) ids.add("kaboom");
        if (ataqueSlimes) ids.add("ataqueslimes");
        if (gigaEndermite) ids.add("gigaendermite");
        if (megaHoglin) ids.add("megahoglin");
        if (permadeath) ids.add("permadeath");
        if (apocalipsisMinecraftTotal) ids.add("apocalipsis6");
        if (maldicionLunaDeSangre) ids.add("lunasangre");
        if (monturaAcuatica) ids.add("monturaacuatica");
        return ids;
    }

    public static void activarPorId(String id) {
        switch (id.toLowerCase()) {
            case "creepersperla" -> activarCreepersEnPerla();
            case "fuegofatuo" -> activarFuegoFatuo();
            case "apocalipsis" -> activarApocalipsisZombie();
            case "wardens" -> activarWardensDeepDark();
            case "cuevastenebrosas" -> activarCuevasTenebrosas();
            case "defensavillera" -> activarDefensaVillera();
            case "speedpers" -> activarSpeedpers();
            case "filomortal" -> activarFiloMortal();
            case "muerteespinosa" -> activarMuerteEspinosa();
            case "errortotemico" -> activarErrorTotemico();
            case "artesmaritimos" -> activarArtesMaritimos();
            case "boogiewoogie" -> activarBoogieWoogie();
            case "danocolateral" -> activarDanoColateral();
            case "ultrafundido" -> activarUltraFundido();
            case "poderenlamano" -> activarPoderEnLaMano();
            case "intrusoscalavericos" -> activarIntrusosCalavericos();
            case "spiderman" -> activarSpiderman();
            case "enderghast" -> activarEnderGhast();
            case "kaboom" -> activarKaBoom();
            case "ataqueslimes" -> activarAtaqueSlimes();
            case "gigaendermite" -> activarGigaEndermite();
            case "megahoglin" -> activarMegaHoglin();
            case "permadeath" -> activarPermadeath();
            case "apocalipsis6", "apocalipsistotal" -> activarApocalipsisMinecraftTotal();
            case "lunasangre", "bloodmoon" -> activarLunaDeSangre();
            case "monturaacuatica" -> activarMonturaAcuatica();
        }
    }

    public static List<Component> obtenerMensajesMaldicionesActivas() {
        List<Component> lista = new ArrayList<>();
        if (fuegoFatuo) {
            lista.add(Component.literal("  §c☠ FUEGO FATUO:§r Necesitas armadura completa en el Nether o arderás hasta morir."));
        }
        if (creepersEnPerla) {
            lista.add(Component.literal("  §5✦ CREEPERS EN PERLA:§r 5% de probabilidad de que aparezca un Creeper al lanzar Ender Pearl."));
        }
        if (apocalipsisZombie) {
            lista.add(Component.literal("  §2☣ ¿APOCALIPSIS MINECRAFT?:§r 50% de los zombies escalan paredes como arañas y son inmunes al sol."));
        }
        if (wardensDeepDark) {
            lista.add(Component.literal("  §1☠ WARDENS:§r Los Wardens ahora pueden aparecer naturalmente en el Deep Dark."));
        }
        if (cuevasTenebrosas) {
            lista.add(Component.literal("  §8🌑 CUEVAS TENEBROSAS:§r Recibes Oscuridad por debajo de Y=0 (disminuye con un farol en la mano secundaria)."));
        }
        if (defensaVillera) {
            lista.add(Component.literal("  §7🛡 DEFENSA VILLERA:§r Los Gólems de Hierro ahora son hostiles contra todos los jugadores."));
        }
        if (speedpers) {
            lista.add(Component.literal("  §e⚡ SPEEDPERS:§r Todos los Creepers tienen efecto permanente de Velocidad II."));
        }
        if (filoMortal) {
            lista.add(Component.literal("  §c⚔ FILO MORTAL:§r Todos los zombies están armados con una espada de hierro con Filo III."));
        }
        if (muerteEspinosa) {
            lista.add(Component.literal("  §2🌵 MUERTE ESPINOSA:§r Todos los cactus infligen 1x10^9 de daño al contacto."));
        }
        if (errorTotemico) {
            lista.add(Component.literal("  §4✝ ERROR TOTÉMICO:§r Los tótems de la inmortalidad tienen un 2% de probabilidad de fallar."));
        }
        if (artesMaritimos) {
            lista.add(Component.literal("  §3🔱 ARTES MARÍTIMOS:§r Todos los Zombies Ahogados aparecen armados con Tridentes."));
        }
        if (boogieWoogie) {
            lista.add(Component.literal("  §d🏹 BOOGIE WOOGIE:§r Los esqueletos intercambian posición contigo cuando su flecha te impacta."));
        }
        if (danoColateral) {
            lista.add(Component.literal("  §5💥 DAÑO COLATERAL:§r Las Ender Pearls hacen x3 su daño e ignoran armadura."));
        }
        if (ultraFundido) {
            lista.add(Component.literal("  §6🔥 ULTRA FUNDIDO:§r El fuego hace x10 su daño."));
        }
        if (poderEnLaMano) {
            lista.add(Component.literal("  §6🪓 PODER EN LA MANO:§r Los Piglins Brutos empuñan un hacha de diamante con Filo V."));
        }
        if (intrusosCalavericos) {
            lista.add(Component.literal("  §8💀 INTRUSOS CALAVÉRICOS:§r Wither Skeletons aparecen de noche en el Overworld con Diamante Prot IV, Espada Filo V, Velocidad I y Fuerza II."));
        }
        if (spiderman) {
            lista.add(Component.literal("  §9🕷 ¿SPIDERMAN?:§r Todas las arañas tienen Fuerza II, Velocidad II, Invisibilidad, Resistencia II y Regeneración II."));
        }
        if (enderGhast) {
            lista.add(Component.literal("  §d👻 ENDER GHAST:§r Los Ghasts son mucho más agresivos y sus proyectiles hacen x3 de daño explosivo."));
        }
        if (kaBoom) {
            lista.add(Component.literal("  §c💣 KA-BOOM:§r Los Shulkers explotan 3 segundos después de morir como una TNT."));
        }
        if (ataqueSlimes) {
            lista.add(Component.literal("  §a🟢 ATAQUE A LOS SLIMES:§r 5% de probabilidad de que aparezca un Slime Gigante en pantanos con x10 de daño."));
        }
        if (gigaEndermite) {
            lista.add(Component.literal("  §5👾 GIGA-ENDERMITE:§r 5% de probabilidad en el End de que aparezca un Endermite Gigante con 250 HP y x2 de daño."));
        }
        if (megaHoglin) {
            lista.add(Component.literal("  §4🐗 MEGA HOGLIN:§r 5% de probabilidad en el Nether de que aparezca un Hoglin Gigante con 300 HP, x2 de daño y Lentitud II."));
        }
        if (permadeath) {
            lista.add(Component.literal("  §4§l☠ PERMADEATH:§r Se ha eliminado la regeneración pasiva de vida."));
        }
        if (apocalipsisMinecraftTotal) {
            lista.add(Component.literal("  §4§l☣ ¡APOCALIPSIS MINECRAFT !!!!:§r Los zombies se apilan en torres, huelen sangre y acuden al ruido al picar."));
        }
        if (maldicionLunaDeSangre) {
            lista.add(Component.literal("  §4§l☾ LUNA DE SANGRE:§r Cada noche hay 5% de probabilidad de Luna de Sangre: sin camas y hordas masivas."));
        }
        if (monturaAcuatica) {
            lista.add(Component.literal("  §3§l🐚 MONTURA ACUÁTICA:§r Los Zombies Ahogados van montados en Nautilos y sus tridentes tienen Empalamiento V."));
        }
        return lista;
    }

    public static void limpiar() {
        creepersEnPerla = false;
        fuegoFatuo = false;
        apocalipsisZombie = false;
        wardensDeepDark = false;
        cuevasTenebrosas = false;
        defensaVillera = false;
        speedpers = false;
        filoMortal = false;
        muerteEspinosa = false;
        errorTotemico = false;
        artesMaritimos = false;
        boogieWoogie = false;
        danoColateral = false;
        ultraFundido = false;
        poderEnLaMano = false;
        intrusosCalavericos = false;
        spiderman = false;
        enderGhast = false;
        kaBoom = false;
        ataqueSlimes = false;
        gigaEndermite = false;
        megaHoglin = false;
        permadeath = false;
        apocalipsisMinecraftTotal = false;
        maldicionLunaDeSangre = false;
        monturaAcuatica = false;
        isBloodMoonActive = false;
        ultimoDiaBloodMoon = -1L;
        ruletaAutomatica = false;
        vidasIniciales = 3;
        mundoConfigurado = false;
    }
}
