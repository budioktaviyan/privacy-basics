package id.android.basics.security

import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

data class Log(
  val date: String,
  val place: String,
  val photos: List<File>) {

  companion object {

    fun fromLogEntry(logEntry: LogEntry, photoFolder: File): Log {
      return Log(
        date = logEntry.date,
        place = logEntry.place,
        photos = listOfNotNull(
          logEntry.photo1,
          logEntry.photo2,
          logEntry.photo3
        ).map {
          File(photoFolder, it)
        }
      )
    }
  }

  val timeInMillis = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)?.time ?: throw Exception("Unknown date format!")

  fun toLogEntry(): LogEntry {
    return LogEntry(
      date = date,
      place = place,
      photo1 = photos[0].name,
      photo2 = photos.getOrNull(1)?.name,
      photo3 = photos.getOrNull(2)?.name
    )
  }
}