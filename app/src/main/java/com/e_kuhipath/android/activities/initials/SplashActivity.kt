package com.e_kuhipath.android.activities.initials

import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.e_kuhipath.android.R
import com.e_kuhipath.android.activities.authentication.LoginActivity
import com.e_kuhipath.android.activities.landingpage.WelcomeActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class SplashActivity : AppCompatActivity() {
    private var PRIVATE_MODE = 0
    private lateinit var sharedPref: SharedPreferences
    private lateinit var appUpdateManager: AppUpdateManager
    private val REQUEST_CODE = 101

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Log.i("vvv", "splash--->")

        appUpdateManager = AppUpdateManagerFactory.create(this)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

       // checkForAppUpdate()

        animation()
        proceedToNextActivity()
    }

    private fun animation() {
        val backgroundImage = findViewById<ImageView>(R.id.SplashScreenImage)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.top_slide)
        backgroundImage.startAnimation(slideAnimation)
    }

    private fun checkForAppUpdate() {
        Log.i("vvv", "proceedToNextActivity00--->")

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        REQUEST_CODE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            } else {
                // No update available, proceed to Login or Welcome activity
               // Log.i("vvv", "proceedToNextActivity0--->")
                proceedToNextActivity()
            }
        }
    }

    private fun proceedToNextActivity() {
        sharedPref = this.getSharedPreferences("sharedpref", PRIVATE_MODE)
        if (!sharedPref.contains("switch")) {
            val editor = sharedPref.edit()
            editor.putString("switch", "0")
            editor.apply()
        }

        Handler().postDelayed({
            val switch = sharedPref.getString("switch", "0")
            Log.i("vvv", "switch--->"+switch)
            if(switch == "0"){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else if (switch == "1"){
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000) // 3000 is the delayed time in milliseconds.

    /*val switch = sharedPref.getString("switch", "0")
        Log.i("vvv", "switch--->" + switch)
        if (switch == "0") {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else if (switch == "1") {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }
        finish()*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    // Update successful, proceed to Login or Welcome activity
                    proceedToNextActivity()
                }
                RESULT_CANCELED -> {
                    // Update canceled by the user, exit from the app
                    finish()
                }
                RESULT_IN_APP_UPDATE_FAILED -> {
                    // Update failed, general error
                }
            }
        }
    }

    /*override fun onResume() {
        super.onResume()
        // Check if the update is still in progress
        Log.i("vvv", "resume--->")

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                // Resume the update
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        REQUEST_CODE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            } else {
                // Update not in progress, proceed to Login or Welcome activity
                Log.i("vvv", "proceedToNextActivity001--->")

                proceedToNextActivity()
            }
        }
    }*/

    override fun onDestroy() {
        super.onDestroy()
        // Dispose the update manager
        appUpdateManager.unregisterListener { }
    }
}
