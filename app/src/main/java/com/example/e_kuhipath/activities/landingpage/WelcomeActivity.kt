package com.example.e_kuhipath.activities.landingpage

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.e_kuhipath.R
import com.example.e_kuhipath.activities.authentication.LoginActivity
import com.example.e_kuhipath.activities.authentication.RegisterActivity
import com.example.e_kuhipath.models.StudentLoginTokens
import com.example.e_kuhipath.models.StudentLogoutTokens
import com.example.e_kuhipath.services.RetroService
import com.example.e_kuhipath.services.ServiceBuilder
import com.google.android.material.navigation.NavigationView
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WelcomeActivity: AppCompatActivity() {

    private var pressedTime: Long = 0
    lateinit var toggle: ActionBarDrawerToggle
    private var PRIVATE_MODE = 0
    lateinit var sharedPref: SharedPreferences
    private lateinit var dialog: Dialog

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


        val toolbarsd = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarsd)
        setSupportActionBar(toolbarsd)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)
        toolbarsd.title = ""
        toolbarsd.subtitle = ""



        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout_sd)
        val navview: NavigationView = findViewById(R.id.nav_view_student)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPref = this.getSharedPreferences("sharedpref", PRIVATE_MODE)
        val editor = sharedPref.edit()
        editor.putString("switch", "1")
        editor.apply()
        val accesstoken = sharedPref.getString("accesstoken", "")
        val name = sharedPref.getString("name", "")

        val name_tv = findViewById<TextView>(R.id.name_tv)

        name_tv.text = "Hello, "+ name

        val final_token = "Bearer " + accesstoken
        Log.i("vvv", final_token)


        navview.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> Toast.makeText(
                    applicationContext,
                    "Clicked Home",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_signout -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Are you sure you want to logout?")
                    builder.setPositiveButton("Yes") { _, _ ->

                        showProgressDialog()
                        val retroService: RetroService = ServiceBuilder.buildService(
                            RetroService::class.java
                        )
                        val requestcall: Call<StudentLogoutTokens> =
                            retroService.getLogoutTokens(final_token)

                        requestcall.enqueue(object : Callback<StudentLogoutTokens> {
                            override fun onResponse(
                                call: Call<StudentLogoutTokens>,
                                response: Response<StudentLogoutTokens>
                            ) {
                                if (response.isSuccessful) {
                                    val code = response.body()
                                    if (code == null) {
                                        Toast.makeText(
                                            this@WelcomeActivity,
                                            "Wrong Code!!",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    } else {
                                        dialog.dismiss()
                                        Log.i("zz","inside on response parenthomepage another activity--->")
                                        anotherActivity()
                                    }
                                    Log.i("qqq", "response->" + response)
                                    Log.i("qqq", "code->" + response.body()!!)
                                } else {
                                    dialog.dismiss()
                                    if (response.code() == 401) {
                                        val b = JSONObject(response.errorBody()!!.string())

                                        if (b.has("message")) {
                                            val message = b.get("message").toString()
                                            Log.i("zzg", "message---->" + message)

                                            Toast.makeText(
                                                this@WelcomeActivity,
                                                message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            //  edittext.requestFocus()
                                            //  awesomeValidation.addValidation(dialog,R.id.complainuniqueid, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", message)
                                            //   awesomeValidation.addValidation(uniqueid,"^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", message)

                                        }
                                    } else {
                                        Toast.makeText(
                                            this@WelcomeActivity,
                                            "error code: " + response.code(),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<StudentLogoutTokens>, t: Throwable) {
                                Log.i("www", "failure----->")
                                Toast.makeText(
                                    this@WelcomeActivity,
                                    "Failed to retrieve details",
                                    Toast.LENGTH_SHORT
                                ).show()



                            }

                        })
                    }
                    builder.setNegativeButton("No") { _, _ -> }
                    builder.create().show()
                }

            }
            true
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
    private fun showProgressDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_progress)
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return false
    }
    private fun anotherActivity() {
        Log.i("xxx", "inside another activity----->")
        val editor = sharedPref.edit()
        editor.putString("switch", "0")
        editor.apply()

        if (sharedPref.contains("accesstoken")){
            editor.remove("accesstoken")
            editor.apply()
        }
        if (sharedPref.contains("mobileno")){
            editor.remove("mobileno")
            editor.apply()
        }
        if (sharedPref.contains("name")){
            editor.remove("name")
            editor.apply()
        }
        if (sharedPref.contains("email")){
            editor.remove("email")
            editor.apply()
        }


        val intent = Intent(
            this,
            LoginActivity::class.java
        )
        startActivity(intent)
    }
}