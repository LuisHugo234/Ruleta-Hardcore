package com.luishugomod01.item;

import com.luishugomod01.Ruleta_Hardcore;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class CorazonDoradoItem extends Item {

    public CorazonDoradoItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayer sp)) {
            return InteractionResult.PASS;
        }
        boolean ok = Ruleta_Hardcore.intentarAbrirMenuRevivir(sp, hand == InteractionHand.MAIN_HAND);
        return ok ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            TooltipDisplay tooltipDisplay,
            Consumer<Component> tooltipAdder,
            TooltipFlag flag
    ) {
        tooltipAdder.accept(Component.literal("§6Al ser consumido, devuelve el alma al jugador eliminado que elijas (§e1 ♥§6)."));
        tooltipAdder.accept(Component.literal("§7Solo puede usarse al aire libre en la superficie del Overworld."));
    }
}
