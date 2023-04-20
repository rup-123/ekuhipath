package com.example.e_kuhipath.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UnpaidCourse(
    @SerializedName("id")
    val id: String,
    @SerializedName("sub_course_name")
    val sub_course_name: String,
    @SerializedName("sub_course_id")
    val sub_course_id: String,
    @SerializedName("sub_course_details")
    val sub_course_details: String,
    @SerializedName("payment_type")
    val payment_type: String,
    @SerializedName("g_form_link")
    val g_form_link: String,
    @SerializedName("price")
    val price: String
): Parcelable