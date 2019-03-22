package com.jojajones.smackchat.Model

import android.os.Parcel
import android.os.Parcelable

class User(var name: String? = "", var email: String? = "", var password: String? = "", var token: String? = "", var avatarBg: String? = "", var avatarIcon: String? = "" ): Parcelable{


    var isCreated = false
    var isLoggedIn = false


    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
        ) {
        isCreated = parcel.readByte() != 0.toByte()
        isLoggedIn = parcel.readByte() != 0.toByte()
    }


    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(name)
        dest?.writeString(email)
        dest?.writeString(password)
        dest?.writeString(token)
        dest?.writeString(avatarBg)
        dest?.writeString(avatarIcon)
        if(isCreated){dest?.writeByte(1)}else{dest?.writeByte(0)}
        if(isLoggedIn){dest?.writeByte(1)}else{dest?.writeByte(0)}
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }


}