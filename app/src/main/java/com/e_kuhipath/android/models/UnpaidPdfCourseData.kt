package com.e_kuhipath.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnpaidPdfCourseData(
    @SerializedName("unpaid_courses")
    val unpaid_courses: List<UnpaidPdfCourse>
): Parcelable