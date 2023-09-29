package com.example.ejerciciossensores_1_2

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var accelerometerSensor: Sensor? = null
    private var lightSensor: Sensor? = null

    private var isSensorReadingEnabled by mutableStateOf(false)
    private var lightThreshold by mutableStateOf(1000f)
    private var currentLightValue by mutableStateOf(0f)
    private var accelerationMagnitude by mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        setContent {
            SensorApp(
                isSensorReadingEnabled,
                lightThreshold,
                currentLightValue,
                accelerationMagnitude,
                onToggleSensorReading = { newValue ->
                    isSensorReadingEnabled = newValue
                },
                onLightThresholdChange = { newValue ->
                    lightThreshold = newValue
                }
            )
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager?.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                if (isSensorReadingEnabled) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    accelerationMagnitude = sqrt(x * x + y * y + z * z)
                }
            }
            Sensor.TYPE_LIGHT -> {
                if (isSensorReadingEnabled) {
                    currentLightValue = event.values[0]
                }
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No se utiliza
    }
}

@Composable
fun SensorApp(
    isSensorReadingEnabled: Boolean,
    lightThreshold: Float,
    currentLightValue: Float,
    accelerationMagnitude: Float,
    onToggleSensorReading: (Boolean) -> Unit,
    onLightThresholdChange: (Float) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ejercicios propuestos 1,2,3",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lectura de sensores",
                    style = TextStyle(fontSize = 18.sp)
                )
                Switch(
                    checked = isSensorReadingEnabled,
                    onCheckedChange = {
                        onToggleSensorReading(it)
                    }
                )
            }

            if (isSensorReadingEnabled) {
                Text(
                    text = "Magnitud de la aceleración total: %.2f".format(accelerationMagnitude),
                    style = TextStyle(fontSize = 18.sp),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Umbral de luz: %.2f".format(lightThreshold),
                style = TextStyle(fontSize = 18.sp)
            )
            Slider(
                value = lightThreshold,
                onValueChange = {
                    onLightThresholdChange(it)
                },
                valueRange = 0f..2000f,
                steps = 200
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Valor de luz actual: %.2f".format(currentLightValue),
                style = TextStyle(fontSize = 18.sp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            SensorStatus(
                isSensorReadingEnabled = isSensorReadingEnabled,
                lightThreshold = lightThreshold,
                currentLightValue = currentLightValue
            )
        }
    }
}

@Composable
fun SensorStatus(
    isSensorReadingEnabled: Boolean,
    lightThreshold: Float,
    currentLightValue: Float
) {
    val sensorStatus = if (isSensorReadingEnabled) {
        if (currentLightValue < lightThreshold) {
            "Luz actual está por debajo del umbral"
        } else {
            "Luz actual está por encima del umbral"
        }
    } else {
        "Lectura de sensores desactivada"
    }

    val sensorColor = if (isSensorReadingEnabled) {
        if (currentLightValue < lightThreshold) {
            Color.Red
        } else {
            Color.Green
        }
    } else {
        Color.Gray
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(sensorColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = sensorStatus,
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SensorAppPreview() {
    SensorApp(
        isSensorReadingEnabled = false,
        lightThreshold = 1000f,
        currentLightValue = 0f,
        accelerationMagnitude = 0f,
        onToggleSensorReading = {},
        onLightThresholdChange = {}
    )
}