package own.moderpach.extinguish.ui.ext

import android.content.Context
import java.io.InputStream


fun Context.stringRowResource(id: Int?): String {
    if (id == null) {
        return ""
    }
    val inputStream: InputStream = this.resources.openRawResource(id)
    val bytes = ByteArray(inputStream.available())
    inputStream.read(bytes)
    return String(bytes)
}