package com.e_kuhipath.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StudentRegisterTokens(
    @SerializedName("status")
    val status: String,
   /* @SerializedName("message")
    val message:String,*/
    /*@SerializedName("data")
    val data:StudentData*/
): Parcelable