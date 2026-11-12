package com.luishugomod01.client.hombre;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

/**
 * Animaciones exportadas de hombre_roca.bbmodel (idle, walk y throw_rock).
 */
public class HombreRocaAnimations {
    public static final AnimationDefinition IDLE = AnimationDefinition.Builder.withLength(2.4F).looping()
        .addAnimation("torso", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.2F, KeyframeAnimations.degreeVec(-5.0F, 0.0F, 1.5F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(2.4F, KeyframeAnimations.degreeVec(-2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.2F, KeyframeAnimations.degreeVec(-1.0F, -3.0F, -1.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(2.4F, KeyframeAnimations.degreeVec(2.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 12.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.2F, KeyframeAnimations.degreeVec(16.0F, 0.0F, 15.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(2.4F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 12.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("right_forearm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(38.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.2F, KeyframeAnimations.degreeVec(44.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(2.4F, KeyframeAnimations.degreeVec(38.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(6.0F, 0.0F, -8.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.2F, KeyframeAnimations.degreeVec(10.0F, 0.0F, -10.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(2.4F, KeyframeAnimations.degreeVec(6.0F, 0.0F, -8.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("left_forearm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(18.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.2F, KeyframeAnimations.degreeVec(22.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(2.4F, KeyframeAnimations.degreeVec(18.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .build();

    public static final AnimationDefinition WALK = AnimationDefinition.Builder.withLength(1.0F).looping()
        .addAnimation("pelvis", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, 0.8F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.75F, KeyframeAnimations.posVec(0.0F, 0.8F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("torso", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(6.0F, 5.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5F, KeyframeAnimations.degreeVec(6.0F, -5.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0F, KeyframeAnimations.degreeVec(6.0F, 5.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("right_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(26.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5F, KeyframeAnimations.degreeVec(-26.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0F, KeyframeAnimations.degreeVec(26.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("right_shin", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.25F, KeyframeAnimations.degreeVec(-35.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.75F, KeyframeAnimations.degreeVec(-28.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("left_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-26.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5F, KeyframeAnimations.degreeVec(26.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0F, KeyframeAnimations.degreeVec(-26.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("left_shin", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.25F, KeyframeAnimations.degreeVec(-28.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5F, KeyframeAnimations.degreeVec(-12.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.75F, KeyframeAnimations.degreeVec(-35.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0F, KeyframeAnimations.degreeVec(-8.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(16.0F, 0.0F, 14.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 12.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0F, KeyframeAnimations.degreeVec(16.0F, 0.0F, 14.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("right_forearm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(42.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5F, KeyframeAnimations.degreeVec(36.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0F, KeyframeAnimations.degreeVec(42.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(20.0F, 0.0F, -8.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, -8.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0F, KeyframeAnimations.degreeVec(20.0F, 0.0F, -8.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("left_forearm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(26.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.5F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.0F, KeyframeAnimations.degreeVec(26.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .build();

    public static final AnimationDefinition THROW_ROCK = AnimationDefinition.Builder.withLength(1.35F)
        .addAnimation("pelvis", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.4F, KeyframeAnimations.posVec(0.0F, -1.2F, 2.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.68F, KeyframeAnimations.posVec(0.0F, -1.6F, -3.2F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.95F, KeyframeAnimations.posVec(0.0F, -0.6F, -1.2F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.35F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("torso", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.4F, KeyframeAnimations.degreeVec(-14.0F, 26.0F, -6.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.68F, KeyframeAnimations.degreeVec(22.0F, -24.0F, 6.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.95F, KeyframeAnimations.degreeVec(10.0F, -12.0F, 3.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.4F, KeyframeAnimations.degreeVec(10.0F, -24.0F, 4.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.68F, KeyframeAnimations.degreeVec(-16.0F, 22.0F, -4.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("right_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 12.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.4F, KeyframeAnimations.degreeVec(-95.0F, -18.0F, 42.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.56F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 48.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.68F, KeyframeAnimations.degreeVec(86.0F, 16.0F, 14.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.84F, KeyframeAnimations.degreeVec(54.0F, 18.0F, 4.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.35F, KeyframeAnimations.degreeVec(12.0F, 0.0F, 12.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("right_forearm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(38.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.4F, KeyframeAnimations.degreeVec(60.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.68F, KeyframeAnimations.degreeVec(10.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.88F, KeyframeAnimations.degreeVec(22.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.35F, KeyframeAnimations.degreeVec(38.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("left_arm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(6.0F, 0.0F, -8.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.4F, KeyframeAnimations.degreeVec(58.0F, 12.0F, -26.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.68F, KeyframeAnimations.degreeVec(-34.0F, -12.0F, -20.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.0F, KeyframeAnimations.degreeVec(4.0F, 0.0F, -10.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.35F, KeyframeAnimations.degreeVec(6.0F, 0.0F, -8.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("left_forearm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(18.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.4F, KeyframeAnimations.degreeVec(22.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.68F, KeyframeAnimations.degreeVec(32.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.35F, KeyframeAnimations.degreeVec(18.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("left_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.4F, KeyframeAnimations.degreeVec(26.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.68F, KeyframeAnimations.degreeVec(34.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("left_shin", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.4F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.68F, KeyframeAnimations.degreeVec(-28.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("right_leg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.4F, KeyframeAnimations.degreeVec(-24.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.68F, KeyframeAnimations.degreeVec(-32.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("right_shin", new AnimationChannel(AnimationChannel.Targets.ROTATION,
            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.4F, KeyframeAnimations.degreeVec(-16.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(0.68F, KeyframeAnimations.degreeVec(-14.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
            new Keyframe(1.35F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .addAnimation("rock", new AnimationChannel(AnimationChannel.Targets.POSITION,
            new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.66F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.84F, KeyframeAnimations.posVec(0.0F, 4.0F, -30.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.88F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.35F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
        ))
        .addAnimation("rock", new AnimationChannel(AnimationChannel.Targets.SCALE,
            new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.66F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.84F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.12F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.35F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.CATMULLROM)
        ))
        .build();

}
