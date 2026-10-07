package com.nexora.launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class NexoraLiquidBackgroundView extends View {

    private final Paint glassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dropPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final ArrayList<Drop> drops = new ArrayList<>();
    private final Random random = new Random();

    private long lastFrameTime;

    public NexoraLiquidBackgroundView(Context context) {
        super(context);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        glassPaint.setShader(
                new RadialGradient(
                        0,
                        0,
                        900,
                        new int[]{
                                Color.rgb(38, 42, 48),
                                Color.rgb(12, 15, 20),
                                Color.rgb(3, 5, 8)
                        },
                        new float[]{
                                0f,
                                0.55f,
                                1f
                        },
                        Shader.TileMode.CLAMP
                )
        );

        glowPaint.setStyle(Paint.Style.FILL);

        dropPaint.setStyle(Paint.Style.FILL);
        dropPaint.setShadowLayer(
                12f,
                0,
                3f,
                Color.argb(90, 220, 235, 255)
        );

        ripplePaint.setStyle(Paint.Style.STROKE);
        ripplePaint.setStrokeWidth(2.5f);

        lastFrameTime = System.currentTimeMillis();

        post(
                new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                        postDelayed(this, 16L);
                    }
                }
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long now = System.currentTimeMillis();
        float delta =
                Math.min(
                        0.05f,
                        (now - lastFrameTime) / 1000f
                );

        lastFrameTime = now;

        drawGlass(canvas);
        updateDrops(delta);
        drawDrops(canvas);
        drawReflections(canvas);
    }

    private void drawGlass(Canvas canvas) {
        glassPaint.setShader(
                new RadialGradient(
                        getWidth() * 0.5f,
                        getHeight() * 0.18f,
                        Math.max(getWidth(), getHeight()) * 0.95f,
                        new int[]{
                                Color.rgb(40, 44, 51),
                                Color.rgb(13, 17, 22),
                                Color.rgb(3, 5, 8)
                        },
                        new float[]{
                                0f,
                                0.48f,
                                1f
                        },
                        Shader.TileMode.CLAMP
                )
        );

        canvas.drawRect(
                0,
                0,
                getWidth(),
                getHeight(),
                glassPaint
        );
    }

    private void updateDrops(float delta) {

        if (drops.size() < 18 && random.nextFloat() < delta * 3.2f) {
            drops.add(
                    new Drop(
                            random.nextFloat() * getWidth(),
                            -20f,
                            3f + random.nextFloat() * 4f,
                            250f + random.nextFloat() * 280f
                    )
            );
        }

        Iterator<Drop> iterator = drops.iterator();

        while (iterator.hasNext()) {

            Drop drop = iterator.next();

            drop.y += drop.speed * delta;

            if (drop.y >= getHeight() * 0.88f) {
                drop.rippleRadius = 3f;
                drop.rippleAlpha = 150;
                drop.hit = true;
            }

            if (drop.hit) {
                drop.rippleRadius += 180f * delta;
                drop.rippleAlpha -= 170f * delta;

                if (drop.rippleAlpha <= 0) {
                    iterator.remove();
                }
            }
        }
    }

    private void drawDrops(Canvas canvas) {

        for (Drop drop : drops) {

            if (drop.hit) {
                ripplePaint.setColor(
                        Color.argb(
                                Math.max(0, (int) drop.rippleAlpha),
                                205,
                                220,
                                235
                        )
                );

                canvas.drawOval(
                        drop.x - drop.rippleRadius,
                        getHeight() * 0.88f
                                - drop.rippleRadius * 0.22f,
                        drop.x + drop.rippleRadius,
                        getHeight() * 0.88f
                                + drop.rippleRadius * 0.22f,
                        ripplePaint
                );

                continue;
            }

            dropPaint.setColor(
                    Color.argb(
                            155,
                            215,
                            225,
                            235
                    )
            );

            canvas.drawOval(
                    drop.x - drop.size,
                    drop.y - drop.size * 1.7f,
                    drop.x + drop.size,
                    drop.y + drop.size * 1.7f,
                    dropPaint
            );

            dropPaint.setColor(
                    Color.argb(
                            215,
                            245,
                            250,
                            255
                    )
            );

            canvas.drawCircle(
                    drop.x - drop.size * 0.35f,
                    drop.y - drop.size * 0.65f,
                    drop.size * 0.28f,
                    dropPaint
            );
        }
    }

    private void drawReflections(Canvas canvas) {

        float wave =
                (float)
                        Math.sin(
                                System.currentTimeMillis()
                                        / 2300.0
                        );

        glowPaint.setShader(
                new RadialGradient(
                        getWidth() * (0.25f + wave * 0.08f),
                        getHeight() * 0.18f,
                        getWidth() * 0.55f,
                        new int[]{
                                Color.argb(28, 255, 255, 255),
                                Color.argb(8, 220, 230, 240),
                                Color.TRANSPARENT
                        },
                        null,
                        Shader.TileMode.CLAMP
                )
        );

        canvas.drawRect(
                0,
                0,
                getWidth(),
                getHeight(),
                glowPaint
        );
    }

    private static class Drop {

        float x;
        float y;
        float size;
        float speed;

        boolean hit = false;

        float rippleRadius = 0f;
        float rippleAlpha = 0f;

        Drop(
                float x,
                float y,
                float size,
                float speed
        ) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
        }
    }
}
