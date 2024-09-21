package own.moderpach.extinguish

import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import rikka.shizuku.Shizuku

class SolutionDependencyManager(
    private val context: Context
) : ISolutionDependencyManager {
    private val _state = MutableStateFlow(SolutionDependencyState())
    override val state: StateFlow<SolutionDependencyState> = _state

    private interface ShizukuDependencyListener {
        val binderReceivedListener: Shizuku.OnBinderReceivedListener
        val binderDeadListener: Shizuku.OnBinderDeadListener
        val requestPermissionResultListener: Shizuku.OnRequestPermissionResultListener
    }

    private val shizuku = object : ShizukuDependencyListener {
        override val binderReceivedListener = Shizuku.OnBinderReceivedListener {
            _state.getAndUpdate {
                it.copy(isShizukuBinderAlive = true)
            }
        }
        override val binderDeadListener = Shizuku.OnBinderDeadListener {
            _state.getAndUpdate {
                it.copy(isShizukuBinderAlive = false)
            }
        }
        override val requestPermissionResultListener =
            Shizuku.OnRequestPermissionResultListener { _, grantResult ->
                _state.getAndUpdate {
                    it.copy(isShizukuPermissionGranted = grantResult == PackageManager.PERMISSION_GRANTED)
                }
            }
    }

    override fun updateImmediately() {
        _state.update {
            val isShizukuBinderAlive = runCatching { Shizuku.pingBinder() }.getOrElse { false }
            val isShizukuPermissionGranted = runCatching {
                isShizukuBinderAlive.and(
                    Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
                )
            }.getOrElse { false }
            SolutionDependencyState(
                isShizukuBinderAlive = isShizukuBinderAlive,
                isShizukuPermissionGranted = isShizukuPermissionGranted
            )
        }
    }

    override fun requestShizukuPermission(onShouldShowRequestPermissionRationale: () -> Unit) {
        val isShizukuBinderAlive = runCatching { Shizuku.pingBinder() }.getOrElse { false }
        val isShizukuPermissionGranted = runCatching {
            isShizukuBinderAlive.and(
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            )
        }.getOrElse { false }
        if (isShizukuPermissionGranted) return
        if (Shizuku.shouldShowRequestPermissionRationale()) {
            onShouldShowRequestPermissionRationale()
            return
        }
        Shizuku.requestPermission(1)
    }

    init {
        Shizuku.addBinderReceivedListener(shizuku.binderReceivedListener)
        Shizuku.addBinderDeadListener(shizuku.binderDeadListener)
        Shizuku.addRequestPermissionResultListener(shizuku.requestPermissionResultListener)
    }

    override fun destroy() {
        Shizuku.removeBinderReceivedListener(shizuku.binderReceivedListener)
        Shizuku.removeBinderDeadListener(shizuku.binderDeadListener)
        Shizuku.removeRequestPermissionResultListener(shizuku.requestPermissionResultListener)
    }

}

data class SolutionDependencyState(
    val isShizukuBinderAlive: Boolean = false,
    val isShizukuPermissionGranted: Boolean = false
)