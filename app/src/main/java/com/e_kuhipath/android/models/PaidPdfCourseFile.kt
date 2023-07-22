package com.e_kuhipath.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaidPdfCourseFile(
    @SerializedName("id")
    val id: String,
    @SerializedName("pdf_name")
    val pdf_name: String,
    @SerializedName("pdf_file_id")
    val pdf_file_id: String,
    @SerializedName("course_id")
    val course_id: String,
    @SerializedName("password")
    val password: String
): Parcelable