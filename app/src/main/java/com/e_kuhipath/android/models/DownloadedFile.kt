package com.e_kuhipath.android.models

data class DownloadedFile(
    val id: Long,
    val fileName: String,
    val filePath: String,
    val fileThumbnail: String,
    val downloadDate: String
)
