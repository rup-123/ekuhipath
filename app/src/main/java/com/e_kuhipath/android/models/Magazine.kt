package com.e_kuhipath.android.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Magazine(
    @SerializedName("id")
    val id: String,
    @SerializedName("pdf_name")
    val pdf_name: String,
    @SerializedName("pdf_path")
    val pdf_path: String,
    @SerializedName("thumbnail_path")
    val thumbnail_path: String
): Parcelable