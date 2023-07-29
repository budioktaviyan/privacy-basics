package id.android.basics.security

import android.app.Application
import android.content.Context
import android.text.format.DateUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.room.Room
import id.android.basics.security.AppDatabase.Companion.DB_NAME
import kotlinx.coroutines.launch

class HomeViewModel(
  application: Application,
  private val photoSaver: PhotoSaverRepository) : AndroidViewModel(application) {

  data class UiState(
    val loading: Boolean = true,
    val logs: List<Log> = emptyList()
  )

  var uiState by mutableStateOf(UiState())
    private set

  private val context: Context
    get() = getApplication()

  private val db = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()

  fun formatDateTime(timeInMillis: Long): String {
    return DateUtils.formatDateTime(context, timeInMillis, DateUtils.FORMAT_ABBREV_ALL)
  }

  fun loadLogs() {
    viewModelScope.launch {
      uiState = uiState.copy(
        loading = false,
        logs = db.logDao().getAllWithFiles(photoSaver.photoFolder)
      )
    }
  }

  fun delete(log: Log) {
    viewModelScope.launch {
      db.logDao().delete(log.toLogEntry())
      loadLogs()
    }
  }
}

class HomeViewModelFactory : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
    val app = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SecureXApplication
    return HomeViewModel(app, app.photoSaver) as T
  }
}