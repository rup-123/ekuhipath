package com.e_kuhipath.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaidPdfCourseFilesReturnData(
    @SerializedName("paid_pdf_courses_files")
    val paid_pdf_course_files: List<PaidPdfCourseFile>
): Parcelable