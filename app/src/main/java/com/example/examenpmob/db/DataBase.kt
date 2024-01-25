package com.example.examenpmob.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Entity::class], version = 6)
@TypeConverters(Conv::class)
abstract class DataBase : RoomDatabase() {
    abstract fun entityDao(): EntityDao

    companion object {
        @Volatile
        private var BASE_DATOS: DataBase? = null

        fun getInstace(context: Context): DataBase {

            return BASE_DATOS ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    DataBase::class.java,
                    "BD.bd"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { BASE_DATOS = it}
            }
        }
    }
}


