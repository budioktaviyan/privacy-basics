package id.android.basics.security

import android.util.Log
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun CameraScreen(
  navController: NavHostController,
  viewModel: CameraViewModel = viewModel(factory = CameraViewModelFactory())) {
  val lifecycleOwner = LocalLifecycleOwner.current
  val state = viewModel.cameraState
  val previewUseCase = remember { Preview.Builder().build() }

  LaunchedEffect(Unit) {
    val cameraProvider = viewModel.getCameraProvider()
    try {
      cameraProvider.unbindAll()
      cameraProvider.bindToLifecycle(
        lifecycleOwner,
        CameraSelector.DEFAULT_BACK_CAMERA,
        previewUseCase,
        state.imageCapture
      )
    } catch (ex: Exception) {
      Log.e("CameraCapture", "Failed to bind camera use cases", ex)
    }
  }

  LaunchedEffect(state.imageFile) {
    if (state.imageFile != null) {
      navController.popBackStack()
    }
  }

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(onClick = { if (!state.isTakingPicture) viewModel.takePicture() } ) {
        Icon(Icons.Filled.PhotoCamera, contentDescription = "Take picture")
      }
    }
  ) { innerPadding ->
    AndroidView(
      modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize(),
      factory = { context ->
        PreviewView(context).apply {
          layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
          previewUseCase.setSurfaceProvider(this.surfaceProvider)
        }
      }
    )
  }
}