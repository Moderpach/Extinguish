package own.moderpach.extinguish.settings.data

interface ISettingsRepository {

    var initVersion: Int
    var solution: SettingsTokens.SolutionValue
    var keepWakeWhileScreenOff: Boolean

    interface FloatingButton {
        var enabled: Boolean
        var autoMoveToEdge: Boolean
        var fadeWhenUnused: Boolean
        var fadeTransparency: Float
        var blackStyle: Boolean
        var showTimerButton: Boolean
        var mergeTimerButton: Boolean
        var hideWhenScreenOff: Boolean
    }

    val floatingButton: FloatingButton

    interface VolumeKeyEvent {
        var enabled: Boolean
        var clickToTurnScreenOff: Boolean
        var clickToTurnScreenOn: Boolean
        var listeningMethod: SettingsTokens.VolumeKeyEvent.ListeningMethodValue
    }

    val volumeKeyEvent: VolumeKeyEvent

    interface ScreenEvent {
        var enabled: Boolean
    }

    val screenEvent: ScreenEvent

    interface Compatibility {
        var brightnessManualWhenScreenOff: Boolean
    }

    val compatibility: Compatibility
}
