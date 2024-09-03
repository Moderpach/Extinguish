package own.moderpach.extinguish.controller

import androidx.compose.ui.unit.dp

private const val Tag = "StopMarkOverlayWindow"
private val ConstraintToInsetsMinDistance = 40.dp // Only use when sdk below android R
private const val EnterTransitionDuration = 300
private const val EnterTransitionScaleInitial = 0.5f
private const val ExitTransitionDuration = 100

private val PointerInputExtendSize = 8.dp

/*
class StopMarkOverlayWindow<T>(
    context: T
) : ComposableWindow<T>(context)
        where T : Context,
              T : LifecycleOwner,
              T : SavedStateRegistryOwner,
              T : ViewModelStoreOwner {


    /**
     * Get current size of the window in pixel.
     * */
    var size = IntSize.Zero
        private set

    /**
     * Get mark position in window.
     * */
    var positionInWindow = Offset.Unspecified
        private set

    /**
     * Get point input extend size in px in current density.
     * */
    val pointerInputDetectedRange = Rect()

    private val constraintLayoutRange: Rect
        get() = run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Rect(windowManager.getScreenSafeRectIgnoringInsetsVisibility())
            } else {
                val screen = windowManager.getScreenRectNew()
                Rect(screen).apply {
                    top += Density(context).run { ConstraintToInsetsMinDistance.roundToPx() }
                }
            }
        }

    override fun create() {
        if (currentState != State.Destroyed) return
        if (targetState == State.Created) return

        targetState = State.Created

        mView.setContent {
            ExtinguishControllerTheme {
                Content()
            }
        }

        mLayoutParams.apply {
            addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)

            val screen = windowManager.getScreenRectNew()
            width = screen.width()
            height = screen.height()
        }

        mView.isInvisible = true
        windowManager.addView(
            mView, mLayoutParams
        )

        updatedConstraintLayoutRange = constraintLayoutRange
        currentState = State.Created
    }

    override fun show() {
        if (currentState == State.Destroyed) return
        if (targetState == State.Showing) return

        //Log.d(Tag, "#show")

        mView.isInvisible = false
        targetState = State.Showing
    }

    override fun hide() {
        if (currentState == State.Destroyed) return
        if (targetState == State.Hiding) return

        //Log.d(Tag, "#hide")
        if (currentState != State.Showing && targetState == State.Showing) {
            lifecycleScope.launch {
                delay(2)
                targetState = State.Hiding
            }
        } else {
            targetState = State.Hiding
        }
    }

    private fun onHide() {
        //Log.d(Tag, "#onHide")

        mView.isInvisible = true
        currentState = targetState
    }

    override fun destroy() {
        if (currentState == State.Destroyed) return
        if (targetState == State.Destroyed) return

        targetState = State.Destroyed
        if (currentState != State.Showing) onDestroy()
    }

    private fun onDestroy() {
        windowManager.removeView(mView)
        currentState = targetState
    }

    /**
     * Get updated content layout range.
     * Notice Content to update margin and window size class.
     * */
    private var updatedConstraintLayoutRange: Rect by mutableStateOf(Rect())

    /**
     * Update constraint layout range to fit configuration changing.
     * */
    private fun updateConstraintLayoutRange() {
        val screen = windowManager.getScreenRectNew()
        mLayoutParams.apply {
            width = screen.width()
            height = screen.height()
        }
        windowManager.updateViewLayout(mView, mLayoutParams)
        updatedConstraintLayoutRange = Rect(constraintLayoutRange)
    }

    /**
     * Get if pointer input in the stop area
     * */
    var isPointerInputOnStopMark by mutableStateOf(false)

    private fun Modifier.animatedVisibilityMark(
        visibility: Boolean,
        finishedListener: (Boolean) -> Unit
    ) = composed {


        val alphaTarget = if (visibility) 1f else 0f
        val animatedAlpha = animateFloatAsState(
            label = "",
            targetValue = alphaTarget,
            animationSpec = if (visibility) tween(
                durationMillis = EnterTransitionDuration,
                easing = FastOutSlowInEasing/*EaseOutCubic*/
            ) else tween(
                durationMillis = ExitTransitionDuration,
                easing = FastOutSlowInEasing/*EaseInCubic*/
            )
        )
        val scaleTarget = if (visibility)
            if (isPointerInputOnStopMark) 1.1f else 1f
        else EnterTransitionScaleInitial
        val animatedScale = animateFloatAsState(
            label = "",
            targetValue = scaleTarget,
            animationSpec = if (visibility) tween(
                durationMillis = EnterTransitionDuration,
                easing = FastOutSlowInEasing/*EaseOutCubic*/
            ) else tween(
                durationMillis = ExitTransitionDuration,
                easing = FastOutSlowInEasing/*EaseInCubic*/
            ),
            finishedListener = {
                //Log.d(Tag, "animatedVisibilityFinished")
                finishedListener(visibility)
            }
        )

        this.graphicsLayer {
            this.alpha = animatedAlpha.value
            this.scaleX = animatedScale.value
            this.scaleY = animatedScale.value
        }
    }

    @Composable
    override fun Content() {

        val configuration = LocalConfiguration.current
        LaunchedEffect(configuration) {
            updateConstraintLayoutRange()
        }

        val color = animateColorAsState(
            targetValue = if (isPointerInputOnStopMark) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surface,
            label = "StopControllerCover color animation"
        )
        val contentColor = animateColorAsState(
            targetValue = if (isPointerInputOnStopMark) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSurface,
            label = "StopControllerCover content color animation"
        )

        val density = LocalDensity.current
        val constrainMargin by remember(updatedConstraintLayoutRange) {
            mutableStateOf(
                density.run {
                    val screen = windowManager.getScreenRectNew()
                    val start = updatedConstraintLayoutRange.left - screen.left
                    val top = updatedConstraintLayoutRange.top - screen.top
                    val end = screen.right - updatedConstraintLayoutRange.right
                    val bottom = screen.bottom - updatedConstraintLayoutRange.bottom
                    PaddingValues(
                        start.toDp(), top.toDp(), end.toDp(), bottom.toDp()
                    )
                }
            )
        }

        ConstraintLayout(
            modifier = Modifier
                .padding(constrainMargin)
        ) {
            val popup = createRef()
            Surface(
                modifier = Modifier
                    .constrainAs(popup) {
                        centerHorizontallyTo(parent, 0.5f)
                        centerVerticallyTo(parent, 0.9f)
                    }
                    .onGloballyPositioned { coordinates ->
                        // This will be the size(px) of the Column.
                        size = coordinates.size
                        // The position of the Column relative to the application window.
                        positionInWindow = coordinates.positionInWindow()

                        Density(context)
                            .run {
                                PointerInputExtendSize.roundToPx()
                            }
                            .also {
                                pointerInputDetectedRange.apply {
                                    left = positionInWindow.x.toInt() - it
                                    top = positionInWindow.y.toInt() - it
                                    right = positionInWindow.x.toInt() + size.width + it
                                    bottom = positionInWindow.y.toInt() + size.height + it
                                }
                            }

                        //Log.d(Tag, "size $size")
                        //Log.d(Tag, "positionInWindow $positionInWindow")
                    }
                    .animatedVisibilityMark(
                        visibility = targetState == State.Showing,
                        finishedListener = {
                            when (targetState) {
                                State.Created -> Unit
                                State.Showing -> {
                                    currentState = targetState
                                    //Log.d(Tag, "#onShow")
                                }

                                State.Hiding -> onHide()
                                State.Destroyed -> onDestroy()
                            }
                        }
                    ),
                shape = CircleShape,
                color = color.value,
                contentColor = contentColor.value,
                border = BorderStroke(
                    0.4.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.stop_20px),
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(id = R.string.stop),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600
                    )
                }
            }

        }
    }
}*/