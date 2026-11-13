package com.luishugomod01.mixin;

import com.luishugomod01.Ruleta_Hardcore;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "broadcastToPlayer", at = @At("HEAD"), cancellable = true)
    private void ruleta$ocultarEntidadesCordura(ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (self.entityTags().contains("alucinacion_cordura")) {
            if (!Ruleta_Hardcore.puedeVerAlucinacion(player.getUUID())) {
                cir.setReturnValue(false);
            }
        } else if (self.entityTags().contains("pesadilla_materializada")) {
            if (!Ruleta_Hardcore.estaEnCorduraMinima(player.getUUID())) {
                cir.setReturnValue(false);
            }
        }
    }
}
