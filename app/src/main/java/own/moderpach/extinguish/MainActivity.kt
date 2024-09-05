package own.moderpach.extinguish

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import own.moderpach.extinguish.settings.data.ISettingsRepository
import own.moderpach.extinguish.settings.data.settingsDataStore
import own.moderpach.extinguish.settings.data.settingsRepository
import own.moderpach.extinguish.timer.data.ITimersRepository
import own.moderpach.extinguish.timer.data.TimersDatabase
import own.moderpach.extinguish.timer.data.timersRepository
import rikka.sui.Sui

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    private lateinit var solutionsStateManager: ISolutionsStateManager
    private lateinit var systemPermissionsManager: ISystemPermissionsManager
    private lateinit var settingsRepository: ISettingsRepository
    private lateinit var timersRepository: ITimersRepository

    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Sui.init(packageName)
        enableEdgeToEdge()

        solutionsStateManager = SolutionsStateManager(this).also {
            it.initialize()
            it.update()
        }
        systemPermissionsManager = SystemPermissionsManager(this)
        settingsRepository = settingsRepository(settingsDataStore)
        timersRepository = timersRepository(TimersDatabase.get(this).timersDao())

        setContent {
            ExtinguishApp(
                solutionsStateManager,
                systemPermissionsManager,
                settingsRepository,
                timersRepository
            )
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    //分屏时点击不同窗口只会触发这个
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Log.d(TAG, "onWindowFocusChanged: ")
        solutionsStateManager.update()
    }

}
