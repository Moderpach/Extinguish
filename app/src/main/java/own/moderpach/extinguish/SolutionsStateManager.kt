package own.moderpach.extinguish

import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import rikka.shizuku.Shizuku

class SolutionsStateManager(
    val context: Context
) : ISolutionsStateManager {

    private val _state = MutableStateFlow(SolutionsState())
    override val state: StateFlow<SolutionsState> = _state

    override fun initialize() {
        Shizuku.addBinderReceivedListener(shizukuBinderReceivedListener)
        Shizuku.addBinderDeadListener(shizukuBinderDeadListener)
        Shizuku.addRequestPermissionResultListener(shizukuRequestPermissionResultListener)
    }

    override fun destroy() {
        Shizuku.removeBinderReceivedListener(shizukuBinderReceivedListener)
        Shizuku.removeBinderDeadListener(shizukuBinderDeadListener)
        Shizuku.removeRequestPermissionResultListener(shizukuRequestPermissionResultListener)
    }

    override fun update() {
        _state.update {
            it.copy(
                isShizukuRunning = Shizuku.pingBinder(),
                isShizukuGranted = checkShizukuPermission(),
            )
        }
    }

    //-> Shizuku

    private val shizukuBinderReceivedListener =
        Shizuku.OnBinderReceivedListener {
            _state.update {
                it.copy(isShizukuRunning = true)
            }
        }
    private val shizukuBinderDeadListener =
        Shizuku.OnBinderDeadListener {
            _state.update {
                it.copy(isShizukuRunning = false)
            }
        }
    private val shizukuRequestPermissionResultListener =
        Shizuku.OnRequestPermissionResultListener { _, grantResult ->
            _state.update {
                it.copy(isShizukuGranted = grantResult == PackageManager.PERMISSION_GRANTED)
            }
        }

    override fun checkShizukuPermission() = try {
        Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    } catch (_: Exception) {
        false
    }

    override fun requestShizukuPermission() = try {
        when {
            checkShizukuPermission() -> Unit
            Shizuku.shouldShowRequestPermissionRationale() -> Unit
            else -> {
                Shizuku.requestPermission(1)
            }
        }
    } catch (_: Exception) {
    }
}