package extinguish.ipc.result

import android.os.Parcel
import android.os.Parcelable


class IntResult private constructor(
) : Parcelable, Result<Int>() {

    constructor(parcel: Parcel) : this() {
        resultCode = parcel.readInt()
        if (isOk) {
            value = parcel.readInt()
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
        if (isOk) {
            checkNotNull(value)
            parcel.writeInt(value!!)
            return
        }
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

    companion object CREATOR : Parcelable.Creator<IntResult> {
        @JvmStatic
        fun Ok(value: Int) = IntResult().apply {
            resultCode = RESULT_CODE_OK
            this.value = value
        }

        @JvmStatic
        fun Err(e: Exception) = IntResult().apply {
            resultCode = RESULT_CODE_ERR
            decodeException(e)
        }

        override fun createFromParcel(parcel: Parcel): IntResult {
            return IntResult(parcel)
        }

        override fun newArray(size: Int): Array<IntResult?> {
            return arrayOfNulls(size)
        }
    }

}