package com.example.e_kuhipath.activities.authentication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.e_kuhipath.R

class LoginActivity: AppCompatActivity() {

    private var pressedTime: Long = 0

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val register = findViewById<TextView>(R.id.register_tv)

        register.setOnClickListener{
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {


        if (pressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity()
            finish()
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT)
                .show()
        }
        pressedTime = System.currentTimeMillis()

    }

}