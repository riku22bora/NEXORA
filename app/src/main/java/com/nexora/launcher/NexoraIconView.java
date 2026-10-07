package com.nexora.launcher;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class NexoraIconView extends FrameLayout {

    private final ImageView iconView;

    private int iconSize = 72;
    private int cornerRadius = 20;
    private int backgroundColor = Color.TRANSPARENT;
    private boolean glowEnabled = false;

    public NexoraIconView(Context context) {
        super(context);

        setClipChildren(false);
        setClipToPadding(false);

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
                new GradientDrawable();

        background.setColor(backgroundColor);
        background.setCornerRadius(cornerRadius);

        setBackground(background);
    }

    private void applyGlow() {
        if (glowEnabled) {
            setElevation(8f);
        } else {
            setElevation(0f);
        }
    }
}
