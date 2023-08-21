package com.e_kuhipath.android.activities.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.e_kuhipath.android.R
import com.e_kuhipath.android.activities.pages.UnpaidCourseDetailsActivity
import com.e_kuhipath.android.models.UnpaidCourses
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.skydoves.androidribbon.ShimmerRibbonView

class UnpaidCourseAdapter(var unpaidcourses: UnpaidCourses): RecyclerView.Adapter<UnpaidCourseAdapter.DataViewHolder>() {

    lateinit var context: Context
    lateinit var sliderLayout: View

    inner class DataViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        val unpaid_course_name = v.findViewById<TextView>(R.id.unpaid_course_name)
        val buy_now = v.findViewById<TextView>(R.id.buy_now)
        val image = v.findViewById<ImageView>(R.id.imageView)
        val shimmerview = v.findViewById<ShimmerRibbonView>(R.id.shimmerview_unpaidcourses)
        val view_details = v.findViewById<TextView>(R.id.view_details)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val v = inflater.inflate(R.layout.unpaidcoursecarditem, parent, false)
        sliderLayout = inflater.inflate(R.layout.slider_layout, parent, false)
        return DataViewHolder(v)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.unpaid_course_name.text = unpaidcourses.unpaidcourse[position].sub_course_name
        Glide.with(context)
            .load(R.drawable.ekuhipath)
            .placeholder(R.drawable.no_content)
            .timeout(60000)
            .circleCrop()
            .into(holder.image)

        holder.shimmerview.ribbon.text = "\u20B9"+unpaidcourses.unpaidcourse[position].price
        holder.shimmerview.ribbon.setBackgroundColor(Color.parseColor("#13CE3F"))

        holder.buy_now.setOnClickListener{
            if (sliderLayout.getParent() != null) {
                (sliderLayout.getParent() as ViewGroup).removeView(sliderLayout) // <- fix
            }
            val dialog = BottomSheetDialog(context)

            dialog.setContentView(sliderLayout)

            // on below line we are calling
            // a show method to display a dialog.
            dialog.show()
            val img = sliderLayout.findViewById<ImageView>(R.id.img)
            val unpaid_course_name = sliderLayout.findViewById<TextView>(R.id.unpaid_course_name)
            val price = sliderLayout.findViewById<TextView>(R.id.price)
            val buy_now = sliderLayout.findViewById<TextView>(R.id.buy_now)

            val course_id = unpaidcourses.unpaidcourse[position].sub_course_id

            val img_url = "https://www.ekuhica.com/api/ekuhipath-v1/video-course/get-course-thumbnail/" + course_id
            Glide.with(context)
                .load(img_url)
                .timeout(60000)
                .into(img)
            Log.i("zz","img_url--->"+img_url)
            unpaid_course_name.text = unpaidcourses.unpaidcourse[position].sub_course_name
            price.text = "\u20B9"+unpaidcourses.unpaidcourse[position].price

            buy_now.setOnClickListener{
                if (unpaidcourses.unpaidcourse[position].payment_type == "OFF"){
                   // Log.i("zz","gformlink--->"+unpaidcourses.unpaidcourse[position].g_form_link)
                    val gformlink = "https://"+unpaidcourses.unpaidcourse[position].g_form_link
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(gformlink))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        holder.view_details.setOnClickListener{
            val intent = Intent(context, UnpaidCourseDetailsActivity::class.java)
            intent.putExtra("subcourseid", unpaidcourses.unpaidcourse[position].sub_course_id)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = unpaidcourses.unpaidcourse.size

}