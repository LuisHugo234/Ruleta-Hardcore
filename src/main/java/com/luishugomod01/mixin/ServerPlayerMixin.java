package com.luishugomod01.mixin;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.ruleta.CursosActivos;
import com.luishugomod01.vidas.VidasJugadorAccessor;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractBedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements VidasJugadorAccessor {

    @Unique
    private int ruleta$vidas = -1;

    @Unique
    private boolean ruleta$vidasInicializadas = false;

    @Override
    public int ruleta$getVidas() {
        return this.ruleta$vidas;
    }

    @Override
    public void ruleta$setVidas(int vidas) {
        this.ruleta$vidas = Math.max(0, vidas);
    }

    @Override
    public boolean ruleta$isVidasInicializadas() {
        return this.ruleta$vidasInicializadas;
    }

    @Override
    public void ruleta$setVidasInicializadas(boolean inicializadas) {
        this.ruleta$vidasInicializadas = inicializadas;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void ruleta$guardarVidasNbt(ValueOutput output, CallbackInfo ci) {
        output.putInt("PermaRuletaVidas", this.ruleta$vidas);
        output.putBoolean("PermaRuletaVidasInit", this.ruleta$vidasInicializadas);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void ruleta$cargarVidasNbt(ValueInput input, CallbackInfo ci) {
        this.ruleta$vidas = input.getIntOr("PermaRuletaVidas", -1);
        this.ruleta$vidasInicializadas = input.getBooleanOr("PermaRuletaVidasInit", false);
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void ruleta$restaurarVidasAlRevivir(ServerPlayer oldPlayer, boolean restoreAll, CallbackInfo ci) {
        if (oldPlayer instanceof VidasJugadorAccessor oldAccessor) {
            this.ruleta$vidas = oldAccessor.ruleta$getVidas();
            this.ruleta$vidasInicializadas = oldAccessor.ruleta$isVidasInicializadas();
            ServerPlayer self = (ServerPlayer) (Object) this;
            if (this.ruleta$vidasInicializadas && this.ruleta$vidas <= 0) {
                self.setGameMode(GameType.SPECTATOR);
            }
            Ruleta_Hardcore.sincronizarVidasJugador(self);
        }
    }

    @Inject(method = "setGameMode", at = @At("HEAD"), cancellable = true)
    private void ruleta$bloquearCambioModoEliminado(GameType gameType, CallbackInfoReturnable<Boolean> cir) {
        if (this.ruleta$vidasInicializadas && this.ruleta$vidas <= 0 && gameType != GameType.SPECTATOR) {
            cir.setReturnValue(false);
        }
    }

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
