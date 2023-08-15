package id.android.basics.security

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow

class PermissionManager(private val context: Context) {

  data class State(
    val hasStorageAccess: Boolean,
    val hasCameraAccess: Boolean,
    val hasLocationAccess: Boolean
  )

  @SuppressLint("InlinedApi")
  private val _state = MutableStateFlow(
    State(
      hasStorageAccess = hasAccess(READ_EXTERNAL_STORAGE) || hasAccess(READ_MEDIA_IMAGES),
      hasCameraAccess = hasAccess(CAMERA),
      hasLocationAccess = hasAccess(ACCESS_COARSE_LOCATION)
    )
  )

  @SuppressLint("InlinedApi")
  suspend fun checkPermissions() {
    val newState = State(
      hasStorageAccess = hasAccess(READ_EXTERNAL_STORAGE) || hasAccess(READ_MEDIA_IMAGES),
      hasCameraAccess = hasAccess(CAMERA),
      hasLocationAccess = hasAccess(ACCESS_COARSE_LOCATION)
    )

    _state.emit(newState)
  }

  private fun hasAccess(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
      context,
      permission
    ) == PackageManager.PERMISSION_GRANTED
  }
}