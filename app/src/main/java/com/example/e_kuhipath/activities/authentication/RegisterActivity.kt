package com.example.e_kuhipath.activities.authentication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.e_kuhipath.R

class RegisterActivity: AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var backbtn = findViewById<ImageView>(R.id.login_back)

        backbtn.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {

        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

}