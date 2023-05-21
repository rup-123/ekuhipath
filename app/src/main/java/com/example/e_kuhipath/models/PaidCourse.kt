package com.example.e_kuhipath.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaidCourse(
    @SerializedName("sub_course_name")
    val sub_course_name: String,
    @SerializedName("sub_course_id")
    val sub_course_id: String,
    @SerializedName("course_duration")
    val course_duration: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("total_videos")
    val total_videos: String,
    @SerializedName("video_duration")
    val video_duration: String
): Parcelable