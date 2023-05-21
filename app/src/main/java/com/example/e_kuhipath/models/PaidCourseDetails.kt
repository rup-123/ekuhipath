package com.example.e_kuhipath.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaidCourseDetails(
    @SerializedName("data")
    val data: PaidCourseChapterVideoList
): Parcelable