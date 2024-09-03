package own.moderpach.extinguish.service.hosts.windows

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

abstract class ComposableWindow(
    context: Context,
    layoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        format = PixelFormat.RGBA_8888
        gravity = Gravity.START or Gravity.TOP
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
    }
) : LifecycleOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)

    fun handleLifecycleEvent(event: Lifecycle.Event) = lifecycleRegistry.handleLifecycleEvent(event)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    private val savedStateRegistryController =
        SavedStateRegistryController.create(this).apply {
            performRestore(null)
        }

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    protected open val windowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    protected open val mView = ComposeView(context).apply {
        setViewTreeLifecycleOwner(this@ComposableWindow)
        setViewTreeSavedStateRegistryOwner(this@ComposableWindow)
    }

    protected val mLayoutParams = layoutParams

    abstract fun create()

    abstract fun hide()

    abstract fun show()

    abstract fun destroy()

    @Composable
    abstract fun ComposeContent()
}

