package com.nexora.launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.ImageView;

public class NexoraParallaxWallpaperView
        extends ImageView
        implements SensorEventListener {

    private final SensorManager sensorManager;

    private final Sensor rotationSensor;
    private final Sensor accelerometer;

    private boolean usingRotationSensor;

    private float targetX;
    private float targetY;
    private float targetRotation;

    private float currentX;
    private float currentY;
    private float currentRotation;

    private float targetPerspectiveX;
    private float targetPerspectiveY;

    private float currentPerspectiveX;
    private float currentPerspectiveY;

    private float shakeX;
    private float shakeY;

    private float lastAcceleration;
    private boolean hasAccelerationSample;

    private final float shakeStrength = 2.2f;
    private final float shakeDecay = 0.88f;

    private static final int DEPTH_BACKGROUND = 0;
    private static final int DEPTH_MIDGROUND = 1;
    private static final int DEPTH_FOREGROUND = 2;

    private final float[] depthMultiplier = {
            0.35f,
            0.70f,
            1.0f
    };

    private final float maxOffset = 42f;
    private final float maxRotation = 5f;

    private final float smoothing = 0.075f;
    private final float wallpaperScale = 1.10f;

    private final float[] rotationMatrix =
            new float[9];

    private final float[] orientation =
            new float[3];

    public NexoraParallaxWallpaperView(
            Context context
    ) {
        super(context);

        setScaleType(
                ScaleType.CENTER_CROP
        );

        setWillNotDraw(false);

        sensorManager =
                (SensorManager)
                        context.getSystemService(
                                Context.SENSOR_SERVICE
                        );

        rotationSensor =
                sensorManager.getDefaultSensor(
                        Sensor.TYPE_ROTATION_VECTOR
                );

        accelerometer =
                sensorManager.getDefaultSensor(
                        Sensor.TYPE_ACCELEROMETER
                );

        usingRotationSensor =
                rotationSensor != null;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        currentX +=
                (targetX - currentX)
                        * smoothing;

        currentY +=
                (targetY - currentY)
                        * smoothing;

        currentRotation +=
                (targetRotation - currentRotation)
                        * smoothing;

        currentPerspectiveX +=
                (targetPerspectiveX - currentPerspectiveX)
                        * smoothing;

        currentPerspectiveY +=
                (targetPerspectiveY - currentPerspectiveY)
                        * smoothing;

        canvas.save();

        float centerX =
                getWidth() / 2f;

        float centerY =
                getHeight() / 2f;

        canvas.scale(
                wallpaperScale + currentPerspectiveX,
                wallpaperScale + currentPerspectiveY,
                centerX,
                centerY
        );

        canvas.skew(
                currentPerspectiveX * 0.75f,
                currentPerspectiveY * 0.55f
        );

        canvas.rotate(
                currentRotation,
                centerX,
                centerY
        );

        canvas.translate(
                currentX + shakeX,
                currentY + shakeY
        );

        super.onDraw(canvas);

        canvas.restore();

        shakeX *= shakeDecay;
        shakeY *= shakeDecay;

        if (
                Math.abs(currentX - targetX) > 0.15f
                        || Math.abs(currentY - targetY) > 0.15f
                        || Math.abs(
                        currentRotation
                                - targetRotation
                ) > 0.05f
                        || Math.abs(
                        currentPerspectiveX
                                - targetPerspectiveX
                ) > 0.001f
                        || Math.abs(
                        currentPerspectiveY
                                - targetPerspectiveY
                ) > 0.001f
                        || Math.abs(shakeX) > 0.15f
                        || Math.abs(shakeY) > 0.15f
        ) {
            postInvalidateOnAnimation();
        }
    }

    @Override
    public void onSensorChanged(
            SensorEvent event
    ) {
        if (
                event == null
                        || event.values == null
                        || event.sensor == null
        ) {
            return;
        }

        if (
                usingRotationSensor
                        && event.sensor.getType()
                        == Sensor.TYPE_ROTATION_VECTOR
        ) {
            updateFromRotationVector(
                    event
            );

            return;
        }

        if (
                event.sensor.getType()
                        == Sensor.TYPE_ACCELEROMETER
        ) {
            updateFromAccelerometer(
                    event
            );
        }
    }

    private void updateFromRotationVector(
            SensorEvent event
    ) {
        if (event.values.length < 3) {
            return;
        }

        SensorManager.getRotationMatrixFromVector(
                rotationMatrix,
                event.values
        );

        SensorManager.getOrientation(
                rotationMatrix,
                orientation
        );

        float azimuth =
                orientation[0];

        float pitch =
                orientation[1];

        float roll =
                orientation[2];

        float responsiveRoll =
                applyDepthResponse(
                        clamp(
                                roll,
                                -1f,
                                1f
                        )
                );

        float responsivePitch =
                applyDepthResponse(
                        clamp(
                                pitch,
                                -1f,
                                1f
                        )
                );

        targetX =
                clamp(
                        -responsiveRoll * 52f,
                        -maxOffset,
                        maxOffset
                );

        targetY =
                clamp(
                        responsivePitch * 52f,
                        -maxOffset,
                        maxOffset
                );

        targetRotation =
                clamp(
                        -responsiveRoll * 3.2f,
                        -maxRotation,
                        maxRotation
                );

        targetPerspectiveX =
                clamp(
                        responsiveRoll * 0.045f,
                        -0.045f,
                        0.045f
                );

        targetPerspectiveY =
                clamp(
                        responsivePitch * 0.045f,
                        -0.045f,
                        0.045f
                );

        postInvalidateOnAnimation();
    }

    private void updateFromAccelerometer(
            SensorEvent event
    ) {
        if (event.values.length < 3) {
            return;
        }

        float sensorX =
                event.values[0];

        float sensorY =
                event.values[1];

        float sensorZ =
                event.values[2];

        float acceleration =
                (float) Math.sqrt(
                        sensorX * sensorX
                                + sensorY * sensorY
                                + sensorZ * sensorZ
                );

        if (!hasAccelerationSample) {
            lastAcceleration =
                    acceleration;

            hasAccelerationSample = true;

            postInvalidateOnAnimation();

            return;
        }

        float delta =
                acceleration
                        - lastAcceleration;

        lastAcceleration =
                acceleration;

        if (Math.abs(delta) > 1.4f) {

            shakeX +=
                    clamp(
                            -sensorX * shakeStrength,
                            -14f,
                            14f
                    );

            shakeY +=
                    clamp(
                            sensorY * shakeStrength,
                            -14f,
                            14f
                    );

            shakeX =
                    clamp(
                            shakeX,
                            -24f,
                            24f
                    );

            shakeY =
                    clamp(
                            shakeY,
                            -24f,
                            24f
                    );
        }

        if (!usingRotationSensor) {

            targetX =
                    clamp(
                            -sensorX * 3.5f,
                            -maxOffset,
                            maxOffset
                    );

            targetY =
                    clamp(
                            sensorY * 3.5f,
                            -maxOffset,
                            maxOffset
                    );

            targetRotation = 0f;

            targetPerspectiveX =
                    clamp(
                            -sensorX * 0.004f,
                            -0.045f,
                            0.045f
                    );

            targetPerspectiveY =
                    clamp(
                            sensorY * 0.004f,
                            -0.045f,
                            0.045f
                    );
        }

        postInvalidateOnAnimation();
    }

    @Override
    public void onAccuracyChanged(
            Sensor sensor,
            int accuracy
    ) {
    }

    private float getDepthOffset(
            float baseOffset,
            int depthLevel
    ) {
        if (
                depthLevel < DEPTH_BACKGROUND
                        || depthLevel > DEPTH_FOREGROUND
        ) {
            return baseOffset;
        }

        return baseOffset
                * depthMultiplier[depthLevel];
    }

    private float applyDepthResponse(
            float value
    ) {
        float sign =
                value < 0f
                        ? -1f
                        : 1f;

        float magnitude =
                Math.abs(value);

        magnitude =
                magnitude * magnitude;

        return sign * magnitude;
    }

    private float clamp(
            float value,
            float min,
            float max
    ) {
        return Math.max(
                min,
                Math.min(
                        max,
                        value
                )
        );
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (usingRotationSensor) {

            sensorManager.registerListener(
                    this,
                    rotationSensor,
                    SensorManager.SENSOR_DELAY_GAME
            );
        }

        if (accelerometer != null) {

            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_GAME
            );
        }
    }

    @Override
    protected void onDetachedFromWindow() {

        sensorManager.unregisterListener(
                this
        );

        super.onDetachedFromWindow();
    }
}
