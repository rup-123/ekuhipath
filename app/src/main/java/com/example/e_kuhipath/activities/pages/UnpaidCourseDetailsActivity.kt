package com.example.e_kuhipath.activities.pages

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_kuhipath.R
import com.example.e_kuhipath.activities.adapters.UnpaidCourseAdapter
import com.example.e_kuhipath.activities.adapters.UnpaidCourseSubjectsAdapter
import com.example.e_kuhipath.activities.landingpage.WelcomeActivity
import com.example.e_kuhipath.models.UnpaidCourseDetailsReturn
import com.example.e_kuhipath.models.UnpaidCourseReturn
import com.example.e_kuhipath.services.RetroService
import com.example.e_kuhipath.services.ServiceBuilder
import com.example.e_kuhipath.utils.IsOnline
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class UnpaidCourseDetailsActivity: AppCompatActivity() {

    private var PRIVATE_MODE = 0
    lateinit var sharedPref: SharedPreferences
    lateinit var context: Context
    private lateinit var dialog: Dialog

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unpaid_coursedetails)

        val backbtn = findViewById<ImageView>(R.id.unpaidcoursedetails_backbtn)
        val sub_course_id = intent.getStringExtra("subcourseid")
        Log.i("zz","subcourseid--->"+sub_course_id)
        backbtn.setOnClickListener{
            val intent = Intent(this, UnpaidCoursesActivity::class.java)
            startActivity(intent)
        }

        context = this
        val a = IsOnline()
        val isOnline = a.isOnline(this)
        if (isOnline == false){
            val intent = Intent(context, NoInternetActivity::class.java)
            startActivity(intent)
        }

        sharedPref = this.getSharedPreferences("sharedpref", PRIVATE_MODE)
        val editor = sharedPref.edit()
        editor.putString("switch", "1")
        editor.apply()
        val accesstoken = sharedPref.getString("accesstoken", "")
        val final_token = "Bearer " + accesstoken
        Log.i("vvv", final_token)


        val retroService: RetroService = ServiceBuilder.buildService(
            RetroService::class.java
        )
        if (isOnline== true) {
            lifecycleScope.launch {
                try {
                    showProgressDialog()
                    val requestcall1: Call<UnpaidCourseDetailsReturn> = retroService.getUnpaidCourseDetails(
                        sub_course_id!!,final_token)
                    requestcall1.enqueue(object : Callback<UnpaidCourseDetailsReturn> {

                        override fun onResponse(
                            call: Call<UnpaidCourseDetailsReturn>,
                            response: Response<UnpaidCourseDetailsReturn>
                        ) {
                            Log.i("qqq", "sucessfull------->")
                            if (response.isSuccessful) {
                                val code = response.body()
                                if (code == null) {
                                    Toast.makeText(
                                        this@UnpaidCourseDetailsActivity,
                                        "Wrong Code!!",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    dialog.dismiss()
                                    val price_tv =  findViewById<TextView>(R.id.price_tv)
                                    val subject_tv =  findViewById<TextView>(R.id.subject_tv)
                                    val buy_now = findViewById<TextView>(R.id.buy_now)
                                    price_tv.visibility = View.VISIBLE
                                    subject_tv.visibility = View.VISIBLE
                                    buy_now.visibility = View.VISIBLE
                                    val img = findViewById<ImageView>(R.id.img_coursedetails)
                                    val unpaid_course_name = findViewById<TextView>(R.id.unpaid_course_name)
                                    val price = findViewById<TextView>(R.id.price)
                                    val view = findViewById<WebView>(R.id.web_view)


                                    view.setBackgroundColor(Color.TRANSPARENT)
                                    val data = code.data.unpaidcourse.sub_course_details
                                    //val data = "<html><p class=\"MsoNormal\" style=\"text-align:justify\"><span lang=\"EN-US\" style=\"font-size:12.0pt;line-height:107%;font-family:&quot;Book Antiqua&quot;,serif\">It<br>has both quantitative and reasoning part as prescribed in the new <b>APSC CCE<br>Syllabus</b>. The course will have around 30 lectures. Course materials can be<br>downloaded from the website and the students will have access to the video<br>lectures for six months from the date of enrollment.<o:p></o:p></span></p></html>"
                                    view.loadData(data, "text/html; charset=utf-8", "utf-8")
                                    val img_url = "https://www.ekuhipath.com/api/ekuhipath-v1/video-course/get-course-thumbnail/" + sub_course_id
                                    Glide.with(context)
                                        .load(img_url)
                                        .timeout(60000)
                                        .into(img)
                                    Log.i("zz","img_url--->"+img_url)
                                    unpaid_course_name.text = code.data.unpaidcourse.sub_course_name
                                    price.text = "\u20B9"+code.data.unpaidcourse.price


                                    val subjects_recycler_view = findViewById<RecyclerView>(R.id.subjects_recycler_view)
                                    subjects_recycler_view.layoutManager = LinearLayoutManager(this@UnpaidCourseDetailsActivity)
                                    val unpaidcoursesubjectsAdapter = UnpaidCourseSubjectsAdapter(code.data.subjects)

                                    subjects_recycler_view.adapter = unpaidcoursesubjectsAdapter

                                    buy_now.setOnClickListener{
                                        if (code.data.unpaidcourse.payment_type == "OFF"){
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(code.data.unpaidcourse.g_form_link))

                                            // Start activity to open URL
                                            context.startActivity(intent)
                                        }
                                    }

                                }
                            } else {
                                if (response.code() == 401) {
                                    val b = JSONObject(response.errorBody()!!.string())

                                    if (b.has("message")) {
                                        val message = b.get("message").toString()
                                        Log.i("zzg", "message---->" + message)

                                        Toast.makeText(
                                            this@UnpaidCourseDetailsActivity,
                                            message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        //  edittext.requestFocus()
                                        //  awesomeValidation.addValidation(dialog,R.id.complainuniqueid, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", message)
                                        //   awesomeValidation.addValidation(uniqueid,"^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", message)

                                    }
                                }
                                else if (response.code() == 400){
                                    dialog.dismiss()
                                    val jObjError = JSONObject(response.errorBody()!!.string())
                                    if (jObjError.has("message")){
                                        val message = jObjError.get("message").toString()
                                        Toast.makeText(context,message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }


                        override fun onFailure(call: Call<UnpaidCourseDetailsReturn>, t: Throwable) {

                            Log.i("www", "failure@----->" + t.message)
                            val intent = Intent(context, NoInternetActivity::class.java)
                            startActivity(intent)
                        }

                    })
                } catch (e: Exception) {
                    Log.i("zzz","exception--->"+e)
                }
            }
        }
        else if (isOnline == false){
            val intent = Intent(context, NoInternetActivity::class.java)
            startActivity(intent)
        }

    }

    private fun showProgressDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_progress)
        dialog.show()
    }
    override fun onBackPressed() {
        val intent = Intent(this,UnpaidCoursesActivity::class.java)
        startActivity(intent)
    }

}