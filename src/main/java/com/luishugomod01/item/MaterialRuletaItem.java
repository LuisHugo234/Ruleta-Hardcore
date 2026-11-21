package com.luishugomod01.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;

public class MaterialRuletaItem extends Item {

    private final List<Component> tooltips;

    public MaterialRuletaItem(Properties properties, String... lineasTooltip) {
        super(properties);
        this.tooltips = java.util.Arrays.stream(lineasTooltip)
                .map(s -> (Component) Component.literal(s))
                .toList();
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            TooltipDisplay tooltipDisplay,
            Consumer<Component> tooltipAdder,
            TooltipFlag flag
    ) {
        for (Component linea : this.tooltips) {
            tooltipAdder.accept(linea);
        }
    }
}
