package com.example.e_kuhipath.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaidCourseChapterVideoList(
    @SerializedName("chapter_and_video_list")
    val chapters: List<PaidCourseChapters>
): Parcelable