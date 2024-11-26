package com.example.parking_01

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DatabaseViewerActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var database: SQLiteDatabase
    private lateinit var viewerDB: TextView
    private lateinit var editTextPlaca: EditText
    private lateinit var btnBuscar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnActualizar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.database_viewer)
        dbHelper = DatabaseHelper(this)
        database = dbHelper.writableDatabase

        viewerDB = findViewById(R.id.ViewerDB)
        editTextPlaca = findViewById(R.id.editTextText)
        btnBuscar = findViewById(R.id.Buscar)
        btnEliminar = findViewById(R.id.button3)
        btnActualizar = findViewById(R.id.button4)
        val regisMainBT: Button = findViewById(R.id.RegisActiReturn)
        mostrarTodosLosVehiculos()

        regisMainBT.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnBuscar.setOnClickListener {
            buscarVehiculoPorPlaca()
        }

        btnEliminar.setOnClickListener {
            eliminarVehiculoPorPlaca()
        }
        btnActualizar.setOnClickListener {
            mostrarTodosLosVehiculos()
        }
    }

    private fun mostrarTodosLosVehiculos() {
        val cursor = database.query(
            DatabaseHelper.TABLE_VEHICLES,
            null,
            null,
            null,
            null,
            null,
            null
        )

        val vehiculosBuilder = StringBuilder()
        while (cursor.moveToNext()) {
            val placa = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PLACA))
            val tipo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIPO))
            val tiempo = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIEMPO))
            val precio = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRECIO))

            vehiculosBuilder.append("Placa: $placa\n")
            vehiculosBuilder.append("Tipo: $tipo\n")
            vehiculosBuilder.append("Tiempo (min): $tiempo\n")
            vehiculosBuilder.append("Precio: $precio COP\n\n")
        }
        cursor.close()

        viewerDB.text = vehiculosBuilder.toString()
    }

    private fun buscarVehiculoPorPlaca() {
        val placa = editTextPlaca.text.toString()
        val selection = "${DatabaseHelper.COLUMN_PLACA} = ?"
        val selectionArgs = arrayOf(placa)

        val cursor = database.query(
            DatabaseHelper.TABLE_VEHICLES,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val vehiculosBuilder = StringBuilder()
        while (cursor.moveToNext()) {
            val tipo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIPO))
            val tiempo = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIEMPO))
            val precio = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRECIO))

            vehiculosBuilder.append("Placa: $placa\n")
            vehiculosBuilder.append("Tipo: $tipo\n")
            vehiculosBuilder.append("Tiempo (min): $tiempo\n")
            vehiculosBuilder.append("Precio: $precio COP\n")
        }
        cursor.close()

        viewerDB.text = if (vehiculosBuilder.isNotEmpty()) {
            vehiculosBuilder.toString()
        } else {
            "No se encontró ningún vehículo con esa placa"
        }
    }

    private fun eliminarVehiculoPorPlaca() {
        val placa = editTextPlaca.text.toString()
        val whereClause = "${DatabaseHelper.COLUMN_PLACA} = ?"
        val whereArgs = arrayOf(placa)

        val deletedRows = database.delete(DatabaseHelper.TABLE_VEHICLES, whereClause, whereArgs)

        if (deletedRows > 0) {
            Toast.makeText(this, "Vehículo eliminado", Toast.LENGTH_SHORT).show()
            mostrarTodosLosVehiculos()
        } else {
            Toast.makeText(this, "No se encontró el vehículo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        database.close()
        super.onDestroy()
    }
}