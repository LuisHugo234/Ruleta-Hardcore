package com.luishugomod01.mixin;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.ruleta.CursosActivos;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin {

    @Inject(method = "slotChangedCraftingGrid", at = @At("TAIL"))
    private static void ruleta$bloquearPastillaSinCordura(
            AbstractContainerMenu menu,
            ServerLevel level,
            Player player,
            CraftingContainer craftSlots,
            ResultContainer resultSlots,
            RecipeHolder<CraftingRecipe> recipeHint,
            CallbackInfo ci
    ) {
        if (!CursosActivos.isCordura() && resultSlots.getItem(0).is(Ruleta_Hardcore.PASTILLA_CORDURA)) {
            resultSlots.setItem(0, ItemStack.EMPTY);
            menu.setRemoteSlot(0, ItemStack.EMPTY);
            if (player instanceof ServerPlayer sp) {
                sp.connection.send(new ClientboundContainerSetSlotPacket(
                        menu.containerId,
                        menu.incrementStateId(),
                        0,
                        ItemStack.EMPTY
                ));
            }
        }
    }
}
