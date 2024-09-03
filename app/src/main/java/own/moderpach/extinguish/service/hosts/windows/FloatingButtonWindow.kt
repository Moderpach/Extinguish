package own.moderpach.extinguish.service.hosts.windows

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
import android.view.WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import own.moderpach.extinguish.R
import own.moderpach.extinguish.settings.data.SettingsTokens
import own.moderpach.extinguish.ui.theme.ExtinguishThemeForNonActivityContext
import own.moderpach.extinguish.util.ext.addFlags
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

private const val TAG = "FloatingButtonWindow"

private val Margin get() = 2.dp
private val InnerPadding get() = 6.dp
private val ItemSpacing get() = 2.dp
private val FloatingButtonSize get() = 36.dp

/**
 * Intrinsic window size to calculate window position.
 * */
private val IntrinsicWindowSizeDp = DpSize(
    width = InnerPadding * 2 + FloatingButtonSize + Margin * 2,
    height = InnerPadding * 2 + FloatingButtonSize * 2 + ItemSpacing + Margin * 2
)

class FloatingButtonWindow(
    val context: Context,
    isScreenOn: Boolean,
    var onAction: (Action) -> Unit,
) : ComposableWindow(
    context,
    WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        format = PixelFormat.RGBA_8888
        gravity = Gravity.START or Gravity.TOP
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT

        addFlags(FLAG_NOT_TOUCH_MODAL)
        addFlags(FLAG_NOT_FOCUSABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            addFlags(FLAG_LAYOUT_NO_LIMITS)
            addFlags(FLAG_LAYOUT_IN_SCREEN)
        }
        addFlags(FLAG_SPLIT_TOUCH)
    }
) {

    sealed class Action {
        data object ShowTimerSetterDialog : Action()
        data object TurnScreenOff : Action()
        data object TurnScreenOn : Action()
    }

    /**
     * process of enter exit with animation
     *
     * ## Enter
     *
     * 1. [create] - add view, lifecycle to onResume, send enter request to [enterExitRequestChannel].
     * 2. composition complete - collect [enterExitRequestFlow], receive enter request.
     * 3. animate to enter.
     *
     * ## Exit
     * 1. [destroy] - send exit request to [enterExitRequestChannel] or remove view if lifecycle onPause.
     * 2. [enterExitRequestFlow] is collecting, receive exit request.
     * 3. animate to exit.
     * 4. remove view.
     * */
    private val enterExitRequestChannel = Channel<EnterExitEvent>()
    private val enterExitRequestFlow = enterExitRequestChannel.receiveAsFlow()
    private val enterExitAnimatable = Animatable(0f)

    /**
     * Screen state
     * */
    private var isScreenOn by mutableStateOf(isScreenOn)

    fun updateScreenState(isScreenOn: Boolean) {
        this.isScreenOn = isScreenOn
    }

    /**
     * Feature for floating button window.
     *
     * usage chain:
     * [_feature]: MutableStateFlow -> [feature]: StateFlow -> feature (in ComposeContent): State
     * */
    data class Feature(
        val autoMoveToEdge: Boolean = SettingsTokens.FloatingButton.AutoMoveToEdge.default,
        val fadeWhenUnused: Boolean = SettingsTokens.FloatingButton.FadeWhenUnused.default,
        val fadeTransparency: Float = SettingsTokens.FloatingButton.FadeTransparency.default,
        val blackStyle: Boolean = SettingsTokens.FloatingButton.BlackStyle.default,
        val mergeTimerButton: Boolean = SettingsTokens.FloatingButton.MergeTimerButton.default,
    )

    private val _feature = MutableStateFlow(Feature())
    val feature: StateFlow<Feature> = _feature

    fun updateFeature(feature: Feature) {
        _feature.update { feature }
    }

    /**
     * Manage fade when unused timer
     * */
    interface IFadeWhenUnusedTimer {
        fun resetAndLaunch()
        fun cancel()
    }

    val fadeWhenUnusedTimer = object : IFadeWhenUnusedTimer {
        private var timer: Job? = null

        override fun resetAndLaunch() {
            timer?.cancel()
            fadeState.update { false }
            timer = lifecycleScope.launch {
                delay(3000) // 3s
                if (isActive) fadeState.update { true }
            }
        }

        override fun cancel() {
            timer?.cancel()
            fadeState.update { false }
        }
    }
    private val fadeState = MutableStateFlow(false)

    /**
     * Tracing window position with its boundary ratio as the
     * location baseline in different screen size.
     * This should be update with [updateBoundaryRatio] when dragging or
     * auto moving to edge completely.
     * */
    var boundaryRatio = Offset(0f, 0f)
        private set

    /**
     * update [boundaryRatio] when dragging or
     * auto moving to edge completely.
     * */
    private fun updateBoundaryRatio() {
        boundaryRatio = positionBoundary.calculateBoundaryRatio(
            mLayoutParams.x,
            mLayoutParams.y
        )
    }

    /**
     * Tracing intrinsic window size.
     * This should be update with [calculateIntrinsicWindowSize]
     * when configuration changed.
     * */
    var intrinsicWindowSize = calculateIntrinsicWindowSize()
        private set

    /**
     * Calculate intrinsic window size with current density.
     * */
    fun calculateIntrinsicWindowSize(): IntSize {
        return with(Density(context)) {
            val width = IntrinsicWindowSizeDp.width.roundToPx()
            val height = IntrinsicWindowSizeDp.height.roundToPx()
            IntSize(width, height)
        }
    }

    var positionBoundary = windowManager
        .calculateSafePositionBoundary(
            intrinsicWindowSize
        )


    private fun onConfigurationChange() {
        Log.d(TAG, "onConfigurationChange")
        if (!mView.isAttachedToWindow) return
        intrinsicWindowSize = calculateIntrinsicWindowSize()
        positionBoundary = windowManager.calculateSafePositionBoundary(
            intrinsicWindowSize
        )
        updateBoundaryRatio()
        positionBoundary.calculatePosition(
            boundaryRatio
        ).let { (x, y) ->
            mLayoutParams.x = x
            mLayoutParams.y = y
        }
        windowManager.updateViewLayout(mView, mLayoutParams)
    }

    /**
     * Auto move to edge
     * */
    suspend fun autoMoveToEdge() {
        val target = if (mLayoutParams.x <= positionBoundary.centerX())
            positionBoundary.left
        else positionBoundary.right

        animate(
            initialValue = mLayoutParams.x,
            targetValue = target,
            typeConverter = Int.VectorConverter,
            animationSpec = spring(
                stiffness = Spring.StiffnessMediumLow
            ),
            block = { value, velocity ->
                mLayoutParams.x = value
                windowManager.updateViewLayout(mView, mLayoutParams)
            }
        )
        updateBoundaryRatio()
    }


    override fun create() {
        Log.d(TAG, "create: ")
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        mLayoutParams.apply {
            x = positionBoundary.left
            y = positionBoundary.top
        }
        mView.setContent { ComposeContent() }
        if (!mView.isAttachedToWindow) {
            windowManager.addView(mView, mLayoutParams)
            Log.d(TAG, "addedView")
        }
        lifecycleScope.launch {
            Log.d(TAG, "send enter request")
            enterExitRequestChannel.send(EnterExitEvent.Enter)
            Log.d(TAG, "sent enter request")
        }
    }

    override fun hide() {
        Log.d(TAG, "hide: ")
        handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        mView.isVisible = false
    }

    override fun show() {
        Log.d(TAG, "show: ")
        mView.isVisible = true
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun destroy() {
        Log.d(TAG, "destroy: ")
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            lifecycleScope.launch {
                Log.d(TAG, "send exit request")
                enterExitRequestChannel.send(EnterExitEvent.Exit)
                Log.d(TAG, "sent exit request")
            }
        } else if (mView.isAttachedToWindow) {
            handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            windowManager.removeView(mView)
        }
    }

    @Composable
    override fun ComposeContent() = ExtinguishThemeForNonActivityContext {
        val lifecycleOwner = LocalLifecycleOwner.current
        val coroutineScope = rememberCoroutineScope()
        val feature = this@FloatingButtonWindow.feature.collectAsStateWithLifecycle().value
        val fadeState = this@FloatingButtonWindow.fadeState.collectAsStateWithLifecycle().value

        val configuration = LocalConfiguration.current
        // 变化只会再onResumed时感知，resume后会立即感知最新的变化
        LaunchedEffect(configuration) {
            onConfigurationChange()
        }

        val containerColor = if (feature.blackStyle) Color.Black
        else MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)

        val boarder = BorderStroke(
            0.4.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        val fadeAlpha by animateFloatAsState(
            targetValue = if (fadeState) feature.fadeTransparency
            else 1f,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "fade when unused"
        )

        Surface(
            shape = CircleShape,
            color = containerColor,
            modifier = Modifier
                .graphicsLayer {
                    // add dead zone and nonlinear for enter exit alpha
                    val enterExitAlpha = if (enterExitAnimatable.value < 0.375f) 0f
                    else ((enterExitAnimatable.value - 0.375f) / 0.625f).pow(0.75f)
                    alpha = min(fadeAlpha, enterExitAlpha)
                    scaleX = enterExitAnimatable.value
                    scaleY = enterExitAnimatable.value
                    transformOrigin = TransformOrigin(boundaryRatio.x, boundaryRatio.y)
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            fadeWhenUnusedTimer.cancel()
                        },
                        onDrag = { change, dragAmount ->
                            mLayoutParams.x = mLayoutParams.x
                                .plus(dragAmount.x.roundToInt())
                                .coerceIn(positionBoundary.left, positionBoundary.right)
                            mLayoutParams.y = mLayoutParams.y
                                .plus(dragAmount.y.roundToInt())
                                .coerceIn(positionBoundary.top, positionBoundary.bottom)
                            windowManager.updateViewLayout(
                                mView,
                                mLayoutParams
                            )
                        },
                        onDragEnd = {
                            updateBoundaryRatio()
                            fadeWhenUnusedTimer.resetAndLaunch()
                            if (feature.autoMoveToEdge) coroutineScope.launch {
                                autoMoveToEdge()
                            }
                        },
                        onDragCancel = {
                            updateBoundaryRatio()
                            fadeWhenUnusedTimer.resetAndLaunch()
                            if (feature.autoMoveToEdge) coroutineScope.launch {
                                autoMoveToEdge()
                            }
                        }
                    )
                }
                .padding(Margin),
            border = boarder
        ) {
            val animatedHeight: Animatable<Int, AnimationVector1D> = remember {
                Animatable(0, Int.VectorConverter)
            }
            Layout(
                modifier = Modifier.padding(InnerPadding),
                content = {
                    val onMainButtonLongClick = if (feature.mergeTimerButton) {
                        {
                            fadeWhenUnusedTimer.resetAndLaunch()
                            onAction.invoke(Action.ShowTimerSetterDialog)
                        }
                    } else {
                        null
                    }
                    FloatingButton(
                        icon = {
                            val iconRotation = animateFloatAsState(
                                if (isScreenOn) 0f else 180f,
                                animationSpec = spring(),
                                label = "switch screen state button icon rotation"
                            )
                            Icon(
                                painterResource(R.drawable.extinguish_20px),
                                if (isScreenOn) "turn screen off" else "turn screen on",
                                modifier = Modifier.graphicsLayer {
                                    rotationZ = iconRotation.value
                                }
                            )
                        },
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            fadeWhenUnusedTimer.resetAndLaunch()
                            if (isScreenOn) {
                                onAction.invoke(Action.TurnScreenOff)
                            } else {
                                onAction.invoke(Action.TurnScreenOn)
                            }
                        },
                        onLongClick = onMainButtonLongClick
                    )
                    AnimatedVisibility(
                        !feature.mergeTimerButton && isScreenOn,
                        enter = fadeIn(spring(stiffness = Spring.StiffnessMediumLow)),
                        exit = fadeOut(spring(stiffness = Spring.StiffnessMedium)),
                        modifier = Modifier.clipToBounds()
                    ) {
                        FloatingButton(
                            icon = painterResource(R.drawable.timer_20px),
                            enabled = isScreenOn,
                            contentDescription = "open timer setter dialog",
                            onClick = {
                                fadeWhenUnusedTimer.resetAndLaunch()
                                onAction.invoke(Action.ShowTimerSetterDialog)
                            },
                        )
                    }
                },
                measurePolicy = { measurables, constraints ->
                    val placeables = measurables.map { measurable ->
                        measurable.measure(constraints)
                    }
                    val height =
                        placeables.sumOf { it.height } + ItemSpacing.roundToPx() * (placeables.size - 1)
                    coroutineScope.launch {
                        if (animatedHeight.value == 0) animatedHeight.snapTo(height)
                        else animatedHeight.animateTo(height, spring())
                    }
                    layout(
                        placeables.maxOf { it.width },
                        if (animatedHeight.value == 0) height else animatedHeight.value
                    ) {
                        var yPosition = 0
                        placeables.forEach { placeable ->
                            placeable.placeRelative(x = 0, y = yPosition)
                            yPosition += placeable.height + ItemSpacing.roundToPx()
                        }
                    }
                }
            )
        }

        LaunchedEffect(feature.autoMoveToEdge) {
            if (feature.autoMoveToEdge) autoMoveToEdge()
        }

        LaunchedEffect(feature.fadeWhenUnused, lifecycleOwner) {
            if (feature.fadeWhenUnused && lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
                fadeWhenUnusedTimer.resetAndLaunch()
            else fadeWhenUnusedTimer.cancel()
        }

        OnEnterExitEvent(
            lifecycleOwner,
            enterExitRequestFlow,
            onEnter = {
                coroutineScope.launch {
                    enterExitAnimatable.animateTo(
                        EnterExitAnimationDefault.EnterTarget,
                        EnterExitAnimationDefault.EnterAnimationSpec
                    )
                }
            },
            onExit = {
                coroutineScope.launch {
                    enterExitAnimatable.animateTo(
                        EnterExitAnimationDefault.ExitTarget,
                        EnterExitAnimationDefault.ExitAnimationSpec
                    )
                    if (isActive) {
                        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                        if (mView.isAttachedToWindow) windowManager.removeView(mView)
                        Log.d(TAG, "removedView")
                    }
                }
            }
        )
    }

}

@Composable
private fun FloatingButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    enabled: Boolean = true,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    containerColor: Color = Color.Transparent,
    contentDescription: String,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    FloatingButton(
        modifier,
        icon = {
            Icon(icon, contentDescription)
        },
        enabled, contentColor, containerColor,
        onClick, onLongClick
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FloatingButton(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    enabled: Boolean = true,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    containerColor: Color = Color.Transparent,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
) {
    Box(
        modifier
            .clip(CircleShape)
            .requiredSize(FloatingButtonSize)
            .background(containerColor)
            .combinedClickable(
                enabled = enabled,
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor
        ) {
            icon()
        }
    }
}
