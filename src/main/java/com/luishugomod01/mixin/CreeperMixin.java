package com.luishugomod01.mixin;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.ruleta.CreeperCustomAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Entity implements CreeperCustomAccessor {

    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> DATA_IS_POWERED;

    @Shadow
    private int explosionRadius;

    @Shadow
    private int maxSwell;

    protected CreeperMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void ruleta$setPowered(boolean powered) {
        this.entityData.set(DATA_IS_POWERED, powered);
    }

    @Override
    public void ruleta$setExplosionRadius(int radius) {
        this.explosionRadius = radius;
    }

    @Override
    public void ruleta$setMaxSwell(int maxSwell) {
        this.maxSwell = maxSwell;
    }

    @Inject(method = "explodeCreeper", at = @At("HEAD"), cancellable = true)
    private void ruleta$implosionAntimateria(CallbackInfo ci) {
        if (this.entityTags().contains("creeper_antimateria") && this.level() instanceof ServerLevel serverLevel) {
            Ruleta_Hardcore.iniciarVorticeAntimateria(serverLevel, this.getX(), this.getY() + 0.5, this.getZ());
            this.discard();
            ci.cancel();
        }
    }
}
