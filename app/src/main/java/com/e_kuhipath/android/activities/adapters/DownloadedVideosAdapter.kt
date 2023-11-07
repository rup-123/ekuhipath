package com.e_kuhipath.android.activities.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e_kuhipath.android.R
import com.e_kuhipath.android.activities.pages.VideoPlayerActivity
import com.e_kuhipath.android.models.DownloadedFile
import com.e_kuhipath.android.models.PaidCourseVideos


class DownloadedVideosAdapter(
    private val context: Context,
    private val filesList: List<DownloadedFile>
) :
    RecyclerView.Adapter<DownloadedVideosAdapter.DataViewHolder>() {

    inner class DataViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        val downloaded_videotitle = v.findViewById<TextView>(R.id.downloaded_videotitle)
        val card = v.findViewById<View>(R.id.downloadedvideocard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.downloadedvideoscarditem, parent, false)
        return DataViewHolder(v)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.downloaded_videotitle.text= filesList[position].fileName
        Log.d("zz","files-->"+filesList[position].fileName)
        val videoPath = "file:/${filesList[position].filePath}"
        holder.card.setOnClickListener{
           val intent = Intent(context,VideoPlayerActivity::class.java)
            intent.putExtra("video_url",videoPath)
            Log.d("zz","video_url--->"+videoPath)

            intent.putExtra("video_thumbnail",filesList[position].fileThumbnail)
            Log.d("zz","video_thumbnail--->"+filesList[position].fileThumbnail)

            intent.putExtra("videoname",filesList[position].fileName)
            Log.d("zz","videoname--->"+filesList[position].fileName)

            intent.putExtra("subcourseid","")
            intent.putExtra("totalvideos","")
            intent.putExtra("videoid","")
            intent.putExtra("pdfpath","")
            intent.putExtra("main_video_name","")
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = filesList.size

}

