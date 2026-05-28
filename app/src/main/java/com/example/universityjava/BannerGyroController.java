package com.example.universityjava;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BannerGyroController implements SensorEventListener {

    private final SensorManager sensorManager;
    private final Sensor gyroscope;
    private final List<View> banners = new ArrayList<>();
    private float rotationX = 0f;
    private float rotationY = 0f;
    private float velocityX = 0f;
    private float velocityY = 0f;

    public BannerGyroController(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void addBanner(View banner) {
        banner.setCameraDistance(8000 * banner.getResources().getDisplayMetrics().density);
        banners.add(banner);
    }

    public void start() {
        if (AnimationSettings.areAnimationsEnabled())
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float gyroX = event.values[0];
        float gyroY = event.values[1];

        velocityX += gyroX * 1.9f;
        velocityY += gyroY * 1.2f;

        // Damping
        velocityX *= 0.80f;
        velocityY *= 0.80f;

        rotationX += velocityX;
        rotationY += velocityY;

        // Spring back to center
        rotationX *= 0.7f;
        rotationY *= 0.7f;

        rotationX = clamp(rotationX, -9f, 9f);
        rotationY = clamp(rotationY, -12f, 12f);

        for (View banner : banners) {
            banner.animate()
                    .rotationX(rotationX)
                    .rotationY(-rotationY)
                    .setDuration(50)
                    .start();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private float clamp(float value, float min, float max) {

        return Math.max(min, Math.min(max, value));
    }
}
