package com.luishugomod01.mixin;

import com.luishugomod01.ruleta.CursosActivos;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    @Unique
    private boolean ruleta$hadAnvilBeforeTake = false;

    public AnvilMenuMixin(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {
        super(menuType, containerId, inventory, access, slotDefinition);
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void ruleta$verificarYunqueAntes(Player player, ItemStack stack, CallbackInfo ci) {
        this.ruleta$hadAnvilBeforeTake = false;
        if (!CursosActivos.isMaldicionYunque()) return;
        this.access.execute((level, pos) -> {
            if (!level.isClientSide() && level.getBlockState(pos).is(BlockTags.ANVIL)) {
                this.ruleta$hadAnvilBeforeTake = true;
            }
        });
    }

    @Inject(method = "onTake", at = @At("TAIL"))
    private void ruleta$explosionAlRomperseYunque(Player player, ItemStack stack, CallbackInfo ci) {
        if (!this.ruleta$hadAnvilBeforeTake || !CursosActivos.isMaldicionYunque()) return;
        this.ruleta$hadAnvilBeforeTake = false;
        this.access.execute((level, pos) -> {
            if (level instanceof ServerLevel serverLevel && !serverLevel.getBlockState(pos).is(BlockTags.ANVIL)) {
                if (ThreadLocalRandom.current().nextFloat() < 0.10f) {
                    ejecutarExplosionYunque(serverLevel, pos);
                }
            }
        });
    }

    @Unique
    private static void ejecutarExplosionYunque(ServerLevel level, BlockPos pos) {
        level.explode(
                null,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                2.5f,
                false,
                Level.ExplosionInteraction.MOB
        );
    }
}
