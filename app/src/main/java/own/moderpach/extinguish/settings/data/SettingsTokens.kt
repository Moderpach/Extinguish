package own.moderpach.extinguish.settings.data

object SettingsTokens {
    val InitVersion = intSettingElement("InitVersion", 0)

    enum class SolutionValue {
        ShizukuPowerOffScreen,
        ShizukuScreenBrightnessNeg1
    }

    val Solution = stringSettingElement("Solution", SolutionValue.ShizukuPowerOffScreen.name)

    val AgreedPolicyVersion = intSettingElement("AgreedPolicyVersion", 0)
    val KeepWakeWhileScreenOff = booleanSettingElement("KeepWakeWhileScreenOff", true)

    object FloatingButton {
        val Enabled = booleanSettingElement("FloatingButton/Enabled", true)
        val AutoMoveToEdge = booleanSettingElement("FloatingButton/AutoMoveToEdge", true)
        val FadeWhenUnused = booleanSettingElement("FloatingButton/FadeWhenUnused", true)
        val FadeTransparency = floatSettingElement("FloatingButton/FadeTransparency", 0.6f)
        val BlackStyle = booleanSettingElement("FloatingButton/BlackStyle", false)

        //该功能不再被需要，因为按键策略变化
        val ShowTimerButton = booleanSettingElement("FloatingButton/ShowTimerButton", true)
        val MergeTimerButton = booleanSettingElement("FloatingButton/MergeTimerButton", false)
        val HideWhenScreenOff = booleanSettingElement("FloatingButton/HideWhenScreenOff", false)
    }

    object VolumeKeyEvent {
        val Enabled = booleanSettingElement("VolumeKeyEvent/Enabled", true)
        val ClickToTurnScreenOff =
            booleanSettingElement("VolumeKeyEvent/ClickToTurnScreenOff", true)
        val ClickToTurnScreenOn = booleanSettingElement("VolumeKeyEvent/ClickToTurnScreenOn", true)

        enum class ListeningMethodValue {
            Shell,
            Window
        }

        val ListeningMethod =
            stringSettingElement("VolumeKeyEvent/ListeningMethod", ListeningMethodValue.Shell.name)
    }

    object ScreenEvent {
        val Enabled = booleanSettingElement("ScreenEvent/Enabled", false)
    }

    object Compatibility {
        val BrightnessManualWhenScreenOff =
            booleanSettingElement("Compatibility/BrightnessManualWhenScreenOff", true)
    }
}