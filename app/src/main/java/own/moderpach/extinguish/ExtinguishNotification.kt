package own.moderpach.extinguish

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.annotation.StringRes

enum class ExtinguishNotificationChannels(
    val id: String,
    @StringRes val nameResId: Int,
    @StringRes val descriptionResId: Int,
    val importance: Int
) {
    Service(
        id = "service",
        nameResId = R.string.str_notificationCh_service_name,
        descriptionResId = R.string.str_notificationCh_service_description,
        importance = NotificationManager.IMPORTANCE_MIN,
    ),
    Exception(
        id = "exception",
        nameResId = R.string.str_notificationCh_exception_name,
        descriptionResId = R.string.str_notificationCh_exception_description,
        importance = NotificationManager.IMPORTANCE_HIGH,
    )
}

fun Context.createNotificationChannels() {
    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    ExtinguishNotificationChannels.entries.forEach {
        val name = getString(it.nameResId)
        val description = getString(it.descriptionResId)
        val channel = NotificationChannel(it.id, name, it.importance)
        channel.description = description
        notificationManager.createNotificationChannel(channel)
    }
}