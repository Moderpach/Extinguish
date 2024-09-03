package own.moderpach.extinguish.settings.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty

private const val TAG = "SettingsRepository"

fun LifecycleOwner.settingsRepository(
    settingsDatabase: DataStore<Preferences>
) = SettingsRepository(
    settingsDatabase, this
)

class SettingsRepository(
    private val settingsDatabase: DataStore<Preferences>,
    private val lifecycleOwner: LifecycleOwner,
) : ISettingsRepository {

    override var initVersion: Int by Delegate(SettingsTokens.InitVersion)
    private var _solution by Delegate(SettingsTokens.Solution)
    override var solution
        get() = SettingsTokens.SolutionValue.valueOf(_solution)
        set(value) {
            _solution = value.name
        }
    override var keepWakeWhileScreenOff by Delegate(SettingsTokens.KeepWakeWhileScreenOff)

    override val floatingButton = object : ISettingsRepository.FloatingButton {
        override var enabled by Delegate(SettingsTokens.FloatingButton.Enabled)
        override var autoMoveToEdge by Delegate(SettingsTokens.FloatingButton.AutoMoveToEdge)
        override var fadeWhenUnused by Delegate(SettingsTokens.FloatingButton.FadeWhenUnused)
        override var fadeTransparency by Delegate(SettingsTokens.FloatingButton.FadeTransparency)
        override var blackStyle by Delegate(SettingsTokens.FloatingButton.BlackStyle)
        override var showTimerButton by Delegate(SettingsTokens.FloatingButton.ShowTimerButton)
        override var mergeTimerButton by Delegate(SettingsTokens.FloatingButton.MergeTimerButton)
        override var hideWhenScreenOff by Delegate(SettingsTokens.FloatingButton.HideWhenScreenOff)
    }

    override val volumeKeyEvent = object : ISettingsRepository.VolumeKeyEvent {
        override var enabled by Delegate(SettingsTokens.VolumeKeyEvent.Enabled)
        override var clickToTurnScreenOff by Delegate(SettingsTokens.VolumeKeyEvent.ClickToTurnScreenOff)
        override var clickToTurnScreenOn by Delegate(SettingsTokens.VolumeKeyEvent.ClickToTurnScreenOn)
        private var _listeningMethod by Delegate(SettingsTokens.VolumeKeyEvent.ListeningMethod)
        override var listeningMethod
            get() = SettingsTokens.VolumeKeyEvent.ListeningMethodValue.valueOf(_listeningMethod)
            set(value) {
                _listeningMethod = value.name
            }
    }

    override val screenEvent = object : ISettingsRepository.ScreenEvent {
        override var enabled: Boolean by Delegate(SettingsTokens.ScreenEvent.Enabled)
    }

    override val compatibility = object : ISettingsRepository.Compatibility {
        override var brightnessManualWhenScreenOff: Boolean by Delegate(SettingsTokens.Compatibility.BrightnessManualWhenScreenOff)
    }

    inner class Delegate<T>(private val element: SettingElement<T>) {

        private var value by mutableStateOf(element.default)

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            Log.d(TAG, "setValue: $value")
            lifecycleOwner.lifecycleScope.launch {
                settingsDatabase.edit {
                    it[element.key] = value
                }
            }
        }

        init {
            lifecycleOwner.lifecycleScope.launch {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    settingsDatabase.data.map {
                        it[element.key] ?: element.default
                    }.collectLatest {
                        value = it
                    }
                }
            }
        }
    }

}