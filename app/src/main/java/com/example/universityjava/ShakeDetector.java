package com.example.universityjava;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

    public interface OnShakeListener {
        void onShake();
    }

    private static final float SHAKE_THRESHOLD = 13.0f;
    private static final int SHAKE_COOLDOWN_MS = 1000;

    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final OnShakeListener listener;

    private long lastShakeTime = 0;

    public ShakeDetector(Context context, OnShakeListener listener) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.listener = listener;
    }

    public void start() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

        long now = System.currentTimeMillis();

        if (acceleration > SHAKE_THRESHOLD && now - lastShakeTime > SHAKE_COOLDOWN_MS) {
            lastShakeTime = now;

            if (listener != null) {
                listener.onShake();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
