package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.CursosActivos;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractBedBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "startSleepInBed", at = @At("HEAD"), cancellable = true)
    private void ruleta$bloquearCamaEnLunaDeSangre(
            AbstractBedBlock bedBlock,
            BlockState state,
            BedRule bedRule,
            BlockPos pos,
            CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir
    ) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (CursosActivos.isBloodMoonActive() && self.level().dimension().equals(Level.OVERWORLD)) {
            self.sendOverlayMessage(
                    Component.literal("No puedes descansar durante la Luna de Sangre...")
                            .withStyle(ChatFormatting.RED)
            );
            cir.setReturnValue(Either.left(Player.BedSleepingProblem.OTHER_PROBLEM));
        }
    }
}
