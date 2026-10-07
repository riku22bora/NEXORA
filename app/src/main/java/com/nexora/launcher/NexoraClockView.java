package com.nexora.launcher;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;

public class NexoraClockView extends NexoraAnimatedTextView {

    public NexoraClockView(Context context) {
        super(context);

        setGravity(Gravity.CENTER);
        setTextColor(Color.WHITE);
        setTextSize(52);
        setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        setLetterSpacing(0.08f);

        setSolidColor(Color.WHITE);
        setGlowColor(Color.rgb(0, 255, 255));
        setGlowRadius(24f);
        setAnimationSpeed(2600L);

        setAnimationMode(
                NexoraAnimatedTextView.AnimationMode.SHIMMER
        );
    }
}
