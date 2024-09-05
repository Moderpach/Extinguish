package own.moderpach.extinguish

import android.app.Application

class ExtinguishApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
    }
}

object BuildExt {
    const val INIT_VERSION = 3
}