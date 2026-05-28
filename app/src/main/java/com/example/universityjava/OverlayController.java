package com.example.universityjava;


import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.RenderEffect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.view.View;

public class OverlayController implements SensorEventListener {

    private final SensorManager sensorManager;
    private final Sensor lightSensor;

    private final View contentView;
    private final View overlayView;

    private float currentOverlayAlpha = 0f;
    private float currentContrast = 1f;

    public OverlayController(Context context, View contentView, View overlayView) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        this.contentView = contentView;
        this.overlayView = overlayView;
    }

    public void start() {
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void reset() {
        currentOverlayAlpha = 0f;
        currentContrast = 1f;

        overlayView.animate().cancel();
        overlayView.setAlpha(0f);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float lux = event.values[0];
        applyLightSettings(lux);
    }

    private void applyLightSettings(float lux) {
        float targetOverlayAlpha;
        float targetContrast;

        if (lux < 10f) {
            // Very dark
            targetOverlayAlpha = 0.4f;
            targetContrast = 0.75f;
        } else if (lux < 80f) {
            // Dim indoor
            targetOverlayAlpha = 0.25f;
            targetContrast = 0.95f;
        } else if (lux < 800f) {
            // Normal indoor
            targetOverlayAlpha = 0.05f;
            targetContrast = 1.0f;
        } else {
            // Very bright
            targetOverlayAlpha = 0f;
            targetContrast = 1.25f;
        }

        currentOverlayAlpha = lerp(currentOverlayAlpha, targetOverlayAlpha, 0.08f);
        currentContrast = lerp(currentContrast, targetContrast, 0.08f);

        overlayView.setAlpha(currentOverlayAlpha);

        applyColorEffect(currentContrast);
    }

    private void applyColorEffect(float contrast) {
        float midpoint = 128f * (1f - contrast);

        ColorMatrix contrastMatrix = new ColorMatrix(new float[]{
                contrast, 0,        0,        0, midpoint,
                0,        contrast, 0,        0, midpoint,
                0,        0,        contrast, 0, midpoint,
                0,        0,        0,        1, 0
        });

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(contrastMatrix);
        RenderEffect effect = RenderEffect.createColorFilterEffect(filter);
        contentView.setRenderEffect(effect);
    }

    private float lerp(float current, float target, float factor) {
        return current + (target - current) * factor;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}

