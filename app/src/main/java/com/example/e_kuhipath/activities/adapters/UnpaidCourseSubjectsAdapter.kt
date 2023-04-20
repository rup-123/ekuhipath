package com.example.e_kuhipath.activities.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_kuhipath.R
import com.example.e_kuhipath.activities.pages.UnpaidCourseDetailsActivity
import com.example.e_kuhipath.models.Subjects
import com.example.e_kuhipath.models.UnpaidCourses
import com.google.android.material.bottomsheet.BottomSheetDialog

class UnpaidCourseSubjectsAdapter(var subjects: List<Subjects>): RecyclerView.Adapter<UnpaidCourseSubjectsAdapter.DataViewHolder>() {

    lateinit var context: Context

    inner class DataViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        val subject_name = v.findViewById<TextView>(R.id.subject_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val v = inflater.inflate(R.layout.subjectscarditem, parent, false)
        return DataViewHolder(v)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.subject_name.text =subjects[position].subject_name
    }


    override fun getItemCount(): Int = subjects.size

}