package id.android.basics.security

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PhotoSaverRepository(
  context: Context,
  private val contentResolver: ContentResolver) {

  private val _photos = mutableListOf<File>()
  private val cacheFolder = File(context.cacheDir, "photos").also { it.mkdir() }

  val photoFolder = File(context.filesDir, "photos").also { it.mkdir() }

  fun getPhotos() = _photos.toList()

  fun isEmpty() = _photos.isEmpty()

  fun canAddPhoto() = _photos.size < MAX_LOG_PHOTOS_LIMIT

  fun generatePhotoCacheFile() = File(cacheFolder, generateFileName())

  fun cacheCapturedPhoto(photo: File) {
    if (_photos.size + 1 > MAX_LOG_PHOTOS_LIMIT) {
      return
    }

    _photos.plus(photo)
  }

  suspend fun cacheFromUri(uri: Uri) {
    withContext(Dispatchers.IO) {
      if (_photos.size + 1 > MAX_LOG_PHOTOS_LIMIT) {
        return@withContext
      }

      contentResolver.openInputStream(uri)?.use { input ->
        val cachedPhoto = generatePhotoCacheFile()

        cachedPhoto.outputStream().use { output ->
          input.copyTo(output)
          _photos.plus(cachedPhoto)
        }
      }
    }
  }

  suspend fun cacheFromUris(uris: List<Uri>) {
    uris.forEach {
      cacheFromUri(it)
    }
  }

  suspend fun removeFile(photo: File) {
    withContext(Dispatchers.IO) {
      photo.delete()
      _photos.minus(photo)
    }
  }

  suspend fun savePhotos(): List<File> {
    return withContext(Dispatchers.IO) {
      val savedPhotos = _photos.map { it.copyTo(generatePhotoLogFile()) }
      _photos.forEach { it.delete() }
      _photos.clear()

      savedPhotos
    }
  }

  private fun generateFileName() = "${System.currentTimeMillis()}.jpg"

  private fun generatePhotoLogFile() = File(photoFolder, generateFileName())
}