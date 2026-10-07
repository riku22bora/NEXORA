package com.nexora.launcher;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

public class NexoraAnimatedTextView extends TextView {

    public enum AnimationMode {
        STATIC,
        SHIMMER,
        FLASH,
        HEARTBEAT,
        LIQUID,
        RAINBOW,
        COLOR_FLOW
    }

    private AnimationMode animationMode = AnimationMode.LIQUID;

    private int[] gradientColors = {
            Color.rgb(255, 40, 120),
            Color.rgb(255, 120, 30),
            Color.rgb(255, 240, 40),
            Color.rgb(60, 255, 150),
            Color.rgb(30, 220, 255),
            Color.rgb(80, 100, 255),
            Color.rgb(190, 60, 255),
            Color.rgb(255, 60, 220),
            Color.WHITE,
            Color.rgb(255, 40, 120)
    };

    private int solidColor = Color.WHITE;
    private int glowColor = Color.rgb(30, 220, 255);

    private float glowRadius = 18f;
    private long animationSpeed = 3000L;

    private ValueAnimator animator;
    private LinearGradient gradient;
    private final Matrix matrix = new Matrix();

    public NexoraAnimatedTextView(Context context) {
        super(context);
        setup();
    }

    public NexoraAnimatedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public NexoraAnimatedTextView(
            Context context,
            AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        setGravity(Gravity.CENTER);
        setIncludeFontPadding(false);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setTextColor(solidColor);
    }

    public static int[] colorsNeon() {
        return new int[] {
                Color.rgb(0, 255, 255),
                Color.rgb(80, 120, 255),
                Color.rgb(190, 60, 255),
                Color.rgb(255, 40, 180)
        };
    }

    public static int[] colorsFire() {
        return new int[] {
                Color.rgb(255, 40, 40),
                Color.rgb(255, 100, 20),
                Color.rgb(255, 220, 40),
                Color.rgb(255, 255, 255)
        };
    }

    public static int[] colorsOcean() {
        return new int[] {
                Color.rgb(0, 100, 255),
                Color.rgb(0, 220, 255),
                Color.rgb(60, 255, 200),
                Color.WHITE
        };
    }

    public static int[] colorsGalaxy() {
        return new int[] {
                Color.rgb(80, 40, 255),
                Color.rgb(180, 50, 255),
                Color.rgb(255, 60, 220),
                Color.rgb(80, 120, 255)
        };
    }

    public static int[] colorsRainbow() {
        return new int[] {
                Color.RED,
                Color.rgb(255, 140, 0),
                Color.YELLOW,
                Color.GREEN,
                Color.CYAN,
                Color.BLUE,
                Color.MAGENTA,
                Color.RED
        };
    }

    public static int[] colorsIce() {
        return new int[] {
                Color.WHITE,
                Color.rgb(150, 220, 255),
                Color.rgb(30, 150, 255),
                Color.rgb(100, 80, 255),
                Color.WHITE
        };
    }

    public static int[] colorsPink() {
        return new int[] {
                Color.rgb(255, 80, 160),
                Color.rgb(255, 40, 220),
                Color.rgb(180, 60, 255),
                Color.WHITE
        };
    }

    public static int[] colorsGreen() {
        return new int[] {
                Color.rgb(40, 255, 100),
                Color.rgb(0, 220, 180),
                Color.rgb(0, 255, 255),
                Color.WHITE
        };
    }

    public void applyPreset(
            int[] colors,
            int glow,
            float glowSize,
            AnimationMode mode,
            long speed
    ) {
        setGradientColors(colors);
        setGlowColor(glow);
        setGlowRadius(glowSize);
        setAnimationSpeed(speed);
        setAnimationMode(mode);
    }

    public void presetNeonLiquid() {
        applyPreset(
                colorsNeon(),
                Color.rgb(0, 255, 255),
                20f,
                AnimationMode.LIQUID,
                3000L
        );
    }

    public void presetRainbowShimmer() {
        applyPreset(
                colorsRainbow(),
                Color.WHITE,
                18f,
                AnimationMode.SHIMMER,
                2200L
        );
    }

    public void presetFireFlash() {
        applyPreset(
                colorsFire(),
                Color.rgb(255, 70, 20),
                22f,
                AnimationMode.FLASH,
                900L
        );
    }

    public void presetGalaxyHeartbeat() {
        applyPreset(
                colorsGalaxy(),
                Color.rgb(190, 60, 255),
                24f,
                AnimationMode.HEARTBEAT,
                1200L
        );
    }

    public void presetOceanFlow() {
        applyPreset(
                colorsOcean(),
                Color.rgb(0, 220, 255),
                20f,
                AnimationMode.COLOR_FLOW,
                2800L
        );
    }

    public void presetIceShimmer() {
        applyPreset(
                colorsIce(),
                Color.rgb(120, 220, 255),
                16f,
                AnimationMode.SHIMMER,
                2600L
        );
    }

    public void presetPinkLiquid() {
        applyPreset(
                colorsPink(),
                Color.rgb(255, 60, 200),
                22f,
                AnimationMode.LIQUID,
                2500L
        );
    }

