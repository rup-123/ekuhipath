package com.e_kuhipath.android.activities.adapters

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.e_kuhipath.android.R
import com.e_kuhipath.android.activities.pages.PaidPdfCourseDetailsActivity
import com.e_kuhipath.android.models.PaidPdfCourse
import com.e_kuhipath.android.models.PaidPdfCourseFile
import kotlinx.android.synthetic.main.activity_video_player.*
import java.lang.Exception

class PaidPdfCourseFileAdapter(var paidPdfCourseFile: List<PaidPdfCourseFile>): RecyclerView.Adapter<PaidPdfCourseFileAdapter.DataViewHolder>() {

    lateinit var context: Context
    lateinit var sliderLayout: View
    private var PRIVATE_MODE = 0
    lateinit var sharedPref: SharedPreferences
    inner class DataViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        val pdf_name = v.findViewById<TextView>(R.id.pdf_name)
        val password = v.findViewById<TextView>(R.id.password_d)
        val img = v.findViewById<ImageView>(R.id.imageView2)
        val img2 = v.findViewById<ImageView>(R.id.download_icon)
        val paidpdfdetailscard = v.findViewById<CardView>(R.id.paidpdfdetailscard)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val v = inflater.inflate(R.layout.paidpdfcoursedetailscarditem, parent, false)
        return DataViewHolder(v)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.pdf_name.text =paidPdfCourseFile[position].pdf_name
        if (paidPdfCourseFile[position].password.isNullOrEmpty()){
            holder.password.visibility = View.GONE
        }
        else {
            holder.password.text = "Password: " + paidPdfCourseFile[position].password
        }
        sharedPref = context.getSharedPreferences("sharedpref", PRIVATE_MODE)
        Glide.with(context)
            .load(R.drawable.pdf_img)
            .placeholder(R.drawable.pdf_img)
            .timeout(60000)
            .into(holder.img)
        Glide.with(context)
            .load(R.drawable.download_icon)
            .placeholder(R.drawable.download_icon)
            .timeout(60000)
            .into(holder.img2)
        holder.paidpdfdetailscard.setOnClickListener {
            Toast.makeText(context,"Downloading...", Toast.LENGTH_LONG).show()
            val video_pdf = "https://www.app.ekuhipath.com/api/ekuhipath-v1/pdf-course/download-paid-pdf-course-file/" +  paidPdfCourseFile[position].pdf_file_id

            Log.i("zzz","downloadinvoice----->")

            val accesstoken = sharedPref.getString("accesstoken", "")
            val final_token = "Bearer " + accesstoken
            val mgr = context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
            try {
                if (mgr!=null) {
                    val replacedpdfname = paidPdfCourseFile[position].pdf_name
                        .replace("/", "-")
                        .replace("\\", "-")
                        .replace(":", "-")
                        .replace("*", "-")
                        .replace("?", "-")
                        .replace("<<", "-")
                        .replace("<", "-")
                        .replace(">", "-")
                        .replace("|", "-")
                    val request = DownloadManager.Request(Uri.parse(video_pdf))
                    request.addRequestHeader("Authorization", final_token)
                    request.setMimeType("application/pdf")
                    request.setDescription("Downloading...")
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
                    request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "eKuhipath-${replacedpdfname}.pdf"
                    )

                    mgr.enqueue(request)
                    // Toast.makeText(this,"Download Started!!!",Toast.LENGTH_LONG).show()
                    Toast.makeText(context,"Downloaded Successfully", Toast.LENGTH_LONG).show()

                }
                else{
                    Toast.makeText(context,"Download Unsuccessfull!!!", Toast.LENGTH_LONG).show()
                }
            }catch (e: Exception){
                Log.i("zzz","exception--->"+e)
            }


        }

    }


    override fun getItemCount(): Int =paidPdfCourseFile.size

}