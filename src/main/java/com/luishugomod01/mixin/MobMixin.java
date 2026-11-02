package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.WallClimberAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin implements WallClimberAccessor {

    @Shadow
    protected PathNavigation navigation;

    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void ruleta$inmuneAlSol(CallbackInfoReturnable<Boolean> cir) {
        Mob self = (Mob) (Object) this;
        if (self.entityTags().contains("apoc_mutante")) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public void ruleta$setWallClimberNavigation() {
        Mob self = (Mob) (Object) this;
        this.navigation = new WallClimberNavigation(self, self.level());
    }
}
