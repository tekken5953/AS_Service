package app.as_service.db

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import app.as_service.dao.StaticDataObject.TAG_R
import app.as_service.db.dao.GpsRepository
import app.as_service.db.model.DBModel

@Database(entities = [DBModel::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun gpsRepository(): GpsRepository

    companion object {
        private var INSTANCE: AppDataBase? = null

        fun getInstance() : AppDataBase? {
            INSTANCE ?: synchronized(AppDataBase::class.java) {  // 멀티스레드에서 동시생성하는 것을 막음
                INSTANCE ?: INSTANCE.also {
                    Log.d(TAG_R, "DB 인스턴스 생성")
                    INSTANCE = it
                    return INSTANCE
                }
            }
            return null
        }
    }
}