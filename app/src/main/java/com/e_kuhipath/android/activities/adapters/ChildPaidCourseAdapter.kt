package com.e_kuhipath.android.activities.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e_kuhipath.android.R
import com.e_kuhipath.android.models.PaidCourseVideos

class ChildPaidCourseAdapter(
    var videos: List<PaidCourseVideos>,
    var context: Context,
    private val onVideoClick: (String,String,String) -> Unit
) :
    RecyclerView.Adapter<ChildPaidCourseAdapter.DataViewHolder>() {
    private var PRIVATE_MODE = 0
    lateinit var sharedPref: SharedPreferences
    inner class DataViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        val paidcourse_videocard = v.findViewById<View>(R.id.paidcourse_videocard)
        val paidcourse_videotitle = v.findViewById<TextView>(R.id.paidcourse_videotitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.childpaidcoursecarditem, parent, false)
        return DataViewHolder(v)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.paidcourse_videotitle.text =videos[position].video_title

        sharedPref = context.getSharedPreferences("sharedpref", PRIVATE_MODE)
        val accesstoken = sharedPref.getString("accesstoken", "")
        val final_token = "Bearer " + accesstoken

        holder.paidcourse_videocard.setOnClickListener{
            videos[position].pdf_path?.let { it1 ->
                onVideoClick(videos[position].video_id,videos[position].video_title,
                    it1
                )
            }
        }
    }


    override fun getItemCount(): Int = videos.size

}