    public void presetGreenFlow() {
        applyPreset(
                colorsGreen(),
                Color.rgb(40, 255, 140),
                20f,
                AnimationMode.COLOR_FLOW,
                2400L
        );
    }

    public void presetStatic(int color, int glow) {
        setSolidColor(color);
        setGlowColor(glow);
        setAnimationMode(AnimationMode.STATIC);
    }

    public void setAnimationMode(AnimationMode mode) {
        animationMode = mode == null
                ? AnimationMode.STATIC
                : mode;

        restartAnimation();
    }

    public AnimationMode getAnimationMode() {
        return animationMode;
    }

    public void setSolidColor(int color) {
        solidColor = color;

        if (animationMode == AnimationMode.STATIC) {
            setTextColor(solidColor);
        }
    }

    public int getSolidColor() {
        return solidColor;
    }

    public void setGradientColors(int[] colors) {
        if (colors == null || colors.length == 0) {
            return;
        }

        gradientColors = colors;
        restartAnimation();
    }

    public int[] getGradientColors() {
        return gradientColors;
    }

    public void setGlowColor(int color) {
        glowColor = color;
        applyGlow();
    }

    public int getGlowColor() {
        return glowColor;
    }

    public void setGlowRadius(float radius) {
        glowRadius = Math.max(0f, radius);
        applyGlow();
    }

    public float getGlowRadius() {
        return glowRadius;
    }

    public void setAnimationSpeed(long speed) {
        animationSpeed = Math.max(200L, speed);
        restartAnimation();
    }

    public long getAnimationSpeed() {
        return animationSpeed;
    }

    public void startAnimation() {
        stopAnimation();

        if (animationMode == AnimationMode.STATIC) {
            getPaint().setShader(null);
            setTextColor(solidColor);
            setAlpha(1f);
            setScaleX(1f);
            setScaleY(1f);
            applyGlow();
            invalidate();
            return;
        }

        gradient = new LinearGradient(
                0,
                0,
                500,
                0,
                gradientColors,
                null,
                Shader.TileMode.CLAMP
        );

        getPaint().setShader(gradient);

        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(animationSpeed);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {

                        float progress =
                                (float) valueAnimator.getAnimatedValue();

                        applyAnimation(progress);
                        invalidate();
                    }
                }
        );

        animator.start();
    }

    public void stopAnimation() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }

        getPaint().setShader(null);
        setAlpha(1f);
        setScaleX(1f);
        setScaleY(1f);
    }

    public void restartAnimation() {
        if (isAttachedToWindow()) {
            startAnimation();
        }
    }

    private void applyAnimation(float progress) {

        switch (animationMode) {

            case SHIMMER:
                applyShimmer(progress);
                break;

            case FLASH:
                applyFlash(progress);
                break;

            case HEARTBEAT:
                applyHeartbeat(progress);
                break;

            case LIQUID:
                applyLiquid(progress);
                break;

            case RAINBOW:
                applyRainbow(progress);
                break;

            case COLOR_FLOW:
                applyColorFlow(progress);
                break;

            case STATIC:
            default:
                break;
        }
    }

    private void applyShimmer(float progress) {

        float shift = progress * 700f;

        matrix.setTranslate(shift, 0);
        gradient.setLocalMatrix(matrix);

        setAlpha(
                0.78f
                        + (float)
                        (Math.sin(progress * Math.PI * 2) * 0.22f)
        );

        applyGlow();
    }

    private void applyFlash(float progress) {

        float flash =
                (float)
                        ((Math.sin(progress * Math.PI * 4) + 1f) / 2f);

        setAlpha(0.45f + (flash * 0.55f));

        applyGlow();
    }

    private void applyHeartbeat(float progress) {

        float beat = (float) Math.sin(progress * Math.PI * 4);

        float scale = 1f + Math.max(0f, beat) * 0.08f;

        setScaleX(scale);
        setScaleY(scale);

        applyGlow();
    }

    private void applyLiquid(float progress) {

        float shift = progress * 900f;

        matrix.setTranslate(shift, 0);
        gradient.setLocalMatrix(matrix);

        float wave =
                (float) Math.sin(progress * Math.PI * 6);

        setScaleX(1f + wave * 0.025f);
        setScaleY(1f - wave * 0.025f);

        setAlpha(0.88f + Math.abs(wave) * 0.12f);

        setShadowLayer(
                glowRadius + Math.abs(wave) * 8f,
                0,
                0,
                glowColor
        );
    }

    private void applyRainbow(float progress) {

        float shift = progress * 1200f;

        matrix.setTranslate(shift, 0);
        gradient.setLocalMatrix(matrix);

        setAlpha(1f);

        applyGlow();
    }

    private void applyColorFlow(float progress) {

        float shift = progress * 1500f;

        matrix.setTranslate(shift, 0);
        gradient.setLocalMatrix(matrix);

        float pulse =
                1f
                        + (float)
                        (Math.sin(progress * Math.PI * 2) * 0.025f);

        setScaleX(pulse);
        setScaleY(pulse);

        applyGlow();
    }

    private void applyGlow() {

        Paint paint = getPaint();

        paint.setShadowLayer(
                glowRadius,
                0,
                0,
                glowColor
        );
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimation();
        super.onDetachedFromWindow();
    }
}
