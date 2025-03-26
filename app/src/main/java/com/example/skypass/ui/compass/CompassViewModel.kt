// ui/compass/CompassViewModel.kt
package com.example.skypass.ui.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class CompassViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel(), SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _compassState = MutableStateFlow<CompassState>(CompassState.Initializing)
    val compassState: StateFlow<CompassState> = _compassState.asStateFlow()

    private val _compassDirection = MutableStateFlow(0f)
    val compassDirection: StateFlow<Float> = _compassDirection.asStateFlow()

    private val _sensorAccuracy = MutableStateFlow(SensorAccuracy.Low)
    val sensorAccuracy: StateFlow<SensorAccuracy> = _sensorAccuracy.asStateFlow()

    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null

    init {
        checkSensors()
    }

    private fun checkSensors() {
        if (accelerometer == null || magnetometer == null) {
            _compassState.value = CompassState.SensorsNotAvailable
        } else {
            _compassState.value = CompassState.Active
        }
    }

    fun startListening() {
        if (_compassState.value == CompassState.Active) {
            accelerometer?.let {
                sensorManager.registerListener(
                    this,
                    it,
                    SensorManager.SENSOR_DELAY_GAME
                )
            }

            magnetometer?.let {
                sensorManager.registerListener(
                    this,
                    it,
                    SensorManager.SENSOR_DELAY_GAME
                )
            }
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    fun calibrate() {
        gravity = null
        geomagnetic = null
        _compassDirection.value = 0f
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values.clone()
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values.clone()
        }

        if (gravity != null && geomagnetic != null) {
            val r = FloatArray(9)
            val i = FloatArray(9)

            if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)

                // Convert radians to degrees
                val degrees = (Math.toDegrees(orientation[0].toDouble()) + 360) % 360
                _compassDirection.value = degrees.toFloat()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        _sensorAccuracy.value = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> SensorAccuracy.High
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> SensorAccuracy.Medium
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> SensorAccuracy.Low
            else -> SensorAccuracy.Unreliable
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}

sealed class CompassState {
    object Initializing : CompassState()
    object Active : CompassState()
    object SensorsNotAvailable : CompassState()
}

enum class SensorAccuracy {
    High, Medium, Low, Unreliable
}