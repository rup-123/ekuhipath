package com.e_kuhipath.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaidCourseData(
    @SerializedName("paid_courses")
    val paidCourse: List<PaidCourse>
): Parcelable