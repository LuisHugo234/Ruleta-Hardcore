package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.CursosActivos;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.SpawnState.class)
public abstract class NaturalSpawnerSpawnStateMixin {

    @Unique
    private static final int RULETA_MAGIC_NUMBER = 289;
    @Unique
    private static final int BLOOD_MOON_CAP_MULTIPLIER = 3;

    @Shadow
    @Final
    private int spawnableChunkCount;

    @Shadow
    @Final
    private Object2IntOpenHashMap<MobCategory> mobCategoryCounts;

    @Inject(method = "canSpawnForCategoryGlobal", at = @At("HEAD"), cancellable = true)
    private void ruleta$aumentarSpawnCapGlobalLunaSangre(MobCategory category, CallbackInfoReturnable<Boolean> cir) {
        if (CursosActivos.isBloodMoonActive() && category == MobCategory.MONSTER) {
            int boostedMax = (category.getMaxInstancesPerChunk() * BLOOD_MOON_CAP_MULTIPLIER)
                    * this.spawnableChunkCount / RULETA_MAGIC_NUMBER;
            cir.setReturnValue(this.mobCategoryCounts.getInt(category) < boostedMax);
        }
    }

    @Inject(method = "canSpawnForCategoryLocal", at = @At("HEAD"), cancellable = true)
    private void ruleta$ignorarSpawnCapLocalLunaSangre(MobCategory category, ChunkPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (CursosActivos.isBloodMoonActive() && category == MobCategory.MONSTER) {
            cir.setReturnValue(true);
        }
    }
}
