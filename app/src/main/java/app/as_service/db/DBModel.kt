package app.as_service.db

import androidx.room.Entity
import androidx.room.PrimaryKey


class DBModel {
    @Entity
    data class GetGPS(@PrimaryKey(autoGenerate = true)val id: Int, val xAxis: String, val yAxis: String, val mills: Long)

    @Entity
    data class GetBookMark(@PrimaryKey(autoGenerate = true)val id: Int, val sn: String)
}