package own.moderpach.extinguish.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlin.reflect.KProperty

private const val TAG = "SyncSettingsRepository"

class SyncSettingsRepository(
    private val settingsDatabase: DataStore<Preferences>
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

    private var data: Preferences? = null

    suspend fun update() {
        data = settingsDatabase.data.first()
    }

    suspend fun keptUpdate() {
        settingsDatabase.data.collectLatest {
            data = it
        }
    }

    inner class Delegate<T>(private val element: SettingElement<T>) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
            data?.get(element.key) ?: element.default

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            require(false) {
                "SyncSettingsRepository should not be set."
            }
        }

    }
}