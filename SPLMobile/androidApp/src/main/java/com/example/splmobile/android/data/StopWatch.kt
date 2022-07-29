package com.example.splmobile.android.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class StopWatch {
    private var coroutineScope = CoroutineScope(Dispatchers.Main)

    var formattedTime by mutableStateOf("00:00:00")
    private var timeMillis = 0L
    private var lastTimestamp = 0L

    fun startStopwatch() {
        coroutineScope.launch {
            lastTimestamp = System.currentTimeMillis()

            delay(1000L)
            timeMillis += System.currentTimeMillis() - lastTimestamp
            lastTimestamp = System.currentTimeMillis()

            formattedTime = formatTime(timeMillis)
        }
    }

    private fun formatTime(timeMillis : Long) : String {
        // Set Millis to Date format
        val localDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timeMillis),
            ZoneId.systemDefault()
        )

        // Set Date Format to Timer Format
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault())

        return localDateTime.format(formatter)
    }
}