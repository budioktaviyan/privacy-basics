package id.android.basics.security

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

class PermissionViewModel(private val permissions: PermissionManager) : ViewModel() {

  val uiState = permissions.state

  fun onPermissionChange(requestedPermissions: Map<String, Boolean>) {
    permissions.onPermissionChange(requestedPermissions)
  }

  fun createSettingsIntent(): Intent {
    return permissions.createSettingsIntent()
  }
}

class PermissionViewModelFactory : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
    val app = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SecureXApplication
    return PermissionViewModel(app.permissions) as T
  }
}