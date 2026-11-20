package com.luishugomod01.client.ritual;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

/**
 * Animaciones de las 4 Alas Celestiales y Halo giratorio para el jugador reviviendo:
 * - DESCEND: aleteo majestuoso continuo mientras el jugador desciende desde la grieta celestial.
 * - LANDING_BRAKE: frenado potente con las alas en el último segundo antes de tocar el suelo
 *                  y apertura solemne mientras las alas se disuelven tras aterrizar.
 */
public final class AngelRevivirAnimations {

    private AngelRevivirAnimations() {}

    public static final AnimationDefinition DESCEND = AnimationDefinition.Builder.withLength(1.6F).looping()
            .addAnimation("halo", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(5.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(5.0F, 180.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(5.0F, 360.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("right_wing_upper", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 18.0F, -20.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, -28.0F, 22.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 18.0F, -20.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("right_wing_upper_mid", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 14.0F, -14.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.28F, KeyframeAnimations.degreeVec(0.0F, 24.0F, -22.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0.0F, -22.0F, 16.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 14.0F, -14.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("right_wing_upper_tip", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 10.0F, -10.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.42F, KeyframeAnimations.degreeVec(0.0F, 26.0F, -18.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(0.0F, -24.0F, 14.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 10.0F, -10.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("left_wing_upper", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -18.0F, 20.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0.0F, 28.0F, -22.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, -18.0F, 20.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("left_wing_upper_mid", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -14.0F, 14.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.28F, KeyframeAnimations.degreeVec(0.0F, -24.0F, 22.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0.0F, 22.0F, -16.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, -14.0F, 14.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("left_wing_upper_tip", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -10.0F, 10.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.42F, KeyframeAnimations.degreeVec(0.0F, -26.0F, 18.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(0.0F, 24.0F, -14.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, -10.0F, 10.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("right_wing_lower", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 10.0F, 12.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.85F, KeyframeAnimations.degreeVec(0.0F, -22.0F, -14.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 10.0F, 12.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("right_wing_lower_tip", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 12.0F, 8.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, -18.0F, -12.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 12.0F, 8.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("left_wing_lower", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -10.0F, -12.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.85F, KeyframeAnimations.degreeVec(0.0F, 22.0F, 14.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, -10.0F, -12.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("left_wing_lower_tip", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -12.0F, -8.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 18.0F, 12.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, -12.0F, -8.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition LANDING_BRAKE = AnimationDefinition.Builder.withLength(2.8F)
            .addAnimation("halo", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.8F, KeyframeAnimations.degreeVec(0.0F, 720.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            // 0.0s .. 0.45s: sube alas para coger impulso; 0.45s .. 1.0s: fuerte aletazo de frenado hacia adelante/abajo; 1.0s .. 2.8s: apertura solemne mientras se disuelven
            .addAnimation("right_wing_upper", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 18.0F, -22.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(0.0F, 34.0F, -32.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0.0F, -32.0F, 16.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.45F, KeyframeAnimations.degreeVec(0.0F, 24.0F, -18.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.8F, KeyframeAnimations.degreeVec(0.0F, 14.0F, -10.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("right_wing_upper_mid", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 12.0F, -14.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 24.0F, -20.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, -24.0F, 14.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, 16.0F, -12.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.8F, KeyframeAnimations.degreeVec(0.0F, 8.0F, -6.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("right_wing_upper_tip", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 8.0F, -8.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, 22.0F, -16.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.05F, KeyframeAnimations.degreeVec(0.0F, -20.0F, 12.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.8F, KeyframeAnimations.degreeVec(0.0F, 6.0F, -4.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("left_wing_upper", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -18.0F, 22.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(0.0F, -34.0F, 32.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0.0F, 32.0F, -16.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.45F, KeyframeAnimations.degreeVec(0.0F, -24.0F, 18.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.8F, KeyframeAnimations.degreeVec(0.0F, -14.0F, 10.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("left_wing_upper_mid", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -12.0F, 14.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, -24.0F, 20.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.0F, 24.0F, -14.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0.0F, -16.0F, 12.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.8F, KeyframeAnimations.degreeVec(0.0F, -8.0F, 6.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("left_wing_upper_tip", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -8.0F, 8.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(0.0F, -22.0F, 16.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.05F, KeyframeAnimations.degreeVec(0.0F, 20.0F, -12.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.8F, KeyframeAnimations.degreeVec(0.0F, -6.0F, 4.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("right_wing_lower", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 10.0F, 10.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0.0F, -26.0F, -14.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, 20.0F, 12.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.8F, KeyframeAnimations.degreeVec(0.0F, 12.0F, 6.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("left_wing_lower", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, -10.0F, -10.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0.0F, 26.0F, 14.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.0F, -20.0F, -12.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.8F, KeyframeAnimations.degreeVec(0.0F, -12.0F, -6.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();
}
