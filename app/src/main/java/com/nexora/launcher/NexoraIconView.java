package com.nexora.launcher;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class NexoraIconView extends FrameLayout {

    private final ImageView iconView;

    private int iconSize = 72;
    private int cornerRadius = 22;
    private int backgroundColor = Color.TRANSPARENT;
    private boolean glowEnabled = false;

    public NexoraIconView(Context context) {
        super(context);

        setClipChildren(false);
        setClipToPadding(false);

        setClickable(true);
        setFocusable(true);

        iconView = new ImageView(context);
        iconView.setScaleType(
                ImageView.ScaleType.FIT_CENTER
        );

        addView(
                iconView,
                new FrameLayout.LayoutParams(
                        iconSize,
                        iconSize,
                        Gravity.CENTER
                )
        );

        setOnTouchListener(
                (v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        animatePress(0.90f);
                    } else if (
                            event.getAction() == MotionEvent.ACTION_UP
                                    || event.getAction() == MotionEvent.ACTION_CANCEL
                    ) {
                        animatePress(1.0f);
                    }

                    return false;
                }
        );
    }

    public void setIcon(Drawable drawable) {
        iconView.setImageDrawable(drawable);
    }

    public Drawable getIcon() {
        return iconView.getDrawable();
    }

    public void setIconSize(int size) {
        iconSize = Math.max(48, size);

        FrameLayout.LayoutParams params =
                (FrameLayout.LayoutParams)
                        iconView.getLayoutParams();

        params.width = iconSize;
        params.height = iconSize;
        params.gravity = Gravity.CENTER;

        iconView.setLayoutParams(params);
    }

    public int getIconSize() {
        return iconSize;
    }

    public void setBackgroundColorValue(int color) {
        backgroundColor = color;
        applyBackground();
    }

    public int getBackgroundColorValue() {
        return backgroundColor;
    }

    public void setCornerRadius(int radius) {
        cornerRadius = Math.max(0, radius);
        applyBackground();
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setGlowEnabled(boolean enabled) {
        glowEnabled = enabled;
        applyGlow();
    }

    public boolean isGlowEnabled() {
        return glowEnabled;
    }

    private void applyBackground() {
        if (backgroundColor == Color.TRANSPARENT) {
            setBackground(null);
            return;
        }

        GradientDrawable background =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                Color.argb(105, 255, 255, 255),
                                backgroundColor,
                                Color.argb(35, 255, 255, 255)
                        }
                );

        background.setCornerRadius(cornerRadius);
        background.setStroke(
                2,
                Color.argb(145, 255, 255, 255)
        );

        setBackground(background);
    }

    private void applyGlow() {
        if (glowEnabled) {
            setElevation(12f);
            setTranslationZ(4f);
        } else {
            setElevation(0f);
            setTranslationZ(0f);
        }
    }

    private void animatePress(float scale) {
        ObjectAnimator scaleX =
                ObjectAnimator.ofFloat(
                        this,
                        "scaleX",
                        scale
                );

        ObjectAnimator scaleY =
                ObjectAnimator.ofFloat(
                        this,
                        "scaleY",
                        scale
                );

        ObjectAnimator translationZ =
                ObjectAnimator.ofFloat(
                        this,
                        "translationZ",
                        scale < 1.0f ? 10f : 4f
                );

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                scaleX,
                scaleY,
                translationZ
        );
        set.setDuration(130L);
        set.start();
    }
}
