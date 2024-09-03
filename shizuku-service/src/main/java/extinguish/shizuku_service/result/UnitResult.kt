package extinguish.shizuku_service.result

import android.os.Parcel
import android.os.Parcelable


sealed class UnitResult : Parcelable {
    data class Ok(val value: Unit = Unit) : UnitResult() {

        private constructor(parcel: Parcel) : this(Unit) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(0)
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

    data class Err(val code: Int, val message: String?) : UnitResult() {
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

    companion object CREATOR : Parcelable.Creator<UnitResult> {
        override fun createFromParcel(parcel: Parcel): UnitResult {
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

        override fun newArray(size: Int): Array<UnitResult?> {
            return arrayOfNulls(size)
        }
    }
}