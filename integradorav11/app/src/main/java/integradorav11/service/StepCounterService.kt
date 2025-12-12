package com.example.integradorav11.service


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.flow.MutableStateFlow

// StepCounterService.kt
class StepCounterService : Service(), SensorEventListener {


    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var stepsToday = 0
    private var initialSteps = 0

    companion object {
        val stepsFlow = MutableStateFlow(0)
        const val CHANNEL_ID = "StepCounterServiceChannel"
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        createNotificationChannel()

        if (stepSensor == null) {
            Log.e("StepCounterService", "Sensor de pasos no disponible en este dispositivo.")
            stopSelf()
            return
        }

        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Contador de Pasos Activo")
            .setContentText("Contando tus pasos...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true) // Notificación persistente
            .build()

        // Iniciar como servicio en primer plano (ID debe ser > 0)
        startForeground(1, notification)

        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalStepsFromSensor = event.values[0].toInt()

            if (initialSteps == 0) {
                initialSteps = totalStepsFromSensor
            }

            stepsToday = totalStepsFromSensor - initialSteps
            if (stepsToday < 0) stepsToday = 0

            stepsFlow.value = stepsToday
            Log.d("StepCounterService", "Pasos detectados: $stepsToday")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Canal del Servicio de Pasos",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}