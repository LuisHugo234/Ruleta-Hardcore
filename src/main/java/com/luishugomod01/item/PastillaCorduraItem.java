package com.luishugomod01.item;

import com.luishugomod01.Ruleta_Hardcore;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class PastillaCorduraItem extends Item {

    public PastillaCorduraItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 16; // 0.8 segundos: consumo rápido tipo pastilla
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.EAT;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide() && entity instanceof ServerPlayer sp && level instanceof ServerLevel sLvl) {
            Ruleta_Hardcore.restablecerCordura(sp, 100);
            sLvl.playSound(null, sp.getX(), sp.getY(), sp.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.2F, 1.35F);
            sLvl.playSound(null, sp.getX(), sp.getY(), sp.getZ(),
                    SoundEvents.GENERIC_EAT.value(), SoundSource.PLAYERS, 0.9F, 1.2F);
            sLvl.sendParticles(ParticleTypes.END_ROD,
                    sp.getX(), sp.getY() + 1.1, sp.getZ(), 14, 0.35, 0.45, 0.35, 0.04);
            sp.sendOverlayMessage(Component.literal("§d✦ Tu mente se despeja... Cordura restaurada al §f§l100%§d."));
            stack.consume(1, sp);
        }
        return stack;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            TooltipDisplay tooltipDisplay,
            Consumer<Component> tooltipAdder,
            TooltipFlag flag
    ) {
        tooltipAdder.accept(Component.literal("§dRestaura tu cordura al §f§l100%§d."));
        tooltipAdder.accept(Component.literal("§8§o\"Silencia las voces... al menos por hoy.\""));
    }
}
