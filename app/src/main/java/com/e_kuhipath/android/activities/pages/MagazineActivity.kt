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
import com.e_kuhipath.android.activities.adapters.MagazineCourseAdapter
import com.e_kuhipath.android.activities.adapters.UnpaidCourseAdapter
import com.e_kuhipath.android.activities.authentication.LoginActivity
import com.e_kuhipath.android.activities.common.BottomNavActivity
import com.e_kuhipath.android.activities.landingpage.WelcomeActivity
import com.e_kuhipath.android.models.Magazines
import com.e_kuhipath.android.models.UnpaidCourseReturn
import com.e_kuhipath.android.services.RetroService
import com.e_kuhipath.android.services.ServiceBuilder
import com.e_kuhipath.android.utils.IsOnline
import kotlinx.coroutines.launch
import org.json.JSONObject

import java.lang.Exception


class MagazineActivity: AppCompatActivity() {
    private var PRIVATE_MODE = 0
    lateinit var sharedPref: SharedPreferences
    lateinit var context: Context
    private lateinit var dialog: Dialog

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_magazines)

        val backbtn = findViewById<ImageView>(R.id.magazine_backbtn)
        backbtn.setOnClickListener{
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

        val magazinecourses_recycler_view = findViewById<RecyclerView>(R.id.magazinecourses_recycler_view)
        val no_magazines_ll = findViewById<LinearLayout>(R.id.no_magazines_ll)
        val retroService: RetroService = ServiceBuilder.buildService(
            RetroService::class.java
        )

            if (isOnline== true) {
                lifecycleScope.launch {
                    try {
                        showProgressDialog()
                        Log.i("ss","paid frag---->")
                        val response = retroService.getMagazines(final_token)

                        if (response.isSuccessful) {
                            val code = response.body()
                            if (code == null) {
                                Toast.makeText(
                                    context,
                                    "Response Body is empty",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                dialog.hide()
                                //GlobalPaidPdfCourseDetails.paidPdfCourseDetails = code.data.paid_pdf_course_files
                                if (code.data.magazines.isEmpty()){
                                    no_magazines_ll.visibility = View.VISIBLE
                                    magazinecourses_recycler_view.visibility = View.GONE
                                }
                                else {
                                    no_magazines_ll.visibility = View.GONE
                                    magazinecourses_recycler_view.visibility = View.VISIBLE
                                    setupUI(code.data)
                                }
                            }
                        }
                        else {
                            dialog.hide()
                            if (response.code() == 401) {
                                val b = JSONObject(response.errorBody()!!.string())

                                if (b.has("message")) {
                                    val message = b.get("message").toString()
                                    Log.i("zzg", "message---->" + message)

                                    Toast.makeText(
                                        this@MagazineActivity,
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

    private fun setupUI(code: Magazines) {
        val magazinecourses_recycler_view = findViewById<RecyclerView>(R.id.magazinecourses_recycler_view)
        magazinecourses_recycler_view.layoutManager = LinearLayoutManager(this@MagazineActivity)
        val magazinecourseAdapter = MagazineCourseAdapter(code.magazines)

        magazinecourses_recycler_view.adapter = magazinecourseAdapter
    }

    override fun onBackPressed() {
        GlobalUnpaidCourses.unpaidCourseReturn = null
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
    }
}
