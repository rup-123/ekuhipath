package com.example.e_kuhipath.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaidCourseData(
    @SerializedName("paid_courses")
    val paidCourse: List<PaidCourse>
): Parcelable