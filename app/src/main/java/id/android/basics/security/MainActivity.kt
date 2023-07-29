package id.android.basics.security

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import id.android.basics.security.ui.theme.SecureXTheme
import kotlinx.coroutines.launch

sealed class Screens(val route: String) {
  object Permissions : Screens("permissions")
  object Home : Screens("home")
  object AddLog : Screens("add_log")
  object Camera : Screens("camera")
}

class MainActivity : ComponentActivity() {

  lateinit var permissionManager: PermissionManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    permissionManager = (application as SecureXApplication).permissions

    // TODO: Step 2. Register Data Access Audit Callback
    setContent {
      SecureXTheme {
        /**
         * A surface container using the 'background' color from the theme
         */
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          val navController = rememberNavController()

          val startNavigation = if (permissionManager.hasAllPermissions) {
            Screens.Home.route
          } else {
            Screens.Permissions.route
          }

          NavHost(
            navController = navController,
            startDestination = startNavigation
          ) {
            composable(Screens.Permissions.route) { PermissionScreen(navController) }
            composable(Screens.Home.route) { HomeScreen(navController) }
            composable(Screens.AddLog.route) { AddLogScreen(navController) }
            composable(Screens.Camera.route) { CameraScreen(navController) }
          }
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()

    lifecycleScope.launch {
      permissionManager.checkPermissions()
    }
  }
}