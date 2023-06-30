package com.e_kuhipath.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StudentDataDetails(
    @SerializedName("id")
    val id: Number,
    @SerializedName("user_id")
    val user_id:String,
    @SerializedName("name")
    val name: String,
    @SerializedName("mobile_no")
    val mobile_no:String,
    @SerializedName("email")
    val email: String
): Parcelable