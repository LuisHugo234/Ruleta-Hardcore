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
    private boolean ruleta$savedNoPhysics;

    protected CreeperMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private boolean ruleta$esCreeperFantasma() {
        Creeper self = (Creeper) (Object) this;
        float scale = self.getScale();
        return self.entityTags().contains("creeper_fantasma")
                || (scale > 1.015f && scale < 1.055f);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void ruleta$activarFaseFantasma(CallbackInfo ci) {
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
