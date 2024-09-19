package own.moderpach.extinguish.service.hosts

import android.content.Context
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import own.moderpach.extinguish.service.hosts.windows.FloatingButtonWindow
import own.moderpach.extinguish.service.hosts.windows.FloatingButtonWindow.Action
import own.moderpach.extinguish.service.hosts.windows.TimerSetterPopupWindow
import own.moderpach.extinguish.settings.data.SettingsTokens
import own.moderpach.extinguish.timer.data.TimersDatabase
import own.moderpach.extinguish.timer.data.TimersRepository

private const val TAG = "FloatingButtonHost"

class FloatingButtonHost<T>(
    private val owner: T,
    private var feature: Feature = Feature(),
    private var timersRepository: TimersRepository = TimersRepository(
        TimersDatabase.get(owner).timersDao(), owner.lifecycleScope
    ),
    private var isScreenOn: Boolean,
    private var onAction: (Action) -> Unit
) : LifecycleOwner by owner
        where T : Context, T : LifecycleOwner {

    var floatingButtonWindow: FloatingButtonWindow? = null
    var timerSetterPopupWindow: TimerSetterPopupWindow? = null

    sealed class Action {
        data object TurnScreenOff : Action()
        data class TurnScreenOffWithTimer(val seconds: Int) : Action()
        data object TurnScreenOn : Action()
    }

    data class Feature(
        val autoMoveToEdge: Boolean = SettingsTokens.FloatingButton.AutoMoveToEdge.default,
        val fadeWhenUnused: Boolean = SettingsTokens.FloatingButton.FadeWhenUnused.default,
        val fadeTransparency: Float = SettingsTokens.FloatingButton.FadeTransparency.default,
        val blackStyle: Boolean = SettingsTokens.FloatingButton.BlackStyle.default,
        val mergeTimerButton: Boolean = SettingsTokens.FloatingButton.MergeTimerButton.default,
    )

    var isShowing = false
        private set

    fun create() {
        if (floatingButtonWindow != null) return
        floatingButtonWindow = FloatingButtonWindow(owner, isScreenOn) { action ->
            Log.d(TAG, "FloatingButtonWindow | onAction: $action")
            when (action) {
                FloatingButtonWindow.Action.ShowTimerSetterDialog -> {
                    if (timerSetterPopupWindow == null) {
                        timerSetterPopupWindow = TimerSetterPopupWindow(
                            owner, timersRepository,
                            onDismiss = {
                                destroy()
                                timerSetterPopupWindow = null
                            },
                            onRequestLaunchTimer = {
                                onAction(Action.TurnScreenOffWithTimer(it.inSeconds()))
                            }
                        ).also {
                            it.popupBias = floatingButtonWindow?.boundaryRatio ?: Offset(0f, 0f)
                            it.create()
                        }
                    }
                }

                FloatingButtonWindow.Action.TurnScreenOff -> onAction(Action.TurnScreenOff)
                FloatingButtonWindow.Action.TurnScreenOn -> onAction(Action.TurnScreenOn)
            }
        }.also {
            it.updateFeature(
                FloatingButtonWindow.Feature(
                    feature.autoMoveToEdge,
                    feature.fadeWhenUnused,
                    feature.fadeTransparency,
                    feature.blackStyle,
                    feature.mergeTimerButton
                )
            )
            it.create()
        }
        isShowing = true
    }

    fun updateFeature(feature: Feature) {
        this.feature = feature

        floatingButtonWindow?.let {
            val updatingFeature = with(feature) {
                FloatingButtonWindow.Feature(
                    autoMoveToEdge, fadeWhenUnused, fadeTransparency, blackStyle, mergeTimerButton
                )
            }
            it.updateFeature(updatingFeature)
        }
    }

    fun updateScreenState(isScreenOn: Boolean) {
        this.isScreenOn = isScreenOn
        floatingButtonWindow?.updateScreenState(isScreenOn)
    }

    fun updateOnAction(onAction: (Action) -> Unit) {
        this.onAction = onAction
    }

    fun hide() {
        isShowing = false
        floatingButtonWindow?.hide()
    }

    fun show() {
        isShowing = true
        floatingButtonWindow?.show()
    }

    fun destroy() {
        if (floatingButtonWindow == null) return
        floatingButtonWindow?.destroy()
        isShowing = false
        floatingButtonWindow = null
        if (timerSetterPopupWindow == null) return
        timerSetterPopupWindow?.destroy()
        timerSetterPopupWindow = null
    }
}