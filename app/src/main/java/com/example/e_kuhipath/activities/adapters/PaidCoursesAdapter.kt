package com.example.e_kuhipath.activities.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_kuhipath.R
import com.example.e_kuhipath.activities.pages.PaidCourseDetailsActivity
import com.example.e_kuhipath.models.PaidCourses

class PaidCoursesAdapter(var paidCourses: PaidCourses, var context: Context): RecyclerView.Adapter<PaidCoursesAdapter.DataViewHolder>() {


    inner class DataViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        val paidcourse_title = v.findViewById<TextView>(R.id.paidcourse_title)
        val paidcourse_lang = v.findViewById<TextView>(R.id.paidcourse_lang)
        val lectures = v.findViewById<TextView>(R.id.lectures)
        val duration = v.findViewById<TextView>(R.id.duration)

        val image = v.findViewById<ImageView>(R.id.paidcourse_img)

        val paidcoursecard = v.findViewById<CardView>(R.id.paidcoursecard)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.paidcoursecarditem, parent, false)
        return DataViewHolder(v)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.paidcourse_title.text = paidCourses.data.paidCourse[position].sub_course_name
        holder.paidcourse_lang.text = paidCourses.data.paidCourse[position].language
        val img_url = "https://www.ekuhipath.com/api/ekuhipath-v1/video-course/get-course-thumbnail/" +  paidCourses.data.paidCourse[position].sub_course_id

        Glide.with(holder.itemView.context)
            .load(img_url)
            .timeout(60000)
            .into(holder.image)

        holder.lectures.text = "Lectures: "+paidCourses.data.paidCourse[position].total_videos + " lectures"
        holder.duration.text = "Duration: "+paidCourses.data.paidCourse[position].video_duration + " hours"


        holder.paidcoursecard.setOnClickListener{
            val intent = Intent(context, PaidCourseDetailsActivity::class.java)
            intent.putExtra("subcourseid",paidCourses.data.paidCourse[position].sub_course_id)
            intent.putExtra("totalvideos",paidCourses.data.paidCourse[position].total_videos)
            context.startActivity(intent)

        }
    }


    override fun getItemCount(): Int = paidCourses.data.paidCourse.size

}