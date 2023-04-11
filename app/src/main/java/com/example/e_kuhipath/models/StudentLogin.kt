package com.example.e_kuhipath.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StudentLogin(
    @SerializedName("mobile_no")
    val mobile_no: String,
    @SerializedName("password")
    val password:String
): Parcelable