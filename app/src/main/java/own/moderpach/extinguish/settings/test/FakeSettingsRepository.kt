package own.moderpach.extinguish.settings.test

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.data.SettingsTokens

class FakeSettingsRepository : ISettingsRepository {
    override var initVersion by mutableStateOf(0)
    override var solution by mutableStateOf(SettingsTokens.SolutionValue.ShizukuPowerOffScreen)
    override var keepWakeWhileScreenOff by mutableStateOf(false)

    override val floatingButton = object : ISettingsRepository.FloatingButton {
        override var enabled by mutableStateOf(false)
        override var autoMoveToEdge by mutableStateOf(true)
        override var fadeWhenUnused by mutableStateOf(false)
        override var fadeTransparency by mutableFloatStateOf(0.6f)
        override var blackStyle by mutableStateOf(false)
        override var showTimerButton by mutableStateOf(false)
        override var mergeTimerButton by mutableStateOf(false)
        override var hideWhenScreenOff by mutableStateOf(false)
    }

    override val volumeKeyEvent = object : ISettingsRepository.VolumeKeyEvent {
        override var enabled by mutableStateOf(false)
        override var clickToTurnScreenOff by mutableStateOf(false)
        override var clickToTurnScreenOn by mutableStateOf(false)
        override var listeningMethod by mutableStateOf(SettingsTokens.VolumeKeyEvent.ListeningMethodValue.Shell)
    }

    override val screenEvent = object : ISettingsRepository.ScreenEvent {
        override var enabled: Boolean by mutableStateOf(false)
    }

    override val compatibility = object : ISettingsRepository.Compatibility {
        override var brightnessManualWhenScreenOff: Boolean by mutableStateOf(false)
    }

}