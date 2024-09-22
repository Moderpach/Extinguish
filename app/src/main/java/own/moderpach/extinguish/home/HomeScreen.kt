package own.moderpach.extinguish.home

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import kotlinx.coroutines.delay
import own.moderpach.extinguish.ExtinguishNavGraph
import own.moderpach.extinguish.ExtinguishNavRoute
import own.moderpach.extinguish.LocalSolutionDependencyManager
import own.moderpach.extinguish.LocalSystemPermissionsManager
import own.moderpach.extinguish.Permission
import own.moderpach.extinguish.R
import own.moderpach.extinguish.SpecificPermission
import own.moderpach.extinguish.home.cards.externalControl
import own.moderpach.extinguish.home.cards.floatingButton
import own.moderpach.extinguish.home.cards.moreSettings
import own.moderpach.extinguish.home.cards.notificationControl
import own.moderpach.extinguish.home.cards.screenEventControl
import own.moderpach.extinguish.home.cards.solution
import own.moderpach.extinguish.home.cards.tileControl
import own.moderpach.extinguish.home.cards.volumeKeyControl
import own.moderpach.extinguish.service.ExtinguishService
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.test.FakeSettingsRepository
import own.moderpach.extinguish.ui.components.ExtinguishTopAppBar
import own.moderpach.extinguish.ui.navigation.extinguishComposable
import own.moderpach.extinguish.ui.theme.ExtinguishTheme
import own.moderpach.extinguish.util.add

