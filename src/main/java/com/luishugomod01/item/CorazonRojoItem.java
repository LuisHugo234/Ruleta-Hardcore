package com.luishugomod01.item;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.ruleta.CursosActivos;
import com.luishugomod01.vidas.VidasJugadorAccessor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class CorazonRojoItem extends Item {

    public CorazonRojoItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayer sp) || !(level instanceof ServerLevel sLvl)) {
            return InteractionResult.PASS;
        }
        if (!(sp instanceof VidasJugadorAccessor acc)) {
            return InteractionResult.FAIL;
        }

        int limiteServidor = CursosActivos.getVidasIniciales();
        int vidasActuales = acc.ruleta$getVidas();

        if (vidasActuales >= limiteServidor) {
            sp.sendOverlayMessage(Component.literal(
                    "§c♥ Ya tienes el máximo de vidas permitidas (§f" + vidasActuales + "/" + limiteServidor + "§c)."));
            return InteractionResult.FAIL;
        }

        int nuevasVidas = Math.min(limiteServidor, vidasActuales + 1);
        acc.ruleta$setVidas(nuevasVidas);
        acc.ruleta$setVidasInicializadas(true);
        Ruleta_Hardcore.sincronizarVidasJugador(sp);

        stack.consume(1, sp);
        sp.getCooldowns().addCooldown(stack, 20);

        sLvl.playSound(null, sp.getX(), sp.getY(), sp.getZ(),
                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.25F);
        sLvl.playSound(null, sp.getX(), sp.getY(), sp.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.1F, 1.15F);
        sLvl.sendParticles(ParticleTypes.HEART,
                sp.getX(), sp.getY() + 1.1, sp.getZ(), 14, 0.45, 0.55, 0.45, 0.04);

        sp.sendOverlayMessage(Component.literal(
                "§c♥ §f¡Has recuperado §c§l+1 vida permanente§f! (§c" + nuevasVidas + "/" + limiteServidor + " ♥§f)"));
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            TooltipDisplay tooltipDisplay,
            Consumer<Component> tooltipAdder,
            TooltipFlag flag
    ) {
        tooltipAdder.accept(Component.literal("§cClic derecho: §fRecupera §c+1 vida permanente§f."));
        tooltipAdder.accept(Component.literal("§7No supera el límite máximo del servidor."));
        tooltipAdder.accept(Component.literal("§8También sirve para forjar el §6Corazón Dorado§8."));
    }
}
