package com.example.parking_01

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity

class MainActivity : ComponentActivity() {
    private lateinit var Placa:EditText
    private lateinit var Calcular:Button
    private lateinit var Moto:Button
    private lateinit var Camioneta:Button
    private lateinit var Automovil:Button
    private lateinit var Tiempo:EditText
    private lateinit var PrecioTotal:TextView
    private lateinit var ButonDB:Button
    private lateinit var RegisMainBT:Button


    private lateinit var dbHelper: DatabaseHelper
    private lateinit var database: SQLiteDatabase

    //moto
    private var Mt = false
    //camioneta
    private var Ct = false
    //automovil
    private var At = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        database = dbHelper.writableDatabase

        //inicializacion
        Placa = findViewById(R.id.PLACA)
        Calcular = findViewById(R.id.CALCULAR)
        Moto = findViewById(R.id.MOTO)
        Camioneta = findViewById(R.id.CAMIONETA)
        Automovil = findViewById(R.id.AUTOMOVIL)
        Tiempo = findViewById(R.id.MINUTOS)
        PrecioTotal = findViewById(R.id.PRECIOTOTAL)
        ButonDB = findViewById(R.id.ButtonDB)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "vehicle_notification_channel"
            val channelName = "Registro de Vehículos"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }


        //Verificar que tipo es
        Camioneta.setOnClickListener{
            Ct=true
            Mt = false
            At = false
        }
        Moto.setOnClickListener{
            Mt=true
            At = false
            Ct = false
        }
        Automovil.setOnClickListener{
            At=true
            Ct = false
            Mt = false
        }
        /*
              moto 90cop por minuto
              carro 110cop por minuto
              camioneta 130 por minuto
        */
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //Intent de la alarma
        val intent = Intent(this, AlarmReceiver::class.java)

        // PendingIntent para ejecutar la acción cuando la alarma se active
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        Calcular.setOnClickListener{
            val notificationManager = NotificationManagerCompat.from(this)

            if(Mt) {
                var builderMoto = NotificationCompat.Builder(this, "vehicle_notification_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Registrado Exitosamente")
                    .setContentText("Cronometro iniciado para Moto de Placa: " + Placa.text)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                notificationManager.notify(1, builderMoto.build())

                var PrecTo1 = Tiempo.text.toString().toInt() * 90
                PrecioTotal.setText("El vehículo de placas: " + Placa.text + " tiene un precio de: " + PrecTo1.toString() + " pesos colombianos")
                val triggerTime1 = System.currentTimeMillis() + Tiempo.text.toString()
                    .toInt() * 60 * 1000 // 1 minuto

                // Programar la alarma exacta usando AlarmManagerCompat
                AlarmManagerCompat.setExact(
                    alarmManager,
                    AlarmManager.RTC_WAKEUP,
                    triggerTime1,
                    pendingIntent
                )
                // Guardar Datos de el vehiculo en la BD
                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_PLACA, Placa.text.toString())
                    put(DatabaseHelper.COLUMN_TIPO, "Moto")
                    put(DatabaseHelper.COLUMN_TIEMPO, Tiempo.text.toString().toInt())
                    put(DatabaseHelper.COLUMN_PRECIO, PrecTo1)
                }

                database.insertWithOnConflict(
                    DatabaseHelper.TABLE_VEHICLES,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
            }
            if(Ct) {
                var builderCamioneta =
                    NotificationCompat.Builder(this, "vehicle_notification_channel")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Registrado Exitosamente")
                        .setContentText("Cronometro iniciado para Camioneta de Placa: " + Placa.text)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                notificationManager.notify(2, builderCamioneta.build())

                var PrecTo2 = Tiempo.text.toString().toInt() * 130
                PrecioTotal.setText("El vehículo de placas: " + Placa.text + " tiene un precio de: " + PrecTo2.toString() + " pesos colombianos")

                val triggerTime2 = System.currentTimeMillis() + Tiempo.text.toString()
                    .toInt() * 60 * 1000 // 1 minuto

                // Programar la alarma exacta usando AlarmManagerCompat
                AlarmManagerCompat.setExact(
                    alarmManager,
                    AlarmManager.RTC_WAKEUP,
                    triggerTime2,
                    pendingIntent
                )

                // Guardar Datos de el vehiculo en la BD
                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_PLACA, Placa.text.toString())
                    put(DatabaseHelper.COLUMN_TIPO, "Camioneta")
                    put(DatabaseHelper.COLUMN_TIEMPO, Tiempo.text.toString().toInt())
                    put(DatabaseHelper.COLUMN_PRECIO, PrecTo2)
                }

                database.insertWithOnConflict(
                    DatabaseHelper.TABLE_VEHICLES,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
                )

            }
            if(At){
                var builderCarro = NotificationCompat.Builder(this, "vehicle_notification_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Registrado Exitosamente")
                    .setContentText("Cronometro iniciado para Carro de Placa: "+ Placa.text)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                notificationManager.notify(3, builderCarro.build())

                var PrecTo3 = Tiempo.text.toString().toInt() * 110
                PrecioTotal.setText("El vehículo de placas: "+Placa.text+" tiene un precio de: " +PrecTo3.toString()+" pesos colombianos")

                val triggerTime3 = System.currentTimeMillis() + Tiempo.text.toString().toInt() * 60 * 1000 // 1 minuto

                // Programar la alarma exacta usando AlarmManagerCompat
                AlarmManagerCompat.setExact(
                    alarmManager,
                    AlarmManager.RTC_WAKEUP,
                    triggerTime3,
                    pendingIntent
                )

                // Guardar Datos de el vehiculo en la BD
                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_PLACA, Placa.text.toString())
                    put(DatabaseHelper.COLUMN_TIPO, "Automovil")
                    put(DatabaseHelper.COLUMN_TIEMPO, Tiempo.text.toString().toInt())
                    put(DatabaseHelper.COLUMN_PRECIO, PrecTo3)
                }

                database.insertWithOnConflict(
                    DatabaseHelper.TABLE_VEHICLES,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
            }
        }
        ButonDB.setOnClickListener {
            val intent2 = Intent(this, DatabaseViewerActivity::class.java)
            startActivity(intent2)
        }

    }
    override fun onDestroy() {
        database.close()
        super.onDestroy()
    }
}
