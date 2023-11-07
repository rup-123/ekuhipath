package com.e_kuhipath.android.services

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.e_kuhipath.android.models.DownloadedFile
import java.text.SimpleDateFormat
import java.util.*

class DownloadedFilesDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "downloaded_files.db"
        private const val TABLE_NAME = "downloaded_files"
        private const val COLUMN_ID = "id"
        private const val COLUMN_FILE_NAME = "file_name"
        private const val COLUMN_FILE_PATH = "file_path"
        private const val COLUMN_FILE_THUMBNAIL = "file_thumbnail"
        private const val COLUMN_DOWNLOAD_DATE = "download_date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = (
                "CREATE TABLE $TABLE_NAME (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$COLUMN_FILE_NAME TEXT," +
                        "$COLUMN_FILE_PATH TEXT," +
                        "$COLUMN_FILE_THUMBNAIL TEXT," +
                        "$COLUMN_DOWNLOAD_DATE TEXT)"
                )
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addDownloadedFile(fileName: String, filePath: String,fileThumbnail:String) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_FILE_NAME, fileName)
        values.put(COLUMN_FILE_PATH, filePath)
        values.put(COLUMN_FILE_THUMBNAIL, fileThumbnail)

        // Format the download date as a string
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val downloadDate = dateFormat.format(Date())
        values.put(COLUMN_DOWNLOAD_DATE, downloadDate)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun deleteDownloadedFile(fileId: Long) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(fileId.toString()))
        db.close()
    }

    @SuppressLint("Range")
    fun getAllDownloadedFiles(): List<DownloadedFile> {
        val fileList = mutableListOf<DownloadedFile>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val fileName = cursor.getString(cursor.getColumnIndex(COLUMN_FILE_NAME))
                val filePath = cursor.getString(cursor.getColumnIndex(COLUMN_FILE_PATH))
                val fileThumbnail = cursor.getString(cursor.getColumnIndex(COLUMN_FILE_THUMBNAIL))
                val downloadDate = cursor.getString(cursor.getColumnIndex(COLUMN_DOWNLOAD_DATE))

                val downloadedFile = DownloadedFile(id, fileName, filePath, fileThumbnail, downloadDate)
                fileList.add(downloadedFile)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return fileList
    }

}
