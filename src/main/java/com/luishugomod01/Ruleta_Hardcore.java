package com.luishugomod01;

import com.luishugomod01.network.AbrirMenuAdminPayload;
import com.luishugomod01.network.AbrirMenuEstadoPayload;
import com.luishugomod01.network.BloodMoonSyncPayload;
import com.luishugomod01.network.GuardarConfigAdminPayload;
import com.luishugomod01.network.MuerteFantasmaPayload;
import com.luishugomod01.network.RuletaAnimacionPayload;
import com.luishugomod01.network.VidasJugadorPayload;
import com.luishugomod01.network.VidasSyncPayload;
import com.luishugomod01.ruleta.CatalogoCastigos;
import com.luishugomod01.ruleta.CreeperCustomAccessor;
import com.luishugomod01.ruleta.CursosActivos;
import com.luishugomod01.ruleta.NivelRuleta;
import com.luishugomod01.ruleta.RuletaPersistencia;
import com.luishugomod01.ruleta.WallClimberAccessor;
import com.luishugomod01.vidas.VidasJugadorAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cow.AbstractCow;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.cubemob.MagmaCube;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.illager.Illusioner;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.illager.Vindicator;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.skeleton.Stray;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ruleta_Hardcore implements ModInitializer {
    public static final String MOD_ID = "ruleta_hardcore";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final String[] MOBS_CUSTOM = new String[] {
            "coloso_ruina",
            "creeper_termonuclear",
            "creeper_antimateria",
            "creeper_cosmico",
            "creeper_fantasma",
            "creeper_avispon",
            "creeper_escondido",
            "mini_creeper",
            "el_verdugo",
            "mega_hoglin",
            "berserker",
            "nigromante_criptas",
            "colosal_fusion",
            "cazador_igneo",
            "intruso_calaverico",
            "giga_endermite",
            "phantom_alfa",
            "slime_gigante",
            "cucco_salvaje",
            "apocalipsis_zombie",
            "pykete",
            "sombra_parpadeante"
    };

    private static EntityType<?> CREEPER_TYPE_CACHE = null;
    private static EntityType<?> WARDEN_TYPE_CACHE = null;
    private static EntityType<?> WITHER_SKELETON_TYPE_CACHE = null;
    private static EntityType<?> ZOMBIE_NAUTILUS_TYPE_CACHE = null;
    private static EntityType<?> DOLPHIN_TYPE_CACHE = null;
    private static EntityType<?> ENDERMITE_TYPE_CACHE = null;
    private static EntityType<?> ENDERMAN_TYPE_CACHE = null;
    private static EntityType<?> PHANTOM_TYPE_CACHE = null;
    private static EntityType<?> STRAY_TYPE_CACHE = null;
    private static EntityType<?> WITCH_TYPE_CACHE = null;
    private static EntityType<?> BAT_TYPE_CACHE = null;
    private static EntityType<?> BREEZE_TYPE_CACHE = null;
    private static EntityType<?> ILLUSIONER_TYPE_CACHE = null;
    private static EntityType<?> CREAKING_TYPE_CACHE = null;
    private static EntityType<?> LIGHTNING_BOLT_TYPE_CACHE = null;
    private static EntityType<?> MAGMA_CUBE_TYPE_CACHE = null;

    private static final Map<UUID, Integer> TICKS_EXPOSICION_NIEVE = new HashMap<>();
    private static final Map<UUID, Integer> TICKS_INSOLACION_DESIERTO = new HashMap<>();
    private static final Map<UUID, Long> ULTIMA_EXPLOSION_MINERIA = new HashMap<>();
    private static final Map<UUID, Integer> TICKS_OSCURIDAD_PARANOIA = new HashMap<>();
    private static final Map<UUID, Long> ULTIMA_PARANOIA_NOTIFICACION = new HashMap<>();
    private static final Map<UUID, Long> ULTIMA_ONDA_PHANTOM_ALFA = new HashMap<>();
    private static final Map<UUID, Integer> TICKS_AVISO_RAYO = new HashMap<>();
    private static final Map<UUID, Integer> RAYOS_IMPACTADOS = new HashMap<>();
    private static final Map<UUID, Boolean> JUGADORES_SUELO_PREVIO = new HashMap<>();
    private static final Map<UUID, Long> DESESTABILIZACION_HOGLIN_EXPIRY = new HashMap<>();
    private static final Map<UUID, Boolean> COLOSAL_SUELO_PREVIO = new HashMap<>();
    private static final Map<UUID, Integer> TEMPERATURA_CREEPER_TERMONUCLEAR = new HashMap<>();
    private static final Map<UUID, Long> ULTIMO_GRITO_ENDERMAN = new HashMap<>();
    private static final Map<UUID, Long> ULTIMO_PARPADEO_SOMBRA = new HashMap<>();

    public record VorticeAntimateria(ServerLevel level, double x, double y, double z, long tickFin) {}
    private static final List<VorticeAntimateria> VORTICES_ANTIMATERIA = new ArrayList<>();

    public static Map<UUID, Integer> getRayosImpactados() {
        return RAYOS_IMPACTADOS;
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Ruleta Hardcore inicializada correctamente.");

        PayloadTypeRegistry.clientboundPlay().register(RuletaAnimacionPayload.TYPE, RuletaAnimacionPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MuerteFantasmaPayload.TYPE, MuerteFantasmaPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(BloodMoonSyncPayload.TYPE, BloodMoonSyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(AbrirMenuAdminPayload.TYPE, AbrirMenuAdminPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(AbrirMenuEstadoPayload.TYPE, AbrirMenuEstadoPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(VidasSyncPayload.TYPE, VidasSyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(VidasJugadorPayload.TYPE, VidasJugadorPayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(GuardarConfigAdminPayload.TYPE, GuardarConfigAdminPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(GuardarConfigAdminPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            MinecraftServer srv = player.level().getServer();
            if (srv == null || !esAdministrador(player, srv)) {
                return;
            }
            srv.execute(() -> {
                boolean eraConfigurado = CursosActivos.isMundoConfigurado();
                CursosActivos.setRuletaAutomatica(payload.ruletaAutomatica());
                CursosActivos.setVidasIniciales(payload.vidasIniciales());
                CursosActivos.setMundoConfigurado(true);

                for (ServerPlayer p : srv.getPlayerList().getPlayers()) {
                    if (p instanceof VidasJugadorAccessor accessor) {
                        if (!eraConfigurado || !accessor.ruleta$isVidasInicializadas()) {
                            accessor.ruleta$setVidas(CursosActivos.getVidasIniciales());
                            accessor.ruleta$setVidasInicializadas(true);
                        }
                        sincronizarVidasJugador(p);
                    }
                }
                RuletaPersistencia.guardar(srv);

                String modoStr = CursosActivos.isRuletaAutomatica() ? "§aAUTOMÁTICO" : "§eMANUAL";
                srv.getPlayerList().broadcastSystemMessage(
                        Component.literal("§6§l[PermaRuleta] §fConfiguración guardada: Giro " + modoStr
                                + " §7| §c♥ Vidas Iniciales: §f§l" + CursosActivos.getVidasIniciales()),
                        false
                );
            });
        });

        ServerLifecycleEvents.SERVER_STARTED.register(RuletaPersistencia::cargar);
        ServerLifecycleEvents.SERVER_STOPPING.register(RuletaPersistencia::guardar);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("ruleta")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.literal("girar")
                    .executes(ctx -> ejecutarGiroAleatorio(ctx.getSource()))
                    .then(Commands.literal("1").executes(ctx -> ejecutarGiroNivel(ctx.getSource(), NivelRuleta.NIVEL_1)))
                    .then(Commands.literal("2").executes(ctx -> ejecutarGiroNivel(ctx.getSource(), NivelRuleta.NIVEL_2)))
                    .then(Commands.literal("3").executes(ctx -> ejecutarGiroNivel(ctx.getSource(), NivelRuleta.NIVEL_3)))
                    .then(Commands.literal("4").executes(ctx -> ejecutarGiroNivel(ctx.getSource(), NivelRuleta.NIVEL_4)))
                    .then(Commands.literal("5").executes(ctx -> ejecutarGiroNivel(ctx.getSource(), NivelRuleta.NIVEL_5)))
                    .then(Commands.literal("6").executes(ctx -> ejecutarGiroNivel(ctx.getSource(), NivelRuleta.NIVEL_6))))
                .then(Commands.literal("nivel")
                    .then(Commands.literal("1").executes(ctx -> ejecutarGiroNivel(ctx.getSource(), NivelRuleta.NIVEL_1)))
                    .then(Commands.literal("2").executes(ctx -> ejecutarGiroNivel(ctx.getSource(), NivelRuleta.NIVEL_2)))
                    .then(Commands.literal("3").executes(ctx -> ejecutarGiroNivel(ctx.getSource(), NivelRuleta.NIVEL_3)))
                    .then(Commands.literal("4").executes(ctx -> ejecutarGiroNivel(ctx.getSource(), NivelRuleta.NIVEL_4)))
                    .then(Commands.literal("5").executes(ctx -> ejecutarGiroNivel(ctx.getSource(), NivelRuleta.NIVEL_5)))
                    .then(Commands.literal("6").executes(ctx -> ejecutarGiroNivel(ctx.getSource(), NivelRuleta.NIVEL_6))))
                .then(Commands.literal("activar")
                    .then(Commands.literal("creepersperla").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "creepersperla")))
                    .then(Commands.literal("fuegofatuo").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "fuegofatuo")))
                    .then(Commands.literal("apocalipsis").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "apocalipsis")))
                    .then(Commands.literal("wardens").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "wardens")))
                    .then(Commands.literal("cuevastenebrosas").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "cuevastenebrosas")))
                    .then(Commands.literal("comidaechada").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "comidaechada")))
                    .then(Commands.literal("cuccossalvajes").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "cuccossalvajes")))
                    .then(Commands.literal("cadaverespisados").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "cadaverespisados")))
                    .then(Commands.literal("vacaexplosiva").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "vacaexplosiva")))
                    .then(Commands.literal("corrientesaereas").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "corrientesaereas")))
                    .then(Commands.literal("invasionazul").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "invasionazul")))
                    .then(Commands.literal("speedpers").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "speedpers")))
                    .then(Commands.literal("filomortal").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "filomortal")))
                    .then(Commands.literal("muerteespinosa").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "muerteespinosa")))
                    .then(Commands.literal("cucarachaplateada").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "cucarachaplateada")))
                    .then(Commands.literal("abejasasesinas").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "abejasasesinas")))
                    .then(Commands.literal("cargapesada").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "cargapesada")))
                    .then(Commands.literal("vexgrandes").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "vexgrandes")))
                    .then(Commands.literal("resistenciafisica").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "resistenciafisica")))
                    .then(Commands.literal("escudopesado").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "escudopesado")))
                    .then(Commands.literal("wingardiumleviosa").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "wingardiumleviosa")))
                    .then(Commands.literal("lavaactiva").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "lavaactiva")))
                    .then(Commands.literal("webones").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "webones")))
                    .then(Commands.literal("mineriainestable").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "mineriainestable")))
                    .then(Commands.literal("errortotemico").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "errortotemico")))
                    .then(Commands.literal("artesmaritimos").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "artesmaritimos")))
                    .then(Commands.literal("boogiewoogie").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "boogiewoogie")))
                    .then(Commands.literal("energiaposeica").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "energiaposeica")))
                    .then(Commands.literal("infestacionminera").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "infestacionminera")))
                    .then(Commands.literal("muerteinstantanea").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "muerteinstantanea")))
                    .then(Commands.literal("manzanaregeneradora").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "manzanaregeneradora")))
                    .then(Commands.literal("magmacontacto").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "magmacontacto")))
                    .then(Commands.literal("contaminacioncruzada").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "contaminacioncruzada")))
                    .then(Commands.literal("sinoxigeno").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "sinoxigeno")))
                    .then(Commands.literal("fallamecanica").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "fallamecanica")))
                    .then(Commands.literal("adivinaquien").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "adivinaquien")))
                    .then(Commands.literal("danocolateral").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "danocolateral")))
                    .then(Commands.literal("ultrafundido").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "ultrafundido")))
                    .then(Commands.literal("contaminacion").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "contaminacion")))
                    .then(Commands.literal("congelacionprofunda").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "congelacionprofunda")))
                    .then(Commands.literal("escopeta").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "escopeta")))
                    .then(Commands.literal("reydeloprofundo").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "reydeloprofundo")))
                    .then(Commands.literal("desiertoabrasador").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "desiertoabrasador")))
                    .then(Commands.literal("paranoianocturna").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "paranoianocturna")))
                    .then(Commands.literal("intrusoscalavericos").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "intrusoscalavericos")))
                    .then(Commands.literal("spiderman").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "spiderman")))
                    .then(Commands.literal("enderghast").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "enderghast")))
                    .then(Commands.literal("kaboom").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "kaboom")))
                    .then(Commands.literal("ataqueslimes").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "ataqueslimes")))
                    .then(Commands.literal("gigaendermite").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "gigaendermite")))
                    .then(Commands.literal("megahoglin").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "megahoglin")))
                    .then(Commands.literal("creepersescondidos").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "creepersescondidos")))
                    .then(Commands.literal("creepercosmico").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "creepercosmico")))
                    .then(Commands.literal("torbellinoigneo").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "torbellinoigneo")))
                    .then(Commands.literal("phantomalfa").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "phantomalfa")))
                    .then(Commands.literal("cegueraabisal").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "cegueraabisal")))
                    .then(Commands.literal("permadeath").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "permadeath")))
                    .then(Commands.literal("apocalipsis6").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "apocalipsis6")))
                    .then(Commands.literal("lunasangre").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "lunasangre")))
                    .then(Commands.literal("monturaacuatica").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "monturaacuatica")))
                    .then(Commands.literal("eclipsesolar").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "eclipsesolar")))
                    .then(Commands.literal("elverdugo").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "elverdugo")))
                    .then(Commands.literal("maresacidos").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "maresacidos")))
                    .then(Commands.literal("parecequevallover").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "parecequevallover")))
                    .then(Commands.literal("tensionmuscular").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "tensionmuscular")))
                    .then(Commands.literal("falladimensional").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "falladimensional")))
                    .then(Commands.literal("anomaliagravitacional").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "anomaliagravitacional")))
                    .then(Commands.literal("corrupcionvacio").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "corrupcionvacio")))
                    .then(Commands.literal("refuerzospodridos").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "refuerzospodridos")))
                    .then(Commands.literal("maldicionyunque").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "maldicionyunque")))
                    .then(Commands.literal("inventariopesado").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "inventariopesado")))
                    .then(Commands.literal("caidabrutal").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "caidabrutal")))
                    .then(Commands.literal("ojosenlaoscuridad").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "ojosenlaoscuridad")))
                    .then(Commands.literal("telaranaalfallo").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "telaranaalfallo")))
                    .then(Commands.literal("brujareflejante").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "brujareflejante")))
                    .then(Commands.literal("pykete").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "pykete")))
                    .then(Commands.literal("fragmentacionhelada").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "fragmentacionhelada")))
                    .then(Commands.literal("multicreeper").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "multicreeper")))
                    .then(Commands.literal("creeperantimateria").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "creeperantimateria")))
                    .then(Commands.literal("gritoprimal").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "gritoprimal")))
                    .then(Commands.literal("comopedro").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "comopedro")))
                    .then(Commands.literal("creeperavispon").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "creeperavispon")))
                    .then(Commands.literal("colosalfusion").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "colosalfusion")))
                    .then(Commands.literal("cazadorigneo").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "cazadorigneo")))
                    .then(Commands.literal("sombraparpadeante").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "sombraparpadeante")))
                    .then(Commands.literal("berserker").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "berserker")))
                    .then(Commands.literal("nigromantecriptas").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "nigromantecriptas")))
                    .then(Commands.literal("creeperfantasma").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "creeperfantasma")))
                    .then(Commands.literal("creepertermonuclear").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "creepertermonuclear")))
                    .then(Commands.literal("colosoruina").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "colosoruina"))))
                .then(Commands.literal("bloodmoon")
                    .then(Commands.literal("iniciar").executes(ctx -> {
                        var srv = ctx.getSource().getServer();
                        long dayTime = ((srv.overworld().getOverworldClockTime() % 24000L) + 24000L) % 24000L;
                        if (dayTime < 12000L || dayTime >= 23000L) {
                            ctx.getSource().sendFailure(Component.literal("§c[PermaRuleta] Es de día (tick " + dayTime + "). Usa primero /time set 12500 para iniciar la noche de Luna de Sangre."));
                            return 0;
                        }
                        CursosActivos.setBloodMoonActive(true);
                        RuletaPersistencia.guardar(srv);
                        sincronizarLunaSangreClientes(srv);
                        ctx.getSource().sendSuccess(() -> Component.literal("§4[PermaRuleta] §c¡Evento de Luna de Sangre forzado para esta noche!"), true);
                        return 1;
                    }))
                    .then(Commands.literal("detener").executes(ctx -> {
                        var srv = ctx.getSource().getServer();
                        CursosActivos.setBloodMoonActive(false);
                        RuletaPersistencia.guardar(srv);
                        sincronizarLunaSangreClientes(srv);
                        ctx.getSource().sendSuccess(() -> Component.literal("§e[PermaRuleta] Evento de Luna de Sangre detenido."), true);
                        return 1;
                    })))
                .then(Commands.literal("eclipse")
                    .then(Commands.literal("iniciar").executes(ctx -> {
                        var srv = ctx.getSource().getServer();
                        long dayTime = ((srv.overworld().getOverworldClockTime() % 24000L) + 24000L) % 24000L;
                        if (dayTime >= 12000L) {
                            ctx.getSource().sendFailure(Component.literal("§c[PermaRuleta] Es de noche (tick " + dayTime + "). Usa primero /time set 1000 para iniciar el Eclipse Solar."));
                            return 0;
                        }
                        CursosActivos.setEclipseActive(true);
                        RuletaPersistencia.guardar(srv);
                        sincronizarLunaSangreClientes(srv);
                        ctx.getSource().sendSuccess(() -> Component.literal("§5[PermaRuleta] §d¡Evento de Eclipse Solar forzado para este día!"), true);
                        return 1;
                    }))
                    .then(Commands.literal("detener").executes(ctx -> {
                        var srv = ctx.getSource().getServer();
                        CursosActivos.setEclipseActive(false);
                        RuletaPersistencia.guardar(srv);
                        sincronizarLunaSangreClientes(srv);
                        ctx.getSource().sendSuccess(() -> Component.literal("§e[PermaRuleta] Evento de Eclipse Solar detenido."), true);
                        return 1;
                    })))
                .then(Commands.literal("estado").executes(ctx -> {
                    var source = ctx.getSource();
                    ServerPlayer sp = source.getPlayer();
                    if (sp != null) {
                        var lista = CursosActivos.obtenerMaldicionesActivasDetalladas();
                        boolean bloodMoon = CursosActivos.isBloodMoonActive();
                        boolean eclipse = CursosActivos.isEclipseActive();
                        ServerPlayNetworking.send(sp, new AbrirMenuEstadoPayload(lista, bloodMoon, eclipse));
                    } else {
                        if (!CursosActivos.hayMaldicionesActivas() && !CursosActivos.isBloodMoonActive() && !CursosActivos.isEclipseActive()) {
                            source.sendSuccess(() -> Component.literal("§a[PermaRuleta] No hay ninguna maldición activa en el mundo."), false);
                        } else {
                            source.sendSuccess(() -> Component.literal("§6§l[PermaRuleta] Maldiciones activas:"), false);
                            for (Component c : CursosActivos.obtenerMensajesMaldicionesActivas()) {
                                source.sendSuccess(() -> c, false);
                            }
                            if (CursosActivos.isBloodMoonActive()) {
                                source.sendSuccess(() -> Component.literal("  §4§l☾ ESTADO ACTUAL:§c ¡LUNA DE SANGRE EN CURSO ESTA NOCHE!"), false);
                            }
                            if (CursosActivos.isEclipseActive()) {
                                source.sendSuccess(() -> Component.literal("  §5§l☼ ESTADO ACTUAL:§d ¡ECLIPSE SOLAR EN CURSO ESTE DÍA!"), false);
                            }
                        }
                    }
                    return 1;
                }))
                .then(Commands.literal("config").executes(ctx -> {
                    ServerPlayer sp = ctx.getSource().getPlayerOrException();
                    ServerPlayNetworking.send(sp, new AbrirMenuAdminPayload(CursosActivos.isRuletaAutomatica(), CursosActivos.getVidasIniciales()));
                    return 1;
                }))
                .then(Commands.literal("limpiar").executes(ctx -> {
                    var srv = ctx.getSource().getServer();
                    CursosActivos.limpiar();
                    CatalogoCastigos.reiniciar();
                    RuletaPersistencia.limpiar(srv);
                    VORTICES_ANTIMATERIA.clear();
                    DESESTABILIZACION_HOGLIN_EXPIRY.clear();
                    TEMPERATURA_CREEPER_TERMONUCLEAR.clear();
                    ULTIMO_GRITO_ENDERMAN.clear();
                    ULTIMO_PARPADEO_SOMBRA.clear();
                    sincronizarLunaSangreClientes(srv);
                    for (ServerLevel lvl : srv.getAllLevels()) {
                        lvl.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, true, srv);
                    }
                    ctx.getSource().sendSuccess(() -> Component.literal("§e[PermaRuleta] Se han desactivado todas las maldiciones y reiniciado el catálogo."), true);
                    return 1;
                }))
                .then(Commands.literal("test_fantasma")
                    .executes(ctx -> {
                        ServerPlayer sp = ctx.getSource().getPlayer();
                        String name = sp != null ? sp.getScoreboardName() : "JugadorPrueba";
                        MinecraftServer srv = ctx.getSource().getServer();
                        MuerteFantasmaPayload payload = new MuerteFantasmaPayload(name, 2, CursosActivos.getVidasIniciales(), false);
                        for (ServerPlayer player : srv.getPlayerList().getPlayers()) {
                            ServerPlayNetworking.send(player, payload);
                        }
                        ctx.getSource().sendSuccess(() -> Component.literal("§a[PermaRuleta] Animación de prueba del fantasma enviada a todos los jugadores."), false);
                        return 1;
                    })
                    .then(Commands.literal("eliminado").executes(ctx -> {
                        ServerPlayer sp = ctx.getSource().getPlayer();
                        String name = sp != null ? sp.getScoreboardName() : "JugadorPrueba";
                        MinecraftServer srv = ctx.getSource().getServer();
                        MuerteFantasmaPayload payload = new MuerteFantasmaPayload(name, 0, CursosActivos.getVidasIniciales(), true);
                        for (ServerPlayer player : srv.getPlayerList().getPlayers()) {
                            ServerPlayNetworking.send(player, payload);
                        }
                        ctx.getSource().sendSuccess(() -> Component.literal("§4[PermaRuleta] Animación de prueba de ELIMINACIÓN enviada a todos los jugadores."), false);
                        return 1;
                    })))
                .then(construirComandoRuletaSummon())
            );

            // Inyección en el comando nativo /summon de Minecraft para admitir /summon <mob_custom> [pos]
            var nativeSummon = Commands.literal("summon")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER));
            for (String mob : MOBS_CUSTOM) {
                nativeSummon.then(Commands.literal(mob)
                        .executes(ctx -> spawnMobCustom(ctx.getSource(), mob, ctx.getSource().getPosition()))
                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                .executes(ctx -> spawnMobCustom(ctx.getSource(), mob, Vec3Argument.getVec3(ctx, "pos")))));
            }
            dispatcher.register(nativeSummon);
        });

        // Espejismo de Ender Pearl: 5% de probabilidad de generar un Creeper al lanzar
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClientSide() && player instanceof ServerPlayer sp) {
                if (sp.getItemInHand(hand).is(Items.ENDER_PEARL)
                        && CursosActivos.isCreepersEnPerla()
                        && ThreadLocalRandom.current().nextFloat() < 0.05f) {
                    ServerLevel serverLevel = (ServerLevel) world;
                    BuiltInRegistries.ENTITY_TYPE
                        .getOptional(Identifier.fromNamespaceAndPath("minecraft", "creeper"))
                        .ifPresent(entityType -> {
                            var entity = entityType.create(serverLevel, EntitySpawnReason.MOB_SUMMONED);
                            if (entity != null) {
                                entity.setPos(sp.getX(), sp.getY(), sp.getZ());
                                serverLevel.addFreshEntity(entity);
                            }
                        });
                }
            }
            return InteractionResult.PASS;
        });

        // Falla Dimensional (Nivel 5): al cruzar portal del Nether (Nether <-> Overworld), 15% de sobrecargar e invocar 2 Blazes agresivos
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, origin, destination) -> {
            if (CursosActivos.isFallaDimensional() && !player.isCreative() && !player.isSpectator()) {
                boolean esNetherOverworld = (origin.dimension().equals(Level.NETHER) && destination.dimension().equals(Level.OVERWORLD))
                        || (origin.dimension().equals(Level.OVERWORLD) && destination.dimension().equals(Level.NETHER));
                if (esNetherOverworld && ThreadLocalRandom.current().nextFloat() < 0.15f) {
                    ejecutarFallaDimensional(player, destination);
                }
            }
        });

        // Muerte Instantánea (Nivel 3) y desestabilización de Mega Hoglin (Nivel 5)
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (CursosActivos.isMegaHoglin() && !player.isCreative() && !player.isSpectator()
                    && estaDesestabilizadoHoglin(player.getUUID(), world.getGameTime())
                    && player.getItemInHand(hand).getItem() instanceof BlockItem) {
                if (!world.isClientSide() && player instanceof ServerPlayer sp) {
                    sp.sendSystemMessage(Component.literal("§c🐗 ¡Estás desestabilizado por el Mega Hoglin y no puedes colocar bloques!"), true);
                }
                return InteractionResult.FAIL;
            }
            if (!world.isClientSide() && CursosActivos.isMuerteInstantanea()
                    && world instanceof ServerLevel serverLevel
                    && player instanceof ServerPlayer sp
                    && !sp.isCreative() && !sp.isSpectator()) {
                BlockState hitState = serverLevel.getBlockState(hitResult.getBlockPos());
                if (hitState.is(BlockTags.WOODEN_BUTTONS)
                        || hitState.is(BlockTags.WOODEN_TRAPDOORS)
                        || hitState.is(BlockTags.WOODEN_DOORS)
                        || hitState.is(BlockTags.FENCE_GATES)) {
                    sp.hurtServer(serverLevel, serverLevel.damageSources().generic(), 1.0E9f);
                }
            }
            return InteractionResult.PASS;
        });

        // ¡Apocalipsis Minecraft !!!! (Nivel 6) y Creepers Escondidos (Nivel 5) al picar bloques
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClientSide() && world instanceof ServerLevel serverLevel) {
                if (CursosActivos.isApocalipsisMinecraftTotal()) {
                    double radius = player.isShiftKeyDown() ? 10.0 : 24.0;
                    var nearby = serverLevel.getEntitiesOfClass(Zombie.class, new AABB(pos).inflate(radius), Entity::isAlive);
                    for (Zombie z : nearby) {
                        if (z.getTarget() == null || !z.getTarget().isAlive()) {
                            z.getNavigation().moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 1.25);
                        }
                    }
                }
                if (CursosActivos.isCreepersEscondidos()
                        && (state.is(Blocks.SHORT_GRASS) || state.is(Blocks.TALL_GRASS)
                        || state.is(Blocks.FERN) || state.is(Blocks.LARGE_FERN)
                        || state.is(Blocks.GRASS_BLOCK))) {
                    if (ThreadLocalRandom.current().nextFloat() < 0.20f) {
                        EntityType<?> creeperType = getCreeperType();
                        if (creeperType != null) {
                            Entity created = creeperType.create(serverLevel, EntitySpawnReason.EVENT);
                            if (created instanceof Creeper creeper) {
                                creeper.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                                        ThreadLocalRandom.current().nextFloat() * 360.0f, 0.0f);
                                configurarCreeperEscondido(creeper);
                                creeper.setTarget(player);
                                serverLevel.addFreshEntity(creeper);
                            }
                        }
                    }
                }
                // Minería Inestable (Nivel 2): 5% al romper mineral de provocar una explosión pequeña sin destruir bloques (con enfriamiento de 5s)
                if (CursosActivos.isMineriaInestable() && esBloqueMineral(state)) {
                    long now = serverLevel.getGameTime();
                    long lastExplosion = ULTIMA_EXPLOSION_MINERIA.getOrDefault(player.getUUID(), -1000L);
                    if (now - lastExplosion >= 100L && ThreadLocalRandom.current().nextFloat() < 0.05f) {
                        ULTIMA_EXPLOSION_MINERIA.put(player.getUUID(), now);
                        serverLevel.explode(
                                null,
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5,
                                2.0f,
                                false,
                                Level.ExplosionInteraction.NONE
                        );
                    }
                }
            }
        });

        // KA-BOOM (Nivel 5): los Shulkers explotan 3 segundos (60 ticks) después de morir
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayer sp && sp instanceof VidasJugadorAccessor accessor) {
                if (!accessor.ruleta$isVidasInicializadas()) {
                    accessor.ruleta$setVidas(CursosActivos.getVidasIniciales());
                    accessor.ruleta$setVidasInicializadas(true);
                }
                int vidasActuales = accessor.ruleta$getVidas();
                int nuevasVidas = Math.max(0, vidasActuales - 1);
                accessor.ruleta$setVidas(nuevasVidas);
                sincronizarVidasJugador(sp);

                MinecraftServer srv = sp.level().getServer();
                if (srv != null) {
                    MuerteFantasmaPayload muertePayload = new MuerteFantasmaPayload(
                            sp.getScoreboardName(),
                            nuevasVidas,
                            CursosActivos.getVidasIniciales(),
                            nuevasVidas <= 0
                    );
                    for (ServerPlayer player : srv.getPlayerList().getPlayers()) {
                        ServerPlayNetworking.send(player, muertePayload);
                    }
                }
                if (nuevasVidas <= 0) {
                    sp.setGameMode(GameType.SPECTATOR);
                    sp.sendSystemMessage(Component.literal("§4§l[PermaRuleta] ☠ ¡Te has quedado sin vidas! Has sido eliminado y ahora estás en modo Espectador permanente."));
                    if (srv != null) {
                        srv.getPlayerList().broadcastSystemMessage(
                                Component.literal("§4§l☠ [PermaRuleta] §c" + sp.getScoreboardName() + " ha perdido su última vida y ha sido eliminado permanentemente."),
                                false
                        );
                    }
                } else {
                    sp.sendSystemMessage(Component.literal("§c§l[PermaRuleta] ♥ Has muerto. Te quedan §e§l" + nuevasVidas + " §cvidas."));
                    if (srv != null) {
                        srv.getPlayerList().broadcastSystemMessage(
                                Component.literal("§c[PermaRuleta] " + sp.getScoreboardName() + " perdió una vida. Vidas restantes: §e" + nuevasVidas),
                                false
                        );
                    }
                }
                if (srv != null && CursosActivos.isRuletaAutomatica()) {
                    long tickGiro = srv.getTickCount() + 40;
                    programarTarea(tickGiro, () -> ejecutarGiroAleatorio(srv.createCommandSourceStack()));
                }
            }

            // Vaca Explosiva (Nivel 1): 1% de probabilidad de que una vaca explote al morir
            if (CursosActivos.isVacaExplosiva() && entity instanceof AbstractCow cow && cow.level() instanceof ServerLevel serverLevel) {
                if (ThreadLocalRandom.current().nextFloat() < 0.01f) {
                    serverLevel.explode(cow, cow.getX(), cow.getY() + 0.5, cow.getZ(), 4.0f, Level.ExplosionInteraction.MOB);
                }
            }

            // Refuerzos Podridos (Nivel 1): Al morir un zombi, 10% de probabilidad de invocar un zombi bebé
            if (CursosActivos.isRefuerzosPodridos() && entity instanceof Zombie zombie && !zombie.isBaby()
                    && zombie.level() instanceof ServerLevel serverLevel) {
                if (ThreadLocalRandom.current().nextFloat() < 0.10f) {
                    var created = zombie.getType().create(serverLevel, EntitySpawnReason.EVENT);
                    if (created instanceof Zombie baby) {
                        baby.snapTo(zombie.getX(), zombie.getY(), zombie.getZ(), zombie.getYRot(), 0.0f);
                        baby.setBaby(true);
                        if (zombie.getTarget() != null && zombie.getTarget().isAlive()) {
                            baby.setTarget(zombie.getTarget());
                        }
                        serverLevel.addFreshEntity(baby);
                        serverLevel.sendParticles(ParticleTypes.SOUL, zombie.getX(), zombie.getY() + 0.5, zombie.getZ(), 15, 0.3, 0.3, 0.3, 0.04);
                        serverLevel.playSound(null, zombie.getX(), zombie.getY(), zombie.getZ(), SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 1.2f, 1.3f);
                    }
                }
            }

            // Fragmentación Helada (Nivel 3): Al morir un Stray, transforma el suelo en hielo (4 bloques) e inflige Congelación Profunda
            if (CursosActivos.isFragmentacionHelada() && entity instanceof Stray stray && stray.level() instanceof ServerLevel serverLevel) {
                ejecutarFragmentacionHelada(stray, serverLevel);
            }

            // Colosal de Fusión (Nivel 4): Al morir un Magma Cube colosal, sus crías salen disparadas en arco en todas direcciones como proyectiles de mortero
            if (entity instanceof MagmaCube magma
                    && magma.entityTags().contains("colosal_fusion")
                    && magma.level() instanceof ServerLevel serverLevel) {
                dispararCriasMorteroColosal(magma, serverLevel);
            }

            if (CursosActivos.isKaBoom() && entity instanceof Shulker shulker && shulker.level() instanceof ServerLevel serverLevel) {
                double ex = shulker.getX();
                double ey = shulker.getY() + 0.5;
                double ez = shulker.getZ();

                serverLevel.playSound(null, ex, ey, ez, SoundEvents.TNT_PRIMED, SoundSource.HOSTILE, 1.2f, 1.0f);
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, ex, ey, ez, 20, 0.3, 0.3, 0.3, 0.05);

                MinecraftServer srv = serverLevel.getServer();
                if (srv != null) {
                    long baseTick = srv.getTickCount();
                    programarTarea(baseTick + 20, () -> serverLevel.sendParticles(ParticleTypes.FLAME, ex, ey, ez, 15, 0.25, 0.25, 0.25, 0.03));
                    programarTarea(baseTick + 40, () -> serverLevel.sendParticles(ParticleTypes.LAVA, ex, ey, ez, 15, 0.25, 0.25, 0.25, 0.05));
                    programarTarea(baseTick + 60, () -> {
                        serverLevel.explode(null, ex, ey, ez, 4.0f, Level.ExplosionInteraction.TNT);
                    });
                }
            }

            // Creeper Avispón (Nivel 3): Al morir o ser eliminado, apagar inmediatamente el audio de zumbido
            if (entity instanceof Creeper creeper && creeper.entityTags().contains("creeper_avispon")) {
                detenerSonidoCreeperAvispon(creeper, creeper.level());
            }

            // El Coloso de Ruina (Nivel 6): Al morir, estalla liberando esquirlas volcánicas y humo
            if (entity instanceof IronGolem golem && golem.entityTags().contains("coloso_ruina") && golem.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, golem.getX(), golem.getY() + 1.5, golem.getZ(), 2, 0.5, 0.5, 0.5, 0.0);
                serverLevel.sendParticles(ParticleTypes.LAVA, golem.getX(), golem.getY() + 1.5, golem.getZ(), 40, 1.0, 1.0, 1.0, 0.2);
                serverLevel.playSound(null, golem.getX(), golem.getY(), golem.getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.5f, 0.5f);
            }
        });

        // Notificación de bienvenida con las maldiciones activas al unirse al servidor
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            ServerPlayNetworking.send(player, new BloodMoonSyncPayload(
                    CursosActivos.isBloodMoonActive(),
                    CursosActivos.isEclipseActive(),
                    CursosActivos.isContaminacion(),
                    CursosActivos.isCuevasTenebrosas(),
                    CursosActivos.isMaresAcidos(),
                    CursosActivos.isGritoPrimal(),
                    CursosActivos.isSombraParpadeante(),
                    0
            ));

            if (player instanceof VidasJugadorAccessor accessor) {
                if (!accessor.ruleta$isVidasInicializadas()) {
                    accessor.ruleta$setVidas(CursosActivos.getVidasIniciales());
                    accessor.ruleta$setVidasInicializadas(true);
                }
                if (accessor.ruleta$getVidas() <= 0) {
                    player.setGameMode(GameType.SPECTATOR);
                }
                sincronizarVidasJugador(player);

                for (ServerPlayer existing : server.getPlayerList().getPlayers()) {
                    if (existing != player && existing instanceof VidasJugadorAccessor existingAccessor) {
                        ServerPlayNetworking.send(player, new VidasJugadorPayload(
                                existing.getUUID(),
                                existingAccessor.ruleta$getVidas(),
                                CursosActivos.getVidasIniciales()
                        ));
                    }
                }
            }

            if (!CursosActivos.isMundoConfigurado() && esAdministrador(player, server)) {
                programarTarea(server.getTickCount() + 10, () -> {
                    if (!CursosActivos.isMundoConfigurado() && player.connection != null) {
                        ServerPlayNetworking.send(player, new AbrirMenuAdminPayload(CursosActivos.isRuletaAutomatica(), CursosActivos.getVidasIniciales()));
                    }
                });
            }

            if (CursosActivos.hayMaldicionesActivas()) {
                player.sendSystemMessage(Component.literal("§6§l[PermaRuleta] §c¡Atención! Este mundo tiene maldiciones activas:"));
                for (Component msg : CursosActivos.obtenerMensajesMaldicionesActivas()) {
                    player.sendSystemMessage(msg);
                }
            }
        });

        // Configuración y equipamiento al cargar entidades
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            // Detección directa por NBT tags (para comandos /summon vanilla, generadores o mods externos)
            if (entity instanceof Creeper creeper) {
                if (creeper.entityTags().contains("creeper_termonuclear")) {
                    configurarCreeperTermonuclear(creeper);
                } else if (creeper.entityTags().contains("creeper_antimateria")) {
                    configurarCreeperAntimateria(creeper);
                } else if (creeper.entityTags().contains("creeper_cosmico")) {
                    configurarCreeperCosmico(creeper);
                } else if (creeper.entityTags().contains("creeper_fantasma")) {
                    configurarCreeperFantasma(creeper);
                } else if (creeper.entityTags().contains("creeper_avispon")) {
                    configurarCreeperAvispon(creeper);
                } else if (creeper.entityTags().contains("creeper_escondido")) {
                    configurarCreeperEscondido(creeper);
                } else if (creeper.entityTags().contains("mini_multicreeper") || creeper.entityTags().contains("mini_creeper")) {
                    configurarMiniCreeper(creeper);
                }
            } else if (entity instanceof PiglinBrute brute && (brute.entityTags().contains("piglin_verdugo") || brute.entityTags().contains("el_verdugo"))) {
                configurarElVerdugo(brute, level);
            } else if (entity instanceof Hoglin hoglin && hoglin.entityTags().contains("mega_hoglin")) {
                configurarMegaHoglin(hoglin);
            } else if (entity instanceof Vindicator vindicator && (vindicator.entityTags().contains("berserker_equipado") || vindicator.entityTags().contains("berserker"))) {
                configurarBerserker(vindicator, level);
            } else if (entity instanceof Stray stray && (stray.entityTags().contains("nigromante_criptas") || stray.entityTags().contains("nigromante"))) {
                configurarNigromanteCriptas(stray, level);
            } else if (entity instanceof MagmaCube magmaCube && magmaCube.entityTags().contains("colosal_fusion")) {
                configurarColosalFusion(magmaCube);
            } else if (entity instanceof WitherSkeleton ws) {
                if (ws.entityTags().contains("cazador_igneo")) {
                    configurarCazadorIgneo(ws, level);
                } else if (ws.entityTags().contains("intruso_calaverico_equipado") || ws.entityTags().contains("intruso_calaverico")) {
                    configurarIntrusoCalaverico(ws, level);
                }
            } else if (entity instanceof Endermite endermite && endermite.entityTags().contains("giga_endermite")) {
                configurarGigaEndermite(endermite);
            } else if (entity instanceof Phantom phantom && phantom.entityTags().contains("phantom_alfa")) {
                configurarPhantomAlfa(phantom);
            } else if (entity instanceof Slime slime && !(entity instanceof MagmaCube) && (slime.entityTags().contains("slime_gigante_pantano") || slime.entityTags().contains("slime_gigante"))) {
                configurarSlimeGigante(slime);
            } else if (entity instanceof Chicken chicken && (chicken.entityTags().contains("cucco_salvaje_aplicado") || chicken.entityTags().contains("cucco_salvaje"))) {
                configurarCuccoSalvaje(chicken);
            } else if (entity instanceof Zombie zombie && (zombie.entityTags().contains("apoc_mutante") || zombie.entityTags().contains("apocalipsis_zombie"))) {
                configurarApocalipsisZombie(zombie);
            } else if (entity instanceof Drowned drowned && (drowned.entityTags().contains("pykete_arponero") || drowned.entityTags().contains("pykete"))) {
                configurarPykete(drowned);
            } else if (entity instanceof Enderman enderman && enderman.entityTags().contains("sombra_parpadeante")) {
                configurarSombraParpadeante(enderman);
            } else if (entity instanceof IronGolem golem && golem.entityTags().contains("coloso_ruina")) {
                configurarColosoRuina(golem);
            }

            if (entity instanceof Zombie zombie) {
                if (CursosActivos.isApocalipsisZombie()) {
                    evaluarZombieApocalipsis(zombie);
                }
                if (CursosActivos.isFiloMortal() && !(zombie instanceof Drowned && (CursosActivos.isArtesMaritimos() || CursosActivos.isEnergiaPoseica() || CursosActivos.isMonturaAcuatica()))) {
                    equiparEspadaFiloMortal(zombie, level);
                }
                if (CursosActivos.isApocalipsisMinecraftTotal()) {
                    evaluarSpawnApocalipsisTotal(zombie, level);
                }
            }
            if (entity instanceof Drowned drowned) {
                if (CursosActivos.isArtesMaritimos() || CursosActivos.isPykete()) {
                    equiparTridenteArtesMaritimos(drowned);
                }
                if (CursosActivos.isEnergiaPoseica()) {
                    aplicarEnergiaPoseica(drowned, level);
                }
                if (CursosActivos.isMonturaAcuatica()) {
                    aplicarMonturaAcuatica(drowned, level);
                }
            }
            if (entity instanceof Chicken chicken && CursosActivos.isCuccosSalvajes()) {
                aplicarCuccoSalvaje(chicken);
            }
            if (entity instanceof Bee bee && CursosActivos.isAbejasAsesinas()) {
                aplicarAbejaAsesina(bee, level);
            }
            if (entity instanceof Ravager ravager && CursosActivos.isCargaPesada()) {
                aplicarCargaPesada(ravager);
            }
            if (entity instanceof Vex vex && CursosActivos.isVexGrandes()) {
                aplicarVexGrande(vex);
            }
            if (entity instanceof Vindicator vindicator) {
                if (CursosActivos.isResistenciaFisica()) {
                    aplicarResistenciaFisica(vindicator);
                }
                if (CursosActivos.isBerserker()) {
                    prepararBerserker(vindicator, level);
                }
            }
            if (entity instanceof Pillager pillager && CursosActivos.isEscopeta()) {
                equiparEscopetaPillager(pillager, level);
            }
            if (entity instanceof IronGolem golem) {
                if (CursosActivos.isColosoRuina() && !golem.entityTags().contains("coloso_ruina_evaluado")) {
                    evaluarColosoRuina(golem);
                } else if (CursosActivos.isFallaMecanica()) {
                    aplicarFallaMecanica(golem, level);
                }
            }
            if (entity instanceof Illusioner illusioner && CursosActivos.isAdivinaQuien()) {
                prepararIllusioner(illusioner, level);
            }
            if (entity instanceof Raider raider && !(entity instanceof Illusioner) && !(entity instanceof Witch) && CursosActivos.isAdivinaQuien()) {
                evaluarSpawnIllusionerAcompaniante(raider, level);
            }
            if (entity instanceof PiglinBrute brute && CursosActivos.isElVerdugo()) {
                evaluarVerdugo(brute, level);
            }
            if (entity instanceof Phantom phantom && CursosActivos.isPhantomAlfa()) {
                evaluarPhantomAlfa(phantom);
            }
            if (entity instanceof ElderGuardian elderGuardian && CursosActivos.isReyDeLoProfundo()) {
                aplicarReyDeLoProfundo(elderGuardian);
            }
            if (entity instanceof WitherSkeleton witherSkeleton) {
                if (CursosActivos.isIntrusosCalavericos()) {
                    prepararIntrusoCalaverico(witherSkeleton, level);
                }
                if (CursosActivos.isCazadorIgneo()) {
                    evaluarCazadorIgneo(witherSkeleton, level);
                }
            }
            if (entity instanceof MagmaCube magmaCube && CursosActivos.isColosalFusion()) {
                evaluarColosalFusion(magmaCube, level);
            }
            if (entity instanceof Stray stray && CursosActivos.isNigromanteCriptas()) {
                evaluarNigromanteCriptas(stray, level);
            }
            if (entity instanceof Spider spider && CursosActivos.isSpiderman()) {
                aplicarSpiderman(spider);
            }
            if (entity instanceof Ghast ghast && CursosActivos.isEnderGhast()) {
                prepararEnderGhast(ghast, level);
            }
            if (entity instanceof Slime slime && !(entity instanceof MagmaCube) && CursosActivos.isAtaqueSlimes()) {
                evaluarSlimePantano(slime, level);
            }
            if (entity instanceof Endermite endermite && CursosActivos.isGigaEndermite()) {
                evaluarGigaEndermite(endermite, level);
            }
            if (CursosActivos.isGigaEndermite() && entity.getType() == getEndermanType()) {
                evaluarSpawnNaturalGigaEndermiteEnEnd(entity, level);
            }
            if (entity instanceof Hoglin hoglin && CursosActivos.isMegaHoglin()) {
                evaluarMegaHoglin(hoglin, level);
            }
            if (CursosActivos.isCreeperCosmico() && level.dimension().equals(Level.END)
                    && (entity instanceof Shulker || entity.getType() == getEndermanType())) {
                evaluarSpawnCreeperCosmico(entity, level);
            }
            if (entity instanceof Creeper creeper) {
                if (CursosActivos.isCreeperAntimateria()) {
                    evaluarCreeperAntimateria(creeper);
                }
                if (CursosActivos.isCreeperFantasma()) {
                    evaluarCreeperFantasma(creeper);
                }
                if (CursosActivos.isCreeperTermonuclear()) {
                    evaluarCreeperTermonuclear(creeper);
                }
                if (CursosActivos.isCreeperAvispon()) {
                    evaluarCreeperAvispon(creeper);
                }
            }
            if (entity instanceof Monster monster && CursosActivos.isBloodMoonActive() && level.dimension().equals(Level.OVERWORLD)) {
                aplicarBuffsLunaDeSangre(monster);
            }
            if (entity instanceof Creaking creaking && CursosActivos.isEclipseActive() && level.dimension().equals(Level.OVERWORLD)) {
                creaking.addEffect(new MobEffectInstance(MobEffects.SPEED, MobEffectInstance.INFINITE_DURATION, 1));
            }
        });

        // Bucle de actualización periódica en el servidor
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            long currentTick = server.getTickCount();

            // Tareas programadas (animaciones y eventos diferidos)
            if (!TAREAS_PROGRAMADAS.isEmpty()) {
                List<RunnableTask> snapshot = new ArrayList<>(TAREAS_PROGRAMADAS);
                TAREAS_PROGRAMADAS.removeIf(task -> currentTick >= task.tickEjecucion());
                for (RunnableTask task : snapshot) {
                    if (currentTick >= task.tickEjecucion()) {
                        try {
                            task.accion().run();
                        } catch (Exception e) {
                            LOGGER.error("Error al ejecutar tarea programada de la ruleta", e);
                        }
                    }
                }
            }

            // Cuevas Tenebrosas (Nivel 1): Oscuridad por debajo de Y=0 (se reduce cuando el jugador porte un farol en la mano secundaria)
            if (CursosActivos.isCuevasTenebrosas() && currentTick % 20 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                    if (player.getY() < 0.0) {
                        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false, true));
                    }
                }
            }

            // Cadáveres Pisados (Nivel 1): Pisar arena de almas otorga Debilidad I
            if (CursosActivos.isCadaveresPisados() && currentTick % 10 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                    ServerLevel sLvl = (ServerLevel) player.level();
                    BlockPos posAt = player.blockPosition();
                    BlockPos posBelow = player.getBlockPosBelowThatAffectsMyMovement();
                    if (sLvl.getBlockState(posAt).is(Blocks.SOUL_SAND) || sLvl.getBlockState(posBelow).is(Blocks.SOUL_SAND)) {
                        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, true, true));
                    }
                }
            }

            // Invasión Azul (Nivel 2): Los Breeze pueden spawnear de día en todos los biomas del Overworld (15% probabilidad, máx. 3 en 32 bloques)
            if (CursosActivos.isInvasionAzul() && currentTick % 80 == 0) {
                EntityType<?> breezeType = getBreezeType();
                if (breezeType != null) {
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                        if (!player.level().dimension().equals(Level.OVERWORLD)) continue;
                        ServerLevel sLvl = (ServerLevel) player.level();
                        if (!sLvl.isBrightOutside() || sLvl.getDifficulty() == Difficulty.PEACEFUL) continue;

                        var nearbyBreezes = sLvl.getEntitiesOfClass(Breeze.class, player.getBoundingBox().inflate(32.0), Entity::isAlive);
                        if (nearbyBreezes.size() >= 3) continue;

                        if (ThreadLocalRandom.current().nextFloat() < 0.15f) {
                            intentarSpawnearBreezeDia(sLvl, player, breezeType);
                        }
                    }
                }
            }

            // Falla Mecánica (Nivel 3): Gólems x1.8 con Fuerza y Velocidad escalables por deterioro y locura <25% HP
            if (CursosActivos.isFallaMecanica() && currentTick % 20 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof IronGolem golem && golem.isAlive()) {
                            aplicarFallaMecanica(golem, lvl);
                        }
                    }
                }
            }

            // Adivina Quién Ha Vuelto (Nivel 3): Illusioners equipados y acompañando raids, outposts y patrullas
            if (CursosActivos.isAdivinaQuien() && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Illusioner illusioner && illusioner.isAlive() && !illusioner.entityTags().contains("illusioner_equipado")) {
                            prepararIllusioner(illusioner, lvl);
                        } else if (entity instanceof Raider raider && !(entity instanceof Illusioner) && !(entity instanceof Witch)
                                && raider.isAlive() && !raider.entityTags().contains("illusioner_spawn_evaluado")) {
                            evaluarSpawnIllusionerAcompaniante(raider, lvl);
                        }
                    }
                }
            }

            // Cuevas Tenebrosas (Nivel 1): Oscuridad por debajo de la capa 0.
            // Portar farol (en mano principal o secundaria) anula la oscuridad. Portar antorcha atenúa el efecto en menor medida.
            if (CursosActivos.isCuevasTenebrosas() && currentTick % 10 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                    if (player.getY() < 0.0) {
                        ItemStack main = player.getMainHandItem();
                        ItemStack off = player.getOffhandItem();
                        boolean tieneFarol = main.is(Items.LANTERN) || main.is(Items.SOUL_LANTERN)
                                || off.is(Items.LANTERN) || off.is(Items.SOUL_LANTERN);
                        boolean tieneAntorcha = main.is(Items.TORCH) || main.is(Items.SOUL_TORCH) || main.is(Items.REDSTONE_TORCH)
                                || off.is(Items.TORCH) || off.is(Items.SOUL_TORCH) || off.is(Items.REDSTONE_TORCH);

                        if (tieneFarol) {
                            if (player.hasEffect(MobEffects.DARKNESS)) {
                                player.removeEffect(MobEffects.DARKNESS);
                            }
                        } else if (tieneAntorcha) {
                            // Antorcha protege pero en mucha menor medida: pulsos breves cada 8 segundos (160 ticks)
                            if (currentTick % 160 == 0) {
                                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 50, 0, false, false, true));
                            }
                        } else {
                            // Sin fuentes de luz en mano: Oscuridad continua
                            if (currentTick % 40 == 0) {
                                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 120, 0, false, false, true));
                            }
                        }
                    }
                }
            }

            // Cuccos Salvajes (Nivel 1): Pollos con 15 de vida que atacan (1 corazón) al ser golpeados
            if (CursosActivos.isCuccosSalvajes()) {
                if (currentTick % 40 == 0) {
                    for (ServerLevel lvl : server.getAllLevels()) {
                        for (Entity entity : lvl.getAllEntities()) {
                            if (entity instanceof Chicken chicken && chicken.isAlive() && !chicken.entityTags().contains("cucco_salvaje_aplicado")) {
                                aplicarCuccoSalvaje(chicken);
                            }
                        }
                    }
                }
                if (currentTick % 10 == 0) {
                    for (ServerLevel lvl : server.getAllLevels()) {
                        for (Entity entity : lvl.getAllEntities()) {
                            if (entity instanceof Chicken chicken && chicken.isAlive() && chicken.entityTags().contains("cucco_enfurecido")) {
                                Player target = lvl.getNearestPlayer(
                                        chicken.getX(), chicken.getY(), chicken.getZ(),
                                        24.0,
                                        e -> e instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator()
                                );
                                if (target != null) {
                                    chicken.getNavigation().moveTo(target, 1.6);
                                    if (chicken.distanceToSqr(target) <= 2.25 && currentTick % 20 == 0) {
                                        target.hurtServer(lvl, lvl.damageSources().mobAttack(chicken), 2.0f);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Abejas Asesinas (Nivel 2): Abejas agresivas que aplican Wither II
            if (CursosActivos.isAbejasAsesinas() && currentTick % 20 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Bee bee && bee.isAlive()) {
                            aplicarAbejaAsesina(bee, lvl);
                        }
                    }
                }
            }

            // Carga Pesada (Nivel 2): Devastador con 150 corazones (300 HP), x1.5 daño y Lentitud II
            if (CursosActivos.isCargaPesada() && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Ravager ravager && ravager.isAlive()) {
                            aplicarCargaPesada(ravager);
                        }
                    }
                }
            }

            // Vex Grandes (Nivel 2): Vexes x2 tamaño y x2 daño
            if (CursosActivos.isVexGrandes() && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Vex vex && vex.isAlive() && !vex.entityTags().contains("vex_grande_aplicado")) {
                            aplicarVexGrande(vex);
                        }
                    }
                }
            }

            // Resistencia Física (Nivel 2): Vindicadores con Resistencia I y Resistencia al Fuego
            if (CursosActivos.isResistenciaFisica() && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Vindicator vindicator && vindicator.isAlive()) {
                            aplicarResistenciaFisica(vindicator);
                        }
                    }
                }
            }

            // Apocalipsis Zombie: mutaciones de escalado e inmunidad solar
            if (CursosActivos.isApocalipsisZombie() && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Zombie zombie && !zombie.entityTags().contains("apoc_evaluado")) {
                            evaluarZombieApocalipsis(zombie);
                        }
                    }
                }
            }

            // Filo Mortal: espadas con Filo III a zombies
            if (CursosActivos.isFiloMortal() && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Zombie zombie && !zombie.entityTags().contains("filo_mortal_equipado")) {
                            if (zombie instanceof Drowned && (CursosActivos.isArtesMaritimos() || CursosActivos.isEnergiaPoseica() || CursosActivos.isMonturaAcuatica())) {
                                continue;
                            }
                            equiparEspadaFiloMortal(zombie, lvl);
                        }
                    }
                }
            }

            // Artes Marítimos (Nivel 3), Pykete (Nivel 3), Energía Poseica (Nivel 3) y Montura Acuática (Nivel 3)
            if ((CursosActivos.isArtesMaritimos() || CursosActivos.isPykete() || CursosActivos.isEnergiaPoseica() || CursosActivos.isMonturaAcuatica()) && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Drowned drowned && drowned.isAlive()) {
                            if (CursosActivos.isArtesMaritimos() || CursosActivos.isPykete()) {
                                equiparTridenteArtesMaritimos(drowned);
                            }
                            if (CursosActivos.isEnergiaPoseica()) {
                                aplicarEnergiaPoseica(drowned, lvl);
                            }
                            if (CursosActivos.isMonturaAcuatica()) {
                                aplicarMonturaAcuatica(drowned, lvl);
                            }
                        }
                    }
                }
            }

            // Pykete Arponero (Nivel 3): lanzamiento activo de tridentes con cuerda de algas y arrastre
            if (currentTick % 5 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Drowned drowned && drowned.isAlive()
                                && (drowned.entityTags().contains("pykete_arponero") || (CursosActivos.isPykete() && drowned.entityTags().contains("artes_maritimos_equipado")))) {
                            procesarPykete(drowned, lvl, currentTick);
                        }
                    }
                }
            }

            // Conducir monturas acuáticas (Delfín / Nautilo) hacia el objetivo del Ahogado
            if (CursosActivos.isMonturaAcuatica() && currentTick % 15 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Drowned drowned && drowned.isAlive() && drowned.isPassenger()) {
                            Entity vehicle = drowned.getVehicle();
                            LivingEntity target = drowned.getTarget();
                            if (vehicle instanceof Mob mount && mount.isAlive() && target != null && target.isAlive()) {
                                mount.getNavigation().moveTo(target, 1.35);
                            }
                        }
                    }
                }
            }

            // Infestación Minera (Nivel 3): Bloques infestados en las paredes de las Mineshafts
            if (CursosActivos.isInfestacionMinera() && currentTick % 40 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                    if (!player.level().dimension().equals(Level.OVERWORLD)) continue;
                    if (player.getY() > 55.0) continue;
                    infestarParedesMineshaft((ServerLevel) player.level(), player.blockPosition());
                }
            }

            // Magma al Contacto (Nivel 3): Tocar directamente cualquier cara de un bloque de magma hace 1x10^9 de daño
            if (CursosActivos.isMagmaContacto() && currentTick % 5 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                    ServerLevel sLvl = (ServerLevel) player.level();
                    if (estaTocandoMagmaDirectamente(sLvl, player.getBoundingBox())) {
                        player.hurtServer(sLvl, sLvl.damageSources().hotFloor(), 1.0E9f);
                    }
                }
            }

            // ¿Contaminación? (Nivel 4): Sobre la capa Y=160, humo/niebla verde y Veneno V
            if (CursosActivos.isContaminacion() && currentTick % 20 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                    if (!player.level().dimension().equals(Level.OVERWORLD)) continue;
                    if (player.getY() > 160.0) {
                        player.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 4, false, true, true));
                    }
                }
            }

            // Rey de lo Profundo (Nivel 4): Elder Guardian con 100 de vida
            if (CursosActivos.isReyDeLoProfundo() && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof ElderGuardian elder && elder.isAlive() && !elder.entityTags().contains("rey_profundo_aplicado")) {
                            aplicarReyDeLoProfundo(elder);
                        }
                    }
                }
            }

            // Desierto Abrasador (Nivel 3): Quedarse en el desierto durante 15s (300 ticks) acumula Insolación (1 corazón cada 2s y partículas de agua)
            // Se reinicia al entrar en contacto con el agua o beber agua. Se anula con casco puesto (gastando durabilidad).
            if (CursosActivos.isDesiertoAbrasador() && currentTick % 10 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) {
                        TICKS_INSOLACION_DESIERTO.remove(player.getUUID());
                        continue;
                    }
                    ServerLevel sLvl = (ServerLevel) player.level();
                    BlockPos pos = player.blockPosition();
                    boolean enDesierto = sLvl.getBiome(pos).is(Biomes.DESERT);
                    boolean enContactoAgua = player.isInWater() || player.isInWaterOrRain() || player.isUnderWater() || player.isInShallowWater();
                    ItemStack casco = player.getItemBySlot(EquipmentSlot.HEAD);
                    boolean tieneCasco = !casco.isEmpty();

                    if (enContactoAgua) {
                        TICKS_INSOLACION_DESIERTO.put(player.getUUID(), 0);
                        continue;
                    }

                    if (tieneCasco) {
                        TICKS_INSOLACION_DESIERTO.put(player.getUUID(), 0);
                        // El casco protege por completo de la insolación, pero desgasta su durabilidad en el desierto
                        if (enDesierto && currentTick % 40 == 0 && casco.isDamageableItem()) {
                            casco.hurtAndBreak(1, player, EquipmentSlot.HEAD);
                        }
                        continue;
                    }

                    int acumulados = TICKS_INSOLACION_DESIERTO.getOrDefault(player.getUUID(), 0);
                    if (enDesierto) {
                        acumulados = Math.min(2400, acumulados + 10);
                    } else {
                        acumulados = Math.max(0, acumulados - 20);
                    }
                    TICKS_INSOLACION_DESIERTO.put(player.getUUID(), acumulados);

                    if (acumulados >= 300) {
                        // Partículas de agua cayendo del personaje
                        sLvl.sendParticles(
                                ParticleTypes.FALLING_WATER,
                                player.getX(), player.getY() + 1.1, player.getZ(),
                                6, 0.28, 0.35, 0.28, 0.0
                        );
                        // Quemaduras leves de 1 corazón (2.0f) cada 2 segundos (40 ticks)
                        if (currentTick % 40 == 0) {
                            player.hurtServer(sLvl, sLvl.damageSources().dryOut(), 2.0f);
                        }
                    }
                }
            }

            // Congelación Profunda (Nivel 4): Biomas nevados >15s sin fuente de calor -> mitad de velocidad y daño por congelación
            if (CursosActivos.isCongelacionProfunda() && currentTick % 20 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) {
                        TICKS_EXPOSICION_NIEVE.remove(player.getUUID());
                        continue;
                    }
                    ServerLevel sLvl = (ServerLevel) player.level();
                    BlockPos pos = player.blockPosition();
                    boolean biomaNevado = sLvl.getBiome(pos).value().coldEnoughToSnow(pos, sLvl.getSeaLevel());
                    boolean cercaCalor = tieneFuenteDeCalorCercana(sLvl, pos, 6);

                    int acumulados = TICKS_EXPOSICION_NIEVE.getOrDefault(player.getUUID(), 0);
                    if (biomaNevado && !cercaCalor) {
                        acumulados = Math.min(2400, acumulados + 20);
                    } else {
                        acumulados = Math.max(0, acumulados - 40);
                    }
                    TICKS_EXPOSICION_NIEVE.put(player.getUUID(), acumulados);

                    if (acumulados >= 300) {
                        // Más de 15 segundos (300 ticks): reduce velocidad a la mitad y aplica congelación progresiva
                        player.setTicksFrozen(Math.max(player.getTicksFrozen(), 200));
                        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 50, 2, false, false, true));
                        float freezeDmg = 1.0f + Math.min(6.0f, (acumulados - 300) / 120.0f);
                        player.hurtServer(sLvl, sLvl.damageSources().freeze(), freezeDmg);
                    }
                }
            }

            // Paranoia Nocturna (Nivel 1): Nivel de luz 0 por >15s -> pasos/siseos a la espalda y Ceguera I por 2s cada 40s
            if (CursosActivos.isParanoiaNocturna() && currentTick % 20 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) {
                        TICKS_OSCURIDAD_PARANOIA.remove(player.getUUID());
                        continue;
                    }
                    ServerLevel sLvl = (ServerLevel) player.level();
                    BlockPos pos = player.blockPosition();
                    int blockLight = sLvl.getBrightness(LightLayer.BLOCK, pos);
                    int skyLight = Math.max(0, sLvl.getBrightness(LightLayer.SKY, pos) - sLvl.getSkyDarken());
                    int totalLight = Math.max(blockLight, skyLight);

                    int acumulados = TICKS_OSCURIDAD_PARANOIA.getOrDefault(player.getUUID(), 0);
                    if (totalLight == 0) {
                        acumulados = Math.min(2400, acumulados + 20);
                    } else {
                        acumulados = Math.max(0, acumulados - 40);
                    }
                    TICKS_OSCURIDAD_PARANOIA.put(player.getUUID(), acumulados);

                    if (acumulados >= 300) {
                        long lastTrigger = ULTIMA_PARANOIA_NOTIFICACION.getOrDefault(player.getUUID(), -1000L);
                        if (sLvl.getGameTime() - lastTrigger >= 800L) {
                            ULTIMA_PARANOIA_NOTIFICACION.put(player.getUUID(), sLvl.getGameTime());
                            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false, true));

                            Vec3 look = player.getLookAngle();
                            double sx = player.getX() - look.x * 2.5;
                            double sy = player.getY();
                            double sz = player.getZ() - look.z * 2.5;
                            if (ThreadLocalRandom.current().nextBoolean()) {
                                sLvl.playSound(null, sx, sy, sz, SoundEvents.CREEPER_PRIMED, SoundSource.HOSTILE, 1.3f, 0.95f);
                            } else {
                                sLvl.playSound(null, sx, sy, sz, SoundEvents.ZOMBIE_STEP, SoundSource.HOSTILE, 1.5f, 0.8f);
                            }
                        }
                    }
                }
            }

            // Mares Ácidos (Nivel 6): Agua corrosiva en Overworld (2 corazones/s y desgaste x3 de armadura)
            if (CursosActivos.isMaresAcidos() && currentTick % 20 == 0) {
                EquipmentSlot[] armorSlots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                    if (player.level().dimension().equals(Level.OVERWORLD) && (player.isInWater() || player.isUnderWater())) {
                        ServerLevel sLvl = (ServerLevel) player.level();
                        player.hurtServer(sLvl, sLvl.damageSources().magic(), 4.0f);
                        for (EquipmentSlot slot : armorSlots) {
                            ItemStack piece = player.getItemBySlot(slot);
                            if (!piece.isEmpty() && piece.isDamageableItem()) {
                                int newDmg = piece.getDamageValue() + 3;
                                if (newDmg >= piece.getMaxDamage()) {
                                    player.setItemSlot(slot, ItemStack.EMPTY);
                                    sLvl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0f, 1.0f);
                                } else {
                                    piece.setDamageValue(newDmg);
                                }
                            }
                        }
                        sLvl.sendParticles(ParticleTypes.ITEM_SLIME, player.getX(), player.getY() + 0.6, player.getZ(), 14, 0.35, 0.45, 0.35, 0.05);
                        sLvl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 0.65f, 1.3f);
                    }
                }
            }

            // Tensión Muscular (Nivel 4): tensar arco o ballesta >3s (60 ticks) fatiga los brazos
            if (CursosActivos.isTensionMuscular() && currentTick % 2 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                    if (player.isUsingItem()) {
                        ItemStack using = player.getUseItem();
                        if (using.is(Items.BOW) || using.is(Items.CROSSBOW)) {
                            int ticks = player.getTicksUsingItem();
                            if (ticks >= 60) {
                                player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1, false, false, true));
                                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, false, false, true));

                                if (currentTick % 4 == 0) {
                                    player.sendSystemMessage(Component.literal("§c💪 ¡Tus brazos tiemblan de fatiga por tensar demasiado el arma!"), true);
                                    ServerLevel sLvl = (ServerLevel) player.level();
                                    sLvl.sendParticles(ParticleTypes.CRIT, player.getX(), player.getEyeY(), player.getZ(), 2, 0.15, 0.15, 0.15, 0.05);
                                    float jitterPitch = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 3.5f;
                                    float jitterYaw = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 3.5f;
                                    player.setXRot(player.getXRot() + jitterPitch);
                                    player.setYRot(player.getYRot() + jitterYaw);
                                }

                                if (ticks >= 120) {
                                    player.stopUsingItem();
                                    player.getCooldowns().addCooldown(using, 40);
                                    player.sendSystemMessage(Component.literal("§4💪 ¡Tus brazos han colapsado por el cansancio!"), true);
                                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0f, 1.5f);
                                }
                            }
                        }
                    }
                }
            }

            // Anomalía Gravitacional (Nivel 6): saltar en el End deja flotando en caída lenta durante 3 segundos
            if (CursosActivos.isAnomaliaGravitacional()) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                    if (player.level().dimension().equals(Level.END)) {
                        boolean wasOnGround = JUGADORES_SUELO_PREVIO.getOrDefault(player.getUUID(), true);
                        boolean nowOnGround = player.onGround();
                        JUGADORES_SUELO_PREVIO.put(player.getUUID(), nowOnGround);
                        if (wasOnGround && !nowOnGround && player.getDeltaMovement().y > 0.18
                                && !player.isInWater() && !player.isPassenger() && !player.isFallFlying()) {
                            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, false, true));
                        }
                    }
                }
            }

            // Escopeta (Nivel 4): Pillagers con Multishot VI, Carga Rápida II y Perforación V
            if (CursosActivos.isEscopeta() && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Pillager pillager && pillager.isAlive() && !pillager.entityTags().contains("escopeta_equipada")) {
                            equiparEscopetaPillager(pillager, lvl);
                        }
                    }
                }
            }

            // El Verdugo (Nivel 6): Piglins Brutos gigantes y letales
            if (CursosActivos.isElVerdugo() && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof PiglinBrute brute && !brute.entityTags().contains("piglin_verdugo_evaluado")) {
                            evaluarVerdugo(brute, lvl);
                        }
                    }
                }
            }

            // Phantom Alfa (Nivel 5): evaluar Phantoms y ataque sónico en picada
            if (currentTick % 10 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Phantom phantom && phantom.isAlive()) {
                            if (CursosActivos.isPhantomAlfa() && !phantom.entityTags().contains("phantom_alfa_evaluado")) {
                                evaluarPhantomAlfa(phantom);
                            }
                            if (phantom.entityTags().contains("phantom_alfa")) {
                                ejecutarOndaPhantomAlfa(phantom, lvl, currentTick);
                            }
                        }
                    }
                }
            }

            // Parece que va´ llover (Nivel 6): Tormentas frecuentes y rayos atraídos por armadura y herramientas metálicas
            if (CursosActivos.isPareceQueVaALlover()) {
                ServerLevel overworld = server.overworld();
                // Aumentar la frecuencia de tormentas eléctricas si está despejado
                if (currentTick % 900 == 0 && !overworld.isThundering()) {
                    if (ThreadLocalRandom.current().nextFloat() < 0.20f) {
                        server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), "weather thunder 6000");
                        server.getPlayerList().broadcastSystemMessage(
                                Component.literal("§8⚡ El cielo se oscurece con truenos retumbantes... Parece que va´ llover..."), false);
                    }
                }

                // Durante tormenta eléctrica en el Overworld
                if (overworld.isThundering()) {
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        if (!player.isAlive() || player.isCreative() || player.isSpectator()) {
                            TICKS_AVISO_RAYO.remove(player.getUUID());
                            continue;
                        }
                        if (!player.level().dimension().equals(Level.OVERWORLD)) continue;

                        UUID pid = player.getUUID();
                        ServerLevel sLvl = (ServerLevel) player.level();
                        boolean bajoTecho = !sLvl.canSeeSky(player.blockPosition())
                                || sLvl.getHeight(Heightmap.Types.MOTION_BLOCKING, player.getBlockX(), player.getBlockZ()) > player.getBlockY() + 1;
                        boolean tieneMetal = tieneConductorEquipado(player);

                        if (TICKS_AVISO_RAYO.containsKey(pid)) {
                            // Si se refugió bajo techo, el rayo se cancela
                            if (bajoTecho) {
                                TICKS_AVISO_RAYO.remove(pid);
                                player.sendSystemMessage(Component.literal("§a⚡ El techo bloquea la atracción del rayo..."), true);
                                sLvl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.8f, 1.2f);
                                continue;
                            }
                            // Si se despojó completamente de la armadura y no sostiene metales, se cancela el rayo
                            if (!tieneMetal) {
                                TICKS_AVISO_RAYO.remove(pid);
                                player.sendSystemMessage(Component.literal("§a⚡ La carga eléctrica se ha disipado a tierra..."), true);
                                sLvl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.8f, 1.2f);
                                continue;
                            }

                            // Contador de aviso previo con partículas de estática
                            int remaining = TICKS_AVISO_RAYO.get(pid);
                            remaining--;
                            if (remaining > 0) {
                                TICKS_AVISO_RAYO.put(pid, remaining);
                                if (currentTick % 2 == 0) {
                                    sLvl.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                                            player.getX(), player.getY() + 1.0, player.getZ(),
                                            6, 0.35, 0.55, 0.35, 0.05);
                                }
                                if (remaining % 20 == 0) {
                                    int segs = remaining / 20;
                                    player.sendSystemMessage(
                                            Component.literal("§c⚡ ¡Rayo inminente en " + segs + "s! ¡Busca techo o suelta metales!"), true);
                                    sLvl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEE_STING, SoundSource.PLAYERS, 0.7f, 0.5f);
                                }
                            } else {
                                // Expiró el tiempo de aviso y sigue expuesto: impacto fulminante
                                TICKS_AVISO_RAYO.remove(pid);
                                EntityType<?> boltType = getLightningBoltType();
                                if (boltType != null) {
                                    Entity bolt = boltType.create(sLvl, EntitySpawnReason.EVENT);
                                    if (bolt != null) {
                                        bolt.setPos(player.getX(), player.getY(), player.getZ());
                                        sLvl.addFreshEntity(bolt);
                                    }
                                }
                                // Daño directo de 5 corazones completos (10.0f)
                                player.hurtServer(sLvl, sLvl.damageSources().lightningBolt(), 10.0f);

                                int previos = RAYOS_IMPACTADOS.getOrDefault(pid, 0);
                                RAYOS_IMPACTADOS.put(pid, previos + 1);
                            }
                        } else {
                            // Si está bajo techo, no entra en la probabilidad de que le caiga un rayo
                            if (bajoTecho || !tieneMetal) {
                                continue;
                            }

                            // Evaluación periódica de selección cada 200 ticks (10s)
                            if (currentTick % 200 == 0) {
                                int previos = RAYOS_IMPACTADOS.getOrDefault(pid, 0);
                                float prob;
                                if (previos == 0) {
                                    prob = 0.15f; // 15% base
                                } else if (previos == 1) {
                                    prob = 0.45f; // +30% tras el primer impacto
                                } else {
                                    prob = Math.min(0.95f, 0.45f + (previos - 1) * 0.05f); // +5% acumulativo adicional
                                }

                                if (ThreadLocalRandom.current().nextFloat() < prob) {
                                    // Primer rayo: 10s (200 ticks). A partir del segundo disminuye 5s -> 5s (100 ticks)
                                    int avisoTicks = (previos == 0) ? 200 : 100;
                                    TICKS_AVISO_RAYO.put(pid, avisoTicks);
                                    int segsInicial = avisoTicks / 20;
                                    player.sendSystemMessage(
                                            Component.literal("§e⚡ ¡La estática eriza tu piel! ¡Rayo en " + segsInicial + "s! ¡Busca techo o suelta metales!"), true);
                                    sLvl.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 0.4f, 1.8f);
                                }
                            }
                        }
                    }
                } else {
                    TICKS_AVISO_RAYO.clear();
                }
            }

            // ¿Spiderman?: efectos permanentes a todas las arañas
            if (CursosActivos.isSpiderman() && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Spider spider && !spider.entityTags().contains("spiderman_aplicado")) {
                            aplicarSpiderman(spider);
                        }
                    }
                }
            }

            // Ender Ghast (Nivel 5): Ghasts más agresivos con mayor rango de persecución
            if (CursosActivos.isEnderGhast() && currentTick % 30 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Ghast ghast && ghast.isAlive()) {
                            prepararEnderGhast(ghast, lvl);
                        }
                    }
                }
            }

            // Mega Hoglin (Nivel 5): mantener Lentitud I activa, romper obstáculos y embestida lineal con onda expansiva desestabilizadora
            if (CursosActivos.isMegaHoglin() || currentTick % 4 == 0) {
                if (currentTick % 60 == 0) {
                    for (ServerLevel lvl : server.getAllLevels()) {
                        for (Entity entity : lvl.getAllEntities()) {
                            if (entity instanceof Hoglin hoglin && hoglin.isAlive() && hoglin.entityTags().contains("mega_hoglin")) {
                                hoglin.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 0));
                            }
                        }
                    }
                }
                if (currentTick % 4 == 0) {
                    for (ServerLevel lvl : server.getAllLevels()) {
                        for (Entity entity : lvl.getAllEntities()) {
                            if (entity instanceof Hoglin hoglin && hoglin.isAlive() && hoglin.entityTags().contains("mega_hoglin")) {
                                romperBloquesMegaHoglin(hoglin, lvl);
                            }
                        }
                    }
                }
                if (currentTick % 20 == 0) {
                    for (ServerLevel lvl : server.getAllLevels()) {
                        for (Entity entity : lvl.getAllEntities()) {
                            if (entity instanceof Hoglin hoglin && hoglin.isAlive() && hoglin.entityTags().contains("mega_hoglin")) {
                                ejecutarEmbestidaMegaHoglin(hoglin, lvl, currentTick);
                            }
                        }
                    }
                }
            }

            // Giga-Endermite (Nivel 5): ataque anunciado y embestida/salto hacia el jugador
            if (currentTick % 20 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Endermite endermite && endermite.isAlive()
                                && endermite.entityTags().contains("giga_endermite")) {
                            ejecutarAtaqueGigaEndermite(endermite, lvl, currentTick);
                        }
                    }
                }
            }

            // Intrusos Calavéricos: equipar Wither Skeletons y spawn nocturno en Overworld
            if (CursosActivos.isIntrusosCalavericos()) {
                if (currentTick % 40 == 0) {
                    for (ServerLevel lvl : server.getAllLevels()) {
                        for (Entity entity : lvl.getAllEntities()) {
                            if (entity instanceof WitherSkeleton ws && !ws.entityTags().contains("intruso_calaverico_equipado")) {
                                prepararIntrusoCalaverico(ws, lvl);
                            }
                        }
                    }
                }

                if (currentTick % 60 == 0) {
                    EntityType<?> wsType = getWitherSkeletonType();
                    if (wsType != null) {
                        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                            if (!player.isAlive()) continue;
                            if (!player.level().dimension().equals(Level.OVERWORLD)) continue;

                            ServerLevel serverLevel = (ServerLevel) player.level();
                            if (!serverLevel.isDarkOutside() || serverLevel.getDifficulty() == Difficulty.PEACEFUL) continue;

                            var nearbyWS = serverLevel.getEntities(
                                    player,
                                    player.getBoundingBox().inflate(64.0),
                                    entity -> entity.getType() == wsType && entity.isAlive()
                            );
                            if (nearbyWS.size() >= 4) continue;

                            if (ThreadLocalRandom.current().nextFloat() >= 0.45f) continue;

                            intentarSpawnearWitherSkeletonNoche(serverLevel, player.blockPosition(), wsType);
                        }
                    }
                }
            }

            // Fuego Fatuo: daño continuo sin armadura completa en el Nether
            if (CursosActivos.isFuegoFatuo()) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (player.tickCount % 20 != 0) continue;
                    if (!player.level().dimension().equals(Level.NETHER)) continue;

                    if (!tieneArmorCompleta(player)) {
                        player.igniteForTicks(40);
                    }
                }
            }

            // Speedpers (Nivel 2): Velocidad II para Creepers que aparezcan en el Overworld (excluyendo Creepers Escondidos)
            if (CursosActivos.isSpeedpers() && currentTick % 40 == 0) {
                EntityType<?> creeperType = getCreeperType();
                ServerLevel overworldLvl = server.overworld();
                if (creeperType != null && overworldLvl != null) {
                    for (Entity entity : overworldLvl.getAllEntities()) {
                        if (entity.getType() == creeperType && entity instanceof LivingEntity living
                                && !entity.entityTags().contains("creeper_escondido")) {
                            living.addEffect(new MobEffectInstance(MobEffects.SPEED, 100, 1));
                        }
                    }
                }
            }

            // Wardens: aparición en Deep Dark con tiempo de calma configurado
            if (CursosActivos.isWardensDeepDark()) {
                EntityType<?> wardenType = getWardenType();
                if (wardenType != null) {
                    if (currentTick % 40 == 0) {
                        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                            if (!player.isAlive()) continue;
                            if (!player.level().dimension().equals(Level.OVERWORLD)) continue;

                            ServerLevel serverLevel = (ServerLevel) player.level();
                            BlockPos playerPos = player.blockPosition();

                            boolean enDeepDark = serverLevel.getBiome(playerPos).is(Biomes.DEEP_DARK);
                            if (!enDeepDark && playerPos.getY() > 15) continue;

                            var nearbyWardens = serverLevel.getEntities(
                                    player,
                                    player.getBoundingBox().inflate(48.0),
                                    entity -> entity.getType() == wardenType && entity.isAlive()
                            );
                            if (!nearbyWardens.isEmpty()) continue;

                            if (ThreadLocalRandom.current().nextFloat() >= 0.35f) continue;

                            intentarSpawnearWarden(serverLevel, playerPos, wardenType, 40, 8, 26);
                        }
                    }
                }
            }

            // PERMADEATH: bloqueo continuo de regeneración pasiva
            if (CursosActivos.isPermadeath() && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    if (lvl.getGameRules().get(GameRules.NATURAL_HEALTH_REGENERATION)) {
                        lvl.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, false, server);
                    }
                }
            }

            // ¡Apocalipsis Minecraft !!!! (Nivel 6): Rastrear sangre de jugadores heridos
            if (CursosActivos.isApocalipsisMinecraftTotal() && currentTick % 20 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive()) continue;
                    if (player.getHealth() < player.getMaxHealth()) {
                        ServerLevel sLvl = (ServerLevel) player.level();
                        List<Zombie> zombies = sLvl.getEntitiesOfClass(Zombie.class, player.getBoundingBox().inflate(48.0), Entity::isAlive);
                        for (Zombie z : zombies) {
                            if (z.getTarget() == null || z.getTarget() != player) {
                                z.setTarget(player);
                            }
                            z.addEffect(new MobEffectInstance(MobEffects.SPEED, 40, 0, false, false, false));
                        }
                        if (currentTick % 40 == 0) {
                            sLvl.sendParticles(ParticleTypes.DAMAGE_INDICATOR, player.getX(), player.getY() + 0.6, player.getZ(), 2, 0.2, 0.2, 0.2, 0.02);
                        }
                    }
                }
            }

            // ¡Apocalipsis Minecraft !!!! (Nivel 6): Apilar zombies en torres si el jugador está elevado (Anti-Pilares)
            if (CursosActivos.isApocalipsisMinecraftTotal() && currentTick % 10 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isSpectator() || player.isCreative()) continue;
                    ServerLevel sLvl = (ServerLevel) player.level();
                    List<Zombie> nearbyZombies = sLvl.getEntitiesOfClass(Zombie.class, player.getBoundingBox().inflate(20.0, 10.0, 20.0), Entity::isAlive);
                    if (nearbyZombies.isEmpty()) continue;

                    double pX = player.getX();
                    double pZ = player.getZ();

                    List<Zombie> groundZombies = new ArrayList<>();
                    for (Zombie z : nearbyZombies) {
                        if (!z.isPassenger()) {
                            groundZombies.add(z);
                        }
                    }
                    if (groundZombies.isEmpty()) continue;

                    groundZombies.sort(Comparator.comparingDouble(z -> z.distanceToSqr(pX, z.getY(), pZ)));
                    Zombie baseZombie = groundZombies.get(0);

                    if (player.getY() - baseZombie.getY() >= 1.8) {
                        for (Zombie z : groundZombies) {
                            if (z.getTarget() == null || z.getTarget() != player) {
                                z.setTarget(player);
                            }
                            double horizDistSq = z.distanceToSqr(pX, z.getY(), pZ);
                            if (horizDistSq > 4.0) {
                                z.getNavigation().moveTo(pX, baseZombie.getY(), pZ, 1.25);
                            }
                        }

                        double baseDistSq = baseZombie.distanceToSqr(pX, baseZombie.getY(), pZ);
                        if (baseDistSq <= 12.25) {
                            Zombie topZombie = baseZombie;
                            int towerHeight = 1;
                            while (!topZombie.getPassengers().isEmpty()) {
                                Entity rider = topZombie.getFirstPassenger();
                                if (rider instanceof Zombie rz && rz.isAlive()) {
                                    topZombie = rz;
                                    towerHeight++;
                                } else {
                                    break;
                                }
                            }

                            double heightDiff = player.getY() - topZombie.getY();
                            if (heightDiff >= 1.2 && towerHeight < 5) {
                                for (int i = 1; i < groundZombies.size(); i++) {
                                    Zombie climber = groundZombies.get(i);
                                    if (climber != baseZombie && !climber.isPassenger() && climber.getPassengers().isEmpty()) {
                                        double climberDistSq = climber.distanceToSqr(pX, climber.getY(), pZ);
                                        if (climberDistSq <= 20.25) {
                                            climber.setPos(topZombie.getX(), topZombie.getY() + 1.8, topZombie.getZ());
                                            boolean mounted = climber.startRiding(topZombie, true, true);
                                            if (mounted) {
                                                climber.setTarget(player);
                                                sLvl.getChunkSource().sendToTrackingPlayers(topZombie, new ClientboundSetPassengersPacket(topZombie));
                                                sLvl.getChunkSource().sendToTrackingPlayers(climber, new ClientboundSetPassengersPacket(climber));
                                                sLvl.playSound(null, topZombie.getX(), topZombie.getY(), topZombie.getZ(),
                                                        SoundEvents.ZOMBIE_AMBIENT, SoundSource.HOSTILE, 1.0f, 1.2f);
                                                sLvl.sendParticles(ParticleTypes.ANGRY_VILLAGER, topZombie.getX(), topZombie.getY() + 1.8, topZombie.getZ(), 5, 0.2, 0.2, 0.2, 0.0);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }

                            if (topZombie.distanceToSqr(player) <= 9.0 && currentTick % 20 == 0) {
                                topZombie.doHurtTarget(sLvl, player);
                            }
                        }
                    }
                }
            }

            // Luna de Sangre (Nivel 6) y Eclipse Solar (Nivel 6): Ciclos diarios de probabilidad (5%)
            ServerLevel overworld = server.overworld();
            if (overworld != null) {
                long clockTime = overworld.getOverworldClockTime();
                long dayTime = ((clockTime % 24000L) + 24000L) % 24000L;
                long dayNumber = Math.floorDiv(clockTime, 24000L);

                if (dayTime >= 12000L && dayTime < 12500L && CursosActivos.getUltimoDiaBloodMoon() != dayNumber) {
                    CursosActivos.setUltimoDiaBloodMoon(dayNumber);
                    if (CursosActivos.isMaldicionLunaDeSangre() && !CursosActivos.isBloodMoonActive()) {
                        if (ThreadLocalRandom.current().nextFloat() < 0.05f) {
                            CursosActivos.setBloodMoonActive(true);
                            sincronizarLunaSangreClientes(server);
                            LOGGER.info("[PermaRuleta] ¡Luna de Sangre activada para la noche del día {}!", dayNumber);
                        }
                    }
                    RuletaPersistencia.guardar(server);
                }

                if (CursosActivos.isBloodMoonActive() && (dayTime >= 23000L || dayTime < 12000L)) {
                    CursosActivos.setBloodMoonActive(false);
                    RuletaPersistencia.guardar(server);
                    sincronizarLunaSangreClientes(server);
                    LOGGER.info("[PermaRuleta] La Luna de Sangre ha finalizado al llegar el amanecer.");
                }

                // Eclipse Solar (Nivel 6): 5% de probabilidad cada mañana (tick 500-1000) y fin al anochecer (tick 12000)
                if (dayTime >= 500L && dayTime < 1000L && CursosActivos.getUltimoDiaEclipse() != dayNumber) {
                    CursosActivos.setUltimoDiaEclipse(dayNumber);
                    if (CursosActivos.isEclipseSolar() && !CursosActivos.isEclipseActive()) {
                        if (ThreadLocalRandom.current().nextFloat() < 0.05f) {
                            CursosActivos.setEclipseActive(true);
                            sincronizarLunaSangreClientes(server);
                            LOGGER.info("[PermaRuleta] ¡Eclipse Solar activado para el día {}!", dayNumber);
                        }
                    }
                    RuletaPersistencia.guardar(server);
                }

                if (CursosActivos.isEclipseActive() && dayTime >= 12000L) {
                    CursosActivos.setEclipseActive(false);
                    RuletaPersistencia.guardar(server);
                    sincronizarLunaSangreClientes(server);
                    LOGGER.info("[PermaRuleta] El Eclipse Solar ha finalizado al llegar la noche.");
                }

                // Spawns y conducción aérea del Eclipse Solar (Strays con Lanza de Diamante Filo II en Phantoms y Brujas en Murciélagos)
                if (CursosActivos.isEclipseActive() && overworld.getDifficulty() != Difficulty.PEACEFUL) {
                    if (currentTick % 80 == 0) {
                        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                            if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                            if (!player.level().dimension().equals(Level.OVERWORLD)) continue;
                            intentarSpawnearJineteEclipse(overworld, player);
                        }
                    }
                    if (currentTick % 10 == 0) {
                        for (Entity entity : overworld.getAllEntities()) {
                            if (entity instanceof Bat bat && bat.isAlive() && bat.entityTags().contains("eclipse_bat")) {
                                bat.setResting(false);
                                Player nearest = overworld.getNearestPlayer(
                                        bat.getX(), bat.getY(), bat.getZ(),
                                        48.0,
                                        e -> e instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator()
                                );
                                if (nearest != null) {
                                    double dx = nearest.getX() - bat.getX();
                                    double dy = (nearest.getY() + 2.5) - bat.getY();
                                    double dz = nearest.getZ() - bat.getZ();
                                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                                    if (dist > 2.5) {
                                        bat.setDeltaMovement(
                                                (dx / dist) * 0.28,
                                                (dy / dist) * 0.20,
                                                (dz / dist) * 0.28
                                        );
                                    }
                                    if (bat.getFirstPassenger() instanceof Witch witch && witch.isAlive()) {
                                        witch.setTarget(nearest);
                                    }
                                }
                            } else if (entity instanceof Phantom phantom && phantom.isAlive() && phantom.entityTags().contains("eclipse_inmune_sol")) {
                                Player nearest = overworld.getNearestPlayer(
                                        phantom.getX(), phantom.getY(), phantom.getZ(),
                                        64.0,
                                        e -> e instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator()
                                );
                                if (nearest != null) {
                                    phantom.setTarget(nearest);
                                    if (phantom.getFirstPassenger() instanceof Stray stray && stray.isAlive()) {
                                        stray.setTarget(nearest);
                                        if (stray.distanceToSqr(nearest) <= 9.0 && currentTick % 20 == 0) {
                                            stray.doHurtTarget(overworld, nearest);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (CursosActivos.isBloodMoonActive() && currentTick % 10 == 0) {
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        if (player.isSleeping() && player.level().dimension().equals(Level.OVERWORLD)) {
                            player.stopSleepInBed(true, true);
                            player.sendOverlayMessage(
                                    Component.literal("No puedes descansar durante la Luna de Sangre...")
                                            .withStyle(ChatFormatting.RED)
                            );
                        }
                    }
                }

                // Luna de Sangre (Nivel 6): Aplicar efectos aleatorios a todos los monstruos de la noche
                if (CursosActivos.isBloodMoonActive() && currentTick % 40 == 0) {
                    for (Entity entity : overworld.getAllEntities()) {
                        if (entity instanceof Monster monster && monster.isAlive()
                                && !monster.entityTags().contains("bloodmoon_buff_evaluado")) {
                            aplicarBuffsLunaDeSangre(monster);
                        }
                    }
                }
            }

            // Inventario Pesado (Nivel 1): Impide correr si todos los huecos del inventario están ocupados
            if (CursosActivos.isInventarioPesado() && currentTick % 5 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                    if (tieneInventarioLleno(player)) {
                        if (player.isSprinting()) {
                            player.setSprinting(false);
                        }
                        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 15, 0, true, false, true));
                    }
                }
            }

            // Ojos en la Oscuridad (Nivel 1): Endermans se vuelven hostiles a menos de 3 bloques sin mirarlos
            if (CursosActivos.isOjosEnLaOscuridad() && currentTick % 10 == 0) {
                EntityType<?> endermanType = getEndermanType();
                if (endermanType != null) {
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                        ServerLevel sLvl = (ServerLevel) player.level();
                        List<Mob> nearbyEndermen = sLvl.getEntitiesOfClass(
                                Mob.class,
                                player.getBoundingBox().inflate(3.0),
                                e -> e.isAlive() && e.getType() == endermanType && e.distanceToSqr(player) <= 9.0
                        );
                        for (Mob enderman : nearbyEndermen) {
                            if (enderman.getTarget() != player) {
                                enderman.setTarget(player);
                                if (enderman instanceof NeutralMob neutral) {
                                    neutral.startPersistentAngerTimer();
                                }
                                sLvl.playSound(null, enderman.getX(), enderman.getY(), enderman.getZ(),
                                        SoundEvents.ENDERMAN_STARE, SoundSource.HOSTILE, 1.0f, 1.0f);
                            }
                        }
                    }
                }
            }

            // Ataque a los Slimes (Nivel 4): Mega Salto periódico de los Slimes Gigantes de pantano
            if (CursosActivos.isAtaqueSlimes() && currentTick % 60 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Slime slime && slime.isAlive()
                                && slime.entityTags().contains("slime_gigante_pantano")
                                && slime.onGround()) {
                            Player nearest = lvl.getNearestPlayer(
                                    slime.getX(), slime.getY(), slime.getZ(),
                                    32.0,
                                    e -> e instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator()
                            );
                            double vx = slime.getDeltaMovement().x;
                            double vz = slime.getDeltaMovement().z;
                            if (nearest != null) {
                                double dx = nearest.getX() - slime.getX();
                                double dz = nearest.getZ() - slime.getZ();
                                double horiz = Math.sqrt(dx * dx + dz * dz);
                                if (horiz > 0.01) {
                                    vx = (dx / horiz) * 0.95;
                                    vz = (dz / horiz) * 0.95;
                                }
                            }
                            slime.setDeltaMovement(vx, 1.45, vz);
                            lvl.getChunkSource().sendToTrackingPlayers(slime, new ClientboundSetEntityMotionPacket(slime));
                            lvl.playSound(null, slime.getX(), slime.getY(), slime.getZ(),
                                    SoundEvents.SLIME_JUMP, SoundSource.HOSTILE, 2.0f, 0.5f);
                            lvl.sendParticles(ParticleTypes.ITEM_SLIME,
                                    slime.getX(), slime.getY() + 0.5, slime.getZ(),
                                    35, 1.5, 0.4, 1.5, 0.15);
                        }
                    }
                }
            }

            // Creeper Cósmico (Nivel 5): Atracción gravitacional en un radio de 30 bloques
            if (CursosActivos.isCreeperCosmico() && currentTick % 2 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Creeper creeper && creeper.isAlive()
                                && creeper.entityTags().contains("creeper_cosmico")) {
                            lvl.sendParticles(ParticleTypes.PORTAL,
                                    creeper.getX(), creeper.getY() + 1.0, creeper.getZ(),
                                    8, 0.6, 0.8, 0.6, 0.2);
                            List<Entity> pulled = lvl.getEntities(
                                    creeper,
                                    creeper.getBoundingBox().inflate(30.0),
                                    e -> e.isAlive() && !e.isSpectator()
                                            && !(e instanceof Player p && p.isCreative())
                                            && !(e instanceof Creeper c && c.entityTags().contains("creeper_cosmico"))
                                            && e.distanceToSqr(creeper) <= 900.0
                            );
                            for (Entity target : pulled) {
                                double dx = creeper.getX() - target.getX();
                                double dy = (creeper.getY() + 0.5) - target.getY();
                                double dz = creeper.getZ() - target.getZ();
                                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                                if (dist > 0.5) {
                                    double strength = 0.08 + (1.0 - Math.min(1.0, dist / 30.0)) * 0.10;
                                    target.setDeltaMovement(target.getDeltaMovement().add(
                                            (dx / dist) * strength,
                                            (dy / dist) * (strength * 0.5),
                                            (dz / dist) * strength
                                    ));
                                    if (target instanceof ServerPlayer sp) {
                                        sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Creeper de Implosión Antimateria (Nivel 6): Partículas moradas del End y evaluación
            if (currentTick % 4 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Creeper creeper && creeper.isAlive()) {
                            if (CursosActivos.isCreeperAntimateria() && !creeper.entityTags().contains("creeper_antimateria_evaluado")) {
                                evaluarCreeperAntimateria(creeper);
                            }
                            if (creeper.entityTags().contains("creeper_antimateria")) {
                                lvl.sendParticles(ParticleTypes.PORTAL,
                                        creeper.getX(), creeper.getY() + 0.9, creeper.getZ(),
                                        10, 0.35, 0.6, 0.35, 0.15);
                                lvl.sendParticles(ParticleTypes.REVERSE_PORTAL,
                                        creeper.getX(), creeper.getY() + 0.9, creeper.getZ(),
                                        6, 0.3, 0.5, 0.3, 0.04);
                                lvl.sendParticles(ParticleTypes.WITCH,
                                        creeper.getX(), creeper.getY() + 0.5, creeper.getZ(),
                                        4, 0.25, 0.4, 0.25, 0.01);
                            }
                        }
                    }
                }
            }

            // Procesamiento de Vórtices de Implosión Antimateria activos (2.5s = 50 ticks de succión + colapso letal)
            if (!VORTICES_ANTIMATERIA.isEmpty()) {
                var iter = VORTICES_ANTIMATERIA.iterator();
                while (iter.hasNext()) {
                    VorticeAntimateria v = iter.next();
                    ServerLevel lvl = v.level();
                    if (currentTick < v.tickFin()) {
                        lvl.sendParticles(ParticleTypes.REVERSE_PORTAL, v.x(), v.y() + 1.0, v.z(), 35, 1.8, 1.8, 1.8, 0.35);
                        lvl.sendParticles(ParticleTypes.PORTAL, v.x(), v.y() + 1.0, v.z(), 40, 2.5, 2.5, 2.5, 0.6);
                        lvl.sendParticles(ParticleTypes.SQUID_INK, v.x(), v.y() + 1.0, v.z(), 12, 0.6, 0.6, 0.6, 0.05);
                        if (currentTick % 5 == 0) {
                            lvl.playSound(null, v.x(), v.y(), v.z(), SoundEvents.PORTAL_TRIGGER, SoundSource.HOSTILE, 1.6f, 0.5f);
                        }

                        AABB area = new AABB(v.x() - 18.0, v.y() - 18.0, v.z() - 18.0, v.x() + 18.0, v.y() + 18.0, v.z() + 18.0);
                        List<Entity> succionados = lvl.getEntities((Entity) null, area,
                                e -> e.isAlive() && !e.isSpectator() && !(e instanceof Player p && p.isCreative()));
                        for (Entity target : succionados) {
                            double dx = v.x() - target.getX();
                            double dy = (v.y() + 0.5) - target.getY();
                            double dz = v.z() - target.getZ();
                            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                            if (dist <= 18.0 && dist > 0.25) {
                                double pull = 0.22 + (1.0 - (dist / 18.0)) * 0.28;
                                target.setDeltaMovement(
                                        target.getDeltaMovement().x * 0.4 + (dx / dist) * pull,
                                        target.getDeltaMovement().y * 0.4 + (dy / dist) * pull,
                                        target.getDeltaMovement().z * 0.4 + (dz / dist) * pull
                                );
                                if (target instanceof ServerPlayer sp) {
                                    sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
                                }
                            }
                        }
                    } else {
                        iter.remove();
                        lvl.sendParticles(ParticleTypes.SONIC_BOOM, v.x(), v.y() + 1.0, v.z(), 3, 0.5, 0.5, 0.5, 0.0);
                        lvl.sendParticles(ParticleTypes.EXPLOSION_EMITTER, v.x(), v.y() + 1.0, v.z(), 4, 1.0, 1.0, 1.0, 0.0);
                        lvl.sendParticles(ParticleTypes.WITCH, v.x(), v.y() + 1.0, v.z(), 120, 2.5, 2.5, 2.5, 0.15);
                        lvl.playSound(null, v.x(), v.y(), v.z(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 2.5f, 0.6f);
                        lvl.playSound(null, v.x(), v.y(), v.z(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.5f, 0.5f);

                        AABB collapseArea = new AABB(v.x() - 12.0, v.y() - 12.0, v.z() - 12.0, v.x() + 12.0, v.y() + 12.0, v.z() + 12.0);
                        List<LivingEntity> victims = lvl.getEntitiesOfClass(LivingEntity.class, collapseArea,
                                e -> e.isAlive() && !e.isSpectator() && !(e instanceof Player p && p.isCreative()));
                        for (LivingEntity victim : victims) {
                            double dist = Math.sqrt(victim.distanceToSqr(v.x(), v.y(), v.z()));
                            if (dist <= 6.0) {
                                victim.hurtServer(lvl, lvl.damageSources().fellOutOfWorld(), 150.0f);
                            } else if (dist <= 12.0) {
                                float dmg = (float) (45.0 * (1.0 - (dist - 6.0) / 6.0));
                                victim.hurtServer(lvl, lvl.damageSources().magic(), Math.max(10.0f, dmg));
                            }
                        }
                    }
                }
            }

            // Grito Primal (Nivel 2): cuando un Enderman se enoja con el jugador, lo aturde y deja sordo por 30 segundos
            if (CursosActivos.isGritoPrimal() && currentTick % 10 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Enderman enderman && enderman.isAlive()
                                && enderman.getTarget() instanceof ServerPlayer sp
                                && sp.isAlive() && !sp.isCreative() && !sp.isSpectator()) {
                            long lastScream = ULTIMO_GRITO_ENDERMAN.getOrDefault(enderman.getUUID(), -2000L);
                            if (currentTick - lastScream >= 1200L) {
                                ULTIMO_GRITO_ENDERMAN.put(enderman.getUUID(), currentTick);
                                aplicarGritoPrimal(sp, lvl);
                            }
                        }
                    }
                }
            }

            // Como Pedro (Nivel 3): Creepers caminan sobre el agua y su velocidad se multiplica por 3
            if (CursosActivos.isComoPedro()) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Creeper creeper && creeper.isAlive()) {
                            procesarCreeperComoPedro(creeper, lvl, currentTick);
                        }
                    }
                }
            }

            // Colosal de Fusión (Nivel 4): Magma Cubes colosales en el Nether (tamaño 7-8), mega saltos y cráter de magma al aterrizar
            if (CursosActivos.isColosalFusion() && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    if (!lvl.dimension().equals(Level.NETHER)) continue;
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof MagmaCube mc && mc.isAlive() && !mc.entityTags().contains("colosal_fusion_evaluado")) {
                            evaluarColosalFusion(mc, lvl);
                        }
                    }
                }
            }
            if (currentTick % 2 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof MagmaCube mc && mc.isAlive() && mc.entityTags().contains("colosal_fusion")) {
                            procesarSaltoYCraterColosalFusion(mc, lvl, currentTick);
                        }
                    }
                }
            }

            // Cazador Ígneo (Nivel 4): 15% de Wither Skeletons en el Nether portan arco con flechas de fuego fatuo azul
            if (CursosActivos.isCazadorIgneo() && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof WitherSkeleton ws && ws.isAlive() && !ws.entityTags().contains("cazador_igneo_evaluado")) {
                            evaluarCazadorIgneo(ws, lvl);
                        }
                    }
                }
            }

            // Sombra Parpadeante (Nivel 5): Teletransporte a la espalda al mirarlos y pulso psíquico hacia sus ojos si miras al suelo
            if (CursosActivos.isSombraParpadeante() && currentTick % 4 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                    procesarSombraParpadeanteJugador(player, (ServerLevel) player.level(), currentTick);
                }
            }

            // Berserker (Nivel 5): Vindicadores con Hacha de Netherite, casco reforzado y Carrera Frenética (+60% velocidad a <15 bloques)
            if (currentTick % 10 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Vindicator vindicator && vindicator.isAlive()) {
                            if (CursosActivos.isBerserker() && !vindicator.entityTags().contains("berserker_equipado")) {
                                prepararBerserker(vindicator, lvl);
                            }
                            if (vindicator.entityTags().contains("berserker_equipado")) {
                                procesarCarreraFreneticaBerserker(vindicator, lvl);
                            }
                        }
                    }
                }
            }

            // El Nigromante de las Criptas (Nivel 5): Stray con túnica y báculo que canaliza rayo gélido e invoca mini-strays con hacha
            if (CursosActivos.isNigromanteCriptas() && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Stray stray && stray.isAlive() && !stray.entityTags().contains("nigromante_evaluado")) {
                            evaluarNigromanteCriptas(stray, lvl);
                        }
                    }
                }
            }
            if (currentTick % 5 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    List<Stray> nigromantes = new ArrayList<>();
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Stray stray && stray.isAlive() && stray.entityTags().contains("nigromante_criptas")) {
                            nigromantes.add(stray);
                        }
                    }
                    for (Stray nigromante : nigromantes) {
                        procesarNigromanteCriptas(nigromante, lvl, currentTick);
                    }
                }
            }

            // Creeper Fantasma (Nivel 5): Creeper espectral translúcido inmune a proyectiles
            if (currentTick % 6 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Creeper creeper && creeper.isAlive()) {
                            if (CursosActivos.isCreeperFantasma() && !creeper.entityTags().contains("creeper_fantasma_evaluado")) {
                                evaluarCreeperFantasma(creeper);
                            }
                            if (creeper.entityTags().contains("creeper_fantasma")) {
                                lvl.sendParticles(ParticleTypes.SOUL,
                                        creeper.getX(), creeper.getY() + 0.9, creeper.getZ(),
                                        2, 0.25, 0.45, 0.25, 0.01);
                            }
                        }
                    }
                }
            }

            // Creeper Termonuclear (Nivel 6): Creeper acorazado cuya temperatura sube en combate (>70% quema en 4 bloques, 100% explota)
            if (currentTick % 5 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    List<Creeper> termonucleares = new ArrayList<>();
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Creeper creeper && creeper.isAlive()) {
                            if (CursosActivos.isCreeperTermonuclear() && !creeper.entityTags().contains("creeper_termonuclear_evaluado")) {
                                evaluarCreeperTermonuclear(creeper);
                            }
                            if (creeper.entityTags().contains("creeper_termonuclear")) {
                                termonucleares.add(creeper);
                            }
                        }
                    }
                    for (Creeper creeper : termonucleares) {
                        procesarCreeperTermonuclear(creeper, lvl, currentTick);
                    }
                }
            }

            // Creeper Avispón (Nivel 3): Pequeño creeper volador estilo abeja con vuelo zigzagueante
            if (currentTick % 2 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    List<Creeper> avispones = new ArrayList<>();
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Creeper creeper && creeper.isAlive()) {
                            if (CursosActivos.isCreeperAvispon() && !creeper.entityTags().contains("creeper_avispon_evaluado")) {
                                evaluarCreeperAvispon(creeper);
                            }
                            if (creeper.entityTags().contains("creeper_avispon")) {
                                avispones.add(creeper);
                            }
                        }
                    }
                    for (Creeper creeper : avispones) {
                        procesarCreeperAvispon(creeper, lvl, currentTick);
                    }
                }
            }

            // El Coloso de Ruina (Nivel 6): Golem titánico de basalto con golpe sísmico y anti-torres
            for (ServerLevel lvl : server.getAllLevels()) {
                List<IronGolem> colosos = new ArrayList<>();
                for (Entity entity : lvl.getAllEntities()) {
                    if (entity instanceof IronGolem golem && golem.isAlive()) {
                        if (CursosActivos.isColosoRuina() && !golem.entityTags().contains("coloso_ruina_evaluado")) {
                            evaluarColosoRuina(golem);
                        }
                        if (golem.entityTags().contains("coloso_ruina")) {
                            colosos.add(golem);
                        }
                    }
                }
                for (IronGolem coloso : colosos) {
                    procesarColosoRuina(coloso, lvl, currentTick);
                }
            }
        });
    }

    public static void sincronizarLunaSangreClientes(MinecraftServer server) {
        if (server == null) return;
        BloodMoonSyncPayload payload = new BloodMoonSyncPayload(
                CursosActivos.isBloodMoonActive(),
                CursosActivos.isEclipseActive(),
                CursosActivos.isContaminacion(),
                CursosActivos.isCuevasTenebrosas(),
                CursosActivos.isMaresAcidos(),
                CursosActivos.isGritoPrimal(),
                CursosActivos.isSombraParpadeante(),
                0
        );
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sincronizarVidasJugador(ServerPlayer player) {
        if (player instanceof VidasJugadorAccessor accessor) {
            MinecraftServer server = player.level().getServer();
            if (server == null) return;
            int restantes = accessor.ruleta$getVidas();
            int maximas = CursosActivos.getVidasIniciales();

            // Sincronizar el HUD personal del jugador
            ServerPlayNetworking.send(player, new VidasSyncPayload(restantes, maximas));

            // Transmitir a los demás jugadores para el indicador overhead
            VidasJugadorPayload broadcastPayload = new VidasJugadorPayload(player.getUUID(), restantes, maximas);
            for (ServerPlayer other : server.getPlayerList().getPlayers()) {
                if (other != player) {
                    ServerPlayNetworking.send(other, broadcastPayload);
                }
            }
        }
    }

    public static boolean esAdministrador(ServerPlayer player, MinecraftServer server) {
        if (player == null || server == null) return false;
        return player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)
                || server.isSingleplayerOwner(player.nameAndId());
    }

    public static void aplicarCuccoSalvaje(Chicken chicken) {
        if (chicken.entityTags().contains("cucco_salvaje_aplicado")) return;
        configurarCuccoSalvaje(chicken);
    }

    public static void configurarCuccoSalvaje(Chicken chicken) {
        chicken.addTag("cucco_salvaje_aplicado");
        AttributeInstance healthAttr = chicken.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.setBaseValue(15.0);
            chicken.setHealth(15.0f);
        }
    }

    public static void aplicarAbejaAsesina(Bee bee, ServerLevel level) {
        if (bee.getTarget() instanceof Player targetPlayer && targetPlayer.isAlive()
                && !targetPlayer.isCreative() && !targetPlayer.isSpectator()) {
            return;
        }
        Player nearest = level.getNearestPlayer(
                bee.getX(), bee.getY(), bee.getZ(),
                32.0,
                entity -> entity instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator()
        );
        if (nearest != null) {
            bee.setTarget(nearest);
            bee.startPersistentAngerTimer();
        }
    }

    public static void aplicarCargaPesada(Ravager ravager) {
        if (!ravager.entityTags().contains("carga_pesada_aplicada")) {
            ravager.addTag("carga_pesada_aplicada");
            AttributeInstance healthAttr = ravager.getAttribute(Attributes.MAX_HEALTH);
            if (healthAttr != null) {
                healthAttr.setBaseValue(300.0);
                ravager.setHealth(300.0f);
            }
            AttributeInstance dmgAttr = ravager.getAttribute(Attributes.ATTACK_DAMAGE);
            if (dmgAttr != null) {
                dmgAttr.setBaseValue(dmgAttr.getBaseValue() * 1.5);
            }
        }
        ravager.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 1));
    }

    public static void aplicarVexGrande(Vex vex) {
        if (vex.entityTags().contains("vex_grande_aplicado")) return;
        vex.addTag("vex_grande_aplicado");
        AttributeInstance scaleAttr = vex.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.setBaseValue(2.0);
        }
        AttributeInstance dmgAttr = vex.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmgAttr != null) {
            dmgAttr.setBaseValue(dmgAttr.getBaseValue() * 2.0);
        }
    }

    public static void aplicarResistenciaFisica(Vindicator vindicator) {
        vindicator.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 200, 0));
        vindicator.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0));
    }

    public static void aplicarReyDeLoProfundo(ElderGuardian elderGuardian) {
        if (elderGuardian.entityTags().contains("rey_profundo_aplicado")) return;
        elderGuardian.addTag("rey_profundo_aplicado");
        AttributeInstance healthAttr = elderGuardian.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.setBaseValue(100.0);
            elderGuardian.setHealth(100.0f);
        }
    }

    public static void equiparEscopetaPillager(Pillager pillager, ServerLevel level) {
        if (pillager.entityTags().contains("escopeta_equipada")) return;
        pillager.addTag("escopeta_equipada");

        ItemStack crossbow = new ItemStack(Items.CROSSBOW);
        level.registryAccess().lookup(Registries.ENCHANTMENT).ifPresent(reg -> {
            reg.get(Enchantments.MULTISHOT).ifPresent(h -> crossbow.enchant(h, 6));
            reg.get(Enchantments.QUICK_CHARGE).ifPresent(h -> crossbow.enchant(h, 2));
            reg.get(Enchantments.PIERCING).ifPresent(h -> crossbow.enchant(h, 5));
        });
        pillager.setItemSlot(EquipmentSlot.MAINHAND, crossbow);
        pillager.setDropChance(EquipmentSlot.MAINHAND, 0.05f);
    }

    public static void aplicarFallaMecanica(IronGolem golem, ServerLevel level) {
        if (golem.isPlayerCreated()) {
            golem.setPlayerCreated(false);
        }
        if (!golem.entityTags().contains("falla_mecanica_escala")) {
            golem.addTag("falla_mecanica_escala");
            AttributeInstance scaleAttr = golem.getAttribute(Attributes.SCALE);
            if (scaleAttr != null) {
                scaleAttr.setBaseValue(1.8);
            }
        }

        // Por cada deterioro recibido aumenta en +1 sus efectos (base Fuerza I y Velocidad I = amplificador 0)
        int bonusDeterioro = switch (golem.getCrackiness()) {
            case NONE -> 0;
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
        };
        golem.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 60, bonusDeterioro, false, false, true));
        golem.addEffect(new MobEffectInstance(MobEffects.SPEED, 60, bonusDeterioro, false, false, true));

        boolean menosDe25Vida = golem.getHealth() < (golem.getMaxHealth() * 0.25f);
        if (menosDe25Vida) {
            // Al tener menos del 25% de vida, ataca a cualquier ser con vida a su alrededor
            LivingEntity currentTarget = golem.getTarget();
            if (currentTarget != null && currentTarget.isAlive() && currentTarget != golem
                    && !(currentTarget instanceof Player p && (p.isCreative() || p.isSpectator()))) {
                return;
            }
            List<LivingEntity> cercanos = level.getEntitiesOfClass(
                    LivingEntity.class,
                    golem.getBoundingBox().inflate(24.0),
                    e -> e != golem && e.isAlive() && !(e instanceof IronGolem)
                            && !(e instanceof Player p && (p.isCreative() || p.isSpectator()))
            );
            cercanos.sort(Comparator.comparingDouble(golem::distanceToSqr));
            if (!cercanos.isEmpty()) {
                golem.setTarget(cercanos.get(0));
                golem.startPersistentAngerTimer();
            }
        } else {
            if (golem.getTarget() instanceof Player targetPlayer && targetPlayer.isAlive()
                    && !targetPlayer.isCreative() && !targetPlayer.isSpectator()) {
                return;
            }
            Player nearest = level.getNearestPlayer(
                    golem.getX(), golem.getY(), golem.getZ(),
                    32.0,
                    entity -> entity instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator()
            );
            if (nearest != null) {
                golem.setTarget(nearest);
                golem.startPersistentAngerTimer();
            }
        }
    }

    public static void prepararIllusioner(Illusioner illusioner, ServerLevel level) {
        if (!illusioner.entityTags().contains("illusioner_equipado")) {
            illusioner.addTag("illusioner_equipado");
            ItemStack bow = new ItemStack(Items.BOW);
            level.registryAccess().lookup(Registries.ENCHANTMENT).ifPresent(reg -> {
                reg.get(Enchantments.PUNCH).ifPresent(h -> bow.enchant(h, 10));
                reg.get(Enchantments.POWER).ifPresent(h -> bow.enchant(h, 3));
            });
            illusioner.setItemSlot(EquipmentSlot.MAINHAND, bow);
            illusioner.setDropChance(EquipmentSlot.MAINHAND, 0.05f);
        }
        illusioner.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, MobEffectInstance.INFINITE_DURATION, 2));
    }

    public static void evaluarSpawnIllusionerAcompaniante(Raider raider, ServerLevel level) {
        if (!CursosActivos.isAdivinaQuien()) return;
        if (raider.entityTags().contains("illusioner_spawn_evaluado")) return;
        raider.addTag("illusioner_spawn_evaluado");

        boolean enRaid = raider.hasActiveRaid();
        boolean enPatrulla = raider.isPatrolLeader() || raider.hasPatrolTarget();
        boolean enOutpost = level.structureManager().getStructureWithPieceAt(
                raider.blockPosition(),
                holder -> holder.is(BuiltinStructures.PILLAGER_OUTPOST)
        ).isValid();

        if (!enRaid && !enPatrulla && !enOutpost) return;

        var nearbyIllusioners = level.getEntitiesOfClass(Illusioner.class, raider.getBoundingBox().inflate(32.0), Entity::isAlive);
        if (nearbyIllusioners.size() >= 2) return;

        if (ThreadLocalRandom.current().nextFloat() < 0.25f) {
            EntityType<?> illusionerType = getIllusionerType();
            if (illusionerType != null) {
                Entity created = illusionerType.create(level, EntitySpawnReason.EVENT);
                if (created instanceof Illusioner illusioner) {
                    illusioner.snapTo(raider.getX(), raider.getY(), raider.getZ(), raider.getYRot(), 0.0f);
                    illusioner.addTag("illusioner_spawn_evaluado");
                    prepararIllusioner(illusioner, level);
                    if (enRaid && raider.getCurrentRaid() != null) {
                        raider.getCurrentRaid().addWaveMob(level, raider.getWave(), illusioner, false);
                    } else if (enPatrulla && raider.getPatrolTarget() != null) {
                        illusioner.setPatrolTarget(raider.getPatrolTarget());
                    }
                    level.addFreshEntity(illusioner);
                }
            }
        }
    }

    private static boolean estaTocandoMagmaDirectamente(ServerLevel level, AABB playerBox) {
        // Comprobación axial por caras (X, Y, Z por separado) encogiendo ligeramente los otros 2 ejes
        // para evitar falsos positivos diagonales cuando el bloque de magma está tapado por bloques adyacentes
        AABB[] axialBoxes = new AABB[] {
                playerBox.inflate(0.03, -0.02, -0.02),
                playerBox.inflate(-0.02, 0.03, -0.02),
                playerBox.inflate(-0.02, -0.02, 0.03)
        };
        for (AABB box : axialBoxes) {
            for (BlockPos bp : BlockPos.betweenClosed(
                    (int) Math.floor(box.minX), (int) Math.floor(box.minY), (int) Math.floor(box.minZ),
                    (int) Math.floor(box.maxX), (int) Math.floor(box.maxY), (int) Math.floor(box.maxZ))) {
                if (level.getBlockState(bp).is(Blocks.MAGMA_BLOCK)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void equiparTridenteArtesMaritimos(Drowned drowned) {
        if (drowned.entityTags().contains("artes_maritimos_equipado")) {
            return;
        }
        configurarPykete(drowned);
    }

    public static void configurarPykete(Drowned drowned) {
        drowned.addTag("artes_maritimos_equipado");
        drowned.addTag("pykete_arponero");
        if (!drowned.getMainHandItem().is(Items.TRIDENT)) {
            drowned.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
            drowned.setDropChance(EquipmentSlot.MAINHAND, 0.05f);
        }
        // Protección solar cuando está en tierra para evitar que muera quemado
        ItemStack helmet = drowned.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.isEmpty()) {
            drowned.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.TURTLE_HELMET));
            drowned.setDropChance(EquipmentSlot.HEAD, 0.0f);
        }
    }

    public static void procesarPykete(Drowned drowned, ServerLevel level, long currentTick) {
        if (!drowned.isAlive()) return;

        // Búsqueda de objetivo: jugador de supervivencia más cercano en 24 bloques
        LivingEntity target = drowned.getTarget();
        if (target == null || !target.isAlive() || (target instanceof Player p && (p.isCreative() || p.isSpectator()))) {
            target = level.getNearestPlayer(
                    drowned.getX(), drowned.getY(), drowned.getZ(),
                    24.0,
                    e -> e instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator()
            );
            if (target != null) {
                drowned.setTarget(target);
            }
        }

        if (target != null && target.isAlive()) {
            drowned.getLookControl().setLookAt(target, 35.0f, 35.0f);
            double distSqr = drowned.distanceToSqr(target);

            // Si está entre 3.0 y 24.0 bloques y tiene línea de visión, arrojar tridente periódicamente
            if (distSqr >= 9.0 && distSqr <= 576.0 && drowned.hasLineOfSight(target)) {
                long seed = Math.abs(drowned.getUUID().getMostSignificantBits());
                if ((currentTick + seed) % 45 == 0) {
                    ThrownTrident trident = new ThrownTrident(level, drowned, new ItemStack(Items.TRIDENT));
                    trident.addTag("tridente_pykete");
                    double dx = target.getX() - drowned.getX();
                    double dy = (target.getY() + target.getEyeHeight() * 0.5) - (drowned.getY() + 1.2);
                    double dz = target.getZ() - drowned.getZ();
                    double horizDist = Math.sqrt(dx * dx + dz * dz);
                    trident.shoot(dx, dy + horizDist * 0.12, dz, 1.6f, 1.0f);

                    level.playSound(null, drowned.getX(), drowned.getY(), drowned.getZ(),
                            SoundEvents.DROWNED_SHOOT, SoundSource.HOSTILE, 1.2f, 1.0f / (ThreadLocalRandom.current().nextFloat() * 0.4f + 0.8f));
                    drowned.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, true);
                    level.addFreshEntity(trident);
                }
            }
        }
    }

    public static void aplicarEnergiaPoseica(Drowned drowned, ServerLevel level) {
        if (drowned.entityTags().contains("energia_poseica_aplicada")) return;
        drowned.addTag("energia_poseica_aplicada");

        ItemStack current = drowned.getMainHandItem();
        if (!current.is(Items.TRIDENT)) {
            if (!CursosActivos.isArtesMaritimos() && !CursosActivos.isMonturaAcuatica()
                    && ThreadLocalRandom.current().nextFloat() >= 0.35f) {
                return;
            }
            current = new ItemStack(Items.TRIDENT);
        }

        final ItemStack trident = current.copy();
        level.registryAccess().lookup(Registries.ENCHANTMENT).ifPresent(reg -> {
            reg.get(Enchantments.IMPALING).ifPresent(h -> trident.enchant(h, 5));
            reg.get(Enchantments.CHANNELING).ifPresent(h -> trident.enchant(h, 1));
        });
        drowned.setItemSlot(EquipmentSlot.MAINHAND, trident);
        drowned.setDropChance(EquipmentSlot.MAINHAND, 0.05f);
    }

    public static void aplicarMonturaAcuatica(Drowned drowned, ServerLevel level) {
        if (drowned.entityTags().contains("montura_acuatica_evaluada")) return;
        drowned.addTag("montura_acuatica_evaluada");

        if (drowned.isPassenger() || drowned.isBaby()) return;

        // 10% de probabilidad de aparecer montado en un Nautilo o Delfín
        if (ThreadLocalRandom.current().nextFloat() < 0.10f) {
            boolean usarDelfin = ThreadLocalRandom.current().nextBoolean();
            EntityType<?> mountType = usarDelfin ? getDolphinType() : getZombieNautilusType();
            if (mountType == null) {
                mountType = getZombieNautilusType();
            }
            if (mountType != null) {
                Entity created = mountType.create(level, EntitySpawnReason.JOCKEY);
                if (created instanceof Mob mount) {
                    mount.snapTo(drowned.getX(), drowned.getY(), drowned.getZ(), drowned.getYRot(), 0.0f);
                    mount.finalizeSpawn(level, level.getCurrentDifficultyAt(drowned.blockPosition()), EntitySpawnReason.JOCKEY, null);
                    level.addFreshEntity(mount);
                    drowned.startRiding(mount, true, true);
                    level.getChunkSource().sendToTrackingPlayers(mount, new ClientboundSetPassengersPacket(mount));
                }
            }
        }
    }

    private static void infestarParedesMineshaft(ServerLevel level, BlockPos center) {
        boolean enMineshaft = false;
        for (int dx = -8; dx <= 8 && !enMineshaft; dx += 2) {
            for (int dy = -3; dy <= 3 && !enMineshaft; dy += 2) {
                for (int dz = -8; dz <= 8 && !enMineshaft; dz += 2) {
                    BlockPos p = center.offset(dx, dy, dz);
                    if (!level.isLoaded(p)) continue;
                    BlockState s = level.getBlockState(p);
                    if (s.is(Blocks.OAK_PLANKS) || s.is(Blocks.DARK_OAK_PLANKS)
                            || s.is(Blocks.OAK_FENCE) || s.is(Blocks.DARK_OAK_FENCE)
                            || s.is(Blocks.RAIL) || s.is(Blocks.COBWEB)) {
                        enMineshaft = true;
                    }
                }
            }
        }
        if (!enMineshaft) return;

        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int i = 0; i < 12; i++) {
            int dx = rng.nextInt(-10, 11);
            int dy = rng.nextInt(-3, 4);
            int dz = rng.nextInt(-10, 11);
            BlockPos targetPos = center.offset(dx, dy, dz);
            if (!level.isLoaded(targetPos)) continue;

            BlockState state = level.getBlockState(targetPos);
            boolean expuestaEnPared = false;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockState adj = level.getBlockState(targetPos.relative(dir));
                if (adj.isAir() || adj.is(Blocks.COBWEB) || adj.is(Blocks.OAK_FENCE) || adj.is(Blocks.DARK_OAK_FENCE)) {
                    expuestaEnPared = true;
                    break;
                }
            }
            if (!expuestaEnPared) continue;

            if (state.is(Blocks.STONE)) {
                level.setBlock(targetPos, Blocks.INFESTED_STONE.defaultBlockState(), Block.UPDATE_CLIENTS);
            } else if (state.is(Blocks.DEEPSLATE)) {
                level.setBlock(targetPos, Blocks.INFESTED_DEEPSLATE.defaultBlockState(), Block.UPDATE_CLIENTS);
            } else if (state.is(Blocks.COBBLESTONE)) {
                level.setBlock(targetPos, Blocks.INFESTED_COBBLESTONE.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    private static boolean tieneFuenteDeCalorCercana(ServerLevel level, BlockPos center, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos p = center.offset(dx, dy, dz);
                    if (!level.isLoaded(p)) continue;
                    BlockState s = level.getBlockState(p);
                    if (s.is(Blocks.TORCH) || s.is(Blocks.WALL_TORCH)
                            || s.is(Blocks.SOUL_TORCH) || s.is(Blocks.SOUL_WALL_TORCH)
                            || s.is(Blocks.LANTERN) || s.is(Blocks.SOUL_LANTERN)
                            || s.is(Blocks.CAMPFIRE) || s.is(Blocks.SOUL_CAMPFIRE)
                            || s.is(Blocks.FIRE) || s.is(Blocks.SOUL_FIRE)
                            || s.is(Blocks.LAVA) || s.is(Blocks.MAGMA_BLOCK)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void intentarSpawnearJineteEclipse(ServerLevel level, ServerPlayer player) {
        var nearbyEclipseMobs = level.getEntities(
                player,
                player.getBoundingBox().inflate(48.0),
                e -> e.isAlive() && (e.entityTags().contains("eclipse_inmune_sol") || e.entityTags().contains("eclipse_bat"))
        );
        if (nearbyEclipseMobs.size() >= 8) return;

        ThreadLocalRandom rng = ThreadLocalRandom.current();
        double angle = rng.nextDouble() * Math.PI * 2.0;
        double dist = 16.0 + rng.nextDouble() * 16.0;
        double sx = player.getX() + Math.cos(angle) * dist;
        double sz = player.getZ() + Math.sin(angle) * dist;
        BlockPos col = BlockPos.containing(sx, player.getY(), sz);
        if (!level.isLoaded(col)) return;

        BlockPos ground = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, col);
        int branch = rng.nextInt(3);

        if (branch == 2) {
            // Creaking con Velocidad II en el suelo durante el Eclipse Solar
            EntityType<?> creakingType = getCreakingType();
            BlockPos floorPos = ground.below();
            if (creakingType != null && level.isLoaded(floorPos)
                    && level.getBlockState(floorPos).isFaceSturdy(level, floorPos, Direction.UP)
                    && level.getBlockState(ground).getCollisionShape(level, ground).isEmpty()) {
                Entity cEnt = creakingType.create(level, EntitySpawnReason.EVENT);
                if (cEnt instanceof Creaking creaking) {
                    creaking.snapTo(ground.getX() + 0.5, ground.getY(), ground.getZ() + 0.5, rng.nextFloat() * 360.0f, 0.0f);
                    creaking.addTag("eclipse_inmune_sol");
                    creaking.addEffect(new MobEffectInstance(MobEffects.SPEED, MobEffectInstance.INFINITE_DURATION, 1));
                    creaking.setTarget(player);
                    level.addFreshEntity(creaking);
                    return;
                }
            }
        }

        double sy = Math.max(player.getY() + 8.0, ground.getY() + 8.0);
        BlockPos airPos = BlockPos.containing(sx, sy, sz);
        if (!level.getBlockState(airPos).isAir()) return;

        if (branch == 0) {
            // Stray con Lanza de Diamante Filo II montando un Phantom
            EntityType<?> phantomType = getPhantomType();
            EntityType<?> strayType = getStrayType();
            if (phantomType != null && strayType != null) {
                Entity pEnt = phantomType.create(level, EntitySpawnReason.EVENT);
                Entity sEnt = strayType.create(level, EntitySpawnReason.JOCKEY);
                if (pEnt instanceof Phantom phantom && sEnt instanceof Stray stray) {
                    phantom.snapTo(sx, sy, sz, rng.nextFloat() * 360.0f, 0.0f);
                    phantom.addTag("eclipse_inmune_sol");
                    phantom.setTarget(player);

                    stray.snapTo(sx, sy, sz, rng.nextFloat() * 360.0f, 0.0f);
                    stray.addTag("eclipse_inmune_sol");

                    ItemStack spear = new ItemStack(Items.DIAMOND_SPEAR);
                    level.registryAccess().lookup(Registries.ENCHANTMENT).ifPresent(reg -> {
                        reg.get(Enchantments.SHARPNESS).ifPresent(h -> spear.enchant(h, 2));
                    });
                    stray.setItemSlot(EquipmentSlot.MAINHAND, spear);
                    stray.setDropChance(EquipmentSlot.MAINHAND, 0.05f);
                    stray.setTarget(player);

                    level.addFreshEntity(phantom);
                    level.addFreshEntity(stray);
                    stray.startRiding(phantom, true, true);
                    level.getChunkSource().sendToTrackingPlayers(phantom, new ClientboundSetPassengersPacket(phantom));
                }
            }
        } else {
            // Bruja montando un Murciélago
            EntityType<?> batType = getBatType();
            EntityType<?> witchType = getWitchType();
            if (batType != null && witchType != null) {
                Entity bEnt = batType.create(level, EntitySpawnReason.EVENT);
                Entity wEnt = witchType.create(level, EntitySpawnReason.JOCKEY);
                if (bEnt instanceof Bat bat && wEnt instanceof Witch witch) {
                    bat.snapTo(sx, sy - 3.0, sz, rng.nextFloat() * 360.0f, 0.0f);
                    bat.setResting(false);
                    bat.addTag("eclipse_bat");
                    AttributeInstance batHp = bat.getAttribute(Attributes.MAX_HEALTH);
                    if (batHp != null) {
                        batHp.setBaseValue(24.0);
                        bat.setHealth(24.0f);
                    }

                    witch.snapTo(sx, sy - 3.0, sz, rng.nextFloat() * 360.0f, 0.0f);
                    witch.setTarget(player);

                    level.addFreshEntity(bat);
                    level.addFreshEntity(witch);
                    witch.startRiding(bat, true, true);
                    level.getChunkSource().sendToTrackingPlayers(bat, new ClientboundSetPassengersPacket(bat));
                }
            }
        }
    }

    public static void evaluarVerdugo(PiglinBrute brute, ServerLevel level) {
        if (!CursosActivos.isElVerdugo()) return;
        if (brute.entityTags().contains("piglin_verdugo_evaluado")) return;
        brute.addTag("piglin_verdugo_evaluado");

        if (ThreadLocalRandom.current().nextFloat() < 0.15f) {
            configurarElVerdugo(brute, level);
        }
    }

    public static void configurarElVerdugo(PiglinBrute brute, ServerLevel level) {
        brute.addTag("piglin_verdugo_evaluado");
        brute.addTag("piglin_verdugo");

        AttributeInstance scaleAttr = brute.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.setBaseValue(1.5);
        }
        AttributeInstance hpAttr = brute.getAttribute(Attributes.MAX_HEALTH);
        if (hpAttr != null) {
            hpAttr.setBaseValue(100.0);
            brute.setHealth(100.0f);
        }

        ItemStack axe = new ItemStack(Items.NETHERITE_AXE);
        level.registryAccess().lookup(Registries.ENCHANTMENT).ifPresent(reg -> {
            reg.get(Enchantments.SHARPNESS).ifPresent(sharpnessHolder -> {
                axe.enchant(sharpnessHolder, 6);
            });
        });
        brute.setItemSlot(EquipmentSlot.MAINHAND, axe);
        brute.setDropChance(EquipmentSlot.MAINHAND, 0.08f);
    }

    public static void evaluarPhantomAlfa(Phantom phantom) {
        if (!CursosActivos.isPhantomAlfa()) return;
        if (phantom.entityTags().contains("phantom_alfa_evaluado")) return;
        configurarPhantomAlfa(phantom);
    }

    public static void configurarPhantomAlfa(Phantom phantom) {
        phantom.addTag("phantom_alfa_evaluado");
        phantom.addTag("phantom_alfa");
        phantom.addTag("eclipse_inmune_sol");

        AttributeInstance scaleAttr = phantom.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.setBaseValue(2.5);
        }
        AttributeInstance hpAttr = phantom.getAttribute(Attributes.MAX_HEALTH);
        if (hpAttr != null) {
            hpAttr.setBaseValue(60.0);
            phantom.setHealth(60.0f);
        }
    }

    public static void ejecutarOndaPhantomAlfa(Phantom phantom, ServerLevel level, long currentTick) {
        if (phantom.getDeltaMovement().y > -0.05) return;
        long lastWave = ULTIMA_ONDA_PHANTOM_ALFA.getOrDefault(phantom.getUUID(), -1000L);
        if (currentTick - lastWave < 100L) return;

        List<Player> targets = level.getEntitiesOfClass(
                Player.class,
                phantom.getBoundingBox().inflate(12.0),
                p -> p.isAlive() && !p.isCreative() && !p.isSpectator()
        );
        if (targets.isEmpty()) return;

        ULTIMA_ONDA_PHANTOM_ALFA.put(phantom.getUUID(), currentTick);
        level.sendParticles(ParticleTypes.SONIC_BOOM, phantom.getX(), phantom.getY(), phantom.getZ(), 1, 0, 0, 0, 0);
        level.playSound(null, phantom.getX(), phantom.getY(), phantom.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.5f, 1.0f);

        for (Player target : targets) {
            target.hurtServer(level, level.damageSources().sonicBoom(phantom), 6.0f);
        }
    }

    public static void prepararEnderGhast(Ghast ghast, ServerLevel level) {
        if (!ghast.entityTags().contains("ender_ghast_aplicado")) {
            ghast.addTag("ender_ghast_aplicado");
            AttributeInstance followRange = ghast.getAttribute(Attributes.FOLLOW_RANGE);
            if (followRange != null && followRange.getBaseValue() < 100.0) {
                followRange.setBaseValue(100.0);
            }
        }
        if (ghast.getTarget() == null || !ghast.getTarget().isAlive()) {
            Player nearest = level.getNearestPlayer(
                    ghast.getX(), ghast.getY(), ghast.getZ(),
                    100.0,
                    e -> e instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator()
            );
            if (nearest != null) {
                ghast.setTarget(nearest);
            }
        }
        if (ghast.getTarget() instanceof Player target && target.isAlive()) {
            // Acercamiento agresivo hacia el jugador
            double dx = target.getX() - ghast.getX();
            double dy = (target.getY() + 6.0) - ghast.getY();
            double dz = target.getZ() - ghast.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > 18.0) {
                Vec3 vel = ghast.getDeltaMovement();
                ghast.setDeltaMovement(
                        vel.x + (dx / dist) * 0.04,
                        vel.y + (dy / dist) * 0.04,
                        vel.z + (dz / dist) * 0.04
                );
            }
        }
    }

    public static boolean tieneConductorEquipado(ServerPlayer player) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            if (!player.getItemBySlot(slot).isEmpty()) {
                return true;
            }
        }
        return esHerramientaConductora(player.getMainHandItem()) || esHerramientaConductora(player.getOffhandItem());
    }

    public static boolean esHerramientaConductora(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        return path.startsWith("iron_") || path.startsWith("golden_") || path.startsWith("gold_")
                || path.startsWith("diamond_") || path.startsWith("netherite_")
                || path.contains("shears") || path.contains("trident") || path.contains("mace")
                || path.contains("shield");
    }

    public static void evaluarSlimePantano(Slime slime, ServerLevel level) {
        // Si es un hijo producto de división de un Slime Gigante, restaurar su escala normal
        if (slime.entityTags().contains("slime_gigante_pantano") && slime.getSize() < 4) {
            slime.removeTag("slime_gigante_pantano");
            AttributeInstance scaleAttr = slime.getAttribute(Attributes.SCALE);
            if (scaleAttr != null) {
                scaleAttr.setBaseValue(1.0);
            }
            return;
        }

        if (!CursosActivos.isAtaqueSlimes()) return;
        if (slime.entityTags().contains("slime_pantano_evaluado")) return;
        slime.addTag("slime_pantano_evaluado");

        var biome = level.getBiome(slime.blockPosition());
        if (!biome.is(Biomes.SWAMP) && !biome.is(Biomes.MANGROVE_SWAMP)) {
            return;
        }

        if (ThreadLocalRandom.current().nextFloat() < 0.05f) {
            configurarSlimeGigante(slime);
        }
    }

    public static void configurarSlimeGigante(Slime slime) {
        slime.addTag("slime_pantano_evaluado");
        slime.addTag("slime_gigante_pantano");
        slime.setSize(4, true);

        AttributeInstance scaleAttr = slime.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.setBaseValue(4.5);
        }
        AttributeInstance healthAttr = slime.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.setBaseValue(100.0);
            slime.setHealth(100.0f);
        }
        AttributeInstance dmgAttr = slime.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmgAttr != null) {
            // Slime grande (tamaño 4) hace 4.0 de daño -> x2.5 = 10.0 de daño
            dmgAttr.setBaseValue(10.0);
        }
    }

    public static void evaluarGigaEndermite(Endermite endermite, ServerLevel level) {
        if (!CursosActivos.isGigaEndermite()) return;
        if (endermite.entityTags().contains("giga_endermite_evaluado")) return;
        endermite.addTag("giga_endermite_evaluado");

        if (!level.dimension().equals(Level.END)) return;

        if (ThreadLocalRandom.current().nextFloat() < 0.05f) {
            configurarGigaEndermite(endermite);
        }
    }

    public static void evaluarSpawnNaturalGigaEndermiteEnEnd(Entity enderman, ServerLevel level) {
        if (!CursosActivos.isGigaEndermite()) return;
        if (enderman.entityTags().contains("giga_endermite_enderman_check")) return;
        enderman.addTag("giga_endermite_enderman_check");

        if (!level.dimension().equals(Level.END)) return;

        if (ThreadLocalRandom.current().nextFloat() < 0.05f) {
            EntityType<?> endermiteType = getEndermiteType();
            if (endermiteType != null) {
                Entity created = endermiteType.create(level, EntitySpawnReason.NATURAL);
                if (created instanceof Endermite endermite) {
                    endermite.setPos(enderman.getX(), enderman.getY(), enderman.getZ());
                    endermite.addTag("giga_endermite_evaluado");
                    configurarGigaEndermite(endermite);
                    level.addFreshEntity(endermite);
                }
            }
        }
    }

    public static void configurarGigaEndermite(Endermite endermite) {
        endermite.addTag("giga_endermite_evaluado");
        endermite.addTag("giga_endermite");

        AttributeInstance scaleAttr = endermite.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.setBaseValue(6.0);
        }
        AttributeInstance healthAttr = endermite.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.setBaseValue(250.0);
            endermite.setHealth(250.0f);
        }
        AttributeInstance dmgAttr = endermite.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmgAttr != null) {
            dmgAttr.setBaseValue(dmgAttr.getBaseValue() * 2.0);
        }
        AttributeInstance kbAttr = endermite.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (kbAttr != null) {
            kbAttr.setBaseValue(1.0);
        }
    }

    public static void ejecutarAtaqueGigaEndermite(Endermite endermite, ServerLevel level, long currentTick) {
        LivingEntity target = endermite.getTarget();
        if (target == null || !target.isAlive() || (target instanceof Player p && (p.isCreative() || p.isSpectator()))) {
            target = level.getNearestPlayer(
                    endermite.getX(), endermite.getY(), endermite.getZ(),
                    28.0,
                    e -> e instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator()
            );
            if (target != null) {
                endermite.setTarget(target);
            }
        }
        if (target == null) return;

        double dx = target.getX() - endermite.getX();
        double dy = target.getY() - endermite.getY();
        double dz = target.getZ() - endermite.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (dist < 2.5 || dist > 20.0) return;

        long phase = currentTick % 80;
        if (phase == 40) {
            // Fase de anuncio del ataque: partículas de energía del vacío y sonido de carga
            level.sendParticles(
                    ParticleTypes.REVERSE_PORTAL,
                    endermite.getX(), endermite.getY() + 1.0, endermite.getZ(),
                    35, 0.8, 0.8, 0.8, 0.15
            );
            level.playSound(
                    null, endermite.getX(), endermite.getY(), endermite.getZ(),
                    SoundEvents.ENDERMAN_SCREAM, SoundSource.HOSTILE, 1.4f, 0.55f
            );
        } else if (phase == 0) {
            // Fase de embestida / salto anunciado hacia el jugador
            double vx = (dx / dist) * 1.35;
            double vy = Math.max(0.42, Math.min(0.75, (dy / dist) * 0.6 + 0.45));
            double vz = (dz / dist) * 1.35;
            endermite.setDeltaMovement(vx, vy, vz);
            level.sendParticles(
                    ParticleTypes.PORTAL,
                    endermite.getX(), endermite.getY() + 0.8, endermite.getZ(),
                    40, 0.6, 0.6, 0.6, 0.25
            );
            level.playSound(
                    null, endermite.getX(), endermite.getY(), endermite.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.5f, 0.7f
            );
        }
    }

    private static boolean esBloqueMineral(BlockState state) {
        return state.is(Blocks.COAL_ORE) || state.is(Blocks.DEEPSLATE_COAL_ORE)
                || state.is(Blocks.IRON_ORE) || state.is(Blocks.DEEPSLATE_IRON_ORE)
                || state.is(Blocks.COPPER_ORE) || state.is(Blocks.DEEPSLATE_COPPER_ORE)
                || state.is(Blocks.GOLD_ORE) || state.is(Blocks.DEEPSLATE_GOLD_ORE) || state.is(Blocks.NETHER_GOLD_ORE)
                || state.is(Blocks.REDSTONE_ORE) || state.is(Blocks.DEEPSLATE_REDSTONE_ORE)
                || state.is(Blocks.EMERALD_ORE) || state.is(Blocks.DEEPSLATE_EMERALD_ORE)
                || state.is(Blocks.LAPIS_ORE) || state.is(Blocks.DEEPSLATE_LAPIS_ORE)
                || state.is(Blocks.DIAMOND_ORE) || state.is(Blocks.DEEPSLATE_DIAMOND_ORE)
                || state.is(Blocks.NETHER_QUARTZ_ORE)
                || state.is(Blocks.ANCIENT_DEBRIS);
    }

    public static void evaluarMegaHoglin(Hoglin hoglin, ServerLevel level) {
        if (!CursosActivos.isMegaHoglin()) return;
        if (hoglin.entityTags().contains("mega_hoglin_evaluado")) return;
        hoglin.addTag("mega_hoglin_evaluado");

        if (!level.dimension().equals(Level.NETHER)) return;

        if (ThreadLocalRandom.current().nextFloat() < 0.05f) {
            configurarMegaHoglin(hoglin);
        }
    }

    public static void configurarMegaHoglin(Hoglin hoglin) {
        hoglin.addTag("mega_hoglin_evaluado");
        hoglin.addTag("mega_hoglin");
        if (hoglin.isBaby()) {
            hoglin.setAge(0);
        }

        AttributeInstance scaleAttr = hoglin.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.setBaseValue(4.5);
        }
        AttributeInstance healthAttr = hoglin.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.setBaseValue(300.0);
            hoglin.setHealth(300.0f);
        }
        AttributeInstance dmgAttr = hoglin.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmgAttr != null) {
            dmgAttr.setBaseValue(dmgAttr.getBaseValue() * 2.0);
        }
        hoglin.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, MobEffectInstance.INFINITE_DURATION, 0));
    }

    public static void romperBloquesMegaHoglin(Hoglin hoglin, ServerLevel lvl) {
        AABB box = hoglin.getBoundingBox().inflate(0.25, 0.0, 0.25);
        int minX = (int) Math.floor(box.minX);
        int maxX = (int) Math.floor(box.maxX);
        int minY = (int) Math.floor(hoglin.getY() + 0.1);
        int maxY = (int) Math.floor(box.maxY);
        int minZ = (int) Math.floor(box.minZ);
        int maxZ = (int) Math.floor(box.maxZ);

        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    mPos.set(x, y, z);
                    BlockState state = lvl.getBlockState(mPos);
                    if (!state.isAir() && !state.is(Blocks.BEDROCK) && !state.is(Blocks.NETHER_PORTAL)
                            && state.getDestroySpeed(lvl, mPos) >= 0.0f) {
                        lvl.destroyBlock(mPos, true, hoglin);
                    }
                }
            }
        }
    }

    public static void reiniciarInsolacion(UUID uuid) {
        TICKS_INSOLACION_DESIERTO.remove(uuid);
    }

    public static void ejecutarFallaDimensional(ServerPlayer player, ServerLevel level) {
        level.sendParticles(ParticleTypes.LAVA, player.getX(), player.getY() + 1.0, player.getZ(), 40, 1.0, 1.0, 1.0, 0.2);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 1.5f, 0.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLAZE_BURN, SoundSource.HOSTILE, 1.5f, 0.8f);

        EntityType<?> blazeType = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "blaze")).orElse(null);
        if (blazeType != null) {
            for (int i = 0; i < 2; i++) {
                double ox = (ThreadLocalRandom.current().nextBoolean() ? 1 : -1) * (1.5 + ThreadLocalRandom.current().nextDouble() * 1.5);
                double oz = (ThreadLocalRandom.current().nextBoolean() ? 1 : -1) * (1.5 + ThreadLocalRandom.current().nextDouble() * 1.5);
                Entity b = blazeType.create(level, EntitySpawnReason.EVENT);
                if (b instanceof Blaze blaze) {
                    blaze.snapTo(player.getX() + ox, player.getY(), player.getZ() + oz, ThreadLocalRandom.current().nextFloat() * 360.0f, 0.0f);
                    blaze.setTarget(player);
                    level.addFreshEntity(blaze);
                }
            }
        }
        player.sendSystemMessage(Component.literal("§4🔥 ¡FALLA DIMENSIONAL! §c¡El portal ha sufrido una sobrecarga y ha atraído Blazes hostiles!"), true);
    }

    public static void evaluarSpawnCreeperCosmico(Entity refEntity, ServerLevel level) {
        if (!CursosActivos.isCreeperCosmico()) return;
        if (refEntity.entityTags().contains("creeper_cosmico_check")) return;
        refEntity.addTag("creeper_cosmico_check");

        if (!level.dimension().equals(Level.END)) return;

        var nearbyCosmicos = level.getEntitiesOfClass(
                Creeper.class,
                refEntity.getBoundingBox().inflate(48.0),
                c -> c.isAlive() && c.entityTags().contains("creeper_cosmico")
        );
        if (nearbyCosmicos.size() >= 2) return;

        if (ThreadLocalRandom.current().nextFloat() < 0.15f) {
            EntityType<?> creeperType = getCreeperType();
            if (creeperType != null) {
                Entity created = creeperType.create(level, EntitySpawnReason.NATURAL);
                if (created instanceof Creeper creeper) {
                    creeper.snapTo(refEntity.getX(), refEntity.getY(), refEntity.getZ(), refEntity.getYRot(), 0.0f);
                    configurarCreeperCosmico(creeper);
                    level.addFreshEntity(creeper);
                }
            }
        }
    }

    public static void configurarCreeperCosmico(Creeper creeper) {
        creeper.addTag("creeper_cosmico");
        creeper.addTag("creeper_antimateria_evaluado");
        AttributeInstance speedAttr = creeper.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(0.05);
        }
        creeper.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, MobEffectInstance.INFINITE_DURATION, 3, false, false));
        if (creeper instanceof CreeperCustomAccessor accessor) {
            accessor.ruleta$setPowered(true);
            // En Creeper#explodeCreeper, cuando isPowered() es true, el radio se multiplica por 2.0f -> 15 * 2 = 30 bloques
            accessor.ruleta$setExplosionRadius(15);
        }
    }

    public static void configurarCreeperEscondido(Creeper creeper) {
        creeper.addTag("creeper_escondido");
        AttributeInstance scaleAttr = creeper.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.setBaseValue(0.35);
        }
        AttributeInstance speedAttr = creeper.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(0.38);
        }
        if (creeper instanceof CreeperCustomAccessor accessor) {
            accessor.ruleta$setExplosionRadius(2);
        }
    }

    public static boolean tieneInventarioLleno(Player player) {
        return player.getInventory().getFreeSlot() == -1;
    }

    public static void colocarTelaranaTemporal(ServerLevel level, BlockPos pos) {
        BlockState current = level.getBlockState(pos);
        if (!current.isAir() && !current.canBeReplaced()) {
            pos = pos.above();
            current = level.getBlockState(pos);
            if (!current.isAir() && !current.canBeReplaced()) {
                return;
            }
        }
        final BlockPos targetPos = pos.immutable();
        level.setBlock(targetPos, Blocks.COBWEB.defaultBlockState(), Block.UPDATE_ALL);
        level.playSound(null, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5,
                SoundEvents.SPIDER_AMBIENT, SoundSource.HOSTILE, 1.0f, 1.4f);
        MinecraftServer srv = level.getServer();
        if (srv != null) {
            programarTarea(srv.getTickCount() + 50, () -> {
                if (level.isLoaded(targetPos) && level.getBlockState(targetPos).is(Blocks.COBWEB)) {
                    level.removeBlock(targetPos, false);
                    level.sendParticles(ParticleTypes.POOF,
                            targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5,
                            8, 0.25, 0.25, 0.25, 0.02);
                }
            });
        }
    }

    public static void dividirMulticreeper(ServerLevel level, Creeper original, Player attacker) {
        EntityType<?> creeperType = getCreeperType();
        if (creeperType == null) return;

        level.sendParticles(ParticleTypes.SNEEZE,
                original.getX(), original.getY() + 0.8, original.getZ(),
                25, 0.4, 0.5, 0.4, 0.05);
        level.playSound(null, original.getX(), original.getY(), original.getZ(),
                SoundEvents.SLIME_SQUISH, SoundSource.HOSTILE, 1.4f, 1.3f);

        for (int i = 0; i < 2; i++) {
            Entity created = creeperType.create(level, EntitySpawnReason.EVENT);
            if (created instanceof Creeper mini) {
                double angle = (i == 0 ? 0.0 : Math.PI) + ThreadLocalRandom.current().nextDouble() * 0.6;
                double ox = Math.cos(angle) * 0.65;
                double oz = Math.sin(angle) * 0.65;
                mini.snapTo(original.getX() + ox, original.getY(), original.getZ() + oz,
                        original.getYRot(), 0.0f);
                configurarMiniCreeper(mini);
                mini.setTarget(attacker);
                mini.ignite();
                mini.setDeltaMovement(ox * 0.35, 0.28, oz * 0.35);
                level.addFreshEntity(mini);
            }
        }
    }

    public static void configurarMiniCreeper(Creeper creeper) {
        creeper.addTag("mini_multicreeper");
        creeper.addTag("creeper_antimateria_evaluado");

        AttributeInstance scaleAttr = creeper.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.setBaseValue(0.55);
        }
        AttributeInstance speedAttr = creeper.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(0.34);
        }
        if (creeper instanceof CreeperCustomAccessor accessor) {
            accessor.ruleta$setMaxSwell(20); // 1 segundo de mecha ultracorta
            accessor.ruleta$setExplosionRadius(2);
        }
    }

    public static void ejecutarFragmentacionHelada(Stray stray, ServerLevel level) {
        BlockPos center = stray.blockPosition();
        level.sendParticles(ParticleTypes.SNOWFLAKE,
                stray.getX(), stray.getY() + 1.0, stray.getZ(),
                80, 2.2, 0.8, 2.2, 0.15);
        level.playSound(null, stray.getX(), stray.getY(), stray.getZ(),
                SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.8f, 0.6f);
        level.playSound(null, stray.getX(), stray.getY(), stray.getZ(),
                SoundEvents.PLAYER_HURT_FREEZE, SoundSource.HOSTILE, 1.5f, 0.8f);

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (dx * dx + dz * dz > 16) continue;
                for (int dy = 2; dy >= -2; dy--) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (!level.isLoaded(pos)) continue;
                    BlockState state = level.getBlockState(pos);
                    BlockState above = level.getBlockState(pos.above());
                    if (!state.isAir() && !state.is(Blocks.BEDROCK)
                            && state.isFaceSturdy(level, pos, Direction.UP)
                            && (above.isAir() || above.canBeReplaced())) {
                        level.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), Block.UPDATE_CLIENTS);
                        break;
                    }
                }
            }
        }

        List<LivingEntity> nearby = level.getEntitiesOfClass(
                LivingEntity.class,
                stray.getBoundingBox().inflate(4.5),
                e -> e != stray && e.isAlive() && !e.isSpectator()
                        && !(e instanceof Stray)
                        && !(e instanceof Player p && p.isCreative())
                        && e.distanceToSqr(stray) <= 20.25
        );
        for (LivingEntity target : nearby) {
            target.setTicksFrozen(Math.max(target.getTicksFrozen(), target.getTicksRequiredToFreeze() + 160));
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 2));
            target.hurtServer(level, level.damageSources().freeze(), 4.0f);
        }
    }

    public static void evaluarCreeperAntimateria(Creeper creeper) {
        if (!CursosActivos.isCreeperAntimateria()) return;
        if (creeper.entityTags().contains("creeper_antimateria_evaluado")) return;
        creeper.addTag("creeper_antimateria_evaluado");

        if (creeper.entityTags().contains("creeper_cosmico")
                || creeper.entityTags().contains("creeper_escondido")
                || creeper.entityTags().contains("mini_multicreeper")
                || creeper.entityTags().contains("creeper_fantasma")
                || creeper.entityTags().contains("creeper_termonuclear")) {
            return;
        }

        // Probabilidad rara (8%) en cualquier bioma
        if (ThreadLocalRandom.current().nextFloat() < 0.08f) {
            configurarCreeperAntimateria(creeper);
        }
    }

    public static void configurarCreeperAntimateria(Creeper creeper) {
        creeper.addTag("creeper_antimateria_evaluado");
        creeper.addTag("creeper_antimateria");
        AttributeInstance scaleAttr = creeper.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            // Escala 1.08 detectada por CreeperRendererMixin para renderizar la textura negra de Antimateria
            scaleAttr.setBaseValue(1.08);
        }
        AttributeInstance healthAttr = creeper.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.setBaseValue(40.0);
            creeper.setHealth(40.0f);
        }
    }

    public static void iniciarVorticeAntimateria(ServerLevel level, double x, double y, double z) {
        MinecraftServer srv = level.getServer();
        if (srv == null) return;
        long tickFin = srv.getTickCount() + 50; // 2.5 segundos (50 ticks) de succión violenta
        VORTICES_ANTIMATERIA.add(new VorticeAntimateria(level, x, y, z, tickFin));
        level.playSound(null, x, y, z, SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 2.0f, 0.5f);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, x, y + 1.0, z, 80, 1.5, 1.5, 1.5, 0.4);
    }

    public static void aplicarBuffsLunaDeSangre(Monster monster) {
        if (monster.entityTags().contains("bloodmoon_buff_evaluado")) return;
        monster.addTag("bloodmoon_buff_evaluado");

        ThreadLocalRandom rng = ThreadLocalRandom.current();
        float roll = rng.nextFloat();
        int cantidadEfectos = (roll < 0.05f) ? 3 : (roll < 0.20f) ? 2 : 1;

        List<MobEffectInstance> pool = new ArrayList<>(5);
        pool.add(new MobEffectInstance(MobEffects.STRENGTH, MobEffectInstance.INFINITE_DURATION, 9));
        pool.add(new MobEffectInstance(MobEffects.SPEED, MobEffectInstance.INFINITE_DURATION, 4));
        pool.add(new MobEffectInstance(MobEffects.INVISIBILITY, MobEffectInstance.INFINITE_DURATION, 0));
        pool.add(new MobEffectInstance(MobEffects.RESISTANCE, MobEffectInstance.INFINITE_DURATION, 2));
        pool.add(new MobEffectInstance(MobEffects.REGENERATION, MobEffectInstance.INFINITE_DURATION, 2));

        for (int i = 0; i < cantidadEfectos && !pool.isEmpty(); i++) {
            int idx = rng.nextInt(pool.size());
            monster.addEffect(pool.remove(idx));
        }
    }

    public static void evaluarSpawnApocalipsisTotal(Zombie zombie, ServerLevel level) {
        if (!CursosActivos.isApocalipsisZombie()) return;
        if (zombie.entityTags().contains("apoc6_spawn_evaluado")) return;
        zombie.addTag("apoc6_spawn_evaluado");

        if (ThreadLocalRandom.current().nextFloat() < 0.15f && !zombie.isPassenger()) {
            var entity = zombie.getType().create(level, EntitySpawnReason.NATURAL);
            if (entity instanceof Zombie rider) {
                rider.setPos(zombie.getX(), zombie.getY(), zombie.getZ());
                rider.addTag("apoc6_spawn_evaluado");
                rider.startRiding(zombie, true, true);
                level.addFreshEntity(rider);
                level.getChunkSource().sendToTrackingPlayers(zombie, new ClientboundSetPassengersPacket(zombie));
            }
        }
    }

    public static void evaluarZombieApocalipsis(Zombie zombie) {
        if (!CursosActivos.isApocalipsisZombie()) return;
        if (zombie.entityTags().contains("apoc_evaluado")) return;
        zombie.addTag("apoc_evaluado");
        if (ThreadLocalRandom.current().nextFloat() < 0.50f) {
            configurarApocalipsisZombie(zombie);
        }
    }

    public static void configurarApocalipsisZombie(Zombie zombie) {
        zombie.addTag("apoc_evaluado");
        zombie.addTag("apoc_mutante");
        if (zombie instanceof WallClimberAccessor climber) {
            climber.ruleta$setWallClimberNavigation();
        }
    }

    public static void equiparEspadaFiloMortal(Zombie zombie, ServerLevel level) {
        if (zombie.entityTags().contains("filo_mortal_equipado")) return;
        zombie.addTag("filo_mortal_equipado");

        ItemStack sword = new ItemStack(Items.IRON_SWORD);
        level.registryAccess().lookup(Registries.ENCHANTMENT).ifPresent(reg -> {
            reg.get(Enchantments.SHARPNESS).ifPresent(sharpnessHolder -> {
                sword.enchant(sharpnessHolder, 3);
            });
        });
        zombie.setItemSlot(EquipmentSlot.MAINHAND, sword);
        zombie.setDropChance(EquipmentSlot.MAINHAND, 0.05f);
    }

    public static void prepararIntrusoCalaverico(WitherSkeleton ws, ServerLevel level) {
        if (ws.entityTags().contains("intruso_calaverico_equipado")) return;
        configurarIntrusoCalaverico(ws, level);
    }

    public static void configurarIntrusoCalaverico(WitherSkeleton ws, ServerLevel level) {
        ws.addTag("intruso_calaverico_equipado");

        ItemStack helmet = new ItemStack(Items.DIAMOND_HELMET);
        ItemStack chest  = new ItemStack(Items.DIAMOND_CHESTPLATE);
        ItemStack legs   = new ItemStack(Items.DIAMOND_LEGGINGS);
        ItemStack boots  = new ItemStack(Items.DIAMOND_BOOTS);
        ItemStack sword  = new ItemStack(Items.DIAMOND_SWORD);

        level.registryAccess().lookup(Registries.ENCHANTMENT).ifPresent(reg -> {
            reg.get(Enchantments.PROTECTION).ifPresent(protHolder -> {
                helmet.enchant(protHolder, 4);
                chest.enchant(protHolder, 4);
                legs.enchant(protHolder, 4);
                boots.enchant(protHolder, 4);
            });
            reg.get(Enchantments.SHARPNESS).ifPresent(sharpHolder -> {
                sword.enchant(sharpHolder, 5);
            });
        });

        ws.setItemSlot(EquipmentSlot.HEAD, helmet);
        ws.setItemSlot(EquipmentSlot.CHEST, chest);
        ws.setItemSlot(EquipmentSlot.LEGS, legs);
        ws.setItemSlot(EquipmentSlot.FEET, boots);
        ws.setItemSlot(EquipmentSlot.MAINHAND, sword);

        ws.setDropChance(EquipmentSlot.HEAD, 0.02f);
        ws.setDropChance(EquipmentSlot.CHEST, 0.02f);
        ws.setDropChance(EquipmentSlot.LEGS, 0.02f);
        ws.setDropChance(EquipmentSlot.FEET, 0.02f);
        ws.setDropChance(EquipmentSlot.MAINHAND, 0.02f);

        ws.addEffect(new MobEffectInstance(MobEffects.SPEED, MobEffectInstance.INFINITE_DURATION, 0));
        ws.addEffect(new MobEffectInstance(MobEffects.STRENGTH, MobEffectInstance.INFINITE_DURATION, 1));
    }

    public static void aplicarSpiderman(Spider spider) {
        if (spider.entityTags().contains("spiderman_aplicado")) return;
        spider.addTag("spiderman_aplicado");

        spider.addEffect(new MobEffectInstance(MobEffects.STRENGTH, MobEffectInstance.INFINITE_DURATION, 1));
        spider.addEffect(new MobEffectInstance(MobEffects.SPEED, MobEffectInstance.INFINITE_DURATION, 1));
        spider.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, MobEffectInstance.INFINITE_DURATION, 0));
        spider.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, MobEffectInstance.INFINITE_DURATION, 1));
        spider.addEffect(new MobEffectInstance(MobEffects.REGENERATION, MobEffectInstance.INFINITE_DURATION, 1));
    }

    private static boolean intentarSpawnearWitherSkeletonNoche(ServerLevel serverLevel, BlockPos centerPos, EntityType<?> wsType) {
        for (int attempt = 0; attempt < 25; attempt++) {
            int dx = ThreadLocalRandom.current().nextInt(-40, 41);
            int dz = ThreadLocalRandom.current().nextInt(-40, 41);
            int distSq = dx * dx + dz * dz;
            if (distSq < 18 * 18 || distSq > 40 * 40) continue;

            int candidateX = centerPos.getX() + dx;
            int candidateZ = centerPos.getZ() + dz;
            BlockPos checkCol = new BlockPos(candidateX, centerPos.getY(), candidateZ);
            if (!serverLevel.isLoaded(checkCol)) continue;

            BlockPos spawnPos = serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, checkCol);
            BlockPos floorPos = spawnPos.below();
            if (!serverLevel.isLoaded(floorPos) || !serverLevel.isLoaded(spawnPos)) continue;

            if (serverLevel.getBrightness(LightLayer.BLOCK, spawnPos) > 0) continue;

            BlockState floorState = serverLevel.getBlockState(floorPos);
            if (!floorState.isFaceSturdy(serverLevel, floorPos, Direction.UP) || !floorState.getFluidState().isEmpty()) continue;

            BlockState spawnState = serverLevel.getBlockState(spawnPos);
            if (!spawnState.getCollisionShape(serverLevel, spawnPos).isEmpty() || !spawnState.getFluidState().isEmpty()) continue;

            double x = spawnPos.getX() + 0.5;
            double y = spawnPos.getY();
            double z = spawnPos.getZ() + 0.5;
            AABB box = new AABB(x - 0.35, y, z - 0.35, x + 0.35, y + 2.4, z + 0.35);

            if (serverLevel.noCollision(box)) {
                var entity = wsType.create(serverLevel, EntitySpawnReason.NATURAL);
                if (entity instanceof WitherSkeleton ws) {
                    ws.setPos(x, y, z);
                    prepararIntrusoCalaverico(ws, serverLevel);
                    serverLevel.addFreshEntity(ws);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean intentarSpawnearWarden(ServerLevel serverLevel, BlockPos centerPos, EntityType<?> wardenType, int maxIntentos, int minDist, int maxDist) {
        for (int attempt = 0; attempt < maxIntentos; attempt++) {
            int dx = ThreadLocalRandom.current().nextInt(-maxDist, maxDist + 1);
            int dz = ThreadLocalRandom.current().nextInt(-maxDist, maxDist + 1);
            if (dx * dx + dz * dz < minDist * minDist) continue;

            int candidateX = centerPos.getX() + dx;
            int candidateZ = centerPos.getZ() + dz;

            for (int dy = -12; dy <= 12; dy++) {
                BlockPos floorPos = new BlockPos(candidateX, centerPos.getY() + dy, candidateZ);
                if (!serverLevel.isLoaded(floorPos)) continue;

                BlockState floorState = serverLevel.getBlockState(floorPos);

                if (!floorState.isFaceSturdy(serverLevel, floorPos, Direction.UP) || !floorState.getFluidState().isEmpty()) continue;

                BlockPos spawnPos = floorPos.above();
                if (!serverLevel.isLoaded(spawnPos)) continue;

                if (!serverLevel.getBiome(spawnPos).is(Biomes.DEEP_DARK)) continue;

                BlockState spawnState = serverLevel.getBlockState(spawnPos);
                if (!spawnState.getCollisionShape(serverLevel, spawnPos).isEmpty() || !spawnState.getFluidState().isEmpty()) continue;

                double x = spawnPos.getX() + 0.5;
                double y = spawnPos.getY();
                double z = spawnPos.getZ() + 0.5;
                AABB box = new AABB(x - 0.45, y, z - 0.45, x + 0.45, y + 2.9, z + 0.45);

                if (serverLevel.noCollision(box)) {
                    var warden = wardenType.create(serverLevel, EntitySpawnReason.NATURAL);
                    if (warden instanceof LivingEntity living) {
                        living.setPos(x, y, z);
                        living.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 600L);
                        serverLevel.addFreshEntity(living);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static int ejecutarActivacionDirecta(CommandSourceStack source, String id) {
        var srv = source.getServer();
        var players = srv.getPlayerList().getPlayers();

        return CatalogoCastigos.activarYEliminarPorId(id).map(entrada -> {
            if (!players.isEmpty()) {
                players.forEach(p -> entrada.castigo().ejecutar((ServerLevel) p.level(), p));
            } else {
                entrada.castigo().ejecutar(srv.overworld(), null);
            }
            RuletaPersistencia.guardar(srv);
            sincronizarLunaSangreClientes(srv);
            source.sendSuccess(() -> Component.literal(
                    "§a[PermaRuleta] Maldición '§e" + entrada.nombre() + "§a' activada y retirada del catálogo de la ruleta."), true);
            return 1;
        }).orElseGet(() -> {
            source.sendFailure(Component.literal(
                    "§c[PermaRuleta] La maldición '" + id + "' ya está activa o ya fue consumida de la ruleta."));
            return 0;
        });
    }

    private static int ejecutarGiroAleatorio(CommandSourceStack source) {
        var srv = source.getServer();
        var players = srv.getPlayerList().getPlayers();

        if (players.isEmpty()) {
            source.sendFailure(Component.literal("No hay jugadores conectados."));
            return 0;
        }

        NivelRuleta nivel = elegirNivel();
        if (nivel == null) {
            source.sendFailure(Component.literal("§c[PermaRuleta] Ya no quedan maldiciones disponibles en la ruleta."));
            return 0;
        }
        ejecutarGiroParaNivel(source, nivel);
        return 1;
    }

    private static int ejecutarGiroNivel(CommandSourceStack source, NivelRuleta nivel) {
        var srv = source.getServer();
        var players = srv.getPlayerList().getPlayers();

        if (players.isEmpty()) {
            source.sendFailure(Component.literal("No hay jugadores conectados."));
            return 0;
        }

        ejecutarGiroParaNivel(source, nivel);
        return 1;
    }

    private static final List<RunnableTask> TAREAS_PROGRAMADAS = new ArrayList<>();
    record RunnableTask(long tickEjecucion, Runnable accion) {}

    public static void programarTarea(long tickEjecucion, Runnable accion) {
        TAREAS_PROGRAMADAS.add(new RunnableTask(tickEjecucion, accion));
    }

    public static float calcularAnguloRuleta(NivelRuleta nivel) {
        float midAngle;
        float maxJitter = 22.0f;
        switch (nivel) {
            case NIVEL_1 -> midAngle = 30.0f;  // [0°, 60°] - Verde
            case NIVEL_2 -> midAngle = 90.0f;  // [60°, 120°] - Amarillo
            case NIVEL_3 -> midAngle = 150.0f; // [120°, 180°] - Naranja
            case NIVEL_4 -> midAngle = 210.0f; // [180°, 240°] - Salmón / Coral
            case NIVEL_5 -> midAngle = 270.0f; // [240°, 300°] - Magenta / Púrpura
            case NIVEL_6 -> midAngle = 330.0f; // [300°, 360°] - Rojo Carmesí
            default      -> midAngle = 30.0f;
        }
        float jitter = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2.0f * maxJitter;
        float targetOnWheel = (midAngle + jitter) % 360.0f;
        if (targetOnWheel < 0) targetOnWheel += 360.0f;

        float stopAngle = (360.0f - targetOnWheel) % 360.0f;
        if (stopAngle < 0) stopAngle += 360.0f;

        int fullSpins = 6 + ThreadLocalRandom.current().nextInt(2);
        return fullSpins * 360.0f + stopAngle;
    }

    private static void ejecutarGiroParaNivel(CommandSourceStack source, NivelRuleta nivel) {
        var srv = source.getServer();
        var players = srv.getPlayerList().getPlayers();

        var optEntrada = CatalogoCastigos.elegirYEliminar(nivel);
        if (optEntrada.isEmpty()) {
            source.sendFailure(Component.literal("§c[PermaRuleta] No quedan castigos disponibles en " + nivel.getNombreVisible()));
            return;
        }

        var entrada = optEntrada.get();
        float targetAngle = calcularAnguloRuleta(nivel);
        int duracionTicks = 100; // 5 segundos de animación

        RuletaAnimacionPayload payload = new RuletaAnimacionPayload(
                nivel.getNombreVisible(),
                entrada.nombre(),
                entrada.explicacion(),
                targetAngle,
                duracionTicks
        );

        for (ServerPlayer p : players) {
            ServerPlayNetworking.send(p, payload);
        }

        long tickFinal = srv.getTickCount() + duracionTicks;
        programarTarea(tickFinal, () -> {
            var currentPlayers = srv.getPlayerList().getPlayers();
            for (ServerPlayer p : currentPlayers) {
                entrada.castigo().ejecutar((ServerLevel) p.level(), p);
            }
            RuletaPersistencia.guardar(srv);
            sincronizarLunaSangreClientes(srv);
        });
    }

    private static boolean tieneArmorCompleta(ServerPlayer player) {
        return !player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
            && !player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()
            && !player.getItemBySlot(EquipmentSlot.LEGS).isEmpty()
            && !player.getItemBySlot(EquipmentSlot.FEET).isEmpty();
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> construirComandoRuletaSummon() {
        var ruletaSummon = Commands.literal("summon");
        for (String mob : MOBS_CUSTOM) {
            ruletaSummon.then(Commands.literal(mob)
                    .executes(ctx -> spawnMobCustom(ctx.getSource(), mob, ctx.getSource().getPosition()))
                    .then(Commands.argument("pos", Vec3Argument.vec3())
                            .executes(ctx -> spawnMobCustom(ctx.getSource(), mob, Vec3Argument.getVec3(ctx, "pos")))));
        }
        return ruletaSummon;
    }

    public static int spawnMobCustom(CommandSourceStack source, String tipo, Vec3 pos) {
        ServerLevel level = source.getLevel();
        Entity entity = null;
        String mobNombre = tipo;

        switch (tipo.toLowerCase()) {
            case "coloso_ruina" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "iron_golem")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof IronGolem golem) {
                    configurarColosoRuina(golem);
                    entity = golem;
                    mobNombre = "§4§l☄ El Coloso de Ruina";
                }
            }
            case "creeper_termonuclear" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "creeper")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Creeper creeper) {
                    configurarCreeperTermonuclear(creeper);
                    entity = creeper;
                    mobNombre = "§4§l☢ Creeper Termonuclear";
                }
            }
            case "creeper_antimateria" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "creeper")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Creeper creeper) {
                    configurarCreeperAntimateria(creeper);
                    entity = creeper;
                    mobNombre = "§8§l✦ Creeper de Antimateria ✦";
                }
            }
            case "creeper_cosmico" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "creeper")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Creeper creeper) {
                    configurarCreeperCosmico(creeper);
                    entity = creeper;
                    mobNombre = "§5§l☄ Creeper Cósmico ☄";
                }
            }
            case "creeper_fantasma" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "creeper")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Creeper creeper) {
                    configurarCreeperFantasma(creeper);
                    entity = creeper;
                    mobNombre = "§b§l👻 Creeper Fantasma";
                }
            }
            case "creeper_avispon" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "creeper")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Creeper creeper) {
                    configurarCreeperAvispon(creeper);
                    entity = creeper;
                    mobNombre = "§e§l🐝 Creeper Avispón";
                }
            }
            case "creeper_escondido" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "creeper")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Creeper creeper) {
                    configurarCreeperEscondido(creeper);
                    entity = creeper;
                    mobNombre = "§2§l🔍 Creeper Mimetizado";
                }
            }
            case "mini_creeper" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "creeper")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Creeper creeper) {
                    configurarMiniCreeper(creeper);
                    entity = creeper;
                    mobNombre = "§a§l💥 Mini Creeper Fragmentado";
                }
            }
            case "el_verdugo" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "piglin_brute")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof PiglinBrute brute) {
                    configurarElVerdugo(brute, level);
                    entity = brute;
                    mobNombre = "§4§l☠ El Verdugo ☠";
                }
            }
            case "mega_hoglin" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "hoglin")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Hoglin hoglin) {
                    configurarMegaHoglin(hoglin);
                    entity = hoglin;
                    mobNombre = "§c§l🐗 Mega Hoglin Devastador";
                }
            }
            case "berserker" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "vindicator")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Vindicator vindicator) {
                    configurarBerserker(vindicator, level);
                    entity = vindicator;
                    mobNombre = "§c§l🪓 Berserker Frenético";
                }
            }
            case "nigromante_criptas" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "stray")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Stray stray) {
                    configurarNigromanteCriptas(stray, level);
                    entity = stray;
                    mobNombre = "§3§l❄ Nigromante de las Criptas ❄";
                }
            }
            case "colosal_fusion" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "magma_cube")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof MagmaCube magmaCube) {
                    configurarColosalFusion(magmaCube);
                    entity = magmaCube;
                    mobNombre = "§6§l🔥 Colosal de Fusión 🔥";
                }
            }
            case "cazador_igneo" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "wither_skeleton")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof WitherSkeleton ws) {
                    configurarCazadorIgneo(ws, level);
                    entity = ws;
                    mobNombre = "§e§l🏹 Cazador Ígneo 🏹";
                }
            }
            case "intruso_calaverico" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "wither_skeleton")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof WitherSkeleton ws) {
                    configurarIntrusoCalaverico(ws, level);
                    entity = ws;
                    mobNombre = "§8§l💀 Intruso Calavérico 💀";
                }
            }
            case "giga_endermite" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "endermite")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Endermite endermite) {
                    configurarGigaEndermite(endermite);
                    entity = endermite;
                    mobNombre = "§5§l👾 Giga Endermite 👾";
                }
            }
            case "phantom_alfa" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "phantom")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Phantom phantom) {
                    configurarPhantomAlfa(phantom);
                    entity = phantom;
                    mobNombre = "§9§l✦ Phantom Alfa ✦";
                }
            }
            case "slime_gigante" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "slime")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Slime slime) {
                    configurarSlimeGigante(slime);
                    entity = slime;
                    mobNombre = "§a§l🧪 Slime de Pantano Gigante";
                }
            }
            case "cucco_salvaje" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "chicken")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Chicken chicken) {
                    configurarCuccoSalvaje(chicken);
                    entity = chicken;
                    mobNombre = "§e§l🐔 Cucco Salvaje 🐔";
                }
            }
            case "apocalipsis_zombie" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "zombie")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Zombie zombie) {
                    configurarApocalipsisZombie(zombie);
                    entity = zombie;
                    mobNombre = "§2§l☣ Caminante del Apocalipsis ☣";
                }
            }
            case "pykete" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "drowned")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Drowned drowned) {
                    configurarPykete(drowned);
                    entity = drowned;
                    mobNombre = "§3§l🔱 Pykete Arponero 🔱";
                }
            }
            case "sombra_parpadeante" -> {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", "enderman")).orElse(null);
                if (type != null && type.create(level, EntitySpawnReason.COMMAND) instanceof Enderman enderman) {
                    configurarSombraParpadeante(enderman);
                    entity = enderman;
                    mobNombre = "§8§l👁 Sombra Parpadeante 👁";
                }
            }
            default -> {
                source.sendFailure(Component.literal("§c[PermaRuleta] Tipo de mob desconocido: " + tipo));
                return 0;
            }
        }

        if (entity != null) {
            entity.snapTo(pos.x, pos.y, pos.z, source.getRotation().y, 0.0f);
            level.addFreshEntity(entity);
            level.sendParticles(ParticleTypes.PORTAL, pos.x, pos.y + 0.5, pos.z, 25, 0.4, 0.5, 0.4, 0.1);
            level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.2f, 1.2f);
            final String nombreFinal = mobNombre;
            source.sendSuccess(() -> Component.literal("§a[PermaRuleta] Se ha invocado a " + nombreFinal + " §aen §e"
                    + String.format(java.util.Locale.ROOT, "%.1f, %.1f, %.1f", pos.x, pos.y, pos.z)), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("§c[PermaRuleta] No se pudo instanciar la entidad para el mob: " + tipo));
            return 0;
        }
    }

    private static EntityType<?> getCreeperType() {
        if (CREEPER_TYPE_CACHE == null) {
            CREEPER_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "creeper"))
                    .orElse(null);
        }
        return CREEPER_TYPE_CACHE;
    }

    private static EntityType<?> getWardenType() {
        if (WARDEN_TYPE_CACHE == null) {
            WARDEN_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "warden"))
                    .orElse(null);
        }
        return WARDEN_TYPE_CACHE;
    }

    private static EntityType<?> getWitherSkeletonType() {
        if (WITHER_SKELETON_TYPE_CACHE == null) {
            WITHER_SKELETON_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "wither_skeleton"))
                    .orElse(null);
        }
        return WITHER_SKELETON_TYPE_CACHE;
    }

    private static EntityType<?> getZombieNautilusType() {
        if (ZOMBIE_NAUTILUS_TYPE_CACHE == null) {
            ZOMBIE_NAUTILUS_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "zombie_nautilus"))
                    .orElse(null);
        }
        return ZOMBIE_NAUTILUS_TYPE_CACHE;
    }

    private static EntityType<?> getDolphinType() {
        if (DOLPHIN_TYPE_CACHE == null) {
            DOLPHIN_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "dolphin"))
                    .orElse(null);
        }
        return DOLPHIN_TYPE_CACHE;
    }

    private static EntityType<?> getEndermiteType() {
        if (ENDERMITE_TYPE_CACHE == null) {
            ENDERMITE_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "endermite"))
                    .orElse(null);
        }
        return ENDERMITE_TYPE_CACHE;
    }

    private static EntityType<?> getEndermanType() {
        if (ENDERMAN_TYPE_CACHE == null) {
            ENDERMAN_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "enderman"))
                    .orElse(null);
        }
        return ENDERMAN_TYPE_CACHE;
    }

    private static EntityType<?> getPhantomType() {
        if (PHANTOM_TYPE_CACHE == null) {
            PHANTOM_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "phantom"))
                    .orElse(null);
        }
        return PHANTOM_TYPE_CACHE;
    }

    private static EntityType<?> getStrayType() {
        if (STRAY_TYPE_CACHE == null) {
            STRAY_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "stray"))
                    .orElse(null);
        }
        return STRAY_TYPE_CACHE;
    }

    private static EntityType<?> getWitchType() {
        if (WITCH_TYPE_CACHE == null) {
            WITCH_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "witch"))
                    .orElse(null);
        }
        return WITCH_TYPE_CACHE;
    }

    private static EntityType<?> getBatType() {
        if (BAT_TYPE_CACHE == null) {
            BAT_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "bat"))
                    .orElse(null);
        }
        return BAT_TYPE_CACHE;
    }

    private static EntityType<?> getBreezeType() {
        if (BREEZE_TYPE_CACHE == null) {
            BREEZE_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "breeze"))
                    .orElse(null);
        }
        return BREEZE_TYPE_CACHE;
    }

    private static EntityType<?> getIllusionerType() {
        if (ILLUSIONER_TYPE_CACHE == null) {
            ILLUSIONER_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "illusioner"))
                    .orElse(null);
        }
        return ILLUSIONER_TYPE_CACHE;
    }

    private static EntityType<?> getCreakingType() {
        if (CREAKING_TYPE_CACHE == null) {
            CREAKING_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "creaking"))
                    .orElse(null);
        }
        return CREAKING_TYPE_CACHE;
    }

    private static EntityType<?> getLightningBoltType() {
        if (LIGHTNING_BOLT_TYPE_CACHE == null) {
            LIGHTNING_BOLT_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "lightning_bolt"))
                    .orElse(null);
        }
        return LIGHTNING_BOLT_TYPE_CACHE;
    }

    private static EntityType<?> getMagmaCubeType() {
        if (MAGMA_CUBE_TYPE_CACHE == null) {
            MAGMA_CUBE_TYPE_CACHE = BuiltInRegistries.ENTITY_TYPE
                    .getOptional(Identifier.fromNamespaceAndPath("minecraft", "magma_cube"))
                    .orElse(null);
        }
        return MAGMA_CUBE_TYPE_CACHE;
    }

    // --- GRITO PRIMAL (Nivel 2) ---
    public static void aplicarGritoPrimal(ServerPlayer sp, ServerLevel level) {
        sp.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 50, 3));
        sp.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 100, 0));
        level.sendParticles(ParticleTypes.SONIC_BOOM, sp.getX(), sp.getY() + 1.2, sp.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        level.sendParticles(ParticleTypes.PORTAL, sp.getX(), sp.getY() + 1.0, sp.getZ(), 35, 0.4, 0.6, 0.4, 0.25);
        sp.sendSystemMessage(Component.literal("§5§l🔊 ¡GRITO PRIMAL! §dEl alarido del Enderman te ha aturdido y dejado sordo por 30 segundos..."), true);

        ServerPlayNetworking.send(sp, new BloodMoonSyncPayload(
                CursosActivos.isBloodMoonActive(),
                CursosActivos.isEclipseActive(),
                CursosActivos.isContaminacion(),
                CursosActivos.isCuevasTenebrosas(),
                CursosActivos.isMaresAcidos(),
                CursosActivos.isGritoPrimal(),
                CursosActivos.isSombraParpadeante(),
                600 // 30 segundos (600 ticks) de sordera total en el cliente
        ));
    }

    // --- COMO PEDRO (Nivel 3) ---
    public static void procesarCreeperComoPedro(Creeper creeper, ServerLevel level, long currentTick) {
        BlockPos pos = creeper.blockPosition();
        BlockPos below = pos.below();
        boolean aguaEnPies = level.getFluidState(pos).is(FluidTags.WATER);
        boolean aguaDebajo = level.getFluidState(below).is(FluidTags.WATER);

        if (aguaEnPies || aguaDebajo || creeper.isInWater()) {
            if (aguaEnPies) {
                creeper.setPos(creeper.getX(), pos.getY() + 0.92, creeper.getZ());
            }
            Vec3 vel = creeper.getDeltaMovement();
            double ny = Math.max(0.0, vel.y);
            double vx = vel.x;
            double vz = vel.z;

            if (creeper.getTarget() != null && creeper.getTarget().isAlive()) {
                double dx = creeper.getTarget().getX() - creeper.getX();
                double dz = creeper.getTarget().getZ() - creeper.getZ();
                double horiz = Math.sqrt(dx * dx + dz * dz);
                if (horiz > 0.2) {
                    // Velocidad multiplicada por 3 sobre el agua
                    double targetSpeed = 0.52;
                    vx = (dx / horiz) * targetSpeed;
                    vz = (dz / horiz) * targetSpeed;
                }
            } else {
                vx *= 1.35;
                vz *= 1.35;
            }

            creeper.setDeltaMovement(vx, ny, vz);
            creeper.setOnGround(true);
            creeper.addEffect(new MobEffectInstance(MobEffects.SPEED, 15, 2, false, false));

            if (currentTick % 3 == 0) {
                level.sendParticles(ParticleTypes.SPLASH,
                        creeper.getX(), creeper.getY() + 0.05, creeper.getZ(),
                        6, 0.25, 0.05, 0.25, 0.08);
            }
        }
    }

    // --- COLOSAL DE FUSIÓN (Nivel 4) ---
    public static void evaluarColosalFusion(MagmaCube magma, ServerLevel level) {
        if (!CursosActivos.isColosalFusion()) return;
        if (magma.entityTags().contains("colosal_fusion_evaluado")) return;
        magma.addTag("colosal_fusion_evaluado");

        if (!level.dimension().equals(Level.NETHER)) return;
        if (magma.entityTags().contains("cria_mortero_colosal")) return;

        if (ThreadLocalRandom.current().nextFloat() < 0.08f) {
            configurarColosalFusion(magma);
        }
    }

    public static void configurarColosalFusion(MagmaCube magma) {
        magma.addTag("colosal_fusion_evaluado");
        magma.addTag("colosal_fusion");
        int colosalSize = ThreadLocalRandom.current().nextBoolean() ? 7 : 8;
        magma.setSize(colosalSize, true);

        AttributeInstance hpAttr = magma.getAttribute(Attributes.MAX_HEALTH);
        if (hpAttr != null) {
            hpAttr.setBaseValue(120.0);
            magma.setHealth(120.0f);
        }
        AttributeInstance dmgAttr = magma.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmgAttr != null) {
            dmgAttr.setBaseValue(14.0);
        }
        AttributeInstance kbAttr = magma.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (kbAttr != null) {
            kbAttr.setBaseValue(0.85);
        }
    }

    public static void procesarSaltoYCraterColosalFusion(MagmaCube magma, ServerLevel level, long currentTick) {
        UUID id = magma.getUUID();
        boolean wasOnGround = COLOSAL_SUELO_PREVIO.getOrDefault(id, true);
        boolean nowOnGround = magma.onGround();
        COLOSAL_SUELO_PREVIO.put(id, nowOnGround);

        // Detectar aterrizaje tras un salto para crear el cráter de magma hirviente (8s)
        if (!wasOnGround && nowOnGround) {
            crearCraterColosalFusion(magma, level);
        }

        // Mega salto periódico cada 60 ticks cuando está en el suelo
        if (nowOnGround && currentTick % 60 == 0) {
            Player nearest = level.getNearestPlayer(
                    magma.getX(), magma.getY(), magma.getZ(),
                    36.0,
                    e -> e instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator()
            );
            double vx = magma.getDeltaMovement().x;
            double vz = magma.getDeltaMovement().z;
            if (nearest != null) {
                double dx = nearest.getX() - magma.getX();
                double dz = nearest.getZ() - magma.getZ();
                double horiz = Math.sqrt(dx * dx + dz * dz);
                if (horiz > 0.05) {
                    vx = (dx / horiz) * 1.05;
                    vz = (dz / horiz) * 1.05;
                }
            }
            magma.setDeltaMovement(vx, 1.55, vz);
            level.getChunkSource().sendToTrackingPlayers(magma, new ClientboundSetEntityMotionPacket(magma));
            level.playSound(null, magma.getX(), magma.getY(), magma.getZ(),
                    SoundEvents.MAGMA_CUBE_JUMP, SoundSource.HOSTILE, 2.2f, 0.5f);
            level.sendParticles(ParticleTypes.LAVA, magma.getX(), magma.getY() + 0.5, magma.getZ(), 25, 1.5, 0.4, 1.5, 0.1);
        }
    }

    public static void crearCraterColosalFusion(MagmaCube magma, ServerLevel level) {
        BlockPos center = magma.blockPosition();
        level.sendParticles(ParticleTypes.LAVA, magma.getX(), magma.getY() + 0.3, magma.getZ(), 45, 2.2, 0.5, 2.2, 0.15);
        level.sendParticles(ParticleTypes.FLAME, magma.getX(), magma.getY() + 0.4, magma.getZ(), 60, 2.5, 0.4, 2.5, 0.08);
        level.playSound(null, magma.getX(), magma.getY(), magma.getZ(),
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.8f, 0.65f);

        Map<BlockPos, BlockState> bloquesOriginales = new HashMap<>();
        int radius = 3;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) continue;
                for (int dy = 1; dy >= -2; dy--) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (!level.isLoaded(pos)) continue;
                    BlockState state = level.getBlockState(pos);
                    BlockPos abovePos = pos.above();
                    BlockState above = level.getBlockState(abovePos);
                    if (!state.isAir() && !state.is(Blocks.BEDROCK) && !state.is(Blocks.MAGMA_BLOCK)
                            && !state.is(Blocks.NETHER_PORTAL) && !state.is(Blocks.END_PORTAL_FRAME)
                            && level.getBlockEntity(pos) == null
                            && state.isFaceSturdy(level, pos, Direction.UP)
                            && (above.isAir() || above.canBeReplaced())) {
                        bloquesOriginales.put(pos.immutable(), state);
                        level.setBlock(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
                        if (above.isAir() && ThreadLocalRandom.current().nextFloat() < 0.40f) {
                            level.setBlock(abovePos, Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
                        }
                        break;
                    }
                }
            }
        }

        MinecraftServer srv = level.getServer();
        if (srv != null && !bloquesOriginales.isEmpty()) {
            // Restaurar los bloques circundantes tras 8 segundos (160 ticks)
            programarTarea(srv.getTickCount() + 160, () -> {
                for (var entry : bloquesOriginales.entrySet()) {
                    BlockPos pos = entry.getKey();
                    if (level.isLoaded(pos) && level.getBlockState(pos).is(Blocks.MAGMA_BLOCK)) {
                        level.setBlock(pos, entry.getValue(), Block.UPDATE_ALL);
                    }
                }
            });
        }
    }

    public static void dispararCriasMorteroColosal(MagmaCube colosal, ServerLevel level) {
        EntityType<?> mcType = getMagmaCubeType();
        if (mcType == null) return;

        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, colosal.getX(), colosal.getY() + 1.0, colosal.getZ(), 2, 0.5, 0.5, 0.5, 0.0);
        level.sendParticles(ParticleTypes.LAVA, colosal.getX(), colosal.getY() + 1.2, colosal.getZ(), 60, 1.5, 1.2, 1.5, 0.25);
        level.playSound(null, colosal.getX(), colosal.getY(), colosal.getZ(),
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.2f, 0.7f);

        int count = 8;
        for (int i = 0; i < count; i++) {
            Entity created = mcType.create(level, EntitySpawnReason.EVENT);
            if (created instanceof MagmaCube cria) {
                double angle = (Math.PI * 2.0 * i) / count + (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.25;
                double speedHoriz = 0.75 + ThreadLocalRandom.current().nextDouble() * 0.35;
                double vy = 0.68 + ThreadLocalRandom.current().nextDouble() * 0.25;
                double vx = Math.cos(angle) * speedHoriz;
                double vz = Math.sin(angle) * speedHoriz;

                cria.snapTo(colosal.getX() + Math.cos(angle) * 0.8, colosal.getY() + 0.8, colosal.getZ() + Math.sin(angle) * 0.8,
                        (float) Math.toDegrees(angle), 0.0f);
                cria.addTag("colosal_fusion_evaluado");
                cria.addTag("cria_mortero_colosal");
                cria.setSize(2, true);
                cria.setDeltaMovement(vx, vy, vz);
                level.addFreshEntity(cria);
            }
        }
    }

    // --- CAZADOR ÍGNEO (Nivel 4) ---
    public static void evaluarCazadorIgneo(WitherSkeleton ws, ServerLevel level) {
        if (!CursosActivos.isCazadorIgneo()) return;
        if (ws.entityTags().contains("cazador_igneo_evaluado")) return;
        ws.addTag("cazador_igneo_evaluado");

        if (!level.dimension().equals(Level.NETHER)) return;

        if (ThreadLocalRandom.current().nextFloat() < 0.15f) {
            configurarCazadorIgneo(ws, level);
        }
    }

    public static void configurarCazadorIgneo(WitherSkeleton ws, ServerLevel level) {
        ws.addTag("cazador_igneo_evaluado");
        ws.addTag("cazador_igneo");
        ItemStack bow = new ItemStack(Items.BOW);
        level.registryAccess().lookup(Registries.ENCHANTMENT).ifPresent(reg -> {
            reg.get(Enchantments.POWER).ifPresent(h -> bow.enchant(h, 2));
            reg.get(Enchantments.FLAME).ifPresent(h -> bow.enchant(h, 1));
        });
        ws.setItemSlot(EquipmentSlot.MAINHAND, bow);
        ws.setDropChance(EquipmentSlot.MAINHAND, 0.05f);
    }

    // --- MEGA HOGLIN ACTUALIZADO (Nivel 5) ---
    public static boolean estaDesestabilizadoHoglin(UUID playerUuid, long currentGameTime) {
        return DESESTABILIZACION_HOGLIN_EXPIRY.getOrDefault(playerUuid, 0L) > currentGameTime;
    }

    public static void ejecutarEmbestidaMegaHoglin(Hoglin hoglin, ServerLevel level, long currentTick) {
        LivingEntity target = hoglin.getTarget();
        if (target == null || !target.isAlive() || (target instanceof Player p && (p.isCreative() || p.isSpectator()))) {
            target = level.getNearestPlayer(
                    hoglin.getX(), hoglin.getY(), hoglin.getZ(),
                    28.0,
                    e -> e instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator()
            );
            if (target != null) {
                hoglin.setTarget(target);
            }
        }
        if (target == null) return;

        double dx = target.getX() - hoglin.getX();
        double dz = target.getZ() - hoglin.getZ();
        double horizDist = Math.sqrt(dx * dx + dz * dz);
        if (horizDist < 1.5 || horizDist > 22.0) return;

        if (currentTick % 80 == 0) {
            // Embestida en línea recta
            double chargeSpeed = 1.65;
            hoglin.setDeltaMovement((dx / horizDist) * chargeSpeed, 0.25, (dz / horizDist) * chargeSpeed);
            level.getChunkSource().sendToTrackingPlayers(hoglin, new ClientboundSetEntityMotionPacket(hoglin));
            level.playSound(null, hoglin.getX(), hoglin.getY(), hoglin.getZ(),
                    SoundEvents.HOGLIN_ANGRY, SoundSource.HOSTILE, 2.2f, 0.55f);
            level.sendParticles(ParticleTypes.FLAME, hoglin.getX(), hoglin.getY() + 1.2, hoglin.getZ(), 35, 1.2, 0.8, 1.2, 0.1);

            // Onda expansiva tras la embestida (15 ticks después) que desestabiliza e impide saltar o colocar bloques por 3s (60 ticks)
            MinecraftServer srv = level.getServer();
            if (srv != null) {
                programarTarea(srv.getTickCount() + 15, () -> {
                    if (!hoglin.isAlive()) return;
                    level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, hoglin.getX(), hoglin.getY() + 0.5, hoglin.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
                    level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, hoglin.getX(), hoglin.getY() + 0.3, hoglin.getZ(), 40, 2.8, 0.3, 2.8, 0.05);
                    level.playSound(null, hoglin.getX(), hoglin.getY(), hoglin.getZ(),
                            SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.8f, 0.6f);

                    List<ServerPlayer> hitPlayers = level.getEntitiesOfClass(
                            ServerPlayer.class,
                            hoglin.getBoundingBox().inflate(6.5),
                            p -> p.isAlive() && !p.isCreative() && !p.isSpectator()
                    );
                    long expiry = level.getGameTime() + 60L; // 3 segundos
                    for (ServerPlayer sp : hitPlayers) {
                        DESESTABILIZACION_HOGLIN_EXPIRY.put(sp.getUUID(), expiry);
                        sp.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
                        Vec3 push = sp.position().subtract(hoglin.position()).normalize().scale(0.85);
                        sp.setDeltaMovement(push.x, 0.32, push.z);
                        sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
                        sp.sendSystemMessage(Component.literal("§4🐗 ¡ONDA EXPANSIVA! §cEstás desestabilizado: no puedes saltar ni colocar bloques por 3 segundos."), true);
                    }
                });
            }
        }
    }

    // --- SOMBRA PARPADEANTE (Nivel 5) ---
    public static void teletransportarEndermanAEspalda(Enderman enderman, ServerPlayer player, ServerLevel level) {
        Vec3 look = player.getLookAngle();
        Vec3 horizBack = new Vec3(-look.x, 0.0, -look.z);
        if (horizBack.lengthSqr() < 0.001) {
            horizBack = new Vec3(0.0, 0.0, -1.0);
        } else {
            horizBack = horizBack.normalize();
        }

        double tx = player.getX() + horizBack.x * 1.65;
        double ty = player.getY();
        double tz = player.getZ() + horizBack.z * 1.65;

        level.sendParticles(ParticleTypes.PORTAL, enderman.getX(), enderman.getY() + 1.2, enderman.getZ(), 25, 0.35, 0.8, 0.35, 0.2);
        enderman.teleportTo(tx, ty, tz);
        enderman.setYRot(player.getYRot());
        enderman.setTarget(player);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, tx, ty + 1.2, tz, 30, 0.35, 0.8, 0.35, 0.15);
        level.playSound(null, tx, ty, tz, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.3f, 0.7f);
    }

    public static void procesarSombraParpadeanteJugador(ServerPlayer player, ServerLevel level, long currentTick) {
        List<Enderman> nearby = level.getEntitiesOfClass(
                Enderman.class,
                player.getBoundingBox().inflate(28.0),
                Entity::isAlive
        );
        if (nearby.isEmpty()) return;

        Vec3 playerLook = player.getLookAngle().normalize();
        Vec3 playerEye = player.getEyePosition();

        for (Enderman enderman : nearby) {
            if (!CursosActivos.isSombraParpadeante() && !enderman.entityTags().contains("sombra_parpadeante")) continue;
            double distSq = enderman.distanceToSqr(player);

            // 1. Si el jugador intenta mirar al Enderman, éste se teletransporta instantáneamente a su espalda exacta
            Vec3 toEnderman = new Vec3(
                    enderman.getX() - playerEye.x,
                    (enderman.getY() + enderman.getBbHeight() * 0.65) - playerEye.y,
                    enderman.getZ() - playerEye.z
            );
            double dist = toEnderman.length();
            if (dist > 1.2 && dist <= 28.0) {
                double dot = playerLook.dot(toEnderman.scale(1.0 / dist));
                if (dot > 0.94 && player.hasLineOfSight(enderman)) {
                    long lastBlink = ULTIMO_PARPADEO_SOMBRA.getOrDefault(enderman.getUUID(), -100L);
                    if (currentTick - lastBlink >= 25L) {
                        ULTIMO_PARPADEO_SOMBRA.put(enderman.getUUID(), currentTick);
                        teletransportarEndermanAEspalda(enderman, player, level);
                    }
                }
            }

            // 2. Mientras te ataca en un radio de 12 bloques, si intentas mirar al suelo fuerza tu vista hacia sus ojos
            if (distSq <= 144.0 && (enderman.getTarget() == player || enderman.isCreepy())) {
                if (player.getXRot() > 24.0f) {
                    player.lookAt(EntityAnchorArgument.Anchor.EYES, enderman, EntityAnchorArgument.Anchor.EYES);
                    level.sendParticles(ParticleTypes.WITCH, player.getX(), player.getEyeY(), player.getZ(), 8, 0.25, 0.25, 0.25, 0.05);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ENDERMAN_STARE, SoundSource.HOSTILE, 0.9f, 0.6f);
                    player.sendSystemMessage(Component.literal("§5👁 ¡El pulso psíquico de la Sombra fuerza tu mirada hacia sus ojos!"), true);
                }
            }
        }
    }

    public static void configurarSombraParpadeante(Enderman enderman) {
        enderman.addTag("sombra_parpadeante");
        AttributeInstance speedAttr = enderman.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(0.35);
        }
    }

    // --- BERSERKER (Nivel 5) ---
    public static void prepararBerserker(Vindicator vindicator, ServerLevel level) {
        if (vindicator.entityTags().contains("berserker_equipado")) return;
        configurarBerserker(vindicator, level);
    }

    public static void configurarBerserker(Vindicator vindicator, ServerLevel level) {
        vindicator.addTag("berserker_equipado");

        ItemStack axe = new ItemStack(Items.NETHERITE_AXE);
        ItemStack helmet = new ItemStack(Items.NETHERITE_HELMET);
        level.registryAccess().lookup(Registries.ENCHANTMENT).ifPresent(reg -> {
            reg.get(Enchantments.SHARPNESS).ifPresent(h -> axe.enchant(h, 3));
            reg.get(Enchantments.PROTECTION).ifPresent(h -> helmet.enchant(h, 4));
            reg.get(Enchantments.UNBREAKING).ifPresent(h -> helmet.enchant(h, 3));
        });
        vindicator.setItemSlot(EquipmentSlot.MAINHAND, axe);
        vindicator.setItemSlot(EquipmentSlot.HEAD, helmet);
        vindicator.setDropChance(EquipmentSlot.MAINHAND, 0.03f);
        vindicator.setDropChance(EquipmentSlot.HEAD, 0.03f);
    }

    public static void procesarCarreraFreneticaBerserker(Vindicator vindicator, ServerLevel level) {
        Player nearest = level.getNearestPlayer(
                vindicator.getX(), vindicator.getY(), vindicator.getZ(),
                15.0,
                e -> e instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator()
        );
        if (nearest != null) {
            if (vindicator.getTarget() == null || !vindicator.getTarget().isAlive()) {
                vindicator.setTarget(nearest);
            }
            // Carrera Frenética (+60% de velocidad = Speed III)
            vindicator.addEffect(new MobEffectInstance(MobEffects.SPEED, 30, 2, false, false));
            level.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                    vindicator.getX(), vindicator.getY() + 1.8, vindicator.getZ(),
                    1, 0.2, 0.2, 0.2, 0.0);
        }
    }

    // --- EL NIGROMANTE DE LAS CRIPTAS (Nivel 5) ---
    public static void evaluarNigromanteCriptas(Stray stray, ServerLevel level) {
        if (!CursosActivos.isNigromanteCriptas()) return;
        if (stray.entityTags().contains("nigromante_evaluado")) return;
        stray.addTag("nigromante_evaluado");

        if (stray.entityTags().contains("mini_stray_cripta")) return;

        if (ThreadLocalRandom.current().nextFloat() < 0.25f) {
            configurarNigromanteCriptas(stray, level);
        }
    }

    public static void configurarNigromanteCriptas(Stray stray, ServerLevel level) {
        stray.addTag("nigromante_evaluado");
        stray.addTag("nigromante_criptas");

        AttributeInstance hpAttr = stray.getAttribute(Attributes.MAX_HEALTH);
        if (hpAttr != null) {
            hpAttr.setBaseValue(80.0);
            stray.setHealth(80.0f);
        }
        AttributeInstance scaleAttr = stray.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.setBaseValue(1.25);
        }

        ItemStack tunic = new ItemStack(Items.LEATHER_CHESTPLATE);
        ItemStack hood = new ItemStack(Items.LEATHER_HELMET);
        ItemStack staff = new ItemStack(Items.BLUE_ICE);
        level.registryAccess().lookup(Registries.ENCHANTMENT).ifPresent(reg -> {
            reg.get(Enchantments.PROTECTION).ifPresent(h -> {
                tunic.enchant(h, 4);
                hood.enchant(h, 4);
            });
            reg.get(Enchantments.KNOCKBACK).ifPresent(h -> staff.enchant(h, 2));
        });

        stray.setItemSlot(EquipmentSlot.HEAD, hood);
        stray.setItemSlot(EquipmentSlot.CHEST, tunic);
        stray.setItemSlot(EquipmentSlot.MAINHAND, staff);
        stray.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.PACKED_ICE));
        stray.setDropChance(EquipmentSlot.HEAD, 0.02f);
        stray.setDropChance(EquipmentSlot.CHEST, 0.02f);
        stray.setDropChance(EquipmentSlot.MAINHAND, 0.05f);
        stray.setDropChance(EquipmentSlot.OFFHAND, 0.05f);
    }

    public static void procesarNigromanteCriptas(Stray nigromante, ServerLevel level, long currentTick) {
        Player target = level.getNearestPlayer(
                nigromante.getX(), nigromante.getY(), nigromante.getZ(),
                20.0,
                e -> e instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator()
        );
        if (target == null) return;
        nigromante.setTarget(target);

        // 1. Canaliza un rayo gélido continuo que convierte el suelo bajo los pies del jugador en hielo resbaladizo
        double sx = nigromante.getX();
        double sy = nigromante.getY() + 1.4;
        double sz = nigromante.getZ();
        double tx = target.getX();
        double ty = target.getY() + 0.5;
        double tz = target.getZ();
        double dx = tx - sx;
        double dy = ty - sy;
        double dz = tz - sz;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist <= 18.0 && dist > 0.5 && nigromante.hasLineOfSight(target)) {
            int steps = Math.min(24, Math.max(5, (int) (dist * 1.6)));
            for (int i = 1; i <= steps; i++) {
                double t = (double) i / steps;
                level.sendParticles(
                        ParticleTypes.SNOWFLAKE,
                        sx + dx * t, sy + dy * t, sz + dz * t,
                        1, 0.02, 0.02, 0.02, 0.0
                );
            }
            target.setTicksFrozen(Math.max(target.getTicksFrozen(), 100));

            for (int ox = -1; ox <= 1; ox++) {
                for (int oz = -1; oz <= 1; oz++) {
                    BlockPos floorPos = target.blockPosition().below().offset(ox, 0, oz);
                    if (!level.isLoaded(floorPos)) continue;
                    BlockState floorState = level.getBlockState(floorPos);
                    if (!floorState.isAir() && !floorState.is(Blocks.BEDROCK)
                            && !floorState.is(Blocks.BLUE_ICE) && !floorState.is(Blocks.PACKED_ICE)
                            && level.getBlockEntity(floorPos) == null
                            && floorState.isFaceSturdy(level, floorPos, Direction.UP)) {
                        level.setBlock(floorPos, Blocks.BLUE_ICE.defaultBlockState(), Block.UPDATE_CLIENTS);
                    }
                }
            }
        }

        // 2. Invoca periódicamente mini-strays congelados armados con hachas de piedra (cada 6 segundos = 120 ticks)
        if (currentTick % 120 == 0) {
            List<Stray> minions = level.getEntitiesOfClass(
                    Stray.class,
                    nigromante.getBoundingBox().inflate(16.0),
                    s -> s.isAlive() && s.entityTags().contains("mini_stray_cripta")
            );
            if (minions.size() < 4) {
                EntityType<?> strayType = getStrayType();
                if (strayType != null) {
                    level.playSound(null, nigromante.getX(), nigromante.getY(), nigromante.getZ(),
                            SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE, 1.4f, 0.75f);
                    for (int i = 0; i < 2; i++) {
                        Entity created = strayType.create(level, EntitySpawnReason.MOB_SUMMONED);
                        if (created instanceof Stray mini) {
                            double angle = (i == 0 ? 0.0 : Math.PI) + ThreadLocalRandom.current().nextDouble() * 0.8;
                            double mx = nigromante.getX() + Math.cos(angle) * 1.5;
                            double mz = nigromante.getZ() + Math.sin(angle) * 1.5;
                            mini.snapTo(mx, nigromante.getY(), mz, nigromante.getYRot(), 0.0f);
                            mini.addTag("nigromante_evaluado");
                            mini.addTag("mini_stray_cripta");

                            AttributeInstance scaleAttr = mini.getAttribute(Attributes.SCALE);
                            if (scaleAttr != null) {
                                scaleAttr.setBaseValue(0.55);
                            }
                            AttributeInstance speedAttr = mini.getAttribute(Attributes.MOVEMENT_SPEED);
                            if (speedAttr != null) {
                                speedAttr.setBaseValue(0.32);
                            }
                            mini.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_AXE));
                            mini.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
                            mini.setTarget(target);
                            level.addFreshEntity(mini);
                            level.sendParticles(ParticleTypes.SNOWFLAKE, mx, nigromante.getY() + 0.6, mz, 20, 0.3, 0.4, 0.3, 0.05);
                        }
                    }
                }
            }
        }
    }

    // --- CREEPER FANTASMA (Nivel 5) ---
    public static void evaluarCreeperFantasma(Creeper creeper) {
        if (!CursosActivos.isCreeperFantasma()) return;
        if (creeper.entityTags().contains("creeper_fantasma_evaluado")) return;
        creeper.addTag("creeper_fantasma_evaluado");

        if (creeper.entityTags().contains("creeper_cosmico")
                || creeper.entityTags().contains("creeper_escondido")
                || creeper.entityTags().contains("mini_multicreeper")
                || creeper.entityTags().contains("creeper_antimateria")
                || creeper.entityTags().contains("creeper_termonuclear")) {
            return;
        }

        if (ThreadLocalRandom.current().nextFloat() < 0.20f) {
            configurarCreeperFantasma(creeper);
        }
    }

    public static void configurarCreeperFantasma(Creeper creeper) {
        creeper.addTag("creeper_fantasma_evaluado");
        creeper.addTag("creeper_fantasma");
        AttributeInstance scaleAttr = creeper.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            // Escala 1.03 detectada por CreeperRendererMixin para renderizado espectral translúcido
            scaleAttr.setBaseValue(1.03);
        }
    }

    // --- CREEPER TERMONUCLEAR (Nivel 6) ---
    public static void evaluarCreeperTermonuclear(Creeper creeper) {
        if (!CursosActivos.isCreeperTermonuclear()) return;
        if (creeper.entityTags().contains("creeper_termonuclear_evaluado")) return;
        creeper.addTag("creeper_termonuclear_evaluado");

        if (creeper.entityTags().contains("creeper_cosmico")
                || creeper.entityTags().contains("creeper_escondido")
                || creeper.entityTags().contains("mini_multicreeper")
                || creeper.entityTags().contains("creeper_antimateria")
                || creeper.entityTags().contains("creeper_fantasma")) {
            return;
        }

        if (ThreadLocalRandom.current().nextFloat() < 0.12f) {
            configurarCreeperTermonuclear(creeper);
        }
    }

    public static void configurarCreeperTermonuclear(Creeper creeper) {
        creeper.addTag("creeper_termonuclear_evaluado");
        creeper.addTag("creeper_termonuclear");
        TEMPERATURA_CREEPER_TERMONUCLEAR.put(creeper.getUUID(), 0);

        if (creeper instanceof CreeperCustomAccessor accessor) {
            accessor.ruleta$setMaxSwell(Integer.MAX_VALUE);
        }

        AttributeInstance scaleAttr = creeper.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            // Escala 1.22 detectada por CreeperRendererMixin para textura volcánica
            scaleAttr.setBaseValue(1.22);
        }
        AttributeInstance hpAttr = creeper.getAttribute(Attributes.MAX_HEALTH);
        if (hpAttr != null) {
            hpAttr.setBaseValue(60.0);
            creeper.setHealth(60.0f);
        }
        AttributeInstance armorAttr = creeper.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            armorAttr.setBaseValue(12.0);
        }
        AttributeInstance kbAttr = creeper.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (kbAttr != null) {
            kbAttr.setBaseValue(0.8);
        }
        creeper.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, MobEffectInstance.INFINITE_DURATION, 0, false, false));
    }

    public static void enfriarCreeperTermonuclear(Creeper creeper, ServerLevel level, int reduccion) {
        UUID id = creeper.getUUID();
        int actual = TEMPERATURA_CREEPER_TERMONUCLEAR.getOrDefault(id, 0);
        int nueva = Math.max(0, actual - reduccion);
        TEMPERATURA_CREEPER_TERMONUCLEAR.put(id, nueva);

        level.sendParticles(ParticleTypes.CLOUD, creeper.getX(), creeper.getY() + 1.0, creeper.getZ(), 20, 0.35, 0.5, 0.35, 0.05);
        level.sendParticles(ParticleTypes.SNOWFLAKE, creeper.getX(), creeper.getY() + 1.0, creeper.getZ(), 15, 0.35, 0.5, 0.35, 0.04);
        level.playSound(null, creeper.getX(), creeper.getY(), creeper.getZ(),
                SoundEvents.FIRE_EXTINGUISH, SoundSource.HOSTILE, 1.2f, 1.1f);
    }

    public static void procesarCreeperTermonuclear(Creeper creeper, ServerLevel level, long currentTick) {
        UUID id = creeper.getUUID();
        int temp = TEMPERATURA_CREEPER_TERMONUCLEAR.getOrDefault(id, 0);

        // Enfriamiento por contacto con agua, lluvia o proyectiles fríos cercanos
        if (creeper.isInWaterOrRain()) {
            enfriarCreeperTermonuclear(creeper, level, 12);
            return;
        }
        List<Snowball> snowballs = level.getEntitiesOfClass(Snowball.class, creeper.getBoundingBox().inflate(1.8), Entity::isAlive);
        if (!snowballs.isEmpty()) {
            for (Snowball sb : snowballs) {
                sb.discard();
            }
            enfriarCreeperTermonuclear(creeper, level, 35);
            return;
        }
        List<AbstractThrownPotion> potions = level.getEntitiesOfClass(AbstractThrownPotion.class, creeper.getBoundingBox().inflate(2.2), Entity::isAlive);
        if (!potions.isEmpty()) {
            enfriarCreeperTermonuclear(creeper, level, 45);
            return;
        }

        // Búsqueda de jugador objetivo
        LivingEntity target = creeper.getTarget();
        if (target == null || !target.isAlive() || (target instanceof Player p && (p.isCreative() || p.isSpectator()))) {
            target = level.getNearestPlayer(
                    creeper.getX(), creeper.getY(), creeper.getZ(),
                    24.0,
                    e -> e instanceof Player p && p.isAlive() && !p.isCreative() && !p.isSpectator()
            );
            if (target != null) {
                creeper.setTarget(target);
            }
        }

        // Mecánica de distanciamiento táctico:
        // Cerca (< 7 bloques): la caldera se calienta (+3 cada 5 ticks)
        // Muy cerca (< 3.5 bloques): sobrecalentamiento rápido (+5 cada 5 ticks)
        // Lejos (> 8 bloques): se enfría (-2 cada 5 ticks, o -3 si > 14 bloques)
        if (target != null) {
            double distSqr = creeper.distanceToSqr(target);
            if (distSqr <= 12.25) { // < 3.5 bloques
                temp = Math.min(100, temp + 5);
            } else if (distSqr <= 49.0) { // < 7.0 bloques
                temp = Math.min(100, temp + 3);
            } else if (distSqr > 196.0) { // > 14 bloques
                temp = Math.max(0, temp - 3);
            } else { // 7 a 14 bloques
                temp = Math.max(0, temp - 2);
            }
        } else {
            temp = Math.max(0, temp - 2);
        }
        TEMPERATURA_CREEPER_TERMONUCLEAR.put(id, temp);

        double bellyX = creeper.getX();
        double bellyY = creeper.getY() + 0.65;
        double bellyZ = creeper.getZ();
        double headY = creeper.getY() + 1.6;

        // Efecto visual brillante de carga en el estómago / caldera volcánica
        if (temp > 0 && temp < 35) {
            // Carga baja: brasas tenues en el estómago y humo leve en el cráter de la cabeza
            level.sendParticles(ParticleTypes.SMALL_FLAME, bellyX, bellyY, bellyZ, 2, 0.12, 0.15, 0.12, 0.01);
            if (currentTick % 10 == 0) {
                level.sendParticles(ParticleTypes.SMOKE, bellyX, headY, bellyZ, 1, 0.08, 0.1, 0.08, 0.02);
            }
        } else if (temp >= 35 && temp < 70) {
            // Carga media: núcleo del estómago ardiendo al rojo vivo, gotas de magma y humo denso
            level.sendParticles(ParticleTypes.FLAME, bellyX, bellyY, bellyZ, 5, 0.18, 0.2, 0.18, 0.02);
            level.sendParticles(ParticleTypes.LAVA, bellyX, bellyY, bellyZ, 1, 0.12, 0.15, 0.12, 0.02);
            if (currentTick % 6 == 0) {
                level.sendParticles(ParticleTypes.LARGE_SMOKE, bellyX, headY, bellyZ, 2, 0.1, 0.15, 0.1, 0.03);
                level.playSound(null, bellyX, bellyY, bellyZ, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.HOSTILE, 0.8f, 1.2f);
            }
        } else if (temp >= 70 && temp < 100) {
            // Carga crítica (>70%): sobrecalentamiento volcánico, columna de humo y quemadura en 4 bloques
            level.sendParticles(ParticleTypes.FLAME, bellyX, bellyY, bellyZ, 14, 0.22, 0.28, 0.22, 0.04);
            level.sendParticles(ParticleTypes.LAVA, bellyX, bellyY, bellyZ, 4, 0.2, 0.25, 0.2, 0.03);
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, bellyX, headY, bellyZ, 3, 0.1, 0.2, 0.1, 0.05);

            if (currentTick % 10 == 0) {
                level.playSound(null, creeper.getX(), creeper.getY(), creeper.getZ(),
                        SoundEvents.BLAZE_BURN, SoundSource.HOSTILE, 1.2f, 0.5f);
            }

            List<Player> burnedPlayers = level.getEntitiesOfClass(
                    Player.class,
                    creeper.getBoundingBox().inflate(4.0),
                    p -> p.isAlive() && !p.isCreative() && !p.isSpectator() && p.distanceToSqr(creeper) <= 16.0
            );
            for (Player p : burnedPlayers) {
                p.igniteForTicks(60);
                p.hurtServer(level, level.damageSources().onFire(), 2.0f);
            }
        }

        // Solamente explota al cargarse al 100%: MEGA EXPLOSIÓN VOLCÁNICA
        if (temp >= 100) {
            TEMPERATURA_CREEPER_TERMONUCLEAR.remove(id);
            double cx = creeper.getX();
            double cy = creeper.getY() + 0.5;
            double cz = creeper.getZ();
            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, cx, cy, cz, 5, 2.0, 2.0, 2.0, 0.0);
            level.sendParticles(ParticleTypes.LAVA, cx, cy, cz, 120, 3.0, 2.5, 3.0, 0.35);
            level.playSound(null, cx, cy, cz, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 3.0f, 0.5f);
            level.explode(creeper, cx, cy, cz, 9.0f, true, Level.ExplosionInteraction.MOB);
            creeper.discard();
        }
    }

    // --- CREEPER AVISPÓN (Nivel 3) ---
    public static void evaluarCreeperAvispon(Creeper creeper) {
        if (!CursosActivos.isCreeperAvispon()) return;
        if (creeper.entityTags().contains("creeper_avispon_evaluado")) return;
        creeper.addTag("creeper_avispon_evaluado");

        if (creeper.entityTags().contains("creeper_cosmico")
                || creeper.entityTags().contains("creeper_escondido")
                || creeper.entityTags().contains("mini_multicreeper")
                || creeper.entityTags().contains("creeper_antimateria")
                || creeper.entityTags().contains("creeper_fantasma")
                || creeper.entityTags().contains("creeper_termonuclear")) {
            return;
        }

        // 20% de probabilidad al aparecer en el Overworld
        if (creeper.level().dimension().equals(Level.OVERWORLD) && ThreadLocalRandom.current().nextFloat() < 0.20f) {
            configurarCreeperAvispon(creeper);
        }
    }

    public static void configurarCreeperAvispon(Creeper creeper) {
        creeper.addTag("creeper_avispon_evaluado");
        creeper.addTag("creeper_avispon");

        // Tamaño de una abeja bebé (escala 0.28)
        AttributeInstance scaleAttr = creeper.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.setBaseValue(0.28);
        }
        AttributeInstance speedAttr = creeper.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(0.35);
        }
        AttributeInstance maxHealth = creeper.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(10.0);
        }
        creeper.setHealth(10.0f);
        creeper.setNoGravity(true);
    }

    public static void procesarCreeperAvispon(Creeper creeper, ServerLevel level, long currentTick) {
        if (!creeper.isAlive() || creeper.isRemoved()) {
            detenerSonidoCreeperAvispon(creeper, level);
            return;
        }

        // Mantener vuelo sin gravedad
        if (!creeper.isNoGravity()) {
            creeper.setNoGravity(true);
        }

        // Sonido de zumbido agudo tipo abeja
        if (currentTick % 20 == 0) {
            level.playSound(null, creeper.getX(), creeper.getY(), creeper.getZ(),
                    SoundEvents.BEE_LOOP, SoundSource.HOSTILE, 1.1f, 1.7f);
        }

        // Buscar jugador más cercano en un radio de 20 bloques
        Player target = level.getNearestPlayer(creeper, 20.0);
        if (target != null && target.isAlive() && !target.isCreative() && !target.isSpectator()) {
            double targetX = target.getX();
            double targetY = target.getY() + target.getEyeHeight() * 0.7;
            double targetZ = target.getZ();

            Vec3 diff = new Vec3(targetX - creeper.getX(), targetY - creeper.getY(), targetZ - creeper.getZ());
            double dist = diff.length();

            // Revoloteo sinusoidal
            double bob = Math.sin((currentTick + creeper.getId() * 11) * 0.35) * 0.06;
            Vec3 dir = dist > 0.1 ? diff.normalize() : Vec3.ZERO;
            double flySpeed = 0.22;
            creeper.setDeltaMovement(new Vec3(dir.x * flySpeed, dir.y * flySpeed + bob, dir.z * flySpeed));

            // Mirar hacia el objetivo
            creeper.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());

            // Si está muy cerca, encender mecha
            if (dist <= 2.2) {
                creeper.ignite();
            }
        } else {
            // Vuelo suave de patrulla
            double idleBob = Math.sin((currentTick + creeper.getId() * 13) * 0.25) * 0.04;
            Vec3 currentMotion = creeper.getDeltaMovement();
            creeper.setDeltaMovement(new Vec3(currentMotion.x * 0.8, idleBob, currentMotion.z * 0.8));
        }
    }

    public static void detenerSonidoCreeperAvispon(Entity creeper, Level level) {
        if (level instanceof ServerLevel sl && sl.getServer() != null) {
            sl.getServer().getPlayerList().broadcast(
                    null,
                    creeper.getX(),
                    creeper.getY(),
                    creeper.getZ(),
                    48.0,
                    sl.dimension(),
                    new ClientboundStopSoundPacket(SoundEvents.BEE_LOOP.location(), SoundSource.HOSTILE)
            );
        }
    }

    public static void detonarCreeperAvispon(Creeper creeper, ServerLevel level) {
        detenerSonidoCreeperAvispon(creeper, level);
        double cx = creeper.getX();
        double cy = creeper.getY() + 0.2;
        double cz = creeper.getZ();

        // Efectos visuales y de sonido: Cero rotura de bloques
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, cx, cy, cz, 1, 0.0, 0.0, 0.0, 0.0);
        level.sendParticles(ParticleTypes.POOF, cx, cy, cz, 20, 0.35, 0.35, 0.35, 0.05);
        level.playSound(null, cx, cy, cz, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.5f, 1.4f);

        // Mismo radio que un creeper vanilla (6.0 bloques de distancia de explosión)
        // Inflige 2/3 del daño vanilla en el epicentro (~45.0f * 2/3 = 30.0f) escalando con la distancia
        AABB box = creeper.getBoundingBox().inflate(6.0);
        List<LivingEntity> victimas = level.getEntitiesOfClass(LivingEntity.class, box, e -> e.isAlive() && e != creeper);
        for (LivingEntity victima : victimas) {
            double dist = victima.distanceTo(creeper);
            if (dist <= 6.0) {
                float factor = (float) (1.0 - (dist / 6.0));
                float damage = factor * 30.0f;
                if (damage > 0.5f) {
                    victima.hurtServer(level, level.damageSources().explosion(creeper, creeper), damage);
                    Vec3 empuje = victima.position().subtract(creeper.position()).normalize();
                    victima.push(empuje.x * factor * 0.8, factor * 0.35 + 0.1, empuje.z * factor * 0.8);
                }
            }
        }
    }

    private static void intentarSpawnearBreezeDia(ServerLevel level, ServerPlayer player, EntityType<?> breezeType) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int attempt = 0; attempt < 15; attempt++) {
            double angle = rng.nextDouble() * Math.PI * 2.0;
            double dist = 18.0 + rng.nextDouble() * 18.0;
            int sx = (int) Math.floor(player.getX() + Math.cos(angle) * dist);
            int sz = (int) Math.floor(player.getZ() + Math.sin(angle) * dist);
            BlockPos col = new BlockPos(sx, player.blockPosition().getY(), sz);
            if (!level.isLoaded(col)) continue;

            BlockPos spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, col);
            BlockPos floorPos = spawnPos.below();
            if (!level.isLoaded(floorPos) || !level.isLoaded(spawnPos)) continue;

            BlockState floorState = level.getBlockState(floorPos);
            if (!floorState.isFaceSturdy(level, floorPos, Direction.UP) || !floorState.getFluidState().isEmpty()) continue;

            BlockState spawnState = level.getBlockState(spawnPos);
            if (!spawnState.getCollisionShape(level, spawnPos).isEmpty() || !spawnState.getFluidState().isEmpty()) continue;

            double x = spawnPos.getX() + 0.5;
            double y = spawnPos.getY();
            double z = spawnPos.getZ() + 0.5;
            AABB box = new AABB(x - 0.4, y, z - 0.4, x + 0.4, y + 1.8, z + 0.4);
            if (level.noCollision(box)) {
                Entity entity = breezeType.create(level, EntitySpawnReason.NATURAL);
                if (entity instanceof Breeze breeze) {
                    breeze.snapTo(x, y, z, rng.nextFloat() * 360.0f, 0.0f);
                    breeze.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, player);
                    level.addFreshEntity(breeze);
                    return;
                }
            }
        }
    }

    // --- EL COLOSO DE RUINA (Nivel 6) ---
    private static final Map<Integer, Integer> colosoSlamCooldowns = new HashMap<>();
    private static final Map<Integer, Integer> colosoSlamWindups = new HashMap<>();
    private static final Map<Integer, Integer> colosoAntiTowerCooldowns = new HashMap<>();

    public static void evaluarColosoRuina(IronGolem golem) {
        if (!CursosActivos.isColosoRuina()) return;
        if (golem.entityTags().contains("coloso_ruina_evaluado")) return;
        golem.addTag("coloso_ruina_evaluado");

        // 20% de probabilidad al aparecer en el mundo
        if (ThreadLocalRandom.current().nextFloat() < 0.20f) {
            configurarColosoRuina(golem);
        }
    }

    public static void configurarColosoRuina(IronGolem golem) {
        golem.addTag("coloso_ruina_evaluado");
        golem.addTag("coloso_ruina");

        // Escala x1.45 imponente
        AttributeInstance scaleAttr = golem.getAttribute(Attributes.SCALE);
        if (scaleAttr != null) {
            scaleAttr.setBaseValue(1.45);
        }
        // Vida masiva de 300 HP
        AttributeInstance maxHealth = golem.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(300.0);
        }
        golem.setHealth(300.0f);

        // Inmune al empuje (Knockback Resistance 1.0)
        AttributeInstance kbAttr = golem.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (kbAttr != null) {
            kbAttr.setBaseValue(1.0);
        }

        // Daño aumentado
        AttributeInstance attackAttr = golem.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) {
            attackAttr.setBaseValue(21.0);
        }

        // Velocidad pesada y constante
        AttributeInstance speedAttr = golem.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(0.28);
        }
    }

    public static void procesarColosoRuina(IronGolem golem, ServerLevel level, long currentTick) {
        if (!golem.isAlive()) {
            colosoSlamCooldowns.remove(golem.getId());
            colosoSlamWindups.remove(golem.getId());
            colosoAntiTowerCooldowns.remove(golem.getId());
            return;
        }

        int golemId = golem.getId();
        int slamCooldown = colosoSlamCooldowns.getOrDefault(golemId, 120);
        int slamWindup = colosoSlamWindups.getOrDefault(golemId, 0);
        int antiTowerCooldown = colosoAntiTowerCooldowns.getOrDefault(golemId, 40);

        // Buscar jugador objetivo en un radio de 28 bloques si no tiene o murió
        LivingEntity target = golem.getTarget();
        if (target == null || !target.isAlive() || (target instanceof Player p && (p.isCreative() || p.isSpectator()))) {
            Player nearest = level.getNearestPlayer(golem, 28.0);
            if (nearest != null && nearest.isAlive() && !nearest.isCreative() && !nearest.isSpectator()) {
                golem.setTarget(nearest);
                target = nearest;
            }
        }

        // Partículas sutiles de brasas volcánicas desde sus fisuras dorsales
        if (currentTick % 10 == 0) {
            Vec3 facing = golem.getViewVector(1.0f);
            level.sendParticles(ParticleTypes.FLAME,
                    golem.getX() - facing.x * 0.7, golem.getY() + 1.8, golem.getZ() - facing.z * 0.7,
                    2, 0.2, 0.2, 0.2, 0.02);
            level.sendParticles(ParticleTypes.LAVA,
                    golem.getX() - facing.x * 0.7, golem.getY() + 1.5, golem.getZ() - facing.z * 0.7,
                    1, 0.1, 0.1, 0.1, 0.0);
        }

        // --- FASE 1: WIND-UP Y EJECUCIÓN DEL GOLPE SÍSMICO RADIAL ---
        if (slamWindup > 0) {
            slamWindup--;
            colosoSlamWindups.put(golemId, slamWindup);

            // Inmovilizar traslación mientras canaliza el golpe
            Vec3 mot = golem.getDeltaMovement();
            golem.setDeltaMovement(new Vec3(0, mot.y, 0));

            // Mirar hacia el objetivo
            if (target != null) {
                golem.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
            }

            // Brasas y humo en los pies mientras levanta los brazos
            level.sendParticles(ParticleTypes.LAVA, golem.getX(), golem.getY() + 0.1, golem.getZ(), 3, 0.5, 0.1, 0.5, 0.05);

            if (slamWindup == 0) {
                // ¡MOMENTO DE IMPACTO DEL GOLPE SÍSMICO!
                ejecutarImpactoSismicoColoso(golem, level);
                colosoSlamCooldowns.put(golemId, 180); // 9 segundos de enfriamiento
            }
            return;
        }

        if (slamCooldown > 0) {
            slamCooldown--;
            colosoSlamCooldowns.put(golemId, slamCooldown);
        }

        // Si el objetivo está en rango de combate terrestre (<= 9.0 bloques) y el golpe sísmico está listo
        if (target != null && slamCooldown <= 0) {
            double dist = golem.distanceTo(target);
            if (dist <= 9.0) {
                colosoSlamWindups.put(golemId, 25);
                level.broadcastEntityEvent(golem, (byte) 66); // Desencadena animación de slam en clientes
                level.playSound(null, golem.getX(), golem.getY(), golem.getZ(),
                        SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 1.4f, 0.65f);
                return;
            }
        }

        // --- FASE 2: ANTI-CAMPING / DESTRUCTOR DE TORRES ---
        if (target != null && target.isAlive()) {
            double dx = target.getX() - golem.getX();
            double dz = target.getZ() - golem.getZ();
            double horizontalDistSq = dx * dx + dz * dz;
            double verticalDist = target.getY() - golem.getY();

            // Si el jugador está subido a una torre o pilar alto (>= 2.2 bloques más alto y a menos de 7 bloques)
            if (verticalDist >= 2.2 && horizontalDistSq <= 49.0) {
                if (antiTowerCooldown > 0) {
                    antiTowerCooldown--;
                    colosoAntiTowerCooldowns.put(golemId, antiTowerCooldown);
                } else {
                    colosoAntiTowerCooldowns.put(golemId, 45); // cada 2.25 segundos
                    demolerTorreCampista(golem, target, level);
                }
            }
        }
    }

    private static void ejecutarImpactoSismicoColoso(IronGolem golem, ServerLevel level) {
        double gx = golem.getX();
        double gy = golem.getY();
        double gz = golem.getZ();

        // Sonidos colosales de impacto sísmico
        level.playSound(null, gx, gy, gz, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.5f, 0.55f);
        level.playSound(null, gx, gy, gz, SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 2.0f, 0.5f);

        // Onda expansiva radial visible con humo denso y fragmentos de lava
        for (int deg = 0; deg < 360; deg += 20) {
            double rad = Math.toRadians(deg);
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);
            for (double r = 1.5; r <= 8.5; r += 1.4) {
                double px = gx + cos * r;
                double pz = gz + sin * r;
                level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, px, gy + 0.15, pz, 1, 0.0, 0.06, 0.0, 0.02);
                level.sendParticles(ParticleTypes.LAVA, px, gy + 0.15, pz, 1, 0.08, 0.04, 0.08, 0.04);
            }
        }

        // Dañar entidades en un radio de 8.5 bloques
        AABB box = golem.getBoundingBox().inflate(8.5);
        List<LivingEntity> victimas = level.getEntitiesOfClass(LivingEntity.class, box, e -> e.isAlive() && !(e instanceof IronGolem));
        for (LivingEntity victima : victimas) {
            double dist = victima.distanceTo(golem);
            if (dist <= 8.5) {
                // Si la víctima está tocando el suelo o muy cerca de la superficie
                boolean enSuelo = victima.onGround() || (victima.getY() <= gy + 0.65);
                if (enSuelo) {
                    float factor = (float) (1.0 - (dist / 8.5) * 0.45);
                    float damage = 22.0f * factor;
                    victima.hurtServer(level, level.damageSources().mobAttack(golem), damage);
                    victima.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 70, 3)); // Lentitud IV
                    victima.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 70, 0));

                    Vec3 push = victima.position().subtract(golem.position()).normalize();
                    victima.push(push.x * 0.45, 0.58, push.z * 0.45);
                } else {
                    // ¡Esquivó la onda sísmica saltando en el aire! (feedback sonoro)
                    level.playSound(null, victima.getX(), victima.getY(), victima.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.2f, 1.4f);
                }
            }
        }
    }

    private static void demolerTorreCampista(IronGolem golem, LivingEntity target, ServerLevel level) {
        level.broadcastEntityEvent(golem, (byte) 4); // Animación vanilla de golpe de brazo
        level.playSound(null, golem.getX(), golem.getY(), golem.getZ(),
                SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.HOSTILE, 1.6f, 0.7f);
        level.playSound(null, golem.getX(), golem.getY(), golem.getZ(),
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.2f, 1.6f);

        BlockPos targetPos = target.blockPosition();
        BlockPos basePos = golem.blockPosition();

        int minY = Math.max(level.getMinY(), basePos.getY() - 1);
        int maxY = targetPos.getY() - 1;

        boolean demolido = false;
        for (int y = maxY; y >= minY; y--) {
            BlockPos checkPos = new BlockPos(targetPos.getX(), y, targetPos.getZ());
            BlockState state = level.getBlockState(checkPos);
            if (!state.isAir() && !state.is(BlockTags.DRAGON_IMMUNE) && !state.is(BlockTags.WITHER_IMMUNE)) {
                level.destroyBlock(checkPos, true, golem, 512);
                level.sendParticles(ParticleTypes.LARGE_SMOKE, checkPos.getX() + 0.5, checkPos.getY() + 0.5, checkPos.getZ() + 0.5, 10, 0.3, 0.3, 0.3, 0.05);
                demolido = true;
            }
        }

        if (demolido && target instanceof ServerPlayer sp) {
            sp.sendSystemMessage(Component.literal("§4§l¡El Coloso de Ruina ha demolido tu estructura!"));
        }
    }

    public static float procesarDanoColosoRuina(IronGolem golem, ServerLevel level, DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (attacker != null) {
            Vec3 colosoFacing = golem.getViewVector(1.0f);
            Vec3 dirToAttacker = attacker.position().subtract(golem.position()).normalize();
            double dot = colosoFacing.x * dirToAttacker.x + colosoFacing.z * dirToAttacker.z;

            if (dot > 0.0) {
                // Golpe frontal: armadura de basalto mitiga el 60% del daño
                level.playSound(null, golem.getX(), golem.getY(), golem.getZ(),
                        SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 1.4f, 1.6f);
                level.playSound(null, golem.getX(), golem.getY(), golem.getZ(),
                        SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 1.5f, 0.8f);

                double fx = golem.getX() + colosoFacing.x * 0.8;
                double fy = golem.getY() + 1.8;
                double fz = golem.getZ() + colosoFacing.z * 0.8;
                level.sendParticles(ParticleTypes.CRIT, fx, fy, fz, 12, 0.3, 0.3, 0.3, 0.2);
                level.sendParticles(ParticleTypes.LAVA, fx, fy, fz, 5, 0.2, 0.2, 0.2, 0.1);

                return amount * 0.40f;
            } else {
                // Golpe trasero: impacto en la fisura dorsal de magma expuesta (punto débil)
                level.playSound(null, golem.getX(), golem.getY(), golem.getZ(),
                        SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.HOSTILE, 1.8f, 0.7f);

                double bx = golem.getX() - colosoFacing.x * 0.8;
                double by = golem.getY() + 1.8;
                double bz = golem.getZ() - colosoFacing.z * 0.8;
                level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, bx, by, bz, 8, 0.2, 0.2, 0.2, 0.1);
                level.sendParticles(ParticleTypes.FLAME, bx, by, bz, 15, 0.3, 0.3, 0.3, 0.05);

                return amount;
            }
        }
        return amount;
    }

    private static NivelRuleta elegirNivel() {
        float totalDisponible = 0f;
        NivelRuleta ultimoDisponible = null;
        for (NivelRuleta nivel : NivelRuleta.values()) {
            if (CatalogoCastigos.hayDisponibles(nivel)) {
                totalDisponible += nivel.getProbabilidad();
                ultimoDisponible = nivel;
            }
        }
        if (ultimoDisponible == null || totalDisponible <= 0f) {
            return null;
        }

        float r = ThreadLocalRandom.current().nextFloat() * totalDisponible;
        float acumulado = 0f;
        for (NivelRuleta nivel : NivelRuleta.values()) {
            if (!CatalogoCastigos.hayDisponibles(nivel)) continue;
            acumulado += nivel.getProbabilidad();
            if (r < acumulado) return nivel;
        }
        return ultimoDisponible;
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
