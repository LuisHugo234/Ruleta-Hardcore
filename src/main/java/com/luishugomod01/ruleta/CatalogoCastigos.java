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
                "Recibes Oscuridad por debajo de la capa 0 (se anula con faroles o se atenúa con antorchas en cualquier mano).",
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

        registrar("refuerzospodridos", NivelRuleta.NIVEL_1, "Refuerzos Podridos",
                "Al morir un zombi, tiene un 10% de probabilidad de invocar un zombi bebé.",
                (level, player) -> CursosActivos.activarRefuerzosPodridos());

        registrar("maldicionyunque", NivelRuleta.NIVEL_1, "La Maldición del Yunque",
                "Los yunques tienen un 10% de probabilidad de generar una pequeña explosión al romperse por desgaste.",
                (level, player) -> CursosActivos.activarMaldicionYunque());

        registrar("inventariopesado", NivelRuleta.NIVEL_1, "Inventario Pesado",
                "Si tienes el inventario completamente lleno (cada slot con al menos un ítem), no puedes correr.",
                (level, player) -> CursosActivos.activarInventarioPesado());

        registrar("caidabrutal", NivelRuleta.NIVEL_1, "Caída Brutal",
                "El daño por caída aumenta un 25%, sin importar la armadura que lleves.",
                (level, player) -> CursosActivos.activarCaidaBrutal());

        registrar("ojosenlaoscuridad", NivelRuleta.NIVEL_1, "Ojos en la Oscuridad",
                "Los Enderman pueden volverse agresivos si te acercas a menos de 3 bloques, incluso sin mirarlos.",
                (level, player) -> CursosActivos.activarOjosEnLaOscuridad());

        registrar("escupitajomortal", NivelRuleta.NIVEL_1, "Escupitajo Mortal",
                "Los escupitajos de las llamas causan 5 corazones de daño al impactar contra un jugador, ignorando completamente su armadura y resistencia.",
                (level, player) -> CursosActivos.activarEscupitajoMortal());

        registrar("camellosfuriosos", NivelRuleta.NIVEL_1, "Camellos Furiosos",
                "Todos los camellos son agresivos hacia los jugadores, causando 3 corazones de daño por golpe.",
                (level, player) -> CursosActivos.activarCamellosFuriosos());

        registrar("toteminestable", NivelRuleta.NIVEL_1, "Tótem Inestable",
                "Cuando se activa un tótem, el jugador recibe Lentitud II durante 3 segundos después de salvarse.",
                (level, player) -> CursosActivos.activarTotemInestable());

        registrar("supermurcielago", NivelRuleta.NIVEL_1, "Super Murciélago",
                "Los murciélagos son agresivos hacia los jugadores, tienen el doble de tamaño, 10 puntos de vida, poseen omnivampirismo (recuperando 50% del daño infligido) e infligen 2 corazones de daño por golpe.",
                (level, player) -> CursosActivos.activarSuperMurcielago());

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

        registrar("telaranaalfallo", NivelRuleta.NIVEL_2, "Telaraña al Fallo",
                "Cuando una araña salta con éxito sobre el cuerpo de un jugador, coloca un bloque temporal de telaraña pegajosa en sus pies que dura 2.5 segundos antes de desvanecerse.",
                (level, player) -> CursosActivos.activarTelaranaAlFallo());

        registrar("brujareflejante", NivelRuleta.NIVEL_2, "Bruja Reflejante",
                "Las brujas tienen un 30% de probabilidad de repeler proyectiles: cualquier flecha o tridente que le disparen rebota en un escudo mágico visible y sale disparado de vuelta hacia el jugador.",
                (level, player) -> CursosActivos.activarBrujaReflejante());

        registrar("gritoprimal", NivelRuleta.NIVEL_2, "Grito Primal",
                "Ahora los endermans aturden al jugador que atacan y lo dejan sordo durante 30 segundos después de que se enoje con el jugador.",
                (level, player) -> CursosActivos.activarGritoPrimal());

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

        registrar("desiertoabrasador", NivelRuleta.NIVEL_3, "Desierto Abrasador",
                "Permanecer en el desierto acumula insolación (1 corazón cada 2s). Se reinicia con agua o al beber, y se evita con casco (gastando durabilidad).",
                (level, player) -> CursosActivos.activarDesiertoAbrasador());

        registrar("pykete", NivelRuleta.NIVEL_3, "Pykete",
                "Los ahogados portan un tridente con cuerda de algas. Al lanzarlo contra un jugador, si no es bloqueado con escudo, además del daño lo arrastra violentamente 8 bloques hacia el agua, y si impacta contra un barco de madera lo rompe instantáneamente de un solo golpe.",
                (level, player) -> CursosActivos.activarPykete());

        registrar("fragmentacionhelada", NivelRuleta.NIVEL_3, "Fragmentación Helada",
                "Al morir un Stray (esqueleto del bioma helado), su cuerpo se quiebra en una ráfaga de esquirlas congeladas que transforma el suelo en un radio de 4 bloques en hielo resbaladizo e inflige el efecto de Congelación Profunda inmediata a quien esté cerca.",
                (level, player) -> CursosActivos.activarFragmentacionHelada());

        registrar("comopedro", NivelRuleta.NIVEL_3, "Como Pedro",
                "Ahora los creepers pueden caminar sobre el agua y mientras la pisan su velocidad se multiplica por 3.",
                (level, player) -> CursosActivos.activarComoPedro());

        registrar("creeperavispon", NivelRuleta.NIVEL_3, "Creeper Avispón",
                "En el Overworld pueden aparecer pequeños creepers voladores del tamaño de una abeja bebé que revolotean y zumban hacia el jugador. Al detonar no rompen bloques y su explosión inflige 2/3 del daño de un creeper ordinario en el mismo radio.",
                (level, player) -> CursosActivos.activarCreeperAvispon());

        registrar("hombreroca", NivelRuleta.NIVEL_3, "Hombre Roca",
                "Aparece de día y de noche en cualquier bioma (80 HP, 4 de armadura, 50% resistencia al empuje) para atormentar a los jugadores arrojando rocas gigantes que infligen 13 de daño directo (6 en área pequeña), gran empuje, Lentitud II por 2.5s y desactivan el escudo por 2s.",
                (level, player) -> CursosActivos.activarHombreRoca());

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

        registrar("ataqueslimes", NivelRuleta.NIVEL_4, "Ataque a los Slimes",
                "5% de probabilidad de que aparezca un Slime Gigante en los biomas de pantano con x2.5 de daño de un slime grande y un mega salto.",
                (level, player) -> CursosActivos.activarAtaqueSlimes());

        registrar("tensionmuscular", NivelRuleta.NIVEL_4, "Tensión Muscular",
                "Mantener tensado el arco o la ballesta por más de 3 segundos seguidos fatiga los brazos del jugador, provocando temblores de puntería, lentitud al caminar y Debilidad II hasta soltar el arma.",
                (level, player) -> CursosActivos.activarTensionMuscular());

        registrar("colosalfusion", NivelRuleta.NIVEL_4, "Colosal de Fusión",
                "En el Nether pueden aparecer Magma Cubes colosales (tamaño 7-8). Al aterrizar de sus mega saltos crean un cráter que enciende fuego y convierte temporalmente los bloques circundantes en magma hirviente durante 8 segundos. Al morir, sus crías salen disparadas en arco en todas direcciones como proyectiles de mortero.",
                (level, player) -> CursosActivos.activarColosalFusion());

        registrar("cazadorigneo", NivelRuleta.NIVEL_4, "Cazador Ígneo",
                "Un 15% de los Wither Skeletons en fortalezas del Nether portan un arco corto de hueso con flechas de fuego fatuo azul. Sus disparos prenden fuego azul e imbuyen el efecto de Descomposición (Wither I) a distancia durante 6 segundos.",
                (level, player) -> CursosActivos.activarCazadorIgneo());

        registrar("cordura", NivelRuleta.NIVEL_4, "Cordura",
                "Existe un medidor de cordura que baja progresivamente en la oscuridad, dimensiones hostiles o cerca de monstruos hasta llegar a 0, provocando alucinaciones auditivas y visuales.",
                (level, player) -> CursosActivos.activarCordura());

        // --- NIVEL 5 ---
        registrar("intrusoscalavericos", NivelRuleta.NIVEL_5, "Intrusos Calavéricos",
                "Wither Skeletons con armadura diamante y espada Filo V aparecen de noche.",
                (level, player) -> CursosActivos.activarIntrusosCalavericos());

        registrar("spiderman", NivelRuleta.NIVEL_5, "¿Spiderman?",
                "Todas las arañas obtienen Fuerza II, Velocidad II, Invisibilidad y Regeneración.",
                (level, player) -> CursosActivos.activarSpiderman());

        registrar("enderghast", NivelRuleta.NIVEL_5, "Ender Ghast",
                "El Ghast se vuelve mas agresivo y sus proyectiles hacen un x3 el daño explosivo.",
                (level, player) -> CursosActivos.activarEnderGhast());

        registrar("kaboom", NivelRuleta.NIVEL_5, "KA-BOOM",
                "Los Shulkers explotan 3 segundos después de morir como una TNT.",
                (level, player) -> CursosActivos.activarKaBoom());

        registrar("gigaendermite", NivelRuleta.NIVEL_5, "Giga-Endermite",
                "En el End hay 5% de probabilidad de un Endermite Gigante con 250 de vida, x2 de daño, sin empuje y con embestida.",
                (level, player) -> CursosActivos.activarGigaEndermite());

        registrar("megahoglin", NivelRuleta.NIVEL_5, "Mega Hoglin",
                "En el Nether hay 5% de probabilidad de un Hoglin gigante con 300 HP, x2 de daño y Lentitud. Al embestir en línea recta genera una onda expansiva que desestabiliza a los jugadores e impide saltar o colocar bloques durante 3 segundos.",
                (level, player) -> CursosActivos.activarMegaHoglin());

        registrar("creepercosmico", NivelRuleta.NIVEL_5, "Creeper Cósmico",
                "En el End puede aparecer (15%) un Creeper Eléctrico lentísimo con radio de explosión de 30 bloques y un centro de gravedad mayor que te arrastra hacia él en un radio de 30 bloques.",
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

        registrar("falladimensional", NivelRuleta.NIVEL_5, "Falla Dimensional",
                "Al cruzar un portal del Nether hacia el Nether o de vuelta al Overworld, hay un 15% de probabilidad de que el portal sufra una sobrecarga que invoca instantáneamente 2 Blazes agresivos junto al marco.",
                (level, player) -> CursosActivos.activarFallaDimensional());

        registrar("multicreeper", NivelRuleta.NIVEL_5, "Multicreeper",
                "Si un Creeper recibe daño de un arma cuerpo a cuerpo y sobrevive al golpe, tiene un 30% de probabilidad de dividirse instantáneamente en 2 mini-creepers con temporizador de mecha ultracorto (1 segundo) cuyas explosiones no dañan a creepers.",
                (level, player) -> CursosActivos.activarMulticreeper());

        registrar("sombraparpadeante", NivelRuleta.NIVEL_5, "Sombra Parpadeante",
                "Los endermans se vuelven depredadores sigilosos. Cada vez que el jugador intenta mirarlos o golpearlos cuerpo a cuerpo, la Sombra se teletransporta instantáneamente a su espalda exacta. Además, mientras te ataca desactiva el sonido ambiental en un radio de 12 bloques (silencio sepulcral táctico) y emite un pulso psíquico que fuerza tu vista hacia sus ojos si intentas mirar al suelo.",
                (level, player) -> CursosActivos.activarSombraParpadeante());

        registrar("berserker", NivelRuleta.NIVEL_5, "Berserker",
                "Vindicadores con hacha de Netherite y casco reforzado. Al detectar a un jugador a menos de 15 bloques entran en Carrera Frenética (+60% de velocidad). Sus golpes ignoran el 50% de la protección de cualquier armadura, y si golpean a un jugador con el escudo levantado, rompen el escudo por 10 segundos y aplican Lentitud III por 2 segundos debido al impacto cinético.",
                (level, player) -> CursosActivos.activarBerserker());

        registrar("nigromantecriptas", NivelRuleta.NIVEL_5, "El Nigromante de las Criptas",
                "Stray con túnica y báculo de hielo comprimido. Canaliza un rayo gélido continuo que convierte el suelo bajo los pies del jugador en hielo resbaladizo e invoca periódicamente mini-strays congelados armados con hachas de piedra.",
                (level, player) -> CursosActivos.activarNigromanteCriptas());

        registrar("creeperfantasma", NivelRuleta.NIVEL_5, "Creeper Fantasma",
                "Creeper translúcido de aspecto espectral. Es completamente inmune a proyectiles (las flechas y tridentes lo atraviesan sin dañarlo), obligando a los jugadores a enfrentarlo a corta distancia con armas cuerpo a cuerpo o explosiones.",
                (level, player) -> CursosActivos.activarCreeperFantasma());

        registrar("mimic", NivelRuleta.NIVEL_5, "Mimic",
                "Cofre trampa indistinguible de un cofre vanilla. Al recibir daño, intentar abrirlo o acercarse a menos de 2.5 bloques, despierta en postura cuadrúpeda arácnida y ataca con embestidas y mordidas de cepo (80 HP, 16 de daño y Lentitud III).",
                (level, player) -> CursosActivos.activarMimic());

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
                "El agua del Overworld se vuelve corrosiva verde: sumergirse inflige 2 corazones de daño por segundo y triplica el desgaste de la armadura.",
                (level, player) -> CursosActivos.activarMaresAcidos());

        registrar("parecequevallover", NivelRuleta.NIVEL_6, "Parece que va´ llover",
                "Tormentas frecuentes con probabilidad base de 15% de ser alcanzado por rayos si portas armadura o herramientas metálicas. Aviso de 10s con estática para despojarte de metales.",
                (level, player) -> CursosActivos.activarPareceQueVaALlover());

        registrar("anomaliagravitacional", NivelRuleta.NIVEL_6, "Anomalía Gravitacional",
                "En la dimensión del End, todo empuje recibido por proyectiles o ataques de monstruos se triplica, y saltar deja al jugador flotando en caída lenta durante 3 segundos, quedando indefenso en el aire.",
                (level, player) -> CursosActivos.activarAnomaliaGravitacional());

        registrar("corrupcionvacio", NivelRuleta.NIVEL_6, "Corrupción del Vacío",
                "Cualquier golpe recibido de un Enderman o proyectil de Shulker desactiva por completo la protección de tu armadura durante 5 segundos (recibiendo daño como si estuvieras desnudo).",
                (level, player) -> CursosActivos.activarCorrupcionVacio());

        registrar("creeperantimateria", NivelRuleta.NIVEL_6, "Creeper de Implosión Antimateria",
                "Existe una probabilidad pequeña de que aparezca un Creeper negro con partículas moradas del End. Al detonar, crea un vórtice gravitacional que succiona violentamente en un radio de 18 bloques durante 2.5 segundos antes de colapsar.",
                (level, player) -> CursosActivos.activarCreeperAntimateria());

        registrar("creepertermonuclear", NivelRuleta.NIVEL_6, "Creeper Termonuclear",
                "Creeper acorazado con núcleo de magma y placas de obsidiana llorosa. Al entrar en combate su temperatura interna sube constantemente; si supera el 70% emite un aura de calor que quema en 4 bloques, y al llegar al 100% detona en una explosión masiva con fuego persistente. Los jugadores pueden arrojarle agua, bolas de nieve o flechas heladas para enfriarlo tácticamente antes de que colapse.",
                (level, player) -> CursosActivos.activarCreeperTermonuclear());

        registrar("colosoruina", NivelRuleta.NIVEL_6, "El Coloso de Ruina",
                "Golem titánico (escala x1.45 y 300 HP) con armadura de basalto y núcleo de magma. Destruye torres de acampar, desata Golpes Sísmicos Radiales esquivables saltando y su armadura frontal reduce un 60% el daño recibido.",
                (level, player) -> CursosActivos.activarColosoRuina());
    }
}
