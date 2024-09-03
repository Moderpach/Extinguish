package own.moderpach.extinguish.ui.ext

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import own.moderpach.extinguish.util.ext.addFlags

abstract class ComposableWindow<T>(
    val context: T
) where T : Context,
        T : LifecycleOwner,
        T : SavedStateRegistryOwner,
        T : ViewModelStoreOwner {
    //Lifecycle
    protected open val lifecycle get() = context.lifecycle
    protected open val lifecycleScope get() = context.lifecycleScope

    //State
    open var currentState by mutableStateOf(State.Destroyed)
        protected set
    open var targetState by mutableStateOf(State.Destroyed)
        protected set

    enum class State {
        Created, Showing, Hiding, Destroyed
    }

    //WindowManager
    protected open val windowManager = context
        .getSystemService(Context.WINDOW_SERVICE) as WindowManager

    //View & LayoutParam
    protected open val mView = ComposeView(context).apply {
        setViewTreeLifecycleOwner(this@ComposableWindow.context)
        setViewTreeSavedStateRegistryOwner(this@ComposableWindow.context)
        setViewTreeViewModelStoreOwner(this@ComposableWindow.context)
    }

    protected open val mLayoutParams = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        format = PixelFormat.RGBA_8888
        gravity = Gravity.START or Gravity.TOP
    }

    abstract fun create()

    abstract fun destroy()

    abstract fun show()

    abstract fun hide()

    @Composable
    protected abstract fun Content()

}