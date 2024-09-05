package own.moderpach.extinguish.service

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import extinguish.shizuku_service.DisplayControlService
import extinguish.shizuku_service.EventsProviderService
import extinguish.shizuku_service.IDisplayControl
import extinguish.shizuku_service.IEventsProvider
import extinguish.shizuku_service.result.UnitResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import own.moderpach.extinguish.BuildConfig
import own.moderpach.extinguish.ExceptionMassages
import own.moderpach.extinguish.notifyException
import own.moderpach.extinguish.service.hosts.AwakeHost
import own.moderpach.extinguish.service.hosts.FloatingButtonHost
import own.moderpach.extinguish.service.hosts.NotificationHost
import own.moderpach.extinguish.service.hosts.ScreenEventHost
import own.moderpach.extinguish.service.hosts.VolumeKeyEventShizukuHost
import own.moderpach.extinguish.service.hosts.VolumeKeyEventWindowHost
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.data.SettingsTokens
import own.moderpach.extinguish.settings.data.SettingsTokens.SolutionValue.ShizukuPowerOffScreen
import own.moderpach.extinguish.settings.data.SettingsTokens.SolutionValue.ShizukuScreenBrightnessNeg1
import own.moderpach.extinguish.settings.data.SettingsTokens.VolumeKeyEvent.ListeningMethodValue.Shell
import own.moderpach.extinguish.settings.data.SettingsTokens.VolumeKeyEvent.ListeningMethodValue.Window
import own.moderpach.extinguish.settings.data.SyncSettingsRepository
import own.moderpach.extinguish.settings.data.settingsDataStore
import rikka.shizuku.Shizuku

private const val TAG = "ExtinguishService"

class ExtinguishService : LifecycleService() {

    enum class State {
        Destroyed, Created, Prepared, Error
    }

    companion object {
        @JvmStatic
        val state = MutableStateFlow(State.Destroyed)

        @JvmStatic
        val screenState = MutableStateFlow(ScreenState.On)

        const val EXTRA_SCREEN = "screen"
        const val EXTRA_VALUE_SCREEN_ON = 0
        const val EXTRA_VALUE_SCREEN_OFF = 1
        const val EXTRA_VALUE_SCREEN_SWITCH = 2

        //const val EXTRA_PASSWORD = "password"

        const val EXTRA_FLOATING_BUTTON = "fb"
        const val EXTRA_VALUE_FLOATING_BUTTON_SHOW = 0
        const val EXTRA_VALUE_FLOATING_BUTTON_HIDE = 1
        const val EXTRA_VALUE_FLOATING_BUTTON_SWITCH = 2

        const val EXTRA_REMOVE_FLOATING_BUTTON = "rfb"
        const val EXTRA_VALUE_FLOATING_BUTTON_REMOVE = 1

        const val EXTRA_STOP = "stop"
        const val EXTRA_VALUE_STOP = 1

        const val EXTRA_VALUE_INVALID = -1
    }

    //todo: password support
    //lateinit var internalPassword: String

    var floatingButtonHost: FloatingButtonHost<ExtinguishService>? = null
    var awakeHost: AwakeHost? = null
    var volumeKeyEventWindowHost: VolumeKeyEventWindowHost? = null
    var volumeKeyEventShizukuHost: VolumeKeyEventShizukuHost? = null
    var screenEventHost: ScreenEventHost? = null
    val notificationHost by lazy { NotificationHost(this) }

    fun updateHostState(
        requestScreenOn: Boolean,
        feature: Feature
    ) {
        lifecycleScope.launch {
            if (requestScreenOn) awakeHost?.stopKeepAwake()
            else awakeHost?.startKeepAwake()
        }

        if (feature.enabledFloatingButtonControl) {
            if (feature.hideFloatingButtonWhenScreenOff) {
                if (requestScreenOn) floatingButtonHost?.show()
                else floatingButtonHost?.hide()
            } else {
                floatingButtonHost?.updateScreenState(requestScreenOn)
            }
        }

        if (feature.enabledVolumeKeyEventControl) {
            when (feature.volumeKeyEventControlMethod) {
                Shell -> {
                    if (
                        (feature.clickVolumeKeyToTurnScreenOn && !requestScreenOn).or(
                            feature.clickVolumeKeyToTurnScreenOff && requestScreenOn
                        )
                    ) volumeKeyEventShizukuHost?.wake()
                    else volumeKeyEventShizukuHost?.sleep()
                }

                Window -> {
                    if (
                        (feature.clickVolumeKeyToTurnScreenOn && !requestScreenOn).or(
                            feature.clickVolumeKeyToTurnScreenOff && requestScreenOn
                        )
                    ) volumeKeyEventWindowHost?.wake()
                    else volumeKeyEventWindowHost?.sleep()
                }
            }
        }

        if (feature.enabledScreenEventControl) {
            if (requestScreenOn) screenEventHost?.sleep()
            else lifecycleScope.launch {
                delay(100)
                screenEventHost?.wake()
            }
        }

        notificationHost.notify(
            requestScreenOn,
            floatingButtonHost?.isShowing ?: false,
            feature.enabledFloatingButtonControl
        )
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate: ")
        //internalPassword = UUID.randomUUID().toString()
        super.onCreate()
        screenState.update { ScreenState.On }
    }

