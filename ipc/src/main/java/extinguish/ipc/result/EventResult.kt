package extinguish.ipc.result

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * shell `getevent` result is like this:
 * ```bash
 * /dev/input/event6: 0001 014a 00000001
 * ```
 * @param event - `/dev/input/event6:`
 * @param v0 - `0001`
 * @param v1 - `014a`
 * @param v2 - `00000001`
 * */
@Parcelize
data class EventResult(
    val event: String,
    val v0: String,
    val v1: String,
    val v2: String,
) : Parcelable
