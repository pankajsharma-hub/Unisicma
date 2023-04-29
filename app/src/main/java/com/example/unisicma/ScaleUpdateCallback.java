package com.example.unisicma;

public interface ScaleUpdateCallback {
    void handleState(BluetoothService.ConnectionState state);

    void handleScaleReading(ScaleReading scaleReading);
}
