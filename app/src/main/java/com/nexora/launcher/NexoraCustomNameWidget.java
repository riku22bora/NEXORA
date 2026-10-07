package com.nexora.launcher;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class NexoraCustomNameWidget extends FrameLayout {

    public interface OnLongPressListener {
        void onLongPress(NexoraCustomNameWidget widget);
    }

    public interface OnPositionChangedListener {
        void onPositionChanged(
                NexoraCustomNameWidget widget
        );
    }

    private final NexoraAnimatedTextView textView;

    private int color1 = Color.WHITE;
    private int color2 = Color.CYAN;
    private int glowColor = Color.CYAN;
    private float glowRadius = 16f;
    private long animationSpeed = 3000L;

    private NexoraAnimatedTextView.AnimationMode animationMode =
            NexoraAnimatedTextView.AnimationMode.LIQUID;

    private float downX;
    private float downY;
    private float startX;
    private float startY;

    private boolean dragging;
    private boolean longPressed;

    private final Runnable longPressRunnable =
            new Runnable() {
                @Override
                public void run() {
                    if (!dragging) {
                        longPressed = true;

                        if (longPressListener != null) {
                            longPressListener.onLongPress(
                                    NexoraCustomNameWidget.this
                            );
                        }
                    }
                }
            };

    private OnLongPressListener longPressListener;

    private OnPositionChangedListener positionChangedListener;

    public NexoraCustomNameWidget(Context context) {
        super(context);

        setClipChildren(false);
        setClipToPadding(false);

        setClickable(true);
        setFocusable(true);

        textView = new NexoraAnimatedTextView(context);

        textView.setText("RIKU");

        textView.setTextSize(28);

        textView.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        textView.setGravity(
                Gravity.CENTER
        );

        textView.setLetterSpacing(
                0.12f
        );

        applyTextStyle();

        addView(
                textView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        setOnTouchListener(
                (v, event) -> {

                    switch (event.getActionMasked()) {

                        case MotionEvent.ACTION_DOWN:

                            downX = event.getRawX();
                            downY = event.getRawY();

                            startX = getX();
                            startY = getY();

                            dragging = false;
                            longPressed = false;

                            postDelayed(
                                    longPressRunnable,
                                    600L
                            );

                            return true;

                        case MotionEvent.ACTION_MOVE:

                            float dx =
                                    event.getRawX() - downX;

                            float dy =
                                    event.getRawY() - downY;

                            if (
                                    Math.abs(dx) > 12f
                                            || Math.abs(dy) > 12f
                            ) {
                                dragging = true;

                                removeCallbacks(
                                        longPressRunnable
                                );
                            }

                            if (dragging) {
                                setX(startX + dx);
                                setY(startY + dy);
                            }

                            return true;

                        case MotionEvent.ACTION_UP:

                            removeCallbacks(
                                    longPressRunnable
                            );

                            if (dragging) {
                                if (positionChangedListener != null) {
                                    positionChangedListener.onPositionChanged(
                                            NexoraCustomNameWidget.this
                                    );
                                }
                            } else if (!longPressed) {
                                performClick();
                            }

                            return true;

                        case MotionEvent.ACTION_CANCEL:

                            removeCallbacks(
                                    longPressRunnable
                            );

                            return true;
                    }

                    return false;
                }
        );
    }

    private void applyTextStyle() {

        textView.setSolidColor(
                color1
        );

        textView.setGradientColors(
                new int[]{
                        color1,
                        color2,
                        color1
                }
        );

        textView.setGlowColor(
                glowColor
        );

        textView.setGlowRadius(
                glowRadius
        );

        textView.setAnimationSpeed(
                animationSpeed
        );

        textView.setAnimationMode(
                animationMode
        );
    }

    public void setOnLongPressListener(
            OnLongPressListener listener
    ) {
        longPressListener = listener;
    }

    public void setOnPositionChangedListener(
            OnPositionChangedListener listener
    ) {
        positionChangedListener = listener;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    public void setName(String name) {
        textView.setText(name);
    }

    public String getName() {
        return textView.getText().toString();
    }

    public void setColor1(int color) {
        color1 = color;
        applyTextStyle();
    }

    public int getColor1() {
        return color1;
    }

    public void setColor2(int color) {
        color2 = color;
        applyTextStyle();
    }

    public int getColor2() {
        return color2;
    }

    public void setGlow(int color, float radius) {
        glowColor = color;
        glowRadius = radius;
        applyTextStyle();
    }

    public int getGlowColor() {
        return glowColor;
    }

    public float getGlowRadius() {
        return glowRadius;
    }

    public void setSpeed(long speed) {
        animationSpeed = Math.max(
                100L,
                speed
        );

        textView.setAnimationSpeed(
                animationSpeed
        );
    }

    public long getSpeed() {
        return animationSpeed;
    }

    public void setAnimation(
            NexoraAnimatedTextView.AnimationMode mode
    ) {
        if (mode == null) {
            return;
        }

        animationMode = mode;

        textView.setAnimationMode(
                animationMode
        );
    }

    public NexoraAnimatedTextView.AnimationMode
    getNameAnimation() {
        return animationMode;
    }
}
