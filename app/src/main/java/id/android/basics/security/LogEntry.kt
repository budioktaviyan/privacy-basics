package id.android.basics.security

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val MAX_LOG_PHOTOS_LIMIT = 3

@Entity(tableName = "logs")
data class LogEntry(
  @PrimaryKey val date: String, /* date format: yyyy-MM-dd */
  @ColumnInfo(name = "place") val place: String,
  @ColumnInfo(name = "photo1_name") val photo1: String,
  @ColumnInfo(name = "photo2_name") val photo2: String? = null,
  @ColumnInfo(name = "photo3_name") val photo3: String? = null
)