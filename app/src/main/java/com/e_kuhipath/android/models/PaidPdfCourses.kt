package com.e_kuhipath.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaidPdfCourses(
    @SerializedName("data")
    val data: PaidPdfReturnData
): Parcelable