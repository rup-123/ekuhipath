package com.e_kuhipath.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaidPdfCourse(
    @SerializedName("id")
    val id: String,
    @SerializedName("course_name")
    val course_name: String,
    @SerializedName("course_id")
    val course_id: String,
    @SerializedName("no_of_pdf")
    val no_of_pdf: String,
    @SerializedName("image_path")
    val image_path: String,
    @SerializedName("no_of_pages")
    val no_of_pages: String,
    @SerializedName("course_duration")
    val course_duration: String
): Parcelable