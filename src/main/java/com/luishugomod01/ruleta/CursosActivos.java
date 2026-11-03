package com.luishugomod01.ruleta;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Gestor del estado global de maldiciones activas en el mundo.
 */
public class CursosActivos {

    private static volatile boolean creepersEnPerla           = false;
    private static volatile boolean fuegoFatuo                = false;
    private static volatile boolean speedpers                 = false;
    private static volatile boolean wardensDeepDark            = false;
    private static volatile boolean apocalipsisZombie          = false;
    private static volatile boolean filoMortal                 = false;
    private static volatile boolean errorTotemico              = false;
    private static volatile boolean danoColateral              = false;
    private static volatile boolean ultraFundido               = false;
    private static volatile boolean intrusosCalavericos         = false;
    private static volatile boolean spiderman                  = false;
    private static volatile boolean permadeath                 = false;
    private static volatile boolean apocalipsisMinecraftTotal  = false;

    // Creepers en Perla
    public static void activarCreepersEnPerla() { creepersEnPerla = true; }
    public static boolean isCreepersEnPerla() { return creepersEnPerla; }
    public static boolean tieneCreepersEnPerla(UUID playerId) { return creepersEnPerla; }

    // Fuego Fatuo
    public static void activarFuegoFatuo() { fuegoFatuo = true; }
    public static boolean isFuegoFatuo() { return fuegoFatuo; }
    public static boolean tieneFuegoFatuo(UUID playerId) { return fuegoFatuo; }

    // Speedpers
    public static void activarSpeedpers() { speedpers = true; }
    public static boolean isSpeedpers() { return speedpers; }

    // Wardens en Deep Dark
    public static void activarWardensDeepDark() { wardensDeepDark = true; }
    public static boolean isWardensDeepDark() { return wardensDeepDark; }
    public static boolean tieneWardensDeepDark(UUID playerId) { return wardensDeepDark; }

    // ¿Apocalipsis Minecraft? (Nivel 1)
    public static void activarApocalipsisZombie() { apocalipsisZombie = true; }
    public static boolean isApocalipsisZombie() { return apocalipsisZombie; }
    public static boolean tieneApocalipsisZombie(UUID playerId) { return apocalipsisZombie; }

    // Filo Mortal
    public static void activarFiloMortal() { filoMortal = true; }
    public static boolean isFiloMortal() { return filoMortal; }
    public static boolean tieneFiloMortal(UUID playerId) { return filoMortal; }

    // Error Totémico
    public static void activarErrorTotemico() { errorTotemico = true; }
    public static boolean isErrorTotemico() { return errorTotemico; }
    public static boolean tieneErrorTotemico(UUID playerId) { return errorTotemico; }

    // Daño Colateral
    public static void activarDanoColateral() { danoColateral = true; }
    public static boolean isDanoColateral() { return danoColateral; }
    public static boolean tieneDanoColateral(UUID playerId) { return danoColateral; }

    // Ultra Fundido
    public static void activarUltraFundido() { ultraFundido = true; }
    public static boolean isUltraFundido() { return ultraFundido; }
    public static boolean tieneUltraFundido(UUID playerId) { return ultraFundido; }

    // Intrusos Calavéricos
    public static void activarIntrusosCalavericos() { intrusosCalavericos = true; }
    public static boolean isIntrusosCalavericos() { return intrusosCalavericos; }
    public static boolean tieneIntrusosCalavericos(UUID playerId) { return intrusosCalavericos; }

    // ¿Spiderman?
    public static void activarSpiderman() { spiderman = true; }
    public static boolean isSpiderman() { return spiderman; }
    public static boolean tieneSpiderman(UUID playerId) { return spiderman; }

    // PERMADEATH
    public static void activarPermadeath() { permadeath = true; }
    public static boolean isPermadeath() { return permadeath; }
    public static boolean tienePermadeath(UUID playerId) { return permadeath; }

    // ¡Apocalipsis Minecraft !!!! (Nivel 6)
    public static void activarApocalipsisMinecraftTotal() { apocalipsisMinecraftTotal = true; }
    public static boolean isApocalipsisMinecraftTotal() { return apocalipsisMinecraftTotal; }

    public static boolean hayMaldicionesActivas() {
        return fuegoFatuo || creepersEnPerla || speedpers || wardensDeepDark
                || apocalipsisZombie || filoMortal || errorTotemico || danoColateral
                || ultraFundido || intrusosCalavericos || spiderman || permadeath
                || apocalipsisMinecraftTotal;
    }

