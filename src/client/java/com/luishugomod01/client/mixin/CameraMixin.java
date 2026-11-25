package com.luishugomod01.client.mixin;

import com.luishugomod01.client.frogzilla.RanaCamaraClientState;
import com.luishugomod01.client.ritual.ReviveRitualClientManager;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    @Final
    private static Vector3fc UP;

    @Shadow
    @Final
    private static Vector3fc LEFT;

    @Shadow
    @Final
    private Quaternionf rotation;

    @Shadow
    @Final
    private Vector3f up;

    @Shadow
    @Final
    private Vector3f left;

    @Shadow
    private boolean detached;

    @Shadow
    private float xRot;

    @Shadow
    private float yRot;

    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    @Shadow
    protected abstract void setPosition(net.minecraft.world.phys.Vec3 pos);

    @Inject(method = "alignWithEntity", at = @At("TAIL"))
    private void ruleta$aplicarCamaraCinematicaYShake(float partialTick, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = (mc != null) ? mc.player : null;
        float shakeYaw = ReviveRitualClientManager.getShakeYawOffset();
        float shakePitch = ReviveRitualClientManager.getShakePitchOffset();

        if (player != null) {
            ReviveRitualClientManager.CameraPose cine = ReviveRitualClientManager.computeCinematicCamera(player, partialTick);
            if (cine != null) {
                double distSq = cine.position().distanceToSqr(player.getEyePosition(partialTick));
                this.detached = distSq > 0.55;
                this.setPosition(cine.position());
                this.setRotation(
                        cine.yRot() + shakeYaw,
                        Mth.clamp(cine.xRot() + shakePitch, -89.0f, 89.0f)
                );
                return;
            }
        }

        if (Math.abs(shakeYaw) > 0.001f || Math.abs(shakePitch) > 0.001f) {
            this.setRotation(
                    this.yRot + shakeYaw,
                    Mth.clamp(this.xRot + shakePitch, -89.0f, 89.0f)
            );
        }

        if (RanaCamaraClientState.hasActiveRoll()) {
            float rollDeg = RanaCamaraClientState.getInterpolatedRollDegrees(partialTick);
            if (Math.abs(rollDeg) > 0.01f) {
                this.rotation.rotateZ((float) Math.toRadians(rollDeg));
                UP.rotate(this.rotation, this.up);
                LEFT.rotate(this.rotation, this.left);
            }
        }
    }

    @Inject(method = "calculateFov", at = @At("RETURN"), cancellable = true)
    private void ruleta$aplicarFovCinematico(float partialTick, CallbackInfoReturnable<Float> cir) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = (mc != null) ? mc.player : null;
        if (player == null) {
            return;
        }
        ReviveRitualClientManager.CameraPose cine = ReviveRitualClientManager.computeCinematicCamera(player, partialTick);
        if (cine != null && Math.abs(cine.fovDelta()) > 0.01f) {
            float base = cir.getReturnValue();
            cir.setReturnValue(Mth.clamp(base + cine.fovDelta(), 38.0f, 110.0f));
        }
    }
}
