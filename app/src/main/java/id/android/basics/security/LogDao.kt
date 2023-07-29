package id.android.basics.security

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.io.File

@Dao
interface LogDao {

  @Query("SELECT * FROM logs ORDER BY date DESC")
  suspend fun getAll(): List<LogEntry>

  suspend fun getAllWithFiles(photoFolder: File): List<Log> {
    return getAll().map { Log.fromLogEntry(it, photoFolder) }
  }

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(log: LogEntry)

  @Delete
  suspend fun delete(log: LogEntry)
}