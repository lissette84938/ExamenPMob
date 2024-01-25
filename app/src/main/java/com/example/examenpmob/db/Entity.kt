package com.example.examenpmob.db

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity
data class Entity(
    @PrimaryKey(autoGenerate = true)
    val uid:Int,

    @ColumnInfo
    var place:String,

    @ColumnInfo
    var imgRef: Bitmap?,

    @ColumnInfo
    var longitud:Double?,

    @ColumnInfo
    var latitud:Double?,

    @ColumnInfo
    var order:Int,

    @ColumnInfo
    var price:Double,

    @ColumnInfo
    var movePrice:Double,

    @ColumnInfo
    var comments:String?
)

