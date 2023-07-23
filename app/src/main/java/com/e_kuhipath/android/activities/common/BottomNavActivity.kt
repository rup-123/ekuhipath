package com.e_kuhipath.android.activities.common

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import com.e_kuhipath.android.R
import com.e_kuhipath.android.activities.landingpage.WelcomeActivity
import com.e_kuhipath.android.activities.pages.PaidCoursesActivity

class BottomNavActivity(context: Context) : View.OnClickListener{
    var context = context
    private var PRIVATE_MODE = 0
    lateinit var sharedPref: SharedPreferences
    override fun onClick(v: View?) {
        Log.i("zz","start onclick--->")
        sharedPref =context .getSharedPreferences("sharedpref", PRIVATE_MODE)
        /*val usertype = sharedPref.getString("usertype","")
        val usertypeemail = sharedPref.getString("usertypeemail","")
        Log.i("zz","usertypeemail--->"+usertypeemail)
        Log.i("zz","usertype--->"+usertype)*/
        when (v!!.id){

            R.id.home->{
                Log.i("aa","id--->"+v.id)
                val intent = Intent(context,WelcomeActivity::class.java)
                context.startActivity(intent)
                Log.i("aa","home clicked--->")
            }

            R.id.purchases->{
                Log.i("aa","id--->"+v.id)
                val intent = Intent(context,PaidCoursesActivity::class.java)
                context.startActivity(intent)
            }

            R.id.downloads->{
                Log.i("aa","id--->"+v.id)
                Log.i("aa","downloads clicked--->")
            }

            R.id.help->{
                Log.i("aa","id--->"+v.id)
                Log.i("aa","help clicked--->")
            }

        }
    }

}