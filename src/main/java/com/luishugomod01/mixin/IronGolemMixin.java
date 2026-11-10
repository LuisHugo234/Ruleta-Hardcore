package com.luishugomod01.mixin;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.ruleta.ColosoRuinaAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolem.class)
public abstract class IronGolemMixin implements ColosoRuinaAccessor {

    @Unique
    private int ruleta$slamAnimationTicks = 0;

    @Override
    public boolean ruleta$isColosoRuina() {
        return ((IronGolem) (Object) this).entityTags().contains("coloso_ruina");
    }

    @Override
    public void ruleta$setColosoRuina(boolean coloso) {
        IronGolem golem = (IronGolem) (Object) this;
        if (coloso) {
            golem.addTag("coloso_ruina");
        } else {
            golem.removeTag("coloso_ruina");
        }
    }

    @Override
    public int ruleta$getSlamTicks() {
        return this.ruleta$slamAnimationTicks;
    }

    @Override
    public void ruleta$setSlamTicks(int ticks) {
        this.ruleta$slamAnimationTicks = ticks;
    }

    @Override
    public float ruleta$getSlamProgress(float partialTicks) {
        if (this.ruleta$slamAnimationTicks <= 0) {
            return 0.0f;
        }
        float remaining = Math.max(0.0f, (float) this.ruleta$slamAnimationTicks - partialTicks);
        return Math.clamp((25.0f - remaining) / 25.0f, 0.0f, 1.0f);
    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"), cancellable = true)
    private void ruleta$onEntityEvent(byte id, CallbackInfo ci) {
        if (id == 66) {
            this.ruleta$slamAnimationTicks = 25;
            ci.cancel();
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void ruleta$tickSlam(CallbackInfo ci) {
        if (this.ruleta$slamAnimationTicks > 0) {
            this.ruleta$slamAnimationTicks--;
        }
    }

    @Inject(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void ruleta$colosoCanAttackPlayer(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (this.ruleta$isColosoRuina() && target instanceof Player) {
            cir.setReturnValue(true);
        }
    }

    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
    private float ruleta$mitigarDanoFrontal(float amount, ServerLevel level, DamageSource damageSource) {
        if (this.ruleta$isColosoRuina()) {
            return Ruleta_Hardcore.procesarDanoColosoRuina((IronGolem) (Object) this, level, damageSource, amount);
        }
        return amount;
    }
}