    /**
     * Control Extinguish Service with Intent
     *
     * ## PendingIntent Internal
     *
     * ## Shell Command External
     *
     * ```
     * adb shell am startservice -n own.moderpach.extinguish/.service.ExtinguishService --ei [key] [int_value]
     * ```
     * */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")

        intent?.getIntExtra(EXTRA_STOP, EXTRA_VALUE_INVALID)?.let {
            if (it == EXTRA_VALUE_STOP) {
                stopSelf()
                return super.onStartCommand(intent, flags, startId)
            }
        }

        var removeFloatingButton = false
        intent?.getIntExtra(EXTRA_REMOVE_FLOATING_BUTTON, EXTRA_VALUE_INVALID)?.let {
            Log.d(TAG, "EXTRA_REMOVE_FLOATING_BUTTON: $it")
            if (it == EXTRA_VALUE_FLOATING_BUTTON_REMOVE) {
                Log.d(TAG, "args request - remove floating button")
                removeFloatingButton = true
            }
        }

        when (state.value) {
            State.Destroyed -> initialize(removeFloatingButton)
            State.Error -> initialize(removeFloatingButton)
            else -> Unit
        }

        processArgs(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    var processArgsJob: Job? = null

    private fun processArgs(intent: Intent?) {
        if (processArgsJob?.isActive == true) return
        processArgsJob = lifecycleScope.launch {
            initializeJob?.join()
            if (state.value != State.Prepared) return@launch

            intent?.getIntExtra(EXTRA_STOP, EXTRA_VALUE_INVALID)?.let {
                if (it == EXTRA_VALUE_STOP) Log.d(TAG, "request stop service")
            }
            intent?.getIntExtra(EXTRA_SCREEN, EXTRA_VALUE_INVALID)?.let {
                Log.d(TAG, "EXTRA_SCREEN: $it")
                when (it) {
                    EXTRA_VALUE_SCREEN_ON -> {
                        if (!isScreenOn) {
                            updateHostState(true, feature)
                            turnScreenOn()
                        }
                    }

                    EXTRA_VALUE_SCREEN_OFF -> {
                        if (isScreenOn) {
                            updateHostState(false, feature)
                            turnScreenOff()
                        }
                    }

                    EXTRA_VALUE_SCREEN_SWITCH -> {
                        if (isScreenOn) {
                            updateHostState(false, feature)
                            turnScreenOff()
                        } else {
                            updateHostState(true, feature)
                            turnScreenOn()
                        }
                    }

                    else -> Unit
                }
            }
            intent?.getIntExtra(EXTRA_FLOATING_BUTTON, EXTRA_VALUE_INVALID)?.let {
                Log.d(TAG, "EXTRA_FLOATING_BUTTON: $it")
                when (it) {
                    EXTRA_VALUE_FLOATING_BUTTON_SHOW -> {
                        floatingButtonHost?.show()
                        notificationHost.notify(
                            isScreenOn,
                            floatingButtonHost?.isShowing ?: false,
                            feature.enabledFloatingButtonControl
                        )
                    }

                    EXTRA_VALUE_FLOATING_BUTTON_HIDE -> {
                        floatingButtonHost?.hide()
                        notificationHost.notify(
                            isScreenOn,
                            floatingButtonHost?.isShowing ?: false,
                            feature.enabledFloatingButtonControl
                        )
                    }

                    EXTRA_VALUE_FLOATING_BUTTON_SWITCH -> {
                        floatingButtonHost?.let { host ->
                            if (host.isShowing) host.hide()
                            else host.show()
                        }
                        notificationHost.notify(
                            isScreenOn,
                            floatingButtonHost?.isShowing ?: false,
                            feature.enabledFloatingButtonControl
                        )
                    }

                    else -> Unit
                }
            }
        }
    }

    var initializeJob: Job? = null

    private fun initialize(removeFloatingButton: Boolean) {
        if (initializeJob?.isActive == true) return
        initializeJob = lifecycleScope.launch {
            state.update { State.Created }

            val settingRepository = SyncSettingsRepository(settingsDataStore)
            settingRepository.update()
            loadFeatureFromSettings(settingRepository, removeFloatingButton)


            if (!checkShizukuPermission()) {
                state.update { State.Error }
                return@launch
            }

            awakeHost = AwakeHost(this@ExtinguishService)

            if (feature.enabledFloatingButtonControl) {
                floatingButtonHost = FloatingButtonHost(
                    this@ExtinguishService,
                    isScreenOn = isScreenOn,
                    feature = FloatingButtonHost.Feature(
                        settingRepository.floatingButton.autoMoveToEdge,
                        settingRepository.floatingButton.fadeWhenUnused,
                        settingRepository.floatingButton.fadeTransparency,
                        settingRepository.floatingButton.blackStyle,
                        settingRepository.floatingButton.mergeTimerButton
                    )
                ) { action ->
                    Log.d(TAG, "FloatingButtonHostAction: $action")
                    when (action) {
                        FloatingButtonHost.Action.TurnScreenOff -> {
                            turnScreenOff()
                            updateHostState(false, feature)
                        }

                        is FloatingButtonHost.Action.TurnScreenOffWithTimer -> {
                            turnScreenOff()
                            updateHostState(false, feature)
                            timer.launch(action.seconds) {
                                updateHostState(true, feature)
                                turnScreenOn()
                            }
                        }

                        FloatingButtonHost.Action.TurnScreenOn -> {
                            turnScreenOn()
                            updateHostState(true, feature)
                        }
                    }
                }
            }

            val shouldBindEventsProviderService = feature.enabledScreenEventControl.or(
                feature.enabledVolumeKeyEventControl.and(
                    feature.volumeKeyEventControlMethod == Shell
                )
            )

            if (shouldBindEventsProviderService) {
                bindEventsProviderService()
            }

            if (
                feature.enabledVolumeKeyEventControl.and(
                    feature.volumeKeyEventControlMethod == Window
                )
            ) {
                volumeKeyEventWindowHost = VolumeKeyEventWindowHost(this@ExtinguishService) {
                    if (isScreenOn) {
                        updateHostState(false, feature)
                        turnScreenOff()
                    } else {
                        updateHostState(true, feature)
                        turnScreenOn()
                    }
                }.also {
                    it.create()
                    if (
                        (isScreenOn && !feature.clickVolumeKeyToTurnScreenOff).or(
                            !isScreenOn && !feature.clickVolumeKeyToTurnScreenOn
                        )
                    ) {
                        it.sleep()
                    }
                }
            }

            bindDisplayControlService()

            withTimeout(3000) {
                while (
                    displayControlService == null ||
                    (shouldBindEventsProviderService && eventsProviderService == null)
                ) {
                    delay(50)
                }
                if (isActive) {
                    awakeHost?.create()
                    floatingButtonHost?.create()

                    eventsProviderService?.let { service ->
                        volumeKeyEventShizukuHost = VolumeKeyEventShizukuHost(
                            this@ExtinguishService, service
                        ) {
                            if (isScreenOn) {
                                updateHostState(false, feature)
                                turnScreenOff()
                            } else {
                                updateHostState(true, feature)
                                turnScreenOn()
                            }
                        }.also { it.register() }

                        screenEventHost = ScreenEventHost(
                            this@ExtinguishService, service
                        ) {
                            if (!isScreenOn) {
                                updateHostState(true, feature)
                                turnScreenOn()
                            }
                        }.also {
                            it.register()
                            if (isScreenOn) it.sleep()
                        }

                        service.launch("-F -e \": 0001 014a\" -e \": 0001 0072\" -e \": 0001 0073\"")
                    }

                    registerSystemLockReceiver()

                    notificationHost.notify(
                        isScreenOn,
                        floatingButtonHost?.isShowing ?: false,
                        feature.enabledFloatingButtonControl
                    ).also {
                        startForeground(NotificationHost.NOTIFICATION_ID, it)
                    }

                    state.update { State.Prepared }
                } else {
                    notifyException(ExceptionMassages.ShizukuFailed, null)
                    state.update { State.Error }
                }
            }

        }
    }

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): ExtinguishService = this@ExtinguishService
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind: ")
        super.onBind(intent)
        if (BuildConfig.DEBUG) return binder
        return null
    }

    override fun onRebind(intent: Intent?) {
        Log.d(TAG, "onRebind: ")
        super.onRebind(intent)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        unregisterSystemLockReceiver()
        awakeHost?.destroy()
        floatingButtonHost?.destroy()
        volumeKeyEventWindowHost?.destroy()
        volumeKeyEventShizukuHost?.unregister()
        screenEventHost?.unregister()

        eventsProviderService?.stop()

        unbindEventsProviderService()
        unbindDisplayControlService()

        super.onDestroy()
        state.update { State.Destroyed }
    }

    //== Feature

    var feature = Feature(
        SettingsTokens.SolutionValue.valueOf(
            SettingsTokens.Solution.default
        ),
        SettingsTokens.FloatingButton.Enabled.default,
        SettingsTokens.FloatingButton.HideWhenScreenOff.default,
        SettingsTokens.VolumeKeyEvent.Enabled.default,
        SettingsTokens.VolumeKeyEvent.ClickToTurnScreenOff.default,
        SettingsTokens.VolumeKeyEvent.ClickToTurnScreenOn.default,
        SettingsTokens.VolumeKeyEvent.ListeningMethodValue.valueOf(
            SettingsTokens.VolumeKeyEvent.ListeningMethod.default
        ),
        SettingsTokens.ScreenEvent.Enabled.default,
        SettingsTokens.Compatibility.BrightnessManualWhenScreenOff.default
    )

    data class Feature(
        val solution: SettingsTokens.SolutionValue,
        val enabledFloatingButtonControl: Boolean,
        val hideFloatingButtonWhenScreenOff: Boolean,
        val enabledVolumeKeyEventControl: Boolean,
        val clickVolumeKeyToTurnScreenOff: Boolean,
        val clickVolumeKeyToTurnScreenOn: Boolean,
        val volumeKeyEventControlMethod: SettingsTokens.VolumeKeyEvent.ListeningMethodValue,
        val enabledScreenEventControl: Boolean,
        val brightnessManualWhenScreenOff: Boolean
    )

    fun loadFeatureFromSettings(repository: ISettingsRepository, removeFloatingButton: Boolean) {
        feature = Feature(
            repository.solution,
            repository.floatingButton.enabled && !removeFloatingButton,
            repository.floatingButton.hideWhenScreenOff,
            repository.volumeKeyEvent.enabled,
            repository.volumeKeyEvent.clickToTurnScreenOff,
            repository.volumeKeyEvent.clickToTurnScreenOn,
            repository.volumeKeyEvent.listeningMethod,
            repository.screenEvent.enabled && repository.solution == SettingsTokens.SolutionValue.ShizukuScreenBrightnessNeg1,
            repository.compatibility.brightnessManualWhenScreenOff
        )
    }

    //== Bind with shizuku

    fun checkShizukuPermission() =
        Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED

    var displayControlService: IDisplayControl? = null

    private val displayControlServiceArgs = Shizuku.UserServiceArgs(
        ComponentName(
            BuildConfig.APPLICATION_ID,
            DisplayControlService::class.java.name
        )
    )
        .processNameSuffix(DisplayControlService.TAG)
        .tag(DisplayControlService.TAG)
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)
        .daemon(false)

    private val displayControlServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service != null && binder.pingBinder()) {
                displayControlService = IDisplayControl.Stub.asInterface(service)
            } else {
                notifyException(ExceptionMassages.ShizukuFailed, null)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            displayControlService = null
        }

    }

    fun bindDisplayControlService() {
        try {
            Shizuku.bindUserService(
                displayControlServiceArgs,
                displayControlServiceConnection
            )
        } catch (e: Exception) {
            notifyException(ExceptionMassages.ShizukuFailed, e.message)
        }
    }

    fun unbindDisplayControlService() {
        try {
            Shizuku.unbindUserService(
                displayControlServiceArgs,
                displayControlServiceConnection,
                true
            )
        } catch (e: Exception) {
            notifyException(ExceptionMassages.ShizukuFailed, e.message)
        }
    }

    var eventsProviderService: IEventsProvider? = null

    private val eventsProviderServiceArgs = Shizuku.UserServiceArgs(
        ComponentName(
            BuildConfig.APPLICATION_ID,
            EventsProviderService::class.java.name
        )
    )
        .processNameSuffix(EventsProviderService.TAG)
        .tag(EventsProviderService.TAG)
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)
        .daemon(false)

    private val eventsProviderServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service != null && binder.pingBinder()) {
                eventsProviderService = IEventsProvider.Stub.asInterface(service)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            eventsProviderService = null
        }

    }

    fun bindEventsProviderService() {
        Shizuku.bindUserService(
            eventsProviderServiceArgs,
            eventsProviderServiceConnection
        )
    }

    fun unbindEventsProviderService() {
        Shizuku.unbindUserService(
            eventsProviderServiceArgs,
            eventsProviderServiceConnection,
            true
        )
    }

    //== Screen state

    val isScreenOn get() = screenState.value == ScreenState.On
    var recordBrightness = 255
    var recordBrightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC

    enum class ScreenState {
        On, Off
    }

    fun turnScreenOn() {
        screenState.update { ScreenState.On }
        try {
            when (feature.solution) {
                ShizukuPowerOffScreen -> {
                    displayControlService?.setPowerModeToSurfaceControl(DisplayControlService.POWER_MODE_NORMAL)
                        .let {
                            if (it is UnitResult.Err) {
                                notifyException(ExceptionMassages.MethodNotFound, it.message)
                            }
                        }
                }

                ShizukuScreenBrightnessNeg1 -> {
                    displayControlService?.setBrightnessToSurfaceControl(0f).let {
                        if (it is UnitResult.Err) {
                            notifyException(ExceptionMassages.MethodNotFound, it.message)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            notifyException(ExceptionMassages.ShizukuFailed, e.message)
        }
        lifecycleScope.launch {
            try {
                val firstBrightness = if (recordBrightness < 8) recordBrightness + 5
                else recordBrightness - 5
                displayControlService?.setBrightnessToSetting(firstBrightness)
                delay(25)
                displayControlService?.setBrightnessToSetting(recordBrightness)

                if (feature.brightnessManualWhenScreenOff) {
                    displayControlService?.setBrightnessModeToSetting(recordBrightnessMode)
                }
            } catch (e: Exception) {
                notifyException(ExceptionMassages.ShizukuFailed, e.message)
            }
        }
    }

    fun turnScreenOff() {
        screenState.update { ScreenState.Off }
        try {
            recordBrightness = Settings.System.getInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS
            )
            if (feature.brightnessManualWhenScreenOff) {
                recordBrightnessMode = Settings.System.getInt(
                    contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE
                )
                try {
                    displayControlService?.setBrightnessModeToSetting(Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
                } catch (e: Exception) {
                    notifyException(ExceptionMassages.ShizukuFailed, e.message)
                }
            }
        } catch (e: Exception) {
            notifyException(ExceptionMassages.ReadBrightnessSettingFailed, e.message)
        }
        try {
            when (feature.solution) {
                ShizukuPowerOffScreen -> {
                    displayControlService?.setPowerModeToSurfaceControl(DisplayControlService.POWER_MODE_OFF)
                        .let {
                            if (it is UnitResult.Err) {
                                notifyException(ExceptionMassages.MethodNotFound, it.message)
                            }
                        }
                }

                ShizukuScreenBrightnessNeg1 -> {
                    displayControlService?.setBrightnessToSurfaceControl(-1f).let {
                        if (it is UnitResult.Err) {
                            notifyException(ExceptionMassages.MethodNotFound, it.message)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            notifyException(ExceptionMassages.ShizukuFailed, e.message)
        }
    }

    /**
     * Listen system lock. Update host when locking.
     * */
    private var systemLockReceiver: BroadcastReceiver? = null

    private fun registerSystemLockReceiver() {
        systemLockReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                displayControlService?.setBrightnessToSetting(recordBrightness)
                updateHostState(true, feature)
            }
        }
        systemLockReceiver?.let {
            registerReceiver(
                it,
                IntentFilter().apply {
                    addAction(Intent.ACTION_SCREEN_OFF)
                }
            )
        }
    }

    private fun unregisterSystemLockReceiver() {
        systemLockReceiver?.let {
            unregisterReceiver(it)
        }
    }

    //== Timer

    interface Timer {
        fun launch(second: Int, onComplete: suspend () -> Unit)
        fun cancel()
    }

    val timer = object : Timer {
        private var job: Job? = null

        override fun launch(second: Int, onComplete: suspend () -> Unit) {
            Log.d(TAG, "timer launch: $second")
            cancel()
            job = lifecycleScope.launch {
                delay(second * 1000L)
                if (isActive) {
                    Log.d(TAG, "timer complete: $second")
                    onComplete()
                }
            }
        }

        override fun cancel() {
            job?.cancel()
        }
    }
}