val ExtinguishNavGraph.Home: ExtinguishNavRoute get() = "Home"

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun NavGraphBuilder.home(
    settingsRepository: ISettingsRepository,
    onNavigateTo: (ExtinguishNavRoute) -> Unit
) = extinguishComposable(
    ExtinguishNavGraph.Home,
) {
    val extinguishServiceState by ExtinguishService.state.collectAsStateWithLifecycle()
    val context = LocalContext.current as Activity
    val windowSizeClass = calculateWindowSizeClass(context)
    val solutionsDependencyManager = LocalSolutionDependencyManager.current
    val solutionState by solutionsDependencyManager.state.collectAsStateWithLifecycle()
    val systemPermissionsManager = LocalSystemPermissionsManager.current
    val permissionRequestLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}
    val largerFAB = windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Medium &&
            windowSizeClass.heightSizeClass >= WindowHeightSizeClass.Medium

    HomeScreen(
        extinguishServiceState,
        settingsRepository,
        largerFAB,
        onNavigateTo
    ) {
        when (ExtinguishService.state.value) {
            ExtinguishService.State.Destroyed -> {
                solutionsDependencyManager.requestShizukuPermission { }
                systemPermissionsManager.requestSpecial(SpecificPermission.CanDrawOverlays)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    systemPermissionsManager.request(
                        Permission.PostNotification,
                        {},
                        permissionRequestLauncher,
                        context
                    )
                }
                solutionsDependencyManager.updateImmediately()
                if (!solutionState.isShizukuPermissionGranted) return@HomeScreen
                if (
                    settingsRepository.floatingButton.enabled &&
                    !systemPermissionsManager.checkSpecial(SpecificPermission.CanDrawOverlays)
                ) return@HomeScreen

                val intent = Intent(
                    context,
                    ExtinguishService::class.java
                )
                context.startService(intent)
            }

            ExtinguishService.State.Created -> Unit
            else -> Intent(
                context,
                ExtinguishService::class.java
            ).let {
                context.stopService(it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    extinguishServiceState: ExtinguishService.State,
    settingsRepository: ISettingsRepository,
    largerFAB: Boolean,
    onNavigateTo: (ExtinguishNavRoute) -> Unit,
    onRequestService: () -> Unit
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val gridState = rememberLazyStaggeredGridState()
    val coroutineScope = rememberCoroutineScope()

    val cardList = listOf(
        HomeScreenCardKeys.solution,
        HomeScreenCardKeys.floatingButton,
        HomeScreenCardKeys.volumeKeyControl,
        HomeScreenCardKeys.screenEventControl,
        HomeScreenCardKeys.notificationControl,
        HomeScreenCardKeys.tileControl,
        HomeScreenCardKeys.externalControl,
        HomeScreenCardKeys.moreSettings,
    )

    val fabHeight = if (largerFAB) (96 + 16).dp else (56 + 16).dp

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.union(WindowInsets.displayCutout),
        topBar = {
            ExtinguishTopAppBar(
                titleString = stringResource(R.string.app_name),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ServiceButton(
                state = extinguishServiceState,
                isLarge = largerFAB,
                onClick = onRequestService
            )
        }
    ) { innerPadding ->
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            state = gridState,
            columns = StaggeredGridCells.Adaptive(300.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp,
            contentPadding = innerPadding.add(horizontal = 12.dp).add(bottom = fabHeight),
            content = {
                homeScreenCards(
                    cardList,
                    extinguishServiceState,
                    settingsRepository,
                    onNavigateTo,
                    onRequestService
                )
            }
        )
    }

}

@Composable
private fun ServiceButton(
    modifier: Modifier = Modifier,
    state: ExtinguishService.State,
    isLarge: Boolean,
    onClick: () -> Unit
) = if (isLarge) {
    LargeFloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        val contentColor: Color = LocalContentColor.current
        AnimatedContent(
            state,
            transitionSpec = {
                fadeIn() + slideInVertically { it } togetherWith
                        fadeOut() + slideOutVertically { -it } using
                        SizeTransform()
            },
            label = "service button in solution card"
        ) {
            when (it) {
                ExtinguishService.State.Destroyed -> {
                    Icon(
                        painterResource(R.drawable.play_arrow_40px),
                        stringResource(R.string.Start)
                    )
                }

                ExtinguishService.State.Created -> {
                    val contentDescription = stringResource(R.string.Starting)
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(28.dp)
                            .semantics {
                                this.contentDescription = contentDescription
                                this.role = Role.Image
                            },
                        color = contentColor,
                        strokeWidth = 3.dp
                    )
                }

                else -> {
                    Icon(painterResource(R.drawable.stop_40px), stringResource(R.string.Stop))
                }
            }
        }
    }
} else {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        val contentColor: Color = LocalContentColor.current
        AnimatedContent(
            state,
            transitionSpec = {
                fadeIn() + slideInVertically { it } togetherWith
                        fadeOut() + slideOutVertically { -it } using
                        SizeTransform()
            },
            label = "service button in solution card"
        ) {
            when (it) {
                ExtinguishService.State.Destroyed -> {
                    Icon(
                        painterResource(R.drawable.play_arrow_24px),
                        stringResource(R.string.Start)
                    )
                }

                ExtinguishService.State.Created -> {
                    val contentDescription = stringResource(R.string.Starting)
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(16.dp)
                            .semantics {
                                this.contentDescription = contentDescription
                                this.role = Role.Image
                            },
                        color = contentColor,
                        strokeWidth = 2.dp
                    )
                }

                else -> {
                    Icon(painterResource(R.drawable.stop_24px), stringResource(R.string.Stop))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ServiceButtonPreview() = ExtinguishTheme {
    var currentState by remember {
        mutableStateOf(ExtinguishService.State.Destroyed)
    }
    Surface {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                ServiceButton(state = ExtinguishService.State.Destroyed, isLarge = false) {}
                ServiceButton(state = ExtinguishService.State.Created, isLarge = false) {}
                ServiceButton(state = ExtinguishService.State.Prepared, isLarge = false) {}
                ServiceButton(state = ExtinguishService.State.Error, isLarge = false) {}
                ServiceButton(state = currentState, isLarge = false) {}
            }
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                ServiceButton(state = ExtinguishService.State.Destroyed, isLarge = true) {}
                ServiceButton(state = ExtinguishService.State.Created, isLarge = true) {}
                ServiceButton(state = ExtinguishService.State.Prepared, isLarge = true) {}
                ServiceButton(state = ExtinguishService.State.Error, isLarge = true) {}
                ServiceButton(state = currentState, isLarge = true) {}
            }
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            currentState = ExtinguishService.State.Destroyed
            delay(2000)
            currentState = ExtinguishService.State.Created
            delay(2000)
            currentState = ExtinguishService.State.Prepared
            delay(2000)
            currentState = ExtinguishService.State.Error
            delay(2000)
        }
    }
}


@Preview
@Composable
private fun HomeScreenPreviewNormalFAB() = ExtinguishTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        HomeScreen(
            extinguishServiceState = ExtinguishService.State.Prepared,
            settingsRepository = FakeSettingsRepository(),
            largerFAB = false,
            { }
        ) {}
    }
}

@Preview
@Composable
private fun HomeScreenPreviewLargerFAB() = ExtinguishTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        HomeScreen(
            extinguishServiceState = ExtinguishService.State.Prepared,
            settingsRepository = FakeSettingsRepository(),
            largerFAB = true,
            { }
        ) {}
    }
}