    public static List<String> obtenerIdsActivas() {
        List<String> ids = new ArrayList<>();
        if (creepersEnPerla) ids.add("creepersperla");
        if (fuegoFatuo) ids.add("fuegofatuo");
        if (speedpers) ids.add("speedpers");
        if (wardensDeepDark) ids.add("wardens");
        if (apocalipsisZombie) ids.add("apocalipsis");
        if (filoMortal) ids.add("filomortal");
        if (errorTotemico) ids.add("errortotemico");
        if (danoColateral) ids.add("danocolateral");
        if (ultraFundido) ids.add("ultrafundido");
        if (intrusosCalavericos) ids.add("intrusoscalavericos");
        if (spiderman) ids.add("spiderman");
        if (permadeath) ids.add("permadeath");
        if (apocalipsisMinecraftTotal) ids.add("apocalipsis6");
        return ids;
    }

    public static void activarPorId(String id) {
        switch (id.toLowerCase()) {
            case "creepersperla" -> activarCreepersEnPerla();
            case "fuegofatuo" -> activarFuegoFatuo();
            case "speedpers" -> activarSpeedpers();
            case "wardens" -> activarWardensDeepDark();
            case "apocalipsis" -> activarApocalipsisZombie();
            case "filomortal" -> activarFiloMortal();
            case "errortotemico" -> activarErrorTotemico();
            case "danocolateral" -> activarDanoColateral();
            case "ultrafundido" -> activarUltraFundido();
            case "intrusoscalavericos" -> activarIntrusosCalavericos();
            case "spiderman" -> activarSpiderman();
            case "permadeath" -> activarPermadeath();
            case "apocalipsis6", "apocalipsistotal" -> activarApocalipsisMinecraftTotal();
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
        if (speedpers) {
            lista.add(Component.literal("  §e⚡ SPEEDPERS:§r Todos los Creepers tienen efecto permanente de Velocidad II."));
        }
        if (wardensDeepDark) {
            lista.add(Component.literal("  §1☠ WARDENS:§r Los Wardens ahora pueden aparecer naturalmente en el Deep Dark."));
        }
        if (apocalipsisZombie) {
            lista.add(Component.literal("  §2☣ ¿APOCALIPSIS MINECRAFT?:§r 50% de los zombies escalan paredes como arañas y son inmunes al sol."));
        }
        if (filoMortal) {
            lista.add(Component.literal("  §c⚔ FILO MORTAL:§r Todos los zombies están armados con una espada de hierro con Filo III."));
        }
        if (errorTotemico) {
            lista.add(Component.literal("  §4✝ ERROR TOTÉMICO:§r Los tótems de la inmortalidad tienen un 2% de probabilidad de fallar."));
        }
        if (danoColateral) {
            lista.add(Component.literal("  §5💥 DAÑO COLATERAL:§r Las Ender Pearls hacen x5 su daño e ignoran armadura."));
        }
        if (ultraFundido) {
            lista.add(Component.literal("  §6🔥 ULTRA FUNDIDO:§r El fuego hace x10 su daño."));
        }
        if (intrusosCalavericos) {
            lista.add(Component.literal("  §8💀 INTRUSOS CALAVÉRICOS:§r Wither Skeletons aparecen de noche en el Overworld con Diamante Prot IV, Espada Filo V, Velocidad I y Fuerza II."));
        }
        if (spiderman) {
            lista.add(Component.literal("  §9🕷 ¿SPIDERMAN?:§r Todas las arañas tienen Fuerza II, Velocidad II, Invisibilidad, Resistencia II y Regeneración II."));
        }
        if (permadeath) {
            lista.add(Component.literal("  §4§l☠ PERMADEATH:§r Se ha eliminado la regeneración pasiva de vida."));
        }
        if (apocalipsisMinecraftTotal) {
            lista.add(Component.literal("  §4§l☣ ¡APOCALIPSIS MINECRAFT !!!!:§r Los zombies se apilan en torres, huelen sangre y acuden al ruido al picar."));
        }
        return lista;
    }

    public static void limpiar() {
        creepersEnPerla = false;
        fuegoFatuo = false;
        speedpers = false;
        wardensDeepDark = false;
        apocalipsisZombie = false;
        filoMortal = false;
        errorTotemico = false;
        danoColateral = false;
        ultraFundido = false;
        intrusosCalavericos = false;
        spiderman = false;
        permadeath = false;
        apocalipsisMinecraftTotal = false;
    }
}
