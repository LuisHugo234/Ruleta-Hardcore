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
    private static volatile boolean apocalipsisZombie         = false;
    private static volatile boolean cuevasTenebrosas          = false;
    private static volatile boolean comidaEchada              = false;
    private static volatile boolean cuccosSalvajes            = false;
    private static volatile boolean cadaveresPisados          = false;
    private static volatile boolean vacaExplosiva             = false;
    private static volatile boolean corrientesAereas          = false;
    private static volatile boolean contaminacionCruzada      = false;
    private static volatile boolean paranoiaNocturna          = false;
    private static volatile boolean refuerzosPodridos         = false;
    private static volatile boolean maldicionYunque           = false;
    private static volatile boolean inventarioPesado          = false;
    private static volatile boolean caidaBrutal               = false;
    private static volatile boolean ojosEnLaOscuridad         = false;

    // Nivel 2
    private static volatile boolean speedpers                 = false;
    private static volatile boolean filoMortal                = false;
    private static volatile boolean muerteEspinosa            = false;
    private static volatile boolean cucarachaPlateada         = false;
    private static volatile boolean abejasAsesinas            = false;
    private static volatile boolean cargaPesada               = false;
    private static volatile boolean vexGrandes                = false;
    private static volatile boolean resistenciaFisica         = false;
    private static volatile boolean escudoPesado              = false;
    private static volatile boolean wingardiumLeviosa         = false;
    private static volatile boolean lavaActiva                = false;
    private static volatile boolean webones                   = false;
    private static volatile boolean fuegoFatuo                = false;
    private static volatile boolean wardensDeepDark           = false;
    private static volatile boolean invasionAzul              = false;
    private static volatile boolean infestacionMinera         = false;
    private static volatile boolean mineriaInestable          = false;
    private static volatile boolean telaranaAlFallo           = false;
    private static volatile boolean brujaReflejante           = false;
    private static volatile boolean gritoPrimal               = false;

    // Nivel 3
    private static volatile boolean errorTotemico             = false;
    private static volatile boolean artesMaritimos            = false;
    private static volatile boolean boogieWoogie              = false;
    private static volatile boolean energiaPoseica            = false;
    private static volatile boolean muerteInstantanea         = false;
    private static volatile boolean manzanaRegeneradora       = false;
    private static volatile boolean magmaContacto             = false;
    private static volatile boolean sinOxigeno                = false;
    private static volatile boolean fallaMecanica             = false;
    private static volatile boolean adivinaQuien              = false;
    private static volatile boolean creepersEscondidos        = false;
    private static volatile boolean monturaAcuatica           = false;
    private static volatile boolean pykete                    = false;
    private static volatile boolean fragmentacionHelada       = false;
    private static volatile boolean comoPedro                 = false;
    private static volatile boolean creeperAvispon            = false;
    private static volatile boolean hombreRoca                = false;

    // Nivel 4
    private static volatile boolean danoColateral             = false;
    private static volatile boolean ultraFundido              = false;
    private static volatile boolean contaminacion             = false;
    private static volatile boolean congelacionProfunda       = false;
    private static volatile boolean escopeta                  = false;
    private static volatile boolean reyDeLoProfundo           = false;
    private static volatile boolean desiertoAbrasador         = false;
    private static volatile boolean ataqueSlimes              = false;
    private static volatile boolean tensionMuscular           = false;
    private static volatile boolean colosalFusion             = false;
    private static volatile boolean cazadorIgneo              = false;

    // Nivel 5
    private static volatile boolean intrusosCalavericos       = false;
    private static volatile boolean spiderman                 = false;
    private static volatile boolean enderGhast                = false;
    private static volatile boolean kaBoom                    = false;
    private static volatile boolean gigaEndermite             = false;
    private static volatile boolean megaHoglin                = false;
    private static volatile boolean creeperCosmico            = false;
    private static volatile boolean torbellinoIgneo           = false;
    private static volatile boolean phantomAlfa               = false;
    private static volatile boolean cegueraAbisal             = false;
    private static volatile boolean fallaDimensional          = false;
    private static volatile boolean multicreeper              = false;
    private static volatile boolean sombraParpadeante         = false;
    private static volatile boolean berserker                 = false;
    private static volatile boolean nigromanteCriptas         = false;
    private static volatile boolean creeperFantasma           = false;

    // Nivel 6
    private static volatile boolean permadeath                = false;
    private static volatile boolean apocalipsisMinecraftTotal = false;
    private static volatile boolean maldicionLunaDeSangre     = false;
    private static volatile boolean eclipseSolar              = false;
    private static volatile boolean elVerdugo                 = false;
    private static volatile boolean maresAcidos               = false;
    private static volatile boolean pareceQueVaALlover        = false;
    private static volatile boolean anomaliaGravitacional     = false;
    private static volatile boolean corrupcionVacio           = false;
    private static volatile boolean creeperAntimateria        = false;
    private static volatile boolean creeperTermonuclear       = false;
    private static volatile boolean colosoRuina               = false;

    // Estados dinámicos de eventos temporales (Luna de Sangre / Eclipse Solar)
    private static volatile boolean isBloodMoonActive         = false;
    private static volatile long ultimoDiaBloodMoon           = -1L;
    private static volatile boolean isEclipseActive           = false;
    private static volatile long ultimoDiaEclipse             = -1L;

    // Configuración global de administración y vidas
    private static volatile boolean mundoConfigurado          = false;
    private static volatile boolean ruletaAutomatica          = true;
    private static volatile int vidasIniciales                = 3;

    public static boolean isMundoConfigurado() { return mundoConfigurado; }
    public static void setMundoConfigurado(boolean configurado) { mundoConfigurado = configurado; }

    public static boolean isRuletaAutomatica() { return ruletaAutomatica; }
    public static void setRuletaAutomatica(boolean automatica) { ruletaAutomatica = automatica; }

    public static int getVidasIniciales() { return vidasIniciales; }
    public static void setVidasIniciales(int vidas) { vidasIniciales = Math.max(1, Math.min(20, vidas)); }

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

    public static void activarComidaEchada() { comidaEchada = true; }
    public static boolean isComidaEchada() { return comidaEchada; }

    public static void activarCuccosSalvajes() { cuccosSalvajes = true; }
    public static boolean isCuccosSalvajes() { return cuccosSalvajes; }

    public static void activarCadaveresPisados() { cadaveresPisados = true; }
    public static boolean isCadaveresPisados() { return cadaveresPisados; }

    public static void activarVacaExplosiva() { vacaExplosiva = true; }
    public static boolean isVacaExplosiva() { return vacaExplosiva; }

    public static void activarCorrientesAereas() { corrientesAereas = true; }
    public static boolean isCorrientesAereas() { return corrientesAereas; }

    public static void activarInvasionAzul() { invasionAzul = true; }
    public static boolean isInvasionAzul() { return invasionAzul; }

    public static void activarParanoiaNocturna() { paranoiaNocturna = true; }
    public static boolean isParanoiaNocturna() { return paranoiaNocturna; }

    public static void activarRefuerzosPodridos() { refuerzosPodridos = true; }
    public static boolean isRefuerzosPodridos() { return refuerzosPodridos; }

    public static void activarMaldicionYunque() { maldicionYunque = true; }
    public static boolean isMaldicionYunque() { return maldicionYunque; }

    public static void activarInventarioPesado() { inventarioPesado = true; }
    public static boolean isInventarioPesado() { return inventarioPesado; }

    public static void activarCaidaBrutal() { caidaBrutal = true; }
    public static boolean isCaidaBrutal() { return caidaBrutal; }

    public static void activarOjosEnLaOscuridad() { ojosEnLaOscuridad = true; }
    public static boolean isOjosEnLaOscuridad() { return ojosEnLaOscuridad; }

    // --- Nivel 2 ---
    public static void activarSpeedpers() { speedpers = true; }
    public static boolean isSpeedpers() { return speedpers; }

    public static void activarFiloMortal() { filoMortal = true; }
    public static boolean isFiloMortal() { return filoMortal; }
    public static boolean tieneFiloMortal(UUID playerId) { return filoMortal; }

    public static void activarMuerteEspinosa() { muerteEspinosa = true; }
    public static boolean isMuerteEspinosa() { return muerteEspinosa; }

    public static void activarCucarachaPlateada() { cucarachaPlateada = true; }
    public static boolean isCucarachaPlateada() { return cucarachaPlateada; }

    public static void activarAbejasAsesinas() { abejasAsesinas = true; }
    public static boolean isAbejasAsesinas() { return abejasAsesinas; }

    public static void activarCargaPesada() { cargaPesada = true; }
    public static boolean isCargaPesada() { return cargaPesada; }

    public static void activarVexGrandes() { vexGrandes = true; }
    public static boolean isVexGrandes() { return vexGrandes; }

    public static void activarResistenciaFisica() { resistenciaFisica = true; }
    public static boolean isResistenciaFisica() { return resistenciaFisica; }

    public static void activarEscudoPesado() { escudoPesado = true; }
    public static boolean isEscudoPesado() { return escudoPesado; }

    public static void activarWingardiumLeviosa() { wingardiumLeviosa = true; }
    public static boolean isWingardiumLeviosa() { return wingardiumLeviosa; }

    public static void activarLavaActiva() { lavaActiva = true; }
    public static boolean isLavaActiva() { return lavaActiva; }

    public static void activarWebones() { webones = true; }
    public static boolean isWebones() { return webones; }

    public static void activarMineriaInestable() { mineriaInestable = true; }
    public static boolean isMineriaInestable() { return mineriaInestable; }

    public static void activarTelaranaAlFallo() { telaranaAlFallo = true; }
    public static boolean isTelaranaAlFallo() { return telaranaAlFallo; }

    public static void activarBrujaReflejante() { brujaReflejante = true; }
    public static boolean isBrujaReflejante() { return brujaReflejante; }

    public static void activarGritoPrimal() { gritoPrimal = true; }
    public static boolean isGritoPrimal() { return gritoPrimal; }

    // --- Nivel 3 ---
    public static void activarErrorTotemico() { errorTotemico = true; }
    public static boolean isErrorTotemico() { return errorTotemico; }
    public static boolean tieneErrorTotemico(UUID playerId) { return errorTotemico; }

    public static void activarArtesMaritimos() { artesMaritimos = true; }
    public static boolean isArtesMaritimos() { return artesMaritimos; }

    public static void activarBoogieWoogie() { boogieWoogie = true; }
    public static boolean isBoogieWoogie() { return boogieWoogie; }

    public static void activarEnergiaPoseica() { energiaPoseica = true; }
    public static boolean isEnergiaPoseica() { return energiaPoseica; }

    public static void activarInfestacionMinera() { infestacionMinera = true; }
    public static boolean isInfestacionMinera() { return infestacionMinera; }

    public static void activarMuerteInstantanea() { muerteInstantanea = true; }
    public static boolean isMuerteInstantanea() { return muerteInstantanea; }

    public static void activarManzanaRegeneradora() { manzanaRegeneradora = true; }
    public static boolean isManzanaRegeneradora() { return manzanaRegeneradora; }

    public static void activarMagmaContacto() { magmaContacto = true; }
    public static boolean isMagmaContacto() { return magmaContacto; }

    public static void activarContaminacionCruzada() { contaminacionCruzada = true; }
    public static boolean isContaminacionCruzada() { return contaminacionCruzada; }

    public static void activarSinOxigeno() { sinOxigeno = true; }
    public static boolean isSinOxigeno() { return sinOxigeno; }

    public static void activarFallaMecanica() { fallaMecanica = true; }
    public static boolean isFallaMecanica() { return fallaMecanica; }

    public static void activarAdivinaQuien() { adivinaQuien = true; }
    public static boolean isAdivinaQuien() { return adivinaQuien; }

    public static void activarPykete() { pykete = true; }
    public static boolean isPykete() { return pykete; }

    public static void activarFragmentacionHelada() { fragmentacionHelada = true; }
    public static boolean isFragmentacionHelada() { return fragmentacionHelada; }

    public static void activarComoPedro() { comoPedro = true; }
    public static boolean isComoPedro() { return comoPedro; }

    public static void activarCreeperAvispon() { creeperAvispon = true; }
    public static boolean isCreeperAvispon() { return creeperAvispon; }

    public static void activarHombreRoca() { hombreRoca = true; }
    public static boolean isHombreRoca() { return hombreRoca; }

    // --- Nivel 4 ---
    public static void activarDanoColateral() { danoColateral = true; }
    public static boolean isDanoColateral() { return danoColateral; }
    public static boolean tieneDanoColateral(UUID playerId) { return danoColateral; }

    public static void activarUltraFundido() { ultraFundido = true; }
    public static boolean isUltraFundido() { return ultraFundido; }
    public static boolean tieneUltraFundido(UUID playerId) { return ultraFundido; }

    public static void activarContaminacion() { contaminacion = true; }
    public static boolean isContaminacion() { return contaminacion; }

    public static void activarCongelacionProfunda() { congelacionProfunda = true; }
    public static boolean isCongelacionProfunda() { return congelacionProfunda; }

    public static void activarEscopeta() { escopeta = true; }
    public static boolean isEscopeta() { return escopeta; }

    public static void activarReyDeLoProfundo() { reyDeLoProfundo = true; }
    public static boolean isReyDeLoProfundo() { return reyDeLoProfundo; }

    public static void activarDesiertoAbrasador() { desiertoAbrasador = true; }
    public static boolean isDesiertoAbrasador() { return desiertoAbrasador; }

    public static void activarTensionMuscular() { tensionMuscular = true; }
    public static boolean isTensionMuscular() { return tensionMuscular; }

    public static void activarColosalFusion() { colosalFusion = true; }
    public static boolean isColosalFusion() { return colosalFusion; }

    public static void activarCazadorIgneo() { cazadorIgneo = true; }
    public static boolean isCazadorIgneo() { return cazadorIgneo; }

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

    public static void activarCreepersEscondidos() { creepersEscondidos = true; }
    public static boolean isCreepersEscondidos() { return creepersEscondidos; }

    public static void activarCreeperCosmico() { creeperCosmico = true; }
    public static boolean isCreeperCosmico() { return creeperCosmico; }

    public static void activarTorbellinoIgneo() { torbellinoIgneo = true; }
    public static boolean isTorbellinoIgneo() { return torbellinoIgneo; }

    public static void activarPhantomAlfa() { phantomAlfa = true; }
    public static boolean isPhantomAlfa() { return phantomAlfa; }

    public static void activarCegueraAbisal() { cegueraAbisal = true; }
    public static boolean isCegueraAbisal() { return cegueraAbisal; }

    public static void activarFallaDimensional() { fallaDimensional = true; }
    public static boolean isFallaDimensional() { return fallaDimensional; }

    public static void activarMulticreeper() { multicreeper = true; }
    public static boolean isMulticreeper() { return multicreeper; }

    public static void activarSombraParpadeante() { sombraParpadeante = true; }
    public static boolean isSombraParpadeante() { return sombraParpadeante; }

    public static void activarBerserker() { berserker = true; }
    public static boolean isBerserker() { return berserker; }

    public static void activarNigromanteCriptas() { nigromanteCriptas = true; }
    public static boolean isNigromanteCriptas() { return nigromanteCriptas; }

    public static void activarCreeperFantasma() { creeperFantasma = true; }
    public static boolean isCreeperFantasma() { return creeperFantasma; }

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

    public static void activarEclipseSolar() { eclipseSolar = true; }
    public static boolean isEclipseSolar() { return eclipseSolar; }

    public static void activarElVerdugo() { elVerdugo = true; }
    public static boolean isElVerdugo() { return elVerdugo; }

    public static void activarMaresAcidos() { maresAcidos = true; }
    public static boolean isMaresAcidos() { return maresAcidos; }

    public static void activarPareceQueVaALlover() { pareceQueVaALlover = true; }
    public static boolean isPareceQueVaALlover() { return pareceQueVaALlover; }

    public static void activarAnomaliaGravitacional() { anomaliaGravitacional = true; }
    public static boolean isAnomaliaGravitacional() { return anomaliaGravitacional; }

    public static void activarCorrupcionVacio() { corrupcionVacio = true; }
    public static boolean isCorrupcionVacio() { return corrupcionVacio; }

    public static void activarCreeperAntimateria() { creeperAntimateria = true; }
    public static boolean isCreeperAntimateria() { return creeperAntimateria; }

    public static void activarCreeperTermonuclear() { creeperTermonuclear = true; }
    public static boolean isCreeperTermonuclear() { return creeperTermonuclear; }

    public static void activarColosoRuina() { colosoRuina = true; }
    public static boolean isColosoRuina() { return colosoRuina; }

    public static void setBloodMoonActive(boolean active) { isBloodMoonActive = active; }
    public static boolean isBloodMoonActive() { return isBloodMoonActive; }

    public static void setUltimoDiaBloodMoon(long dia) { ultimoDiaBloodMoon = dia; }
    public static long getUltimoDiaBloodMoon() { return ultimoDiaBloodMoon; }

    public static void setEclipseActive(boolean active) { isEclipseActive = active; }
    public static boolean isEclipseActive() { return isEclipseActive; }

    public static void setUltimoDiaEclipse(long dia) { ultimoDiaEclipse = dia; }
    public static long getUltimoDiaEclipse() { return ultimoDiaEclipse; }

    public static boolean hayMaldicionesActivas() {
        return fuegoFatuo || creepersEnPerla || speedpers || wardensDeepDark
                || apocalipsisZombie || cuevasTenebrosas || comidaEchada || cuccosSalvajes
                || cadaveresPisados || vacaExplosiva || corrientesAereas || invasionAzul
                || paranoiaNocturna || refuerzosPodridos || maldicionYunque || inventarioPesado
                || caidaBrutal || ojosEnLaOscuridad
                || filoMortal || muerteEspinosa || cucarachaPlateada || abejasAsesinas
                || cargaPesada || vexGrandes || resistenciaFisica || escudoPesado
                || wingardiumLeviosa || lavaActiva || webones || mineriaInestable
                || telaranaAlFallo || brujaReflejante || gritoPrimal
                || errorTotemico || artesMaritimos || boogieWoogie || energiaPoseica
                || infestacionMinera || muerteInstantanea || manzanaRegeneradora
                || magmaContacto || contaminacionCruzada || sinOxigeno || fallaMecanica || adivinaQuien
                || pykete || fragmentacionHelada || comoPedro
                || danoColateral || ultraFundido || contaminacion
                || congelacionProfunda || escopeta || reyDeLoProfundo || desiertoAbrasador || tensionMuscular
                || colosalFusion || cazadorIgneo
                || intrusosCalavericos || spiderman || enderGhast || kaBoom
                || ataqueSlimes || gigaEndermite || megaHoglin || creepersEscondidos || creeperCosmico
                || torbellinoIgneo || phantomAlfa || cegueraAbisal || fallaDimensional || multicreeper
                || sombraParpadeante || berserker || nigromanteCriptas || creeperFantasma
                || permadeath || apocalipsisMinecraftTotal || maldicionLunaDeSangre
                || monturaAcuatica || eclipseSolar || elVerdugo || maresAcidos || pareceQueVaALlover
                || anomaliaGravitacional || corrupcionVacio || creeperAntimateria || creeperTermonuclear || colosoRuina;
    }

    public static List<String> obtenerIdsActivas() {
        List<String> ids = new ArrayList<>();
        if (creepersEnPerla) ids.add("creepersperla");
        if (fuegoFatuo) ids.add("fuegofatuo");
        if (apocalipsisZombie) ids.add("apocalipsis");
        if (wardensDeepDark) ids.add("wardens");
        if (cuevasTenebrosas) ids.add("cuevastenebrosas");
        if (comidaEchada) ids.add("comidaechada");
        if (cuccosSalvajes) ids.add("cuccossalvajes");
        if (cadaveresPisados) ids.add("cadaverespisados");
        if (vacaExplosiva) ids.add("vacaexplosiva");
        if (corrientesAereas) ids.add("corrientesaereas");
        if (invasionAzul) ids.add("invasionazul");
        if (paranoiaNocturna) ids.add("paranoianocturna");
        if (refuerzosPodridos) ids.add("refuerzospodridos");
        if (maldicionYunque) ids.add("maldicionyunque");
        if (inventarioPesado) ids.add("inventariopesado");
        if (caidaBrutal) ids.add("caidabrutal");
        if (ojosEnLaOscuridad) ids.add("ojosenlaoscuridad");
        if (speedpers) ids.add("speedpers");
        if (filoMortal) ids.add("filomortal");
        if (muerteEspinosa) ids.add("muerteespinosa");
        if (cucarachaPlateada) ids.add("cucarachaplateada");
        if (abejasAsesinas) ids.add("abejasasesinas");
        if (cargaPesada) ids.add("cargapesada");
        if (vexGrandes) ids.add("vexgrandes");
        if (resistenciaFisica) ids.add("resistenciafisica");
        if (escudoPesado) ids.add("escudopesado");
        if (wingardiumLeviosa) ids.add("wingardiumleviosa");
        if (lavaActiva) ids.add("lavaactiva");
        if (webones) ids.add("webones");
        if (mineriaInestable) ids.add("mineriainestable");
        if (telaranaAlFallo) ids.add("telaranaalfallo");
        if (brujaReflejante) ids.add("brujareflejante");
        if (gritoPrimal) ids.add("gritoprimal");
        if (errorTotemico) ids.add("errortotemico");
        if (artesMaritimos) ids.add("artesmaritimos");
        if (boogieWoogie) ids.add("boogiewoogie");
        if (energiaPoseica) ids.add("energiaposeica");
        if (infestacionMinera) ids.add("infestacionminera");
        if (muerteInstantanea) ids.add("muerteinstantanea");
        if (manzanaRegeneradora) ids.add("manzanaregeneradora");
        if (magmaContacto) ids.add("magmacontacto");
        if (contaminacionCruzada) ids.add("contaminacioncruzada");
        if (sinOxigeno) ids.add("sinoxigeno");
        if (fallaMecanica) ids.add("fallamecanica");
        if (adivinaQuien) ids.add("adivinaquien");
        if (pykete) ids.add("pykete");
        if (fragmentacionHelada) ids.add("fragmentacionhelada");
        if (comoPedro) ids.add("comopedro");
        if (creeperAvispon) ids.add("creeperavispon");
        if (hombreRoca) ids.add("hombreroca");
        if (danoColateral) ids.add("danocolateral");
        if (ultraFundido) ids.add("ultrafundido");
        if (contaminacion) ids.add("contaminacion");
        if (congelacionProfunda) ids.add("congelacionprofunda");
        if (escopeta) ids.add("escopeta");
        if (reyDeLoProfundo) ids.add("reydeloprofundo");
        if (desiertoAbrasador) ids.add("desiertoabrasador");
        if (tensionMuscular) ids.add("tensionmuscular");
        if (colosalFusion) ids.add("colosalfusion");
        if (cazadorIgneo) ids.add("cazadorigneo");
        if (intrusosCalavericos) ids.add("intrusoscalavericos");
        if (spiderman) ids.add("spiderman");
        if (enderGhast) ids.add("enderghast");
        if (kaBoom) ids.add("kaboom");
        if (ataqueSlimes) ids.add("ataqueslimes");
        if (gigaEndermite) ids.add("gigaendermite");
        if (megaHoglin) ids.add("megahoglin");
        if (creepersEscondidos) ids.add("creepersescondidos");
        if (creeperCosmico) ids.add("creepercosmico");
        if (torbellinoIgneo) ids.add("torbellinoigneo");
        if (phantomAlfa) ids.add("phantomalfa");
        if (cegueraAbisal) ids.add("cegueraabisal");
        if (fallaDimensional) ids.add("falladimensional");
        if (multicreeper) ids.add("multicreeper");
        if (sombraParpadeante) ids.add("sombraparpadeante");
        if (berserker) ids.add("berserker");
        if (nigromanteCriptas) ids.add("nigromantecriptas");
        if (creeperFantasma) ids.add("creeperfantasma");
        if (permadeath) ids.add("permadeath");
        if (apocalipsisMinecraftTotal) ids.add("apocalipsis6");
        if (maldicionLunaDeSangre) ids.add("lunasangre");
        if (monturaAcuatica) ids.add("monturaacuatica");
        if (eclipseSolar) ids.add("eclipsesolar");
        if (elVerdugo) ids.add("elverdugo");
        if (maresAcidos) ids.add("maresacidos");
        if (pareceQueVaALlover) ids.add("parecequevallover");
        if (anomaliaGravitacional) ids.add("anomaliagravitacional");
        if (corrupcionVacio) ids.add("corrupcionvacio");
        if (creeperAntimateria) ids.add("creeperantimateria");
        if (creeperTermonuclear) ids.add("creepertermonuclear");
        if (colosoRuina) ids.add("colosoruina");
        return ids;
    }

    public static void activarPorId(String id) {
        switch (id.toLowerCase()) {
            case "creepersperla" -> activarCreepersEnPerla();
            case "fuegofatuo" -> activarFuegoFatuo();
            case "apocalipsis" -> activarApocalipsisZombie();
            case "wardens" -> activarWardensDeepDark();
            case "cuevastenebrosas" -> activarCuevasTenebrosas();
            case "comidaechada" -> activarComidaEchada();
            case "cuccossalvajes" -> activarCuccosSalvajes();
            case "cadaverespisados" -> activarCadaveresPisados();
            case "vacaexplosiva" -> activarVacaExplosiva();
            case "corrientesaereas" -> activarCorrientesAereas();
            case "invasionazul" -> activarInvasionAzul();
            case "paranoianocturna" -> activarParanoiaNocturna();
            case "refuerzospodridos" -> activarRefuerzosPodridos();
            case "maldicionyunque" -> activarMaldicionYunque();
            case "inventariopesado" -> activarInventarioPesado();
            case "caidabrutal" -> activarCaidaBrutal();
            case "ojosenlaoscuridad" -> activarOjosEnLaOscuridad();
            case "speedpers" -> activarSpeedpers();
            case "filomortal" -> activarFiloMortal();
            case "muerteespinosa" -> activarMuerteEspinosa();
            case "cucarachaplateada" -> activarCucarachaPlateada();
            case "abejasasesinas" -> activarAbejasAsesinas();
            case "cargapesada" -> activarCargaPesada();
            case "vexgrandes" -> activarVexGrandes();
            case "resistenciafisica" -> activarResistenciaFisica();
            case "escudopesado" -> activarEscudoPesado();
            case "wingardiumleviosa" -> activarWingardiumLeviosa();
            case "lavaactiva" -> activarLavaActiva();
            case "webones" -> activarWebones();
            case "mineriainestable" -> activarMineriaInestable();
            case "telaranaalfallo" -> activarTelaranaAlFallo();
            case "brujareflejante" -> activarBrujaReflejante();
            case "gritoprimal" -> activarGritoPrimal();
            case "errortotemico" -> activarErrorTotemico();
            case "artesmaritimos" -> activarArtesMaritimos();
            case "boogiewoogie" -> activarBoogieWoogie();
            case "energiaposeica" -> activarEnergiaPoseica();
            case "infestacionminera" -> activarInfestacionMinera();
            case "muerteinstantanea" -> activarMuerteInstantanea();
            case "manzanaregeneradora" -> activarManzanaRegeneradora();
            case "magmacontacto" -> activarMagmaContacto();
            case "contaminacioncruzada" -> activarContaminacionCruzada();
            case "sinoxigeno" -> activarSinOxigeno();
            case "fallamecanica" -> activarFallaMecanica();
            case "adivinaquien" -> activarAdivinaQuien();
            case "pykete" -> activarPykete();
            case "fragmentacionhelada" -> activarFragmentacionHelada();
            case "comopedro" -> activarComoPedro();
            case "creeperavispon" -> activarCreeperAvispon();
            case "hombreroca" -> activarHombreRoca();
            case "danocolateral" -> activarDanoColateral();
            case "ultrafundido" -> activarUltraFundido();
            case "contaminacion" -> activarContaminacion();
            case "congelacionprofunda" -> activarCongelacionProfunda();
            case "escopeta" -> activarEscopeta();
            case "reydeloprofundo" -> activarReyDeLoProfundo();
            case "desiertoabrasador" -> activarDesiertoAbrasador();
            case "tensionmuscular" -> activarTensionMuscular();
            case "colosalfusion" -> activarColosalFusion();
            case "cazadorigneo" -> activarCazadorIgneo();
            case "intrusoscalavericos" -> activarIntrusosCalavericos();
            case "spiderman" -> activarSpiderman();
            case "enderghast" -> activarEnderGhast();
            case "kaboom" -> activarKaBoom();
            case "ataqueslimes" -> activarAtaqueSlimes();
            case "gigaendermite" -> activarGigaEndermite();
            case "megahoglin" -> activarMegaHoglin();
            case "creepersescondidos" -> activarCreepersEscondidos();
            case "creepercosmico" -> activarCreeperCosmico();
            case "torbellinoigneo" -> activarTorbellinoIgneo();
            case "phantomalfa" -> activarPhantomAlfa();
            case "cegueraabisal" -> activarCegueraAbisal();
            case "falladimensional" -> activarFallaDimensional();
            case "multicreeper" -> activarMulticreeper();
            case "sombraparpadeante" -> activarSombraParpadeante();
            case "berserker" -> activarBerserker();
            case "nigromantecriptas" -> activarNigromanteCriptas();
            case "creeperfantasma" -> activarCreeperFantasma();
            case "permadeath" -> activarPermadeath();
            case "apocalipsis6", "apocalipsistotal" -> activarApocalipsisMinecraftTotal();
            case "lunasangre", "bloodmoon" -> activarLunaDeSangre();
            case "monturaacuatica" -> activarMonturaAcuatica();
            case "eclipsesolar", "eclipse" -> activarEclipseSolar();
            case "elverdugo", "verdugo" -> activarElVerdugo();
            case "maresacidos" -> activarMaresAcidos();
            case "parecequevallover" -> activarPareceQueVaALlover();
            case "anomaliagravitacional" -> activarAnomaliaGravitacional();
            case "corrupcionvacio" -> activarCorrupcionVacio();
            case "creeperantimateria" -> activarCreeperAntimateria();
            case "creepertermonuclear" -> activarCreeperTermonuclear();
            case "colosoruina", "coloso_ruina" -> activarColosoRuina();
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
            lista.add(Component.literal("  §8🌑 CUEVAS TENEBROSAS:§r Recibes Oscuridad por debajo de Y=0 (se anula con faroles o se atenúa con antorchas en cualquier mano)."));
        }
        if (comidaEchada) {
            lista.add(Component.literal("  §2🍖 COMIDA ECHADA A PERDER:§r 5% de probabilidad de recibir Infestado por 15s al comer alimentos (excepto dorados o cocinados)."));
        }
        if (cuccosSalvajes) {
            lista.add(Component.literal("  §f🐔 CUCCOS SALVAJES:§r Los pollos tienen 15 de vida y te contraatacan quitando 1 corazón por golpe."));
        }
        if (cadaveresPisados) {
            lista.add(Component.literal("  §8💀 CADÁVERES PISADOS:§r Pisar arena de almas otorga Debilidad I."));
        }
        if (vacaExplosiva) {
            lista.add(Component.literal("  §c🐄 VACA EXPLOSIVA:§r Las vacas tienen 1% de probabilidad de explotar al morir."));
        }
        if (corrientesAereas) {
            lista.add(Component.literal("  §b🌪 CORRIENTES AÉREAS:§r Los Breeze ahora disparan ráfagas de 6 Wind Charges."));
        }
        if (invasionAzul) {
            lista.add(Component.literal("  §9🌬 INVASIÓN AZUL:§r Los Breeze pueden aparecer de día en cualquier bioma del Overworld (15% de probabilidad, máx. 3 en 32 bloques)."));
        }
        if (paranoiaNocturna) {
            lista.add(Component.literal("  §0§l👁 PARANOIA NOCTURNA:§r Estar a oscuras (luz 0) >15s provoca sonidos falsos de creepers/zombies y Ceguera I."));
        }
        if (refuerzosPodridos) {
            lista.add(Component.literal("  §2🧟 REFUERZOS PODRIDOS:§r Al morir un zombi, tiene un 10% de probabilidad de invocar un zombi bebé."));
        }
        if (maldicionYunque) {
            lista.add(Component.literal("  §8🔨 LA MALDICIÓN DEL YUNQUE:§r Los yunques tienen un 10% de probabilidad de explotar al romperse por desgaste."));
        }
        if (inventarioPesado) {
            lista.add(Component.literal("  §7🎒 INVENTARIO PESADO:§r Si tu inventario está completamente lleno, no puedes correr."));
        }
        if (caidaBrutal) {
            lista.add(Component.literal("  §c🥾 CAÍDA BRUTAL:§r El daño por caída aumenta un 25%, sin importar la armadura que lleves."));
        }
        if (ojosEnLaOscuridad) {
            lista.add(Component.literal("  §5👁 OJOS EN LA OSCURIDAD:§r Los Enderman se vuelven agresivos si te acercas a menos de 3 bloques, incluso sin mirarlos."));
        }
        if (speedpers) {
            lista.add(Component.literal("  §e⚡ SPEEDPERS:§r Todos los Creepers del Overworld (excepto Creepers Escondidos) tienen Velocidad II."));
        }
        if (filoMortal) {
            lista.add(Component.literal("  §c⚔ FILO MORTAL:§r Todos los zombies están armados con una espada de hierro con Filo III."));
        }
        if (muerteEspinosa) {
            lista.add(Component.literal("  §2🌵 MUERTE ESPINOSA:§r Todos los cactus infligen 1x10^9 de daño al contacto."));
        }
        if (cucarachaPlateada) {
            lista.add(Component.literal("  §7🪲 CUCARACHA PLATEADA:§r Los lepismas otorgan Lentitud II por 5s al golpearte y alertan a otros lepismas en 12 bloques."));
        }
        if (abejasAsesinas) {
            lista.add(Component.literal("  §e🐝 ABEJAS ASESINAS:§r Las abejas son agresivas con el jugador e infligen Descomposición II por 5 segundos."));
        }
        if (cargaPesada) {
            lista.add(Component.literal("  §8🦏 CARGA PESADA:§r Los Ravagers tienen Lentitud II, 150 corazones de vida y x1.5 su daño base."));
        }
        if (vexGrandes) {
            lista.add(Component.literal("  §b👻 VEX GRANDES:§r Los Vex ahora son el doble de grandes y el doble de fuertes."));
        }
        if (resistenciaFisica) {
            lista.add(Component.literal("  §7🪓 RESISTENCIA FÍSICA:§r Los Vindicadores tienen Resistencia I y Resistencia al Fuego."));
        }
        if (escudoPesado) {
            lista.add(Component.literal("  §6🛡 ESCUDO PESADO:§r Bloquear ataques consume el doble de comida y se deshabilita el escudo al recibir 5 golpes en 10s."));
        }
        if (wingardiumLeviosa) {
            lista.add(Component.literal("  §d🪄 WINGARDIUM LEVIOSA:§r Las brujas pueden arrojarte pociones de Levitación XV por 1 segundo."));
        }
        if (lavaActiva) {
            lista.add(Component.literal("  §6🌋 LAVA ACTIVA:§r La lava en el Overworld fluye con la misma velocidad que en el Nether."));
        }
        if (webones) {
            lista.add(Component.literal("  §a🧑‍🌾 WEBONES:§r 25% de probabilidad de que los aldeanos al crecer sean Nitwit (verdes)."));
        }
        if (mineriaInestable) {
            lista.add(Component.literal("  §c⛏ MINERÍA INESTABLE:§r 5% de probabilidad al romper minerales de provocar una explosión pequeña sin destruir bloques."));
        }
        if (telaranaAlFallo) {
            lista.add(Component.literal("  §f🕸 TELARAÑA AL FALLO:§r Cuando una araña salta sobre ti, coloca una telaraña temporal en tus pies por 2.5 segundos."));
        }
        if (brujaReflejante) {
            lista.add(Component.literal("  §d🔮 BRUJA REFLEJANTE:§r Las brujas tienen un 30% de probabilidad de reflejar flechas y tridentes de vuelta hacia ti."));
        }
        if (gritoPrimal) {
            lista.add(Component.literal("  §5🔊 GRITO PRIMAL:§r Los Endermans aturden al jugador que atacan y lo dejan sordo por 30s tras enojarse."));
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
        if (energiaPoseica) {
            lista.add(Component.literal("  §b⚡ ENERGÍA POSEICA:§r Los tridentes de los Ahogados tienen Empalamiento V y Conductividad."));
        }
        if (infestacionMinera) {
            lista.add(Component.literal("  §7⛏ INFESTACIÓN MINERA:§r En las paredes de las Mineshafts se generan bloques de lepisma."));
        }
        if (muerteInstantanea) {
            lista.add(Component.literal("  §4🚪 MUERTE INSTANTÁNEA:§r Todos los botones, trampillas, puertas y puertas de valla de madera hacen 1x10^9 de daño al usarlos."));
        }
        if (manzanaRegeneradora) {
            lista.add(Component.literal("  §e🍎 MANZANAS REGENERADORAS:§r Las manzanas de oro normales ya no otorgan el efecto de Absorción."));
        }
        if (magmaContacto) {
            lista.add(Component.literal("  §6🌋 MAGMA AL CONTACTO:§r Los bloques de magma hacen 1x10^9 de daño al tocarlos directamente desde cualquier ángulo."));
        }
        if (contaminacionCruzada) {
            lista.add(Component.literal("  §2🥩 CONTAMINACIÓN CRUZADA:§r Consumir alimentos crudos otorga Hambre II y Veneno I."));
        }
        if (sinOxigeno) {
            lista.add(Component.literal("  §9🫧 SIN OXÍGENO:§r El oxígeno bajo el agua se agota el triple de rápido."));
        }
        if (fallaMecanica) {
            lista.add(Component.literal("  §7⚙ FALLA MECÁNICA:§r Gólems x1.8 con Fuerza y Velocidad que aumentan con el deterioro; <25% HP atacan a todo ser vivo."));
        }
        if (adivinaQuien) {
            lista.add(Component.literal("  §5🎩 ADIVINA QUIÉN HA VUELTO:§r El Illusioner aparece (25%) en raids, outposts y patrullas con Arco Retroceso X, Poder III y Resistencia III."));
        }
        if (pykete) {
            lista.add(Component.literal("  §3🪝 PYKETE:§r Los tridentes con cuerda de algas de los Ahogados arrastran 8 bloques si no usas escudo y rompen barcos de un golpe."));
        }
        if (fragmentacionHelada) {
            lista.add(Component.literal("  §b❄ FRAGMENTACIÓN HELADA:§r Al morir un Stray, congela el suelo en 4 bloques en hielo resbaladizo y aplica Congelación Profunda."));
        }
        if (comoPedro) {
            lista.add(Component.literal("  §a🌊 COMO PEDRO:§r Los Creepers caminan sobre el agua y mientras la pisan su velocidad se multiplica por 3."));
        }
        if (creeperAvispon) {
            lista.add(Component.literal("  §e🐝 CREEPER AVISPÓN:§r Pequeños creepers voladores del tamaño de una abeja bebé rondan el Overworld. No destruyen bloques y causan 2/3 de daño."));
        }
        if (hombreRoca) {
            lista.add(Component.literal("  §9🪨 HOMBRE ROCA:§r Aparece de día y de noche en cualquier bioma (80 HP, 4 de armadura) arrojando rocas gigantes que infligen 13 de daño, Lentitud II, gran empuje y desactivan el escudo."));
        }
        if (danoColateral) {
            lista.add(Component.literal("  §5💥 DAÑO COLATERAL:§r Las Ender Pearls hacen x3 su daño e ignoran armadura."));
        }
        if (ultraFundido) {
            lista.add(Component.literal("  §6🔥 ULTRA FUNDIDO:§r El fuego hace x10 su daño."));
        }
        if (contaminacion) {
            lista.add(Component.literal("  §a☣ ¿CONTAMINACIÓN?:§r Por encima de la capa 160 hay niebla y humo verde que inflige Veneno V."));
        }
        if (congelacionProfunda) {
            lista.add(Component.literal("  §b❄ CONGELACIÓN PROFUNDA:§r Permanecer >15s en biomas nevados sin calor reduce tu velocidad a la mitad y causa daño progresivo."));
        }
        if (escopeta) {
            lista.add(Component.literal("  §c🔫 ESCOPETA:§r Los Pillagers tienen ballestas con Multidisparo VI, Carga Rápida II y Perforación V."));
        }
        if (reyDeLoProfundo) {
            lista.add(Component.literal("  §3👁 REY DE LO PROFUNDO:§r El Elder Guardian tiene 100 de vida y su rayo aplica Debilidad acumulativa (+1 por golpe) por 20s."));
        }
        if (desiertoAbrasador) {
            lista.add(Component.literal("  §6☀ DESIERTO ABRASADOR:§r Permanecer en el desierto causa insolación (1 corazón cada 2s). Se reinicia con agua o al beber, y se evita con casco (gastando durabilidad)."));
        }
        if (tensionMuscular) {
            lista.add(Component.literal("  §c🏹 TENSIÓN MUSCULAR:§r Tensar arco/ballesta >3s provoca fatiga, lentitud, debilidad II y temblores de puntería hasta soltar el arma."));
        }
        if (colosalFusion) {
            lista.add(Component.literal("  §6☄ COLOSAL DE FUSIÓN:§r Magma Cubes colosales en el Nether que al aterrizar crean cráteres de magma y al morir lanzan crías como morteros."));
        }
        if (cazadorIgneo) {
            lista.add(Component.literal("  §b🏹 CAZADOR ÍGNEO:§r 15% de los Wither Skeletons en el Nether portan arcos de fuego fatuo azul que infligen Wither I por 6s."));
        }
        if (intrusosCalavericos) {
            lista.add(Component.literal("  §8💀 INTRUSOS CALAVÉRICOS:§r Wither Skeletons aparecen de noche en el Overworld con Diamante Prot IV, Espada Filo V, Velocidad I y Fuerza II."));
        }
        if (spiderman) {
            lista.add(Component.literal("  §9🕷 ¿SPIDERMAN?:§r Todas las arañas tienen Fuerza II, Velocidad II, Invisibilidad, Resistencia II y Regeneración II."));
        }
        if (enderGhast) {
            lista.add(Component.literal("  §d👻 ENDER GHAST:§r El Ghast se vuelve más agresivo y sus proyectiles hacen un x3 el daño explosivo."));
        }
        if (kaBoom) {
            lista.add(Component.literal("  §c💣 KA-BOOM:§r Los Shulkers explotan 3 segundos después de morir como una TNT."));
        }
        if (ataqueSlimes) {
            lista.add(Component.literal("  §a🟢 ATAQUE A LOS SLIMES:§r 5% de probabilidad de que aparezca un Slime Gigante en pantanos con x2.5 de daño y mega salto."));
        }
        if (gigaEndermite) {
            lista.add(Component.literal("  §5👾 GIGA-ENDERMITE:§r 5% de probabilidad en el End de un Endermite Gigante con 250 HP, x2 de daño, sin empuje y con embestida."));
        }
        if (megaHoglin) {
            lista.add(Component.literal("  §4🐗 MEGA HOGLIN:§r 5% en el Nether de un Hoglin Gigante (300 HP, x2 daño) cuya embestida genera onda expansiva e impide saltar/colocar bloques por 3s."));
        }
        if (creepersEscondidos) {
            lista.add(Component.literal("  §2🌱 CREEPERS ESCONDIDOS:§r 20% de probabilidad al romper pasto de que aparezca un mini-creeper cabezón con 1/3 de daño explosivo."));
        }
        if (creeperCosmico) {
            lista.add(Component.literal("  §b⚡ CREEPER CÓSMICO:§r En el End puede aparecer (15%) un Creeper Eléctrico lentísimo con atracción gravitacional y explosión de 30 bloques."));
        }
        if (torbellinoIgneo) {
            lista.add(Component.literal("  §6🌪 TORBELLINO ÍGNEO:§r Las bolas de fuego de los Blazes empujan violentamente y crean un charco temporal de 3x3 de lava y fuego."));
        }
        if (phantomAlfa) {
            lista.add(Component.literal("  §9🦅 PHANTOM ALFA:§r Phantoms x2.5 de tamaño, 60 HP, inmunes al sol y con picada sónica que inflige 3 corazones ignorando armadura."));
        }
        if (cegueraAbisal) {
            lista.add(Component.literal("  §8👁 CEGUERA ABISAL:§r En el End o Deep Dark, cualquier golpe de un monstruo aplica Ceguera y Oscuridad por 8s incorregibles con leche."));
        }
        if (fallaDimensional) {
            lista.add(Component.literal("  §4🔥 FALLA DIMENSIONAL:§r Cruzar un portal del Nether (ida o vuelta) tiene un 15% de sobrecargar el portal e invocar 2 Blazes agresivos."));
        }
        if (multicreeper) {
            lista.add(Component.literal("  §a🧬 MULTICREEPER:§r Si un Creeper sobrevive a un golpe cuerpo a cuerpo, tiene 30% de dividirse en 2 mini-creepers con mecha de 1s."));
        }
        if (sombraParpadeante) {
            lista.add(Component.literal("  §5👤 SOMBRA PARPADEANTE:§r Endermans se teletransportan a tu espalda al mirarlos/golpearlos, silencian el ambiente (12 bloques) y fuerzan tu vista."));
        }
        if (berserker) {
            lista.add(Component.literal("  §4🪓 BERSERKER:§r Vindicadores con Netherite (+60% vel. a <15 bloques), ignoran 50% de armadura y rompen escudos por 10s con Lentitud III."));
        }
        if (nigromanteCriptas) {
            lista.add(Component.literal("  §b🧙 EL NIGROMANTE DE LAS CRIPTAS:§r Stray con báculo de hielo que canaliza un rayo congelante al suelo e invoca mini-strays con hacha."));
        }
        if (creeperFantasma) {
            lista.add(Component.literal("  §f👻 CREEPER FANTASMA:§r Creeper espectral con manta translúcida que flota estilo Vex, atraviesa bloques y es inmune a proyectiles."));
        }
        if (permadeath) {
            lista.add(Component.literal("  §4§l☠ PERMADEATH:§r Se ha eliminado la regeneración pasiva de vida."));
        }
        if (apocalipsisMinecraftTotal) {
            lista.add(Component.literal("  §4§l☣ ¡APOCALIPSIS MINECRAFT !!!!:§r Los zombies se apilan en torres, huelen sangre y acuden al ruido al picar."));
        }
        if (maldicionLunaDeSangre) {
            lista.add(Component.literal("  §4§l☾ LUNA DE SANGRE:§r Noches sangrientas sin camas, hordas masivas y mobs con hasta 3 efectos potentes (Fuerza X, Velocidad V, Invisibilidad, Resistencia III, Regeneración III)."));
        }
        if (monturaAcuatica) {
            lista.add(Component.literal("  §3§l🐚 MONTURA ACUÁTICA:§r Los Zombies Ahogados tienen 10% de probabilidad de aparecer montados en un Nautilo o Delfín."));
        }
        if (eclipseSolar) {
            lista.add(Component.literal("  §5§l☀ ECLIPSE SOLAR:§r Probabilidad de Eclipse Solar con Strays en Phantoms, Brujas en Murciélagos y Creakings con Velocidad II."));
        }
        if (elVerdugo) {
            lista.add(Component.literal("  §4§l🪓 EL VERDUGO:§r Piglins Brutos con 80 HP, Netherite Filo VI y golpes críticos que desarman al jugador."));
        }
        if (maresAcidos) {
            lista.add(Component.literal("  §2§l🧪 MARES ÁCIDOS:§r El agua del Overworld es corrosiva verde: inflige 2 corazones/s y triplica el desgaste de armadura."));
        }
        if (pareceQueVaALlover) {
            lista.add(Component.literal("  §e§l⚡ PARECE QUE VA´ LLOVER:§r Tormentas eléctricas frecuentes y riesgo de rayos al portar armadura o herramientas metálicas."));
        }
        if (anomaliaGravitacional) {
            lista.add(Component.literal("  §5🪐 ANOMALÍA GRAVITACIONAL:§r En el End el empuje recibido se triplica y saltar deja al jugador en caída lenta por 3 segundos."));
        }
        if (corrupcionVacio) {
            lista.add(Component.literal("  §d§l🌌 CORRUPCIÓN DEL VACÍO:§r Golpes de Enderman o proyectiles de Shulker desactivan toda tu armadura durante 5 segundos."));
        }
        if (creeperAntimateria) {
            lista.add(Component.literal("  §5§l🕳 CREEPER DE IMPLOSIÓN ANTIMATERIA:§r Raro Creeper negro con partículas del End que al detonar crea un vórtice de succión de 18 bloques por 2.5s antes de colapsar."));
        }
        if (creeperTermonuclear) {
            lista.add(Component.literal("  §6§l☢ CREEPER TERMONUCLEAR:§r Creeper acorazado de magma cuya temperatura sube en combate (>70% quema en 4 bloques, 100% explosión ígnea masiva; enfriable con agua/nieve)."));
        }
        if (colosoRuina) {
            lista.add(Component.literal("  §4§l☄ EL COLOSO DE RUINA:§r Golem titánico (300 HP) con armadura de basalto. Destruye torres de bloques y desata Golpes Sísmicos esquivables saltando."));
        }
        return lista;
    }

    public static List<com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo> obtenerMaldicionesActivasDetalladas() {
        List<com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo> lista = new ArrayList<>();
        // Nivel 1
        if (creepersEnPerla) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(1, "Espejismo de Ender Pearl", "5% de probabilidad de que aparezca un Creeper al lanzar una Ender Pearl."));
        if (apocalipsisZombie) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(1, "¿Apocalipsis Minecraft?", "50% de los zombies escalan paredes como arañas y son inmunes al sol."));
        if (cuevasTenebrosas) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(1, "Cuevas Tenebrosas", "Recibes Oscuridad por debajo de Y=0 (se anula con farol o atenúa con antorcha)."));
        if (comidaEchada) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(1, "Comida Echada a Perder", "5% de probabilidad de recibir Infestado por 15s al comer comida cruda/normal."));
        if (cuccosSalvajes) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(1, "Cuccos Salvajes", "Los pollos tienen 15 de vida y te atacan quitando 1 corazón por golpe."));
        if (cadaveresPisados) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(1, "Cadáveres Pisados", "Pisar arena de almas otorga el efecto de Debilidad I."));
        if (vacaExplosiva) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(1, "Vaca Explosiva", "Las vacas tienen un 1% de probabilidad de explotar al morir."));
        if (corrientesAereas) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(1, "Corrientes Aéreas", "Los Breeze disparan ráfagas de 6 Wind Charges continuas."));
        if (contaminacionCruzada) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(1, "Contaminación Cruzada", "Consumir alimentos crudos otorga Hambre II y Veneno I."));
        if (paranoiaNocturna) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(1, "Paranoia Nocturna", "Estar con nivel de luz 0 por >15s provoca sonidos falsos y Ceguera I."));
        if (refuerzosPodridos) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(1, "Refuerzos Podridos", "Al morir un zombi, tiene un 10% de probabilidad de invocar un zombi bebé."));
        if (maldicionYunque) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(1, "La Maldición del Yunque", "Los yunques tienen un 10% de probabilidad de explotar al romperse por desgaste."));
        if (inventarioPesado) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(1, "Inventario Pesado", "Si tienes todos los slots del inventario ocupados, no puedes correr."));
        if (caidaBrutal) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(1, "Caída Brutal", "El daño por caída aumenta un 25%, sin importar la armadura que lleves."));
        if (ojosEnLaOscuridad) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(1, "Ojos en la Oscuridad", "Los Enderman pueden volverse agresivos si te acercas a menos de 3 bloques."));

        // Nivel 2
        if (speedpers) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Speedpers", "Todos los Creepers del Overworld tienen Velocidad II permanente."));
        if (filoMortal) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Filo Mortal", "Todos los zombies empuñan espadas de hierro con Filo III."));
        if (muerteEspinosa) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Muerte Espinosa", "Todos los cactus infligen 1x10^9 de daño al contacto."));
        if (cucarachaPlateada) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Cucaracha Plateada", "Lepismas dan Lentitud II por 5s y alertan a otros en 12 bloques."));
        if (abejasAsesinas) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Abejas Asesinas", "Las abejas son agresivas e infligen Descomposición II por 5 segundos."));
        if (cargaPesada) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Carga Pesada", "Los Ravagers tienen Lentitud II, 150 corazones de vida y x1.5 su daño base."));
        if (vexGrandes) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Vex Grandes", "Los Vex son el doble de grandes y el doble de fuertes."));
        if (resistenciaFisica) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Resistencia Física", "Los Vindicadores tienen Resistencia I y Resistencia al Fuego."));
        if (escudoPesado) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Escudo Pesado", "Usar el escudo consume el doble de comida y se deshabilita con 5 golpes seguidos."));
        if (wingardiumLeviosa) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Wingardium Leviosa", "Las brujas te pueden arrojar pociones de Levitación XV por 1 segundo."));
        if (lavaActiva) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Lava Activa", "La lava en el Overworld se mueve con la misma velocidad que en el Nether."));
        if (webones) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Webones", "25% de probabilidad de que los aldeanos al crecer sean Nitwit (verdes)."));
        if (fuegoFatuo) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Fuego Fatuo", "Necesitas armadura completa en el Nether o arderás hasta morir."));
        if (wardensDeepDark) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Wardens en Deep Dark", "Los Wardens pueden aparecer naturalmente en el Deep Dark."));
        if (invasionAzul) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Invasión Azul", "Los Breeze pueden aparecer de día en biomas del Overworld (15%)."));
        if (infestacionMinera) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Infestación Minera", "En las paredes de las Mineshafts se generan bloques de lepisma."));
        if (mineriaInestable) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Minería Inestable", "5% de probabilidad de pequeña explosión sin destruir bloques al minar."));
        if (telaranaAlFallo) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Telaraña al Fallo", "Cuando una araña salta sobre un jugador, coloca una telaraña temporal por 2.5s."));
        if (brujaReflejante) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Bruja Reflejante", "30% de probabilidad de que las brujas repelan proyectiles de vuelta hacia el jugador."));
        if (gritoPrimal) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(2, "Grito Primal", "Los Endermans aturden al jugador que atacan y lo dejan sordo durante 30 segundos tras enojarse."));

        // Nivel 3
        if (errorTotemico) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Error Totémico", "Los Tótems de Inmortalidad tienen un 2% de probabilidad de fallar."));
        if (artesMaritimos) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Artes Marítimos", "Todos los Zombies Ahogados aparecen armados con Tridentes."));
        if (boogieWoogie) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Boogie Woogie", "Los esqueletos intercambian posición contigo cuando su flecha te impacta."));
        if (energiaPoseica) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Energía Poseica", "Los tridentes de los Ahogados tienen Empalamiento V y Conductividad."));
        if (muerteInstantanea) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Muerte Instantánea", "Botones, trampillas, puertas y puertas de valla de madera hacen 1x10^9 de daño."));
        if (manzanaRegeneradora) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Manzanas Regeneradoras", "Las manzanas doradas comunes no otorgan el efecto de Absorción."));
        if (magmaContacto) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Magma al Contacto", "Los bloques de magma hacen 1x10^9 de daño al tocarlos por cualquier ángulo."));
        if (sinOxigeno) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Sin Oxígeno", "El oxígeno bajo el agua se consume el triple de rápido."));
        if (fallaMecanica) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Falla Mecánica", "Gólems miden x1.8, ganan fuerza/velocidad y con <25% de vida atacan a todos."));
        if (adivinaQuien) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Adivina Quién Ha Vuelto", "El Illusioner aparece en raids y outposts con Arco Retroceso X y Resistencia III."));
        if (creepersEscondidos) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Creepers Escondidos", "Al romper pasto hay 20% de probabilidad de spawnear un mini-creeper."));
        if (monturaAcuatica) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Montura Acuática", "Los Zombies Ahogados tienen 10% de probabilidad de montar Delfín o Nautilo."));
        if (desiertoAbrasador) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Desierto Abrasador", "Permanecer en el desierto acumula insolación progresiva a menos que lleves casco."));
        if (pykete) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Pykete", "Tridente con cuerda de algas: arrastra 8 bloques al agua si no usas escudo y rompe barcos de un golpe."));
        if (fragmentacionHelada) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Fragmentación Helada", "Al morir un Stray, transforma el suelo en hielo resbaladizo (4 bloques) e inflige Congelación Profunda."));
        if (comoPedro) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(3, "Como Pedro", "Los Creepers caminan sobre el agua y mientras la pisan su velocidad se multiplica por 3."));

        // Nivel 4
        if (danoColateral) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(4, "Daño Colateral", "Las Ender Pearls hacen x3 su daño original e ignoran armadura."));
        if (ultraFundido) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(4, "Ultra Fundido", "El daño por fuego se multiplica por 10."));
        if (contaminacion) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(4, "¿Contaminación?", "Por encima de la capa 160 hay niebla verde que inflige Veneno V."));
        if (congelacionProfunda) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(4, "Congelación Profunda", "Estar en nieve sin calor por >15s reduce velocidad a la mitad y causa daño."));
        if (escopeta) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(4, "Escopeta", "Pillagers armados con Multidisparo VI, Carga Rápida II y Perforación V."));
        if (reyDeLoProfundo) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(4, "Rey de lo Profundo", "Elder Guardian con 100 HP y rayo acumulativo de Debilidad."));
        if (ataqueSlimes) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(4, "Ataque a los Slimes", "5% de probabilidad de Slime Gigante en pantanos con x2.5 de daño y mega salto."));
        if (tensionMuscular) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(4, "Tensión Muscular", "Tensar arco/ballesta >3s provoca fatiga, temblor de puntería y Debilidad II."));
        if (colosalFusion) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(4, "Colosal de Fusión", "Magma Cubes colosales en el Nether que crean cráteres de magma al aterrizar y lanzan crías al morir."));
        if (cazadorIgneo) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(4, "Cazador Ígneo", "15% de Wither Skeletons en el Nether portan arcos de fuego azul que aplican Wither I por 6s."));

        // Nivel 5
        if (intrusosCalavericos) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "Intrusos Calavéricos", "Wither Skeletons con armadura de diamante y Filo V aparecen de noche."));
        if (spiderman) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "¿Spiderman?", "Todas las arañas tienen Fuerza II, Velocidad II, Invisibilidad y Regeneración."));
        if (enderGhast) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "Ender Ghast", "El Ghast se vuelve más agresivo y sus proyectiles hacen un x3 el daño explosivo."));
        if (kaBoom) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "KA-BOOM", "Los Shulkers explotan 3 segundos después de morir como una TNT."));
        if (gigaEndermite) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "Giga-Endermite", "Endermite Gigante en el End con 250 HP, embestida y sin retroceso."));
        if (megaHoglin) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "Mega Hoglin", "Hoglin Gigante (300 HP, x2 daño) cuya embestida desestabiliza e impide saltar/colocar bloques por 3s."));
        if (creeperCosmico) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "Creeper Cósmico", "Creeper Eléctrico lentísimo en el End con atracción gravitacional y explosión de 30 bloques."));
        if (torbellinoIgneo) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "Torbellino Ígneo", "Proyectiles de Blazes empujan y crean charcos temporales de lava y fuego."));
        if (phantomAlfa) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "Phantom Alfa", "Phantoms gigantes (x2.5), 60 HP, onda sónica que inflige 3 corazones."));
        if (cegueraAbisal) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "Ceguera Abisal", "Golpes en End y Deep Dark aplican Ceguera y Oscuridad sin poder curarse."));
        if (fallaDimensional) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "Falla Dimensional", "15% de probabilidad al cruzar portal del Nether de invocar 2 Blazes agresivos."));
        if (multicreeper) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "Multicreeper", "30% de probabilidad de que un Creeper golpeado cuerpo a cuerpo se divida en 2 mini-creepers (mecha 1s)."));
        if (sombraParpadeante) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "Sombra Parpadeante", "Endermans se teletransportan a tu espalda, silencian el ambiente (12 bloques) y fuerzan tu mirada."));
        if (berserker) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "Berserker", "Vindicadores con Netherite (+60% vel.), ignoran 50% de armadura y rompen el escudo por 10s."));
        if (nigromanteCriptas) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "El Nigromante de las Criptas", "Stray con báculo de hielo que congela el suelo con un rayo e invoca mini-strays con hachas."));
        if (creeperFantasma) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(5, "Creeper Fantasma", "Creeper espectral con manta translúcida que flota estilo Vex, atraviesa bloques e inmune a proyectiles."));

        // Nivel 6
        if (permadeath) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(6, "PERMADEATH", "Se elimina permanentemente la regeneración natural de salud."));
        if (apocalipsisMinecraftTotal) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(6, "¡Apocalipsis Minecraft !!!!", "Zombies se apilan en torres, huelen sangre y acuden al ruido al picar."));
        if (maldicionLunaDeSangre) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(6, "Luna de Sangre", "Hordas masivas de monstruos con hasta 3 efectos potenciados e insomnio forzado."));
        if (eclipseSolar) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(6, "Eclipse Solar", "Eclipse diurno con Strays en Phantoms, Brujas en Murciélagos y Creakings."));
        if (elVerdugo) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(6, "El Verdugo", "Piglins Brutos gigantes con Hacha de Netherite Filo VI y capacidad de desarmar."));
        if (maresAcidos) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(6, "Mares Ácidos", "Océanos corrosivos: sumergirse causa daño continuo y desgasta la armadura."));
        if (pareceQueVaALlover) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(6, "Parece que va´ llover", "Tormentas eléctricas con riesgo de caída de rayos al portar metales."));
        if (anomaliaGravitacional) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(6, "Anomalía Gravitacional", "Empuje triplicado en el End y saltos dejan al jugador en caída lenta por 3s."));
        if (corrupcionVacio) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(6, "Corrupción del Vacío", "Golpes de Enderman o proyectiles de Shulker anulan la armadura por 5s."));
        if (creeperAntimateria) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(6, "Creeper de Implosión Antimateria", "Raro Creeper negro que al detonar crea un vórtice de succión de 18 bloques por 2.5s antes de colapsar."));
        if (creeperTermonuclear) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(6, "Creeper Termonuclear", "Creeper acorazado de magma cuya temperatura sube en combate hasta detonar; enfriable con agua o nieve."));
        if (colosoRuina) lista.add(new com.luishugomod01.network.AbrirMenuEstadoPayload.MaldicionInfo(6, "El Coloso de Ruina", "Golem titánico (300 HP) con armadura de basalto. Destruye torres y desata Golpes Sísmicos esquivables saltando."));

        return lista;
    }

    public static void limpiar() {
        creepersEnPerla = false;
        fuegoFatuo = false;
        apocalipsisZombie = false;
        wardensDeepDark = false;
        cuevasTenebrosas = false;
        comidaEchada = false;
        cuccosSalvajes = false;
        cadaveresPisados = false;
        vacaExplosiva = false;
        corrientesAereas = false;
        invasionAzul = false;
        paranoiaNocturna = false;
        refuerzosPodridos = false;
        maldicionYunque = false;
        inventarioPesado = false;
        caidaBrutal = false;
        ojosEnLaOscuridad = false;
        speedpers = false;
        filoMortal = false;
        muerteEspinosa = false;
        cucarachaPlateada = false;
        abejasAsesinas = false;
        cargaPesada = false;
        vexGrandes = false;
        resistenciaFisica = false;
        escudoPesado = false;
        wingardiumLeviosa = false;
        lavaActiva = false;
        webones = false;
        mineriaInestable = false;
        telaranaAlFallo = false;
        brujaReflejante = false;
        gritoPrimal = false;
        errorTotemico = false;
        artesMaritimos = false;
        boogieWoogie = false;
        energiaPoseica = false;
        infestacionMinera = false;
        muerteInstantanea = false;
        manzanaRegeneradora = false;
        magmaContacto = false;
        contaminacionCruzada = false;
        sinOxigeno = false;
        fallaMecanica = false;
        adivinaQuien = false;
        pykete = false;
        fragmentacionHelada = false;
        comoPedro = false;
        creeperAvispon = false;
        hombreRoca = false;
        danoColateral = false;
        ultraFundido = false;
        contaminacion = false;
        congelacionProfunda = false;
        escopeta = false;
        reyDeLoProfundo = false;
        desiertoAbrasador = false;
        tensionMuscular = false;
        colosalFusion = false;
        cazadorIgneo = false;
        intrusosCalavericos = false;
        spiderman = false;
        enderGhast = false;
        kaBoom = false;
        ataqueSlimes = false;
        gigaEndermite = false;
        megaHoglin = false;
        creepersEscondidos = false;
        creeperCosmico = false;
        torbellinoIgneo = false;
        phantomAlfa = false;
        cegueraAbisal = false;
        fallaDimensional = false;
        multicreeper = false;
        sombraParpadeante = false;
        berserker = false;
        nigromanteCriptas = false;
        creeperFantasma = false;
        permadeath = false;
        apocalipsisMinecraftTotal = false;
        maldicionLunaDeSangre = false;
        monturaAcuatica = false;
        eclipseSolar = false;
        elVerdugo = false;
        maresAcidos = false;
        pareceQueVaALlover = false;
        anomaliaGravitacional = false;
        corrupcionVacio = false;
        creeperAntimateria = false;
        creeperTermonuclear = false;
        colosoRuina = false;
        isBloodMoonActive = false;
        ultimoDiaBloodMoon = -1L;
        isEclipseActive = false;
        ultimoDiaEclipse = -1L;
    }
}
