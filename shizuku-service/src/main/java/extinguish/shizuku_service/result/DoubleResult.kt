package extinguish.shizuku_service.result

import android.os.Parcel
import android.os.Parcelable


sealed class DoubleResult : Parcelable {
    data class Ok(val value: Double) : DoubleResult() {

        private constructor(parcel: Parcel) : this(parcel.readDouble()) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(0)
            parcel.writeDouble(value)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Ok> {
            override fun createFromParcel(parcel: Parcel): Ok {
                return Ok(parcel)
            }

            override fun newArray(size: Int): Array<Ok?> {
                return arrayOfNulls(size)
            }
        }
    }

    data class Err(val code: Int, val message: String?) : DoubleResult() {
        private constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(1)
            parcel.writeInt(code)
            parcel.writeString(message)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Err> {
            override fun createFromParcel(parcel: Parcel): Err {
                return Err(parcel)
            }

            override fun newArray(size: Int): Array<Err?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object CREATOR : Parcelable.Creator<DoubleResult> {
        override fun createFromParcel(parcel: Parcel): DoubleResult {
            val type = parcel.readInt()
            return when (type) {
                0 -> {
                    Ok.createFromParcel(parcel)
                }

                else -> {
                    Err.createFromParcel(parcel)
                }
            }
        }

        override fun newArray(size: Int): Array<DoubleResult?> {
            return arrayOfNulls(size)
        }
    }
}