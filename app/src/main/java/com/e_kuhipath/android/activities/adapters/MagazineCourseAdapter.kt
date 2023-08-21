package com.e_kuhipath.android.activities.adapters

import android.app.DownloadManager
import android.content.Context
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
import com.e_kuhipath.android.models.Magazine
import com.e_kuhipath.android.models.PaidPdfCourseFile
import java.lang.Exception

class MagazineCourseAdapter(var magazine: List<Magazine>): RecyclerView.Adapter<MagazineCourseAdapter.DataViewHolder>() {

    lateinit var context: Context
    lateinit var sliderLayout: View
    private var PRIVATE_MODE = 0
    lateinit var sharedPref: SharedPreferences

    inner class DataViewHolder(var v: View) : RecyclerView.ViewHolder(v) {
        val magazine_img = v.findViewById<ImageView>(R.id.magazine_img)
        val magazinecoursecard = v.findViewById<CardView>(R.id.magazinecoursecard)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        val v = inflater.inflate(R.layout.magazinecoursecarditem, parent, false)
        return DataViewHolder(v)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val img_url = "https://www.ekuhica.com/" +  magazine[position].thumbnail_path

        Glide.with(context)
            .load(img_url)
            .placeholder(R.drawable.no_content)
            .timeout(60000)
            .into(holder.magazine_img)

        /*Glide.with(context)
            .load(R.drawable.download_icon)
            .placeholder(R.drawable.download_icon)
            .timeout(60000)
            .into(holder.img2)*/
        holder.magazinecoursecard.setOnClickListener {
            Toast.makeText(context,"Downloading...", Toast.LENGTH_LONG).show()
            val video_pdf = "https://www.ekuhica.com/api/ekuhipath-v1/magazines/download-magazine/" +  magazine[position].id

            Log.i("zzz","downloadinvoice----->")
            sharedPref = context.getSharedPreferences("sharedpref", PRIVATE_MODE)

            val accesstoken = sharedPref.getString("accesstoken", "")
            val final_token = "Bearer " + accesstoken
            val mgr = context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
            try {
                if (mgr!=null) {
                    val replacedpdfname = magazine[position].pdf_name
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


    override fun getItemCount(): Int =magazine.size

}