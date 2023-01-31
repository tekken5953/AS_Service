package app.as_service.db

import androidx.room.*

@Dao
interface BookMarkRepository {
    @Query("SELECT * FROM DBModel.GetBookMark")
    fun findAll(): List<DBModel?>?

    @Query("SELECT * FROM DBModel.GetBookMark WHERE sn=:serialNumber")
    fun findBySerialNumber(serialNumber: String): DBModel?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(bookmark: DBModel.GetBookMark)

    @Query("DELETE FROM DBModel.GetGPS")
    fun delete(bookmark: DBModel.GetBookMark?) //내부에 값을 넣어서 삭제 가능(오버로딩)

}