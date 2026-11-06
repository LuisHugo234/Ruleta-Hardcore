package com.luishugomod01;

import com.luishugomod01.network.AbrirMenuAdminPayload;
import com.luishugomod01.network.BloodMoonSyncPayload;
import com.luishugomod01.network.GuardarConfigAdminPayload;
import com.luishugomod01.network.RuletaAnimacionPayload;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Unit;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cow.AbstractCow;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.ElderGuardian;
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
import net.minecraft.world.entity.raid.Raider;
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

    private static final Map<UUID, Integer> TICKS_EXPOSICION_NIEVE = new HashMap<>();
    private static final Map<UUID, Integer> TICKS_INSOLACION_DESIERTO = new HashMap<>();

    @Override
    public void onInitialize() {
        LOGGER.info("Ruleta Hardcore inicializada correctamente.");

        PayloadTypeRegistry.clientboundPlay().register(RuletaAnimacionPayload.TYPE, RuletaAnimacionPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(BloodMoonSyncPayload.TYPE, BloodMoonSyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(AbrirMenuAdminPayload.TYPE, AbrirMenuAdminPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(VidasSyncPayload.TYPE, VidasSyncPayload.STREAM_CODEC);
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

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
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
                    .then(Commands.literal("poderenlamano").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "poderenlamano")))
                    .then(Commands.literal("contaminacion").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "contaminacion")))
                    .then(Commands.literal("congelacionprofunda").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "congelacionprofunda")))
                    .then(Commands.literal("escopeta").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "escopeta")))
                    .then(Commands.literal("reydeloprofundo").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "reydeloprofundo")))
                    .then(Commands.literal("desiertoabrasador").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "desiertoabrasador")))
                    .then(Commands.literal("intrusoscalavericos").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "intrusoscalavericos")))
                    .then(Commands.literal("spiderman").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "spiderman")))
                    .then(Commands.literal("enderghast").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "enderghast")))
                    .then(Commands.literal("kaboom").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "kaboom")))
                    .then(Commands.literal("ataqueslimes").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "ataqueslimes")))
                    .then(Commands.literal("gigaendermite").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "gigaendermite")))
                    .then(Commands.literal("megahoglin").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "megahoglin")))
                    .then(Commands.literal("creepersescondidos").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "creepersescondidos")))
                    .then(Commands.literal("creepercosmico").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "creepercosmico")))
                    .then(Commands.literal("permadeath").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "permadeath")))
                    .then(Commands.literal("apocalipsis6").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "apocalipsis6")))
                    .then(Commands.literal("lunasangre").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "lunasangre")))
                    .then(Commands.literal("monturaacuatica").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "monturaacuatica")))
                    .then(Commands.literal("eclipsesolar").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "eclipsesolar"))))
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
                    sincronizarLunaSangreClientes(srv);
                    for (ServerLevel lvl : srv.getAllLevels()) {
                        lvl.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, true, srv);
                    }
                    ctx.getSource().sendSuccess(() -> Component.literal("§e[PermaRuleta] Se han desactivado todas las maldiciones y reiniciado el catálogo."), true);
                    return 1;
                }))
            ));

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

        // Muerte Instantánea (Nivel 3): usar botones, trampillas, puertas o puertas de valla de madera mata al instante
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
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
                                creeper.addTag("creeper_escondido");
                                AttributeInstance scaleAttr = creeper.getAttribute(Attributes.SCALE);
                                if (scaleAttr != null) {
                                    scaleAttr.setBaseValue(0.35);
                                }
                                if (creeper instanceof CreeperCustomAccessor accessor) {
                                    accessor.ruleta$setExplosionRadius(2);
                                }
                                creeper.setTarget(player);
                                serverLevel.addFreshEntity(creeper);
                            }
                        }
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
        });

        // Notificación de bienvenida con las maldiciones activas al unirse al servidor
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            ServerPlayNetworking.send(player, new BloodMoonSyncPayload(
                    CursosActivos.isBloodMoonActive(),
                    CursosActivos.isEclipseActive(),
                    CursosActivos.isContaminacion(),
                    CursosActivos.isCuevasTenebrosas()
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
                if (CursosActivos.isArtesMaritimos()) {
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
            if (entity instanceof Vindicator vindicator && CursosActivos.isResistenciaFisica()) {
                aplicarResistenciaFisica(vindicator);
            }
            if (entity instanceof Pillager pillager && CursosActivos.isEscopeta()) {
                equiparEscopetaPillager(pillager, level);
            }
            if (entity instanceof IronGolem golem && CursosActivos.isFallaMecanica()) {
                aplicarFallaMecanica(golem, level);
            }
            if (entity instanceof Illusioner illusioner && CursosActivos.isAdivinaQuien()) {
                prepararIllusioner(illusioner, level);
            }
            if (entity instanceof Raider raider && !(entity instanceof Illusioner) && !(entity instanceof Witch) && CursosActivos.isAdivinaQuien()) {
                evaluarSpawnIllusionerAcompaniante(raider, level);
            }
            if (entity instanceof PiglinBrute brute && CursosActivos.isPoderEnLaMano()) {
                equiparPoderEnLaMano(brute, level);
            }
            if (entity instanceof ElderGuardian elderGuardian && CursosActivos.isReyDeLoProfundo()) {
                aplicarReyDeLoProfundo(elderGuardian);
            }
            if (entity instanceof WitherSkeleton witherSkeleton && CursosActivos.isIntrusosCalavericos()) {
                prepararIntrusoCalaverico(witherSkeleton, level);
            }
            if (entity instanceof Spider spider && CursosActivos.isSpiderman()) {
                aplicarSpiderman(spider);
            }
            if (entity instanceof Ghast ghast && CursosActivos.isEnderGhast()) {
                prepararEnderGhast(ghast, level);
            }
            if (entity instanceof Slime slime && CursosActivos.isAtaqueSlimes()) {
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

            // Invasión Azul (Nivel 1): Los Breeze pueden spawnear de día en todos los biomas del Overworld con 20% de probabilidad
            if (CursosActivos.isInvasionAzul() && currentTick % 80 == 0) {
                EntityType<?> breezeType = getBreezeType();
                if (breezeType != null) {
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                        if (!player.level().dimension().equals(Level.OVERWORLD)) continue;
                        ServerLevel sLvl = (ServerLevel) player.level();
                        if (!sLvl.isBrightOutside() || sLvl.getDifficulty() == Difficulty.PEACEFUL) continue;

                        var nearbyBreezes = sLvl.getEntitiesOfClass(Breeze.class, player.getBoundingBox().inflate(64.0), Entity::isAlive);
                        if (nearbyBreezes.size() >= 3) continue;

                        if (ThreadLocalRandom.current().nextFloat() < 0.20f) {
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

            // Artes Marítimos (Nivel 3), Energía Poseica (Nivel 3) y Montura Acuática (Nivel 6)
            if ((CursosActivos.isArtesMaritimos() || CursosActivos.isEnergiaPoseica() || CursosActivos.isMonturaAcuatica()) && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Drowned drowned && drowned.isAlive()) {
                            if (CursosActivos.isArtesMaritimos()) {
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

            // Desierto Abrasador (Nivel 4): Quedarse en el desierto durante 15s (300 ticks) acumula Insolación (1 corazón cada 2s y partículas de agua)
            if (CursosActivos.isDesiertoAbrasador() && currentTick % 10 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) {
                        TICKS_INSOLACION_DESIERTO.remove(player.getUUID());
                        continue;
                    }
                    ServerLevel sLvl = (ServerLevel) player.level();
                    BlockPos pos = player.blockPosition();
                    boolean enDesierto = sLvl.getBiome(pos).is(Biomes.DESERT);

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

            // Poder en la Mano (Nivel 4): Piglins Brutos con Hacha de Diamante Filo V
            if (CursosActivos.isPoderEnLaMano() && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof PiglinBrute brute && !brute.entityTags().contains("poder_mano_equipado")) {
                            equiparPoderEnLaMano(brute, lvl);
                        }
                    }
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

            // Mega Hoglin (Nivel 5): mantener Lentitud I activa
            if (CursosActivos.isMegaHoglin() && currentTick % 60 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Hoglin hoglin && hoglin.isAlive() && hoglin.entityTags().contains("mega_hoglin")) {
                            hoglin.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 0));
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
        });
    }

    public static void sincronizarLunaSangreClientes(MinecraftServer server) {
        if (server == null) return;
        BloodMoonSyncPayload payload = new BloodMoonSyncPayload(
                CursosActivos.isBloodMoonActive(),
                CursosActivos.isEclipseActive(),
                CursosActivos.isContaminacion(),
                CursosActivos.isCuevasTenebrosas()
        );
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sincronizarVidasJugador(ServerPlayer player) {
        if (player instanceof VidasJugadorAccessor accessor) {
            ServerPlayNetworking.send(player, new VidasSyncPayload(accessor.ruleta$getVidas(), CursosActivos.getVidasIniciales()));
        }
    }

    public static boolean esAdministrador(ServerPlayer player, MinecraftServer server) {
        if (player == null || server == null) return false;
        return player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)
                || server.isSingleplayerOwner(player.nameAndId());
    }

    public static void aplicarCuccoSalvaje(Chicken chicken) {
        if (chicken.entityTags().contains("cucco_salvaje_aplicado")) return;
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
        drowned.addTag("artes_maritimos_equipado");
        if (!drowned.getMainHandItem().is(Items.TRIDENT)) {
            drowned.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
            drowned.setDropChance(EquipmentSlot.MAINHAND, 0.05f);
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

    public static void equiparPoderEnLaMano(PiglinBrute brute, ServerLevel level) {
        if (brute.entityTags().contains("poder_mano_equipado")) return;
        brute.addTag("poder_mano_equipado");

        ItemStack axe = new ItemStack(Items.DIAMOND_AXE);
        level.registryAccess().lookup(Registries.ENCHANTMENT).ifPresent(reg -> {
            reg.get(Enchantments.SHARPNESS).ifPresent(sharpnessHolder -> {
                axe.enchant(sharpnessHolder, 5);
            });
        });
        brute.setItemSlot(EquipmentSlot.MAINHAND, axe);
        brute.setDropChance(EquipmentSlot.MAINHAND, 0.05f);
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

        if (slime.entityTags().contains("slime_pantano_evaluado")) return;
        slime.addTag("slime_pantano_evaluado");

        var biome = level.getBiome(slime.blockPosition());
        if (!biome.is(Biomes.SWAMP) && !biome.is(Biomes.MANGROVE_SWAMP)) {
            return;
        }

        if (ThreadLocalRandom.current().nextFloat() < 0.05f) {
            slime.addTag("slime_gigante_pantano");
            slime.setSize(4, true);

            AttributeInstance scaleAttr = slime.getAttribute(Attributes.SCALE);
            if (scaleAttr != null) {
                scaleAttr.setBaseValue(3.0);
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
    }

    public static void evaluarGigaEndermite(Endermite endermite, ServerLevel level) {
        if (endermite.entityTags().contains("giga_endermite_evaluado")) return;
        endermite.addTag("giga_endermite_evaluado");

        if (!level.dimension().equals(Level.END)) return;

        if (ThreadLocalRandom.current().nextFloat() < 0.05f) {
            convertirEnGigaEndermite(endermite);
        }
    }

    public static void evaluarSpawnNaturalGigaEndermiteEnEnd(Entity enderman, ServerLevel level) {
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
                    convertirEnGigaEndermite(endermite);
                    level.addFreshEntity(endermite);
                }
            }
        }
    }

    private static void convertirEnGigaEndermite(Endermite endermite) {
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

    public static void evaluarMegaHoglin(Hoglin hoglin, ServerLevel level) {
        if (hoglin.entityTags().contains("mega_hoglin_evaluado")) return;
        hoglin.addTag("mega_hoglin_evaluado");

        if (!level.dimension().equals(Level.NETHER)) return;

        if (ThreadLocalRandom.current().nextFloat() < 0.05f) {
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
    }

    public static void evaluarSpawnCreeperCosmico(Entity refEntity, ServerLevel level) {
        if (refEntity.entityTags().contains("creeper_cosmico_check")) return;
        refEntity.addTag("creeper_cosmico_check");

        if (!level.dimension().equals(Level.END)) return;

        if (!level.structureManager().getStructureWithPieceAt(
                refEntity.blockPosition(),
                holder -> holder.is(BuiltinStructures.END_CITY)
        ).isValid()) {
            return;
        }

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
                    creeper.addTag("creeper_cosmico");
                    if (creeper instanceof CreeperCustomAccessor accessor) {
                        accessor.ruleta$setPowered(true);
                        // En Creeper#explodeCreeper, cuando isPowered() es true, el radio se multiplica por 2.0f -> 15 * 2 = 30 bloques
                        accessor.ruleta$setExplosionRadius(15);
                    }
                    level.addFreshEntity(creeper);
                }
            }
        }
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
        if (zombie.entityTags().contains("apoc_evaluado")) return;
        zombie.addTag("apoc_evaluado");
        if (ThreadLocalRandom.current().nextFloat() < 0.50f) {
            zombie.addTag("apoc_mutante");
            if (zombie instanceof WallClimberAccessor climber) {
                climber.ruleta$setWallClimberNavigation();
            }
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
        float maxJitter;
        switch (nivel) {
            case NIVEL_1 -> { midAngle = 63.0f;  maxJitter = 30.0f; }
            case NIVEL_2 -> { midAngle = 171.0f; maxJitter = 22.0f; }
            case NIVEL_3 -> { midAngle = 248.4f; maxJitter = 16.0f; }
            case NIVEL_4 -> { midAngle = 302.4f; maxJitter = 10.0f; }
            case NIVEL_5 -> { midAngle = 336.6f; maxJitter = 5.0f;  }
            case NIVEL_6 -> { midAngle = 354.6f; maxJitter = 2.2f;  }
            default      -> { midAngle = 63.0f;  maxJitter = 0.0f;  }
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
