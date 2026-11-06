package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.CreeperCustomAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Entity implements CreeperCustomAccessor {

    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> DATA_IS_POWERED;

    @Shadow
    private int explosionRadius;

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
}
