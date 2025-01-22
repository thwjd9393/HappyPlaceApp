package com.airapssinsj.happyplaceapp.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

//data class HappyPlaceModel(
//    val id : Int,
//    val title : String,
//    val image : String,
//    val description : String,
//    val date : String,
//    val location : String,
//    val latitude : Double,
//    val longitude : Double,
//):Serializable 한 클래스에서 다른 클래스로 데이터를 전달할 수 있는 타입
//객체를 저장할 수도 있음

data class HappyPlaceModel(
    val id : Int,
    val title : String?,
    val image : String?,
    val description : String?,
    val date : String?,
    val location : String?,
    val latitude : Double,
    val longitude : Double,
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(), //프로그램을 작동하려면 string타입은 모두 null값을 가질 수 있게 해야됨
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(description)
        parcel.writeString(date)
        parcel.writeString(location)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HappyPlaceModel> {
        override fun createFromParcel(parcel: Parcel): HappyPlaceModel {
            return HappyPlaceModel(parcel)
        }

        override fun newArray(size: Int): Array<HappyPlaceModel?> {
            return arrayOfNulls(size)
        }
    }
}