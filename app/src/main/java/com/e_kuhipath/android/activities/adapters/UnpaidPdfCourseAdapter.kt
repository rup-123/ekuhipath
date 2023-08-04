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
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.e_kuhipath.android.R
import com.e_kuhipath.android.activities.pages.UnpaidCourseDetailsActivity
import com.e_kuhipath.android.models.UnpaidCourses
import com.e_kuhipath.android.models.UnpaidPdfCourseData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.skydoves.androidribbon.ShimmerRibbonView

class UnpaidPdfCourseAdapter(var unpaidcourses: UnpaidPdfCourseData): RecyclerView.Adapter<UnpaidPdfCourseAdapter.DataViewHolder>() {

    lateinit var context: Context
    lateinit var sliderLayout: View

    inner class DataViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        val unpaidcourse_title = v.findViewById<TextView>(R.id.unpaidpdfcourse_title)
        val pdfno = v.findViewById<TextView>(R.id.pdfno)
        val noofpages = v.findViewById<TextView>(R.id.noofpages)
        val unpaidpdfcourse_price = v.findViewById<TextView>(R.id.unpaidpdfcourse_price)
        val unpaidpdfcourseduration = v.findViewById<TextView>(R.id.unpaidpdfcourseduration)

        val image = v.findViewById<ImageView>(R.id.unpaidpdfcourse_img)

        val unpaidpdfpaidcoursecard = v.findViewById<CardView>(R.id.unpaidpdfpaidcoursecard)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val v = inflater.inflate(R.layout.unpaidpdfcoursecarditem, parent, false)
        return DataViewHolder(v)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.unpaidcourse_title.text = unpaidcourses.unpaid_courses[position].course_name
        holder.unpaidpdfcourse_price.text = "\u20B9"+unpaidcourses.unpaid_courses[position].price
        holder.pdfno.text = unpaidcourses.unpaid_courses[position].no_of_pdf + " pdfs"
        holder.noofpages.text = unpaidcourses.unpaid_courses[position].no_of_pages + " pages"
        holder.unpaidpdfcourseduration.text = unpaidcourses.unpaid_courses[position].course_duration + " months"

        val img_url = "https://www.app.ekuhipath.com/" + unpaidcourses.unpaid_courses[position].image_path

        Glide.with(context)
            .load(img_url)
            .placeholder(R.drawable.no_content)
            .timeout(60000)
            .into(holder.image)

        holder.unpaidpdfpaidcoursecard.setOnClickListener{
            if (unpaidcourses.unpaid_courses[position].payment_type == "OFF"){
                // Log.i("zz","gformlink--->"+unpaidcourses.unpaidcourse[position].g_form_link)
                val gformlink = "https://"+unpaidcourses.unpaid_courses[position].g_form_link
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(gformlink))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
/*
        holder.shimmerview.ribbon.text = "\u20B9"+unpaidcourses.unpaidcourse[position].price
        holder.shimmerview.ribbon.setBackgroundColor(Color.parseColor("#13CE3F"))*/

/*
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

            val img_url = "https://www.app.ekuhipath.com/api/ekuhipath-v1/video-course/get-course-thumbnail/" + course_id
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
*/


    }


    override fun getItemCount(): Int = unpaidcourses.unpaid_courses.size

}