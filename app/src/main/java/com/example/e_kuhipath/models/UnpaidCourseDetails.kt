package com.example.e_kuhipath.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnpaidCourseDetails(
    @SerializedName("course")
    val unpaidcourse: UnpaidCourse,
    @SerializedName("subjects")
    val subjects: List<Subjects>,
): Parcelable