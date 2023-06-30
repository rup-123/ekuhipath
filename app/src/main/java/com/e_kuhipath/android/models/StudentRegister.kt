package com.e_kuhipath.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StudentRegister(
    @SerializedName("full_name")
    val full_name: String,
    @SerializedName("email")
    val email:String,
    @SerializedName("mobile_no")
    val mobile_no: String,
    @SerializedName("password")
    val password:String,
    @SerializedName("password_confirmation")
    val password_confirmation:String
): Parcelable