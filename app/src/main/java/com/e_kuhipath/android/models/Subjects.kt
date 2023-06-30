package com.e_kuhipath.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Subjects(
    @SerializedName("id")
    val id: String,
    @SerializedName("subject_name")
    val subject_name: String,
    @SerializedName("subject_id")
    val subject_id: String,
    @SerializedName("subject_details")
    val subject_details: String,
    @SerializedName("subcourse")
    val subcourse: String
): Parcelable