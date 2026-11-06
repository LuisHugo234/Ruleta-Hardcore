package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.CursosActivos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(Witch.class)
public abstract class WitchMixin {

    @ModifyArg(
            method = "performRangedAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectileUsingShoot(Lnet/minecraft/world/entity/projectile/Projectile$ProjectileFactory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;DDDFF)Lnet/minecraft/world/entity/projectile/Projectile;"
            ),
            index = 2
    )
    private ItemStack ruleta$pocionWingardiumLeviosa(ItemStack originalPotion) {
        if (!CursosActivos.isWingardiumLeviosa()) {
            return originalPotion;
        }
        Witch self = (Witch) (Object) this;
        LivingEntity target = self.getTarget();
        if (target instanceof Raider) {
            return originalPotion;
        }

        // Probabilidad de arrojar poción de Levitación XV (amplificador 14) por 1 segundo (25 ticks para compensar distancia de splash)
        boolean targetYaLevitando = target != null && target.hasEffect(MobEffects.LEVITATION);
        if (!targetYaLevitando && ThreadLocalRandom.current().nextFloat() < 0.45f) {
            ItemStack levitationSplash = new ItemStack(Items.SPLASH_POTION);
            levitationSplash.set(
                    DataComponents.POTION_CONTENTS,
                    PotionContents.EMPTY.withEffectAdded(new MobEffectInstance(MobEffects.LEVITATION, 25, 14))
            );
            return levitationSplash;
        }
        return originalPotion;
    }
}
