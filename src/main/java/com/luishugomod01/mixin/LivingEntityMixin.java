package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.CursosActivos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    private void ruleta$escalarParedes(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.horizontalCollision && self.entityTags().contains("apoc_mutante")) {
            cir.setReturnValue(true);
        }
    }

    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
    private float ruleta$multiplicarDanoEntrante(float amount, ServerLevel level, DamageSource damageSource) {
        if (CursosActivos.isDanoColateral() && damageSource.is(DamageTypes.ENDER_PEARL)) {
            amount *= 5.0f;
        }
        if (CursosActivos.isUltraFundido() && (Object) this instanceof Player && damageSource.is(DamageTypeTags.IS_FIRE)) {
            amount *= 10.0f;
        }
        return amount;
    }

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true)
    private void ruleta$ignorarArmaduraEnderPearl(DamageSource damageSource, float damage, CallbackInfoReturnable<Float> cir) {
        if (CursosActivos.isDanoColateral() && damageSource.is(DamageTypes.ENDER_PEARL)) {
            cir.setReturnValue(damage);
        }
    }

    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("HEAD"), cancellable = true)
    private void ruleta$ignorarEncantamientosArmaduraEnderPearl(DamageSource damageSource, float damage, CallbackInfoReturnable<Float> cir) {
        if (CursosActivos.isDanoColateral() && damageSource.is(DamageTypes.ENDER_PEARL)) {
            LivingEntity self = (LivingEntity) (Object) this;
            if (self.hasEffect(MobEffects.RESISTANCE) && !damageSource.is(DamageTypeTags.BYPASSES_RESISTANCE)) {
                var effect = self.getEffect(MobEffects.RESISTANCE);
                if (effect != null) {
                    int factor = 25 - (effect.getAmplifier() + 1) * 5;
                    damage = Math.max((damage * factor) / 25.0f, 0.0f);
                }
            }
            cir.setReturnValue(damage);
        }
    }

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void ruleta$errorTotemico(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (!CursosActivos.isErrorTotemico())
            return;
        if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
            return;

        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack totemStack = null;
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack held = self.getItemInHand(hand);
            if (held.has(DataComponents.DEATH_PROTECTION)) {
                totemStack = held;
                break;
            }
        }

        if (totemStack == null)
            return;

        // 2% de probabilidad de fallo
        if (ThreadLocalRandom.current().nextFloat() < 0.02f) {
            totemStack.shrink(1);
            self.level().playSound(null, self.getX(), self.getY(), self.getZ(),
                    SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, 0.4f);

            if (self instanceof ServerPlayer sp) {
                sp.sendSystemMessage(Component.literal(
                        "§4§l☠ ¡ERROR TOTÉMICO! §cTu tótem de la inmortalidad ha fallado y no ha podido salvarte..."));
                if (sp.level().getServer() != null) {
                    sp.level().getServer().getPlayerList().broadcastSystemMessage(
                            Component.literal("§c☠ ¡El tótem de " + sp.getScoreboardName()
                                    + " sufrió un §4ERROR TOTÉMICO§c y falló!"),
                            false);
                }
            }
            cir.setReturnValue(false);
        }
    }
}
