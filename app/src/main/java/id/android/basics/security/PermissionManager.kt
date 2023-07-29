package id.android.basics.security

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionManager(private val context: Context) {

  data class State(
    val hasStorageAccess: Boolean,
    val hasCameraAccess: Boolean,
    val hasLocationAccess: Boolean) {
    val hasAllAccess: Boolean
      get() = hasStorageAccess && hasCameraAccess && hasLocationAccess
  }

  companion object {

    val REQUIRED_PERMISSIONS_PRE_T = arrayOf(
      READ_EXTERNAL_STORAGE,
      CAMERA,
      ACCESS_FINE_LOCATION,
      ACCESS_COARSE_LOCATION
    )

    @SuppressLint("InlinedApi")
    val REQUIRED_PERMISSIONS_POST_T = arrayOf(
      READ_MEDIA_IMAGES,
      CAMERA,
      ACCESS_FINE_LOCATION,
      ACCESS_COARSE_LOCATION
    )
  }

  val hasAllPermissions: Boolean
    get() = _state.value.hasAllAccess

  @SuppressLint("InlinedApi")
  private val _state = MutableStateFlow(
    State(
      hasStorageAccess = hasAccess(READ_EXTERNAL_STORAGE) || hasAccess(READ_MEDIA_IMAGES),
      hasCameraAccess = hasAccess(CAMERA),
      hasLocationAccess = hasAccess(
        listOf(
          ACCESS_FINE_LOCATION,
          ACCESS_COARSE_LOCATION
        )
      )
    )
  )

  val state = _state.asStateFlow()

  @SuppressLint("InlinedApi")
  fun onPermissionChange(permissions: Map<String, Boolean>) {
    val hasLocationAccess = hasAccess(ACCESS_FINE_LOCATION) && hasAccess(ACCESS_COARSE_LOCATION)
    val hasStorageAccess = hasAccess(READ_MEDIA_IMAGES) || hasAccess(READ_EXTERNAL_STORAGE)

    _state.value = State(
      hasStorageAccess = hasStorageAccess,
      hasCameraAccess = permissions[CAMERA] ?: _state.value.hasCameraAccess,
      hasLocationAccess = hasLocationAccess
    )
  }

  fun createSettingsIntent(): Intent {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      data = Uri.fromParts("package", context.packageName, null)
    }

    return intent
  }

  @SuppressLint("InlinedApi")
  suspend fun checkPermissions() {
    val newState = State(
      hasStorageAccess = hasAccess(READ_EXTERNAL_STORAGE) || hasAccess(READ_MEDIA_IMAGES),
      hasCameraAccess = hasAccess(CAMERA),
      hasLocationAccess = hasAccess(ACCESS_FINE_LOCATION) && hasAccess(ACCESS_COARSE_LOCATION)
    )

    _state.emit(newState)
  }

  private fun hasAccess(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
      context,
      permission
    ) == PackageManager.PERMISSION_GRANTED
  }

  private fun hasAccess(permissions: List<String>): Boolean {
    return permissions.all(::hasAccess)
  }
}