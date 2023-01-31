package app.as_service.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GpsRepository {
    @Query("SELECT * FROM DBModel.GetGPS")
    fun findGps(): DBModel

    @Query("SELECT * FROM DBModel.GetGPS WHERE id=:mills")
    fun findGpsByMills(mills: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGps(gps: DBModel.GetGPS)

    @Query("DELETE FROM DBModel.GetGPS")
    suspend fun deleteAll()
}