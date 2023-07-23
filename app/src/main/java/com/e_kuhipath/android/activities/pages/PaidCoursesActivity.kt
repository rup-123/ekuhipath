package com.e_kuhipath.android.activities.pages

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.e_kuhipath.android.R
import com.e_kuhipath.android.activities.landingpage.WelcomeActivity
import com.e_kuhipath.android.databinding.ActivityPaidCoursesBinding
import com.e_kuhipath.android.fragments.GlobalPaidCourse

class PaidCoursesActivity: AppCompatActivity() {
    private var PRIVATE_MODE = 0
    lateinit var sharedPref: SharedPreferences
    lateinit var context: Context
    private lateinit var dialog: Dialog
    private lateinit var navController: NavController
    private lateinit var binding:ActivityPaidCoursesBinding
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaidCoursesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val backbtn = findViewById<ImageView>(R.id.unpaid_backbtn)
        sharedPref = this.getSharedPreferences("sharedpref", PRIVATE_MODE)

        backbtn.setOnClickListener{
            GlobalPaidCourse.paidCourses = null
            if (sharedPref.contains("segment")){
                val editor = sharedPref.edit()
                editor.remove("segment")
                editor.apply()
            }
            val intent = Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
        }
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Get the NavController from the NavHostFragment
        navController = navHostFragment.navController

        if (!sharedPref.contains("segment")){
            coursesFrag()
        }
        else if (sharedPref.contains("segment")){
            val segment = sharedPref.getString("segment","")
            Log.i("zz","segment--->"+segment)

            if (segment == "courses"){
                coursesFrag()
            }
            else if (segment == "pdfcourses"){
                pdfFrag()
            }
            else if (segment == "mock"){
                mockFrag()
            }
        }
        /*binding.paidcoursesCard.setCardBackgroundColor(Color.parseColor("#253A4B"))
        binding.coursestxt.setTextColor(Color.parseColor("#FFFFFF"))*/

        binding.paidcoursesCard.setOnClickListener {
            Log.i("zz","paidcourse2----->")
            val editor = sharedPref.edit()
            editor.putString("segment","courses")
            editor.apply()
            navController.navigate(R.id.action_courses)
            Log.i("zz","paidcourse3----->")
            binding.paidcoursesCard.setCardBackgroundColor(Color.parseColor("#253A4B"))
            binding.coursestxt.setTextColor(Color.parseColor("#FFFFFF"))

            binding.pdfCourseCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.pdftxt.setTextColor(Color.parseColor("#000000"))

            binding.mockCourseCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.mocktxt.setTextColor(Color.parseColor("#000000"))

        }

        binding.pdfCourseCard.setOnClickListener {
            val editor = sharedPref.edit()
            editor.putString("segment","pdfcourses")
            editor.apply()
            navController.navigate(R.id.action_pdf)

            binding.pdfCourseCard.setCardBackgroundColor(Color.parseColor("#253A4B"))
            binding.pdftxt.setTextColor(Color.parseColor("#FFFFFF"))

            binding.paidcoursesCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.coursestxt.setTextColor(Color.parseColor("#000000"))

            binding.mockCourseCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.mocktxt.setTextColor(Color.parseColor("#000000"))
        }

        binding.mockCourseCard.setOnClickListener {
            val editor = sharedPref.edit()
            editor.putString("segment","mock")
            editor.apply()
            navController.navigate(R.id.action_mock)

            binding.mockCourseCard.setCardBackgroundColor(Color.parseColor("#253A4B"))
            binding.mocktxt.setTextColor(Color.parseColor("#FFFFFF"))

            binding.paidcoursesCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.coursestxt.setTextColor(Color.parseColor("#000000"))

            binding.pdfCourseCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.pdftxt.setTextColor(Color.parseColor("#000000"))
        }
    }

    fun coursesFrag(){
        navController.navigate(R.id.action_courses)
        Log.i("zz","paidcourse3----->")
        binding.paidcoursesCard.setCardBackgroundColor(Color.parseColor("#253A4B"))
        binding.coursestxt.setTextColor(Color.parseColor("#FFFFFF"))

        binding.pdfCourseCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
        binding.pdftxt.setTextColor(Color.parseColor("#000000"))

        binding.mockCourseCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
        binding.mocktxt.setTextColor(Color.parseColor("#000000"))
    }

    fun pdfFrag(){
        navController.navigate(R.id.action_pdf)

        binding.pdfCourseCard.setCardBackgroundColor(Color.parseColor("#253A4B"))
        binding.pdftxt.setTextColor(Color.parseColor("#FFFFFF"))

        binding.paidcoursesCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
        binding.coursestxt.setTextColor(Color.parseColor("#000000"))

        binding.mockCourseCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
        binding.mocktxt.setTextColor(Color.parseColor("#000000"))
    }

    fun mockFrag(){
        navController.navigate(R.id.action_mock)

        binding.mockCourseCard.setCardBackgroundColor(Color.parseColor("#253A4B"))
        binding.mocktxt.setTextColor(Color.parseColor("#FFFFFF"))

        binding.paidcoursesCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
        binding.coursestxt.setTextColor(Color.parseColor("#000000"))

        binding.pdfCourseCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
        binding.pdftxt.setTextColor(Color.parseColor("#000000"))
    }

    override fun onBackPressed() {
        GlobalPaidCourse.paidCourses = null
        if (sharedPref.contains("segment")){
            val editor = sharedPref.edit()
            editor.remove("segment")
            editor.apply()
        }
        val intent = Intent(this,WelcomeActivity::class.java)
        startActivity(intent)
    }
}