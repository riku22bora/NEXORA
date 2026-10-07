package com.nexora.launcher;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;

public class NexoraClockView extends NexoraAnimatedTextView {

    public NexoraClockView(Context context) {
        super(context);

        setGravity(Gravity.CENTER);
        setTextSize(54);

        Typeface futuristic =
                Typeface.create(
                        "sans-serif-light",
                        Typeface.NORMAL
                );

        setTypeface(futuristic);
        setLetterSpacing(0.12f);

        setSolidColor(Color.rgb(205, 205, 205));

        setGradientColors(
                new int[]{
                        Color.rgb(105, 105, 105),
                        Color.rgb(225, 225, 225),
                        Color.WHITE,
                        Color.rgb(225, 225, 225),
                        Color.rgb(105, 105, 105)
                }
        );

        setGlowColor(
                Color.rgb(150, 150, 150)
        );

        setGlowRadius(8f);
        setAnimationSpeed(5200L);

        setAnimationMode(
                NexoraAnimatedTextView.AnimationMode.SHIMMER
        );
    }
}
