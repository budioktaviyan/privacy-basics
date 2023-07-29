package id.android.basics.security

import android.app.Application

class SecureXApplication : Application() {

  lateinit var photoSaver: PhotoSaverRepository
  lateinit var permissions: PermissionManager

  override fun onCreate() {
    super.onCreate()

    photoSaver = PhotoSaverRepository(this, this.contentResolver)
    permissions = PermissionManager(this)
  }
}