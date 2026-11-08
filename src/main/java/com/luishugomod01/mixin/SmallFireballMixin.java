package com.luishugomod01.mixin;

import com.luishugomod01.Ruleta_Hardcore;
import com.luishugomod01.ruleta.CursosActivos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(SmallFireball.class)
public abstract class SmallFireballMixin {

    @Inject(method = "onHit", at = @At("HEAD"))
    private void ruleta$torbellinoIgneo(HitResult hitResult, CallbackInfo ci) {
        SmallFireball self = (SmallFireball) (Object) this;
        if (!CursosActivos.isTorbellinoIgneo()) return;
        if (!(self.level() instanceof ServerLevel serverLevel)) return;
        if (!(self.getOwner() instanceof Blaze)) return;

        Vec3 hitPos = hitResult.getLocation();
        double hx = hitPos.x();
        double hy = hitPos.y();
        double hz = hitPos.z();

        // Empujar fuertemente al jugador hacia atrás si está cerca (radio 6 bloques)
        List<Player> nearbyPlayers = serverLevel.getEntitiesOfClass(
                Player.class,
                self.getBoundingBox().inflate(6.0),
                p -> p.isAlive() && !p.isCreative() && !p.isSpectator()
        );
        for (Player p : nearbyPlayers) {
            double dx = p.getX() - hx;
            double dz = p.getZ() - hz;
            p.knockback(1.35, -dx, -dz, serverLevel.damageSources().generic(), 1.0f);
        }

        // Crear charco de fuego y lava temporal de 3x3 bloques (desaparece a los 8 segundos / 160 ticks)
        BlockPos center = BlockPos.containing(hx, hy, hz);
        record SavedBlock(BlockPos pos, BlockState state) {}
        List<SavedBlock> savedBlocks = new ArrayList<>(9);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos colPos = center.offset(dx, 0, dz);
                BlockPos targetPos = colPos;
                if (!serverLevel.getBlockState(targetPos).isAir()) {
                    targetPos = colPos.above();
                } else if (serverLevel.getBlockState(colPos.below()).isAir()) {
                    targetPos = colPos.below();
                }

                if (serverLevel.isLoaded(targetPos)) {
                    BlockState current = serverLevel.getBlockState(targetPos);
                    if (current.isAir() || current.is(Blocks.FIRE) || current.canBeReplaced()) {
                        savedBlocks.add(new SavedBlock(targetPos, current));
                        if (dx == 0 && dz == 0) {
                            serverLevel.setBlock(targetPos, Blocks.LAVA.defaultBlockState(), 3);
                        } else {
                            serverLevel.setBlock(targetPos, Blocks.FIRE.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }

        serverLevel.sendParticles(ParticleTypes.LAVA, hx, hy + 0.5, hz, 25, 0.5, 0.5, 0.5, 0.1);
        serverLevel.playSound(null, hx, hy, hz, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.5f, 0.8f);

        if (!savedBlocks.isEmpty() && serverLevel.getServer() != null) {
            long revertTick = serverLevel.getServer().getTickCount() + 160L;
            Ruleta_Hardcore.programarTarea(revertTick, () -> {
                for (SavedBlock sb : savedBlocks) {
                    if (serverLevel.isLoaded(sb.pos())) {
                        BlockState nowState = serverLevel.getBlockState(sb.pos());
                        if (nowState.is(Blocks.LAVA) || nowState.is(Blocks.FIRE) || nowState.is(Blocks.OBSIDIAN) || nowState.is(Blocks.COBBLESTONE)) {
                            serverLevel.setBlock(sb.pos(), sb.state(), 3);
                        }
                    }
                }
            });
        }
    }
}
