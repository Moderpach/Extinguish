package extinguish.ipc.result

import android.os.Parcel
import android.os.Parcelable

class FloatResult private constructor(
) : Parcelable, Result<Float>() {

    constructor(parcel: Parcel) : this() {
        resultCode = parcel.readInt()
        if (isOk) {
            value = parcel.readFloat()
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
            parcel.writeFloat(value!!)
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

    companion object CREATOR : Parcelable.Creator<FloatResult> {
        @JvmStatic
        fun Ok(value: Float) = FloatResult().apply {
            resultCode = RESULT_CODE_OK
            this.value = value
        }

        @JvmStatic
        fun Err(e: Exception) = FloatResult().apply {
            resultCode = RESULT_CODE_ERR
            decodeException(e)
        }

        override fun createFromParcel(parcel: Parcel): FloatResult {
            return FloatResult(parcel)
        }

        override fun newArray(size: Int): Array<FloatResult?> {
            return arrayOfNulls(size)
        }
    }

}
