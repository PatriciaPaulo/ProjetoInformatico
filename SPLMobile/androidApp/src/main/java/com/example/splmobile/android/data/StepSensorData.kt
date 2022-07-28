package com.example.splmobile.android.data

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.SystemClock
import java.text.SimpleDateFormat

class StepSensorData(var stepData : Float) : SensorEventListener {
    override fun onSensorChanged(sensorEvent : SensorEvent?) {
        sensorEvent ?: return

        // First Value of SensorEvent is the Step Count
        sensorEvent.values.firstOrNull()?.let {
            print("StepCount : $it")
            stepData = it
        }

        // Second Value The number of nanosecond passed since the time of last boot
        val lastDeviceBootTimeInMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime()
        val sensorEventTimeInNanos = sensorEvent.timestamp // The number of nanosecond passed since the time of last boot
        val sensorEventTimeInMillis = sensorEventTimeInNanos / 1000_000

        val actualSensorEventTimeInMillis = lastDeviceBootTimeInMillis + sensorEventTimeInMillis
        val displayDateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(actualSensorEventTimeInMillis)
        print("Sensor event is triggered at $displayDateStr")

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        print("onAccuracyChanged: Sensor: $sensor; accuracy: $accuracy")
    }

}