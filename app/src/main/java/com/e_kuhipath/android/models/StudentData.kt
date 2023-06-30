package com.e_kuhipath.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class StudentData(
    @SerializedName("user")
    val user: StudentDataDetails,
    @SerializedName("token")
    val token:String
): Parcelable