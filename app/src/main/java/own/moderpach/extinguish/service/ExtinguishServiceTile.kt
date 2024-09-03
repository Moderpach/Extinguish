package own.moderpach.extinguish.service

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import own.moderpach.extinguish.ISolutionsStateManager
import own.moderpach.extinguish.R
import own.moderpach.extinguish.SolutionsStateManager
import own.moderpach.extinguish.settings.data.SyncSettingsRepository
import own.moderpach.extinguish.settings.data.settingsDataStore

class ExtinguishServiceTile : TileService() {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    lateinit var solutionsStateManager: ISolutionsStateManager
    lateinit var settingsRepository: SyncSettingsRepository

    override fun onCreate() {
        super.onCreate()
        solutionsStateManager = SolutionsStateManager(this)
        settingsRepository = SyncSettingsRepository(settingsDataStore)
    }

    override fun onStartListening() {
        super.onStartListening()
        coroutineScope.launch {
            ExtinguishService.state.collectLatest {
                when (it) {
                    ExtinguishService.State.Destroyed -> setQsStateOff()
                    ExtinguishService.State.Created -> setQsStateOn()
                    ExtinguishService.State.Prepared -> setQsStateOn()
                    ExtinguishService.State.Error -> setQsStateOn()
                }
            }
        }
        coroutineScope.launch {
            settingsDataStore.data.collectLatest {
                settingsRepository.keptUpdate()
            }
        }
    }

    override fun onStopListening() {
        super.onStopListening()
        coroutineScope.cancel()
    }

    override fun onClick() {
        super.onClick()
        if (!solutionsStateManager.checkShizukuPermission()) return
        if (settingsRepository.floatingButton.enabled && !isDisplayOnOtherAppsGranted) return

        when (ExtinguishService.state.value) {
            ExtinguishService.State.Destroyed -> startExtinguishService()
            ExtinguishService.State.Created -> Unit
            ExtinguishService.State.Prepared -> stopExtinguishService()
            ExtinguishService.State.Error -> stopExtinguishService()
        }
    }

    private fun startExtinguishService() {
        startService(Intent(this, ExtinguishService::class.java))
    }

    private fun stopExtinguishService() {
        stopService(Intent(this, ExtinguishService::class.java))
    }

    private fun setQsStateOff() {
        qsTile.state = Tile.STATE_INACTIVE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            qsTile.stateDescription = getText(R.string.Off)
        }
        qsTile.updateTile()
    }

    private fun setQsStateOn() {
        qsTile.state = Tile.STATE_ACTIVE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            qsTile.stateDescription = getText(R.string.On)
        }
        qsTile.updateTile()
    }

    val isDisplayOnOtherAppsGranted
        get() = Settings.canDrawOverlays(this)

}