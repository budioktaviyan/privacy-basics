package id.android.basics.security

import android.app.AppOpsManager
import android.app.AppOpsManager.OPSTR_CAMERA
import android.app.AppOpsManager.OPSTR_COARSE_LOCATION
import android.app.AsyncNotedAppOp
import android.app.SyncNotedAppOp
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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

  object Home : Screens("home")
  object AddLog : Screens("add_log")
  object Camera : Screens("camera")
}

class MainActivity : ComponentActivity() {

  @RequiresApi(Build.VERSION_CODES.R)
  object DataAccessAuditListener : AppOpsManager.OnOpNotedCallback() {
    /**
     * We are just logging to console for this codelab but you can also integrate
     * other logging and reporting systems here to track your app's private data access
     */
    override fun onNoted(op: SyncNotedAppOp) {
      Log.d("DataAccessAuditListener", "Sync Private Data Accessed: ${op.op}")
    }

    override fun onSelfNoted(op: SyncNotedAppOp) {
      Log.d("DataAccessAuditListener", "Self Private Data accessed: ${op.op}")
    }

    override fun onAsyncNoted(asyncNotedAppOp: AsyncNotedAppOp) {
      val emoji = when (asyncNotedAppOp.op) {
        OPSTR_COARSE_LOCATION -> "\uD83D\uDDFA"
        OPSTR_CAMERA -> "\uD83D\uDCF8"
        else -> "?"
      }

      Log.d("DataAccessAuditListener", "Async Private Data ($emoji) Accessed: ${asyncNotedAppOp.op}")
    }
  }

  private lateinit var permissionManager: PermissionManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    permissionManager = (application as SecureXApplication).permissions

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val appOpsManager = getSystemService(AppOpsManager::class.java) as AppOpsManager
      appOpsManager.setOnOpNotedCallback(mainExecutor, DataAccessAuditListener)
    }

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
          val startNavigation = Screens.Home.route

          NavHost(
            navController = navController,
            startDestination = startNavigation
          ) {
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