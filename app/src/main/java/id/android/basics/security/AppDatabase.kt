package id.android.basics.security

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  entities = [LogEntry::class],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

  companion object {

    const val DB_NAME = "main"
  }

  abstract fun logDao(): LogDao
}