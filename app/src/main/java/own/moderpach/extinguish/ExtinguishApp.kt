package own.moderpach.extinguish

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import own.moderpach.extinguish.guide.GuideAgreement
import own.moderpach.extinguish.guide.GuideShizukuRunning
import own.moderpach.extinguish.guide.GuideSolution
import own.moderpach.extinguish.guide.guideAgreement
import own.moderpach.extinguish.guide.guideShizukuRunning
import own.moderpach.extinguish.guide.guideSolution
import own.moderpach.extinguish.home.Home
import own.moderpach.extinguish.home.home
import own.moderpach.extinguish.settings.about
import own.moderpach.extinguish.settings.compatible
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.data.SettingsTokens
import own.moderpach.extinguish.settings.data.settingsDataStore
import own.moderpach.extinguish.settings.externalControl
import own.moderpach.extinguish.settings.floatingButton
import own.moderpach.extinguish.settings.solution
import own.moderpach.extinguish.settings.volumeKeyControl
import own.moderpach.extinguish.timer.data.ITimersRepository
import own.moderpach.extinguish.timer.timerPreset
import own.moderpach.extinguish.timer.timerPresetDialog
import own.moderpach.extinguish.ui.theme.ExtinguishTheme

private const val TAG = "ExtinguishApp"

@Composable
fun ExtinguishApp(
    settingsRepository: ISettingsRepository,
    timersRepository: ITimersRepository
) = ExtinguishTheme {
    val context = LocalContext.current
    val solutionDependencyManager = LocalSolutionDependencyManager.current
    val solutionState by solutionDependencyManager.state.collectAsState()
    val navController = rememberNavController()

    val onNavigateTo: (ExtinguishNavRoute) -> Unit = {
        navController.navigate(it)
    }
    val onBack: () -> Unit = {
        navController.popBackStack()
    }

    var startDestination by remember {
        mutableStateOf(ExtinguishNavGraph.Home)
    }

    LaunchedEffect(solutionState) {
        if (
            startDestination == ExtinguishNavGraph.GuideShizukuRunning ||
            startDestination == ExtinguishNavGraph.GuideAgreement ||
            startDestination == ExtinguishNavGraph.GuideSolution
        ) return@LaunchedEffect
        val initVersion = context.settingsDataStore.data.map {
            it[SettingsTokens.InitVersion.key] ?: SettingsTokens.InitVersion.default
        }.first()
        if (initVersion < BuildExt.INIT_VERSION) {
            val deviceRegion = Locale.current.region
            startDestination = if (deviceRegion == "CN") ExtinguishNavGraph.GuideAgreement
            else ExtinguishNavGraph.GuideSolution
            return@LaunchedEffect
        }
        if (!solutionState.isShizukuBinderAlive) {
            startDestination = ExtinguishNavGraph.GuideShizukuRunning
        }
    }

    NavHost(
        navController,
        startDestination = startDestination,
    ) {
        home(
            settingsRepository,
            onNavigateTo
        )
        solution(
            onBack,
            settingsRepository,
            onNavigateTo
        )
        floatingButton(
            onBack,
            settingsRepository,
            onNavigateTo
        )
        timerPreset(
            onBack,
            settingsRepository,
            timersRepository,
            onNavigateTo
        )
        timerPresetDialog(
            onBack, timersRepository, onNavigateTo
        )
        volumeKeyControl(
            onBack,
            settingsRepository,
            onNavigateTo
        )
        externalControl(
            onBack,
            settingsRepository,
            onNavigateTo
        )
        compatible(
            onBack,
            settingsRepository,
            onNavigateTo
        )
        about(
            onBack,
            settingsRepository,
            onNavigateTo
        )
        guideAgreement(settingsRepository) {
            startDestination = ExtinguishNavGraph.GuideSolution
        }
        guideSolution(settingsRepository) {
            settingsRepository.initVersion = BuildExt.INIT_VERSION
            startDestination = if (solutionState.isShizukuBinderAlive) ExtinguishNavGraph.Home
            else ExtinguishNavGraph.GuideShizukuRunning
        }
        guideShizukuRunning(
            settingsRepository,
            reselectSolution = {
                startDestination = ExtinguishNavGraph.GuideSolution
            },
            onNext = {
                startDestination = ExtinguishNavGraph.Home
            },
        )
    }
}

object ExtinguishNavGraph
typealias ExtinguishNavRoute = String
