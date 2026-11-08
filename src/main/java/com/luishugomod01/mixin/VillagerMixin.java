package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.CursosActivos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(Villager.class)
public abstract class VillagerMixin {

    @Shadow
    public abstract VillagerData getVillagerData();

    @Shadow
    public abstract void setVillagerData(VillagerData data);

    @Shadow
    public abstract void refreshBrain(ServerLevel level);

    @Inject(method = "ageBoundaryReached", at = @At("TAIL"))
    private void ruleta$webonesAldeanoNitwitAlCrecer(CallbackInfo ci) {
        if (!CursosActivos.isWebones()) {
            return;
        }
        Villager self = (Villager) (Object) this;
        if (!self.isBaby() && self.level() instanceof ServerLevel serverLevel) {
            if (ThreadLocalRandom.current().nextFloat() < 0.25f) {
                this.setVillagerData(this.getVillagerData().withProfession(serverLevel.registryAccess(), VillagerProfession.NITWIT));
                this.refreshBrain(serverLevel);
            }
        }
    }
}
