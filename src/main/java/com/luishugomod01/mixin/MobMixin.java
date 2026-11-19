package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.CursosActivos;
import com.luishugomod01.ruleta.NigromanteAccessor;
import com.luishugomod01.ruleta.PhantomRaptorAccessor;
import com.luishugomod01.ruleta.WallClimberAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.skeleton.Stray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin implements WallClimberAccessor, NigromanteAccessor, PhantomRaptorAccessor {

    @Shadow
    protected PathNavigation navigation;

    @Unique
    private static final EntityDataAccessor<Boolean> RULETA$DATA_NIGROMANTE =
            SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BOOLEAN);

    @Unique
    private static final EntityDataAccessor<Boolean> RULETA$DATA_PHANTOM_RAPTOR =
            SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BOOLEAN);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void ruleta$defineMobSyncData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(RULETA$DATA_NIGROMANTE, false);
        builder.define(RULETA$DATA_PHANTOM_RAPTOR, false);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void ruleta$syncNigromanteTag(CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        if (!self.level().isClientSide()) {
            if (self instanceof Stray
                    && !self.getEntityData().get(RULETA$DATA_NIGROMANTE)
                    && self.entityTags().contains("nigromante_criptas")) {
                self.getEntityData().set(RULETA$DATA_NIGROMANTE, true);
            } else if (self instanceof Phantom
                    && !self.getEntityData().get(RULETA$DATA_PHANTOM_RAPTOR)
                    && self.entityTags().contains("phantom_raptor")) {
                self.getEntityData().set(RULETA$DATA_PHANTOM_RAPTOR, true);
            }
        }
    }

    @Override
    public boolean ruleta$isPhantomRaptor() {
        Mob self = (Mob) (Object) this;
        return self.getEntityData().get(RULETA$DATA_PHANTOM_RAPTOR) || self.entityTags().contains("phantom_raptor");
    }

    @Override
    public void ruleta$setPhantomRaptor(boolean raptor) {
        Mob self = (Mob) (Object) this;
        self.getEntityData().set(RULETA$DATA_PHANTOM_RAPTOR, raptor);
        if (raptor) {
            self.addTag("phantom_raptor");
        } else {
            self.removeTag("phantom_raptor");
        }
    }

    @Override
    public boolean ruleta$isNigromante() {
        Mob self = (Mob) (Object) this;
        return self.getEntityData().get(RULETA$DATA_NIGROMANTE) || self.entityTags().contains("nigromante_criptas");
    }

    @Override
    public void ruleta$setNigromante(boolean nigromante) {
        Mob self = (Mob) (Object) this;
        self.getEntityData().set(RULETA$DATA_NIGROMANTE, nigromante);
        if (nigromante) {
            self.addTag("nigromante_criptas");
        } else {
            self.removeTag("nigromante_criptas");
        }
    }

    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void ruleta$inmuneAlSol(CallbackInfoReturnable<Boolean> cir) {
        Mob self = (Mob) (Object) this;
        if (self.entityTags().contains("apoc_mutante")
                || self.entityTags().contains("eclipse_inmune_sol")
                || CursosActivos.isEclipseActive()) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public void ruleta$setWallClimberNavigation() {
        Mob self = (Mob) (Object) this;
        this.navigation = new WallClimberNavigation(self, self.level());
    }
}
