package own.moderpach.extinguish

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class ExtinguishApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
    }
}

object BuildExt {
    const val INIT_VERSION = 3
}