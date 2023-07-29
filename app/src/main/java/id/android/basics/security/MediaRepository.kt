package id.android.basics.security

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns.DATA
import android.provider.MediaStore.Files.FileColumns.DATE_ADDED
import android.provider.MediaStore.Files.FileColumns.DISPLAY_NAME
import android.provider.MediaStore.Files.FileColumns.MIME_TYPE
import android.provider.MediaStore.Files.FileColumns.SIZE
import android.provider.MediaStore.Files.FileColumns._ID
import kotlinx.coroutines.flow.flow
import java.io.File

class MediaRepository(private val context: Context) {

  data class MediaEntry(
    val uri: Uri,
    val filename: String,
    val mimeType: String,
    val size: Long,
    val path: String) {

    val file: File
      get() = File(path)
  }

  fun fetchImages() = flow {
    val externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val projection = arrayOf(
      _ID,
      DISPLAY_NAME,
      SIZE,
      MIME_TYPE,
      DATA
    )

    val cursor = context.contentResolver.query(
      externalContentUri,
      projection,
      null,
      null,
      "$DATE_ADDED DESC"
    ) ?: throw Exception("Query could not be executed")

    cursor.use {
      while (cursor.moveToNext()) {
        val idColumn = cursor.getColumnIndexOrThrow(_ID)
        val displayNameColumn = cursor.getColumnIndexOrThrow(DISPLAY_NAME)
        val sizeColumn = cursor.getColumnIndexOrThrow(SIZE)
        val mimeTypeColumn = cursor.getColumnIndexOrThrow(MIME_TYPE)
        val dataColumn = cursor.getColumnIndexOrThrow(DATA)

        val contentUri = ContentUris.withAppendedId(
          externalContentUri,
          cursor.getLong(idColumn)
        )

        emit(
          MediaEntry(
            uri = contentUri,
            filename = cursor.getString(displayNameColumn),
            size = cursor.getLong(sizeColumn),
            mimeType = cursor.getString(mimeTypeColumn),
            path = cursor.getString(dataColumn)
          )
        )
      }
    }
  }
}