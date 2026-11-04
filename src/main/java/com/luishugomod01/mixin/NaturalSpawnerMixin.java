package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.CursosActivos;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {

    @Unique
    private static final int EXTRA_BLOOD_MOON_SPAWN_ATTEMPTS = 3;
    @Unique
    private static final int BLOOD_MOON_CAP_MULTIPLIER = 3;
    @Unique
    private static final int RULETA_MAGIC_NUMBER = 289;

    @Inject(method = "spawnForChunk", at = @At("TAIL"))
    private static void ruleta$forzarHordasLunaSangre(
            ServerLevel level,
            LevelChunk chunk,
            NaturalSpawner.SpawnState state,
            List<MobCategory> categories,
            CallbackInfo ci
    ) {
        if (!CursosActivos.isBloodMoonActive()) return;
        if (!level.dimension().equals(Level.OVERWORLD)) return;
        if (!categories.contains(MobCategory.MONSTER)) return;

        int boostedMax = (MobCategory.MONSTER.getMaxInstancesPerChunk() * BLOOD_MOON_CAP_MULTIPLIER)
                * state.getSpawnableChunkCount() / RULETA_MAGIC_NUMBER;
        int baseCount = state.getMobCategoryCounts().getInt(MobCategory.MONSTER);
        if (baseCount >= boostedMax) return;

        AtomicInteger extraSpawned = new AtomicInteger(0);
        for (int i = 0; i < EXTRA_BLOOD_MOON_SPAWN_ATTEMPTS; i++) {
            if (baseCount + extraSpawned.get() >= boostedMax) break;
            NaturalSpawner.spawnCategoryForChunk(
                    MobCategory.MONSTER,
                    level,
                    chunk,
                    (type, spawnLevel, pos, chunkAccess) -> (baseCount + extraSpawned.get()) < boostedMax,
                    (mob, chunkAccess) -> extraSpawned.incrementAndGet()
            );
        }
    }
}
