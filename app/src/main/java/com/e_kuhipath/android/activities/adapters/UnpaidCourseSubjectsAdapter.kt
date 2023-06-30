package com.e_kuhipath.android.activities.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.e_kuhipath.android.R
import com.e_kuhipath.android.models.Subjects

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