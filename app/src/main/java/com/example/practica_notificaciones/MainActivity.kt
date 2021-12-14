package com.example.practica_notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var location: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        location = findViewById(R.id.textView2)
        setUpSensorStuff()

        val text = findViewById<View>(R.id.editTextTextMultiLine) as EditText
        val btn1: Button = findViewById(R.id.btn)

        btn1.setOnClickListener {
            //para la notificacion
            val channelID = "channelId"
            val channelname = "channelName"
            val notificationId = 1
            var value = text.text.toString()

            if (value.isEmpty()) {
                Toast.makeText(this, "Escribe un texto, por favor.", Toast.LENGTH_LONG).show()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    val importance = NotificationManager.IMPORTANCE_HIGH

                    val channel = NotificationChannel(channelID, channelname, importance)

                    //manager para construir la noti
                    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    manager.createNotificationChannel(channel)
                }

                val notification = NotificationCompat.Builder(this@MainActivity, channelID).also {
                    it.setContentTitle("Mensaje de texto")
                    it.setContentText(value)
                    it.setSmallIcon(R.drawable.ic_programacion)
                    it.priority = NotificationCompat.PRIORITY_HIGH
                }.build()

                val notificationManager = NotificationManagerCompat.from(this@MainActivity)

                notificationManager.notify(notificationId, notification)

                text.setText("");
            }
        }
    }

    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            //para la notificacion
            val channelID = "channelId2"
            val channelname = "channelName2"
            val notificationId = 2

            val sides = event.values[0]
            val upDown = event.values[1]

            location.text = "arriba/abajo ${upDown.toInt()}\nizquierda/derecha ${sides.toInt()}"

            if (upDown >= 9) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    val importance = NotificationManager.IMPORTANCE_HIGH

                    val channel = NotificationChannel(channelID, channelname, importance)

                    //manager para construir la noti
                    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    manager.createNotificationChannel(channel)
                }

                val notification = NotificationCompat.Builder(this@MainActivity, channelID).also {
                    it.setContentTitle("Posición del teléfono")
                    it.setContentText("Teléfono en la altura correcta!")
                    it.setSmallIcon(R.drawable.ic_acelerometro)
                    it.priority = NotificationCompat.PRIORITY_HIGH
                }.setOnlyAlertOnce(true).build()

                val notificationManager = NotificationManagerCompat.from(this@MainActivity)

                notificationManager.notify(notificationId, notification)
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
}