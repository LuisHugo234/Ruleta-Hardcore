package com.luishugomod01.mixin;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.ruleta.CreeperCustomAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Unique
    private static final EntityDataAccessor<Byte> RULETA$DATA_TIPO =
            SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.BYTE);

    @Unique
    private static final EntityDataAccessor<Integer> RULETA$DATA_TERMO_TEMP =
            SynchedEntityData.defineId(Creeper.class, EntityDataSerializers.INT);

    @Unique
    private boolean ruleta$savedNoPhysics;

    protected CreeperMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void ruleta$defineCreeperSyncData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(RULETA$DATA_TIPO, CreeperCustomAccessor.TIPO_NORMAL);
        builder.define(RULETA$DATA_TERMO_TEMP, 0);
    }

    @Override
    public byte ruleta$getTipoCreeper() {
        return this.entityData.get(RULETA$DATA_TIPO);
    }

    @Override
    public void ruleta$setTipoCreeper(byte tipo) {
        this.entityData.set(RULETA$DATA_TIPO, tipo);
    }

    @Override
    public int ruleta$getTemperaturaTermonuclear() {
        return this.entityData.get(RULETA$DATA_TERMO_TEMP);
    }

    @Override
    public void ruleta$setTemperaturaTermonuclear(int temperatura) {
        this.entityData.set(RULETA$DATA_TERMO_TEMP, Math.clamp(temperatura, 0, 100));
    }

    @Unique
    private boolean ruleta$esCreeperFantasma() {
        if (this.ruleta$getTipoCreeper() == CreeperCustomAccessor.TIPO_FANTASMA) {
            return true;
        }
        Creeper self = (Creeper) (Object) this;
        return self.entityTags().contains("creeper_fantasma");
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void ruleta$activarFaseFantasma(CallbackInfo ci) {
        if (!this.level().isClientSide() && this.ruleta$getTipoCreeper() == CreeperCustomAccessor.TIPO_NORMAL) {
            Creeper self = (Creeper) (Object) this;
            if (self.entityTags().contains("creeper_fantasma")) {
                this.ruleta$setTipoCreeper(CreeperCustomAccessor.TIPO_FANTASMA);
            } else if (self.entityTags().contains("creeper_antimateria")) {
                this.ruleta$setTipoCreeper(CreeperCustomAccessor.TIPO_ANTIMATERIA);
            } else if (self.entityTags().contains("creeper_cosmico")) {
                this.ruleta$setTipoCreeper(CreeperCustomAccessor.TIPO_COSMICO);
            } else if (self.entityTags().contains("creeper_termonuclear")) {
                this.ruleta$setTipoCreeper(CreeperCustomAccessor.TIPO_TERMONUCLEAR);
            } else if (self.entityTags().contains("creeper_avispon")) {
                this.ruleta$setTipoCreeper(CreeperCustomAccessor.TIPO_AVISPON);
            }
        }
        if (this.ruleta$esCreeperFantasma()) {
            this.ruleta$savedNoPhysics = this.noPhysics;
            this.noPhysics = true;
            this.setNoGravity(true);
            this.resetFallDistance();
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void ruleta$restaurarFaseFantasma(CallbackInfo ci) {
        if (this.ruleta$esCreeperFantasma()) {
            this.noPhysics = this.ruleta$savedNoPhysics;
        }
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
            return;
        }

        // Creeper Termonuclear (Nivel 6): NUNCA detona por proximidad vanilla ni por mecha ordinaria.
        // Únicamente explota al cargarse al 100% de temperatura en su núcleo volcánico.
        if (this.entityTags().contains("creeper_termonuclear")) {
            ci.cancel();
            return;
        }

        // Creeper Avispón (Nivel 3): No rompe bloques y realiza 2/3 del daño vanilla en el mismo radio.
        if (this.entityTags().contains("creeper_avispon") && this.level() instanceof ServerLevel serverLevel) {
            Ruleta_Hardcore.detenerSonidoCreeperAvispon(this, serverLevel);
            Ruleta_Hardcore.detonarCreeperAvispon((Creeper) (Object) this, serverLevel);
            this.discard();
            ci.cancel();
            return;
        }
    }

    @Inject(method = "spawnLingeringCloud", at = @At("HEAD"), cancellable = true)
    private void ruleta$cancelarNubePocionCosmico(CallbackInfo ci) {
        if (this.entityTags().contains("creeper_cosmico")) {
            ci.cancel();
        }
    }
}
