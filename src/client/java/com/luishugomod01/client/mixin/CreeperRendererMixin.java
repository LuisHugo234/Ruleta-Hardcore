package com.luishugomod01.client.mixin;

import com.luishugomod01.client.CreeperAvisponWingsLayer;
import com.luishugomod01.client.CreeperRenderStateAccessor;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperRenderer.class)
public abstract class CreeperRendererMixin {

    @Unique
    private static final Identifier ANTIMATERIA_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/creeper_antimateria.png");

    @Unique
    private static final Identifier FANTASMA_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/creeper_fantasma.png");

    @Unique
    private static final Identifier TERMONUCLEAR_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/creeper_termonuclear.png");

    @Unique
    private static final Identifier AVISPON_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/creeper_avispon.png");

    @Unique
    private static final Identifier COSMICO_TEXTURE = Identifier.fromNamespaceAndPath("ruleta_hardcore", "textures/entity/creeper_cosmico.png");

    @Inject(method = "<init>", at = @At("TAIL"))
    private void ruleta$addWingsLayer(EntityRendererProvider.Context context, CallbackInfo ci) {
        if ((Object) this instanceof LivingEntityRendererAccessor accessor) {
            accessor.ruleta$addLayer(new CreeperAvisponWingsLayer((CreeperRenderer) (Object) this));
        }
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/monster/Creeper;Lnet/minecraft/client/renderer/entity/state/CreeperRenderState;F)V", at = @At("TAIL"))
    private void ruleta$estadoCreeperFantasma(Creeper creeper, CreeperRenderState state, float partialTicks, CallbackInfo ci) {
        boolean isFantasma = (state.scale > 1.015f && state.scale < 1.055f)
                || creeper.entityTags().contains("creeper_fantasma");
        if (isFantasma) {
            // Renderizado espectral propio con alpha translúcido en textura (evita invisibilidad vanilla al 15%)
            state.isInvisible = false;
            state.isInvisibleToPlayer = false;
        }
        boolean isAvispon = (state.scale > 0.25f && state.scale < 0.31f)
                || creeper.entityTags().contains("creeper_avispon");
        boolean isTermonuclear = (state.scale > 1.18f && state.scale < 1.26f)
                || creeper.entityTags().contains("creeper_termonuclear");
        boolean isCosmico = (state.scale > 1.125f && state.scale < 1.165f)
                || creeper.entityTags().contains("creeper_cosmico");
        if (state instanceof CreeperRenderStateAccessor acc) {
            acc.ruleta$setAvispon(isAvispon);
            acc.ruleta$setFantasma(isFantasma);
            acc.ruleta$setTermonuclear(isTermonuclear);
            acc.ruleta$setCosmico(isCosmico);
            acc.ruleta$setAgeInTicks(creeper.tickCount + partialTicks);
        }
    }

    @Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/CreeperRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("HEAD"), cancellable = true)
    private void ruleta$texturaCreeperAntimateria(CreeperRenderState state, CallbackInfoReturnable<Identifier> cir) {
        if ((state instanceof CreeperRenderStateAccessor acc && acc.ruleta$isAvispon())
                || (state.scale > 0.25f && state.scale < 0.31f)) {
            cir.setReturnValue(AVISPON_TEXTURE);
        } else if ((state instanceof CreeperRenderStateAccessor acc && acc.ruleta$isFantasma())
                || (state.scale > 1.015f && state.scale < 1.055f)) {
            cir.setReturnValue(FANTASMA_TEXTURE);
        } else if (state.scale > 1.06f && state.scale < 1.12f) {
            cir.setReturnValue(ANTIMATERIA_TEXTURE);
        } else if ((state instanceof CreeperRenderStateAccessor acc && acc.ruleta$isCosmico())
                || (state.scale > 1.125f && state.scale < 1.165f)) {
            cir.setReturnValue(COSMICO_TEXTURE);
        } else if ((state instanceof CreeperRenderStateAccessor acc && acc.ruleta$isTermonuclear())
                || (state.scale > 1.18f && state.scale < 1.26f)) {
            cir.setReturnValue(TERMONUCLEAR_TEXTURE);
        }
    }
}
