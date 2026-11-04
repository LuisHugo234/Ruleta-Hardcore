package com.luishugomod01.ruleta;

import net.minecraft.world.level.gamerules.GameRules;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class CatalogoCastigos {

    public record EntradaCastigo(String id, NivelRuleta nivel, String nombre, String explicacion, Castigo castigo) {}

    private static final EnumMap<NivelRuleta, List<EntradaCastigo>> sesion = new EnumMap<>(NivelRuleta.class);
    private static final Set<String> consumidos = new HashSet<>();

    static {
        reiniciar();
    }

    public static void reiniciar() {
        sesion.clear();
        consumidos.clear();
        for (NivelRuleta nivel : NivelRuleta.values()) {
            sesion.put(nivel, new ArrayList<>());
        }
        registrarTodos();
    }

    private static void registrar(String id, NivelRuleta nivel, String nombre, String explicacion, Castigo castigo) {
        sesion.get(nivel).add(new EntradaCastigo(id, nivel, nombre, explicacion, castigo));
    }

    public static Optional<EntradaCastigo> elegirYEliminar(NivelRuleta nivel) {
        List<EntradaCastigo> lista = sesion.get(nivel);
        if (lista == null || lista.isEmpty()) return Optional.empty();
        int indice = ThreadLocalRandom.current().nextInt(lista.size());
        EntradaCastigo entrada = lista.remove(indice);
        consumidos.add(entrada.id().toLowerCase());
        return Optional.of(entrada);
    }

    public static Optional<EntradaCastigo> activarYEliminarPorId(String id) {
        for (List<EntradaCastigo> lista : sesion.values()) {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).id().equalsIgnoreCase(id)) {
                    EntradaCastigo entrada = lista.remove(i);
                    consumidos.add(entrada.id().toLowerCase());
                    return Optional.of(entrada);
                }
            }
        }
        return Optional.empty();
    }

    public static Set<String> obtenerIdsConsumidos() {
        return new HashSet<>(consumidos);
    }

    public static void marcarConsumido(String id) {
        consumidos.add(id.toLowerCase());
        for (List<EntradaCastigo> lista : sesion.values()) {
            lista.removeIf(entrada -> entrada.id().equalsIgnoreCase(id));
        }
    }

    public static boolean hayDisponibles(NivelRuleta nivel) {
        List<EntradaCastigo> lista = sesion.get(nivel);
        return lista != null && !lista.isEmpty();
    }

    public static int cantidadDisponible(NivelRuleta nivel) {
        List<EntradaCastigo> lista = sesion.get(nivel);
        return lista == null ? 0 : lista.size();
    }

    private static void registrarTodos() {

        // --- NIVEL 1 ---
        registrar("creepersperla", NivelRuleta.NIVEL_1, "Espejismo de Ender Pearl",
                "Al arrojar una Ender Pearl hay un 5% de probabilidad de spawnear un Creeper.",
                (level, player) -> CursosActivos.activarCreepersEnPerla());

        registrar("fuegofatuo", NivelRuleta.NIVEL_1, "Fuego Fatuo",
                "Necesitas armadura completa en el Nether o arderás hasta morir.",
                (level, player) -> CursosActivos.activarFuegoFatuo());

        registrar("apocalipsis", NivelRuleta.NIVEL_1, "¿Apocalipsis Minecraft?",
                "50% de los zombies escalan paredes como arañas y son inmunes al sol.",
                (level, player) -> CursosActivos.activarApocalipsisZombie());

        registrar("wardens", NivelRuleta.NIVEL_1, "Wardens en Deep Dark",
                "Los Wardens ahora pueden aparecer naturalmente en el Deep Dark.",
                (level, player) -> CursosActivos.activarWardensDeepDark());

        registrar("cuevastenebrosas", NivelRuleta.NIVEL_1, "Cuevas Tenebrosas",
                "Recibes Oscuridad por debajo de la capa 0 (disminuye con un farol en la mano secundaria).",
                (level, player) -> CursosActivos.activarCuevasTenebrosas());

        registrar("defensavillera", NivelRuleta.NIVEL_1, "Defensa Villera",
                "Los Gólems de Hierro ahora son agresivos con todos los jugadores.",
                (level, player) -> CursosActivos.activarDefensaVillera());

        // --- NIVEL 2 ---
        registrar("speedpers", NivelRuleta.NIVEL_2, "Speedpers",
                "Todos los Creepers tienen Velocidad II permanente.",
                (level, player) -> CursosActivos.activarSpeedpers());

        registrar("filomortal", NivelRuleta.NIVEL_2, "Filo Mortal",
                "Todos los zombies empuñan espadas de hierro con Filo III.",
                (level, player) -> CursosActivos.activarFiloMortal());

        registrar("muerteespinosa", NivelRuleta.NIVEL_2, "Muerte Espinosa",
                "Todos los cactus hacen 1x10^9 de daño al contacto.",
                (level, player) -> CursosActivos.activarMuerteEspinosa());

        // --- NIVEL 3 ---
        registrar("errortotemico", NivelRuleta.NIVEL_3, "Error Totémico",
                "Los Tótems de Inmortalidad tienen un 2% de probabilidad de fallar.",
                (level, player) -> CursosActivos.activarErrorTotemico());

        registrar("artesmaritimos", NivelRuleta.NIVEL_3, "Artes Marítimos",
                "Todos los Zombies Ahogados aparecen armados con Tridentes.",
                (level, player) -> CursosActivos.activarArtesMaritimos());

        registrar("boogiewoogie", NivelRuleta.NIVEL_3, "Boogie Woogie",
                "Los esqueletos intercambian lugares contigo cuando su flecha te impacte.",
                (level, player) -> CursosActivos.activarBoogieWoogie());

        // --- NIVEL 4 ---
        registrar("danocolateral", NivelRuleta.NIVEL_4, "Daño Colateral",
                "Las Ender Pearls hacen x3 su daño original e ignoran armadura.",
                (level, player) -> CursosActivos.activarDanoColateral());

        registrar("ultrafundido", NivelRuleta.NIVEL_4, "Ultra Fundido",
                "El daño por fuego se multiplica por 10.",
                (level, player) -> CursosActivos.activarUltraFundido());

        registrar("poderenlamano", NivelRuleta.NIVEL_4, "Poder en la Mano",
                "Los Piglins Brutos poseen un hacha de diamante con Filo V.",
                (level, player) -> CursosActivos.activarPoderEnLaMano());

        // --- NIVEL 5 ---
        registrar("intrusoscalavericos", NivelRuleta.NIVEL_5, "Intrusos Calavéricos",
                "Wither Skeletons con armadura diamante y espada Filo V aparecen de noche.",
                (level, player) -> CursosActivos.activarIntrusosCalavericos());

        registrar("spiderman", NivelRuleta.NIVEL_5, "¿Spiderman?",
                "Todas las arañas obtienen Fuerza II, Velocidad II, Invisibilidad y Regeneración.",
                (level, player) -> CursosActivos.activarSpiderman());

        registrar("enderghast", NivelRuleta.NIVEL_5, "Ender Ghast",
                "El Ghast se vuelve más agresivo y sus proyectiles hacen x3 de daño explosivo.",
                (level, player) -> CursosActivos.activarEnderGhast());

        registrar("kaboom", NivelRuleta.NIVEL_5, "KA-BOOM",
                "Los Shulkers explotan 3 segundos después de morir como una TNT.",
                (level, player) -> CursosActivos.activarKaBoom());

        registrar("ataqueslimes", NivelRuleta.NIVEL_5, "Ataque a los Slimes",
                "5% de probabilidad de que aparezca un Slime Gigante en pantanos con x10 de daño.",
                (level, player) -> CursosActivos.activarAtaqueSlimes());

        registrar("gigaendermite", NivelRuleta.NIVEL_5, "Giga-Endermite",
                "En el End hay 5% de probabilidad de un Endermite Gigante con 250 de vida y x2 de daño.",
                (level, player) -> CursosActivos.activarGigaEndermite());

        registrar("megahoglin", NivelRuleta.NIVEL_5, "Mega Hoglin",
                "En el Nether hay 5% de probabilidad de un Hoglin Gigante con 300 de vida, x2 de daño y Lentitud II.",
                (level, player) -> CursosActivos.activarMegaHoglin());

        // --- NIVEL 6 ---
        registrar("permadeath", NivelRuleta.NIVEL_6, "PERMADEATH",
                "Se elimina permanentemente la regeneración pasiva de vida natural.",
                (level, player) -> {
                    CursosActivos.activarPermadeath();
                    if (level != null && level.getServer() != null) {
                        for (var lvl : level.getServer().getAllLevels()) {
                            lvl.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, false, level.getServer());
                        }
                    }
                });

        registrar("apocalipsis6", NivelRuleta.NIVEL_6, "¡Apocalipsis Minecraft !!!!",
                "Los zombies se apilan en torres, huelen sangre y acuden al ruido al picar.",
                (level, player) -> CursosActivos.activarApocalipsisMinecraftTotal());

        registrar("lunasangre", NivelRuleta.NIVEL_6, "Luna de Sangre",
                "Cada noche hay 5% de probabilidad de Luna de Sangre: sin camas y hordas masivas.",
                (level, player) -> CursosActivos.activarLunaDeSangre());

        registrar("monturaacuatica", NivelRuleta.NIVEL_6, "Montura Acuática",
                "Los Zombies Ahogados van en nautilos y sus tridentes tienen Empalamiento V.",
                (level, player) -> CursosActivos.activarMonturaAcuatica());
    }
}
