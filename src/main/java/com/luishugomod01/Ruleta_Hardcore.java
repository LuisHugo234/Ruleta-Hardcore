package com.luishugomod01;

import com.luishugomod01.network.BloodMoonSyncPayload;
import com.luishugomod01.network.RuletaAnimacionPayload;
import com.luishugomod01.ruleta.CatalogoCastigos;
import com.luishugomod01.ruleta.CursosActivos;
import com.luishugomod01.ruleta.NivelRuleta;
import com.luishugomod01.ruleta.RuletaPersistencia;
import com.luishugomod01.ruleta.WallClimberAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilus;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ruleta_Hardcore implements ModInitializer {
    public static final String MOD_ID = "ruleta_hardcore";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static EntityType<?> CREEPER_TYPE_CACHE = null;
    private static EntityType<?> WARDEN_TYPE_CACHE = null;
    private static EntityType<?> WITHER_SKELETON_TYPE_CACHE = null;
    private static EntityType<?> ZOMBIE_NAUTILUS_TYPE_CACHE = null;
    private static EntityType<?> ENDERMITE_TYPE_CACHE = null;
    private static EntityType<?> ENDERMAN_TYPE_CACHE = null;

    @Override
    public void onInitialize() {
        LOGGER.info("Ruleta Hardcore inicializada correctamente.");

        PayloadTypeRegistry.clientboundPlay().register(RuletaAnimacionPayload.TYPE, RuletaAnimacionPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(BloodMoonSyncPayload.TYPE, BloodMoonSyncPayload.STREAM_CODEC);

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
                    .then(Commands.literal("defensavillera").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "defensavillera")))
                    .then(Commands.literal("speedpers").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "speedpers")))
                    .then(Commands.literal("filomortal").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "filomortal")))
                    .then(Commands.literal("muerteespinosa").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "muerteespinosa")))
                    .then(Commands.literal("errortotemico").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "errortotemico")))
                    .then(Commands.literal("artesmaritimos").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "artesmaritimos")))
                    .then(Commands.literal("boogiewoogie").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "boogiewoogie")))
                    .then(Commands.literal("danocolateral").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "danocolateral")))
                    .then(Commands.literal("ultrafundido").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "ultrafundido")))
                    .then(Commands.literal("poderenlamano").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "poderenlamano")))
                    .then(Commands.literal("intrusoscalavericos").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "intrusoscalavericos")))
                    .then(Commands.literal("spiderman").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "spiderman")))
                    .then(Commands.literal("enderghast").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "enderghast")))
                    .then(Commands.literal("kaboom").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "kaboom")))
                    .then(Commands.literal("ataqueslimes").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "ataqueslimes")))
                    .then(Commands.literal("gigaendermite").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "gigaendermite")))
                    .then(Commands.literal("megahoglin").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "megahoglin")))
                    .then(Commands.literal("permadeath").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "permadeath")))
                    .then(Commands.literal("apocalipsis6").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "apocalipsis6")))
                    .then(Commands.literal("lunasangre").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "lunasangre")))
                    .then(Commands.literal("monturaacuatica").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "monturaacuatica"))))
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
                .then(Commands.literal("estado").executes(ctx -> {
                    var source = ctx.getSource();
                    if (!CursosActivos.hayMaldicionesActivas() && !CursosActivos.isBloodMoonActive()) {
                        source.sendSuccess(() -> Component.literal("§a[PermaRuleta] No hay ninguna maldición activa en el mundo."), false);
                    } else {
                        source.sendSuccess(() -> Component.literal("§6§l[PermaRuleta] Maldiciones activas:"), false);
                        for (Component c : CursosActivos.obtenerMensajesMaldicionesActivas()) {
                            source.sendSuccess(() -> c, false);
                        }
                        if (CursosActivos.isBloodMoonActive()) {
                            source.sendSuccess(() -> Component.literal("  §4§l☾ ESTADO ACTUAL:§c ¡LUNA DE SANGRE EN CURSO ESTA NOCHE!"), false);
                        }
                    }
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

        // ¡Apocalipsis Minecraft !!!! (Nivel 6): alerta a zombies al picar bloques
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClientSide() && CursosActivos.isApocalipsisMinecraftTotal() && world instanceof ServerLevel serverLevel) {
                double radius = player.isShiftKeyDown() ? 10.0 : 24.0;
                var nearby = serverLevel.getEntitiesOfClass(Zombie.class, new AABB(pos).inflate(radius), Entity::isAlive);
                for (Zombie z : nearby) {
                    if (z.getTarget() == null || !z.getTarget().isAlive()) {
                        z.getNavigation().moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 1.25);
                    }
                }
            }
        });

        // KA-BOOM (Nivel 5): los Shulkers explotan 3 segundos (60 ticks) después de morir
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
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
            ServerPlayNetworking.send(player, new BloodMoonSyncPayload(CursosActivos.isBloodMoonActive()));
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
                if (CursosActivos.isFiloMortal() && !(zombie instanceof Drowned && (CursosActivos.isArtesMaritimos() || CursosActivos.isMonturaAcuatica()))) {
                    equiparEspadaFiloMortal(zombie, level);
                }
                if (CursosActivos.isApocalipsisMinecraftTotal()) {
                    evaluarSpawnApocalipsisTotal(zombie, level);
                }
            }
            if (entity instanceof Drowned drowned) {
                if (CursosActivos.isMonturaAcuatica()) {
                    aplicarMonturaAcuatica(drowned, level);
                } else if (CursosActivos.isArtesMaritimos()) {
                    equiparTridenteArtesMaritimos(drowned);
                }
            }
            if (entity instanceof IronGolem golem && CursosActivos.isDefensaVillera()) {
                aplicarDefensaVillera(golem, level);
            }
            if (entity instanceof PiglinBrute brute && CursosActivos.isPoderEnLaMano()) {
                equiparPoderEnLaMano(brute, level);
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

            // Cuevas Tenebrosas (Nivel 1): Oscuridad por debajo de Y=0, disminuye con farol en mano secundaria
            if (CursosActivos.isCuevasTenebrosas() && currentTick % 20 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isCreative() || player.isSpectator()) continue;
                    if (player.getY() < 0.0) {
                        ItemStack offhand = player.getItemBySlot(EquipmentSlot.OFFHAND);
                        boolean tieneFarol = offhand.is(Items.LANTERN) || offhand.is(Items.SOUL_LANTERN);
                        if (!tieneFarol) {
                            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false, true));
                        } else {
                            var currentDarkness = player.getEffect(MobEffects.DARKNESS);
                            if (currentDarkness != null && currentDarkness.getDuration() > 30) {
                                player.removeEffect(MobEffects.DARKNESS);
                            }
                        }
                    }
                }
            }

            // Defensa Villera (Nivel 1): Gólems agresivos con todos los jugadores
            if (CursosActivos.isDefensaVillera() && currentTick % 20 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof IronGolem golem && golem.isAlive()) {
                            aplicarDefensaVillera(golem, lvl);
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
                            if (zombie instanceof Drowned && (CursosActivos.isArtesMaritimos() || CursosActivos.isMonturaAcuatica())) {
                                continue;
                            }
                            equiparEspadaFiloMortal(zombie, lvl);
                        }
                    }
                }
            }

            // Artes Marítimos (Nivel 3) y Montura Acuática (Nivel 6): Tridentes y Nautilos para Zombies Ahogados
            if ((CursosActivos.isArtesMaritimos() || CursosActivos.isMonturaAcuatica()) && currentTick % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Drowned drowned && drowned.isAlive()) {
                            if (CursosActivos.isMonturaAcuatica()) {
                                aplicarMonturaAcuatica(drowned, lvl);
                            } else if (CursosActivos.isArtesMaritimos()) {
                                equiparTridenteArtesMaritimos(drowned);
                            }
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

            // Mega Hoglin (Nivel 5): mantener Lentitud II activa
            if (CursosActivos.isMegaHoglin() && currentTick % 60 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Hoglin hoglin && hoglin.isAlive() && hoglin.entityTags().contains("mega_hoglin")) {
                            hoglin.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 1));
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

            // Speedpers: velocidad constante para Creepers
            if (CursosActivos.isSpeedpers() && currentTick % 60 == 0) {
                EntityType<?> creeperType = getCreeperType();
                if (creeperType != null) {
                    for (ServerLevel lvl : server.getAllLevels()) {
                        for (Entity entity : lvl.getAllEntities()) {
                            if (entity.getType() == creeperType && entity instanceof LivingEntity living) {
                                living.addEffect(new MobEffectInstance(MobEffects.SPEED, 100, 1));
                            }
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

            // Luna de Sangre (Nivel 6): Ciclo diario de probabilidad (5% al atardecer) y fin al amanecer (tick 23000)
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
            }
        });
    }

    public static void sincronizarLunaSangreClientes(MinecraftServer server) {
        if (server == null) return;
        BloodMoonSyncPayload payload = new BloodMoonSyncPayload(CursosActivos.isBloodMoonActive());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void aplicarDefensaVillera(IronGolem golem, ServerLevel level) {
        if (golem.isPlayerCreated()) {
            golem.setPlayerCreated(false);
        }
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

    public static void equiparTridenteArtesMaritimos(Drowned drowned) {
        if (drowned.entityTags().contains("artes_maritimos_equipado")
                || drowned.entityTags().contains("montura_acuatica_aplicada")) {
            return;
        }
        drowned.addTag("artes_maritimos_equipado");
        if (!drowned.getMainHandItem().is(Items.TRIDENT)) {
            drowned.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
            drowned.setDropChance(EquipmentSlot.MAINHAND, 0.05f);
        }
    }

    public static void aplicarMonturaAcuatica(Drowned drowned, ServerLevel level) {
        if (drowned.entityTags().contains("montura_acuatica_aplicada")) return;
        drowned.addTag("montura_acuatica_aplicada");

        ItemStack trident = new ItemStack(Items.TRIDENT);
        level.registryAccess().lookup(Registries.ENCHANTMENT).ifPresent(reg -> {
            reg.get(Enchantments.IMPALING).ifPresent(impalingHolder -> {
                trident.enchant(impalingHolder, 5);
            });
        });
        drowned.setItemSlot(EquipmentSlot.MAINHAND, trident);
        drowned.setDropChance(EquipmentSlot.MAINHAND, 0.05f);

        if (!drowned.isPassenger() && !drowned.isBaby()) {
            EntityType<?> nautilusType = getZombieNautilusType();
            if (nautilusType != null) {
                Entity created = nautilusType.create(level, EntitySpawnReason.JOCKEY);
                if (created instanceof ZombieNautilus nautilus) {
                    nautilus.snapTo(drowned.getX(), drowned.getY(), drowned.getZ(), drowned.getYRot(), 0.0f);
                    nautilus.finalizeSpawn(level, level.getCurrentDifficultyAt(drowned.blockPosition()), EntitySpawnReason.JOCKEY, null);
                    level.addFreshEntity(nautilus);
                    drowned.startRiding(nautilus, true, true);
                    level.getChunkSource().sendToTrackingPlayers(nautilus, new ClientboundSetPassengersPacket(nautilus));
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
                // Slime grande (tamaño 4) hace 4.0 de daño -> x10 = 40.0 de daño
                dmgAttr.setBaseValue(40.0);
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
            hoglin.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, MobEffectInstance.INFINITE_DURATION, 1));
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
