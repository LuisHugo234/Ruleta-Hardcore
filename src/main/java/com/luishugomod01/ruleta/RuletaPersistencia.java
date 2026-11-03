package com.luishugomod01.ruleta;

import com.luishugomod01.Ruleta_Hardcore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RuletaPersistencia {

    private static Path obtenerRutaArchivo(MinecraftServer server) {
        Path dataDir = server.getWorldPath(LevelResource.ROOT).resolve("data");
        try {
            Files.createDirectories(dataDir);
        } catch (IOException ignored) {}
        return dataDir.resolve("ruleta_hardcore.dat");
    }

    public static void guardar(MinecraftServer server) {
        if (server == null) return;
        try {
            Path file = obtenerRutaArchivo(server);
            CompoundTag root = new CompoundTag();

            ListTag activasTag = new ListTag();
            for (String id : CursosActivos.obtenerIdsActivas()) {
                activasTag.add(StringTag.valueOf(id));
            }
            root.put("MaldicionesActivas", activasTag);

            ListTag consumidosTag = new ListTag();
            for (String id : CatalogoCastigos.obtenerIdsConsumidos()) {
                consumidosTag.add(StringTag.valueOf(id));
            }
            root.put("CastigosConsumidos", consumidosTag);

            NbtIo.writeCompressed(root, file);
        } catch (Exception e) {
            Ruleta_Hardcore.LOGGER.error("Error al guardar estado de la ruleta en disco", e);
        }
    }

    public static void cargar(MinecraftServer server) {
        if (server == null) return;
        try {
            Path file = obtenerRutaArchivo(server);
            if (!Files.exists(file)) return;

            CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());

            ListTag activasTag = root.getListOrEmpty("MaldicionesActivas");
            for (int i = 0; i < activasTag.size(); i++) {
                String id = activasTag.getStringOr(i, "");
                if (!id.isEmpty()) {
                    CursosActivos.activarPorId(id);
                }
            }

            ListTag consumidosTag = root.getListOrEmpty("CastigosConsumidos");
            for (int i = 0; i < consumidosTag.size(); i++) {
                String id = consumidosTag.getStringOr(i, "");
                if (!id.isEmpty()) {
                    CatalogoCastigos.marcarConsumido(id);
                }
            }

            if (CursosActivos.isPermadeath()) {
                for (var lvl : server.getAllLevels()) {
                    lvl.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, false, server);
                }
            }

            Ruleta_Hardcore.LOGGER.info("Estado de PermaRuleta cargado: {} maldiciones activas restauradas.", activasTag.size());
        } catch (Exception e) {
            Ruleta_Hardcore.LOGGER.error("Error al cargar estado de la ruleta desde disco", e);
        }
    }

    public static void limpiar(MinecraftServer server) {
        if (server == null) return;
        try {
            Path file = obtenerRutaArchivo(server);
            Files.deleteIfExists(file);
        } catch (Exception e) {
            Ruleta_Hardcore.LOGGER.error("Error al limpiar archivo de persistencia de la ruleta", e);
        }
    }
}
