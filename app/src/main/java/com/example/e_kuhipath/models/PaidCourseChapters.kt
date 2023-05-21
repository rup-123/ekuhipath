package com.example.e_kuhipath.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaidCourseChapters(
    @SerializedName("chapter_name")
    val chapter_name: String,
    @SerializedName("chapter_id")
    val chapter_id: String,
    @SerializedName("course")
    val course: String,
    @SerializedName("subcourse")
    val subcourse: String,
    @SerializedName("user_id")
    val user_id: String,
    @SerializedName("videos")
    val videos: List<PaidCourseVideos>
): Parcelable