package com.e_kuhipath.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaidPdfReturnData(
    @SerializedName("paid_pdf_courses")
    val paid_pdf_courses: List<PaidPdfCourse>
): Parcelable