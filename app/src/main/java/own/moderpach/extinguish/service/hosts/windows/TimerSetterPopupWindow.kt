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
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import own.moderpach.extinguish.R
import own.moderpach.extinguish.timer.components.BackSpaceIcon
import own.moderpach.extinguish.timer.components.NumberKeyBoard
import own.moderpach.extinguish.timer.components.NumberKeyboardEvent
import own.moderpach.extinguish.timer.components.NumberKeyboardEvent.BackSpace
import own.moderpach.extinguish.timer.components.NumberKeyboardEvent.Number
import own.moderpach.extinguish.timer.components.TimerDisplay
import own.moderpach.extinguish.timer.components.TimerDisplayDefault
import own.moderpach.extinguish.timer.data.ITimersRepository
import own.moderpach.extinguish.timer.data.TimerLiteral
import own.moderpach.extinguish.ui.theme.ExtinguishThemeForNonActivityContext
import own.moderpach.extinguish.util.ext.addFlags
import own.moderpach.extinguish.util.getScreenSafeRectIgnoringInsetsVisibility
import kotlin.math.pow

private const val TAG = "TimerSetterPopupWindow"
private val PopupMargin = 4.dp
private val EnterAnimationSpec = spring(
    stiffness = Spring.StiffnessMedium,
    visibilityThreshold = 0.001f
)
private val ExitAnimationSpec = spring(
    stiffness = Spring.StiffnessMedium,
    visibilityThreshold = 0.01f
)

