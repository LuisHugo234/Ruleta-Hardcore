package com.luishugomod01;

import com.luishugomod01.network.RuletaAnimacionPayload;
import com.luishugomod01.ruleta.CatalogoCastigos;
import com.luishugomod01.ruleta.CursosActivos;
import com.luishugomod01.ruleta.NivelRuleta;
import com.luishugomod01.ruleta.RuletaPersistencia;
import com.luishugomod01.ruleta.WallClimberAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.Zombie;
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

import java.util.concurrent.ThreadLocalRandom;

public class Ruleta_Hardcore implements ModInitializer {
    public static final String MOD_ID = "ruleta_hardcore";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static EntityType<?> CREEPER_TYPE_CACHE = null;
    private static EntityType<?> WARDEN_TYPE_CACHE = null;
    private static EntityType<?> WITHER_SKELETON_TYPE_CACHE = null;

    @Override
    public void onInitialize() {
        LOGGER.info("Ruleta Hardcore inicializada correctamente.");

        PayloadTypeRegistry.clientboundPlay().register(RuletaAnimacionPayload.TYPE, RuletaAnimacionPayload.STREAM_CODEC);

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
                    .then(Commands.literal("wardens").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "wardens")))
                    .then(Commands.literal("fuegofatuo").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "fuegofatuo")))
                    .then(Commands.literal("speedpers").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "speedpers")))
                    .then(Commands.literal("creepersperla").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "creepersperla")))
                    .then(Commands.literal("apocalipsis").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "apocalipsis")))
                    .then(Commands.literal("filomortal").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "filomortal")))
                    .then(Commands.literal("errortotemico").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "errortotemico")))
                    .then(Commands.literal("danocolateral").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "danocolateral")))
                    .then(Commands.literal("ultrafundido").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "ultrafundido")))
                    .then(Commands.literal("intrusoscalavericos").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "intrusoscalavericos")))
                    .then(Commands.literal("spiderman").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "spiderman")))
                    .then(Commands.literal("permadeath").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "permadeath")))
                    .then(Commands.literal("apocalipsis6").executes(ctx -> ejecutarActivacionDirecta(ctx.getSource(), "apocalipsis6"))))
                .then(Commands.literal("estado").executes(ctx -> {
                    var source = ctx.getSource();
                    if (!CursosActivos.hayMaldicionesActivas()) {
                        source.sendSuccess(() -> Component.literal("§a[PermaRuleta] No hay ninguna maldición activa en el mundo."), false);
                    } else {
                        source.sendSuccess(() -> Component.literal("§6§l[PermaRuleta] Maldiciones activas:"), false);
                        for (Component c : CursosActivos.obtenerMensajesMaldicionesActivas()) {
                            source.sendSuccess(() -> c, false);
                        }
                    }
                    return 1;
                }))
                .then(Commands.literal("limpiar").executes(ctx -> {
                    var srv = ctx.getSource().getServer();
                    CursosActivos.limpiar();
                    CatalogoCastigos.reiniciar();
                    RuletaPersistencia.limpiar(srv);
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
                var nearby = serverLevel.getEntitiesOfClass(Zombie.class, new net.minecraft.world.phys.AABB(pos).inflate(radius), Entity::isAlive);
                for (Zombie z : nearby) {
                    if (z.getTarget() == null || !z.getTarget().isAlive()) {
                        z.getNavigation().moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 1.25);
                    }
                }
            }
        });

        // Notificación de bienvenida con las maldiciones activas al unirse al servidor
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (CursosActivos.hayMaldicionesActivas()) {
                ServerPlayer player = handler.getPlayer();
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
                if (CursosActivos.isFiloMortal()) {
                    equiparEspadaFiloMortal(zombie, level);
                }
                if (CursosActivos.isApocalipsisMinecraftTotal()) {
                    evaluarSpawnApocalipsisTotal(zombie, level);
                }
            }
            if (entity instanceof WitherSkeleton witherSkeleton && CursosActivos.isIntrusosCalavericos()) {
                prepararIntrusoCalaverico(witherSkeleton, level);
            }
            if (entity instanceof Spider spider && CursosActivos.isSpiderman()) {
                aplicarSpiderman(spider);
            }
        });

        // Bucle de actualización periódica en el servidor
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            long currentTick = server.getTickCount();

            // Tareas programadas (animaciones y eventos diferidos)
            if (!TAREAS_PROGRAMADAS.isEmpty()) {
                TAREAS_PROGRAMADAS.removeIf(task -> {
                    if (currentTick >= task.tickEjecucion()) {
                        try {
                            task.accion().run();
                        } catch (Exception e) {
                            LOGGER.error("Error al ejecutar tarea programada de la ruleta", e);
                        }
                        return true;
                    }
                    return false;
                });
            }

            // Apocalipsis Zombie: mutaciones de escalado e inmunidad solar
            if (CursosActivos.isApocalipsisZombie() && server.getTickCount() % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Zombie zombie && !zombie.entityTags().contains("apoc_evaluado")) {
                            evaluarZombieApocalipsis(zombie);
                        }
                    }
                }
            }

            // Filo Mortal: espadas con Filo III a zombies
            if (CursosActivos.isFiloMortal() && server.getTickCount() % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Zombie zombie && !zombie.entityTags().contains("filo_mortal_equipado")) {
                            equiparEspadaFiloMortal(zombie, lvl);
                        }
                    }
                }
            }

            // ¿Spiderman?: efectos permanentes a todas las arañas
            if (CursosActivos.isSpiderman() && server.getTickCount() % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    for (Entity entity : lvl.getAllEntities()) {
                        if (entity instanceof Spider spider && !spider.entityTags().contains("spiderman_aplicado")) {
                            aplicarSpiderman(spider);
                        }
                    }
                }
            }

            // Intrusos Calavéricos: equipar Wither Skeletons y spawn nocturno en Overworld
            if (CursosActivos.isIntrusosCalavericos()) {
                if (server.getTickCount() % 40 == 0) {
                    for (ServerLevel lvl : server.getAllLevels()) {
                        for (Entity entity : lvl.getAllEntities()) {
                            if (entity instanceof WitherSkeleton ws && !ws.entityTags().contains("intruso_calaverico_equipado")) {
                                prepararIntrusoCalaverico(ws, lvl);
                            }
                        }
                    }
                }

                if (server.getTickCount() % 60 == 0) {
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
            if (CursosActivos.isSpeedpers() && server.getTickCount() % 60 == 0) {
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
                    if (server.getTickCount() % 40 == 0) {
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
            if (CursosActivos.isPermadeath() && server.getTickCount() % 40 == 0) {
                for (ServerLevel lvl : server.getAllLevels()) {
                    if (lvl.getGameRules().get(GameRules.NATURAL_HEALTH_REGENERATION)) {
                        lvl.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, false, server);
                    }
                }
            }

            // ¡Apocalipsis Minecraft !!!! (Nivel 6): Rastrear sangre de jugadores heridos
            if (CursosActivos.isApocalipsisMinecraftTotal() && server.getTickCount() % 20 == 0) {
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
                        if (server.getTickCount() % 40 == 0) {
                            sLvl.sendParticles(ParticleTypes.DAMAGE_INDICATOR, player.getX(), player.getY() + 0.6, player.getZ(), 2, 0.2, 0.2, 0.2, 0.02);
                        }
                    }
                }
            }

            // ¡Apocalipsis Minecraft !!!! (Nivel 6): Apilar zombies en torres si el jugador está elevado (Anti-Pilares)
            if (CursosActivos.isApocalipsisMinecraftTotal() && server.getTickCount() % 10 == 0) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (!player.isAlive() || player.isSpectator() || player.isCreative()) continue;
                    ServerLevel sLvl = (ServerLevel) player.level();
                    List<Zombie> nearbyZombies = sLvl.getEntitiesOfClass(Zombie.class, player.getBoundingBox().inflate(20.0, 10.0, 20.0), Entity::isAlive);
                    if (nearbyZombies.isEmpty()) continue;

                    // 1. Forzar que todos los zombies detecten y persigan al jugador incluso si está sobre un pilar
                    for (Zombie z : nearbyZombies) {
                        if (z.getTarget() != player) {
                            z.setTarget(player);
                        }
                    }

                    if (nearbyZombies.size() < 2) continue;

                    double pX = player.getX();
                    double pZ = player.getZ();

                    // 2. Obtener zombies que estén en el suelo (no montados como pasajeros)
                    List<Zombie> groundZombies = nearbyZombies.stream()
                            .filter(z -> !z.isPassenger())
                            .sorted(Comparator.comparingDouble(z -> z.distanceToSqr(pX, z.getY(), pZ)))
                            .toList();

                    if (groundZombies.isEmpty()) continue;

                    Zombie baseZombie = groundZombies.get(0);
                    double baseDistSq = baseZombie.distanceToSqr(pX, baseZombie.getY(), pZ);
                    double dyGround = player.getY() - baseZombie.getY();

                    // Solo activar modo torre si el jugador está elevado (de 1.2 a 6.5 bloques de altura)
                    if (dyGround >= 1.2 && dyGround <= 6.5) {
                        // Guiar a los zombies terrestres directamente al pie del pilar (X, Z del jugador a nivel de suelo)
                        for (Zombie gz : groundZombies) {
                            if (gz.distanceToSqr(pX, gz.getY(), pZ) > 4.0) {
                                gz.getNavigation().moveTo(pX, gz.getY(), pZ, 1.25);
                            }
                        }

                        // Si el zombie base ya está cerca del pilar (<= 3.5 bloques de distancia)
                        if (baseDistSq <= 12.25) {
                            // Encontrar el zombie más alto de la torre actual sobre la base
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

                            // Si el jugador sigue estando más alto que la punta de la torre y no excedemos altura 5
                            if (heightDiff >= 1.2 && towerHeight < 5) {
                                // Buscar otro zombie en el suelo cercano que pueda escalar la torre
                                for (int i = 1; i < groundZombies.size(); i++) {
                                    Zombie climber = groundZombies.get(i);
                                    if (climber != baseZombie && !climber.isPassenger() && climber.getPassengers().isEmpty()) {
                                        double climberDistSq = climber.distanceToSqr(pX, climber.getY(), pZ);
                                        if (climberDistSq <= 20.25) { // dentro de 4.5 bloques
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

                            // Si el zombie más alto está al alcance del jugador, atacarlo activamente
                            if (topZombie.distanceToSqr(player) <= 9.0 && server.getTickCount() % 20 == 0) {
                                topZombie.doHurtTarget(sLvl, player);
                            }
                        }
                    }
                }
            }
        });
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
