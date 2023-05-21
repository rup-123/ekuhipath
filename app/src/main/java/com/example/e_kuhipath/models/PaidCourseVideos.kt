package com.example.e_kuhipath.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaidCourseVideos(
    @SerializedName("video_title")
    val video_title: String,
    @SerializedName("video_id")
    val video_id: String,
    @SerializedName("video_details")
    val video_details: String,
    @SerializedName("duration_hours")
    val duration_hours: String,
    @SerializedName("duration_minutes")
    val duration_minutes: String
): Parcelable