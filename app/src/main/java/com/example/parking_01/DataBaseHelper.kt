package com.example.parking_01

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "ParkingDatabase"
        const val DATABASE_VERSION = 1
        const val TABLE_VEHICLES = "vehicles"
        const val COLUMN_ID = "_id"
        const val COLUMN_PLACA = "placa"
        const val COLUMN_TIPO = "tipo"
        const val COLUMN_TIEMPO = "tiempo"
        const val COLUMN_PRECIO = "precio"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_VEHICLES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PLACA TEXT NOT NULL UNIQUE,
                $COLUMN_TIPO TEXT NOT NULL,
                $COLUMN_TIEMPO INTEGER NOT NULL,
                $COLUMN_PRECIO REAL NOT NULL
            )
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_VEHICLES")
        onCreate(db)
    }
}