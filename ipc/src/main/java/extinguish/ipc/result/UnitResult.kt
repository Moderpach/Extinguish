package extinguish.ipc.result

import android.os.Parcel
import android.os.Parcelable


class UnitResult private constructor(
) : Parcelable, Result<Unit>() {

    constructor(parcel: Parcel) : this() {
        resultCode = parcel.readInt()
        if (isOk) {
            value = Unit
            return
        }
        if (isErr) {
            exceptionName = parcel.readString()
            exceptionDetail = parcel.readString()
            return
        }
        throw ParcelableException.UnsupportedResultCode
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(resultCode)
        if (isOk) return
        if (isErr) {
            checkNotNull(exceptionName)
            parcel.writeString(exceptionName)
            parcel.writeString(exceptionDetail)
            return
        }
        throw ParcelableException.UnsupportedResultCode
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UnitResult> {
        @JvmStatic
        fun Ok() = UnitResult().apply {
            resultCode = RESULT_CODE_OK
            value = Unit
        }

        @JvmStatic
        fun Err(e: Exception) = UnitResult().apply {
            resultCode = RESULT_CODE_ERR
            decodeException(e)
        }

        override fun createFromParcel(parcel: Parcel): UnitResult {
            return UnitResult(parcel)
        }

        override fun newArray(size: Int): Array<UnitResult?> {
            return arrayOfNulls(size)
        }
    }

}