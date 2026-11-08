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

        registrar("apocalipsis", NivelRuleta.NIVEL_1, "¿Apocalipsis Minecraft?",
                "50% de los zombies escalan paredes como arañas y son inmunes al sol.",
                (level, player) -> CursosActivos.activarApocalipsisZombie());

        registrar("cuevastenebrosas", NivelRuleta.NIVEL_1, "Cuevas Tenebrosas",
                "Recibes Oscuridad por debajo de la capa 0 (se reduce al portar un farol en la mano secundaria como fuente de luz).",
                (level, player) -> CursosActivos.activarCuevasTenebrosas());

        registrar("comidaechada", NivelRuleta.NIVEL_1, "Comida Echada a Perder",
                "5% de probabilidad de recibir Infestado por 15 segundos al comer cualquier alimento (excepto dorados o cocinados).",
                (level, player) -> CursosActivos.activarComidaEchada());

        registrar("cuccossalvajes", NivelRuleta.NIVEL_1, "Cuccos Salvajes",
                "Los pollos tienen 15 de vida y te golpean cuando les pegas (hacen 1 corazón por golpe).",
                (level, player) -> CursosActivos.activarCuccosSalvajes());

        registrar("cadaverespisados", NivelRuleta.NIVEL_1, "Cadáveres Pisados",
                "Ahora al pisar la arena de almas dará el efecto de Debilidad I.",
                (level, player) -> CursosActivos.activarCadaveresPisados());

        registrar("vacaexplosiva", NivelRuleta.NIVEL_1, "Vaca Explosiva",
                "Las vacas tienen una probabilidad del 1% de hacer una explosión cuando mueran.",
                (level, player) -> CursosActivos.activarVacaExplosiva());

        registrar("corrientesaereas", NivelRuleta.NIVEL_1, "Corrientes Aéreas",
                "Los Breeze ahora disparan 6 Wind Charges en ráfaga (como los Blazes).",
                (level, player) -> CursosActivos.activarCorrientesAereas());

        registrar("contaminacioncruzada", NivelRuleta.NIVEL_1, "Contaminación Cruzada",
                "Los alimentos crudos dan Hambre II y Veneno I al consumirlos.",
                (level, player) -> CursosActivos.activarContaminacionCruzada());

        registrar("paranoianocturna", NivelRuleta.NIVEL_1, "Paranoia Nocturna",
                "Permanecer con nivel de luz 0 por >15s provoca pasos falsos y Ceguera I por 2 segundos cada 40 segundos.",
                (level, player) -> CursosActivos.activarParanoiaNocturna());

        // --- NIVEL 2 ---
        registrar("speedpers", NivelRuleta.NIVEL_2, "Speedpers",
                "Todos los Creepers del Overworld (excepto Creepers Escondidos) tienen Velocidad II permanente.",
                (level, player) -> CursosActivos.activarSpeedpers());

        registrar("filomortal", NivelRuleta.NIVEL_2, "Filo Mortal",
                "Todos los zombies empuñan espadas de hierro con Filo III.",
                (level, player) -> CursosActivos.activarFiloMortal());

        registrar("muerteespinosa", NivelRuleta.NIVEL_2, "Muerte Espinosa",
                "Todos los cactus hacen 1x10^9 de daño al contacto.",
                (level, player) -> CursosActivos.activarMuerteEspinosa());

        registrar("cucarachaplateada", NivelRuleta.NIVEL_2, "Cucaracha Plateada",
                "Los lepismas otorgan Lentitud II por 5 segundos al golpearte y alertan a otros lepismas en un radio de 12 bloques.",
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
                "Usar el escudo consume el doble de barra de comida y se deshabilita al recibir 5 golpes seguidos en 10 segundos.",
                (level, player) -> CursosActivos.activarEscudoPesado());

        registrar("wingardiumleviosa", NivelRuleta.NIVEL_2, "Wingardium Leviosa",
                "Las brujas te pueden arrojar pociones de Levitación XV por 1 segundo.",
                (level, player) -> CursosActivos.activarWingardiumLeviosa());

        registrar("lavaactiva", NivelRuleta.NIVEL_2, "Lava Activa",
                "Ahora la lava en el Overworld se mueve con la misma velocidad que en el Nether.",
                (level, player) -> CursosActivos.activarLavaActiva());

        registrar("webones", NivelRuleta.NIVEL_2, "Webones",
                "Existe un 25% de probabilidad de que los aldeanos al crecer sean Nitwit (verdes).",
                (level, player) -> CursosActivos.activarWebones());

        registrar("fuegofatuo", NivelRuleta.NIVEL_2, "Fuego Fatuo",
                "Necesitas armadura completa en el Nether o arderás hasta morir.",
                (level, player) -> CursosActivos.activarFuegoFatuo());

        registrar("wardens", NivelRuleta.NIVEL_2, "Wardens en Deep Dark",
                "Los Wardens ahora pueden aparecer naturalmente en el Deep Dark.",
                (level, player) -> CursosActivos.activarWardensDeepDark());

        registrar("invasionazul", NivelRuleta.NIVEL_2, "Invasión Azul",
                "Los Breeze pueden spawnear de día en todos los biomas del Overworld (15% de probabilidad, máx. 3 en 32 bloques).",
                (level, player) -> CursosActivos.activarInvasionAzul());

        registrar("infestacionminera", NivelRuleta.NIVEL_2, "Infestación Minera",
                "En las paredes de las Mineshafts se pueden generar bloques de lepisma.",
                (level, player) -> CursosActivos.activarInfestacionMinera());

        registrar("mineriainestable", NivelRuleta.NIVEL_2, "Minería Inestable",
                "5% de probabilidad al romper minerales de provocar una explosión pequeña sin destruir bloques.",
                (level, player) -> CursosActivos.activarMineriaInestable());

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

        registrar("muerteinstantanea", NivelRuleta.NIVEL_3, "Muerte Instantánea",
                "Todos los botones, trampillas, puertas y puertas de valla de madera hacen 1x10^9 de daño al usarlos.",
                (level, player) -> CursosActivos.activarMuerteInstantanea());

        registrar("manzanaregeneradora", NivelRuleta.NIVEL_3, "Manzanas Regeneradoras",
                "Ahora las manzanas de oro (excepto las encantadas) no dan el efecto de absorción.",
                (level, player) -> CursosActivos.activarManzanaRegeneradora());

        registrar("magmacontacto", NivelRuleta.NIVEL_3, "Magma al Contacto",
                "Los bloques de magma hacen 1x10^9 de daño al tocarlos directamente por cualquier ángulo.",
                (level, player) -> CursosActivos.activarMagmaContacto());

        registrar("sinoxigeno", NivelRuleta.NIVEL_3, "Sin Oxígeno",
                "Ahora el oxígeno cuando te sumerges en el agua se agota el triple de rápido.",
                (level, player) -> CursosActivos.activarSinOxigeno());

        registrar("fallamecanica", NivelRuleta.NIVEL_3, "Falla Mecánica",
                "Los Gólems miden x1.8, tienen Fuerza y Velocidad que aumentan por cada deterioro (+1) y con <25% de vida atacan a todo ser vivo.",
                (level, player) -> CursosActivos.activarFallaMecanica());

        registrar("adivinaquien", NivelRuleta.NIVEL_3, "Adivina Quién Ha Vuelto",
                "El Illusioner aparece (25%) junto a raids, outposts y patrullas con Arco Retroceso X, Poder III y Resistencia III.",
                (level, player) -> CursosActivos.activarAdivinaQuien());

        registrar("creepersescondidos", NivelRuleta.NIVEL_3, "Creepers Escondidos",
                "Al romper bloques de pasto hay 20% de probabilidad de spawnear un mini-creeper cabezón con 1/3 de su daño explosivo.",
                (level, player) -> CursosActivos.activarCreepersEscondidos());

        registrar("monturaacuatica", NivelRuleta.NIVEL_3, "Montura Acuática",
                "Los Zombies Ahogados tienen 10% de probabilidad de aparecer montados en un Nautilo o Delfín.",
                (level, player) -> CursosActivos.activarMonturaAcuatica());

        // --- NIVEL 4 ---
        registrar("danocolateral", NivelRuleta.NIVEL_4, "Daño Colateral",
                "Las Ender Pearls hacen x3 su daño original e ignoran armadura.",
                (level, player) -> CursosActivos.activarDanoColateral());

        registrar("ultrafundido", NivelRuleta.NIVEL_4, "Ultra Fundido",
                "El daño por fuego se multiplica por 10.",
                (level, player) -> CursosActivos.activarUltraFundido());

        registrar("contaminacion", NivelRuleta.NIVEL_4, "¿Contaminación?",
                "Por encima de la capa 160 hay una niebla y humo verde que hace daño de Veneno V.",
                (level, player) -> CursosActivos.activarContaminacion());

        registrar("congelacionprofunda", NivelRuleta.NIVEL_4, "Congelación Profunda",
                "Quedarse >15s en biomas nevados sin fuente de calor reduce la velocidad a la mitad y causa daño progresivo.",
                (level, player) -> CursosActivos.activarCongelacionProfunda());

        registrar("escopeta", NivelRuleta.NIVEL_4, "Escopeta",
                "Los Pillagers poseen en sus ballestas Multidisparo VI, Carga Rápida II y Perforación V.",
                (level, player) -> CursosActivos.activarEscopeta());

        registrar("reydeloprofundo", NivelRuleta.NIVEL_4, "Rey de lo Profundo",
                "El Elder Guardian posee 100 de vida y su rayo da Debilidad I (+1 por cada golpe recibido) por 20 segundos.",
                (level, player) -> CursosActivos.activarReyDeLoProfundo());

        registrar("desiertoabrasador", NivelRuleta.NIVEL_4, "Desierto Abrasador",
                "Permanecer 15 segundos en el desierto acumula Insolación, causando 1 corazón de daño cada 2 segundos y partículas de agua.",
                (level, player) -> CursosActivos.activarDesiertoAbrasador());

        registrar("ataqueslimes", NivelRuleta.NIVEL_4, "Ataque a los Slimes",
                "5% de probabilidad de que aparezca un Slime Gigante en pantanos con x2.5 de daño de un slime grande.",
                (level, player) -> CursosActivos.activarAtaqueSlimes());

        // --- NIVEL 5 ---
        registrar("intrusoscalavericos", NivelRuleta.NIVEL_5, "Intrusos Calavéricos",
                "Wither Skeletons con armadura diamante y espada Filo V aparecen de noche.",
                (level, player) -> CursosActivos.activarIntrusosCalavericos());

        registrar("spiderman", NivelRuleta.NIVEL_5, "¿Spiderman?",
                "Todas las arañas obtienen Fuerza II, Velocidad II, Invisibilidad y Regeneración.",
                (level, player) -> CursosActivos.activarSpiderman());

        registrar("enderghast", NivelRuleta.NIVEL_5, "Ender Ghast",
                "El Ghast se vuelve más agresivo y sus proyectiles tienen un radio de explosión de 20 bloques.",
                (level, player) -> CursosActivos.activarEnderGhast());

        registrar("kaboom", NivelRuleta.NIVEL_5, "KA-BOOM",
                "Los Shulkers explotan 3 segundos después de morir como una TNT.",
                (level, player) -> CursosActivos.activarKaBoom());

        registrar("gigaendermite", NivelRuleta.NIVEL_5, "Giga-Endermite",
                "En el End hay 5% de probabilidad de un Endermite Gigante con 250 de vida, x2 de daño, sin empuje y con embestida.",
                (level, player) -> CursosActivos.activarGigaEndermite());

        registrar("megahoglin", NivelRuleta.NIVEL_5, "Mega Hoglin",
                "En el Nether hay 5% de probabilidad de un Hoglin Gigante con 300 de vida, x2 de daño y Lentitud.",
                (level, player) -> CursosActivos.activarMegaHoglin());

        registrar("creepercosmico", NivelRuleta.NIVEL_5, "Creeper Cósmico",
                "Cerca de las End Cities puede aparecer (15%) un Creeper Eléctrico con radio de explosión de 30 bloques.",
                (level, player) -> CursosActivos.activarCreeperCosmico());

        registrar("torbellinoigneo", NivelRuleta.NIVEL_5, "Torbellino Ígneo",
                "Los proyectiles de los Blazes empujan fuertemente y crean un charco de lava y fuego de 3x3 temporal (8s).",
                (level, player) -> CursosActivos.activarTorbellinoIgneo());

        registrar("phantomalfa", NivelRuleta.NIVEL_5, "Phantom Alfa",
                "Phantoms x2.5 escala, 60 HP, inmunes al sol y onda sónica en picada que inflige 3 corazones ignorando armadura.",
                (level, player) -> CursosActivos.activarPhantomAlfa());

        registrar("cegueraabisal", NivelRuleta.NIVEL_5, "Ceguera Abisal",
                "En el End y Deep Dark, cualquier golpe de un monstruo aplica Ceguera y Oscuridad por 8s sin poder curarse con leche.",
                (level, player) -> CursosActivos.activarCegueraAbisal());

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
                "5% de probabilidad de Luna de Sangre: sin camas, hordas masivas y mobs con hasta 3 efectos potentes aleatorios.",
                (level, player) -> CursosActivos.activarLunaDeSangre());

        registrar("eclipsesolar", NivelRuleta.NIVEL_6, "Eclipse Solar",
                "Probabilidad de Eclipse Solar con Strays con Lanza de Diamante (Filo II) en Phantoms, Brujas en Murciélagos y Creakings con Velocidad II.",
                (level, player) -> CursosActivos.activarEclipseSolar());

        registrar("elverdugo", NivelRuleta.NIVEL_6, "El Verdugo (Piglin Executioner)",
                "15% de Piglins Brutos aparecen gigantes (x1.5), 80 HP, Hacha de Netherite Filo VI y sus golpes críticos desarman al jugador.",
                (level, player) -> CursosActivos.activarElVerdugo());

        registrar("maresacidos", NivelRuleta.NIVEL_6, "Mares Ácidos",
                "Todo cuerpo de agua en Overworld es verde corrosivo: sumergirse daña 2 corazones/s y triplica el desgaste de armadura.",
                (level, player) -> CursosActivos.activarMaresAcidos());

        registrar("parecequevallover", NivelRuleta.NIVEL_6, "Parece que va´ llover",
                "Tormentas frecuentes con probabilidad base de 15% de ser alcanzado por rayos si portas armadura o herramientas metálicas. Aviso de 10s con estática para despojarte de metales.",
                (level, player) -> CursosActivos.activarPareceQueVaALlover());
    }
}