class TimerSetterPopupWindow(
    val context: Context,
    val timersRepository: ITimersRepository,
    var onDismiss: TimerSetterPopupWindow.() -> Unit,
    var onRequestLaunchTimer: (TimerLiteral) -> Unit
) : ComposableWindow(
    context,
    WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        format = PixelFormat.RGBA_8888
        gravity = Gravity.START or Gravity.TOP

        addFlags(FLAG_NOT_TOUCH_MODAL)
        addFlags(FLAG_NOT_FOCUSABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //config x, y, width, height later
            addFlags(FLAG_LAYOUT_NO_LIMITS)
            addFlags(FLAG_LAYOUT_IN_SCREEN)
        } else {
            x = 0
            y = 0
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
        }
        addFlags(FLAG_SPLIT_TOUCH)
    }
) {

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
    /*private val enterExitTransformOrigin by mutableStateOf(
        Offset(0f, 0f)
    )*/

    /**
     * Preset timers
     * */
    private val presetTimers = timersRepository.readAll()

    /**
     * Tracing window position with its boundary ratio as the
     * location baseline in different screen size.
     * This should be set with  before [create]
     * */
    //todo: 这里要用dialog offset 和 transform source offset 窗口大小是全屏
    var popupBias by mutableStateOf(Offset(0f, 0f))

    private fun onConfigurationChange() {
        Log.d(TAG, "onConfigurationChange")
        if (!mView.isAttachedToWindow) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowRect = windowManager.getScreenSafeRectIgnoringInsetsVisibility()
            mLayoutParams.x = windowRect.left
            mLayoutParams.y = windowRect.top
            mLayoutParams.width = windowRect.width()
            mLayoutParams.height = windowRect.height()
        }
        windowManager.updateViewLayout(mView, mLayoutParams)
    }

    override fun create() {
        Log.d(TAG, "create: ")
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        mView.setContent { ComposeContent() }
        if (!mView.isAttachedToWindow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowRect = windowManager.getScreenSafeRectIgnoringInsetsVisibility()
                mLayoutParams.x = windowRect.left
                mLayoutParams.y = windowRect.top
                mLayoutParams.width = windowRect.width()
                mLayoutParams.height = windowRect.height()
            }
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

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun ComposeContent() = ExtinguishThemeForNonActivityContext {
        val lifecycleOwner = LocalLifecycleOwner.current
        val coroutineScope = rememberCoroutineScope()

        val configuration = LocalConfiguration.current
        // 变化只会再onResumed时感知，resume后会立即感知最新的变化
        LaunchedEffect(configuration) {
            onConfigurationChange()
        }

        val presetTimers = presetTimers.collectAsStateWithLifecycle(
            emptyList()
        ).value

        val boarder = BorderStroke(
            0.4.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Box(
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onDismiss()
                    }
                )
            }
        )

        ConstraintLayout {
            val popupRef = createRef()
            Surface(
                modifier = Modifier
                    .constrainAs(popupRef) {
                        centerHorizontallyTo(parent, popupBias.x)
                        centerVerticallyTo(parent, popupBias.y)
                    }
                    .padding(PopupMargin)
                    .graphicsLayer {
                        // add dead zone and nonlinear for enter exit alpha
                        val enterExitAlpha = if (enterExitAnimatable.value < 0.25f) 0f
                        else ((enterExitAnimatable.value - 0.255f) / 0.75f).pow(0.5f)
                        alpha = enterExitAlpha
                        scaleX = enterExitAnimatable.value
                        scaleY = enterExitAnimatable.value
                        transformOrigin = TransformOrigin(popupBias.x, popupBias.y)
                    },
                shape = MaterialTheme.shapes.large,
                contentColor = MaterialTheme.colorScheme.onSurface,
                color = MaterialTheme.colorScheme.surfaceContainer,
                border = boarder
            ) {
                val pagerState = rememberPagerState(pageCount = {
                    2
                })
                val pagerTitles = listOf(
                    stringResource(R.string.Presets),
                    stringResource(R.string.Keyboard)
                )
                Column(
                    Modifier.width(250.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.height(250.dp)
                    ) { page ->
                        // Our page content
                        when (page) {
                            0 -> PresetsPager(
                                modifier = Modifier.fillMaxSize(),
                                presetTimers = presetTimers,
                                onRequestLaunchTimer = {
                                    onRequestLaunchTimer(it)
                                    onDismiss()
                                }
                            )

                            1 -> KeyboardPager(
                                modifier = Modifier.fillMaxSize(),
                                onTimerSave = {
                                    timersRepository.insert(it)
                                },
                                onRequestLaunchTimer = {
                                    onRequestLaunchTimer(it)
                                    onDismiss()
                                }
                            )

                            else -> Unit
                        }
                    }

                    PrimaryTabRow(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = Color.Transparent,
                        divider = {}
                    ) {
                        pagerTitles.forEachIndexed { index, title ->
                            Tab(
                                modifier = Modifier
                                    .height(32.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            topStartPercent = 25, topEndPercent = 25
                                        )
                                    ),
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                text = {
                                    Text(
                                        text = title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        OnEnterExitEvent(
            lifecycleOwner,
            enterExitRequestFlow,
            onEnter = {
                coroutineScope.launch {
                    enterExitAnimatable.animateTo(
                        EnterExitAnimationDefault.EnterTarget,
                        EnterAnimationSpec
                    )
                }
            },
            onExit = {
                coroutineScope.launch {
                    enterExitAnimatable.animateTo(
                        EnterExitAnimationDefault.ExitTarget,
                        ExitAnimationSpec
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
private fun PresetsPager(
    modifier: Modifier = Modifier,
    presetTimers: List<TimerLiteral>,
    onRequestLaunchTimer: (TimerLiteral) -> Unit
) {
    LazyColumn(
        modifier
    ) {
        items(presetTimers) { timer ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .clickable { onRequestLaunchTimer(timer) },
                Alignment.Center
            ) {
                val timerText = buildAnnotatedString {
                    if (timer.hours != 0) {
                        append(timer.hours.toString())
                        append(stringResource(R.string.str_Hour))
                    }
                    if (timer.minutes != 0) {
                        append(timer.minutes.toString())
                        append(stringResource(R.string.str_Minute))
                    }
                    if (timer.seconds != 0) {
                        append(timer.seconds.toString())
                        append(stringResource(R.string.str_Second))
                    }
                }
                Text(
                    timerText,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun KeyboardPager(
    modifier: Modifier = Modifier,
    onRequestLaunchTimer: (TimerLiteral) -> Unit,
    onTimerSave: (TimerLiteral) -> Unit,
) {
    Column(
        modifier = modifier.padding(
            top = 16.dp,
            bottom = 4.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var timer by remember {
            mutableStateOf(TimerLiteral.zero())
        }
        val onKeyBoardEvent: (NumberKeyboardEvent) -> Unit = {
            when (it) {
                BackSpace -> try {
                    timer = timer.pop()
                } catch (_: Exception) {
                }

                is Number -> try {
                    timer = timer.push(it.num)
                } catch (_: Exception) {
                }

                NumberKeyboardEvent.Clear -> timer = TimerLiteral.zero()
            }
        }

        TimerDisplay(
            timer = timer,
            style = TimerDisplayDefault.ExtraSmall,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            NumberKeyBoard(
                modifier = Modifier.aspectRatio(3.5f / 4),
                textStyle = MaterialTheme.typography.bodyMedium,
                backSpaceIcon = BackSpaceIcon.Small,
                onEvent = onKeyBoardEvent
            )
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            onTimerSave(timer)
                            timer = TimerLiteral.zero()
                        }
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .width(36.dp)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(R.drawable.save_20px),
                        stringResource(R.string.Save)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            onRequestLaunchTimer(timer)
                            timer = TimerLiteral.zero()
                        }
                        .background(MaterialTheme.colorScheme.primary)
                        .width(36.dp)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(R.drawable.play_arrow_20px),
                        stringResource(R.string.Launch_timer),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}