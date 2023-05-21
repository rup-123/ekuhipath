package com.example.e_kuhipath.activities.pages

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.e_kuhipath.R
import com.example.e_kuhipath.databinding.ActivityPaidCoursesBinding

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

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Get the NavController from the NavHostFragment
        navController = navHostFragment.navController

        binding.paidcoursesCard.setCardBackgroundColor(Color.parseColor("#253A4B"))
        binding.coursestxt.setTextColor(Color.parseColor("#FFFFFF"))

        binding.paidcoursesCard.setOnClickListener {
            Log.i("zz","paidcourse2----->")

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
            navController.navigate(R.id.action_pdf)

            binding.pdfCourseCard.setCardBackgroundColor(Color.parseColor("#253A4B"))
            binding.pdftxt.setTextColor(Color.parseColor("#FFFFFF"))

            binding.paidcoursesCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.coursestxt.setTextColor(Color.parseColor("#000000"))

            binding.mockCourseCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.mocktxt.setTextColor(Color.parseColor("#000000"))
        }

        binding.mockCourseCard.setOnClickListener {
            navController.navigate(R.id.action_mock)

            binding.mockCourseCard.setCardBackgroundColor(Color.parseColor("#253A4B"))
            binding.mocktxt.setTextColor(Color.parseColor("#FFFFFF"))

            binding.paidcoursesCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.coursestxt.setTextColor(Color.parseColor("#000000"))

            binding.pdfCourseCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.pdftxt.setTextColor(Color.parseColor("#000000"))
        }
    }
}