package com.example.examenpmob.db

import android.graphics.Bitmap
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface EntityDao {

    @Query("SELECT * FROM Entity ORDER BY `order` ASC")
    fun findAll(): List<Entity>

    @Delete
    fun delete(product:Entity)

    @Insert
    fun insert(product:Entity):Long

    @Update
    fun update(product: Entity)

    @Query("DELETE FROM Entity")
    fun deleteAll()

    @Query("UPDATE Entity SET imgRef = :imgRef WHERE uid = :uid")
    fun updateImgRef(uid:Int, imgRef: Bitmap)

    @Query("UPDATE Entity SET place = :place, `order` = :order, price = :price, movePrice = :movePrice, comments = :comments WHERE uid = :uid")
    fun updatePlace(uid:Int, place:String, order:Int, price:Double, movePrice:Double, comments:String)

    @Query("UPDATE Entity SET longitud = :longitud, latitud = :latitud WHERE uid = :uid")
    fun updateLatLon(uid:Int, longitud:Double, latitud:Double)
}
