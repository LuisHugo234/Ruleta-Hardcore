package com.luishugomod01.mixin;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.ruleta.CursosActivos;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected ItemStack useItem;

    @Unique
    private Item ruleta$lastUsedItem;

    @Unique
    private boolean ruleta$lastUsedWasFood;

    @Unique
    private final java.util.ArrayDeque<Long> ruleta$shieldHitTicks = new java.util.ArrayDeque<>();

    @Unique
    private long ruleta$cegueraAbisalExpiry = 0L;

    @Unique
    private long ruleta$corrupcionVacioExpiry = 0L;

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    private void ruleta$escalarParedes(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.horizontalCollision && self.entityTags().contains("apoc_mutante")) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void ruleta$inmunidadCreeperMulticreeper(ServerLevel level, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Creeper && damageSource.is(DamageTypeTags.IS_EXPLOSION)
                && damageSource.getEntity() instanceof Creeper sourceCreeper
                && sourceCreeper.entityTags().contains("mini_multicreeper")) {
            cir.setReturnValue(false);
        }
    }

    @ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true)
    private float ruleta$multiplicarDanoEntrante(float amount, ServerLevel level, DamageSource damageSource) {
        if (CursosActivos.isDanoColateral() && damageSource.is(DamageTypes.ENDER_PEARL)) {
            amount *= 3.0f;
        }
        if (CursosActivos.isCaidaBrutal() && (Object) this instanceof Player
                && damageSource.is(DamageTypeTags.IS_FALL)) {
            amount *= 1.25f;
        }
        if (CursosActivos.isMuerteEspinosa() && damageSource.is(DamageTypes.CACTUS)) {
            amount = 1.0E9f;
        }
        if (CursosActivos.isMagmaContacto() && damageSource.is(DamageTypes.HOT_FLOOR)) {
            amount = 1.0E9f;
        }
        if (CursosActivos.isUltraFundido() && (Object) this instanceof Player
                && damageSource.is(DamageTypeTags.IS_FIRE)) {
            amount *= 10.0f;
        }
        if (CursosActivos.isEnderGhast() && !((Object) this instanceof Ghast)) {
            if (damageSource.getDirectEntity() instanceof LargeFireball
                    || damageSource.getEntity() instanceof Ghast
                    || (damageSource.is(DamageTypeTags.IS_EXPLOSION) && damageSource.getEntity() instanceof Ghast)) {
                amount *= 3.0f;
            }
        }
        if (CursosActivos.isVexGrandes() && damageSource.getEntity() instanceof Vex) {
            amount *= 2.0f;
        }
        if (CursosActivos.isCreepersEscondidos() && damageSource.getEntity() instanceof Creeper creeper
                && creeper.entityTags().contains("creeper_escondido")) {
            amount *= (1.0f / 3.0f);
        }
        return amount;
    }

    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void ruleta$efectosPostGolpe(ServerLevel level, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        // Cuccos Salvajes: al golpear a un pollo, éste y los pollos cercanos se vuelven agresivos contra el jugador
        if (CursosActivos.isCuccosSalvajes() && self instanceof Chicken chicken && damageSource.getEntity() instanceof Player) {
            chicken.addTag("cucco_enfurecido");
            List<Chicken> flock = level.getEntitiesOfClass(Chicken.class, chicken.getBoundingBox().inflate(16.0), Entity::isAlive);
            for (Chicken c : flock) {
                c.addTag("cucco_enfurecido");
            }
        }

        // Cucaracha Plateada: los lepismas otorgan Lentitud II por 5s (100 ticks) al golpear al jugador y alertan a otros lepismas a un máximo de 12 bloques
        if (CursosActivos.isCucarachaPlateada() && cir.getReturnValueZ()
                && self instanceof Player player && damageSource.getEntity() instanceof Silverfish) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 1));
            List<Silverfish> nearbySilverfish = level.getEntitiesOfClass(
                    Silverfish.class,
                    player.getBoundingBox().inflate(12.0),
                    Entity::isAlive
            );
            for (Silverfish sf : nearbySilverfish) {
                if (sf.getTarget() == null || !sf.getTarget().isAlive()) {
                    sf.setTarget(player);
                }
            }
        }

        // Rey de lo Profundo (Nivel 4): el Elder Guardian aplica Debilidad I (+1 por cada golpe recibido por el rayo) por 20s (400 ticks)
        if (CursosActivos.isReyDeLoProfundo() && cir.getReturnValueZ()
                && self instanceof Player player && damageSource.getEntity() instanceof ElderGuardian
                && damageSource.is(DamageTypes.INDIRECT_MAGIC)) {
            MobEffectInstance currentWeakness = player.getEffect(MobEffects.WEAKNESS);
            int nextAmplifier = (currentWeakness != null) ? Math.min(254, currentWeakness.getAmplifier() + 1) : 0;
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400, nextAmplifier));
        }

        // El Verdugo (Nivel 6): Piglin Bruto desarma al jugador al golpearlo (sin necesidad de críticos, sin mensaje en pantalla ni chat)
        if (CursosActivos.isElVerdugo() && cir.getReturnValueZ()
                && damageSource.getEntity() instanceof PiglinBrute brute && brute.entityTags().contains("piglin_verdugo")
                && self instanceof ServerPlayer sp) {
            ItemStack held = sp.getMainHandItem();
            if (!held.isEmpty()) {
                level.sendParticles(ParticleTypes.CRIT, sp.getX(), sp.getY() + 1.0, sp.getZ(), 20, 0.3, 0.4, 0.3, 0.15);
                level.playSound(null, sp.getX(), sp.getY(), sp.getZ(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.2f, 0.8f);
                level.playSound(null, sp.getX(), sp.getY(), sp.getZ(), SoundEvents.PIGLIN_BRUTE_ANGRY, SoundSource.HOSTILE, 1.2f, 0.9f);
                ItemEntity dropped = new ItemEntity(level, sp.getX(), sp.getY() + 0.5, sp.getZ(), held.copy());
                dropped.setDefaultPickUpDelay();
                level.addFreshEntity(dropped);
                sp.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                sp.getCooldowns().addCooldown(held, 60);
            }
        }

        // Ceguera Abisal (Nivel 5): En el End o Deep Dark, cualquier golpe de un mob hostil aplica Ceguera y Oscuridad por 8s
        if (CursosActivos.isCegueraAbisal() && cir.getReturnValueZ()
                && self instanceof ServerPlayer sp && damageSource.getEntity() instanceof Monster) {
            boolean enEnd = sp.level().dimension().equals(Level.END);
            boolean enDeepDark = sp.level().getBiome(sp.blockPosition()).is(Biomes.DEEP_DARK);
            if (enEnd || enDeepDark) {
                sp.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 160, 0, false, true, true));
                sp.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 160, 0, false, true, true));
                this.ruleta$cegueraAbisalExpiry = level.getGameTime() + 160L;
            }
        }

        // Corrupción del Vacío (Nivel 6): golpe de Enderman o proyectil de Shulker desactiva la armadura por 5s
        if (CursosActivos.isCorrupcionVacio() && cir.getReturnValueZ() && self instanceof ServerPlayer sp) {
            boolean deVacio = damageSource.getEntity() instanceof Enderman
                    || damageSource.getEntity() instanceof Shulker
                    || damageSource.getDirectEntity() instanceof ShulkerBullet;
            if (deVacio) {
                this.ruleta$corrupcionVacioExpiry = level.getGameTime() + 100L;
                level.sendParticles(ParticleTypes.PORTAL, sp.getX(), sp.getY() + 1.0, sp.getZ(), 35, 0.35, 0.5, 0.35, 0.2);
                level.playSound(null, sp.getX(), sp.getY(), sp.getZ(), SoundEvents.SHULKER_BULLET_HIT, SoundSource.PLAYERS, 1.2f, 0.5f);
                sp.sendSystemMessage(Component.literal("§5§l☠ ¡CORRUPCIÓN DEL VACÍO! §dTu armadura ha quedado completamente inutilizada por 5 segundos..."), true);
            }
        }

        // Telaraña al Fallo (Nivel 2): araña salta/impacta sobre el jugador y coloca telaraña temporal por 2.5s
        if (CursosActivos.isTelaranaAlFallo() && cir.getReturnValueZ()
                && self instanceof ServerPlayer sp && damageSource.getEntity() instanceof Spider) {
            Ruleta_Hardcore.colocarTelaranaTemporal(level, sp.blockPosition());
        }

        // Multicreeper (Nivel 5): Creeper sobrevive a golpe cuerpo a cuerpo -> 30% de dividirse en 2 mini-creepers (mecha 1s)
        if (CursosActivos.isMulticreeper() && cir.getReturnValueZ()
                && self instanceof Creeper creeper && creeper.isAlive()
                && !creeper.entityTags().contains("mini_multicreeper")
                && damageSource.getDirectEntity() instanceof Player player
                && !damageSource.is(DamageTypeTags.IS_PROJECTILE)
                && !damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
            if (ThreadLocalRandom.current().nextFloat() < 0.30f) {
                Ruleta_Hardcore.dividirMulticreeper(level, creeper, player);
            }
        }
    }

    @Inject(method = "applyItemBlocking", at = @At("RETURN"))
    private void ruleta$escudoPesadoConsumoComida(ServerLevel level, DamageSource damageSource, float damage, CallbackInfoReturnable<Float> cir) {
        if (!CursosActivos.isEscudoPesado()) return;
        if (cir.getReturnValueF() > 0.0f && (Object) this instanceof ServerPlayer sp) {
            // Consume el doble de comida por el esfuerzo de bloquear el impacto
            int currentFood = sp.getFoodData().getFoodLevel();
            sp.getFoodData().setFoodLevel(Math.max(0, currentFood - 2));
            sp.causeFoodExhaustion(4.0f);

            // Deshabilitar el escudo al recibir 5 golpes seguidos en un rango de 10 segundos (200 ticks)
            long now = level.getGameTime();
            while (!this.ruleta$shieldHitTicks.isEmpty() && now - this.ruleta$shieldHitTicks.peekFirst() > 200L) {
                this.ruleta$shieldHitTicks.pollFirst();
            }
            this.ruleta$shieldHitTicks.addLast(now);
            if (this.ruleta$shieldHitTicks.size() >= 5) {
                this.ruleta$shieldHitTicks.clear();
                ItemStack blockingStack = sp.getItemBlockingWith();
                if (blockingStack == null || blockingStack.isEmpty()) {
                    blockingStack = new ItemStack(Items.SHIELD);
                }
                sp.getCooldowns().addCooldown(blockingStack, 100);
                sp.stopUsingItem();
                level.playSound(null, sp.getX(), sp.getY(), sp.getZ(), SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 1.0f, 0.9f);
            }
        }
    }

    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void ruleta$capturarItemConsumido(CallbackInfo ci) {
        if (this.useItem != null && !this.useItem.isEmpty()) {
            this.ruleta$lastUsedItem = this.useItem.getItem();
            this.ruleta$lastUsedWasFood = this.useItem.has(DataComponents.FOOD);
        } else {
            this.ruleta$lastUsedItem = null;
            this.ruleta$lastUsedWasFood = false;
        }
    }

    @Inject(method = "completeUsingItem", at = @At("TAIL"))
    private void ruleta$efectosConsumoAlimentos(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide() || this.ruleta$lastUsedItem == null) return;

        Item consumed = this.ruleta$lastUsedItem;
        boolean wasFood = this.ruleta$lastUsedWasFood;
        this.ruleta$lastUsedItem = null;
        this.ruleta$lastUsedWasFood = false;

        // Manzanas Regeneradoras: las manzanas de oro (exceptuando las Manzanas Doradas Encantadas) no dan absorción
        if (CursosActivos.isManzanaRegeneradora() && consumed == Items.GOLDEN_APPLE) {
            self.removeEffect(MobEffects.ABSORPTION);
            self.setAbsorptionAmount(0.0f);
        }

        // Comida Echada a Perder (Nivel 1): 5% de probabilidad de recibir Infestado por 15s (300 ticks)
        // No cuentan: Manzanas de oro, Manzanas de oro encantadas, Zanahorias doradas y cualquier alimento cocinado
        if (CursosActivos.isComidaEchada() && wasFood && !ruleta$esComidaExentaInfestado(consumed)) {
            if (ThreadLocalRandom.current().nextFloat() < 0.05f) {
                self.addEffect(new MobEffectInstance(MobEffects.INFESTED, 300, 0));
            }
        }

        // Contaminación Cruzada: los alimentos crudos dan Hambre II y Veneno I
        if (CursosActivos.isContaminacionCruzada() && ruleta$esAlimentoCrudo(consumed)) {
            self.addEffect(new MobEffectInstance(MobEffects.HUNGER, 400, 1));
            self.addEffect(new MobEffectInstance(MobEffects.POISON, 160, 0));
        }

        // Ceguera Abisal: la leche no puede disipar la Ceguera y Oscuridad mientras sigan activas
        if (CursosActivos.isCegueraAbisal() && consumed == Items.MILK_BUCKET && this.ruleta$cegueraAbisalExpiry > self.level().getGameTime()) {
            int remaining = (int) (this.ruleta$cegueraAbisalExpiry - self.level().getGameTime());
            self.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, remaining, 0, false, true, true));
            self.addEffect(new MobEffectInstance(MobEffects.DARKNESS, remaining, 0, false, true, true));
            if (self instanceof ServerPlayer sp) {
                sp.sendOverlayMessage(Component.literal("§8§l¡La Ceguera Abisal no puede disiparse con leche!").withStyle(ChatFormatting.DARK_RED));
            }
        }

        // Desierto Abrasador: el efecto de insolación se reinicia al beber agua / pociones o leche
        if (CursosActivos.isDesiertoAbrasador() && (consumed == Items.POTION || consumed == Items.MILK_BUCKET) && self instanceof Player player) {
            Ruleta_Hardcore.reiniciarInsolacion(player.getUUID());
        }
    }

    @Unique
    private static boolean ruleta$esComidaExentaInfestado(Item item) {
        return item == Items.GOLDEN_APPLE
                || item == Items.ENCHANTED_GOLDEN_APPLE
                || item == Items.GOLDEN_CARROT
                || item == Items.COOKED_BEEF
                || item == Items.COOKED_PORKCHOP
                || item == Items.COOKED_CHICKEN
                || item == Items.COOKED_MUTTON
                || item == Items.COOKED_RABBIT
                || item == Items.COOKED_COD
                || item == Items.COOKED_SALMON
                || item == Items.BAKED_POTATO
                || item == Items.DRIED_KELP
                || item == Items.BREAD
                || item == Items.COOKIE
                || item == Items.PUMPKIN_PIE
                || item == Items.MUSHROOM_STEW
                || item == Items.RABBIT_STEW
                || item == Items.BEETROOT_SOUP
                || item == Items.SUSPICIOUS_STEW;
    }

    @Unique
    private static boolean ruleta$esAlimentoCrudo(Item item) {
        return item == Items.BEEF
                || item == Items.PORKCHOP
                || item == Items.CHICKEN
                || item == Items.MUTTON
                || item == Items.RABBIT
                || item == Items.COD
                || item == Items.SALMON
                || item == Items.TROPICAL_FISH
                || item == Items.PUFFERFISH
                || item == Items.ROTTEN_FLESH
                || item == Items.POTATO
                || item == Items.POISONOUS_POTATO;
    }

    @Inject(method = "decreaseAirSupply", at = @At("RETURN"), cancellable = true)
    private void ruleta$sinOxigenoTripleAgotamiento(int currentSupply, CallbackInfoReturnable<Integer> cir) {
        if (!CursosActivos.isSinOxigeno()) {
            return;
        }
        if (!((Object) this instanceof Player)) {
            return;
        }
        int vanillaResult = cir.getReturnValueI();
        int diff = currentSupply - vanillaResult;
        if (diff > 0) {
            cir.setReturnValue(Math.max(-20, currentSupply - diff * 3));
        }
    }

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true)
    private void ruleta$ignorarArmaduraEnderPearl(DamageSource damageSource, float damage,
            CallbackInfoReturnable<Float> cir) {
        if (CursosActivos.isDanoColateral() && damageSource.is(DamageTypes.ENDER_PEARL)) {
            cir.setReturnValue(damage);
        }
        if (CursosActivos.isCaidaBrutal() && (Object) this instanceof Player && damageSource.is(DamageTypeTags.IS_FALL)) {
            cir.setReturnValue(damage);
        }
    }

    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("HEAD"), cancellable = true)
    private void ruleta$ignorarEncantamientosArmaduraEnderPearl(DamageSource damageSource, float damage,
            CallbackInfoReturnable<Float> cir) {
        if ((CursosActivos.isDanoColateral() && damageSource.is(DamageTypes.ENDER_PEARL))
                || (CursosActivos.isCaidaBrutal() && (Object) this instanceof Player && damageSource.is(DamageTypeTags.IS_FALL))) {
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

    @Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
    private void ruleta$bloquearCorrerInventarioPesado(boolean sprinting, CallbackInfo ci) {
        if (sprinting && CursosActivos.isInventarioPesado() && (Object) this instanceof Player player
                && !player.isCreative() && !player.isSpectator() && Ruleta_Hardcore.tieneInventarioLleno(player)) {
            player.setSprinting(false);
            ci.cancel();
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

    // Creepers Escondidos (Nivel 3): no sueltan drops ni experiencia al morir
    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"), cancellable = true)
    private void ruleta$cancelCreeperEscondidoLoot(ServerLevel level, DamageSource damageSource, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.entityTags().contains("creeper_escondido")) {
            ci.cancel();
        }
    }

    @Inject(method = "shouldDropExperience", at = @At("HEAD"), cancellable = true)
    private void ruleta$cancelCreeperEscondidoXp(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.entityTags().contains("creeper_escondido")) {
            cir.setReturnValue(false);
        }
    }

    // Corrupción del Vacío (Nivel 6): anulación de armadura y absorción
    @Inject(method = "getArmorValue", at = @At("HEAD"), cancellable = true)
    private void ruleta$anularArmaduraCorrupcion(CallbackInfoReturnable<Integer> cir) {
        if (CursosActivos.isCorrupcionVacio() && this.ruleta$corrupcionVacioExpiry > ((LivingEntity) (Object) this).level().getGameTime()) {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true)
    private void ruleta$anularAbsorcionCorrupcion(DamageSource damageSource, float damage, CallbackInfoReturnable<Float> cir) {
        if (CursosActivos.isCorrupcionVacio() && this.ruleta$corrupcionVacioExpiry > ((LivingEntity) (Object) this).level().getGameTime()) {
            cir.setReturnValue(damage);
        }
    }

    // Anomalía Gravitacional (Nivel 6): triplica el knockback en el End
    @ModifyVariable(method = "knockback(DDDLnet/minecraft/world/damagesource/DamageSource;FZ)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private double ruleta$triplicarKnockbackEnEnd(double strength) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (CursosActivos.isAnomaliaGravitacional() && self instanceof ServerPlayer
                && self.level().dimension().equals(Level.END)) {
            return strength * 3.0;
        }
        return strength;
    }

    // Anomalía Gravitacional (Nivel 6): saltar en el End aplica caída lenta por 3 segundos
    @Inject(method = "jumpFromGround", at = @At("HEAD"))
    private void ruleta$anomaliaSalto(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (CursosActivos.isAnomaliaGravitacional() && self instanceof ServerPlayer sp
                && sp.level().dimension().equals(Level.END)) {
            sp.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, false, true));
        }
    }
}
