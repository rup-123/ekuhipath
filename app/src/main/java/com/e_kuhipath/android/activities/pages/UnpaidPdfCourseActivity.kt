package com.e_kuhipath.android.activities.pages

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_kuhipath.android.R
import com.e_kuhipath.android.activities.adapters.UnpaidCourseAdapter
import com.e_kuhipath.android.activities.adapters.UnpaidPdfCourseAdapter
import com.e_kuhipath.android.activities.authentication.LoginActivity
import com.e_kuhipath.android.activities.common.BottomNavActivity
import com.e_kuhipath.android.activities.landingpage.WelcomeActivity
import com.e_kuhipath.android.models.UnpaidCourseReturn
import com.e_kuhipath.android.models.UnpaidPdfCourses
import com.e_kuhipath.android.services.RetroService
import com.e_kuhipath.android.services.ServiceBuilder
import com.e_kuhipath.android.utils.IsOnline
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

/*object GlobalUnpaidCourses {
    var unpaidCourseReturn: UnpaidCourseReturn? = null
}*/
class UnpaidPdfCourseActivity: AppCompatActivity() {
    private var PRIVATE_MODE = 0
    lateinit var sharedPref: SharedPreferences
    lateinit var context: Context
    private lateinit var dialog: Dialog

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unpaidpdfcourse)

        val backbtn = findViewById<ImageView>(R.id.unpaid_backbtn)
        backbtn.setOnClickListener{
           // GlobalUnpaidCourses.unpaidCourseReturn = null
            val intent = Intent(this, WelcomeActivity::class.java)
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
        val name = sharedPref.getString("name", "")

        val unpaidcourse_recycler_view = findViewById<RecyclerView>(R.id.unpaidpdfcourse_recycler_view)
        val no_unpaidcourse_ll = findViewById<LinearLayout>(R.id.no_unpaidpdfcourse_ll)
        val retroService: RetroService = ServiceBuilder.buildService(
            RetroService::class.java
        )
        if (isOnline== true) {
            lifecycleScope.launch {
                try {
                    showProgressDialog()
                    val requestcall1: Call<UnpaidPdfCourses> = retroService.getUnpaidPdfCourses(final_token)
                    requestcall1.enqueue(object : Callback<UnpaidPdfCourses> {

                        override fun onResponse(
                            call: Call<UnpaidPdfCourses>,
                            response: Response<UnpaidPdfCourses>
                        ) {
                            Log.i("qqq", "sucessfull------->")
                            if (response.isSuccessful) {
                                val code = response.body()
                                if (code == null) {
                                    Toast.makeText(
                                        this@UnpaidPdfCourseActivity,
                                        "Wrong Code!!",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    dialog.hide()
                                    if (code.data.unpaid_courses.isEmpty()) {
                                        unpaidcourse_recycler_view.visibility = View.GONE
                                        no_unpaidcourse_ll.visibility = View.VISIBLE
                                        /* val buttonLayoutParams: LinearLayout.LayoutParams =
                                             LinearLayout.LayoutParams(
                                                 ViewGroup.LayoutParams.WRAP_CONTENT,
                                                 ViewGroup.LayoutParams.WRAP_CONTENT
                                             )
                                         buttonLayoutParams.setMargins(250, 500, 100, 0)
                                         no_subjects_ll.setLayoutParams(buttonLayoutParams)*/
                                    }
                                    else {
                                        unpaidcourse_recycler_view.visibility = View.VISIBLE
                                        no_unpaidcourse_ll.visibility = View.GONE
                                        setupUI(code)
                                        Log.i("qqq", "response->" + response)
                                        Log.i("qqq", "code->" + response.body()!!)
                                    }

                                }
                            } else {
                                dialog.hide()
                                if (response.code() == 401) {
                                    val b = JSONObject(response.errorBody()!!.string())

                                    if (b.has("message")) {
                                        val message = b.get("message").toString()
                                        Log.i("zzg", "message---->" + message)

                                        Toast.makeText(
                                            this@UnpaidPdfCourseActivity,
                                            message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val editor = sharedPref.edit()
                                        editor.putString("accesstoken", null)
                                        editor.apply()
                                        val intent = Intent(
                                            context,
                                            LoginActivity::class.java
                                        )
                                        context.startActivity(intent)
                                        //  edittext.requestFocus()
                                        //  awesomeValidation.addValidation(dialog,R.id.complainuniqueid, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", message)
                                        //   awesomeValidation.addValidation(uniqueid,"^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", message)

                                    }
                                }
                                else if (response.code() == 400){

                                    val jObjError = JSONObject(response.errorBody()!!.string())
                                    if (jObjError.has("message")){
                                        val message = jObjError.get("message").toString()
                                        Toast.makeText(context,message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }


                        override fun onFailure(call: Call<UnpaidPdfCourses>, t: Throwable) {

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
        /*if (GlobalUnpaidCourses.unpaidCourseReturn!=null){
            setupUI(GlobalUnpaidCourses.unpaidCourseReturn!!)
        }*/



        val home = findViewById<View>(R.id.home)
        val purchases = findViewById<View>(R.id.purchases)
        val downloads = findViewById<View>(R.id.downloads)
        val help = findViewById<View>(R.id.help)


        home.setOnClickListener(BottomNavActivity(this))
        purchases.setOnClickListener(BottomNavActivity(this))
        downloads.setOnClickListener(BottomNavActivity(this))
        help.setOnClickListener(BottomNavActivity(this))

    }

    private fun showProgressDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_progress)
        dialog.show()
    }

    private fun setupUI(code: UnpaidPdfCourses) {
        val unpaidcourse_recycler_view = findViewById<RecyclerView>(R.id.unpaidpdfcourse_recycler_view)
        unpaidcourse_recycler_view.layoutManager = LinearLayoutManager(this@UnpaidPdfCourseActivity)
        val unpaidcourseAdapter = UnpaidPdfCourseAdapter(code.data)

        unpaidcourse_recycler_view.adapter = unpaidcourseAdapter
    }

    override fun onBackPressed() {
        GlobalUnpaidCourses.unpaidCourseReturn = null
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
    }
}