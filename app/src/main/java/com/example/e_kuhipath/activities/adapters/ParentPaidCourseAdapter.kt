package com.example.e_kuhipath.activities.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_kuhipath.R
import com.example.e_kuhipath.models.PaidCourseDetails

class ParentPaidCourseAdapter(var paidCourseDetails: PaidCourseDetails,var context: Context, private val onVideoClick: (String,String,String) -> Unit): RecyclerView.Adapter<ParentPaidCourseAdapter.DataViewHolder>() {

    private var recycledViewPool = RecyclerView.RecycledViewPool()
    inner class DataViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        val parentpaidcourse_title = v.findViewById<TextView>(R.id.parentpaidcourse_title)
        val parentpaidcoursecard = v.findViewById<CardView>(R.id.parentpaidcoursecard)
        val childrecyclerview = v.findViewById<RecyclerView>(R.id.paidcoursedetails_child_recycler_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val v = inflater.inflate(R.layout.parentpaidcoursecarditem, parent, false)
        return DataViewHolder(v)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.parentpaidcourse_title.text = paidCourseDetails.data.chapters[position].chapter_name


        val childPaidCourseAdapter = ChildPaidCourseAdapter(paidCourseDetails.data.chapters[position].videos,context){ videoid,videoname,pdfpath -> onVideoClick(videoid as String,videoname as String,pdfpath as String) }

        holder.childrecyclerview.layoutManager = LinearLayoutManager(context)
        holder.childrecyclerview.adapter = childPaidCourseAdapter
        holder.childrecyclerview.setRecycledViewPool(recycledViewPool)

    }


    override fun getItemCount(): Int = paidCourseDetails.data.chapters.size

}