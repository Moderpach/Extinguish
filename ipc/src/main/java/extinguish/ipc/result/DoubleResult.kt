package extinguish.ipc.result

import android.os.Parcel
import android.os.Parcelable


class DoubleResult private constructor(
) : Parcelable, Result<Double>() {

    constructor(parcel: Parcel) : this() {
        resultCode = parcel.readInt()
        if (isOk) {
            value = parcel.readDouble()
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
            parcel.writeDouble(value!!)
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

    companion object CREATOR : Parcelable.Creator<DoubleResult> {
        @JvmStatic
        fun Ok(value: Double) = DoubleResult().apply {
            resultCode = RESULT_CODE_OK
            this.value = value
        }

        @JvmStatic
        fun Err(e: Exception) = DoubleResult().apply {
            resultCode = RESULT_CODE_ERR
            decodeException(e)
        }

        override fun createFromParcel(parcel: Parcel): DoubleResult {
            return DoubleResult(parcel)
        }

        override fun newArray(size: Int): Array<DoubleResult?> {
            return arrayOfNulls(size)
        }
    }

}