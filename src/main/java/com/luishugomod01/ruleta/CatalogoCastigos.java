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
                "Recibes Oscuridad por debajo de la capa 0 (se reduce levemente con un farol en la mano secundaria).",
                (level, player) -> CursosActivos.activarCuevasTenebrosas());

        registrar("defensavillera", NivelRuleta.NIVEL_1, "Defensa Villera",
                "Los Gólems de Hierro ahora son agresivos con todos los jugadores.",
                (level, player) -> CursosActivos.activarDefensaVillera());

        registrar("cuccossalvajes", NivelRuleta.NIVEL_1, "Cuccos Salvajes",
                "Los pollos tienen 15 de vida y te golpean cuando les pegas (hacen 1 corazón por golpe).",
                (level, player) -> CursosActivos.activarCuccosSalvajes());

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

        registrar("cucarachaplateada", NivelRuleta.NIVEL_2, "Cucaracha Plateada",
                "Los lepismas otorgan Lentitud II por 5 segundos al golpear al jugador.",
                (level, player) -> CursosActivos.activarCucarachaPlateada());

        registrar("abejasasesinas", NivelRuleta.NIVEL_2, "Abejas Asesinas",
                "Las abejas son agresivas con el jugador y dan Descomposición II por 5 segundos.",
                (level, player) -> CursosActivos.activarAbejasAsesinas());

        registrar("cargapesada", NivelRuleta.NIVEL_2, "Carga Pesada",
                "Los Ravagers tienen Lentitud II, 150 corazones de vida y x1.5 su daño base.",
                (level, player) -> CursosActivos.activarCargaPesada());

        registrar("vexgrandes", NivelRuleta.NIVEL_2, "Vex Grandes",
                "Los Vex ahora son el doble de grandes y el doble de fuertes.",
                (level, player) -> CursosActivos.activarVexGrandes());

        registrar("resistenciafisica", NivelRuleta.NIVEL_2, "Resistencia Física",
                "Los Vindicadores tienen el efecto de Resistencia I y Resistencia contra el Fuego.",
                (level, player) -> CursosActivos.activarResistenciaFisica());

        registrar("escudopesado", NivelRuleta.NIVEL_2, "Escudo Pesado",
                "Usar el escudo para bloquear ataques consume el doble de barra de comida.",
                (level, player) -> CursosActivos.activarEscudoPesado());

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

        registrar("energiaposeica", NivelRuleta.NIVEL_3, "Energía Poseica",
                "Los tridentes de los Ahogados tienen Empalamiento V y Conductividad.",
                (level, player) -> CursosActivos.activarEnergiaPoseica());

        registrar("infestacionminera", NivelRuleta.NIVEL_3, "Infestación Minera",
                "En las paredes de las Mineshafts se pueden generar bloques de lepisma.",
                (level, player) -> CursosActivos.activarInfestacionMinera());

        registrar("muerteinstantanea", NivelRuleta.NIVEL_3, "Muerte Instantánea",
                "Todos los botones, trampillas, puertas y puertas de valla de madera hacen 1x10^9 de daño al usarlos.",
                (level, player) -> CursosActivos.activarMuerteInstantanea());

        registrar("manzanaregeneradora", NivelRuleta.NIVEL_3, "Manzanas Regeneradoras",
                "Ahora las manzanas de oro no dan el efecto de absorción.",
                (level, player) -> CursosActivos.activarManzanaRegeneradora());

        registrar("magmacontacto", NivelRuleta.NIVEL_3, "Magma al Contacto",
                "Los bloques de magma hacen 1x10^9 de daño al tocarlos por cualquier ángulo.",
                (level, player) -> CursosActivos.activarMagmaContacto());

        registrar("contaminacioncruzada", NivelRuleta.NIVEL_3, "Contaminación Cruzada",
                "Los alimentos crudos dan Hambre II y Veneno I al consumirlos.",
                (level, player) -> CursosActivos.activarContaminacionCruzada());

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

        registrar("contaminacion", NivelRuleta.NIVEL_4, "¿Contaminación?",
                "Por encima de la capa 100 hay una niebla y humo verde que hace daño de Veneno V.",
                (level, player) -> CursosActivos.activarContaminacion());

        registrar("congelacionprofunda", NivelRuleta.NIVEL_4, "Congelación Profunda",
                "Quedarse >15s en biomas nevados sin fuente de calor reduce la velocidad a la mitad y causa daño progresivo.",
                (level, player) -> CursosActivos.activarCongelacionProfunda());

        registrar("escopeta", NivelRuleta.NIVEL_4, "Escopeta",
                "Los Pillagers poseen en sus ballestas Multidisparo VI, Carga Rápida II y Perforación V.",
                (level, player) -> CursosActivos.activarEscopeta());

        // --- NIVEL 5 ---
        registrar("intrusoscalavericos", NivelRuleta.NIVEL_5, "Intrusos Calavéricos",
                "Wither Skeletons con armadura diamante y espada Filo V aparecen de noche.",
                (level, player) -> CursosActivos.activarIntrusosCalavericos());

        registrar("spiderman", NivelRuleta.NIVEL_5, "¿Spiderman?",
                "Todas las arañas obtienen Fuerza II, Velocidad II, Invisibilidad y Regeneración.",
                (level, player) -> CursosActivos.activarSpiderman());

        registrar("enderghast", NivelRuleta.NIVEL_5, "Ender Ghast",
                "El Ghast se vuelve más agresivo y sus proyectiles hacen x3 el daño explosivo.",
                (level, player) -> CursosActivos.activarEnderGhast());

        registrar("kaboom", NivelRuleta.NIVEL_5, "KA-BOOM",
                "Los Shulkers explotan 3 segundos después de morir como una TNT.",
                (level, player) -> CursosActivos.activarKaBoom());

        registrar("ataqueslimes", NivelRuleta.NIVEL_5, "Ataque a los Slimes",
                "5% de probabilidad de que aparezca un Slime Gigante en pantanos con x2.5 de daño de un slime grande.",
                (level, player) -> CursosActivos.activarAtaqueSlimes());

        registrar("gigaendermite", NivelRuleta.NIVEL_5, "Giga-Endermite",
                "En el End hay 5% de probabilidad de un Endermite Gigante con 250 de vida, x2 de daño y sin empuje.",
                (level, player) -> CursosActivos.activarGigaEndermite());

        registrar("megahoglin", NivelRuleta.NIVEL_5, "Mega Hoglin",
                "En el Nether hay 5% de probabilidad de un Hoglin Gigante con 300 de vida, x2 de daño y Lentitud.",
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
                "Los Zombies Ahogados tienen 10% de probabilidad de aparecer montados en un Nautilo o Delfín.",
                (level, player) -> CursosActivos.activarMonturaAcuatica());

        registrar("eclipsesolar", NivelRuleta.NIVEL_6, "Eclipse Solar",
                "Probabilidad de Eclipse Solar con Strays con Lanza de Diamante (Filo II) en Phantoms y Brujas en Murciélagos.",
                (level, player) -> CursosActivos.activarEclipseSolar());

        registrar("insomniodesastroso", NivelRuleta.NIVEL_6, "Insomnio Desastroso",
                "Las camas explotan al romperse.",
                (level, player) -> CursosActivos.activarInsomnioDesastroso());
    }
}
